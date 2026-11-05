CREATE DATABASE elostaz CHARACTER SET utf8mb4;
USE elostaz;

CREATE TABLE users (
    user_id        INT AUTO_INCREMENT PRIMARY KEY,
    username       VARCHAR(50)  NOT NULL UNIQUE,
    password_hash  VARCHAR(255) NOT NULL,
    role           ENUM('ADMIN','TUTOR','PARENT') NOT NULL
);

CREATE TABLE parents (
    parent_id  INT AUTO_INCREMENT PRIMARY KEY,
    user_id    INT NOT NULL UNIQUE,
    full_name  VARCHAR(100) NOT NULL,
    phone      VARCHAR(20)  NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE subjects (
    subject_id    INT AUTO_INCREMENT PRIMARY KEY,
    subject_name  VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE grade_levels (
    grade_id    INT AUTO_INCREMENT PRIMARY KEY,
    grade_name  VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE areas (
    area_id    INT AUTO_INCREMENT PRIMARY KEY,
    area_name  VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE teachers (
    teacher_id        INT AUTO_INCREMENT PRIMARY KEY,
    user_id           INT NOT NULL UNIQUE,
    full_name         VARCHAR(100) NOT NULL,
    phone             VARCHAR(20)  NOT NULL,
    email             VARCHAR(100),
    price_per_lesson  DECIMAL(8,2) NOT NULL CHECK (price_per_lesson >= 0),
    lesson_type       ENUM('ONLINE','HOME','CENTER') NOT NULL,
    experience_years  INT NOT NULL DEFAULT 0 CHECK (experience_years >= 0),
    bio               TEXT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE teacher_subjects (
    teacher_id  INT NOT NULL,
    subject_id  INT NOT NULL,
    grade_id    INT NOT NULL,
    PRIMARY KEY (teacher_id, subject_id, grade_id),
    FOREIGN KEY (teacher_id) REFERENCES teachers(teacher_id)   ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id)   ON DELETE CASCADE,
    FOREIGN KEY (grade_id)   REFERENCES grade_levels(grade_id) ON DELETE CASCADE
);

CREATE TABLE teacher_areas (
    teacher_id  INT NOT NULL,
    area_id     INT NOT NULL,
    PRIMARY KEY (teacher_id, area_id),
    FOREIGN KEY (teacher_id) REFERENCES teachers(teacher_id) ON DELETE CASCADE,
    FOREIGN KEY (area_id)    REFERENCES areas(area_id)       ON DELETE CASCADE
);

CREATE TABLE availability (
    availability_id  INT AUTO_INCREMENT PRIMARY KEY,
    teacher_id       INT NOT NULL,
    day_of_week      ENUM('SATURDAY','SUNDAY','MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY') NOT NULL,
    start_time       TIME NOT NULL,
    end_time         TIME NOT NULL,
    CHECK (end_time > start_time),
    FOREIGN KEY (teacher_id) REFERENCES teachers(teacher_id) ON DELETE CASCADE
);

CREATE TABLE bookings (
    booking_id   INT AUTO_INCREMENT PRIMARY KEY,
    parent_id    INT NOT NULL,
    teacher_id   INT NOT NULL,
    subject_id   INT NOT NULL,
    lesson_date  DATE NOT NULL,
    start_time   TIME NOT NULL,
    end_time     TIME NOT NULL,
    status       ENUM('PENDING','CONFIRMED','REJECTED','CANCELLED') NOT NULL DEFAULT 'PENDING',
    CHECK (end_time > start_time),
    UNIQUE (teacher_id, lesson_date, start_time),
    FOREIGN KEY (parent_id)  REFERENCES parents(parent_id),
    FOREIGN KEY (teacher_id) REFERENCES teachers(teacher_id),
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id)
);

INSERT INTO subjects (subject_name) VALUES
 ('Arabic'),('English'),('Math'),('Science'),('Physics'),('Chemistry'),('Biology'),('History'),('Geography'),('French'),('German'),('Computer Science');

INSERT INTO grade_levels (grade_name) VALUES
 ('Primary'),('Preparatory'),('Secondary'),('University');

INSERT INTO areas (area_name) VALUES
 ('Smouha'),('Sidi Gaber'),('Miami'),('Stanley'),('Gleem'),('Roushdy'),('San Stefano'),('Sporting'),('Agami'),('Borg El Arab');
