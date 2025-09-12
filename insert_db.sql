-- =============================================
-- ManageZoo 테스트 데이터 삽입 스크립트
-- 예외처리 및 안전성이 강화된 버전
-- =============================================

-- 안전한 환경 설정
SET sql_mode = 'STRICT_TRANS_TABLES,NO_ZERO_DATE,NO_ZERO_IN_DATE,ERROR_FOR_DIVISION_BY_ZERO';
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;
SET collation_connection = utf8mb4_unicode_ci;

-- 데이터베이스 선택
USE manazoo;

-- 트랜잭션 시작
START TRANSACTION;

-- =============================================
-- 기존 데이터 안전한 삭제 (참조 무결성 고려)
-- =============================================
SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM `animal_income_expends` WHERE 1=1;
DELETE FROM `enclosure_income_expends` WHERE 1=1;
DELETE FROM `zoo_keeper_income_expends` WHERE 1=1;
DELETE FROM `enclosure_caretakers` WHERE 1=1;
DELETE FROM `income_expends` WHERE 1=1;
DELETE FROM `animals` WHERE 1=1;
DELETE FROM `reservations` WHERE 1=1;
DELETE FROM `zoo_keepers` WHERE 1=1;
DELETE FROM `enclosures` WHERE 1=1;
DELETE FROM `id_generator` WHERE 1=1;

SET FOREIGN_KEY_CHECKS = 1;

-- =============================================
-- 1. ID 생성기 초기 데이터
-- =============================================
INSERT IGNORE INTO `id_generator` (`prefix`, `last_number`) VALUES
('E', 5),     -- 사육장 ID 생성기
('K', 5),     -- 사육사 ID 생성기  
('R', 5),     -- 예약 ID 생성기
('A', 8),     -- 동물 ID 생성기
('I', 10)     -- 수입지출 ID 생성기
ON DUPLICATE KEY UPDATE 
    `last_number` = VALUES(`last_number`);

-- =============================================
-- 2. 사육장 테이블 데이터
-- =============================================
INSERT INTO `enclosures` (`id`, `name`, `area_size`, `temperature`, `location_type`, `environment_type`, `created_at`) VALUES
('E-0001', '아프리카 사바나 서식지', 2500.0, 28.5, 'OUTDOOR', 'LAND', '2024-01-15 09:00:00'),
('E-0002', '아시아 열대우림관', 1800.0, 32.0, 'INDOOR', 'MIXED', '2024-01-20 10:30:00'),
('E-0003', '북극 툰드라 전시관', 3200.0, -5.0, 'OUTDOOR', 'MIXED', '2024-02-01 11:15:00'),
('E-0004', '맹금류 비행장', 1500.0, 22.0, 'OUTDOOR', 'LAND', '2024-02-10 14:20:00'),
('E-0005', '해양 수족관 단지', 2200.0, 18.0, 'INDOOR', 'AQUATIC', '2024-02-15 16:45:00')
ON DUPLICATE KEY UPDATE 
    `name` = VALUES(`name`),
    `area_size` = VALUES(`area_size`),
    `temperature` = VALUES(`temperature`),
    `updated_at` = CURRENT_TIMESTAMP;

-- =============================================
-- 3. 사육사 테이블 데이터
-- =============================================
INSERT INTO `zoo_keepers` (`id`, `name`, `age`, `gender`, `department`, `rank_level`, `is_working`, `experience_year`, `can_handle_danger_animal`, `licenses`, `salary`, `created_at`) VALUES
('K-0001', '김동물', 35, 'MALE', 'MAMMAL', 'SENIOR_KEEPER', TRUE, 12, TRUE, '야생동물관리 자격증, 동물보건 인증서', 4500000, '2024-01-05 08:30:00'),
('K-0002', '박조류', 28, 'FEMALE', 'BIRD', 'KEEPER', TRUE, 5, FALSE, '조류전문가 자격증, 응급처치 인증서', 3200000, '2024-01-10 09:15:00'),
('K-0003', '이해양', 42, 'MALE', 'FISH', 'HEAD_KEEPER', TRUE, 18, TRUE, '수생동물관리 자격증, 잠수 인증서', 5800000, '2024-01-12 10:00:00'),
('K-0004', '정파충', 31, 'FEMALE', 'REPTILE', 'SENIOR_KEEPER', TRUE, 8, TRUE, '파충류전문가 자격증, 독성동물취급 자격증', 4200000, '2024-01-18 11:30:00'),
('K-0005', '최교육', 26, 'MALE', 'EDUCATION', 'JUNIOR_KEEPER', TRUE, 2, FALSE, '환경교육 인증서, 관광가이드 자격증', 2800000, '2024-02-01 13:45:00')
ON DUPLICATE KEY UPDATE 
    `name` = VALUES(`name`),
    `age` = VALUES(`age`),
    `salary` = VALUES(`salary`),
    `updated_at` = CURRENT_TIMESTAMP;

