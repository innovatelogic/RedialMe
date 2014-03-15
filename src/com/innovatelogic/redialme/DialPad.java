package com.innovatelogic.redialme;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

enum EDialButtons
{
	Btn_ONE,
	Btn_TWO,
	Btn_THREE,
	Btn_FOUR,
	Btn_FIVE,
	Btn_SIX,
	Btn_SEVEN,
	Btn_EIGHT,
	Btn_NINE,
	Btn_ZERO,
}

//----------------------------------------------------------------------------------------------
//
//----------------------------------------------------------------------------------------------
public class DialPad 
{
	private static final int esize = EDialButtons.values().length;
	
	private MainActivity mActivity;
	private Button[] 	mDialButtons = new Button[esize];
	private Button 		mBtnAction;
	private TextView 	mEditNumber;
	private ListView    mListRecentCallsLite;
	private Button		mBtnBackspace;
	private RecentCallsListPresenter mListPresenter;
	
	//----------------------------------------------------------------------------------------------
	public DialPad(MainActivity activity)
	{
		mActivity = activity;
		mListPresenter = new RecentCallsListPresenter(activity, R.id.listRecentCallsLite);
		mListPresenter.FillList(activity.GetRecentCallsStore());
	}
	
	//----------------------------------------------------------------------------------------------
	public String GetNumber()
	{
		return mEditNumber.getText().toString();
	}
	
	//----------------------------------------------------------------------------------------------
    public void findAllViewsById(Activity activity)
    {
    	mListRecentCallsLite = (ListView) activity.findViewById(R.id.listRecentCallsLite);
    	mEditNumber = (TextView)activity.findViewById(R.id.editNumber);
    	mBtnBackspace = (Button)activity.findViewById(R.id.BtnBackspace);
    	
    	mBtnAction = (Button)activity.findViewById(R.id.buttonActionDialPad);
    	
    	mDialButtons[0] = (Button)activity.findViewById(R.id.Btn_ONE);
    	mDialButtons[1] = (Button)activity.findViewById(R.id.Btn_TWO);
    	mDialButtons[2] = (Button)activity.findViewById(R.id.Btn_THREE);
    	
    	mDialButtons[3] = (Button)activity.findViewById(R.id.Btn_FORE);
    	mDialButtons[4] = (Button)activity.findViewById(R.id.Btn_FIVE);
    	mDialButtons[5] = (Button)activity.findViewById(R.id.Btn_SIX);
    	
    	mDialButtons[6] = (Button)activity.findViewById(R.id.Btn_SEVEN);
    	mDialButtons[7] = (Button)activity.findViewById(R.id.Btn_EIGHT);
    	mDialButtons[8] = (Button)activity.findViewById(R.id.Btn_NINE);
    	
    	mDialButtons[9] = (Button)activity.findViewById(R.id.Btn_ZERO);
    	    	
    	mListRecentCallsLite.setOnItemClickListener(new OnItemClickListener() 
    	{
    		@Override
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
    		{ 
    			mActivity.GetTabView().setCurrentTab(1);
    		}
    	});
    	
    	mBtnBackspace.setOnClickListener(new OnClickListener()
    	{
    		@Override
    		public void onClick(View v)
    		{
    			String str = mEditNumber.getText().toString();
    			if (str.length() > 0)
    			{
    				mEditNumber.setText(str.substring(0, str.length() - 1));
    			}
    		}
    	});
    	
    	for (int i = 0; i < esize; ++i)
		{
	    	mDialButtons[i].setOnClickListener(new OnClickListener() 
	    	{
	    		@Override
	    		public void onClick(View v) 
	    		{
	    			if (v == mDialButtons[0])
	    			{
	    				mEditNumber.setText(mEditNumber.getText().toString() + '1');
	    			}
	    			else if (v == mDialButtons[1])
	    			{
	    				mEditNumber.setText(mEditNumber.getText().toString() + '2');
	    			}
	    			else if (v == mDialButtons[2])
	    			{
	    				mEditNumber.setText(mEditNumber.getText().toString() + '3');
	    			}
	    			else if (v == mDialButtons[3])
	    			{
	    				mEditNumber.setText(mEditNumber.getText().toString() + '4');
	    			}
	    			else if (v == mDialButtons[4])
	    			{
	    				mEditNumber.setText(mEditNumber.getText().toString() + '5');
	    			}
	    			else if (v == mDialButtons[5])
	    			{
	    				mEditNumber.setText(mEditNumber.getText().toString() + '6');
	    			}
	    			else if (v == mDialButtons[6])
	    			{
	    				mEditNumber.setText(mEditNumber.getText().toString() + '7');
	    			}
	    			else if (v == mDialButtons[7])
	    			{
	    				mEditNumber.setText(mEditNumber.getText().toString() + '8');
	    			}
	    			else if (v == mDialButtons[8])
	    			{
	    				mEditNumber.setText(mEditNumber.getText().toString() + '9');
	    			}
	    			else if (v == mDialButtons[9])
	    			{
	    				mEditNumber.setText(mEditNumber.getText().toString() + '0');
	    			}
	    		}
	    	});
		}
    	
    	mBtnAction.setOnClickListener(new OnClickListener()
    	{
    		@Override
    		public void onClick(View v)
    		{
    			String number = mEditNumber.getText().toString();
    			
    			if (number.length() > 0)
    			{
    				UserContactInfo info = mActivity.getContactsStore().GetInfoByNum(number);
				
    				mActivity.GetPopupWindow().mName = (info != null) ? info.Name : "Unknown number";
    				mActivity.GetPopupWindow().mNumber = number;
				
    				mActivity.GetPopupWindow().Toggle(true);
    			}
    		}
    	});
    }
}
