BEGIN;

    -- Add default members when a new board has been inserted
    CREATE FUNCTION ADD_DEFAULT_BOARD_MEMBERS_FNT()
        RETURNS TRIGGER
        LANGUAGE plpgsql
    AS
    $$
    DECLARE
        maxID int := 0;
    BEGIN
        -- add owner as member
        INSERT into board_members (user_uuid, board_id, status, can_view, can_use, can_manage, can_administrate)
        VALUES (NEW.ownership, NEW.board_id, 'GRANTED', true, true, true, true);
        -- add guest as member
        IF NEW.visibility = 'ANYONE' THEN
            INSERT into board_members (user_uuid, board_id, status, can_view, can_use, can_manage, can_administrate)
            VALUES ('00000000-0000-4000-0000-000000000002', NEW.board_id, 'GRANTED', true, false, false, false);
        ELSE
            INSERT into board_members (user_uuid, board_id, status, can_view, can_use, can_manage, can_administrate)
            VALUES ('00000000-0000-4000-0000-000000000002', NEW.board_id, 'BLOCKED', false, false, false, false);
        END IF;
        RETURN NULL;
    END;
    $$;

    CREATE TRIGGER add_default_board_members
        AFTER INSERT
        ON boards
        FOR EACH ROW
    EXECUTE PROCEDURE ADD_DEFAULT_BOARD_MEMBERS_FNT();

    -- Update guest user on visibility change
    CREATE FUNCTION UPDATE_GUEST_MEMBER_ON_VISIBILITY_CHANGE_FNT()
        RETURNS TRIGGER
        LANGUAGE plpgsql
    AS
    $$
    BEGIN
        IF NEW.visibility = 'ANYONE' THEN
            INSERT into board_members (user_uuid, board_id, status, can_view)
            VALUES ('00000000-0000-4000-0000-000000000002', NEW.board_id, 'GRANTED', true)
            ON CONFLICT (user_uuid, board_id) DO UPDATE SET status = 'GRANTED', can_view = true;
        ELSE
            INSERT into board_members (user_uuid, board_id, status, can_view)
            VALUES ('00000000-0000-4000-0000-000000000002', NEW.board_id, 'BLOCKED', false)
            ON CONFLICT (user_uuid, board_id) DO UPDATE SET status = 'BLOCKED', can_view = false;
        END IF;
        RETURN NULL;
    END;
    $$;

    CREATE TRIGGER update_guest_member_on_visibility_change
        AFTER UPDATE OF visibility
        ON boards
        FOR EACH ROW
    EXECUTE PROCEDURE UPDATE_GUEST_MEMBER_ON_VISIBILITY_CHANGE_FNT();

    -- Apply default permissions on member status changes
    CREATE FUNCTION APPLY_STATUS_PERMISSION_OVERRIDES_FNT()
        RETURNS TRIGGER
        LANGUAGE plpgsql
    AS
    $$
    BEGIN
        IF NEW.status != 'GRANTED' THEN -- deny all permissions if access has not been granted
            NEW.can_use = false;
            NEW.can_view = false;
            NEW.can_manage = false;
            NEW.can_administrate = false;
        END IF;
        RETURN (NEW);
    END;
    $$;

    CREATE TRIGGER apply_status_permission_override
        BEFORE INSERT OR UPDATE OF status
        ON board_members
        FOR EACH ROW
    EXECUTE PROCEDURE APPLY_STATUS_PERMISSION_OVERRIDES_FNT();

COMMIT;