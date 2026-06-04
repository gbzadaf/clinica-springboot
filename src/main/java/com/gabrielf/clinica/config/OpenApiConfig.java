package com.gabrielf.clinica.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Clínica API",
        version = "v1",
        description = "Sistema de Agendamento Médico"
))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {

    static {
        SpringDocUtils.getConfig().replaceWithClass(Pageable.class,
                org.springdoc.core.converters.models.PageableAsQueryParam.class);
    }
}
