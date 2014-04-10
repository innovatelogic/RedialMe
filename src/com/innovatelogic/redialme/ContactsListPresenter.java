package com.innovatelogic.redialme;

import java.util.ArrayList;
import java.util.Map;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

//----------------------------------------------------------------------------------------------
//
//----------------------------------------------------------------------------------------------
public class ContactsListPresenter 
{
	private class OrderAdapter extends ArrayAdapter<ArrayList<String>> 
	{
		ArrayList<ArrayList<String>> mListItem;
		
	    public OrderAdapter(Context context, int textViewResourceId, ArrayList<ArrayList<String>> items) 
	    {
	    	super(context, textViewResourceId, items);
	    	this.mListItem = items;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) 
	    {
            View v = convertView;
            if (v == null) 
            {
                LayoutInflater vi = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.activity_contacts, null);
            }
            
            ArrayList<String> info = mListItem.get(position);
            if (info != null) 
            {
            	ImageView imageuser = (ImageView) v.findViewById(R.id.imguser);
                TextView username = (TextView) v.findViewById(R.id.username);
                
            	int contactID = Integer.parseInt(info.get(0));
            	String name = (String)info.get(1);
            	
            	username.setText(name);

            	Map<Integer, UserContactInfo> contacts = mActivity.getContactsStore().GetContactsStoreMap();
            	boolean bDefault = true;
            	
            	UserContactInfo findInfo = contacts.get(contactID);
				if (findInfo != null)
				{
					int thumbnailID = findInfo.thumbnailID;
					if (thumbnailID > 0)
					{
						Bitmap bitmap = MainActivity.fetchThumbnail(thumbnailID, mActivity.getApplicationContext()); 
						if (bitmap != null)
						{
							imageuser.setImageBitmap(bitmap);
							bDefault = false;
						}
					}
				}
            	
            	if (bDefault){
            		imageuser.setImageResource(R.drawable.default_person);
				}
            }
            return v;
	    }
	}
	
	private MainActivity mActivity = null;
	private ListView mList = null;
	
	//----------------------------------------------------------------------------------------------
	static public ArrayList<String> ParseToList(String str)
	{
		ArrayList<String> out = new ArrayList<String>();
		
		str = str.replace("[", "");
		str = str.replace("]", "");
		
		String delims = "[,]+";
		String [] tokens = str.split(delims);
		
		for (String token : tokens)
		{
			out.add(token);
		}
		return out;
	}
	
	//----------------------------------------------------------------------------------------------
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

    			ArrayList<String> list = ParseToList(str);
    			
    			if (list.size() >= 2)
    			{
    				int contactId = Integer.parseInt(list.get(0));
    	
    				Map<Integer, UserContactInfo> mapContacts = mActivity.getContactsStore().GetContactsStoreMap();
    				
    				UserContactInfo findInfo = mapContacts.get(contactId);
    				
    				if (findInfo != null)
    				{
    					mActivity.GetPopupWindow().mContactID = contactId;
    					mActivity.GetPopupWindow().mName = findInfo.Name;
        				
        				for (String number : findInfo.ContactNumbers){
        					mActivity.GetPopupWindow().AddNumber(number);
        				}
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
		
		ArrayList <ArrayList<String>> listItems = new ArrayList <ArrayList<String>>();
		
		Map<Integer, UserContactInfo> mapContacts = store.GetContactsStoreMap();
		
		for (Map.Entry<Integer, UserContactInfo> entry : mapContacts.entrySet())
		{
			ArrayList<String> map = new ArrayList<String>();
			String  name = entry.getValue().GetName();
			
			if (queryNum.equals("") || name.toLowerCase().contains(queryNum.toLowerCase()))
			{
				map.add(Integer.toString(entry.getKey()));
				map.add(name);
				
				listItems.add(map);
			}
		}
		
		ArrayAdapter<ArrayList<String>> adapter = new OrderAdapter(mActivity.getBaseContext(), R.layout.activity_contacts, listItems);
		
		mList.setAdapter(adapter);
	}
	
	//----------------------------------------------------------------------------------------------
	public void ClearList()
	{
		mList.setAdapter(null);
	}
}
