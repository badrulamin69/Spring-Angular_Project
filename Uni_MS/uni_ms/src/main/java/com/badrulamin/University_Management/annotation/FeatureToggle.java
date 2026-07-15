package com.badrulamin.University_Management.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FeatureToggle {
    String value();
    String message() default "This feature has been disabled by the Super Admin.";
}
