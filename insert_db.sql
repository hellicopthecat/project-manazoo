-- =============================================
-- Project Manazoo Test Data Insertion Script (English)
-- =============================================

-- Clean existing data (considering referential integrity)
DELETE FROM animal_income_expends;
DELETE FROM enclosure_income_expends;
DELETE FROM zoo_keeper_income_expends;
DELETE FROM enclosure_caretakers;
DELETE FROM income_expends;
DELETE FROM animals;
DELETE FROM reservations;
DELETE FROM zoo_keepers;
DELETE FROM enclosures;
DELETE FROM id_generator;

-- =============================================
-- 1. ID Generator Initial Data
-- =============================================
INSERT INTO id_generator (prefix, last_number) VALUES
('E', 5),     -- Enclosure ID generator
('K', 5),     -- Keeper ID generator
('R', 5),     -- Reservation ID generator
('A', 8),     -- Animal ID generator
('I', 10);    -- Income/Expense ID generator

-- =============================================
-- 2. Enclosures Table
-- =============================================
INSERT INTO enclosures (id, name, area_size, temperature, location_type, environment_type, created_at) VALUES
('E-0001', 'African Savanna Habitat', 2500.0, 28.5, 'OUTDOOR', 'LAND', '2024-01-15 09:00:00'),
('E-0002', 'Asian Rainforest Pavilion', 1800.0, 32.0, 'INDOOR', 'MIXED', '2024-01-20 10:30:00'),
('E-0003', 'Arctic Tundra Exhibit', 3200.0, -5.0, 'OUTDOOR', 'MIXED', '2024-02-01 11:15:00'),
('E-0004', 'Birds of Prey Flight Arena', 1500.0, 22.0, 'OUTDOOR', 'LAND', '2024-02-10 14:20:00'),
('E-0005', 'Marine Aquarium Complex', 2200.0, 18.0, 'INDOOR', 'AQUATIC', '2024-02-15 16:45:00');

-- =============================================
-- 3. Zoo Keepers Table
-- =============================================
INSERT INTO zoo_keepers (id, name, age, gender, department, rank_level, is_working, experience_year, can_handle_danger_animal, licenses, salary, created_at) VALUES
('K-0001', 'John Wildlife', 35, 'MALE', 'MAMMAL', 'SENIOR_KEEPER', TRUE, 12, TRUE, 'Wildlife Management License, Animal Health Certificate', 4500000, '2024-01-05 08:30:00'),
('K-0002', 'Sarah Birdson', 28, 'FEMALE', 'BIRD', 'KEEPER', TRUE, 5, FALSE, 'Avian Specialist License, First Aid Certification', 3200000, '2024-01-10 09:15:00'),
('K-0003', 'Mike Oceandeep', 42, 'MALE', 'FISH', 'HEAD_KEEPER', TRUE, 18, TRUE, 'Aquatic Animal Management, Diving Certification', 5800000, '2024-01-12 10:00:00'),
('K-0004', 'Emma Scalewing', 31, 'FEMALE', 'REPTILE', 'SENIOR_KEEPER', TRUE, 8, TRUE, 'Reptile Specialist, Venomous Animal Handler', 4200000, '2024-01-18 11:30:00'),
('K-0005', 'David Forestgreen', 26, 'MALE', 'EDUCATION', 'JUNIOR_KEEPER', TRUE, 2, FALSE, 'Environmental Education Certificate, Tour Guide License', 2800000, '2024-02-01 13:45:00');

-- =============================================
-- 4. Reservations Table
-- =============================================
INSERT INTO reservations (id, name, phone_number, visit_date, number_of_visitors, number_of_adults, number_of_childs, created_at) VALUES
('R-0001', 'Johnson Family', '555-1234-5678', '2024-03-15', 4, 2, 2, '2024-03-10 14:30:00'),
('R-0002', 'Smith Couple', '555-2345-6789', '2024-03-16', 2, 2, 0, '2024-03-11 16:20:00'),
('R-0003', 'Williams Group', '555-3456-7890', '2024-03-17', 8, 4, 4, '2024-03-12 10:15:00'),
('R-0004', 'Lincoln Elementary School', '555-4567-8901', '2024-03-18', 25, 3, 22, '2024-03-13 09:45:00'),
('R-0005', 'TechCorp Company', '555-5678-9012', '2024-03-20', 12, 12, 0, '2024-03-15 11:30:00');

