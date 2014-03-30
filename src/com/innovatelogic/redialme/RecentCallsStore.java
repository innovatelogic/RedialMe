package com.innovatelogic.redialme;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import android.database.Cursor;
import android.provider.CallLog;

//----------------------------------------------------------------------------------------------
//
//----------------------------------------------------------------------------------------------
public class RecentCallsStore 
{
	enum ECallType
	{
		EOutgoing,
		EIncoming,
		EMissed,
	}

	public class CallInfo
	{
		String 		mNumber;
		ECallType 	mCallType;
		int 		mCallDuration;
		Date 		mCallDayTime;
		
		public CallInfo()
		{
		}
	}
	
	public static int MaxNumberCalls = 100; 
	
	private List<CallInfo> mListRecentCalls;
	
	private MainActivity mActivity;
	
	public List<CallInfo> GetRecentInfoList() { return mListRecentCalls; }
	
	//----------------------------------------------------------------------------------------------
	public RecentCallsStore(MainActivity activity)
	{
		mActivity = activity;
		mListRecentCalls = new ArrayList<CallInfo>();
	}
	
	//----------------------------------------------------------------------------------------------
	public void Initialize()
	{
		Cursor managedCursor = 
				mActivity.getApplicationContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, 
						null, null, null, CallLog.Calls.DATE + " DESC");

		int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
		int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
		int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
		int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
				
		int counter = 0;
		
		while (managedCursor.moveToNext() && counter < MaxNumberCalls) 
		{
			String phNumber = managedCursor.getString(number);
			String callType = managedCursor.getString(type);
			String callDate = managedCursor.getString(date);
			
			int dircode = Integer.parseInt(callType);
			
			ECallType calltype = ECallType.EOutgoing;
			
			switch (dircode)
			{
			case CallLog.Calls.INCOMING_TYPE:
				calltype = ECallType.EIncoming;
			break;
			
			case CallLog.Calls.MISSED_TYPE:
				calltype = ECallType.EMissed;
			break;
			}
			
			String NormNumber = ContactsStore.NormalizeNumber(phNumber, "+380");
			
			CallInfo info = new CallInfo();
			
			info.mNumber = NormNumber;
			info.mCallType = calltype;
			info.mCallDayTime = new Date(Long.valueOf(callDate));
			info.mCallDuration = managedCursor.getInt(duration);
			
			mListRecentCalls.add(info);
			
			counter++;
		}
	}
}
