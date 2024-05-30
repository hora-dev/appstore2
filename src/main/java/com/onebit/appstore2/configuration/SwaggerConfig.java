package com.onebit.appstore2.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;
import static java.util.Objects.nonNull;

@Configuration
@OpenAPIDefinition
public class SwaggerConfig {

    private static final String DOCUMENTATION_DESCRIPTION = "API Documentation";

    @Value("${apiinfo.title}")
    private String title;

    @Value("${apiinfo.version}")
    private String version;

    @Value("${apiinfo.description}")
    private String description;

    @Value("${apiinfo.nameContact}")
    private String contactName;

    @Value("${apiinfo.mailContact}")
    private String contactEmail;

    @Value("${swag.url.confluence}")
    private String confluenceUrl;

    @Value("${swag.url.terms}")
    private String termsUrl;

    @Bean
    public OpenAPI openAPI() {
        final var contact = new Contact()
                .name(contactName)
                .email(contactEmail);

        final var info = new Info()
                .title(title)
                .version(version)
                .description(description)
                .termsOfService(termsUrl)
                .contact(contact);

        final var externalDocumentation = new ExternalDocumentation()
                .url(confluenceUrl)
                .description(DOCUMENTATION_DESCRIPTION);

        return new OpenAPI()
                .info(info)
                .externalDocs(externalDocumentation);
    }

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {

            List<Server> servers = openApi.getServers();
            if (nonNull(servers)) {
                servers.stream()
                        .filter(x -> !x.getUrl().contains("localhost"))
                        .forEach(x -> {
                            x.setUrl(x.getUrl().replace("http", "https"));
                            x.setDescription(null);
                        });
            }
        };
    }
}