-- =============================================
-- ManageZoo 데이터베이스 스키마 생성 스크립트
-- 예외처리 및 안전성이 강화된 버전
-- =============================================

-- 안전한 환경 설정
SET sql_mode = 'STRICT_TRANS_TABLES,NO_ZERO_DATE,NO_ZERO_IN_DATE,ERROR_FOR_DIVISION_BY_ZERO';
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;
SET collation_connection = utf8mb4_unicode_ci;

-- 트랜잭션 시작
START TRANSACTION;

-- 데이터베이스 존재 확인 및 생성
CREATE DATABASE IF NOT EXISTS manazoo 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

USE manazoo;

-- =============================================
-- 기존 테이블 존재 시 안전한 삭제 (역순으로 삭제)
-- =============================================
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `animal_income_expends`;
DROP TABLE IF EXISTS `enclosure_income_expends`;
DROP TABLE IF EXISTS `zoo_keeper_income_expends`;
DROP TABLE IF EXISTS `enclosure_caretakers`;
DROP TABLE IF EXISTS `income_expends`;
DROP TABLE IF EXISTS `animals`;
DROP TABLE IF EXISTS `reservations`;
DROP TABLE IF EXISTS `zoo_keepers`;
DROP TABLE IF EXISTS `enclosures`;
DROP TABLE IF EXISTS `id_generator`;

SET FOREIGN_KEY_CHECKS = 1;

