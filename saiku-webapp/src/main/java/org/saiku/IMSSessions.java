package org.saiku;

import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Obtains IMS Session by looking at the {@link ServletContext} of the IMS webapp. This application must be deployed
 * with crossSharing enabled in order to be able to read others webapps contexts.
 *
 * @author Octavian Ciubotaru
 */
public class IMSSessions {

    private static final String IMS_WEBAPP_PATH = "/";
    private static final String IMS_SESSIONS_ATTRIBUTE = "sessions";

    public static final String IMS_SESSION_COOKIE_NAME = "SESSIONID";
    public static final String USERNAME_ATTRIBUTE = "username";
    public static final String AUTHORITIES_ATTRIBUTE = "authorities";

    public static ConcurrentHashMap<String, Object> getSession(HttpServletRequest req) {
        HttpSession session = req.getSession();

        ServletContext context = session.getServletContext().getContext(IMS_WEBAPP_PATH);

        ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> imsSessions =
                (ConcurrentHashMap<String, ConcurrentHashMap<String, Object>>) context.getAttribute(IMS_SESSIONS_ATTRIBUTE);
        if (imsSessions == null) {
            return null;
        }

        String imsSessionId = getImsSessionId(req);
        if (imsSessionId == null) {
            return null;
        }

        return imsSessions.get(imsSessionId);
    }

    public static String getImsSessionId(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equalsIgnoreCase(IMS_SESSION_COOKIE_NAME)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
