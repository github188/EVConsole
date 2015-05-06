package com.easivend.http;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.easivend.alipay.AlipayConfigAPI;
import com.easivend.common.ToolClass;
import com.easivend.weixing.WeiConfigAPI;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class Weixinghttp implements Runnable 
{
	//����
	public final static int SETCHILD=2;//what���,���͸����߳�֧��������
	public final static int SETMAIN=1;//what���,���͸����߳�֧��������ά��	
	public final static int SETFAILPROCHILD=5;//what���,���͸����߳̽���Э��ʧ��
	public final static int SETFAILBUSCHILD=6;//what���,���͸����߳̽�����Ϣʧ��
	//��ѯ
	public final static int SETQUERYCHILD=7;//what���,���͸����߳�֧������ѯ
	public final static int SETQUERYMAIN=8;//what���,���͸����̲߳�ѯ���
	public final static int SETFAILQUERYPROCHILD=9;//what���,���͸����߳̽���Э��ʧ��
	public final static int SETFAILQUERYBUSCHILD=10;//what���,���͸����߳̽�����Ϣʧ��
	//�˿�
	public final static int SETPAYOUTCHILD=11;//what���,���͸����߳�֧�����˿�
	public final static int SETPAYOUTMAIN=12;//what���,���͸����߳��˿���
	public final static int SETFAILPAYOUTPROCHILD=13;//what���,���͸����߳̽���Э��ʧ��
	public final static int SETFAILPAYOUTBUSCHILD=14;//what���,���͸����߳̽�����Ϣʧ��
	//��������
	public final static int SETDELETECHILD=15;//what���,���͸����߳�֧������������
	public final static int SETDELETEMAIN=16;//what���,���͸����߳��˿���
	public final static int SETFAILDELETEPROCHILD=17;//what���,���͸����߳̽���Э��ʧ��
	public final static int SETFAILDELETEBUSCHILD=18;//what���,���͸����߳̽�����Ϣʧ��
	private Handler mainhand=null,childhand=null;
	
	public Weixinghttp(Handler mainhand) {
		this.mainhand=mainhand;		
	}
	public Handler obtainHandler()
	{
		return this.childhand;
	}
	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		Looper.prepare();//�û��Լ�������࣬�����߳���Ҫ�Լ�׼��loop
		childhand=new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what)
				{
					case SETCHILD://���߳̽������߳���Ϣ
						ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIweixing>>]"+msg.obj.toString());
						Map<String, String> sPara = new HashMap<String, String>();
						//1.���Ӷ�����Ϣ
						JSONObject ev=null;
						try {
							ev = new JSONObject(msg.obj.toString());							
							sPara.put("out_trade_no", ev.getString("out_trade_no"));//�������
							long fee=ToolClass.MoneySend(Float.parseFloat(ev.getString("total_fee")));
							String total_fee=String.valueOf(fee);
							sPara.put("total_fee",total_fee);//�����ܽ��		
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						Log.i("EV_JNI","Send0.2="+sPara.toString());
						//2.����֧��������Ϣ
						Map<String, String> map1 = WeiConfigAPI.PostWeiBuy(sPara);
						//3.����֧��������Ϣ
						String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";			            
						String content= WeiConfigAPI.sendPost(url, map1);
						try {
				            //4.������ص���Ϣ
				            InputStream is = new ByteArrayInputStream(content.getBytes());// ��ȡ��������
					           
				           Map<String, String> map2=WeiConfigAPI.PendWeiBuy(is);
					       Log.i("EV_JNI","rec2="+map2.toString());
					       //�����̷߳�����Ϣ
					        Message tomain=mainhand.obtainMessage();
					      //Э��ʧ��
				           if(map2.get("return_code").equals("FAIL"))
				           {
				        	   tomain.what=SETFAILPROCHILD;
							   tomain.obj=map2.get("return_msg");
				           }
				           else
				           {
				        	   //ҵ����ʧ��
				        	   if(map2.get("result_code").equals("FAIL"))
					           {
					        	   tomain.what=SETFAILBUSCHILD;
								   tomain.obj=map2.get("err_code")+map2.get("err_code_des");
					           }
				        	   //ͨ��֧�����ṩ�Ķ���ֱ�����ɶ�ά��
				        	   else if(map2.get("result_code").equals("SUCCESS"))
					           {
					        	   tomain.what=SETMAIN;
								   tomain.obj=map2.get("code_url");
					           }
				           }
				           Log.i("EV_JNI","rec3="+tomain.obj);	
					       mainhand.sendMessage(tomain); // ������Ϣ	
				           
				        } catch (Exception e) {
				            // TODO Auto-generated catch block
				            System.out.println(e);
				        } 
						
					break;	
					case SETQUERYCHILD://���߳̽������߳���Ϣ
						ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIweixing>>]"+msg.obj.toString());
						Map<String, String> sPara2 = new HashMap<String, String>();
						//1.���Ӷ�����Ϣ
						JSONObject ev2=null;
						try {
							ev2 = new JSONObject(msg.obj.toString());							
							sPara2.put("out_trade_no", ev2.getString("out_trade_no"));//�������									
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						Log.i("EV_JNI","Send0.2="+sPara2.toString());
						//2.����������Ϣ
						Map<String, String> map3 = WeiConfigAPI.PostWeiQuery(sPara2);
						//3.���Ͷ�����Ϣ
						String url2 = "https://api.mch.weixin.qq.com/pay/orderquery";			            
						String content2= WeiConfigAPI.sendPost(url2, map3);
						try {
				            //4.������ص���Ϣ
				            InputStream is = new ByteArrayInputStream(content2.getBytes());// ��ȡ��������
					           
				           Map<String, String> map4=WeiConfigAPI.PendWeiQuery(is);
					       Log.i("EV_JNI","rec2="+map4.toString());
					       //�����̷߳�����Ϣ
					        Message tomain=mainhand.obtainMessage();
					      //Э��ʧ��
				           if(map4.get("return_code").equals("FAIL"))
				           {
				        	   tomain.what=SETFAILQUERYPROCHILD;
							   tomain.obj=map4.get("return_msg");
				           }
				           else
				           {
				        	   //ҵ����ʧ��
				        	   if(map4.get("result_code").equals("FAIL"))
					           {
					        	   tomain.what=SETFAILQUERYBUSCHILD;
								   tomain.obj=map4.get("err_code")+map4.get("err_code_des");
					           }
				        	   //ͨ��֧�����ṩ�Ķ���ֱ�����ɶ�ά��
				        	   else if(map4.get("result_code").equals("SUCCESS"))
					           {
					        	   tomain.what=SETQUERYMAIN;
								   tomain.obj=map4.get("trade_state");
					           }
				           }
				           Log.i("EV_JNI","rec3="+tomain.obj);	
					       mainhand.sendMessage(tomain); // ������Ϣ	
				           
				        } catch (Exception e) {
				            // TODO Auto-generated catch block
				            System.out.println(e);
				        }
					break;
					case SETPAYOUTCHILD://���߳̽������߳���Ϣ
						ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIweixing>>]"+msg.obj.toString());
						Map<String, String> sPara3 = new HashMap<String, String>();
						//1.���Ӷ�����Ϣ
						JSONObject ev3=null;
						try {
							ev3 = new JSONObject(msg.obj.toString());							
							sPara3.put("out_trade_no", ev3.getString("out_trade_no"));//�������
							sPara3.put("out_refund_no", ev3.getString("out_refund_no"));//�˿���
							long fee3=ToolClass.MoneySend(Float.parseFloat(ev3.getString("total_fee")));
							String total_fee=String.valueOf(fee3);
							sPara3.put("total_fee",total_fee);//�����ܽ��
							
							fee3=ToolClass.MoneySend(Float.parseFloat(ev3.getString("refund_fee")));
							String refund_fee=String.valueOf(fee3);
							sPara3.put("refund_fee",refund_fee);//�����ܽ��	
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						Log.i("EV_JNI","Send0.2="+sPara3.toString());
						//2.����֧��������Ϣ
						Map<String, String> map5 = WeiConfigAPI.PostWeiPayout(sPara3);
						//3.����֧��������Ϣ
						String url3 = "https://api.mch.weixin.qq.com/secapi/pay/refund";			            
						String content3= WeiConfigAPI.sendPost(url3, map5);
						try {
				            //4.������ص���Ϣ
				            InputStream is = new ByteArrayInputStream(content3.getBytes());// ��ȡ��������
					           
				           Map<String, String> map2=WeiConfigAPI.PendWeiPayout(is);
					       Log.i("EV_JNI","rec2="+map2.toString());
					       //�����̷߳�����Ϣ
					        Message tomain=mainhand.obtainMessage();
					      //Э��ʧ��
				           if(map2.get("return_code").equals("FAIL"))
				           {
				        	   tomain.what=SETFAILPAYOUTPROCHILD;
							   tomain.obj=map2.get("return_msg");
				           }
				           else
				           {
				        	   //ҵ����ʧ��
				        	   if(map2.get("result_code").equals("FAIL"))
					           {
					        	   tomain.what=SETFAILPAYOUTBUSCHILD;
								   tomain.obj=map2.get("err_code")+map2.get("err_code_des");
					           }
				        	   //ͨ��֧�����ṩ�Ķ���ֱ�����ɶ�ά��
				        	   else if(map2.get("result_code").equals("SUCCESS"))
					           {
					        	   tomain.what=SETPAYOUTMAIN;
								   tomain.obj=map2.get("code_url");
					           }
				           }
				           Log.i("EV_JNI","rec3="+tomain.obj);	
					       mainhand.sendMessage(tomain); // ������Ϣ	
				           
				        } catch (Exception e) {
				            // TODO Auto-generated catch block
				            System.out.println(e);
				        } 
						
					break;
				}
			}
			
		};
		Looper.loop();//�û��Լ�������࣬�����߳���Ҫ�Լ�׼��loop
	}

}