create database seckill;
use seckill;
create table seckill(
	`seckill_id` int auto_increment COMMENT '商品id',
	`name` varchar(120) not null COMMENT '商品名称',
	`number` int not null COMMENT '库存数量',
	`create_time` timestamp not null default CURRENT_TIMESTAMP COMMENT '创建时间',
	`start_time` timestamp not null COMMENT '秒杀开始时间',
	`end_time` timestamp not null COMMENT '秒杀结束时间',
	primary key (`seckill_id`),
	key idx_start_time(`start_time`),
	key idx_end_time(`end_time`),
	key idx_create_time(`create_time`)
)ENGINE=InnoDB AUTO_INCREMENT=1000 COMMENT='秒杀库存表';
-- 初始化数据
insert into 
	seckill(`name`,`number`,start_time,end_time)
values
	('1000元秒杀iphone6s',100,'2015-11-11 00:00:00','2015-11-12 00:00:00'),
	('500元秒杀ipad2',200,'2015-11-11 00:00:00','2015-11-12 00:00:00'),
	('300元秒杀小米4',300,'2015-11-11 00:00:00','2015-11-12 00:00:00'),
	('200元秒杀红米note',400,'2015-11-11 00:00:00','2015-11-12 00:00:00');
	
create table success_seckilled(
	seckill_id int COMMENT '秒杀商品id',
	`user_phone` bigint not null COMMENT '用户手机号',
	`state` tinyint not null COMMENT '状态标识', 
	`create_time` timestamp not null COMMENT '创建时间',
	primary key (seckill_id,user_phone),
	key (create_time)
)ENGINE=InnoDB COMMENT='秒杀成功明细表';

-- 秒杀执行存储过程
DELIMITER $$
CREATE PROCEDURE `seckill`.`excute_seckill`
	(in v_seckill_id bigint, in v_phone bigint,
	in v_kill_time timestamp, out r_result int)
	BEGIN
		DECLARE insert_count int default 0;
		START TRANSACTION;
		insert ignore into success_seckilled
			(seckill_id,user_phone,create_time)
			values (v_seckill_id,v_phone,v_kill_time);
		select row_count() into insert_count;	
		IF(insert_count = 0) THEN
			ROLLBACK;
			set r_result = -1;
		ELSEIF(insert_count < 0) THEN
			ROLLBACK;
			set r_result = -2;
		ELSE 
			update seckill set number = number - 1
				where seckill_id = v_seckill_id
				and end_time > v_kill_time
				and start_time < v_kill_time
				and number > 0;
			select row_count() into insert_count;
			IF (insert_count = 0) THEN
				ROLLBACK;
				set r_result = 0;
			ELSEIF (insert_count < 0) THEN
				ROLLBACK;
				set r_result = -2;
			ELSE 
				COMMIT;
				set r_result = 1;
			END IF;
		END IF;
	END 
$$
DELIMITER ;

set @r_result = -3;
-- 执行存储过程
call excute_seckill(1003,15197257004,now(),@r_result);
--获取结果
select @r_result;

