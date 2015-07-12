mysqldump -h 192.168.1.30 -u admin -p zcdh_uni_test zcdh_tag zcdh_area zcdh_param zcdh_post zcdh_category_post zcdh_technology zcdh_technology_gategory zcdh_industry zcdh_ent_post zcdh_ent_post_status zcdh_ent_promotion zcdh_ent_ability_require zcdh_view_ent_post zcdh_ent_account zcdh_ent_enterprise zcdh_ent_lbs > zcdh_uni_test.sql

DROP DATABASE IF EXISTS zcdh_uni_test;
CREATE DATABASE IF NOT EXISTS zcdh_uni_test DEFAULT CHARACTER SET utf8;

ALTER TABLE zcdh_ent_post MODIFY COLUMN post_remark TEXT;
ALTER TABLE zcdh_ent_post ADD COLUMN data_src VARCHAR(50);
ALTER TABLE zcdh_ent_post ADD COLUMN data_url VARCHAR(500);
ALTER TABLE zcdh_ent_enterprise MODIFY COLUMN introduction TEXT;
ALTER TABLE zcdh_ent_enterprise ADD COLUMN data_src VARCHAR(50);
ALTER TABLE zcdh_ent_enterprise ADD COLUMN data_url VARCHAR(500);

DELETE FROM zcdh_ent_ability_require;
DELETE FROM zcdh_ent_account;
DELETE FROM zcdh_ent_enterprise;
DELETE FROM zcdh_ent_lbs;
DELETE FROM zcdh_ent_post;
DELETE FROM zcdh_ent_post_status;
DELETE FROM zcdh_ent_promotion;
DELETE FROM zcdh_view_ent_post;
