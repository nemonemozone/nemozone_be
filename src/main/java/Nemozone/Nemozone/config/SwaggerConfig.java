package Nemozone.Nemozone.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("nemozone-api")
                        .version("0.1")
                        .description("nemozone-api swagger-ui"));
    }

    @Bean
    public GroupedOpenApi api() {
        String[] paths = {"/api/v1/**"};
        String[] packagesToScan = {"Nemozone.Nemozone"};
        return GroupedOpenApi.builder().group("nemozone-api")
                .pathsToMatch(paths)
                .packagesToScan(packagesToScan)
                .build();
    }
}