-- =============================================
-- 5. Animals Table
-- =============================================
INSERT INTO animals (id, name, species, age, gender, health_status, enclosure_id, created_at) VALUES
('A-0001', 'Simba', 'Lion', 8, 'MALE', 'Healthy', 'E-0001', '2024-01-20 09:00:00'),
('A-0002', 'Nala', 'Lion', 6, 'FEMALE', 'Healthy', 'E-0001', '2024-01-20 09:30:00'),
('A-0003', 'Rajah', 'Tiger', 5, 'MALE', 'Under Treatment', 'E-0002', '2024-01-25 10:15:00'),
('A-0004', 'Artemis', 'Eagle', 3, 'FEMALE', 'Healthy', 'E-0004', '2024-02-05 11:45:00'),
('A-0005', 'Frost', 'Bear', 10, 'FEMALE', 'Healthy', 'E-0003', '2024-02-12 14:20:00'),
('A-0006', 'Shadow', 'Wolf', 4, 'MALE', 'Healthy', 'E-0001', '2024-02-18 15:30:00'),
('A-0007', 'Athena', 'Owl', 2, 'FEMALE', 'Healthy', 'E-0004', '2024-02-20 16:00:00'),
('A-0008', 'Serpent', 'Snake', 1, 'MALE', 'Healthy', 'E-0002', '2024-02-25 17:15:00');

-- =============================================
-- 6. Income/Expense Table
-- =============================================
INSERT INTO income_expends (id, amount, description, date, type, event_type, reservation_id, created_at) VALUES
('I-0001', 32000, 'Johnson Family admission fee (2 adults, 2 children)', '2024-03-15', 'INCOME', 'FEE', 'R-0001', '2024-03-15 09:30:00'),
('I-0002', 20000, 'Smith Couple admission fee (2 adults)', '2024-03-16', 'INCOME', 'FEE', 'R-0002', '2024-03-16 10:15:00'),
('I-0003', 4500000, 'John Wildlife March salary', '2024-03-01', 'EXPENSE', 'EMPLOYEE_MONTH', NULL, '2024-03-01 09:00:00'),
('I-0004', 3200000, 'Sarah Birdson March salary', '2024-03-01', 'EXPENSE', 'EMPLOYEE_MONTH', NULL, '2024-03-01 09:00:00'),
('I-0005', 150000, 'Lion food supply purchase', '2024-03-05', 'EXPENSE', 'FOOD', NULL, '2024-03-05 14:20:00'),
('I-0006', 64000, 'Williams Group admission fee (4 adults, 4 children)', '2024-03-17', 'INCOME', 'FEE', 'R-0003', '2024-03-17 11:30:00'),
('I-0007', 350000, 'Lincoln Elementary School group admission (3 adults, 22 children)', '2024-03-18', 'INCOME', 'FEE', 'R-0004', '2024-03-18 13:45:00'),
('I-0008', 120000, 'TechCorp Company admission fee (12 adults)', '2024-03-20', 'INCOME', 'FEE', 'R-0005', '2024-03-20 10:00:00'),
('I-0009', 80000, 'Eagle medical treatment cost', '2024-03-10', 'EXPENSE', 'FOOD', NULL, '2024-03-10 16:30:00'),
('I-0010', 200000, 'Enclosure maintenance expenses', '2024-03-12', 'EXPENSE', 'ENCLOSURE', NULL, '2024-03-12 15:45:00');

-- =============================================
-- 7. Enclosure-Caretaker Relationships
-- =============================================
INSERT INTO enclosure_caretakers (enclosure_id, keeper_id, assigned_at) VALUES
('E-0001', 'K-0001', '2024-01-20 09:00:00'), -- African Savanna - John Wildlife (Mammal specialist)
('E-0002', 'K-0001', '2024-01-25 10:00:00'), -- Asian Rainforest - John Wildlife (Mammal specialist)
('E-0003', 'K-0001', '2024-02-12 14:00:00'), -- Arctic Tundra - John Wildlife (Mammal specialist)
('E-0004', 'K-0002', '2024-02-05 11:30:00'), -- Birds of Prey - Sarah Birdson (Bird specialist)
('E-0005', 'K-0003', '2024-02-15 16:30:00'), -- Marine Aquarium - Mike Oceandeep (Fish specialist)
('E-0002', 'K-0004', '2024-02-25 17:00:00'), -- Asian Rainforest - Emma Scalewing (Reptile specialist, snake care)
('E-0001', 'K-0005', '2024-03-01 08:30:00'), -- African Savanna - David Forestgreen (Education programs)
('E-0004', 'K-0005', '2024-03-01 08:30:00'); -- Birds of Prey - David Forestgreen (Education programs)

