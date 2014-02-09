package com.innovatelogic.redialme;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.innovatelogic.redialme.IUserOperation;

public class ProviderEntry
{
	private static final String ENTRY_TAG = "Entry";
	private static final String CALLME_TAG = "CallMe";
	private static final String SMS_TYPE_TAG = "SMS";
	private static final String CALL_TYPE_TAG = "Call";
		
	private String ProviderCode;
	
	private IUserOperation OpCallMeSMS = null;
	private IUserOperation OpCallMeCall = null;
	
	//----------------------------------------------------------------------------------------------
	public ProviderEntry(String code)
	{
		this.ProviderCode = code;
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
					
					// Object fabric
					if (atrOperation.equals(CALLME_TAG))
					{
						if (atrType.equals(SMS_TYPE_TAG))
						{
							OpCallMeSMS = new UserOperationSMS(atrMask);
						}
						else if (atrType.equals(CALL_TYPE_TAG))
						{
							OpCallMeCall = new UserOperationCall(atrMask);
						}
					}
					readTag = true;
				}
				depth++;
				break;
				
			case XmlPullParser.END_TAG:
				if (name.equals(ENTRY_TAG))
					readTag = false;
				depth--;
				break;
			}
			
			event = parser.next();
		}
	}
}
