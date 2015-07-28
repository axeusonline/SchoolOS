/* 
 *  Description:  เพิ่มตารางสำหรับห้องบุคลากร เพื่อให้การีมได้ใช้เป็นข้อมูลพื้นฐาน
 *  Date: 04/04/2015
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
 *  Description:  เพิ่มตารางสำหรับงานแผนการเรียน
 * Date: 05/04/2015
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
 *  Description:  แก้ไขโครงสร้างตาราง และเพิ่มตารางผู้สอน
 *  Date: 16/04/2015
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
 *  Description:  เพิ่มตารางสอน
 *  Date: 17/04/2015
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

ALTER TABLE  `family` CHANGE  `current_address`  `current_address` VARCHAR( 256 ) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT  'สถานที่อยู่ปัจจุบัน',
CHANGE  `current_city_id`  `current_city_id` INT( 11 ) NULL COMMENT  'ตำบล สถานที่อยู่ปัจจุบัน',
CHANGE  `current_district_id`  `current_district_id` INT( 11 ) NULL COMMENT  'อำเภอ สถานที่อยู่ปัจจุบัน',
CHANGE  `current_province_id`  `current_province_id` INT( 11 ) NULL COMMENT  'จังหวัด สถานที่อยู่ปัจจุบัน',
CHANGE  `current_postcode_id`  `current_postcode_id` INT( 11 ) NULL COMMENT  'ไปรษณีย์ สถานที่อยู่ปัจจุบัน';

/*
 * Description: เพิ่มผู้ใช้งาน
 * Date: 30/04/2015
*/
CREATE TABLE IF NOT EXISTS `user` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'PK ตารางผู้ใช้',
  `school_id` int(11) NOT NULL COMMENT 'FK โรงเรียน',
  `firstname` varchar(250) NOT NULL COMMENT 'ชื่อ',
  `lastname` varchar(250) NOT NULL COMMENT 'สกุล',
  `email` varchar(128) NOT NULL COMMENT 'อีเมลล์',
  `password` varchar(128) NOT NULL COMMENT 'รหัสผ่าน',
  `status` tinyint(4) NOT NULL COMMENT '*Fix สถานะการใช้งาน',
  `ref_user_id` int(11) NOT NULL COMMENT 'FK นักเรียน บุคลากร',
  `ref_user_type` tinyint(4) NOT NULL COMMENT '*Fix ประเภทผู้ใช้',
  `permission` text COMMENT 'สิทธิ์การใช้งาน',
  `created_by_id` int(11) DEFAULT NULL COMMENT 'FK ผู้เพิ่มข้อมูล',
  `created_date` datetime DEFAULT NULL COMMENT 'วันเดือนปีที่เพิ่ม',
  `modified_by_id` int(11) DEFAULT NULL COMMENT 'FL ผู้แก้ไขข้อมูล',
  `modified_date` datetime DEFAULT NULL COMMENT 'วันเดือนปีที่แก้ไขข้อมูล',
  PRIMARY KEY (`user_id`),
  KEY `fk_user_has_school_idx` (`school_id`),
  CONSTRAINT `fk_user_has_school` FOREIGN KEY (`school_id`) REFERENCES `school` (`school_id`),
  UNIQUE KEY `username_UNIQUE` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='บัญชีผู้ใช้งาน';

INSERT INTO user (school_id,firstname,lastname,email,password,status,ref_user_id,ref_user_type,permission,created_by_id,created_date)
SELECT school_id,firstname,lastname,email,password,0,school_id,0,null,school_id,now() FROM school;

ALTER TABLE `school`
  DROP `firstname`,
  DROP `lastname`,
  DROP `email`,
  DROP `password`;
  

ALTER TABLE `school` ADD `student_signup_pass` VARCHAR(10) NULL COMMENT 'รหัสสมัครสำหรับนักเรียน' AFTER `short_url`, ADD `personnel_signup_pass` VARCHAR(10) NULL COMMENT 'รหัสสมัครสำหรับบุคคลากร' AFTER `student_signup_pass`;

