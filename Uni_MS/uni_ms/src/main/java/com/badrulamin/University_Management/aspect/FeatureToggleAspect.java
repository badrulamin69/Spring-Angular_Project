package com.badrulamin.University_Management.aspect;

import com.badrulamin.University_Management.annotation.FeatureToggle;
import com.badrulamin.University_Management.exception.FeatureDisabledException;
import com.badrulamin.University_Management.service.FeatureService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class FeatureToggleAspect {

    @Autowired
    private FeatureService featureService;

    @Around("@annotation(com.badrulamin.University_Management.annotation.FeatureToggle)")
    public Object checkFeatureToggle(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        FeatureToggle annotation = method.getAnnotation(FeatureToggle.class);

        if (annotation != null) {
            String featureKey = annotation.value();
            if (!featureService.isFeatureEnabled(featureKey)) {
                throw new FeatureDisabledException(featureKey, annotation.message());
            }
        }

        return joinPoint.proceed();
    }
}
