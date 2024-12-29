INSERT INTO users (username, password, email, country_id)
VALUES ('John', 'JohnJohn', 'JohnJohnJohn@gmail.com', 5);

INSERT INTO project (name, owner_id, visibility)
VALUES ('some name', 1, 'PUBLIC');

INSERT INTO campaign (title, created_by, project_id)
VALUES ('some title', 1, 1);
