-- ============================================================
-- Phase 3: Add Missing Tables and Alter Existing Tables
-- Migration V3
-- ============================================================

-- ============================================================
-- 1. ALTER EXISTING TABLES
-- ============================================================

-- roles: add code and is_active
ALTER TABLE roles ADD COLUMN IF NOT EXISTS code VARCHAR(50) AFTER name;
ALTER TABLE roles ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE AFTER description;
UPDATE roles SET code = UPPER(name) WHERE code IS NULL;
ALTER TABLE roles ADD UNIQUE INDEX IF NOT EXISTS uk_roles_code (code);

-- permissions: add code, module, action
ALTER TABLE permissions ADD COLUMN IF NOT EXISTS code VARCHAR(100) AFTER name;
ALTER TABLE permissions ADD COLUMN IF NOT EXISTS module VARCHAR(50) AFTER code;
ALTER TABLE permissions ADD COLUMN IF NOT EXISTS action VARCHAR(50) AFTER module;
UPDATE permissions SET code = UPPER(REPLACE(name, ' ', '_')) WHERE code IS NULL;
ALTER TABLE permissions ADD UNIQUE INDEX IF NOT EXISTS uk_permissions_code (code);

-- users: add email verification and password reset columns
ALTER TABLE users ADD COLUMN IF NOT EXISTS email_verified BOOLEAN DEFAULT FALSE AFTER is_active;
ALTER TABLE users ADD COLUMN IF NOT EXISTS email_verification_token VARCHAR(255) AFTER email_verified;
ALTER TABLE users ADD COLUMN IF NOT EXISTS password_reset_token VARCHAR(255) AFTER email_verification_token;
ALTER TABLE users ADD COLUMN IF NOT EXISTS password_reset_token_expiry TIMESTAMP NULL AFTER password_reset_token;

-- menus: add visible and active columns
ALTER TABLE menus ADD COLUMN IF NOT EXISTS visible BOOLEAN DEFAULT TRUE AFTER permission_code;
ALTER TABLE menus ADD COLUMN IF NOT EXISTS active BOOLEAN DEFAULT TRUE AFTER visible;

-- departments: add FK to teachers
ALTER TABLE departments ADD FOREIGN KEY IF NOT EXISTS (head_id) REFERENCES teachers(id) ON DELETE SET NULL;

-- batches: add academic_session_id
ALTER TABLE batches ADD COLUMN IF NOT EXISTS academic_session_id BIGINT AFTER course_id;
ALTER TABLE batches ADD FOREIGN KEY IF NOT EXISTS (academic_session_id) REFERENCES academic_sessions(id) ON DELETE SET NULL;

-- ============================================================
-- 2. MISSING ACADEMIC TABLES
-- ============================================================

-- academic_sessions
CREATE TABLE IF NOT EXISTS academic_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    status VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_session_name (name),
    UNIQUE KEY uk_session_code (code)
);

-- campuses
CREATE TABLE IF NOT EXISTS campuses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    code VARCHAR(20) NOT NULL,
    address TEXT,
    phone VARCHAR(20),
    email VARCHAR(100),
    campus_type VARCHAR(50),
    latitude DOUBLE,
    longitude DOUBLE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_campus_name (name),
    UNIQUE KEY uk_campus_code (code),
    UNIQUE KEY uk_campus_email (email)
);

-- faculties_name
CREATE TABLE IF NOT EXISTS faculties_name (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    faculty_name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_faculty_name (faculty_name)
);

-- faculty_divisions
CREATE TABLE IF NOT EXISTS faculty_divisions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(20) NOT NULL,
    description TEXT,
    dean_name VARCHAR(100),
    campus_id BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_division_name (name),
    UNIQUE KEY uk_division_code (code),
    FOREIGN KEY (campus_id) REFERENCES campuses(id) ON DELETE SET NULL
);

-- faculty_attendance
CREATE TABLE IF NOT EXISTS faculty_attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    faculty_id BIGINT NOT NULL,
    attendance_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PRESENT',
    check_in TIME,
    check_out TIME,
    remarks TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (faculty_id) REFERENCES faculties(id) ON DELETE CASCADE,
    INDEX idx_faculty_attendance_date (attendance_date)
);

-- faculty_assignments (maps to FacultyAssignment entity)
CREATE TABLE IF NOT EXISTS faculty_assignments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    faculty_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    section_id BIGINT,
    assignment_type VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (faculty_id) REFERENCES faculties(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
    FOREIGN KEY (section_id) REFERENCES sections(id) ON DELETE SET NULL
);

-- subject_offerings
CREATE TABLE IF NOT EXISTS subject_offerings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject_id BIGINT NOT NULL,
    semester_id BIGINT NOT NULL,
    batch_id BIGINT,
    section_id BIGINT,
    faculty_id BIGINT,
    day_of_week VARCHAR(10),
    start_time VARCHAR(5),
    end_time VARCHAR(5),
    room_number VARCHAR(50),
    max_seats INT NOT NULL DEFAULT 40,
    enrolled_count INT NOT NULL DEFAULT 0,
    waitlist_count INT NOT NULL DEFAULT 0,
    max_waitlist INT DEFAULT 10,
    is_active BOOLEAN DEFAULT TRUE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    remarks VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
    FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE CASCADE,
    FOREIGN KEY (batch_id) REFERENCES batches(id) ON DELETE SET NULL,
    FOREIGN KEY (section_id) REFERENCES sections(id) ON DELETE SET NULL,
    FOREIGN KEY (faculty_id) REFERENCES faculties(id) ON DELETE SET NULL,
    INDEX idx_subject_offerings_semester (semester_id)
);

