# 备份数据库
mysqldump -h 192.168.1.30 -u admin -p zcdh_uni_test zcdh_tag zcdh_area zcdh_param zcdh_post zcdh_category_post zcdh_technology zcdh_technology_gategory zcdh_industry zcdh_ent_post zcdh_ent_post_status zcdh_ent_promotion zcdh_ent_ability_require zcdh_view_ent_post zcdh_ent_account zcdh_ent_enterprise zcdh_ent_lbs > zcdh_uni_test.sql

# 创建数据库
DROP DATABASE IF EXISTS zcdh_uni_test;
CREATE DATABASE IF NOT EXISTS zcdh_uni_test DEFAULT CHARACTER SET utf8;

# 修改表结构
ALTER TABLE zcdh_ent_post MODIFY COLUMN post_remark TEXT;
ALTER TABLE zcdh_ent_post ADD COLUMN data_src VARCHAR(50);
ALTER TABLE zcdh_ent_post ADD COLUMN data_url VARCHAR(500);
ALTER TABLE zcdh_ent_enterprise MODIFY COLUMN introduction TEXT;
ALTER TABLE zcdh_ent_enterprise ADD COLUMN data_src VARCHAR(50);
ALTER TABLE zcdh_ent_enterprise ADD COLUMN data_url VARCHAR(500);

# 清空数据
DELETE FROM zcdh_ent_ability_require;
DELETE FROM zcdh_ent_account;
DELETE FROM zcdh_ent_enterprise;
DELETE FROM zcdh_ent_lbs;
DELETE FROM zcdh_ent_post;
DELETE FROM zcdh_ent_post_status;
DELETE FROM zcdh_ent_promotion;
DELETE FROM zcdh_view_ent_post;

岗位：
SELECT id FROM zcdh_ent_post;
SELECT id, publish_date, update_date, ent_id, post_aliases, post_name, post_code, pjob_category, headcounts, is_several, psalary, salary_type, tag_selected, post_address, parea, lbs_id, post_remark, data_src, data_url FROM zcdh_ent_post WHERE id=?;

企业：
SELECT ent_id FROM zcdh_ent_enterprise;
SELECT ent_id, create_date, ent_name, industry, property, employ_num, ent_web, address, parea, lbs_id, introduction, data_src, data_url FROM zcdh_ent_enterprise WHERE ent_id=?;

SELECT a.account_id, a.account, e.create_date, e.ent_name, e.industry, e.property, e.employ_num, e.ent_web, e.address, e.parea, e.lbs_id, e.introduction, e.data_src, e.data_url, l.latitude, l.longitude
FROM zcdh_ent_account a
JOIN zcdh_ent_enterprise e ON a.ent_id=e.ent_id
JOIN zcdh_ent_lbs l ON e.lbs_id=l.lbs_id
WHERE e.ent_name IS NOT NULL
AND e.data_src IS NOT NULL;
