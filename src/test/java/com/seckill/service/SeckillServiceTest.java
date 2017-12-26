package com.seckill.service;


import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.seckill.dto.Exposer;
import com.seckill.dto.SeckillExceution;
import com.seckill.entity.Seckill;
import com.seckill.exception.RepeatKillException;
import com.seckill.exception.SeckillCloseException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class SeckillServiceTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SeckillService seckillService;
	
	@Test
	public void testGetSeckillList() {
		List<Seckill> list = seckillService.getSeckillList();
		logger.info("list={}", list);
	}

	@Test
	public void testGetById() {
		long id = 1000;
		Seckill seckill = seckillService.getById(id);
		logger.info("seckill={}", seckill);
	}

	@Test
	public void testExportSeckillUrl() {
		long id = 1000;
		Exposer exposer = seckillService.exportSeckillUrl(id);
		if(exposer.isExposed()){
			logger.info("exposer={}", exposer);
			long phone = 15197257004l;
			String md5 = exposer.getMd5();
			try{
				SeckillExceution exceution = seckillService.executeSeckill(id, phone, md5);
				logger.info("result={}", exceution);
			}catch(RepeatKillException e){
				logger.info(e.getMessage());
			}catch(SeckillCloseException e){
				logger.info(e.getMessage());
			}
		}else{
			logger.warn("exposer={}", exposer);
		}
		
	}

	/*@Test
	public void testExecuteSeckill() {
		long id = 1000;
		long phone = 15197257004l;
		String md5 = "dfsdfdfdsgf5d4fdsfds";
		try{
			SeckillExceution exceution = seckillService.executeSeckill(id, phone, md5);
			logger.info("result={}", exceution);
		}catch(RepeatKillException e){
			logger.info(e.getMessage());
		}catch(SeckillCloseException e){
			logger.info(e.getMessage());
		}
		
	}*/

}