-- prerequisites
CREATE TABLE IF NOT EXISTS prerequisites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject_id BIGINT NOT NULL,
    prerequisite_subject_id BIGINT NOT NULL,
    min_grade VARCHAR(2) DEFAULT 'D',
    is_mandatory BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
    FOREIGN KEY (prerequisite_subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
    UNIQUE KEY uk_prerequisite_pair (subject_id, prerequisite_subject_id)
);

-- semester_routines
CREATE TABLE IF NOT EXISTS semester_routines (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    semester_id BIGINT NOT NULL,
    program_id BIGINT NOT NULL,
    batch_id BIGINT NOT NULL,
    description TEXT,
    total_weeks INT DEFAULT 16,
    midterm_week INT DEFAULT 8,
    final_exam_week INT DEFAULT 16,
    start_date DATE,
    end_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE CASCADE,
    FOREIGN KEY (program_id) REFERENCES programs(id) ON DELETE CASCADE,
    FOREIGN KEY (batch_id) REFERENCES batches(id) ON DELETE CASCADE,
    INDEX idx_semester_routines_semester (semester_id)
);

-- time_slots
CREATE TABLE IF NOT EXISTS time_slots (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(20) NOT NULL,
    start_time VARCHAR(5) NOT NULL,
    end_time VARCHAR(5) NOT NULL,
    slot_type VARCHAR(20) NOT NULL DEFAULT 'REGULAR',
    duration_minutes INT NOT NULL,
    sort_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    remarks TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_timeslot_name (name),
    UNIQUE KEY uk_timeslot_code (code)
);

-- ============================================================
-- 3. MISSING ADMISSION TABLES
-- ============================================================

-- pre_admission_registration
CREATE TABLE IF NOT EXISTS pre_admission_registration (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    registration_number VARCHAR(50) NOT NULL,
    tracking_number VARCHAR(50),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    date_of_birth DATE NOT NULL,
    gender VARCHAR(20),
    blood_group VARCHAR(10),
    address TEXT,
    photo_url TEXT,
    signature_url TEXT,
    father_name VARCHAR(100),
    mother_name VARCHAR(100),
    guardian_phone VARCHAR(20),
    ssc_gpa DOUBLE,
    ssc_year INT,
    ssc_board VARCHAR(100),
    hsc_gpa DOUBLE,
    hsc_year INT,
    hsc_board VARCHAR(100),
    program_preference1 VARCHAR(100),
    program_preference2 VARCHAR(100),
    program_preference3 VARCHAR(100),
    status VARCHAR(50) DEFAULT 'DRAFT',
    remarks VARCHAR(2000),
    is_email_verified BOOLEAN DEFAULT FALSE,
    session_id BIGINT,
    circular_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_pre_reg_number (registration_number),
    UNIQUE KEY uk_pre_reg_tracking (tracking_number),
    UNIQUE KEY uk_pre_reg_email (email),
    FOREIGN KEY (session_id) REFERENCES academic_sessions(id) ON DELETE SET NULL,
    FOREIGN KEY (circular_id) REFERENCES admission_circular(id) ON DELETE SET NULL,
    INDEX idx_pre_reg_status (status),
    INDEX idx_pre_reg_email (email)
);

-- admission_circular
CREATE TABLE IF NOT EXISTS admission_circular (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    eligibility VARCHAR(5000),
    required_documents VARCHAR(1000),
    admission_process VARCHAR(1000),
    publish_date DATE NOT NULL,
    valid_until DATE,
    status VARCHAR(20) NOT NULL,
    attachment_url VARCHAR(500),
    is_published BOOLEAN,
    session_id BIGINT,
    program_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES academic_sessions(id) ON DELETE SET NULL,
    FOREIGN KEY (program_id) REFERENCES programs(id) ON DELETE SET NULL,
    INDEX idx_circular_status (status)
);

-- admission_test_question
CREATE TABLE IF NOT EXISTS admission_test_question (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    test_id BIGINT NOT NULL,
    question_text TEXT NOT NULL,
    option_a VARCHAR(500) NOT NULL,
    option_b VARCHAR(500) NOT NULL,
    option_c VARCHAR(500) NOT NULL,
    option_d VARCHAR(500) NOT NULL,
    option_e VARCHAR(500),
    correct_option VARCHAR(1) NOT NULL,
    marks DOUBLE NOT NULL DEFAULT 1.0,
    negative_marks DOUBLE DEFAULT 0.0,
    subject VARCHAR(100),
    difficulty VARCHAR(20) DEFAULT 'MEDIUM',
    question_type VARCHAR(20) DEFAULT 'MCQ',
    explanation TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (test_id) REFERENCES admission_tests(id) ON DELETE CASCADE,
    INDEX idx_test_question_test (test_id)
);

-- admission_test_attempt
CREATE TABLE IF NOT EXISTS admission_test_attempt (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    registration_id BIGINT NOT NULL,
    test_id BIGINT NOT NULL,
    answers TEXT,
    total_questions INT,
    correct_answers INT,
    score DOUBLE,
    max_score DOUBLE,
    percentage DOUBLE,
    time_taken_seconds INT,
    started_at TIMESTAMP NULL,
    submitted_at TIMESTAMP NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (registration_id) REFERENCES pre_admission_registration(id) ON DELETE CASCADE,
    FOREIGN KEY (test_id) REFERENCES admission_tests(id) ON DELETE CASCADE,
    INDEX idx_test_attempt_registration (registration_id),
    INDEX idx_test_attempt_test (test_id)
);

