-- =====================================================
-- MySQL Triggers for Standard Question Status Update
-- =====================================================
-- Purpose: Automatically update std_questions.status based on std_answers changes
-- Tables: std_questions, std_answers
-- Status Values: 'WAITING_ANSWERS', 'ANSWERED' for questions; 'ACCEPTED', 'OMITTED' for answers

-- =====================================================
-- Trigger 1: Handle INSERT on std_answers
-- =====================================================
DELIMITER //
CREATE TRIGGER trg_std_answer_insert
AFTER INSERT ON std_answers
FOR EACH ROW
BEGIN
    -- Only update to ANSWERED if there's at least one ACCEPTED answer
    IF NEW.status = 'ACCEPTED' THEN
        UPDATE std_questions 
        SET status = 'ANSWERED' 
        WHERE id = NEW.std_question_id 
        AND status != 'ANSWERED';  -- Avoid unnecessary updates
    END IF;
END//
DELIMITER ;

-- =====================================================
-- Trigger 2: Handle UPDATE on std_answers
-- =====================================================
DELIMITER //
CREATE TRIGGER trg_std_answer_update
AFTER UPDATE ON std_answers
FOR EACH ROW
BEGIN
    DECLARE accepted_count INT DEFAULT 0;
    
    -- Only proceed if status actually changed
    IF OLD.status != NEW.status THEN
        -- Count current ACCEPTED answers for this question
        SELECT COUNT(*) INTO accepted_count
        FROM std_answers 
        WHERE std_question_id = NEW.std_question_id 
        AND status = 'ACCEPTED';
        
        -- Update question status based on accepted answer count
        IF accepted_count > 0 THEN
            UPDATE std_questions 
            SET status = 'ANSWERED' 
            WHERE id = NEW.std_question_id 
            AND status != 'ANSWERED';
        ELSE
            UPDATE std_questions 
            SET status = 'WAITING_ANSWERS' 
            WHERE id = NEW.std_question_id 
            AND status != 'WAITING_ANSWERS';
        END IF;
    END IF;
END//
DELIMITER ;

-- =====================================================
-- Trigger 3: Handle DELETE on std_answers
-- =====================================================
DELIMITER //
CREATE TRIGGER trg_std_answer_delete
AFTER DELETE ON std_answers
FOR EACH ROW
BEGIN
    DECLARE accepted_count INT DEFAULT 0;
    
    -- Count remaining ACCEPTED answers for this question
    SELECT COUNT(*) INTO accepted_count
    FROM std_answers 
    WHERE std_question_id = OLD.std_question_id 
    AND status = 'ACCEPTED';
    
    -- Update question status based on remaining accepted answers
    IF accepted_count > 0 THEN
        UPDATE std_questions 
        SET status = 'ANSWERED' 
        WHERE id = OLD.std_question_id 
        AND status != 'ANSWERED';
    ELSE
        UPDATE std_questions 
        SET status = 'WAITING_ANSWERS' 
        WHERE id = OLD.std_question_id 
        AND status != 'WAITING_ANSWERS';
    END IF;
END//
DELIMITER ;

-- =====================================================
-- Verification Queries
-- =====================================================
-- Check if triggers were created successfully
SHOW TRIGGERS WHERE `Table` IN ('std_answers');

-- Query to verify trigger behavior (run after testing)
SELECT 
    sq.id as question_id,
    sq.status as question_status,
    COUNT(sa.id) as total_answers,
    SUM(CASE WHEN sa.status = 'ACCEPTED' THEN 1 ELSE 0 END) as accepted_answers,
    SUM(CASE WHEN sa.status = 'OMITTED' THEN 1 ELSE 0 END) as omitted_answers
FROM std_questions sq
LEFT JOIN std_answers sa ON sq.id = sa.std_question_id
GROUP BY sq.id, sq.status
ORDER BY sq.id;

-- =====================================================
-- Emergency: Drop Triggers (if needed)
-- =====================================================
-- Uncomment these lines if you need to remove the triggers:
-- DROP TRIGGER IF EXISTS trg_std_answer_insert;
-- DROP TRIGGER IF EXISTS trg_std_answer_update;
-- DROP TRIGGER IF EXISTS trg_std_answer_delete;

-- =====================================================
-- Testing Script
-- =====================================================
-- Test the triggers with sample data:
/*
-- 1. Insert a standard question
INSERT INTO std_questions (original_raw_question_id, type, content) 
VALUES (1, 'OBJECTIVE', 'Test question for trigger verification');

-- Check initial status (should be WAITING_ANSWERS)
SELECT id, status FROM std_questions WHERE content = 'Test question for trigger verification';

-- 2. Insert a standard answer with ACCEPTED status
INSERT INTO std_answers (std_question_id, type, score, status, selected_from_candidate_id) 
VALUES (LAST_INSERT_ID(), 'OBJECTIVE', 8, 'ACCEPTED', 1);

-- Check status after insert (should be ANSWERED)
SELECT sq.id, sq.status, sa.status as answer_status 
FROM std_questions sq 
JOIN std_answers sa ON sq.id = sa.std_question_id 
WHERE sq.content = 'Test question for trigger verification';

-- 3. Update answer status to OMITTED
UPDATE std_answers 
SET status = 'OMITTED' 
WHERE std_question_id = (SELECT id FROM std_questions WHERE content = 'Test question for trigger verification');

-- Check status after update (should be WAITING_ANSWERS)
SELECT sq.id, sq.status 
FROM std_questions sq 
WHERE sq.content = 'Test question for trigger verification';

-- 4. Clean up test data
DELETE FROM std_answers WHERE std_question_id = (SELECT id FROM std_questions WHERE content = 'Test question for trigger verification');
DELETE FROM std_questions WHERE content = 'Test question for trigger verification';
*/ 