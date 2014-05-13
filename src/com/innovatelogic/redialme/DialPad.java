package com.innovatelogic.redialme;

import java.util.List;

import com.innovatelogic.redialme.MainActivity.ESizeType;
import com.innovatelogic.redialme.RecentCallsStore.CallInfo;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
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
	private ImageButton mBtnAction;
	private TextView 	mEditNumber;
	private ListView    mListRecentCallsLite;
	private ImageButton	mBtnBackspace;
	private RecentCallsListPresenter mListPresenter;
	
	//----------------------------------------------------------------------------------------------
	public DialPad(MainActivity activity)
	{
		mActivity = activity;
		mListPresenter = new RecentCallsListPresenter(activity, R.id.listRecentCallsLite);
		
		findAllViewsById(mActivity);
		
		//mListPresenter.FillList(activity.GetRecentCallsStore());
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
    	mBtnBackspace = (ImageButton)activity.findViewById(R.id.BtnBackspace);
    	
    	mBtnAction = (ImageButton)activity.findViewById(R.id.buttonActionDialPad);
    	mBtnAction.setBackgroundResource(R.layout.buttonstyle_action_process);
    	
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
    	
    	// fit up size
    	float txt_size = mActivity.GetDefTextSize(ESizeType.ESizeDef);
    	for (int i = 0; i < esize; ++i){
	    	mDialButtons[i].setTextSize(txt_size);
		}
    	
    	mEditNumber.setTextSize(txt_size);
    	    	
    	mListRecentCallsLite.setOnItemClickListener(new OnItemClickListener() 
    	{
    		@Override
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
    		{
    			String str = parent.getItemAtPosition(position).toString();
    			String idx = MainActivity.GetValueByKey(str, "idx");
    			
    			if (idx != null)
    			{
    				int index = Integer.parseInt(idx);
    				List<CallInfo> calls = mActivity.GetRecentCallsStore().GetRecentInfoList();
    				
    				if (index >= 0 && index < calls.size())
    				{
    					CallInfo info = calls.get(index);
    					ContactsStore.KeyContactInfo contactinfo = mActivity.getContactsStore().GetInfoByNum(info.mNumber);
    					
    					if (contactinfo != null){
    						mActivity.GetPopupWindow().mContactID = contactinfo.mKey;
    					}
    					mActivity.GetPopupWindow().mName = (contactinfo != null) ? contactinfo.mInfo.Name : mActivity.getString(R.string.unknown_number);
        				mActivity.GetPopupWindow().AddNumber(info.mNumber);
        				mActivity.GetPopupWindow().Toggle(true, false);
    				}
    			}
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
    				UpdateRecentList(mEditNumber.getText().toString());
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
	    			UpdateRecentList(mEditNumber.getText().toString());
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
    				ContactsStore.KeyContactInfo info = mActivity.getContactsStore().GetInfoByNum(number);
				
					if (info != null){
						mActivity.GetPopupWindow().mContactID = info.mKey;
					}
    				mActivity.GetPopupWindow().mName = (info != null) ? info.mInfo.Name : mActivity.getString(R.string.unknown_number);
    				mActivity.GetPopupWindow().AddNumber(number);
    				mActivity.GetPopupWindow().Toggle(true, true);
    			}
    			else
    			{
    				Toast.makeText(mActivity.getApplicationContext(), mActivity.getString(R.string.enter_number), Toast.LENGTH_LONG).show();
    			}
    		}
    	});
    }
    
  //----------------------------------------------------------------------------------------------
  public void UpdateRecentList(String selectionQuery)
  {
	  mListPresenter.FillList(mActivity.GetRecentCallsStore(), selectionQuery);
  }
}
