package com.rawat.hisab;

import java.math.BigDecimal;
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
import android.content.Intent;
import android.graphics.Color;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainAdapter extends BaseAdapter {

	protected static final String CARDNAME = null;
	protected static final String AMT = null;
	private LayoutInflater mInflater;
	private Context mContext;
	private Activity aa;
	private String mnt;
	private String result;

	private List<String> cardName;
	private List<Double> cardAmt;

	private Iterator<String> itrName;
	private Iterator<Double> itrAmt;

	private int ck;


	public MainAdapter(Context context,Activity a)
	{
		mInflater = LayoutInflater.from(context);
		this.mContext=context;
		this.aa=a;

		cardName= new ArrayList<String>();
		cardAmt = new ArrayList<Double>();
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
		LinearLayout lo;
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

	public void setResult(String rst)
	{
		this.result=rst ;
	}
	public void setCardAmt(List<Double> amt)
	{
		this.cardAmt=amt;
		itrAmt=cardAmt.iterator();

	}
	public void setCardName(List<String> cardName)
	{
		this.cardName=cardName;
		itrName=cardName.iterator();
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
					holder.lo=(LinearLayout) convertView.findViewById(id.chart_container);
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
		final DefaultRenderer defaultRenderer  = new DefaultRenderer();
		Iterator<Integer> col = ColorCombination.getColor().iterator();
		while(itrName.hasNext())
		{	
			distributionSeries.add(itrName.next(),itrAmt.next());
			SimpleSeriesRenderer seriesRenderer = new SimpleSeriesRenderer();
			seriesRenderer.setColor(col.next());
			defaultRenderer.addSeriesRenderer(seriesRenderer);
		}
		// Instantiating a renderer for the Pie Chart


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
					Intent intent = new Intent(aa, DisplayDetails.class);
					intent.putExtra(CARDNAME, cardName.get(seriesSelection.getPointIndex())+"_"+seriesSelection.getValue());
					//intent.putExtra(AMT, seriesSelection.getValue()+"");
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					aa.startActivity(intent);
				
				}
			}
		});
		defaultRenderer.setClickEnabled(true);
		return mChart;
	}

}
