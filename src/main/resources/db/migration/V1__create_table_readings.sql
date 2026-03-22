CREATE TABLE readings (
    id serial PRIMARY KEY,
    timestamp TIMESTAMPTZ NOT NULL,
    ambient_temp FLOAT NOT NULL,
    liquid_temp FLOAT NOT NULL,
    humidity FLOAT NOT NULL,
    stage VARCHAR(30) NOT NULL,
    device_id VARCHAR(50) NOT NULL
);