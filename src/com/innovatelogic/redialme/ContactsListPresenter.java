package com.innovatelogic.redialme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;


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
                
            	int idxInContacts = Integer.parseInt(info.get(0));
            	String name = (String)info.get(1);
            	
            	username.setText(name);

            	List<UserContactInfo> contacts = mActivity.getContactsStore().GetContactsStore();
            	
            	boolean bDefault = true;
            	if (idxInContacts >= 0 && idxInContacts < contacts.size())
            	{
					if (contacts.get(idxInContacts).thumbnailID > 0)
					{
						 Bitmap bitmap = fetchThumbnail(contacts.get(idxInContacts).thumbnailID, mActivity.getApplicationContext());
						 
						 if (bitmap != null){
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
    				int index = Integer.parseInt(list.get(0));
    				
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
		
		ArrayList <ArrayList<String>> listItems = new ArrayList <ArrayList<String>>();
		
		int index = 0;
		List<UserContactInfo> contacts = store.GetContactsStore();
		
		for (UserContactInfo contact : contacts )
		{
			ArrayList<String> map = new ArrayList<String>();
			
			String  name = contact.GetName();
			
			if (queryNum.equals("") || name.toLowerCase().contains(queryNum.toLowerCase()))
			{
				//map.add(String.valueOf(R.drawable.ic_launcher));
				map.add(Integer.toString(index));
				map.add(name);
				
				listItems.add(map);
			}
			++index;
		}
		
		ArrayAdapter<ArrayList<String>> adapter = new OrderAdapter(mActivity.getBaseContext(), R.layout.activity_contacts, listItems);
		
		mList.setAdapter(adapter);
	}
	
	//----------------------------------------------------------------------------------------------
	public void ClearList()
	{
		mList.setAdapter(null);
	}
	
	//----------------------------------------------------------------------------------------------
	final Bitmap fetchThumbnail(final int thumbnailId, Context context) 
	{
	    final Uri uri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, thumbnailId);
	    final Cursor cursor = context.getContentResolver().query(uri, new String[] {
	    	    ContactsContract.CommonDataKinds.Photo.PHOTO}, null, null, null);

	    try {
	        Bitmap thumbnail = null;
	        if (cursor.moveToFirst()) {
	            final byte[] thumbnailBytes = cursor.getBlob(0);
	            if (thumbnailBytes != null) {
	                thumbnail = BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.length);
	            }
	        }
	        return thumbnail;
	    }
	    finally {
	        cursor.close();
	    }
	}
}
