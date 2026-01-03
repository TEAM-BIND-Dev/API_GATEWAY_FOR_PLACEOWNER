package com.teambind.springproject.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "gateway.rate-limit")
public class RateLimitProperties {

    private boolean enabled = true;
    private Policy defaultPolicy = new Policy();
    private Map<String, Policy> endpoints = new HashMap<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Policy getDefaultPolicy() {
        return defaultPolicy;
    }

    public void setDefaultPolicy(Policy defaultPolicy) {
        this.defaultPolicy = defaultPolicy;
    }

    public Map<String, Policy> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(Map<String, Policy> endpoints) {
        this.endpoints = endpoints;
    }

    public Policy getPolicyForPath(String path) {
        return endpoints.entrySet().stream()
                .filter(entry -> path.startsWith(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(defaultPolicy);
    }

    public static class Policy {
        private int limit = 100;
        private Duration duration = Duration.ofMinutes(1);
        private int burstCapacity = 120;

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public Duration getDuration() {
            return duration;
        }

        public void setDuration(Duration duration) {
            this.duration = duration;
        }

        public int getBurstCapacity() {
            return burstCapacity;
        }

        public void setBurstCapacity(int burstCapacity) {
            this.burstCapacity = burstCapacity;
        }

        public double getRefillRate() {
            return (double) limit / duration.toSeconds();
        }
    }
}
