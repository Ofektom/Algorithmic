package com.algorithm.web.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * Password-protects the whole app when {@code app.access.password} is set (the owner's private copy,
 * where generation runs on their own Claude Code). Uses HTTP Basic so phone browsers show their
 * built-in sign-in prompt and remember it. Unset (the public site): every request passes through.
 */
@Component
public class AccessControlFilter extends OncePerRequestFilter {

    private final byte[] password;

    public AccessControlFilter(@Value("${app.access.password:}") String password) {
        this.password = password.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return password.length == 0;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (hasValidPassword(request.getHeader("Authorization"))) {
            chain.doFilter(request, response);
            return;
        }
        response.setHeader("WWW-Authenticate", "Basic realm=\"Algorithmic\", charset=\"UTF-8\"");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

    // Any username; only the password is checked, in constant time
    private boolean hasValidPassword(String header) {
        if (header == null || !header.regionMatches(true, 0, "Basic ", 0, 6)) {
            return false;
        }
        String decoded;
        try {
            decoded = new String(Base64.getDecoder().decode(header.substring(6).trim()), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return false;
        }
        int colon = decoded.indexOf(':');
        if (colon < 0) {
            return false;
        }
        byte[] supplied = decoded.substring(colon + 1).getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(supplied, password);
    }
}
