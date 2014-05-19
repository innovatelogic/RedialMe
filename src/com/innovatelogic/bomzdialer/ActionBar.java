package com.innovatelogic.bomzdialer;
/*package com.innovatelogic.redialme;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

//----------------------------------------------------------------------------------------------
//
//----------------------------------------------------------------------------------------------
public class ActionBar 
{
	private class MaskParser
	{
		public final String mTERR;
		public final String mPROVIDER;
		public final String mABONENT;
		
		public MaskParser(String terr, String provider, String abonent, boolean found)
		{
			mTERR = terr;
			mPROVIDER = provider;
			mABONENT = abonent;
		}
	}
	
	private class ActionInfo
	{
		public Button mBtn;
		public IUserOperation mOperation;
		
		public ActionInfo(IUserOperation op, Button btn)
		{
			mOperation = op;
			mBtn = btn;
		}
	}
	
	private MainActivity mActivity;
	private LinearLayout mLayout;
	private LayoutParams mLayoutParams;
	private List<ActionInfo> mActions;
		
	//----------------------------------------------------------------------------------------------
	public ActionBar(MainActivity activity, int layoutID)
	{
		mActions = new ArrayList<ActionInfo>();
		
		mActivity = activity;
		mLayout = (LinearLayout)activity.findViewById(layoutID);
		//mLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	}
	
	//----------------------------------------------------------------------------------------------
	public Button AddButton()
	{
		Button btn = new Button(mActivity);
		
		//mLayout.addView(btn, mLayoutParams);
		return btn;
	}
	
	//----------------------------------------------------------------------------------------------
	public void ApplyActionBar(ProviderEntry provider)
	{
		List<IUserOperation> operations = provider.GetOperationList();
		
		for (IUserOperation op : operations)
		{
			Button btn = AddButton();
			
			btn.setOnClickListener(new OnClickListener() 
	    	{
	    		@Override
	    		public void onClick(View v) 
	    		{
	    			ActionInfo action = GetActionByControl((Button)v);
	    			if (action != null && action.mOperation != null)
	    			{
	    				String number = mActivity.GetCurrentNumber();
	    				MaskParser info = ParseNumber(number);
	    				
	    				String mask = action.mOperation.GetMask();
    					
    					mask = mask.replace("%TERR%", info.mTERR);
						mask = mask.replace("%PRV%", info.mPROVIDER);
						mask = mask.replace("%NUM%", info.mABONENT);
						
						action.mOperation.Process(mask);
	    			}
	    			else
	    			{
	    				// TODO: LOG Warning
	    			}
	    		}
	    	});
			
			ActionInfo info = new ActionInfo(op, btn);
			
			mActions.add(info);
		}
	}
	
    //----------------------------------------------------------------------------------------------
    ActionInfo GetActionByControl(Button view)
    {
    	for (ActionInfo info : mActions)
    	{
    		if (info.mBtn == view)
    			return info;
    	}
    	return null;
    }
    
    //----------------------------------------------------------------------------------------------
    private MaskParser ParseNumber(String number)
    {
    	boolean bFound = false;
    	
    	String TERR = mActivity.GetCurrentTerritory().GetCode();
    	String PROVIDER = "";
    	String ABONENT = "";
    	
    	int length = number.length();
		if (length >= 10 && length <= 13)
		{
			ABONENT = number.substring(length - 7);
			
			number = number.substring(0, number.length() - 7);
			
			if (number.length() >= 3 && number.length() <= 6)
			{
				PROVIDER = number.substring(number.length() - 2);
				bFound = true;
			}
		}
    	return new MaskParser(TERR, PROVIDER, ABONENT, bFound);
    }
    
    //----------------------------------------------------------------------------------------------
    public void ProcessAction(ProviderEntry provider, String number)
    {
    	List<IUserOperation> operations = provider.GetOperationList();
		
		for (IUserOperation op : operations)
		{
			if (op instanceof UserOperationSMS || op instanceof UserOperationCall)
			{
				MaskParser info = ParseNumber(number);
				
				String mask = op.GetMask();
				
				mask = mask.replace("%TERR%", info.mTERR);
				mask = mask.replace("%PRV%", info.mPROVIDER);
				mask = mask.replace("%NUM%", info.mABONENT);
				
				op.Process(mask);
				break;
			}
		}
    }
}*/
