package com.badrulamin.University_Management.interceptor;

import com.badrulamin.University_Management.service.FeatureService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;

@Component
public class FeatureToggleInterceptor implements HandlerInterceptor {

    @Autowired
    private FeatureService featureService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Map<String, String> MODULE_MAP = new HashMap<>();
    static {
        MODULE_MAP.put("admission-circulars", "admission.circular");
        MODULE_MAP.put("admission-applications", "admission.application");
        MODULE_MAP.put("admission-tests", "admission.test");
        MODULE_MAP.put("admission-merit-lists", "admission.merit");
        MODULE_MAP.put("admission-waiting-lists", "admission.waiting");
        MODULE_MAP.put("admission-interviews", "admission.interview");
        MODULE_MAP.put("document-verifications", "admission.document");
        MODULE_MAP.put("admission-requirements", "admission.requirement");
        MODULE_MAP.put("admission-fee-collection", "admission.fee");
        MODULE_MAP.put("admission-enrollments", "admission.enrollment");
        MODULE_MAP.put("admission-candidates", "admission.candidate");
        MODULE_MAP.put("admission-offer-letters", "admission.offer");
        MODULE_MAP.put("admission-sessions", "admission.session");

        MODULE_MAP.put("faculties", "academic.faculty");
        MODULE_MAP.put("departments", "academic.department");
        MODULE_MAP.put("programs", "academic.program");
        MODULE_MAP.put("courses", "academic.course");
        MODULE_MAP.put("subjects", "academic.subject");
        MODULE_MAP.put("semesters", "academic.semester");
        MODULE_MAP.put("batches", "academic.batch");
        MODULE_MAP.put("sections", "academic.section");
        MODULE_MAP.put("academic-sessions", "academic.session");
        MODULE_MAP.put("curriculum", "academic.curriculum");
        MODULE_MAP.put("credit-rules", "academic.credit");
        MODULE_MAP.put("prerequisites", "academic.prerequisite");
        MODULE_MAP.put("course-assignments", "academic.assignment");
        MODULE_MAP.put("academic-calendar", "academic.calendar");
        MODULE_MAP.put("class-routines", "academic.routine");
        MODULE_MAP.put("semester-routines", "academic.routine");
        MODULE_MAP.put("academic-policies", "academic.policy");

        MODULE_MAP.put("students", "student.view");
        MODULE_MAP.put("student-enrollments", "student.enrollment");
        MODULE_MAP.put("student-profiles", "student.profile");
        MODULE_MAP.put("student-guardians", "student.guardian");
        MODULE_MAP.put("student-attendance", "student.attendance");
        MODULE_MAP.put("student-results", "student.result");
        MODULE_MAP.put("student-transcripts", "student.transcript");
        MODULE_MAP.put("student-certificates", "student.certificate");
        MODULE_MAP.put("student-documents", "student.document");

        MODULE_MAP.put("employees", "hrm.employee");
        MODULE_MAP.put("attendance", "hrm.attendance");
        MODULE_MAP.put("leave-requests", "hrm.leave");
        MODULE_MAP.put("payrolls", "hrm.payroll");

        MODULE_MAP.put("exams", "exam.exam");
        MODULE_MAP.put("schedules", "exam.schedule");
        MODULE_MAP.put("marks", "exam.marks");
        MODULE_MAP.put("grade-rules", "exam.grade");
        MODULE_MAP.put("exam-results", "exam.result");

        MODULE_MAP.put("assignments", "lms.assignment");
        MODULE_MAP.put("submissions", "lms.submission");
        MODULE_MAP.put("materials", "lms.material");
        MODULE_MAP.put("online-classes", "lms.online");

        MODULE_MAP.put("fee-types", "finance.feetype");
        MODULE_MAP.put("student-fees", "finance.studentfee");
        MODULE_MAP.put("invoices", "finance.invoice");
        MODULE_MAP.put("payments", "finance.payment");
        MODULE_MAP.put("accounts", "finance.account");
        MODULE_MAP.put("transactions", "finance.transaction");

        MODULE_MAP.put("books", "library.book");
        MODULE_MAP.put("categories", "library.category");
        MODULE_MAP.put("issues", "library.issue");
        MODULE_MAP.put("library-returns", "library.return");

        MODULE_MAP.put("hostels", "hostel.hostel");
        MODULE_MAP.put("rooms", "hostel.room");
        MODULE_MAP.put("hostel-allocations", "hostel.allocation");

        MODULE_MAP.put("vehicles", "transport.vehicle");
        MODULE_MAP.put("routes", "transport.route");
        MODULE_MAP.put("transport-allocations", "transport.allocation");

        MODULE_MAP.put("notices", "communication.notice");
        MODULE_MAP.put("announcements", "communication.announcement");
        MODULE_MAP.put("messages", "communication.message");
        MODULE_MAP.put("notifications", "communication.notification");

        MODULE_MAP.put("clubs", "activity.club");
        MODULE_MAP.put("sports", "activity.sport");
        MODULE_MAP.put("events", "activity.event");
        MODULE_MAP.put("registrations", "activity.registration");

        MODULE_MAP.put("users", "security.user");
        MODULE_MAP.put("roles", "security.role");
        MODULE_MAP.put("permissions", "security.permission");
        MODULE_MAP.put("menus", "security.menu");
        MODULE_MAP.put("audit-logs", "security.audit");
        MODULE_MAP.put("settings", "security.setting");

        MODULE_MAP.put("reports", "reports.generate");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();

        if (path.startsWith("/api/auth/") || path.startsWith("/api/features") || path.startsWith("/api/pre-admission/")) {
            return true;
        }

        String featureKey = resolveFeatureKey(path, request.getMethod());
        if (featureKey != null && !featureService.isFeatureEnabled(featureKey)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            Map<String, Object> body = new HashMap<>();
            body.put("status", 403);
            body.put("error", "Forbidden");
            body.put("message", "This feature has been disabled by the Super Admin.");
            body.put("featureKey", featureKey);
            body.put("path", path);
            response.getWriter().write(objectMapper.writeValueAsString(body));
            return false;
        }

        return true;
    }

    private String resolveFeatureKey(String path, String method) {
        String[] parts = path.replace("/api/", "").split("/");

        if (parts.length == 0 || parts[0].isEmpty()) return null;

        String module = parts[0];

        if (parts.length >= 2) {
            String subResource = parts[0] + "-" + parts[1];
            if (MODULE_MAP.containsKey(subResource)) {
                return MODULE_MAP.get(subResource);
            }
        }

        if (MODULE_MAP.containsKey(module)) {
            return MODULE_MAP.get(module);
        }

        String normalized = module.replace("-", ".");
        return normalized;
    }
}
