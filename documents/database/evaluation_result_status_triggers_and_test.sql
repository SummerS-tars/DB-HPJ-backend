-- =====================================================
-- MySQL Triggers for Evaluation Result Status Update
-- =====================================================
-- Purpose: Automatically update evaluation_results.status based on evaluation_analysis changes
-- Tables: evaluation_results, evaluation_analysis
-- Status Values: 'PENDING', 'ANALYZED', 'OMITTED' for evaluation results

-- =====================================================
-- Trigger 1: Handle INSERT on evaluation_analysis
-- =====================================================
DELIMITER //
CREATE TRIGGER trg_evaluation_analysis_insert
AFTER INSERT ON evaluation_analysis
FOR EACH ROW
BEGIN
    -- Update evaluation result status to ANALYZED when analysis is added
    UPDATE evaluation_results 
    SET status = 'ANALYZED' 
    WHERE id = NEW.evaluation_result_id 
    AND status != 'ANALYZED';  -- Avoid unnecessary updates
END//
DELIMITER ;

-- =====================================================
-- Trigger 2: Handle UPDATE on evaluation_analysis
-- =====================================================
DELIMITER //
CREATE TRIGGER trg_evaluation_analysis_update
AFTER UPDATE ON evaluation_analysis
FOR EACH ROW
BEGIN
    -- No specific logic needed for updates since the existence of analysis
    -- is what matters, not the content. Status should remain ANALYZED
    -- as long as at least one analysis exists.
    
    -- Ensure the evaluation result is marked as ANALYZED
    UPDATE evaluation_results 
    SET status = 'ANALYZED' 
    WHERE id = NEW.evaluation_result_id 
    AND status != 'ANALYZED';
END//
DELIMITER ;

-- =====================================================
-- Trigger 3: Handle DELETE on evaluation_analysis
-- =====================================================
DELIMITER //
CREATE TRIGGER trg_evaluation_analysis_delete
AFTER DELETE ON evaluation_analysis
FOR EACH ROW
BEGIN
    DECLARE analysis_count INT DEFAULT 0;
    
    -- Count remaining analysis records for this evaluation result
    SELECT COUNT(*) INTO analysis_count
    FROM evaluation_analysis 
    WHERE evaluation_result_id = OLD.evaluation_result_id;
    
    -- Update evaluation result status based on remaining analysis count
    IF analysis_count > 0 THEN
        -- Still has analysis, ensure status is ANALYZED
        UPDATE evaluation_results 
        SET status = 'ANALYZED' 
        WHERE id = OLD.evaluation_result_id 
        AND status != 'ANALYZED';
    ELSE
        -- No analysis left, set status back to PENDING
        UPDATE evaluation_results 
        SET status = 'PENDING' 
        WHERE id = OLD.evaluation_result_id 
        AND status != 'PENDING';
    END IF;
END//
DELIMITER ;

-- =====================================================
-- Verification Queries
-- =====================================================
-- Check if triggers were created successfully
SHOW TRIGGERS WHERE `Table` IN ('evaluation_analysis');

-- Query to verify trigger behavior (run after testing)
SELECT 
    er.id as evaluation_result_id,
    er.status as evaluation_result_status,
    COUNT(ea.id) as total_analysis,
    er.evaluation_tag_id,
    er.std_question_id
FROM evaluation_results er
LEFT JOIN evaluation_analysis ea ON er.id = ea.evaluation_result_id
GROUP BY er.id, er.status, er.evaluation_tag_id, er.std_question_id
ORDER BY er.id;

-- =====================================================
-- Emergency: Drop Triggers (if needed)
-- =====================================================
-- Uncomment these lines if you need to remove the triggers:
-- DROP TRIGGER IF EXISTS trg_evaluation_analysis_insert;
-- DROP TRIGGER IF EXISTS trg_evaluation_analysis_update;
-- DROP TRIGGER IF EXISTS trg_evaluation_analysis_delete;

-- =====================================================
-- Testing Script
-- =====================================================
-- Test the triggers with sample data:
/*
-- Prerequisites: Ensure you have evaluation_results with PENDING status

-- 1. Check initial status of an evaluation result
SELECT id, status FROM evaluation_results WHERE id = 7;  -- Should be PENDING

-- 2. Insert an evaluation analysis
INSERT INTO evaluation_analysis (evaluation_result_id, analysis_tag_id, score, created_at) 
VALUES (7, 1, 10, NOW());

-- Check status after insert (should be ANALYZED)
SELECT er.id, er.status, ea.score 
FROM evaluation_results er 
LEFT JOIN evaluation_analysis ea ON er.id = ea.evaluation_result_id 
WHERE er.id = 7;

-- 3. Insert another analysis for the same evaluation result
INSERT INTO evaluation_analysis (evaluation_result_id, analysis_tag_id, score, created_at) 
VALUES (7, 2, 8, NOW());

-- Check status (should still be ANALYZED)
SELECT er.id, er.status, COUNT(ea.id) as analysis_count
FROM evaluation_results er 
LEFT JOIN evaluation_analysis ea ON er.id = ea.evaluation_result_id 
WHERE er.id = 7
GROUP BY er.id, er.status;

-- 4. Delete one analysis
DELETE FROM evaluation_analysis 
WHERE evaluation_result_id = 7 AND analysis_tag_id = 2;

-- Check status (should still be ANALYZED since one analysis remains)
SELECT er.id, er.status, COUNT(ea.id) as analysis_count
FROM evaluation_results er 
LEFT JOIN evaluation_analysis ea ON er.id = ea.evaluation_result_id 
WHERE er.id = 7
GROUP BY er.id, er.status;

-- 5. Delete the remaining analysis
DELETE FROM evaluation_analysis 
WHERE evaluation_result_id = 7 AND analysis_tag_id = 1;

-- Check status (should be PENDING since no analysis remains)
SELECT er.id, er.status, COUNT(ea.id) as analysis_count
FROM evaluation_results er 
LEFT JOIN evaluation_analysis ea ON er.id = ea.evaluation_result_id 
WHERE er.id = 7
GROUP BY er.id, er.status;
*/

-- =====================================================
-- Data Consistency Fix (Run once after trigger creation)
-- =====================================================
-- Update existing evaluation results to have correct status based on current analysis
UPDATE evaluation_results er
SET status = CASE 
    WHEN EXISTS (
        SELECT 1 FROM evaluation_analysis ea 
        WHERE ea.evaluation_result_id = er.id
    ) THEN 'ANALYZED'
    ELSE 'PENDING'
END
WHERE er.status IN ('PENDING', 'ANALYZED');

-- Verification query for the consistency fix
SELECT 
    'After consistency fix' as description,
    er.status,
    COUNT(*) as count,
    SUM(CASE WHEN EXISTS (
        SELECT 1 FROM evaluation_analysis ea 
        WHERE ea.evaluation_result_id = er.id
    ) THEN 1 ELSE 0 END) as with_analysis,
    COUNT(*) - SUM(CASE WHEN EXISTS (
        SELECT 1 FROM evaluation_analysis ea 
        WHERE ea.evaluation_result_id = er.id
    ) THEN 1 ELSE 0 END) as without_analysis
FROM evaluation_results er
GROUP BY er.status
ORDER BY er.status; 