package com.innovatelogic.redialme;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.innovatelogic.redialme.RecentCallsStore.CallInfo;

//----------------------------------------------------------------------------------------------
//
//----------------------------------------------------------------------------------------------
public class MainActivity extends Activity 
{
	private static Context  mContext;
	
	private TabHost			mTabView;
	private ListView		mListRecentCalls;
	private ListView		mListContacts;
	private RecentCallsListPresenter mListPresenter;

	// stores
	private ProviderStore 	mProviderStore;
	private ContactsStore	mContactsStore;
			
    private String mOperatorName = null;
    private String mDefaultTerritory = "UA";
    private String mDataFilename = "Providers.xml";
    
	private ActionBar mActionBar = null;
    private TerritoryEntry mTerritory = null;
    private DialPad mDialPad = null;
    private RecentCallsStore mRecentCallsStore = null;
    private ActionPopupWindow mActionPopupWindow = null;

    //----------------------------------------------------------------------------------------------
    public static Context getAppContext() { return mContext; }
    
    public ContactsStore getContactsStore() { return mContactsStore; }
    
    public TerritoryEntry GetCurrentTerritory() { return mTerritory; }
    
    public RecentCallsStore GetRecentCallsStore() { return mRecentCallsStore; }
    
    public ActionPopupWindow GetPopupWindow() { return mActionPopupWindow; }
    
    public TabHost GetTabView() { return mTabView; }
    
    public String GetCurrentOperator() { return mOperatorName; }
    
    public ActionBar GetActionBar() { return mActionBar; }
    
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
    		
    		mProviderStore = new ProviderStore();
    		mProviderStore.Deserialize(ism);
    		    		
    		mContactsStore = new ContactsStore();
    		mContactsStore.LoadContacts(getAppContext());
    		mContactsStore.FillListContacts(getAppContext(), mListContacts);
    		
    		mRecentCallsStore = new RecentCallsStore(this);
    		mRecentCallsStore.Initialize();
    		
    		mTerritory = mProviderStore.GetTerritory(mDefaultTerritory);
        	
        	mListPresenter = new RecentCallsListPresenter(this, R.id.listRecentCalls);
        	mListPresenter.FillList(GetRecentCallsStore());
        	
        	mDialPad = new DialPad(this);
        	mDialPad.findAllViewsById(MainActivity.this);
        	
        	ProviderEntry provider = mTerritory.GetProvider(mOperatorName);
        	
           	mActionBar = new ActionBar(this, R.id.ActionLayout);
        	mActionBar.ApplyActionBar(provider);

        	mActionPopupWindow = new ActionPopupWindow(this);
        	
    	}
    	catch (IOException ex)
    	{
    	}
    	catch (XmlPullParserException e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	mListRecentCalls.setOnItemClickListener(new OnItemClickListener() 
    	{
    		@Override
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
    		{
    			List<CallInfo> store = mRecentCallsStore.GetRecentInfoList();
    			
    			if (position >= 0 && position < store.size())
    			{
    				UserContactInfo info = mContactsStore.GetInfoByNum(store.get(position).mNumber);
    				
    				mActionPopupWindow.mName = (info != null) ? info.Name : "Unknown number";
    				mActionPopupWindow.mNumber = store.get(position).mNumber;
    				
    				mActionPopupWindow.Toggle(true);
    			}
    		}
    	});
    	
    	mListContacts.setOnItemClickListener(new OnItemClickListener() 
    	{
    		@Override
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
    		{ 
    			ArrayList<UserContactInfo> contacts = mContactsStore.GetContactsStore();
    			
    			if (position >= 0 && position < contacts.size())
    			{
    				mActionPopupWindow.mName = contacts.get(position).Name;
    				mActionPopupWindow.mNumber = contacts.get(position).ContactNumber;
    				mActionPopupWindow.Toggle(true);
    			}
    			else
    			{
    				// log error
    			}
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
    	mTabView = (TabHost) findViewById(android.R.id.tabhost);
    	mTabView.setup();
    	
    	TabSpec spec1 = mTabView.newTabSpec("DialPad");
    	spec1.setIndicator("Number");
    	spec1.setContent(R.id.tab1);

    	TabSpec spec2 = mTabView.newTabSpec("Recent");
    	spec2.setIndicator(getString(R.string.Recent));
    	spec2.setContent(R.id.tab2);
              
    	TabSpec spec3 = mTabView.newTabSpec("Contacts");
    	spec3.setIndicator(getString(R.string.Contacts));
    	spec3.setContent(R.id.tab3);
         
    	mTabView.addTab(spec1);
    	mTabView.addTab(spec2);
    	mTabView.addTab(spec3);
    	
    	mListRecentCalls = (ListView) findViewById(R.id.listRecentCalls);
    	mListContacts = (ListView) findViewById(R.id.listContacts);
     }
}
