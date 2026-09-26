package com.storetemplate.store.config;

import com.storetemplate.store.service.LoginAttemptService;
import com.storetemplate.store.web.LoginRateLimitFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

/**
 * Security is enforced server-side, default-deny: only the public storefront and
 * static assets are open; the admin area is role-gated; everything else needs an
 * authenticated session. Per-user data isolation (a customer only ever sees their
 * own orders) is additionally enforced in the controllers by verifying ownership.
 *
 * The H2 console is OFF by default and must be explicitly enabled for local dev
 * via {@code store.security.h2-console-enabled=true}. It is never enabled in a
 * profile that reaches production.
 */
@Configuration
public class SecurityConfig {

    private final boolean h2ConsoleEnabled;
    private final LoginAttemptService loginAttempts;

    public SecurityConfig(
            @Value("${store.security.h2-console-enabled:false}") boolean h2ConsoleEnabled,
            LoginAttemptService loginAttempts) {
        this.h2ConsoleEnabled = h2ConsoleEnabled;
        this.loginAttempts = loginAttempts;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt with the framework's vetted defaults (strength 10). Never a plain hash.
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers("/", "/products/**", "/cart/**", "/css/**", "/js/**",
                        "/images/**", "/concepts/**", "/auth/**", "/error").permitAll();
                if (h2ConsoleEnabled) {
                    auth.requestMatchers("/h2-console/**").permitAll();
                }
                auth.requestMatchers("/admin/**").hasRole("ADMIN");
                auth.anyRequest().authenticated();   // default deny
            })
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .defaultSuccessUrl("/account", false)
                .failureUrl("/auth/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/?loggedout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .headers(headers -> {
                // HSTS (only emitted over HTTPS by the framework) + tight referrer leakage.
                headers.httpStrictTransportSecurity(hsts -> hsts
                        .includeSubDomains(true).maxAgeInSeconds(31_536_000));
                headers.referrerPolicy(r -> r.policy(
                        ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN));
                // Content-Security-Policy: same-origin scripts only (no inline JS),
                // inline styles allowed (the templates use style attributes), and the
                // Google Fonts hosts explicitly allowlisted. Framing denied unless the
                // H2 console (which frames itself) is enabled for local dev.
                String frameAncestors = h2ConsoleEnabled ? "'self'" : "'none'";
                headers.contentSecurityPolicy(csp -> csp.policyDirectives(
                        "default-src 'self'; " +
                        "img-src 'self' data:; " +
                        "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; " +
                        "font-src 'self' https://fonts.gstatic.com; " +
                        "script-src 'self'; " +
                        "object-src 'none'; base-uri 'self'; form-action 'self'; " +
                        "frame-ancestors " + frameAncestors));
                if (h2ConsoleEnabled) {
                    headers.frameOptions(frame -> frame.sameOrigin());
                }
            })
            // Cookie-session app → CSRF stays ON. The stateless H2 console is the only
            // exemption, and only when it is explicitly enabled for dev.
            .csrf(csrf -> {
                if (h2ConsoleEnabled) {
                    csrf.ignoringRequestMatchers("/h2-console/**");
                }
            })
            // Brute-force guard: block a login POST from an IP over the attempt limit.
            .addFilterBefore(new LoginRateLimitFilter(loginAttempts),
                    UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
