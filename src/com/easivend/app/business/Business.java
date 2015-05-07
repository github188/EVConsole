package com.easivend.app.business;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.easivend.app.maintain.MaintainActivity;
import com.easivend.common.MediaFileAdapter;
import com.easivend.common.ToolClass;
import com.easivend.evprotocol.EVprotocolAPI;
import com.example.evconsole.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class Business extends Activity
{
	TextView txtadsTip=null;
	Button btnads1=null,btnads2=null,btnads3=null,btnads4=null,btnads5=null,btnads6=null,
		   btnads7=null,btnads8=null,btnads9=null,btnadscancel=null,btnads0=null,btnadsenter=null,
		   btnadsclass=null,btnadscuxiao=null,btnadsbuysale=null,btnadsquhuo=null;	
	Intent intent=null;
	//VideoView
	private VideoView videoView=null;
	private File filev;
	private int curIndex = 0,isClick=0;//  
    Random r=new Random(); 
    private List<String> mMusicList = new ArrayList<String>();  
    //ImageView
    private ImageView ivads=null;
    private List<String> imgMusicList = new ArrayList<String>();  
    private boolean viewvideo=false;
    private final int SPLASH_DISPLAY_LENGHT = 30000; // 延迟30秒
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.business);
		videoView=(VideoView)findViewById(R.id.video);
		ivads=(ImageView)findViewById(R.id.ivads);
		//动态设置控件高度
    	//
    	DisplayMetrics  dm = new DisplayMetrics();  
        //取得窗口属性  
        getWindowManager().getDefaultDisplay().getMetrics(dm);  
        //窗口的宽度  
        int screenWidth = dm.widthPixels;          
        //窗口高度  
        int screenHeight = dm.heightPixels;      
        ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<屏幕"+screenWidth
				+"],["+screenHeight+"]");	
		
    	LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) videoView.getLayoutParams(); // 取控件videoView当前的布局参数
    	//linearParams.height =  (int)screenHeight/3;// 当控件的高强制设成75象素
    	linearParams.weight= screenHeight;
    	videoView.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件mGrid2
    	ivads.setLayoutParams(linearParams);
    	
		listFiles(); 
		startVideo();		
		//=======
		//操作模块
		//=======
		txtadsTip = (TextView) findViewById(R.id.txtadsTip);
		btnads1 = (Button) findViewById(R.id.btnads1);
		btnads2 = (Button) findViewById(R.id.btnads2);
		btnads3 = (Button) findViewById(R.id.btnads3);
		btnads4 = (Button) findViewById(R.id.btnads4);
		btnads5 = (Button) findViewById(R.id.btnads5);
		btnads6 = (Button) findViewById(R.id.btnads6);
		btnads7 = (Button) findViewById(R.id.btnads7);
		btnads8 = (Button) findViewById(R.id.btnads8);
		btnads9 = (Button) findViewById(R.id.btnads9);
		btnadscancel = (Button) findViewById(R.id.btnadscancel);
		btnads0 = (Button) findViewById(R.id.btnads0);
		btnadsenter = (Button) findViewById(R.id.btnadsenter);
		btnadsclass = (Button) findViewById(R.id.btnadsclass);
		btnadsclass.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	intent = new Intent(Business.this, BusgoodsClass.class);// 使用Accountflag窗口初始化Intent
                startActivity(intent);// 打开Accountflag
		    }
		});
		btnadscuxiao = (Button) findViewById(R.id.btnadscuxiao);
		btnadsbuysale = (Button) findViewById(R.id.btnadsbuysale);
		btnadsquhuo = (Button) findViewById(R.id.btnadsquhuo);
	}
	
	 /* 播放列表 */  
    private void listFiles() 
    {  
    	//遍历这个文件夹里的所有文件
		File file = new File("/sdcard/ads/");
		File[] files = file.listFiles();
		if (files.length > 0) 
		{  
			for (int i = 0; i < files.length; i++) 
			{
			  if(!files[i].isDirectory())
			  {		
				  ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品1ID="+files[i].toString());
				  //是否视频文件
				  if(MediaFileAdapter.isVideoFileType(files[i].toString())==true)
				  {
					  ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品videoID="+files[i].toString());
					  mMusicList.add(files[i].toString());
				  }
				  //是否图片文件
				  else if(MediaFileAdapter.isImgFileType(files[i].toString())==true)
				  {
					  ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<商品imageID="+files[i].toString());
					  imgMusicList.add(files[i].toString());
				  }
			  }
			}
		}    
    } 
    //打开播放器
    private void startVideo()
    { 
    	videoView.requestFocus(); 
    	show();  
    	//播放完成事件监听器
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {  
  
                    @Override  
                    public void onCompletion(MediaPlayer mp) {  
                        // TODO Auto-generated method stub  
                    	show();//播放完毕再继续下一首  
                    }  
                });  
        //播放出错事件监听器        
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {  
              
            @Override  
            public boolean onError(MediaPlayer mp, int what, int extra) {  
                // TODO Auto-generated method stub  
            	show();//播放出错再继续下一首  
                return true;  
            }  
        });  
        
        //单击事件监听器
        videoView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
//				if(isClick==0)
//				{
//					onPause();	
//					isClick=1;
//				}
//				else 
//				{
//					onResume();	
//					isClick=0;
//				}
				return true;
			}
		});       
        
	}  
    //图片和视频切换显示
    private void show()
    {
    	//播放视频
    	if(viewvideo==false)
    	{
    		viewvideo=true;
    		ivads.setVisibility(View.GONE);//图片关闭
    		videoView.setVisibility(View.VISIBLE);//视频打开
    		play();
    	}
    	//播放图片
    	else 
    	{
    		viewvideo=false;
    		videoView.setVisibility(View.GONE);//视频关闭
    		ivads.setVisibility(View.VISIBLE);//图片打开
    		showImage();
		}
    }
    //显示图片
    private void showImage()
    {  
        curIndex=r.nextInt(imgMusicList.size()); 
        try 
		{
	        /*为什么图片一定要转化为 Bitmap格式的！！ */
	        Bitmap bitmap = ToolClass.getLoacalBitmap(imgMusicList.get(curIndex)); //从本地取图片(在cdcard中获取)  //
	        ivads.setImageBitmap(bitmap);// 设置图像的二进制值
	        //延时10s
	        new Handler().postDelayed(new Runnable() 
			{
                @Override
                public void run() 
                {
                	viewvideo=true;
            		ivads.setVisibility(View.GONE);//图片关闭
            		videoView.setVisibility(View.VISIBLE);//视频打开
            		play();
                }

			}, SPLASH_DISPLAY_LENGHT);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}	
    }  
    //播放视频
    private void play()
    {  
        curIndex=r.nextInt(mMusicList.size()); 
        try 
		{
	        videoView.setVideoPath(mMusicList.get(curIndex));  
	        videoView.start(); 
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}	
    }  
    
    //暂停
    @Override  
    protected void onPause() {  
        // TODO Auto-generated method stub  
        super.onPause();  
        if(videoView!=null&&videoView.isPlaying()){  
            videoView.pause();  
        }  
          
    }  
  
    //恢复
    @Override  
    protected void onResume() {  
        // TODO Auto-generated method stub  
        super.onResume();  
        startVideo();  
    } 
    
    
}
