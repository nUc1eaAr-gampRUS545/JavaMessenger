INSERT INTO garages (location, type, condition)
VALUES ('New York, NY', 'Repair', 'Good'),
       ('Los Angeles, CA', 'Maintenance', 'Excellent'),
       ('Chicago, IL', 'Storage', 'Fair');
INSERT INTO masters (name, surname, email, telephone, schedule, is_active)
VALUES ('John', 'Doe', 'johndoe@example.com', '+1234567890', 'Mon-Fri 9:00-18:00', true),
       ('Jane', 'Smith', 'janesmith@example.com', '+1987654321', 'Tue-Sat 10:00-19:00', true),
       ('Robert', 'Johnson', 'robertjohnson@example.com', '+1122334455', 'Mon-Fri 8:00-17:00', true);

INSERT INTO orders (title, description, created_at, start_repaired_at, end_repaired_at, order_status, is_closed,
                    is_completed, garage_id, price)
VALUES ('Engine Repair', 'Fixing engine overheating issue', CURRENT_DATE, '2024-02-18', '2024-02-20', 'pending', false,
        false, 1, 300.00),
       ('Brake Replacement', 'Replacing front brake pads', CURRENT_DATE, '2024-02-19', '2024-02-21', 'in progress',
        false, false, 2, 150.50),
       ('Oil Change', 'Routine oil change service', CURRENT_DATE, '2024-02-20', '2024-02-20', 'completed', true, true,
        3, 50.00),
       ('Oil Change', 'Routine oil change service', CURRENT_DATE, '2024-02-20', '2024-02-20', 'completed', true, true,
        3, 50.00);

INSERT INTO masters_orders (master_id, order_id)
VALUES (1, 1),
       (2, 2),
       (3, 3);