package com.rawat.hisab.DB;

public class CardDetails {
	
	private long msgtimeStamp;
	private int id;
	private double amt;
	private String mnt;
	private int yr;
	private String at;
	
	public long getMsgtimeStamp()
	{
		return msgtimeStamp;
	}
	
	public void setMsgtimeStamp(long msgtimeStamp)
	{
		this.msgtimeStamp=msgtimeStamp;
	}
	
	public int getID()
	{
		return id;
	}
	
	public void setID(int id)
	{
		this.id=id;
	}
	
	public double getAmt()
	{
		return amt;
	}
	
	public void setAmt(double amt)
	{
		this.amt=amt;
	}
	public void setAt(String at)
	{
		this.at=at;
	}
	public String getAt()
	{
		return at;
	}
	public String getMnt()
	{
		return mnt;
	}
	
	public void setMnt(String mnt)
	{
		this.mnt=mnt;
	}
	
	public int getYr()
	{
		return yr;
	}
	
	public void setYr(int yr)
	{
		this.yr=yr;
	}

	
}
