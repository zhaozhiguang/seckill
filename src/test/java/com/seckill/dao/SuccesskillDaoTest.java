package com.seckill.dao;


import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.seckill.entity.Successkilled;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class SuccesskillDaoTest {

	@Resource
	private SuccesskillDao sDao;
	
	@Test
	public void testInsertSuccesskilled() {
		long id = 1000;
		long phone = 15197257004l;
		int insertCount = sDao.insertSuccesskilled(id, phone);
		System.out.println(insertCount);
	}

	@Test
	public void testQueryByIdwithSeckill() {
		long id = 1000l;
		long phone = 15197257004l;
		Successkilled su = sDao.queryByIdwithSeckill(id,phone);
		System.out.println(su);
	}

}
