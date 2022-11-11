-- Representing a logged in user; Used to store additional configurations and permissions away from the auth provider
CREATE TABLE users
(
    user_uuid        UUID        NOT NULL,
    created_at       TIMESTAMP   NOT NULL DEFAULT NOW(),
    first_name       VARCHAR(64)          DEFAULT NULL,
    last_name        VARCHAR(64)          DEFAULT NULL,
    display_name     VARCHAR(64) NOT NULL,
    email            VARCHAR(64)          DEFAULT NULL,
    -- permissions and overrides
    is_admin         BOOLEAN     NOT NULL DEFAULT false,
    can_login        BOOLEAN     NOT NULL DEFAULT true,
    can_board_create BOOLEAN     NOT NULL DEFAULT false,
    can_board_join   BOOLEAN     NOT NULL DEFAULT true,

    PRIMARY KEY (user_uuid)
);

-- Board may be visible to anyone, only logged in users, only members of the board
CREATE TYPE BOARD_VISIBILITY AS ENUM ('ANYONE', 'LOGGED_IN_USER', 'MEMBERS_ONLY');
-- Board membership can be acquired by
CREATE TYPE BOARD_ACCESS_MODE AS ENUM ('PUBLIC_JOIN', 'MANUAL_VERIFY', 'MANUAL_ADD');

-- Representing a workspace;
CREATE TABLE boards
(
    board_id    VARCHAR(16) NOT NULL CHECK ( upper(board_id) = board_id ),
    created_at  TIMESTAMP   NOT NULL DEFAULT now(),
    title       VARCHAR(64) NOT NULL,
    description TEXT,
    ownership   UUID        NOT NULL,
    visibility  BOARD_VISIBILITY  NOT NULL DEFAULT 'MEMBERS_ONLY',
    access_mode BOARD_ACCESS_MODE NOT NULL DEFAULT 'MANUAL_ADD',

    PRIMARY KEY (board_id),
    CONSTRAINT fk_owner
        FOREIGN KEY (ownership)
            REFERENCES users (user_uuid)
);

-- Board membership can be requested, offered, granted or blocked from requesting again
CREATE TYPE MEMBERSHIP_STATUS AS ENUM ('REQUESTED', 'OFFERED', 'GRANTED', 'BLOCKED');

-- Represents users within a board; Contains board specific permissions
CREATE TABLE board_members
(
    user_uuid        UUID              NOT NULL,
    board_id         VARCHAR(16)       NOT NULL,
    created_at       TIMESTAMP                  DEFAULT NOW(),
    status           MEMBERSHIP_STATUS NOT NULL DEFAULT 'BLOCKED',
    -- permissions and overrides
    can_view         BOOLEAN                    DEFAULT true,
    can_use          BOOLEAN                    DEFAULT true,
    can_manage       BOOLEAN                    DEFAULT false,
    can_administrate BOOLEAN                    DEFAULT false,

    PRIMARY KEY (user_uuid, board_id),
    CONSTRAINT fk_user
        FOREIGN KEY (user_uuid)
            REFERENCES users (user_uuid),
    CONSTRAINT fk_board
        FOREIGN KEY (board_id)
            REFERENCES boards (board_id)
);

-- Assigns a general category to a ticket
CREATE TYPE TICKET_CATEGORY AS ENUM ('EPIC', 'BUG', 'STORY');
-- Describes the urgency to resolve a ticket
CREATE TYPE TICKET_PRIORITY AS ENUM ('CRITICAL', 'HIGH', 'NORMAL', 'LOW', 'NONE');
--
CREATE TYPE TICKET_STATUS AS ENUM ('OPEN', 'CLOSED', 'DELETED');

-- Represents a work ticket
CREATE TABLE ticket
(
    ticket_id  INT                      DEFAULT -1,
    board_id   VARCHAR(16)     NOT NULL,
    created_at TIMESTAMP                DEFAULT NOW(),
    created_by UUID            NOT NULL,
    category   TICKET_CATEGORY NOT NULL,
    priority   TICKET_PRIORITY NOT NULL DEFAULT 'NORMAL',
    status     TICKET_STATUS   NOT NULL DEFAULT 'OPEN',
    title      VARCHAR(100)    NOT NULL,
    content    TEXT            NOT NULL DEFAULT '',
    assignee   UUID                     DEFAULT NULL,

    PRIMARY KEY (ticket_id, board_id),
    CONSTRAINT fk_user
        FOREIGN KEY (created_by)
            REFERENCES users (user_uuid),
    CONSTRAINT fk_board
        FOREIGN KEY (board_id)
            REFERENCES boards (board_id),
    CONSTRAINT fk_assignee
        FOREIGN KEY (created_by)
            REFERENCES users (user_uuid)
);

-- Function to create increasing ticket ids based on the board
-- As seen https://stackoverflow.com/a/34571410
CREATE
OR REPLACE FUNCTION TICKET_ID_INCREMENT() RETURNS TRIGGER LANGUAGE plpgsql AS
$$
    DECLARE
maxID int := 0;
BEGIN
SELECT MAX(ticket_id) + 1
INTO maxID
FROM ticket
WHERE board_id = NEW.board_id;
IF
maxID is null THEN
            NEW.ticket_id := 1;
ELSE
             NEW.ticket_id := maxID;
END IF;
RETURN (NEW);
END;
$$;

-- create trigger to set the ticket id
CREATE TRIGGER trg_ticket_id_increment
    BEFORE INSERT
    ON ticket
    FOR EACH ROW EXECUTE PROCEDURE TICKET_ID_INCREMENT();