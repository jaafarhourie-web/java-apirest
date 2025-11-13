package com.letocart.java_apirest_2026.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    /**
     * Configuration de l'API OpenAPI avec support de l'authentification HTTP Basic
     */
    @Bean
    public OpenAPI customOpenAPI() {
        // Nom du schéma de sécurité
        final String securitySchemeName = "basicAuth";

        return new OpenAPI()
                // Informations sur l'API
                .info(new Info()
                        .title("API REST LetoCart")
                        .description("API REST pour la gestion de commandes - TD Java JEE Spring Boot\n\n" +
                                "**Authentification:**\n" +
                                "- User: `user` / `userpassword` (accès limité)\n" +
                                "- Admin: `admin` / `adminpassword` (accès complet)")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("LetoCart Team")
                                .email("contact@letocart.com")))
                // Configuration du schéma de sécurité HTTP Basic
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic")
                                        .description("Authentification HTTP Basic - Utilisez 'admin/adminpassword' pour accès complet")))
                // Application du schéma de sécurité à toutes les opérations
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName));
    }
}
