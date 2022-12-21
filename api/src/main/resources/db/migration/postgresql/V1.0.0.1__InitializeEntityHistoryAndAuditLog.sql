START TRANSACTION;

-- Representing changes made to a board which could be restored later
CREATE TABLE board_history
(
    board_id    VARCHAR(16) NOT NULL,
    version_id  INT         NOT NULL DEFAULT -1,
    changed_at  TIMESTAMP   NOT NULL DEFAULT NOW(),
    editor      UUID        NOT NULL,

    title       VARCHAR(64)          DEFAULT NULL,
    description TEXT                 DEFAULT NULL,
    visibility  BOARD_VISIBILITY     DEFAULT NULL,
    access_mode BOARD_ACCESS_MODE    DEFAULT NULL,

    PRIMARY KEY (board_id, version_id),
    CONSTRAINT fk_board
        FOREIGN KEY (board_id)
            REFERENCES boards (board_id)
            ON DELETE CASCADE
);

-- Trigger to inject history versions
CREATE FUNCTION BOARD_HISTORY_ID_INCREMENT_FNT()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
DECLARE
    maxID int := 0;
BEGIN
    SELECT MAX(version_id) + 1 INTO maxID FROM board_history WHERE board_id = NEW.board_id;
    IF maxID IS NULL THEN
        NEW.version_id := 1;
    ELSE
        NEW.version_id := maxID;
    END IF;
    RETURN (NEW);
END;
$$;

CREATE TRIGGER board_history_id_increment
    BEFORE INSERT
    ON board_history
    FOR EACH ROW
EXECUTE PROCEDURE BOARD_HISTORY_ID_INCREMENT_FNT();

-- Representing changes made to a ticket which may be restored later
CREATE TABLE ticket_history
(
    board_id   VARCHAR(16) NOT NULL,
    ticket_id  INT         NOT NULL,
    version_id INT         NOT NULL DEFAULT -1,
    changed_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    editor     UUID        NOT NULL DEFAULT '00000000-0000-4000-0000-000000000001',

    category   TICKET_CATEGORY      DEFAULT NULL,
    priority   TICKET_PRIORITY      DEFAULT NULL,
    status     TICKET_STATUS        DEFAULT NULL,
    title      VARCHAR(100)         DEFAULT NULL,
    content    TEXT                 DEFAULT NULL,
    assignee   UUID                 DEFAULT '00000000-0000-4000-0000-000000000001',

    PRIMARY KEY (board_id, ticket_id, version_id),
    CONSTRAINT fk_ticket
        FOREIGN KEY (board_id, ticket_id)
            REFERENCES tickets (board_id, ticket_id)
            ON DELETE CASCADE,
    CONSTRAINT fk_editor
        FOREIGN KEY (editor)
            REFERENCES users (user_uuid)
            ON DELETE SET DEFAULT,
    CONSTRAINT fk_assignee
        FOREIGN KEY (assignee)
            REFERENCES users (user_uuid)
            ON DELETE SET DEFAULT
);

-- Trigger to inject history versions
CREATE FUNCTION TICKET_HISTORY_ID_INCREMENT_FNT()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
DECLARE
    maxID int := 0;
BEGIN
    SELECT MAX(version_id) + 1 INTO maxID FROM ticket_history WHERE ticket_id = NEW.ticket_id;
    IF maxID IS NULL THEN
        NEW.version_id := 1;
    ELSE
        NEW.version_id := maxID;
    END IF;
    RETURN (NEW);
END;
$$;

CREATE TRIGGER ticket_history_id_increment
    BEFORE INSERT
    ON ticket_history
    FOR EACH ROW
EXECUTE PROCEDURE TICKET_HISTORY_ID_INCREMENT_FNT();

-- Trigger to correct actual default for user fallback
-- If no user has been provided, null should be inserted instead of the default
CREATE FUNCTION TICKET_HISTORY_FIX_USER_DEFAULT_FNT()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
BEGIN
    IF NEW.assignee = '00000000-0000-4000-0000-000000000001' THEN
        NEW.assignee := NULL;
    END IF;
    RETURN (NEW);
END;
$$;

CREATE TRIGGER ticket_history_fix_user_default
    BEFORE INSERT
    ON ticket_history
    FOR EACH ROW
EXECUTE PROCEDURE TICKET_HISTORY_FIX_USER_DEFAULT_FNT();

-- Create audit actions
CREATE TYPE AUDIT_ACTION AS ENUM (
    'BOARD_CREATE', 'BOARD_MODIFY', 'BOARD_DELETE', 'BOARD_HISTORY_RESTORE',
    'TICKET_CREATE', 'TICKET_MODIFY', 'TICKET_EDIT', 'TICKET_HISTORY_RESTORE',
    'MEMBERSHIP_INVITE_CREATE', 'MEMBERSHIP_INVITE_ACCEPT', 'MEMBERSHIP_MODIFY', 'MEMBERSHIP_DELETE'
    );

CREATE TABLE audit_log
(
    audit_id    BIGSERIAL    NOT NULL,
    actor       UUID         NOT NULL DEFAULT '00000000-0000-4000-0000-000000000001',
    action_time TIMESTAMP    NOT NULL DEFAULT NOW(),
    action      AUDIT_ACTION NOT NULL,
    entity_hint TEXT         NOT NULL,

    PRIMARY KEY (audit_id),
    CONSTRAINT fk_actor
        FOREIGN KEY (actor)
            REFERENCES users (user_uuid)
            ON DELETE SET DEFAULT
);

COMMIT;