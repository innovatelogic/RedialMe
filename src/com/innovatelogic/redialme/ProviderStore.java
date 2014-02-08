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

	private static final String ns = null;
	
	public ProviderStore()
	{
		MapTerritoryEntries = new HashMap<String, TerritoryEntry>();
	}
	
	public void Deserialize(InputStream stream) throws XmlPullParserException, IOException
	{
		try
		{
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(stream, null);
			parser.nextTag();
		
			parser.require(XmlPullParser.START_TAG, ns, "Providers");
			
			while (parser.next() != XmlPullParser.END_TAG)
			{
				int tag = parser.getEventType();
				String text = parser.getText();
				
				if (tag != XmlPullParser.START_TAG){
					String name1 = parser.getName();
					continue;
				}
				
				String name = parser.getName();
				
				if (name.equals("Territory"))
				{
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

	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
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
