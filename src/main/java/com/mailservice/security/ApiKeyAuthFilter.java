package com.mailservice.security;

import com.mailservice.config.ClientConfig;
import com.mailservice.config.ClientConfigurationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(ApiKeyAuthFilter.class);
    private final ClientConfigurationService clientConfigService;

    private static final String HEADER_API_KEY = "X-API-KEY";
    private static final String HEADER_USER_ID = "X-USER-ID";
    private static final String HEADER_SIGNATURE = "X-SIGNATURE";
    private static final String HEADER_TIMESTAMP = "X-TIMESTAMP";

    private static final long MAX_REQUEST_AGE = 5 * 60 * 1000;

    public ApiKeyAuthFilter(ClientConfigurationService clientConfigService) {
        this.clientConfigService = clientConfigService;
    }

    @Override
    protected void doFilterInternal(@org.springframework.lang.NonNull HttpServletRequest request,
            @org.springframework.lang.NonNull HttpServletResponse response,
            @org.springframework.lang.NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!path.startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = request.getHeader(HEADER_API_KEY);
        String userId = request.getHeader(HEADER_USER_ID);
        String signature = request.getHeader(HEADER_SIGNATURE);
        String timestampStr = request.getHeader(HEADER_TIMESTAMP);

        if (apiKey == null || userId == null || signature == null || timestampStr == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing required security headers");
            return;
        }

        // Trim headers to avoid whitespace issues from proxies/environment variables
        apiKey = apiKey.trim();
        userId = userId.trim();
        signature = signature.trim();
        timestampStr = timestampStr.trim();

        Optional<ClientConfig> clientOpt = clientConfigService.getClientByApiKey(apiKey);
        if (clientOpt.isEmpty()) {
            log.warn("Invalid API Key received: [{}]", apiKey);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API Key");
            return;
        }
        ClientConfig client = clientOpt.get();

        try {
            long timestamp = Long.parseLong(timestampStr);
            long now = System.currentTimeMillis();
            if (Math.abs(now - timestamp) > MAX_REQUEST_AGE) {
                log.warn("Request expired for Client={}. Now={}, Timestamp={}", client.getClientId(), now, timestamp);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                        "Request expired (Timestamp too old or future)");
                return;
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Timestamp format");
            return;
        }
        String dataToSign = apiKey + userId + timestampStr;
        if (!isValidSignature(signature, client.getSecretKey(), dataToSign)) {
            log.error("SIGNATURE MISMATCH! Client={} User={}", client.getClientId(), userId);
            log.debug("Data signed: [{}]", dataToSign);
            log.debug("Incoming signature: [{}]", signature);

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid Signature. Data signed was: " + dataToSign);
            return;
        }

        ApiKeyAuthenticationToken auth = new ApiKeyAuthenticationToken(client, userId);
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }

    private boolean isValidSignature(String incomingSignature, String secretKey, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            String expectedSignature = Base64.getEncoder().encodeToString(hmacBytes);
            return expectedSignature.equals(incomingSignature);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Crypto error", e);
            return false;
        }
    }
}
