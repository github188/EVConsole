/****************************************Copyright (c)*************************************************
**                      Fujian Junpeng Communicaiton Technology Co.,Ltd.
**                               http://www.easivend.com.cn
**--------------File Info------------------------------------------------------------------------------
** File name:           COMService.java
** Last modified Date:  2015-01-10
** Last Version:         
** Descriptions:        �봮���豸���ӵķ���      
**------------------------------------------------------------------------------------------------------
** Created by:          guozhenzhen 
** Created date:        2015-01-10
** Version:             V1.0 
** Descriptions:        The original version       
********************************************************************************************************/

package com.easivend.view;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easivend.common.SerializableMap;
import com.easivend.common.ToolClass;
import com.easivend.dao.vmc_cabinetDAO;
import com.easivend.evprotocol.COMThread;
import com.easivend.evprotocol.EVprotocol;
import com.easivend.evprotocol.ExtraCOMThread;
import com.easivend.evprotocol.VboxProtocol;
import com.easivend.model.Tb_vmc_cabinet;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;

public class COMService extends Service {
	//=====================����(���ӣ����ɣ�����������ɽ)===================
	public static final int EV_CHECKALLCHILD= 1;	//��ѯȫ������״̬	
	public static final int EV_CHECKCHILD	= 3;	//������ѯ	
	public static final int EV_CHUHUOCHILD	= 4;	//��������		
	public static final int EV_LIGHTCHILD	= 5;	//����	
	public static final int EV_COOLCHILD	= 6;	//����	
	public static final int EV_HOTCHILD		= 7;	//����	
	public static final int EV_SETHUOCHILD	= 10;	//��������
	
	
	
	
	ActivityReceiver receiver;
	LocalBroadcastManager localBroadreceiver;
	//��ͨ�����߳�
	private Thread thread=null;
    private Handler mainhand=null,childhand=null; 
    COMThread comserial=null;
    //���贮���߳�
    private Thread extrathread=null;
    private Handler mainextrahand=null,childextrahand=null; 
    ExtraCOMThread extracomserial=null;
    ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);		
    Map<String,Integer> huoSet=new LinkedHashMap<String,Integer>();
    private String[] cabinetID = null;//���������������    
    private int huom = 0;// ����һ����ʼ��ʶ
    
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	//8.����activity�Ľ������㲥��������������
	public class ActivityReceiver extends BroadcastReceiver {

		@Override
		public synchronized void onReceive(Context context, Intent intent)
		{
			// TODO Auto-generated method stub
			Bundle bundle=intent.getExtras();
			int EVWhat=bundle.getInt("EVWhat");
			switch(EVWhat)
			{
			//*************************************************************	
			//�����豸ģ��:��ѯ�࣬�����������ϱ��ֵ࣬��ʹ��COMThread���У���Χ1-10��40-60
			//*************************************************************	
			//��ѯȫ������״̬
			case EV_CHECKALLCHILD:
				ToolClass.Log(ToolClass.INFO,"EV_COM","COMService ������ѯȫ��","com.txt");
				vmc_cabinetDAO cabinetDAO = new vmc_cabinetDAO(context);// ����InaccountDAO����
			    // 1.��ȡ���й��
			    List<Tb_vmc_cabinet> listinfos = cabinetDAO.getScrollData();
			    cabinetID = new String[listinfos.size()];// �����ַ�������ĳ���
			    // ����List���ͼ���
			    for (Tb_vmc_cabinet tb_inaccount : listinfos) 
			    {
			        cabinetID[huom] = tb_inaccount.getCabID();
			        ToolClass.Log(ToolClass.INFO,"EV_COM","��ȡ���="+cabinetID[huom],"com.txt");
				    huom++;// ��ʶ��1
			    }
			    huom=0;
			    if(listinfos.size()==0)
			    {
			    	//���ظ�activity�㲥
					Intent recintent=new Intent();
					recintent.putExtra("EVWhat", COMThread.EV_CHECKALLMAIN);						
					//��������
			        SerializableMap myMap=new SerializableMap();
			        myMap.setMap(huoSet);//��map������ӵ���װ��myMap<span></span>��
			        Bundle bundlerec=new Bundle();
			        bundlerec.putSerializable("result", myMap);
			        recintent.putExtras(bundlerec);
			        recintent.setAction("android.intent.action.comrec");//action���������ͬ
			        localBroadreceiver.sendBroadcast(recintent);
			    }
			    else
				{					
			    	//���һ�������
	        		vmc_cabinetDAO cabinetDAOrec = new vmc_cabinetDAO(context);// ����InaccountDAO����
	        		// ��ȡ����������Ϣ�����洢��List���ͼ�����
	        	    Tb_vmc_cabinet listinfosrec = cabinetDAOrec.findScrollData(cabinetID[huom]);
	        	    if((ToolClass.getExtraComType()==1)&&(listinfosrec.getCabType()==4))
					{
						childextrahand=extracomserial.obtainHandler();
						Message childrec=childextrahand.obtainMessage();
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMService ��ɽ���ѯ="+String.valueOf(bundle.getInt("cabinet")),"com.txt");
						childrec.what=COMThread.EV_BENTO_CHECKALLCHILD;
						JSONObject evrec=null;
			    		try {
			    			evrec=new JSONObject();
			    			evrec.put("cabinet", cabinetID[huom]);	    			  			
			    			ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+evrec.toString(),"com.txt");
			    		} catch (JSONException e) {
			    			// TODO Auto-generated catch block
			    			e.printStackTrace();
			    		}
			    		childrec.obj=evrec;
			    		childextrahand.sendMessage(childrec);	
					}
					else
					{	        	    
		        	    childhand=comserial.obtainHandler();
		        		Message childrec=childhand.obtainMessage();
		        		//���ӹ�
		        	    if(listinfosrec.getCabType()==5)
		        		{
		        			ToolClass.Log(ToolClass.INFO,"EV_COM","COMService ���ӹ��ѯ="+cabinetID[huom],"com.txt");
		        			childrec.what=COMThread.EV_BENTO_CHECKALLCHILD;
		        		}
		        	    //���ɻ���
		        	    else if(listinfosrec.getCabType()==1)
		        		{
		        			ToolClass.Log(ToolClass.INFO,"EV_COM","COMService �������ѯ="+cabinetID[huom],"com.txt");
		        			childrec.what=COMThread.EV_COLUMN_CHECKALLCHILD;
		        		}
		        	    //����������
		        		else if((listinfosrec.getCabType()==2)||(listinfosrec.getCabType()==3))
		        		{
		        			ToolClass.Log(ToolClass.INFO,"EV_COM","COMService ���������ѯ="+cabinetID[huom],"com.txt");
		        			childrec.what=COMThread.EV_ELEVATOR_CHECKALLCHILD;
		        		}
		        		JSONObject evrec=null;
			    		try {
			    			evrec=new JSONObject();
			    			evrec.put("cabinet", cabinetID[huom]);	    			  			
			    			ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+evrec.toString(),"com.txt");
			    		} catch (JSONException e) {
			    			// TODO Auto-generated catch block
			    			e.printStackTrace();
			    		}
			    		childrec.obj=evrec;
		        		childhand.sendMessage(childrec);
					}
				}
				break;    
			//������ѯ	
			case EV_CHECKCHILD:				
				//���һ�������
        		vmc_cabinetDAO cabinetDAO2 = new vmc_cabinetDAO(context);// ����InaccountDAO����
        	    // ��ȡ����������Ϣ�����洢��List���ͼ�����
        	    Tb_vmc_cabinet listinfos2 = cabinetDAO2.findScrollData(String.valueOf(bundle.getInt("cabinet")));
        	    
        	    if((ToolClass.getExtraComType()==1)&&(listinfos2.getCabType()==4))
				{
					childextrahand=extracomserial.obtainHandler();
					Message child2=childextrahand.obtainMessage();
					ToolClass.Log(ToolClass.INFO,"EV_COM","COMService ��ɽ���ѯ="+String.valueOf(bundle.getInt("cabinet")),"com.txt");
					child2.what=COMThread.EV_BENTO_CHECKCHILD;
					JSONObject ev2=null;
		    		try {
		    			ev2=new JSONObject();
		    			ev2.put("cabinet", bundle.getInt("cabinet"));	    			  			
		    			ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+ev2.toString(),"com.txt");
		    		} catch (JSONException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		}
		    		child2.obj=ev2;
		    		childextrahand.sendMessage(child2);	
				}
				else
				{       
					childhand=comserial.obtainHandler();
	        		Message child2=childhand.obtainMessage();	        		
	        	    //���ӹ�
	        	    if(listinfos2.getCabType()==5)
	        		{
	        			ToolClass.Log(ToolClass.INFO,"EV_COM","COMService ���ӹ��ѯ="+String.valueOf(bundle.getInt("cabinet")),"com.txt");
	        			child2.what=COMThread.EV_BENTO_CHECKCHILD;
	        		}
	        	    //���ɻ���
	        		else if(listinfos2.getCabType()==1)
	        		{
	        			ToolClass.Log(ToolClass.INFO,"EV_COM","COMService �������ѯ="+String.valueOf(bundle.getInt("cabinet")),"com.txt");
	        			child2.what=COMThread.EV_COLUMN_CHECKCHILD;
	        		}
	        	    //����������
	        		else if((listinfos2.getCabType()==2)||(listinfos2.getCabType()==3))
	        		{
	        			ToolClass.Log(ToolClass.INFO,"EV_COM","COMService ���������ѯ="+String.valueOf(bundle.getInt("cabinet")),"com.txt");
	        			child2.what=COMThread.EV_ELEVATOR_CHECKCHILD;
	        		}
	        		JSONObject ev2=null;
		    		try {
		    			ev2=new JSONObject();
		    			ev2.put("cabinet", bundle.getInt("cabinet"));	    			  			
		    			ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+ev2.toString(),"com.txt");
		    		} catch (JSONException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		}
		    		child2.obj=ev2;
	        		childhand.sendMessage(child2);
				}
				break;
			/**************************************************************	
			 *�����豸ģ��:�����࣬
			 * EVprotocol.EV_BENTO_OPEN=11	
			 * COMThread.EV_COLUMN_OPENCHILD=42	
			 * COMThread.EV_ELEVATOR_OPENCHILD=45		
			 */
			//*************************************************************/		
			//��������	
			case EV_CHUHUOCHILD:		
				//���һ�������
        		vmc_cabinetDAO cabinetDAO3 = new vmc_cabinetDAO(context);// ����InaccountDAO����
        	    // ��ȡ����������Ϣ�����洢��List���ͼ�����
        	    Tb_vmc_cabinet listinfos3 = cabinetDAO3.findScrollData(String.valueOf(bundle.getInt("cabinet")));
        	  
        	    if((ToolClass.getExtraComType()==1)&&(listinfos3.getCabType()==4))
				{
					childextrahand=extracomserial.obtainHandler();
					Message child3=childextrahand.obtainMessage();
					ToolClass.Log(ToolClass.INFO,"EV_COM","COMService ��ɽ�����","com.txt");					
	    			child3.what=EVprotocol.EV_BENTO_OPEN;
					JSONObject ev3=null;
		    		try {
		    			ev3=new JSONObject();
		    			ev3.put("cabinet", bundle.getInt("cabinet"));
		    			ev3.put("column", bundle.getInt("column"));	
		    			ev3.put("cost", bundle.getInt("cost"));	
		    			ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+ev3.toString(),"com.txt");
		    		} catch (JSONException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		}
		    		child3.obj=ev3;
		    		childextrahand.sendMessage(child3);	
				}
				else
				{
	        	    //ToolClass.Log(ToolClass.INFO,"EV_COM","COMService cabinet="+bundle.getInt("cabinet"),"com.txt");
					int cabinet=bundle.getInt("cabinet");
					childhand=comserial.obtainHandler();
	        	    Message child3=childhand.obtainMessage();				
	        		JSONObject ev3=null;
		    		try {
		    			ev3=new JSONObject();
		    			ev3.put("cabinet", bundle.getInt("cabinet"));	
		    			//���ӹ�
		        	    if(listinfos3.getCabType()==5)
		        		{
		        	    	ToolClass.Log(ToolClass.INFO,"EV_COM","COMService ���ӳ���","com.txt");
		    				child3.what=EVprotocol.EV_BENTO_OPEN;
		    				ev3.put("column", bundle.getInt("column"));
		        		}
		        	    //���ɻ���
		        	    else if(listinfos3.getCabType()==1)
		        		{
		        	    	ToolClass.Log(ToolClass.INFO,"EV_COM","COMService ��������","com.txt");
		    				child3.what=COMThread.EV_COLUMN_OPENCHILD;
		    				//����
		    				if(cabinet==2)
		    					ev3.put("column", ToolClass.columnChuhuo2(bundle.getInt("column")));
		    				//����
		    				else if(cabinet==1)
		    					ev3.put("column", ToolClass.columnChuhuo(bundle.getInt("column")));
		        		}
		        	    //����������
		        		else if((listinfos3.getCabType()==2)||(listinfos3.getCabType()==3))
		        		{
		        	    	ToolClass.Log(ToolClass.INFO,"EV_COM","COMService ����������","com.txt");
		    				child3.what=COMThread.EV_ELEVATOR_OPENCHILD;
		    				//����
		    				if(cabinet==2)
		    					ev3.put("column", ToolClass.elevatorChuhuo2(bundle.getInt("column")));
		    				//����
		    				else if(cabinet==1)
		    					ev3.put("column", ToolClass.elevatorChuhuo(bundle.getInt("column")));
		        		}	
		    			ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+ev3.toString(),"com.txt");
		    		} catch (JSONException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		}
		    		child3.obj=ev3;
	        		childhand.sendMessage(child3);	
				}
				break;
			//��������
			case EV_SETHUOCHILD:		
				childhand=comserial.obtainHandler();
				Message child7=childhand.obtainMessage();
				//���һ�������
        		vmc_cabinetDAO cabinetDAO7 = new vmc_cabinetDAO(context);// ����InaccountDAO����
        	    // ��ȡ����������Ϣ�����洢��List���ͼ�����
        	    Tb_vmc_cabinet listinfos7 = cabinetDAO7.findScrollData(String.valueOf(bundle.getInt("cabinet")));
        		
        		JSONObject ev7=null;
	    		try {
	    			ev7=new JSONObject();
	    			ev7.put("cabinet", bundle.getInt("cabinet"));	
	    			//���ӹ�
	        	    if(listinfos7.getCabType()==5)
	        		{
	        	    	ToolClass.Log(ToolClass.INFO,"EV_COM","COMService ���ӳ���","com.txt");
	    				child7.what=EVprotocol.EV_BENTO_OPEN;
	    				ev7.put("column", bundle.getInt("column"));
	        		}
	        	    //���ɻ���
	        	    else if(listinfos7.getCabType()==1)
	        		{
	        	    	ToolClass.Log(ToolClass.INFO,"EV_COM","COMService ��������","com.txt");
	    				child7.what=COMThread.EV_COLUMN_OPENCHILD;
	    				ev7.put("column", bundle.getInt("column"));
	        		}
	        	    //����������
	        		else if((listinfos7.getCabType()==2)||(listinfos7.getCabType()==3))
	        		{
	        	    	ToolClass.Log(ToolClass.INFO,"EV_COM","COMService ����������","com.txt");
	    				child7.what=COMThread.EV_ELEVATOR_OPENCHILD;
	    				ev7.put("column", bundle.getInt("column"));
	        		}
	    			ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+ev7.toString(),"com.txt");
	    		} catch (JSONException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	    		child7.obj=ev7;
        		childhand.sendMessage(child7);	
				break;
			case COMThread.VBOX_HUODAO_SET_INDALLCHILD:
				//���һ�������
        		vmc_cabinetDAO cabinetDAO4 = new vmc_cabinetDAO(context);// ����InaccountDAO����
        	    // ��ȡ����������Ϣ�����洢��List���ͼ�����
        	    Tb_vmc_cabinet listinfos4 = cabinetDAO4.findScrollData(String.valueOf(bundle.getInt("cabinet")));
        	  
        	    if((ToolClass.getExtraComType()==1)&&(listinfos4.getCabType()==4))
				{
					childextrahand=extracomserial.obtainHandler();
					Message child3=childextrahand.obtainMessage();
					ToolClass.Log(ToolClass.INFO,"EV_COM","COMService ��ɽ��ȫ������","com.txt");					
	    			child3.what=COMThread.VBOX_HUODAO_SET_INDALLCHILD;
					JSONObject ev3=null;
		    		try {
		    			ev3=new JSONObject();
		    			ev3.put("cabinet", bundle.getInt("cabinet"));
		    			ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+ev3.toString(),"com.txt");
		    		} catch (JSONException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		}
		    		child3.obj=ev3;
		    		childextrahand.sendMessage(child3);	
				}
				break;
			/*
			 * �豸����ģ��
			 * EVprotocol.EV_BENTO_LIGHT=13
			 * EVprotocol.EV_BENTO_COOL=14
			 * EVprotocol.EV_BENTO_HOT=15
			 * */	
			//��ݹ�����	
			case EV_LIGHTCHILD:
				ToolClass.Log(ToolClass.INFO,"EV_COM","COMService ����","com.txt");
				childhand=comserial.obtainHandler();
				Message child4=childhand.obtainMessage();
				child4.what=EVprotocol.EV_BENTO_LIGHT;
        		JSONObject ev4=null;
	    		try {
	    			ev4=new JSONObject();
	    			ev4.put("cabinet", bundle.getInt("cabinet"));	
	    			ev4.put("opt", bundle.getInt("opt"));
	    			ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+ev4.toString(),"com.txt");
	    		} catch (JSONException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	    		child4.obj=ev4;
        		childhand.sendMessage(child4);	
				break;
			//��ݹ�����	
			case EV_COOLCHILD:
				ToolClass.Log(ToolClass.INFO,"EV_COM","COMService ����","com.txt");
				childhand=comserial.obtainHandler();
				Message child5=childhand.obtainMessage();
				child5.what=EVprotocol.EV_BENTO_COOL;
        		JSONObject ev5=null;
	    		try {
	    			ev5=new JSONObject();
	    			ev5.put("cabinet", bundle.getInt("cabinet"));	
	    			ev5.put("opt", bundle.getInt("opt"));
	    			ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+ev5.toString(),"com.txt");
	    		} catch (JSONException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	    		child5.obj=ev5;
        		childhand.sendMessage(child5);	
				break;
			//��ݹ����	
			case EV_HOTCHILD:
				ToolClass.Log(ToolClass.INFO,"EV_COM","COMService ����","com.txt");
				childhand=comserial.obtainHandler();
				Message child6=childhand.obtainMessage();
				child6.what=EVprotocol.EV_BENTO_HOT;
        		JSONObject ev6=null;
	    		try {
	    			ev6=new JSONObject();
	    			ev6.put("cabinet", bundle.getInt("cabinet"));	
	    			ev6.put("opt", bundle.getInt("opt"));
	    			ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+ev6.toString(),"com.txt");
	    		} catch (JSONException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	    		child6.obj=ev6;
        		childhand.sendMessage(child6);	
				break;
			//*************************************	
			//�ֽ��豸ģ�飬ֵ��ʹ��EVprotocol���У���Χ21-31
			//*************************************	
			//�ֽ��豸ʹ�ܽ���	
			case EVprotocol.EV_MDB_ENABLE:
				ToolClass.Log(ToolClass.INFO,"EV_COM","COMService ʹ�ܽ���","com.txt");
				if(ToolClass.getExtraComType()==1)
				{
					childextrahand=extracomserial.obtainHandler();
					Message child8=childextrahand.obtainMessage();
					child8.what=EVprotocol.EV_MDB_ENABLE;
	        		JSONObject ev8=null;
		    		try {
		    			ev8=new JSONObject();
		    			ev8.put("bill", bundle.getInt("bill"));	
		    			ev8.put("coin", bundle.getInt("coin"));
		    			ev8.put("opt", bundle.getInt("opt"));
		    			ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceExtraSend0.1="+ev8.toString(),"com.txt");
		    		} catch (JSONException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		}
		    		child8.obj=ev8;
		    		childextrahand.sendMessage(child8);	
				}
				else
				{
					childhand=comserial.obtainHandler();
					Message child8=childhand.obtainMessage();
					child8.what=EVprotocol.EV_MDB_ENABLE;
	        		JSONObject ev8=null;
		    		try {
		    			ev8=new JSONObject();
		    			ev8.put("bill", bundle.getInt("bill"));	
		    			ev8.put("coin", bundle.getInt("coin"));
		    			ev8.put("opt", bundle.getInt("opt"));
		    			ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+ev8.toString(),"com.txt");
		    		} catch (JSONException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		}
		    		child8.obj=ev8;
	        		childhand.sendMessage(child8);	
				}
				break;
				//ֽ������ѯ�ӿ�
			case EVprotocol.EV_MDB_B_INFO:
				ToolClass.Log(ToolClass.INFO,"EV_COM","COMService ֽ������ѯ�ӿ�","com.txt");
				if(ToolClass.getExtraComType()==1)
				{
					childextrahand=extracomserial.obtainHandler();
					Message child9=childextrahand.obtainMessage();
					child9.what=EVprotocol.EV_MDB_B_INFO;
	        		JSONObject ev9=null;
		    		ev9=new JSONObject();	    			
					ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+ev9.toString(),"com.txt");
		    		child9.obj=ev9;
		    		childextrahand.sendMessage(child9);	
				}
				else
				{
					childhand=comserial.obtainHandler();
					Message child9=childhand.obtainMessage();
					child9.what=EVprotocol.EV_MDB_B_INFO;
	        		JSONObject ev9=null;
		    		ev9=new JSONObject();	    			
					ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+ev9.toString(),"com.txt");
		    		child9.obj=ev9;
	        		childhand.sendMessage(child9);
				}
				break;
			//ֽ������	
			case EVprotocol.EV_MDB_B_CON:
				childhand=comserial.obtainHandler();
				Message child12=childhand.obtainMessage();
				child12.what=EVprotocol.EV_MDB_B_CON;
				
				JSONObject jsonObject12 = new JSONObject(); 
				JSONObject EV_json12 = new JSONObject(); 
				JSONArray ch_r12=new JSONArray();
				JSONArray ch_d12=new JSONArray();
				try {
					jsonObject12.put("EV_type", EVprotocol.EV_MDB_B_CON);
					jsonObject12.put("port_id", ToolClass.getCom_id());
					jsonObject12.put("acceptor", bundle.getInt("billtype"));
					jsonObject12.put("dispenser", bundle.getInt("billtype"));
					for(int i=1;i<=16;i++)
					{
						JSONObject ch12 = new JSONObject(); 
						ch12.put("ch", i);
						ch12.put("value", 0);
						ch_r12.put(ch12);
					}
					jsonObject12.put("ch_r", ch_r12);
					for(int i=1;i<=16;i++)
					{
						JSONObject ch12 = new JSONObject(); 
						ch12.put("ch", i);
						ch12.put("value", 0);
						ch_d12.put(ch12);
					}
					jsonObject12.put("ch_d", ch_d12);
					EV_json12.put("EV_json", jsonObject12);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+EV_json12.toString(),"com.txt");
				        		
	    		child12.obj=EV_json12;
        		childhand.sendMessage(child12);	
				break;
				//Ӳ������ѯ�ӿ�
			case EVprotocol.EV_MDB_C_INFO:
				ToolClass.Log(ToolClass.INFO,"EV_COM","COMService Ӳ������ѯ�ӿ�","com.txt");
				if(ToolClass.getExtraComType()==1)
				{
					childextrahand=extracomserial.obtainHandler();
					Message child10=childextrahand.obtainMessage();
					child10.what=EVprotocol.EV_MDB_C_INFO;
	        		JSONObject ev10=null;
		    		ev10=new JSONObject();	    			
					ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+ev10.toString(),"com.txt");
		    		child10.obj=ev10;
		    		childextrahand.sendMessage(child10);	
				}
				else
				{
					childhand=comserial.obtainHandler();
					Message child10=childhand.obtainMessage();
					child10.what=EVprotocol.EV_MDB_C_INFO;
	        		JSONObject ev10=null;
		    		ev10=new JSONObject();	    			
					ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+ev10.toString(),"com.txt");
		    		child10.obj=ev10;
	        		childhand.sendMessage(child10);	
				}
				break;	
			case EVprotocol.EV_MDB_C_CON:
				childhand=comserial.obtainHandler();
				Message child13=childhand.obtainMessage();
				child13.what=EVprotocol.EV_MDB_C_CON;
				
				JSONObject jsonObject13 = new JSONObject(); 
				JSONObject EV_json13 = new JSONObject(); 
				JSONArray ch_r13=new JSONArray();
				JSONArray ch_d13=new JSONArray();
				try {
					jsonObject13.put("EV_type", EVprotocol.EV_MDB_C_CON);
					jsonObject13.put("port_id", ToolClass.getCom_id());
					jsonObject13.put("acceptor", bundle.getInt("acceptor"));
					jsonObject13.put("dispenser", bundle.getInt("dispenser"));
					jsonObject13.put("hight_en", 0);
					//�������
					SerializableMap serializableMap = (SerializableMap) bundle.get("ch_r");
					Map<String, Integer> Set=serializableMap.getMap();
			        Set<Map.Entry<String,Integer>> allset=Set.entrySet();  //ʵ����
			        Iterator<Map.Entry<String,Integer>> iter=allset.iterator();
			        while(iter.hasNext())
			        {
			            Map.Entry<String,Integer> me=iter.next();
			            //System.out.println(me.getKey()+"--"+me.getValue());
			            JSONObject ch = new JSONObject(); 
						ch.put("ch", me.getKey());
						ch.put("value",me.getValue());
						ch_r13.put(ch);
			        } 	        
					jsonObject13.put("ch_r", ch_r13);
					
					//�������
					SerializableMap serializableMap2 = (SerializableMap) bundle.get("ch_d");
					Map<String, Integer> Set2=serializableMap2.getMap();
			        Set<Map.Entry<String,Integer>> allset2=Set2.entrySet();  //ʵ����
			        Iterator<Map.Entry<String,Integer>> iter2=allset2.iterator();
			        while(iter2.hasNext())
			        {
			            Map.Entry<String,Integer> me2=iter2.next();
			            //System.out.println(me.getKey()+"--"+me.getValue());
			            JSONObject ch = new JSONObject(); 
						ch.put("ch", me2.getKey());
						ch.put("value",me2.getValue());
						ch_d13.put(ch);
			        } 	   
					jsonObject13.put("ch_d", ch_d13);
					EV_json13.put("EV_json", jsonObject13);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+EV_json13.toString(),"com.txt");
				        		
	    		child13.obj=EV_json13;
        		childhand.sendMessage(child13);
				break;
			case EVprotocol.EV_MDB_PAYOUT:
				ToolClass.Log(ToolClass.INFO,"EV_COM","COMService MDB�豸�ұ�","com.txt");
				if(ToolClass.getExtraComType()==1)
				{
					childextrahand=extracomserial.obtainHandler();
					Message child14=childextrahand.obtainMessage();
					child14.what=EVprotocol.EV_MDB_PAYOUT;
	        		JSONObject ev14=null;
		    		try {
		    			ev14=new JSONObject();
		    			ev14.put("bill", bundle.getInt("bill"));	
		    			ev14.put("coin", bundle.getInt("coin"));
		    			ev14.put("billPay", bundle.getInt("billPay"));
		    			ev14.put("coinPay", bundle.getInt("coinPay"));
		    			ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+ev14.toString(),"com.txt");
		    		} catch (JSONException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		}
		    		child14.obj=ev14;
		    		childextrahand.sendMessage(child14);	
				}
				else
				{
					childhand=comserial.obtainHandler();
					Message child14=childhand.obtainMessage();
					child14.what=EVprotocol.EV_MDB_PAYOUT;
	        		JSONObject ev14=null;
		    		try {
		    			ev14=new JSONObject();
		    			ev14.put("bill", bundle.getInt("bill"));	
		    			ev14.put("coin", bundle.getInt("coin"));
		    			ev14.put("billPay", bundle.getInt("billPay"));
		    			ev14.put("coinPay", bundle.getInt("coinPay"));
		    			ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+ev14.toString(),"com.txt");
		    		} catch (JSONException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		}
		    		child14.obj=ev14;
	        		childhand.sendMessage(child14);	
				}
				break;
			case EVprotocol.EV_MDB_HP_PAYOUT:
				ToolClass.Log(ToolClass.INFO,"EV_COM","COMService hopperӲ�����ұ�","com.txt");
				childhand=comserial.obtainHandler();
				Message child15=childhand.obtainMessage();
				child15.what=EVprotocol.EV_MDB_HP_PAYOUT;
        		JSONObject ev15=null;
	    		try {
	    			ev15=new JSONObject();
	    			ev15.put("no", bundle.getInt("no"));	
	    			ev15.put("nums", bundle.getInt("nums"));
	    			ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+ev15.toString(),"com.txt");
	    		} catch (JSONException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	    		child15.obj=ev15;
        		childhand.sendMessage(child15);
				break;
				//������ѯ�ӿ�
			case EVprotocol.EV_MDB_HEART:
				ToolClass.Log(ToolClass.INFO,"EV_COM","COMService EV_MDB_HEART�ӿ�","com.txt");
				if(ToolClass.getExtraComType()==1)
				{
					childextrahand=extracomserial.obtainHandler();
					Message child11=childextrahand.obtainMessage();
					child11.what=EVprotocol.EV_MDB_HEART;
	        		JSONObject ev11=null;
		    		ev11=new JSONObject();	    			
					ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+ev11.toString(),"com.txt");
		    		child11.obj=ev11;
		    		childextrahand.sendMessage(child11);	
				}
				else
				{
					childhand=comserial.obtainHandler();
					Message child11=childhand.obtainMessage();
					child11.what=EVprotocol.EV_MDB_HEART;
	        		JSONObject ev11=null;
		    		ev11=new JSONObject();	    			
					ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+ev11.toString(),"com.txt");
		    		child11.obj=ev11;
	        		childhand.sendMessage(child11);
				}
				break;
			case EVprotocol.EV_MDB_COST:
				ToolClass.Log(ToolClass.INFO,"EV_COM","COMService �ۿ�ӿ�","com.txt");
				if(ToolClass.getExtraComType()==1)
				{
					childextrahand=extracomserial.obtainHandler();
					Message child16=childextrahand.obtainMessage();
					child16.what=EVprotocol.EV_MDB_COST;
	        		JSONObject ev16=null;
		    		try {
		    			ev16=new JSONObject();	
		    			ev16.put("cost", bundle.getInt("cost"));
		    			ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+ev16.toString(),"com.txt");
		    		} catch (JSONException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		}
		    		child16.obj=ev16;
		    		childextrahand.sendMessage(child16);	
				}
				else
				{
					childhand=comserial.obtainHandler();
					Message child16=childhand.obtainMessage();
					child16.what=EVprotocol.EV_MDB_COST;
	        		JSONObject ev16=null;
		    		try {
		    			ev16=new JSONObject();	
		    			ev16.put("cost", bundle.getInt("cost"));
		    			ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+ev16.toString(),"com.txt");
		    		} catch (JSONException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		}
		    		child16.obj=ev16;
	        		childhand.sendMessage(child16);
				}
				break;
			case EVprotocol.EV_MDB_PAYBACK://�˱Ұ�ť�ӿ�
				ToolClass.Log(ToolClass.INFO,"EV_COM","COMService �˱Ұ�ť�ӿ�","com.txt");
				if(ToolClass.getExtraComType()==1)
				{
					childextrahand=extracomserial.obtainHandler();
					Message child17=childextrahand.obtainMessage();
					child17.what=EVprotocol.EV_MDB_PAYBACK;
	        		JSONObject ev17=null;
		    		try {
		    			ev17=new JSONObject();	
		    			ev17.put("bill", bundle.getInt("bill"));
		    			ev17.put("coin", bundle.getInt("coin"));
		    			ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+ev17.toString(),"com.txt");
		    		} catch (JSONException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		}
		    		child17.obj=ev17;
		    		childextrahand.sendMessage(child17);	
				}
				else
				{
					childhand=comserial.obtainHandler();
					Message child17=childhand.obtainMessage();
					child17.what=EVprotocol.EV_MDB_PAYBACK;
	        		JSONObject ev17=null;
		    		try {
		    			ev17=new JSONObject();	
		    			ev17.put("bill", bundle.getInt("bill"));
		    			ev17.put("coin", bundle.getInt("coin"));
		    			ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+ev17.toString(),"com.txt");
		    		} catch (JSONException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		}
		    		child17.obj=ev17;
	        		childhand.sendMessage(child17);
				}
				break;	
			}			
		}

	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		ToolClass.Log(ToolClass.INFO,"EV_COM","COMService create","com.txt");
		super.onCreate();
		//9.ע�������
		localBroadreceiver = LocalBroadcastManager.getInstance(this);
		receiver=new ActivityReceiver();
		IntentFilter filter=new IntentFilter();
		filter.addAction("android.intent.action.comsend");
		localBroadreceiver.registerReceiver(receiver,filter);	
	}
	
	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		ToolClass.Log(ToolClass.INFO,"EV_COM","COMService start","com.txt");
		//***********************
		//�߳̽���vmserver����ͨ���ڲ���
		//***********************
		mainhand=new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub				
				switch (msg.what)
				{
					//ȫ����ѯ
					case COMThread.EV_CHECKALLMAIN://���߳̽������߳���Ϣǩ�����
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMService ����ȫ����ѯ����="+msg.obj,"com.txt");
						String tempno6=null; 
						Map<String, Object> Set6= (Map<String, Object>) msg.obj;
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMService ������ѯ���ؽ���...","com.txt");
						//�������
				        Set<Entry<String, Object>> allmap6=Set6.entrySet();  //ʵ����
				        Iterator<Entry<String, Object>> iter6=allmap6.iterator();
				        while(iter6.hasNext())
				        {
				            Entry<String, Object> me=iter6.next();
				            if(
				               (me.getKey().equals("cabinet")!=true)&&(me.getKey().equals("cool")!=true)
				               &&(me.getKey().equals("hot")!=true)&&(me.getKey().equals("light")!=true)
				               &&(me.getKey().equals("EV_TYPE")!=true)
				            )   
				            {
				            	if(Integer.parseInt(me.getKey())<10)
				    				tempno6="0"+me.getKey();
				    			else 
				    				tempno6=me.getKey();
				            	
				            	huoSet.put(cabinetID[huom]+tempno6,(Integer)me.getValue());
				            }
				        } 
				        ToolClass.Log(ToolClass.INFO,"EV_COM","COMService<<"+huoSet.size()+"����״̬:"+huoSet.toString(),"com.txt");	
				        huom++;
				        //2.������ȡ���л�����
				        if(huom<cabinetID.length)
				        {					        	
				        	//���һ�������
			        		vmc_cabinetDAO cabinetDAOrec = new vmc_cabinetDAO(COMService.this);// ����InaccountDAO����
			        		// ��ȡ����������Ϣ�����洢��List���ͼ�����
			        	    Tb_vmc_cabinet listinfosrec = cabinetDAOrec.findScrollData(cabinetID[huom]);
			        	    if((ToolClass.getExtraComType()==1)&&(listinfosrec.getCabType()==4))
			        	    {
			        	    	childextrahand=extracomserial.obtainHandler();
			        	    	Message childrec=childextrahand.obtainMessage();
			        	    	ToolClass.Log(ToolClass.INFO,"EV_COM","COMService ��ɽ���ѯ="+cabinetID[huom],"com.txt");
			        	    	childrec.what=COMThread.EV_BENTO_CHECKALLCHILD;
			        	    	JSONObject evrec=null;
					    		try {
					    			evrec=new JSONObject();
					    			evrec.put("cabinet", cabinetID[huom]);	    			  			
					    			ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+evrec.toString(),"com.txt");
					    		} catch (JSONException e) {
					    			// TODO Auto-generated catch block
					    			e.printStackTrace();
					    		}
					    		childrec.obj=evrec;
			        	    	childextrahand.sendMessage(childrec);	
			        	    }
			        	    else
			        	    {
				        	    childhand=comserial.obtainHandler();
				        		Message childrec=childhand.obtainMessage();
				        		//���ӹ�
				        	    if(listinfosrec.getCabType()==5)
				        		{
				        			ToolClass.Log(ToolClass.INFO,"EV_COM","COMService ���ӹ��ѯ="+cabinetID[huom],"com.txt");
				        			childrec.what=COMThread.EV_BENTO_CHECKALLCHILD;
				        		}
				        	    //���ɻ���
				        	    else if(listinfosrec.getCabType()==1)
				        		{
				        			ToolClass.Log(ToolClass.INFO,"EV_COM","COMService �������ѯ="+cabinetID[huom],"com.txt");
				        			childrec.what=COMThread.EV_COLUMN_CHECKALLCHILD;
				        		}
				        	    //����������
				        		else if((listinfosrec.getCabType()==2)||(listinfosrec.getCabType()==3))
				        		{
				        			ToolClass.Log(ToolClass.INFO,"EV_COM","COMService ���������ѯ="+cabinetID[huom],"com.txt");
				        			childrec.what=COMThread.EV_ELEVATOR_CHECKALLCHILD;
				        		}
				        		JSONObject evrec=null;
					    		try {
					    			evrec=new JSONObject();
					    			evrec.put("cabinet", cabinetID[huom]);	    			  			
					    			ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+evrec.toString(),"com.txt");
					    		} catch (JSONException e) {
					    			// TODO Auto-generated catch block
					    			e.printStackTrace();
					    		}
					    		childrec.obj=evrec;
				        		childhand.sendMessage(childrec);
			        	    }
				        }
				        else
				        {
				        	ToolClass.Log(ToolClass.INFO,"EV_COM","COMService ���ӹ��������","com.txt");
				        	//���ظ�activity�㲥
							Intent recintent=new Intent();
							recintent.putExtra("EVWhat", COMThread.EV_CHECKALLMAIN);						
							//��������
					        SerializableMap myMap=new SerializableMap();
					        myMap.setMap(huoSet);//��map������ӵ���װ��myMap<span></span>��
					        Bundle bundle=new Bundle();
					        bundle.putSerializable("result", myMap);
					        recintent.putExtras(bundle);
							recintent.setAction("android.intent.action.comrec");//action���������ͬ
							localBroadreceiver.sendBroadcast(recintent);
						}												
						break;
					//��ѯ
					case COMThread.EV_CHECKMAIN://���߳̽������߳���Ϣǩ�����
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMService ������ѯ����="+msg.obj,"com.txt");
						//���ظ�activity�㲥
						Intent recintent=new Intent();
						recintent.putExtra("EVWhat", COMThread.EV_CHECKMAIN);						
						//��������
				        SerializableMap myMap=new SerializableMap();
				        myMap.setMap((Map<String, Integer>) msg.obj);//��map������ӵ���װ��myMap<span></span>��
				        Bundle bundle=new Bundle();
				        bundle.putSerializable("result", myMap);
				        recintent.putExtras(bundle);
						recintent.setAction("android.intent.action.comrec");//action���������ͬ
						localBroadreceiver.sendBroadcast(recintent);						
						break;	
					//�������	
					case COMThread.EV_OPTMAIN:
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMService �ۺϲ���="+msg.obj,"com.txt");	
						//���ظ�activity�㲥
						Intent recintent2=new Intent();
						recintent2.putExtra("EVWhat", COMThread.EV_OPTMAIN);						
						//��������
				        SerializableMap myMap2=new SerializableMap();
				        myMap2.setMap((Map<String, Integer>) msg.obj);//��map������ӵ���װ��myMap<span></span>��
				        Bundle bundle2=new Bundle();
				        bundle2.putSerializable("result", myMap2);
				        recintent2.putExtras(bundle2);
						recintent2.setAction("android.intent.action.comrec");//action���������ͬ
						localBroadreceiver.sendBroadcast(recintent2);
						break;
				}				
			}
			
		};
		//�����û��Լ�������࣬������ͨ�����߳�
		comserial=new COMThread(mainhand);
  		thread=new Thread(comserial,"thread");
  		thread.start();	
  		
  		
  	    //***********************
		//�߳̽���vmserver����Э���ڲ���
		//***********************
  		mainextrahand=new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub				
				switch (msg.what)
				{	
					//ȫ����ѯ
					case COMThread.EV_CHECKALLMAIN://���߳̽������߳���Ϣǩ�����
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMService ����ȫ����ѯ����="+msg.obj,"com.txt");
						String tempno6=null; 
						Map<String, Object> Set6= (Map<String, Object>) msg.obj;
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMService ������ѯ���ؽ���...","com.txt");
						//�������
				        Set<Entry<String, Object>> allmap6=Set6.entrySet();  //ʵ����
				        Iterator<Entry<String, Object>> iter6=allmap6.iterator();
				        while(iter6.hasNext())
				        {
				            Entry<String, Object> me=iter6.next();
				            if(
				               (me.getKey().equals("cabinet")!=true)&&(me.getKey().equals("cool")!=true)
				               &&(me.getKey().equals("hot")!=true)&&(me.getKey().equals("light")!=true)
				               &&(me.getKey().equals("EV_TYPE")!=true)
				            )   
				            {
				            	if(Integer.parseInt(me.getKey())<10)
				    				tempno6="0"+me.getKey();
				    			else 
				    				tempno6=me.getKey();
				            	
				            	huoSet.put(cabinetID[huom]+tempno6,(Integer)me.getValue());
				            }
				        } 
				        ToolClass.Log(ToolClass.INFO,"EV_COM","COMService<<"+huoSet.size()+"����״̬:"+huoSet.toString(),"com.txt");	
				        huom++;
				        //2.������ȡ���л�����
				        if(huom<cabinetID.length)
				        {					        	
				        	//���һ�������
			        		vmc_cabinetDAO cabinetDAOrec = new vmc_cabinetDAO(COMService.this);// ����InaccountDAO����
			        		// ��ȡ����������Ϣ�����洢��List���ͼ�����
			        	    Tb_vmc_cabinet listinfosrec = cabinetDAOrec.findScrollData(cabinetID[huom]);
			        	    if((ToolClass.getExtraComType()==1)&&(listinfosrec.getCabType()==4))
			        	    {
			        	    	childextrahand=extracomserial.obtainHandler();
			        	    	Message childrec=childextrahand.obtainMessage();
			        	    	ToolClass.Log(ToolClass.INFO,"EV_COM","COMService ��ɽ���ѯ="+cabinetID[huom],"com.txt");
			        	    	childrec.what=COMThread.EV_BENTO_CHECKALLCHILD;
			        	    	JSONObject evrec=null;
					    		try {
					    			evrec=new JSONObject();
					    			evrec.put("cabinet", cabinetID[huom]);	    			  			
					    			ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+evrec.toString(),"com.txt");
					    		} catch (JSONException e) {
					    			// TODO Auto-generated catch block
					    			e.printStackTrace();
					    		}
					    		childrec.obj=evrec;
			        	    	childextrahand.sendMessage(childrec);	
			        	    }
			        	    else
			        	    {
				        	    childhand=comserial.obtainHandler();
				        		Message childrec=childhand.obtainMessage();
				        		//���ӹ�
				        	    if(listinfosrec.getCabType()==5)
				        		{
				        			ToolClass.Log(ToolClass.INFO,"EV_COM","COMService ���ӹ��ѯ="+cabinetID[huom],"com.txt");
				        			childrec.what=COMThread.EV_BENTO_CHECKALLCHILD;
				        		}
				        	    //���ɻ���
				        	    else if(listinfosrec.getCabType()==1)
				        		{
				        			ToolClass.Log(ToolClass.INFO,"EV_COM","COMService �������ѯ="+cabinetID[huom],"com.txt");
				        			childrec.what=COMThread.EV_COLUMN_CHECKALLCHILD;
				        		}
				        	    //����������
				        		else if((listinfosrec.getCabType()==2)||(listinfosrec.getCabType()==3))
				        		{
				        			ToolClass.Log(ToolClass.INFO,"EV_COM","COMService ���������ѯ="+cabinetID[huom],"com.txt");
				        			childrec.what=COMThread.EV_ELEVATOR_CHECKALLCHILD;
				        		}
				        		JSONObject evrec=null;
					    		try {
					    			evrec=new JSONObject();
					    			evrec.put("cabinet", cabinetID[huom]);	    			  			
					    			ToolClass.Log(ToolClass.INFO,"EV_COM","ServiceSend0.1="+evrec.toString(),"com.txt");
					    		} catch (JSONException e) {
					    			// TODO Auto-generated catch block
					    			e.printStackTrace();
					    		}
					    		childrec.obj=evrec;
				        		childhand.sendMessage(childrec);
			        	    }
				        }
				        else
				        {
				        	ToolClass.Log(ToolClass.INFO,"EV_COM","COMService ���ӹ��������","com.txt");
				        	//���ظ�activity�㲥
							Intent recintent=new Intent();
							recintent.putExtra("EVWhat", COMThread.EV_CHECKALLMAIN);						
							//��������
					        SerializableMap myMap=new SerializableMap();
					        myMap.setMap(huoSet);//��map������ӵ���װ��myMap<span></span>��
					        Bundle bundle=new Bundle();
					        bundle.putSerializable("result", myMap);
					        recintent.putExtras(bundle);
							recintent.setAction("android.intent.action.comrec");//action���������ͬ
							localBroadreceiver.sendBroadcast(recintent);
						}												
						break;
					//��ѯ
					case COMThread.EV_CHECKMAIN://���߳̽������߳���Ϣǩ�����
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMService ������ѯ����="+msg.obj,"com.txt");
						//���ظ�activity�㲥
						Intent recintent=new Intent();
						recintent.putExtra("EVWhat", COMThread.EV_CHECKMAIN);						
						//��������
				        SerializableMap myMap=new SerializableMap();
				        myMap.setMap((Map<String, Integer>) msg.obj);//��map������ӵ���װ��myMap<span></span>��
				        Bundle bundle=new Bundle();
				        bundle.putSerializable("result", myMap);
				        recintent.putExtras(bundle);
						recintent.setAction("android.intent.action.comrec");//action���������ͬ
						localBroadreceiver.sendBroadcast(recintent);						
						break;
					//�������	
					case COMThread.EV_OPTMAIN:
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMExtraService �ۺϲ���="+msg.obj,"com.txt");	
						//���ظ�activity�㲥
						Intent recintent2=new Intent();
						recintent2.putExtra("EVWhat", COMThread.EV_OPTMAIN);						
						//��������
				        SerializableMap myMap2=new SerializableMap();
				        myMap2.setMap((Map<String, Integer>) msg.obj);//��map������ӵ���װ��myMap<span></span>��
				        Bundle bundle2=new Bundle();
				        bundle2.putSerializable("result", myMap2);
				        recintent2.putExtras(bundle2);
						recintent2.setAction("android.intent.action.comrec");//action���������ͬ
						localBroadreceiver.sendBroadcast(recintent2);
						break;
						//��ť����
					case COMThread.EV_BUTTONMAIN:
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMExtraService ��������="+msg.obj,"com.txt");	
						//���ظ�activity�㲥
						Intent recintent3=new Intent();
						recintent3.putExtra("EVWhat", COMThread.EV_BUTTONMAIN);
						//��������
				        SerializableMap myMap3=new SerializableMap();
				        myMap3.setMap((Map<String, Integer>) msg.obj);//��map������ӵ���װ��myMap<span></span>��
				        Bundle bundle3=new Bundle();
				        bundle3.putSerializable("result", myMap3);
				        recintent3.putExtras(bundle3);
						recintent3.setAction("android.intent.action.comrec");//action���������ͬ
						localBroadreceiver.sendBroadcast(recintent3);
						break;	
				}				
			}
			
		};				
  	    //�����û��Լ�������࣬�������贮���߳�
  		extracomserial=new ExtraCOMThread(mainextrahand);
  		extrathread=new Thread(extracomserial,"extrathread");
  		extrathread.start();
  		
  		
  		//*************
  		//�����̼߳�ض�ʱ��
  		//*************
  		timer.scheduleWithFixedDelay(new Runnable() { 
	        @Override 
	        public void run() { 
	        	Boolean bool=false;   
	        	//�����ͨ�����߳�
	        	bool=thread.isAlive();
	        	if(bool==false)
	        	{
	        		thread=new Thread(comserial,"thread");
	          		thread.start();		
	        	}
	        	
	        	//������贮���߳�
	        	bool=extrathread.isAlive();
	        	if(bool==false)
	        	{
	        		extrathread=new Thread(extracomserial,"extrathread");
	          		extrathread.start();	
	        	}
	        } 
	    },5*60,15*60,TimeUnit.SECONDS);       // 10*60timeTask  
	}
	
	

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub		
		ToolClass.Log(ToolClass.INFO,"EV_COM","COMService destroy","com.txt");
		EVprotocol.EVPortRelease(ToolClass.getBentcom());
		EVprotocol.EVPortRelease(ToolClass.getColumncom());
		EVprotocol.EVPortRelease(ToolClass.getCom());
		EVprotocol.EVPortRelease(ToolClass.getExtracom());
		EVprotocol.EVPortRelease(ToolClass.getColumncom2());
		if(ToolClass.getExtraComType()==1)
		{
			childextrahand=extracomserial.obtainHandler();
			Message child2=childextrahand.obtainMessage();
			ToolClass.Log(ToolClass.INFO,"EV_COM","COMService ��ɽ��ر�","com.txt");
			child2.what=VboxProtocol.VBOX_PROTOCOL;
			JSONObject ev2=null;
    		ev2=new JSONObject();	    			
			child2.obj=ev2;
    		childextrahand.sendMessage(child2);
		}
		//���ע�������
		localBroadreceiver.unregisterReceiver(receiver);
		//�ر��Լ�������ʱ��
		timer.shutdown();
		super.onDestroy();
	}
	

}