-- =============================================
-- 8. Keeper-Salary Relationships
-- =============================================
INSERT INTO zoo_keeper_income_expends (keeper_id, income_expend_id, salary_month, created_at) VALUES
('K-0001', 'I-0003', '2024-03', '2024-03-01 09:00:00'), -- John Wildlife March salary
('K-0002', 'I-0004', '2024-03', '2024-03-01 09:00:00'); -- Sarah Birdson March salary

-- =============================================
-- 9. Animal-Expense Relationships
-- =============================================
INSERT INTO animal_income_expends (animal_id, income_expend_id, expense_type, created_at) VALUES
('A-0001', 'I-0005', 'FOOD', '2024-03-05 14:20:00'), -- Simba (Lion) food cost
('A-0002', 'I-0005', 'FOOD', '2024-03-05 14:20:00'), -- Nala (Lion) food cost
('A-0004', 'I-0009', 'MEDICAL', '2024-03-10 16:30:00'); -- Artemis (Eagle) medical cost

-- =============================================
-- 10. Enclosure-Maintenance Relationships
-- =============================================
INSERT INTO enclosure_income_expends (enclosure_id, income_expend_id, maintenance_type, created_at) VALUES
('E-0001', 'I-0010', 'FACILITY', '2024-03-12 15:45:00'), -- African Savanna maintenance
('E-0004', 'I-0010', 'FACILITY', '2024-03-12 15:45:00'); -- Birds of Prey maintenance

-- =============================================
-- Data Insertion Verification Queries
-- =============================================

-- Count records in each table
SELECT 'id_generator' as table_name, COUNT(*) as count FROM id_generator
UNION ALL
SELECT 'enclosures', COUNT(*) FROM enclosures
UNION ALL
SELECT 'zoo_keepers', COUNT(*) FROM zoo_keepers
UNION ALL
SELECT 'reservations', COUNT(*) FROM reservations
UNION ALL
SELECT 'animals', COUNT(*) FROM animals
UNION ALL
SELECT 'income_expends', COUNT(*) FROM income_expends
UNION ALL
SELECT 'enclosure_caretakers', COUNT(*) FROM enclosure_caretakers
UNION ALL
SELECT 'zoo_keeper_income_expends', COUNT(*) FROM zoo_keeper_income_expends
UNION ALL
SELECT 'animal_income_expends', COUNT(*) FROM animal_income_expends
UNION ALL
SELECT 'enclosure_income_expends', COUNT(*) FROM enclosure_income_expends;

-- =============================================
-- Test Data Validation Queries (Optional)
-- =============================================

-- Animal count by enclosure
SELECT 
    e.id as enclosure_id,
    e.name as enclosure_name,
    COUNT(a.id) as animal_count,
    GROUP_CONCAT(a.name SEPARATOR ', ') as animals
FROM enclosures e
LEFT JOIN animals a ON e.id = a.enclosure_id
GROUP BY e.id, e.name
ORDER BY e.id;

-- Enclosures managed by each keeper
SELECT 
    k.id as keeper_id,
    k.name as keeper_name,
    k.department,
    COUNT(ec.enclosure_id) as enclosure_count,
    GROUP_CONCAT(e.name SEPARATOR ', ') as enclosures
FROM zoo_keepers k
LEFT JOIN enclosure_caretakers ec ON k.id = ec.keeper_id
LEFT JOIN enclosures e ON ec.enclosure_id = e.id
GROUP BY k.id, k.name, k.department
ORDER BY k.id;

-- Monthly income/expense summary
SELECT 
    DATE_FORMAT(date, '%Y-%m') as month,
    type,
    COUNT(*) as transaction_count,
    SUM(amount) as total_amount
FROM income_expends
GROUP BY DATE_FORMAT(date, '%Y-%m'), type
ORDER BY month, type;

-- Visitor statistics by reservation type
SELECT 
    CASE 
        WHEN number_of_visitors <= 2 THEN 'Individual/Couple'
        WHEN number_of_visitors <= 10 THEN 'Small Group'
        ELSE 'Large Group'
    END as group_type,
    COUNT(*) as reservation_count,
    SUM(number_of_visitors) as total_visitors,
    SUM(number_of_adults) as total_adults,
    SUM(number_of_childs) as total_children
FROM reservations
GROUP BY 
    CASE 
        WHEN number_of_visitors <= 2 THEN 'Individual/Couple'
        WHEN number_of_visitors <= 10 THEN 'Small Group'
        ELSE 'Large Group'
    END
ORDER BY total_visitors DESC;

-- =============================================
-- English Test Data Insertion Script Complete
-- =============================================
