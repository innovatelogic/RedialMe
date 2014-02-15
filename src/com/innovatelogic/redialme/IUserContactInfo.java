package com.innovatelogic.redialme;

//----------------------------------------------------------------------------------------------
public interface IUserContactInfo
{
	public String GetName();
	public String GetNumber();
}

//----------------------------------------------------------------------------------------------
class UserContactInfo implements IUserContactInfo
{
	public String GetName() { return Name; }
	public String GetNumber() { return ContactNumber; }
		
	public String Name;
	public String ContactNumber;
	public int Id;
}