-- admission_test_result
CREATE TABLE IF NOT EXISTS admission_test_result (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    registration_id BIGINT NOT NULL,
    test_id BIGINT,
    written_marks DOUBLE,
    mcq_marks DOUBLE,
    viva_marks DOUBLE,
    written_max DOUBLE DEFAULT 100.0,
    mcq_max DOUBLE DEFAULT 100.0,
    viva_max DOUBLE DEFAULT 50.0,
    total_weighted_score DOUBLE,
    status VARCHAR(50) DEFAULT 'PENDING',
    remarks VARCHAR(1000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (registration_id) REFERENCES pre_admission_registration(id) ON DELETE CASCADE,
    FOREIGN KEY (test_id) REFERENCES admission_tests(id) ON DELETE SET NULL,
    INDEX idx_test_result_registration (registration_id)
);

-- admission_waiting_list
CREATE TABLE IF NOT EXISTS admission_waiting_list (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(2000),
    academic_year VARCHAR(20),
    session_id BIGINT,
    faculty_id BIGINT,
    department_id BIGINT,
    program_id BIGINT,
    shift VARCHAR(20),
    test_id BIGINT,
    status VARCHAR(30) DEFAULT 'DRAFT',
    total_slots INT,
    total_applicants INT,
    cutoff_score DOUBLE,
    published_at TIMESTAMP NULL,
    version BIGINT DEFAULT 0,
    remarks VARCHAR(1000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES academic_sessions(id) ON DELETE SET NULL,
    FOREIGN KEY (faculty_id) REFERENCES faculties(id) ON DELETE SET NULL,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL,
    FOREIGN KEY (program_id) REFERENCES programs(id) ON DELETE SET NULL,
    FOREIGN KEY (test_id) REFERENCES admission_tests(id) ON DELETE SET NULL,
    INDEX idx_waiting_list_status (status)
);

-- admission_waiting_list_entry
CREATE TABLE IF NOT EXISTS admission_waiting_list_entry (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    waiting_list_id BIGINT NOT NULL,
    registration_id BIGINT,
    waiting_rank INT NOT NULL,
    roll_number VARCHAR(30),
    application_number VARCHAR(50),
    applicant_name VARCHAR(200),
    score DOUBLE,
    test_marks DOUBLE,
    total_weighted_score DOUBLE,
    status VARCHAR(50) DEFAULT 'WAITING',
    is_promoted BOOLEAN DEFAULT FALSE,
    is_offered BOOLEAN DEFAULT FALSE,
    remarks VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (waiting_list_id) REFERENCES admission_waiting_list(id) ON DELETE CASCADE,
    FOREIGN KEY (registration_id) REFERENCES pre_admission_registration(id) ON DELETE SET NULL,
    INDEX idx_waiting_entry_list (waiting_list_id),
    INDEX idx_waiting_entry_registration (registration_id)
);

-- admit_cards
CREATE TABLE IF NOT EXISTS admit_cards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    registration_id BIGINT NOT NULL,
    test_id BIGINT NOT NULL,
    admit_card_number VARCHAR(50) NOT NULL,
    roll_number VARCHAR(30) NOT NULL,
    seat_number VARCHAR(30),
    center_name VARCHAR(200),
    building_name VARCHAR(200),
    room_name VARCHAR(100),
    qr_code TEXT,
    issued_at TIMESTAMP NULL,
    status VARCHAR(30) DEFAULT 'GENERATED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_admit_card_number (admit_card_number),
    FOREIGN KEY (registration_id) REFERENCES pre_admission_registration(id) ON DELETE CASCADE,
    FOREIGN KEY (test_id) REFERENCES admission_tests(id) ON DELETE CASCADE,
    INDEX idx_admit_card_registration (registration_id)
);

-- seat_allocations
CREATE TABLE IF NOT EXISTS seat_allocations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    test_id BIGINT NOT NULL,
    registration_id BIGINT NOT NULL,
    center_id BIGINT,
    center_name VARCHAR(200),
    building_name VARCHAR(200),
    room_name VARCHAR(100),
    seat_number VARCHAR(30) NOT NULL,
    roll_number VARCHAR(30) NOT NULL,
    status VARCHAR(30) DEFAULT 'ASSIGNED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (test_id) REFERENCES admission_tests(id) ON DELETE CASCADE,
    FOREIGN KEY (registration_id) REFERENCES pre_admission_registration(id) ON DELETE CASCADE,
    FOREIGN KEY (center_id) REFERENCES exam_centers(id) ON DELETE SET NULL,
    INDEX idx_seat_alloc_test (test_id),
    INDEX idx_seat_alloc_registration (registration_id)
);

-- seat_allocation_config
CREATE TABLE IF NOT EXISTS seat_allocation_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id BIGINT NOT NULL,
    academic_year VARCHAR(20) NOT NULL,
    allocation_round INT NOT NULL DEFAULT 1,
    auto_allocation BOOLEAN DEFAULT TRUE,
    manual_allocation BOOLEAN DEFAULT TRUE,
    allocation_start_date TIMESTAMP NOT NULL,
    allocation_end_date TIMESTAMP NOT NULL,
    accept_deadline_hours INT DEFAULT 72,
    lock_after_publish BOOLEAN DEFAULT TRUE,
    enable_quota BOOLEAN DEFAULT FALSE,
    enable_reserved_seats BOOLEAN DEFAULT FALSE,
    status VARCHAR(30) DEFAULT 'DRAFT',
    remarks VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES academic_sessions(id) ON DELETE CASCADE,
    INDEX idx_seat_alloc_config_session (session_id)
);

