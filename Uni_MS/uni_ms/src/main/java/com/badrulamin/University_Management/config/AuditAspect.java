package com.badrulamin.University_Management.config;

import com.badrulamin.University_Management.entity.AuditLog;
import com.badrulamin.University_Management.entity.User;
import com.badrulamin.University_Management.repository.AuditLogRepository;
import com.badrulamin.University_Management.repository.UserRepository;
import com.badrulamin.University_Management.security.services.UserDetailsImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Aspect
@Component
public class AuditAspect {

    private static final Logger logger = LoggerFactory.getLogger(AuditAspect.class);

    @Autowired private AuditLogRepository auditLogRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ObjectMapper objectMapper;

    @Pointcut("execution(* com.badrulamin.University_Management.controller.*.*(..))")
    public void controllerMethods() {}

    @Around("controllerMethods()")
    public Object audit(ProceedingJoinPoint joinPoint) throws Throwable {
        String action = determineAction(joinPoint);
        String entityType = determineEntityType(joinPoint);

        if ("AuthController".equals(entityType) && (action.contains("login") || action.contains("logout") || action.contains("register") || action.contains("refresh"))) {
            return joinPoint.proceed();
        }

        Object result = joinPoint.proceed();

        try {
            String entityId = extractEntityId(joinPoint, result);
            String ipAddress = getClientIp();
            User currentUser = getCurrentUser();

            if (currentUser == null) {
                return result;
            }

            AuditLog auditLog = new AuditLog();
            auditLog.setUser(currentUser);
            auditLog.setAction(action);
            auditLog.setEntityType(entityType);
            auditLog.setEntityId(entityId);
            auditLog.setIpAddress(ipAddress);
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            logger.warn("Failed to save audit log: {}", e.getMessage());
        }

        return result;
    }

    private String determineAction(ProceedingJoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String httpMethod = getHttpMethod();

        if ("findAll".equals(methodName)) return httpMethod + " " + methodName;
        if ("findById".equals(methodName)) return httpMethod + " " + methodName;
        if ("save".equals(methodName)) return "CREATE";
        if ("update".equals(methodName)) return "UPDATE";
        if ("delete".equals(methodName)) return "DELETE";
        return httpMethod + " " + methodName;
    }

    private String determineEntityType(ProceedingJoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        return className.replace("Controller", "");
    }

    private String extractEntityId(ProceedingJoinPoint joinPoint, Object result) {
        try {
            if (result instanceof org.springframework.http.ResponseEntity<?> response) {
                Object body = response.getBody();
                if (body instanceof com.badrulamin.University_Management.payload.response.PagedResponse) {
                    return null;
                }
                if (body != null) {
                    var idField = body.getClass().getMethod("getId");
                    Object id = idField.invoke(body);
                    return id != null ? id.toString() : null;
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetailsImpl user) {
            return userRepository.findByUsername(user.getUsername()).orElse(null);
        }
        return null;
    }

    private String getClientIp() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String xff = request.getHeader("X-Forwarded-For");
                if (xff != null && !xff.isEmpty()) return xff.split(",")[0].trim();
                return request.getRemoteAddr();
            }
        } catch (Exception ignored) {}
        return null;
    }

    private String getHttpMethod() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                return attrs.getRequest().getMethod();
            }
        } catch (Exception ignored) {}
        return "UNKNOWN";
    }
}
