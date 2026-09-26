# Deploying to Railway / Render + Supabase Postgres

The app is a Spring Boot server, so it runs on a JVM host (Railway, Render,
Fly.io) — **not** Vercel. The database is Supabase's managed Postgres. Per-user
data isolation is enforced in Java (each customer only ever sees their own
orders), so you do **not** need Supabase Row-Level Security for this setup.

Two profiles:
- **default** (local): H2 in-memory DB, demo catalogue + demo accounts, H2 console on.
- **prod**: Postgres from env vars, no demo data, no default credentials, secure cookies, templates cached.

---

## 1. Create the database (Supabase)

1. Create a project at [supabase.com](https://supabase.com).
2. **Project → Settings → Database → Connection string → JDBC.**
3. You'll get a host, port, database, user, and password. Use the **connection
   pooler** host (port `6543`) for a container host. Build a JDBC URL like:
   ```
   jdbc:postgresql://aws-0-xxxx.pooler.supabase.com:6543/postgres?sslmode=require
   ```

## 2. Set environment variables (on Railway or Render)

| Variable | Value |
|----------|-------|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `DATABASE_URL` | the JDBC URL from step 1 |
| `DATABASE_USER` | Supabase DB user (e.g. `postgres`) |
| `DATABASE_PASSWORD` | Supabase DB password |
| `ADMIN_EMAIL` | the email for your first admin account |
| `ADMIN_PASSWORD` | a strong password (used once to create the admin) |

On first boot the app creates exactly one ADMIN account from `ADMIN_EMAIL` /
`ADMIN_PASSWORD`. After you've logged in once you can remove `ADMIN_PASSWORD`
from the environment.

## 3a. Deploy on Railway

1. Push this repo to GitHub.
2. Railway → **New Project → Deploy from GitHub repo**.
3. Railway detects the `Dockerfile` and builds it. Add the env vars from step 2.
4. Railway injects `PORT`; the container already binds to it. Done.

## 3b. Deploy on Render

1. Push to GitHub.
2. Render → **New → Web Service → your repo**, Runtime **Docker**.
3. Add the env vars from step 2. Render injects `PORT`; the container binds to it.

## 4. Verify

- Visit the URL — the storefront loads (empty catalogue until you add products).
- Sign in with `ADMIN_EMAIL` / `ADMIN_PASSWORD`, go to `/admin/products`, add products.
- Confirm the H2 console is **not** reachable at `/h2-console` (it's disabled in prod).

---

## Local production smoke test (optional)

Run the prod profile against a local Postgres via Docker:

```bash
docker build -t iconsi-store .
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DATABASE_URL="jdbc:postgresql://host.docker.internal:5432/postgres" \
  -e DATABASE_USER=postgres -e DATABASE_PASSWORD=postgres \
  -e ADMIN_EMAIL=you@example.com -e ADMIN_PASSWORD='a-strong-password' \
  iconsi-store
```

---

## Security notes (what's enforced)

- **Access control** — default-deny; `/admin/**` is ADMIN-only; customers can only
  read their own orders (ownership checked in `AccountController`).
- **No default credentials in prod** — demo accounts only exist when
  `store.seed-demo-data=true` (dev only). Prod admin comes from env vars.
- **Headers** — CSP, HSTS, Referrer-Policy, `X-Content-Type-Options: nosniff`,
  framing denied.
- **CSRF** on for the whole app; cookies `HttpOnly` + `SameSite=Lax` + `Secure` (prod).
- **Errors** never expose stack traces or messages.

## Still recommended before real traffic

- Login **rate-limiting / lockout** (e.g. bucket4j or a WAF) — not included.
- Schema **migrations** (Flyway/Liquibase) instead of `ddl-auto=update`.
- A real **payment gateway** (checkout is currently stubbed).
