package com.easivend.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.easivend.app.business.BusPort;
import com.easivend.app.business.BusPort.BusPortMovieFragInteraction;
import com.easivend.common.MediaFileAdapter;
import com.easivend.common.ToolClass;
import com.easivend.dao.vmc_system_parameterDAO;
import com.easivend.model.Tb_vmc_system_parameter;
import com.easivend.view.MyVideoView;
import  com.example.evconsole.R;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

public class MoviewlandFragment extends Fragment {
	//VideoView
	private MyVideoView videoView=null;
	private int curIndex = 0;//  
    Random r=new Random(); 
    private List<String> mMusicList = new ArrayList<String>();  
    private WebView webtishiInfo;
    private ImageView ivads=null,ivmobile=null;
    private TextView txtcashlessamount=null;
    private List<String> imgMusicList = new ArrayList<String>();  
    private boolean viewvideo=false;//true���ڲ�����Ƶ,falseû�в�����Ƶ
    private boolean videopause=false;//true���ڽ��ף���������Ƶ,false����״̬�����Բ�����Ƶ
    private final int SPLASH_DISPLAY_LENGHT = 30000; // �ӳ�30��
    
    //=========================
    //fragment��activity�ص����
    //=========================
    /**
     * �������ⲿactivity������
     */
    private MovieFragInteraction listterner;
    /**
     * �����ġ���ContentFragment�����ص�activity��ʱ������ע��ص���Ϣ
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(activity instanceof MovieFragInteraction)
        {
            listterner = (MovieFragInteraction)activity;
        }
        else{
            throw new IllegalArgumentException("activity must implements MovieFragInteraction");
        }

    }
    /**
     * ����һ������������activity����ʵ�ֵĽӿ�
     */
    public interface MovieFragInteraction
    {
        /**
         * Fragment ��Activity����ָ�����������Ը�������������
         * @param str
         */
        void switchBusiness();
    }
    @Override
    public void onDetach() {
        super.onDetach();

        listterner = null;
    }
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_movieland, container, false);  
		txtcashlessamount=(TextView)view.findViewById(R.id.txtcashlessamount);
		txtcashlessamount.setVisibility(View.GONE);//�����ر�		
		videoView=(MyVideoView)view.findViewById(R.id.video);
		//�õ���ʾ����
		webtishiInfo = (WebView) view.findViewById(R.id.webtishiInfo); 
		ivads=(ImageView)view.findViewById(R.id.ivads);
		ivads.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				changefragment();
			}
		});
		//�õ�����״̬
		ivmobile=(ImageView)view.findViewById(R.id.ivmobile);
		switch(ToolClass.getNetType())
		{
			case 1:
				ivmobile.setImageResource(R.drawable.network);
				break;
			case 2:
				ivmobile.setImageResource(R.drawable.wifi);
				break;
			case 3:
				ivmobile.setImageResource(R.drawable.mobile);
				break;	
			case 4:
				ivmobile.setImageResource(R.drawable.nosignal);
				break;	
		}
		listFiles(); 
		if((mMusicList.size()==0)&&(imgMusicList.size()==0))
        {
            videoView.setVisibility(View.GONE);//�ر���Ƶ
            webtishiInfo.setVisibility(View.GONE);//�ر���ʾ
            ivads.setVisibility(View.VISIBLE);//��ͼƬ
        }
        else
        {
            startVideo();
        }

		/**
	     * ����������fragment������,
	     * �����塢��Fragment�����ص�activity��ʱ��ע��ص���Ϣ
	     * @param activity
	     */
		BusPort.setMovieCallBack(new buportIntermoviefaceImp());
		return view;
	}
	
	private class buportIntermoviefaceImp implements BusPortMovieFragInteraction//���ؽӿ�
	{
		/**
	     * ����������fragment������,
	     * ��������ʵ��BusPortFragInteraction�ӿ�
	     * @param activity
	     */		
		@Override
		public void BusportMovie(int infotype) {
			// TODO Auto-generated method stub
			 ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<��ʾ������ʾ��Ϣ="+infotype,"log.txt");
			 showtishiInfo(infotype);
		}
		
		@Override
		public void BusportAds() {
			// TODO Auto-generated method stub
			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<ˢ�¹���б�������","log.txt");
			mMusicList.clear();
			imgMusicList.clear();
			listFiles(); 
			startVideo();
		}
		
		@Override
		public void BusportCashless(String cashbalance) {
			// TODO Auto-generated method stub
			ToolClass.Log(ToolClass.INFO,"EV_COM","APP<�����="+cashbalance,"com.txt");			
			txtcashlessamount.setVisibility(View.VISIBLE);
			txtcashlessamount.setText("�����:"+cashbalance);
			//��ʱ
		    new Handler().postDelayed(new Runnable() 
			{
	            @Override
	            public void run() 
	            {   
	            	txtcashlessamount.setText("");
	            	txtcashlessamount.setVisibility(View.GONE);
				}

			}, 5000);
		}
				
		@Override
		//type=0��ͣ��Ƶ����,1�ָ���Ƶ����
		public void BusportVideoStop(int type) {
			// TODO Auto-generated method stub
			if(type==0)
			{
				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<��ͣ��Ƶ�ļ�","log.txt");
				//��Ƶ��ͼƬ�ļ���Ҫ�в���
		    	if((mMusicList.size()>0)||(imgMusicList.size()>0))
		    	{
		    		//������Ƶ
			    	if((viewvideo==true)&&(mMusicList.size()>0))
			    	{
			    		videoView.pause(); 
			    	}
			    	else
			    	{
			    		videopause=true;
			    	}
		    	}				
			}
			else if(type==1)
			{
				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<�ָ���Ƶ�ļ�","log.txt");
				//��Ƶ��ͼƬ�ļ���Ҫ�в���
		    	if((mMusicList.size()>0)||(imgMusicList.size()>0))
		    	{
		    		//������Ƶ
			    	if((viewvideo==true)&&(mMusicList.size()>0))
			    	{
			    		videoView.start();
			    	}
			    	else
			    	{
			    		videopause=false;
			    	}
		    	}				
			}
		}		
	}

	
	/* �����б� */  
    private void listFiles() 
    {  
    	//��������ļ�����������ļ�
		File file = new File(ToolClass.ReadAdsFile());
		File[] files = file.listFiles();
		if (files.length > 0) 
		{  
			for (int i = 0; i < files.length; i++) 
			{
			  if(!files[i].isDirectory())
			  {		
				  ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<��Ʒ1ID="+files[i].toString(),"log.txt");
				  try
	        	  {
					  //�Ƿ���Ƶ�ļ�
					  if(MediaFileAdapter.isVideoFileType(files[i].toString())==true)
					  {
						  ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<��ƷvideoID="+files[i].toString(),"log.txt");
						  mMusicList.add(files[i].toString());
					  }
					  //�Ƿ�ͼƬ�ļ�
					  else if(MediaFileAdapter.isImgFileType(files[i].toString())==true)
					  {
						  ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<��ƷimageID="+files[i].toString(),"log.txt");
						  imgMusicList.add(files[i].toString());
					  }
	        	  }
				  catch(Exception e)
	        	  {
	        			ToolClass.Log(ToolClass.INFO,"EV_JNI","�ļ�="+files[i].toString()+"�쳣���޷��ж�","log.txt");
	        	  }
			  }
			}
		}    
    } 
    //�򿪲�����
    private void startVideo()
    { 
    	videoView.requestFocus(); 
    	show();  
    	//��������¼�������
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {  
  
                    @Override  
                    public void onCompletion(MediaPlayer mp) {  
                        // TODO Auto-generated method stub  
                    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<�������...","log.txt");
                    	show();//��������ټ�����һ��  
                    }  
                });  
        //���ų����¼�������        
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {  
              
            @Override  
            public boolean onError(MediaPlayer mp, int what, int extra) {  
                // TODO Auto-generated method stub 
            	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<���Ź���...","log.txt");
            	show();//���ų����ټ�����һ��  
                return true;  
            }  
        });  
        
        //�����¼�������
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
				changefragment();								
				return true;
			}
		});       
        
	}  
    /**�õ����������ֵ
     * @param type 0��Ƶ,1ͼƬ
     * @return
     */
    private void rNext(int type)
    {
        if(type==0)// 0��Ƶ
        {
        	curIndex=r.nextInt(mMusicList.size());
        }
        else if(type==1)//1ͼƬ
        {
        	curIndex=r.nextInt(imgMusicList.size());
        }
    }
    
    //ͼƬ����Ƶ�л���ʾ
    private String showOpt()
    {
        String rst=null;
        //��Ƶ��ͼƬ�ļ�����Ҫ��һ������
        //������Ƶ:û�в���         ����Ƶ�ļ�             �ڿ���״̬
        if((viewvideo==false)&&(mMusicList.size()>0)&&(videopause==false))
        {
        	viewvideo=true;//���ÿ�ʼ����
            rNext(0);
            ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<������ƵID="+mMusicList.get(curIndex),"log.txt");
            rst=mMusicList.get(curIndex);
        }
        //����ͼƬ
        else
        {
        	viewvideo=false;
            if(imgMusicList.size()>0)
            {
                rNext(1);
                ToolClass.Log(ToolClass.INFO, "EV_JNI", "APP<����ͼƬID=" + imgMusicList.get(curIndex), "log.txt");
                rst = imgMusicList.get(curIndex);
            }
            else
            {
            	viewvideo=true;//���ÿ�ʼ����
                rNext(0);
                ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<������ƵID="+mMusicList.get(curIndex),"log.txt");
                rst=mMusicList.get(curIndex);
            }
        }
        return rst;
    }
    //ͼƬ����Ƶ�л���ʾ
    private void show()
    {
    	String rst=showOpt();
    	//�Ƿ���Ƶ�ļ�
		if(MediaFileAdapter.isVideoFileType(rst)==true)
		{
			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<��Ƶ���Բ���ID="+rst,"log.txt");
			ivads.setVisibility(View.GONE);//ͼƬ�ر�
    		webtishiInfo.setVisibility(View.GONE);//��ʾ�ر�
    		videoView.setVisibility(View.VISIBLE);//��Ƶ��
    		play();
    	}
		//�Ƿ�ͼƬ�ļ�
		else if(MediaFileAdapter.isImgFileType(rst)==true)
		{
			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<ͼƬ���Բ���ID="+rst,"log.txt");
    		videoView.setVisibility(View.GONE);//��Ƶ�ر�
    		webtishiInfo.setVisibility(View.GONE);//��ʾ�ر�
    		ivads.setVisibility(View.VISIBLE);//ͼƬ��
    		playImage();
		}
    }
    //��ʾͼƬ
    private void playImage()
    {  
    	/*ΪʲôͼƬһ��Ҫת��Ϊ Bitmap��ʽ�ģ��� */
        Bitmap bitmap = ToolClass.getLoacalBitmap(imgMusicList.get(curIndex)); //�ӱ���ȡͼƬ(��cdcard�л�ȡ)  //
        ivads.setImageBitmap(bitmap);// ����ͼ��Ķ�����ֵ
        //��ʱ10s
        new Handler().postDelayed(new Runnable() 
		{
            @Override
            public void run() 
            {
            	show();
            }

		}, SPLASH_DISPLAY_LENGHT);	
    }  
    //������Ƶ
    private void play()
    {  
    	videoView.setVideoPath(mMusicList.get(curIndex));  
        videoView.start(); 
    }   
    
    private boolean checkAds(String CLS_URL)
    {
    	String ATT_ID="",TypeStr="";
//  		int FileType=0;//1ͼƬ,2��Ƶ
  		boolean rst=false;
  		if(CLS_URL.equals("null")!=true)
  		{
  			String a[] = CLS_URL.split("/");  
  			ATT_ID=a[a.length-1];  
  			String tmp = ATT_ID;
	    	ATT_ID=tmp.substring(0,tmp.lastIndexOf("."));		    	
	    	TypeStr=tmp.substring(tmp.lastIndexOf(".")+1);
		    //�Ƿ���Ƶ�ļ�
		    if(MediaFileAdapter.isVideoFileType(tmp)==true)
		    {
//		    	FileType=2;
		        ToolClass.Log(ToolClass.INFO,"EV_JNI","�����ƵATT_ID="+ATT_ID+"."+TypeStr,"log.txt");										
		    }
		    //�Ƿ�ͼƬ�ļ�
		    else if(MediaFileAdapter.isImgFileType(tmp)==true)
		    {
//		    	FileType=1;
	  			ToolClass.Log(ToolClass.INFO,"EV_JNI","���ͼƬATT_ID="+ATT_ID+"."+TypeStr,"log.txt");										
	  		}
  			
		    if(ATT_ID.equals("")==true)
  			{
  				ToolClass.Log(ToolClass.INFO,"EV_JNI","���["+ATT_ID+"]��","log.txt");
  			}
		    else if(ToolClass.isAdsFile(ATT_ID,TypeStr,"ads"))
			{
				ToolClass.Log(ToolClass.INFO,"EV_JNI","���["+ATT_ID+"]�Ѵ���","log.txt");
				rst=true;
			}
		    else
		    {
				ToolClass.Log(ToolClass.INFO,"EV_JNI","���["+ATT_ID+"]������","log.txt");
			}
  		}
  		return rst;
    }
    
    //��ʾ��ʾ��Ϣ
    private void showtishiInfo(int infotype)
    {  
    	ivads.setVisibility(View.GONE);//ͼƬ�ر�
    	videoView.setVisibility(View.GONE);//��Ƶ�ر�
    	
		 webtishiInfo.setVisibility(View.VISIBLE);//��ʾ��
		 WebSettings settings = webtishiInfo.getSettings();
	     settings.setSupportZoom(true);
	     settings.setTextSize(WebSettings.TextSize.LARGEST);
	     webtishiInfo.getSettings().setSupportMultipleWindows(true);
	     webtishiInfo.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY); //���ù�������ʽ
	     webtishiInfo.getSettings().setDefaultTextEncodingName("UTF -8");//����Ĭ��Ϊutf-8
	     String info="�����ڴ�!";
	     //������ʾ����ʾ��Ϣ
  		 vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(ToolClass.getContext());// ����InaccountDAO����
	     // ��ȡ����������Ϣ�����洢��List���ͼ�����
    	 Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
    	 if(tb_inaccount!=null)
    	 {
    		 //������ʾ
    		 if(infotype==1)
    		 {
    			 if(ToolClass.isEmptynull(tb_inaccount.getDemo())==false)
	    		 {
    				 info=tb_inaccount.getDemo();
    			 }	 
    		 }
    		 //���Ϣ
    		 else if(infotype==2)
    		 {    			 
				 if(ToolClass.isEmptynull(tb_inaccount.getEvent())==false)
    			 {
    				 info=tb_inaccount.getEvent();
    			 }    			 
    		 }
    	 }
	     
	     webtishiInfo.loadDataWithBaseURL(null,info, "text/html; charset=UTF-8","utf-8", null);//����д��������ȷ���Ľ���
		    
    	//��ʱ10s
        new Handler().postDelayed(new Runnable() 
		{
            @Override
            public void run() 
            {
            	show();
            }

		}, SPLASH_DISPLAY_LENGHT);
    }  
    
    
    //�л�������fragment
    private void changefragment()
    {
    	ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<goto=businesslandFragment","log.txt");
    	//�������fragment��activity���ͻص���Ϣ
    	listterner.switchBusiness();
    }
    
//    //��ͣ
//    @Override  
//    protected void onPause() {  
//        // TODO Auto-generated method stub  
//        super.onPause();  
//        if(videoView!=null&&videoView.isPlaying()){  
//            videoView.pause();  
//        }  
//          
//    }  
//  
//    //�ָ�
//    @Override  
//    protected void onResume() {  
//        // TODO Auto-generated method stub  
//        super.onResume();  
//        startVideo();  
//    } 
}
