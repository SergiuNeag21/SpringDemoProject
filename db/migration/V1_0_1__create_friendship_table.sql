CREATE TABLE IF NOT EXISTS friendship (
    id SERIAL PRIMARY KEY,
    user_id1 INT NOT NULL,
    user_id2 INT NOT NULL,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id1) REFERENCES app_user(id),
    FOREIGN KEY (user_id2) REFERENCES app_user(id)
);