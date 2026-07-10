

CREATE TABLE IF NOT EXISTS projects (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    color       VARCHAR(20)  NOT NULL DEFAULT '#e8a33d',
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS tasks (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    project_id  BIGINT       NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS time_entries (
    id                BIGSERIAL PRIMARY KEY,
    task_id           BIGINT     NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    start_time        TIMESTAMP  NOT NULL,
    end_time          TIMESTAMP  NOT NULL,
    duration_seconds  BIGINT     NOT NULL,
    CHECK (end_time > start_time)
);

CREATE INDEX IF NOT EXISTS idx_tasks_project_id ON tasks(project_id);
CREATE INDEX IF NOT EXISTS idx_time_entries_task_id ON time_entries(task_id);
CREATE INDEX IF NOT EXISTS idx_time_entries_end_time ON time_entries(end_time);