-- =============================================
-- 4. 방문 예약 테이블 데이터
-- =============================================
INSERT INTO `reservations` (`id`, `name`, `phone_number`, `visit_date`, `number_of_visitors`, `number_of_adults`, `number_of_children`, `created_at`) VALUES
('R-0001', '김가족', '010-1234-5678', DATE_ADD(CURDATE(), INTERVAL 5 DAY), 4, 2, 2, '2024-03-10 14:30:00'),
('R-0002', '박부부', '010-2345-6789', DATE_ADD(CURDATE(), INTERVAL 6 DAY), 2, 2, 0, '2024-03-11 16:20:00'),
('R-0003', '이그룹', '010-3456-7890', DATE_ADD(CURDATE(), INTERVAL 7 DAY), 8, 4, 4, '2024-03-12 10:15:00'),
('R-0004', '서울초등학교', '010-4567-8901', DATE_ADD(CURDATE(), INTERVAL 8 DAY), 25, 3, 22, '2024-03-13 09:45:00'),
('R-0005', '테크회사', '010-5678-9012', DATE_ADD(CURDATE(), INTERVAL 10 DAY), 12, 12, 0, '2024-03-15 11:30:00')
ON DUPLICATE KEY UPDATE 
    `name` = VALUES(`name`),
    `phone_number` = VALUES(`phone_number`),
    `visit_date` = VALUES(`visit_date`),
    `updated_at` = CURRENT_TIMESTAMP;

-- =============================================
-- 5. 동물 테이블 데이터
-- =============================================
INSERT INTO `animals` (`id`, `name`, `species`, `age`, `gender`, `health_status`, `enclosure_id`, `created_at`) VALUES
('A-0001', '심바', 'Lion', 8, 'MALE', 'Good', 'E-0001', '2024-01-20 09:00:00'),
('A-0002', '날라', 'Lion', 6, 'FEMALE', 'Good', 'E-0001', '2024-01-20 09:30:00'),
('A-0003', '라자', 'Tiger', 5, 'MALE', 'Fair', 'E-0002', '2024-01-25 10:15:00'),
('A-0004', '아르테미스', 'Eagle', 3, 'FEMALE', 'Good', 'E-0004', '2024-02-05 11:45:00'),
('A-0005', '프로스트', 'Bear', 10, 'FEMALE', 'Good', 'E-0003', '2024-02-12 14:20:00'),
('A-0006', '섀도우', 'Wolf', 4, 'MALE', 'Good', 'E-0001', '2024-02-18 15:30:00'),
('A-0007', '아테나', 'Owl', 2, 'FEMALE', 'Good', 'E-0004', '2024-02-20 16:00:00'),
('A-0008', '서펀트', 'Snake', 1, 'MALE', 'Good', 'E-0002', '2024-02-25 17:15:00')
ON DUPLICATE KEY UPDATE 
    `name` = VALUES(`name`),
    `age` = VALUES(`age`),
    `health_status` = VALUES(`health_status`),
    `updated_at` = CURRENT_TIMESTAMP;

-- =============================================
-- 6. 수입/지출 테이블 데이터
-- =============================================
INSERT INTO `income_expends` (`id`, `amount`, `description`, `date`, `type`, `event_type`, `reservation_id`, `zookeeper_id`, `created_at`) VALUES
('I-0001', 32000, '김가족 입장료 (성인 2명, 어린이 2명)', CURDATE(), 'INCOME', 'FEE', 'R-0001', NULL, '2024-03-15 09:30:00'),
('I-0002', 20000, '박부부 입장료 (성인 2명)', CURDATE(), 'INCOME', 'FEE', 'R-0002', NULL, '2024-03-16 10:15:00'),
('I-0003', 4500000, '김동물 3월 급여', DATE_FORMAT(CURDATE(), '%Y-%m-01'), 'EXPENSE', 'EMPLOYEE_MONTH', NULL, 'K-0001', '2024-03-01 09:00:00'),
('I-0004', 3200000, '박조류 3월 급여', DATE_FORMAT(CURDATE(), '%Y-%m-01'), 'EXPENSE', 'EMPLOYEE_MONTH', NULL, 'K-0002', '2024-03-01 09:00:00'),
('I-0005', 150000, '사자 사료 구매', CURDATE(), 'EXPENSE', 'FOOD', NULL, NULL, '2024-03-05 14:20:00'),
('I-0006', 64000, '이그룹 입장료 (성인 4명, 어린이 4명)', CURDATE(), 'INCOME', 'FEE', 'R-0003', NULL, '2024-03-17 11:30:00'),
('I-0007', 350000, '서울초등학교 단체입장료 (성인 3명, 어린이 22명)', CURDATE(), 'INCOME', 'FEE', 'R-0004', NULL, '2024-03-18 13:45:00'),
('I-0008', 120000, '테크회사 입장료 (성인 12명)', CURDATE(), 'INCOME', 'FEE', 'R-0005', NULL, '2024-03-20 10:00:00'),
('I-0009', 80000, '독수리 의료비', CURDATE(), 'EXPENSE', 'FOOD', NULL, NULL, '2024-03-10 16:30:00'),
('I-0010', 200000, '사육장 유지보수비', CURDATE(), 'EXPENSE', 'ENCLOSURE', NULL, NULL, '2024-03-12 15:45:00')
ON DUPLICATE KEY UPDATE 
    `amount` = VALUES(`amount`),
    `description` = VALUES(`description`),
    `updated_at` = CURRENT_TIMESTAMP;

