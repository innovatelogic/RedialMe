package com.innovatelogic.redialme;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

public class SettingsPopupWindow 
{
	private MainActivity mActivity = null;
	private PopupWindow mPopupWindow = null;

	//----------------------------------------------------------------------------------------------
	SettingsPopupWindow(MainActivity activity)
	{
		mActivity = activity;
	}

	//----------------------------------------------------------------------------------------------
	public boolean IsVisible() { return mPopupWindow != null; }

	//----------------------------------------------------------------------------------------------
	public void Toggle(boolean bFlag)
	{
		if (bFlag && mPopupWindow == null)
		{
			LayoutInflater layoutInflater = (LayoutInflater) mActivity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    
	    	View popupView = layoutInflater.inflate(R.layout.activity_settings, null);
	    	
	    	mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	    	
	    	mPopupWindow.setFocusable(false);
	    	mPopupWindow.setTouchable(true); 
	    	mPopupWindow.setOutsideTouchable(true);	
	     	
	    	//mPopupWindow.showAtLocation(root, Gravity.CENTER, 0, 0);
	    	
	    	popupView.post(new Runnable() {
	            public void run() 
	            {
	            	ViewGroup decor = (ViewGroup) mActivity.getWindow().getDecorView().findViewById(android.R.id.content);
	    	    	View root = (ViewGroup) decor.getChildAt(0);
	    	    	
	            	mPopupWindow.showAtLocation(root, Gravity.CENTER, 0, 0);
	            }
	        });
		}
		else if (!bFlag && mPopupWindow != null)
		{
			mPopupWindow.dismiss();
			mPopupWindow = null;		
		}
	}
}
