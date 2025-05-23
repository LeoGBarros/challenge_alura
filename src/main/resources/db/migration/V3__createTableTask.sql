CREATE TABLE task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    statement VARCHAR(255) NOT NULL,
    order_index INT NOT NULL,
    course_id BIGINT NOT NULL,
    task_type VARCHAR(31) NOT NULL,
    CONSTRAINT fk_task_course FOREIGN KEY (course_id) REFERENCES course(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;
