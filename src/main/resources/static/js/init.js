/* Runs in <head> before paint so reveal-animations only apply when JS is on.
   Kept as an external file so the site's Content-Security-Policy can forbid
   inline scripts (script-src 'self'). */
document.documentElement.classList.add("js");
