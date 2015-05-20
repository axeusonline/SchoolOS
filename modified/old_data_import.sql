-- ? จะมีอยู่ทั้งหมด 3 แบบ ซึ่งแสดงถึง ชื่อ DATABASE ของโรงเรียนเก่า
-- ?; จะอยู่ใน USE ?; หมายถึงกำหนดว่า SQL ล่างคำสั่ง USE จะไปทำกับ DATABASE  ของโรงเรียนเก่า
-- ?, จะอยู่ใน SELECT หมายถึงให้ใส่ PK ของ school_id ที่โรงเรียนสมัครก่อนหน้านี้
-- ?. จะอยู่ใน FROM ?.table หมายถึงว่า เป็นการดึงจากตารางเป้าหมาย ภายใน DATABASE ของโรงเรียนเก่า
-- หลักการคือ replace ? ให้เป็นค่าที่ต้องการแล้วนำไปรัน

-- ################ FAMILY ################
USE ?;
CREATE TABLE schoolos.family_tmp SELECT * FROM family;

USE schoolos;
DELETE FROM family_tmp WHERE LENGTH(people_id) <> 13;
DELETE FROM family_tmp WHERE people_id IN (SELECT people_id FROM family);
ALTER TABLE `family_tmp` ADD `current_district_id` INT NOT NULL AFTER `current_city_id`;
UPDATE `family_tmp` f SET f.current_province_id = 1 WHERE f.current_province_id IS NULL;
UPDATE `family_tmp` f SET f.current_district_id = 1 WHERE f.current_district_id IS NULL;
UPDATE `family_tmp` f SET f.current_city_id = 1 WHERE f.current_city_id IS NULL;
UPDATE `family_tmp` f SET f.current_postcode_id = 1 WHERE f.current_postcode_id IS NULL;
UPDATE family_tmp f
SET f.current_city_id = 1, f.current_district_id = 1, f.current_postcode_id = 1 
WHERE f.current_province_id = 1 OR f.current_province_id IS NULL;
UPDATE `family_tmp` f SET f.current_district_id = 1 WHERE f.current_district_id = 0;
UPDATE `family_tmp` f SET f.current_province_id = 1 WHERE f.current_district_id = 1 OR f.current_province_id IS NULL;

ALTER TABLE `family_tmp`
  DROP `family_id`,
  DROP `firstname_rd`,
  DROP `lastname_rd`,
  DROP `salary_status`,
  DROP `job_street`,
  DROP `job_city`,
  DROP `job_district`,
  DROP `job_postcode`,
  DROP `job_city_id`,
  DROP `job_province_id`,
  DROP `job_postcode_id`,
  DROP `current_street`,
  DROP `current_city`,
  DROP `current_district`,
  DROP `current_postcode`,
  DROP `created_by_id`,
  DROP `created_by_type`,
  DROP `created_date`,
  DROP `modified_by_id`,
  DROP `modified_by_type`,
  DROP `modified_date`;
  
INSERT INTO `family` (`people_id`, `people_id_type`, `prename`, `firstname`, `lastname`, `firstname_nd`, `lastname_nd`, `religion`, `gender`, `race`, `nationality`, `birth_date`, `tel`, `mobile`, `email`, `salary`, `alive_status`, `occupation`, `job_address`, `current_address`, `current_city_id`, `current_district_id`, `current_province_id`, `current_postcode_id`)
SELECT * FROM family_tmp;

-- ################ PERSONNEL ################
USE ?;

ALTER TABLE `personal_work` CHANGE `personal_id` `personal_fk_id` INT(11) NOT NULL COMMENT 'FK บุคลากร';
ALTER TABLE `personal_work` DROP `created_by_id`, DROP `created_by_type`, DROP `created_date`, DROP `modified_by_id`, DROP `modified_by_type`, DROP `modified_date`;

CREATE TABLE schoolos.personnel_tmp
SELECT * FROM personal_work pw
INNER JOIN personal p ON pw.personal_fk_id = p.personal_id;
ALTER TABLE schoolos.`personnel_tmp` CHANGE `spouse_id` `spouse_id` VARCHAR(13) NULL DEFAULT NULL COMMENT 'fk ภารยา', CHANGE `father_id` `father_id` VARCHAR(13) NULL DEFAULT NULL COMMENT 'FK บิดา', CHANGE `mother_id` `mother_id` VARCHAR(13) NULL DEFAULT NULL COMMENT 'FK มารดา';
UPDATE schoolos.personnel_tmp pt
INNER JOIN family pw ON pt.father_id = pw.family_id
SET pt.father_id  = pw.people_id;
UPDATE schoolos.personnel_tmp pt
INNER JOIN family pw ON pt.mother_id = pw.family_id
SET pt.mother_id  = pw.people_id;
UPDATE schoolos.personnel_tmp pt
INNER JOIN family pw ON pt.spouse_id = pw.family_id
SET pt.spouse_id  = pw.people_id;