-- seat_allocation_log
CREATE TABLE IF NOT EXISTS seat_allocation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    allocation_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL,
    previous_status VARCHAR(30),
    new_status VARCHAR(30),
    previous_program_id BIGINT,
    new_program_id BIGINT,
    previous_department_id BIGINT,
    new_department_id BIGINT,
    remarks VARCHAR(500),
    performed_by BIGINT,
    performed_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (allocation_id) REFERENCES department_allocation(id) ON DELETE CASCADE,
    FOREIGN KEY (performed_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_seat_alloc_log_allocation (allocation_id)
);

-- program_seat_config
CREATE TABLE IF NOT EXISTS program_seat_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_id BIGINT NOT NULL,
    faculty_id BIGINT NOT NULL,
    department_id BIGINT NOT NULL,
    program_id BIGINT NOT NULL,
    shift VARCHAR(20) NOT NULL DEFAULT 'DAY',
    total_seats INT NOT NULL DEFAULT 0,
    general_seats INT NOT NULL DEFAULT 0,
    quota_seats INT NOT NULL DEFAULT 0,
    reserved_seats INT NOT NULL DEFAULT 0,
    allocated_seats INT NOT NULL DEFAULT 0,
    waiting_seats INT NOT NULL DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_program_seat_config (config_id, program_id),
    FOREIGN KEY (config_id) REFERENCES seat_allocation_config(id) ON DELETE CASCADE,
    FOREIGN KEY (faculty_id) REFERENCES faculties(id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE CASCADE,
    FOREIGN KEY (program_id) REFERENCES programs(id) ON DELETE CASCADE,
    INDEX idx_program_seat_config_config (config_id)
);

-- department_allocation
CREATE TABLE IF NOT EXISTS department_allocation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    allocation_number VARCHAR(50) NOT NULL,
    config_id BIGINT,
    allocation_round INT DEFAULT 1,
    choice_number INT,
    allocated_faculty_id BIGINT,
    shift VARCHAR(20),
    merit_rank INT,
    total_score DOUBLE,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    allocated_at TIMESTAMP NULL,
    accepted_at TIMESTAMP NULL,
    declined_at TIMESTAMP NULL,
    deadline TIMESTAMP NULL,
    confirmed_at TIMESTAMP NULL,
    is_waiting BOOLEAN DEFAULT FALSE,
    waiting_rank INT,
    remarks VARCHAR(2000),
    registration_id BIGINT NOT NULL,
    allocated_program_id BIGINT,
    allocated_department_id BIGINT,
    allocated_batch_id BIGINT,
    allocated_section_id BIGINT,
    semester_id BIGINT,
    allocated_by_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_allocation_number (allocation_number),
    FOREIGN KEY (config_id) REFERENCES seat_allocation_config(id) ON DELETE SET NULL,
    FOREIGN KEY (allocated_faculty_id) REFERENCES faculties(id) ON DELETE SET NULL,
    FOREIGN KEY (registration_id) REFERENCES pre_admission_registration(id) ON DELETE CASCADE,
    FOREIGN KEY (allocated_program_id) REFERENCES programs(id) ON DELETE SET NULL,
    FOREIGN KEY (allocated_department_id) REFERENCES departments(id) ON DELETE SET NULL,
    FOREIGN KEY (allocated_batch_id) REFERENCES batches(id) ON DELETE SET NULL,
    FOREIGN KEY (allocated_section_id) REFERENCES sections(id) ON DELETE SET NULL,
    FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE SET NULL,
    FOREIGN KEY (allocated_by_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_dept_alloc_config (config_id),
    INDEX idx_dept_alloc_registration (registration_id),
    INDEX idx_dept_alloc_status (status)
);

-- ============================================================
-- 4. MISSING STUDENT TABLES
-- ============================================================

-- student_attendance
CREATE TABLE IF NOT EXISTS student_attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    course_id BIGINT,
    semester_id BIGINT,
    attendance_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    remarks VARCHAR(200),
    check_in_time TIME,
    check_out_time TIME,
    recorded_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE SET NULL,
    FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE SET NULL,
    FOREIGN KEY (recorded_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_student_attendance_date (attendance_date),
    INDEX idx_student_attendance_student (student_id)
);

-- student_documents
CREATE TABLE IF NOT EXISTS student_documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    document_type VARCHAR(100) NOT NULL,
    document_name VARCHAR(255) NOT NULL,
    file_url VARCHAR(500),
    file_size BIGINT,
    uploaded_at TIMESTAMP NULL,
    status VARCHAR(20) NOT NULL,
    verified_by BIGINT,
    verified_at TIMESTAMP NULL,
    remarks VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (verified_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_student_docs_student (student_id)
);

-- student_id_generation
CREATE TABLE IF NOT EXISTS student_id_generation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id_ref BIGINT,
    student_id VARCHAR(50) NOT NULL,
    student_name VARCHAR(100) NOT NULL,
    department VARCHAR(100),
    program VARCHAR(100),
    batch VARCHAR(50),
    status VARCHAR(20),
    id_card_number VARCHAR(50),
    issued_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id_ref) REFERENCES students(id) ON DELETE SET NULL,
    INDEX idx_student_id_gen_student (student_id_ref)
);

