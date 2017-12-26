package com.seckill.web;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.seckill.dto.Exposer;
import com.seckill.dto.SeckillExceution;
import com.seckill.dto.SeckillResult;
import com.seckill.entity.Seckill;
import com.seckill.enums.SeckillStatEnum;
import com.seckill.exception.RepeatKillException;
import com.seckill.exception.SeckillCloseException;
import com.seckill.service.SeckillService;

@Controller
public class SeckillController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SeckillService seckillService;

	@RequestMapping(value="/list",method=RequestMethod.GET)
	public String list(Model model){
		List<Seckill> list = seckillService.getSeckillList();
		model.addAttribute("list", list);
		return "list";
	}
	
	@RequestMapping(value="/{seckillId}/detail",method=RequestMethod.GET)
	public String detail(@PathVariable("seckillId") Long seckillId, Model model){
		if(seckillId == null){
			return "forward:/seckill/list";
		}
		Seckill seckill = seckillService.getById(seckillId);
		if(seckill == null){
			return "forward:/seckill/list";
		}
		model.addAttribute("seckill", seckill);
		return "detail";
	}
	
	@RequestMapping(value="/{seckillId}/exposer",method=RequestMethod.POST
			,produces="application/json;charset=UTF-8")
	@ResponseBody
	public SeckillResult<Exposer> exposer(@PathVariable("seckillId")Long seckillId){
		SeckillResult<Exposer> result;
		try{
			Exposer exposer = seckillService.exportSeckillUrl(seckillId);
			result = new SeckillResult<Exposer>(true,exposer);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			result = new SeckillResult<Exposer>(false,e.getMessage());
		}
		return result;
	}
	
	@RequestMapping(value="/{seckillId}/{md5}/execute",
			method=RequestMethod.POST,
			produces="application/json;charset=UTF-8")
	@ResponseBody
	public SeckillResult<SeckillExceution> exceution(
			@PathVariable("seckillId")Long seckillId,
			@PathVariable("md5")String md5,
			@CookieValue(value="killPhone",required=false)Long phone){
		if(phone == null){
			return new SeckillResult<SeckillExceution>(false,"未注册");
		}
		SeckillResult<SeckillExceution> result;
		try{
			SeckillExceution seckillExcetion = seckillService.executeSeckill(seckillId, phone, md5);
			return new SeckillResult<SeckillExceution>(true,seckillExcetion);
		}catch(RepeatKillException e){
			SeckillExceution seckillExceution = new SeckillExceution(seckillId,SeckillStatEnum.REPEAT_KILL);
			return new SeckillResult<SeckillExceution>(true,seckillExceution);
		}catch(SeckillCloseException e){
			SeckillExceution seckillExceution = new SeckillExceution(seckillId,SeckillStatEnum.END);
			return new SeckillResult<SeckillExceution>(true,seckillExceution);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			SeckillExceution seckillExceution = new SeckillExceution(seckillId,SeckillStatEnum.INNER_ERROR);
			return new SeckillResult<SeckillExceution>(true,seckillExceution);
		}
	}
	
	@RequestMapping(value="/time/now",method=RequestMethod.GET)
	@ResponseBody
	public SeckillResult<Long> time(){
		Date now = new Date();
		return new SeckillResult<Long>(true,now.getTime());
	}
	
	
	
	
	
}
