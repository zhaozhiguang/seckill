package com.seckill.dao;

import org.apache.ibatis.annotations.Param;

import com.seckill.entity.Successkilled;

public interface SuccesskillDao {

	int insertSuccesskilled(@Param("seckillid")long seckillid, @Param("userphone")long userphone);
	
	Successkilled queryByIdwithSeckill(@Param("seckillid")long seckillid, @Param("userphone")long userphone);
}
