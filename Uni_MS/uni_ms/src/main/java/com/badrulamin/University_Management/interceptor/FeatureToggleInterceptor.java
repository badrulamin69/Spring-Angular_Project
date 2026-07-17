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

        Map<String, String> moduleMap = new HashMap<>();
        moduleMap.put("admission-circulars", "admission.circular");
        moduleMap.put("admission-applications", "admission.application");
        moduleMap.put("admission-tests", "admission.test");
        moduleMap.put("admission-merit-lists", "admission.merit");
        moduleMap.put("admission-waiting-lists", "admission.waiting");
        moduleMap.put("admission-interviews", "admission.interview");
        moduleMap.put("document-verifications", "admission.document");
        moduleMap.put("admission-requirements", "admission.requirement");
        moduleMap.put("admission-fee-collection", "admission.fee");
        moduleMap.put("admission-enrollments", "admission.enrollment");
        moduleMap.put("admission-candidates", "admission.candidate");
        moduleMap.put("admission-offer-letters", "admission.offer");
        moduleMap.put("admission-sessions", "admission.session");

        moduleMap.put("faculties", "academic.faculty");
        moduleMap.put("departments", "academic.department");
        moduleMap.put("programs", "academic.program");
        moduleMap.put("courses", "academic.course");
        moduleMap.put("subjects", "academic.subject");
        moduleMap.put("semesters", "academic.semester");
        moduleMap.put("batches", "academic.batch");
        moduleMap.put("sections", "academic.section");
        moduleMap.put("academic-sessions", "academic.session");
        moduleMap.put("curriculum", "academic.curriculum");
        moduleMap.put("credit-rules", "academic.credit");
        moduleMap.put("prerequisites", "academic.prerequisite");
        moduleMap.put("course-assignments", "academic.assignment");
        moduleMap.put("academic-calendar", "academic.calendar");
        moduleMap.put("class-routines", "academic.routine");
        moduleMap.put("semester-routines", "academic.routine");
        moduleMap.put("academic-policies", "academic.policy");

        moduleMap.put("students", "student.view");
        moduleMap.put("student-enrollments", "student.enrollment");
        moduleMap.put("student-profiles", "student.profile");
        moduleMap.put("student-guardians", "student.guardian");
        moduleMap.put("student-attendance", "student.attendance");
        moduleMap.put("student-results", "student.result");
        moduleMap.put("student-transcripts", "student.transcript");
        moduleMap.put("student-certificates", "student.certificate");
        moduleMap.put("student-documents", "student.document");

        moduleMap.put("employees", "hrm.employee");
        moduleMap.put("attendance", "hrm.attendance");
        moduleMap.put("leave-requests", "hrm.leave");
        moduleMap.put("payrolls", "hrm.payroll");

        moduleMap.put("exams", "exam.exam");
        moduleMap.put("schedules", "exam.schedule");
        moduleMap.put("marks", "exam.marks");
        moduleMap.put("grade-rules", "exam.grade");
        moduleMap.put("exam-results", "exam.result");

        moduleMap.put("assignments", "lms.assignment");
        moduleMap.put("submissions", "lms.submission");
        moduleMap.put("materials", "lms.material");
        moduleMap.put("online-classes", "lms.online");

        moduleMap.put("fee-types", "finance.feetype");
        moduleMap.put("student-fees", "finance.studentfee");
        moduleMap.put("invoices", "finance.invoice");
        moduleMap.put("payments", "finance.payment");
        moduleMap.put("accounts", "finance.account");
        moduleMap.put("transactions", "finance.transaction");

        moduleMap.put("books", "library.book");
        moduleMap.put("categories", "library.category");
        moduleMap.put("issues", "library.issue");
        moduleMap.put("library-returns", "library.return");

        moduleMap.put("hostels", "hostel.hostel");
        moduleMap.put("rooms", "hostel.room");
        moduleMap.put("hostel-allocations", "hostel.allocation");

        moduleMap.put("vehicles", "transport.vehicle");
        moduleMap.put("routes", "transport.route");
        moduleMap.put("transport-allocations", "transport.allocation");

        moduleMap.put("notices", "communication.notice");
        moduleMap.put("announcements", "communication.announcement");
        moduleMap.put("messages", "communication.message");
        moduleMap.put("notifications", "communication.notification");

        moduleMap.put("clubs", "activity.club");
        moduleMap.put("sports", "activity.sport");
        moduleMap.put("events", "activity.event");
        moduleMap.put("registrations", "activity.registration");

        moduleMap.put("users", "security.user");
        moduleMap.put("roles", "security.role");
        moduleMap.put("permissions", "security.permission");
        moduleMap.put("menus", "security.menu");
        moduleMap.put("audit-logs", "security.audit");
        moduleMap.put("settings", "security.setting");

        moduleMap.put("reports", "reports.generate");

        if (parts.length >= 2) {
            String subResource = parts[0] + "-" + parts[1];
            if (moduleMap.containsKey(subResource)) {
                return moduleMap.get(subResource);
            }
        }

        if (moduleMap.containsKey(module)) {
            return moduleMap.get(module);
        }

        String normalized = module.replace("-", ".");
        return normalized;
    }
}
