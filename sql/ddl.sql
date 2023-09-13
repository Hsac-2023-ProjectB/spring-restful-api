CREATE TABLE member (
    member_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    loginId VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    email VARCHAR(255),
    birthDate DATE,
    gender VARCHAR(255),
    fields TEXT
);



CREATE TABLE profile (
     profile_id INT AUTO_INCREMENT PRIMARY KEY,
     authorId INT NOT NULL,
     tags TEXT,
     introduction TEXT,
     FOREIGN KEY (authorId) REFERENCES member(id)
    ON DELETE cascade
);
