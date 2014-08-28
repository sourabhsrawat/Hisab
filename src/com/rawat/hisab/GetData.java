package com.rawat.hisab;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;

public class GetData {

	private Cursor cur;
	private ConfigDate CfgDate;
	private ContentResolver cR;
	private float icicBank=0;
	private float hdfcCredit=0;
	private float city=0;
	private float iciciDB=0;
	private float sbi=0;
	private float amex=0;
	private float hdfcDebit=0;
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
			setParm(icicBank,"ICICI CREDIT",Color.RED);
			count++;
		}
		if(hdfcCredit > 0 )
		{
			setParm(hdfcCredit,"HDFC",Color.BLUE);
			count++;
		}
		if(city>0)
		{
			setParm(city,"CITI",Color.GREEN);
			count++;
		}
		if(iciciDB>0)
		{
			setParm(iciciDB,"ICICI Debit",Color.BLACK);
			count++;
		}
		if(sbi>0)
		{
			setParm(sbi,"SBI Debit",Color.LTGRAY);
			count++;
		}
		if(amex>0)
		{
			setParm(amex,"Amex",Color.CYAN);
			count++;
		}
		if(hdfcDebit>0)
		{
			setParm(hdfcDebit,"HDFC Debit",Color.MAGENTA);
			count++;
		}
		
		return count;
	}
	private void setParm(float cardAmt, String cName, int col)
	{
		amt.add(cardAmt);
		cardName.add(cName);
		percent.add(cardAmt/total);
		cl.add(col);
	}

	public float getTotal()
	{
		int mnt = CfgDate.getEndMonth();
		int year = CfgDate.getEndYear();
		Uri uriSMSURI = Uri.parse("content://sms/");
		cur = cR.query(uriSMSURI, null, null, null,null);
		Calendar cl = Calendar.getInstance();
		while (cur.moveToNext()) {
			long ms= Long.parseLong(cur.getString(4));

			cl.setTimeInMillis(ms);

			if(cl.get(Calendar.MONTH)+1==mnt && cl.get(Calendar.YEAR) == year )
			{
				String msg="";
				msg=cur.getString(12);
				//Log.w("msg", msg);
				if(msg.contains("Tranx of INR"))
				{
					iciciCredit(msg);	
				}	
				else if(msg.contains("HDFCBank CREDIT Card"))
				{
					hdfcBankCredit(msg);
				}
				else if(msg.contains("HDFC Bank credit card to pay your SMARTPAY"))
				{
					hdfcBankSmt(msg);
				}
				else if(msg.contains("Citibank ATM"))
				{
					cityAtm(msg);
				}
				else if(msg.contains("Dear Customer, Your Ac") && msg.contains("debited"))
				{
					iciciDebit(msg);
				}
				else if(msg.contains("SBI Debit Card") || msg.contains("Debited INR"))
				{
					sbiDebit(msg);
				}
				else if(msg.contains("Dear Customer, You have made a Debit Card purchase") || msg.contains("WDL*"))
				{
					iciciDebit(msg);
				}
				else if(msg.contains("State Bank Internet Banking"))
				{
					sbiDebit(msg);
				}
				else if(msg.contains("HDFC Bank DEBIT"))
				{
					hdfcDebit(msg);
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
	public void hdfcDebit(String tmp1)
	{
		String tmp2="";
		String[] separated = tmp1.split("Rs.");
		tmp2=separated[1];
		String[] separated1 = tmp2.split("towards");
		tmp1=separated1[0];
		hdfcDebit=Float.valueOf((tmp1))+hdfcDebit;
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
		if(tmp1.contains("Debit Card purchase"))
		{
			String[] separatednew = tmp1.split("INR");
			tmp2=separatednew[1];
			String[] separatednew1 = tmp2.split("on");
			tmp1=separatednew1[0];
			tmp1=tmp1.replaceAll(",", "");
			iciciDB=Float.valueOf((tmp1))+iciciDB;
		}
		else if(tmp1.contains("Dear Customer, Your Ac") && tmp1.contains("debited"))
		{
			String[] separated = tmp1.split("with");
			tmp2=separated[1];
			String[] separated1 = tmp2.split("INR");
			tmp2=separated1[1];
			String[] separated2 = tmp2.split("on");
			tmp2=separated2[0];
			tmp2=tmp2.replaceAll(",", "");
			iciciDB=Float.valueOf((tmp2))+iciciDB;
		}
		else if (tmp1.contains("WDL"))
		{
			if(tmp1.contains("ATM"))
			{
				String[] separated = tmp1.split("INR");
				tmp2=separated[1];
				String[] separated1 = tmp2.split("ATM");
				tmp2=separated1[0];
				tmp2=tmp2.replaceAll(",", "");
				iciciDB=Float.valueOf((tmp2))+iciciDB;
			}
			if(tmp1.contains("NFS"))
			{
				String[] separated = tmp1.split("INR");
				tmp2=separated[1];
				String[] separated1 = tmp2.split("NFS");
				tmp2=separated1[0];
				tmp2=tmp2.replaceAll(",", "");
				iciciDB=Float.valueOf((tmp2))+iciciDB;
			}
		}
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
		else if (tmp1.contains("State Bank Internet Banking"))
		{
			String tmp2="";
			String[] separated = tmp1.split("Rs.");
			tmp2=separated[1];
			String[] separated1 = tmp2.split("on");
			tmp2=separated1[0];
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
