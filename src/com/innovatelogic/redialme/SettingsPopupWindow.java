package com.innovatelogic.redialme;

import java.util.Map;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

public class SettingsPopupWindow 
{
	private MainActivity mActivity = null;
	private PopupWindow mPopupWindow = null;
	
	private String mSelectedTerrKey = null;
	private String mSelectedProviderKey = null;
	
	//----------------------------------------------------------------------------------------------
	SettingsPopupWindow(MainActivity activity)
	{
		mActivity = activity;
	}

	//----------------------------------------------------------------------------------------------
	public boolean IsVisible() { return mPopupWindow != null; }

	//----------------------------------------------------------------------------------------------
	public void Toggle(boolean bFlag)
	{
		if (bFlag && mPopupWindow == null)
		{
			LayoutInflater layoutInflater = (LayoutInflater) mActivity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    
	    	final View popupView = layoutInflater.inflate(R.layout.activity_settings, null);
	    	
	    	mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	    	
	    	mPopupWindow.setFocusable(false);
	    	mPopupWindow.setTouchable(true); 
	    	mPopupWindow.setOutsideTouchable(true);	
	     	
	    	//ViewGroup decor = (ViewGroup) mActivity.getWindow().getDecorView().findViewById(android.R.id.content);
	    	//View root = (ViewGroup) decor.getChildAt(0);
	    	//mPopupWindow.showAtLocation(root, Gravity.CENTER, 0, 0);
	    	
	    	Button btnConfigure = (Button)popupView.findViewById(R.id.buttonOptionsConfigure);
	    	
	    	btnConfigure.setOnClickListener(new Button.OnClickListener()
	    	{
	    		@Override
		    	public void onClick(View v)
		    	{
	    			ToggleCountrySelector();
		    	}
	    	});
	    	
	    	popupView.post(new Runnable() 
	    	{
	            public void run() 
	            {
	            	ViewGroup decor = (ViewGroup) mActivity.getWindow().getDecorView().findViewById(android.R.id.content);
	    	    	View root = (ViewGroup) decor.getChildAt(0);
	    	    	
	            	mPopupWindow.showAtLocation(root, Gravity.CENTER, 0, 0);
	            }
	        });
		}
		else if (!bFlag && mPopupWindow != null)
		{
			mPopupWindow.dismiss();
			mPopupWindow = null;		
		}
	}
	
	//----------------------------------------------------------------------------------------------
	private void ToggleCountrySelector()
	{
		Map<String, TerritoryEntry> map = mActivity.GetProviderStore().GetTerritoryEntries();
		int size = map.size();
		
		final String [] arrayCountry = new String[map.size() + 1];
		final String [] arrayKeys = new String[map.size()];
		
		int index = 0;
		for (Map.Entry<String, TerritoryEntry> entry : map.entrySet())
		{
			arrayKeys[index] = entry.getKey();
			arrayCountry[index] = entry.getValue().GetAlias();
			index++;
		}
	    
		arrayCountry[size] = mActivity.getString(R.string.not_in_list);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		
		builder.setTitle("Pick your choice").setItems(arrayCountry, new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int selected) 
			{
				Map<String, TerritoryEntry> map = mActivity.GetProviderStore().GetTerritoryEntries();
				
				int size = map.size();
				
				if (selected < arrayKeys.length)
				{
					TerritoryEntry entry = mActivity.GetProviderStore().GetTerritory(arrayKeys[selected]);
					
					if (entry != null)
					{
						mSelectedTerrKey = arrayKeys[selected];
								
						ToggleProviderSelector(entry);
					
						Toast.makeText(mActivity.getApplicationContext(), "U clicked " + arrayCountry[selected], Toast.LENGTH_LONG).show();
					}
					else
					{
						// ERROR
					}
				}
				else
				{
					// quit
				}
			}
		});
		builder.show();
	}
	
	//----------------------------------------------------------------------------------------------
	private void ToggleProviderSelector(TerritoryEntry territory)
	{
		Map<String, ProviderEntry> providers = territory.GetProviders();
		
		final String [] arrayKeys = new String[providers.size()];
		final String [] arrayProvider = new String[providers.size() + 1];
		
		int index = 0;
		for (Map.Entry<String, ProviderEntry> entry : providers.entrySet())
		{
			arrayKeys[index] = entry.getKey();
			arrayProvider[index] = entry.getValue().GetAliasName();
			index++;
		}
		
		arrayProvider[providers.size()] = mActivity.getString(R.string.not_in_list);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		
		builder.setTitle(mActivity.getString(R.string.pick_choice)).setItems(arrayProvider, new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int selected)
			{
				if (selected < arrayKeys.length)
				{
					Map<String, TerritoryEntry> mapTerritory = mActivity.GetProviderStore().GetTerritoryEntries();

					TerritoryEntry territory = mapTerritory.get(mSelectedTerrKey);
					if (territory != null)
					{
						Map<String, ProviderEntry> providers = territory.GetProvidersAlias();
						
						ProviderEntry provider = providers.get(arrayKeys[selected]);
						if (provider != null)
						{
							// TODO Auto-generated method stub
							Toast.makeText(mActivity.getApplicationContext(), "U clicked " + provider.GetAliasName(), Toast.LENGTH_LONG).show();
						}
						else
						{
							// error
						}
					}
					else 
					{
						//ERROR
					}
				}
				else
				{
					// QUIT
				}
			}
		});
		builder.show();
	}
}
