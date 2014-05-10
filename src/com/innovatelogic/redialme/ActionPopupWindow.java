package com.innovatelogic.redialme;

import java.util.ArrayList;
import java.util.List;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.innovatelogic.redialme.MainActivity;
import com.innovatelogic.redialme.MainActivity.ESizeType;

//----------------------------------------------------------------------------------------------
//
//----------------------------------------------------------------------------------------------
public class ActionPopupWindow
{
	enum EActionType
	{
		EProcessAction,
		ECancelAction,
	}
	
	private class MaskParser
	{
		public final String mTERR;
		public final String mPROVIDER;
		public final String mABONENT;
		
		public MaskParser(String terr, String provider, String abonent, boolean found)
		{
			mTERR = terr;
			mPROVIDER = provider;
			mABONENT = abonent;
		}
	}

	private MainActivity mActivity;

	public int mContactID = -1;
	public String mName;
	private ArrayList<String> mNumbersList = null;
	
	public Button mBtnAction;

	private PopupWindow mPopupWindow = null;

	private final int mInterval = 1000; // 1 Second
	private final int mIntervalAnim = 250; 
	private int mAnimCounter = 0;
		
	private EActionType mActionType = EActionType.EProcessAction;
			
	private Handler mHandler = null;
	private Runnable mRunnable = null;
	private Runnable mRunnableAnim = null;
	
	private int mSelectedNumber = -1;
	
	private Spinner mSpinnerNumber = null;
	private Spinner mSpinnerProvider = null;
	private ProviderEntry mUseProvider = null;
	
	private ImageView[] mAnimImages = new ImageView[3];
	
	//----------------------------------------------------------------------------------------------
	public ActionPopupWindow(MainActivity activity)
	{
		mActivity = activity;
		
		mHandler = new Handler();
		
		mNumbersList = new ArrayList<String>();
		
		mSpinnerNumber = null;
		mSpinnerProvider = null;
		
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
	public void Toggle(boolean bFlag, boolean bAutoStart)
	{
		if (bFlag && mPopupWindow == null)
		{
			mActivity.OnHideInputKeyboard();
			
			mActionType = EActionType.EProcessAction; // reset state
					
			mUseProvider = mActivity.GetProviderDefault();
			
			LayoutInflater layoutInflater = (LayoutInflater) mActivity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    
	    	View popupView = layoutInflater.inflate(R.layout.activity_popupaction, null);
	    	
	    	mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	    	    	
	    	ImageButton btnAction = (ImageButton)popupView.findViewById(R.id.processActionPopUp);
	    	TextView txtName = (TextView)popupView.findViewById(R.id.popupname);
	    	ImageView imageuser = (ImageView)popupView.findViewById(R.id.userpic);
	    	
	    	mSpinnerNumber = (Spinner)popupView.findViewById(R.id.spinnerNumbers);
	    	mSpinnerProvider = (Spinner)popupView.findViewById(R.id.spinnerProviders);
	    	
			mAnimImages[0] = (ImageView)popupView.findViewById(R.id.imageAnim0);
			mAnimImages[1] = (ImageView)popupView.findViewById(R.id.imageAnim1);
			mAnimImages[2] = (ImageView)popupView.findViewById(R.id.imageAnim2);
			
	    	txtName.setText(mName);
	    	txtName.setTextSize(mActivity.GetDefTextSize(ESizeType.ESizeMid));
	    	
	    	boolean bDefault = true;
	    	
	    	if (mContactID >= 0)
	    	{
	    		Map<Integer, UserContactInfo> contacts = mActivity.getContactsStore().GetContactsStoreMap();
	    		UserContactInfo findInfo = contacts.get(mContactID);
	    		
	    		if (findInfo != null)
	    		{
	    			// add numbers if necessary 
	    			for (String number : findInfo.ContactNumbers){
    					AddNumber(number);
    				}
	    			
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
        		imageuser.setImageResource(R.drawable.ic_action_person);
			}

	    	btnAction.setBackgroundResource(R.layout.buttonstyle_action_process);
	    	
	    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity, R.layout.spinner_style, mNumbersList);
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        
	        mSpinnerNumber.setAdapter(adapter);
	        mSpinnerNumber.setPrompt("Select number");
	        
	        mSelectedNumber = 0;
	        mSpinnerNumber.setSelection(mSelectedNumber);
	        
	        mSpinnerNumber.setOnItemSelectedListener(new OnItemSelectedListener()
	        {
		        public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) 
		        {
		        	mSelectedNumber = position;
		        }
		        public void onNothingSelected(AdapterView<?> parentView)
		        {       
		        }
	        });
	        
	        InitSpinerProviders(mSpinnerProvider);
	    	
	        ShowAnim(false);
	        
	    	ViewGroup decor = (ViewGroup) mActivity.getWindow().getDecorView().findViewById(android.R.id.content);
	    	View root = (ViewGroup) decor.getChildAt(0);
	     	
	    	mPopupWindow.showAtLocation(root, Gravity.CENTER, 0, 0);
	    	
	    	if (bAutoStart){
	    		ProcessBtnStart(btnAction);
	    	}
	    	
	    	btnAction.setOnClickListener(new Button.OnClickListener()
	    	{
	    		@Override
		    	public void onClick(View v)
		    	{
	    			// check style
	    			ImageButton btnHandler = (ImageButton) v;
	    			ProcessBtnStart(btnHandler);
		    	}
	    	});
		}
		else if (!bFlag && mPopupWindow != null)
		{
			StopAnimation();
			StopDelayAction();
			ClearNumberList();
			
			ShowAnim(false);
			
			mContactID = -1;
			mPopupWindow.dismiss();
			
			mSpinnerNumber = null;
			mSpinnerProvider = null;
			mPopupWindow = null;
		}
	}
	
