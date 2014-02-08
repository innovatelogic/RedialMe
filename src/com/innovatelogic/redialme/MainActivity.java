package com.innovatelogic.redialme;


import android.os.Bundle;
import android.view.View;
import android.app.Activity;
import android.view.Menu;
import android.widget.EditText;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.telephony.SmsManager;
import android.content.res.AssetManager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.InputStream;
import java.io.IOException;
import java.io.StringReader;

import com.innovatelogic.redialme.ProviderStore;

public class MainActivity extends Activity {

	private EditText 		callEditText;
	private Button	 		callButton;
	private ProviderStore 	providerStore;

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
    		public void onClick(View v) {
    			String str = callEditText.getText().toString();
    			sendSMS(str);
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
    }
    
    private void sendSMS(String number)
    {
    	SmsManager smsManager = SmsManager.getDefault();
    	smsManager.sendTextMessage("124", null, "3 " + number, null, null);
    }
}
