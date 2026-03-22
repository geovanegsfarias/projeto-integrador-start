CREATE TABLE alerts (
    id serial PRIMARY KEY,
    reading_id INTEGER NOT NULL,
    type VARCHAR(20) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    message VARCHAR(100) NOT NULL,
    value FLOAT NOT NULL,
    threshold FLOAT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    FOREIGN KEY (reading_id) REFERENCES readings(id)
);