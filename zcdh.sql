# 备份数据库
mysqldump -h 192.168.1.30 -u admin -p zcdh_uni_test zcdh_area zcdh_category_post zcdh_ent_ability_require zcdh_ent_account zcdh_ent_enterprise zcdh_ent_lbs zcdh_ent_post zcdh_ent_post_status zcdh_ent_promotion zcdh_industry zcdh_param zcdh_post zcdh_tag  zcdh_technology zcdh_technology_gategory zcdh_view_ent_post > zcdh_uni_test.sql.2015.7.22
mysqldump -h 192.168.1.30 -u admin -p zcdh_uni_test zcdh_ent_ability_require zcdh_ent_account zcdh_ent_enterprise zcdh_ent_lbs zcdh_ent_post zcdh_ent_post_status zcdh_ent_promotion zcdh_view_ent_post > zcdh_uni_test.sql.2015.7.22

# 创建数据库
drop database if exists zcdh_uni_test;
create database if not exists zcdh_uni_test default character set utf8;
drop database if exists my_zcdh_uni;
create database if not exists my_zcdh_uni default character set utf8;

# 修改表结构
alter table zcdh_ent_account add column create_mode tinyint;
alter table zcdh_ent_post modify column post_remark text;
alter table zcdh_ent_post add column data_src varchar(50);
alter table zcdh_ent_post add column data_url varchar(500);
alter table zcdh_ent_enterprise modify column introduction text;
alter table zcdh_ent_enterprise add column data_src varchar(50);
alter table zcdh_ent_enterprise add column data_url varchar(500);

update zcdh_ent_account set create_mode=1 where account regexp '^zcdh[0-9]{5}$';

# 清空数据
delete from zcdh_ent_ability_require;
delete from zcdh_ent_account;
delete from zcdh_ent_enterprise;
delete from zcdh_ent_lbs;
delete from zcdh_ent_post;
delete from zcdh_ent_post_status;
delete from zcdh_ent_promotion;
delete from zcdh_view_ent_post;

select e.ent_id, e.ent_name, e.industry, e.property, e.employ_num, e.introduction, e.ent_web, e.parea, e.address, e.data_src, e.data_url, e.create_date,
 l.lbs_id, l.longitude, l.latitude,
 a.account_id, a.create_mode
from zcdh_ent_enterprise e
left join zcdh_ent_lbs l on l.lbs_id=e.lbs_id
left join zcdh_ent_account a on a.ent_id=e.ent_id

select distinct p.id, p.post_aliases, p.post_name, p.post_code, p.headcounts, p.is_several, p.pjob_category, p.psalary, p.salary_type, p.tag_selected, p.post_remark, p.parea, p.post_address, p.data_src, p.data_url, p.update_date, p.publish_date,
 l.lbs_id, l.longitude, l.latitude,
 ex.ent_ability_id ex_id, ex.param_code ex_code,
 ed.ent_ability_id ed_id, ed.param_code ed_code,
 ps.ps_id status_id,
 v.id view_id,
 e.ent_name, e.data_url ent_url
from zcdh_ent_post p
left join zcdh_ent_lbs l on l.lbs_id=p.lbs_id
left join zcdh_ent_ability_require ex on ex.post_id=p.id and ex.technology_code='-0000000000003'
left join zcdh_ent_ability_require ed on ed.post_id=p.id and ed.technology_code='-0000000000004'
left join zcdh_ent_post_status ps on ps.post_id=p.id
join zcdh_view_ent_post v on v.post_id=p.id
join zcdh_ent_enterprise e on e.ent_id=p.ent_id
where p.data_src is not null
and p.data_url is not null