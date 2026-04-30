CREATE TABLE IF NOT EXISTS building (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  location VARCHAR(200),
  floors INT
);

CREATE TABLE IF NOT EXISTS classroom (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  building_id BIGINT NOT NULL,
  room_number VARCHAR(20) NOT NULL,
  capacity INT NOT NULL,
  has_multimedia TINYINT(1) NOT NULL DEFAULT 0,
  has_ac TINYINT(1) NOT NULL DEFAULT 0,
  CONSTRAINT fk_classroom_building FOREIGN KEY (building_id) REFERENCES building(id),
  CONSTRAINT uq_classroom_room UNIQUE (building_id, room_number)
);

CREATE TABLE IF NOT EXISTS course_schedule (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  classroom_id BIGINT NOT NULL,
  course_name VARCHAR(100) NOT NULL,
  day_of_week INT NOT NULL,
  start_period INT NOT NULL,
  end_period INT NOT NULL,
  week_start INT NOT NULL,
  week_end INT NOT NULL,
  teacher VARCHAR(50),
  CONSTRAINT fk_course_classroom FOREIGN KEY (classroom_id) REFERENCES classroom(id)
);

CREATE TABLE IF NOT EXISTS reservation (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id VARCHAR(50) NOT NULL,
  user_name VARCHAR(50) NOT NULL,
  classroom_id BIGINT NOT NULL,
  purpose VARCHAR(200) NOT NULL,
  reservation_date DATE NOT NULL,
  start_period INT NOT NULL,
  end_period INT NOT NULL,
  attendee_count INT NOT NULL,
  contact VARCHAR(100) NOT NULL,
  status INT NOT NULL DEFAULT 0,
  reviewer_id VARCHAR(50),
  review_comment VARCHAR(300),
  create_time DATETIME NOT NULL,
  update_time DATETIME NOT NULL,
  CONSTRAINT fk_reservation_classroom FOREIGN KEY (classroom_id) REFERENCES classroom(id)
);

CREATE INDEX idx_course_classroom_dow_period ON course_schedule(classroom_id, day_of_week, start_period, end_period);
CREATE INDEX idx_reservation_classroom_date_period_status ON reservation(classroom_id, reservation_date, start_period, end_period, status);
CREATE INDEX idx_reservation_user_status ON reservation(user_id, status);

