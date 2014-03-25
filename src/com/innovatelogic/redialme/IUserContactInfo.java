package com.innovatelogic.redialme;

import android.net.Uri;

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
	
	public int 	  Id;
	public String Name;
	public String ContactNumber;
	public int thumbnailID;
}