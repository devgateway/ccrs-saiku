package org.saiku;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Octavian Ciubotaru
 */
public class IMSUserDetailsService implements UserDetailsService {

    private String serviceUrl;

    private RestTemplate template;

    public IMSUserDetailsService() {
        template = new RestTemplate();

        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(UserDetails.class, new UserDeserializer());
        objectMapper.registerModule(module);

        template.setMessageConverters(Collections.<HttpMessageConverter<?>>singletonList(
                new MappingJackson2HttpMessageConverter(objectMapper)));
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String url = UriComponentsBuilder.fromHttpUrl(serviceUrl)
                .queryParam("u", username)
                .toUriString();
        ResponseEntity<UserDetails> responseEntity = template.getForEntity(url, UserDetails.class);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        } else {
            throw new RuntimeException("Failed to retrieve the user. Response was: " + responseEntity);
        }
    }

    private static class UserDeserializer extends StdDeserializer<UserDetails> {

        public UserDeserializer() {
            super(UserDetails.class);
        }

        @Override
        public UserDetails deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.getCodec().readTree(p);
            String username = node.get("username").textValue();
            String password = node.get("password").textValue();
            boolean enabled = node.get("enabled").booleanValue();
            boolean credentialsNonExpired = node.get("credentialsNonExpired").booleanValue();
            boolean accountNonExpired = node.get("accountNonExpired").booleanValue();
            boolean accountNonLocked = node.get("accountNonLocked").booleanValue();
            Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
            for (JsonNode el : node.get("authorities")) {
                if (el.has("authority")) {
                    authorities.add(new SimpleGrantedAuthority(el.get("authority").textValue()));
                }
            }
            return new User(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked,
                    authorities);
        }
    }
}
