/* Storefront micro-interactions — CodePen-style, dependency-free. */
(function () {
    "use strict";

    // 0. Missing photos: hide the broken image so the tee-colour .slot placeholder shows.
    document.querySelectorAll("img").forEach(function (img) {
        var miss = function () { img.classList.add("img-missing"); };
        if (img.complete && img.naturalWidth === 0 && img.getAttribute("src")) miss();
        else img.addEventListener("error", miss);
    });

    // 0a. Cinematic loader — plays once per browser session, then just clears.
    var loader = document.getElementById("loader");
    var body = document.body;
    function finishLoad() {
        body.classList.remove("loading");
        body.classList.add("ready");
        if (loader) {
            loader.classList.add("done");
            setTimeout(function () { loader.style.display = "none"; }, 950);
        }
    }
    if (loader) {
        var reduceLoaderMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
        if (sessionStorage.getItem("introSeen") || reduceLoaderMotion) {
            loader.style.display = "none";
            body.classList.add("ready");
        } else {
            body.classList.add("loading");
            var countEl = document.getElementById("loaderCount");
            var progressEl = document.getElementById("loaderProgress");
            var n = 0;
            var tick = setInterval(function () {
                n = Math.min(100, n + Math.floor(Math.random() * 18) + 6);
                if (countEl) countEl.textContent = String(n).padStart(3, "0");
                if (progressEl) progressEl.style.width = n + "%";
                if (n >= 100) {
                    clearInterval(tick);
                    sessionStorage.setItem("introSeen", "1");
                    setTimeout(finishLoad, 350);
                }
            }, 130);
        }
    } else {
        body.classList.add("ready");
    }

    // 0b. Editorial label cursor + hero image-trail (pointer devices only).
    if (window.matchMedia("(pointer:fine)").matches) {
        body.classList.add("has-cursor");
        var dot = document.createElement("div"); dot.className = "cursor-dot";
        var label = document.createElement("div"); label.className = "cursor-label";
        body.appendChild(dot); body.appendChild(label);

        var mx = window.innerWidth / 2, my = window.innerHeight / 2, lx = mx, ly = my;
        document.addEventListener("mousemove", function (e) {
            mx = e.clientX; my = e.clientY;
            dot.style.transform = "translate(" + mx + "px," + my + "px)";
        });
        // The label trails the dot with a little lag for weight.
        (function loop() {
            lx += (mx - lx) * 0.2; ly += (my - ly) * 0.2;
            label.style.transform = "translate(" + lx + "px," + ly + "px)";
            requestAnimationFrame(loop);
        })();

        // Contextual labels — the cursor tells you what a target does.
        var bind = function (selector, text) {
            document.querySelectorAll(selector).forEach(function (el) {
                el.addEventListener("mouseenter", function () {
                    label.textContent = text || el.getAttribute("data-cursor") || "View";
                    label.classList.add("show");
                });
                el.addEventListener("mouseleave", function () { label.classList.remove("show"); });
            });
        };
        bind(".quick-add button", "Add +");
        bind(".card, .look, .cat, .pdp-thumb, .pdp-main", "View");
        bind(".btn, .cart-link, .nav-links a", "");        // hide label over plain links
        bind("[data-cursor]", null);

        // Hero image-trail: sweeping the hero spawns thumbnails that fade away.
        var hero = document.querySelector(".hero");
        if (hero) {
            var trail = document.createElement("div"); trail.className = "trail";
            body.appendChild(trail);
            var imgs = [
                "iconsi_tee_blackout.webp", "iconsi_tee_cream_statement.webp",
                "iconsi_tee_forest.webp", "iconsi_tee_cobalt.webp",
                "iconsi_tee_varsity_orange.webp", "iconsi_tee_blackout_alt.webp"
            ].map(function (f) { return "/images/" + f; });
            imgs.forEach(function (src) { var pre = new Image(); pre.src = src; }); // preload
            var idx = 0, lastX = 0, lastY = 0, live = [];
            hero.addEventListener("mousemove", function (e) {
                if (Math.hypot(e.clientX - lastX, e.clientY - lastY) < 95) return;  // spacing
                lastX = e.clientX; lastY = e.clientY;
                var img = document.createElement("img");
                img.src = imgs[idx % imgs.length]; idx++;
                img.style.left = e.clientX + "px";
                img.style.top = e.clientY + "px";
                trail.appendChild(img); live.push(img);
                requestAnimationFrame(function () { img.classList.add("on"); });
                setTimeout(function () { img.classList.add("off"); }, 320);
                setTimeout(function () { img.remove(); var k = live.indexOf(img); if (k > -1) live.splice(k, 1); }, 900);
                if (live.length > 8) { var old = live.shift(); if (old) old.remove(); }
            });
        }
    }

    // 1. Nav shrinks / gains solid background once you scroll past the hero top.
    var nav = document.getElementById("nav");
    function onScroll() {
        if (!nav) return;
        nav.classList.toggle("nav-solid", window.scrollY > 40);
    }
    window.addEventListener("scroll", onScroll, { passive: true });
    onScroll();

    // 2. Reveal-on-scroll: elements with .reveal fade + rise into view once.
    var reveals = document.querySelectorAll(".reveal");
    if ("IntersectionObserver" in window && reveals.length) {
        var io = new IntersectionObserver(function (entries) {
            entries.forEach(function (e) {
                if (e.isIntersecting) {
                    e.target.classList.add("in");
                    io.unobserve(e.target);
                }
            });
        }, { threshold: 0.12, rootMargin: "0px 0px -40px 0px" });
        reveals.forEach(function (el, i) {
            el.style.transitionDelay = Math.min(i % 4, 3) * 60 + "ms";
            io.observe(el);
        });
        // Safety net: never leave content hidden if observation stalls.
        setTimeout(function () {
            reveals.forEach(function (el) {
                var r = el.getBoundingClientRect();
                if (r.top < window.innerHeight) el.classList.add("in");
            });
        }, 1400);
    } else {
        reveals.forEach(function (el) { el.classList.add("in"); });
    }

    // 3. Subtle parallax drift on the hero image as you scroll.
    var heroImg = document.querySelector(".hero-media img");
    if (heroImg) {
        window.addEventListener("scroll", function () {
            var y = window.scrollY;
            if (y < window.innerHeight) {
                heroImg.style.transform = "scale(1.08) translateY(" + y * 0.12 + "px)";
            }
        }, { passive: true });
    }

    // 4. Scroll-driven background: crossfade the fixed image layers by scroll %.
    var layers = document.querySelectorAll(".scroll-bg .layer");
    if (layers.length) {
        var updateBg = function () {
            var max = document.documentElement.scrollHeight - window.innerHeight;
            var p = max > 0 ? window.scrollY / max : 0;         // 0 → 1
            var pos = p * (layers.length - 1);                   // which layer
            layers.forEach(function (l, i) {
                var d = Math.abs(pos - i);
                l.style.opacity = String(Math.max(0, 1 - d));
            });
        };
        window.addEventListener("scroll", updateBg, { passive: true });
        window.addEventListener("resize", updateBg);
        updateBg();
    }

    // 5. Product gallery: click a thumbnail to swap the main image.
    var main = document.getElementById("pdpMain");
    if (main) {
        document.querySelectorAll("[data-gallery-thumb]").forEach(function (thumb) {
            thumb.addEventListener("click", function () {
                main.style.opacity = "0";
                setTimeout(function () {
                    main.src = thumb.src;
                    main.style.opacity = "1";
                }, 180);
                document.querySelectorAll(".pdp-thumb").forEach(function (t) {
                    t.classList.remove("active");
                });
                thumb.classList.add("active");
            });
        });
    }

    // 6b. "The Edit" showcase slider — auto-advancing, no dependencies.
    var showcase = document.getElementById("showcase");
    if (showcase) {
        var slides = showcase.querySelectorAll(".sc-slide");
        var dots = showcase.querySelectorAll(".sc-dot");
        var current = 0, timer = null;
        var reduced = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
        var OFFSET = 55;   // px the slides travel, giving a clear direction

        // dir = +1 → new slide enters from the right (next),
        // dir = -1 → new slide enters from the left (prev).
        var goTo = function (i, dir) {
            dir = dir || 1;
            var old = current;
            current = (i + slides.length) % slides.length;
            if (current === old) return;
            slides.forEach(function (s, k) {
                if (k === current) {
                    s.style.transform = "translateX(" + (dir * OFFSET) + "px)";
                    s.classList.add("is-active");
                    requestAnimationFrame(function () {
                        requestAnimationFrame(function () { s.style.transform = ""; });
                    });
                } else if (k === old) {
                    s.classList.remove("is-active");
                    s.style.transform = "translateX(" + (-dir * OFFSET) + "px)";
                } else {
                    s.classList.remove("is-active");
                    s.style.transform = "";
                }
            });
            dots.forEach(function (d, k) { d.classList.toggle("is-active", k === current); });
        };
        var next = function () { goTo(current + 1, 1); };
        var prev = function () { goTo(current - 1, -1); };

        var start = function () {
            if (reduced) return;
            stop();
            timer = setInterval(next, 4500);
        };
        var stop = function () { if (timer) { clearInterval(timer); timer = null; } };
        // Any manual control restarts the clock so it never fights the user.
        var bump = function (fn) { return function () { fn(); start(); }; };

        showcase.querySelector(".sc-next").addEventListener("click", bump(next));
        showcase.querySelector(".sc-prev").addEventListener("click", bump(prev));
        dots.forEach(function (dot, i) {
            dot.addEventListener("click", bump(function () {
                goTo(i, i >= current ? 1 : -1);
            }));
        });
        // Pause while hovered, resume on leave.
        showcase.addEventListener("mouseenter", stop);
        showcase.addEventListener("mouseleave", start);
        start();
    }

    // 6c. Newsletter — inline no-op replaced with a CSP-safe confirmation.
    var news = document.getElementById("newsletter");
    if (news) {
        news.addEventListener("submit", function (e) {
            e.preventDefault();
            var input = news.querySelector("input");
            if (!input.value) return;
            news.innerHTML = '<p class="news-thanks">Thanks — you’re on the list.</p>';
        });
    }

    // 6d. Creative: hero headline "decode" — the accent word scrambles through
    // glyphs and resolves, once, after the intro clears.
    var decodeEl = document.querySelector(".hero-copy h1 .thin");
    if (decodeEl && !reducedMotion()) {
        var finalText = decodeEl.textContent;
        var glyphs = "ABCDEFGHIJKLMNOPQRSTUVWXYZ#%&/()=+*<>";
        var frame = 0;
        var settleStart = sessionStorage.getItem("introSeen") ? 200 : 1600; // wait for loader
        var run = function () {
            var out = "", done = 0;
            for (var i = 0; i < finalText.length; i++) {
                if (finalText[i] === " ") { out += " "; done++; continue; }
                if (i < frame / 3) { out += finalText[i]; done++; }
                else { out += glyphs[Math.floor(Math.random() * glyphs.length)]; }
            }
            decodeEl.textContent = out;
            frame++;
            if (done < finalText.length) requestAnimationFrame(run);
            else decodeEl.textContent = finalText;
        };
        setTimeout(run, settleStart);
    }

    function reducedMotion() {
        return window.matchMedia("(prefers-reduced-motion: reduce)").matches;
    }

    // 6. Magnetic pull on primary buttons (pointer devices only).
    if (window.matchMedia("(pointer:fine)").matches) {
        document.querySelectorAll(".btn-primary").forEach(function (btn) {
            btn.addEventListener("mousemove", function (ev) {
                var r = btn.getBoundingClientRect();
                var mx = ev.clientX - r.left - r.width / 2;
                var my = ev.clientY - r.top - r.height / 2;
                btn.style.transform = "translate(" + mx * 0.18 + "px," + my * 0.28 + "px)";
            });
            btn.addEventListener("mouseleave", function () {
                btn.style.transform = "";
            });
        });
    }
})();