-- =============================================
-- 1. ID 생성기 테이블
-- =============================================
CREATE TABLE `id_generator` (
    `prefix` VARCHAR(10) NOT NULL COMMENT 'ID 접두사',
    `last_number` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '마지막 생성된 번호',
    PRIMARY KEY (`prefix`)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci 
  COMMENT='자동 ID 생성을 위한 시퀀스 관리 테이블';

-- =============================================
-- 2. 사육장 테이블
-- =============================================
CREATE TABLE `enclosures` (
    `id` VARCHAR(50) NOT NULL COMMENT '사육장 ID',
    `name` VARCHAR(100) NOT NULL COMMENT '사육장 이름',
    `area_size` DECIMAL(10,1) DEFAULT NULL COMMENT '면적 (㎡)',
    `temperature` DECIMAL(5,1) DEFAULT NULL COMMENT '온도 (℃)',
    `location_type` ENUM('INDOOR', 'OUTDOOR') NOT NULL COMMENT '위치 유형',
    `environment_type` ENUM('LAND', 'AQUATIC', 'MIXED') NOT NULL COMMENT '환경 유형',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (`id`),
    INDEX `idx_location_type` (`location_type`),
    INDEX `idx_environment_type` (`environment_type`),
    CONSTRAINT `chk_area_size` CHECK (`area_size` >= 0),
    CONSTRAINT `chk_temperature` CHECK (`temperature` >= -50 AND `temperature` <= 60)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci 
  COMMENT='사육장 정보 테이블';

-- =============================================
-- 3. 사육사 테이블
-- =============================================
CREATE TABLE `zoo_keepers` (
    `id` VARCHAR(50) NOT NULL COMMENT '사육사 ID',
    `name` VARCHAR(100) NOT NULL COMMENT '사육사 이름',
    `age` INT UNSIGNED DEFAULT NULL COMMENT '나이',
    `gender` ENUM('MALE', 'FEMALE') NOT NULL COMMENT '성별',
    `department` ENUM('MAMMAL', 'BIRD', 'REPTILE', 'FISH', 'MIXED', 'BREEDING_RESEARCH', 'VETERINARY_REHAB', 'EDUCATION') NOT NULL COMMENT '담당 부서',
    `rank_level` ENUM('JUNIOR_KEEPER', 'KEEPER', 'SENIOR_KEEPER', 'HEAD_KEEPER', 'MANAGER', 'DIRECTOR') NOT NULL COMMENT '직급',
    `is_working` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '재직 여부',
    `experience_year` INT UNSIGNED DEFAULT 0 COMMENT '경력 연수',
    `can_handle_danger_animal` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '위험동물 처리 가능 여부',
    `licenses` TEXT DEFAULT NULL COMMENT '보유 자격증',
    `salary` DECIMAL(12,0) UNSIGNED DEFAULT 0 COMMENT '급여',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (`id`),
    INDEX `idx_department` (`department`),
    INDEX `idx_rank_level` (`rank_level`),
    INDEX `idx_is_working` (`is_working`),
    CONSTRAINT `chk_age` CHECK (`age` >= 18 AND `age` <= 100),
    CONSTRAINT `chk_experience_year` CHECK (`experience_year` >= 0 AND `experience_year` <= 50),
    CONSTRAINT `chk_salary` CHECK (`salary` >= 0)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci 
  COMMENT='사육사 정보 테이블';

-- =============================================
-- 4. 방문 예약 테이블
-- =============================================
CREATE TABLE `reservations` (
    `id` VARCHAR(50) NOT NULL COMMENT '예약 ID',
    `name` VARCHAR(100) NOT NULL COMMENT '예약자 이름',
    `phone_number` VARCHAR(20) NOT NULL COMMENT '연락처',
    `visit_date` DATE NOT NULL COMMENT '방문일',
    `number_of_visitors` INT UNSIGNED NOT NULL DEFAULT 1 COMMENT '총 방문자 수',
    `number_of_adults` INT UNSIGNED NOT NULL DEFAULT 1 COMMENT '성인 수',
    `number_of_children` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '어린이 수',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (`id`),
    INDEX `idx_visit_date` (`visit_date`),
    INDEX `idx_phone_number` (`phone_number`),
    CONSTRAINT `chk_visitors_consistency` CHECK (`number_of_visitors` = `number_of_adults` + `number_of_children`),
    CONSTRAINT `chk_visitors_positive` CHECK (`number_of_visitors` > 0),
    CONSTRAINT `chk_adults_positive` CHECK (`number_of_adults` >= 0),
    CONSTRAINT `chk_children_positive` CHECK (`number_of_children` >= 0)
    -- 날짜 검증은 애플리케이션 레벨에서 처리 (MySQL 8.0 CHECK 제약조건 비결정적 함수 제한)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci 
  COMMENT='방문 예약 정보 테이블';

-- =============================================
-- 5. 동물 테이블
-- =============================================
CREATE TABLE `animals` (
    `id` VARCHAR(50) NOT NULL COMMENT '동물 ID',
    `name` VARCHAR(100) NOT NULL COMMENT '동물 이름',
    `species` ENUM('Lion', 'Tiger', 'Bear', 'Elephant', 'Wolf', 'Eagle', 'Owl', 'Snake') NOT NULL COMMENT '종류',
    `age` INT UNSIGNED DEFAULT NULL COMMENT '나이',
    `gender` ENUM('MALE', 'FEMALE') DEFAULT NULL COMMENT '성별',
    `health_status` ENUM('Good', 'Fair', 'Poor') DEFAULT 'Good' COMMENT '건강 상태',
    `enclosure_id` VARCHAR(50) DEFAULT NULL COMMENT '사육장 ID',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (`id`),
    INDEX `idx_species` (`species`),
    INDEX `idx_enclosure_id` (`enclosure_id`),
    INDEX `idx_health_status` (`health_status`),
    CONSTRAINT `chk_age_positive` CHECK (`age` >= 0 AND `age` <= 200),
    CONSTRAINT `fk_animals_enclosure` FOREIGN KEY (`enclosure_id`) REFERENCES `enclosures`(`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci 
  COMMENT='동물 정보 테이블';

-- =============================================
-- 6. 수입/지출 테이블
-- =============================================
CREATE TABLE `income_expends` (
    `id` VARCHAR(50) NOT NULL COMMENT '수입지출 ID',
    `amount` DECIMAL(15,0) NOT NULL DEFAULT 0 COMMENT '금액',
    `description` TEXT DEFAULT NULL COMMENT '설명',
    `date` DATE NOT NULL COMMENT '발생일',
    `type` ENUM('INCOME', 'EXPENSE') NOT NULL COMMENT '수입/지출 구분',
    `event_type` ENUM('FEE', 'EMPLOYEE_MONTH', 'EMPLOYEE_EXTRA', 'ENCLOSURE', 'SAFARI', 'AQUASHOW', 'EXPERIENCE', 'FOOD') NOT NULL COMMENT '이벤트 유형',
    `reservation_id` VARCHAR(50) DEFAULT NULL COMMENT '연관된 예약 ID',
    `zookeeper_id` VARCHAR(50) DEFAULT NULL COMMENT '연관된 사육사 ID',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (`id`),
    INDEX `idx_date` (`date`),
    INDEX `idx_type` (`type`),
    INDEX `idx_event_type` (`event_type`),
    INDEX `idx_reservation_id` (`reservation_id`),
    INDEX `idx_zookeeper_id` (`zookeeper_id`),
    CONSTRAINT `chk_amount_not_negative` CHECK (`amount` >= 0),
    CONSTRAINT `fk_income_expends_reservation` FOREIGN KEY (`reservation_id`) REFERENCES `reservations`(`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `fk_income_expends_zookeeper` FOREIGN KEY (`zookeeper_id`) REFERENCES `zoo_keepers`(`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci 
  COMMENT='수입지출 정보 테이블';

-- =============================================
-- 7. 사육장-사육사 관계 테이블
-- =============================================
CREATE TABLE `enclosure_caretakers` (
    `enclosure_id` VARCHAR(50) NOT NULL COMMENT '사육장 ID',
    `keeper_id` VARCHAR(50) NOT NULL COMMENT '사육사 ID',
    `assigned_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '배정일시',
    PRIMARY KEY (`enclosure_id`, `keeper_id`),
    INDEX `idx_keeper_id` (`keeper_id`),
    CONSTRAINT `fk_caretakers_enclosure` FOREIGN KEY (`enclosure_id`) REFERENCES `enclosures`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_caretakers_keeper` FOREIGN KEY (`keeper_id`) REFERENCES `zoo_keepers`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci 
  COMMENT='사육장-사육사 관계 테이블';

-- =============================================
-- 8. 사육사-수입지출 중간테이블
-- =============================================
CREATE TABLE `zoo_keeper_income_expends` (
    `keeper_id` VARCHAR(50) NOT NULL COMMENT '사육사 ID',
    `income_expend_id` VARCHAR(50) NOT NULL COMMENT '수입지출 ID',
    `salary_month` VARCHAR(7) NOT NULL COMMENT '급여 월 (YYYY-MM)',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    PRIMARY KEY (`keeper_id`, `income_expend_id`),
    INDEX `idx_salary_month` (`salary_month`),
    CONSTRAINT `fk_keeper_income_keeper` FOREIGN KEY (`keeper_id`) REFERENCES `zoo_keepers`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_keeper_income_expend` FOREIGN KEY (`income_expend_id`) REFERENCES `income_expends`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `chk_salary_month_format` CHECK (`salary_month` REGEXP '^[0-9]{4}-[0-9]{2}$')
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci 
  COMMENT='사육사 급여 지급 기록 테이블';

-- =============================================
-- 9. 동물-수입지출 중간테이블
-- =============================================
CREATE TABLE `animal_income_expends` (
    `animal_id` VARCHAR(50) NOT NULL COMMENT '동물 ID',
    `income_expend_id` VARCHAR(50) NOT NULL COMMENT '수입지출 ID',
    `expense_type` ENUM('FOOD', 'MEDICAL', 'CARE', 'TRANSPORT', 'OTHER') NOT NULL DEFAULT 'OTHER' COMMENT '비용 유형',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    PRIMARY KEY (`animal_id`, `income_expend_id`),
    INDEX `idx_expense_type` (`expense_type`),
    CONSTRAINT `fk_animal_income_animal` FOREIGN KEY (`animal_id`) REFERENCES `animals`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_animal_income_expend` FOREIGN KEY (`income_expend_id`) REFERENCES `income_expends`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci 
  COMMENT='동물 관련 비용 기록 테이블';

-- =============================================
-- 10. 사육장-수입지출 중간테이블
-- =============================================
CREATE TABLE `enclosure_income_expends` (
    `enclosure_id` VARCHAR(50) NOT NULL COMMENT '사육장 ID',
    `income_expend_id` VARCHAR(50) NOT NULL COMMENT '수입지출 ID',
    `maintenance_type` ENUM('FACILITY', 'UTILITIES', 'REPAIR', 'UPGRADE', 'CLEANING', 'OTHER') NOT NULL DEFAULT 'OTHER' COMMENT '유지보수 유형',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    PRIMARY KEY (`enclosure_id`, `income_expend_id`),
    INDEX `idx_maintenance_type` (`maintenance_type`),
    CONSTRAINT `fk_enclosure_income_enclosure` FOREIGN KEY (`enclosure_id`) REFERENCES `enclosures`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_enclosure_income_expend` FOREIGN KEY (`income_expend_id`) REFERENCES `income_expends`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci 
  COMMENT='사육장 유지보수 비용 기록 테이블';

-- 트랜잭션 커밋
COMMIT;

-- =============================================
-- 테이블 생성 확인
-- =============================================
SELECT 
    TABLE_NAME as '생성된_테이블',
    TABLE_COMMENT as '설명'
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_SCHEMA = 'manazoo' 
ORDER BY TABLE_NAME;

-- =============================================
-- 스키마 생성 완료
-- =============================================

