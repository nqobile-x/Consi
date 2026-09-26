# ICONSI — Graphic Tee Storefront

A streetwear storefront for **ICONSI**, a bold Joburg label whose first collection is graphic T-shirts. It's built with **Spring Boot + Thymeleaf**, uses **PostgreSQL** in production, and deploys to **Render**.

![ICONSI campaign](src/main/resources/static/images/iconsi_campaign_studio.webp)

> *Wear your presence.* Graphic T-shirts with a point of view.

---

## The collection

One name, different energy. Every shirt carries a single ICONSI wordmark.

| Blackout | Cream Statement | Forest | Cobalt |
|---|---|---|---|
| ![Blackout](src/main/resources/static/images/iconsi_tee_blackout.webp) | ![Cream Statement](src/main/resources/static/images/iconsi_tee_cream_statement.webp) | ![Forest](src/main/resources/static/images/iconsi_tee_forest.webp) | ![Cobalt](src/main/resources/static/images/iconsi_tee_cobalt.webp) |

> Prices and stock in the local demo catalogue (R349 × 40) are placeholders, not confirmed product data.

---

## Features

**Storefront**
- The homepage runs from the "Wear your presence" hero to the tee colourways, a campaign feature, **The Edit** (an auto-advancing carousel) and the **Lookbook**
- Colourway cards come from the live catalogue, with real prices and quick-add
- Product pages with a thumbnail gallery and a hover image-swap
- Session cart, checkout and order history

**Motion** (dependency-free, CSP-safe, degrades without JS)
- Cinematic load-in intro, reveal-on-scroll, a hero image trail, a headline decode and directional carousel transitions
- Tee-colour placeholders if a product photo is ever missing

**Accounts and admin**
- Register, sign in and sign out; per-user order history; an admin dashboard for products and orders

**Security**
- Spring Security with default-deny, a role-gated admin area and per-user data isolation (IDOR-safe)
- BCrypt passwords, login rate-limiting and CSRF protection
- CSP, HSTS, Referrer-Policy and nosniff headers; no default credentials in production

---

## Design

| | |
|---|---|
| Palette | Midnight navy `#091735` · Burnt orange `#ff641f` · Ivory `#f5f0e4` |
| Type | Archivo (wide, heavy) for headings · Playfair Display for accent words · Inter for body text |
| Imagery | Optimised `.webp` for mobile data (PNG masters kept alongside) |

Design tokens live at the top of `src/main/resources/static/css/style.css`.

---

## Tech stack

- **Spring Boot 4** (Java 21), Spring MVC + **Thymeleaf**
- **Spring Data JPA**: H2 in local dev, **PostgreSQL** (Neon) in production
- **Spring Security** (form login, BCrypt, roles)
- **Docker** and **Render**

---

## Run locally

```bash
./mvnw spring-boot:run
```

Open http://localhost:8080. Local dev uses an in-memory H2 database seeded with the four tees and two demo accounts: `admin@example.com` / `admin123` and `demo@example.com` / `demo123`. These accounts exist **only** in dev.

## Deploy

See **[DEPLOY.md](DEPLOY.md)** for the Render and Neon/Postgres setup. Production runs the `prod` profile: Postgres from environment variables, no demo data, secure cookies, and a first admin created from `ADMIN_EMAIL` / `ADMIN_PASSWORD`.

To keep the free Render instance awake, set the repository variable `SITE_URL` to the deployed URL. `.github/workflows/keep-alive.yml` pings it every 10 minutes and does nothing while the variable isn't set.

## Customise

- **Brand and tagline:** `store.brand-name` and `store.tagline` in `application.properties`
- **Catalogue:** the admin panel at `/admin/products`, or `DataSeeder.java` for dev data
- **Images:** `src/main/resources/static/images/`. Use `.webp` for anything the site serves.

---

## Credits

The Fox Street, Johannesburg photograph in the lookbook is by **Adamina**, via [Wikimedia Commons](https://commons.wikimedia.org/wiki/File:Johannesburg_Fox_Street_01.jpg), licensed [CC BY 2.0](https://creativecommons.org/licenses/by/2.0/). The ICONSI tee and campaign imagery is original to the project.
