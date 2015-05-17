package com.bn.Main;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class DrawActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.setFinishOnTouchOutside(false);
		//设置为全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,  
		              WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 设置为横屏模式
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.draw_main);
		
		
	}

}
