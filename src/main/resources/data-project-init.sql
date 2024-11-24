/*
insert into project(id, name, description, parent_project_id, storage_size,max_storage_size, owner_id, created_at ,
                    updated_at,    status, visibility, cover_image_id)
VALUES (1,
        'Name project 1',
        'description project 1',
        null,
        1005,
        10000006,
        107,
        null,
        null,
        'CREATED',
        'PUBLIC',
        'cover_image_id 1 String');
insert into team(id, project_id) VALUES (1, 1);
insert into team_member(id, user_id, team_id) VALUES (1, 1, 1);
insert into team_member_roles(team_member_id, role) VALUES (1,'DEVELOPER');
insert into team_member_roles(team_member_id, role) VALUES (1,'OWNER');

insert into candidate (id, user_id, resume_doc_key, cover_letter, candidate_status, vacancy_id)
VALUES (1,
        1,
        'resume_doc_key1',
        'cover_letter',
        'WAITING_RESPONSE',
        1
        ) ;

insert into skill ( title) VALUES ( 'Skill titel 1');


insert into skill_offer (skill_id, recommendation_id) VALUES (1,'skill_offer recommendation_id');
insert into skill_request (request_id, skill_id)  VALUES (1,1);

insert into vacancy (id, name, description, project_id, created_at, updated_at, created_by,
                      updated_by, status, salary, work_schedule, count)
VALUES (1,
        'vacancy name 1',
        'description vacancy 1',
        1,
        null,
        null,
        1,
        1,
        'OPEN',
        100,
        'FULL_TIME',
        1);

*/
insert into candidate( user_id, resume_doc_key, cover_letter, candidate_status, vacancy_id)
values (1, 'resume_doc_key 1', 'cover_letter 1', 'WAITING_RESPONSE', 1 );


