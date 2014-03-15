package com.innovatelogic.redialme;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.innovatelogic.redialme.MainActivity;

public class ActionPopupWindow 
{
	private MainActivity mActivity;
	
	public String mName;
	public String mNumber;
	
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
	    	
	    	mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	    
	    	Button btnCancel = (Button)popupView.findViewById(R.id.process);
	    	TextView txtName = (TextView)popupView.findViewById(R.id.popupname);
	    	TextView txtNumber = (TextView)popupView.findViewById(R.id.popupnumber);
	    	
	    	txtName.setText(mName);
	    	txtNumber.setText(mNumber);
	    	
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
	    	    	
	    	//mPopupWindow.showAtLocation(root, Gravity.TOP | Gravity.RIGHT, 0, 0);
	    	
	    	mPopupWindow.showAtLocation(root/*mActivity.findViewById(android.R.id.main)*/, Gravity.CENTER, 0, 0); 
	    	
		}
		else if (!bFlag && mPopupWindow != null)
		{
			mPopupWindow.dismiss();
			mPopupWindow = null;
		}
	}
}