-- student_promotions
CREATE TABLE IF NOT EXISTS student_promotions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    from_semester_id BIGINT,
    to_semester_id BIGINT,
    from_batch_id BIGINT,
    to_batch_id BIGINT,
    promotion_date DATE,
    status VARCHAR(20) NOT NULL,
    remarks VARCHAR(500),
    approved_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (from_semester_id) REFERENCES semesters(id) ON DELETE SET NULL,
    FOREIGN KEY (to_semester_id) REFERENCES semesters(id) ON DELETE SET NULL,
    FOREIGN KEY (from_batch_id) REFERENCES batches(id) ON DELETE SET NULL,
    FOREIGN KEY (to_batch_id) REFERENCES batches(id) ON DELETE SET NULL,
    FOREIGN KEY (approved_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_student_promotions_student (student_id)
);

-- medical_info
CREATE TABLE IF NOT EXISTS medical_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    blood_group VARCHAR(5),
    height DOUBLE,
    weight DOUBLE,
    allergies VARCHAR(2000),
    medications VARCHAR(2000),
    conditions VARCHAR(2000),
    emergency_contact VARCHAR(200),
    emergency_phone VARCHAR(20),
    insurance_provider VARCHAR(200),
    insurance_number VARCHAR(100),
    doctor_name VARCHAR(200),
    doctor_phone VARCHAR(20),
    notes VARCHAR(2000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_medical_student (student_id),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

-- alumni
CREATE TABLE IF NOT EXISTS alumni (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    graduation_date DATE,
    degree VARCHAR(100),
    program_id BIGINT,
    department_id BIGINT,
    current_company VARCHAR(200),
    current_designation VARCHAR(200),
    current_location VARCHAR(200),
    email VARCHAR(200),
    phone VARCHAR(20),
    linked_in_profile VARCHAR(500),
    is_available_for_mentoring BOOLEAN,
    is_available_for_recruitment BOOLEAN,
    remarks VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_alumni_student (student_id),
    UNIQUE KEY uk_alumni_email (email),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (program_id) REFERENCES programs(id) ON DELETE SET NULL,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL
);

-- transcripts
CREATE TABLE IF NOT EXISTS transcripts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transcript_number VARCHAR(50) NOT NULL,
    student_id BIGINT NOT NULL,
    program_id BIGINT,
    semester_id BIGINT,
    issued_at TIMESTAMP NULL,
    status VARCHAR(20) NOT NULL,
    gpa DOUBLE,
    total_credits INT,
    remarks VARCHAR(500),
    issued_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_transcript_number (transcript_number),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (program_id) REFERENCES programs(id) ON DELETE SET NULL,
    FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE SET NULL,
    FOREIGN KEY (issued_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_transcript_student (student_id)
);

-- ============================================================
-- 5. MISSING FINANCE TABLES
-- ============================================================

-- fee_structures
CREATE TABLE IF NOT EXISTS fee_structures (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fee_type_id BIGINT NOT NULL,
    program_id BIGINT NOT NULL,
    semester_id BIGINT NOT NULL,
    batch_id BIGINT,
    amount DOUBLE NOT NULL,
    due_days INT DEFAULT 30,
    academic_year VARCHAR(20),
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (fee_type_id) REFERENCES fee_types(id) ON DELETE CASCADE,
    FOREIGN KEY (program_id) REFERENCES programs(id) ON DELETE CASCADE,
    FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE CASCADE,
    FOREIGN KEY (batch_id) REFERENCES batches(id) ON DELETE SET NULL,
    INDEX idx_fee_structure_program (program_id),
    INDEX idx_fee_structure_semester (semester_id)
);

-- invoice_items
CREATE TABLE IF NOT EXISTS invoice_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_id BIGINT,
    fee_type_id BIGINT,
    description VARCHAR(255),
    amount DOUBLE,
    discount_amount DOUBLE DEFAULT 0.0,
    net_amount DOUBLE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE,
    FOREIGN KEY (fee_type_id) REFERENCES fee_types(id) ON DELETE SET NULL,
    INDEX idx_invoice_items_invoice (invoice_id)
);

-- payment_transactions
CREATE TABLE IF NOT EXISTS payment_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id BIGINT,
    transaction_id VARCHAR(50) NOT NULL,
    gateway_transaction_id VARCHAR(100),
    amount DOUBLE,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    gateway_response TEXT,
    gateway_name VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_transaction_id (transaction_id),
    FOREIGN KEY (payment_id) REFERENCES payments(id) ON DELETE SET NULL,
    INDEX idx_payment_transactions_payment (payment_id)
);

-- refunds
CREATE TABLE IF NOT EXISTS refunds (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    refund_number VARCHAR(30) NOT NULL,
    payment_id BIGINT,
    student_id BIGINT,
    amount DOUBLE,
    reason TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    approved_by VARCHAR(100),
    approved_at TIMESTAMP NULL,
    rejection_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_refund_number (refund_number),
    FOREIGN KEY (payment_id) REFERENCES payments(id) ON DELETE SET NULL,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE SET NULL,
    INDEX idx_refunds_student (student_id)
);

-- fines
CREATE TABLE IF NOT EXISTS fines (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT,
    invoice_id BIGINT,
    fee_type_id BIGINT,
    amount DOUBLE NOT NULL,
    reason TEXT,
    issued_by VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    issued_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE SET NULL,
    FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE SET NULL,
    FOREIGN KEY (fee_type_id) REFERENCES fee_types(id) ON DELETE SET NULL,
    INDEX idx_fines_student (student_id)
);

