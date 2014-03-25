package com.innovatelogic.redialme;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.innovatelogic.redialme.UserContactInfo;

//----------------------------------------------------------------------------------------------
//
//----------------------------------------------------------------------------------------------
public class AdapterContacts extends ArrayAdapter<UserContactInfo>
{
	Context context;
	int layoutResourceId;
	ArrayList<UserContactInfo> data = null;
	
	//----------------------------------------------------------------------------------------------	
	public AdapterContacts(Context context, int layoutViewResourceId, ArrayList<UserContactInfo> data)
	{
		super(context, layoutViewResourceId, data);
		
		this.layoutResourceId = layoutViewResourceId;
		this.context = context;
		this.data = data;
	}
	
	//----------------------------------------------------------------------------------------------
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		TextHolder holder = null;
		
		if (row == null)
		{
			LayoutInflater inflater;
			inflater = LayoutInflater.from(context);
			row = inflater.inflate(layoutResourceId, parent, false);
			
			holder = new TextHolder();
			holder.mImg = (ImageView) row.findViewById(R.id.imguser);
			holder.txtTitle = (TextView)row.findViewById(R.id.username);
			
			row.setTag(holder);
		}
		else
		{
			holder = (TextHolder) row.getTag();
		}
		
		UserContactInfo info = data.get(position);
		
		holder.txtTitle.setText(info.Name);
		holder.mImg.setImageResource(R.drawable.ic_launcher);
		
		return row;
	}
	
	//----------------------------------------------------------------------------------------------
	static class TextHolder
	{
		ImageView mImg;
		TextView  txtTitle;
	}
}