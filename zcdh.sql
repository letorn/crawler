# 备份数据库
mysqldump -h 192.168.1.30 -u admin -p zcdh_uni_test zcdh_area zcdh_category_post zcdh_ent_ability_require zcdh_ent_account zcdh_ent_enterprise zcdh_ent_lbs zcdh_ent_post zcdh_ent_post_status zcdh_ent_promotion zcdh_industry zcdh_param zcdh_post zcdh_tag  zcdh_technology zcdh_technology_gategory zcdh_view_ent_post > zcdh_uni_test.sql.2015.7.22
mysqldump -h 192.168.1.30 -u admin -p zcdh_uni_test zcdh_ent_ability_require zcdh_ent_account zcdh_ent_enterprise zcdh_ent_lbs zcdh_ent_post zcdh_ent_post_status zcdh_ent_promotion zcdh_view_ent_post > zcdh_uni_test.sql.2015.7.22

# 创建数据库
DROP DATABASE IF EXISTS zcdh_uni_test;
CREATE DATABASE IF NOT EXISTS zcdh_uni_test DEFAULT CHARACTER SET utf8;
DROP DATABASE IF EXISTS my_zcdh_uni;
CREATE DATABASE IF NOT EXISTS my_zcdh_uni DEFAULT CHARACTER SET utf8;

# 修改表结构
ALTER TABLE zcdh_ent_account ADD COLUMN create_mode TINYINT;
ALTER TABLE zcdh_ent_post MODIFY COLUMN post_remark TEXT;
ALTER TABLE zcdh_ent_post ADD COLUMN data_src VARCHAR(50);
ALTER TABLE zcdh_ent_post ADD COLUMN data_url VARCHAR(500);
ALTER TABLE zcdh_ent_enterprise MODIFY COLUMN introduction TEXT;
ALTER TABLE zcdh_ent_enterprise ADD COLUMN data_src VARCHAR(50);
ALTER TABLE zcdh_ent_enterprise ADD COLUMN data_url VARCHAR(500);

UPDATE zcdh_ent_account SET create_mode=1 WHERE account REGEXP '^zcdh[0-9]+';
UPDATE zcdh_ent_account SET create_mode=0 WHERE account NOT REGEXP '^zcdh[0-9]+';

# 清空数据
DELETE FROM zcdh_ent_ability_require;
DELETE FROM zcdh_ent_account;
DELETE FROM zcdh_ent_enterprise;
DELETE FROM zcdh_ent_lbs;
DELETE FROM zcdh_ent_post;
DELETE FROM zcdh_ent_post_status;
DELETE FROM zcdh_ent_promotion;
DELETE FROM zcdh_view_ent_post;

SELECT e.ent_id, e.ent_name, e.industry, e.property, e.employ_num, e.introduction, e.ent_web, e.parea, e.address, e.data_src, e.data_url, e.create_date,
 l.lbs_id, l.longitude, l.latitude,
 a.account_id, a.create_mode
FROM zcdh_ent_enterprise e
LEFT JOIN zcdh_ent_lbs l ON l.lbs_id=e.lbs_id
LEFT JOIN zcdh_ent_account a ON a.ent_id=e.ent_id

SELECT DISTINCT p.id, p.post_aliases, p.post_name, p.post_code, p.headcounts, p.is_several, p.pjob_category, p.psalary, p.salary_type, p.tag_selected, p.post_remark, p.parea, p.post_address, p.data_src, p.data_url, p.update_date, p.publish_date,
 l.lbs_id, l.longitude, l.latitude,
 ex.ent_ability_id ex_id, ex.param_code ex_code,
 ed.ent_ability_id ed_id, ed.param_code ed_code,
 ps.ps_id status_id,
 v.id view_id,
 e.ent_name, e.data_url ent_url
FROM zcdh_ent_post p
LEFT JOIN zcdh_ent_lbs l ON l.lbs_id=p.lbs_id
LEFT JOIN zcdh_ent_ability_require ex ON ex.post_id=p.id AND ex.technology_code='-0000000000003'
LEFT JOIN zcdh_ent_ability_require ed ON ed.post_id=p.id AND ed.technology_code='-0000000000004'
LEFT JOIN zcdh_ent_post_status ps ON ps.post_id=p.id
JOIN zcdh_view_ent_post v ON v.post_id=p.id
JOIN zcdh_ent_enterprise e ON e.ent_id=p.ent_id
WHERE p.data_src IS NOT NULL
AND p.data_url IS NOT NULL