-- =============================================
-- 7. 사육장-사육사 관계 데이터
-- =============================================
INSERT INTO `enclosure_caretakers` (`enclosure_id`, `keeper_id`, `assigned_at`) VALUES
('E-0001', 'K-0001', '2024-01-20 09:00:00'), -- 아프리카 사바나 - 김동물 (포유류 전문)
('E-0002', 'K-0001', '2024-01-25 10:00:00'), -- 아시아 열대우림 - 김동물 (포유류 전문)
('E-0003', 'K-0001', '2024-02-12 14:00:00'), -- 북극 툰드라 - 김동물 (포유류 전문)
('E-0004', 'K-0002', '2024-02-05 11:30:00'), -- 맹금류 비행장 - 박조류 (조류 전문)
('E-0005', 'K-0003', '2024-02-15 16:30:00'), -- 해양 수족관 - 이해양 (어류 전문)
('E-0002', 'K-0004', '2024-02-25 17:00:00'), -- 아시아 열대우림 - 정파충 (파충류 전문, 뱀 관리)
('E-0001', 'K-0005', '2024-03-01 08:30:00'), -- 아프리카 사바나 - 최교육 (교육 프로그램)
('E-0004', 'K-0005', '2024-03-01 08:30:00')  -- 맹금류 비행장 - 최교육 (교육 프로그램)
ON DUPLICATE KEY UPDATE 
    `assigned_at` = VALUES(`assigned_at`);

-- =============================================
-- 8. 사육사-급여 관계 데이터
-- =============================================
INSERT INTO `zoo_keeper_income_expends` (`keeper_id`, `income_expend_id`, `salary_month`, `created_at`) VALUES
('K-0001', 'I-0003', DATE_FORMAT(CURDATE(), '%Y-%m'), '2024-03-01 09:00:00'), -- 김동물 3월 급여
('K-0002', 'I-0004', DATE_FORMAT(CURDATE(), '%Y-%m'), '2024-03-01 09:00:00')  -- 박조류 3월 급여
ON DUPLICATE KEY UPDATE 
    `created_at` = VALUES(`created_at`);

-- =============================================
-- 9. 동물-비용 관계 데이터
-- =============================================
INSERT INTO `animal_income_expends` (`animal_id`, `income_expend_id`, `expense_type`, `created_at`) VALUES
('A-0001', 'I-0005', 'FOOD', '2024-03-05 14:20:00'), -- 심바 (사자) 사료비
('A-0002', 'I-0005', 'FOOD', '2024-03-05 14:20:00'), -- 날라 (사자) 사료비
('A-0004', 'I-0009', 'MEDICAL', '2024-03-10 16:30:00') -- 아르테미스 (독수리) 의료비
ON DUPLICATE KEY UPDATE 
    `created_at` = VALUES(`created_at`);

-- =============================================
-- 10. 사육장-유지보수 관계 데이터
-- =============================================
INSERT INTO `enclosure_income_expends` (`enclosure_id`, `income_expend_id`, `maintenance_type`, `created_at`) VALUES
('E-0001', 'I-0010', 'FACILITY', '2024-03-12 15:45:00'), -- 아프리카 사바나 유지보수
('E-0004', 'I-0010', 'FACILITY', '2024-03-12 15:45:00')  -- 맹금류 비행장 유지보수
ON DUPLICATE KEY UPDATE 
    `created_at` = VALUES(`created_at`);

-- 트랜잭션 커밋
COMMIT;

-- =============================================
-- 데이터 삽입 검증 쿼리
-- =============================================
SELECT 
    'id_generator' as 테이블명, COUNT(*) as 레코드수 FROM `id_generator`
UNION ALL
SELECT 'enclosures', COUNT(*) FROM `enclosures`
UNION ALL
SELECT 'zoo_keepers', COUNT(*) FROM `zoo_keepers`
UNION ALL
SELECT 'reservations', COUNT(*) FROM `reservations`
UNION ALL
SELECT 'animals', COUNT(*) FROM `animals`
UNION ALL
SELECT 'income_expends', COUNT(*) FROM `income_expends`
UNION ALL
SELECT 'enclosure_caretakers', COUNT(*) FROM `enclosure_caretakers`
UNION ALL
SELECT 'zoo_keeper_income_expends', COUNT(*) FROM `zoo_keeper_income_expends`
UNION ALL
SELECT 'animal_income_expends', COUNT(*) FROM `animal_income_expends`
UNION ALL
SELECT 'enclosure_income_expends', COUNT(*) FROM `enclosure_income_expends`;

-- =============================================
-- 데이터 삽입 완료
-- =============================================
