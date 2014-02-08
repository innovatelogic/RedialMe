package com.innovatelogic.redialme;

import java.io.IOException;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.innovatelogic.redialme.ProviderEntry;

public class TerritoryEntry 
{
	public static final String PROVIDER_TAG = "Provider";
	
	private String Name;
	
	private Map<String, ProviderEntry> MapProviders; 
		
	public TerritoryEntry(String name)
	{
		Name = name;
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
			
			if (name.equals(PROVIDER_TAG))
			{
				String atrName = parser.getAttributeValue(ProviderStore.ns, "Name");
				String atrCodes = parser.getAttributeValue(ProviderStore.ns, "Codes");
				
				ProviderEntry provider = new ProviderEntry(atrCodes);
				
				provider.Deserialize(parser);
				
				MapProviders.put(atrName, provider);
			}
			else
			{
				ProviderStore.skip(parser);
			}
		}
	}
}
