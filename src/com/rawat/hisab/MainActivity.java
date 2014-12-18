package com.rawat.hisab;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import com.rawat.hisab.DB.HisabDataSource;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.LinearLayout;
import android.widget.ListView;


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
		
		hds.close();
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
		SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		Log.w("NOtification", SP.getBoolean("notifications_new_message", false)+"");
		
		CfgDate = (ConfigDate) getApplication();
		md= new MainAdapter(this,this);
		md.setMnt(CfgDate.getMonthInWrd());
		//md.setYear(CfgDate.getEndYear());
		GetData data = new GetData(CfgDate,this.getContentResolver());
		hds.open();
		int total=0;
		try
		{
		 total = hds.getTotal(CfgDate.getMonthInWrd(),CfgDate.getEndYear());
		}
		catch(NullPointerException e)
		{
			Log.w("Catch", "Catch excption");
		}
		
		//hds.getCardTotal(CfgDate.getMonthInWrd(), CfgDate.getEndYear());
		//hds.getTotal(CfgDate.getMonthInWrd(), CfgDate.getEndYear());
		md.setCheck(total);
		md.setResult(total+"");
		md.setCardAmt(hds.getCardAmt());
		md.setCardName(hds.getCardName());
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
		final Calendar c = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(this, mDateSetListner, c.get(Calendar.YEAR),
                c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        dialog.getDatePicker().setSpinnersShown(true);

        // hiding calendarview and daySpinner in datePicker
           dialog.getDatePicker().setCalendarViewShown(false);

            LinearLayout pickerParentLayout = (LinearLayout) dialog.getDatePicker().getChildAt(0);

            LinearLayout pickerSpinnersHolder = (LinearLayout) pickerParentLayout.getChildAt(0);

            pickerSpinnersHolder.getChildAt(1).setVisibility(View.GONE);


        //dialog.setTitle("Pick a date");
        return dialog;
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
