-- Add demo board
INSERT INTO boards (board_id, title, description, ownership, visibility, access_mode)
VALUES  ('TICAT', 'TiCat - Demo Board', 'This is a demo board public to everyone', '00000000-0000-4000-0000-000000000000', 'ANYONE', 'MANUAL_ADD');
-- Add a couple of tickets
INSERT INTO ticket (ticket_id, board_id, created_by, category, priority, status, title, content)
VALUES  (0, 'TICAT', '00000000-0000-4000-0000-000000000000', 'EPIC', 'HIGH', 'OPEN', 'A big task', 'This ticket is epic!'),
        (0, 'TICAT', '00000000-0000-4000-0000-000000000000', 'BUG', 'CRITICAL', 'OPEN', 'A critical bug', 'A bug that needs to be fixed asap!'),
        (0, 'TICAT', '00000000-0000-4000-0000-000000000000', 'STORY', 'LOW', 'OPEN', 'An easy story', 'A good story for later!'),
        (0, 'TICAT', '00000000-0000-4000-0000-000000000000', 'STORY', 'NORMAL', 'CLOSED', 'A resolved story', 'This ticket has been closed'),
        (0, 'TICAT', '00000000-0000-4000-0000-000000000000', 'BUG', 'NONE', 'DELETED', 'A deleted bug', 'This bug did not actually exist')