/*
 * Description: แก้ไขข้อมูลการเรียน
 * Date: 01/05/2015
 */ 
 ALTER TABLE `student_study`
  DROP `boarding_type`,
  DROP `recruit_by_id`,
  DROP `recruit_date`,
  DROP `recruit_type`,
  DROP `recruit_class_year`,
  DROP `recruit_year`,
  DROP `recruit_semester`,
  DROP `recruit_description`;
  
  ALTER TABLE `student` CHANGE `family_status` `family_status` TINYINT(4) NULL COMMENT '*Fix สถานภาพ';
 
  /*
 * Description: อาจารย์ประจำชั้น
 * Date: 03/05/2015
 */ 
  ALTER TABLE `personnel` ADD `resign_description` TEXT NULL COMMENT 'รายละเอียดการจำหน่ายออก' AFTER `resign_date`;
  
  CREATE TABLE IF NOT EXISTS `teacher_homeroom` (
  `teacher_homeroom_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'PK อาจารย์ประจำชั้น',
  `school_id` int(11) NOT NULL COMMENT 'FK โรงเรียน',
  `personnel_id` int(11) DEFAULT NULL COMMENT 'FK บุคลากร',
  `personnel_name_tmp` varchar(150) DEFAULT NULL COMMENT 'ชื่อชั่วคราวบุคลากร',
  `class_room_id` int(11) NOT NULL COMMENT 'FK ชั้นเรียน',
  `academic_year` varchar(4) NOT NULL COMMENT 'ปีการศึกษา',
  `created_by_id` int(11) DEFAULT NULL COMMENT 'FK ผู้เพิ่มข้อมูล',
  `created_date` datetime DEFAULT NULL COMMENT 'วันเวลาที่เพิ่ม',
  `modified_by_id` int(11) DEFAULT NULL COMMENT 'FK ผู้แก้ไขข้อมูล',
  `modified_date` datetime DEFAULT NULL COMMENT 'วันเวลาที่แก้ไข',
  PRIMARY KEY (`teacher_homeroom_id`),
  KEY `fk_teacher_homeroom_has_school_idx` (`school_id`),
  KEY `fk_teacher_homeroom_has_personnel_idx` (`personnel_id`),
  KEY `fk_teacher_homeroom_has_class_room_idx` (`class_room_id`),
  CONSTRAINT `fk_teacher_homeroom_has_personnel` FOREIGN KEY (`personnel_id`) REFERENCES `personnel` (`personnel_id`),
  CONSTRAINT `fk_teacher_homeroom_has_school` FOREIGN KEY (`school_id`) REFERENCES `school` (`school_id`),
  CONSTRAINT `fk_teacher_homeroom_has_class_room` FOREIGN KEY (`class_room_id`) REFERENCES `class_room` (`class_room_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='อาจารย์ประจำชั้น';

ALTER TABLE `student_study`
  DROP `resign_class_year`,
  DROP `resign_year`,
  DROP `resign_semester`;
  
/* 
 * Description : รองรับข้อมูลไม่ครบของนักเรียน และ พฤติกรรม
 * Date : 04/05/2013
 */
ALTER TABLE `student` CHANGE `gender` `gender` TINYINT(4) NULL COMMENT '*Fix เพศ ', CHANGE `religion` `religion` TINYINT(4) NULL COMMENT '*Fix ศาสนา ', CHANGE `race` `race` SMALLINT(6) NULL COMMENT '*Fix เชื่อชาติ ', CHANGE `nationality` `nationality` SMALLINT(6) NULL COMMENT '*Fix สัญชาติ', CHANGE `birth_date` `birth_date` DATE NULL COMMENT 'วันเกิด เก็บเป็น 01-12-2011', CHANGE `blood` `blood` TINYINT(4) NULL COMMENT '*Fix หมู่เลือด', CHANGE `sibling_qty` `sibling_qty` TINYINT(4) NULL DEFAULT '0' COMMENT 'จำนวนพี่น้อง', CHANGE `sibling_sequence` `sibling_sequence` TINYINT(4) NULL DEFAULT '0' COMMENT 'พี่น้องลำดับที่';

ALTER TABLE `student_study` CHANGE `current_address` `current_address` VARCHAR(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL, CHANGE `current_city_id` `current_city_id` INT(11) NULL COMMENT 'ตำบล ที่อยู่ปัจจุบัน', CHANGE `current_district_id` `current_district_id` INT(11) NULL COMMENT 'อำเภอ ที่อยู่ปัจจุบัน', CHANGE `current_province_id` `current_province_id` INT(11) NULL COMMENT 'จังหวัด ที่อยู่ปัจจุบัน', CHANGE `current_postcode_id` `current_postcode_id` INT(11) NULL COMMENT 'ไปรษณีย์ ที่อยู่ปัจจุบัน', CHANGE `graduated_school` `graduated_school` VARCHAR(250) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'โรงเรียนที่จบ', CHANGE `graduated_school_province_id` `graduated_school_province_id` INT(11) NULL COMMENT 'จังหวัดโรงเรียนที่จบ', CHANGE `graduated_gpa` `graduated_gpa` DOUBLE NULL COMMENT 'เกรดเฉลี่ยที่จบ', CHANGE `graduated_year` `graduated_year` VARCHAR(4) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'ปีการศึกษา', CHANGE `graduated_class_range` `graduated_class_range` TINYINT(4) NULL COMMENT 'ช่วงชั้นทีจบ';
 
ALTER TABLE `teaching` ADD `weekend` VARCHAR(15) NULL COMMENT 'วันหยุดอาจารย์' AFTER `subject_id`;

CREATE TABLE IF NOT EXISTS `behavior` (
  `behavior_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'PK ตารางพฤติกรรม',
  `school_id` int(11) NOT NULL COMMENT 'FK โรงเรียน',
  `name` varchar(128) NOT NULL COMMENT 'ชื่อพฤติกรรม',
  `min_score` double NOT NULL COMMENT 'คะแนนต่ำสุด',
  `max_score` double NOT NULL COMMENT 'คะแนนสูงสุด',
  `severity_type` tinyint(4) NOT NULL COMMENT '*Fix ระดับความรุนแรง',
  `description` varchar(256) DEFAULT NULL COMMENT 'ราบละเอียด',
  `created_by_id` int(11) DEFAULT NULL COMMENT 'FK ผู้ใส่ข้อมูล',
  `created_date` datetime DEFAULT NULL COMMENT 'วันที่ใส่ข้อมูล',
  `modified_by_id` int(11) DEFAULT NULL COMMENT 'ผู้แก้ไขข้อมูล',
 `modified_date` datetime DEFAULT NULL COMMENT 'วันที่แก้ไขข้อมูล',
  PRIMARY KEY (`behavior_id`),
  KEY `fk_behavior_has_school_idx` (`school_id`),
  CONSTRAINT `fk_behavior_has_school` FOREIGN KEY (`school_id`) REFERENCES `school` (`school_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ตารางพฤติกรรม';

CREATE TABLE IF NOT EXISTS `student_behavior` (
  `student_behavior_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'PK พฤติกรรมนักเรียน',
  `school_id` int(11) NOT NULL COMMENT 'FK โรงเรียน',
  `student_study_id` int(11) NOT NULL COMMENT 'FK ข้อมูลการเรียน',
  `behavior_id` int(11) NOT NULL COMMENT 'FK พฤติกรรม',
  `score` double NOT NULL COMMENT 'คะแนนที่หัก',
  `date` date NOT NULL COMMENT 'วันที่หัก',
  `description` text COMMENT 'รายละเอียด',
  `created_by_id` int(10) DEFAULT NULL COMMENT 'FK ผู้ใส่ข้อมูล',
  `created_date` datetime DEFAULT NULL COMMENT 'วันเดือนปีที่ใส่ข้อมูล',
  `modified_by_id` int(11) DEFAULT NULL COMMENT 'FK ผู้แก้ไขข้อมูล',
  `modified_date` datetime DEFAULT NULL COMMENT 'วันเดือนปีที่แก้ไขข้อมูล',
  PRIMARY KEY (`student_behavior_id`),
  KEY `fk_student_behavior_has_school_idx` (`school_id`),
  KEY `fk_student_behavior_has_student_study_idx` (`student_study_id`),
  KEY `fk_student_behavior_has_behavior_idx` (`behavior_id`),
  CONSTRAINT `fk_student_behavior_has_behavior` FOREIGN KEY (`behavior_id`) REFERENCES `behavior` (`behavior_id`),
  CONSTRAINT `fk_student_behavior_has_student_study` FOREIGN KEY (`student_study_id`) REFERENCES `student_study` (`student_study_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='พฤติกรรมนักเรียน';

/* 
 * Description : เพิ่มเติมข้อมูลการบรรจุ ของ Personnel
 * Date : 08/05/2015
 */
ALTER TABLE `personnel`  
ADD `license_lecturer_number` VARCHAR(32) NULL COMMENT 'เลขที่ใบประกอบวิชาชีพครู' AFTER `start_work_date`,  
ADD `license_lecturer_type` TINYINT NULL COMMENT 'ประเภทใบประกอบวิชาชีพ' AFTER `license_lecturer_number`,  
ADD `license_lecturer_issued_date` DATE NULL COMMENT 'วัน เดือน ปี ที่ได้หมายเลขครู' AFTER `license_lecturer_type`,  
ADD `license_lecturer_expired_date` DATE NULL COMMENT 'วัน เดือน ปี ที่หมดอายุหมายเลขครู' AFTER `license_lecturer_issued_date`,  
ADD `license_11_number` VARCHAR(32) NULL COMMENT 'เลขที่ใบอนุญาติ สช 11' AFTER `license_lecturer_expired_date`,  
ADD `license_issue_area` VARCHAR(128) NULL COMMENT 'เขตพื้นที่ ที่ออก' AFTER `license_11_number`,  
ADD `license_issue_province_id` INT NULL COMMENT 'ออกโดย(จังหวัด)' AFTER `license_issue_area`,  
ADD `license_17_number` VARCHAR(32) NULL COMMENT 'เลขที่ใบอนุญาติ สช 17' AFTER `license_issue_province_id`,  
ADD `license_18_number` VARCHAR(32) NULL COMMENT 'เลขที่ใบอนุญาติ สช 18' AFTER `license_17_number`,  
ADD `license_19_number` VARCHAR(32) NULL COMMENT 'เลขที่ใบอนุญาติ สช 19' AFTER `license_18_number`,  
ADD `fill_degree_post` VARCHAR(128) NULL COMMENT 'วุฒิที่ได้รับการบรรจุ' AFTER `license_19_number`,  
ADD `fill_degree_post_date` DATE NULL COMMENT 'วัน เดือน ปีที่ได้รับการบรรจุ' AFTER `fill_degree_post`;

ALTER TABLE `personnel_graduated_history` ADD `school_id` INT NOT NULL COMMENT 'FK โรงเรียน' ;

ALTER TABLE `personnel_graduated_history`
ADD CONSTRAINT fk_personnel_graduated_history_has_school
FOREIGN KEY (school_id)
REFERENCES school(school_id);

DELETE FROM student WHERE school_id NOT IN (SELECT school_id FROM school);

ALTER TABLE `student`
ADD CONSTRAINT `fk_student_has_father` FOREIGN KEY (father_id) REFERENCES family(family_id),
ADD CONSTRAINT `fk_student_has_mother` FOREIGN KEY (mother_id) REFERENCES family(family_id),
ADD CONSTRAINT `fk_student_has_school` FOREIGN KEY (school_id) REFERENCES school(school_id);

ALTER TABLE `student_study`
ADD CONSTRAINT `fk_student_study_has_guardian` FOREIGN KEY (guardian_id) REFERENCES family(family_id),
ADD CONSTRAINT `fk_student_study_has_student` FOREIGN KEY (student_id) REFERENCES student(student_id),
ADD CONSTRAINT `fk_student_study_has_school` FOREIGN KEY (school_id) REFERENCES school(school_id);

ALTER TABLE `student_behavior`
ADD CONSTRAINT `fk_student_behavior_has_school` FOREIGN KEY (school_id) REFERENCES school(school_id);

DROP TABLE `personnel_graduated_history`;
CREATE TABLE IF NOT EXISTS `personnel_graduated_history` (
  `graduated_history_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'PK ตารางประวัติการศึกษา',
  `school_id` int(11) NOT NULL COMMENT 'FK โรงเรียน',
  `personnel_id` int(11) NOT NULL COMMENT 'FK บุคลากร',
  `institute` varchar(128) NOT NULL COMMENT 'ชื่อสถาบัน',
  `graduated_level` tinyint(4) NOT NULL COMMENT '*Fix ระดับการศึกษาที่จบ',
  `degree` varchar(128) DEFAULT NULL COMMENT 'วุฒิการศึกษา',
  `major` varchar(128) DEFAULT NULL COMMENT 'วิชาเอก',
  `minor` varchar(128) DEFAULT NULL COMMENT 'วิชาโท',
  `description` varchar(256) DEFAULT NULL COMMENT 'วิชาโท',
  `year` int(11) DEFAULT NULL COMMENT 'ปีทืจบ',
  `location` varchar(128) DEFAULT NULL COMMENT 'สถานที่สถาบัน',
  `province_id` int(11) NULL COMMENT 'จังหวัดสถาบัน',
  `created_by_id` int(11) DEFAULT NULL COMMENT 'FK ผู้ใส่ข้อมูล',
  `created_date` datetime DEFAULT NULL COMMENT 'วันที่ใส่ข้อมูล',
  `modified_by_id` int(11) DEFAULT NULL COMMENT 'FK ผู้แก้ไขข้อมูล',
  `modified_date` datetime DEFAULT NULL COMMENT 'วันที่แก้ไขข้อมูล',
  PRIMARY KEY (`graduated_history_id`),
  KEY `fk_graduated_history_has_personnel_idx` (`personnel_id`),
  KEY `fk_graduated_history_has_school_idx` (`school_id`),
  CONSTRAINT `fk_graduated_history_has_personnel` FOREIGN KEY (`personnel_id`) REFERENCES `personnel` (`personnel_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_graduated_history_has_school` FOREIGN KEY (`school_id`) REFERENCES `school` (`school_id`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ประวัติการศึกษาบุคลากร';
 
UPDATE user SET permission = '0,0,0,0,0,0' WHERE school_id <> ref_user_id;
UPDATE user SET permission = '1,1,1,1,1,1' WHERE school_id = ref_user_id AND ref_user_type = 0;

/*
 * Description : แก้ไข Schema ผิด
 * Date : 11/05/2014
 * */
ALTER TABLE `personnel_graduated_history` CHANGE `year` `year` VARCHAR(4) NULL DEFAULT NULL COMMENT 'ปีทืจบ';

UPDATE personnel SET marital_status =0 WHERE marital_status >1;

UPDATE personnel p 
SET current_postcode_id = null
WHERE current_postcode_id NOT IN (SELECT postcode_id FROM postcode);

UPDATE personnel SET current_city_id = null WHERE current_city_id NOT IN (SELECT city_id FROM city);
UPDATE personnel SET department = 0 WHERE department IS null;

/*
 * Description : เพิ่มข้อมูล
 * Date : 16/05/2014
 * */
ALTER TABLE `personnel` ADD `alive_status` TINYINT NOT NULL COMMENT '*Fix สถานะการมีชีวิต' AFTER `personnel_status`;

INSERT INTO `department` VALUES
(1, NULL, 'ฝ่ายบริหาร', NULL, NULL, NULL, NULL, NULL),
(2, NULL, 'ฝ่ายงานวิชาการ', NULL, NULL, NULL, NULL, NULL),
(3, NULL, 'ฝ่ายงานทะเบียน', NULL, NULL, NULL, NULL, NULL),
(4, NULL, 'ฝ่ายงานพัฒนาบุคลากร', NULL, NULL, NULL, NULL, NULL),
(5, NULL, 'ฝ่ายงานผู้ดูแลระบบ', NULL, NULL, NULL, NULL, NULL),
(6, NULL, 'ฝ่ายพัสดุ', NULL, NULL, NULL, NULL, NULL),
(7, NULL, 'ฝ่ายธุรการ', NULL, NULL, NULL, NULL, NULL),
(8, NULL, 'ฝ่ายงานกิจการนักเรียน', NULL, NULL, NULL, NULL, NULL),
(9, NULL, 'ฝ่ายงานการเงิน', NULL, NULL, NULL, NULL, NULL),
(10, NULL, 'ฝ่ายงานพัฒนาผู้เรียน', NULL, NULL, NULL, NULL, NULL),
(11, NULL, 'ฝ่ายงานห้องสมุด', NULL, NULL, NULL, NULL, NULL),
(12, NULL, 'ฝ่ายงานหอพักนักเรียน', NULL, NULL, NULL, NULL, NULL),
(13, NULL, 'ฝ่ายงานสหกรณ์', NULL, NULL, NULL, NULL, NULL),
(14, NULL, 'ฝ่ายสมัครเรียน', NULL, NULL, NULL, NULL, NULL),
(15, NULL, 'ฝ่ายอาคารและสถานที่', NULL, NULL, NULL, NULL, NULL),
(16, NULL, 'ฝ่ายอาจารย์ผู้สอน', NULL, NULL, NULL, NULL, NULL),
(17, NULL, 'ฝ่ายอาจารย์ประจำชั้น', NULL, NULL, NULL, NULL, NULL),
(18, NULL, 'ฝ่ายงานสาระการเรียนรู้ภาษาไทย', NULL, NULL, NULL, NULL, NULL),
(19, NULL, 'ฝ่ายงานสาระการเรียนรู้ศิลปะ', NULL, NULL, NULL, NULL, NULL),
(20, NULL, 'ฝ่ายงานสาระการเรียนรู้วิทยาศาสตร์', NULL, NULL, NULL, NULL, NULL),
(21, NULL, 'ฝ่ายงานสาระการเรียนรู้สังคมศึกษา ศาสนา และ วัฒนธรรม', NULL, NULL, NULL, NULL, NULL),
(22, NULL, 'ฝ่ายงานสาระการเรียนรู้คณิตศาสตร์', NULL, NULL, NULL, NULL, NULL),
(23, NULL, 'ฝ่ายงานสาระการเรียนรู้สุขศึกษาและพละศึกษา', NULL, NULL, NULL, NULL, NULL),
(24, NULL, 'ฝ่ายงานสาระการเรียนรู้การงานอาชีพและเทคโนโลยี', NULL, NULL, NULL, NULL, NULL),
(25, NULL, 'ฝ่ายงานสาระการเรียนรู้ภาษาต่างประเทศ', NULL, NULL, NULL, NULL, NULL);

INSERT INTO `job_position` (`name`) VALUES
("กรรมการ"),
("ครูสามัญ"),
("ครูศาสนา"),
("เจ้าหน้าที่"),
("เจ้าหน้าที่หอพักนักการภารโรง"),
("พนักงานขับรถ"),
("พนักงานรักษาความปลอดภัย"),
("ข้าราชการ"),
("พนักงานช้าราชการ"),
("พนักงานช้าราชการชั่วคราว"),
("ผู้บริหาร"),
("ครูชำนาญการ"),
("ครูพิเศษ"),
("อาจารย์ฝึกสอน"),
("นักศึกษาฝึกงาน"),
("ผู้อำนวยการ"),
("นายทะเบียน"),
("เจ้าหน้าที่ไอที");

UPDATE personnel SET department = department+1;
UPDATE personnel SET job_position = job_position+1;

ALTER TABLE `personnel` CHANGE `job_position` `job_position_id` INT(11) NULL DEFAULT NULL COMMENT 'ตำแหน่ง', CHANGE `department` `department_id` INT(11) NULL DEFAULT NULL COMMENT 'แผนก';

/*
 * Description : สถานะการแก้ไข
 * Date : 25/05/2014
 * */
ALTER TABLE `user` ADD `is_edited` BOOLEAN NULL COMMENT 'สถานะการแก้ไข Username' AFTER `permission`;
ALTER TABLE `family` CHANGE `people_id` `people_id` VARCHAR(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'หมายเลขประจำตัวประชาชน', CHANGE `people_id_type` `people_id_type` TINYINT(4) NULL COMMENT '*Fix ประเภทหมายเลขประจำตัว';
ALTER TABLE `school` ADD `contact_email` VARCHAR(100) NOT NULL AFTER `province_id`;
UPDATE school s INNER JOIN user u ON s.school_id = u.school_id SET s.contact_email = u.email WHERE u.ref_user_type = 0;
ALTER TABLE `timetable` ADD `semester` TINYINT NOT NULL DEFAULT '0' COMMENT 'ภาคเรียน' AFTER `working_day`, ADD `academic_year` VARCHAR(4) NOT NULL DEFAULT '2558' COMMENT 'ปีการศึกษา' AFTER `semester`;

/*
 * Description : เช็คชื่อเข้าเรียน
 * Date : 24/07/2014
 * */
CREATE TABLE IF NOT EXISTS `student_attendance` (
  `student_attendance_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'PK ตารางเช็คชื่อ',
  `school_id` int(11) NOT NULL COMMENT 'FK โรงเรียน',
  `timetable_id` int(11) NOT NULL COMMENT 'FK ตารางสอน',
  `student_study_id` int(11) NOT NULL COMMENT 'FK นักเรียน',
  `check_date` date NOT NULL COMMENT 'วัน เดือน ปี เวลา',
  `attendance_status` tinyint(4) NOT NULL COMMENT '*Fix สถานะ',
  `created_by_id` int(11) DEFAULT NULL COMMENT 'FK ผู้ใส่ข้อมูล',
  `created_date` datetime DEFAULT NULL COMMENT 'วันเดือนปี ที่ใส่ข้อมูล',
  `modified_by_id` int(11) DEFAULT NULL COMMENT 'FK ผู้แก้ไขข้อมูล',
  `modified_date` datetime DEFAULT NULL COMMENT 'วันเดือนปี ที่แก้ไขข้อมูล',
  PRIMARY KEY (`student_attendance_id`),
  KEY `fk_student_attendance_has_student_study_idx` (`student_study_id`),
  KEY `fk_student_attendance_has_school_idx` (`school_id`),
  KEY `fk_student_attendance_has_timetable` (`timetable_id`),
  CONSTRAINT `fk_student_attendance_has_school` FOREIGN KEY (`school_id`) REFERENCES `school` (`school_id`),
  CONSTRAINT `fk_student_attendance_has_student_study` FOREIGN KEY (`student_study_id`) REFERENCES `student_study` (`student_study_id`),
  CONSTRAINT `fk_student_attendance_has_timetable` FOREIGN KEY (`timetable_id`) REFERENCES `timetable` (`timetable_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ตารางเช็คชื่อนักเรียน';
