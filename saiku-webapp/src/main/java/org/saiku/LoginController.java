package org.saiku;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author idobre
 * @since 08/11/15
 *
 * Dummy login controller used to redirect the requests to IMS application
 */

@Controller
public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping(value = "/login", method = {
            RequestMethod.GET, RequestMethod.POST
    })
    public ModelAndView getLogin(HttpServletRequest request, HttpServletResponse response, ModelAndView modelAndView) {
        // if the request is coming through ajax then return an json object with session null
        if(isAjax(request)) {
            ModelAndView mav = new ModelAndView();
            mav.setView(new MappingJackson2JsonView());
            mav.addObject("session", null);

            return mav;
        } else {
            String loginUrl = String.format("%s://%s:%d/login/", request.getScheme(), request.getServerName(), request.getServerPort());

            // redirect the user to /login page from IMS
            return new ModelAndView("redirect:" + loginUrl);
        }
    }

    /**
     * Check if the request is coming through an ajax request
     *
     * @param request
     * @return
     */
    private boolean isAjax(HttpServletRequest request) {
        String requestedWithHeader = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(requestedWithHeader);
    }
}
