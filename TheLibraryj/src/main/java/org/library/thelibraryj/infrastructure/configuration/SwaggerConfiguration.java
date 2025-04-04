package org.library.thelibraryj.infrastructure.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;

@OpenAPIDefinition(
        info = @Info(
                title = "The Library",
                version = "1.0",
                description = "The best library"
        ),
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT authentication",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
//@SecurityScheme(
//        name = "xsrfAuth",
//        description = "XSRF token sent in header and in cookie",
//        scheme = "cookie",
//        type = SecuritySchemeType.APIKEY,
//        in = SecuritySchemeIn.HEADER
//)
class SwaggerConfiguration {
    @Bean
    public OpenAPI TheLibraryAPI() {
        return new OpenAPI();
    }
}
