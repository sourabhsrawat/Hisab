package com.rawat.hisab;

import android.app.Application;
import android.content.res.Configuration;

public class ConfigDate extends Application {

	private static ConfigDate singleton;

	private int day_St;
	private int month_St;
	private int year_St;
	private int day_Ed;
	private int month_Ed;
	private int year_Ed;
	private String mnt;

	public ConfigDate getInstance(){
		return singleton;
	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	public void onCreate() {
		super.onCreate(); 
		singleton = this;
	}
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	public int getStartDay()
	{
		return day_St;
	}
	public void setStartDay(int startDay)
	{
		this.day_St=startDay;
	}
	public int getStartMonth()
	{
		return month_St;
	}
	public void setStartMonth(int startMt)
	{
		this.month_St=startMt;
	}
	public int getStartYear()
	{
		return year_St;
	}
	public void setStartYear(int startYr)
	{
		this.year_St=startYr;
	}
	public int getEndDay()
	{
		return day_Ed;
	}
	public void setEndDay(int endDay)
	{
		this.day_Ed=endDay;
	}
	public int getEndMonth()
	{
		return month_Ed;
	}
	public void setEndMonth(int endMt)
	{
		this.month_Ed=endMt;
	}
	public int getEndYear()
	{
		return year_Ed;
	}
	public void setEndYear(int endYr)
	{
		this.year_Ed=endYr;
	}
	public String getMonthInWrd()
	{
		switch (month_Ed){
		case 1 :
			return "JANUARY";
		case 2 :
			return "FEBRUARY";
		case 3 :
			return "MARCH";
		case 4 :
			return "APRIL";
		case 5 :
			return "MAY";
		case 6 :
			return "JUNE";
		case 7 :
			return "JULY";
		case 8 :
			return "AUGUST";
		case 9 :
			return "SEPTEMBER";
		case 10 :
			return "OCTOBER ";
		case 11 :
			return "NOVEMBER ";
		case 12 :
			return "DECEMBER ";
		default :
			return "ENTER MONTH";

		}
	}
}
