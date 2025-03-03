DROP INDEX IF EXISTS project_stage_executors_stage_id_idx;
DROP TABLE IF EXISTS meet_participant;
DROP TABLE IF EXISTS meet;
DROP TABLE IF EXISTS resource_allowed_roles;
DROP TABLE IF EXISTS team_member_roles;
DROP TABLE IF EXISTS project_stage_executors;
DROP TABLE IF EXISTS stage_invitation;
DROP TABLE IF EXISTS internship_interns;
DROP TABLE IF EXISTS internship;
DROP TABLE IF EXISTS moment_user;
DROP TABLE IF EXISTS moment_resource;
DROP TABLE IF EXISTS moment_project;
DROP TABLE IF EXISTS moment;
DROP TABLE IF EXISTS initiative_project_stages;
DROP TABLE IF EXISTS initiative_project;
DROP TABLE IF EXISTS initiative;
DROP TABLE IF EXISTS team_member;
DROP TABLE IF EXISTS team;
DROP TABLE IF EXISTS task;
DROP TABLE IF EXISTS project_resource;
DROP TABLE IF EXISTS resource;
DROP TABLE IF EXISTS schedule;
DROP TABLE IF EXISTS project_stage_roles;
DROP TABLE IF EXISTS project_stage;
DROP TABLE IF EXISTS campaign;
DROP TABLE IF EXISTS vacancy_skills;
DROP TABLE IF EXISTS candidate;
DROP TABLE IF EXISTS vacancy;
DROP TABLE IF EXISTS project;
DROP TABLE IF EXISTS donation;

DELETE FROM databasechangelog;