package Nemozone.Nemozone.config;

import Nemozone.Nemozone.service.UserService;
import Nemozone.Nemozone.session.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private UserService userService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/api/login/kakao", "/api/login/kakao/callback", "/api-docs/**", "/swagger-ui/**", "/api/photos");
    }

    @Bean
    public LoginInterceptor loginInterceptor() {
        return new LoginInterceptor(userService);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
//                .allowedHeaders("Authorization", "Content-Type")
//                .exposedHeaders("Custom-Header")
                .allowCredentials(true);
//                .maxAge(3600);
    }
}
