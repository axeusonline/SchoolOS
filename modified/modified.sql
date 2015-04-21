/* 
   Description:  เพิ่มตารางสำหรับห้องบุคลากร เพื่อให้การีมได้ใช้เป็นข้อมูลพื้นฐาน
   Date: 04/04/2015
*/

CREATE TABLE IF NOT EXISTS `department` (
  `department_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ตาราง แผนก',
  `school_id` int(11) NULL COMMENT 'FK โรงเรียน',
  `name` varchar(64) NOT NULL COMMENT 'ชื่อ',
  `name_nd` varchar(64) DEFAULT NULL COMMENT 'ชื่อภาษาที่สอง',
  `created_by_id` int(11) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `modified_by_id` int(11) DEFAULT NULL,
  `modified_date` datetime DEFAULT NULL,
  PRIMARY KEY (`department_id`),
  KEY `fk_department_has_school_idx` (`school_id`),
  CONSTRAINT `fk_department_has_school` FOREIGN KEY (`school_id`) REFERENCES `school` (`school_id`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ตารางแผนก';

CREATE TABLE IF NOT EXISTS `job_position` (
  `job_position_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ตาราง แผนก',
  `school_id` int(11) NULL COMMENT 'FK โรงเรียน',
  `name` varchar(64) NOT NULL COMMENT 'ชื่อ',
  `name_nd` varchar(64) DEFAULT NULL COMMENT 'ชื่อภาษาที่สอง',
  `created_by_id` int(11) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `modified_by_id` int(11) DEFAULT NULL,
  `modified_date` datetime DEFAULT NULL,
  PRIMARY KEY (`job_position_id`),
  KEY `fk_job_position_has_school_idx` (`school_id`),
  CONSTRAINT `fk_job_position_has_school` FOREIGN KEY (`school_id`) REFERENCES `school` (`school_id`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ตารางแผนก';

CREATE TABLE IF NOT EXISTS `personnel` (
  `personnel_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'PK ตารางบุคลากร',
  `school_id` int(11) NOT NULL COMMENT 'FK โรงเรียน',
  `people_id` varchar(16) NOT NULL COMMENT 'หมายเลขบัตรประชาชน',
  `people_id_type` tinyint(4) NOT NULL COMMENT '*Fix ประเภทบัตรประชาชน',
  `personnel_code` varchar(16) NOT NULL DEFAULT '' COMMENT 'รหัสบุคลากร',
  `prename` tinyint(4) NOT NULL COMMENT '*Fix ชื่อต้น',
  `firstname` varchar(64) NOT NULL DEFAULT '' COMMENT 'ชื่อ',
  `lastname` varchar(64) NOT NULL DEFAULT '' COMMENT 'นามสกุล',
  `firstname_nd` varchar(64) DEFAULT NULL COMMENT 'ชื่อภาษาที่สอง',
  `lastname_nd` varchar(64) DEFAULT NULL COMMENT 'สกุลภาษาที่สอง',
  `firstname_rd` varchar(64) DEFAULT NULL COMMENT 'ชื่อถาษาที่สาม',
  `lastname_rd` varchar(64) DEFAULT NULL COMMENT 'สกุลภาษาที่สาม',
  `nickname` varchar(32) DEFAULT NULL COMMENT 'ชื่อเล่น',
  `gender` tinyint(4) NOT NULL COMMENT '*Fix เพศ ',
  `religion` tinyint(4) NOT NULL COMMENT '*Fix ศาสนา',
  `race` smallint(6) NOT NULL COMMENT '*Fix เชื้อชาติ',
  `nationality` smallint(6) NOT NULL COMMENT '*Fix สัญชาติ',
  `marital_status` tinyint(4) NOT NULL COMMENT '*Fix สถานะการแต่งงาน',
  `birth_date` date NOT NULL COMMENT 'วันเดือนปี เกิด ',
  `blood` tinyint(4) NOT NULL COMMENT '*Fix หมู่เลือด',
  `height` double DEFAULT NULL COMMENT 'ส่วนสูง',
  `weight` double DEFAULT NULL COMMENT 'น้ำหนัก',
  `congenital_disease` varchar(256) DEFAULT NULL COMMENT 'โรคประจำตัว',
  `personnel_status` tinyint(4) NOT NULL COMMENT '*Fix สถานะบุคลากร เช่น จำหน่ายออก ไล่ออก ทำงาน',
  `job_position` int(11) DEFAULT NULL COMMENT 'ตำแหน่ง',
  `department` int(11) DEFAULT NULL COMMENT 'แผนก',
  `tel` varchar(16) DEFAULT NULL COMMENT 'โทรศัพท์',
  `mobile` varchar(16) DEFAULT NULL COMMENT 'มือถือ',
  `email` varchar(64) DEFAULT NULL COMMENT 'อีเมลล์',
  `census_address` varchar(256) DEFAULT NULL COMMENT 'ที่อยู่ตามทะเบียนบ้าน',
  `census_city_id` int(11) DEFAULT NULL COMMENT 'ตำบล ที่อยู่ตามทะเบียนบ้าน',
  `census_district_id` int(11) DEFAULT NULL COMMENT 'อำเภอ ที่อยู่ตามทะเบียนบ้าน',
  `census_province_id` int(11) DEFAULT NULL COMMENT 'จังหวัด ที่อยู่ตามทะเบียนบ้าน',
  `census_postcode_id` int(11) DEFAULT NULL COMMENT 'ไปรษณีย์ ที่อยู่ตามทะเบียนบ้าน',
  `current_address` varchar(256) DEFAULT NULL COMMENT 'ที่อยู่ปัจจุบัน',
  `current_city_id` int(11) DEFAULT NULL COMMENT 'ตำบล ที่อยู่ปัจจุบัน',
  `current_district_id` int(11) DEFAULT NULL COMMENT 'อำเภอ ที่อยู่ปัจจุบัน',
  `current_province_id` int(11) DEFAULT NULL COMMENT 'จังหวัด ที่อยู่ปัจจุบัน',
  `current_postcode_id` int(11) DEFAULT NULL COMMENT 'ไปรษณีย์ ที่อยู่ปัจจุบัน',
  `employment_type` tinyint(4) NOT NULL COMMENT '*Fix ประเภทการว่าจ้าง',
  `start_work_date` date NOT NULL COMMENT 'วันที่เริ่มทำงาน',
  `recruit_by_id` int(11) NOT NULL COMMENT 'FK ผู้รับสมัคร',
  `recruit_date` date NOT NULL COMMENT 'วัน เดือน ปีที่รับสมัคร',
  `recruit_description` varchar(256) DEFAULT NULL COMMENT 'รายละเอียดการสมัคร',
  `resign_by_id` int(11) DEFAULT NULL COMMENT 'FK ผู้รับเรื่องการจำหน่ายออก',
  `resign_type` tinyint(4) DEFAULT NULL COMMENT '*Fix ประเภทจำหน่ายออก ',
  `resign_date` date DEFAULT NULL COMMENT 'วันเดือน ปี ปี ที่จำหน่ายออก',
  `bank_name` varchar(64) DEFAULT NULL COMMENT 'ชื่อธนาคาร',
  `bank_account_number` varchar(32) DEFAULT NULL COMMENT 'หมายเลขบัญชี',
  `bank_account_type` tinyint(4) DEFAULT NULL COMMENT '*Fix ประเภทบัญชี',
  `bank_account_name` varchar(32) DEFAULT NULL COMMENT 'ชื่อบัญชี',
  `bank_account_branch` varchar(128) DEFAULT NULL COMMENT 'สาขา',
  `bank_account_province_id` int(11) DEFAULT NULL COMMENT 'จังหวัด',
  `father_id` int(11) DEFAULT NULL COMMENT 'FK บิดา',
  `mother_id` int(11) DEFAULT NULL COMMENT 'FK มารดา',
  `spouse_id` int(11) DEFAULT NULL COMMENT 'FK คู่สมรส',
  `created_by_id` int(11) DEFAULT NULL COMMENT 'FK ผู้ลงข้่อมูล',
  `created_date` datetime DEFAULT NULL COMMENT 'วันที่ลงข้อมูล',
  `modified_by_id` int(11) DEFAULT NULL COMMENT 'FK ผู้แก้ไขข้อมูล',
  `modified_date` datetime DEFAULT NULL COMMENT 'วันที่แก้ไขข้อมูล',
  PRIMARY KEY (`personnel_id`),
  UNIQUE KEY `personnel_columns_uniq` (`school_id`,`people_id`,`people_id_type`),
  KEY `fk_personnel_has_mother` (`mother_id`),
  KEY `fk_personnel_has_father` (`father_id`),
  KEY `fk_personnel_has_school_idx` (`school_id`),
  KEY `fk_personnel_has_spouse_idx` (`spouse_id`),
  CONSTRAINT `fk_personnel_has_father` FOREIGN KEY (`father_id`) REFERENCES `family` (`family_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_personnel_has_mother` FOREIGN KEY (`mother_id`) REFERENCES `family` (`family_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_personnel_has_spouse` FOREIGN KEY (`spouse_id`) REFERENCES `family` (`family_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_personnel_has_school` FOREIGN KEY (`school_id`) REFERENCES `school` (`school_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ข้อมูลพื้นฐานบุคลากร';

CREATE TABLE IF NOT EXISTS `personnel_graduated_history` (
  `graduated_history_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'PK ตารางประวัติการศึกษา',
  `personnel_id` int(11) NOT NULL COMMENT 'FK บุคลากร',
  `institute` varchar(128) NOT NULL COMMENT 'ชื่อสถาบัน',
  `graduated_level` tinyint(4) NOT NULL COMMENT '*Fix ระดับการศึกษาที่จบ',
  `degree` varchar(128) DEFAULT NULL COMMENT 'วุฒิการศึกษา',
  `major` varchar(128) DEFAULT NULL COMMENT 'วิชาเอก',
  `minor` varchar(128) DEFAULT NULL COMMENT 'วิชาโท',
  `description` varchar(256) DEFAULT NULL COMMENT 'วิชาโท',
  `year` int(11) DEFAULT NULL COMMENT 'ปีทืจบ',
  `location` varchar(128) DEFAULT NULL COMMENT 'สถานที่สถาบัน',
  `province_id` int(11) NOT NULL COMMENT 'จังหวัดสถาบัน',
  `created_by_id` int(11) DEFAULT NULL COMMENT 'FK ผู้ใส่ข้อมูล',
  `created_date` datetime DEFAULT NULL COMMENT 'วันที่ใส่ข้อมูล',
  `modified_by_id` int(11) DEFAULT NULL COMMENT 'FK ผู้แก้ไขข้อมูล',
  `modified_date` datetime DEFAULT NULL COMMENT 'วันที่แก้ไขข้อมูล',
  PRIMARY KEY (`graduated_history_id`),
  KEY `fk_graduated_history_has_personnel_idx` (`personnel_id`),
  CONSTRAINT `fk_graduated_history_has_personnel` FOREIGN KEY (`personnel_id`) REFERENCES `personnel` (`personnel_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ประวัติการศึกษาบุคลากร';

ALTER TABLE `recruit_student` CHANGE `father_id` `father_id` INT(11) NULL COMMENT 'FK รหัสบิดา', CHANGE `mother_id` `mother_id` INT(11) NULL COMMENT 'FK รหัสมารดา',CHANGE `guardian_id` `guardian_id` INT(11) NULL COMMENT 'FK ผู้ปกครอง';

/* 
   Description:  เพิ่มตารางสำหรับงานแผนการเรียน
   Date: 05/04/2015
*/
CREATE TABLE IF NOT EXISTS `subject_type` (
  `subject_type_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'PK ตารางประเภทวิชา',
  `school_id` int(11) NULL COMMENT 'FK โรงเรียน',
  `name` varchar(64) NOT NULL COMMENT 'ชื่อ',
  `name_nd` varchar(64) DEFAULT NULL COMMENT 'ชื่อภาษาที่สอง',
  `created_by_id` int(11) DEFAULT NULL COMMENT 'FK ผู้ใส่ข้อมูล',
  `created_date` datetime DEFAULT NULL COMMENT 'วันเดือนปี ที่ใส่ข้อมูล',
  `modified_by_id` int(11) DEFAULT NULL COMMENT 'FK ผู้แก้ไขข้อมูล',
  `modified_date` datetime DEFAULT NULL COMMENT 'วันเดือนปี ที่แก้ไขข้อมูล',
  PRIMARY KEY (`subject_type_id`),
  KEY `fk_subject_type_has_school_idx` (`school_id`),
  CONSTRAINT `fk_subject_type_has_school` FOREIGN KEY (`school_id`) REFERENCES `school` (`school_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ตารางแผนก';

CREATE TABLE IF NOT EXISTS `lesson_type` (
  `lesson_type_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'PK ตารางสาระการเรียนรู้',
  `school_id` int(11) NULL COMMENT 'FK โรงเรียน',
  `name` varchar(64) NOT NULL COMMENT 'ชื่อ',
  `name_nd` varchar(64) DEFAULT NULL COMMENT 'ชื่อภาษาที่สอง',
  `created_by_id` int(11) DEFAULT NULL COMMENT 'FK ผู้ใส่ข้อมูล',
  `created_date` datetime DEFAULT NULL COMMENT 'วันเดือนปี ที่ใส่ข้อมูล',
  `modified_by_id` int(11) DEFAULT NULL COMMENT 'FK ผู้แก้ไขข้อมูล',
  `modified_date` datetime DEFAULT NULL COMMENT 'วันเดือนปี ที่แก้ไขข้อมูล',
  PRIMARY KEY (`lesson_type_id`),
  KEY `fk_lesson_type_has_school_idx` (`school_id`),
  CONSTRAINT `fk_lesson_type_has_school` FOREIGN KEY (`school_id`) REFERENCES `school` (`school_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ตารางแผนก';

CREATE TABLE IF NOT EXISTS `subject` (
  `subject_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'PK ตารางรายวิชา',
  `school_id` int(11) NOT NULL COMMENT 'FK โรงเรียน',
  `code` varchar(16) DEFAULT '' COMMENT 'รหัสวิชา เช่น ท1101',
  `code_nd` varchar(16) DEFAULT NULL COMMENT 'รหัสวิชา ภาษาที่สอง',
  `name` varchar(64) NOT NULL DEFAULT '' COMMENT 'ชื่อวิชา',
  `name_nd` varchar(16) DEFAULT NULL COMMENT 'ชื่อวิชาภาษาที่สอง',
  `weight` double DEFAULT NULL COMMENT 'น้ำหนักของวิชา',
  `hours` double DEFAULT NULL COMMENT 'จำนวนชั่วโมงที่สอน',
  `lesson_type` int(4) NOT NULL COMMENT '*Fix สาระการเรียนรู้',
  `subject_type` int(4) NOT NULL COMMENT '*Fix ประเภทวิชา เช่น วิชาพื้นฐาน วิชาเพิ่มเติม วิชาเลือก และ กิจกรรมพัฒนาผู้เรียน',
  `description` text COMMENT 'รายละเอียด',
  `created_by_id` int(11) DEFAULT NULL COMMENT 'FK ผู้ใส่ข้อมูล',
  `created_date` datetime DEFAULT NULL COMMENT 'วันเดือนปี ที่ใส่ข้อมูล',
  `modified_by_id` int(11) DEFAULT NULL COMMENT 'FK ผู้แก้ไขข้อมูล',
  `modified_date` datetime DEFAULT NULL COMMENT 'วันเดือนปี ที่แก้ไขข้อมูล',
  PRIMARY KEY (`subject_id`),
  KEY `fk_subject_has_school_idx` (`school_id`),
  CONSTRAINT `fk_subject_has_school` FOREIGN KEY (`school_id`) REFERENCES `school` (`school_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `lesson_plan` (
  `lesson_plan_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'PK แผนการเรียน',
  `school_id` int(11) NOT NULL COMMENT 'FK โรงเรียน',
  `name` varchar(128) NOT NULL DEFAULT '' COMMENT 'ชื่อแผนการเรียน',
  `description` text COMMENT 'รายละเอียด',
  `class_range` tinyint NOT NULL COMMENT '*Fix ช่วงชั้น',
  `created_by_id` int(11) DEFAULT NULL COMMENT 'FK ผู้ใส่ข้อมูล',
  `created_date` datetime DEFAULT NULL COMMENT 'วันเดือนปี ที่ใส่ข้อมูล',
  `modified_by_id` int(11) DEFAULT NULL COMMENT 'FK ผู้แก้ไขข้อมูล',
  `modified_date` datetime DEFAULT NULL COMMENT 'วันเดือนปี ที่แก้ไขข้อมูล',
  PRIMARY KEY (`lesson_plan_id`),
  KEY `fk_lesson_plan_has_school_idx` (`school_id`),
  CONSTRAINT `fk_lesson_plan_has_school` FOREIGN KEY (`school_id`) REFERENCES `school` (`school_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `lesson_plan_subject` (
  `lesson_plan_subject_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'PK ตารางรายวิชาในแผนการเรียน',
  `school_id` int(11) NOT NULL COMMENT 'FK โรงเรียน',
  `lesson_plan_id` int(11) NOT NULL COMMENT 'FK รหัสแผนการเรียน',
  `subject_id` int(11) NOT NULL COMMENT 'FK รหัสวิชา',
  `class_year` tinyint(4) NOT NULL COMMENT '*Fix ชั้นปีที่ เช่น ม 1',
  `semester` tinyint(4) NOT NULL COMMENT '*Fix เทอมการศึกษา เช่น สอนเทอม 1 , เทอม 2',
  `created_by_id` int(11) DEFAULT NULL COMMENT 'FK ผู้ใส่ข้อมูล',
  `created_date` datetime DEFAULT NULL COMMENT 'วันเดือนปี ที่ใส่ข้อมูล',
  `modified_by_id` int(11) DEFAULT NULL COMMENT 'FK ผู้แก้ไขข้อมูล',
  `modified_date` datetime DEFAULT NULL COMMENT 'วันเดือนปี ที่แก้ไขข้อมูล',
  PRIMARY KEY (`lesson_plan_subject_id`),
  KEY `fk_lesson_plan_subject_has_school_idx` (`school_id`),
  KEY `fk_lesson_plan_id_idx` (`lesson_plan_id`),
  KEY `fk_subject_id_idx` (`subject_id`),
  CONSTRAINT `lesson_plan_subject_subject_id_fk` FOREIGN KEY (`subject_id`) REFERENCES `subject` (`subject_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `lesson_plan_subject_lesson_plan_id_fk` FOREIGN KEY (`lesson_plan_id`) REFERENCES `lesson_plan` (`lesson_plan_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_lesson_plan_subject_has_school` FOREIGN KEY (`school_id`) REFERENCES `school` (`school_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `class_room_lesson_plan` (
  `class_room_lesson_plan_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'PK ตารางจัดการแผนการเรียน',
  `school_id` int(11) NOT NULL COMMENT 'FK โรงเรียน',
  `class_room_id` int(11) NOT NULL COMMENT 'FK รหัสห้องเรียน',
  `lesson_plan_id` int(11) NOT NULL COMMENT 'FK รหัสแผนการเรียน',
  `academic_year` int(11) NOT NULL COMMENT 'ปีการศึกษา',
  `created_by_id` int(11) DEFAULT NULL COMMENT 'FK ผู้ใส่ข้อมูล',
  `created_date` datetime DEFAULT NULL COMMENT 'วันเดือนปี ที่ใส่ข้อมูล',
  `modified_by_id` int(11) DEFAULT NULL COMMENT 'FK ผู้แก้ไขข้อมูล',
  `modified_date` datetime DEFAULT NULL COMMENT 'วันเดือนปี ที่แก้ไขข้อมูล',
  PRIMARY KEY (`class_room_lesson_plan_id`),
  KEY `fk_class_room_lesson_plan_has_school_idx` (`school_id`),
  KEY `fk_class_room_id_idx` (`class_room_id`),
  KEY `fk_lesson_plan_id_idx` (`lesson_plan_id`),
  CONSTRAINT `class_room_lesson_plan_lesson_plan_id_fk` FOREIGN KEY (`lesson_plan_id`) REFERENCES `lesson_plan` (`lesson_plan_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `class_room_lesson_plan_class_room_id_fk` FOREIGN KEY (`class_room_id`) REFERENCES `class_room` (`class_room_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_class_room_lesson_plan_has_school` FOREIGN KEY (`school_id`) REFERENCES `school` (`school_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='แผนการเรียนรายชั้น';

ALTER TABLE `building` ADD `created_by_id` INT NULL AFTER `capacity`, ADD `created_date` DATETIME NULL AFTER `created_by_id`, ADD `modified_by_id` INT NULL AFTER `created_date`, ADD `modified_date` DATETIME NULL AFTER `modified_by_id`;
ALTER TABLE `class_room` ADD `created_by_id` INT NULL AFTER `capacity`, ADD `created_date` DATETIME NULL AFTER `created_by_id`, ADD `modified_by_id` INT NULL AFTER `created_date`, ADD `modified_date` DATETIME NULL AFTER `modified_by_id`;

/* 
   Description:  แก้ไขโครงสร้างตาราง และเพิ่มตารางผู้สอน
   Date: 16/04/2015
*/
ALTER TABLE `class_room_lesson_plan` CHANGE `academic_year` `academic_year` VARCHAR(4) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'ปีการศึกษา';

CREATE TABLE IF NOT EXISTS `teaching` (
  `teaching_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'PK อาจารย์ผู้สอน',
  `school_id` int(11) NOT NULL COMMENT 'FK โรงเรียน',
  `personnel_id` int(11) DEFAULT NULL COMMENT 'FK บุคลากร',
  `personnel_name_tmp` varchar(150) DEFAULT NULL COMMENT 'ชื่อชั่วคราวบุคลากร',
  `subject_id` int(11) NOT NULL COMMENT 'FK วิชา',
  `academic_year` VARCHAR(4) NOT NULL COMMENT 'ปีการศึกษา',
  `created_by_id` int(11) NULL COMMENT 'FK ผู้เพิ่มข้อมูล',
  `created_date` datetime NULL COMMENT 'วันเวลาที่เพิ่ม',
  `modified_by_id` int(11) NULL COMMENT 'FK ผู้แก้ไขข้อมูล',
  `modified_date` datetime NULL COMMENT 'วันเวลาที่แก้ไข',
  PRIMARY KEY (`teaching_id`),
  KEY `fk_teaching_has_school_idx` (`school_id`),
  KEY `fk_teaching_has_personnel_idx` (`personnel_id`),
  KEY `fk_teaching_has_subject_idx` (`subject_id`),
  CONSTRAINT `fk_teaching_has_school` FOREIGN KEY (`school_id`) REFERENCES `school` (`school_id`),
  CONSTRAINT `fk_teaching_has_personnel` FOREIGN KEY (`personnel_id`) REFERENCES `personnel` (`personnel_id`),
  CONSTRAINT `fk_teaching_has_subject` FOREIGN KEY (`subject_id`) REFERENCES `subject` (`subject_id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='อาจารย์ผู้สอน';
  
/* 
   Description:  เพิ่มตารางสอน
   Date: 17/04/2015
*/
 CREATE TABLE IF NOT EXISTS `timetable` (
  `timetable_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'PK ตารางสอน',
  `school_id` int(11) NOT NULL COMMENT 'FK โรงเรียน',
  `class_room_id` int(11) NOT NULL COMMENT 'FK ชั้นเรียน',
  `teaching_id` int(11) DEFAULT NULL COMMENT 'FK อาจารย์ผู้สอน',
  `section` tinyint(4) NOT NULL COMMENT 'คาบเรียน',
  `working_day` tinyint(4) NOT NULL COMMENT 'วันที่สอน',
  `created_by_id` int(11) NULL COMMENT 'FK ผู้เพิ่มข้อมูล',
  `created_date` datetime NULL COMMENT 'วันเวลาที่เพิ่ม',
  `modified_by_id` int(11) NULL COMMENT 'FK ผู้แก้ไขข้อมูล',
  `modified_date` datetime NULL COMMENT 'วันเวลาที่แก้ไข',
  PRIMARY KEY (`timetable_id`),
  KEY `fk_timetable_has_school_idx` (`school_id`),
  KEY `fk_timetable_has_class_room_idx` (`class_room_id`),
  KEY `fk_timetable_has_teaching_idx` (`teaching_id`),
  CONSTRAINT `fk_timetable_has_school` FOREIGN KEY (`school_id`) REFERENCES `school` (`school_id`),
  CONSTRAINT `fk_timetable_has_class_room` FOREIGN KEY (`class_room_id`) REFERENCES `class_room` (`class_room_id`),
  CONSTRAINT `fk_timetable_has_teaching` FOREIGN KEY (`teaching_id`) REFERENCES `teaching` (`teaching_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ตารางสอน';