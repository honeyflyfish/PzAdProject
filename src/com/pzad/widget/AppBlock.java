package com.pzad.widget;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pzad.Constants;
import com.pzad.broadcast.StatisticReceiver;
import com.pzad.concurrency.PzExecutorFactory;
import com.pzad.entities.AppInfo;
import com.pzad.entities.Statistic;
import com.pzad.graphics.PzRoundCornerDrawable;
import com.pzad.net.ApkDownloadProvider;
import com.pzad.net.FileLoader;
import com.pzad.net.api.Downloadable;
import com.pzad.services.FloatWindowService;
import com.pzad.utils.CalculationUtil;
import com.pzad.utils.PLog;

public class AppBlock extends RelativeLayout implements Downloadable{

	private UrlImageView appIconView;
	private TextView appName;
	private TextView appSize;
	private PzRatingBar ratingBar;
	
	private ProgressBar progressBar;
	
	private View dividerHorizontal;
	private View dividerVertical;
	
	private AppInfo appInfo;
	
	public AppBlock(Context context){
		this(context, null);
	}
	
	public AppBlock(Context context, AttributeSet attrs){
		this(context, attrs, 0);
	}
	
	public AppBlock(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		
		appIconView = new UrlImageView(context, attrs, defStyle);
		appIconView.setId(1024 * 256);
		int size = CalculationUtil.dip2px(context, 50);
		RelativeLayout.LayoutParams appIconLayoutParams = new RelativeLayout.LayoutParams(size, size);
		appIconLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		int margin = CalculationUtil.dip2px(context, 15);
		appIconLayoutParams.setMargins(margin, margin, margin, margin / 2);
		
		addView(appIconView, appIconLayoutParams);
		
		ColorStateList appNameStateList = new ColorStateList(new int[][]{new int[]{android.R.attr.state_pressed}, new int[]{-android.R.attr.state_pressed}}, new int[]{Constants.GLOBAL_HIGHLIGHT_COLOR, 0xFF000000});
		
		appName = new TextView(context, attrs, defStyle);
		appName.setId(1024 * 255);
		appName.setSingleLine(true);
		appName.setEllipsize(TruncateAt.END);
		appName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
		appName.setTextColor(appNameStateList);
		appName.setGravity(Gravity.CENTER);
		appName.setPadding(appName.getPaddingLeft() + 2, appName.getPaddingTop(), appName.getPaddingRight() + 2, appName.getPaddingBottom());
		RelativeLayout.LayoutParams appNameLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		appNameLayoutParams.addRule(RelativeLayout.BELOW, appIconView.getId());
		appNameLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		int appNameMargin = CalculationUtil.dip2px(context, 5);
		appNameLayoutParams.setMargins(appNameMargin, 0, appNameMargin, 0);
		
		addView(appName, appNameLayoutParams);
		
		ColorStateList appSizeStateList = new ColorStateList(new int[][]{new int[]{android.R.attr.state_pressed}, new int[]{-android.R.attr.state_pressed}}, new int[]{(Constants.GLOBAL_HIGHLIGHT_COLOR & 0xFFFFFF) | 0x66000000, 0x66000000});
		
		appSize = new TextView(context, attrs, defStyle);
		appSize.setId(1024 * 254);
		appSize.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		appSize.setTextColor(appSizeStateList);
		appSize.setGravity(Gravity.LEFT);
		RelativeLayout.LayoutParams appSizeLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		appSizeLayoutParams.addRule(RelativeLayout.BELOW, appName.getId());
		appSizeLayoutParams.addRule(RelativeLayout.ALIGN_LEFT, appName.getId());
        
        addView(appSize, appSizeLayoutParams);
        
        ratingBar = new PzRatingBar(context, attrs);
        RelativeLayout.LayoutParams ratingLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, CalculationUtil.dip2px(context, 30));
        ratingLayoutParams.addRule(RelativeLayout.BELOW, appSize.getId());
        ratingLayoutParams.setMargins(appNameMargin * 2, 0, appNameMargin * 2, 10);
        
