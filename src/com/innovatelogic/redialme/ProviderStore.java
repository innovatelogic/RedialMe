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
			
			while (parser.next() != XmlPullParser.END_TAG)
			{
				if (parser.getEventType() != XmlPullParser.START_TAG){
					continue;
				}
				
				String name = parser.getName();
				
				if (name.equals(TERRITORIES_TAG))
				{
					DeserializeTerritories(parser);
				}
				else
				{
					skip(parser);
				}
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
		while (parser.next() != XmlPullParser.END_TAG)
		{
			if (parser.getEventType() != XmlPullParser.START_TAG){
				continue;
			}
			
			String name = parser.getName();
			
			if (name.equals(TERRITORY_TAG))
			{
				String atrName = parser.getAttributeValue(ns, "Name");
				String atrCode = parser.getAttributeValue(ns, "Code");
				
				TerritoryEntry territory = new TerritoryEntry(atrName);
			
				territory.Deserialize(parser);
				
				MapTerritoryEntries.put(atrCode, territory);
			}
			else
			{
				skip(parser);
			}
		}
	}

	//----------------------------------------------------------------------------------------------
	public static void skip(XmlPullParser parser) throws XmlPullParserException, IOException 
	{
	    if (parser.getEventType() != XmlPullParser.START_TAG) {
	        throw new IllegalStateException();
	    }
	    int depth = 1;
	    while (depth != 0) {
	        switch (parser.next()) {
	        case XmlPullParser.END_TAG:
	            depth--;
	            break;
	        case XmlPullParser.START_TAG:
	            depth++;
	            break;
	        }
    }
 }
}
