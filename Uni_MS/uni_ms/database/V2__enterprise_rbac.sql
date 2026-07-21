-- Phase 2: Enterprise RBAC Schema Additions
-- This file adds new tables and enhances existing ones

-- Login History Table
CREATE TABLE IF NOT EXISTS login_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    username VARCHAR(50) NOT NULL,
    ip_address VARCHAR(45),
    user_agent TEXT,
    login_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    success BOOLEAN NOT NULL DEFAULT FALSE,
    failure_reason VARCHAR(255),
    logout_timestamp TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Refresh Tokens Table
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(500) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expiry_date DATETIME NOT NULL,
    revoked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- System Settings Table
CREATE TABLE IF NOT EXISTS system_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    setting_key VARCHAR(100) NOT NULL UNIQUE,
    setting_value TEXT,
    setting_type VARCHAR(50) DEFAULT 'STRING',
    module VARCHAR(100),
    description TEXT,
    is_public BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Dashboard Widgets Table (for per-role dashboard configuration)
CREATE TABLE IF NOT EXISTS dashboard_widgets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    widget_title VARCHAR(100) NOT NULL,
    widget_type VARCHAR(50) NOT NULL,
    widget_config TEXT,
    api_endpoint VARCHAR(255),
    order_no INT DEFAULT 0,
    column_span INT DEFAULT 1,
    visible BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Quick Actions Table (for per-role quick actions)
CREATE TABLE IF NOT EXISTS quick_actions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    icon VARCHAR(100),
    route VARCHAR(255),
    permission_code VARCHAR(100),
    order_no INT DEFAULT 0,
    visible BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Enhance menus table with module association
ALTER TABLE menus ADD COLUMN IF NOT EXISTS module VARCHAR(100) AFTER route;
ALTER TABLE menus ADD COLUMN IF NOT EXISTS menu_type VARCHAR(50) DEFAULT 'MENU' AFTER module;
ALTER TABLE menus ADD COLUMN IF NOT EXISTS css_class VARCHAR(100) AFTER menu_type;
ALTER TABLE menus ADD COLUMN IF NOT EXISTS badge VARCHAR(50) AFTER css_class;
ALTER TABLE menus ADD COLUMN IF NOT EXISTS badge_color VARCHAR(50) AFTER badge;

