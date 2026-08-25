CREATE TABLE IF NOT EXISTS auth_session (
    id UUID PRIMARY KEY,

    session_id VARCHAR(255),

    keycloak_user_id VARCHAR(255) NOT NULL UNIQUE,

    access_token TEXT NOT NULL,

    refresh_token TEXT NOT NULL,

    access_token_expires_at TIMESTAMP NOT NULL,

    refresh_token_expires_at TIMESTAMP NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);