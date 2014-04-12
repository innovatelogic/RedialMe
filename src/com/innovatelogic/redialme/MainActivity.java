package com.innovatelogic.redialme;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.innovatelogic.redialme.RecentCallsStore.CallInfo;

//----------------------------------------------------------------------------------------------
//
//----------------------------------------------------------------------------------------------
public class MainActivity extends Activity 
{
	private static Context  mContext;
	
	private TabHost			mTabView;
	private TextView		mUserNameEdit;
	private Button			mUserNameBackspace;
	
	private RecentCallsListPresenter mListPresenter;
	private ContactsListPresenter mListPresenterContacts;
	
	// stores
	private ProviderStore 	mProviderStore;
	private ContactsStore	mContactsStore;
			
    private String mOperatorName = null;
    private String mDefaultTerritory = "UA";
    
    private static int mCurrentTab = 0;

    private TerritoryEntry mTerritory = null;
    private DialPad mDialPad = null;
    private RecentCallsStore mRecentCallsStore = null;
    private ActionPopupWindow mActionPopupWindow = null;
    
    private ProviderEntry mProviderDefault = null;
    
    //private static final String AD_UNIT_ID = "ca-app-pub-7743614673711056/4483553123";
    
    public static Context getAppContext() { return mContext; }
    
    public ProviderStore GetProviderStore() { return mProviderStore; }
    
    public ContactsStore getContactsStore() { return mContactsStore; }
    
    public TerritoryEntry GetCurrentTerritory() { return mTerritory; }
    
    public RecentCallsStore GetRecentCallsStore() { return mRecentCallsStore; }
    
    public ActionPopupWindow GetPopupWindow() { return mActionPopupWindow; }
    
    public TabHost GetTabView() { return mTabView; }
    
    public String GetCurrentOperator() { return mOperatorName; }
    
    public String GetCurrentNumber() {	return mDialPad.GetNumber(); }
    
    public ProviderEntry GetProviderDefault() { return mProviderDefault; }

	//----------------------------------------------------------------------------------------------
    private void findAllViewsById()
    {
    	mTabView = (TabHost)findViewById(android.R.id.tabhost);
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
    	
    	mUserNameEdit = (TextView)findViewById(R.id.editUserName);
    	mUserNameBackspace = (Button)findViewById(R.id.BtnBackspaceName);
     }
    
    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        MainActivity.mContext = getApplicationContext();
                
        setContentView(R.layout.activity_main);
      
    	this.findAllViewsById();
    	
    	// TODO catch exception
    	TelephonyManager manager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
    	mOperatorName = manager.getNetworkOperatorName();
    	