-- Create faculties table
CREATE TABLE IF NOT EXISTS faculties (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE,
    code VARCHAR(20) NOT NULL UNIQUE,
    name_bn VARCHAR(150),
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Add faculty_id to departments
ALTER TABLE departments ADD COLUMN IF NOT EXISTS faculty_id BIGINT AFTER head_id;
ALTER TABLE departments ADD FOREIGN KEY IF NOT EXISTS (faculty_id) REFERENCES faculties(id) ON DELETE SET NULL;

-- Enhance users table with additional fields
ALTER TABLE users ADD COLUMN IF NOT EXISTS avatar VARCHAR(500) AFTER phone;
ALTER TABLE users ADD COLUMN IF NOT EXISTS last_login_at DATETIME AFTER avatar;
ALTER TABLE users ADD COLUMN IF NOT EXISTS last_login_ip VARCHAR(45) AFTER last_login_at;
ALTER TABLE users ADD COLUMN IF NOT EXISTS password_changed_at DATETIME AFTER last_login_ip;
ALTER TABLE users ADD COLUMN IF NOT EXISTS login_attempts INT DEFAULT 0 AFTER password_changed_at;
ALTER TABLE users ADD COLUMN IF NOT EXISTS locked_until DATETIME AFTER login_attempts;

-- Seed complete menu hierarchy
-- Root menus
INSERT IGNORE INTO menus (title, icon, route, parent_id, order_no, permission_code, visible, active, module, menu_type) VALUES
('Dashboard', 'dashboard', '/dashboard', NULL, 0, 'DASHBOARD_VIEW', true, true, 'System', 'MENU'),
('Security', 'shield', '/security/users', NULL, 1, 'USER_VIEW', true, true, 'Security', 'MENU'),
('Academic', 'school', '/academic/departments', NULL, 2, 'ACADEMIC_VIEW', true, true, 'Academic', 'MENU'),
('Admissions', 'assignment', '/admissions/candidates', NULL, 3, 'ADMISSION_VIEW', true, true, 'Admissions', 'MENU'),
('Students', 'person', '/students/list', NULL, 4, 'STUDENT_VIEW', true, true, 'Students', 'MENU'),
('Administration', 'person_add', '/Administration/list', NULL, 5, 'ADMINISTRATION_VIEW', true, true, 'Administration', 'MENU'),
('HRM', 'business', '/hrm/employees', NULL, 6, 'HRM_VIEW', true, true, 'HRM', 'MENU'),
('Examination', 'quiz', '/examination/exams', NULL, 7, 'EXAM_VIEW', true, true, 'Examination', 'MENU'),
('Learning Management', 'menu_book', '/lms/assignments', NULL, 8, 'LMS_VIEW', true, true, 'LMS', 'MENU'),
('Finance', 'payments', '/finance/fee-types', NULL, 9, 'FINANCE_VIEW', true, true, 'Finance', 'MENU'),
('Library', 'local_library', '/library/books', NULL, 10, 'LIBRARY_VIEW', true, true, 'Library', 'MENU'),
('Hostel', 'apartment', '/hostel/list', NULL, 11, 'HOSTEL_VIEW', true, true, 'Hostel', 'MENU'),
('Transport', 'directions_bus', '/transport/vehicles', NULL, 12, 'TRANSPORT_VIEW', true, true, 'Transport', 'MENU'),
('Communication', 'mail', '/communication/notices', NULL, 13, 'COMMUNICATION_VIEW', true, true, 'Communication', 'MENU'),
('Activities', 'emoji_events', '/activities/clubs', NULL, 14, 'ACTIVITY_VIEW', true, true, 'Activities', 'MENU'),
('Reports', 'assessment', '/reports/templates', NULL, 15, 'REPORT_VIEW', true, true, 'Reports', 'MENU'),
('Settings', 'settings', '/settings', NULL, 99, 'SETTINGS_VIEW', true, true, 'System', 'MENU');

-- Seed permissions for all modules
INSERT IGNORE INTO permissions (name, code, module, action, description) VALUES
-- Security
('View Users', 'USER_VIEW', 'Security', 'VIEW', 'View user list'),
('Create Users', 'USER_CREATE', 'Security', 'CREATE', 'Create new users'),
('Edit Users', 'USER_EDIT', 'Security', 'EDIT', 'Edit user details'),
('Delete Users', 'USER_DELETE', 'Security', 'DELETE', 'Delete users'),
('View Roles', 'ROLE_VIEW', 'Security', 'VIEW', 'View roles'),
('Create Roles', 'ROLE_CREATE', 'Security', 'CREATE', 'Create roles'),
('Edit Roles', 'ROLE_EDIT', 'Security', 'EDIT', 'Edit roles'),
('Delete Roles', 'ROLE_DELETE', 'Security', 'DELETE', 'Delete roles'),
('View Permissions', 'PERMISSION_VIEW', 'Security', 'VIEW', 'View permissions'),
('Manage Permissions', 'PERMISSION_MANAGE', 'Security', 'MANAGE', 'Manage permissions'),
-- Dashboard
('View Dashboard', 'DASHBOARD_VIEW', 'Dashboard', 'VIEW', 'View dashboard'),
-- Academic
('View Academic', 'ACADEMIC_VIEW', 'Academic', 'VIEW', 'View academic module'),
('Manage Departments', 'DEPARTMENT_MANAGE', 'Academic', 'MANAGE', 'Manage departments'),
('Manage Courses', 'COURSE_MANAGE', 'Academic', 'MANAGE', 'Manage courses'),
('Manage Batches', 'BATCH_MANAGE', 'Academic', 'MANAGE', 'Manage batches'),
('Manage Sections', 'SECTION_MANAGE', 'Academic', 'MANAGE', 'Manage sections'),
('Manage Subjects', 'SUBJECT_MANAGE', 'Academic', 'MANAGE', 'Manage subjects'),
-- Students
('View Students', 'STUDENT_VIEW', 'Students', 'VIEW', 'View students'),
('Create Students', 'STUDENT_CREATE', 'Students', 'CREATE', 'Create students'),
('Edit Students', 'STUDENT_EDIT', 'Students', 'EDIT', 'Edit students'),
('Delete Students', 'STUDENT_DELETE', 'Students', 'DELETE', 'Delete students'),
-- Administration
('View Administration', 'ADMINISTRATION_VIEW', 'Administration', 'VIEW', 'View Administration'),
('Manage Administration', 'ADMINISTRATION_MANAGE', 'Administration', 'MANAGE', 'Manage Administration'),
-- HRM
('View HRM', 'HRM_VIEW', 'HRM', 'VIEW', 'View HRM module'),
('Manage Employees', 'EMPLOYEE_MANAGE', 'HRM', 'MANAGE', 'Manage employees'),
('Manage Payroll', 'PAYROLL_MANAGE', 'HRM', 'MANAGE', 'Manage payroll'),
('Approve Leave', 'LEAVE_APPROVE', 'HRM', 'APPROVE', 'Approve leave requests'),
-- Finance
('View Finance', 'FINANCE_VIEW', 'Finance', 'VIEW', 'View finance module'),
('Manage Fee Types', 'FEE_TYPE_MANAGE', 'Finance', 'MANAGE', 'Manage fee types'),
('Manage Invoices', 'INVOICE_MANAGE', 'Finance', 'MANAGE', 'Manage invoices'),
('Approve Payments', 'PAYMENT_APPROVE', 'Finance', 'APPROVE', 'Approve payments'),
-- Examination
('View Exams', 'EXAM_VIEW', 'Examination', 'VIEW', 'View exams'),
('Manage Exams', 'EXAM_MANAGE', 'Examination', 'MANAGE', 'Manage exams'),
('Enter Marks', 'MARKS_ENTER', 'Examination', 'CREATE', 'Enter marks'),
('Publish Results', 'RESULT_PUBLISH', 'Examination', 'APPROVE', 'Publish results'),
-- Library
('View Library', 'LIBRARY_VIEW', 'Library', 'VIEW', 'View library'),
('Manage Books', 'BOOK_MANAGE', 'Library', 'MANAGE', 'Manage books'),
('Issue Books', 'BOOK_ISSUE', 'Library', 'CREATE', 'Issue books'),
-- Settings
('View Settings', 'SETTINGS_VIEW', 'Settings', 'VIEW', 'View settings'),
('Manage Settings', 'SETTINGS_MANAGE', 'Settings', 'MANAGE', 'Manage settings'),
-- Admission
('View Admissions', 'ADMISSION_VIEW', 'Admissions', 'VIEW', 'View admissions'),
('Manage Admissions', 'ADMISSION_MANAGE', 'Admissions', 'MANAGE', 'Manage admissions'),
-- LMS
('View LMS', 'LMS_VIEW', 'LMS', 'VIEW', 'View LMS'),
('Manage Assignments', 'ASSIGNMENT_MANAGE', 'LMS', 'MANAGE', 'Manage assignments'),
-- Hostel
('View Hostel', 'HOSTEL_VIEW', 'Hostel', 'VIEW', 'View hostel'),
('Manage Hostel', 'HOSTEL_MANAGE', 'Hostel', 'MANAGE', 'Manage hostel'),
-- Transport
('View Transport', 'TRANSPORT_VIEW', 'Transport', 'VIEW', 'View transport'),
('Manage Transport', 'TRANSPORT_MANAGE', 'Transport', 'MANAGE', 'Manage transport'),
-- Communication
('View Communication', 'COMMUNICATION_VIEW', 'Communication', 'VIEW', 'View communication'),
('Manage Notices', 'NOTICE_MANAGE', 'Communication', 'MANAGE', 'Manage notices'),
-- Activities
('View Activities', 'ACTIVITY_VIEW', 'Activities', 'VIEW', 'View activities'),
('Manage Activities', 'ACTIVITY_MANAGE', 'Activities', 'MANAGE', 'Manage activities'),
-- Reports
('View Reports', 'REPORT_VIEW', 'Reports', 'VIEW', 'View reports'),
('Generate Reports', 'REPORT_GENERATE', 'Reports', 'CREATE', 'Generate reports'),
-- Audit
('View Audit Logs', 'AUDIT_VIEW', 'Security', 'VIEW', 'View audit logs');

-- Seed System Settings
INSERT IGNORE INTO system_settings (setting_key, setting_value, setting_type, module, description, is_public) VALUES
('app.name', 'UMS-ERP', 'STRING', 'System', 'Application name', true),
('app.version', '1.0.0', 'STRING', 'System', 'Application version', true),
('academic.currentSemester', '1', 'NUMBER', 'Academic', 'Current semester', true),
('academic.currentYear', '2026', 'NUMBER', 'Academic', 'Academic year', false),
('finance.currency', 'USD', 'STRING', 'Finance', 'Default currency', true),
('finance.taxRate', '0', 'NUMBER', 'Finance', 'Default tax rate', false),
('hostel.maxAllocationDays', '365', 'NUMBER', 'Hostel', 'Maximum allocation days', false),
('library.finePerDay', '1.00', 'NUMBER', 'Library', 'Fine per day for overdue books', true),
('library.maxBooksPerStudent', '5', 'NUMBER', 'Library', 'Max books per student', true),
('transport.defaultFee', '50.00', 'NUMBER', 'Transport', 'Default transport fee', false);
