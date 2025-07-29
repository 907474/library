-- Passwords are "admin", "staff", and "user" respectively, hashed with BCrypt.
INSERT INTO system_accounts (username, password, role, enabled) VALUES
('admin', '$2a$10$8.AgnOfO.3A56t.kNI215uILfTfCHkLqDpp./j9152D/vjT8IG2OW', 'ADMIN', true),
('staff', '$2a$10$Vp31v7./lkOqx2y2GWtV0O4n4cCUb5g33X2e2g.doYw22KBF2u4o6', 'STAFF', true),
('user', '$2a$10$v0v57y6i7512sSgS.aph0eX1.oVqO.Hq4m2tq8f.TP9gB4U85B8/m', 'USER', true);

INSERT INTO categories (name) VALUES
('Fiction'),
('Non-Fiction'),
('Science Fiction'),
('Fantasy'),
('Mystery'),
('History'),
('Biography');

INSERT INTO audiences (name) VALUES
('Adult'),
('Young Adult'),
('Children');