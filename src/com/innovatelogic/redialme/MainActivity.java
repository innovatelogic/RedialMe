package com.innovatelogic.redialme;

import android.os.Bundle;
import android.provider.CallLog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.app.Activity;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
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
	
	private TabHost			mTabView;
	private ListView		mListRecentCalls;
	private ListView		mListContacts;
	private RecentCallsListPresenter mListPresenter;
	
	private ActionBar		mActionBar;
	// stores
	private ProviderStore 	mProviderStore;
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
    
    public TabHost GetTabView() { return mTabView; }
    
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
    		
    		mProviderStore = new ProviderStore();
    		mProviderStore.Deserialize(ism);
    		    		
    		mContactsStore = new ContactsStore();
    		mContactsStore.LoadContacts(getAppContext());
    		mContactsStore.FillListContacts(getAppContext(), mListContacts);
    		
    		mRecentCallsStore = new RecentCallsStore(this);
    		mRecentCallsStore.Initialize();
    		
    		mTerritory = mProviderStore.GetTerritory(mDefaultTerritory);
        	//mTextView.setText(mOperatorName);
        	
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
    	
    	mListRecentCalls.setOnItemClickListener(new OnItemClickListener() 
    	{
    		@Override
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
    		{ 
    			OpenPopup();
    		}
    	});
    	
    	mListContacts.setOnItemClickListener(new OnItemClickListener() 
    	{
    		@Override
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
    		{ 
    			OpenPopup();
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
    
    //----------------------------------------------------------------------------------------------
    public void OpenPopup()
    {
    	LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
    
    	View popupView = layoutInflater.inflate(R.layout.activity_popupaction, null);
    	
    	final PopupWindow popupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    	
    	Button btnCancel = (Button)popupView.findViewById(R.id.cancel);
    	
    	btnCancel.setOnClickListener(new Button.OnClickListener()
    	{
    		@Override
	    	public void onClick(View v)
	    	{
	    		popupWindow.dismiss();
	    	}
    	});
    	
    	popupWindow.showAtLocation(getWindow().getDecorView().findViewById(android.R.id.content), Gravity.LEFT | Gravity.TOP, 0, 0);
    }
}
