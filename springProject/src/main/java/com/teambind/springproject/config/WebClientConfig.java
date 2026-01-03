package com.teambind.springproject.config;

import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    private static final int CONNECT_TIMEOUT_MS = 5000;
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(30);

    // Auth Service
    @Value("${service.auth.url}")
    private String authDns;
    @Value("${service.auth.port}")
    private String authPort;

    // Image Service
    @Value("${service.image.url}")
    private String imageDns;
    @Value("${service.image.port}")
    private String imagePort;

    // Place Info Service
    @Value("${service.place-info.url}")
    private String placeInfoDns;
    @Value("${service.place-info.port}")
    private String placeInfoPort;

    // Room Info Service
    @Value("${service.room-info.url}")
    private String roomInfoDns;
    @Value("${service.room-info.port}")
    private String roomInfoPort;

    // Lee Yong Gwan Lee (Room Reservation) Service
    @Value("${service.lee-yong-gwan-lee.url}")
    private String leeYongGwanLeeDns;
    @Value("${service.lee-yong-gwan-lee.port}")
    private String leeYongGwanLeePort;

    // Ye Yak Hae Yo Service
    @Value("${service.ye-yak-hae-yo.url}")
    private String yeYakHaeYoDns;
    @Value("${service.ye-yak-hae-yo.port}")
    private String yeYakHaeYoPort;

    // Ye Yak Manage Service
    @Value("${service.ye-yak-manage.url}")
    private String yeYakManageDns;
    @Value("${service.ye-yak-manage.port}")
    private String yeYakManagePort;

    // Coupon Service
    @Value("${service.coupon.url}")
    private String couponDns;
    @Value("${service.coupon.port}")
    private String couponPort;

    // Chat Service
    @Value("${service.chat.url}")
    private String chatDns;
    @Value("${service.chat.port}")
    private String chatPort;

    // Notification Service
    @Value("${service.notification.url}")
    private String notificationDns;
    @Value("${service.notification.port}")
    private String notificationPort;

    private String normalizeHost(String raw) {
        if (raw == null) return "";
        String s = raw.trim();
        s = s.replaceFirst("(?i)^https?://", "");
        return s;
    }

    private WebClient createWebClient(WebClient.Builder builder, String dns, String port) {
        String host = normalizeHost(dns);
        String url = "http://%s:%s".formatted(host, port);

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT_MS)
                .responseTimeout(READ_TIMEOUT);

        return builder
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Bean
    public WebClient authWebClient(WebClient.Builder builder) {
        return createWebClient(builder, authDns, authPort);
    }

    @Bean
    public WebClient imageWebClient(WebClient.Builder builder) {
        return createWebClient(builder, imageDns, imagePort);
    }

    @Bean
    public WebClient placeInfoWebClient(WebClient.Builder builder) {
        return createWebClient(builder, placeInfoDns, placeInfoPort);
    }

    @Bean
    public WebClient roomInfoWebClient(WebClient.Builder builder) {
        return createWebClient(builder, roomInfoDns, roomInfoPort);
    }

    @Bean
    public WebClient leeYongGwanLeeWebClient(WebClient.Builder builder) {
        return createWebClient(builder, leeYongGwanLeeDns, leeYongGwanLeePort);
    }

    @Bean
    public WebClient yeYakHaeYoWebClient(WebClient.Builder builder) {
        return createWebClient(builder, yeYakHaeYoDns, yeYakHaeYoPort);
    }

    @Bean
    public WebClient yeYakManageWebClient(WebClient.Builder builder) {
        return createWebClient(builder, yeYakManageDns, yeYakManagePort);
    }

    @Bean
    public WebClient couponWebClient(WebClient.Builder builder) {
        return createWebClient(builder, couponDns, couponPort);
    }

    @Bean
    public WebClient chatWebClient(WebClient.Builder builder) {
        return createWebClient(builder, chatDns, chatPort);
    }

    @Bean
    public WebClient notificationWebClient(WebClient.Builder builder) {
        return createWebClient(builder, notificationDns, notificationPort);
    }
}
