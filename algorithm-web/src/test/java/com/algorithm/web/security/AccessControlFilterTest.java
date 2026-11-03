package com.algorithm.web.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class AccessControlFilterTest {

    private MockHttpServletResponse request(String password, String authorization) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
        if (authorization != null) {
            request.addHeader("Authorization", authorization);
        }
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        new AccessControlFilter(password).doFilter(request, response, chain);
        // Passed through iff the chain ran
        if (chain.getRequest() != null) {
            response.setStatus(200);
        }
        return response;
    }

    private static String basic(String user, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((user + ":" + password).getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void openWhenNoPasswordConfigured() throws Exception {
        // The public site
        assertEquals(200, request("", null).getStatus());
    }

    @Test
    void challengesWithoutCredentials() throws Exception {
        MockHttpServletResponse response = request("s3cret", null);
        assertEquals(401, response.getStatus());
        assertTrue(response.getHeader("WWW-Authenticate").startsWith("Basic "));
    }

    @Test
    void rejectsWrongPassword() throws Exception {
        assertEquals(401, request("s3cret", basic("me", "guess")).getStatus());
    }

    @Test
    void rejectsMalformedHeaders() throws Exception {
        assertEquals(401, request("s3cret", "Basic not-base64!").getStatus());
        assertEquals(401, request("s3cret", "Bearer s3cret").getStatus());
        assertEquals(401, request("s3cret", "Basic " + Base64.getEncoder().encodeToString("nocolon".getBytes())).getStatus());
    }

    @Test
    void acceptsCorrectPasswordWithAnyUsername() throws Exception {
        assertEquals(200, request("s3cret", basic("me", "s3cret")).getStatus());
        assertEquals(200, request("s3cret", basic("", "s3cret")).getStatus());
    }

    @Test
    void passwordMayContainColons() throws Exception {
        assertEquals(200, request("a:b:c", basic("me", "a:b:c")).getStatus());
    }
}
