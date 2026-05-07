package com.roboshop.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Simple session-based authentication interceptor.
 *
 * DR NOTE: Sessions are stored in-memory on this Tomcat instance.
 * In Active/Passive DR, session data is LOST on failover.
 * This requires sticky sessions (load balancer affinity) and
 * users must re-login after failover. In microservices, JWT tokens
 * are stateless and survive failover without session loss.
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("/login?redirect=" + request.getRequestURI());
            return false;
        }
        return true;
    }
}
