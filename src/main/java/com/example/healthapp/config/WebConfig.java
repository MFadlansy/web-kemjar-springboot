package com.example.healthapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Mapped to src/main/resources/templates/auth/login.html
        registry.addViewController("/login").setViewName("auth/login");
        // Redirect root URL to /login
        registry.addViewController("/").setViewName("forward:/login");
        registry.addViewController("/register").setViewName("auth/register"); // Tambahkan juga untuk register
        registry.addViewController("/dashboard").setViewName("user/dashboard"); // Tambahkan untuk dashboard
        registry.addViewController("/profile").setViewName("user/profile"); // Tambahkan untuk profile
    }
}