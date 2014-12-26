package com.rawat.hisab.DB;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.rawat.hisab.CardIdentifier;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.Telephony;
import android.util.Log;

@SuppressLint("InlinedApi") public class SMSScanner {

	List<CardDetails> cd;
	CardDetails cdObj;
	String mntWrd;
	int mnt;
	int yr;

	Context ct;
	public SMSScanner(Context context)
	{
		ct=context;
	}
	public List<CardDetails> getSMS() 
	{
		try
		{
			Log.w("Database", "Get SMS");
			ContentResolver cR = ct.getContentResolver();
			Cursor cur = cR.query(Telephony.Sms.Inbox.CONTENT_URI, null, null, null,null);
			Calendar cl = Calendar.getInstance();
			cd = new ArrayList<CardDetails>();

			String msg="";
			while (cur.moveToNext()) {
				long ms= Long.parseLong(cur.getString(cur.getColumnIndexOrThrow("date")));
				cdObj = new CardDetails();
				cl.setTimeInMillis(ms);
				mnt=cl.get(Calendar.MONTH)+1;
				yr=cl.get(Calendar.YEAR);
				mntWrd=getMonthInWrd(mnt);
				msg=cur.getString(cur.getColumnIndexOrThrow("body"));
				readSMS(msg,ms);
			}
			cur.close();
		}
		catch(Exception e)
		{
			Log.w("Error in fetching ", e.getMessage());
		}
		return cd;
	}
	public List<CardDetails> newSMS(String msg,long ms) 
	{
		try{
			cd = new ArrayList<CardDetails>();
			cdObj = new CardDetails();
			Calendar cl = Calendar.getInstance();
			mnt=cl.get(Calendar.MONTH)+1;
			yr=cl.get(Calendar.YEAR);
			mntWrd=getMonthInWrd(mnt);
			readSMS(msg,ms);
		}
		catch(Exception e)
		{
			Log.w("Error in fetching ", e.getMessage());
		}
		return cd;
	}

