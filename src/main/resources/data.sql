-- Users
INSERT INTO users (id, username, email, password, role) VALUES
(1, 'testuser', 'test@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ROLE_USER'),
(2, 'admin', 'admin@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ROLE_ADMIN'),
(3, 'jane', 'jane@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ROLE_USER')
ON DUPLICATE KEY UPDATE
username=VALUES(username),
email=VALUES(email),
password=VALUES(password),
role=VALUES(role);

-- =====================================================
-- Notes:
-- 1. Password for all users is 'password'
-- 2. Admin user: admin@example.com (username: admin)
-- 3. Regular user: jane
-- =====================================================

-- Medications
INSERT INTO medications (id, name, description, img_url, dosage, active, start_date, end_date, lifetime, user_id) VALUES
(1, 'Aspirin', 'Pain relief and anti-inflammatory', 'https://res.cloudinary.com/pillpal/image/upload/v1758126467/aspirina-xpress-1000mg-12-tabletas.jpg_yhgd4u.webp', '100mg', true, '2024-01-01', '2024-12-31', false, 1),
(2, 'Vitamin D', 'Daily vitamin supplement', 'https://res.cloudinary.com/pillpal/image/upload/v1758126315/es-vitamins-d3-tablets-4k-1_llkk9r.jpg', '1000 IU', true, '2024-01-01', NULL, true, 1),
(3, 'Metformin', 'Diabetes medication', 'https://res.cloudinary.com/pillpal/image/upload/v1758126599/metformin_ddx4hr.jpg', '500mg', true, '2024-01-01', '2024-06-30', false, 1),
(4, 'Ibuprofen', 'NSAID for pain and fever', 'https://res.cloudinary.com/pillpal/image/upload/v1758126715/220-4956_zqtvet.jpg', '200mg', true, '2024-01-10', NULL, true, 1),
(5, 'Lisinopril', 'Blood pressure control', 'https://res.cloudinary.com/pillpal/image/upload/v1758126797/64562_materialas_1_yxh73o.jpg', '10mg', false, '2024-01-01', '2024-03-31', false, 1),
(6, 'Omega-3', 'Fish oil supplement', 'https://res.cloudinary.com/pillpal/image/upload/v1758126890/3D-OMEGA3.jpg_v2op6a.webp', '1000mg', true, '2024-02-01', NULL, true, 3)
ON DUPLICATE KEY UPDATE
name=VALUES(name),
description=VALUES(description),
img_url=VALUES(img_url),
dosage=VALUES(dosage),
active=VALUES(active),
start_date=VALUES(start_date),
end_date=VALUES(end_date),
lifetime=VALUES(lifetime),
user_id=VALUES(user_id);

-- Reminders
INSERT INTO reminders (id, time, frequency, enabled, medication_id) VALUES
(1, '08:00:00', 'DAILY', true, 1),
(2, '20:00:00', 'DAILY', true, 1),
(3, '09:00:00', 'DAILY', true, 2),
(4, '08:30:00', 'DAILY', true, 3),
(5, '07:30:00', 'DAILY', true, 4),
(6, '13:00:00', 'DAILY', true, 4),
(7, '21:00:00', 'DAILY', true, 4),
(8, '08:00:00', 'DAILY', false, 5),
(9, '09:30:00', 'DAILY', true, 6),
(10, '18:00:00', 'DAILY', true, 6)
ON DUPLICATE KEY UPDATE
time=VALUES(time),
frequency=VALUES(frequency),
enabled=VALUES(enabled),
medication_id=VALUES(medication_id);

-- Medication intakes
INSERT INTO medication_intakes (id, date_time, status, medication_id) VALUES
(1, '2024-01-15 08:00:00', 'TAKEN', 1),
(2, '2024-01-15 20:00:00', 'TAKEN', 1),
(3, '2024-01-15 09:00:00', 'TAKEN', 2),
(4, '2024-01-15 08:30:00', 'TAKEN', 3),
(5, '2024-01-16 08:00:00', 'SKIPPED', 1),
(6, '2024-01-16 20:00:00', 'TAKEN', 1),
(7, '2024-01-17 07:30:00', 'TAKEN', 4),
(8, '2024-01-17 13:00:00', 'SKIPPED', 4),
(9, '2024-01-17 21:00:00', 'PENDING', 4),
(10, '2024-01-18 08:00:00', 'SKIPPED', 5),
(11, '2024-02-05 09:30:00', 'TAKEN', 6),
(12, '2024-02-05 18:00:00', 'PENDING', 6),
(13, '2024-02-06 09:30:00', 'TAKEN', 6),
(14, '2024-02-06 18:00:00', 'TAKEN', 6),
(15, '2024-02-07 09:30:00', 'SKIPPED', 6)
ON DUPLICATE KEY UPDATE
date_time=VALUES(date_time),
status=VALUES(status),
medication_id=VALUES(medication_id);