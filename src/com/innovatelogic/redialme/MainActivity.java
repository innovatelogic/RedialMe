package com.innovatelogic.redialme;

import android.os.Bundle;
import android.provider.CallLog;
import android.view.View;
import android.app.Activity;
import android.view.Menu;
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

import com.innovatelogic.redialme.ProviderStore;


public class MainActivity extends Activity 
{
	private static Context  mContext;
	
	private TabHost			tabView;
	private EditText 		editNumber;
	private Button	 		smsButton;
	private Button			callButton;
	private TextView        mTextView;
	private ListView		listRecentCalls;
	private ListView		listContacts;
	
	// stores
	private ProviderStore 	providerStore;
	private ContactsStore	contactsStore;
			
    private String mOperatorName = null;
    private String mDefaultTerritory = "UA";
    private String mDataFilename = "Providers.xml";
    
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
    		InputStream ism = assetManager.open(mDataFilename);
    		
    		providerStore = new ProviderStore();
    		providerStore.Deserialize(ism);
    		    		
    		contactsStore = new ContactsStore();
    		contactsStore.LoadContacts(getAppContext());
    		contactsStore.FillListContacts(getAppContext(), listContacts);
    		
    		mTerritory = providerStore.GetTerritory(mDefaultTerritory);
        	mTextView.setText(mOperatorName);
        	
        	FillRecentCalls();
    	}
    	catch (IOException ex)
    	{
    	} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
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
    
    //----------------------------------------------------------------------------------------------
    private void FillRecentCalls()
    {
		List<String> RecentCalls = new ArrayList<String>();
			
		StringBuffer sb = new StringBuffer();
		Cursor managedCursor = mContext.getContentResolver().query(CallLog.Calls.CONTENT_URI,
								null, null, null, CallLog.Calls.DATE + " DESC");
		   
		int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
		int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
		int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
		int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
		int id = managedCursor.getColumnIndex(CallLog.Calls._ID);
		
		sb.append("Call Details :");
		while (managedCursor.moveToNext()) 
		{
			String phNumber = managedCursor.getString(number);
			String callType = managedCursor.getString(type);
			String callDate = managedCursor.getString(date);
			
			Date callDayTime = new Date(Long.valueOf(callDate));
			String callDuration = managedCursor.getString(duration);
			String dir = null;
			int dircode = Integer.parseInt(callType);
			switch (dircode) {
			case CallLog.Calls.OUTGOING_TYPE:
				dir = "OUTGOING";
			break;
			
			case CallLog.Calls.INCOMING_TYPE:
				dir = "INCOMING";
			break;
			
			case CallLog.Calls.MISSED_TYPE:
				dir = "MISSED";
			break;
		}
			
		String NormNumber = ContactsStore.NormalizeNumber(phNumber, "+380");
			
		UserContactInfo info = contactsStore.GetInfoByNum(NormNumber);
		if (info != null)
		{
			NormNumber = info.GetName();
		}
			
		RecentCalls.add("\nPhone Number:--- " + NormNumber + " \nCall Type:--- "
					+ dir + " \nCall Date:--- " + callDayTime
					+ " \nCall duration in sec :--- " + callDuration +
					"\nID" + id);
		}
		
		managedCursor.close();
	    	
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		            R.layout.recentcallsactivity, RecentCalls);
		
		listRecentCalls.setAdapter(adapter);
    }
}
