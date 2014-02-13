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
class UserOperationSMS implements IUserOperation
{
	private String Mask;
	private String Num;
	
	public String GetMask() { return Mask; }
	
	UserOperationSMS(String mask, String num)
	{
		this.Mask = mask;
		this.Num = num;
	}
	
	public void Process(String param)
	{
		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(Num, null, param, null, null);
	}
}

//----------------------------------------------------------------------------------------------
class UserOperationCall implements IUserOperation
{
	private String Mask;
	
	public String GetMask() { return Mask; }
	
	UserOperationCall(String mask){
		this.Mask = mask;	
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