package com.innovatelogic.redialme;

import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import android.content.ActivityNotFoundException;

//----------------------------------------------------------------------------------------------
public interface IUserOperation
{
	public String GetMask();
	public void Process(String Param);
}

//----------------------------------------------------------------------------------------------
//
//----------------------------------------------------------------------------------------------
class UserOperationSMS implements IUserOperation
{
	private String mMask;
	private String mNum;
	
	public String GetMask() { return mMask; }
	
	UserOperationSMS(String mask, String num)
	{
		this.mMask = mask;
		this.mNum = num;
	}
	
	public void Process(String param)
	{
		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(mNum, null, param, null, null);
	}
}

//----------------------------------------------------------------------------------------------
//
//----------------------------------------------------------------------------------------------
class UserOperationCall implements IUserOperation
{
	private String mMask;
	
	public String GetMask() { return mMask; }
	
	UserOperationCall(String mask){
		this.mMask = mask;	
	}
	
	public void Process(String param)
	{
		try
		{
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:" + Uri.encode(param)));
			callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			MainActivity.getAppContext().startActivity(callIntent);
		}
		catch (ActivityNotFoundException ex)
		{
			
		}
	}
}