USE schoolos;
ALTER TABLE `personnel_tmp` ADD `census_district_id` INT NOT NULL AFTER `census_city_id`;
ALTER TABLE `personnel_tmp` ADD `current_district_id` INT NOT NULL AFTER `current_city_id`;
UPDATE personnel_tmp pt
INNER JOIN family pw ON pt.father_id  = pw.people_id
SET pt.father_id  = pw.people_id;
UPDATE personnel_tmp pt
INNER JOIN family pw ON pt.mother_id  = pw.people_id
SET pt.mother_id  = pw.people_id;
UPDATE personnel_tmp pt
INNER JOIN family pw ON pt.spouse_id  = pw.people_id
SET pt.spouse_id  = pw.people_id;
UPDATE personnel_tmp pt
SET pt.father_id = NULL
WHERE LENGTH(pt.father_id) <> 13;
UPDATE personnel_tmp pt
SET pt.mother_id = NULL
WHERE LENGTH(pt.mother_id) <> 13;
UPDATE personnel_tmp pt
SET pt.spouse_id = NULL
WHERE LENGTH(pt.spouse_id) <> 13;
UPDATE personnel_tmp pt
INNER JOIN family f ON pt.father_id = f.people_id
SET pt.father_id = f.family_id;
UPDATE personnel_tmp pt
INNER JOIN family f ON pt.mother_id = f.people_id
SET pt.mother_id = f.family_id;
UPDATE personnel_tmp pt
INNER JOIN family f ON pt.spouse_id = f.people_id
SET pt.spouse_id = f.family_id;

ALTER TABLE `personnel_tmp`
  DROP `personal_fk_id`,
  DROP `personal_image`,
  DROP `salary`,
  DROP `tax`,
  DROP `other_revenue`,
  DROP `extra_revenue`,
  DROP `census_street`,
  DROP `census_city`,
  DROP `census_district`,
  DROP `census_postcode`,
  DROP `birth_address`,
  DROP `birth_street`,
  DROP `birth_city`,
  DROP `birth_district`,
  DROP `birth_postcode`,
  DROP `birth_city_id`,
  DROP `birth_province_id`,
  DROP `birth_postcode_id`,
  DROP `current_street`,
  DROP `current_city`,
  DROP `current_district`,
  DROP `current_postcode`,
  DROP `recruit_type`,
  DROP `expert`,
  DROP `license_lecturer_number`,
  DROP `license_lecturer_type`,
  DROP `license_lecturer_issued_date`,
  DROP `license_lecturer_expired_date`,
  DROP `license_11_number`,
  DROP `license_issue_area`,
  DROP `license_issue_province_id`,
  DROP `license_17_number`,
  DROP `license_18_number`,
  DROP `license_19_number`,
  DROP `fill_degree_post`,
  DROP `fill_degree_post_date`,
  DROP `capability`,
  DROP `created_by_type`,
  DROP `modified_by_type`;
  
INSERT INTO `personnel` (`school_id`, `people_id`, `people_id_type`, `personnel_code`, `prename`, `firstname`, `lastname`, `firstname_nd`, `lastname_nd`, `firstname_rd`, `lastname_rd`, `nickname`, `gender`, `religion`, `race`, `nationality`, `marital_status`, `birth_date`, `blood`, `height`, `weight`, `congenital_disease`, `personnel_status`, `job_position`, `department`, `tel`, `mobile`, `email`, `census_address`, `census_city_id`, `census_district_id`, `census_province_id`, `census_postcode_id`, `current_address`, `current_city_id`, `current_district_id`, `current_province_id`, `current_postcode_id`, `employment_type`, `start_work_date`, `recruit_by_id`, `recruit_date`, `recruit_description`, `resign_by_id`, `resign_type`, `resign_date`, `bank_name`, `bank_account_number`, `bank_account_type`, `bank_account_name`, `bank_account_branch`, `bank_account_province_id`, `father_id`, `mother_id`, `spouse_id`, `created_by_id`, `created_date`, `modified_by_id`, `modified_date`)
SELECT ?, `people_id`, `people_id_type`, `personal_code`, `prename`, `firstname`, `lastname`, `firstname_en`, `lastname_en`, `firstname_rd`, `lastname_rd`, `nickname`, `gender`, `religion`, `race`, `nationality`, `marital_status`, `birth_date`, `blood`, `height`, `weight`, `congenital_disease`, `personal_status`, `job_position_id`, `department_id`, `tel`, `mobile`, `email`, `census_address`, `census_city_id`, `census_district_id`, `census_province_id`, `census_postcode_id`, `current_address`, `current_city_id`, `current_district_id`, `current_province_id`, `current_postcode_id`, `employment_type`, `start_work_date`, `recruit_by_id`, `recruit_date`, `recruit_description`, `resign_by_id`, `resign_type`, `resign_date`, `bank_name`, `bank_account_number`, `bank_account_type`, `bank_account_name`, `bank_account_branch`, `bank_account_province_id`, `father_id`, `mother_id`, `spouse_id`, `created_by_id`, `created_date`, `modified_by_id`, `modified_date` 
FROM personnel_tmp WHERE people_id NOT IN (SELECT people_id FROM personnel) GROUP BY people_id;
  
