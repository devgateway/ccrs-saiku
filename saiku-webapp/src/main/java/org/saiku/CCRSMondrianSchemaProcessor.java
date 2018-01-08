package org.saiku;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.jersey.core.impl.provider.entity.Inflector;

import mondrian.olap.Util.PropertyList;
import mondrian.spi.DynamicSchemaProcessor;

/**
 * Generates the schema files based on reusable data and automatic configurations.
 * First it does string based data injections based on TAGs. Then it customizes @@references@@.
 * 
 * @see <a href="../../../resources/readme.md">readme.md</a> for more details
 * 
 * @author Nadejda Mandrescu
 */
public class CCRSMondrianSchemaProcessor implements DynamicSchemaProcessor {
    /*
     * TODO
     *  - schema caching in non-dev mode / check for changes?
     */
    
    private static final Inflector INFLECTOR = Inflector.getInstance();
    private static final Pattern YES_NO_DIM_PATTERN = Pattern.compile(
            "<Dimension table=\"YesNoTable\" *name=[\"|']([^=]*)[\"|'] *caption=[\"|']([^=]*)[\"|'] */>");
    private static final String YES_NO_DIM_TEMPLATE =
            "<Dimension name='@@name@@' caption='@@caption@@' table='YesNoTable' key='Dimension Id'>\n" +
                "<Attributes>\n" +
                    "<Attribute name='Dimension Id' keyColumn='id' hasHierarchy='false' \n" +
                                "levelType='Regular' datatype='Integer'/>\n" +
                    "<Attribute name='@@name@@' caption='@@caption@@' keyColumn='answer' \n" +
                                "approxRowCount='2' hierarchyHasAll='true' levelType='Regular' datatype='Boolean'/>\n" +
                "</Attributes>\n" +
            "</Dimension>";
    private static final String CATEGORY_QUERY_TEMPLATE =
            "<Query alias='@@table@@'>\n" +
                "<ExpressionView>\n" +
                    "<SQL dialect='generic'>\n" +
                        "<![CDATA[SELECT ID, LABEL FROM CATEGORY WHERE DTYPE='@@table@@']]>\n" +
                    "</SQL>\n" +
                "</ExpressionView>\n" +
             "</Query>\n";
    /**
     * Name is optional. Explicitly define it when reusing the same category within the same cube
     * or simplifying an existing dimension that may be already referred in a saved query. Check readme.md for more.
     */
    private static final Pattern CATEGORY_DIM_PATTERN = Pattern.compile(
            "<Dimension *source=[\"|']CATEGORY[\"|'] *table=[\"|']([^=]*)[\"|'] *(name=[\"|']([^=]*)[\"|'])? *caption=[\"|']([^=]*)[\"|'] */>");
    private static final String CATEGORY_DIM_TEMPLATE =
            "<Dimension name='@@name@@' caption='@@caption@@' table='@@table@@'\n" +
                        "key='Dimension Id' visible='true'>\n" +
                "<Attributes>\n" +
                    "<Attribute name='Dimension Id' keyColumn='ID'\n" +
                                "hasHierarchy='false' levelType='Regular' datatype='Integer' />\n" +
                    "<Attribute name='@@name@@' caption='@@caption@@' keyColumn='LABEL'\n" +
                                "hierarchyAllMemberName='All @@captions@@' hierarchyCaption='All @@captions@@'\n" +
                                "levelType='Regular' datatype='String' />\n" +
                "</Attributes>\n" +
            "</Dimension>";
    
    /**
     * Mondrian loads / refreshes schema one by one under the same thread
     * (see {@link AbstractConnectionManager#getAllConnections} {@link AbstractConnectionManager#refreshAllConnections})
     * This is a simple way to share loaded data for other schema definitions to process.
     */
    private static ThreadLocal<String> sharedPhysicalSchema = new ThreadLocal<>();
    private static ThreadLocal<String> sharedDimensions = new ThreadLocal<>();
    private static ThreadLocal<String> sharedDimensionsLinks = new ThreadLocal<>();
    

    @Override
    public String processSchema(String schemaURL, PropertyList connectInfo) throws Exception {
        String content = readContent(schemaURL);
        this.prepareData();
        content = processContents(content);
        return content;
    }
    
