package com.mailservice.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ClientConfigurationService {

    private static final Logger log = LoggerFactory.getLogger(ClientConfigurationService.class);

    @Value("classpath:clients.json")
    private Resource clientsConfigFile;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, ClientConfig> apiKeyMap;

    @PostConstruct
    public void loadClients() {
        try {
            List<ClientConfig> clients;
            String envConfig = System.getenv("MAIL_CLIENTS_CONFIG");

            if (envConfig != null && !envConfig.isBlank()) {
                log.info("Loading client configuration from environment variable 'MAIL_CLIENTS_CONFIG'");
                clients = objectMapper.readValue(envConfig, new TypeReference<List<ClientConfig>>() {
                });
            } else {
                log.info("Loading client configuration from classpath:clients.json");
                clients = objectMapper.readValue(
                        clientsConfigFile.getInputStream(),
                        new TypeReference<List<ClientConfig>>() {
                        });
            }

            apiKeyMap = clients.stream()
                    .collect(Collectors.toMap(ClientConfig::getApiKey, Function.identity()));

            log.info("Loaded {} clients from configuration.", clients.size());
        } catch (IOException e) {
            log.error("Failed to load client configuration", e);
            throw new RuntimeException("Critical: Could not load client configuration", e);
        }
    }

    public Optional<ClientConfig> getClientByApiKey(String apiKey) {
        return Optional.ofNullable(apiKeyMap.get(apiKey));
    }

    public Map<String, ClientConfig> getApiKeyMap() {
        return apiKeyMap;
    }
}
