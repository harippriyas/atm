package com.acme.atm.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.builders.PathSelectors;
import java.util.*;

/** Initializes SpringFox so that the swagger UI is auto-generated as part of the build process. 
 * 
 * @author SHarippriya
 *
 */
@Configuration
@EnableSwagger2
@ComponentScan(basePackages = "com.takeoff.atm.server.api")
public class SwaggerInitializer {
	
	@Bean
    public Docket rulesApi(){
    	
    	Tag authApi = new Tag("Authorization", "Account sign in and sign out APIs.");
    	Tag transactionApi = new Tag("Transactions", "Account transactions like deposit, withdrawal APIs.");
    	
    	String protocols[] = {"http"};
    	
    	// Create Tags or groups of the APIs
    	return new Docket(DocumentationType.SWAGGER_2)
                .select()
                	.apis(RequestHandlerSelectors.basePackage("com.takeoff.atm.server.api"))
                	.paths(PathSelectors.regex(".*/v.*"))
                	.build()
                .apiInfo(apiInfo())
                .protocols(new HashSet<String>(Arrays.asList(protocols)))
                .tags(authApi, transactionApi)
                .useDefaultResponseMessages(false);
    }

    private ApiInfo apiInfo() {
    	return new ApiInfoBuilder()
    	        .title("Takeoff ATM Assignment APIs")
    	        .description("APIs to service the ATM commands")
    	        .version("1.0.0")
    	        .build();
    }
    
    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(Arrays.asList(basicAuthReference()))
                .forPaths(PathSelectors.ant("/api/**"))
                .build();
    }

    private SecurityScheme basicAuthScheme() {
        return new BasicAuth("basicAuth");
    }

    private SecurityReference basicAuthReference() {
        return new SecurityReference("basicAuth", new AuthorizationScope[0]);
    }

}
