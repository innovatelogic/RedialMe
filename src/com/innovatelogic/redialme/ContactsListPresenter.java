package com.innovatelogic.redialme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.innovatelogic.redialme.RecentCallsStore.CallInfo;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class ContactsListPresenter 
{
	private MainActivity mActivity = null;
	private ListView mList = null;
	
	public ContactsListPresenter(MainActivity activity, int listId)
	{
		mActivity = activity;
		mList = (ListView)mActivity.findViewById(listId);
		
		mList.setOnItemClickListener(new OnItemClickListener() 
    	{
    		@Override
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
    		{
    			String str = parent.getItemAtPosition(position).toString();
    			String idx = MainActivity.GetValueByKey(str, "idx");
    			
    			if (idx != null)
    			{
    				int index = Integer.parseInt(idx);
    				
    				ArrayList<UserContactInfo> contacts = mActivity.getContactsStore().GetContactsStore();
        			
        			if (index >= 0 && index < contacts.size())
        			{
        				mActivity.GetPopupWindow().mName = contacts.get(index).Name;
        				mActivity.GetPopupWindow().mNumber = contacts.get(index).ContactNumber;
        				mActivity.GetPopupWindow().Toggle(true);
        			}
    			}
    		}
    	});
	}
	
	//----------------------------------------------------------------------------------------------
	public void FillList(ContactsStore store)
	{
		FillList(store, "");
	}
	
	//----------------------------------------------------------------------------------------------
	public void FillList(ContactsStore store, String queryNum)
	{
		ClearList();
		
		ArrayList <HashMap<String, String>> listItem = new ArrayList <HashMap<String , String>>();
		
		int index = 0;
		List<UserContactInfo> contacts = store.GetContactsStore();
		
		for (UserContactInfo contact : contacts )
		{
			HashMap<String, String> map = new HashMap<String, String>();
			
			String  name = contact.GetName();
			
			if (queryNum.equals("") || name.toLowerCase().contains(queryNum.toLowerCase()))
			{
				map.put("imguser", String.valueOf(R.drawable.ic_launcher));
				map.put("username", name);
				map.put("idx", Integer.toString(index));
				
				listItem.add(map);
			}
			++index;
		}
		
		SimpleAdapter adapter = new SimpleAdapter(mActivity.getBaseContext(),
				listItem,
				R.layout.activity_contacts,
				new  String [] { "imguser" , "username" },
				new  int [] { R.id.imguser, R.id.username });
		
		mList.setAdapter(adapter);
	}
	
	//----------------------------------------------------------------------------------------------
	public void ClearList()
	{
		mList.setAdapter(null);
	}
}
