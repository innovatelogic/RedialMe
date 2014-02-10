package com.innovatelogic.redialme;

import java.io.InputStream;
import java.io.IOException;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.util.Map;
import java.util.HashMap;

import android.util.Xml;

import com.innovatelogic.redialme.TerritoryEntry;

public class ProviderStore
{
	private Map<String, TerritoryEntry> MapTerritoryEntries; 

	public static final String ns = null;
	public static final String PROVIDERS_TAG = "Providers";
	public static final String TERRITORIES_TAG = "Territories";
	public static final String TERRITORY_TAG = "Territory";
	
	public ProviderStore()
	{
		MapTerritoryEntries = new HashMap<String, TerritoryEntry>();
	}
	
	//----------------------------------------------------------------------------------------------
	public void Deserialize(InputStream stream) throws XmlPullParserException, IOException
	{
		try
		{
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(stream, null);
			parser.nextTag();
		
			parser.require(XmlPullParser.START_TAG, ns, PROVIDERS_TAG);
			
			boolean readTerritories = false;
			
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT)
			{
				String name = parser.getName();
				
				switch (event)
				{
				case XmlPullParser.START_TAG:
					if (name.equals(TERRITORIES_TAG) && !readTerritories)
					{
						DeserializeTerritories(parser);
						readTerritories = true;
					}
					break;
				case XmlPullParser.END_TAG:
					if (name.equals(TERRITORIES_TAG) && readTerritories)
						readTerritories = false;
					break;
				}
				
				event = parser.next();
			}
		}
		finally
		{
			stream.close();
		}
	}

	//----------------------------------------------------------------------------------------------
	private void DeserializeTerritories(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, ns, TERRITORIES_TAG);
		
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
				if (name.equals(TERRITORY_TAG) && !readTag)
				{
					String atrName = parser.getAttributeValue(ns, "Name");
					String atrCode = parser.getAttributeValue(ns, "Code");
					
					TerritoryEntry territory = new TerritoryEntry(atrCode);
					
					territory.Deserialize(parser);
					
					MapTerritoryEntries.put(atrName, territory);
					readTag = true;
				}
				depth++;
				break;
				
			case XmlPullParser.END_TAG:
				if (name.equals(TERRITORY_TAG) && readTag)
					readTag = false;
				depth--;	
				break;
			}
			event = parser.next();
		}
	}
	
	//----------------------------------------------------------------------------------------------
	public TerritoryEntry GetTerritory(String terrName)
	{
		return  MapTerritoryEntries.get(terrName);
	}
	
	//----------------------------------------------------------------------------------------------
	public ProviderEntry GetProvider(String terrName, String name)
	{
		ProviderEntry outProvider = null;
		
		TerritoryEntry territory = GetTerritory(terrName);
		
		if (territory != null)
		{
			outProvider = territory.GetProvider(name);
		}
		
		return outProvider;
	}
	
	
}
