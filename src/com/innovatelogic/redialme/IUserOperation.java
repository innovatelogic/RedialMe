package com.innovatelogic.redialme;

import android.telephony.SmsManager;

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
	
	UserOperationCall(String mask)
	{
		this.Mask = mask;	
	}
	
	public void Process(String Param)
	{
		
	}
}