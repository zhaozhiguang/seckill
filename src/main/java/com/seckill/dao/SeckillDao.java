package com.seckill.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.seckill.entity.Seckill;

public interface SeckillDao {

	int reduceNumber(@Param("seckillid")long seckillid, @Param("killTime")Date killTime);
	
	Seckill queryById(long seckillid);
	
	/**
	 *    @Param能够让mybatis识别参数
	 * @param offet
	 * @param limit
	 * @return
	 */
	List<Seckill> queryAll(@Param("offet")int offet, @Param("limit")int limit);
	
	
	void killByProcedure(Map<String, Object> paramMap);
}
