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

SELECT p.param_name, p.param_code, p.param_value, p.param_category_code, t.technical_code, t.techonlogy_gategory_code, t.match_type, g.percent FROM zcdh_param p LEFT JOIN zcdh_technology t ON p.param_category_code=t.param_category_code LEFT JOIN zcdh_technology_gategory g ON t.techonlogy_gategory_code=g.technology_gategory_code WHERE (p.is_delete=1 OR p.is_delete IS NULL) AND (t.is_delete=1 OR t.is_delete IS NULL) AND (g.is_delete=1 OR g.is_delete IS NULL) AND p.param_category_code IN('004','005')
SELECT * FROM zcdh_ent_ability_require WHERE post_id=409074

INSERT INTO zcdh_ent_ability_require(ent_ability_id, ent_id, grade, match_type, param_code, post_code, post_id, technology_cate_code, technology_code, total_point, weight_point) VALUES(90000001, 50303, 7, 1, '005.009', '0000239', 409074, '001', '-0000000000003', 5, 0.833)
INSERT INTO zcdh_ent_ability_require(ent_ability_id, ent_id, grade, match_type, param_code, post_code, post_id, technology_cate_code, technology_code, total_point, weight_point) VALUES(90000000, 50303, 6, 1, '004.006', '0000239', 409074, '001', '-0000000000004', 5, 0.833)

DELETE FROM zcdh_ent_ability_require WHERE ent_ability_id IN(90000000, 90000001)

SELECT a.account_id, a.account, a.create_mode, a.create_date account_date,
  e.create_date, e.ent_id, e.ent_name, e.industry, e.property, e.employ_num, e.ent_web, e.address, e.parea, e.lbs_id, e.introduction, e.data_src, e.data_url,
  l.latitude, l.longitude
FROM zcdh_ent_account a 
LEFT JOIN zcdh_ent_enterprise e ON a.ent_id=e.ent_id 
LEFT JOIN zcdh_ent_lbs l ON e.lbs_id=l.lbs_id

SELECT p.id, p.publish_date, p.update_date, p.ent_id, p.post_aliases, p.post_name, p.post_code, p.pjob_category, p.headcounts, p.is_several, p.psalary, p.salary_type, p.tag_selected, p.post_address, p.parea, p.lbs_id, p.post_remark, p.data_src, p.data_url,
  e.ent_name, e.data_url ent_url,
  ex.ent_ability_id ex_id, ex.param_code ex_code, ed.ent_ability_id ed_id, ed.param_code ed_code,
  l.latitude, l.longitude
FROM zcdh_ent_post p
LEFT JOIN zcdh_ent_enterprise e ON p.ent_id=e.ent_id
LEFT JOIN zcdh_ent_lbs l ON p.lbs_id=l.lbs_id
LEFT JOIN zcdh_ent_ability_require ex ON p.id=ex.post_id AND ex.technology_code='-0000000000003'
LEFT JOIN zcdh_ent_ability_require ed ON p.id=ed.post_id AND ed.technology_code='-0000000000004'
WHERE p.id=409074

