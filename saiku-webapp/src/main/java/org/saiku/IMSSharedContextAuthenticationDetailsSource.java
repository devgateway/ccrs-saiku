package org.saiku;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails;

/**
 * Convert authorities from IMS Session to
 * {@link org.springframework.security.core.GrantedAuthority GrantedAuthorities}.
 *
 * @author Octavian Ciubotaru
 */
public class IMSSharedContextAuthenticationDetailsSource implements AuthenticationDetailsSource
        <HttpServletRequest, PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails> {

    @Override
    public PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails buildDetails(HttpServletRequest request) {
        ConcurrentHashMap<String, Object> session = IMSSessions.getSession(request);
        if (session != null) {
            List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();

            List<String> roles = (List<String>) session.get(IMSSessions.AUTHORITIES_ATTRIBUTE);
            for (String role : roles) {
                authorities.add(new SimpleGrantedAuthority(role));
            }

            return new PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails(request, authorities);
        } else {
            return null;
        }
    }
}
