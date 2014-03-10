package com.innovatelogic.redialme;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.innovatelogic.redialme.UserContactInfo;
import com.innovatelogic.redialme.AdapterContacts;

//----------------------------------------------------------------------------------------------
//
//----------------------------------------------------------------------------------------------
public class ContactsStore 
{
	private ArrayList<UserContactInfo> Contacts = null;
	
	//----------------------------------------------------------------------------------------------
	public ContactsStore()
	{
		Contacts = new ArrayList<UserContactInfo>();
	}
	
	//----------------------------------------------------------------------------------------------
	public void LoadContacts(Context context)
	{
		Log.d("Start", "load contact list");
		
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
		  
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		Cursor cursor = context.getContentResolver().query(uri,	new String[] 
				{ ContactsContract.CommonDataKinds.Phone.NUMBER,
				  ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				  ContactsContract.CommonDataKinds.Phone._ID},
				  null, null, sortOrder);
		
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast())
		{
			String contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			int phoneContactID = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
			
			String NumberNorm = ContactsStore.NormalizeNumber(contactNumber, "+380");
			
			UserContactInfo userInfo = new UserContactInfo();
			userInfo.ContactNumber = NumberNorm;
			userInfo.Name = contactName;
			userInfo.Id = phoneContactID;
			
			Contacts.add(userInfo);
			
			cursor.moveToNext();
		}
				
		cursor.close();
		cursor = null;
		
		Log.d("End", "load contact list");
	}
	
	//----------------------------------------------------------------------------------------------
	public void FillListContacts(Context context, ListView list)
	{
		AdapterContacts adapter = new AdapterContacts(context, R.layout.activity_contacts, Contacts);
	
		list.setAdapter(adapter);
	}
	
	//----------------------------------------------------------------------------------------------
	public UserContactInfo GetInfoByNum(String number)
	{
		for (UserContactInfo v : Contacts)
		{
			if (number.equals(v.GetNumber()))
				return v;
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