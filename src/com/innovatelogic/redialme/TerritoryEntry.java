package com.innovatelogic.redialme;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.innovatelogic.redialme.ProviderEntry;

public class TerritoryEntry 
{
	public static final String PROVIDER_TAG = "Provider";
	
	private String Code;
	
	private Map<String, ProviderEntry> MapProviders;
		
	public TerritoryEntry(String code)
	{
		Code = code;
		MapProviders = new HashMap<String, ProviderEntry>();
	}
	//----------------------------------------------------------------------------------------------
	public String GetCode() { return Code; }
	
	//----------------------------------------------------------------------------------------------
	public void Deserialize(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		//parser.require(XmlPullParser.START_TAG, ProviderStore.ns, ProviderStore.TERRITORY_TAG);
		
		parser.nextTag(); // go to next tag
		
		int event = parser.getEventType();
		int depth = 1;
		boolean readTag = false;
		
		while (event != XmlPullParser.END_DOCUMENT)
		{
			String name = parser.getName();
			
			event = parser.getEventType();
			
			if (event == XmlPullParser.START_TAG)
			{
				if (name.equals(PROVIDER_TAG) && !readTag)
				{
					String atrName = parser.getAttributeValue(ProviderStore.ns, "Name");
					String atrCodes = parser.getAttributeValue(ProviderStore.ns, "Codes");
					
					String[] NameAliases = atrName.split(";");
										
					String[] parts = atrCodes.split(";");
					List<String> codes = Arrays.asList(parts);
					
					ProviderEntry provider = new ProviderEntry();
					provider.Deserialize(parser);
					
					for(String s : NameAliases)
						MapProviders.put(s, provider);
					
					event = parser.getEventType();
					readTag = true;
				}
				depth++;
			}
							
			if (event == XmlPullParser.END_TAG)
			{
				if (name.equals(PROVIDER_TAG) && readTag)
					readTag = false;
				depth--;
			}
			
			if (depth == 0)
				break;
			
			event = parser.next();
		}
	}
	
	//----------------------------------------------------------------------------------------------
	public ProviderEntry GetProvider(String name)
	{
		return MapProviders.get(name);
	}
}
