-- Users
INSERT INTO users (id, username, email, password, role) VALUES
(1, 'testuser', 'test@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ROLE_USER'),
(2, 'admin', 'admin@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ROLE_ADMIN'),
(3, 'jane', 'jane@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ROLE_USER');

-- =====================================================
-- Notes:
-- 1. Password for all users is 'password'
-- 2. Admin user: admin@example.com (username: admin)
-- 3. Regular user: jane
-- =====================================================

-- Medications
INSERT INTO medications (id, name, description, img_url, dosage, active, start_date, end_date, lifetime, user_id) VALUES
(1, 'Aspirin', 'Pain relief and anti-inflammatory', 'https://example.com/aspirin.jpg', '100mg', true, '2024-01-01', '2024-12-31', false, 1),
(2, 'Vitamin D', 'Daily vitamin supplement', 'https://example.com/vitamind.jpg', '1000 IU', true, '2024-01-01', NULL, true, 1),
(3, 'Metformin', 'Diabetes medication', 'https://example.com/metformin.jpg', '500mg', true, '2024-01-01', '2024-06-30', false, 1),
(4, 'Ibuprofen', 'NSAID for pain and fever', 'https://example.com/ibuprofen.jpg', '200mg', true, '2024-01-10', NULL, true, 1),
(5, 'Lisinopril', 'Blood pressure control', 'https://example.com/lisinopril.jpg', '10mg', false, '2024-01-01', '2024-03-31', false, 1),
(6, 'Omega-3', 'Fish oil supplement', 'https://example.com/omega3.jpg', '1000mg', true, '2024-02-01', NULL, true, 3);

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
(10, '18:00:00', 'DAILY', true, 6);

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
(15, '2024-02-07 09:30:00', 'SKIPPED', 6);