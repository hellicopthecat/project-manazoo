-- 동물 테이블 (실제 코드 기반)
CREATE TABLE animals (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    species ENUM('Lion', 'Tiger', 'Bear', 'Elephant', 'Wolf', 'Eagle', 'Owl', 'Snake') NOT NULL,
    age INT,
    gender VARCHAR(20),
    health_status VARCHAR(50),
    enclosure_id VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (enclosure_id) REFERENCES enclosures(id) ON DELETE SET NULL
);

-- 사육장 테이블 (실제 코드 기반)
CREATE TABLE enclosures (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    area_size DECIMAL(10, 2),
    temperature DECIMAL(5, 2),
    location_type ENUM('INDOOR', 'OUTDOOR') NOT NULL,
    environment_type ENUM('SAVANNA', 'TROPICAL', 'AQUATIC', 'ARCTIC', 'DESERT', 'FOREST') NOT NULL,
    max_capacity INT NOT NULL DEFAULT 10,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 사육장-동물 관계 테이블 (inhabitants Map 구현)
CREATE TABLE enclosure_animals (
    enclosure_id VARCHAR(50) NOT NULL,
    animal_id VARCHAR(50) NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (enclosure_id, animal_id),
    FOREIGN KEY (enclosure_id) REFERENCES enclosures(id) ON DELETE CASCADE,
    FOREIGN KEY (animal_id) REFERENCES animals(id) ON DELETE CASCADE
);

-- 사육장-사육사 관계 테이블 (caretakers Map 구현)
CREATE TABLE enclosure_caretakers (
    enclosure_id VARCHAR(50) NOT NULL,
    keeper_id VARCHAR(50) NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (enclosure_id, keeper_id),
    FOREIGN KEY (enclosure_id) REFERENCES enclosures(id) ON DELETE CASCADE,
    FOREIGN KEY (keeper_id) REFERENCES zoo_keepers(id) ON DELETE CASCADE
);

-- 사육사 테이블 (실제 코드 기반)
CREATE TABLE zoo_keepers (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT,
    gender ENUM('MALE', 'FEMALE') NOT NULL,
    department ENUM('MAMMAL', 'BIRD', 'REPTILE', 'AQUATIC', 'AMPHIBIAN', 'BREEDING_RESEARCH', 'VETERINARY_REHABILITATION', 'EDUCATION') NOT NULL,
    rank_level ENUM('RANK_1', 'RANK_2', 'RANK_3', 'RANK_4', 'RANK_5', 'RANK_6') NOT NULL,
    is_working BOOLEAN NOT NULL DEFAULT TRUE,
    experience_year INT DEFAULT 0,
    can_handle_danger_animal BOOLEAN NOT NULL DEFAULT FALSE,
    certifications TEXT,
    salary DECIMAL(12, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 수입/지출 테이블 (실제 코드 기반)
CREATE TABLE income_expends (
    id VARCHAR(50) PRIMARY KEY,
    amount DECIMAL(15, 2) NOT NULL,
    description TEXT,
    date DATE NOT NULL,
    type ENUM('INCOME', 'EXPENSE') NOT NULL,
    event_type ENUM('TICKET_SALES', 'MEMBERSHIP_FEE', 'DONATION', 'FACILITY_MAINTENANCE', 'STAFF_SALARY', 'ANIMAL_CARE', 'UTILITIES', 'MARKETING') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 방문 예약 테이블 (실제 코드 기반)
CREATE TABLE reservations (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    visit_date DATE NOT NULL,
    number_of_visitors INT NOT NULL DEFAULT 1,
    number_of_adults INT NOT NULL DEFAULT 1,
    number_of_childs INT NOT NULL DEFAULT 0,
    reservation_date TIMESTAMP NOT NULL, 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 예약-수입지출 중간테이블 (방문 예약 시 입장료 수입 기록)
CREATE TABLE reservation_income_expends (
    reservation_id VARCHAR(50) NOT NULL,
    income_expend_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (reservation_id, income_expend_id),
    FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE CASCADE,
    FOREIGN KEY (income_expend_id) REFERENCES income_expends(id) ON DELETE CASCADE
);

-- 사육사-수입지출 중간테이블 (급여 지출 기록)
CREATE TABLE zoo_keeper_income_expends (
    keeper_id VARCHAR(50) NOT NULL,
    income_expend_id VARCHAR(50) NOT NULL,
    salary_month VARCHAR(7) NOT NULL, -- YYYY-MM 형식
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (keeper_id, income_expend_id),
    FOREIGN KEY (keeper_id) REFERENCES zoo_keepers(id) ON DELETE CASCADE,
    FOREIGN KEY (income_expend_id) REFERENCES income_expends(id) ON DELETE CASCADE
);

-- 미구현 기능
-- 동물-수입지출 중간테이블 (동물 관련 비용 - 사료, 의료비 등)
CREATE TABLE animal_income_expends (
    animal_id VARCHAR(50) NOT NULL,
    income_expend_id VARCHAR(50) NOT NULL,
    expense_type VARCHAR(50), -- 'FOOD', 'MEDICAL', 'CARE' 등
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (animal_id, income_expend_id),
    FOREIGN KEY (animal_id) REFERENCES animals(id) ON DELETE CASCADE,
    FOREIGN KEY (income_expend_id) REFERENCES income_expends(id) ON DELETE CASCADE
);

-- 미구현 기능
-- 사육장-수입지출 중간테이블 (사육장 유지보수 비용)
CREATE TABLE enclosure_income_expends (
    enclosure_id VARCHAR(50) NOT NULL,
    income_expend_id VARCHAR(50) NOT NULL,
    maintenance_type VARCHAR(50), -- 'FACILITY', 'UTILITIES', 'REPAIR' 등
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (enclosure_id, income_expend_id),
    FOREIGN KEY (enclosure_id) REFERENCES enclosures(id) ON DELETE CASCADE,
    FOREIGN KEY (income_expend_id) REFERENCES income_expends(id) ON DELETE CASCADE
);

-- ID 생성기 테이블 (자동 ID 생성용)
CREATE TABLE id_generator (
    prefix VARCHAR(10) PRIMARY KEY,
    last_number INT NOT NULL
);