        addView(ratingBar, ratingLayoutParams);
        ratingBar.setRating(Math.round(Math.random() * 10) * 0.5F);
        
        progressBar = new ProgressBar(context, attrs, defStyle);
        RelativeLayout.LayoutParams progressParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        progressParams.addRule(RelativeLayout.BELOW, appSize.getId());
        progressParams.setMargins(appNameMargin * 2, CalculationUtil.dip2px(context, 10), appNameMargin * 2, CalculationUtil.dip2px(context, 5) + 10);
        
        addView(progressBar, progressParams);
        progressBar.setVisibility(View.GONE);
		
		int backgroundCorner = CalculationUtil.dip2px(context, 4);
		StateListDrawable stateDrawable = new StateListDrawable();
		stateDrawable.addState(new int[]{android.R.attr.state_pressed}, new PzRoundCornerDrawable(backgroundCorner, 0xFFEEEEEE, 1, 0, 0, 0x33000000));
		//int lightHighLight = (Constants.GLOBAL_HIGHLIGHT_COLOR & 0xFFFFFF) | 0x10000000;
		stateDrawable.addState(new int[]{-android.R.attr.state_pressed}, new PzRoundCornerDrawable(backgroundCorner, 0xFFFFFFFF, 3, 0, 2, 0x33000000));
		
		/*
		
		int dividerMargin = CalculationUtil.dip2px(context, 20);
		dividerVertical = new View(context, attrs, defStyle);
		dividerVertical.setBackgroundColor(Constants.GLOBAL_SHADOW_COLOR);
		RelativeLayout.LayoutParams verticalDividerParams = new RelativeLayout.LayoutParams(1, RelativeLayout.LayoutParams.FILL_PARENT);
		verticalDividerParams.setMargins(0, dividerMargin, 0, dividerMargin);
		verticalDividerParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		addView(dividerVertical, verticalDividerParams);
		
		dividerHorizontal = new View(context, attrs, defStyle);
		dividerHorizontal.setBackgroundColor(Constants.GLOBAL_SHADOW_COLOR);
		RelativeLayout.LayoutParams horizontalDividerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, 1);
		horizontalDividerParams.setMargins(dividerMargin, 0, dividerMargin, 0);
		horizontalDividerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		addView(dividerHorizontal, horizontalDividerParams);
		 */
		
