package com.molkov.otpservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация OpenAPI для поддержки JWT-аутентификации в Swagger UI.
 * После добавления этого бина в интерфейсе появится кнопка «Authorize».
 */
@Configuration
public class OpenApiConfig {

    private static final String SCHEME_NAME = "bearerAuth";
    private static final String SCHEME_TYPE = "Bearer";

    @Bean
    public OpenAPI customOpenAPI() {
        // описание схемы "bearerAuth"
        SecurityScheme bearerScheme = new SecurityScheme()
                .name("Authorization")
                .type(SecurityScheme.Type.HTTP)
                .scheme(SCHEME_TYPE.toLowerCase()) // "bearer"
                .bearerFormat("JWT")
                .description("Вставьте JWT токен в формате: Bearer <token>");

        // делаем её глобальной (без повторения @SecurityRequirement в контроллерах)
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(SCHEME_NAME);

        return new OpenAPI()
                .components(new Components().addSecuritySchemes(SCHEME_NAME, bearerScheme))
                .addSecurityItem(securityRequirement);
    }
}