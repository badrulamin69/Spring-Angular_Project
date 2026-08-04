package com.badrulamin.University_Management.controller;

import com.badrulamin.University_Management.entity.DashboardWidget;
import com.badrulamin.University_Management.entity.QuickAction;
import com.badrulamin.University_Management.entity.Role;
import com.badrulamin.University_Management.entity.User;
import com.badrulamin.University_Management.payload.response.ApiResponse;
import com.badrulamin.University_Management.payload.response.DashboardResponse;
import com.badrulamin.University_Management.repository.*;
import com.badrulamin.University_Management.security.services.UserDetailsImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired private UserRepository userRepository;
    @Autowired private DashboardWidgetRepository widgetRepository;
    @Autowired private QuickActionRepository quickActionRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private AdministrationRepository AdministrationRepository;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private BookRepository bookRepository;
    @Autowired private InvoiceRepository invoiceRepository;
    @Autowired private ExamRepository examRepository;
    @Autowired private AssignmentRepository assignmentRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
    public ResponseEntity<?> getMyDashboard() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        DashboardResponse response = new DashboardResponse();
        response.setCards(buildCardsFromDb(user));
        response.setQuickActions(buildQuickActions(user));
        response.setSummary(buildSummary(user));
        response.setRecentActivities(new ArrayList<>());
        response.setCharts(new ArrayList<>());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private Role getPrimaryRole(User user) {
        if (user.getRoles() == null || user.getRoles().isEmpty()) return null;
        if (user.getDefaultRoleCode() != null) {
            return user.getRoles().stream()
                .filter(r -> r.getCode().equals(user.getDefaultRoleCode()))
                .findFirst()
                .orElse(user.getRoles().iterator().next());
        }
        return user.getRoles().iterator().next();
    }

    @GetMapping("/widgets")
    @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
    public ResponseEntity<?> getWidgets() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        Role primaryRole = getPrimaryRole(user);
        if (primaryRole == null) return ResponseEntity.ok(ApiResponse.success(new ArrayList<>()));

        List<DashboardWidget> widgets = widgetRepository.findByRole_IdAndVisibleTrueOrderByOrderNo(primaryRole.getId());
        List<Map<String, Object>> widgetList = widgets.stream().map(w -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", w.getId());
            map.put("title", w.getWidgetTitle());
            map.put("type", w.getWidgetType());
            map.put("config", w.getWidgetConfig());
            map.put("apiEndpoint", w.getApiEndpoint());
            map.put("orderNo", w.getOrderNo());
            map.put("columnSpan", w.getColumnSpan());
            map.put("visible", w.getVisible());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(widgetList));
    }

    private List<Map<String, Object>> buildCardsFromDb(User user) {
        List<Map<String, Object>> cards = new ArrayList<>();
        Role primaryRole = getPrimaryRole(user);
        if (primaryRole == null) return cards;

        List<DashboardWidget> widgets = widgetRepository.findByRole_IdAndVisibleTrueOrderByOrderNo(primaryRole.getId());
        for (DashboardWidget w : widgets) {
            if (!"card".equals(w.getWidgetType())) continue;

            Map<String, Object> configMap = parseConfig(w.getWidgetConfig());
            String title = (String) configMap.getOrDefault("title", w.getWidgetTitle());
            String icon = (String) configMap.getOrDefault("icon", "dashboard");
            String color = (String) configMap.getOrDefault("color", "#4F46E5");
            String dataSource = w.getDataSource();

            String value = String.valueOf(resolveCount(dataSource));

            Map<String, Object> card = new LinkedHashMap<>();
            card.put("title", title);
            card.put("value", value);
            card.put("icon", icon);
            card.put("color", color);
            cards.add(card);
        }
        return cards;
    }

    private long resolveCount(String dataSource) {
        if (dataSource == null) return 0;
        return  switch (dataSource) {
            case "students" -> studentRepository.count();
            case "administration" -> AdministrationRepository.count();
            case "employees" -> employeeRepository.count();
            case "courses" -> courseRepository.count();
            case "books" -> bookRepository.count();
            case "invoices" -> invoiceRepository.count();
            case "exams" -> examRepository.count();
            case "assignments" -> assignmentRepository.count();
            default -> 0;
        };
    }

    private Map<String, Object> parseConfig(String json) {
        if (json == null || json.isBlank()) return new LinkedHashMap<>();
        try {
            return objectMapper.readValue(json, new TypeReference<LinkedHashMap<String, Object>>() {});
        } catch (Exception e) {
            return new LinkedHashMap<>();
        }
    }

    private List<Map<String, Object>> buildQuickActions(User user) {
        Role primaryRole = getPrimaryRole(user);
        if (primaryRole == null) return Collections.emptyList();
        List<QuickAction> actions = quickActionRepository.findByRole_IdAndVisibleTrueOrderByOrderNo(primaryRole.getId());
        return actions.stream().map(a -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("title", a.getTitle());
            map.put("icon", a.getIcon());
            map.put("route", a.getRoute());
            map.put("permissionCode", a.getPermissionCode());
            return map;
        }).collect(Collectors.toList());
    }

    private Map<String, Object> buildSummary(User user) {
        Map<String, Object> summary = new LinkedHashMap<>();
        Role primaryRole = getPrimaryRole(user);
        summary.put("role", primaryRole != null ? primaryRole.getCode() : "UNKNOWN");
        summary.put("timestamp", java.time.LocalDateTime.now());
        return summary;
    }
}
