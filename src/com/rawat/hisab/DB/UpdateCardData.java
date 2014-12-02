package com.rawat.hisab.DB;

import java.util.Calendar;
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

	public UpdateCardData(SQLiteDatabase database,Context ct)
	{
		this.database=database;
		this.ct=ct;
	}

	public void updateCardDetails(String msg,Long ms)
	{
		Log.w("Update", "Under Update Update Card Details");
		CardDataProvider cdp = new CardDataProvider(ct);
		List<CardDetails> cdls = cdp.newSMS(msg,ms);
		if(cdls.isEmpty())
		{
			isEmpty=true;
			Log.w("Update", "No Update");
		}
		else
		{
			Iterator<CardDetails> itr = cdls.iterator();
			Log.w("Update", "Updating");
			while(itr.hasNext())
			{
				CardDetails cd=itr.next();
				Log.w("Mnt", cd.getMnt());
				ContentValues cv = new ContentValues(6);
				cv.put(DBConst.Table1_Column1, cd.getMsgtimeStamp());
				cv.put(DBConst.Table1_Column2, cd.getID());
				cv.put(DBConst.Table1_Column3, cd.getAmt());
				cv.put(DBConst.Table1_Column4, "AT");
				cv.put(DBConst.Table1_Column5, cd.getMnt());
				cv.put(DBConst.Table1_Column6, cd.getYr());
				database.insert(DBConst.Table1_Name, null, cv);
		
				updateCardMontlyData();
			}
		}
	}

	public void updateCardMontlyData()
	{
		Log.w("Update", "Under UpdateCardMnt");
		if(!isEmpty)
		{
			long lastSync=getLastSync();
			Log.w("Last sy", lastSync+"");
 			Cursor cr = database.query(DBConst.Table1_Name, null, DBConst.Table1_Column1 + " >= ? ", 
					new String[]{String.valueOf(lastSync)}, null, null, null);
 			Log.w("Count", cr.getCount()+"");
			while(cr.moveToNext())
			{
				String[] args = new String[]{cr.getInt(1)+"",cr.getString(4),cr.getInt(5)+""};
				Cursor chk = database.query(DBConst.Table3_Name,null,DBConst.Table3_Column1 + " =? AND "+ DBConst.Table3_Column2+" =? AND "
						+ DBConst.Table3_Column3 +" =? ",args, null, null, null);
				Log.w("Insert Row", "Insert Card Data");
				Cursor crSum = database.rawQuery("SELECT SUM ( "+DBConst.Table1_Column3 +" ) " + " FROM " +
						DBConst.Table1_Name+" WHERE "+DBConst.Table1_Column2 +" = ? "+" AND "+DBConst.Table1_Column5
						+ " = ? "+" AND "+DBConst.Table1_Column6 + " =? ", args);
				crSum.moveToNext();

				if(chk.getCount() == 0)
				{
					Log.w("Update", "Insert new record CardMnt");
					ContentValues cv = new ContentValues(4);
					cv.put(DBConst.Table3_Column1, cr.getInt(1));
					cv.put(DBConst.Table3_Column2, cr.getString(4));
					cv.put(DBConst.Table3_Column3, cr.getInt(5));
					cv.put(DBConst.Table3_Column4, crSum.getInt(0));
					database.insert(DBConst.Table3_Name, null, cv);
					updateMntTotal(cr.getString(4),cr.getInt(5),crSum.getInt(0));
				}
				else if (chk.getCount() > 0)
				{
					Log.w("Update", "Updating old record CardMnt");
					int sum=crSum.getInt(0);
					ContentValues update = new ContentValues();
					update.put(DBConst.Table3_Column4, sum);
					database.update(DBConst.Table3_Name, update, DBConst.Table3_Column2 + " = ? AND " +
							DBConst.Table3_Column3 + " =? ", new String[]{cr.getString(4),cr.getInt(5)+""});
					updateMntTotal(cr.getString(4),cr.getInt(5),crSum.getInt(0));
				}

			}
			updateSyncTime();
		}
	}
	private void updateMntTotal(String mnt, int yr,int sum)
	{
		Cursor chk = database.query(DBConst.Table5_Name, null,DBConst.Table5_Column1 +" = ? AND " + 
				DBConst.Table5_Column2 + " =? ", new String[]{mnt,yr+""}, null, null, null);
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

	}
	public long getLastSync() 
	{
		Log.w("last sync", "ls");
		long lastSync = 0;
		Cursor cursor =database.query(DBConst.Table6_Name,allTable6Columns, null, null, null, null, null);
		cursor.moveToFirst();
		lastSync=cursor.getLong(0);
		return lastSync;
	}
	public void updateSyncTime()
	{
		Log.w("Update", "Update last sync");
		Cursor chk= database.query(DBConst.Table6_Name, null, null, null, null, null, null);
		 
		long date = System.currentTimeMillis();
		chk.moveToNext();
		ContentValues cv = new ContentValues();
		Log.w("Last Sync DB",chk.getLong(0)+ "");
		cv.put(DBConst.Table6_Column1, date);
		database.update(DBConst.Table6_Name, cv, DBConst.Table6_Column1 +" =? ", new String[]{chk.getLong(0)+""});
	}

}