-- ============================================================
-- 6. MISSING REGISTRATION TABLES
-- ============================================================

-- registration_configs
CREATE TABLE IF NOT EXISTS registration_configs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    semester_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    min_credits INT NOT NULL DEFAULT 12,
    max_credits INT NOT NULL DEFAULT 24,
    allow_add_drop BOOLEAN DEFAULT TRUE,
    add_drop_deadline DATE,
    advisor_approval_required BOOLEAN DEFAULT TRUE,
    payment_required BOOLEAN DEFAULT TRUE,
    is_active BOOLEAN DEFAULT TRUE,
    is_closed BOOLEAN DEFAULT FALSE,
    remarks VARCHAR(500),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE CASCADE,
    INDEX idx_reg_config_semester (semester_id)
);

-- registration_history
CREATE TABLE IF NOT EXISTS registration_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    course_id BIGINT,
    semester_id BIGINT NOT NULL,
    course_registration_id BIGINT,
    action VARCHAR(50) NOT NULL,
    details VARCHAR(1000) NOT NULL,
    performed_by BIGINT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE SET NULL,
    FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE CASCADE,
    FOREIGN KEY (course_registration_id) REFERENCES course_registrations(id) ON DELETE SET NULL,
    FOREIGN KEY (performed_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_reg_history_student (student_id),
    INDEX idx_reg_history_semester (semester_id)
);

-- enrollment_configs
CREATE TABLE IF NOT EXISTS enrollment_configs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    academic_session_id BIGINT,
    semester_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    late_enrollment_date DATE,
    min_credits INT NOT NULL DEFAULT 12,
    max_credits INT NOT NULL DEFAULT 24,
    enrollment_status VARCHAR(20) DEFAULT 'OPEN',
    is_active BOOLEAN DEFAULT TRUE,
    is_closed BOOLEAN DEFAULT FALSE,
    requires_advisor_approval BOOLEAN DEFAULT TRUE,
    requires_payment BOOLEAN DEFAULT TRUE,
    allow_late_enrollment BOOLEAN DEFAULT TRUE,
    remarks VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (academic_session_id) REFERENCES academic_sessions(id) ON DELETE SET NULL,
    FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE CASCADE,
    INDEX idx_enrollment_config_semester (semester_id)
);

-- enrollment_approvals
CREATE TABLE IF NOT EXISTS enrollment_approvals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    semester_enrollment_id BIGINT NOT NULL,
    advisor_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL,
    comments VARCHAR(500),
    ip_address VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (semester_enrollment_id) REFERENCES semester_enrollments(id) ON DELETE CASCADE,
    FOREIGN KEY (advisor_id) REFERENCES teachers(id) ON DELETE CASCADE,
    INDEX idx_enrollment_approvals_enrollment (semester_enrollment_id)
);

-- enrollment_history
CREATE TABLE IF NOT EXISTS enrollment_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    semester_id BIGINT NOT NULL,
    semester_enrollment_id BIGINT,
    action VARCHAR(50) NOT NULL,
    details VARCHAR(1000) NOT NULL,
    performed_by BIGINT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE CASCADE,
    FOREIGN KEY (semester_enrollment_id) REFERENCES semester_enrollments(id) ON DELETE SET NULL,
    FOREIGN KEY (performed_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_enrollment_history_student (student_id),
    INDEX idx_enrollment_history_semester (semester_id)
);

-- course_registrations
CREATE TABLE IF NOT EXISTS course_registrations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    semester_id BIGINT NOT NULL,
    batch_id BIGINT,
    status VARCHAR(20) NOT NULL,
    registration_date DATE,
    is_selected BOOLEAN,
    credit_hours INT,
    remarks VARCHAR(500),
    approved_by BIGINT,
    registration_type VARCHAR(20),
    advisor_status VARCHAR(20),
    advisor_comments VARCHAR(500),
    advisor_id BIGINT,
    advisor_approved_at TIMESTAMP NULL,
    payment_status VARCHAR(20),
    payment_reference VARCHAR(100),
    payment_amount DOUBLE,
    finalized BOOLEAN DEFAULT FALSE,
    finalized_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE CASCADE,
    FOREIGN KEY (batch_id) REFERENCES batches(id) ON DELETE SET NULL,
    FOREIGN KEY (approved_by) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (advisor_id) REFERENCES faculties(id) ON DELETE SET NULL,
    INDEX idx_course_reg_student (student_id),
    INDEX idx_course_reg_semester (semester_id)
);

