package com.mailservice.security;

import com.mailservice.config.ClientConfig;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

public class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {

    private final ClientConfig clientConfig;
    private final String userId;

    public ApiKeyAuthenticationToken(ClientConfig clientConfig, String userId) {
        super(AuthorityUtils.createAuthorityList("ROLE_CLIENT"));
        this.clientConfig = clientConfig;
        this.userId = userId;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return clientConfig;
    }

    public String getUserId() {
        return userId;
    }
}
