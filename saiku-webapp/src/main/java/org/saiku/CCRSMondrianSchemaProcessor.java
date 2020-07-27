package org.saiku;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

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

    /**
     * Mondrian loads / refreshes schema one by one under the same thread
     * (see {@link AbstractConnectionManager#getAllConnections} {@link AbstractConnectionManager#refreshAllConnections})
     * This is a simple way to share loaded data for other schema definitions to process.
     */
    private static ThreadLocal<String> sharedPhysicalSchema = new ThreadLocal<String>();
    private static ThreadLocal<String> sharedDimensions = new ThreadLocal<String>();


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
    }
    
    private void setIfNull(ThreadLocal<String> var, String resourceName) throws Exception {
        if (var.get() == null) {
            var.set(this.readRootElements(resourceName));
        }
    }
    
    private String processContents(String content) {
        content = content.replace("<!-- ## _SHARED_PHYSICAL_SCHEMA_TAG_ ## -->", sharedPhysicalSchema.get());
        content = content.replace("<!-- ## _SHARED_DIMENSIONS_TAG_ ## -->", sharedDimensions.get());
        return content;
    }

    private String readRootElements(String resourceName) throws Exception {
        String contents = readContent(resourceName);
        contents = contents.replaceAll("(<(\\077xml.*\\077|/?Root)>)", "");
        return contents;
    }
    
    private String readContent(String resourceName) throws Exception {
        String content;
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(this.getResourceAsStream(resourceName), "utf-8");
            Scanner scanner = null;
            try {
                scanner = new Scanner(isr);
                content = scanner.useDelimiter("\\Z").next();
                if (content == null) {
                    throw new RuntimeException("Could not read '" + resourceName + "'");
                }
            } finally {
                if (scanner != null) {
                    scanner.close();
                }
            }
        } finally {
            if (isr != null) {
                isr.close();
            }
        }   
        return content;
    }
    
    private InputStream getResourceAsStream(String resourceName) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        return classLoader.getResourceAsStream(resourceName);
    }
    
}
