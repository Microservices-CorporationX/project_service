CREATE TABLE IF NOT EXISTS  task_linked_tasks (
     task_id BIGINT NOT NULL,
     linked_task_id BIGINT NOT NULL,
     PRIMARY KEY (task_id, linked_task_id),
     CONSTRAINT fk_task FOREIGN KEY (task_id) REFERENCES task(id) ON DELETE CASCADE,
     CONSTRAINT fk_linked_task FOREIGN KEY (linked_task_id) REFERENCES task(id) ON DELETE CASCADE
);