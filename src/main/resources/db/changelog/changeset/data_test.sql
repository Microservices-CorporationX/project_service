CREATE TABLE IF NOT EXISTS country
(
    id    bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    title varchar(64) UNIQUE NOT NULL
);

INSERT INTO country (title)
VALUES ('United States'),
       ('United Kingdom'),
       ('Australia'),
       ('France');

CREATE TABLE IF NOT EXISTS users
(
    id         bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    username   varchar(64) UNIQUE       NOT NULL,
    password   varchar(128)             NOT NULL,
    email      varchar(64) UNIQUE       NOT NULL,
    phone      varchar(32) UNIQUE,
    about_me   varchar(4096),
    active     boolean     DEFAULT true NOT NULL,
    city       varchar(64),
    country_id bigint                   NOT NULL,
    experience int,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_country_id FOREIGN KEY (country_id) REFERENCES country (id)
);

INSERT INTO users (username, email, phone, password, active, about_me, country_id, city, experience, created_at,
                   updated_at)
VALUES ('JohnDoe', 'johndoe@example.com', '1234567890', 'password1', true, 'About John Doe', 1, 'New York', 2,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('JaneSmith', 'janesmith@example.com', '0987654321', 'password2', true, 'About Jane Smith', 2, 'London', 4,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('MichaelJohnson', 'michaeljohnson@example.com', '1112223333', 'password3', true, 'About Michael Johnson', 1,
        'Sydney', 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('EmilyDavis', 'emilydavis@example.com', '4445556666', 'password4', true, 'About Emily Davis', 3, 'Paris', 8,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('WilliamTaylor', 'williamtaylor@example.com', '7778889999', 'password5', true, 'About William Taylor', 2,
        'Toronto', 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('OliviaAnderson', 'oliviaanderson@example.com', '0001112222', 'password6', true, 'About Olivia Anderson', 1,
        'Berlin', 12, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('JamesWilson', 'jameswilson@example.com', '3334445555', 'password7', true, 'About James Wilson', 3, 'Tokyo', 14,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('SophiaMartin', 'sophiamartin@example.com', '6667778888', 'password8', true, 'About Sophia Martin', 4, 'Rome',
        16, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('BenjaminThompson', 'benjaminthompson@example.com', '9990001111', 'password9', true, 'About Benjamin Thompson',
        4, 'Moscow', 18, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('AvaHarris', 'avaharris@example.com', '2223334444', 'password10', true, 'About Ava Harris', 3, 'Madrid', 20,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


CREATE TABLE IF NOT EXISTS project
(
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR(128) NOT NULL,
    description      VARCHAR(4096),
    parent_project_id        BIGINT,
    storage_size     BIGINT,
    max_storage_size BIGINT,
    owner_id         BIGINT,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status           VARCHAR(255) NOT NULL,
    visibility       VARCHAR(255) NOT NULL,
    cover_image_id   VARCHAR(255),
    CONSTRAINT fk_project_parent FOREIGN KEY (parent_project_id) REFERENCES project (id)
);

CREATE TABLE IF NOT EXISTS team
(
    id         BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    CONSTRAINT fk_team_project FOREIGN KEY (project_id) REFERENCES project (id)
);

CREATE TABLE IF NOT EXISTS team_member
(
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    team_id BIGINT NOT NULL,
    CONSTRAINT fk_team_member_team FOREIGN KEY (team_id) REFERENCES team (id)
);

create table if not exists team_member_roles
(
    team_member_id bigint,
    role           varchar(20) NOT NULL,
    CONSTRAINT fk_team_member_roles_team_member FOREIGN KEY (team_member_id) REFERENCES team_member (id)
);

CREATE TABLE IF NOT EXISTS resource
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    key        VARCHAR(255),
    type       VARCHAR(255),
    status     VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    project_id BIGINT,
    size       BIGINT,
    CONSTRAINT fk_project
        FOREIGN KEY (project_id) REFERENCES project (id)
);

CREATE TABLE IF NOT EXISTS task (
                                    id BIGSERIAL PRIMARY KEY,
                                    name VARCHAR(255) NOT NULL,
                                    description TEXT,
                                    status VARCHAR(255),
                                    performer_user_id BIGINT NOT NULL,
                                    reporter_user_id BIGINT NOT NULL,
                                    minutes_tracked INT,
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    parent_task_id BIGINT,
                                    project_id BIGINT,
                                    stage_id BIGINT,
                                    CONSTRAINT fk_parent_task
                                        FOREIGN KEY (parent_task_id) REFERENCES task(id),
                                    CONSTRAINT fk_project
                                        FOREIGN KEY (project_id) REFERENCES project(id)
);

CREATE TABLE IF NOT EXISTS schedule (
                                        id BIGSERIAL PRIMARY KEY,
                                        name VARCHAR(255) NOT NULL,
                                        description VARCHAR(255),
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        project_id BIGINT,
                                        CONSTRAINT fk_project
                                            FOREIGN KEY (project_id) REFERENCES project(id)
);


INSERT INTO project (name, description, parent_project_id, storage_size, max_storage_size, owner_id, status, visibility, cover_image_id, created_at, updated_at)
VALUES
    ('Project Alpha', 'Description for Project Alpha', NULL, 1048576, 1073741824, 1, 'CREATED', 'PUBLIC', 'img001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Project Beta', 'Description for Project Beta', 1, 2097152, 2147483648, 2, 'IN_PROGRESS', 'PRIVATE', 'img002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Project Gamma', 'Description for Project Gamma', NULL, 524288, 1073741824, 3, 'ON_HOLD', 'PRIVATE', 'img003', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Project Delta', 'Description for Project Delta', 3, 0, 1073741824, 4, 'COMPLETED', 'PUBLIC', 'img004', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Project Epsilon', NULL, 1, 1024, 1073741824, 5, 'CANCELLED', 'PRIVATE', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Project Zeta', 'Description for Project Zeta', NULL, 10485760, 2147483648, 1, 'CREATED', 'PUBLIC', 'img005', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);