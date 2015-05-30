package com.bn.Main;

import com.bn.Object.Body;
import com.bn.Main.MainActivity;
import com.bn.Main.MenuActivity;
import com.bn.Main.R;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {
	private MySurfaceView mGLSurfaceView;
	public Button menu, fill, object, property, delete, cylinder, cube, ball, cone, bool, jiao, bing, cha;
	public LinearLayout first, slide1, slide2;
	public RelativeLayout left,right;		//左右边相对布局
	ObjectAnimator in1, out1, in2, out2;		//回收动画和下拉动画
	boolean ifin1=true, ifin2=true;		//记录fill和object按钮是否下拉
    @Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
        //设置为全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,  
		              WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 设置为横屏模式
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		//切换到主界面
		setContentView(R.layout.main);	
		
		ShaderManager.loadCodeFromFile(MainActivity.this.getResources());
        
		//初始化GLSurfaceView
		mGLSurfaceView = (MySurfaceView)findViewById(R.id.mysurfaceview);
		mGLSurfaceView.requestFocus();//获取焦点
		mGLSurfaceView.setFocusableInTouchMode(true);//设置为可触控  
		
		menu = (Button)findViewById(R.id.menu);
		fill = (Button)findViewById(R.id.fill);
		object = (Button)findViewById(R.id.object);
		property = (Button)findViewById(R.id.object);
		
		bool = (Button)findViewById(R.id.bool);
		jiao = (Button)findViewById(R.id.jiao);
		bing = (Button)findViewById(R.id.bing);
		cha = (Button)findViewById(R.id.cha);
		
		cylinder = (Button)findViewById(R.id.cylinder);
		cube = (Button)findViewById(R.id.cube);
		ball = (Button)findViewById(R.id.ball);
		cone = (Button)findViewById(R.id.cone);
		
		delete = (Button)findViewById(R.id.delete);
		
		first = (LinearLayout)findViewById(R.id.first_linear);
		slide1 = (LinearLayout)findViewById(R.id.slide1);
		slide2 = (LinearLayout)findViewById(R.id.slide2);
		
		left = (RelativeLayout)findViewById(R.id.left_relative);
		right = (RelativeLayout)findViewById(R.id.right_relative);
		
		menu.bringToFront();
		bool.bringToFront();
		delete.bringToFront();
		first.bringToFront();
		left.bringToFront();
		right.bringToFront();
		
		//设置下拉动画
		slide1.setAlpha(0);
		slide2.setAlpha(0);
		PropertyValuesHolder outx = PropertyValuesHolder.ofFloat("TranslationY", 200);
		PropertyValuesHolder outa = PropertyValuesHolder.ofFloat("Alpha", 1);
		out1 = ObjectAnimator.ofPropertyValuesHolder(slide1, outx, outa);
		out1.setDuration(500);
		out1.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				slide1.invalidate();
			}
		});
		
		out2 = ObjectAnimator.ofPropertyValuesHolder(slide2, outx, outa);
		out2.setDuration(500);
		out2.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				slide2.invalidate();
			}
		});
		
		PropertyValuesHolder inx = PropertyValuesHolder.ofFloat("TranslationY", 0);
		PropertyValuesHolder ina = PropertyValuesHolder.ofFloat("Alpha", 0);
		in1 = ObjectAnimator.ofPropertyValuesHolder(slide1, inx, ina);
		in1.setDuration(500);
		in1.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				slide1.invalidate();
			}
		});
		
		in2 = ObjectAnimator.ofPropertyValuesHolder(slide2, inx, ina);
		in2.setDuration(500);
		in2.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				slide2.invalidate();
			}
		});
		
		menu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(ifin1) {
					out1.start();
					slide1.setClickable(true);
					ifin1=false;
				}
				else {
					in1.start();
					slide1.setClickable(false);
					ifin1=true;
				}
			}
		});
		
		bool.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(ifin2) {
					out2.start();
					slide2.setClickable(true);
					ifin2=false;
				}
				else {
					in2.start();
					slide2.setClickable(false);
					ifin2=true;
				}
			}
		});
		
		//添加各按钮的点击事件
		menu.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Intent intent = new Intent(MainActivity.this, MenuActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
				return true;
			}
		});
		
		
        cube.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v)
        	{
        	
        		mGLSurfaceView.isCreateNormal=true;
        		mGLSurfaceView.createType=1;
        		mGLSurfaceView.isCreateNew=true;
        		//mGLSurfaceView.createObject(1,true);
        	}
    	});
        cylinder.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v)
        	{
        		mGLSurfaceView.isCreateNormal=true;
        		mGLSurfaceView.createType=2;
        		mGLSurfaceView.isCreateNew=true;	
        		//mGLSurfaceView.createObject(2,true);
        	}
    	});
        cone.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v)
        	{
        		mGLSurfaceView.isCreateNormal=true;
        		mGLSurfaceView.createType=3;
        		mGLSurfaceView.isCreateNew=true;	
        		//mGLSurfaceView.createObject(3,true);
        	}
    	});
        ball.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v)
        	{
        		mGLSurfaceView.isCreateNormal=true;
        		mGLSurfaceView.createType=4;
        		mGLSurfaceView.isCreateNew=true;
        		//mGLSurfaceView.createObject(4,true);
        	}
    	});
        delete.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v)
        	{
        		mGLSurfaceView.BodyAll.remove(mGLSurfaceView.curBody);
        		if(mGLSurfaceView.BodyAll.size()!=0)
        		{
        			mGLSurfaceView.curBody=mGLSurfaceView.BodyAll.get(mGLSurfaceView.BodyAll.size()-1);
	    			//当前被选图元
	    			for(Body b:mGLSurfaceView.BodyAll)
	    	        {
	    	    		if(b.isChoosed)
	    	    			b.isChoosed=false;
	    	        }
	    			mGLSurfaceView.curBody.isChoosed=true;
        		}
        	}
    	});
        object.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v)
        	{
        		if(mGLSurfaceView.isObject)
        			mGLSurfaceView.isObject = false;
    			else
    				mGLSurfaceView.isObject= true;
        	}
    	});
        fill.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v)
        	{
        		if(MySurfaceView.isFill)
        			MySurfaceView.isFill = false;
    			else
    				MySurfaceView.isFill = true;
        	}
    	});
        
  		property.setOnLongClickListener(new OnLongClickListener() {
  			@Override
  			public boolean onLongClick(View v) {
  				/*
  				float[] euler = mGLSurfaceView.curBody.quater.getEulerAxis();
  				Intent intent = new Intent(MainActivity.this, DrawActivity.class);
  				Bundle bundle = new Bundle();
  				float[] bodyPro=new float[]{
  						mGLSurfaceView.curBody.xLength,
  						mGLSurfaceView.curBody.yLength,
  						mGLSurfaceView.curBody.zLength,
  						mGLSurfaceView.curBody.xScale,
  						mGLSurfaceView.curBody.yScale,
  						mGLSurfaceView.curBody.zScale,
  						euler[0],
  						euler[1],
  						euler[2]};
  				bundle.putFloatArray("curBody", bodyPro);
  				startActivity(intent);
  				overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
  				*/
  				return true;
  			}
  		});
        jiao.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v)
        	{
        		mGLSurfaceView.isBool=true;
        		mGLSurfaceView.boolMode=1;
        	}
    	});
        bing.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v)
        	{
        		mGLSurfaceView.isBool=true;
        		mGLSurfaceView.boolMode=2;
        	}
    	});
        cha.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v)
        	{
        		mGLSurfaceView.isBool=true;
        		mGLSurfaceView.boolMode=3;
        	}
    	});
	}

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause(); 
    } 
}