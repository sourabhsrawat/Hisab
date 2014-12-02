package com.rawat.hisab;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import com.rawat.hisab.DB.CardDetails;
import com.rawat.hisab.DB.HisabDataSource;


import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ListView;
/*
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.GoogleAnalytics;

import 	com.google.android.gms.analytics.HitBuilders;
import com.rawat.hisab.ConfigDate.TrackerName;
*/

public class MainActivity extends ActionBarActivity implements ActionBar.OnNavigationListener{

	public final static String EXTRA_MESSAGE = "com.example.hisab.MESSAGE";
	private ConfigDate CfgDate;
	private ListView mainListView ;  
	private MainAdapter md;
	private Menu menu;
	private String month;
	private MenuItem bedMenuItem;
	private int mYear;
	private int mMonth;
	private int mDay;
	private Notification nf;
	private HisabDataSource hds;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// Find the ListView resource.   
		mainListView = (ListView) findViewById( R.id.mainListView ); 
		ActionBar actionBar = getSupportActionBar(); 
		actionBar.setDisplayHomeAsUpEnabled(false);
		//String defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(getBaseContext());
		//this.deleteDatabase("hisab.db");
		hds = new HisabDataSource(this);
		hds.open();
		//Notification
		nf = new Notification(this);
		CfgDate = (ConfigDate) getApplication();
		if(CfgDate.getEndMonth() == 0)
		{
			Calendar calendar = Calendar.getInstance();
			int yr = calendar.get(Calendar.YEAR);
			int mn = calendar.get(Calendar.MONTH);
			CfgDate.setEndYear(yr);
			CfgDate.setEndMonth(mn+1);
		}
		//openChart();
		Log.w("Card ", "Details");
		/*List<CardDetails> cd =hds.getAllCardDeatils();
		Iterator<CardDetails> itr = cd.iterator();
		itr.hasNext();*/
		hds.selectData();
		//hds.updateCardData();
		Log.w("Database", "After select");

		openRefresh();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		this.menu=menu;
		MenuItem bedMenuItem = menu.findItem(R.id.action_settings);
		month=CfgDate.getMonthInWrd();

		if(month.equals("ENTER MONTH"))
		{
			bedMenuItem.setTitle("Set Date");
		}
		else
		{
			month=month +"\n" + CfgDate.getEndYear();
			//month=CfgDate.getEndYear() +"";
			bedMenuItem.setTitle(month);
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.action_refresh:
			openRefresh();
			return true;
		case R.id.action_settings:
			openSearch();
			return true;
		case R.id.action_set:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void openRefresh() {
		
		/*Tracker t = ((ConfigDate) getApplication()).getTracker(TrackerName.APP_TRACKER);
		t.setScreenName("MainActivity");
		t.send(new HitBuilders.AppViewBuilder().build());*/
		nf.displayNotification();
		CfgDate = (ConfigDate) getApplication();
		md= new MainAdapter(this,this);
		md.setMnt(CfgDate.getMonthInWrd());
		//md.setYear(CfgDate.getEndYear());
		GetData data = new GetData(CfgDate,this.getContentResolver());
		int total=0;
		try
		{
		 total = data.getTotal();
		}
		catch(NullPointerException e)
		{
			Log.w("Catch", "Catch excption");
		}
		md.setCheck(total);
		md.setResult(total+"");
		md.setCardAmt(data.getCardAmt());
		md.setCardName(data.getCardName());
		//md.setVw(openChart());
		mainListView.setAdapter(md);
		//logWrite();
		hds.close();
		
	}

	@SuppressWarnings("deprecation")
	private void openSearch() {
		// TODO Auto-generated method stub
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mMonth=mMonth+1;
		showDialog(0);
	}



	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}



	@Override
	public boolean onNavigationItemSelected(int arg0, long arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	DatePickerDialog.OnDateSetListener mDateSetListner = new OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {

			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDate();
		}
	};
	@SuppressWarnings("deprecation")
	protected void updateDate() {

		CfgDate.setEndMonth(mMonth+1);
		CfgDate.setEndYear(mYear);
		month=CfgDate.getMonthInWrd();
		updateMenuTitles();
		openRefresh();
		showDialog(0);
	}
	protected Dialog onCreateDialog(int id) 
	{

		//return new DatePickerDialog(this, datePickerListener, year_Ed, month_Ed, day_Ed);
		DatePickerDialog datePickerDialog = this.customDatePicker();
		datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
		return datePickerDialog;

	}
	private DatePickerDialog customDatePicker() {
		DatePickerDialog dpd = new DatePickerDialog(this, mDateSetListner,
				mYear, mMonth, mDay);
		try {

			Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
			for (Field datePickerDialogField : datePickerDialogFields) {
				if (datePickerDialogField.getName().equals("mDatePicker")) {
					datePickerDialogField.setAccessible(true);
					DatePicker datePicker = (DatePicker) datePickerDialogField
							.get(dpd);
					Field datePickerFields[] = datePickerDialogField.getType()
							.getDeclaredFields();
					for (Field datePickerField : datePickerFields) {
						if ("mDayPicker".equals(datePickerField.getName())
								|| "mDaySpinner".equals(datePickerField
										.getName())) {
							datePickerField.setAccessible(true);
							Object dayPicker = new Object();
							dayPicker = datePickerField.get(datePicker);
							((View) dayPicker).setVisibility(View.GONE);
						}
					}
				}

			}
		} catch (Exception ex) {
		}
		return dpd;
	}



	private void updateMenuTitles() {
		bedMenuItem = menu.findItem(R.id.action_settings);
		month=month +"\n" + CfgDate.getEndYear();
		
		bedMenuItem.setTitle(month);

	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.activity_main);
		bedMenuItem.setTitle(month);
		openRefresh();
	}

	
public void logWrite()
{
	try {
		
	      Process process = Runtime.getRuntime().exec("logcat -d");
	      BufferedReader bufferedReader = new BufferedReader(
	      new InputStreamReader(process.getInputStream()));
	                       
	      StringBuilder log=new StringBuilder();
	      String line;
	      while ((line = bufferedReader.readLine()) != null) {
	        log.append(line);
	      }
	      File myFile = new File("/sdcard/hisablog.txt");
			myFile.createNewFile();
			FileOutputStream fOut = new FileOutputStream(myFile);
			OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
			myOutWriter.append(log);
			myOutWriter.close();
			fOut.close();
	      
	    } catch (IOException e) {
	    }
}

}
