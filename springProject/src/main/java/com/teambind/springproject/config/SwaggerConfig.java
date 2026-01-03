package com.teambind.springproject.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:30090}")
    private int serverPort;

    @Bean
    public OpenAPI openAPI() {
        String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("PlaceOwner Gateway API")
                        .description("""
                                점주/관리자 전용 API Gateway

                                ## 인증
                                - `Authorization: Bearer {JWT}` 헤더 필수
                                - `X-App-Type: PLACE_MANAGER` 헤더 필수

                                ## 접근 권한
                                - PLACE_OWNER, ADMIN 역할만 접근 가능

                                ## 백엔드 서비스
                                - Auth Service: /api/v1/auth/**
                                - Place Info Service: /api/v1/places/**
                                - Room Info Service: /api/v1/rooms/**
                                - Reservation Service: /api/v1/reservations/**
                                - Coupon Service: /api/v1/coupons/**
                                - Chat Service: /api/v1/chats/**
                                - Notification Service: /api/v1/notifications/**
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("TeamBind")
                                .email("support@teambind.co.kr")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT 토큰을 입력하세요")));
    }
}
