CREATE TABLE users
(
    user_uuid    uuid        NOT NULL,
    created_at   TIMESTAMP   NOT NULL DEFAULT now(),
    first_name   VARCHAR(64)          DEFAULT NULL,
    last_name    VARCHAR(64)          DEFAULT NULL,
    display_name VARCHAR(64) NOT NULL,
    email        VARCHAR(64)          DEFAULT NULL,
    is_disabled  BOOLEAN     NOT NULL DEFAULT false,
    is_admin     BOOLEAN     NOT NULL DEFAULT false,

    PRIMARY KEY (user_uuid)
);

CREATE TABLE boards
(
    board_id          VARCHAR(16) NOT NULL,
    created_at        TIMESTAMP DEFAULT now(),
    board_title       VARCHAR(64) NOT NULL,
    board_description TEXT,
    board_owner       uuid        NOT NULL,
    is_public         BOOLEAN   DEFAULT false,
    is_readonly       BOOLEAN   DEFAULT false,

    PRIMARY KEY (board_id),
    CONSTRAINT fk_owner
        FOREIGN KEY (board_owner)
            REFERENCES users (user_uuid)
);

CREATE TABLE board_kanban_columns
(
    board_id     VARCHAR(16) NOT NULL,
    row_id       VARCHAR(32) NOT NULL,
    row_position INT         NOT NULL,

    PRIMARY KEY (board_id, row_id),
    CONSTRAINT fk_board
        FOREIGN KEY (board_id)
            REFERENCES boards (board_id)
);

CREATE TABLE board_users
(
    user_uuid  uuid        NOT NULL,
    board_id   VARCHAR(16) NOT NULL,
    created_at TIMESTAMP DEFAULT now(),
    can_view   BOOLEAN   DEFAULT true,
    can_use    BOOLEAN   DEFAULT true,
    can_manage BOOLEAN   DEFAULT false,

    PRIMARY KEY (user_uuid, board_id),
    CONSTRAINT fk_user
        FOREIGN KEY (user_uuid)
            REFERENCES users (user_uuid),
    CONSTRAINT fk_board
        FOREIGN KEY (board_id)
            REFERENCES boards (board_id)
);

CREATE TABLE ticket
(
    ticket_id            INT          NOT NULL,
    board_id             VARCHAR(16)  NOT NULL,
    created_at           TIMESTAMP             DEFAULT now(),
    created_by           uuid         NOT NULL,
    ticket_title         VARCHAR(100) NOT NULL,
    ticket_content       TEXT         NOT NULL DEFAULT '',
    ticket_kanban_column VARCHAR(32)  NOT NULL,
    is_open              BOOLEAN               DEFAULT true,
    is_closed            BOOLEAN               DEFAULT false,
    is_deleted           BOOLEAN               DEFAULT false,

    PRIMARY KEY (board_id, ticket_id),
    CONSTRAINT fk_user
        FOREIGN KEY (created_by)
            REFERENCES users (user_uuid),
    CONSTRAINT fk_board
        FOREIGN KEY (board_id)
            REFERENCES boards (board_id),
    CONSTRAINT fk_kanban
        FOREIGN KEY (ticket_kanban_column, board_id)
            REFERENCES board_kanban_columns (row_id, board_id)
);