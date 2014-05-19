package com.innovatelogic.bomzdialer;

import java.util.Map;
import java.util.HashMap;

import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.innovatelogic.bomzdialer.ProviderEntry;

public class TerritoryEntry 
{
	public static final String PROVIDER_TAG = "Provider";
	
	private String mName;
	private String mCode;
	private String mAlias;
	
	private Map<String, ProviderEntry> mMapProviders;
	private Map<String, ProviderEntry> mMapProvidersByAlias;
	
	//----------------------------------------------------------------------------------------------
	public TerritoryEntry(String name, String code, String alias)
	{
		mMapProviders = new HashMap<String, ProviderEntry>();
		mMapProvidersByAlias = new HashMap<String, ProviderEntry>();
		
		mName = name;
		mCode = code;
		mAlias = alias;	
	}
	
	//----------------------------------------------------------------------------------------------
	public String GetName() { return mName; }
	public String GetCode() { return mCode; }
	public String GetAlias() { return mAlias; }
	
	//----------------------------------------------------------------------------------------------
	public Map<String, ProviderEntry> GetProviders() { return mMapProviders; }
	public Map<String, ProviderEntry> GetProvidersAlias() { return mMapProvidersByAlias; }
	
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
					String aliasName = parser.getAttributeValue(ProviderStore.ns, "Alias");
					
					String[] NameAliases = atrName.split(";");
					
					ProviderEntry provider = new ProviderEntry(atrName, aliasName);
					provider.Deserialize(parser);
					
					for (String s : NameAliases){
						mMapProvidersByAlias.put(s, provider);
					}
					mMapProviders.put(atrName, provider);
					
					event = parser.getEventType();
					readTag = true;
				}
				depth++;
			}
							
			if (event == XmlPullParser.END_TAG)
			{
				if (name.equals(PROVIDER_TAG) && readTag){
					readTag = false;
				}
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
		return mMapProviders.get(name);
	}
}
