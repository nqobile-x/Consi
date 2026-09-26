package com.storetemplate.store.config;

import com.storetemplate.store.model.AppUser;
import com.storetemplate.store.model.Product;
import com.storetemplate.store.model.Role;
import com.storetemplate.store.repository.ProductRepository;
import com.storetemplate.store.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

/**
 * Seeds content on startup. Two independent, security-conscious paths:
 *
 * <ol>
 *   <li><b>Demo data</b> (placeholder products + the {@code admin@example.com} /
 *       {@code demo@example.com} accounts) is only created when
 *       {@code store.seed-demo-data=true}. It defaults to <b>false</b> so a
 *       production deploy never ships with default credentials (OWASP A02/A07).</li>
 *   <li><b>Real admin bootstrap:</b> if {@code store.admin.email} and
 *       {@code store.admin.password} are supplied (via environment variables) and
 *       no such account exists yet, one ADMIN account is created from them. This
 *       lets production get a first admin without any hard-coded secret.</li>
 * </ol>
 */
@Configuration
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private static String img(String file) {
        return "/images/" + file;
    }

    @Bean
    CommandLineRunner seed(ProductRepository products, UserRepository users, PasswordEncoder encoder,
                           @Value("${store.seed-demo-data:false}") boolean seedDemoData,
                           @Value("${store.admin.email:}") String adminEmail,
                           @Value("${store.admin.password:}") String adminPassword) {
        return args -> {
            // 1. Bootstrap a real admin from environment-supplied credentials.
            if (!adminEmail.isBlank() && !adminPassword.isBlank()
                    && !users.existsByEmailIgnoreCase(adminEmail)) {
                AppUser admin = new AppUser();
                admin.setEmail(adminEmail.trim().toLowerCase());
                admin.setPasswordHash(encoder.encode(adminPassword));
                admin.setFullName("Administrator");
                admin.setRole(Role.ADMIN);
                users.save(admin);
                log.info("Bootstrapped admin account for {}", admin.getEmail());
            }

            if (!seedDemoData) {
                return;   // production: no demo credentials, no placeholder catalogue
            }

            // 2. Demo accounts (LOCAL DEV ONLY — never enabled in production).
            if (!users.existsByEmailIgnoreCase("admin@example.com")) {
                AppUser admin = new AppUser();
                admin.setEmail("admin@example.com");
                admin.setPasswordHash(encoder.encode("admin123"));
                admin.setFullName("Store Admin");
                admin.setRole(Role.ADMIN);
                users.save(admin);

                AppUser demo = new AppUser();
                demo.setEmail("demo@example.com");
                demo.setPasswordHash(encoder.encode("demo123"));
                demo.setFullName("Demo Customer");
                demo.setRole(Role.USER);
                users.save(demo);
            }

            // Initial collection is graphic tees only: one ICONSI wordmark per shirt,
            // no globe or repeated marks. Prices and stock are PLACEHOLDERS — not
            // confirmed product data. Saved in reverse so Blackout leads "newest first".
            if (products.count() == 0) {
                products.save(product("ICONSI Cobalt",
                        "Cobalt-blue oversized tee with a single ICONSI varsity wordmark in cream. Heavyweight cotton.",
                        "349.00", 40, "tees", "iconsi_tee_cobalt.webp"));
                products.save(product("ICONSI Forest",
                        "Forest-green oversized tee with a single ICONSI varsity wordmark in cream. Heavyweight cotton.",
                        "349.00", 40, "tees", "iconsi_tee_forest.webp"));
                products.save(product("ICONSI Cream Statement",
                        "Ivory oversized tee with a single ICONSI varsity wordmark in midnight navy. Clean base, bold type.",
                        "349.00", 40, "tees", "iconsi_tee_cream_statement.webp"));
                products.save(product("ICONSI Blackout",
                        "Washed-black oversized tee with a single distressed ICONSI wordmark. One graphic, all presence.",
                        "349.00", 40, "tees", "iconsi_tee_blackout.webp", "iconsi_tee_blackout_alt.webp"));
            }
        };
    }

    private static Product product(String name, String description,
                                   String price, int stock, String cat,
                                   String... imageSeeds) {
        Product p = new Product();
        p.setName(name);
        p.setDescription(description);
        p.setCategory(cat);
        p.setPrice(new BigDecimal(price));
        p.setStock(stock);
        p.setActive(true);
        StringBuilder urls = new StringBuilder();
        for (int i = 0; i < imageSeeds.length; i++) {
            if (i > 0) urls.append(",");
            urls.append(img(imageSeeds[i]));
        }
        p.setImageUrls(urls.toString());
        return p;
    }
}