	public void readSMS(String msg,long ms) throws Exception 
	{
		if(msg.contains(CardIdentifier.iciciCredit_Check))
		{
			cdObj.setID(01);
			cdObj.setMsgtimeStamp(ms);
			cdObj.setAmt(getCreditAmt(CardIdentifier.iciciCredit_Split,msg));
			cdObj.setAt(getAt(msg,"at "," on"));
			cdObj.setMnt(mntWrd);
			cdObj.setYr(yr);
			cd.add(cdObj);
		}	
		else if(msg.contains(CardIdentifier.hdfcCredit_Check))
		{
			cdObj.setID(02);
			cdObj.setMsgtimeStamp(ms);
			cdObj.setAmt(getCreditAmt(CardIdentifier.hdfcCredit_Split,msg));
			cdObj.setAt(getAt(msg,"at","\\."));
			cdObj.setMnt(mntWrd);
			cdObj.setYr(yr);
			cd.add(cdObj);

		}
		else if(msg.contains(CardIdentifier.hdfcCreditSMT_Check))
		{
			//hdfcCredit=getCreditAmt(CardComponent.hdfcCreditSMT_Split,msg)+hdfcCredit;
			String tmp2="";
			String[] separated = msg.split("Rs.");
			tmp2=separated[1];
			Double hdfcCredit = Double.valueOf((tmp2));
			cdObj.setID(02);
			cdObj.setMsgtimeStamp(ms);
			cdObj.setAmt(hdfcCredit);
			cdObj.setAt(getAt(msg,"SMARTPAY","for"));
			cdObj.setMnt(mntWrd);
			cdObj.setYr(yr);
			cd.add(cdObj);
		}
		else if(msg.contains(CardIdentifier.cityAtm_Check) || msg.contains(CardIdentifier.cityAtm_Check1))
		{
			cdObj.setID(03);
			cdObj.setMsgtimeStamp(ms);
			if(msg.contains("was"))
			{
				cdObj.setAmt( getCreditAmt(CardIdentifier.cityAtm_Split,msg));
				cdObj.setAt(getAt(msg,"an","from"));
			}
			else
			{
				cdObj.setAmt( getCreditAmt(CardIdentifier.cityAtm2_Split,msg));
				cdObj.setAt("ATM");
			}

			cdObj.setMnt(mntWrd);
			cdObj.setYr(yr);
			cd.add(cdObj);

		}
		else if(msg.contains(CardIdentifier.cityDebit_Check1))
		{
			if(msg.contains(CardIdentifier.cityDebit_Check2))
			{
				cdObj.setID(03);
				cdObj.setMsgtimeStamp(ms);
				cdObj.setAmt( getCreditAmt(CardIdentifier.cityDebit_Split,msg));
				cdObj.setAt(getAt(msg,"at ","on"));
				cdObj.setMnt(mntWrd);
				cdObj.setYr(yr);
				cd.add(cdObj);

			}
		}
		else if(msg.contains(CardIdentifier.cityCredit_Check))
		{
			cdObj.setID(10);
			cdObj.setMsgtimeStamp(ms);
			cdObj.setAmt( getCreditAmt(CardIdentifier.cityCredit_Split,msg));
			cdObj.setAt(getAt(msg,"at ","\\."));
			cdObj.setMnt(mntWrd);
			cdObj.setYr(yr);
			cd.add(cdObj);

		}
		else if(msg.contains(CardIdentifier.iciciDebit_Check1) && msg.contains(CardIdentifier.iciciDebit_Check2))
		{
			iciciDebit(msg,ms);
		}
		else if(msg.contains(CardIdentifier.sbiDebit_Check) || msg.contains(CardIdentifier.sbiDebit_Check2))
		{
			double sbi = 0;
			if(msg.contains(CardIdentifier.sbiDebit_in2_Check2))
			{
				sbi=getCreditAmt(CardIdentifier.sbiDebit_Check2_in2_Split,msg);
				cdObj.setAt("ATM");
			}
			else if(msg.contains(CardIdentifier.sbiDebit_in3_Check2))
			{
				String tmp2="";
				String[] separated = msg.split("Rs");
				tmp2=separated[1];
				sbi=Double.valueOf((tmp2));
				cdObj.setAt("ATM");
			}
			else if(msg.contains(CardIdentifier.sbiDebit_Check1))
			{
				sbi=getCreditAmt(CardIdentifier.sbiDebit_Check1_Split,msg);
				cdObj.setAt(getAt(msg,"at","t"));
			}
			else if (msg.contains(CardIdentifier.sbiDebit_Check2))
			{
				sbi=getCreditAmt(CardIdentifier.sbiDebit_Check2_Split,msg);
				if(msg.contains("WDL"))
					cdObj.setAt("ATM");
				else
					cdObj.setAt("NEFT");
			}
			else if (msg.contains(CardIdentifier.sbiDebit_in1_Check2))
			{
				sbi=getCreditAmt(CardIdentifier.sbiDebit_Check2_Split,msg);
				cdObj.setAt(getAt(msg,"at","t"));
			}

			cdObj.setID(05);
			cdObj.setMsgtimeStamp(ms);
			cdObj.setAmt( sbi);
			cdObj.setMnt(mntWrd);
			cdObj.setYr(yr);
			cd.add(cdObj);

		}
		else if(msg.contains(CardIdentifier.iciciDebitPurchase_Check) || msg.contains(CardIdentifier.iciciDebit_Check3))
		{
			iciciDebit(msg,ms);
		}
		else if(msg.contains(CardIdentifier.sbiDebitIB_Check))
		{
			cdObj.setID(05);
			cdObj.setMsgtimeStamp(ms);
			cdObj.setAmt(getCreditAmt(CardIdentifier.sbiDebitIB_Check_Split,msg));
			cdObj.setAt("NO INFO");
			cdObj.setMnt(mntWrd);
			cdObj.setYr(yr);
			cd.add(cdObj);
		}
		else if(msg.contains(CardIdentifier.amex_Check1)&&msg.contains(CardIdentifier.amex_Check2)) 
		{
			cdObj.setID(06);
			cdObj.setMsgtimeStamp(ms);
			cdObj.setAmt(getCreditAmt(CardIdentifier.amex_Split,msg));
			cdObj.setAt(getAt(msg,"at ","on"));
			cdObj.setMnt(mntWrd);
			cdObj.setYr(yr);
			cd.add(cdObj);
		}
		else if(msg.contains(CardIdentifier.hdfcDebit_Check))
		{
			cdObj.setID(07);
			cdObj.setMsgtimeStamp(ms);
			cdObj.setAmt(getCreditAmt(CardIdentifier.hdfcDedit_Split,msg));
			cdObj.setAt(getAt(msg,"towards","in"));
			cdObj.setMnt(mntWrd);
			cdObj.setYr(yr);
			cd.add(cdObj);
		}
		else if(msg.contains(CardIdentifier.stanChartCredit_Check1))
		{
			double stanChart;
			if(msg.contains(CardIdentifier.stanChartCredit_Check2))
			{
				stanChart=getCreditAmt(CardIdentifier.stanChartCredit_Split2,msg);
			}
			else{
				stanChart=getCreditAmt(CardIdentifier.stanChartCredit_Split1,msg);
			}
			cdObj.setID(8);
			cdObj.setMsgtimeStamp(ms);
			cdObj.setAmt(stanChart);
			cdObj.setAt("NO INFO");
			cdObj.setMnt(mntWrd);
			cdObj.setYr(yr);
			cd.add(cdObj);
		}
		else if (msg.contains(CardIdentifier.kotakDebit_Check))
		{
			cdObj.setID(9);
			cdObj.setMsgtimeStamp(ms);
			cdObj.setAmt(getCreditAmt(CardIdentifier.kotakDebit_Split,msg));
			cdObj.setAt(getAt(msg,"at ","\\."));
			cdObj.setMnt(mntWrd);
			cdObj.setYr(yr);
			cd.add(cdObj);
		}
		else if (msg.contains(CardIdentifier.bob_Check))
		{
			cdObj.setID(12);
			cdObj.setMsgtimeStamp(ms);
			cdObj.setAmt(getCreditAmt(CardIdentifier.bob_Split,msg));
			cdObj.setAt(getAt(msg,"AT","\\."));
			cdObj.setMnt(mntWrd);
			cdObj.setYr(yr);
			cd.add(cdObj);
		}
		else if(msg.contains(CardIdentifier.sbi_credit_Check))
		{
			cdObj.setID(11);
			cdObj.setMsgtimeStamp(ms);
			cdObj.setAmt(getCreditAmt(CardIdentifier.sbi_Credit_Split,msg));
			cdObj.setAt("NO INFO");
			cdObj.setMnt(mntWrd);
			cdObj.setYr(yr);
			cd.add(cdObj);
		}
		else if(msg.contains(CardIdentifier.maha_debit_Check))
		{
			cdObj.setID(13);
			cdObj.setMsgtimeStamp(ms);
			cdObj.setAmt(getCreditAmt(CardIdentifier.maha_Debit_Split,msg));
			cdObj.setAt("NO INFO");
			cdObj.setMnt(mntWrd);
			cdObj.setYr(yr);
			cd.add(cdObj);
		}
		else if(msg.contains(CardIdentifier.cbi_debit_Check))
		{
			cdObj.setID(14);
			cdObj.setMsgtimeStamp(ms);
			cdObj.setAmt(getCreditAmt(CardIdentifier.cbi_Debit_Split,msg));
			cdObj.setAt("NO INFO");
			cdObj.setMnt(mntWrd);
			cdObj.setYr(yr);
			cd.add(cdObj);
		}

	}

