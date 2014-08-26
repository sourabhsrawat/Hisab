package com.rawat.hisab;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


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
import android.widget.TextView;

public class MainAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private Activity aa;
	private String mnt;
	private String result;
	private int i=0;
	private int count;
	private List<String> cardName;
	private List<Float> cardAmt;
	private List<Float> percent;
	private List<Integer> cl;
	private Iterator<String> itrName;
	private Iterator<Float> itrAmt;
	private Iterator<Float> itrPer;
	private Iterator<Integer> itrCl;

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
		return 2+count*2;
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
	}
	public void setMnt(String mt)
	{
		this.mnt=mt;
	}
	public String getMnt()
	{
		return mnt;
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
		Log.w("Count in adapter",count+"");
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
				holder.mText.setText(result);
			}	
			if(position >1 )
			{
				if(position % 2== 0)
				{
					
					convertView = mInflater.inflate(R.layout.graph,null);
					holder.card=(TextView) convertView.findViewById(id.card);
					holder.card.setTextSize(20);
					holder.card.setText(itrName.next()+" " + itrAmt.next());
					i++;
				}
				/*	}

			if(position >2 )
			{*/
				else{
					convertView = mInflater.inflate(R.layout.graph,null);
					holder.mText=(TextView) convertView.findViewById(id.rowgf);
					holder.mText.setHeight(20);
					holder.mText.setMaxWidth(700);
					int tmp = (int) (700*itrPer.next());
					holder.mText.setWidth(tmp);
					holder.mText.setBackgroundColor(itrCl.next());
				}
			}



			convertView.setTag(holder);
		}
		else {

			holder = (Holder) convertView.getTag();
		}
		return convertView;
	}

}
