INSERT INTO building(name, location, floors) VALUES
('一教', '校区东侧', 6),
('二教', '图书馆旁', 5);

INSERT INTO classroom(building_id, room_number, capacity, has_multimedia, has_ac) VALUES
(1, '101', 60, 1, 1),
(1, '102', 40, 1, 0),
(1, '201', 80, 1, 1),
(2, '101', 50, 0, 1),
(2, '201', 120, 1, 1);

-- 示例课程：周一 1-2 节，一教101；周三 5-6 节，二教201
INSERT INTO course_schedule(classroom_id, course_name, day_of_week, start_period, end_period, week_start, week_end, teacher) VALUES
(1, '高等数学', 1, 1, 2, 1, 18, '张老师'),
(5, '大学英语', 3, 5, 6, 1, 18, '李老师');