DROP TABLE personnel_tmp;
DROP TABLE family_tmp;

-- ################ SUBJECT ################

INSERT INTO `subject` (`school_id`, `code`, `code_nd`, `name`, `name_nd`, `weight`, `hours`, `lesson_type`, `subject_type`, `description`, `created_by_id`, `created_date`, `modified_by_id`, `modified_date`) 
SELECT ?, REPLACE(`subject_code`," ",""), REPLACE(`subject_code_nd`," ",""), `subject_name`, `subject_name_nd`, `subject_weight`, `subject_hours`, `lesson_types`, `subject_type_id`,  `description`,  `created_by_id`, `created_date`, `modified_by_id`, `modified_date` FROM ?.subject;

-- ################ LESSON PLAN ################

CREATE TABLE schoolos.lesson_plan_tmp 
SELECT l.*, c.class_range FROM lesson_plan l
INNER JOIN course c ON c.course_id = l.course_id;

USE schoolos;
INSERT INTO `lesson_plan` (`school_id`, `name`, `description`, `class_range`, `created_by_id`, `created_date`, `modified_by_id`, `modified_date`) 
SELECT ?,lesson_plan_name,description,class_range,?,`created_date`,null,null FROM lesson_plan_tmp;
DROP TABLE lesson_plan_tmp;
CREATE TABLE lesson_plan_tmp SELECT * FROM lesson_plan WHERE school_id = ?;
ALTER TABLE `lesson_plan_tmp` DROP `lesson_plan_id`;
DROP TABLE lesson_plan_tmp;

-- ################ LESSON PLAN SUBJECT ################

USE ?;
CREATE TABLE schoolos.lesson_plan_subject_tmp
SELECT l.lesson_plan_name,s.subject_code,ls.class_year,ls.semester,22,l.created_date FROM lesson_plan_subject ls
INNER JOIN course_subject cs ON cs.course_subject_id = ls.course_subject_id
INNER JOIN subject s ON cs.subject_id = s.subject_id
INNER JOIN lesson_plan l ON l.lesson_plan_id = ls.lesson_plan_id;

/*
ทำใน Host ของจริง
CREATE TABLE lesson_plan_tmp SELECT * FROM lesson_plan WHERE school_id = ?;
-- Export lesson_plan_subject_tmp ออกมาแล้วใส่ใน ของจริง
UPDATE lesson_plan_subject_tmp lst
INNER JOIN lesson_plan l ON l.name = lst.lesson_plan_name
SET lst.lesson_plan_name = l.lesson_plan_id
WHERE l.school_id = ?;
UPDATE lesson_plan_subject_tmp lst
INNER JOIN subject s ON s.code = lst.subject_code
SET lst.subject_code = s.subject_id
WHERE s.school_id = ?;
INSERT INTO `lesson_plan_subject` (`school_id`, `lesson_plan_id`, `subject_id`, `class_year`, `semester`, `created_by_id`, `created_date`, `modified_by_id`, `modified_date`)
SELECT ?,`lesson_plan_name`,subject_code,class_year,semester,?, `created_date`, null,null FROM lesson_plan_subject_tmp;
DROP TABLE lesson_plan_tmp;
DROP TABLE  lesson_plan_subject_tmp;
*/

UPDATE user  u
INNER JOIN personnel p ON u.ref_user_id = p.personnel_id 
SET u.email = 'hishammuhamadamin@gmail.com', p.email = 'hishammuhamadamin@gmail.com' 
WHERE u.firstname = 'ดือเร๊ะ' AND u.lastname = 'เวซีลา' AND u.ref_user_type = 1;