	//----------------------------------------------------------------------------------------------	
	void ProcessBtnStart(ImageButton btn)
	{
		if (mActionType == EActionType.EProcessAction) // start
		{
			btn.setBackgroundResource(R.layout.buttonstyle_action_cancel);
			//btn.setText("Cancel");
		
			StartAnimation();
			StartDelayAction();

			mActionType = EActionType.ECancelAction;
		}
		else // cancel
		{
			mActionType = EActionType.EProcessAction;
			
			StopAnimation();
			StopDelayAction();
			
			Toggle(false, false);
		}
		
	}
	
	//----------------------------------------------------------------------------------------------
	void Process()
	{
		if (mUseProvider != null)
		{
			ProcessAction(mUseProvider, mNumbersList.get(mSelectedNumber));
		}
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
			    	Toggle(false, false);
			    }
			};
				  
			mHandler.postDelayed(mRunnable, mInterval * 3);
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
	
	//----------------------------------------------------------------------------------------------
	private void StartAnimation()
	{
		if (mRunnableAnim == null)
		{
			mAnimCounter = 0;
			
			ShowAnim(true);
			
			// run timer
			mRunnableAnim = new Runnable()
			{
			    public void run() 
			    {
			    	ProcessAnim();
			    }
			};
				  
			mHandler.postDelayed(mRunnableAnim, mIntervalAnim);
		}
	}
	
	//----------------------------------------------------------------------------------------------
	private void StopAnimation()
	{
		if (mRunnableAnim != null)
		{
			for (int index = 0; index < 3; index++)
				mAnimImages[index].setVisibility(0);
			
			mHandler.removeCallbacks(mRunnableAnim);
			mRunnableAnim = null;
		}
	}
	
	//----------------------------------------------------------------------------------------------
	private void ProcessAnim()
	{
		int active = mAnimCounter % 3;
		
		for (int index = 0; index < 3; index++)
		{
			mAnimImages[index].setImageResource((index == active) ? R.drawable.ic_action_dot_filled : R.drawable.ic_action_dot_empty);
		}
		
		mHandler.postDelayed(mRunnableAnim, mIntervalAnim);
		mAnimCounter++;
	}
	
	//----------------------------------------------------------------------------------------------
	private void ShowAnim(boolean bFlag)
	{
		for (int index = 0; index < 3; index++){
			
			mAnimImages[index].setVisibility(bFlag == true ? View.VISIBLE : View.INVISIBLE);
		}
		
	}
		
	//----------------------------------------------------------------------------------------------
	public void AddNumber(String number)
	{
		if (!mNumbersList.contains(number))
		{
			mNumbersList.add(number);
		}
	}
	
	//----------------------------------------------------------------------------------------------
	public void ClearNumberList()
	{
		mNumbersList.clear();
	}
	
	//----------------------------------------------------------------------------------------------
	public void InitSpinerProviders(Spinner spinner)
	{
		int selectedElement = -1;
		int Index = 0;
		
		ArrayList<String> mAdapterList = new ArrayList<String>();
		
		Map<String, TerritoryEntry> territories = mActivity.GetProviderStore().GetTerritoryEntries();
		
		for (Map.Entry<String, TerritoryEntry> entry : territories.entrySet())
		{
			Map<String, ProviderEntry> providers = entry.getValue().GetProviders();
			for (Map.Entry<String, ProviderEntry> entPrv : providers.entrySet())
			{
				mAdapterList.add(entPrv.getValue().GetAliasName());
				
				if (mUseProvider != null && mUseProvider.equals(entPrv.getValue())){
					selectedElement = Index;
				}
				++Index;
			}
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity, R.layout.spinner_style, mAdapterList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        spinner.setAdapter(adapter);
        spinner.setPrompt("Select provider");
        
        if (selectedElement != -1){
        	spinner.setSelection(selectedElement);
        }
        	
        spinner.setOnItemSelectedListener(new OnItemSelectedListener()
        {
	        public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) 
	        {
	        	int Index = 0;
				Map<String, TerritoryEntry> territories = mActivity.GetProviderStore().GetTerritoryEntries();
				
				for (Map.Entry<String, TerritoryEntry> entry : territories.entrySet())
				{
					Map<String, ProviderEntry> providers = entry.getValue().GetProviders();
					for (Map.Entry<String, ProviderEntry> entPrv : providers.entrySet())
					{
						if (Index == position)
						{
							mUseProvider = entPrv.getValue();
							break;
						}
						++Index;
					}
				}
	        }
	        public void onNothingSelected(AdapterView<?> parentView)
	        {
	        
	        }
        });
	}
	
	//----------------------------------------------------------------------------------------------
	public void ProcessAction(ProviderEntry provider, String number)
    {
    	List<IUserOperation> operations = provider.GetOperationList();
		
		for (IUserOperation op : operations)
		{
			if (op instanceof UserOperationSMS || op instanceof UserOperationCall)
			{
				MaskParser info = ParseNumber(number);
				
				String mask = op.GetMask();
				
				mask = mask.replace("%TERR%", info.mTERR);
				mask = mask.replace("%PRV%", info.mPROVIDER);
				mask = mask.replace("%NUM%", info.mABONENT);
				
				op.Process(mask);
				break;
			}
		}
    }
	
    //----------------------------------------------------------------------------------------------
    private MaskParser ParseNumber(String number)
    {
    	boolean bFound = false;
    	
    	String TERR = mActivity.GetCurrentTerritory().GetCode();
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
