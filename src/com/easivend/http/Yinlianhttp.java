package com.easivend.http;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

import cn.yoc.unionPay.UnionPay;
import cn.yoc.unionPay.po.UnionOrder;
import cn.yoc.unionPay.sdk.DateUtils;

import com.easivend.common.ToolClass;
import com.easivend.weixing.WeiConfigAPI;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class Yinlianhttp implements Runnable 
{
	//����
	public final static int SETCHILD=2;//what���,���͸����߳�֧��������
	public final static int SETMAIN=1;//what���,���͸����߳�֧��������ά��	
	public final static int SETFAILPROCHILD=5;//what���,���͸����߳̽���Э��ʧ��
	public final static int SETFAILBUSCHILD=6;//what���,���͸����߳̽�����Ϣʧ��
	public final static int SETFAILNETCHILD=4;//what���,���͸����߳̽�������
	//��ѯ
	public final static int SETQUERYCHILD=7;//what���,���͸����߳�֧������ѯ
	public final static int SETQUERYMAIN=8;//what���,���͸����̲߳�ѯ���
	public final static int SETQUERYMAINSUCC=9;//what���,���͸����̲߳�ѯ�������ɹ�
	public final static int SETFAILQUERYPROCHILD=10;//what���,���͸����߳̽���Э��ʧ��
	public final static int SETFAILQUERYBUSCHILD=11;//what���,���͸����߳̽�����Ϣʧ��
	//�˿�
	public final static int SETPAYOUTCHILD=12;//what���,���͸����߳�֧�����˿�
	public final static int SETPAYOUTMAIN=13;//what���,���͸����߳��˿���
	public final static int SETFAILPAYOUTPROCHILD=14;//what���,���͸����߳̽���Э��ʧ��
	public final static int SETFAILPAYOUTBUSCHILD=15;//what���,���͸����߳̽�����Ϣʧ��
	//��������
	public final static int SETDELETECHILD=16;//what���,���͸����߳�֧������������
	public final static int SETDELETEMAIN=17;//what���,���͸����߳��˿���
	public final static int SETFAILDELETEPROCHILD=18;//what���,���͸����߳̽���Э��ʧ��
	public final static int SETFAILDELETEBUSCHILD=19;//what���,���͸����߳̽�����Ϣʧ��
	private Handler mainhand=null,childhand=null;
	private UnionOrder order;
	
	public Yinlianhttp(Handler mainhand) {
		this.mainhand=mainhand;	
		order = new UnionOrder();
	}
	public Handler obtainHandler()
	{
		return this.childhand;
	}
	
	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<yinlianhttp="+Thread.currentThread().getId(),"log.txt");
		Looper.prepare();//�û��Լ�������࣬�����߳���Ҫ�Լ�׼��loop
		childhand=new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what)
				{
					case SETCHILD://���߳̽������߳���Ϣ
						ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIyinlian>>��ά��]["+Thread.currentThread().getId()+"]"+msg.obj.toString(),"log.txt");
						
						//1.��Ӷ�����Ϣ
						JSONObject ev=null;
						try {
							ev = new JSONObject(msg.obj.toString());
							order.setOrderId(ev.getString("out_trade_no"));//�������
							long fee=ToolClass.MoneySend(Float.parseFloat(ev.getString("total_fee")));
							String total_fee=String.valueOf(fee);
							order.setTxnAmt(total_fee);//�����ܽ��	
							order.setTxnTime(DateUtils.currentDateTime("yyyyMMddHHmmss"));//�µ�ʱ��
							order.setTermId("00000001");//�ն˺� �����豸��      
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						ToolClass.Log(ToolClass.INFO,"EV_JNI","Send0.2="+order.toString(),"log.txt");
						try {
							//2.����֧��������Ϣ������
							Map<String, String> map2 = UnionPay.getInstance().precreate(order);						
							
							ToolClass.Log(ToolClass.INFO,"EV_JNI","rec2="+map2.toString(),"log.txt");
					       //�����̷߳�����Ϣ
					        Message tomain=mainhand.obtainMessage();
					        if(map2.get("respCode").equals("00"))
				            {
				        	   tomain.what=SETMAIN;
				        	   JSONObject zhuhe=new JSONObject();
				        	   zhuhe.put("code_url", map2.get("qrCode"));
				        	   zhuhe.put("out_trade_no", ev.getString("out_trade_no"));
							   tomain.obj=zhuhe;
				            }
				           ToolClass.Log(ToolClass.INFO,"EV_JNI","rec3="+tomain.obj,"log.txt");	
					       mainhand.sendMessage(tomain); // ������Ϣ	
				           
				        } catch (Exception e) {
				            // TODO Auto-generated catch block
				        	//�����̷߳�����Ϣ
				           Message tomain=mainhand.obtainMessage();	
				    	   tomain.what=SETFAILNETCHILD;
				    	   tomain.obj="netfail";
				    	   ToolClass.Log(ToolClass.INFO,"EV_JNI","rec="+tomain.obj,"log.txt");				           
						   mainhand.sendMessage(tomain); // ������Ϣ
				        } 
						
					break;	
					case SETQUERYCHILD://���߳̽������߳���Ϣ
						ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIyinlian>>��ѯ]["+Thread.currentThread().getId()+"]"+msg.obj.toString(),"log.txt");
						try {
							//3.���Ͷ�����Ϣ
							Map<String, String> map4 = UnionPay.getInstance().query(order);
					        ToolClass.Log(ToolClass.INFO,"EV_JNI","rec2="+map4.toString(),"log.txt");
					        //�����̷߳�����Ϣ
					        Message tomain=mainhand.obtainMessage();
					        if(map4.get("respCode").equals("00"))
				            {
			        		   //֧���ɹ�
			        		   if(map4.get("origRespCode").equals("00"))
			        		   {
					        	   tomain.what=SETQUERYMAINSUCC;
								   tomain.obj=map4.get("origRespMsg");
			        		   }
			        		   //���ڵȴ�֧�������������
			        		   else 
			        		   {
			        			   tomain.what=SETQUERYMAIN;
								   tomain.obj=map4.get("origRespMsg");
			        		   }
				            }
				            ToolClass.Log(ToolClass.INFO,"EV_JNI","rec3="+tomain.obj,"log.txt");
					        mainhand.sendMessage(tomain); // ������Ϣ	
				           
				        } catch (Exception e) {
				            // TODO Auto-generated catch block
				            System.out.println(e);
				          //�����̷߳�����Ϣ
				           Message tomain=mainhand.obtainMessage();	
				    	   tomain.what=SETFAILNETCHILD;
				    	   tomain.obj="netfail";
				    	   ToolClass.Log(ToolClass.INFO,"EV_JNI","rec="+tomain.obj,"log.txt");			           
						   mainhand.sendMessage(tomain); // ������Ϣ
				        }
					break;
					case SETPAYOUTCHILD://���߳̽������߳���Ϣ
						ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIyinlian>>�˿�]["+Thread.currentThread().getId()+"]"+msg.obj.toString(),"log.txt");
						Map<String, String> sPara3 = new HashMap<String, String>();
						//1.��Ӷ�����Ϣ
						JSONObject ev3=null;
						try {
							ev3 = new JSONObject(msg.obj.toString());							
							long fee3=ToolClass.MoneySend(Float.parseFloat(ev3.getString("refund_fee")));
							String refund_fee=String.valueOf(fee3);
							order.setRefundAmt(refund_fee);//�����ܽ��	
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						ToolClass.Log(ToolClass.INFO,"EV_JNI","Send0.2="+order.toString(),"log.txt");
						try {
							//2.����֧��������Ϣ������
							Map<String, String> map6 = UnionPay.getInstance().refund(order);
                            
					       ToolClass.Log(ToolClass.INFO,"EV_JNI","rec2="+map6.toString(),"log.txt");
					       //�����̷߳�����Ϣ
					       Message tomain=mainhand.obtainMessage();
					       if(map6.get("respCode").equals("00"))
				           {
				        	   tomain.what=SETPAYOUTMAIN;
							   tomain.obj=map6.get("respMsg");
				           }
				           ToolClass.Log(ToolClass.INFO,"EV_JNI","rec3="+tomain.obj,"log.txt");	
					       mainhand.sendMessage(tomain); // ������Ϣ	
				           
				        } catch (Exception e) {
				            // TODO Auto-generated catch block
				            System.out.println(e);
				          //�����̷߳�����Ϣ
				           Message tomain=mainhand.obtainMessage();	
				    	   tomain.what=SETFAILNETCHILD;
				    	   tomain.obj="netfail";
				    	   ToolClass.Log(ToolClass.INFO,"EV_JNI","rec="+tomain.obj,"log.txt");				           
						   mainhand.sendMessage(tomain); // ������Ϣ
				        } 
						
					break;					
				default:
						//�����̷߳�����Ϣ
			           Message tomain=mainhand.obtainMessage();	
			    	   tomain.what=SETFAILNETCHILD;
			    	   tomain.obj="netfail";
			    	   ToolClass.Log(ToolClass.INFO,"EV_JNI","rec="+tomain.obj,"log.txt");				           
					   mainhand.sendMessage(tomain); // ������Ϣ
					break;
				}
			}
			
		};
		Looper.loop();//�û��Լ�������࣬�����߳���Ҫ�Լ�׼��loop
	}
		
}
