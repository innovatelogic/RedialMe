package com.innovatelogic.redialme;

import java.util.ArrayList;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

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
		    
	    	final View popupView = layoutInflater.inflate(R.layout.activity_settings, null);
	    	
	    	mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	    	
	    	mPopupWindow.setFocusable(false);
	    	mPopupWindow.setTouchable(true); 
	    	mPopupWindow.setOutsideTouchable(true);	
	     	
	    	//ViewGroup decor = (ViewGroup) mActivity.getWindow().getDecorView().findViewById(android.R.id.content);
	    	//View root = (ViewGroup) decor.getChildAt(0);
	    	//mPopupWindow.showAtLocation(root, Gravity.CENTER, 0, 0);
	    	
	    	Button btnConfigure = (Button)popupView.findViewById(R.id.buttonOptionsConfigure);
	    	
	    	btnConfigure.setOnClickListener(new Button.OnClickListener()
	    	{
	    		@Override
		    	public void onClick(View v)
		    	{
	    			final CharSequence[] items={"Ukraine", "Uganda", "Germany", "USA", "France", "Marocco", "UK", "Georgia", "Mexico", "Hungary", "Poland"};
	    			AlertDialog.Builder builder3 = new AlertDialog.Builder(mActivity);
	    			
	    			builder3.setTitle("Pick your choice").setItems(items, new DialogInterface.OnClickListener() 
	    			{
		    			@Override
		    			public void onClick(DialogInterface dialog, int which) {
		    				// TODO Auto-generated method stub
		    				Toast.makeText(mActivity.getApplicationContext(), "U clicked "+items[which], Toast.LENGTH_LONG).show();
		    			}
	    			});
	    			builder3.show();
	    			
	 /*   			LayoutInflater layoutInflater = (LayoutInflater) mActivity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    		    
	    	    	final View popupView = layoutInflater.inflate(R.layout.popupspinner, null);
	    	    	
	    	    	PopupWindow mPopupWindowSpinner = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	    	    	
	    	    	mPopupWindowSpinner.setFocusable(false);
	    	    	mPopupWindowSpinner.setTouchable(true); 
	    	    	mPopupWindowSpinner.setOutsideTouchable(true);	
	    	    	
	    	    	ViewGroup decor = (ViewGroup) mActivity.getWindow().getDecorView().findViewById(android.R.id.content);
	    	    	View root = (ViewGroup) decor.getChildAt(0);
	    	    	mPopupWindowSpinner.showAtLocation(root, Gravity.CENTER, 0, 0);
	    	    	
	    			//mActivity.setContentView(R.layout.popupspinner);
	    			
	    			ArrayAdapter myAdapter = new ArrayAdapter(mActivity, android.R.layout.simple_spinner_item,
	    														new String[]{"one","two","three","four","five"});
	    			
	    			Spinner mySpinner = (Spinner) popupView.findViewById(R.id.myspinner);
	    			mySpinner.setAdapter(myAdapter);
	    			*/
    			
	    		   /* LinearLayout layout = new LinearLayout(mActivity);

	    		    ArrayList<String> spinnerArray = new ArrayList<String>();
	    		    
	    		    spinnerArray.add("one");
	    		    spinnerArray.add("two");
	    		    spinnerArray.add("three");
	    		    spinnerArray.add("four");
	    		    spinnerArray.add("five");

	    		    Spinner spinner = new Spinner(popupView.getContext());
	    		    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(popupView.getContext(), android.R.layout.simple_spinner_dropdown_item, spinnerArray);
	    		    spinner.setAdapter(spinnerArrayAdapter);

	    		    layout.addView(spinner);

	    		    mPopupWindow.setContentView(layout);*/
	    			
	    			/*LinearLayout layout = new LinearLayout(MainActivity.getAppContext());

	    		    ArrayList<String> spinnerArray = new ArrayList<String>();
	    		    spinnerArray.add("one");
	    		    spinnerArray.add("two");
	    		    spinnerArray.add("three");
	    		    spinnerArray.add("four");
	    		    spinnerArray.add("five");

	    		    Spinner spinner = new Spinner(MainActivity.getAppContext());
	    		    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(MainActivity.getAppContext(), android.R.layout.simple_spinner_dropdown_item, spinnerArray);
	    		    spinner.setAdapter(spinnerArrayAdapter);

	    		    layout.addView(spinner);

	    		    mActivity.setContentView(layout);*/
		    	}
	    	});
	    	
	    	popupView.post(new Runnable() 
	    	{
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
