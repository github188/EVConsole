/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           MaintainActivity.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        ά���˵���ҳ��          
**------------------------------------------------------------------------------------------------------
** Created by:          guozhenzhen 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.app.maintain;

import java.io.File;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.easivend.dao.vmc_system_parameterDAO;
import com.easivend.evprotocol.COMThread;
import com.easivend.http.EVServerhttp;
import com.easivend.model.Tb_vmc_system_parameter;
import com.easivend.view.COMService;
import com.easivend.view.DogService;
import com.easivend.view.EVServerService;
import com.easivend.view.MobileService;
import com.easivend.weixing.WeiConfigAPI;
import com.easivend.alipay.AlipayConfigAPI;
import com.easivend.app.business.BusLand;
import com.easivend.app.business.BusPort;
import com.easivend.common.AudioSound;
import com.easivend.common.PictureAdapter;
import com.easivend.common.SerializableMap;
import com.easivend.common.ToolClass;
import com.example.evconsole.R;
import com.tencent.bugly.crashreport.CrashReport;


import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MaintainActivity extends Activity
{
	private GridView gvInfo;// ����GridView����	
	//���ȶԻ���
	ProgressDialog dialog= null;
	// �����ַ������飬�洢ϵͳ����
    private String[] titles = new String[] { "��Ʒ����", "��������", "��������", "������Ϣ", "������־", "��������", "����ҳ��", "�˳�" };
    // ����int���飬�洢���ܶ�Ӧ��ͼ��
    private int[] images = new int[] { R.drawable.addoutaccount, R.drawable.addinaccount, R.drawable.outaccountinfo, R.drawable.showinfo,
            R.drawable.inaccountinfo, R.drawable.sysset, R.drawable.accountflag, R.drawable.exit };
    String com=null,bentcom=null,columncom=null,extracom=null,cardcom=null,printcom=null,posip=null,posipport=null,columncom2=null;
    int posisssl=0;
    final static int REQUEST_CODE=1;   
    //��ȡ������Ϣ
   //Map<String,Integer> huoSet=new HashMap<String,Integer>();
    SerializableMap serializableMap = null;
    //Dog�������
    int isallopen=1;//�Ƿ񱣳ֳ���һֱ��,1һֱ��,0�رպ󲻴�
	private final int SPLASH_DISPLAY_LENGHT = 5000; // �ӳ�5��
	//LocalBroadcastManager dogBroadreceiver;
	//Server�������
	LocalBroadcastManager localBroadreceiver;
	EVServerReceiver receiver;
	private boolean issale=false;//true�Ƿ��Ѿ��Զ��򿪹�����ҳ���ˣ�����򿪹����Ͳ��ٴ���
	Map<String, String> vmcmap;
	//COM�������
	LocalBroadcastManager comBroadreceiver;
	COMReceiver comreceiver;
	ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maintain);	
		//bugly���ص���ģʽ,false�����汾��,true���԰汾��
		//CrashReport.testJavaCrash();��������������Ա���
		CrashReport.initCrashReport(getApplicationContext(), "900048192", false);		
		//ȡ����Ļ�ĳ��Ϳ����бȽ����ú������ı���
		Display display = getWindowManager().getDefaultDisplay();  
		int width = display.getWidth();  
		int height = display.getHeight();  
		if (width > height) {  
			ToolClass.setOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//��Ϊ����
		} else { 
			ToolClass.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//��Ϊ����
			//ToolClass.setOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//��Ϊ����
		}
		//���ú������������Ĳ��ֲ���
		this.setRequestedOrientation(ToolClass.getOrientation());
		//��ȡ��Ӧ��context
		ToolClass.setContext(getApplicationContext());
		
		//�������������
		startService(new Intent(this,MobileService.class));
		//���������ļ�
		AudioSound.initsound();
		//==========
		//Dog�������
		//==========
		//��������
		startService(new Intent(this,DogService.class));
		//dogBroadreceiver.getInstance(this);
		//��ʱ5s
	    new Handler().postDelayed(new Runnable() 
		{
            @Override
            public void run() 
            {      
            	evDog(1);
            }

		}, SPLASH_DISPLAY_LENGHT);
	    
		
		
		//=============
		//Server�������
		//=============
	    //3.��������
		startService(new Intent(MaintainActivity.this,EVServerService.class));
		//4.ע�������
		localBroadreceiver = LocalBroadcastManager.getInstance(this);
		receiver=new EVServerReceiver();
		IntentFilter filter=new IntentFilter();
		filter.addAction("android.intent.action.vmserverrec");
		localBroadreceiver.registerReceiver(receiver,filter);
		vmcmap = ToolClass.getvmc_no(MaintainActivity.this);
		
		//=============
		//COM�������
		//=============
		//3.��������
		startService(new Intent(MaintainActivity.this,COMService.class));
		//4.ע�������
		comBroadreceiver = LocalBroadcastManager.getInstance(this);
		comreceiver=new COMReceiver();
		IntentFilter comfilter=new IntentFilter();
		comfilter.addAction("android.intent.action.comrec");
		comBroadreceiver.registerReceiver(comreceiver,comfilter);
		//��ʱ3s
	    new Handler().postDelayed(new Runnable() 
		{
            @Override
            public void run() 
            {      
            	dialog= ProgressDialog.show(MaintainActivity.this,"����ͬ������","���Ժ�Ƭ��...");
            	ToolClass.ResstartPort(1);
            	ToolClass.ResstartPort(2);
            	ToolClass.ResstartPort(3);
            	ToolClass.ResstartPort(4);
            	ToolClass.ResstartPort(5);
            	
            	//7.����ָ��㲥��COMService
        		Intent intent=new Intent();
        		intent.putExtra("EVWhat", COMService.EV_CHECKALLCHILD);	
        		intent.setAction("android.intent.action.comsend");//action���������ͬ
        		comBroadreceiver.sendBroadcast(intent);
            }

		}, 1000);
				
		//================
		//�������ú�ע�����
		//================
		ToolClass.SetDir();	//���ø�Ŀ¼
		//��ȡ�����Ƿ���Ȩ����
		ToolClass.setCLIENT_STATUS_SERVICE(ToolClass.ReadSharedPreferencesAccess());
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<[��������,��Ȩ"+ToolClass.isCLIENT_STATUS_SERVICE()+"...]log·��:"+ToolClass.getEV_DIR()+File.separator+"logs","log.txt");			
		//�������ļ���ȡ����
		Map<String, String> list=ToolClass.ReadConfigFile();
		if(list!=null)
		{
			if(list.containsKey("com"))//�����ֽ𴮿ں�
	        {
				com = list.get("com");
				ToolClass.setCom(com);
	        }
			if(list.containsKey("bentcom"))//���ø��ӹ񴮿ں�
	        {
				bentcom = list.get("bentcom");
				ToolClass.setBentcom(bentcom);
	        }
			if(list.containsKey("columncom"))//�������񴮿ں�
	        {
				columncom = list.get("columncom");
				ToolClass.setColumncom(columncom);	
	        }
	        if(list.containsKey("extracom"))//������Э���ں�
	        {
	        	extracom = list.get("extracom");
	        	ToolClass.setExtracom(extracom);	
	        }
	        if(list.containsKey("cardcom"))//���ö��������ں�
	        {
	        	cardcom = list.get("cardcom");
	        	ToolClass.setCardcom(cardcom);
	        }	
	        if(list.containsKey("printcom"))//���ô�ӡ�����ں�
	        {
	        	printcom = list.get("printcom");
	        	ToolClass.setPrintcom(printcom);
	        }
	        if(list.containsKey("posip"))//������Э���ں�
	        {
	        	posip = list.get("posip");
	        	ToolClass.setPosip(posip);	
	        }
	        if(list.containsKey("posipport"))//������Э���ں�
	        {
	        	posipport = list.get("posipport");
	        	ToolClass.setPosipport(posipport);	
	        }	        
	        if(list.containsKey("isallopen"))//���ø��񴮿ں�
	        {
				columncom2 = list.get("isallopen");
				ToolClass.setColumncom2(columncom2);	
	        }
	        if(list.containsKey("posisssl"))//����ssl����
	        {
	        	posisssl = Integer.parseInt(list.get("posisssl"));	
	        	ToolClass.setPosisssl(posisssl);	        	
	        }	        
	        AlipayConfigAPI.SetAliConfig(list);//���ð����˺�
	        WeiConfigAPI.SetWeiConfig(list);//����΢���˺�	        
	        
	        //2.���ļ�����
  	        ToolClass.ResetConfigFile();  
		}
		else
		{
			dialog.dismiss();
		}
		//��������ˮӡͼƬ		
		Bitmap mark = BitmapFactory.decodeResource(this.getResources(), R.drawable.ysq);  
		ToolClass.setMark(mark);
		//����΢��֤��
		ToolClass.setWeiCertFile();		
		//����goc
		ToolClass.setGoc(MaintainActivity.this);
		//���ػ���
		ToolClass.setExtraComType(MaintainActivity.this);		
		//================
		//�Ź������
		//================		
		gvInfo = (GridView) findViewById(R.id.gvInfo);// ��ȡ�����ļ��е�gvInfo���
        PictureAdapter adapter = new PictureAdapter(titles, images, this);// ����pictureAdapter����
        gvInfo.setAdapter(adapter);// ΪGridView��������Դ
        gvInfo.setOnItemClickListener(new OnItemClickListener() {// ΪGridView��������¼�
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent = null;// ����Intent����
                switch (arg2) {
                case 0:
                	if(issale)
                	{
	                	intent = new Intent(MaintainActivity.this, GoodsManager.class);// ʹ��GoodsManager���ڳ�ʼ��Intent
	                	startActivityForResult(intent,REQUEST_CODE);// ��GoodsManager
                	}
                	break;
                case 1:
                	if(issale)
                	{
	                	intent = new Intent(MaintainActivity.this, HuodaoTest.class);// ʹ��HuodaoTest���ڳ�ʼ��Intent
	                    startActivityForResult(intent,REQUEST_CODE);// ��HuodaoTest
                	}
                	break;
                case 2:
                	if(issale)
                	{
	                	intent = new Intent(MaintainActivity.this, ParamManager.class);// ʹ��ParamManager���ڳ�ʼ��Intent
	                    startActivityForResult(intent,REQUEST_CODE);// ��ParamManager                    
                	}
                	break;    
                case 3:
                	if(issale)
                	{
	                	intent = new Intent(MaintainActivity.this, Order.class);// ʹ��Accountflag���ڳ�ʼ��Intent
	                	startActivityForResult(intent,REQUEST_CODE);
                	}
                	break;                
                case 4:
                	if(issale)
                	{
	                	intent = new Intent(MaintainActivity.this, LogOpt.class);// ʹ��Accountflag���ڳ�ʼ��Intent
	                	startActivityForResult(intent,REQUEST_CODE);
                	}
                	break;
                case 5:
                	if(issale)
                	{
	                	intent = new Intent(MaintainActivity.this, Login.class);// ʹ��Accountflag���ڳ�ʼ��Intent
	                	startActivityForResult(intent,REQUEST_CODE);// ��Accountflag
                	}
                	break;
                case 6:
                	IntentBus();
                    break;
                case 7:
                	//��������Ի���
                	Dialog alert=new AlertDialog.Builder(MaintainActivity.this)
                		.setTitle("�Ի���")//����
                		.setMessage("��ȷ��Ҫ�˳�������")//��ʾ�Ի����е�����
                		.setIcon(R.drawable.ic_launcher)//����logo
                		.setPositiveButton("�˳�", new DialogInterface.OnClickListener()//�˳���ť���������ü����¼�
                			{				
            	    				@Override
            	    				public void onClick(DialogInterface dialog, int which) 
            	    				{
            	    					ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<[����ر�...]","log.txt");			
            	    					//�رտ��Ź�
            	    			        if(isallopen==1)
            	    			        {
            	    			            evDog(0); 
            	    			        }  
            	    					// TODO Auto-generated method stub	
            	    					finish();// �رյ�ǰActivity
            	    				}
                		      }
                			)		    		        
            		        .setNegativeButton("ȡ��", new DialogInterface.OnClickListener()//ȡ����ť���������ü����¼�
            		        	{			
            						@Override
            						public void onClick(DialogInterface dialog, int which) 
            						{
            							// TODO Auto-generated method stub				
            						}
            		        	}
            		        )
            		        .create();//����һ���Ի���
            		        alert.show();//��ʾ�Ի���
                    
                    break;
                }
            }
        });

	}
			
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		// TODO Auto-generated method stub
		if(requestCode==REQUEST_CODE)
		{
			ToolClass.Log(ToolClass.INFO,"EV_JNI","activity=�˳�����ҳ��","log.txt");				
		}	
	}
		
	//=============
	//Server�������
	//=============	
	//2.����EVServerReceiver�Ľ������㲥���������շ�����ͬ��������
	public class EVServerReceiver extends BroadcastReceiver 
	{

		@Override
		public void onReceive(Context context, Intent intent) 
		{
			// TODO Auto-generated method stub
			Bundle bundle=intent.getExtras();
			int EVWhat=bundle.getInt("EVWhat");
			switch(EVWhat)
			{
			case EVServerhttp.SETMAIN:
				ToolClass.Log(ToolClass.INFO,"EV_JNI","activity=ǩ���ɹ�","log.txt");			
				if(dialog.isShowing())
					dialog.dismiss();
				if(issale==false)
				{
					//ǩ����ɣ��Զ������ۻ�����
					IntentBus();
				}
	    		break;
			case EVServerhttp.SETFAILMAIN:
				ToolClass.Log(ToolClass.INFO,"EV_JNI","activity=ʧ�ܣ��������","log.txt");	
				if(dialog.isShowing())
					dialog.dismiss();
				if(issale==false)
				{
					//ǩ����ɣ��Զ������ۻ�����
					IntentBus();
				}
	    		break;	
			}			
		}

	}
	
	//ǩ����ɣ��Զ������ۻ�����
	private void IntentBus()
	{
		if(issale==false)
		{
			issale=true;
			timer.scheduleWithFixedDelay(task, 10,10, TimeUnit.SECONDS);       // timeTask 
		}
		//������Ź�û�򿪣��ʹ���
		if(isallopen==0)
			evDog(1);
		//ǩ����ɣ��Զ������ۻ�����
		Intent intbus = null;
		vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(MaintainActivity.this);// ����InaccountDAO����
	    // ��ȡ����������Ϣ�����洢��List���ͼ�����
    	Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
    	if(tb_inaccount!=null)
    	{
    		//ʹ�ô������ҳ��
    		if(tb_inaccount.getEmptyProduct()==1)
    		{
    			intbus = new Intent(MaintainActivity.this, BusLand.class);// ʹ��Accountflag���ڳ�ʼ��Intent
    		}
    		else
    		{
    			//����
				if(ToolClass.getOrientation()==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
				{
					intbus = new Intent(MaintainActivity.this, BusLand.class);// ʹ��Accountflag���ڳ�ʼ��Intent
				}
				//����
				else
				{
					intbus = new Intent(MaintainActivity.this, BusPort.class);// ʹ��Accountflag���ڳ�ʼ��Intent
				}				
    		}
    		startActivityForResult(intbus,REQUEST_CODE);// ��Accountflag
    	}
    	else
		{
			//����
			if(ToolClass.getOrientation()==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
			{
				intbus = new Intent(MaintainActivity.this, BusLand.class);// ʹ��Accountflag���ڳ�ʼ��Intent
			}
			//����
			else
			{
				intbus = new Intent(MaintainActivity.this, BusPort.class);// ʹ��Accountflag���ڳ�ʼ��Intent
			}	
			startActivityForResult(intbus,REQUEST_CODE);// ��Accountflag
		}	
    	ToolClass.Log(ToolClass.INFO,"EV_JNI","activity=���뽻��ҳ��","log.txt");	
	}
	
	//Dog���������:1.���ֳ���,0�����ִ�
	private void evDog(int allopen)
	{	
		isallopen=allopen;
		if(isallopen==1)
			ToolClass.Log(ToolClass.INFO,"EV_JNI","activity=�򿪿��Ź�","log.txt");
		else
			ToolClass.Log(ToolClass.INFO,"EV_JNI","activity=�رտ��Ź�","log.txt");
		
    	//����ָ��㲥��DogService
		Intent intent=new Intent();
		intent.putExtra("isallopen", isallopen);
		intent.setAction("android.intent.action.dogserversend");//action���������ͬ
		//dogBroadreceiver.sendBroadcast(intent);
		sendBroadcast(intent); 
		//==========
    	//EVDog�������
    	//==========
    	//����ָ��㲥��DogService
		Intent intent2=new Intent();
		intent2.putExtra("isallopen", isallopen);
		if(isallopen==1)
			intent2.putExtra("watchfeed", 1);
		else
			intent2.putExtra("watchfeed", 0);	
		intent2.setAction("android.intent.action.watchdog");//action���������ͬ
		//dogBroadreceiver.sendBroadcast(intent); 
		sendBroadcast(intent2);
	}
		
	
	//=============
	//COM�������
	//=============	
	//2.����COMReceiver�Ľ������㲥���������շ�����ͬ��������
	public class COMReceiver extends BroadcastReceiver 
	{

		@Override
		public void onReceive(Context context, Intent intent) 
		{
			// TODO Auto-generated method stub
			Bundle bundle=intent.getExtras();
			int EVWhat=bundle.getInt("EVWhat");
			switch(EVWhat)
			{
			case COMThread.EV_CHECKALLMAIN:
				dialog.setTitle("����ͬ��������");
				//ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity ������ѯȫ��","com.txt");
				serializableMap = (SerializableMap) bundle.get("result");
				Map<String, Integer> Set=serializableMap.getMap();
				ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity ������ѯȫ��="+Set,"com.txt");	
				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<vmserversend","log.txt");
		    	//*******
				//������ͬ��
				//*******
				Intent intent2=new Intent(); 
				intent2.putExtra("EVWhat", EVServerhttp.SETCHILD);
				intent2.putExtra("vmc_no", vmcmap.get("vmc_no"));
				intent2.putExtra("vmc_auth_code", vmcmap.get("vmc_auth_code"));
				//��������
		        final SerializableMap myMap=new SerializableMap();
		        myMap.setMap(Set);//��map������ӵ���װ��myMap<span></span>��
		        Bundle bundle2=new Bundle();
		        bundle2.putSerializable("huoSet", myMap);
		        intent2.putExtras(bundle2);
				intent2.setAction("android.intent.action.vmserversend");//action���������ͬ
				localBroadreceiver.sendBroadcast(intent2);  
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","������ʼ����ϣ�����������̨����...","server.txt");
	    		break;	
	    		//��ť����
			case COMThread.EV_BUTTONMAIN:
				SerializableMap serializableMap2 = (SerializableMap) bundle.get("result");
				Map<String, Integer> Set2=serializableMap2.getMap();				
				int EV_TYPE=Set2.get("EV_TYPE");
				ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity ��������="+Set2,"com.txt");
				//�ϱ�ά��ģʽ����
				if(EV_TYPE==COMThread.EV_BUTTONRPT_MAINTAIN)
				{
					ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity ά��ģʽ","com.txt");
					if(issale==false)
					{
						// ������Ϣ��ʾ
			            Toast.makeText(MaintainActivity.this, "����������ά��ģʽ������ͬ������", Toast.LENGTH_SHORT).show();
					}
				}
				break;
			}			
		}

	}
	
	//���õ���ʱ��ʱ��
	TimerTask task = new TimerTask() { 
        @Override 
        public void run() {         	
            runOnUiThread(new Runnable() {      // UI thread 
                @Override 
                public void run() 
                {
                	Boolean bool=false;                	
                	//==========
                	//Dog�������
                	//==========
                	bool=ToolClass.isServiceRunning("com.easivend.view.DogService");
                	ToolClass.Log(ToolClass.INFO,"EV_DOG","Check DogService:"+bool,"dog.txt");
                	if(bool==false)
                	{
                		//��������
                		startService(new Intent(MaintainActivity.this,DogService.class));
                		//dogBroadreceiver.getInstance(this);
                		//��ʱ1s
                		new Handler().postDelayed(new Runnable() 
                		{
                			@Override
                			public void run() 
                			{      
                				evDog(1);
                			}

                		}, 1000);
                	}
                	//==========
                	//EVDog�������
                	//==========
                	//����ָ��㲥��DogService
		    		Intent intent=new Intent();
		    		intent.putExtra("isallopen", isallopen);
		    		if(isallopen==1)
		    			intent.putExtra("watchfeed", 1);
		    		else
		    			intent.putExtra("watchfeed", 0);	
		    		intent.setAction("android.intent.action.watchdog");//action���������ͬ
		    		//dogBroadreceiver.sendBroadcast(intent); 
		    		sendBroadcast(intent);
                	//=============
                	//COM�������
                	//=============
                	bool=ToolClass.isServiceRunning("com.easivend.view.COMService");
                	ToolClass.Log(ToolClass.INFO,"EV_DOG","Check COMService:"+bool,"dog.txt");
                	if(bool==false)
                	{
                		//3.��������
                		startService(new Intent(MaintainActivity.this,COMService.class));
                		//4.ע�������
                		IntentFilter comfilter=new IntentFilter();
                		comfilter.addAction("android.intent.action.comrec");
                		comBroadreceiver.registerReceiver(comreceiver,comfilter);
                	}
                	
                	//=============
                	//Server�������
                	//=============
                	bool=ToolClass.isServiceRunning("com.easivend.view.EVServerService");
                	ToolClass.Log(ToolClass.INFO,"EV_DOG","Check EVServerService:"+bool,"dog.txt");
                	if(bool==false)
                	{
                		//3.��������
                		startService(new Intent(MaintainActivity.this,EVServerService.class));
                		//4.ע�������
                		IntentFilter filter=new IntentFilter();
                		filter.addAction("android.intent.action.vmserverrec");
                		localBroadreceiver.registerReceiver(receiver,filter);
                		//��ʱ1s
                		new Handler().postDelayed(new Runnable() 
                		{
                			@Override
                			public void run() 
                			{      
                				//ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity ������ѯȫ��","com.txt");
                				Map<String, Integer> Set=serializableMap.getMap();
                				ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity ������ѯȫ��="+Set,"com.txt");	
                				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<vmserversend","log.txt");
                		    	//*******
                				//������ͬ��
                				//*******
                				Intent intent2=new Intent(); 
                				intent2.putExtra("EVWhat", EVServerhttp.SETCHILD);
                				intent2.putExtra("vmc_no", vmcmap.get("vmc_no"));
                				intent2.putExtra("vmc_auth_code", vmcmap.get("vmc_auth_code"));
                				//��������
                		        final SerializableMap myMap=new SerializableMap();
                		        myMap.setMap(Set);//��map������ӵ���װ��myMap<span></span>��
                		        Bundle bundle2=new Bundle();
                		        bundle2.putSerializable("huoSet", myMap);
                		        intent2.putExtras(bundle2);
                				intent2.setAction("android.intent.action.vmserversend");//action���������ͬ
                				localBroadreceiver.sendBroadcast(intent2); 
                				ToolClass.Log(ToolClass.INFO,"EV_SERVER","�Լ�������̨����...","server.txt");
                			}

                		}, 1000);
                	}
                	
                } 
            }); 
        } 
    };
	
	@Override
	protected void onDestroy() {		
		//=============
		//Server�������
		//=============
		//5.���ע�������
		localBroadreceiver.unregisterReceiver(receiver);
		//6.��������
		stopService(new Intent(MaintainActivity.this, EVServerService.class));
		//=============
		//COM�������
		//=============
		//5.���ע�������
		comBroadreceiver.unregisterReceiver(comreceiver);
		//6.��������
		stopService(new Intent(MaintainActivity.this, COMService.class));
		//�ر��Լ�������ʱ��
		timer.shutdown();		 
		// TODO Auto-generated method stub
		super.onDestroy();		
	}
}


