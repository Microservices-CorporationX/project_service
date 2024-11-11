INSERT INTO project (name, description, parent_project_id, storage_size, max_storage_size, owner_id, created_at,
                     updated_at, status, visibility, cover_image_id)
VALUES
    ('Test1','Test1',null,null,null,2,'2024-11-08 22:38:20.307877','2024-11-08 22:38:20.307877','CREATED','PUBLIC',null),
    ('Test2','Test2',null,null,null,3,'2024-11-08 22:38:20.307877','2024-11-08 22:38:20.307877','IN_PROGRESS','PUBLIC',null),
    ('Test3','Test3',null,null,null,3,'2024-11-08 22:38:20.307877','2024-11-08 22:38:20.307877','IN_PROGRESS','PRIVATE',null),
    ('Test4','Test4',null,null,null,5,'2024-11-08 22:38:20.307877','2024-11-08 22:38:20.307877','IN_PROGRESS','PUBLIC',null),
    ('Test5','Test5',null,null,null,5,'2024-11-08 22:38:20.307877','2024-11-08 22:38:20.307877','ON_HOLD','PRIVATE',null),
    ('Test6','Test6',null,null,null,5,'2024-11-08 22:38:20.307877','2024-11-08 22:38:20.307877','CANCELLED','PRIVATE',null);

INSERT INTO team (id, project_id)
VALUES
    (2,5),
    (1,3);

INSERT INTO team_member (user_id, team_id)
VALUES
    (5,2),
    (3,1);