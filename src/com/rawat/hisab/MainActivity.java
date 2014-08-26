package com.rawat.hisab;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;


public class MainActivity extends ActionBarActivity implements ActionBar.OnNavigationListener{

	public final static String EXTRA_MESSAGE = "com.example.hisab.MESSAGE";
	private ConfigDate CfgDate;
	private ListView mainListView ;  
	private ArrayAdapter<String> listAdapter ;
	private MainAdapter md;
	 // action bar
    private ActionBar actionBar;
    private Spinner spinner;
    private Calendar cal_St;
	private int day_St;
	private int month_St;
	private int year_St;
	private EditText et_St;
	private ImageButton ib_Ed;
	private Calendar cal_Ed;
	private int day_Ed;
	private int month_Ed;
	private int year_Ed;
	private EditText et_Ed;
	private  View vv;
	private String startDate;
	private String endDate;
	private int dl;
	private Menu menu;
	private String month;
	private MenuItem bedMenuItem;
 
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		 // Find the ListView resource.   
	    mainListView = (ListView) findViewById( R.id.mainListView ); 
	    //MenuItem item = (MenuItem) findViewById(R.id.action_settings);
	   // listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow); 
	    //item.setTitle("Date");
	   ActionBar actionBar = getSupportActionBar(); 
	   actionBar.setDisplayHomeAsUpEnabled(false);
	   /* Context context = actionBar.getThemedContext();
	    ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(context, R.array.string_array, R.layout.simple_spinner_item);
	    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
	    actionBar.setListNavigationCallbacks(spinnerAdapter,  this);*/
	   CfgDate = (ConfigDate) getApplication();
		
		cal_St = Calendar.getInstance();
		day_St = cal_St.get(Calendar.DAY_OF_MONTH);
		month_St = cal_St.get(Calendar.MONTH);
		year_St = cal_St.get(Calendar.YEAR);
		
		cal_Ed = Calendar.getInstance();
		day_Ed = cal_St.get(Calendar.DAY_OF_MONTH);
		month_Ed = cal_St.get(Calendar.MONTH);
		year_Ed = cal_St.get(Calendar.YEAR);
		openRefresh();
	    
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		//return true;
		// Inflate the menu items for use in the action bar
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
			 bedMenuItem.setTitle(month);
		 }
		 
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		/*int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);*/
		// Handle presses on the action bar items
		switch (item.getItemId()) {

		case R.id.action_refresh:
			openRefresh();
			return true;
		case R.id.action_settings:
			/*Intent intent = new Intent(this, ConfigActivity.class);
			startActivity(intent);*/
			openSearch();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}


	}

	private void openRefresh() {
		CfgDate = (ConfigDate) getApplication();
		md= new MainAdapter(this,this);
		md.setMnt(CfgDate.getMonthInWrd());
		GetData data = new GetData(CfgDate,getContentResolver());
		
       // listAdapter.clear();
        //listAdapter.add(CfgDate.getMonthInWrd());
	   // mainListView.setAdapter( listAdapter ); 
		/*
		GetSms gt = new GetSms(this ,getContentResolver());
		//gt.execute("content://sms/inbox");
		try {
			Object result = gt.execute("content://sms/inbox").get();
			md.setResult(result +"");
			//listAdapter.add(result + "");
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		//float total = gt.getTotal();
		//listAdapter.add(result + "");
		*/
		md.setResult(data.getTotal()+"");
		md.setCardAmt(data.getCardAmt());
		md.setCardName(data.getCardName());
		md.setPercent(data.getPercent());
		md.setColor(data.getColor());
		
		mainListView.setAdapter(md);

	}

	private void openSearch() {
		// TODO Auto-generated method stub
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
	protected Dialog onCreateDialog(int id) 
	{
		
			return new DatePickerDialog(this, datePickerListener, year_Ed, month_Ed, day_Ed);
	
	}


	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			
				endDate =selectedDay + " / " + (selectedMonth + 1) + " / "+ selectedYear;
				//My.setEndDate(endDate);
				//My.notifyDataSetChanged();
				//mainListView.setAdapter(My);
				CfgDate.setEndDay(selectedDay);
				CfgDate.setEndMonth(selectedMonth+1);
				CfgDate.setEndYear(selectedYear);
				month=CfgDate.getMonthInWrd();
				updateMenuTitles();
				openRefresh();
		}
	};
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


}
