package com.innovatelogic.redialme;

import android.app.ActionBar.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

public class ActionBar 
{
	private MainActivity mActivity;
	private LinearLayout mLayout;
	private LayoutParams mLayoutParams;
		
	//----------------------------------------------------------------------------------------------
	public ActionBar(MainActivity activity, int layoutID)
	{
		mActivity = activity;
		mLayout = (LinearLayout)activity.findViewById(layoutID);
		mLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	}
	
	//----------------------------------------------------------------------------------------------
	public void AddButton()
	{
		Button btn = new Button(mActivity);
		btn.setHeight(5);
        btn.setWidth(5);
        
		mLayout.addView(btn, mLayoutParams);
	}
	
	//----------------------------------------------------------------------------------------------
	public void ApplyActionBar(ProviderEntry provider)
	{
		AddButton();
		AddButton();
		AddButton();
	}
}
