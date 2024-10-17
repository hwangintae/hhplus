package org.hhplus.ecommerce.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .version("v1.0")
                .title("황인태요 e-commerce API 문서")
                .description("항해 플러스 BE 6기");

        return new OpenAPI()
                .info(info);
    }
}
