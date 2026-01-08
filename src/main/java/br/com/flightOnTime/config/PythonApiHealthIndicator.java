package br.com.flightOnTime.config;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.HttpURLConnection;

import java.net.URL;

import java.time.LocalDateTime;
import java.util.Map;


@Slf4j
@Component("pythonApiHealth")
public class PythonApiHealthIndicator implements HealthIndicator {
    @Value("${api.python.url}")
    private String flaskUrl;


    private final org.springframework.core.env.Environment env;

    public PythonApiHealthIndicator(org.springframework.core.env.Environment env) {
        this.env = env;
    }

    @Override
    public Health health() {

        String activeProfiles = java.util.Arrays.toString(env.getActiveProfiles());
        log.debug("Testando Flask em: {} | Perfis: {}", flaskUrl, activeProfiles);

        try {
            URL url = new URL(flaskUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);

            int code = conn.getResponseCode();
            conn.disconnect();

            return Health.up()
                    .withDetail("status", "Flask ONLINE")
                    .withDetail("url", flaskUrl)
                    .withDetail("activeProfiles", activeProfiles)
                    .withDetail("httpCode", code)
                    .build();

        } catch (Exception e) {
            return Health.down()
                    .withDetail("status", "Flask OFFLINE")
                    .withDetail("url", flaskUrl)
                    .withDetail("activeProfiles", activeProfiles)
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }






}