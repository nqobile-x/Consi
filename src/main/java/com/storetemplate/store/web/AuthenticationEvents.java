package com.storetemplate.store.web;

import com.storetemplate.store.service.LoginAttemptService;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

/**
 * Feeds Spring Security auth outcomes into the {@link LoginAttemptService} so the
 * rate limiter can key blocks on the real client IP (resolved from forwarded
 * headers behind Render's proxy).
 */
@Component
public class AuthenticationEvents {

    private final LoginAttemptService loginAttempts;

    public AuthenticationEvents(LoginAttemptService loginAttempts) {
        this.loginAttempts = loginAttempts;
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent event) {
        loginAttempts.loginFailed(clientIp(event.getAuthentication()));
    }

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        loginAttempts.loginSucceeded(clientIp(event.getAuthentication()));
    }

    private String clientIp(Authentication auth) {
        if (auth != null && auth.getDetails() instanceof WebAuthenticationDetails details) {
            return details.getRemoteAddress();
        }
        return null;
    }
}
