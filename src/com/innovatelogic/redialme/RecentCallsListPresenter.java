package com.innovatelogic.redialme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

import com.innovatelogic.redialme.RecentCallsStore;
import com.innovatelogic.redialme.RecentCallsStore.CallInfo;
import com.innovatelogic.redialme.RecentCallsStore.ECallType;

public class RecentCallsListPresenter
{
	private ListView mList;
	private MainActivity mActivity;
	
	//----------------------------------------------------------------------------------------------
	public RecentCallsListPresenter(MainActivity activity, int resourceId)
	{
		mActivity = activity;
		mList = (ListView) mActivity.findViewById(resourceId);
		
		mList.setOnItemClickListener(new OnItemClickListener() 
    	{
    		@Override
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
    		{
    			List<CallInfo> store = mActivity.GetRecentCallsStore().GetRecentInfoList();
    			
    			if (position >= 0 && position < store.size())
    			{
    				ContactsStore.KeyContactInfo info = mActivity.getContactsStore().GetInfoByNum(store.get(position).mNumber);
    				
    				mActivity.GetPopupWindow().mContactID = (info != null) ? info.mInfo.Id : -1;
    				mActivity.GetPopupWindow().mName = (info != null) ? info.mInfo.Name : "Unknown number";
    				mActivity.GetPopupWindow().AddNumber(store.get(position).mNumber);
    				mActivity.GetPopupWindow().Toggle(true);
    			}
    			else
    			{
    				// log error
    			}
    		}
    	});
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
			String duration = call.mCallDuration == 0 ? "" : MainActivity.FormatSec(call.mCallDuration);
			
			if (queryNum.equals("") || title.contains(queryNum))
			{
				ContactsStore.KeyContactInfo info = mActivity.getContactsStore().GetInfoByNum(call.mNumber);
				if (info != null){
					title = info.mInfo.Name;
				}
				
				int img_resource = (call.mCallType == ECallType.EOutgoing) ? R.drawable.out_call :
									(call.mCallType == ECallType.EIncoming ? R.drawable.inner_call : R.drawable.inner_call_missed);
				
				map.put("title", title);
				map.put("duration", duration);
				map.put("calltime", call.mCallDayTime.toString());
				map.put("img", String.valueOf(img_resource));
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
