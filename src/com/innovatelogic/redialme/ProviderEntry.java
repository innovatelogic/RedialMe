package com.innovatelogic.redialme;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ProviderEntry
{
	private static final String ENTRY_TAG = "Entry";
	private String ProviderCode;
	
	public ProviderEntry(String code)
	{
		this.ProviderCode = code;
	}
	
	//----------------------------------------------------------------------------------------------
	public void Deserialize(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		while (parser.next() != XmlPullParser.END_TAG)
		{
			if (parser.getEventType() != XmlPullParser.START_TAG){
				continue;
			}
			
			String name = parser.getName();
			
			if (name.equals(ENTRY_TAG))
			{
				String atrOperation = parser.getAttributeValue(ProviderStore.ns, "Operation");
				String atrType = parser.getAttributeValue(ProviderStore.ns, "Type");
				String atrMask = parser.getAttributeValue(ProviderStore.ns, "Mask");
				
				atrMask = "";
			}
			else
			{
				ProviderStore.skip(parser);
			}
		}
	}
}
