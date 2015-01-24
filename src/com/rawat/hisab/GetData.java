package com.rawat.hisab;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.rawat.hisab.utility.CardIdentifier;
import com.rawat.hisab.utility.ConfigDate;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.Telephony;
import android.util.Log;

public class GetData {

	private Cursor cur;
	private ConfigDate CfgDate;
	private ContentResolver cR;

	///Bank variables
	private double icicBank=0;
	private double hdfcCredit=0;
	private double city=0;
	private double iciciDB=0;
	private double sbi=0;
	private double amex=0;
	private double hdfcDebit=0;
	private double stanChart=0;
	private double kotakDebit=0;
	private double bob=0;
	private double citiCredit=0;
	private double sbiCredit=0;
	public static int count=0;
	public int cn=0;
	private List<Double> amt;
	private List<String> cardName;
	private double total=0 ;

	public GetData(ConfigDate Cfg, ContentResolver c)
	{
		this.CfgDate=Cfg;
		this.cR = c;
		amt = new ArrayList<Double>();
		cardName=new ArrayList<String>();

	}
	public void setTotal(String t)
	{

	}
	public int getCount()
	{
		count=0;
		if(icicBank > 0)
		{
			setParm(icicBank,"ICICI Credit");
			count++;
		}
		if(hdfcCredit > 0 )
		{
			setParm(hdfcCredit,"HDFC Credit");
			count++;
		}
		if(city>0)
		{
			setParm(city,"CITI Debit");
			count++;
		}
		if(iciciDB>0)
		{
			setParm(iciciDB,"ICICI Debit");
			count++;
		}
		if(sbi>0)
		{
			setParm(sbi,"SBI Debit");
			count++;
		}
		if(amex>0)
		{
			setParm(amex,"Amex Credit");
			count++;
		}
		if(hdfcDebit>0)
		{
			setParm(hdfcDebit,"HDFC Debit");
			count++;
		}
		if(stanChart >0)
		{
			setParm(stanChart,"Standard Chartered Credit");
		}
		if(kotakDebit >0)
		{
			setParm(kotakDebit,"Kotak Debit Card");
		}
		if(bob > 0)
		{
			setParm(bob,"BOB");
		}
		if(citiCredit > 0)
		{
			setParm(citiCredit,"Citi Credit");
		}
		if(sbiCredit > 0)
		{
			setParm(sbiCredit,"SBI Credit Card");
		}

		return count;
	}
	private void setParm(double cardAmt, String cName)
	{
		BigDecimal bd = new BigDecimal(Double.toString(cardAmt));
		bd = bd.setScale(2, BigDecimal.ROUND_UP);
		cardAmt=bd.doubleValue();

		amt.add(cardAmt);
		cardName.add(cName);
		//percent.add((cardAmt/total)*100);
	}

