package com.innovatelogic.redialme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.LruCache;

import com.innovatelogic.redialme.UserContactInfo;

//----------------------------------------------------------------------------------------------
//
//----------------------------------------------------------------------------------------------
public class ContactsStore 
{
	public class KeyContactInfo
	{
		public int mKey;
		public UserContactInfo mInfo = null;
		
		KeyContactInfo(int key, UserContactInfo info)
		{
			mKey = key;
			mInfo = info;
		}
	}
	
	private Map<Integer, UserContactInfo> mMapContacts = null;
	private LruCache<Integer, Bitmap> mBitmapCache = null;
	
	//----------------------------------------------------------------------------------------------
	public ContactsStore()
	{
		mMapContacts = new HashMap<Integer, UserContactInfo>();
		
		// Get max available VM memory, exceeding this amount will throw an
	    // OutOfMemory exception. Stored in kilobytes as LruCache takes an
	    // int in its constructor.
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		
		// Use 1/8th of the available memory for this memory cache.
	    final int cacheSize = maxMemory / 8;
	    
	    mBitmapCache = new LruCache<Integer, Bitmap>(cacheSize)
	    {
	        @Override
	        protected int sizeOf(Integer key, Bitmap bitmap) 
	        {
	            // The cache size will be measured in kilobytes rather than
	            // number of items.
	            return bitmap.getByteCount() / 1024;
	       }
	    };
	}
	
	public Map<Integer, UserContactInfo> GetContactsStoreMap() { return mMapContacts; }
	
	//----------------------------------------------------------------------------------------------
	public void AddBitmapToCache(Integer key, Bitmap bitmap)
	{
		if (GetBitmapFromCache(key) == null){			
			mBitmapCache.put(key, bitmap);
		}
	}
	
	//----------------------------------------------------------------------------------------------
	public Bitmap GetBitmapFromCache(Integer key)
	{
		return mBitmapCache.get(key);
	}
	
	//----------------------------------------------------------------------------------------------
	public void LoadContacts(Context context)
	{
		Log.d("Start", "load contact list");
		
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
		  
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		
		Cursor cursor = context.getContentResolver().query(uri,	new String[] 
				{ ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
				  ContactsContract.CommonDataKinds.Phone.NUMBER,
				  ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				  ContactsContract.CommonDataKinds.Photo.PHOTO_ID,
				  ContactsContract.CommonDataKinds.Phone._ID},
				  null, null, sortOrder);
		
		try
		{
			cursor.moveToFirst();
			
			while (!cursor.isAfterLast())
			{
				int contactID = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
				String contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
				int thumbnailId = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_ID));
				
				String NumberNorm = ContactsStore.NormalizeNumber(contactNumber, "+380");
				
				// new flow
				UserContactInfo findInfo = mMapContacts.get(contactID);
				if (findInfo == null)
				{
					UserContactInfo userInfo = new UserContactInfo();
					userInfo.ContactNumbers = new ArrayList<String>();
					userInfo.ContactNumbers.add(NumberNorm);
					userInfo.Name = contactName;
					userInfo.thumbnailID = thumbnailId;
					
					mMapContacts.put(contactID, userInfo);
				}
				else
				{
					findInfo.ContactNumbers.add(NumberNorm);
				}
				
				cursor.moveToNext();
			}
		}
		finally
		{	
			cursor.close();
			cursor = null;
		}
		
		Log.d("End", "load contact list");
	}
	
	//----------------------------------------------------------------------------------------------
	public KeyContactInfo GetInfoByNum(String number)
	{
		for (Map.Entry<Integer, UserContactInfo> entry : mMapContacts.entrySet())
		{
			for (String v : entry.getValue().ContactNumbers)
			{
				if (number.equals(v))
					return new KeyContactInfo(entry.getKey(), entry.getValue());
			}
		}
		return null;
	}
	
	//----------------------------------------------------------------------------------------------
	public static String NormalizeNumber(String number, String terrCode)
	{
    	String PROVIDER = "";
    	String ABONENT = "";
    	
    	number = number.replace(" ", "");
    	number = number.replace("-", "");
    	    	
    	int length = number.length();
		if (length >= 10 && length <= 13)
		{
			ABONENT = number.substring(length - 7);
			
			number = number.substring(0, number.length() - 7);
			
			if (number.length() >= 3 && number.length() <= 6)
			{
				PROVIDER = number.substring(number.length() - 2);
				
				return terrCode + PROVIDER + ABONENT;
			}
		}
		return number;
	}
}