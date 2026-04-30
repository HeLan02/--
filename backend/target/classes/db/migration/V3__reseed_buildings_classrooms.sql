-- 重新灌入教学楼/教室基础数据（用于开发演示环境）
-- 注意：会清空 building/classroom/course_schedule/reservation 四张表数据

DELETE FROM reservation;
DELETE FROM course_schedule;
DELETE FROM classroom;
DELETE FROM building;

INSERT INTO building(name, location, floors) VALUES
('A栋', '校区A区', 4),
('B栋', '校区B区', 4),
('C栋', '校区C区', 4);

-- 每栋楼 4 层，每层 8 个教室：01-08（101-108 ... 401-408）
-- 简化：capacity / 设备用固定值，后续可在管理端维护
INSERT INTO classroom(building_id, room_number, capacity, has_multimedia, has_ac)
SELECT b.id, r.room_number, 60, 1, 1
FROM building b
JOIN (
  SELECT '101' AS room_number UNION ALL SELECT '102' UNION ALL SELECT '103' UNION ALL SELECT '104'
  UNION ALL SELECT '105' UNION ALL SELECT '106' UNION ALL SELECT '107' UNION ALL SELECT '108'
  UNION ALL SELECT '201' UNION ALL SELECT '202' UNION ALL SELECT '203' UNION ALL SELECT '204'
  UNION ALL SELECT '205' UNION ALL SELECT '206' UNION ALL SELECT '207' UNION ALL SELECT '208'
  UNION ALL SELECT '301' UNION ALL SELECT '302' UNION ALL SELECT '303' UNION ALL SELECT '304'
  UNION ALL SELECT '305' UNION ALL SELECT '306' UNION ALL SELECT '307' UNION ALL SELECT '308'
  UNION ALL SELECT '401' UNION ALL SELECT '402' UNION ALL SELECT '403' UNION ALL SELECT '404'
  UNION ALL SELECT '405' UNION ALL SELECT '406' UNION ALL SELECT '407' UNION ALL SELECT '408'
) r
WHERE b.name IN ('A栋','B栋','C栋');

-- 示例课程（用于演示占用逻辑）
INSERT INTO course_schedule(classroom_id, course_name, day_of_week, start_period, end_period, week_start, week_end, teacher)
SELECT c.id, '高等数学', 1, 1, 2, 1, 18, '张老师'
FROM classroom c
JOIN building b ON b.id = c.building_id
WHERE b.name = 'A栋' AND c.room_number = '101';

INSERT INTO course_schedule(classroom_id, course_name, day_of_week, start_period, end_period, week_start, week_end, teacher)
SELECT c.id, '大学英语', 3, 5, 6, 1, 18, '李老师'
FROM classroom c
JOIN building b ON b.id = c.building_id
WHERE b.name = 'C栋' AND c.room_number = '201';

