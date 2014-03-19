package com.innovatelogic.redialme;

import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import com.innovatelogic.redialme.MainActivity;

//----------------------------------------------------------------------------------------------
//
//----------------------------------------------------------------------------------------------
/*private class MyAdListener implements AdListener {

    @Override
    public void onFailedToReceiveAd(Ad ad, ErrorCode errorCode) {
       // mAdStatus.setText(R.string.error_receive_ad);
    }

    @Override
    public void onReceiveAd(Ad ad) {
        //mAdStatus.setText("");
    }
}*/

//----------------------------------------------------------------------------------------------
//
//----------------------------------------------------------------------------------------------
public class ActionPopupWindow 
{
	private MainActivity mActivity;
	
	public String mName;
	public String mNumber;
	public Button mBtnAction;
	
	private PopupWindow mPopupWindow = null;
	
	//----------------------------------------------------------------------------------------------
	public ActionPopupWindow(MainActivity activity)
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
			LayoutInflater layoutInflater = 
					(LayoutInflater) mActivity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    
	    	View popupView = layoutInflater.inflate(R.layout.activity_popupaction, null);
	    	
	    	mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	    	    	
	    	Button btnCancel = (Button)popupView.findViewById(R.id.processActionPopUp);
	    	TextView txtName = (TextView)popupView.findViewById(R.id.popupname);
	    	TextView txtNumber = (TextView)popupView.findViewById(R.id.popupnumber);
	    
 	 /*	   	
	    	AdView mAdView = (AdView) popupView.findViewById(R.id.adView);
	    	
      		mAdView.setAdListener(new MyAdListener());

       	 	AdRequest adRequest = new AdRequest();
       	    adRequest.addKeyword("sporting goods");
       	    mAdView.loadAd(adRequest);*/

       	    
	    	txtName.setText(mName);
	    	txtNumber.setText(mNumber);
	    	
	    	btnCancel.setOnClickListener(new Button.OnClickListener()
	    	{
	    		@Override
		    	public void onClick(View v)
		    	{
	    			Process();
		    	}
	    	});
	     	
	    	ViewGroup decor = (ViewGroup) mActivity.getWindow().getDecorView().findViewById(android.R.id.content);
	    	View root = (ViewGroup) decor.getChildAt(0);
	     	
	    	mPopupWindow.showAtLocation(root, Gravity.CENTER, 0, 0); 
	    	
		}
		else if (!bFlag && mPopupWindow != null)
		{
			mPopupWindow.dismiss();
			mPopupWindow = null;
		}
	}
	
	//----------------------------------------------------------------------------------------------
	void Process()
	{
		String operator = mActivity.GetCurrentOperator();
		ProviderEntry provider = mActivity.GetCurrentTerritory().GetProvider(operator);
		
		mActivity.GetActionBar().ProcessAction(provider, mNumber);
		
		Toggle(false);
	}
}
