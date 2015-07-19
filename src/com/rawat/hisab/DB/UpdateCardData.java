package com.rawat.hisab.DB;


import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UpdateCardData {

	private SQLiteDatabase database;
	private Context ct;
	private boolean isEmpty=false;
	private String[] allTable6Columns= {DBConst.Table6_Column1};
	private  boolean carMsg=true;

	public UpdateCardData(SQLiteDatabase database,Context ct)
	{
		this.database=database;
		this.ct=ct;
	}

	public void updateCardDetails(String msg,Long ms)
	{
		try
		{
			Log.w("Update", "Under Update Update Card Details");
			SMSScanner cdp = new SMSScanner(ct);
			List<CardDetails> cdls = cdp.newSMS(msg,ms);
			if(cdls.isEmpty())
			{
				isEmpty=true;
				setCarMsg(false);
				Log.w("Update", "No Update");
			}
			else
			{
				Iterator<CardDetails> itr = cdls.iterator();
				Log.w("Update", "Updating Card Month Details ");
				while(itr.hasNext())
				{
					CardDetails cd=itr.next();
					Log.w("Mnt", cd.getID()+"");
					ContentValues cv = new ContentValues(6);
					Log.w("TIME", cd.getMsgtimeStamp()+"  "+cd.getAmt());
					cv.put(DBConst.Table1_Column1, cd.getMsgtimeStamp());
					cv.put(DBConst.Table1_Column2, cd.getID());
					cv.put(DBConst.Table1_Column3, cd.getAmt());
					cv.put(DBConst.Table1_Column4, cd.getAt());
					cv.put(DBConst.Table1_Column5, cd.getMnt());
					cv.put(DBConst.Table1_Column6, cd.getYr());
					database.insert(DBConst.Table1_Name, null, cv);

					//Check at is present in the tag info
					if(!checkAt(cd.getAt()))
						updateTagInfo(cd.getAt());

					updateCardMontlyData();
				}

			}
		}
		catch(Exception e)
		{
			Log.w("Error in update card details ", e.getMessage());
		}
	}

	public void updateCardMontlyData() throws Exception
	{
		Log.w("Update", "Under UpdateCardMnt");
		if(!isEmpty)
		{
			long lastSync=getLastSync();
			Cursor cr = database.query(DBConst.Table1_Name, null, DBConst.Table1_Column1 + " >= ? ", 
					new String[]{String.valueOf(lastSync)}, null, null, null);

			while(cr.moveToNext())
			{
				String[] args = new String[]{cr.getInt(1)+"",cr.getString(4),cr.getInt(5)+""};
				Cursor chk = database.query(DBConst.Table3_Name,null,DBConst.Table3_Column1 + " =? AND "+ DBConst.Table3_Column2+" =? AND "
						+ DBConst.Table3_Column3 +" =? ",args, null, null, null);
				Cursor crSum = database.rawQuery("SELECT SUM ( "+DBConst.Table1_Column3 +" ) " + " FROM " +
						DBConst.Table1_Name+" WHERE "+DBConst.Table1_Column2 +" = ? "+" AND "+DBConst.Table1_Column5
						+ " = ? "+" AND "+DBConst.Table1_Column6 + " =? ", args);
				crSum.moveToNext();

				if(chk.getCount() == 0)
				{
					Log.w("Update", "Insert new record CardMnt card id"+cr.getInt(1));
					ContentValues cv = new ContentValues(4);
					cv.put(DBConst.Table3_Column1, cr.getInt(1));
					cv.put(DBConst.Table3_Column2, cr.getString(4));
					cv.put(DBConst.Table3_Column3, cr.getInt(5));
					cv.put(DBConst.Table3_Column4, crSum.getInt(0));
					database.insert(DBConst.Table3_Name, null, cv);
					updateMntTotal(cr.getString(4),cr.getInt(5));
				}
				else if (chk.getCount() > 0)
				{
					Log.w("Update", "Updating old record CardMnt card id "+cr.getInt(1));
					int sum=crSum.getInt(0);
					ContentValues update = new ContentValues();
					update.put(DBConst.Table3_Column4, sum);
					database.update(DBConst.Table3_Name, update,DBConst.Table3_Column1+" =? AND " +DBConst.Table3_Column2 + " = ? AND " +
							DBConst.Table3_Column3 + " =? ", new String[]{cr.getInt(1)+"",cr.getString(4),cr.getInt(5)+""});
					updateMntTotal(cr.getString(4),cr.getInt(5));
				}
				chk.close();
				crSum.close();
			}
			cr.close();

		}
	}
	private void updateMntTotal(String mnt, int yr) throws Exception
	{
		Cursor chk = database.query(DBConst.Table5_Name, null,DBConst.Table5_Column1 +" = ? AND " + 
				DBConst.Table5_Column2 + " =? ", new String[]{mnt,yr+""}, null, null, null);
		Cursor crSum = database.rawQuery("SELECT SUM ( "+DBConst.Table3_Column4 +" ) " + " FROM " +
				DBConst.Table3_Name+" WHERE "+DBConst.Table3_Column2 +" = ? "+" AND "+DBConst.Table3_Column3
				+ " = ? ", new String[]{mnt,yr+""});
		crSum.moveToNext();
		int sum = crSum.getInt(0);
		if(chk.getCount() == 0)
		{
			Log.w("Update", "Insert new record Mnt Total");
			ContentValues cv = new ContentValues(3);
			cv.put(DBConst.Table5_Column1,mnt);
			cv.put(DBConst.Table5_Column2, yr);
			cv.put(DBConst.Table5_Column3, sum);
			database.insert(DBConst.Table5_Name,null, cv);
		}
		else if(chk.getCount() >0)
		{
			Log.w("Update", "Updating old record Mnt Total");
			chk.moveToNext();
			ContentValues cv = new ContentValues();
			cv.put(DBConst.Table5_Column3, sum);
			database.update(DBConst.Table5_Name, cv, DBConst.Table5_Column1 +" = ? AND " + 
					DBConst.Table5_Column2 +" =? ", new String[]{mnt,yr+""});
		}
		updateSyncTime();
		chk.close();
		crSum.close();

	}
	public void deleteTransaction(String msgTmp, int cardId, double amt, String mnt, int yr,double montlyCardTotal, int total) throws Exception 
	{
		database.delete(DBConst.Table1_Name,DBConst.Table1_Column1 +" = "+msgTmp, null);
		updateCardMontlyTotalAfterDeleteTransaction(cardId,amt,mnt,yr,montlyCardTotal);
		updateTotalAfterDeleteTransaction(amt,mnt,yr,total);
	}
	private void updateCardMontlyTotalAfterDeleteTransaction(int cardId, double amt, String mnt, int yr,double montlyCardTotal)
	{
		if(amt == montlyCardTotal)
		{
			database.delete(DBConst.Table3_Name, DBConst.Table3_Column1 + " = ? AND " +
					DBConst.Table3_Column2 + " = ? AND " + DBConst.Table3_Column3 + " = ? " , new String[]{cardId+"",mnt,yr+""});
		}
		else 
		{
			ContentValues cv = new ContentValues();
			cv.put(DBConst.Table3_Column4, montlyCardTotal - amt);
			database.update(DBConst.Table3_Name, cv,DBConst.Table3_Column1 + " = ? AND " +
					DBConst.Table3_Column2 + " = ? AND " + DBConst.Table3_Column3 + " = ? " , new String[]{cardId+"",mnt,yr+""});
		}
	}
	private void updateTotalAfterDeleteTransaction(double amt, String mnt, int yr, int total)
	{
		if(amt == total)
		{
			database.delete(DBConst.Table5_Name, DBConst.Table5_Column1 + " = ? AND "+ 
							DBConst.Table5_Column2 + " = ? ", new String[]{mnt,yr+""});
		}
		else
		{
			ContentValues cv = new ContentValues();
			cv.put(DBConst.Table5_Column3, total- amt);
			database.update(DBConst.Table5_Name, cv, DBConst.Table5_Column1 + " = ? AND "+ 
							DBConst.Table5_Column2 + " = ? ", new String[]{mnt,yr+""});
		}
	}
	public long getLastSync() throws Exception
	{
		Log.w("last sync", "ls");
		long lastSync = 0;
		Cursor cursor =database.query(DBConst.Table6_Name,allTable6Columns, null, null, null, null, null);
		cursor.moveToFirst();
		lastSync=cursor.getLong(0);
		cursor.close();
		return lastSync;
	}
	public void updateSyncTime() throws Exception
	{
		Log.w("Update", "Update last sync");
		Cursor chk= database.query(DBConst.Table6_Name, null, null, null, null, null, null);

		long date = System.currentTimeMillis();
		chk.moveToNext();
		ContentValues cv = new ContentValues();
		cv.put(DBConst.Table6_Column1, date);
		database.update(DBConst.Table6_Name, cv, DBConst.Table6_Column1 +" =? ", new String[]{chk.getLong(0)+""});
		chk.close();
	}

	public void updateTagInfo(String at) throws Exception
	{
		ContentValues cv = new ContentValues();
		cv.put(DBConst.Table7_Column1, at);
		cv.put(DBConst.Table7_Column2, "null");
		database.insert(DBConst.Table7_Name, null, cv);
		Log.w("Update Tag", "Tag updated");
	}

	public Boolean checkAt(String at) throws Exception
	{
		boolean isPresent=false;
		Cursor crCheckAt = database.query(DBConst.Table7_Name, new String[]{DBConst.Table7_Column1}, 
				DBConst.Table7_Column1+" =? ", new String[]{at}, null, null, null);
		if(crCheckAt.getCount() > 0)
			isPresent=true;

		Log.w("Check at",isPresent+" "+at);
		return isPresent;
	}

	public  boolean isCarMsg() {
		return carMsg;
	}

	public  void setCarMsg(boolean carMsg) {
		this.carMsg = carMsg;
	}

}
