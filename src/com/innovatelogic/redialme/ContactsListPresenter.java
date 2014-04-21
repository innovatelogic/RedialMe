package com.innovatelogic.redialme;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Map;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
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
	private class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap>
	{
		private final ContactsListPresenter    mPresenter;
		private final WeakReference<ImageView> mImageViewReference;
		
	    public BitmapWorkerTask(ContactsListPresenter presenter, ImageView imageView) 
	    {
	    	mPresenter = presenter;
	        // Use a WeakReference to ensure the ImageView can be garbage collected
	    	mImageViewReference = new WeakReference<ImageView>(imageView);
	    }
		    
		@Override
	    protected Bitmap doInBackground(Integer... params) 
		{
	    	final Bitmap bitmap = MainActivity.fetchThumbnail(params[0], mPresenter.mActivity.getApplicationContext());
	    	mPresenter.mActivity.getContactsStore().AddBitmapToCache(params[0], bitmap);
	        return bitmap;
	    }
		
		@Override
		protected void onPostExecute(Bitmap result)
		{
			super.onPostExecute(result);
			
			ImageView image = mImageViewReference.get();
			if (image != null){
				image.setImageBitmap(result);
			}
		}
	};
	
	//----------------------------------------------------------------------------------------------
	private class OrderAdapter extends ArrayAdapter<ArrayList<String>> 
	{
		private ContactsListPresenter mPresenter = null;
		ArrayList<ArrayList<String>> mListItem;
				
	    public OrderAdapter(ContactsListPresenter presenter, Context context, int textViewResourceId, ArrayList<ArrayList<String>> items) 
	    {
	    	super(context, textViewResourceId, items);
	    	
	    	mPresenter = presenter;
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
						final Bitmap bitmap = mActivity.getContactsStore().GetBitmapFromCache(thumbnailID);
						if (bitmap != null)
						{
							imageuser.setImageBitmap(bitmap);
							bDefault = false;
						}
						else
						{
							BitmapWorkerTask task = new BitmapWorkerTask(mPresenter, imageuser);
							task.execute(thumbnailID);
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
		
		ArrayList<UserContactInfo> listContacts = store.GetUserContactsSorted();
		
		for (UserContactInfo entry : listContacts)
		{
			ArrayList<String> map = new ArrayList<String>();
			String name = entry.GetName();
			String low_name = name.toLowerCase(); 
					
			if (queryNum.equals("") || low_name.contains(queryNum.toLowerCase()))
			{
				map.add(Integer.toString(entry.ContactID));
				map.add(name);
				
				listItems.add(map);
			}
		}
		
		ArrayAdapter<ArrayList<String>> adapter = new OrderAdapter(this, mActivity.getBaseContext(), R.layout.activity_contacts, listItems);
		
		mList.setAdapter(adapter);
	}
	
	//----------------------------------------------------------------------------------------------
	public void ClearList()
	{
		mList.setAdapter(null);
	}
}
