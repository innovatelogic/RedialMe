package com.innovatelogic.bomzdialer;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.innovatelogic.bomzdialer.IUserOperation;

public class ProviderEntry
{
	private String mName;
	private String mAliasName;
	private List<IUserOperation> mOperationsList;
	
	private static final String ENTRY_TAG = "Entry";
	private static final String CALLME_TAG = "CallMe";
	private static final String SMS_TYPE_TAG = "SMS";
	private static final String CALL_TYPE_TAG = "Call";
		
	public List<IUserOperation> GetOperationList() { return mOperationsList; }
	
	public String GetName() { return mName; }
	public String GetAliasName() { return mAliasName; }
	
	//----------------------------------------------------------------------------------------------
	public ProviderEntry(String name, String alias)
	{
		mName = name;
		mAliasName = alias;
		mOperationsList = new ArrayList<IUserOperation>();
	}
	
	//----------------------------------------------------------------------------------------------
	public void Deserialize(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		//parser.require(XmlPullParser.START_TAG, ProviderStore.ns, TerritoryEntry.PROVIDER_TAG);
		
		parser.nextTag(); // go to next tag
		
		int event = parser.getEventType();
		boolean readTag = false;
		int depth = 1;
		
		while (event != XmlPullParser.END_DOCUMENT && depth != 0)
		{	
			String name = parser.getName();
			
			switch (event)
			{
			case XmlPullParser.START_TAG:
				if (name.equals(ENTRY_TAG) && !readTag)
				{
					String atrOperation = parser.getAttributeValue(ProviderStore.ns, "Operation");
					String atrType = parser.getAttributeValue(ProviderStore.ns, "Type");
					String atrMask = parser.getAttributeValue(ProviderStore.ns, "Mask");
					String atrNum = parser.getAttributeValue(ProviderStore.ns, "Num");
					
					// Object fabric
					if (atrOperation.equals(CALLME_TAG))
					{
						if (atrType.equals(SMS_TYPE_TAG))
						{
							mOperationsList.add(new UserOperationSMS(atrMask, atrNum));
						}
						else if (atrType.equals(CALL_TYPE_TAG))
						{
							mOperationsList.add(new UserOperationCall(atrMask));
						}
					}
					readTag = true;
				}
				depth++;
				break;
				
			case XmlPullParser.END_TAG:
				if (name.equals(ENTRY_TAG) && readTag)
					readTag = false;
				depth--;
				break;
			}
			
			if (depth == 0)
				break;
			
			event = parser.next();
		}
	}
}
