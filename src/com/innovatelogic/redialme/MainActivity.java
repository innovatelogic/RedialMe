package com.innovatelogic.redialme;


import android.os.Bundle;
import android.view.View;
import android.app.Activity;
import android.view.Menu;
import android.widget.EditText;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.content.res.AssetManager;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.content.Context;
import android.telephony.TelephonyManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.innovatelogic.redialme.ProviderStore;



public class MainActivity extends Activity 
{
	private static Context  mContext;
	
	private TabHost			tabView;
	private EditText 		editNumber;
	private Button	 		smsButton;
	private Button			callButton;
	private ProviderStore 	providerStore;
	private TextView        mTextView;
	
    private String mOperatorName = null;
    private String mDefaultTerritory = "UA";
    
    private TerritoryEntry mTerritory = null;
    
    private class MaskParser
    {
    	public final String TERR;
    	public final String PROVIDER;
    	public final String ABONENT;
    	public final boolean isFound;
    	
    	public MaskParser(String terr, String provider, String abonent, boolean found)
    	{
    		TERR = terr;
    		PROVIDER = provider;
    		ABONENT = abonent;
    		isFound = found;
    	}
    }
    
  //----------------------------------------------------------------------------------------------
    public static Context getAppContext() { return mContext; }

  //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        MainActivity.mContext = getApplicationContext();
                
        setContentView(R.layout.activity_main);
      
    	this.findAllViewsById();
    	
    	// TODO catch exception
    	TelephonyManager manager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
    	mOperatorName = manager.getNetworkOperatorName();
    	
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
    	
    	mTerritory = providerStore.GetTerritory(mDefaultTerritory);
    	
    	mTextView.setText(mOperatorName);
    	    	
    	smsButton.setOnClickListener(new OnClickListener() 
    	{
    		@Override
    		public void onClick(View v) 
    		{
    			String number = editNumber.getText().toString();
    			sendSMS(number);
    		}
    	});
    	
    	callButton.setOnClickListener(new OnClickListener()
    	{
    		@Override
    		public void onClick(View v) 
    		{
    			String number = editNumber.getText().toString();
    			makeCall(number);
    		}
    	});
    }

	//----------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
	//----------------------------------------------------------------------------------------------
    private void findAllViewsById()
    {
    	tabView = (TabHost) findViewById(android.R.id.tabhost);
    	tabView.setup();
    	
    	TabSpec spec1 = tabView.newTabSpec("Number");
    	spec1.setIndicator("Number");
    	spec1.setContent(R.id.tab1);

    	TabSpec spec2 = tabView.newTabSpec("Contacts");
    	spec2.setIndicator("Contacts");
    	spec2.setContent(R.id.tab2);
              
    	TabSpec spec3 = tabView.newTabSpec("Digit");
    	spec3.setIndicator("Digit");
    	spec3.setContent(R.id.tab3);
         
    	tabView.addTab(spec1);
    	tabView.addTab(spec2);
    	tabView.addTab(spec3);
    	
    	editNumber = (EditText) findViewById(R.id.editNumber);
    	smsButton = (Button) findViewById(R.id.send_sms);
    	callButton = (Button) findViewById(R.id.button_call);
    	mTextView = (TextView) findViewById(R.id.textView1);
    }
    
	//----------------------------------------------------------------------------------------------
    private void sendSMS(String number)
    {
    	if (mTerritory != null && mOperatorName != null)
    	{
    		ProviderEntry provider = mTerritory.GetProvider(mOperatorName);
	    	
	    	if (provider != null)
	    	{
	    		MaskParser info = ParseNumber(number);
	    		
	    		if (info.isFound)
	    		{
	    			IUserOperation operation = provider.GetOpCallMeSMS();
    				
    				if (operation != null)
    				{
    					String mask = operation.GetMask();
    					
    					mask = mask.replace("%TERR%", info.TERR);
						mask = mask.replace("%PRV%", info.PROVIDER);
						mask = mask.replace("%NUM%", info.ABONENT);
						
						operation.Process(mask);
    				}
	    		}
	    	}
    	}
    }
    
	//----------------------------------------------------------------------------------------------
    private void makeCall(String number)
    {
    	if (mTerritory != null && mOperatorName != null)
    	{
    		ProviderEntry provider = mTerritory.GetProvider(mOperatorName);
	    	
	    	if (provider != null)
	    	{
	    		MaskParser info = ParseNumber(number);
	    		
	    		if (info.isFound)
	    		{
	    			IUserOperation operation = provider.GetOpCallMeCall();
    				
    				if (operation != null)
    				{
    					String mask = operation.GetMask();
    					
    					mask = mask.replace("%TERR%", info.TERR);
						mask = mask.replace("%PRV%", info.PROVIDER);
						mask = mask.replace("%NUM%", info.ABONENT);
						
						operation.Process(mask);
    				}
	    		}
	    	}
    	}
    }
    
    //----------------------------------------------------------------------------------------------
    private MaskParser ParseNumber(String number)
    {
    	boolean bFound = false;
    	
    	String TERR = mTerritory.GetCode();
    	String PROVIDER = "";
    	String ABONENT = "";
    	
    	int length = number.length();
		if (length >= 10 && length <= 13)
		{
			ABONENT = number.substring(length - 7);
			
			number = number.substring(0, number.length() - 7);
			
			if (number.length() >= 3 && number.length() <= 6)
			{
				PROVIDER = number.substring(number.length() - 2);
				bFound = true;
			}
		}
    	return new MaskParser(TERR, PROVIDER, ABONENT, bFound);
    }
}
