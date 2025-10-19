-- Insert this data from MySQL Workbench

INSERT INTO venue (name, address, total_capacity) VALUES
('Eden Gardens', 'Kolkata, West Bengal', 66000),
('Jawaharlal Nehru Stadium', 'New Delhi, Delhi', 60000),
('Wankhede Stadium', 'Mumbai, Maharashtra', 33000),
('M. A. Chidambaram Stadium', 'Chennai, Tamil Nadu', 50000),
('Salt Lake Stadium', 'Kolkata, West Bengal', 85000),
('Narendra Modi Stadium', 'Ahmedabad, Gujarat', 132000);


INSERT INTO event (name, venue_id, total_capacity, left_capacity) VALUES
('IPL Final 2025', 1, 66000, 2000),
('India vs Australia Cricket Match', 2, 60000, 15000),
('Concert by AR Rahman', 3, 33000, 5000),
('2025 FIFA World Cup Qualifier', 4, 50000, 0),
('International Rock Festival', 5, 85000, 10000),
('ODI: India vs New Zealand', 6, 132000, 25000);
