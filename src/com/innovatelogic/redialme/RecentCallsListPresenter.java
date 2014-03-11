package com.innovatelogic.redialme;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.provider.CallLog;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.innovatelogic.redialme.RecentCallsStore;

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
	public void FillList(RecentCallsStore store)
	{
		List<String> RecentCalls = new ArrayList<String>();
		
		List<RecentCallsStore.CallInfo> calls = store.GetRecentInfoList();
		for (RecentCallsStore.CallInfo call : calls )
		{
			RecentCalls.add("\nPhone Number:--- " + call.mNumber +
							" \nCall Date:--- " + call.mCallDayTime +
							" \nCall duration in sec :--- " + call.mCallDuration);
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity, 
				R.layout.recentcallsactivity, 
				RecentCalls);
		
		mList.setAdapter(adapter);
	}
}
