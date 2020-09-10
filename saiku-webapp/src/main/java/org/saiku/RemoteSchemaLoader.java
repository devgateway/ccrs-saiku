package org.saiku;

import mondrian.olap.Util;
import mondrian.spi.DynamicSchemaProcessor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Octavian Ciubotaru
 */
public class RemoteSchemaLoader implements DynamicSchemaProcessor {

    private final RestTemplate template;

    public RemoteSchemaLoader() {
        this.template = new RestTemplate();
    }

    @Override
    public String processSchema(String schemaUrl, Util.PropertyList connectInfo) throws Exception {
        String url = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/mondrianSchema")
                .queryParam("name", schemaUrl)
                .toUriString();

        return template.getForObject(url, String.class);
    }
}