	public void iciciDebit(String tmp1,long ms)
	{
		double iciciDB = 0;
		if(tmp1.contains(CardIdentifier.iciciDebitPurchase_Check))
		{
			iciciDB=getCreditAmt(CardIdentifier.iciciDebit_Purchase_Check1_Check2_Split,tmp1);
			cdObj.setAt(getAt(tmp1,"\\*","\\."));
		}
		else if(tmp1.contains(CardIdentifier.iciciDebit_Check1) && tmp1.contains(CardIdentifier.iciciDebit_Check2))
		{
			iciciDB=getCreditAmt(CardIdentifier.iciciDebit_Purchase_Check1_Check2_Split,tmp1);
			cdObj.setAt("NO INFO");
		}
		else if (tmp1.contains(CardIdentifier.iciciDebit_in_Check))
		{
			if(tmp1.contains(CardIdentifier.iciciDebit_in1_Check))
			{
				iciciDB=getCreditAmt(CardIdentifier.iciciDebit_in1_Split,tmp1);
				cdObj.setAt("ATM");
			}
			if(tmp1.contains(CardIdentifier.iciciDebit_in2_Check))
			{
				iciciDB=getCreditAmt(CardIdentifier.iciciDebit_in2_Split,tmp1);
				cdObj.setAt("ATM");
			}
		}
		cdObj.setID(4);
		cdObj.setMsgtimeStamp(ms);
		cdObj.setAmt(iciciDB);
		cdObj.setMnt(mntWrd);
		cdObj.setYr(yr);
		cd.add(cdObj);

	}

	public double getCreditAmt(String[]  str,String tmp1)
	{
		double amt=0;
		int ln = str.length;
		int i =0;
		while(ln >0)
		{
			--ln;
			try{
				String[] separated = tmp1.split(str[i]);
				tmp1=separated[ln];
				i++;
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				Log.w("ArrayIndexOutOfBoundsException",e.getMessage());
			}
		}
		tmp1=tmp1.replaceAll(",", "");
		try{
			amt=Double.valueOf((tmp1));
		}
		catch(NumberFormatException e)
		{
			Log.w("NumberFormatException",e.getMessage());
		}

		return amt;
	}
	private String getAt(String msg,String arg1, String arg2)
	{
		try{
			String[] at;
			//Log.w("At", msg);
			at= msg.split(arg1);
			at = at[1].split(arg2);
			//Log.w("At", at[0]);
			return at[0];
		}
		catch(Exception e)
		{

			Log.w("Error at at",e.getMessage());
			Log.w("At", msg);
			return "NO INFO";
		}
	}

	public String getMonthInWrd(int mnt)
	{
		switch (mnt){
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
