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
    private RecentCallsStore mRecentCallsStore = null;

   //----------------------------------------------------------------------------------------------
    public static Context getAppContext() { return mContext; }
    
    public ContactsStore getContactsStore() { return mContactsStore; }
    
    public TerritoryEntry GetCurrentTerritory() { return mTerritory; }
    
    public RecentCallsStore GetRecentCallsStore() { return mRecentCallsStore; }
    
    public TabHost GetTabView() { return tabView; }
    
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
    		
    		mRecentCallsStore = new RecentCallsStore(this);
    		mRecentCallsStore.Initialize();
    		
    		mTerritory = providerStore.GetTerritory(mDefaultTerritory);
        	mTextView.setText(mOperatorName);
        	
        	mListPresenter = new RecentCallsListPresenter(this, R.id.listRecentCalls);
        	mListPresenter.FillList(GetRecentCallsStore());
        	
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
}
