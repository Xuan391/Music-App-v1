package com.app.music_application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class webconfig {

@Bean
public WebMvcConfigurer corsConfigureer() {
    return  new WebMvcConfigurer() {
        @Override
        public void addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry registry) {
            registry.addMapping("/**").allowedMethods("*").allowedOrigins("*");
        }
    };
}
}
