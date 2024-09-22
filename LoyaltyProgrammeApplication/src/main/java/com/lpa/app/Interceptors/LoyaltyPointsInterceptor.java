package com.lpa.app.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.lpa.app.ServiceImpl.LoyaltyService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoyaltyPointsInterceptor implements HandlerInterceptor {

    @Autowired
    private LoyaltyService loyaltyService;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // Call the method to expire loyalty points
        loyaltyService.expireLoyaltyPoints();
    }
}
