# Iconsi — Spring Boot Storefront

A production-grade, cinematic e-commerce storefront template built with **Spring Boot + Thymeleaf**, backed by **PostgreSQL**, deployed on **Render** with a **Neon** serverless database.

### 🔗 Live demo: **https://iconsi-store.onrender.com**

> Free-tier hosting sleeps after ~15 min idle — the first load may take ~30–50s to wake, then it's instant.

![Iconsi hero](src/main/resources/static/images/Gemini_Generated_Image_pm0oczpm0oczpm0o.jpeg)

---

## ✨ What it is

A complete, rebrandable fashion storefront with an award-style front end and a secure, real backend — not a static mock. Everything from the catalogue to checkout to an admin panel works against a live database.

| | |
|---|---|
| ![Tweed edit](src/main/resources/static/images/a.jpeg) | ![Lookbook](src/main/resources/static/images/Gemini_Generated_Image_jas7cujas7cujas7.jpeg) |
| ![Hoodie](src/main/resources/static/images/charcoal_hoodie_editorial.webp) | ![Sneakers](src/main/resources/static/images/minimalist_sneakers_flatlay.webp) |

---

## 🚀 Features

**Storefront**
- Cinematic hero, "Shop by collection" cards, featured grid, an auto-advancing **"The Edit"** showcase slider, and a feature-led editorial **Lookbook**
- Interactive product pages (thumbnail gallery, hover image-swap, quick-add)
- Session cart · checkout · order history

**Motion (all original, dependency-free, CSP-safe, degrades without JS)**
- Cinematic load-in intro · long-body stretch cursor · hero image-trail · scroll-driven crossfading background · hero text-decode · directional showcase transitions

**Commerce & accounts**
- Register / sign in / sign out · per-user order history · admin dashboard with product & order management

**Security**
- Spring Security, default-deny, role-gated admin, **per-user data isolation** (IDOR-safe)
- BCrypt passwords · **login rate-limiting** (brute-force guard) · CSRF on
- CSP, HSTS, Referrer-Policy, nosniff headers · no default credentials in production

---

## 🧱 Tech stack

- **Spring Boot 4** (Java 21), Spring MVC + **Thymeleaf**
- **Spring Data JPA** + **PostgreSQL** (H2 in local dev)
- **Spring Security** (form login, BCrypt, roles)
- **Docker** · **Render** (host) · **Neon** (database)

---

## 🖥️ Run locally

```bash
./mvnw spring-boot:run
```

Open http://localhost:8080. Local dev uses an in-memory H2 database with demo data and demo accounts (`admin@example.com` / `admin123`, `demo@example.com` / `demo123`) — these exist **only** in dev.

## ☁️ Deploy

See **[DEPLOY.md](DEPLOY.md)** for Render + Neon/Postgres setup. In production the app runs the `prod` profile: Postgres from env vars, no demo data, secure cookies, and the first admin bootstrapped from `ADMIN_EMAIL` / `ADMIN_PASSWORD`.

## 🎨 Make it yours

- **Brand / tagline** → `store.brand-name`, `store.tagline` in `application.properties`
- **Catalogue** → the admin panel at `/admin/products`, or `DataSeeder.java`
- **Look & feel** → CSS variables at the top of `static/css/style.css`
- **Images** → drop files in `static/images/` and reference them

---

*This is a template — swap the placeholder brand, copy, and imagery for your own.*
