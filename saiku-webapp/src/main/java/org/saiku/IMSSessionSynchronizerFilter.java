package org.saiku;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Does 2 things:
 * - if IMS session expires, then will invalidate Saiku session
 * - if Saiku is being used, then will touch IMS to keep IMS sessions alive
 *
 * @author Octavian Ciubotaru
 */
public class IMSSessionSynchronizerFilter extends OncePerRequestFilter {

    private static final String IMS_URL = "http://localhost:8080/";

    private static final ConcurrentSkipListSet<String> imsSessionsToTouch = new ConcurrentSkipListSet<String>();

    @Override
    protected void initFilterBean() throws ServletException {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                String id;
                while ((id = imsSessionsToTouch.pollFirst()) != null) {
                    HttpURLConnection httpCon = null;
                    try {
                        URL url = new URL(IMS_URL);
                        httpCon = (HttpURLConnection) url.openConnection();
                        httpCon.setRequestMethod("HEAD");
                        httpCon.setRequestProperty("Cookie", IMSSessions.IMS_SESSION_COOKIE_NAME + "=" + id);
                        httpCon.getResponseCode();
                    } catch (IOException e) {
                        // ignore
                        e.printStackTrace();// TODO remove
                    } finally {
                        if (httpCon != null) {
                            httpCon.disconnect();
                        }
                    }
                }
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        ConcurrentHashMap<String, Object> imsSession = IMSSessions.getSession(request);
        if (imsSession == null) {
            HttpSession httpSession = request.getSession(false);
            if (httpSession != null) {
                httpSession.invalidate();
            }
        } else {
            String imsSessionId = IMSSessions.getImsSessionId(request);
            if (imsSessionId != null) {
                imsSessionsToTouch.add(imsSessionId);
            }
        }

        filterChain.doFilter(request, response);
    }
}
