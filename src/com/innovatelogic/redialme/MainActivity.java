package com.innovatelogic.redialme;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

//----------------------------------------------------------------------------------------------
//
//----------------------------------------------------------------------------------------------
public class MainActivity extends Activity 
{
	enum ESizeType
	{
		ESizeDef,
		ESizeMid,
		ESizeMin
	}
	
	private static Context  mContext;
	
	private TabHost			mTabView;
	private TextView		mUserNameEdit;
	private ImageButton		mUserNameBackspace;
	private ImageButton		mImageButtonOptions;
	
	private ActionPopupWindow mActionPopupWindow = null;
	private SettingsPopupWindow mActionSettings = null;
	
	private RecentCallsListPresenter mListPresenter;
	private ContactsListPresenter mListPresenterContacts;
	
	// stores
	private ProviderStore 	mProviderStore;
	private ContactsStore	mContactsStore;
			
    private String mOperatorName = null;
    private String mDefaultTerritory = "UA";
        
    private static int mCurrentTab = 0;

    private TerritoryEntry mTerritory = null;
    private ProviderEntry  mCurrentProvider = null;
    
    private DialPad mDialPad = null;
    private RecentCallsStore mRecentCallsStore = null;
    
    //private static final String AD_UNIT_ID = "ca-app-pub-7743614673711056/4483553123";
    public static final String PREFS_NAME = "RedialMePrefsFile";
    public static final String NODEF = "NODEF";
    
    public static final int DEF_FONT_SIZE = 16;
    public static final int MID_FONT_SIZE = 12;
    public static final int MIN_FONT_SIZE = 8;
    
    public static Context getAppContext() { return mContext; }
    
    public ProviderStore GetProviderStore() { return mProviderStore; }
    
    public ContactsStore getContactsStore() { return mContactsStore; }
    
    public TerritoryEntry GetCurrentTerritory() { return mTerritory; }
    
    public RecentCallsStore GetRecentCallsStore() { return mRecentCallsStore; }
    
    public ActionPopupWindow GetPopupWindow() { return mActionPopupWindow; }

    public SettingsPopupWindow GetSettingsWindow() { return mActionSettings; }
    
    public TabHost GetTabView() { return mTabView; }
    
    public String GetCurrentOperator() { return mOperatorName; }
    
    public String GetCurrentNumber() {	return mDialPad.GetNumber(); }
    
    public ProviderEntry GetProviderDefault() { return mCurrentProvider; }
    
    public float GetDefTextSize(ESizeType esize) { return (esize == ESizeType.ESizeDef ? DEF_FONT_SIZE :
    														(esize == ESizeType.ESizeMid ? MID_FONT_SIZE : MIN_FONT_SIZE)) 
    														* getResources().getDisplayMetrics().density; }

    //----------------------------------------------------------------------------------------------
    private void findAllViewsById()
    {
    	mTabView = (TabHost)findViewById(android.R.id.tabhost);
    	mTabView.setup();
    	
    	TabSpec spec1 = mTabView.newTabSpec("DialPad");
    	spec1.setIndicator(null, getResources().getDrawable(R.drawable.icon_tab0_config));
    	spec1.setContent(R.id.tab1);

    	TabSpec spec2 = mTabView.newTabSpec("Recent");
    	spec2.setIndicator(null, getResources().getDrawable(R.drawable.icon_tab1_config));
    	spec2.setContent(R.id.tab2);
              
    	TabSpec spec3 = mTabView.newTabSpec("Contacts");
    	spec3.setIndicator(null, getResources().getDrawable(R.drawable.icon_tab2_config));
    	spec3.setContent(R.id.tab3);
         
    	mTabView.addTab(spec1);
    	mTabView.addTab(spec2);
    	mTabView.addTab(spec3);
    	
    	mUserNameEdit = (TextView)findViewById(R.id.editUserName);
    	mUserNameBackspace = (ImageButton)findViewById(R.id.BtnBackspaceName);
    	mImageButtonOptions = (ImageButton)findViewById(R.id.imageButtonOptions);
    	
    	mUserNameEdit.setTextSize(GetDefTextSize(ESizeType.ESizeDef));
     }
    
    //----------------------------------------------------------------------------------------------
 /*   private void addTab(int drawableId, Class<?> c, String labelId)
    {
    	final TabWidget = (TabWidget) findViewById(android.R.id.tabs);
        final TabHost tabHost = mTabView;
        Intent intent = new Intent(this, c);
        TabHost.TabSpec spec = tabHost.newTabSpec("tab"+ labelId);  

        View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator, getTabWidget(), false);
        TextView title = (TextView) tabIndicator.findViewById(R.id.title);
        title.setText(labelId);

        ImageView icon = (ImageView) tabIndicator.findViewById(R.id.userpic);
        icon.setImageResource(drawableId);
        spec.setIndicator(tabIndicator);
        spec.setContent(intent);
        tabHost.addTab(spec);
    }*/
    
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
    		mActionSettings = new SettingsPopupWindow(this);
    		mActionPopupWindow = new ActionPopupWindow(this);
    		mProviderStore = new ProviderStore();
    		mContactsStore = new ContactsStore();
    		mRecentCallsStore = new RecentCallsStore(this);
    		mDialPad = new DialPad(this);
    		
