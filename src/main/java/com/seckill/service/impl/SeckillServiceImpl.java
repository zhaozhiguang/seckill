package com.seckill.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import com.seckill.dao.SeckillDao;
import com.seckill.dao.SuccesskillDao;
import com.seckill.dao.cache.RedisDao;
import com.seckill.dto.Exposer;
import com.seckill.dto.SeckillExceution;
import com.seckill.entity.Seckill;
import com.seckill.entity.Successkilled;
import com.seckill.enums.SeckillStatEnum;
import com.seckill.exception.RepeatKillException;
import com.seckill.exception.SeckillCloseException;
import com.seckill.exception.SeckillException;
import com.seckill.service.SeckillService;

@Service
public class SeckillServiceImpl implements SeckillService {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SeckillDao seckillDao;
	
	@Autowired
	private SuccesskillDao successkillDao;
	
	@Autowired
	private RedisDao redisDao;
	
	private String salt = "sdfdfdf32f12ds00f&fsdf$&*";

	@Override
	public List<Seckill> getSeckillList() {
		return seckillDao.queryAll(0, 4);
	}

	@Override
	public Seckill getById(long seckillId) {
		return seckillDao.queryById(seckillId);
	}

	@Override
	public Exposer exportSeckillUrl(long seckillId) {
		//优化点：缓存优化,超时的基础上维护一致性
		//1、访问redis
		Seckill seckill = redisDao.getSeckill(seckillId);
		if(seckill == null){
			//2、访问数据库
			seckill = seckillDao.queryById(seckillId);
			if(seckill == null){
				return new Exposer(false, seckillId);
			}else{
				//3、放入redis
				redisDao.putSeckill(seckill);
			}
		}
		Date startTime = seckill.getStartTime();
		Date endTime = seckill.getEndTime();
		Date nowTime = new Date();
		if(nowTime.getTime() < startTime.getTime() ||
				nowTime.getTime() > endTime.getTime()){
			return new Exposer(false, seckillId, nowTime.getTime(),
					startTime.getTime(), endTime.getTime());
		} 
		String md5 = getMD5(seckillId);
		return new Exposer(true, md5, seckillId);
	}
	
	private String getMD5(long seckillId){
		String base = seckillId + "/" + salt;
		String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
		return md5;
	}

	@Override
	@Transactional
	/**
	 * 使用注解控制事务的优点：
	 * 1、开发团队达成一致约定，明确标注事务方法的编程风格
	 * 2、保证事务方法的执行时间尽可能短
	 * 3、不是所有方法都需要事务
	 */
	public SeckillExceution executeSeckill(long seckillId, long userPhone, String md5)
			throws SeckillException, RepeatKillException, SeckillCloseException {
		if(md5 == null || !md5.equals(getMD5(seckillId))){
			throw new SeckillException("seckill data rewrite");
		}
		Date nowTime = new Date();
		try{
			int insertCount = successkillDao.insertSuccesskilled(seckillId, userPhone);
			if(insertCount <= 0){
				throw new RepeatKillException("seckill repeated");
			}else{
				int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
				if(updateCount <= 0){
					throw new SeckillCloseException("seckill is closed");
				}else{
					Successkilled successkilled = successkillDao.queryByIdwithSeckill(seckillId, userPhone);
					return new SeckillExceution(seckillId,SeckillStatEnum.SUCCESS, successkilled);
				}	
			}
		}catch (SeckillCloseException e1) {
            throw e1;
        } catch (RepeatKillException e2) {
            throw e2;
        } catch(Exception e){
			logger.error(e.getMessage(),e);
			throw new SeckillException("seckill inner error" +e.getMessage());
		}
	}

	@Override
	public SeckillExceution executeSeckillProcedure(long seckillId, long userPhone, String md5){
		if(md5 == null || !md5.equals(getMD5(seckillId))){
			return new SeckillExceution(seckillId, SeckillStatEnum.DATA_REWRITE);
		}
		Date killTime = new Date();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("seckillId", seckillId);
		paramMap.put("phone", userPhone);
		paramMap.put("killTime", killTime);
		paramMap.put("result", null);
		try {
			seckillDao.killByProcedure(paramMap);
			//获取result
			int result = MapUtils.getInteger(paramMap, "result", -2);
			if(result == 1){
				Successkilled successkilled = successkillDao
							.queryByIdwithSeckill(seckillId, userPhone);
				return new SeckillExceution(seckillId, SeckillStatEnum.SUCCESS, successkilled);
			}else{
				return new SeckillExceution(seckillId, SeckillStatEnum.stateOf(result));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new SeckillExceution(seckillId, SeckillStatEnum.INNER_ERROR);
		}
		
		
	}

}
