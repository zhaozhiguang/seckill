package com.seckill.entity;

import java.util.Date;

public class Successkilled {

	private long seckillid;
	
	private long userphone;
	
	private short state;
	
	private Date createTime;
	
	private Seckill seckill;

	public long getSeckillid() {
		return seckillid;
	}

	public void setSeckillid(long seckillid) {
		this.seckillid = seckillid;
	}

	public long getUserphone() {
		return userphone;
	}

	public void setUserphone(long userphone) {
		this.userphone = userphone;
	}

	public short getState() {
		return state;
	}

	public void setState(short state) {
		this.state = state;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	public Seckill getSeckill() {
		return seckill;
	}

	public void setSeckill(Seckill seckill) {
		this.seckill = seckill;
	}

	@Override
	public String toString() {
		return "Successkilled [seckillid=" + seckillid + ", userphone=" + userphone + ", state=" + state
				+ ", createTime=" + createTime + "]";
	}
	
	
}
