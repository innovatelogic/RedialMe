package com.innovatelogic.redialme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import com.innovatelogic.redialme.MainActivity;

public class ActionPopupWindow 
{
	private MainActivity mActivity;
	
	private PopupWindow mPopupWindow = null;
	
	//----------------------------------------------------------------------------------------------
	public ActionPopupWindow(MainActivity activity)
	{
		mActivity = activity;
	}
	
	//----------------------------------------------------------------------------------------------
	public void Toggle(boolean bFlag)
	{
		if (bFlag && mPopupWindow == null)
		{
			LayoutInflater layoutInflater = 
					(LayoutInflater) mActivity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    
	    	View popupView = layoutInflater.inflate(R.layout.activity_popupaction, null);
	    	
	    	//mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	    	mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	    
	    	Button btnCancel = (Button)popupView.findViewById(R.id.cancel);
	    	
	    	btnCancel.setOnClickListener(new Button.OnClickListener()
	    	{
	    		@Override
		    	public void onClick(View v)
		    	{
		    		Toggle(false);
		    	}
	    	});
	    	
	    	//View view = getCurrentFocus();
	    	
	    	//FrameLayout content = (FrameLayout) findViewById(android.R.id.content);
	    	
	    	ViewGroup decor = (ViewGroup) mActivity.getWindow().getDecorView().findViewById(android.R.id.content);
	    	View root = (ViewGroup) decor.getChildAt(0);
	    	
	    	//View root = getWindow().getDecorView().getRootView();
	    	
	    	//ViewGroup vgroup = (ViewGroup)root.getParent();
	    	    	
	    	mPopupWindow.showAsDropDown(root, 100, -600);//(decor, Gravity.LEFT | Gravity.TOP, 0, 0);
		}
		else if (!bFlag && mPopupWindow != null)
		{
			mPopupWindow.dismiss();
			mPopupWindow = null;
		}
	}
}
