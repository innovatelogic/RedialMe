package com.innovatelogic.redialme;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.innovatelogic.redialme.MainActivity;

//----------------------------------------------------------------------------------------------
//
//----------------------------------------------------------------------------------------------
public class ActionPopupWindow implements OnItemSelectedListener
{
	enum EActionType
	{
		EProcessAction,
		ECancelAction,
	}

	private MainActivity mActivity;

	public int mContactID = -1;
	public String mName;
	public String mNumber;
	public ArrayList<String> mNumbersList = null;
	
	public Button mBtnAction;

	private PopupWindow mPopupWindow = null;
	
	private final int interval = 1000; // 1 Second
		
	private EActionType mActionType = EActionType.EProcessAction;
			
	private Handler mHandler = null;
	private Runnable mRunnable = null;
			
	//----------------------------------------------------------------------------------------------
	public ActionPopupWindow(MainActivity activity)
	{
		mActivity = activity;
		
		mHandler = new Handler();
		
		mNumbersList = new ArrayList<String>();
		
/*		LinearLayout parent = (LinearLayout) mActivity.findViewById(R.id.LayoutAdv);
		String strPublisherID = "4483553123";
		AdView ad = new AdView(mActivity.getApplicationContext(), AdSize.BANNER, strPublisherID);
		parent.addView(ad);
		AdRequest r = new AdRequest();
		r.setTesting(true);
		ad.loadAd(r);
*/
	}
	
	//----------------------------------------------------------------------------------------------
	public boolean IsVisible() { return mPopupWindow != null; }
	
	//----------------------------------------------------------------------------------------------
	public void Toggle(boolean bFlag)
	{
		if (bFlag && mPopupWindow == null)
		{
			LayoutInflater layoutInflater = (LayoutInflater) mActivity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    
	    	View popupView = layoutInflater.inflate(R.layout.activity_popupaction, null);
	    	
	    	mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	    	    	
	    	Button btnAction = (Button)popupView.findViewById(R.id.processActionPopUp);
	    	TextView txtName = (TextView)popupView.findViewById(R.id.popupname);
	    	TextView txtNumber = (TextView)popupView.findViewById(R.id.popupnumber);
	    	ImageView imageuser = (ImageView)popupView.findViewById(R.id.userpic);
	    	Spinner spinner = (Spinner)popupView.findViewById(R.id.spinnerNumbers);
	    	
	    	//View viewToLoad = layoutInflater.from(mActivity.getParent()).inflate(R.layout.activity_popupaction, null);  
	    	//mActivity.setContentView(viewToLoad);
	    	
	    	txtName.setText(mName);
	    	txtNumber.setText(mNumber);
	    	
	    	//spinner.setOnItemSelectedListener(this);
	    	
	    	boolean bDefault = true;
	    	
	    	if (mContactID >= 0)
	    	{
	    		Map<Integer, UserContactInfo> contacts = mActivity.getContactsStore().GetContactsStoreMap();
	    		UserContactInfo findInfo = contacts.get(mContactID);
	    		
	    		if (findInfo != null)
	    		{
	    			int thumbnailID = findInfo.thumbnailID;
					if (thumbnailID > 0)
					{
						Bitmap bitmap = MainActivity.fetchThumbnail(thumbnailID, mActivity.getApplicationContext()); 
						if (bitmap != null)
						{
							imageuser.setImageBitmap(bitmap);
							bDefault = false;
						}
					}
	    		}
	    	}
	    	
	    	if (bDefault){
        		imageuser.setImageResource(R.drawable.default_person);
			}

	    	btnAction.setBackgroundResource(R.layout.buttonstyle_action_process);
	    	btnAction.setText("CallMe");
	    	
	    	String[] data = {"one", "two", "three", "four", "five"};
	    	
	    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_dropdown_item, data);
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        
	        spinner.setAdapter(adapter);
	        spinner.setPrompt("Title");
	        spinner.setSelection(2);
	        /*
	        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
	        {
	        	@Override
	            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
	        	{
	              //Toast.makeText(getBaseContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
	            }
	            @Override
	            public void onNothingSelected(AdapterView<?> arg0) {
	            }
	        });*/
	    	
	    	ViewGroup decor = (ViewGroup) mActivity.getWindow().getDecorView().findViewById(android.R.id.content);
	    	View root = (ViewGroup) decor.getChildAt(0);
	     	
	    	mPopupWindow.showAtLocation(root, Gravity.CENTER, 0, 0);
	        //mPopupWindow.showAtLocation(mActivity.getParent(), Gravity.CENTER, 0, 0);
	    	
	    	btnAction.setOnClickListener(new Button.OnClickListener()
	    	{
	    		@Override
		    	public void onClick(View v)
		    	{
	    			// check style
	    			Button btnHandler = (Button) v;
	    			
	    			if (mActionType == EActionType.EProcessAction)
		    		{
	    				btnHandler.setBackgroundResource(R.layout.buttonstyle_action_cancel);
		    			btnHandler.setText("Cancel");
		    		
		    			StartDelayAction();
		    			
		    			mActionType = EActionType.ECancelAction;
		    		}
		    		else
		    		{
		    			mActionType = EActionType.EProcessAction;
		    			
		    			StopDelayAction();
		    			
		    			Toggle(false);
		    		}
		    	}
	    	});
	     	

		}
		else if (!bFlag && mPopupWindow != null)
		{
			mContactID = -1;
			mPopupWindow.dismiss();
			mPopupWindow = null;
		}
	}
	
	//----------------------------------------------------------------------------------------------
	void Process()
	{
		String operator = mActivity.GetCurrentOperator();
		ProviderEntry provider = mActivity.GetCurrentTerritory().GetProvider(operator);
		
		mActivity.GetActionBar().ProcessAction(provider, mNumber);
	}
	
	//----------------------------------------------------------------------------------------------
	private void StartDelayAction()
	{
		if (mRunnable == null)
		{
			// run timer
			mRunnable = new Runnable()
			{
			    public void run() 
			    {
			    	if (mPopupWindow != null && mActionType == EActionType.ECancelAction){
			    		Process();
			    	}
			    	Toggle(false);
			    }
			};
				  
			mHandler.postDelayed(mRunnable, interval * 3);
		}
	}
	
	//----------------------------------------------------------------------------------------------
	private void StopDelayAction()
	{
		if (mRunnable != null)
		{
			mHandler.removeCallbacks(mRunnable);
			mRunnable = null;
		}
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
	 
	
	}
	 
	public void onNothingSelected(AdapterView<?> arg0) {
	 
	}
}
