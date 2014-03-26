package com.pzad.category;

import android.content.Context;

import com.pzad.category.api.AdsCycleAPI;

public abstract class BaseAdsCategory implements AdsCycleAPI{
	
	public static final int CATEGORY_FLOAT_ADS = 0x1;
	public static final int CATEGORY_BANNER = 0x2;
	public static final int CATEGORY_POPUP_ON_RUN = 0x4;
	public static final int CATEGORY_POPUP_ON_EXIT = 0x8;
	
	private Context context;
	
	public BaseAdsCategory(Context context){
		this.context= context;
	}
	
	protected Context getContext(){
		return context;
	}
	
}