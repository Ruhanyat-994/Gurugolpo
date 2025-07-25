package com.loginFeature.login.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI myCustomConfig() {
        return new OpenAPI()
                .info(new Info()
                        .title("Journal App APIs")
                        .description("By Ruhanyat")
                        .version("1.0.0"))
                .servers(List.of(
                        new Server().url("http://localhost:8080/").description("Local Server")
                ))
                .tags(Arrays.asList(
                        new Tag().name("Auth").description("Authentication endpoints"),
                        new Tag().name("Blogs").description("Blog management APIs"),
                        new Tag().name("Comments").description("Comment-related endpoints")
                ));
    }

}
