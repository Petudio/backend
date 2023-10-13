package kuding.petudio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfiguration {

    private final String version = "v1";

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Petudio API")
                .description("Petudio의 API 문서화")
                .build();
    }

    @Bean
    public Docket commonApi() {
        return new Docket(DocumentationType.OAS_30)
                .groupName(version)
                .apiInfo(this.apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("kuding.petudio"))
                .paths(PathSelectors.any())
                .build();
    }
}