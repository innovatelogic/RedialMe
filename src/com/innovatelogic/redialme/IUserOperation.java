package com.innovatelogic.redialme;

//----------------------------------------------------------------------------------------------
public interface IUserOperation
{
	public String GetMask();
}

//----------------------------------------------------------------------------------------------
class UserOperationSMS implements IUserOperation
{
	private String Mask;
	
	public String GetMask() { return Mask; }
	
	UserOperationSMS(String mask)
	{
		this.Mask = mask;	
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
}