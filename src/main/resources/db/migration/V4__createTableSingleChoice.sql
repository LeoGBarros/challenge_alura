CREATE TABLE single_choice_options (
    task_id bigint(20) NOT NULL,
    option_text varchar(80) NOT NULL,
    is_correct boolean NOT NULL,
    PRIMARY KEY (task_id, option_text),
    CONSTRAINT fk_task_single_choice FOREIGN KEY (task_id)
        REFERENCES Task(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;
