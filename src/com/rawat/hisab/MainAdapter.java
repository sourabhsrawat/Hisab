package com.rawat.hisab;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;



import com.rawat.hisab.R.id;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private Activity aa;
	private String mnt;
	private String result;
	private int i=0;
	private int col=0;
	private int count;
	private List<String> cardName;
	private List<Float> cardAmt;
	private List<Float> percent;
	private List<Integer> cl;
	private Iterator<String> itrName;
	private Iterator<Float> itrAmt;
	private Iterator<Float> itrPer;
	private Iterator<Integer> itrCl;
	private String[] code;
	private int yr;
	private int ck;


	public MainAdapter(Context context,Activity a)
	{
		mInflater = LayoutInflater.from(context);
		this.mContext=context;
		this.aa=a;
		count=GetData.count;
		cardName= new ArrayList<String>();
		cardAmt = new ArrayList<Float>();
		percent= new ArrayList<Float>();

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		//return 2+count*2;
			return 3;
		
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	public class Holder
	{
		TextView mText;
		TextView card;
		RelativeLayout lo;
	}
	public void setMnt(String mt)
	{
		this.mnt=mt;
	}
	public String getMnt()
	{
		return mnt;
	}
	public void setCheck(int i)
	{
		this.ck=i;
	}
	public void setYear(int yr)
	{
		this.yr=yr;
	}
	public void setResult(String rst)
	{
		this.result=rst ;
	}
	public void setCardAmt(List<Float> amt)
	{
		this.cardAmt=amt;
		itrAmt=cardAmt.iterator();

	}
	public void setCardName(List<String> cardName)
	{
		this.cardName=cardName;
		itrName=cardName.iterator();
	}
	public void setPercent(List<Float> percent)
	{
		this.percent=percent;
		itrPer=percent.iterator();
	}
	public void setColor(List<Integer> cl)
	{
		this.cl=cl;
		itrCl=cl.iterator();
	}
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		final Holder holder;
		//Log.w("Count in adapter",count+"");
		//int type = getItemViewType(position);
		if (convertView == null) {
			holder = new Holder();
			//switch (position) {
			//case 0:
			
			if(position == 0)
			{
				convertView = mInflater.inflate(R.layout.simplerow,null);
				holder.mText=(TextView) convertView.findViewById(id.rowTextView);
				holder.mText.setText(mnt);
			}
			if(position == 1)
			{
				convertView = mInflater.inflate(R.layout.simplerow,null);
				holder.mText=(TextView) convertView.findViewById(id.rowTextView);
				holder.mText.setTextSize(50);
				holder.mText.setText(result +"\u20B9");
			}	
			if(position ==2 )
			{
				if(ck >0)
				{
					convertView = mInflater.inflate(R.layout.chart,null);
					holder.lo=(RelativeLayout) convertView.findViewById(id.chart_container);
					holder.lo.addView(openChart());
				}
				else {
					convertView = mInflater.inflate(R.layout.simplerow,null);
					holder.mText=(TextView) convertView.findViewById(id.rowTextView);
					holder.mText.setTextSize(20);
					holder.mText.setText("No expenses found for this month. " );

				}
			}

			convertView.setTag(holder);
		}

		else {

			holder = (Holder) convertView.getTag();
		}
		return convertView;
	}
	private GraphicalView openChart(){
		//GraphicalView mChart;
		// Pie Chart Slice Names

		final CategorySeries distributionSeries = new CategorySeries("Bank List");

		//Log.w("Before while", "series" );
		while(itrName.hasNext())
		{
			distributionSeries.add(itrName.next(),itrPer.next());
		}
		// Instantiating a renderer for the Pie Chart
		final DefaultRenderer defaultRenderer  = new DefaultRenderer();
		while(itrCl.hasNext()){

			// Instantiating a render for the slice
			SimpleSeriesRenderer seriesRenderer = new SimpleSeriesRenderer();
			seriesRenderer.setColor(itrCl.next());

			defaultRenderer.addSeriesRenderer(seriesRenderer);
		}

		defaultRenderer.setPanEnabled(false);
		defaultRenderer.setDisplayValues(true);

		defaultRenderer.setLabelsColor(Color.BLACK);
		defaultRenderer.setShowLabels(true);
		defaultRenderer.setLegendTextSize(20);
		defaultRenderer.setLabelsTextSize(20);
		
		final GraphicalView mChart = ChartFactory.getPieChartView(mContext, distributionSeries, defaultRenderer);
		mChart.setOnClickListener(new View.OnClickListener() { 
			@Override
			public void onClick(View v) {
				SeriesSelection seriesSelection =mChart.getCurrentSeriesAndPoint();
				if (seriesSelection == null) {

				} else {
					
					Toast.makeText( aa, "" + cardName.get(seriesSelection.getPointIndex()) + "  " + " Amount " + seriesSelection.getValue()+"\u20B9", Toast.LENGTH_SHORT).show();
				}
			}
		});
		defaultRenderer.setClickEnabled(true);
		return mChart;
	}

}