    private void prepareData() throws Exception {
        this.setIfNull(sharedPhysicalSchema, "ccrs-mondrian-cp-shared-physical-schema.xml");
        this.setIfNull(sharedDimensions, "ccrs-mondrian-cp-shared-dimensions.xml");
        this.setIfNull(sharedDimensionsLinks, "ccrs-mondrian-cp-shared-dimensions-links.xml");
    }
    
    private void setIfNull(ThreadLocal<String> var, String resourceName) throws Exception {
        if (var.get() == null) {
            var.set(this.readRootElements(resourceName));
        }
    }
    
    private String processContents(String content) {
        content = content.replace("<!-- ## _SHARED_PHYSICAL_SCHEMA_TAG_ ## -->", sharedPhysicalSchema.get());
        content = content.replace("<!-- ## _SHARED_DIMENSIONS_TAG_ ## -->", sharedDimensions.get());
        content = content.replace("<!-- ## _SHARED_DIMENSIONS_LINKS_TAG_ ## -->", sharedDimensionsLinks.get());
        content = this.processYesNoTable(content);
        content = this.processCategories(content);
        return content;
    }
    
    private String processYesNoTable(String content) {
        Matcher m = YES_NO_DIM_PATTERN.matcher(content);
        while(m.find()) {
            String yesNoDimension = m.group();
            String name = m.group(1);
            String caption = m.group(2);
            String result = YES_NO_DIM_TEMPLATE.replace("@@name@@", name);
            result = result.replace("@@caption@@", caption);
            content = content.replace(yesNoDimension, result);
        }
        return content;
    }
    
    private String processCategories(String content) {
        Matcher m = CATEGORY_DIM_PATTERN.matcher(content);
        StringBuilder categorySB = new StringBuilder();
        Set<String> categoryQueries = new HashSet<>();
        while(m.find()) {
            String categoryDimension = m.group();
            String table = m.group(1).trim();
            String name = m.group(3);
            String caption = m.group(4).trim();
            String captions = this.getPluralCaption(caption);
            String query = CATEGORY_QUERY_TEMPLATE.replace("@@table@@", table);
            String result = CATEGORY_DIM_TEMPLATE.replace("@@table@@", table);
            result = result.replace("@@caption@@", this.getActualCaption(caption));
            result = result.replace("@@captions@@", captions);
            if (name == null) {
                name = table;
            }
            result = result.replace("@@name@@", name);
            content = content.replace(categoryDimension, result);
            if (categoryQueries.add(query)) {
                categorySB.append(query);
            }
        }
        content = content.replace("<!-- ## _CATEGORY_QUERIES_TAG_ ## -->", categorySB.toString());
        return content;
    }
    
    private String getPluralCaption(String caption) {
        char last = caption.charAt(caption.length() - 1); 
        if (!Character.isLetter(last)) {
            if (last == '.') {
                caption = caption.substring(0, caption.length() - 1);
            }
            return "\"" + caption + "\"";
        }
        if (caption.endsWith("s")) {
            return caption;
        }
        return INFLECTOR.pluralize(caption);
    }
    
    private String getActualCaption(String caption) {
        if (caption.charAt(caption.length() - 1) == '.') {
            return caption.substring(0, caption.length() - 1);
        }
        return caption;
    }
    
    private String generateNameBasedOnCaption(String caption) {
        String normalized = Normalizer.normalize(caption, Form.NFD);
        return normalized.replaceAll("[^A-Za-z0-9]", "");
    }
    
    private String readRootElements(String resourceName) throws Exception {
        String contents = readContent(resourceName);
        contents = contents.replaceAll("(<(\\077xml.*\\077|/?Root)>)", "");
        return contents;
    }
    
    private String readContent(String resourceName) throws Exception {
        String content;
        try(InputStreamReader isr = new InputStreamReader(this.getResourceAsStream(resourceName), "utf-8")) { 
            try(Scanner scanner = new Scanner(isr)) {
                content = scanner.useDelimiter("\\Z").next();
            }}
        if (content == null) {
            throw new RuntimeException("Could not read '" + resourceName + "'");
        }
        return content;
    }
    
    private InputStream getResourceAsStream(String resourceName) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        return classLoader.getResourceAsStream(resourceName);
    }
    
}