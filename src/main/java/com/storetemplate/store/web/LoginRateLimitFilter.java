package com.storetemplate.store.web;

import com.storetemplate.store.service.LoginAttemptService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Short-circuits a login POST from an IP that has exceeded the failed-attempt
 * limit, before Spring Security tries to authenticate. Only guards the login
 * POST; every other request passes straight through.
 */
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private final LoginAttemptService loginAttempts;

    public LoginRateLimitFilter(LoginAttemptService loginAttempts) {
        this.loginAttempts = loginAttempts;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        boolean isLoginPost = "POST".equalsIgnoreCase(request.getMethod())
                && "/auth/login".equals(request.getServletPath());
        if (isLoginPost && loginAttempts.isBlocked(request.getRemoteAddr())) {
            response.sendRedirect(request.getContextPath() + "/auth/login?blocked");
            return;
        }
        chain.doFilter(request, response);
    }
}
