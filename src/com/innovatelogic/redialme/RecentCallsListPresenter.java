package com.innovatelogic.redialme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Handler;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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
		FillList(store, "");
	}
	
	//----------------------------------------------------------------------------------------------
	public void FillList(RecentCallsStore store, String queryNum)
	{
		ClearList();
		
		ArrayList <HashMap<String, String>> listItem = new ArrayList <HashMap<String , String>>();
		
		int index = 0;
		List<RecentCallsStore.CallInfo> calls = store.GetRecentInfoList();
		for (RecentCallsStore.CallInfo call : calls )
		{
			HashMap<String, String> map = new HashMap<String, String>();
			
			String title = call.mNumber;
			
			if (queryNum.equals("") || title.contains(queryNum))
			{
				UserContactInfo info =  mActivity.getContactsStore().GetInfoByNum(call.mNumber);
				if (info != null){
					title = info.Name;
				}
				
				map.put("title", title);
				map.put("duration", call.mCallDuration.toString());
				map.put("calltime", call.mCallDayTime.toString());
				map.put("img", String.valueOf(R.drawable.ic_launcher));
				map.put("idx", Integer.toString(index));
				
				listItem.add(map);
			}
			++index;
		}
		
		SimpleAdapter adapter = new SimpleAdapter(mActivity.getBaseContext(),
				listItem,
				R.layout.recentcallsactivity,
				new  String [] { "img" , "title" , "duration", "calltime" },
				new  int [] { R.id.img, R.id.title , R.id.duration, R.id.calltime });
		
		mList.setAdapter(adapter);
	}
	
	//----------------------------------------------------------------------------------------------
	public void ClearList()
	{
		mList.setAdapter(null);
	}
}
