-- Local H2 data (runs after Hibernate creates tables)
-- Users
INSERT INTO users (id, username, email, password, role) VALUES
(1, 'testuser', 'test@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ROLE_USER'),
(2, 'admin', 'admin@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ROLE_ADMIN'),
(3, 'jane', 'jane@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ROLE_USER');

-- Medications
INSERT INTO medications (id, name, description, img_url, dosage, active, start_date, end_date, lifetime, user_id) VALUES
(1, 'Aspirin', 'Pain relief and anti-inflammatory', 'https://res.cloudinary.com/pillpal/image/upload/v1758126467/aspirina-xpress-1000mg-12-tabletas.jpg_yhgd4u.webp', '100mg', true, '2024-01-01', '2024-12-31', false, 1),
(2, 'Vitamin D', 'Daily vitamin supplement', 'https://res.cloudinary.com/pillpal/image/upload/v1758126315/es-vitamins-d3-tablets-4k-1_llkk9r.jpg', '1000 IU', true, '2024-01-01', NULL, true, 1),
(3, 'Metformin', 'Diabetes medication', 'https://res.cloudinary.com/pillpal/image/upload/v1758126599/metformin_ddx4hr.jpg', '500mg', true, '2024-01-01', '2024-06-30', false, 1);

-- Reminders
INSERT INTO reminders (id, time, frequency, enabled, medication_id) VALUES
(1, '08:00:00', 'DAILY', true, 1),
(2, '20:00:00', 'DAILY', true, 1),
(3, '09:00:00', 'DAILY', true, 2);

-- Medication intakes
INSERT INTO medication_intakes (id, date_time, status, medication_id) VALUES
(1, '2024-01-15 08:00:00', 'TAKEN', 1),
(2, '2024-01-15 20:00:00', 'TAKEN', 1),
(3, '2024-01-15 09:00:00', 'TAKEN', 2);
