CREATE TABLE multiple_choice_options (
    task_id BIGINT NOT NULL,
    option_text VARCHAR(80) NOT NULL,
    isCorrect BOOLEAN NOT NULL,
    PRIMARY KEY (task_id, option_text),
    CONSTRAINT fk_task_multiple_choice FOREIGN KEY (task_id)
        REFERENCES task(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
