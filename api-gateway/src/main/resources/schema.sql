CREATE TABLE IF NOT EXISTS auth_session (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    session_id VARCHAR(255),

    username VARCHAR(255) NOT NULL UNIQUE,

    keycloak_user_id VARCHAR(255) NOT NULL UNIQUE,

    access_token TEXT NOT NULL,

    refresh_token TEXT NOT NULL,

    access_token_expires_at TIMESTAMP NOT NULL,

    refresh_token_expires_at TIMESTAMP NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
