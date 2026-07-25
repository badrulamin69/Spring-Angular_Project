package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.LoginSession;
import com.badrulamin.University_Management.entity.User;
import com.badrulamin.University_Management.repository.LoginSessionRepository;
import com.badrulamin.University_Management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class LoginSessionService {

    private final LoginSessionRepository loginSessionRepository;
    private final UserRepository userRepository;

    public Page<LoginSession> findAll(Pageable pageable) {
        return loginSessionRepository.findAll(pageable);
    }

    public LoginSession findById(Long id) {
        return loginSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Login session", "id", id));
    }

    @Transactional
    public LoginSession save(LoginSession loginSession) {
        return loginSessionRepository.save(loginSession);
    }

    @Transactional
    public void delete(Long id) {
        loginSessionRepository.deleteById(id);
    }

    public List<LoginSession> findByUserId(Long userId) {
        return loginSessionRepository.findByUser_IdOrderByLoginTimeDesc(userId);
    }

    public List<LoginSession> findActiveSessions() {
        return loginSessionRepository.findByIsActiveTrue();
    }

    public List<LoginSession> findActiveSessionsByUserId(Long userId) {
        return loginSessionRepository.findByIsActiveTrueAndUser_Id(userId);
    }

    public long getActiveSessionCount() {
        return loginSessionRepository.countByIsActiveTrue();
    }

    public long getUserSessionCount(Long userId) {
        return loginSessionRepository.countByUser_IdAndIsActiveTrue(userId);
    }

    public LoginSession createSession(Long userId, String token, String ipAddress, String userAgent) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        LoginSession session = new LoginSession();
        session.setUser(user);
        session.setSessionToken(token);
        session.setIpAddress(ipAddress);
        session.setLoginTime(LocalDateTime.now());
        session.setLastActivityTime(LocalDateTime.now());
        session.setActive(true);
        session.setExpired(false);

        if (userAgent != null && !userAgent.isEmpty()) {
            parseUserAgent(session, userAgent);
        }

        return loginSessionRepository.save(session);
    }

    public LoginSession terminateSession(Long sessionId) {
        LoginSession session = findById(sessionId);
        session.setActive(false);
        session.setLogoutTime(LocalDateTime.now());
        return loginSessionRepository.save(session);
    }

    public void terminateAllUserSessions(Long userId) {
        List<LoginSession> activeSessions = loginSessionRepository.findByIsActiveTrueAndUser_Id(userId);
        for (LoginSession session : activeSessions) {
            session.setActive(false);
            session.setLogoutTime(LocalDateTime.now());
        }
        loginSessionRepository.saveAll(activeSessions);
    }

    public void updateLastActivity(String sessionToken) {
        loginSessionRepository.findBySessionToken(sessionToken).ifPresent(session -> {
            session.setLastActivityTime(LocalDateTime.now());
            loginSessionRepository.save(session);
        });
    }

    public void cleanupExpiredSessions() {
        List<LoginSession> activeSessions = loginSessionRepository.findByIsActiveTrue();
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        for (LoginSession session : activeSessions) {
            if (session.getLastActivityTime() != null && session.getLastActivityTime().isBefore(cutoff)) {
                session.setExpired(true);
                session.setActive(false);
                session.setLogoutTime(LocalDateTime.now());
            }
        }
        loginSessionRepository.saveAll(activeSessions);
    }

    private void parseUserAgent(LoginSession session, String userAgent) {
        String ua = userAgent.toLowerCase();

        if (ua.contains("mobile") || ua.contains("android") || ua.contains("iphone")) {
            if (ua.contains("tablet") || ua.contains("ipad")) {
                session.setDeviceType("Tablet");
            } else {
                session.setDeviceType("Mobile");
            }
        } else {
            session.setDeviceType("Desktop");
        }

        if (ua.contains("chrome") && !ua.contains("edg")) {
            session.setBrowser("Chrome");
        } else if (ua.contains("firefox")) {
            session.setBrowser("Firefox");
        } else if (ua.contains("safari") && !ua.contains("chrome")) {
            session.setBrowser("Safari");
        } else if (ua.contains("edg")) {
            session.setBrowser("Edge");
        } else if (ua.contains("opera") || ua.contains("opr")) {
            session.setBrowser("Opera");
        } else {
            session.setBrowser("Other");
        }

        if (ua.contains("windows")) {
            session.setOperatingSystem("Windows");
        } else if (ua.contains("mac os")) {
            session.setOperatingSystem("macOS");
        } else if (ua.contains("linux")) {
            session.setOperatingSystem("Linux");
        } else if (ua.contains("android")) {
            session.setOperatingSystem("Android");
        } else if (ua.contains("ios") || ua.contains("iphone") || ua.contains("ipad")) {
            session.setOperatingSystem("iOS");
        } else {
            session.setOperatingSystem("Other");
        }
    }
}