		setBackgroundDrawable(stateDrawable);
	}
	
	public void setAppInfo(final AppInfo appInfo){
		if(appInfo != null){
			this.appInfo = appInfo;
			appIconView.setImageUrl(appInfo.getIcon());
			appName.setText(appInfo.getName());
			appSize.setText(appInfo.getSize());
			setClickable(true);
			setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					Intent broadIntent = new Intent();
					broadIntent.setAction(StatisticReceiver.ACTION_RECEIVE_STATISTIC);
					
					Statistic s = new Statistic();
					s.setName(appInfo.getName(), Statistic.TYPE_APP);
					s.setDownloadCount(1);
					
					broadIntent.putExtra(StatisticReceiver.NAME, s);
					
					getContext().sendBroadcast(broadIntent);
					//getContext().sendBroadcast(new Intent(FloatWindowService.ACTION_HIDE_FLOAT_DETAIL));
					
					/*
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(appInfo.getDownloadLink()));
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					getContext().startActivity(intent);
					 */
					
					ApkDownloadProvider.getInstance(getContext()).runNewTask(appInfo.getDownloadLink(), appInfo.getName());
					
					/*
					if(progressBar.getVisibility() != View.VISIBLE){
						new FileLoader(getContext(), appInfo.getDownloadLink(), appInfo.getName()){
							
							@Override
							public void onPreExecute(){
								progressBar.setVisibility(View.VISIBLE);
								ratingBar.setVisibility(View.INVISIBLE);
							}
							
							@Override
							public void onProgress(float progress){
								PLog.d("progress", progress + "");
								progressBar.setProgress(progress);
							}
							
							@Override
							public void onFinish(File result){
								progressBar.setVisibility(View.GONE);
								ratingBar.setVisibility(View.VISIBLE);
								if(result != null && result.exists()){
									PLog.d("file", result.toString());
									
									Intent hideIntent = new Intent(FloatWindowService.ACTION_HIDE_FLOAT_DETAIL);
									hideIntent.setData(new Uri.Builder().scheme("package").build());
									getContext().sendBroadcast(hideIntent);
									
									PackageManager pm = getContext().getPackageManager();
									PackageInfo pkgInfo = pm.getPackageArchiveInfo(result.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
									if(pkgInfo != null){
										ApplicationInfo applicationInfo = pkgInfo.applicationInfo;
										applicationInfo.sourceDir = result.getAbsolutePath();
										applicationInfo.publicSourceDir = applicationInfo.sourceDir;
										
										Intent installationIntent = new Intent(FloatWindowService.ACTION_INSTALLATION_PROCESS);
										installationIntent.putExtra("package_name", applicationInfo.packageName);
										installationIntent.putExtra("app_name", appInfo.getName());
										installationIntent.setData(new Uri.Builder().scheme("package").build());
										
										getContext().sendBroadcast(installationIntent);
									}
									
									Intent installIntent = new Intent(Intent.ACTION_VIEW);
									installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									installIntent.setDataAndType(Uri.fromFile(result), "application/vnd.android.package-archive");
									getContext().startActivity(installIntent);
								}
								
								
							}
							
						}.executeOnExecutor(PzExecutorFactory.getApkLoadThreadPool());
					}
					 */
				}
			});
		}
	}
	
	public void setVerticalDividerVisible(int visibility){
		dividerVertical.setVisibility(visibility);
	}
	
	public void setHorizontalDividerVisible(int visibility){
		dividerHorizontal.setVisibility(visibility);
	}

	@Override
	public void refreshProgress(String downloadLink, float progress) {
		if(appInfo != null && appInfo.getDownloadLink().equals(downloadLink)){
			if(progressBar.getVisibility() == View.GONE){
				progressBar.setVisibility(View.VISIBLE);
				ratingBar.setVisibility(View.INVISIBLE);
			}
			progressBar.setProgress(progress);
		}
	}

	@Override
	public void onDownloadStart(String downloadLink) {
		if(appInfo != null && downloadLink.equals(appInfo.getDownloadLink())){
			progressBar.setVisibility(View.VISIBLE);
			ratingBar.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onDownloadComplete(String downloadLink, boolean isFileComplete, String resultFilePath) {
		progressBar.setVisibility(View.GONE);
		ratingBar.setVisibility(View.VISIBLE);
		
		if(isFileComplete && downloadLink != null && resultFilePath != null){
			File result = new File(resultFilePath);
			if(result != null && result.exists()){
				PLog.d("file", result.toString());
				
				Intent hideIntent = new Intent(FloatWindowService.ACTION_HIDE_FLOAT_DETAIL);
				hideIntent.setData(new Uri.Builder().scheme("package").build());
				getContext().sendBroadcast(hideIntent);
				
				PackageManager pm = getContext().getPackageManager();
				PackageInfo pkgInfo = pm.getPackageArchiveInfo(result.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
				if(pkgInfo != null){
					ApplicationInfo applicationInfo = pkgInfo.applicationInfo;
					applicationInfo.sourceDir = result.getAbsolutePath();
					applicationInfo.publicSourceDir = applicationInfo.sourceDir;
					
					Intent installationIntent = new Intent(FloatWindowService.ACTION_INSTALLATION_PROCESS);
					installationIntent.putExtra("package_name", applicationInfo.packageName);
					installationIntent.putExtra("app_name", appInfo.getName());
					installationIntent.setData(new Uri.Builder().scheme("package").build());
					
					getContext().sendBroadcast(installationIntent);
				}
				
				Intent installIntent = new Intent(Intent.ACTION_VIEW);
				installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				installIntent.setDataAndType(Uri.fromFile(result), "application/vnd.android.package-archive");
				getContext().startActivity(installIntent);
			}
		}
	}
}
