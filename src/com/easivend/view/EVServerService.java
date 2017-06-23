package com.easivend.view;

import java.io.File;
import java.io.OutputStream;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easivend.common.SerializableMap;
import com.easivend.common.ToolClass;
import com.easivend.common.Vmc_OrderAdapter;
import com.easivend.dao.vmc_classDAO;
import com.easivend.dao.vmc_columnDAO;
import com.easivend.dao.vmc_orderDAO;
import com.easivend.dao.vmc_productDAO;
import com.easivend.http.EVServerhttp;
import com.easivend.model.Tb_vmc_class;
import com.easivend.model.Tb_vmc_column;
import com.easivend.model.Tb_vmc_product;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;

public class EVServerService extends Service {
	private Thread thread=null;
    private Handler mainhand=null,childhand=null;  
    private String vmc_no=null;
    private String vmc_auth_code=null;
    private int tokenno=0;
    EVServerhttp serverhttp=null;
    LocalBroadcastManager localBroadreceiver;
    ActivityReceiver receiver;
    ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);
    ScheduledExecutorService terminal = Executors.newScheduledThreadPool(1);
	Map<String,Integer> huoSet=null;
    private String LAST_CLASS_TIME="",LAST_CLASSJOIN_TIME="",LAST_PRODUCT_TIME="",
    		LAST_EDIT_TIME="",LAST_VERSION_TIME="",LAST_LOG_TIME=""
    		,LAST_ACCOUNT_TIME="",LAST_ADV_TIME="",LAST_CLIENT_TIME="",
    		LAST_EVENT_TIME="",LAST_DEMO_TIME="";
    private boolean ischeck=false;//trueǩ���ɹ�,false��ʼǩ������
    private boolean isspempty=false;//true�в����ڵ���Ʒ,falseû�в����ڵ���Ʒ
    private int isspretry=0;//�в����ڵ���Ʒʱ������3�Σ����о�����
    Map<String,String> classjoin=new HashMap<String, String>();//��Ʒ���Ͷ�Ӧ����Ʒid��map 
    AlarmManager alarm=null;//���ӷ���
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service bind","server.txt");
		return null;
	}
	//8.����activity�Ľ������㲥��������������
	public class ActivityReceiver extends BroadcastReceiver {

		@Override
		public synchronized void  onReceive(Context context, Intent intent)
		{
			// TODO Auto-generated method stub
			Bundle bundle=intent.getExtras();
			int EVWhat=bundle.getInt("EVWhat");
			switch(EVWhat)
			{
			//ǩ��
			case EVServerhttp.SETCHILD:
				vmc_no=bundle.getString("vmc_no");
				vmc_auth_code=bundle.getString("vmc_auth_code");
				SerializableMap serializableMap = (SerializableMap) bundle.get("huoSet");
				huoSet=serializableMap.getMap();
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","����ǩ��receiver:vmc_no="+vmc_no+"vmc_auth_code="+vmc_auth_code
						+"huoSet="+huoSet.toString(),"server.txt");				
				//������յ�������,����ǩ��������߳���
				//��ʼ��һ:����ǩ��ָ��
	        	childhand=serverhttp.obtainHandler();
	    		Message childmsg=childhand.obtainMessage();
	    		childmsg.what=EVServerhttp.SETCHILD;
	    		JSONObject ev=null;
	    		try {
	    			ev=new JSONObject();
	    			ev.put("vmc_no", vmc_no);
	    			ev.put("vmc_auth_code", vmc_auth_code);
	    			ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send0.1="+ev.toString(),"server.txt");
	    		} catch (JSONException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	    		childmsg.obj=ev;
	    		childhand.sendMessage(childmsg);
	    		ischeck=false;
	    		break;    	
	    	//ǩ����֤
			case EVServerhttp.SETCHECKCMDCHILD:
				//������յ�������,����ǩ��������߳���
				childhand=serverhttp.obtainHandler();
	    		Message childheartmsg6=childhand.obtainMessage();
	    		childheartmsg6.what=EVServerhttp.SETCHECKCMDCHILD;
	    		JSONObject ev6=null;
	    		try {
	    			ev6=new JSONObject();
	    			ev6.put("vmc_no", bundle.getString("vmc_no"));
	    			ev6.put("vmc_auth_code", bundle.getString("vmc_auth_code"));
	    			ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send0.1="+ev6.toString(),"server.txt");
	    		} catch (JSONException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	    		childheartmsg6.obj=ev6;
	    		childhand.sendMessage(childheartmsg6);
				break;
    		//���ͽ��׼�¼������߳���
			case EVServerhttp.SETRECORDCHILD://���߳̽������߳���Ϣ��ȡ������Ϣ
				childhand=serverhttp.obtainHandler();
        		Message childheartmsg2=childhand.obtainMessage();
        		childheartmsg2.what=EVServerhttp.SETRECORDCHILD;
        		childheartmsg2.obj=grid();
        		childhand.sendMessage(childheartmsg2);						
				break;	
    		//���ͻ����ϴ�������߳���	
			case EVServerhttp.SETHUODAOSTATUCHILD:				
				childhand=serverhttp.obtainHandler();
        		Message childheartmsg3=childhand.obtainMessage();
        		childheartmsg3.what=EVServerhttp.SETHUODAOSTATUCHILD;
        		childheartmsg3.obj=columngrid();
        		childhand.sendMessage(childheartmsg3);
				break;
				//����ȡ���뵽���߳���	
			case EVServerhttp.SETPICKUPCHILD:	
				String PICKUP_CODE=bundle.getString("PICKUP_CODE");
				childhand=serverhttp.obtainHandler();
        		Message childheartmsg4=childhand.obtainMessage();
        		childheartmsg4.what=EVServerhttp.SETPICKUPCHILD;
        		childheartmsg4.obj=PICKUP_CODE;
        		childhand.sendMessage(childheartmsg4);
				break;		
			}			
		}

	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service create","server.txt");
		super.onCreate();
		//9.ע�������
		localBroadreceiver = LocalBroadcastManager.getInstance(this);
		receiver=new ActivityReceiver();
		IntentFilter filter=new IntentFilter();
		filter.addAction("android.intent.action.vmserversend");
		localBroadreceiver.registerReceiver(receiver,filter);						
	}

		
	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service start","server.txt");		
		//***********************
		//�߳̽���vmserver����
		//***********************
		mainhand=new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				Intent intent;
				// TODO Auto-generated method stub				
				switch (msg.what)
				{
					//ǩ��
					case EVServerhttp.SETERRFAILMAIN://���߳̽������߳���Ϣǩ��ʧ��
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service ǩ��ʧ�ܣ�ԭ��="+msg.obj.toString(),"server.txt");
						//���ظ�activity�㲥
						intent=new Intent();
						intent.putExtra("EVWhat", EVServerhttp.SETFAILMAIN);
						intent.setAction("android.intent.action.vmserverrec");//action���������ͬ
						localBroadreceiver.sendBroadcast(intent);	
						break;					
					case EVServerhttp.SETMAIN://���߳̽������߳���Ϣǩ�����
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service ǩ���ɹ�","server.txt");
						//��ʼ����:�ϱ��豸״̬						
		        		int bill_err=ToolClass.getBill_err();
						int coin_err=ToolClass.getCoin_err();
		    			ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service �ϱ��豸bill_err="+bill_err
								+" coin_err="+coin_err,"server.txt");				
		    			//
			        	childhand=serverhttp.obtainHandler();
			    		Message childmsg=childhand.obtainMessage();
			    		childmsg.what=EVServerhttp.SETDEVSTATUCHILD;
			    		JSONObject ev3=null;
			    		try {
			    			ev3=new JSONObject();
			    			ev3.put("bill_err", bill_err);
			    			ev3.put("coin_err", coin_err);	    			  			
			    			ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send0.1="+ev3.toString(),"server.txt");
			    		} catch (JSONException e) {
			    			// TODO Auto-generated catch block
			    			e.printStackTrace();
			    		}
			    		childmsg.obj=ev3;
			    		childhand.sendMessage(childmsg);
						break;
						//�ϴ��豸��Ϣ	
					case EVServerhttp.SETERRFAILDEVSTATUMAIN://���߳̽������߳���Ϣ�ϴ��豸��Ϣʧ��
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service ��ȡ�豸��Ϣʧ�ܣ�ԭ��="+msg.obj.toString(),"server.txt");
						//����ǩ��
						//������յ�������,����ǩ��������߳���
						childhand=serverhttp.obtainHandler();
			    		Message childheartmsg6=childhand.obtainMessage();
			    		childheartmsg6.what=EVServerhttp.SETCHECKCHILD;
			    		JSONObject ev6=null;
			    		try {
			    			ev6=new JSONObject();
			    			ev6.put("vmc_no", vmc_no);
			    			ev6.put("vmc_auth_code", vmc_auth_code);
			    			ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send0.1="+ev6.toString(),"server.txt");
			    		} catch (JSONException e) {
			    			// TODO Auto-generated catch block
			    			e.printStackTrace();
			    		}
			    		childheartmsg6.obj=ev6;
			    		childhand.sendMessage(childheartmsg6);
			    		tokenno=0;
						//���ظ�activity�㲥
						intent=new Intent();
						intent.putExtra("EVWhat", EVServerhttp.SETFAILMAIN);
						intent.setAction("android.intent.action.vmserverrec");//action���������ͬ
						localBroadreceiver.sendBroadcast(intent);
						break;
					case EVServerhttp.SETDEVSTATUMAIN://���߳̽������߳���Ϣ��ȡ�豸��Ϣ
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service �ϱ��豸��Ϣ�ɹ�","server.txt");
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service LAST_EDIT_TIME="+LAST_EDIT_TIME,"server.txt");
						//��ʼ����.1:��ȡ��Ʒ������Ϣ
						childhand=serverhttp.obtainHandler();
		        		Message childheartmsg5=childhand.obtainMessage();
		        		childheartmsg5.what=EVServerhttp.SETCLASSCHILD;
		        		childheartmsg5.obj=LAST_CLASS_TIME;
		        		childhand.sendMessage(childheartmsg5);	
						
						break;	
					//��ȡ��Ʒ������Ϣ	
					case EVServerhttp.SETERRFAILCLASSMAIN://���߳̽������߳���Ϣ��ȡ��Ʒ������Ϣʧ��
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service �ϱ���Ʒ������Ϣʧ�ܣ�ԭ��="+msg.obj.toString(),"server.txt");
						//���ظ�activity�㲥
						intent=new Intent();
						intent.putExtra("EVWhat", EVServerhttp.SETFAILMAIN);
						intent.setAction("android.intent.action.vmserverrec");//action���������ͬ
						localBroadreceiver.sendBroadcast(intent);
						break;
					case EVServerhttp.SETCLASSMAIN://���߳̽������߳���Ϣ��ȡ��Ʒ������Ϣ
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service ��ȡ��Ʒ������Ϣ�ɹ�","server.txt");
						try 
						{
							updatevmcClass(msg.obj.toString());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}		
						//��ʼ����.2:��ȡ��Ʒ�����Ӧ����Ʒ��Ϣ
						childhand=serverhttp.obtainHandler();
		        		Message childmsg2=childhand.obtainMessage();
		        		childmsg2.what=EVServerhttp.SETJOINCLASSCHILD;
		        		childmsg2.obj=LAST_CLASSJOIN_TIME;
		        		childhand.sendMessage(childmsg2);
						break;
					//��ȡ��Ʒ�����Ӧ����Ʒ��Ϣ	
					case EVServerhttp.SETERRFAILJOINCLASSMAIN://���߳̽������߳���Ϣ��ȡ��Ʒ�����Ӧ����Ʒ��Ϣʧ��
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service �ϱ���Ʒ������Ϣ��Ӧ����Ʒʧ�ܣ�ԭ��="+msg.obj.toString(),"server.txt");
						//���ظ�activity�㲥
						intent=new Intent();
						intent.putExtra("EVWhat", EVServerhttp.SETFAILMAIN);
						intent.setAction("android.intent.action.vmserverrec");//action���������ͬ
						localBroadreceiver.sendBroadcast(intent);
						break;
					case EVServerhttp.SETJOINCLASSMAIN://���߳̽������߳���Ϣ��ȡ��Ʒ�����Ӧ����Ʒ��Ϣ
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service �ϱ���Ʒ������Ϣ��Ӧ����Ʒ�ɹ�������","server.txt");
						try 
						{
							JSONObject json=new JSONObject(msg.obj.toString());
							JSONArray array=json.getJSONArray("List");
							for(int i=0;i<array.length();i++)
							{
								JSONObject obj=array.getJSONObject(i);
								classjoin.put(obj.getString("PRODUCT_NO"), obj.getString("CLASS_CODE"));
								//����ǩ����ɺ󣬸���ʱ���
								if(ischeck==true) 
								{
									LAST_CLASSJOIN_TIME=ToolClass.getLasttime();
								}
							}
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service ��ȡ��Ʒ������Ϣ��Ӧ����Ʒ�ɹ�="+classjoin.toString(),"server.txt");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
						//��ʼ����:��ȡ��Ʒ��Ϣ
						if(ToolClass.isEmptynull(LAST_PRODUCT_TIME))
		        		{		
		        			// ����InaccountDAO����
		        			vmc_productDAO productDAO = new vmc_productDAO(EVServerService.this);
		        			productDAO.deleteall();
		        		}
						childhand=serverhttp.obtainHandler();
		        		Message childmsg5=childhand.obtainMessage();
		        		childmsg5.what=EVServerhttp.SETPRODUCTCHILD;
		        		childmsg5.obj=LAST_PRODUCT_TIME;
		        		childhand.sendMessage(childmsg5);		        		
						break;	
					//��ȡ��Ʒ��Ϣ	
					case EVServerhttp.SETERRFAILRODUCTMAIN://���߳̽������߳���Ϣ��ȡ��Ʒ��Ϣʧ��
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service ��ȡ��Ʒ��Ϣʧ�ܣ�ԭ��="+msg.obj.toString(),"server.txt");
						//���ظ�activity�㲥
						intent=new Intent();
						intent.putExtra("EVWhat", EVServerhttp.SETFAILMAIN);
						intent.setAction("android.intent.action.vmserverrec");//action���������ͬ
						localBroadreceiver.sendBroadcast(intent);
						break;
					case EVServerhttp.SETRODUCTMAIN://���߳̽������߳���Ϣ��ȡ��Ʒ��Ϣ
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service ��ȡ��Ʒ��Ϣ�ɹ�","server.txt");
						try 
						{
							boolean shprst=updatevmcProduct(msg.obj.toString());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}		
						//��ʼ����.1:��ȡ��Ʒ��ϸ�б���ͼƬ��Ϣ
						childhand=serverhttp.obtainHandler();
		        		Message childmsg6=childhand.obtainMessage();
		        		childmsg6.what=EVServerhttp.SETPRODUCTIMAGECHILD;
		        		childmsg6.obj="";
		        		childhand.sendMessage(childmsg6);
						break;	
						//��ȡ��Ʒ��ϸ�б���ͼƬ��Ϣ
					case EVServerhttp.SETERRFAILRODUCTIMAGEMAIN://���߳̽������߳���Ϣ���͸����̻߳�ȡ��Ʒ��ϸ�б���ͼƬ����
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service ��ȡ��Ʒ��ϸ�б���ͼƬ���ϣ�ԭ��="+msg.obj.toString(),"server.txt");
						//���ظ�activity�㲥
						intent=new Intent();
						intent.putExtra("EVWhat", EVServerhttp.SETFAILMAIN);
						intent.setAction("android.intent.action.vmserverrec");//action���������ͬ
						localBroadreceiver.sendBroadcast(intent);
						break;
					case EVServerhttp.SETRODUCTIMAGEMAIN://���߳̽������߳���Ϣ��ȡ��Ʒ��ϸ�б���ͼƬ����
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service ��ȡ��Ʒ��ϸ�б���ͼƬ���سɹ�","server.txt");
						try 
						{
							boolean shprst=updatevmcProduct(msg.obj.toString());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
						//��ʼ����:��ȡ����������Ϣ
						childhand=serverhttp.obtainHandler();
		        		Message childmsg3=childhand.obtainMessage();
		        		childmsg3.what=EVServerhttp.SETHUODAOCHILD;
		        		childmsg3.obj=LAST_EDIT_TIME;
		        		childhand.sendMessage(childmsg3);
						break;	
					//��ȡ������Ϣ	
					case EVServerhttp.SETERRFAILHUODAOMAIN://���߳̽������߳���Ϣ��ȡ������Ϣʧ��
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service ��ȡ������Ϣʧ�ܣ�ԭ��="+msg.obj.toString(),"server.txt");
						//���ظ�activity�㲥
						intent=new Intent();
						intent.putExtra("EVWhat", EVServerhttp.SETFAILMAIN);
						intent.setAction("android.intent.action.vmserverrec");//action���������ͬ
						localBroadreceiver.sendBroadcast(intent);
						break;
					case EVServerhttp.SETHUODAOMAIN://���߳̽������߳���Ϣ��ȡ������Ϣ
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service ��ȡ������Ϣ�ɹ�","server.txt");
						try 
						{							
							updatevmcColumn(msg.obj.toString());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}						
						//�в����ڵ���Ʒ
						if((isspempty)&&(isspretry<4)) 
						{
							isspempty=false;
							isspretry++;
							//��ʼ����:��ȡ��Ʒ��Ϣ
							childhand=serverhttp.obtainHandler();
			        		Message childmsg4=childhand.obtainMessage();
			        		childmsg4.what=EVServerhttp.SETPRODUCTCHILD;
			        		childmsg4.obj="";
			        		childhand.sendMessage(childmsg4);
						}
						else 
						{
							isspretry=0;
							//��ʼ���塢��������������߳���
			            	childhand=serverhttp.obtainHandler();
			        		Message childheartmsg=childhand.obtainMessage();
			        		childheartmsg.what=EVServerhttp.SETHEARTCHILD;
			        		childhand.sendMessage(childheartmsg);	
						}
						break;					
					//��ȡ������Ϣ	
					case EVServerhttp.SETERRFAILHEARTMAIN://���߳̽������߳���Ϣ��ȡ������Ϣʧ��
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service ��ȡ������Ϣʧ�ܣ�ԭ��="+msg.obj.toString(),"server.txt");
						//���ظ�activity�㲥
						intent=new Intent();
						intent.putExtra("EVWhat", EVServerhttp.SETFAILMAIN);
						intent.setAction("android.intent.action.vmserverrec");//action���������ͬ
						localBroadreceiver.sendBroadcast(intent);
						break;
					case EVServerhttp.SETHEARTMAIN://���߳̽������߳���Ϣ��ȡ������Ϣ
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service ��ȡ������Ϣ�ɹ�","server.txt");
						
						//��ʼ���������ͽ��׼�¼������߳���
		            	childhand=serverhttp.obtainHandler();
		        		Message childheartmsg2=childhand.obtainMessage();
		        		childheartmsg2.what=EVServerhttp.SETRECORDCHILD;
		        		childheartmsg2.obj=grid();
		        		childhand.sendMessage(childheartmsg2);						
						break;
					//��ȡ�ϱ����׼�¼����	
					case EVServerhttp.SETERRFAILRECORDMAIN://���߳̽������߳���Ϣ�ϱ����׼�¼ʧ��
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service �ϱ����׼�¼ʧ��","server.txt");
						//���ظ�activity�㲥
						intent=new Intent();
						intent.putExtra("EVWhat", EVServerhttp.SETFAILMAIN);
						intent.setAction("android.intent.action.vmserverrec");//action���������ͬ
						localBroadreceiver.sendBroadcast(intent);
						break;
					case EVServerhttp.SETRECORDMAIN://���߳̽������߳���Ϣ�ϱ����׼�¼
						//�޸Ľ��������ϱ�״̬Ϊ���ϱ�
						updategrid(msg.obj.toString());
						if(ToolClass.getServerVer()==0)//�ɵĺ�̨
						{
							//��ʼ���ߡ����ͻ����ϴ�������߳���
							childhand=serverhttp.obtainHandler();
			        		Message childheartmsg3=childhand.obtainMessage();
			        		childheartmsg3.what=EVServerhttp.SETHUODAOSTATUCHILD;
			        		childheartmsg3.obj=columngrid();
			        		childhand.sendMessage(childheartmsg3);
						}
						else if(ToolClass.getServerVer()==1)//һ�ں�̨
						{
							//��ʼ����.1����ȡ�汾����
							childhand=serverhttp.obtainHandler();
			        		Message childheartmsg3=childhand.obtainMessage();
			        		childheartmsg3.what=EVServerhttp.SETPVERSIONCHILD;
			        		//�տ���ʱ��ʱ��
			        		//1>>�տ���ֻ����һ��ʱ�䣺�����տ�����������һ�γ���ֻ���ڿ���֮�󣬺�̨�����������򣬲Ż�����
			        		//2>>�Ժ󶼲���������ʱ��
			        		if(ToolClass.isEmptynull(LAST_VERSION_TIME))
			        		{		
			        			LAST_VERSION_TIME=ToolClass.getLasttime();
			        		}
			        		childheartmsg3.obj=LAST_VERSION_TIME;
			        		childhand.sendMessage(childheartmsg3);
						}
						break;	
						//��ȡ�汾��Ϣ	
					case EVServerhttp.SETERRFAILVERSIONMAIN://���߳̽������߳���Ϣ��ȡ�汾ʧ��
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service ��ȡ�汾ʧ�ܣ�ԭ��="+msg.obj.toString(),"server.txt");
						//���ظ�activity�㲥
						intent=new Intent();
						intent.putExtra("EVWhat", EVServerhttp.SETFAILMAIN);
						intent.setAction("android.intent.action.vmserverrec");//action���������ͬ
						localBroadreceiver.sendBroadcast(intent);
						break;
					case EVServerhttp.SETVERSIONMAIN://���߳̽������߳���Ϣ��ȡ�汾��Ϣ
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service ��ȡ�汾��Ϣ�ɹ�","server.txt");
						{
							//��ʼ����.2��������־�ϴ�������߳���
							childhand=serverhttp.obtainHandler();
			        		Message childheartmsg3=childhand.obtainMessage();
			        		childheartmsg3.what=EVServerhttp.SETLOGCHILD;
			        		//1>>ÿ�β�ѯ����������һ��ʱ�䣺���������󲻻������ϴ���־��ֻ���ڿ���֮�󣬺�̨��־�ϴ����󣬲Ż��ϴ�
			        		//2>>����ϴ�ʧ�ܣ���һ�ξͲ������ϴ���
			        		LAST_LOG_TIME=ToolClass.getLasttime();			        		
			        		childheartmsg3.obj=LAST_LOG_TIME;
			        		childhand.sendMessage(childheartmsg3);
						}
						break;						
						//��ȡ�汾��װ��Ϣ	
					case EVServerhttp.SETINSTALLMAIN:
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service ��ȡ��װ��Ϣ�ɹ�="+msg.obj.toString(),"server.txt");
						//3>>��װ����������ʱ��
						LAST_VERSION_TIME=ToolClass.getLasttime();
						installApk(msg.obj.toString());		        		
						break;
						//��ȡ��־��Ϣ	
					case EVServerhttp.SETERRFAILLOGMAIN://���߳̽������߳���Ϣ��ȡ��־ʧ��
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service ��ȡ�汾��־��ԭ��="+msg.obj.toString(),"server.txt");
						//���ظ�activity�㲥
						intent=new Intent();
						intent.putExtra("EVWhat", EVServerhttp.SETFAILMAIN);
						intent.setAction("android.intent.action.vmserverrec");//action���������ͬ
						localBroadreceiver.sendBroadcast(intent);
						break;
					case EVServerhttp.SETLOGMAIN://���߳̽������߳���Ϣ��ȡ��־��Ϣ
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service ��ȡ��־��Ϣ�ɹ�","server.txt");
						{
							//��ʼ����.3�����ͻ�ȡ֧����΢��������߳���
							childhand=serverhttp.obtainHandler();
			        		Message childheartmsg3=childhand.obtainMessage();
			        		childheartmsg3.what=EVServerhttp.SETACCOUNTCHILD;
			        		//1>>�տ���������ʱ�䣺����������ͻ���������һ���˺�
			        		childheartmsg3.obj=LAST_ACCOUNT_TIME;
			        		childhand.sendMessage(childheartmsg3);
						}
						break;
						//��ȡ֧����΢����Ϣ	
					case EVServerhttp.SETERRFAILACCOUNTMAIN://���߳̽������߳���Ϣ��ȡ֧����΢��ʧ��
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service ��ȡ֧����΢�ţ�ԭ��="+msg.obj.toString(),"server.txt");
						//���ظ�activity�㲥
						intent=new Intent();
						intent.putExtra("EVWhat", EVServerhttp.SETFAILMAIN);
						intent.setAction("android.intent.action.vmserverrec");//action���������ͬ
						localBroadreceiver.sendBroadcast(intent);
						break;
					case EVServerhttp.SETACCOUNTMAIN://���߳̽������߳���Ϣ��ȡ֧����΢����Ϣ
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service ��ȡ֧����΢����Ϣ�ɹ�","server.txt");						
						{
							//��ʼ����.4�����ͻ�ȡ�����Ϣ�����߳���
							childhand=serverhttp.obtainHandler();
			        		Message childheartmsg3=childhand.obtainMessage();
			        		childheartmsg3.what=EVServerhttp.SETADVCHILD;
			        		//1>>�տ���������ʱ�䣺����������ͺû���������һ�ι��
			        		childheartmsg3.obj=LAST_ADV_TIME;
			        		childhand.sendMessage(childheartmsg3);
						}
						break;	
						//��ȡ֧����΢���˺���������
					case EVServerhttp.SETACCOUNTRESETMAIN:
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service ��ȡ֧����΢���˺���������="+msg.obj.toString(),"server.txt");
						//2>>�����˺ź�����ʱ��
						LAST_ACCOUNT_TIME=ToolClass.getLasttime();	        		
						break;	
						//��ȡ�����Ϣ	
					case EVServerhttp.SETERRFAILADVMAIN://���߳̽������߳���Ϣ��ȡ���ʧ��
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service ��ȡ��棬ԭ��="+msg.obj.toString(),"server.txt");
						//���ظ�activity�㲥
						intent=new Intent();
						intent.putExtra("EVWhat", EVServerhttp.SETFAILMAIN);
						intent.setAction("android.intent.action.vmserverrec");//action���������ͬ
						localBroadreceiver.sendBroadcast(intent);
						break;
					case EVServerhttp.SETADVMAIN://���߳̽������߳���Ϣ��ȡ�����Ϣ
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service ��ȡ�����Ϣ�ɹ�","server.txt");						
						{
							//��ʼ����.5�����ͻ�ȡ�豸��Ϣ�����߳���
							childhand=serverhttp.obtainHandler();
			        		Message childheartmsg3=childhand.obtainMessage();
			        		childheartmsg3.what=EVServerhttp.SETCLIENTCHILD;
			        		//1>>�տ���������ʱ�䣺����������ͺû���������һ�α�������
			        		childheartmsg3.obj=LAST_CLIENT_TIME;
			        		childhand.sendMessage(childheartmsg3);
						}
						break;	
						//��ȡ����˺���������
					case EVServerhttp.SETADVRESETMAIN:
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service ��ȡ�����������="+msg.obj.toString(),"server.txt");
						//2>>���ù�������ʱ��
						LAST_ADV_TIME=ToolClass.getLasttime();	 
						Intent intent3=new Intent();
						intent3.putExtra("EVWhat", EVServerhttp.SETADVRESETMAIN);
						intent3.setAction("android.intent.action.vmserverrec");//action���������ͬ
						localBroadreceiver.sendBroadcast(intent3);
						break;
						//��ȡ�豸��Ϣ	
					case EVServerhttp.SETERRFAILCLIENTMAIN://���߳̽������߳���Ϣ��ȡ�豸ʧ��
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service ��ȡ�豸��ԭ��="+msg.obj.toString(),"server.txt");
						//���ظ�activity�㲥
						intent=new Intent();
						intent.putExtra("EVWhat", EVServerhttp.SETFAILMAIN);
						intent.setAction("android.intent.action.vmserverrec");//action���������ͬ
						localBroadreceiver.sendBroadcast(intent);
						break;
					case EVServerhttp.SETCLIENTMAIN://���߳̽������߳���Ϣ��ȡ�豸��Ϣ
						int RESTART_SKIP=0;
				  		String RESTART_TIME="";
						try 
						{
							JSONObject json=new JSONObject(msg.obj.toString());
							JSONArray array=json.getJSONArray("List");
							for(int i=0;i<array.length();i++)
							{
								JSONObject obj=array.getJSONObject(i);
								//����ʱ��
						  		RESTART_SKIP=obj.getInt("RESTART_SKIP");
						  		RESTART_TIME=obj.getString("RESTART_TIME");
							}
							setshutdownalarm(RESTART_TIME,RESTART_SKIP);//��������
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//2>>�����豸������ʱ��
						LAST_CLIENT_TIME=ToolClass.getLasttime();
						{
							//��ʼ����.6�����ͻ�ȡ���Ϣ�����߳���
							childhand=serverhttp.obtainHandler();
			        		Message childheartmsg3=childhand.obtainMessage();
			        		childheartmsg3.what=EVServerhttp.SETEVENTINFOCHILD;
			        		//1>>�տ���������ʱ�䣺����������ͺû���������һ�λ
			        		childheartmsg3.obj=LAST_EVENT_TIME;
			        		childhand.sendMessage(childheartmsg3);
						}
						break;	
						//��ȡ���Ϣ	
					case EVServerhttp.SETERRFAILEVENTINFOMAIN://���߳̽������߳���Ϣ��ȡ�ʧ��
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service ��ȡ���ԭ��="+msg.obj.toString(),"server.txt");
						//���ظ�activity�㲥
						intent=new Intent();
						intent.putExtra("EVWhat", EVServerhttp.SETFAILMAIN);
						intent.setAction("android.intent.action.vmserverrec");//action���������ͬ
						localBroadreceiver.sendBroadcast(intent);
						break;
					case EVServerhttp.SETEVENTINFOMAIN://���߳̽������߳���Ϣ��ȡ���Ϣ
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service ��ȡ���Ϣ�ɹ�","server.txt");						
						//2>>�����豸������ʱ��
						LAST_EVENT_TIME=ToolClass.getLasttime();
						{
							//��ʼ����.7�����ͻ�ȡ������ʾ��Ϣ�����߳���
							childhand=serverhttp.obtainHandler();
			        		Message childheartmsg3=childhand.obtainMessage();
			        		childheartmsg3.what=EVServerhttp.SETDEMOINFOCHILD;
			        		//1>>�տ���������ʱ�䣺����������ͺû���������һ�λ
			        		childheartmsg3.obj=LAST_DEMO_TIME;
			        		childhand.sendMessage(childheartmsg3);
						}
						break;
						//��ȡ������ʾ��Ϣ	
					case EVServerhttp.SETERRFAILDEMOINFOMAIN://���߳̽������߳���Ϣ��ȡ������ʾʧ��
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service ��ȡ������ʾ��ԭ��="+msg.obj.toString(),"server.txt");
						//���ظ�activity�㲥
						intent=new Intent();
						intent.putExtra("EVWhat", EVServerhttp.SETFAILMAIN);
						intent.setAction("android.intent.action.vmserverrec");//action���������ͬ
						localBroadreceiver.sendBroadcast(intent);
						break;
					case EVServerhttp.SETDEMOINFOMAIN://���߳̽������߳���Ϣ��ȡ������ʾ��Ϣ
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service ��ȡ������ʾ��Ϣ�ɹ�","server.txt");						
						//2>>�����豸������ʱ��
						LAST_DEMO_TIME=ToolClass.getLasttime();
						{
							//��ʼ���ߡ����ͻ����ϴ�������߳���
							childhand=serverhttp.obtainHandler();
			        		Message childheartmsg3=childhand.obtainMessage();
			        		childheartmsg3.what=EVServerhttp.SETHUODAOSTATUCHILD;
			        		childheartmsg3.obj=columngrid();
			        		childhand.sendMessage(childheartmsg3);
						}
						break;	
					//��ȡ�ϱ�������Ϣ����	
					case EVServerhttp.SETERRFAILHUODAOSTATUMAIN://���߳̽������߳��ϱ�������Ϣʧ��
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service �ϱ�������Ϣʧ��","server.txt");
						//���ظ�activity�㲥
						intent=new Intent();
						intent.putExtra("EVWhat", EVServerhttp.SETFAILMAIN);
						intent.setAction("android.intent.action.vmserverrec");//action���������ͬ
						localBroadreceiver.sendBroadcast(intent);
						break;
					case EVServerhttp.SETHUODAOSTATUMAIN://���߳̽������߳��ϱ�������Ϣ
						//�޸������ϱ�״̬Ϊ���ϱ�
						updatecolumns(msg.obj.toString());
						//���¸���token��ֵ
						if(tokenno>=80)
						{
							//������յ�������,����ǩ��������߳���
							childhand=serverhttp.obtainHandler();
				    		Message childheartmsg4=childhand.obtainMessage();
				    		childheartmsg4.what=EVServerhttp.SETCHECKCHILD;
				    		JSONObject ev=null;
				    		try {
				    			ev=new JSONObject();
				    			ev.put("vmc_no", vmc_no);
				    			ev.put("vmc_auth_code", vmc_auth_code);
				    			ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send0.1="+ev.toString(),"server.txt");
				    		} catch (JSONException e) {
				    			// TODO Auto-generated catch block
				    			e.printStackTrace();
				    		}
				    		childheartmsg4.obj=ev;
				    		childhand.sendMessage(childheartmsg4);
				    		tokenno=0;
						}
						else
						{
							tokenno++;
						}
						//��ʼ���ˡ����ظ�activity�㲥,��ʼ�����
						if(ischeck==false)
						{
							intent=new Intent();
							intent.putExtra("EVWhat", EVServerhttp.SETMAIN);
							intent.setAction("android.intent.action.vmserverrec");//action���������ͬ
							localBroadreceiver.sendBroadcast(intent);
							ischeck=true;
							LAST_CLASS_TIME=ToolClass.getLasttime();
							LAST_CLASSJOIN_TIME=ToolClass.getLasttime();
							LAST_PRODUCT_TIME=ToolClass.getLasttime();
							LAST_EDIT_TIME=ToolClass.getLasttime();
						}
							        		
						break;
						//ǩ����֤
					case EVServerhttp.SETERRFAILDCHECKCMDMAIN://���߳̽������߳���Ϣǩ��ʧ��
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service ǩ��ʧ�ܣ�ԭ��="+msg.obj.toString(),"server.txt");
						//���ظ�activity�㲥
						intent=new Intent();
						intent.putExtra("EVWhat", EVServerhttp.SETERRFAILDCHECKCMDMAIN);
						intent.setAction("android.intent.action.vmserverrec");//action���������ͬ
						localBroadreceiver.sendBroadcast(intent);	
						break;
					case EVServerhttp.SETCHECKCMDMAIN://���߳̽������߳���Ϣǩ�����
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service ǩ���ɹ�","server.txt");
						//���ظ�activity�㲥
						intent=new Intent();
						intent.putExtra("EVWhat", EVServerhttp.SETCHECKCMDMAIN);
						intent.setAction("android.intent.action.vmserverrec");//action���������ͬ
						localBroadreceiver.sendBroadcast(intent);
						break;	
						
						//ȡ����Ƚ����⣬�����������Ƶ�����
						//��ȡȡ���뷵��	
					case EVServerhttp.SETERRFAILPICKUPMAIN://���߳̽������߳��ϱ�ȡ������Ϣʧ��
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service �ϱ�ȡ������Ϣʧ��","server.txt");
						Intent intent2=new Intent();
						intent2.putExtra("EVWhat", EVServerhttp.SETERRFAILPICKUPMAIN);
						intent2.setAction("android.intent.action.vmserverrec");//action���������ͬ
						localBroadreceiver.sendBroadcast(intent2);
						break;
					case EVServerhttp.SETPICKUPMAIN://���߳̽������߳��ϱ�ȡ���������Ϣ
						JSONObject zhuheobj=null;
						try {
							zhuheobj=new JSONObject(msg.obj.toString());
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service ȡ�������="+zhuheobj.toString(),"server.txt");
							
							Intent intent4=new Intent();
							intent4.putExtra("EVWhat", EVServerhttp.SETPICKUPMAIN);
							intent4.putExtra("PRODUCT_NO", zhuheobj.getString("PRODUCT_NO"));
							intent4.putExtra("out_trade_no", zhuheobj.getString("out_trade_no"));
							intent4.setAction("android.intent.action.vmserverrec");//action���������ͬ
							localBroadreceiver.sendBroadcast(intent4);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}						
						break;	
					//�������
					case EVServerhttp.SETFAILMAIN://���߳̽������߳�����ʧ��
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service ʧ�ܣ��������","server.txt");
						//���ظ�activity�㲥
						intent=new Intent();
						intent.putExtra("EVWhat", EVServerhttp.SETFAILMAIN);
						intent.setAction("android.intent.action.vmserverrec");//action���������ͬ
						localBroadreceiver.sendBroadcast(intent);	
						break;	
					default:
						break;	
				}				
			}
			
		};
		//�����û��Լ�������࣬�����߳�
  		serverhttp=new EVServerhttp(mainhand);
  		thread=new Thread(serverhttp,"thread");
  		thread.start();
  		//�����豸ͬ����ʱ��  		
  		timer.scheduleWithFixedDelay(new Runnable() { 
	        @Override 
	        public void run() { 
	        	if(ischeck)
	        	{
	        		//ToolClass.Log(ToolClass.INFO,"EV_SERVER","server["+Thread.currentThread().getId()+"]","server.txt");
	        		int bill_err=ToolClass.getBill_err();
					int coin_err=ToolClass.getCoin_err();
	    			ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service �ϱ��豸bill_err="+bill_err
							+" coin_err="+coin_err,"server.txt");				
	    			//
		        	childhand=serverhttp.obtainHandler();
		    		Message childmsg3=childhand.obtainMessage();
		    		childmsg3.what=EVServerhttp.SETDEVSTATUCHILD;
		    		JSONObject ev3=null;
		    		try {
		    			ev3=new JSONObject();
		    			ev3.put("bill_err", bill_err);
		    			ev3.put("coin_err", coin_err);	    			  			
		    			ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send0.1="+ev3.toString(),"server.txt");
		    		} catch (JSONException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		}
		    		childmsg3.obj=ev3;
		    		childhand.sendMessage(childmsg3);
	        	}
	        	//����ǩ��ָ��
	        	else
	        	{
	        		ToolClass.Log(ToolClass.INFO,"EV_SERVER","checkretry:vmc_no="+vmc_no+"vmc_auth_code="+vmc_auth_code
							+"huoSet="+huoSet.toString(),"server.txt");					
					//������յ�������,����ǩ��������߳���
					//��ʼ��һ:����ǩ��ָ��
		        	childhand=serverhttp.obtainHandler();
		    		Message childmsg=childhand.obtainMessage();
		    		childmsg.what=EVServerhttp.SETCHILD;
		    		JSONObject ev=null;
		    		try {
		    			ev=new JSONObject();
		    			ev.put("vmc_no", vmc_no);
		    			ev.put("vmc_auth_code", vmc_auth_code);
		    			ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send0.1="+ev.toString(),"server.txt");
		    		} catch (JSONException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		}
		    		childmsg.obj=ev;
		    		childhand.sendMessage(childmsg);
	        	}
	        } 
	    },10*60,10*60,TimeUnit.SECONDS);       // 10*60timeTask   
  		
  		//*************
  		//�����̼߳�ض�ʱ��
  		//*************
  		terminal.scheduleWithFixedDelay(new Runnable() { 
	        @Override 
	        public void run() { 
	        	Boolean bool=false;   
	        	//��غ�̨�����߳�
	        	bool=thread.isAlive();
	        	if(bool==false)
	        	{
	        		thread=new Thread(serverhttp,"thread");
	          		thread.start();
   
    				ToolClass.Log(ToolClass.INFO,"EV_SERVER","�߳�����receiver:vmc_no="+vmc_no+"vmc_auth_code="+vmc_auth_code
    						+"huoSet="+huoSet.toString(),"server.txt");				
    				//������յ�������,����ǩ��������߳���
    				//��ʼ��һ:����ǩ��ָ��
    	        	childhand=serverhttp.obtainHandler();
    	    		Message childmsg=childhand.obtainMessage();
    	    		childmsg.what=EVServerhttp.SETCHILD;
    	    		JSONObject ev=null;
    	    		try {
    	    			ev=new JSONObject();
    	    			ev.put("vmc_no", vmc_no);
    	    			ev.put("vmc_auth_code", vmc_auth_code);
    	    			ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send0.1="+ev.toString(),"server.txt");
    	    		} catch (JSONException e) {
    	    			// TODO Auto-generated catch block
    	    			e.printStackTrace();
    	    		}
    	    		childmsg.obj=ev;
    	    		childhand.sendMessage(childmsg);
    	    		ischeck=false;
	        	}	        	
	        } 
	    },5*60,15*60,TimeUnit.SECONDS);       // 10*60timeTask 
	}	
	
	//������Ʒ������Ϣ
	private void updatevmcClass(String classrst) throws JSONException
	{
		JSONObject jsonObject = new JSONObject(classrst); 
		if(ToolClass.getServerVer()==0)//�ɵĺ�̨
		{
			JSONArray arr1=jsonObject.getJSONArray("ProductClassList");
			for(int i=0;i<arr1.length();i++)
			{
				JSONObject object2=arr1.getJSONObject(i);
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","������Ʒ����CLASS_CODE="+object2.getString("CLASS_CODE")
						+"CLASS_NAME="+object2.getString("CLASS_NAME"),"server.txt");										
				// ����InaccountDAO����
	        	vmc_classDAO classDAO = new vmc_classDAO(EVServerService.this);
	            // ����Tb_inaccount����
	        	Tb_vmc_class tb_vmc_class = new Tb_vmc_class(object2.getString("CLASS_CODE"), object2.getString("CLASS_NAME"),object2.getString("LAST_EDIT_TIME"),"");
	        	classDAO.addorupdate(tb_vmc_class);// �޸�
			}
		}
		else if(ToolClass.getServerVer()==1)//һ�ں�̨
		{
			JSONArray arr1=jsonObject.getJSONArray("List");
			for(int i=0;i<arr1.length();i++)
			{
				JSONObject object2=arr1.getJSONObject(i);
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","������Ʒ����CLASS_CODE="+object2.getString("CLASS_CODE")
						+"CLASS_NAME="+object2.getString("CLASS_NAME")+"IS_DELETE="+object2.getInt("IS_DELETE")
						+"AttImg="+object2.getString("AttImg"),"server.txt");										
				// ����InaccountDAO����
	        	vmc_classDAO classDAO = new vmc_classDAO(EVServerService.this);
	            // ����Tb_inaccount����
	        	Tb_vmc_class tb_vmc_class = new Tb_vmc_class(object2.getString("CLASS_CODE"), object2.getString("CLASS_NAME"),object2.getString("LAST_EDIT_TIME"),object2.getString("AttImg"));
	        	//ɾ����Ʒ����
	        	if(object2.getInt("IS_DELETE")==1) 
	        	{
	        		classDAO.detele(tb_vmc_class);
	        	}
	        	else
	        	{
	        		classDAO.addorupdate(tb_vmc_class);// �޸�
	        	}
	        	//����ǩ����ɺ󣬸���ʱ���
				if(ischeck==true) 
				{
					LAST_CLASS_TIME=ToolClass.getLasttime();
				}
			}
			
		}
	}
	
	//������Ʒ��Ϣ
	private boolean updatevmcProduct(String classrst) throws JSONException
	{
		JSONObject jsonObject = new JSONObject(classrst); 
		JSONArray arr1=jsonObject.getJSONArray("ProductList");
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","������Ʒ="+arr1.length()+"txt="+classrst,"server.txt");
		
		for(int i=0;i<arr1.length();i++)
		{
			JSONObject object2=arr1.getJSONObject(i);
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","���µ�["+i+"]����Ʒ="+object2.toString(),"server.txt");
			//��ȡ����Ʒ�Ƿ��ж�Ӧ�ķ�����Ϣ
			String prono=object2.getString("product_NO");			
			if(classjoin.containsKey(prono))
			{
				object2.put("product_Class_NO", classjoin.get(prono));
			}
			else
			{
				object2.put("product_Class_NO", "");
			}	
			String product_Class_NO=(ToolClass.isEmptynull(object2.getString("product_Class_NO")))?"0":object2.getString("product_Class_NO");
			product_Class_NO=product_Class_NO.substring(product_Class_NO.lastIndexOf(',')+1,product_Class_NO.length());
			String product_TXT=object2.getString("product_TXT");
			//�ɱ���market_Price�����ۼ�sales_Price��������promotion_Priceһ����sales_PriceΪ׼
			//����ǩ����ɺ󣬸�����Ʒ��Ϣʱ���
			if(ischeck==true) 
			{
				LAST_PRODUCT_TIME=ToolClass.getLasttime();
			}
			// ����InaccountDAO����
			vmc_productDAO productDAO = new vmc_productDAO(EVServerService.this);
            //����Tb_inaccount����
			Tb_vmc_product tb_vmc_product = new Tb_vmc_product(object2.getString("product_NO"), object2.getString("product_Name"),product_TXT,Float.parseFloat(object2.getString("market_Price")),
					Float.parseFloat(object2.getString("sales_Price")),0,object2.getString("create_Time"),object2.getString("last_Edit_Time"),object2.getString("AttImg"),"","",0,0);
			ToolClass.Log(ToolClass.INFO,"EV_SERVER",">>��ʼ����"+i+"txt=product_NO="+tb_vmc_product.getProductID()
					+"product_Name="+tb_vmc_product.getProductName()+"product_Class_NO="+product_Class_NO
					+"AttImg="+tb_vmc_product.getAttBatch1()+"product_TXT="+tb_vmc_product.getProductDesc(),"server.txt");	
			productDAO.addorupdate(tb_vmc_product,product_Class_NO);// �޸�
		}
		return true;
	}
		
	//���»�����Ϣ
	private void updatevmcColumn(String classrst) throws JSONException
	{
		JSONObject jsonObject = new JSONObject(classrst); 
		JSONArray arr1=null;
		if(ToolClass.getServerVer()==0)//�ɵĺ�̨
		{
			arr1=jsonObject.getJSONArray("PathList");
		}
		else if(ToolClass.getServerVer()==1)//һ�ں�̨
		{
			arr1=jsonObject.getJSONArray("List");
		}
		for(int i=0;i<arr1.length();i++)
		{
			JSONObject object2=arr1.getJSONObject(i);
			int PATH_ID=object2.getInt("PATH_ID");
			int CABINET_NO=Integer.parseInt(object2.getString("CABINET_NO"));
			int PATH_NO=Integer.parseInt(object2.getString("PATH_NO"));
			String PATH_NOSTR=(PATH_NO<10)?("0"+String.valueOf(PATH_NO)):String.valueOf(PATH_NO);
			int PATH_REMAINING=Integer.parseInt(object2.getString("PATH_REMAINING"));
			int IS_DELETE=0;
			if(ToolClass.getServerVer()==1)//һ�ں�̨
			{
				IS_DELETE=Integer.parseInt(object2.getString("IS_DELETE"));
			}
			int status=0;//����״̬
			int j=0;
			//�������
	        Set<Map.Entry<String,Integer>> allset=huoSet.entrySet();  //ʵ����
	        Iterator<Map.Entry<String,Integer>> iter=allset.iterator();
	        while(iter.hasNext())
	        {
	            Map.Entry<String,Integer> me=iter.next();
				String huo=me.getKey();
				int cabid=Integer.parseInt(huo.substring(0, 1));
				int huoid=Integer.parseInt(huo.substring(1, huo.length()));
				status=me.getValue();				
			    if((CABINET_NO==cabid)&&(PATH_NO==huoid))
			    {
			    	j=1;
			    	break;
			    }
				//ToolClass.Log(ToolClass.INFO,"EV_SERVER","huo="+cabid+","+huoid+"sta="+me.getValue(),"server.txt");
	        } 			

	        //���Ը��»���
			if(j==1)
			{		
				status=updatehuodaostatus(status,PATH_REMAINING);
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","���»���PATH_ID="+PATH_ID+"CABINET_NO="+object2.getString("CABINET_NO")
						+"PATH_NO="+PATH_NOSTR+"PRODUCT_NO="+object2.getString("PRODUCT_NO")
						+"PATH_COUNT="+object2.getString("PATH_COUNT")+"IS_DELETE="+IS_DELETE,"server.txt");	
				// ����InaccountDAO����
    			vmc_columnDAO columnDAO = new vmc_columnDAO(EVServerService.this);
	            //����Tb_inaccount����
    			Tb_vmc_column tb_vmc_column = new Tb_vmc_column(object2.getString("CABINET_NO"), PATH_NOSTR,"",object2.getString("PRODUCT_NO"),
    					Integer.parseInt(object2.getString("PATH_COUNT")),Integer.parseInt(object2.getString("PATH_REMAINING")),
    					status,"",PATH_ID,0); 
    			if(ToolClass.getServerVer()==0)//�ɵĺ�̨
    			{
	    			columnDAO.addorupdateforserver(tb_vmc_column);// ��ӻ�����Ϣ
	    			//�鿴��������Ӧ����Ʒ�Ƿ����
	    			// ����InaccountDAO����
	    			vmc_productDAO productDAO = new vmc_productDAO(EVServerService.this);
	    			//����Tb_inaccount����
	    			Tb_vmc_product tb_vmc_product = productDAO.find(object2.getString("PRODUCT_NO"));
	    			if(tb_vmc_product==null)
	    			{
	    				ToolClass.Log(ToolClass.INFO,"EV_SERVER","��ƷPRODUCT_NO="+object2.getString("PRODUCT_NO")
	    						+"������","server.txt");	
	    				isspempty=true;    				
	    			}
    			}	
    			else if(ToolClass.getServerVer()==1)//һ�ں�̨
    			{
    				//ɾ������
    				if(IS_DELETE==1)
    				{
    					columnDAO.detele(tb_vmc_column);
    				}
    				else
        			{
    	    			columnDAO.addorupdateforserver(tb_vmc_column);// ��ӻ�����Ϣ
    	    			//�鿴��������Ӧ����Ʒ�Ƿ����
    	    			// ����InaccountDAO����
    	    			vmc_productDAO productDAO = new vmc_productDAO(EVServerService.this);
    	    			//����Tb_inaccount����
    	    			Tb_vmc_product tb_vmc_product = productDAO.find(object2.getString("PRODUCT_NO"));
    	    			if(tb_vmc_product==null)
    	    			{
    	    				ToolClass.Log(ToolClass.INFO,"EV_SERVER","��ƷPRODUCT_NO="+object2.getString("PRODUCT_NO")
    	    						+"������","server.txt");	
    	    				isspempty=true;    				
    	    			}
        			}
    			}    			
			}
			//���»���ʧ��
			else
			{
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","���»���ʧ��CABINET_NO="+object2.getString("CABINET_NO")
						+"PATH_NO="+PATH_NOSTR+"PRODUCT_NO="+object2.getString("PRODUCT_NO")
						+"PATH_COUNT="+object2.getString("PATH_COUNT"),"server.txt");										
			}			
		}
	}
	
	//���»���״̬��Ϣ
	//huostate�ӻ������ϵõ��Ļ���״̬��upremain�ӷ�������صĻ����������
	private int updatehuodaostatus(int huostate,int upremain)
	{
		int huostatus=0;
		if(huostate==0)//��������
			huostatus=2;
		else if(huostate==1)//��������
		{
			if(upremain>0)
	    		huostatus=1;
	    	else                             //ȱ��
	    		huostatus=3;	
		}
		return huostatus;
	}
	
	//��ȡ��Ҫ�ϱ��ı���
	private JSONArray grid()
	{		
		//��֧������
		String[] ordereID;// ����ID[pk]
		String[] payType;// ֧����ʽ0�ֽ�1������2֧����������3֧������ά�룬4΢��ɨ��
		String[] payStatus;// ����״̬0�����ɹ���1����ʧ�ܣ�2֧��ʧ�ܣ�3δ֧��
		String[] RealStatus;// �˿�״̬��0����ʾδ�����˿����1�˿���ɣ�2�����˿3�˿�ʧ��
		String[] smallNote;// ֽ�ҽ��
		String[] smallConi;// Ӳ�ҽ��
		String[] smallAmount;// �ֽ�Ͷ����
		String[] smallCard;// ���ֽ�֧�����
		String[] shouldPay;// ��Ʒ�ܽ��
		String[] shouldNo;// ��Ʒ������
		String[] realNote;// ֽ���˱ҽ��
		String[] realCoin;// Ӳ���˱ҽ��
		String[] realAmount;// �ֽ��˱ҽ��
		String[] debtAmount;// Ƿ����
		String[] realCard;// ���ֽ��˱ҽ��
		String[] rfd_card_no;//����
		String[] payTime;//֧��ʱ��
			//��ϸ֧������
		String[] productID;//��Ʒid
		String[] cabID;//�����
		String[] columnID;//������
		    //��Ʒ��Ϣ
		String[] productName;// ��Ʒȫ��
		String[] salesPrice;// �Żݼ�,�硱20.00��
		
		//�������Ͷ�����Ϣ
	    //��֧������
		int[] payTypevalue;// ֧����ʽ0�ֽ�1������2֧����������3֧������ά�룬4΢��ɨ��
	    int[] payStatusvalue;// ����״̬0�����ɹ���1����ʧ�ܣ�2֧��ʧ�ܣ�3δ֧��
	    int[] RealStatusvalue;// �˿�״̬��0����ʾδ�����˿����1�˿���ɣ�2�����˿3�˿�ʧ��
	  	double[] smallNotevalue;// ֽ�ҽ��
		double[] smallConivalue;// Ӳ�ҽ��
		double[] smallAmountvalue;// �ֽ�Ͷ����
		double[] smallCardvalue;// ���ֽ�֧�����
		double[] shouldPayvalue;// ��Ʒ�ܽ��
		double[] shouldNovalue;// ��Ʒ������
		double[] realNotevalue;// ֽ���˱ҽ��
		double[] realCoinvalue;// Ӳ���˱ҽ��
		double[] realAmountvalue;// �ֽ��˱ҽ��
		double[] debtAmountvalue;// Ƿ����
		double[] realCardvalue;// ���ֽ��˱ҽ��
	  	//��Ʒ��Ϣ
		double[] salesPricevalue;// �Żݼ�,�硱20.00��
//		int ourdercount=0;//��¼������
		JSONArray array=new JSONArray();
		
		
		Vmc_OrderAdapter vmc_OrderAdapter=new Vmc_OrderAdapter();
		vmc_OrderAdapter.grid(EVServerService.this);
		//��֧������
		ordereID = vmc_OrderAdapter.getOrdereID();// ����ID[pk]
		payType = vmc_OrderAdapter.getPayType();// ֧����ʽ0�ֽ�1������2֧����������3֧������ά�룬4΢��ɨ��
		payStatus = vmc_OrderAdapter.getPayStatus();// ����״̬0�����ɹ���1����ʧ�ܣ�2֧��ʧ�ܣ�3δ֧��
		RealStatus = vmc_OrderAdapter.getRealStatus();// �˿�״̬��0����ʾδ�����˿����1�˿���ɣ�2�����˿3�˿�ʧ��
		smallNote = vmc_OrderAdapter.getSmallNote();// ֽ�ҽ��
		smallConi = vmc_OrderAdapter.getSmallConi();// Ӳ�ҽ��
		smallAmount = vmc_OrderAdapter.getSmallAmount();// �ֽ�Ͷ����
		smallCard = vmc_OrderAdapter.getSmallCard();// ���ֽ�֧�����
		shouldPay = vmc_OrderAdapter.getShouldPay();// ��Ʒ�ܽ��
		shouldNo = vmc_OrderAdapter.getShouldNo();// ��Ʒ������
		realNote = vmc_OrderAdapter.getRealNote();// ֽ���˱ҽ��
		realCoin = vmc_OrderAdapter.getRealCoin();// Ӳ���˱ҽ��
		realAmount = vmc_OrderAdapter.getRealAmount();// �ֽ��˱ҽ��
		debtAmount = vmc_OrderAdapter.getDebtAmount();// Ƿ����
		realCard = vmc_OrderAdapter.getRealCard();// ���ֽ��˱ҽ��
		rfd_card_no = vmc_OrderAdapter.getRfd_card_no();//����
		payTime = vmc_OrderAdapter.getPayTime();//֧��ʱ��
		//��ϸ֧������
		productID = vmc_OrderAdapter.getProductID();//��Ʒid
		cabID = vmc_OrderAdapter.getCabID();//�����
	    columnID = vmc_OrderAdapter.getColumnID();//������
	    //��Ʒ��Ϣ
	    productName = vmc_OrderAdapter.getProductName();// ��Ʒȫ��
	    salesPrice = vmc_OrderAdapter.getSalesPrice();// �Żݼ�,�硱20.00��
	    
	    //�������Ͷ�����Ϣ
	    payTypevalue= vmc_OrderAdapter.getPayTypevalue();// ֧����ʽ0�ֽ�1������2֧����������3֧������ά�룬4΢��ɨ��
		payStatusvalue = vmc_OrderAdapter.getPayStatusvalue();// ����״̬0�����ɹ���1����ʧ�ܣ�2֧��ʧ�ܣ�3δ֧��
		RealStatusvalue = vmc_OrderAdapter.getRealStatusvalue();// �˿�״̬��0����ʾδ�����˿����1�˿���ɣ�2�����˿3�˿�ʧ��
	    smallNotevalue= vmc_OrderAdapter.getSmallNotevalue();// ֽ�ҽ��
	    smallConivalue= vmc_OrderAdapter.getSmallConivalue();// Ӳ�ҽ��
	    smallAmountvalue= vmc_OrderAdapter.getSmallAmountvalue();// �ֽ�Ͷ����
	    smallCardvalue= vmc_OrderAdapter.getSmallCardvalue();// ���ֽ�֧�����
	    shouldPayvalue= vmc_OrderAdapter.getShouldPayvalue();// ��Ʒ�ܽ��
	    shouldNovalue= vmc_OrderAdapter.getShouldNovalue();// ��Ʒ������
	    realNotevalue= vmc_OrderAdapter.getRealNotevalue();// ֽ���˱ҽ��
	    realCoinvalue= vmc_OrderAdapter.getRealCoinvalue();// Ӳ���˱ҽ��
	    realAmountvalue= vmc_OrderAdapter.getRealAmountvalue();// �ֽ��˱ҽ��
	    debtAmountvalue= vmc_OrderAdapter.getDebtAmountvalue();// Ƿ����
	    realCardvalue= vmc_OrderAdapter.getRealCardvalue();// ���ֽ��˱ҽ��
	    //��Ʒ��Ϣ
	    salesPricevalue= vmc_OrderAdapter.getSalesPricevalue();// �Żݼ�,�硱20.00��
//	    ourdercount=vmc_OrderAdapter.getCount();
	    
	    int orderStatus=0;//1δ֧��,2�����ɹ�,3����δ���
	    int payStatue=0;//0δ����,1���ڸ���,2�������,3����ʧ��
	    int payTyp=0;//0�ֽ�,1֧��������,2����,3֧������ά��,4΢�ţ�7����
	    int actualQuantity=0;//ʵ�ʳ���,1�ɹ�,0ʧ��
	    String RefundAmount="";//������
	    int Status=0;//0��δ�˿1�������˿2���˿�ɹ���3���˿�ʧ��'
	    try {
	    	for(int x=0;x<vmc_OrderAdapter.getCount();x++)	
			{
		    	// ����״̬0�����ɹ���1����ʧ�ܣ�2֧��ʧ�ܣ�3δ֧��
		    	if(payStatusvalue[x]==2||payStatusvalue[x]==3)
				{
		    		orderStatus=1;
		    		payStatue=3;
		    		actualQuantity=0;
				}
				else if(payStatusvalue[x]==0)
				{
					orderStatus=2;
					payStatue=2;
					actualQuantity=1;
				}
				else if(payStatusvalue[x]==1)
				{
					orderStatus=3;
					payStatue=2;
					actualQuantity=0;
				}
		    	// ֧����ʽ0�ֽ�1������2֧����������3֧������ά�룬4΢��ɨ��-1ȡ����,7����
		    	if(payTypevalue[x]==0)
				{
		    		payTyp=0;
				}
				else if(payTypevalue[x]==1)
				{
					payTyp=2;
				}
				else if(payTypevalue[x]==2)
				{
					payTyp=1;
				}
				else if(payTypevalue[x]==3)
				{
					payTyp=3;
				}
				else if(payTypevalue[x]==4)
				{
					payTyp=4;
				}
				else if(payTypevalue[x]==-1)
				{
					payTyp=-1;
				}
				else
				{
					payTyp=payTypevalue[x];
				}
		    	
		    	if(RealStatusvalue[x]==0)
		    	{
		    		Status=0;
		    	}
		    	else if(RealStatusvalue[x]==1)
		    	{
		    		Status=2;
		    	}
		    	else if(RealStatusvalue[x]==2||RealStatusvalue[x]==3)
		    	{
		    		Status=3;
		    	}
		    	RefundAmount=String.valueOf(realAmountvalue[x]+realCardvalue[x]);
		    	//ToolClass.Log(ToolClass.INFO,"EV_SERVER","����payStatus="+payStatusvalue[x]+"payType="+payTypevalue[x],"server.txt");
				
//		    	ToolClass.Log(ToolClass.INFO,"EV_SERVER","����orderNo="+ordereID[x]+"orderTime="+ToolClass.getStrtime(payTime[x])+"orderStatus="+orderStatus+"payStatus="
//				+payStatue+"payType="+payTyp+"shouldPay="+shouldPay[x]+"RefundAmount="+RefundAmount+"Status="+Status+"productNo="+productID[x]+"quantity="+1+
//				"actualQuantity="+actualQuantity+"customerPrice="+salesPrice[x]+"productName="+productName[x],"server.txt");			
		    	JSONObject object=new JSONObject();
		    	object.put("orderNo", ordereID[x]);
		    	object.put("orderTime", ToolClass.getStrtime(payTime[x]));
		    	object.put("orderStatus", orderStatus);//1δ֧��,2�����ɹ�,3����δ���
		    	object.put("payStatus", payStatue);//0δ����,1���ڸ���,2�������,3����ʧ��
		    	object.put("payType", payTyp);//֧������:0=�ֽ�1=֧����2=����3=��ά��4=΢֧��
		    	object.put("shouldPay", shouldPay[x]);//��Ʒ�ܽ��		    	
		    	object.put("RefundAmount", RefundAmount);//�˿��ܽ��
		    	object.put("Status", Status);//�˿�״̬:0��δ�˿1�������˿2���˿�ɹ���3���˿�ʧ��
		    	object.put("rfd_card_no", rfd_card_no[x]);//����
		    	object.put("productNo", productID[x]);		    	
		    	object.put("quantity", 1);
		    	object.put("actualQuantity", actualQuantity);
		    	object.put("customerPrice", salesPrice[x]);
		    	object.put("productName", productName[x]);	
		    	
		    	//һ�ں�̨
		    	if(ToolClass.getServerVer()==1)//һ�ں�̨
		    	{
		    		object.put("NOTE_AMOUNT", smallNotevalue[x]);//ֽ���ܽ��
		    		object.put("COIN_AMOUNT", smallConivalue[x]);//Ӳ���ܽ��
		    		object.put("CASH_AMOUNT", smallAmountvalue[x]);//�ֽ��ܽ��
		    		object.put("REFUND_NOTE_AMOUNT", realNotevalue[x]);//�˿�ֽ���ܽ��
		    		object.put("REFUND_COIN_AMOUNT", realCoinvalue[x]);//�˿�Ӳ���ܽ��
		    		object.put("REFUND_CASH_AMOUNT", realAmountvalue[x]);//�˿��ܽ��
		    		object.put("AMOUNT_OWED", debtAmountvalue[x]);//Ƿ����
		    		object.put("Amount", realCardvalue[x]);//���ֽ��˿���
		    		object.put("Cab", cabID[x]);//���
		    		object.put("PATH_NO", columnID[x]);//������
		    	}
		    	ToolClass.Log(ToolClass.INFO,"EV_SERVER","����="+object.toString(),"server.txt");	
		    	
		    	array.put(object);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	    return array;
	}
	
	
	//�޸������ϱ�״̬Ϊ���ϱ�
	private void updategrid(String str)
	{	
		// ����InaccountDAO����
  		vmc_orderDAO orderDAO = new vmc_orderDAO(EVServerService.this);
  		
		JSONArray json=null;
		try {
			json=new JSONArray(str);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service �ϱ����׼�¼�ɹ�="+json.toString(),"server.txt");
		for(int i=0;i<json.length();i++)
		{
			JSONObject object2=null;
			String orderno=null;
			try {
				object2 = json.getJSONObject(i);
				orderno=object2.getString("orderno");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","�ϱ����׼�¼="+orderno
					,"server.txt");
			orderDAO.update(orderno);
		}		  	
	}
	
	//��ȡ��Ҫ�ϱ��Ļ���
	private JSONArray columngrid()
	{				
		JSONArray array=new JSONArray();
		
		// ����InaccountDAO����
		vmc_columnDAO columnDAO = new vmc_columnDAO(EVServerService.this);
		List<Tb_vmc_column> listinfos=columnDAO.getScrollPay();
		String[] pathIDs= new String[listinfos.size()];
		String[] cabinetNumbers= new String[listinfos.size()];
		String[] pathNames= new String[listinfos.size()];
		String[] productIDs= new String[listinfos.size()];
		String[] productNums= new String[listinfos.size()];
		String[] pathCounts= new String[listinfos.size()];
		String[] pathStatuss= new String[listinfos.size()];
		String[] pathRemainings= new String[listinfos.size()];
		String[] lastedittimes= new String[listinfos.size()];
		String[] isdisables= new String[listinfos.size()];
		String[] isupload= new String[listinfos.size()];
		int x=0;
		try {
			// ����List���ͼ���
	  	    for (Tb_vmc_column tb_inaccount : listinfos) 
	  	    {
	  	    	//��֧������
	  	    	pathIDs[x]= String.valueOf(tb_inaccount.getPath_id());
	  	    	cabinetNumbers[x]= tb_inaccount.getCabineID();
	  	    	String PATH_NOSTR=String.valueOf(Integer.parseInt(tb_inaccount.getColumnID()));
	  			pathNames[x]= PATH_NOSTR;
	  			productIDs[x]= tb_inaccount.getCabineID();
	  			productNums[x]= tb_inaccount.getProductID();
	  			pathCounts[x]= String.valueOf(tb_inaccount.getPathCount());
	  			pathStatuss[x]= String.valueOf(tb_inaccount.getColumnStatus());
	  			pathRemainings[x]= String.valueOf(tb_inaccount.getPathRemain());
	  			lastedittimes[x]= tb_inaccount.getLasttime();
	  			isdisables[x]= "0";
	  			isupload[x] = String.valueOf(tb_inaccount.getIsupload());
	  			ToolClass.Log(ToolClass.INFO,"EV_SERVER","�贫����pathID="+pathIDs[x]+"cabinetNumber="+cabinetNumbers[x]+"pathName="+pathNames[x]+"productID="
	  					+productIDs[x]+"productNum="+productNums[x]+"pathCount="+pathCounts[x]+"pathStatus="+pathStatuss[x]+"pathRemaining="+pathRemainings[x]+"lastedittime="+ToolClass.getStrtime(lastedittimes[x])+
	  					"isdisable="+isdisables[x]+"isupload="+isupload[x],"server.txt");			
		    	JSONObject object=new JSONObject();
		    	object.put("pathID", pathIDs[x]);
		    	object.put("cabinetNumber", cabinetNumbers[x]);
		    	object.put("pathName", pathNames[x]);	    	
		    	object.put("productID", productIDs[x]);
		    	object.put("productNum", productNums[x]);
		    	object.put("pathCount", pathCounts[x]);
		    	object.put("pathStatus", pathStatuss[x]);
		    	object.put("pathRemaining", pathRemainings[x]);
		    	object.put("lastedittime", ToolClass.getStrtime(lastedittimes[x]));		    	
		    	object.put("isdisable", isdisables[x]);
		    	object.put("isupload", isupload[x]);
		    	array.put(object);
	  			x++;  			
	  	    }		
		} catch (Exception e) {
			// TODO: handle exception
		}		
	    return array;
	}
	
	//�޸Ļ����ϱ�״̬Ϊ���ϱ�
	private void updatecolumns(String str)
	{	
		// ����InaccountDAO����
  		vmc_columnDAO columnDAO = new vmc_columnDAO(EVServerService.this);
  		
		JSONArray json=null;
		try {
			json=new JSONArray(str);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service �ϱ������ɹ�="+json.toString(),"server.txt");
		for(int i=0;i<json.length();i++)
		{
			JSONObject object2=null;
			String cabinetNumber=null;
			String pathName=null;
			try {
				object2 = json.getJSONObject(i);
				cabinetNumber=object2.getString("cabinetNumber");
				int PATH_NO=Integer.parseInt(object2.getString("pathName"));
				String PATH_NOSTR=(PATH_NO<10)?("0"+String.valueOf(PATH_NO)):String.valueOf(PATH_NO);
				pathName=PATH_NOSTR;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","�ϱ�����cabinetNumber="+cabinetNumber
					+"pathName="+pathName,"server.txt");
			//����ǩ����ɺ󣬸�����Ʒ��Ϣʱ���
			if(ischeck==true)
			{
				LAST_EDIT_TIME=ToolClass.getLasttime();
			}
			columnDAO.updatecol(cabinetNumber,pathName);
		}		  	
	}

	/**
     * ��̨��Ĭ��װapk�ļ�
     */
    private void installApk(String ATTIDS)
    {        	
    	
        ToolClass.Log(ToolClass.INFO,"EV_SERVER","����["+ATTIDS+"]��ʼ��װ...","server.txt");
        String attimg=ATTIDS.substring(ATTIDS.lastIndexOf(".") + 1).toUpperCase();
        ToolClass.Log(ToolClass.INFO,"EV_SERVER","�����ʽ["+attimg+"]","server.txt");
        //��ѹ����,��ѹ��
        if(attimg.equals("RAR")||attimg.equals("ZIP"))
        {
        	//1.��ѹ��
        	String zipFile = ToolClass.setAPKFile(ATTIDS).toString();
        	String upzipFile=ToolClass.getEV_DIR()+File.separator+"APKFile"+File.separator;
	  		ToolClass.Log(ToolClass.INFO,"EV_SERVER","APP<<zipFile="+zipFile+" upzipFile="+upzipFile,"server.txt"); 
	  		if(attimg.equals("RAR"))
	  		{
	  			try {
		  			XZip.unRarFile(zipFile, upzipFile);
		  		} catch (Exception e) {
		  			// TODO Auto-generated catch block
		  			e.printStackTrace();
		  		}
	  		}
	  		else if(attimg.equals("ZIP"))
	  		{
		  		try {
		  			XZip.UnZipFolder(zipFile, upzipFile);
		  		} catch (Exception e) {
		  			// TODO Auto-generated catch block
		  			e.printStackTrace();
		  		}
	  		}
	  		//2.�������Ŀ¼
	  		File dirName = new File(upzipFile);
	  		//��������ļ�����������ļ�
	   		File[] files = dirName.listFiles();
	   		if (files.length > 0) 
	   		{  
	   			for (int i = 0; i < files.length; i++) 
	   			{
	   			  if(!files[i].isDirectory())
	   			  {	
	   				try
	        		{
		   				String filename=files[i].toString();
		   				ToolClass.Log(ToolClass.INFO,"EV_SERVER"," �жϰ�װĿ¼���ļ�="+filename,"server.txt"); 
		   				String attimg2=filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();
		   		        ToolClass.Log(ToolClass.INFO,"EV_SERVER","�����ʽ["+attimg2+"]","server.txt");
		   		        if(attimg2.equals("APK"))
		   		        {
		   		        	String tempATTIDS=filename.substring(filename.lastIndexOf("/") + 1);
		   		        	//�����װ
		   		        	if(tempATTIDS.equals("EVConsole.apk"))
		   		        	{
		   		        		ATTIDS=tempATTIDS;
		   		        		ToolClass.Log(ToolClass.INFO,"EV_SERVER","����["+ATTIDS+"]���ᰲװ...","server.txt");
			   		        	continue;
		   		        	}
		   		        	else
		   		        	{
		   		        		ToolClass.Log(ToolClass.INFO,"EV_SERVER","����["+tempATTIDS+"]��ʼ��װ...","server.txt");
			   		        	//1.����ʾ�İ�װ
			   		             File fileName = ToolClass.setAPKFile(tempATTIDS);
			   		             Intent intent = new Intent();  
			   		             //ִ�ж���  
			   		             intent.setAction(Intent.ACTION_VIEW); 
			   		             intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			   		             //ִ�е���������  
			   		             intent.setDataAndType(Uri.fromFile(fileName), "application/vnd.android.package-archive");  
			   		             startActivity(intent);
		   		        	}
		   		        }
	        		}
	        		catch(Exception e)
	        		{
	        			ToolClass.Log(ToolClass.INFO,"EV_SERVER","�ļ�="+files[i].toString()+"�쳣���޷��ж�","server.txt");
	        		}
	   			  }
	   			}
	   		}
        }
        
        ToolClass.Log(ToolClass.INFO,"EV_SERVER","����["+ATTIDS+"]��ʼ��װ...","server.txt");
        //1.����ʾ�İ�װ
        File fileName = ToolClass.setAPKFile(ATTIDS);
        Intent intent = new Intent();  
        //ִ�ж���  
        intent.setAction(Intent.ACTION_VIEW); 
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
        //ִ�е���������  
        intent.setDataAndType(Uri.fromFile(fileName), "application/vnd.android.package-archive");  
        startActivity(intent); 
        
//        File fileName = new File(  
//        		ToolClass.getEV_DIR()+File.separator+ATTIDS);
//        try {  
//            process = Runtime.getRuntime().exec("su");  
//            out = process.getOutputStream();  
//            DataOutputStream dataOutputStream = new DataOutputStream(out);  
//            dataOutputStream.writeBytes("chmod 777 " + fileName.getPath() + "\n");  
//            dataOutputStream.writeBytes("LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install -r " +  
//            		fileName.getPath());  
//            // �ύ����  
//            dataOutputStream.flush();  
//            // �ر�������  
//            dataOutputStream.close();  
//            out.close();  
//            int value = process.waitFor();  
//              
//            // ����ɹ�  
//            if (value == 0) 
//            {  
//                result = true;  
//                //2.ɱ��������
//	            //android.os.Process.killProcess(android.os.Process.myPid());
//            } else if (value == 1) { // ʧ��  
//                result = false;  
//            } else { // δ֪���  
//                result = false;  
//            }  
//        } catch (IOException e) {  
//            e.printStackTrace();  
//        } catch (InterruptedException e) {  
//            e.printStackTrace();  
//        }
    }
    
    //������������
  	private void setshutdownalarm(String RESTART_TIME,int RESTART_SKIP)
  	{
  		ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service ��ȡ�豸��Ϣ�ɹ�,����ʱ��RESTART_SKIP="+RESTART_SKIP+"RESTART_TIME="+RESTART_TIME,"server.txt");						
  		alarm=(AlarmManager)super.getSystemService(Context.ALARM_SERVICE);//ȡ�����ӷ���
		
  		String a[] = RESTART_TIME.split(":"); 
  		int hour=Integer.parseInt(a[0]);
  		int minute=Integer.parseInt(a[1]);
  		int second=Integer.parseInt(a[2]);
  		SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd" + " "  
                + "HH:mm:ss"); //��ȷ������ 
  		//����ʱ��
    	Calendar todayStart = Calendar.getInstance();  
    	todayStart.set(Calendar.HOUR_OF_DAY, hour);  
        todayStart.set(Calendar.MINUTE, minute);  
        todayStart.set(Calendar.SECOND, second);  
        todayStart.set(Calendar.MILLISECOND, 0); 
        
        Calendar nowdate = Calendar.getInstance();//��ǰʱ�� 
        int i=todayStart.compareTo(nowdate);
        if(i>0)
        {
        	ToolClass.Log(ToolClass.INFO,"EV_SERVER","����ʱ��ȵ�ǰʱ����","server.txt");
        }
        else if(i==0)
        {
        	ToolClass.Log(ToolClass.INFO,"EV_SERVER","����ʱ���뵱ǰʱ����ͬ","server.txt");
        }
        if(i<0)
        {
        	ToolClass.Log(ToolClass.INFO,"EV_SERVER","����ʱ��ȵ�ǰʱ���磬�Ӻ�һ��","server.txt");
        	todayStart.add(todayStart.DATE,1);//��������������һ��.����������,������ǰ�ƶ�
        }
        
    	Date date = todayStart.getTime(); 
        String starttime=tempDate.format(date);
        ParsePosition posstart = new ParsePosition(0);  
//    	Date dstart = (Date) tempDate.parse(starttime, posstart);
    	ToolClass.Log(ToolClass.INFO,"EV_SERVER","����ʱ��="+starttime+",="+todayStart.getTimeInMillis(),"server.txt");
    	//ɾ��ԭ����
    	delalarm();
        //����������
    	Intent intent=new Intent(EVServerService.this,ShutdownAlarmReceiver.class);
    	intent.setAction("shutdownalarm");
    	//һ����ʱ��֮�󣬾���ת����PendingIntent��װ��Intent�У������Intent������
    	//��ת��һ���㲥֮��
    	PendingIntent sender=PendingIntent.getBroadcast(EVServerService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    	//�������ӷ���
    	this.alarm.setRepeating(AlarmManager.RTC_WAKEUP, todayStart.getTimeInMillis(),1000*60*60*24*RESTART_SKIP, sender);
    	
  	}
  	
    //ɾ��ԭ����
  	private void delalarm()
      {
      	if(this.alarm!=null)
      	{
      		Intent intent=new Intent(EVServerService.this,ShutdownAlarmReceiver.class);
  	    	intent.setAction("shutdownalarm");
  	    	//һ����ʱ��֮�󣬾���ת����PendingIntent��װ��Intent�У������Intent������
  	    	//��ת��һ���㲥֮��
  	    	PendingIntent sender=PendingIntent.getBroadcast(EVServerService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
  	    	//ɾ�����ӷ���
  	    	this.alarm.cancel(sender);	    	
      	}
      }
  	
  	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub		
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","Service destroy","server.txt");
		//���ע�������
		localBroadreceiver.unregisterReceiver(receiver);
		//�ر��Լ�������ʱ��
		timer.shutdown();
		terminal.shutdown();
		super.onDestroy();
	}
}
