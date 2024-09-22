package com.lpa.app.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.lpa.app.Interceptors.LoyaltyPointsInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoyaltyPointsInterceptor loyaltyPointsInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loyaltyPointsInterceptor)
                .addPathPatterns("/api/**"); // Apply to all API endpoints
    }
}
