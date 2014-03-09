package com.innovatelogic.redialme;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.provider.CallLog;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class RecentCallsListPresenter
{
	private ListView mList;
	private MainActivity mActivity;
	
	//----------------------------------------------------------------------------------------------
	public RecentCallsListPresenter(MainActivity activity, int resourceId)
	{
		mActivity = activity;
		mList = (ListView) mActivity.findViewById(resourceId);
	}
	
	//----------------------------------------------------------------------------------------------
	public void FillList()
    {
		List<String> RecentCalls = new ArrayList<String>();
			
		StringBuffer sb = new StringBuffer();
		Cursor managedCursor = mActivity.getApplicationContext().getContentResolver().query(CallLog.Calls.CONTENT_URI,
								null, null, null, CallLog.Calls.DATE + " DESC");
		   
		int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
		int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
		int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
		int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
		int id = managedCursor.getColumnIndex(CallLog.Calls._ID);
		
		sb.append("Call Details :");
		
		while (managedCursor.moveToNext()) 
		{
			String phNumber = managedCursor.getString(number);
			String callType = managedCursor.getString(type);
			String callDate = managedCursor.getString(date);
			
			Date callDayTime = new Date(Long.valueOf(callDate));
			String callDuration = managedCursor.getString(duration);
			String dir = null;
			int dircode = Integer.parseInt(callType);
			
			switch (dircode) {
			case CallLog.Calls.OUTGOING_TYPE:
				dir = "OUTGOING";
			break;
			
			case CallLog.Calls.INCOMING_TYPE:
				dir = "INCOMING";
			break;
			
			case CallLog.Calls.MISSED_TYPE:
				dir = "MISSED";
			break;
		}
			
		String NormNumber = ContactsStore.NormalizeNumber(phNumber, "+380");
			
		UserContactInfo info = mActivity.getContactsStore().GetInfoByNum(NormNumber);
		
		if (info != null)
		{
			NormNumber = info.GetName();
		}
			
		RecentCalls.add("\nPhone Number:--- " + NormNumber + " \nCall Type:--- "
					+ dir + " \nCall Date:--- " + callDayTime
					+ " \nCall duration in sec :--- " + callDuration +
					"\nID" + id);
		}
		
		managedCursor.close();
	    	
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity, 
				R.layout.recentcallsactivity, 
				RecentCalls);
		
		mList.setAdapter(adapter);
    }
}