    		mListPresenter = new RecentCallsListPresenter(this, R.id.listRecentCalls);
    		mListPresenterContacts = new ContactsListPresenter(this, R.id.listContacts);
    		
    		AssetManager assetManager = getAssets();
    		InputStream ism = assetManager.open(getString(R.string.providers_data_file));
    		mProviderStore.Deserialize(ism);
   		
    		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
    		String territory = settings.getString("Territory", NODEF);
    		String provider = settings.getString("Provider", NODEF);
    		
    		//mActionSettings.Toggle(true, true);
    		if (territory == NODEF || provider == NODEF)
    		{
    			mActionSettings.Toggle(true, true);
    		}
    		else
    		{
    			mTerritory = mProviderStore.GetTerritory(mDefaultTerritory);
    			mCurrentProvider = mTerritory.GetProvider(mOperatorName);
    			
    			Initialize(mTerritory, mCurrentProvider);
    		}
        	
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
    			if (mUserNameEdit.getText().toString().length() > 0)
    				mUserNameEdit.setText("");
    		}
    	});
    	
    	mImageButtonOptions.setOnClickListener(new OnClickListener()
    	{
    		@Override
    		public void onClick(View v)
    		{
    			GetSettingsWindow().Toggle(true, false);
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
            			OnHideInputKeyboard();
            	    }
            		mCurrentTab = newTab;
        		}
        	}
        });
     }
   
    //----------------------------------------------------------------------------------------------
    public void Initialize(TerritoryEntry territory, ProviderEntry provider)
    {
        mTerritory = territory;
        mCurrentProvider = provider;
    	
		mContactsStore.LoadContacts(getAppContext());  		
    	mRecentCallsStore.Initialize();
    	mListPresenter.FillList(GetRecentCallsStore());
    	mListPresenterContacts.FillList(mContactsStore);		
    	mDialPad.UpdateRecentList("");	
    }
    
	//----------------------------------------------------------------------------------------------
    @Override
    protected void onStart()
    {
        super.onStart();
       // GetSettingsWindow().Toggle(true); // TEST TEST
    }
    
    //----------------------------------------------------------------------------------------------
    @Override
    protected void onStop()
    {
    	super.onStop();
    	
    	if (mTerritory != null && mCurrentProvider != null)
    	{
    		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
    		SharedPreferences.Editor editor = settings.edit();
    		editor.putString("Territory", mTerritory.GetName());
    		editor.putString("Provider", mCurrentProvider.GetName());
    		
    		// commit the edits
    		editor.commit();
    	}
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
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if (mActionSettings.IsVisible())
			{
				boolean bModalMode = mActionSettings.GetToggledOptions();
				
				mActionSettings.Toggle(false, false);
				
				if (bModalMode){
					return super.onKeyDown(keyCode, event);
				}
				
				return false;
			}
			
			if (mActionPopupWindow.IsVisible())
			{
				mActionPopupWindow.Toggle(false, false);
				
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	//----------------------------------------------------------------------------------------------
	public void OnHideInputKeyboard()
	{
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		
		imm.hideSoftInputFromWindow(mUserNameEdit.getWindowToken(), 0);
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
	    	Integer Size = 64;
	    	
	    	float density = context.getResources().getDisplayMetrics().density;
	    	
	    	if (density <= 0.75){
	    		Size = 48; // ldpi
	    	}
	    	else if (density > 0.75 && density <= 1.f){
	    		Size = 48; // mdpi
	    	}
	    	else if (density > 1.0 && density <= 1.5f){
	    		Size = 64; // hdpi
	    	}
	    	else if (density > 1.5){
	    		Size = 96; // hdpi
	    	}
	    	
	        Bitmap thumbnail = null;
	        if (cursor.moveToFirst()) 
	        {
	            final byte[] thumbnailBytes = cursor.getBlob(0);
	            
	            if (thumbnailBytes != null) 
	            {
	            	BitmapFactory.Options options = new BitmapFactory.Options();
	            	options.inJustDecodeBounds = true;
	            	
	            	BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.length, options);
	            	
	            	options.inSampleSize = calculateInSampleSize(options, Size, Size);
	            		            	
	            	options.inJustDecodeBounds = false;
	                thumbnail = BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.length, options);
	            }
	        }
	        return thumbnail;
	    }
	    finally {
	        cursor.close();
	    }
	}
	
	//----------------------------------------------------------------------------------------------
	public static int calculateInSampleSize(BitmapFactory.Options opt, int reqWidth, int reqHeight)
	{
		final int W = opt.outWidth;
		final int H = opt.outHeight;
		
		int outSample = 1;
		
		if (H > reqHeight || W > reqWidth)
		{
			int half_W = W / 2;
			int half_H = H / 2;
			
			while ((half_W / outSample) > reqWidth && (half_H / outSample) > reqHeight){
				outSample *= 2;
			}
		}
		return outSample;
	}
}
