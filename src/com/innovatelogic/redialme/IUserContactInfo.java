package com.innovatelogic.redialme;

import java.util.ArrayList;

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
	public ArrayList<String> ContactNumbers = null; 
	public int thumbnailID;
}