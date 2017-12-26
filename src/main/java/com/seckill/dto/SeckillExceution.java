package com.seckill.dto;

import com.seckill.entity.Successkilled;
import com.seckill.enums.SeckillStatEnum;

public class SeckillExceution {

	private long seckillId;
	
	private int state;
	
	private String stateInfo;
	
	private Successkilled successkilled;

	public SeckillExceution(long seckillId, SeckillStatEnum stat) {
		this.seckillId = seckillId;
		this.state = stat.getState();
		this.stateInfo = stat.getStateInfo();
	}

	public SeckillExceution(long seckillId, SeckillStatEnum stat, Successkilled successkilled) {
		this.seckillId = seckillId;
		this.state = stat.getState();
		this.stateInfo = stat.getStateInfo();
		this.successkilled = successkilled;
	}

	public long getSeckillId() {
		return seckillId;
	}

	public void setSeckillId(long seckillId) {
		this.seckillId = seckillId;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getStateInfo() {
		return stateInfo;
	}

	public void setStateInfo(String stateInfo) {
		this.stateInfo = stateInfo;
	}

	public Successkilled getSuccesskilled() {
		return successkilled;
	}

	public void setSuccesskilled(Successkilled successkilled) {
		this.successkilled = successkilled;
	}

	@Override
	public String toString() {
		return "SeckillExceution [seckillId=" + seckillId + ", state=" + state + ", stateInfo=" + stateInfo
				+ ", successkilled=" + successkilled + "]";
	}
	
}