-- semester_enrollments
CREATE TABLE IF NOT EXISTS semester_enrollments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    semester_id BIGINT NOT NULL,
    batch_id BIGINT,
    academic_session_id BIGINT,
    program_id BIGINT,
    faculty_id BIGINT,
    department_id BIGINT,
    advisor_id BIGINT,
    enrollment_number VARCHAR(50),
    enrollment_date DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'DRAFT',
    registered_credits INT DEFAULT 0,
    min_credits INT,
    max_credits INT,
    advisor_status VARCHAR(20),
    advisor_comments VARCHAR(500),
    advisor_approved_at TIMESTAMP NULL,
    payment_status VARCHAR(20) DEFAULT 'PENDING',
    payment_amount DOUBLE,
    payment_reference VARCHAR(100),
    payment_date TIMESTAMP NULL,
    is_finalized BOOLEAN DEFAULT FALSE,
    finalized_at TIMESTAMP NULL,
    remarks VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    cancelled_at TIMESTAMP NULL,
    cancellation_reason VARCHAR(500),
    enrollment_type VARCHAR(20) DEFAULT 'NORMAL',
    is_late_enrollment BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_semester_enrollment_number (enrollment_number),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE CASCADE,
    FOREIGN KEY (batch_id) REFERENCES batches(id) ON DELETE SET NULL,
    FOREIGN KEY (academic_session_id) REFERENCES academic_sessions(id) ON DELETE SET NULL,
    FOREIGN KEY (program_id) REFERENCES programs(id) ON DELETE SET NULL,
    FOREIGN KEY (faculty_id) REFERENCES faculties(id) ON DELETE SET NULL,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL,
    FOREIGN KEY (advisor_id) REFERENCES teachers(id) ON DELETE SET NULL,
    INDEX idx_semester_enrollment_student (student_id),
    INDEX idx_semester_enrollment_semester (semester_id)
);

-- semester_registrations
CREATE TABLE IF NOT EXISTS semester_registrations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    semester_id BIGINT NOT NULL,
    batch_id BIGINT,
    registration_date DATE,
    status VARCHAR(20) NOT NULL,
    approved_by BIGINT,
    remarks VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE CASCADE,
    FOREIGN KEY (batch_id) REFERENCES batches(id) ON DELETE SET NULL,
    FOREIGN KEY (approved_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_semester_reg_student (student_id),
    INDEX idx_semester_reg_semester (semester_id)
);

-- ============================================================
-- 7. MISSING SYSTEM TABLES
-- ============================================================

-- teachers (required by departments.head_id FK and enrollment_approvals)
CREATE TABLE IF NOT EXISTS teachers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    gender VARCHAR(10),
    date_of_birth DATE,
    blood_group VARCHAR(5),
    nationality VARCHAR(50),
    religion VARCHAR(50),
    marital_status VARCHAR(20),
    photo VARCHAR(500),
    national_id VARCHAR(50),
    passport VARCHAR(50),
    emergency_contact VARCHAR(20),
    present_address TEXT,
    permanent_address TEXT,
    teacher_code VARCHAR(50) NOT NULL,
    unique_code VARCHAR(50),
    joining_date DATE,
    employment_status VARCHAR(20),
    employment_type VARCHAR(50),
    designation VARCHAR(100),
    department_id BIGINT,
    faculty_id BIGINT,
    office_room VARCHAR(50),
    campus VARCHAR(100),
    highest_degree VARCHAR(100),
    university VARCHAR(200),
    specialization VARCHAR(255),
    experience VARCHAR(100),
    certifications TEXT,
    assigned_courses TEXT,
    sections VARCHAR(255),
    semester VARCHAR(50),
    credit_load VARCHAR(50),
    google_scholar VARCHAR(200),
    orcid VARCHAR(100),
    salary_grade VARCHAR(50),
    basic_salary DOUBLE,
    bank_information TEXT,
    tax_id VARCHAR(50),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    user_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_teacher_email (email),
    UNIQUE KEY uk_teacher_code (teacher_code),
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL,
    FOREIGN KEY (faculty_id) REFERENCES faculties(id) ON DELETE SET NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_teacher_department (department_id),
    INDEX idx_teacher_faculty (faculty_id)
);

-- login_sessions
CREATE TABLE IF NOT EXISTS login_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    session_token VARCHAR(255) NOT NULL,
    ip_address VARCHAR(45),
    browser VARCHAR(100),
    operating_system VARCHAR(100),
    device_type VARCHAR(50),
    login_time TIMESTAMP NOT NULL,
    last_activity_time TIMESTAMP NULL,
    logout_time TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE,
    expired BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_session_token (session_token),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_login_sessions_user (user_id),
    INDEX idx_login_sessions_token (session_token)
);

-- workflows
CREATE TABLE IF NOT EXISTS workflows (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    module_name VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_workflow_name (name)
);

-- workflow_steps
CREATE TABLE IF NOT EXISTS workflow_steps (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    workflow_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    step_order INT NOT NULL,
    required_role VARCHAR(100),
    required_permission VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (workflow_id) REFERENCES workflows(id) ON DELETE CASCADE,
    INDEX idx_workflow_steps_workflow (workflow_id)
);

-- workflow_approvals
CREATE TABLE IF NOT EXISTS workflow_approvals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    workflow_step_id BIGINT NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_id BIGINT NOT NULL,
    approver_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    comments TEXT,
    rejection_reason VARCHAR(500),
    acted_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (workflow_step_id) REFERENCES workflow_steps(id) ON DELETE CASCADE,
    FOREIGN KEY (approver_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_workflow_approvals_step (workflow_step_id),
    INDEX idx_workflow_approvals_entity (entity_type, entity_id)
);

-- features
CREATE TABLE IF NOT EXISTS features (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    feature_key VARCHAR(200) NOT NULL,
    feature_name VARCHAR(200) NOT NULL,
    module_name VARCHAR(100) NOT NULL,
    category VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    is_enabled BOOLEAN DEFAULT TRUE,
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_feature_key (feature_key)
);

-- feature_audit_logs
CREATE TABLE IF NOT EXISTS feature_audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    feature_id BIGINT,
    feature_key VARCHAR(200) NOT NULL,
    feature_name VARCHAR(200),
    previous_status BOOLEAN,
    new_status BOOLEAN,
    changed_by VARCHAR(100) NOT NULL,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    change_reason VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (feature_id) REFERENCES features(id) ON DELETE SET NULL,
    INDEX idx_feature_audit_feature (feature_id)
);