	public int getTotal()
	{
		int mnt = CfgDate.getEndMonth();
		int year = CfgDate.getEndYear();
		//Uri uriSMSURI = Uri.parse("content://sms/inbox/");

		cur = cR.query(Telephony.Sms.Inbox.CONTENT_URI, null, null, null,null);
		Calendar cl = Calendar.getInstance();
		/*cur.moveToFirst();

	    //read all messages in your inbox

	        //read all data from all available columns for each message
	        for (int i = 0; i < cur.getColumnCount(); i++)
	        {
	        	Log.w("*****Message***", cur.getColumnName(i) + ": " + cur.getString(i));
	        }*/



		while (cur.moveToNext()) {
			long ms= Long.parseLong(cur.getString(cur.getColumnIndexOrThrow("date")));


			cl.setTimeInMillis(ms);
			//Log.w("msg", cur.getString(12));
			if(cl.get(Calendar.MONTH)+1==mnt && cl.get(Calendar.YEAR) == year )
			{
				String msg="";
				msg=cur.getString(cur.getColumnIndexOrThrow("body"));
				//Log.w("msg", cur.getString(1));
				if(msg.contains(CardIdentifier.iciciCredit_Check))
				{
					icicBank=getCreditAmt(CardIdentifier.iciciCredit_Split,msg)+icicBank;
				}	
				else if(msg.contains(CardIdentifier.hdfcCredit_Check))
				{
					hdfcCredit=getCreditAmt(CardIdentifier.hdfcCredit_Split,msg)+hdfcCredit;
				}
				else if(msg.contains(CardIdentifier.hdfcCreditSMT_Check))
				{
					//hdfcCredit=getCreditAmt(CardComponent.hdfcCreditSMT_Split,msg)+hdfcCredit;
					String tmp2="";
					String[] separated = msg.split("Rs.");
					tmp2=separated[1];
					hdfcCredit=Double.valueOf((tmp2))+hdfcCredit;
				}
				else if(msg.contains(CardIdentifier.cityAtm_Check) || msg.contains(CardIdentifier.cityAtm_Check1))
				{
					if(msg.contains("was"))
						city=getCreditAmt(CardIdentifier.cityAtm_Split,msg)+city;
					else
						city=getCreditAmt(CardIdentifier.cityAtm2_Split,msg)+city;

				}
				else if(msg.contains(CardIdentifier.cityDebit_Check1))
				{
					if(msg.contains(CardIdentifier.cityDebit_Check2))
					{
						city=getCreditAmt(CardIdentifier.cityDebit_Split,msg)+city;

					}
				}
				else if(msg.contains(CardIdentifier.cityCredit_Check))
				{
					citiCredit=getCreditAmt(CardIdentifier.cityCredit_Split,msg)+citiCredit;

				}
				else if(msg.contains(CardIdentifier.iciciDebit_Check1) && msg.contains(CardIdentifier.iciciDebit_Check2))
				{
					iciciDebit(msg);
				}
				else if(msg.contains(CardIdentifier.sbiDebit_Check) || msg.contains(CardIdentifier.sbiDebit_Check2))
				{
					if(msg.contains(CardIdentifier.sbiDebit_in2_Check2))
					{
						sbi=getCreditAmt(CardIdentifier.sbiDebit_Check2_in2_Split,msg)+sbi;
					}
					else if(msg.contains(CardIdentifier.sbiDebit_in3_Check2))
					{
						String tmp2="";
						String[] separated = msg.split("Rs");
						tmp2=separated[1];
						sbi=Double.valueOf((tmp2))+sbi;
					}
					else if(msg.contains(CardIdentifier.sbiDebit_Check1))
					{
						sbi=getCreditAmt(CardIdentifier.sbiDebit_Check1_Split,msg)+sbi;
					}
					else if (msg.contains(CardIdentifier.sbiDebit_Check2))
					{
						sbi=getCreditAmt(CardIdentifier.sbiDebit_Check2_Split,msg)+sbi;
					}
					else if (msg.contains(CardIdentifier.sbiDebit_in1_Check2))
					{
						sbi=getCreditAmt(CardIdentifier.sbiDebit_Check2_Split,msg)+sbi;
					}
				}
				else if(msg.contains(CardIdentifier.iciciDebitPurchase_Check) || msg.contains(CardIdentifier.iciciDebit_Check3))
				{
					iciciDebit(msg);
				}
				else if(msg.contains(CardIdentifier.sbiDebitIB_Check))
				{
					sbi=getCreditAmt(CardIdentifier.sbiDebitIB_Check_Split,msg)+sbi;
				}
				else if(msg.contains(CardIdentifier.amex_Check1)&&msg.contains(CardIdentifier.amex_Check2)) 
				{
					amex=getCreditAmt(CardIdentifier.amex_Split,msg)+amex;
				}
				else if(msg.contains(CardIdentifier.hdfcDebit_Check))
				{
					hdfcDebit= getCreditAmt(CardIdentifier.hdfcDedit_Split,msg)+hdfcDebit;
				}
				else if(msg.contains(CardIdentifier.stanChartCredit_Check1))
				{
					if(msg.contains(CardIdentifier.stanChartCredit_Check2))
					{
						stanChart=getCreditAmt(CardIdentifier.stanChartCredit_Split2,msg)+stanChart;
					}
					else{
						stanChart=getCreditAmt(CardIdentifier.stanChartCredit_Split1,msg)+stanChart;
					}
				}
				else if (msg.contains(CardIdentifier.kotakDebit_Check))
				{
					kotakDebit=getCreditAmt(CardIdentifier.kotakDebit_Split,msg)+kotakDebit;
				}
				else if (msg.contains(CardIdentifier.bob_Check))
				{
					bob=getCreditAmt(CardIdentifier.bob_Split,msg)+bob;
				}
				else if(msg.contains(CardIdentifier.sbi_credit_Check))
				{
					sbiCredit=getCreditAmt(CardIdentifier.sbi_Credit_Split,msg)+sbiCredit;
				}
			}
		}
		//sms=sms+"Total "+ total +"";
		total=icicBank+hdfcCredit+city+iciciDB+sbi+kotakDebit+bob+amex+hdfcDebit+stanChart+citiCredit+sbiCredit;

		cn=getCount();
		int d = (int) Math.ceil(total);
		//total=d;
		cur.close();
		return d;
	}

	public void iciciDebit(String tmp1)
	{
		if(tmp1.contains(CardIdentifier.iciciDebitPurchase_Check))
		{
			iciciDB=getCreditAmt(CardIdentifier.iciciDebit_Purchase_Check1_Check2_Split,tmp1)+iciciDB;
		}
		else if(tmp1.contains(CardIdentifier.iciciDebit_Check1) && tmp1.contains(CardIdentifier.iciciDebit_Check2))
		{
			iciciDB=getCreditAmt(CardIdentifier.iciciDebit_Purchase_Check1_Check2_Split,tmp1)+iciciDB;
		}
		else if (tmp1.contains(CardIdentifier.iciciDebit_in_Check))
		{
			if(tmp1.contains(CardIdentifier.iciciDebit_in1_Check))
			{
				iciciDB=getCreditAmt(CardIdentifier.iciciDebit_in1_Split,tmp1)+iciciDB;
			}
			if(tmp1.contains(CardIdentifier.iciciDebit_in2_Check))
			{
				iciciDB=getCreditAmt(CardIdentifier.iciciDebit_in2_Split,tmp1)+iciciDB;
			}
		}
		//Log.w("ICICI Bank", tmp2+"");
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

	public List<Double> getCardAmt()
	{
		return amt;
	}
	public List<String> getCardName()
	{
		return cardName;
	}

}
