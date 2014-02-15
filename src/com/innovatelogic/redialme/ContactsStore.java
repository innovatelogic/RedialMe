package com.innovatelogic.redialme;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ListView;

import com.innovatelogic.redialme.UserContactInfo;
import com.innovatelogic.redialme.AdapterContacts;

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
		
		while (cursor.isAfterLast() == false)
		{
			String contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			int phoneContactID = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
			
			UserContactInfo userInfo = new UserContactInfo();
			userInfo.ContactNumber = contactNumber;
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
}