-- timeline_events
CREATE TABLE IF NOT EXISTS timeline_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_type VARCHAR(100) NOT NULL,
    entity_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    description VARCHAR(500) NOT NULL,
    old_value TEXT,
    new_value TEXT,
    ip_address VARCHAR(45),
    severity VARCHAR(20) DEFAULT 'INFO',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_timeline_entity (entity_type, entity_id),
    INDEX idx_timeline_user (user_id),
    INDEX idx_timeline_created (created_at)
);

-- entity_comments
CREATE TABLE IF NOT EXISTS entity_comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_type VARCHAR(100) NOT NULL,
    entity_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    parent_id BIGINT,
    is_edited BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_comment_entity (entity_type, entity_id),
    INDEX idx_comment_user (user_id)
);

-- entity_attachments
CREATE TABLE IF NOT EXISTS entity_attachments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_type VARCHAR(100) NOT NULL,
    entity_id BIGINT NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    stored_filename VARCHAR(255) NOT NULL,
    path VARCHAR(500) NOT NULL,
    content_type VARCHAR(255),
    size BIGINT NOT NULL,
    uploaded_by_id BIGINT NOT NULL,
    category VARCHAR(50),
    is_verified BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (uploaded_by_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_attachment_entity (entity_type, entity_id),
    INDEX idx_attachment_uploader (uploaded_by_id)
);

-- ============================================================
-- 8. MISSING EXAM TABLES
-- ============================================================

-- exam_centers
CREATE TABLE IF NOT EXISTS exam_centers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    code VARCHAR(30) NOT NULL,
    address TEXT,
    city VARCHAR(100),
    total_capacity INT,
    contact_person VARCHAR(100),
    contact_phone VARCHAR(20),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_exam_center_name (name),
    UNIQUE KEY uk_exam_center_code (code)
);

-- ============================================================
-- 9. MISSING OTHER TABLES
-- ============================================================

-- universities
CREATE TABLE IF NOT EXISTS universities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    code VARCHAR(50) NOT NULL,
    address TEXT,
    phone VARCHAR(20),
    email VARCHAR(100),
    website VARCHAR(200),
    logo_url VARCHAR(500),
    established_year INT,
    motto VARCHAR(200),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_university_name (name),
    UNIQUE KEY uk_university_code (code),
    UNIQUE KEY uk_university_email (email)
);

-- ============================================================
-- 10. ADDITIONAL SUPPORTING TABLES
-- (Referenced by entities but not in the user's list above,
--  included to ensure all entity FKs resolve)
-- ============================================================

-- semesters (referenced by many entities)
CREATE TABLE IF NOT EXISTS semesters (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) NOT NULL,
    academic_session_id BIGINT,
    program_id BIGINT,
    semester_number INT NOT NULL,
    start_date DATE,
    end_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_semester_code (code),
    FOREIGN KEY (academic_session_id) REFERENCES academic_sessions(id) ON DELETE SET NULL,
    FOREIGN KEY (program_id) REFERENCES programs(id) ON DELETE SET NULL,
    INDEX idx_semester_session (academic_session_id)
);

-- programs (referenced by many entities)
CREATE TABLE IF NOT EXISTS programs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    code VARCHAR(30) NOT NULL,
    description TEXT,
    department_id BIGINT,
    faculty_id BIGINT,
    duration_years INT DEFAULT 4,
    total_credits INT DEFAULT 120,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_program_name (name),
    UNIQUE KEY uk_program_code (code),
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL,
    FOREIGN KEY (faculty_id) REFERENCES faculties(id) ON DELETE SET NULL,
    INDEX idx_program_department (department_id)
);

-- buildings (referenced by Classroom entity)
CREATE TABLE IF NOT EXISTS buildings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(20) NOT NULL,
    address TEXT,
    total_floors INT DEFAULT 1,
    campus_id BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_building_name (name),
    UNIQUE KEY uk_building_code (code),
    FOREIGN KEY (campus_id) REFERENCES campuses(id) ON DELETE SET NULL
);

-- classrooms (referenced by SemesterRoutine and others)
CREATE TABLE IF NOT EXISTS classrooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(20) NOT NULL,
    building_id BIGINT,
    floor INT,
    capacity INT NOT NULL DEFAULT 40,
    room_type VARCHAR(50) DEFAULT 'CLASSROOM',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_classroom_name (name),
    UNIQUE KEY uk_classroom_code (code),
    FOREIGN KEY (building_id) REFERENCES buildings(id) ON DELETE SET NULL,
    INDEX idx_classroom_building (building_id)
);

-- menus (base table if not created by Flyway or another migration)
CREATE TABLE IF NOT EXISTS menus (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    icon VARCHAR(100),
    route VARCHAR(255),
    parent_id BIGINT,
    order_no INT NOT NULL DEFAULT 0,
    permission_code VARCHAR(100),
    module VARCHAR(100) NOT NULL,
    visible BOOLEAN DEFAULT TRUE,
    active BOOLEAN DEFAULT TRUE,
    menu_type VARCHAR(50) DEFAULT 'MENU',
    css_class VARCHAR(100),
    badge VARCHAR(50),
    badge_color VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_id) REFERENCES menus(id) ON DELETE SET NULL,
    INDEX idx_menus_parent (parent_id)
);
