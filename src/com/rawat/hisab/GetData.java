package com.rawat.hisab;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;

public class GetData {

	private Cursor cur;
	private int i =1;
	private String sms = "";
	private ConfigDate CfgDate;
	private ContentResolver cR;
	private float icicBank=0;
	private float hdfcCredit=0;
	private float hdfcSmt=0;
	private float city=0;
	private float iciciDB=0;
	private float sbi=0;
	private float amex=0;
	public static int count=0;
	public int cn=0;
	private List<Float> amt;
	private List<String> cardName;
	private List<Float> percent;
	private List<Integer> cl;
	private float total=0 ;
	public GetData(ConfigDate Cfg, ContentResolver c)
	{
		this.CfgDate=Cfg;
		this.cR = c;
		amt = new ArrayList<Float>();
		cardName=new ArrayList<String>();
		percent= new ArrayList<Float>();
		cl= new ArrayList<Integer>();
	}
	public void setTotal(String t)
	{

	}
	public int getCount()
	{
		count=0;
		if(icicBank > 0)
		{
			amt.add(icicBank);
			cardName.add("ICICI CREDIT");
			percent.add(icicBank/total);
			cl.add(Color.RED);
			count++;
		}
		if(hdfcCredit > 0 )
		{
			amt.add(hdfcCredit+hdfcSmt);
			cardName.add("HDFC");
			percent.add(hdfcCredit/total);
			cl.add(Color.BLUE);
			count++;
		}
		if(city>0)
		{
			amt.add(city);
			cardName.add("CITI");
			percent.add(city/total);
			cl.add(Color.GREEN);
			count++;
		}
		if(iciciDB>0)
		{
			amt.add(iciciDB);
			cardName.add("ICICI Debit");
			percent.add(iciciDB/total);
			cl.add(Color.YELLOW);
			count++;
		}
		if(sbi>0)
		{
			amt.add(sbi);
			cardName.add("SBI Debit");
			percent.add(sbi/total);
			cl.add(Color.LTGRAY);
			count++;
		}
		if(amex>0)
		{
			amt.add(amex);
			cardName.add("Amex");
			percent.add(amex/total);
			cl.add(Color.CYAN);
			count++;
		}
		return count;
	}

