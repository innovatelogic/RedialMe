package com.innovatelogic.redialme;

import android.os.Bundle;
import android.provider.CallLog;
import android.view.View;
import android.app.Activity;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.content.res.AssetManager;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.content.Context;
import android.database.Cursor;
import android.telephony.TelephonyManager;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;
import android.widget.AdapterView.OnItemClickListener;

import com.innovatelogic.redialme.ProviderStore;
import com.innovatelogic.redialme.DialPad;
import com.innovatelogic.redialme.ActionBar;

public class MainActivity extends Activity 
{
	private static Context  mContext;
	
	private TabHost			tabView;
	private TextView        mTextView;
	private ListView		listRecentCalls;
	private ListView		listContacts;
	private RecentCallsListPresenter mListPresenter;
	
	private ActionBar		mActionBar;
	// stores
	private ProviderStore 	providerStore;
	private ContactsStore	mContactsStore;
			
    private String mOperatorName = null;
    private String mDefaultTerritory = "UA";
    private String mDataFilename = "Providers.xml";
    
    private TerritoryEntry mTerritory = null;
    private DialPad mDialPad = null;

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
    public ContactsStore getContactsStore() { return mContactsStore; }
    public TerritoryEntry GetCurrentTerritory() { return mTerritory; }
    
  //----------------------------------------------------------------------------------------------
    public String GetCurrentNumber()
    {
    	return mDialPad.GetNumber();
    }

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
    		InputStream ism = assetManager.open(mDataFilename);
    		
    		mActionBar = new ActionBar(this, R.id.ActionLayout);
    		
    		providerStore = new ProviderStore();
    		providerStore.Deserialize(ism);
    		    		
    		mContactsStore = new ContactsStore();
    		mContactsStore.LoadContacts(getAppContext());
    		mContactsStore.FillListContacts(getAppContext(), listContacts);
    		
    		mTerritory = providerStore.GetTerritory(mDefaultTerritory);
        	mTextView.setText(mOperatorName);
        	
        	mListPresenter = new RecentCallsListPresenter(this, R.id.listRecentCalls);
        	mListPresenter.FillList();
        	
        	mDialPad = new DialPad(this);
        	mDialPad.findAllViewsById(MainActivity.this);
        	
        	ProviderEntry provider = mTerritory.GetProvider(mOperatorName);
        	mActionBar.ApplyActionBar(provider);
    	}
    	catch (IOException ex)
    	{
    	}
    	catch (XmlPullParserException e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	/*smsButton.setOnClickListener(new OnClickListener() 
    	{
    		@Override
    		public void onClick(View v) 
    		{
    			//String number = editNumber.getText().toString();
    			//sendSMS(number);
    		}
    	});
    	
    	callButton.setOnClickListener(new OnClickListener()
    	{
    		@Override
    		public void onClick(View v) 
    		{
    			//String number = editNumber.getText().toString();
    			//makeCall(number);
    		}
    	});*/
    	
    	listRecentCalls.setOnItemClickListener(new OnItemClickListener() 
    	{
    		@Override
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
    		{ 
    			  //TODO
    		}
    	});
    	
    	listContacts.setOnItemClickListener(new OnItemClickListener() 
    	{
    		@Override
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
    		{ 
    			  //TODO
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
    	
    	mTextView = (TextView) findViewById(R.id.textView1);
    	
    	listRecentCalls = (ListView) findViewById(R.id.listRecentCalls);
    	listContacts = (ListView) findViewById(R.id.listContacts);
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
