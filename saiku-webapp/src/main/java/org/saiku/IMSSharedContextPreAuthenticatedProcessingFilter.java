package org.saiku;

import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

/**
 * PreAuth filter that extracts principal from IMS Session.
 *
 * @author Octavian Ciubotaru
 */
public class IMSSharedContextPreAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {

    public IMSSharedContextPreAuthenticatedProcessingFilter() {
        setAuthenticationDetailsSource(new IMSSharedContextAuthenticationDetailsSource());
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        ConcurrentHashMap<String, Object> session = IMSSessions.getSession(request);
        if (session != null) {
            return session.get(IMSSessions.USERNAME_ATTRIBUTE);
        }
        return null;
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "N/A";
    }
}
