package com.rawat.hisab;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;

public class GetData {

	private Cursor cur;
	private ConfigDate CfgDate;
	private ContentResolver cR;

	///Bank variables
	private float icicBank=0;
	private float hdfcCredit=0;
	private float city=0;
	private float iciciDB=0;
	private float sbi=0;
	private float amex=0;
	private float hdfcDebit=0;
	private float stanChart=0;


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
			setParm(icicBank,"ICICI Credit",Color.RED);
			count++;
		}
		if(hdfcCredit > 0 )
		{
			setParm(hdfcCredit,"HDFC Credit",Color.BLUE);
			count++;
		}
		if(city>0)
		{
			setParm(city,"CITI Debit",Color.GREEN);
			count++;
		}
		if(iciciDB>0)
		{
			setParm(iciciDB,"ICICI Debit",Color.GRAY+Color.RED);
			count++;
		}
		if(sbi>0)
		{
			setParm(sbi,"SBI Debit",Color.LTGRAY);
			count++;
		}
		if(amex>0)
		{
			setParm(amex,"Amex Credit",Color.CYAN);
			count++;
		}
		if(hdfcDebit>0)
		{
			setParm(hdfcDebit,"HDFC Debit",Color.MAGENTA);
			count++;
		}
		if(stanChart >0)
		{
			setParm(stanChart,"Standard Chartered Credit",Color.YELLOW);
		}

		return count;
	}
	private void setParm(float cardAmt, String cName, int col)
	{
		amt.add(cardAmt);
		cardName.add(cName);
		//percent.add((cardAmt/total)*100);
		int d = (int) Math.ceil(cardAmt);
		percent.add((float) d );
		cl.add(col);
	}

	public int getTotal()
	{
		int mnt = CfgDate.getEndMonth();
		int year = CfgDate.getEndYear();
		Uri uriSMSURI = Uri.parse("content://sms/inbox");
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
				if(msg.contains(CardComponent.iciciCredit_Check))
				{
					icicBank=getCreditAmt(CardComponent.iciciCredit_Split,msg)+icicBank;
				}	
				else if(msg.contains(CardComponent.hdfcCredit_Check))
				{
					hdfcCredit=getCreditAmt(CardComponent.hdfcCredit_Split,msg)+hdfcCredit;
				}
				else if(msg.contains(CardComponent.hdfcCreditSMT_Check))
				{
					//hdfcCredit=getCreditAmt(CardComponent.hdfcCreditSMT_Split,msg)+hdfcCredit;
					String tmp2="";
					String[] separated = msg.split("Rs.");
					tmp2=separated[1];
					hdfcCredit=Float.valueOf((tmp2))+hdfcCredit;
				}
				else if(msg.contains(CardComponent.cityAtm_Check))
				{
					city=getCreditAmt(CardComponent.cityAtm_Split,msg)+city;
				}
				else if(msg.contains(CardComponent.iciciDebit_Check1) && msg.contains(CardComponent.iciciDebit_Check2))
				{
					iciciDebit(msg);
				}
				else if(msg.contains(CardComponent.sbiDebit_Check) || msg.contains(CardComponent.sbiDebit_Check2))
				{
					if(msg.contains(CardComponent.sbiDebit_in2_Check2))
					{
						sbi=getCreditAmt(CardComponent.sbiDebit_Check2_in2_Split,msg)+sbi;
					}
					else if(msg.contains(CardComponent.sbiDebit_in3_Check2))
					{
						String tmp2="";
						String[] separated = msg.split("Rs");
						tmp2=separated[1];
						sbi=Float.valueOf((tmp2))+sbi;
					}
					else if(msg.contains(CardComponent.sbiDebit_Check1))
					{
						sbi=getCreditAmt(CardComponent.sbiDebit_Check1_Split,msg)+sbi;
					}
					else if (msg.contains(CardComponent.sbiDebit_Check2))
					{
						sbi=getCreditAmt(CardComponent.sbiDebit_Check2_Split,msg)+sbi;
					}
					else if (msg.contains(CardComponent.sbiDebit_in1_Check2))
					{
						sbi=getCreditAmt(CardComponent.sbiDebit_Check2_Split,msg)+sbi;
					}
				}
				else if(msg.contains(CardComponent.iciciDebitPurchase_Check) || msg.contains(CardComponent.iciciDebit_Check3))
				{
					iciciDebit(msg);
				}
				else if(msg.contains(CardComponent.sbiDebitIB_Check))
				{
					sbi=getCreditAmt(CardComponent.sbiDebitIB_Check_Split,msg)+sbi;
				}
				else if(msg.contains(CardComponent.amex_Check1)&&msg.contains(CardComponent.amex_Check2)) 
				{
					amex=getCreditAmt(CardComponent.amex_Split,msg);
				}
				else if(msg.contains(CardComponent.hdfcDebit_Check))
				{
					hdfcDebit= getCreditAmt(CardComponent.hdfcDedit_Split,msg)+hdfcDebit;
				}
				else if(msg.contains(CardComponent.stanChartCredit_Check1))
				{
					if(msg.contains(CardComponent.stanChartCredit_Check2))
					{
						stanChart=getCreditAmt(CardComponent.stanChartCredit_Split2,msg)+stanChart;
					}
					else{
						stanChart=getCreditAmt(CardComponent.stanChartCredit_Split1,msg)+stanChart;
					}
				}
			}
		}
		//sms=sms+"Total "+ total +"";
		total=icicBank+hdfcCredit+city+iciciDB+sbi;
		cn=getCount();
		int d = (int) Math.ceil(total);
		//total=d;
		return d;
	}

	public void iciciDebit(String tmp1)
	{
		if(tmp1.contains(CardComponent.iciciDebitPurchase_Check))
		{
			iciciDB=getCreditAmt(CardComponent.iciciDebit_Purchase_Check1_Check2_Split,tmp1)+iciciDB;
		}
		else if(tmp1.contains(CardComponent.iciciDebit_Check1) && tmp1.contains(CardComponent.iciciDebit_Check2))
		{
			iciciDB=getCreditAmt(CardComponent.iciciDebit_Purchase_Check1_Check2_Split,tmp1)+iciciDB;
		}
		else if (tmp1.contains(CardComponent.iciciDebit_in_Check))
		{
			if(tmp1.contains(CardComponent.iciciDebit_in1_Check))
			{
				iciciDB=getCreditAmt(CardComponent.iciciDebit_in1_Split,tmp1)+iciciDB;
			}
			if(tmp1.contains(CardComponent.iciciDebit_in2_Check))
			{
				iciciDB=getCreditAmt(CardComponent.iciciDebit_in2_Split,tmp1)+iciciDB;
			}
		}
		//Log.w("ICICI Bank", tmp2+"");
	}
	public float getCreditAmt(String[]  str,String tmp1)
	{
		float amt=0;
		int ln = str.length;
		int i =0;
		while(ln >0)
		{
			--ln;
			String[] separated = tmp1.split(str[i]);
			tmp1=separated[ln];
			i++;
		}
		tmp1=tmp1.replaceAll(",", "");
		amt=Float.valueOf((tmp1));
		return amt;
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