    	try
    	{
    		AssetManager assetManager = getAssets();
    		InputStream ism = assetManager.open(getString(R.string.providers_data_file));
    		
    		mProviderStore = new ProviderStore();
    		mProviderStore.Deserialize(ism);
    		    		
    		mContactsStore = new ContactsStore();
    		mContactsStore.LoadContacts(getAppContext());
    		
    		mRecentCallsStore = new RecentCallsStore(this);
    		mRecentCallsStore.Initialize();
    		
    		mTerritory = mProviderStore.GetTerritory(mDefaultTerritory);
        	
        	mListPresenter = new RecentCallsListPresenter(this, R.id.listRecentCalls);
        	mListPresenter.FillList(GetRecentCallsStore());
        	
        	mListPresenterContacts = new ContactsListPresenter(this, R.id.listContacts);
        	mListPresenterContacts.FillList(mContactsStore);		
        	
        	mDialPad = new DialPad(this);
        	mDialPad.findAllViewsById(MainActivity.this);
        	
        	mProviderDefault = mTerritory.GetProvider(mOperatorName);
        	
        	mActionPopupWindow = new ActionPopupWindow(this);
        	
        	mCurrentTab = mTabView.getCurrentTab();
        	
        	 // Create an ad.
       /*
            adView = new AdView(this);
            adView.setAdSize(AdSize.BANNER);
            adView.setAdUnitId(AD_UNIT_ID);
            
           AdRequest adRequest = new AdRequest.Builder()
            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
            .addTestDevice("INSERT_YOUR_HASHED_DEVICE_ID_HERE")
            .build();*/
    	}
    	catch (IOException ex)
    	{
    	}
    	catch (XmlPullParserException e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   	
    	mUserNameBackspace.setOnClickListener(new OnClickListener()
    	{
    		@Override
    		public void onClick(View v)
    		{
    			if (mUserNameBackspace.getText().toString().length() > 0)
    				mUserNameEdit.setText("");
    		}
    	});
    	
    	android.text.TextWatcher inputTextWatcher = new android.text.TextWatcher()
    	{
            public void afterTextChanged(android.text.Editable s) {
            	mListPresenterContacts.FillList(mContactsStore, mUserNameEdit.getText().toString());
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        };
        
        mUserNameEdit.addTextChangedListener(inputTextWatcher);
        
        mTabView.setOnTabChangedListener(new TabHost.OnTabChangeListener() 
        {
        	@Override
        	public void onTabChanged(String tabId)
        	{
        		int newTab = mTabView.getCurrentTab();
        		
        		if (mCurrentTab != newTab)
        		{
            		if (mCurrentTab == 2){
            			OnTabPageChanged();
            	    }
            		mCurrentTab = newTab;
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
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (mActionPopupWindow.IsVisible() && keyCode == KeyEvent.KEYCODE_BACK)
		{
			mActionPopupWindow.Toggle(false);
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	//----------------------------------------------------------------------------------------------
	public void OnTabPageChanged()
	{
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		
		imm.hideSoftInputFromWindow(mUserNameEdit.getWindowToken(), 0);
		
		//imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
	}
	
	//----------------------------------------------------------------------------------------------
	// parse specific list on click data
	static public Map<String, String> ParseData(String str)
	{
		Map<String, String> out = new HashMap<String, String>();
		
		str = str.replace("{", "");
		str = str.replace("}", "");
		
		String delims = "[ ,]+";
		String [] tokens = str.split(delims);
		
		for (String token : tokens)
		{
			String delim = "[=]";
			String [] subtokens = token.split(delim);
			
			if (subtokens.length == 2)
			{
				out.put(subtokens[0], subtokens[1]);
			}
			else
			{
				// TODO: log error
			}
		}
		return out;
	}
	
	//----------------------------------------------------------------------------------------------
	static String GetValueByKey(String str, String key)
	{
		Map<String, String> out = ParseData(str);
		return out.get(key);
	}
	
	//----------------------------------------------------------------------------------------------
	static String FormatSec(int Sec)
	{
		int numHours = Sec / 3600;
		int numMin = (Sec - numHours * 3600) / 60;
		int numSec = Sec - (numHours * 3600) - (numMin * 60);
		
		String outString = "";
		
		if (numHours > 0) {
			outString += Integer.toString(numHours) + "h ";
		}
		
		if (numMin > 0) {
			outString += Integer.toString(numMin) + "m ";
		}
		
		if (numSec > 0) {
			outString += Integer.toString(numSec) + "s ";
		}
		return outString;
	}
	
	//----------------------------------------------------------------------------------------------
	static Bitmap fetchThumbnail(final int thumbnailId, Context context) 
	{
	    final Uri uri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, thumbnailId);
	    final Cursor cursor = context.getContentResolver().query(uri, new String[] {
	    	    ContactsContract.CommonDataKinds.Photo.PHOTO}, null, null, null);

	    try 
	    {
	        Bitmap thumbnail = null;
	        if (cursor.moveToFirst()) {
	            final byte[] thumbnailBytes = cursor.getBlob(0);
	            if (thumbnailBytes != null) {
	                thumbnail = BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.length);
	            }
	        }
	        return thumbnail;
	    }
	    finally {
	        cursor.close();
	    }
	}
}
