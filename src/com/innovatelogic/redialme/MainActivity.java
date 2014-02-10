package com.innovatelogic.redialme;


import android.os.Bundle;
import android.view.View;
import android.app.Activity;
import android.view.Menu;
import android.widget.EditText;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.innovatelogic.redialme.ProviderStore;

public class MainActivity extends Activity {

	private EditText 		callEditText;
	private Button	 		callButton;
	private ProviderStore 	providerStore;
	
	 private RadioButton lifeRadioButton;
     private RadioButton mtsRadioButton;
     private RadioButton kievstarRadioButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
    	this.findAllViewsById();
    	
    	try
    	{
    		AssetManager assetManager = getAssets();
    		InputStream ism = assetManager.open("Providers.xml");
    		
    		providerStore = new ProviderStore();
    		providerStore.Deserialize(ism);
    	}
    	catch (IOException ex)
    	{
    		
    		
    	} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	//providerStore.
    	
    	callButton.setOnClickListener(new OnClickListener() 
    	{
    		@Override
    		public void onClick(View v) 
    		{
    			String number = callEditText.getText().toString();
    			sendSMS(number);
    		}
    	});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    private void findAllViewsById()
    {
    	callEditText = (EditText) findViewById(R.id.editText1);
    	callButton = (Button) findViewById(R.id.send_sms);
    	
    	lifeRadioButton = (RadioButton) findViewById(R.id.radio_life);
    	kievstarRadioButton = (RadioButton) findViewById(R.id.radio_kievstar);
    	mtsRadioButton = (RadioButton) findViewById(R.id.radio_mts);
    }
    
    private void sendSMS(String number)
    {
    	ProviderEntry provider = null;
    	TerritoryEntry terr = providerStore.GetTerritory("UA");
    	
    	if (terr != null)
    	{
	    	//check provider
	    	if (lifeRadioButton.isChecked())
	    	{
	    		provider = terr.GetProvider("life:)");
	    	}
	    	else if (kievstarRadioButton.isChecked())
	    	{
	    		provider = terr.GetProvider("kievstar");
	    	}
	    	else
	    	{
	    		provider = terr.GetProvider("mts");
	    	}
	    	
	    	if (provider != null)
	    	{
	    		int length = number.length();
	    		if (length >= 10 && length <= 13)
	    		{
	    			String abonentNum = number.substring(length - 7);
	    			
	    			number = number.substring(0, number.length() - 7);
	    			
	    			if (number.length() >= 3 && number.length() <= 6)
	    			{
	    				String prvNum = number.substring(number.length() - 2);
	    				
	    				IUserOperation operation = provider.GetOpCallMeSMS();
	    				
	    				if (operation != null)
	    				{
	    					String mask = operation.GetMask();
	    					
	    					//%TERR%%PRV%%NUM%
    						mask = mask.replace("%TERR%", terr.GetCode());
    						mask = mask.replace("%PRV%", prvNum);
    						mask = mask.replace("%NUM%", abonentNum);
	    				
    						operation.Process(mask);
	    				}
	    			}
	    		}
	    	}
    	}
    }
}