	public float getTotal()
	{
		
		String str ="content://sms/";
		int mnt = CfgDate.getEndMonth();
		int year = CfgDate.getEndYear();
		Uri uriSMSURI = Uri.parse("content://sms/");
		cur = cR.query(uriSMSURI, null, null, null,null);
		Calendar cl = Calendar.getInstance();
		while (cur.moveToNext()) {
			long ms= Long.parseLong(cur.getString(4));
			Date dateFromSms = new Date(ms);
			cl.setTimeInMillis(ms);

			if(cl.get(Calendar.MONTH)+1==mnt && cl.get(Calendar.YEAR) == year )
			{
				String tmp1="";
				tmp1=cur.getString(12);
				if(tmp1.contains("Tranx of INR"))
				{
					iciciCredit(tmp1);
				}	
				else if(tmp1.contains("HDFCBank CREDIT Card"))
				{
					hdfcBankCredit(tmp1);
				}
				else if(tmp1.contains("HDFC Bank credit card to pay your SMARTPAY"))
				{
					hdfcBankSmt(tmp1);
				}
				else if(tmp1.contains("Citibank ATM"))
				{
					cityAtm(tmp1);
				}
				else if(tmp1.contains("Dear Customer, Your Ac") && tmp1.contains("debited"))
				{
					iciciDebit(tmp1);
				}
				else if(tmp1.contains("SBI Debit Card") || tmp1.contains("Debited INR") || tmp1.contains("You have made a Debit Card purchase"))
				{
					sbiDebit(tmp1);
				}
				else if(tmp1.contains("American Express Card"))
				{
					Amex(tmp1);
				}
			}
		}
		//sms=sms+"Total "+ total +"";
		total=icicBank+hdfcCredit+city+iciciDB+sbi;
		cn=getCount();
		
		return total;
	}
	public void iciciCredit(String tmp1)
	{
		String tmp2="";
		String[] separated = tmp1.split("Tranx of INR");
		tmp2=separated[1];
		String[] separated1 = tmp2.split("using");
		tmp2=separated1[0];
		tmp2=tmp2.replaceAll(",", "");
		icicBank=Float.valueOf((tmp2))+icicBank;
	}
	public void hdfcBankCredit(String tmp1)
	{
		String tmp2="";
		String[] separated = tmp1.split("Rs.");
		tmp2=separated[1];
		String[] separated1 = tmp2.split("was");
		tmp1=separated1[0];
		hdfcCredit=Float.valueOf((tmp1))+hdfcCredit;
	}
	public void hdfcBankSmt(String tmp1)
	{
		String tmp2="";
		String[] separated = tmp1.split("Rs.");
		tmp2=separated[1];
		hdfcCredit=Float.valueOf((tmp2))+hdfcCredit;
	}
	public void cityAtm(String tmp1)
	{
		String tmp2="";
		String[] separated = tmp1.split("Rs.");
		tmp2=separated[1];
		String[] separated1 = tmp2.split("was");
		tmp1=separated1[0];
		city=Float.valueOf((tmp1))+city;
	}
	public void iciciDebit(String tmp1)
	{
		String tmp2="";
		String[] separated = tmp1.split("with");
		tmp2=separated[1];
		String[] separated1 = tmp2.split("INR");
		tmp2=separated1[1];
		String[] separated2 = tmp2.split("on");
		tmp2=separated2[0];
		tmp2=tmp2.replaceAll(",", "");
		iciciDB=Float.valueOf((tmp2))+iciciDB;
		//Log.w("ICICI Bank", tmp2+"");
	}
	public void sbiDebit(String tmp1)
	{
		if(tmp1.contains("withdrawing"))
		{
			String tmp2="";
			String[] separated = tmp1.split("Rs");
			tmp2=separated[1];
			String[] separated1 = tmp2.split("from");
			tmp2=separated1[0];
			sbi=Float.valueOf((tmp2))+sbi;
		}
		else if(tmp1.contains("It would be our"))
		{
			String tmp2="";
			String[] separated = tmp1.split("Rs");
			tmp2=separated[1];
			sbi=Float.valueOf((tmp2))+sbi;
		}
		else if(tmp1.contains("Thank you for using your SBI Debit Card"))
		{
			String tmp2="";
			String[] separated = tmp1.split("Rs");
			tmp2=separated[1];
			String[] separated1 = tmp2.split("on");
			tmp2=separated1[0];
			sbi=Float.valueOf((tmp2))+sbi;
		}
		else if (tmp1.contains("Debited INR"))
		{
			String tmp2="";
			String[] separated = tmp1.split("INR");
			tmp2=separated[1];
			String[] separated1 = tmp2.split("on");
			tmp2=separated1[0];
			tmp2=tmp2.replaceAll(",", "");
			sbi=Float.valueOf((tmp2))+sbi;
		}
		else if (tmp1.contains("purchase"))
		{
			String tmp2="";
			String[] separated = tmp1.split("INR");
			tmp2=separated[1];
			String[] separated1 = tmp2.split("on");
			tmp2=separated1[0];
			tmp2=tmp2.replaceAll(",", "");
			sbi=Float.valueOf((tmp2))+sbi;
		}
	}
	public void Amex(String tmp1)
	{
		String tmp2="";
		String[] separated = tmp1.split("INR");
		tmp2=separated[1];
		String[] separated1 = tmp2.split("has");
		tmp2=separated1[0];
		tmp2=tmp2.replaceAll(",", "");
		amex=Float.valueOf((tmp2))+amex;
	}
	public List<Float> getCardAmt()
	{
		return amt;
	}
	public List<String> getCardName()
	{
		return cardName;
	}
	public List<Float> getPercent()
	{
		return percent;
	}
	public List<Integer> getColor()
	{
		return cl;
	}
}
