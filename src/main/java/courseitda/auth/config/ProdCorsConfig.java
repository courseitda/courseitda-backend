package courseitda.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile("prod")
public class ProdCorsConfig implements WebMvcConfigurer {

    private static final String[] ALLOWED_METHODS = {
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
    };

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://courseitda.me")
                .allowedMethods(ALLOWED_METHODS)
                .allowCredentials(true)
                .allowedHeaders("*")
                .maxAge(3600); // 같은 요청을 1시간 내에 다시 보낼 때는 preflight를 생략하고 바로 실제 요청을 보냄
    }
}
