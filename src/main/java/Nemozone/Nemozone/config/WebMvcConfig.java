package Nemozone.Nemozone.config;

import Nemozone.Nemozone.service.UserService;
import Nemozone.Nemozone.session.KakaoTokenConst;
import Nemozone.Nemozone.session.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private UserService userService;

    public static final String ALLOWED_METHOD_NAMES = "GET,HEAD,POST,PUT,DELETE,TRACE,OPTIONS,PATCH";

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/api/kakao-token", "/api/login/kakao", "/api/login/kakao/callback", "/api-docs/**", "/swagger-ui/**", "/api/photos");
    }

    @Bean
    public LoginInterceptor loginInterceptor() {
        return new LoginInterceptor(userService);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods(ALLOWED_METHOD_NAMES.split(","))
                .allowedHeaders(KakaoTokenConst.HEADER)
                .allowCredentials(true);
//                .maxAge(3600);
    }
}

//    @Bean
//    public CorsFilter corsFilter() {
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration config = new CorsConfiguration();
//        config.addAllowedOrigin("^https?:\\/\\/www.nemoz.one$"); // e.g. /**, http://domain1.com
//        config.addAllowedHeader(CorsConfiguration.ALL);
//        config.addAllowedMethod(HttpMethod.GET);
//        config.addAllowedMethod(HttpMethod.POST);
//        config.addAllowedMethod(HttpMethod.HEAD);
//        config.addAllowedMethod(HttpMethod.PUT);
//        config.addAllowedMethod(HttpMethod.DELETE);
//        config.addAllowedMethod(HttpMethod.TRACE);
//        config.addAllowedMethod(HttpMethod.OPTIONS);
//        config.setAllowCredentials(true);
//        config.setMaxAge(3600L);
//
//        source.registerCorsConfiguration("^https?:\\/\\/www.nemoz.one$", config); // "/**"
//        return new CorsFilter(source);
//    }

