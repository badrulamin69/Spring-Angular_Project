package com.badrulamin.University_Management.config;

import com.badrulamin.University_Management.entity.*;
import com.badrulamin.University_Management.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
@Order(1)
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired private PermissionRepository permissionRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private MenuRepository menuRepository;
    @Autowired private DashboardWidgetRepository dashboardWidgetRepository;
    @Autowired private SystemSettingRepository systemSettingRepository;
    @Autowired private FacultyRepository facultyRepository;
    @Autowired private DepartmentRepository departmentRepository;
    @Autowired private FeatureRepository featureRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void run(String... args) throws Exception {
        // ===== PERMISSIONS (idempotent) =====
        Map<String, Permission> perms = new LinkedHashMap<>();

        // Security
        perms.put("USER_VIEW", createPerm("View Users", "USER_VIEW", "Security", "VIEW"));
        perms.put("USER_CREATE", createPerm("Create Users", "USER_CREATE", "Security", "CREATE"));
        perms.put("USER_EDIT", createPerm("Edit Users", "USER_EDIT", "Security", "EDIT"));
        perms.put("USER_DELETE", createPerm("Delete Users", "USER_DELETE", "Security", "DELETE"));
        perms.put("ROLE_VIEW", createPerm("View Roles", "ROLE_VIEW", "Security", "VIEW"));
        perms.put("ROLE_MANAGE", createPerm("Manage Roles", "ROLE_MANAGE", "Security", "MANAGE"));
        perms.put("PERMISSION_VIEW", createPerm("View Permissions", "PERMISSION_VIEW", "Security", "VIEW"));
        perms.put("PERMISSION_MANAGE", createPerm("Manage Permissions", "PERMISSION_MANAGE", "Security", "MANAGE"));
        perms.put("AUDIT_VIEW", createPerm("View Audit Logs", "AUDIT_VIEW", "Security", "VIEW"));
        perms.put("MENU_MANAGE", createPerm("Manage Menus", "MENU_MANAGE", "Security", "MANAGE"));

        // Dashboard
        perms.put("DASHBOARD_VIEW", createPerm("View Dashboard", "DASHBOARD_VIEW", "Dashboard", "VIEW"));

        // Academic
        perms.put("ACADEMIC_VIEW", createPerm("View Academic", "ACADEMIC_VIEW", "Academic", "VIEW"));
        perms.put("ACADEMIC_MANAGE", createPerm("Manage Academic", "ACADEMIC_MANAGE", "Academic", "MANAGE"));
        perms.put("FACULTY_MANAGE", createPerm("Manage Faculties", "FACULTY_MANAGE", "Academic", "MANAGE"));
        perms.put("DEPARTMENT_MANAGE", createPerm("Manage Departments", "DEPARTMENT_MANAGE", "Academic", "MANAGE"));
        perms.put("COURSE_MANAGE", createPerm("Manage Courses", "COURSE_MANAGE", "Academic", "MANAGE"));
        perms.put("BATCH_MANAGE", createPerm("Manage Batches", "BATCH_MANAGE", "Academic", "MANAGE"));
        perms.put("SECTION_MANAGE", createPerm("Manage Sections", "SECTION_MANAGE", "Academic", "MANAGE"));
        perms.put("SUBJECT_MANAGE", createPerm("Manage Subjects", "SUBJECT_MANAGE", "Academic", "MANAGE"));
        perms.put("TEACHER_VIEW", createPerm("View Teachers", "TEACHER_VIEW", "Academic", "VIEW"));

        // Students
        perms.put("STUDENT_VIEW", createPerm("View Students", "STUDENT_VIEW", "Students", "VIEW"));
        perms.put("STUDENT_CREATE", createPerm("Create Students", "STUDENT_CREATE", "Students", "CREATE"));
        perms.put("STUDENT_EDIT", createPerm("Edit Students", "STUDENT_EDIT", "Students", "EDIT"));
        perms.put("STUDENT_DELETE", createPerm("Delete Students", "STUDENT_DELETE", "Students", "DELETE"));
        perms.put("STUDENT_MANAGE", createPerm("Manage Students", "STUDENT_MANAGE", "Students", "MANAGE"));

        // Administration
        perms.put("ADMINISTRATION_VIEW", createPerm("View Administration", "ADMINISTRATION_VIEW", "Administration", "VIEW"));
        perms.put("ADMINISTRATION_MANAGE", createPerm("Manage Administration", "ADMINISTRATION_MANAGE", "Administration", "MANAGE"));

        // Admissions
        perms.put("ADMISSION_VIEW", createPerm("View Admissions", "ADMISSION_VIEW", "Admissions", "VIEW"));
        perms.put("ADMISSION_MANAGE", createPerm("Manage Admissions", "ADMISSION_MANAGE", "Admissions", "MANAGE"));
        perms.put("PRE_ADMISSION_VIEW", createPerm("View Pre-Admissions", "PRE_ADMISSION_VIEW", "Admissions", "VIEW"));
        perms.put("PRE_ADMISSION_MANAGE", createPerm("Manage Pre-Admissions", "PRE_ADMISSION_MANAGE", "Admissions", "MANAGE"));
        perms.put("QUESTION_MANAGE", createPerm("Manage Questions", "QUESTION_MANAGE", "Admissions", "MANAGE"));
        perms.put("QUESTION_VIEW", createPerm("View Questions", "QUESTION_VIEW", "Admissions", "VIEW"));

        // HRM
        perms.put("HRM_VIEW", createPerm("View HRM", "HRM_VIEW", "HRM", "VIEW"));
        perms.put("EMPLOYEE_MANAGE", createPerm("Manage Employees", "EMPLOYEE_MANAGE", "HRM", "MANAGE"));
        perms.put("PAYROLL_MANAGE", createPerm("Manage Payroll", "PAYROLL_MANAGE", "HRM", "MANAGE"));
        perms.put("LEAVE_APPROVE", createPerm("Approve Leave", "LEAVE_APPROVE", "HRM", "APPROVE"));

        // Examination
        perms.put("EXAM_VIEW", createPerm("View Exams", "EXAM_VIEW", "Examination", "VIEW"));
        perms.put("EXAM_MANAGE", createPerm("Manage Exams", "EXAM_MANAGE", "Examination", "MANAGE"));
        perms.put("MARKS_ENTER", createPerm("Enter Marks", "MARKS_ENTER", "Examination", "CREATE"));
        perms.put("RESULT_PUBLISH", createPerm("Publish Results", "RESULT_PUBLISH", "Examination", "APPROVE"));

        // LMS
        perms.put("LMS_VIEW", createPerm("View LMS", "LMS_VIEW", "LMS", "VIEW"));
        perms.put("ASSIGNMENT_MANAGE", createPerm("Manage Assignments", "ASSIGNMENT_MANAGE", "LMS", "MANAGE"));

        // Finance
        perms.put("FINANCE_VIEW", createPerm("View Finance", "FINANCE_VIEW", "Finance", "VIEW"));
        perms.put("FEE_TYPE_MANAGE", createPerm("Manage Fee Types", "FEE_TYPE_MANAGE", "Finance", "MANAGE"));
        perms.put("INVOICE_MANAGE", createPerm("Manage Invoices", "INVOICE_MANAGE", "Finance", "MANAGE"));
        perms.put("PAYMENT_APPROVE", createPerm("Approve Payments", "PAYMENT_APPROVE", "Finance", "APPROVE"));

        // Library
        perms.put("LIBRARY_VIEW", createPerm("View Library", "LIBRARY_VIEW", "Library", "VIEW"));
        perms.put("BOOK_MANAGE", createPerm("Manage Books", "BOOK_MANAGE", "Library", "MANAGE"));
        perms.put("BOOK_ISSUE", createPerm("Issue Books", "BOOK_ISSUE", "Library", "CREATE"));

        // Hostel
        perms.put("HOSTEL_VIEW", createPerm("View Hostel", "HOSTEL_VIEW", "Hostel", "VIEW"));
        perms.put("HOSTEL_MANAGE", createPerm("Manage Hostel", "HOSTEL_MANAGE", "Hostel", "MANAGE"));

        // Transport
        perms.put("TRANSPORT_VIEW", createPerm("View Transport", "TRANSPORT_VIEW", "Transport", "VIEW"));
        perms.put("TRANSPORT_MANAGE", createPerm("Manage Transport", "TRANSPORT_MANAGE", "Transport", "MANAGE"));

        // Communication
        perms.put("COMMUNICATION_VIEW", createPerm("View Communication", "COMMUNICATION_VIEW", "Communication", "VIEW"));
        perms.put("NOTICE_MANAGE", createPerm("Manage Notices", "NOTICE_MANAGE", "Communication", "MANAGE"));

        // Activities
        perms.put("ACTIVITY_VIEW", createPerm("View Activities", "ACTIVITY_VIEW", "Activities", "VIEW"));
        perms.put("ACTIVITY_MANAGE", createPerm("Manage Activities", "ACTIVITY_MANAGE", "Activities", "MANAGE"));

        // Reports
        perms.put("REPORT_VIEW", createPerm("View Reports", "REPORT_VIEW", "Reports", "VIEW"));
        perms.put("REPORT_GENERATE", createPerm("Generate Reports", "REPORT_GENERATE", "Reports", "CREATE"));

        // Settings
        perms.put("SETTINGS_VIEW", createPerm("View Settings", "SETTINGS_VIEW", "Settings", "VIEW"));
        perms.put("SETTINGS_MANAGE", createPerm("Manage Settings", "SETTINGS_MANAGE", "Settings", "MANAGE"));

        // ===== ROLES (Hierarchy) =====
        // Level 0: Super Admin
        Set<Permission> allPerms = new HashSet<>(perms.values());
        Role superAdmin = createRole("Super Admin", "ROLE_SUPER_ADMIN", "Full system access", allPerms, 0, null);

        // Level 1: University Admin (child of Super Admin)
        Set<Permission> uniAdminPerms = new HashSet<>(perms.values());
        uniAdminPerms.remove(perms.get("SETTINGS_MANAGE"));
        Role universityAdmin = createRole("University Admin", "ROLE_UNIVERSITY_ADMIN", "University administrative access", uniAdminPerms, 1, superAdmin);

        // Level 2: Department Head (child of University Admin)
        Set<Permission> deptHeadPerms = new HashSet<>();
        deptHeadPerms.add(perms.get("DASHBOARD_VIEW"));
        deptHeadPerms.add(perms.get("ACADEMIC_VIEW"));
        deptHeadPerms.add(perms.get("ACADEMIC_MANAGE"));
        deptHeadPerms.add(perms.get("FACULTY_MANAGE"));
        deptHeadPerms.add(perms.get("DEPARTMENT_MANAGE"));
        deptHeadPerms.add(perms.get("COURSE_MANAGE"));
        deptHeadPerms.add(perms.get("SUBJECT_MANAGE"));
        deptHeadPerms.add(perms.get("BATCH_MANAGE"));
        deptHeadPerms.add(perms.get("SECTION_MANAGE"));
        deptHeadPerms.add(perms.get("STUDENT_VIEW"));
        deptHeadPerms.add(perms.get("STUDENT_CREATE"));
        deptHeadPerms.add(perms.get("STUDENT_EDIT"));
        deptHeadPerms.add(perms.get("STUDENT_MANAGE"));
        deptHeadPerms.add(perms.get("EXAM_VIEW"));
        deptHeadPerms.add(perms.get("EXAM_MANAGE"));
        deptHeadPerms.add(perms.get("MARKS_ENTER"));
        deptHeadPerms.add(perms.get("RESULT_PUBLISH"));
        deptHeadPerms.add(perms.get("LMS_VIEW"));
        deptHeadPerms.add(perms.get("ASSIGNMENT_MANAGE"));
        deptHeadPerms.add(perms.get("REPORT_VIEW"));
        deptHeadPerms.add(perms.get("REPORT_GENERATE"));
        deptHeadPerms.add(perms.get("COMMUNICATION_VIEW"));
        deptHeadPerms.add(perms.get("TEACHER_VIEW"));
        Role departmentHead = createRole("Department Head", "ROLE_DEPT_HEAD", "Department head access", deptHeadPerms, 2, universityAdmin);

        // Level 3: Faculty Member (child of Department Head)
        Set<Permission> facultyPerms = new HashSet<>();
        facultyPerms.add(perms.get("DASHBOARD_VIEW"));
        facultyPerms.add(perms.get("ACADEMIC_VIEW"));
        facultyPerms.add(perms.get("COURSE_MANAGE"));
        facultyPerms.add(perms.get("SUBJECT_MANAGE"));
        facultyPerms.add(perms.get("STUDENT_VIEW"));
        facultyPerms.add(perms.get("EXAM_VIEW"));
        facultyPerms.add(perms.get("MARKS_ENTER"));
        facultyPerms.add(perms.get("LMS_VIEW"));
        facultyPerms.add(perms.get("ASSIGNMENT_MANAGE"));
        facultyPerms.add(perms.get("REPORT_VIEW"));
        facultyPerms.add(perms.get("COMMUNICATION_VIEW"));
        facultyPerms.add(perms.get("TEACHER_VIEW"));
        Role facultyMember = createRole("Faculty Member", "ROLE_FACULTY", "Faculty member access", facultyPerms, 3, departmentHead);

        // Level 3: Advisor (child of Department Head)
        Set<Permission> advisorPerms = new HashSet<>(facultyPerms);
        advisorPerms.add(perms.get("STUDENT_CREATE"));
        advisorPerms.add(perms.get("STUDENT_EDIT"));
        advisorPerms.add(perms.get("STUDENT_DELETE"));
        advisorPerms.add(perms.get("STUDENT_MANAGE"));
        advisorPerms.add(perms.get("BATCH_MANAGE"));
        advisorPerms.add(perms.get("SECTION_MANAGE"));
        Role advisor = createRole("Advisor", "ROLE_ADVISOR", "Academic advisor access", advisorPerms, 3, departmentHead);

        // Level 2: Admission Officer (child of University Admin)
        Set<Permission> admissionPerms = new HashSet<>();
        admissionPerms.add(perms.get("DASHBOARD_VIEW"));
        admissionPerms.add(perms.get("ADMISSION_VIEW"));
        admissionPerms.add(perms.get("ADMISSION_MANAGE"));
        admissionPerms.add(perms.get("STUDENT_VIEW"));
        admissionPerms.add(perms.get("STUDENT_CREATE"));
        admissionPerms.add(perms.get("STUDENT_EDIT"));
        admissionPerms.add(perms.get("STUDENT_MANAGE"));
        admissionPerms.add(perms.get("ACADEMIC_VIEW"));
        admissionPerms.add(perms.get("REPORT_VIEW"));
        admissionPerms.add(perms.get("REPORT_GENERATE"));
        admissionPerms.add(perms.get("COMMUNICATION_VIEW"));
        admissionPerms.add(perms.get("PRE_ADMISSION_VIEW"));
        admissionPerms.add(perms.get("PRE_ADMISSION_MANAGE"));
        admissionPerms.add(perms.get("QUESTION_VIEW"));
        admissionPerms.add(perms.get("QUESTION_MANAGE"));
        Role admissionOfficer = createRole("Admission Officer", "ROLE_ADMISSION_OFFICER", "Admission management access", admissionPerms, 2, universityAdmin);

        // Level 2: Accounts Officer (child of University Admin)
        Set<Permission> accountsPerms = new HashSet<>();
        accountsPerms.add(perms.get("DASHBOARD_VIEW"));
        accountsPerms.add(perms.get("FINANCE_VIEW"));
        accountsPerms.add(perms.get("FEE_TYPE_MANAGE"));
        accountsPerms.add(perms.get("INVOICE_MANAGE"));
        accountsPerms.add(perms.get("PAYMENT_APPROVE"));
        accountsPerms.add(perms.get("STUDENT_VIEW"));
        accountsPerms.add(perms.get("REPORT_VIEW"));
        accountsPerms.add(perms.get("REPORT_GENERATE"));
        accountsPerms.add(perms.get("COMMUNICATION_VIEW"));
        Role accountsOfficer = createRole("Accounts Officer", "ROLE_ACCOUNTS_OFFICER", "Financial accounts access", accountsPerms, 2, universityAdmin);

        // Level 2: Librarian (child of University Admin)
        Set<Permission> libPerms = new HashSet<>();
        libPerms.add(perms.get("DASHBOARD_VIEW"));
        libPerms.add(perms.get("LIBRARY_VIEW"));
        libPerms.add(perms.get("BOOK_MANAGE"));
        libPerms.add(perms.get("BOOK_ISSUE"));
        libPerms.add(perms.get("STUDENT_VIEW"));
        libPerms.add(perms.get("COMMUNICATION_VIEW"));
        libPerms.add(perms.get("REPORT_VIEW"));
        Role librarian = createRole("Librarian", "ROLE_LIBRARIAN", "Library management access", libPerms, 2, universityAdmin);

        // Level 2: Hall Provost (child of University Admin)
        Set<Permission> hallPerms = new HashSet<>();
        hallPerms.add(perms.get("DASHBOARD_VIEW"));
        hallPerms.add(perms.get("HOSTEL_VIEW"));
        hallPerms.add(perms.get("HOSTEL_MANAGE"));
        hallPerms.add(perms.get("STUDENT_VIEW"));
        hallPerms.add(perms.get("COMMUNICATION_VIEW"));
        hallPerms.add(perms.get("REPORT_VIEW"));
        Role hallProvost = createRole("Hall Provost", "ROLE_HALL_PROVOST", "Hostel management access", hallPerms, 2, universityAdmin);

        // Level 2: Transport Manager (child of University Admin)
        Set<Permission> transportPerms = new HashSet<>();
        transportPerms.add(perms.get("DASHBOARD_VIEW"));
        transportPerms.add(perms.get("TRANSPORT_VIEW"));
        transportPerms.add(perms.get("TRANSPORT_MANAGE"));
        transportPerms.add(perms.get("STUDENT_VIEW"));
        transportPerms.add(perms.get("COMMUNICATION_VIEW"));
        transportPerms.add(perms.get("REPORT_VIEW"));
        Role transportManager = createRole("Transport Manager", "ROLE_TRANSPORT_MANAGER", "Transport management access", transportPerms, 2, universityAdmin);

        // Level 2: General Staff (child of University Admin)
        Set<Permission> generalStaffPerms = new HashSet<>();
        generalStaffPerms.add(perms.get("DASHBOARD_VIEW"));
        generalStaffPerms.add(perms.get("ACADEMIC_VIEW"));
        generalStaffPerms.add(perms.get("STUDENT_VIEW"));
        generalStaffPerms.add(perms.get("ADMINISTRATION_VIEW"));
        generalStaffPerms.add(perms.get("COMMUNICATION_VIEW"));
        generalStaffPerms.add(perms.get("REPORT_VIEW"));
        Role generalStaff = createRole("General Staff", "ROLE_GENERAL_STAFF", "General staff access", generalStaffPerms, 2, universityAdmin);

        // Level 2: Applicant (child of University Admin)
        Set<Permission> applicantPerms = new HashSet<>();
        applicantPerms.add(perms.get("DASHBOARD_VIEW"));
        applicantPerms.add(perms.get("ADMISSION_VIEW"));
        applicantPerms.add(perms.get("ACADEMIC_VIEW"));
        applicantPerms.add(perms.get("COMMUNICATION_VIEW"));
        Role applicant = createRole("Applicant", "ROLE_APPLICANT", "Admission applicant access", applicantPerms, 2, universityAdmin);

        // Level 2: Student (child of University Admin)
        Set<Permission> studentPerms = new HashSet<>();
        studentPerms.add(perms.get("DASHBOARD_VIEW"));
        studentPerms.add(perms.get("STUDENT_VIEW"));
        studentPerms.add(perms.get("ACADEMIC_VIEW"));
        studentPerms.add(perms.get("EXAM_VIEW"));
        studentPerms.add(perms.get("LMS_VIEW"));
        studentPerms.add(perms.get("LIBRARY_VIEW"));
        studentPerms.add(perms.get("HOSTEL_VIEW"));
        studentPerms.add(perms.get("TRANSPORT_VIEW"));
        studentPerms.add(perms.get("COMMUNICATION_VIEW"));
        studentPerms.add(perms.get("ACTIVITY_VIEW"));
        studentPerms.add(perms.get("FINANCE_VIEW"));
        studentPerms.add(perms.get("TEACHER_VIEW"));
        Role student = createRole("Student", "ROLE_STUDENT", "Student access", studentPerms, 2, universityAdmin);

        // Level 2: Registrar (child of University Admin)
        Set<Permission> registrarPerms = new HashSet<>();
        registrarPerms.add(perms.get("DASHBOARD_VIEW"));
        registrarPerms.add(perms.get("ACADEMIC_VIEW"));
        registrarPerms.add(perms.get("ACADEMIC_MANAGE"));
        registrarPerms.add(perms.get("STUDENT_VIEW"));
        registrarPerms.add(perms.get("STUDENT_CREATE"));
        registrarPerms.add(perms.get("STUDENT_EDIT"));
        registrarPerms.add(perms.get("STUDENT_MANAGE"));
        registrarPerms.add(perms.get("ADMISSION_VIEW"));
        registrarPerms.add(perms.get("ADMISSION_MANAGE"));
        registrarPerms.add(perms.get("EXAM_VIEW"));
        registrarPerms.add(perms.get("EXAM_MANAGE"));
        registrarPerms.add(perms.get("REPORT_VIEW"));
        registrarPerms.add(perms.get("REPORT_GENERATE"));
        registrarPerms.add(perms.get("COMMUNICATION_VIEW"));
        registrarPerms.add(perms.get("ADMINISTRATION_VIEW"));
        registrarPerms.add(perms.get("SETTINGS_VIEW"));
        Role registrar = createRole("Registrar", "ROLE_REGISTRAR", "Registrar access", registrarPerms, 2, universityAdmin);

        // Level 2: HR Manager (child of University Admin)
        Set<Permission> hrManagerPerms = new HashSet<>();
        hrManagerPerms.add(perms.get("DASHBOARD_VIEW"));
        hrManagerPerms.add(perms.get("HRM_VIEW"));
        hrManagerPerms.add(perms.get("EMPLOYEE_MANAGE"));
        hrManagerPerms.add(perms.get("PAYROLL_MANAGE"));
        hrManagerPerms.add(perms.get("LEAVE_APPROVE"));
        hrManagerPerms.add(perms.get("STUDENT_VIEW"));
        hrManagerPerms.add(perms.get("REPORT_VIEW"));
        hrManagerPerms.add(perms.get("REPORT_GENERATE"));
        hrManagerPerms.add(perms.get("COMMUNICATION_VIEW"));
        hrManagerPerms.add(perms.get("ADMINISTRATION_VIEW"));
        hrManagerPerms.add(perms.get("SETTINGS_VIEW"));
        Role hrManager = createRole("HR Manager", "ROLE_HR_MANAGER", "HR Manager access", hrManagerPerms, 2, universityAdmin);

        // ===== DEFAULT ADMIN USER (only one -- you manage the rest via Postman) =====
        createUser("admin", "admin@erp.com", "admin123", "System", "Administrator", "1234567890", superAdmin);

        // ===== DEMO USERS for login page =====
        createUser("registrar", "registrar@erp.com", "registrar123", "System", "Registrar", "1234567891", registrar);
        createUser("hrmanager", "hrmanager@erp.com", "hr123", "System", "HR Manager", "1234567892", hrManager);

        // ===== MENUS =====
        seedMenus(menuRepository, perms);

        // ===== DASHBOARD WIDGETS =====
        seedDashboardWidgets(superAdmin, universityAdmin, departmentHead, facultyMember, student, accountsOfficer, librarian);

        // ===== SYSTEM SETTINGS =====
        seedSystemSettings();

        // ===== FACULTIES & DEPARTMENTS =====
        seedFacultiesAndDepartments();

        // ===== FEATURES (Feature Toggle System) =====
        seedFeatures();
    }

    private Permission createPerm(String name, String code, String module, String action) {
        return permissionRepository.findByCode(code).orElseGet(() -> {
            Permission p = new Permission();
            p.setName(name);
            p.setCode(code);
            p.setModule(module);
            p.setAction(action);
            p.setDescription(name);
            return permissionRepository.save(p);
        });
    }

    private Role createRole(String name, String code, String desc, Set<Permission> permissions, int level, Role parentRole) {
        Role role = roleRepository.findByCode(code)
                .or(() -> roleRepository.findByName(name))
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setName(name);
                    r.setCode(code);
                    r.setDescription(desc);
                    r.setActive(true);
                    r.setLevel(level);
                    r.setParentRole(parentRole);
                    return r;
                });
        if (role.getCode() == null) role.setCode(code);
        if (role.getName() == null) role.setName(name);
        role.setPermissions(permissions);
        role.setLevel(level);
        role.setParentRole(parentRole);
        return roleRepository.save(role);
    }

    private void createUser(String username, String email, String password, String first, String last, String phone, Role role) {
        if (!userRepository.existsByUsername(username)) {
            User u = new User();
            u.setUsername(username);
            u.setEmail(email);
            u.setPassword(passwordEncoder.encode(password));
            u.setFirstName(first);
            u.setLastName(last);
            u.setPhone(phone);
            u.setActive(true);
            u.setEmailVerified(true);
            u.setRole(role);
            u.setDefaultRoleCode(role.getCode());
            u.addRole(role);
            userRepository.save(u);
        }
    }

    private void seedMenus(MenuRepository repo, Map<String, Permission> perms) {
        // Dashboard
        Menu dashboard = saveMenu(repo, "Dashboard", "dashboard", "/dashboard", null, 0, "DASHBOARD_VIEW", "System");

        // Security
        Menu security = saveMenu(repo, "Security", "shield", null, null, 1, "USER_VIEW", "Security");
        saveMenu(repo, "Users", null, "/security/users", security, 1, "USER_VIEW", "Security");
        saveMenu(repo, "Roles", null, "/security/roles", security, 2, "ROLE_VIEW", "Security");
        saveMenu(repo, "Permissions", null, "/security/permissions", security, 3, "PERMISSION_VIEW", "Security");
        saveMenu(repo, "Menus", null, "/security/menus", security, 4, "MENU_MANAGE", "Security");
        saveMenu(repo, "Role Permission Matrix", null, "/security/role-permission-matrix", security, 5, "PERMISSION_VIEW", "Security");
        saveMenu(repo, "User Permission Override", null, "/security/user-permission-override", security, 6, "ROLE_VIEW", "Security");
        saveMenu(repo, "Menu Permission Mapping", null, "/security/menu-permission-mapping", security, 7, "MENU_MANAGE", "Security");
        saveMenu(repo, "Login Sessions", null, "/security/login-sessions", security, 8, "ROLE_VIEW", "Security");
        saveMenu(repo, "Activity Logs", null, "/security/activity-logs", security, 9, "AUDIT_VIEW", "Security");
        saveMenu(repo, "Security Settings", null, "/security/settings", security, 10, "SETTINGS_MANAGE", "Security");
        saveMenu(repo, "Security Dashboard", null, "/security/dashboard", security, 11, "DASHBOARD_VIEW", "Security");
        saveMenu(repo, "Audit Logs", null, "/security/audit-logs", security, 12, "AUDIT_VIEW", "Security");
        saveMenu(repo, "Route Management", null, "/security/route-management", security, 13, "MENU_MANAGE", "Security");
        saveMenu(repo, "Password Policies", null, "/security/password-policies", security, 14, "SETTINGS_MANAGE", "Security");
        saveMenu(repo, "Account Lock Rules", null, "/security/account-lock-rules", security, 15, "SETTINGS_MANAGE", "Security");
        saveMenu(repo, "Two-Factor Auth", null, "/security/two-factor", security, 16, "SETTINGS_VIEW", "Security");
        saveMenu(repo, "API Tokens", null, "/security/api-tokens", security, 17, "SETTINGS_VIEW", "Security");
        saveMenu(repo, "Workflow Management", null, "/security/workflow-management", security, 18, "SETTINGS_MANAGE", "Security");

        // Academic
        Menu academic = saveMenu(repo, "Academic", "school", null, null, 2, "ACADEMIC_VIEW", "Academic");
        saveMenu(repo, "Dashboard", null, "/academic/dashboard", academic, 0, "ACADEMIC_VIEW", "Academic");
        saveMenu(repo, "University", null, "/academic/university", academic, 1, "ACADEMIC_VIEW", "Academic");
        saveMenu(repo, "Campus", null, "/academic/campus", academic, 2, "ACADEMIC_VIEW", "Academic");
        saveMenu(repo, "Faculties", null, "/academic/faculty", academic, 3, "ACADEMIC_VIEW", "Academic");
        saveMenu(repo, "Administration Divisions", null, "/academic/administration-divisions", academic, 4, "ACADEMIC_VIEW", "Academic");
        saveMenu(repo, "Departments", null, "/academic/departments", academic, 4, "DEPARTMENT_MANAGE", "Academic");
        saveMenu(repo, "Programs", null, "/academic/programs", academic, 5, "ACADEMIC_VIEW", "Academic");
        saveMenu(repo, "Academic Sessions", null, "/academic/academic-sessions", academic, 6, "ACADEMIC_VIEW", "Academic");
        saveMenu(repo, "Semesters", null, "/academic/semesters", academic, 7, "ACADEMIC_VIEW", "Academic");
        saveMenu(repo, "Batches", null, "/academic/batches", academic, 8, "BATCH_MANAGE", "Academic");
        saveMenu(repo, "Sections", null, "/academic/sections", academic, 9, "SECTION_MANAGE", "Academic");
        saveMenu(repo, "Courses", null, "/academic/courses", academic, 10, "COURSE_MANAGE", "Academic");
        saveMenu(repo, "Subjects", null, "/academic/subjects", academic, 11, "SUBJECT_MANAGE", "Academic");
        saveMenu(repo, "Curriculum", null, "/academic/curriculum", academic, 12, "ACADEMIC_VIEW", "Academic");
        saveMenu(repo, "Credit Rules", null, "/academic/credit-rules", academic, 13, "ACADEMIC_VIEW", "Academic");
        saveMenu(repo, "Prerequisites", null, "/academic/prerequisites", academic, 14, "ACADEMIC_VIEW", "Academic");
        saveMenu(repo, "Course Assignments", null, "/academic/course-assignments", academic, 15, "COURSE_MANAGE", "Academic");
        saveMenu(repo, "Academic Calendar", null, "/academic/academic-calendar", academic, 16, "ACADEMIC_VIEW", "Academic");
        saveMenu(repo, "Class Routines", null, "/academic/class-routines", academic, 17, "ACADEMIC_VIEW", "Academic");
        saveMenu(repo, "Semester Routines", null, "/academic/semester-routines", academic, 18, "ACADEMIC_VIEW", "Academic");
        saveMenu(repo, "Academic Policies", null, "/academic/academic-policies", academic, 19, "ACADEMIC_VIEW", "Academic");

        // Admissions
        Menu admissions = saveMenu(repo, "Admissions", "assignment", null, null, 3, "ADMISSION_VIEW", "Admissions");
        saveMenu(repo, "Dashboard", null, "/admissions/dashboard", admissions, 1, "ADMISSION_VIEW", "Admissions");
        saveMenu(repo, "Admission Circulars", null, "/admissions/circulars", admissions, 2, "ADMISSION_VIEW", "Admissions");
        saveMenu(repo, "Online Applications", null, "/admissions/applications", admissions, 3, "ADMISSION_MANAGE", "Admissions");
        saveMenu(repo, "Admission Test", null, "/admissions/tests", admissions, 4, "ADMISSION_VIEW", "Admissions");
        saveMenu(repo, "Merit List", null, "/admissions/merit-lists", admissions, 5, "ADMISSION_VIEW", "Admissions");
        saveMenu(repo, "Waiting List", null, "/admissions/waiting-lists", admissions, 6, "ADMISSION_VIEW", "Admissions");
        saveMenu(repo, "Interview Management", null, "/admissions/interviews", admissions, 7, "ADMISSION_MANAGE", "Admissions");
        saveMenu(repo, "Document Verification", null, "/admissions/document-verifications", admissions, 8, "ADMISSION_MANAGE", "Admissions");
        saveMenu(repo, "Admission Requirements", null, "/admissions/requirements", admissions, 9, "ADMISSION_VIEW", "Admissions");
        saveMenu(repo, "Fee Collection", null, "/admissions/fee-collection", admissions, 10, "ADMISSION_MANAGE", "Admissions");
        saveMenu(repo, "Faculties & Programs", null, "/admissions/faculties-programs", admissions, 11, "ADMISSION_VIEW", "Admissions");
        saveMenu(repo, "Enrollment", null, "/admissions/enrollments", admissions, 12, "ADMISSION_MANAGE", "Admissions");
        saveMenu(repo, "Student ID Generation", null, "/admissions/student-id-generation", admissions, 13, "ADMISSION_MANAGE", "Admissions");
        saveMenu(repo, "Admission Analytics", null, "/admissions/analytics", admissions, 14, "ADMISSION_VIEW", "Admissions");
        saveMenu(repo, "Reports", null, "/admissions/reports", admissions, 15, "ADMISSION_VIEW", "Admissions");
        saveMenu(repo, "Pre-Admission Registrations", null, "/admissions/pre-admissions", admissions, 16, "PRE_ADMISSION_VIEW", "Admissions");
        saveMenu(repo, "Test Results", null, "/admissions/test-results", admissions, 17, "PRE_ADMISSION_VIEW", "Admissions");
        saveMenu(repo, "Merit Processing", null, "/admissions/merit-processing", admissions, 18, "PRE_ADMISSION_MANAGE", "Admissions");
        saveMenu(repo, "Department Allocations", null, "/admissions/allocations", admissions, 19, "PRE_ADMISSION_VIEW", "Admissions");
        saveMenu(repo, "Question Bank", null, "/admissions/question-bank", admissions, 20, "QUESTION_MANAGE", "Admissions");

        // Students
        Menu students = saveMenu(repo, "Students", "person", null, null, 4, "STUDENT_VIEW", "Students");
        saveMenu(repo, "Student List", null, "/students/list", students, 1, "STUDENT_VIEW", "Students");
        saveMenu(repo, "Enrollments", null, "/students/enrollments", students, 2, "STUDENT_CREATE", "Students");
        saveMenu(repo, "Profiles", null, "/students/profiles", students, 3, "STUDENT_VIEW", "Students");
        saveMenu(repo, "Guardians", null, "/students/guardians", students, 4, "STUDENT_VIEW", "Students");
        saveMenu(repo, "Student Dashboard", null, "/students/dashboard", students, 5, "STUDENT_VIEW", "Students");
        saveMenu(repo, "Attendance", null, "/students/attendance", students, 6, "STUDENT_VIEW", "Students");
        saveMenu(repo, "Academic History", null, "/students/academic-history", students, 7, "STUDENT_VIEW", "Students");
        saveMenu(repo, "Course Registration", null, "/students/course-registration", students, 8, "STUDENT_VIEW", "Students");
        saveMenu(repo, "Semester Registration", null, "/students/semester-registration", students, 9, "STUDENT_VIEW", "Students");
        saveMenu(repo, "Results", null, "/students/result", students, 10, "STUDENT_VIEW", "Students");
        saveMenu(repo, "Transcripts", null, "/students/transcript", students, 11, "STUDENT_VIEW", "Students");
        saveMenu(repo, "Certificates", null, "/students/certificates", students, 12, "STUDENT_VIEW", "Students");
        saveMenu(repo, "Disciplinary Records", null, "/students/disciplinary-records", students, 13, "STUDENT_VIEW", "Students");
        saveMenu(repo, "Medical Info", null, "/students/medical-info", students, 14, "STUDENT_VIEW", "Students");
        saveMenu(repo, "Documents", null, "/students/documents", students, 15, "STUDENT_VIEW", "Students");
        saveMenu(repo, "Student Promotions", null, "/students/promotions", students, 16, "STUDENT_VIEW", "Students");
        saveMenu(repo, "Alumni", null, "/students/alumni", students, 17, "STUDENT_VIEW", "Students");

        // Administration
        Menu administration = saveMenu(repo, "Administration", "person_add", null, null, 5, "ADMINISTRATION_VIEW", "administration");
        saveMenu(repo, "Administrative Heads", null, "/administration/administrative-heads", administration, 1, "ADMINISTRATION_VIEW", "administration");
        saveMenu(repo, "Academic Heads", null, "/administration/academic-heads", administration, 2, "ADMINISTRATION_VIEW", "administration");
        saveMenu(repo, "Head of Offices", null, "/administration/head-of-offices", administration, 3, "ADMINISTRATION_VIEW", "administration");
        saveMenu(repo, "Others", null, "/administration/others", administration, 4, "ADMINISTRATION_VIEW", "administration");

        // HRM
        Menu hrm = saveMenu(repo, "HRM", "business", null, null, 6, "HRM_VIEW", "HRM");
        saveMenu(repo, "Employees", null, "/hrm/employees", hrm, 1, "EMPLOYEE_MANAGE", "HRM");
        saveMenu(repo, "Attendance", null, "/hrm/attendance", hrm, 2, "HRM_VIEW", "HRM");
        saveMenu(repo, "Leave Requests", null, "/hrm/leave-requests", hrm, 3, "LEAVE_APPROVE", "HRM");
        saveMenu(repo, "Payrolls", null, "/hrm/payrolls", hrm, 4, "PAYROLL_MANAGE", "HRM");

        // Examination
        Menu exam = saveMenu(repo, "Examination", "quiz", null, null, 7, "EXAM_VIEW", "Examination");
        saveMenu(repo, "Exams", null, "/examination/exams", exam, 1, "EXAM_MANAGE", "Examination");
        saveMenu(repo, "Schedules", null, "/examination/schedules", exam, 2, "EXAM_VIEW", "Examination");
        saveMenu(repo, "Marks", null, "/examination/marks", exam, 3, "MARKS_ENTER", "Examination");
        saveMenu(repo, "Grade Rules", null, "/examination/grade-rules", exam, 4, "EXAM_VIEW", "Examination");
        saveMenu(repo, "Results", null, "/examination/results", exam, 5, "RESULT_PUBLISH", "Examination");

        // LMS
        Menu lms = saveMenu(repo, "Learning Management", "menu_book", null, null, 8, "LMS_VIEW", "LMS");
        saveMenu(repo, "Assignments", null, "/lms/assignments", lms, 1, "ASSIGNMENT_MANAGE", "LMS");
        saveMenu(repo, "Submissions", null, "/lms/submissions", lms, 2, "LMS_VIEW", "LMS");
        saveMenu(repo, "Materials", null, "/lms/materials", lms, 3, "LMS_VIEW", "LMS");
        saveMenu(repo, "Online Classes", null, "/lms/online-classes", lms, 4, "LMS_VIEW", "LMS");

        // Finance
        Menu finance = saveMenu(repo, "Finance", "payments", null, null, 9, "FINANCE_VIEW", "Finance");
        saveMenu(repo, "Fee Types", null, "/finance/fee-types", finance, 1, "FEE_TYPE_MANAGE", "Finance");
        saveMenu(repo, "Student Fees", null, "/finance/student-fees", finance, 2, "FINANCE_VIEW", "Finance");
        saveMenu(repo, "Invoices", null, "/finance/invoices", finance, 3, "INVOICE_MANAGE", "Finance");
        saveMenu(repo, "Payments", null, "/finance/payments", finance, 4, "PAYMENT_APPROVE", "Finance");
        saveMenu(repo, "Accounts", null, "/finance/accounts", finance, 5, "FINANCE_VIEW", "Finance");
        saveMenu(repo, "Transactions", null, "/finance/transactions", finance, 6, "FINANCE_VIEW", "Finance");

        // Library
        Menu library = saveMenu(repo, "Library", "local_library", null, null, 10, "LIBRARY_VIEW", "Library");
        saveMenu(repo, "Books", null, "/library/books", library, 1, "BOOK_MANAGE", "Library");
        saveMenu(repo, "Categories", null, "/library/categories", library, 2, "LIBRARY_VIEW", "Library");
        saveMenu(repo, "Issues", null, "/library/issues", library, 3, "BOOK_ISSUE", "Library");
        saveMenu(repo, "Returns", null, "/library/returns", library, 4, "LIBRARY_VIEW", "Library");

        // Hostel
        Menu hostel = saveMenu(repo, "Hostel", "apartment", null, null, 11, "HOSTEL_VIEW", "Hostel");
        saveMenu(repo, "Hostels", null, "/hostel/list", hostel, 1, "HOSTEL_MANAGE", "Hostel");
        saveMenu(repo, "Rooms", null, "/hostel/rooms", hostel, 2, "HOSTEL_MANAGE", "Hostel");
        saveMenu(repo, "Allocations", null, "/hostel/allocations", hostel, 3, "HOSTEL_VIEW", "Hostel");

        // Transport
        Menu transport = saveMenu(repo, "Transport", "directions_bus", null, null, 12, "TRANSPORT_VIEW", "Transport");
        saveMenu(repo, "Vehicles", null, "/transport/vehicles", transport, 1, "TRANSPORT_MANAGE", "Transport");
        saveMenu(repo, "Routes", null, "/transport/routes", transport, 2, "TRANSPORT_MANAGE", "Transport");
        saveMenu(repo, "Allocations", null, "/transport/allocations", transport, 3, "TRANSPORT_VIEW", "Transport");

        // Communication
        Menu comm = saveMenu(repo, "Communication", "mail", null, null, 13, "COMMUNICATION_VIEW", "Communication");
        saveMenu(repo, "Notices", null, "/communication/notices", comm, 1, "NOTICE_MANAGE", "Communication");
        saveMenu(repo, "Announcements", null, "/communication/announcements", comm, 2, "COMMUNICATION_VIEW", "Communication");
        saveMenu(repo, "Messages", null, "/communication/messages", comm, 3, "COMMUNICATION_VIEW", "Communication");
        saveMenu(repo, "Notifications", null, "/communication/notifications", comm, 4, "COMMUNICATION_VIEW", "Communication");

        // Activities
        Menu activities = saveMenu(repo, "Activities", "emoji_events", null, null, 14, "ACTIVITY_VIEW", "Activities");
        saveMenu(repo, "Clubs", null, "/activities/clubs", activities, 1, "ACTIVITY_MANAGE", "Activities");
        saveMenu(repo, "Sports", null, "/activities/sports", activities, 2, "ACTIVITY_MANAGE", "Activities");
        saveMenu(repo, "Events", null, "/activities/events", activities, 3, "ACTIVITY_MANAGE", "Activities");
        saveMenu(repo, "Registrations", null, "/activities/registrations", activities, 4, "ACTIVITY_VIEW", "Activities");

        // Reports
        Menu reports = saveMenu(repo, "Reports", "assessment", null, null, 15, "REPORT_VIEW", "Reports");
        saveMenu(repo, "Templates", null, "/reports/templates", reports, 1, "REPORT_VIEW", "Reports");
        saveMenu(repo, "Generated Reports", null, "/reports/generated", reports, 2, "REPORT_GENERATE", "Reports");

        // Settings
        Menu settings = saveMenu(repo, "Settings", "settings", null, null, 99, "SETTINGS_VIEW", "Settings");
        saveMenu(repo, "System Settings", null, "/settings/system", settings, 1, "SETTINGS_MANAGE", "Settings");
    }

    private Menu saveMenu(MenuRepository repo, String title, String icon, String route, Menu parent, int orderNo, String permCode, String module) {
        Optional<Menu> existing = repo.findByTitleAndParent(title, parent);
        if (existing.isPresent()) return existing.get();
        Menu m = new Menu();
        m.setTitle(title);
        m.setIcon(icon);
        m.setRoute(route);
        m.setParent(parent);
        m.setOrderNo(orderNo);
        m.setPermissionCode(permCode);
        m.setModule(module);
        m.setVisible(true);
        m.setActive(true);
        return repo.save(m);
    }

    private void seedDashboardWidgets(Role superAdmin, Role universityAdmin, Role departmentHead, Role facultyMember, Role student, Role accountsOfficer, Role librarian) {
        // Super Admin
        createWidget(superAdmin, "Total Students", "card", "{\"title\":\"Total Students\",\"icon\":\"people\",\"color\":\"#4F46E5\"}", "students", 1);
        createWidget(superAdmin, "Total Administration", "card", "{\"title\":\"Total Administration\",\"icon\":\"school\",\"color\":\"#10B981\"}", "administration", 2);
        createWidget(superAdmin, "Total Employees", "card", "{\"title\":\"Total Employees\",\"icon\":\"business\",\"color\":\"#F59E0B\"}", "employees", 3);
        createWidget(superAdmin, "Total Courses", "card", "{\"title\":\"Total Courses\",\"icon\":\"menu_book\",\"color\":\"#EF4444\"}", "courses", 4);

        // University Admin
        createWidget(universityAdmin, "Total Students", "card", "{\"title\":\"Total Students\",\"icon\":\"people\",\"color\":\"#4F46E5\"}", "students", 1);
        createWidget(universityAdmin, "Total Courses", "card", "{\"title\":\"Total Courses\",\"icon\":\"menu_book\",\"color\":\"#10B981\"}", "courses", 2);
        createWidget(universityAdmin, "Active Exams", "card", "{\"title\":\"Active Exams\",\"icon\":\"quiz\",\"color\":\"#F59E0B\"}", "exams", 3);

        // Department Head
        createWidget(departmentHead, "My Department Students", "card", "{\"title\":\"My Department Students\",\"icon\":\"people\",\"color\":\"#4F46E5\"}", "students", 1);
        createWidget(departmentHead, "Active Exams", "card", "{\"title\":\"Active Exams\",\"icon\":\"quiz\",\"color\":\"#10B981\"}", "exams", 2);

        // Faculty Member
        createWidget(facultyMember, "My Courses", "card", "{\"title\":\"My Courses\",\"icon\":\"menu_book\",\"color\":\"#4F46E5\"}", "courses", 1);
        createWidget(facultyMember, "Pending Assignments", "card", "{\"title\":\"Pending Assignments\",\"icon\":\"assignment\",\"color\":\"#10B981\"}", "assignments", 2);

        createWidget(student, "My Courses", "card", "{\"title\":\"My Courses\",\"icon\":\"menu_book\",\"color\":\"#4F46E5\"}", "courses", 1);
        createWidget(student, "Upcoming Exams", "card", "{\"title\":\"Upcoming Exams\",\"icon\":\"quiz\",\"color\":\"#10B981\"}", "exams", 2);

        // Accounts Officer
        createWidget(accountsOfficer, "Total Invoices", "card", "{\"title\":\"Total Invoices\",\"icon\":\"receipt\",\"color\":\"#4F46E5\"}", "invoices", 1);

        // Librarian
        createWidget(librarian, "Total Books", "card", "{\"title\":\"Total Books\",\"icon\":\"local_library\",\"color\":\"#4F46E5\"}", "books", 1);
    }

    private void createWidget(Role role, String title, String type, String config, String dataSource, long orderNo) {
        List<DashboardWidget> existing = dashboardWidgetRepository.findByRole_IdAndVisibleTrueOrderByOrderNo(role.getId());
        boolean alreadyExists = existing.stream().anyMatch(w -> title.equals(w.getWidgetTitle()));
        if (alreadyExists) return;
        DashboardWidget w = new DashboardWidget();
        w.setRole(role);
        w.setWidgetTitle(title);
        w.setWidgetType(type);
        w.setWidgetConfig(config);
        w.setDataSource(dataSource);
        w.setOrderNo(orderNo);
        w.setColumnSpan(1);
        w.setVisible(true);
        dashboardWidgetRepository.save(w);
    }

    private void seedSystemSettings() {
        createSetting("dropdown.modules", "Security,Academic,Admissions,Students,Administration,HRM,Examination,LMS,Finance,Library,Hostel,Transport,Communication,Activities,Reports", "STRING", "dropdown", "List of available modules for dropdowns", true);
        createSetting("dropdown.actions", "VIEW,CREATE,EDIT,DELETE,MANAGE,EXPORT,IMPORT", "STRING", "dropdown", "List of available actions for dropdowns", true);
    }

    private void createSetting(String key, String value, String type, String module, String description, Boolean isPublic) {
        if (systemSettingRepository.findBySettingKey(key).isEmpty()) {
            SystemSetting s = new SystemSetting();
            s.setSettingKey(key);
            s.setSettingValue(value);
            s.setSettingType(type);
            s.setModule(module);
            s.setDescription(description);
            s.setIsPublic(isPublic);
            systemSettingRepository.save(s);
        }
    }

    private void seedFacultiesAndDepartments() {
        Faculty humanities = createFaculty("111", "Faculty of Humanities", "মানবিক বা কলা অনুষদ");
        Faculty law = createFaculty("222", "Faculty of Law", "আইন অনুষদ");
        Faculty science = createFaculty("333", "Faculty of Science", "বিজ্ঞান অনুষদ");
        Faculty business = createFaculty("444", "Faculty of Business Studies", "ব্যবসায় শিক্ষা অনুষদ");
        Faculty engineering = createFaculty("555", "Faculty of Engineering and Technology", "প্রকৌশল ও প্রযুক্তি অনুষদ");

        // Faculty of Humanities departments
        createDepartment("HUM-BAN", "Department of Bangla", "বাংলা বিভাগ", humanities);
        createDepartment("HUM-ENG", "Department of English", "ইংরেজি বিভাগ", humanities);
        createDepartment("HUM-HIS", "Department of History", "ইতিহাস বিভাগ", humanities);
        createDepartment("HUM-PHI", "Department of Philosophy", "দর্শন বিভাগ", humanities);
        createDepartment("HUM-ALS", "Department of Asian Studies", "এশিয়ান স্টাডিজ বিভাগ", humanities);

        // Faculty of Law departments
        createDepartment("LAW-LLB", "Department of Law", "আইন বিভাগ", law);

        // Faculty of Science departments
        createDepartment("SCI-MAT", "Department of Mathematics", "গণিত বিভাগ", science);
        createDepartment("SCI-PHY", "Department of Physics", "পদার্থবিজ্ঞান বিভাগ", science);
        createDepartment("SCI-CHE", "Department of Chemistry", "রসায়ন বিভাগ", science);
        createDepartment("SCI-BOT", "Department of Botany", "উদ্ভিদবিজ্ঞান বিভাগ", science);
        createDepartment("SCI-ZOO", "Department of Zoology", "প্রাণিবিজ্ঞান বিভাগ", science);
        createDepartment("SCI-STAT", "Department of Statistics", "পরিসংখ্যান বিভাগ", science);

        // Faculty of Business Studies departments
        createDepartment("BUS-ACC", "Department of Accounting", "হিসাববিজ্ঞান বিভাগ", business);
        createDepartment("BUS-MGT", "Department of Management", "ব্যবস্থাপনা বিভাগ", business);
        createDepartment("BUS-MKT", "Department of Marketing", "বিপণন বিভাগ", business);
        createDepartment("BUS-FIN", "Department of Finance", "অর্থায়ন বিভাগ", business);
        createDepartment("BUS-ECO", "Department of Economics", "অর্থনীতি বিভাগ", business);
        createDepartment("BUS-IB", "Department of International Business", "আন্তর্জাতিক ব্যবসা বিভাগ", business);
        createDepartment("BUS-ISA", "Department of Information Systems", "তথ্য ব্যবস্থা বিভাগ", business);

        // Faculty of Engineering and Technology departments
        createDepartment("ENG-CSE", "Department of Computer Science", "কম্পিউটার বিজ্ঞান বিভাগ", engineering);
        createDepartment("ENG-ECE", "Department of Electronics & Communication", "ইলেকট্রনিক্স ও যোগাযোগ বিভাগ", engineering);
        createDepartment("ENG-EEE", "Department of Electrical & Electronic Engineering", "বৈদ্যুতিক ও ইলেকট্রনিক প্রকৌশল বিভাগ", engineering);
        createDepartment("ENG-CIV", "Department of Civil Engineering", "সিভিল প্রকৌশল বিভাগ", engineering);
        createDepartment("ENG-ME", "Department of Mechanical Engineering", "মেকানিক্যাল প্রকৌশল বিভাগ", engineering);
    }

    private Faculty createFaculty(String code, String name, String nameBn) {
        return facultyRepository.findByCode(code).orElseGet(() -> {
            Faculty f = new Faculty();
            f.setCode(code);
            f.setName(name);
            f.setNameBn(nameBn);
            f.setEmail(code.toLowerCase() + "@university.edu");
            f.setEmployeeCode("EMP-" + code);
            f.setFirstName("Dean");
            f.setLastName(name);
            f.setStatus("ACTIVE");
            f.setActive(true);
            return facultyRepository.save(f);
        });
    }

    private void createDepartment(String code, String name, String nameBn, Faculty faculty) {
        Optional<Department> existing = departmentRepository.findByCode(code);
        if (existing.isPresent()) return;
        if (departmentRepository.findAll().stream().anyMatch(d -> d.getName().equals(name))) return;
        Department d = new Department();
        d.setCode(code);
        d.setName(name);
        d.setFaculty(faculty);
        departmentRepository.save(d);
    }

    private void seedFeatures() {
        int order = 0;

        // === SECURITY ===
        createFeature("security.user", "User Management", "Security", "Module", "Manage system users", ++order);
        createFeature("security.role", "Role Management", "Security", "Module", "Manage roles and assignments", ++order);
        createFeature("security.permission", "Permission Management", "Security", "Module", "Manage permissions", ++order);
        createFeature("security.menu", "Menu Management", "Security", "Module", "Manage navigation menus", ++order);
        createFeature("security.audit", "Audit Logs", "Security", "Module", "View audit trail", ++order);
        createFeature("security.setting", "Security Settings", "Security", "Module", "Configure security policies", ++order);

        // === DASHBOARD ===
        createFeature("dashboard.view", "Dashboard View", "Dashboard", "Page", "Main dashboard", ++order);
        createFeature("dashboard.analytics", "Dashboard Analytics", "Dashboard", "Widget", "Analytics widgets on dashboard", ++order);

        // === ACADEMIC ===
        createFeature("academic.module", "Academic Module", "Academic", "Module", "Full academic module access", ++order);
        createFeature("academic.faculty", "Faculty Management", "Academic", "Page", "Manage faculties", ++order);
        createFeature("academic.department", "Department Management", "Academic", "Page", "Manage departments", ++order);
        createFeature("academic.program", "Program Management", "Academic", "Page", "Manage academic programs", ++order);
        createFeature("academic.course", "Course Management", "Academic", "Page", "Manage courses", ++order);
        createFeature("academic.subject", "Subject Management", "Academic", "Page", "Manage subjects", ++order);
        createFeature("academic.semester", "Semester Management", "Academic", "Page", "Manage semesters", ++order);
        createFeature("academic.batch", "Batch Management", "Academic", "Page", "Manage student batches", ++order);
        createFeature("academic.section", "Section Management", "Academic", "Page", "Manage sections", ++order);
        createFeature("academic.session", "Academic Session", "Academic", "Page", "Manage academic sessions", ++order);
        createFeature("academic.curriculum", "Curriculum", "Academic", "Page", "Manage curriculum", ++order);
        createFeature("academic.credit", "Credit Rules", "Academic", "Page", "Manage credit rules", ++order);
        createFeature("academic.prerequisite", "Prerequisites", "Academic", "Page", "Manage course prerequisites", ++order);
        createFeature("academic.assignment", "Course Assignments", "Academic", "Page", "Assign courses to faculty", ++order);
        createFeature("academic.calendar", "Academic Calendar", "Academic", "Page", "Manage academic calendar", ++order);
        createFeature("academic.routine", "Class Routines", "Academic", "Page", "Manage class routines", ++order);
        createFeature("academic.policy", "Academic Policies", "Academic", "Page", "Manage academic policies", ++order);

        // === ADMISSIONS ===
        createFeature("admission.module", "Admission Module", "Admissions", "Module", "Full admissions module", ++order);
        createFeature("admission.dashboard", "Admission Dashboard", "Admissions", "Page", "Admission overview dashboard", ++order);
        createFeature("admission.session", "Admission Sessions", "Admissions", "Page", "Manage admission sessions", ++order);
        createFeature("admission.circular", "Admission Circulars", "Admissions", "Page", "Manage admission circulars", ++order);
        createFeature("admission.application", "Online Applications", "Admissions", "Page", "View and process applications", ++order);
        createFeature("admission.test", "Admission Test", "Admissions", "Page", "Manage admission tests", ++order);
        createFeature("admission.merit", "Merit List", "Admissions", "Page", "Generate merit lists", ++order);
        createFeature("admission.waiting", "Waiting List", "Admissions", "Page", "Manage waiting lists", ++order);
        createFeature("admission.interview", "Interview Management", "Admissions", "Page", "Manage admission interviews", ++order);
        createFeature("admission.document", "Document Verification", "Admissions", "Page", "Verify admission documents", ++order);
        createFeature("admission.requirement", "Admission Requirements", "Admissions", "Page", "Manage admission requirements", ++order);
        createFeature("admission.fee", "Admission Fee Collection", "Admissions", "Page", "Collect admission fees", ++order);
        createFeature("admission.enrollment", "Enrollment", "Admissions", "Page", "Student enrollment", ++order);
        createFeature("admission.candidate", "Applicants", "Admissions", "Page", "View applicants", ++order);
        createFeature("admission.offer", "Offer Letters", "Admissions", "Page", "Generate offer letters", ++order);

        // === STUDENTS ===
        createFeature("student.module", "Student Module", "Students", "Module", "Full student module", ++order);
        createFeature("student.view", "Student List", "Students", "Page", "View student list", ++order);
        createFeature("student.profile", "Student Profiles", "Students", "Page", "View student profiles", ++order);
        createFeature("student.guardian", "Guardian Management", "Students", "Page", "Manage student guardians", ++order);
        createFeature("student.attendance", "Student Attendance", "Students", "Page", "Track attendance", ++order);
        createFeature("student.result", "Student Results", "Students", "Page", "View student results", ++order);
        createFeature("student.transcript", "Transcripts", "Students", "Page", "Generate transcripts", ++order);
        createFeature("student.certificate", "Certificates", "Students", "Page", "Generate certificates", ++order);
        createFeature("student.document", "Student Documents", "Students", "Page", "Manage student documents", ++order);
        createFeature("student.enrollment", "Student Enrollment", "Students", "Page", "Enroll students", ++order);
        createFeature("student.promotion", "Student Promotion", "Students", "Page", "Promote students", ++order);
        createFeature("student.create", "Student Create", "Students", "CRUD", "Create new students", ++order);
        createFeature("student.update", "Student Update", "Students", "CRUD", "Update student records", ++order);
        createFeature("student.delete", "Student Delete", "Students", "CRUD", "Delete student records", ++order);
        createFeature("student.export", "Student Export", "Students", "CRUD", "Export student data", ++order);

        // === ADMINISTRATION ===
        createFeature("administration.module", "Administration Module", "Administration", "Module", "Full administration module", ++order);
        createFeature("administration.heads", "Administrative Heads", "Administration", "Page", "Manage admin heads", ++order);
        createFeature("administration.academic-heads", "Academic Heads", "Administration", "Page", "Manage academic heads", ++order);
        createFeature("administration.office-heads", "Head of Offices", "Administration", "Page", "Manage office heads", ++order);

        // === HRM ===
        createFeature("hrm.module", "HRM Module", "HRM", "Module", "Full HRM module", ++order);
        createFeature("hrm.employee", "Employee Management", "HRM", "Page", "Manage employees", ++order);
        createFeature("hrm.attendance", "Employee Attendance", "HRM", "Page", "Track employee attendance", ++order);
        createFeature("hrm.leave", "Leave Requests", "HRM", "Page", "Manage leave requests", ++order);
        createFeature("hrm.payroll", "Payroll", "HRM", "Page", "Manage payroll", ++order);

        // === EXAMINATION ===
        createFeature("exam.module", "Examination Module", "Examination", "Module", "Full examination module", ++order);
        createFeature("exam.exam", "Exam Management", "Examination", "Page", "Manage exams", ++order);
        createFeature("exam.schedule", "Exam Schedules", "Examination", "Page", "Manage exam schedules", ++order);
        createFeature("exam.marks", "Marks Entry", "Examination", "Page", "Enter student marks", ++order);
        createFeature("exam.grade", "Grade Rules", "Examination", "Page", "Manage grade rules", ++order);
        createFeature("exam.result", "Exam Results", "Examination", "Page", "Publish exam results", ++order);
        createFeature("exam.result.publish", "Result Publishing", "Examination", "Workflow", "Publish results workflow", ++order);
        createFeature("exam.cgpa", "CGPA Calculation", "Examination", "Workflow", "Calculate CGPA", ++order);

        // === LMS ===
        createFeature("lms.module", "LMS Module", "LMS", "Module", "Full LMS module", ++order);
        createFeature("lms.assignment", "Assignments", "LMS", "Page", "Manage assignments", ++order);
        createFeature("lms.submission", "Submissions", "LMS", "Page", "View submissions", ++order);
        createFeature("lms.material", "Materials", "LMS", "Page", "Manage learning materials", ++order);
        createFeature("lms.online", "Online Classes", "LMS", "Page", "Manage online classes", ++order);

        // === FINANCE ===
        createFeature("finance.module", "Finance Module", "Finance", "Module", "Full finance module", ++order);
        createFeature("finance.feetype", "Fee Types", "Finance", "Page", "Manage fee types", ++order);
        createFeature("finance.studentfee", "Student Fees", "Finance", "Page", "Manage student fees", ++order);
        createFeature("finance.invoice", "Invoices", "Finance", "Page", "Manage invoices", ++order);
        createFeature("finance.payment", "Payments", "Finance", "Page", "Process payments", ++order);
        createFeature("finance.payment.gateway", "Payment Gateway", "Finance", "Service", "Payment gateway integration", ++order);
        createFeature("finance.account", "Accounts", "Finance", "Page", "Manage accounts", ++order);
        createFeature("finance.transaction", "Transactions", "Finance", "Page", "View transactions", ++order);

        // === LIBRARY ===
        createFeature("library.module", "Library Module", "Library", "Module", "Full library module", ++order);
        createFeature("library.book", "Book Management", "Library", "Page", "Manage books", ++order);
        createFeature("library.category", "Book Categories", "Library", "Page", "Manage book categories", ++order);
        createFeature("library.issue", "Book Issue", "Library", "Page", "Issue books", ++order);
        createFeature("library.return", "Book Return", "Library", "Page", "Return books", ++order);

        // === HOSTEL ===
        createFeature("hostel.module", "Hostel Module", "Hostel", "Module", "Full hostel module", ++order);
        createFeature("hostel.hostel", "Hostel Management", "Hostel", "Page", "Manage hostels", ++order);
        createFeature("hostel.room", "Room Management", "Hostel", "Page", "Manage rooms", ++order);
        createFeature("hostel.allocation", "Hostel Allocation", "Hostel", "Page", "Allocate hostels", ++order);

        // === TRANSPORT ===
        createFeature("transport.module", "Transport Module", "Transport", "Module", "Full transport module", ++order);
        createFeature("transport.vehicle", "Vehicle Management", "Transport", "Page", "Manage vehicles", ++order);
        createFeature("transport.route", "Route Management", "Transport", "Page", "Manage routes", ++order);
        createFeature("transport.allocation", "Transport Allocation", "Transport", "Page", "Allocate transport", ++order);

        // === COMMUNICATION ===
        createFeature("communication.module", "Communication Module", "Communication", "Module", "Full communication module", ++order);
        createFeature("communication.notice", "Notices", "Communication", "Page", "Manage notices", ++order);
        createFeature("communication.announcement", "Announcements", "Communication", "Page", "Manage announcements", ++order);
        createFeature("communication.message", "Messages", "Communication", "Page", "Internal messaging", ++order);
        createFeature("communication.notification", "Notifications", "Communication", "Page", "System notifications", ++order);
        createFeature("notification.email", "Email Service", "Communication", "Service", "Email notification service", ++order);
        createFeature("notification.sms", "SMS Service", "Communication", "Service", "SMS notification service", ++order);

        // === ACTIVITIES ===
        createFeature("activity.module", "Activities Module", "Activities", "Module", "Full activities module", ++order);
        createFeature("activity.club", "Clubs", "Activities", "Page", "Manage clubs", ++order);
        createFeature("activity.sport", "Sports", "Activities", "Page", "Manage sports", ++order);
        createFeature("activity.event", "Events", "Activities", "Page", "Manage events", ++order);
        createFeature("activity.registration", "Activity Registrations", "Activities", "Page", "Manage registrations", ++order);

        // === REPORTS ===
        createFeature("reports.module", "Reports Module", "Reports", "Module", "Full reports module", ++order);
        createFeature("reports.generate", "Report Generation", "Reports", "Page", "Generate reports", ++order);

        // === CROSS-CUTTING WORKFLOWS ===
        createFeature("workflow.admission", "Admission Workflow", "Workflow", "Workflow", "Complete admission workflow", ++order);
        createFeature("workflow.registration", "Course Registration", "Workflow", "Workflow", "Course registration workflow", ++order);
        createFeature("workflow.exam", "Exam Publication", "Workflow", "Workflow", "Exam publication workflow", ++order);
        createFeature("workflow.fee", "Fee Collection", "Workflow", "Workflow", "Fee collection workflow", ++order);
        createFeature("workflow.library", "Library Borrow", "Workflow", "Workflow", "Library borrow workflow", ++order);
        createFeature("workflow.transport", "Transport Allocation", "Workflow", "Workflow", "Transport allocation workflow", ++order);
        createFeature("workflow.certificate", "Certificate Generation", "Workflow", "Workflow", "Certificate generation workflow", ++order);

        // === CERTIFICATES & TRANSCRIPTS ===
        createFeature("certificate.generate", "Certificate Generation", "Certificate", "Workflow", "Generate certificates", ++order);
        createFeature("transcript.download", "Transcript Download", "Certificate", "Workflow", "Download transcripts", ++order);
        createFeature("student.id", "Student ID Generation", "Certificate", "Workflow", "Generate student IDs", ++order);
    }

    private void createFeature(String key, String name, String module, String category, String description, int sortOrder) {
        if (featureRepository.findByFeatureKey(key).isEmpty()) {
            Feature f = new Feature();
            f.setFeatureKey(key);
            f.setFeatureName(name);
            f.setModuleName(module);
            f.setCategory(category);
            f.setDescription(description);
            f.setIsEnabled(true);
            f.setCreatedBy("system");
            f.setUpdatedBy("system");
            f.setSortOrder(sortOrder);
            featureRepository.save(f);
        }
    }
}
