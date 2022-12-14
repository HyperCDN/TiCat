START TRANSACTION;

-- Representing a logged in user; Used to store additional configurations and permissions away from the auth provider
CREATE TABLE users
(
    user_uuid        UUID        NOT NULL,
    created_at       TIMESTAMP   NOT NULL DEFAULT NOW(),
    modified_at      TIMESTAMP   NOT NULL DEFAULT NOW(),
    first_name       VARCHAR(64)          DEFAULT NULL,
    last_name        VARCHAR(64)          DEFAULT NULL,
    display_name     VARCHAR(64) NOT NULL,
    email            VARCHAR(64)          DEFAULT NULL,
    is_system        BOOLEAN     NOT NULL DEFAULT false,
    is_admin         BOOLEAN     NOT NULL DEFAULT false,
    can_login        BOOLEAN     NOT NULL DEFAULT true,
    can_board_create BOOLEAN     NOT NULL DEFAULT false,
    can_board_join   BOOLEAN     NOT NULL DEFAULT true,

    PRIMARY KEY (user_uuid)
);

-- Create placeholder users
INSERT INTO users (user_uuid, first_name, last_name, display_name, email, is_system, is_admin, can_login, can_board_create, can_board_join)
VALUES ('00000000-0000-4000-0000-000000000000', null, null, 'System', null, true, false, false, false, false), -- Representing system user
       ('00000000-0000-4000-0000-000000000002', null, null, 'Guest', null, true, false, true, false, false);
-- Representing not logged in / guest users

-- Board may be visible to anyone, only logged in users, only members of the board
CREATE TYPE BOARD_VISIBILITY AS ENUM ('ANYONE', 'LOGGED_IN_USER', 'MEMBERS_ONLY');
-- Board membership can be acquired by
CREATE TYPE BOARD_ACCESS_MODE AS ENUM ('PUBLIC_JOIN', 'MANUAL_VERIFY', 'MANUAL_ADD');

-- Representing a workspace
CREATE TABLE boards
(
    board_id    VARCHAR(16)       NOT NULL CHECK ( UPPER(board_id) = board_id AND board_id NOT LIKE '%[^A-Z0-9]%'),
    created_at  TIMESTAMP         NOT NULL DEFAULT NOW(),
    modified_at TIMESTAMP         NOT NULL DEFAULT NOW(),
    title       VARCHAR(64)       NOT NULL,
    description TEXT                       DEFAULT NULL,
    ownership   UUID                       DEFAULT NULL,
    visibility  BOARD_VISIBILITY  NOT NULL DEFAULT 'MEMBERS_ONLY',
    access_mode BOARD_ACCESS_MODE NOT NULL DEFAULT 'MANUAL_ADD',

    PRIMARY KEY (board_id),
    CONSTRAINT fk_owner
        FOREIGN KEY (ownership)
            REFERENCES users (user_uuid)
            ON DELETE SET DEFAULT
            ON UPDATE CASCADE
);

-- Board membership can be requested, offered, granted or blocked from requesting again
CREATE TYPE BOARD_MEMBERSHIP_STATUS AS ENUM ('REQUESTED', 'OFFERED', 'GRANTED', 'BLOCKED');

-- Represents users within a board; Contains board specific permissions
CREATE TABLE board_members
(
    user_uuid        UUID                    NOT NULL,
    board_id         VARCHAR(16)             NOT NULL,
    created_at       TIMESTAMP               NOT NULL DEFAULT NOW(),
    modified_at      TIMESTAMP               NOT NULL DEFAULT NOW(),

    status           BOARD_MEMBERSHIP_STATUS NOT NULL DEFAULT 'BLOCKED',
    can_view         BOOLEAN                 NOT NULL DEFAULT false,
    can_use          BOOLEAN                 NOT NULL DEFAULT false,
    can_manage       BOOLEAN                 NOT NULL DEFAULT false,
    can_administrate BOOLEAN                 NOT NULL DEFAULT false,

    PRIMARY KEY (user_uuid, board_id),
    CONSTRAINT fk_user
        FOREIGN KEY (user_uuid)
            REFERENCES users (user_uuid)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    CONSTRAINT fk_board
        FOREIGN KEY (board_id)
            REFERENCES boards (board_id)
            ON DELETE CASCADE
            ON UPDATE CASCADE
);

-- Assigns a general category to a ticket
CREATE TYPE TICKET_CATEGORY AS ENUM ('EPIC', 'BUG', 'STORY');
-- Describes the urgency to resolve a ticket
CREATE TYPE TICKET_PRIORITY AS ENUM ('CRITICAL', 'HIGH', 'NORMAL', 'LOW', 'NONE');
--
CREATE TYPE TICKET_STATUS AS ENUM ('OPEN', 'CLOSED', 'DELETED');

-- Represents a ticket
CREATE TABLE tickets
(
    ticket_id   INT                      DEFAULT -1,
    board_id    VARCHAR(16)     NOT NULL,
    created_at  TIMESTAMP       NOT NULL DEFAULT NOW(),
    modified_at TIMESTAMP       NOT NULL DEFAULT NOW(),
    created_by  UUID                     DEFAULT NULL,
    category    TICKET_CATEGORY NOT NULL,
    priority    TICKET_PRIORITY NOT NULL DEFAULT 'NORMAL',
    status      TICKET_STATUS   NOT NULL DEFAULT 'OPEN',
    title       VARCHAR(100)    NOT NULL,
    content     TEXT                     DEFAULT NULL,
    assignee    UUID                     DEFAULT NULL,

    PRIMARY KEY (ticket_id, board_id),
    CONSTRAINT fk_user
        FOREIGN KEY (created_by)
            REFERENCES users (user_uuid)
            ON DELETE SET DEFAULT
            ON UPDATE CASCADE,
    CONSTRAINT fk_board
        FOREIGN KEY (board_id)
            REFERENCES boards (board_id)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    CONSTRAINT fk_assignee
        FOREIGN KEY (created_by)
            REFERENCES users (user_uuid)
            ON DELETE SET DEFAULT
            ON UPDATE CASCADE
);

-- create trigger to set the ticket id to increasing values based on the board; As seen https://stackoverflow.com/a/34571410
CREATE FUNCTION TICKET_ID_INCREMENT_FNT()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
DECLARE
    maxID int := 0;
BEGIN
    SELECT MAX(ticket_id) + 1 INTO maxID FROM tickets WHERE board_id = NEW.board_id;
    IF maxID IS NULL THEN
        NEW.ticket_id := 1;
    ELSE
        NEW.ticket_id := maxID;
    END IF;
    RETURN (NEW);
END;
$$;

CREATE TRIGGER TICKET_ID_INCREMENT
    BEFORE INSERT
    ON tickets
    FOR EACH ROW
EXECUTE PROCEDURE TICKET_ID_INCREMENT_FNT();

COMMIT;