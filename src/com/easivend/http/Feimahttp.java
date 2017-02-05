package com.easivend.http;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.easivend.alipay.AlipayConfig;
import com.easivend.alipay.AlipayConfigAPI;
import com.easivend.common.ToolClass;
import com.easivend.dao.vmc_system_parameterDAO;
import com.easivend.model.Tb_vmc_system_parameter;
import com.easivend.alipay.HttpRequester;
import com.easivend.alipay.HttpRespons;
import com.example.alizhifu.AlipayAPI;

public class Feimahttp implements Runnable
{
	//����
	public final static int SETCHILD=2;//what���,���͸����߳�֧��������
	public final static int SETMAIN=1;//what���,���͸����߳�֧��������ά��	
	public final static int SETFAILPROCHILD=5;//what���,���͸����߳̽���Э��ʧ��
	public final static int SETFAILBUSCHILD=6;//what���,���͸����߳̽�����Ϣʧ��
	public final static int SETFAILNETCHILD=4;//what���,���͸����߳̽�������
	//��ѯ
	public final static int SETQUERYCHILD=7;//what���,���͸����߳�֧������ѯ
	public final static int SETQUERYMAIN=8;//what���,���͸����̲߳�ѯ������ڸ���
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
	
	//private final int SETWEIMAIN=3;//what���,���߳̽��յ����߳�΢�Ž���ά��
	//private final int SETWEICHILD=4;//what���,���͸����߳�΢�Ž���
	private Handler mainhand=null,childhand=null;
	private int feimatype=2;//1ʹ��1.0�汾֧������2ʹ��2.0�汾֧�����ڱ�
	
	public Feimahttp(Handler mainhand) {
		this.mainhand=mainhand;		
	}
	public Handler obtainHandler()
	{
		return this.childhand;
	}
	@Override
	public void run() {
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<feimathread="+Thread.currentThread().getId(),"log.txt");
		// TODO Auto-generated method stub
		Looper.prepare();//�û��Լ�������࣬�����߳���Ҫ�Լ�׼��loop
		childhand=new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what)
				{
				case SETCHILD://���߳̽������߳���Ϣ
						ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIfeima>>��ά��]["+Thread.currentThread().getId()+"]"+msg.obj.toString(),"log.txt");
						Map<String, String> sPara = new HashMap<String, String>();
						//1.��Ӷ�����Ϣ
						JSONObject ev=null;
						try {
							ev = new JSONObject(msg.obj.toString());							
							sPara.put("out_trade_no", ev.getString("out_trade_no"));//�������
							sPara.put("total_fee",ev.getString("total_fee"));//�����ܽ��		
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						sPara.put("subject","֧������ά��");//��������
						//��Ʒ��ϸ
						String json=null;
					    try {
					    	JSONObject temp=new JSONObject();
							temp.put("goodsName","��Ʒˮ");
							temp.put("quantity","1");
						    temp.put("price",ev.getString("total_fee"));
						    JSONArray singArray=new JSONArray();//�����������
						    singArray.put(temp);
						    json=singArray.toString();	
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					    sPara.put("goods_detail",json);//��Ʒ��ϸ
					    sPara.put("it_b_pay","10m");//���׹ر�ʱ��
					    ToolClass.Log(ToolClass.INFO,"EV_JNI","Send0.2="+sPara.toString(),"log.txt");
					    // ��ȡ֧��������
					    vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(ToolClass.getContext());// ����InaccountDAO����
					    // ��ȡ����������Ϣ�����洢��List���ͼ�����
				    	Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
				    	feimatype=tb_inaccount.getZhifubaoer();
				    	//ʹ��1.0�汾��֧����
				    	if(feimatype==1)
				    	{
						    //2.����֧��������Ϣ
							Map<String, String> map1 = AlipayConfigAPI.PostAliBuy(sPara);
					        try {          	       	                    	       	
					           HttpRequester request = new HttpRequester();              
					           String url = "https://mapi.alipay.com/gateway.do?" + "_input_charset=" + AlipayConfig.getInput_charset();           
					           //3.����֧��������Ϣ
					           HttpRespons hr = request.sendPost(url, map1);
					           //4.�õ������ַ���
					           String strpicString=hr.getContent();	
					           ToolClass.Log(ToolClass.INFO,"EV_JNI","rec1="+strpicString,"log.txt");
					           //5.������ص���Ϣ
					           InputStream is = new ByteArrayInputStream(strpicString.getBytes());// ��ȡ��������
					           Map<String, String> map2=AlipayConfigAPI.PendAliBuy(is);
					           ToolClass.Log(ToolClass.INFO,"EV_JNI","rec2="+map2.toString(),"log.txt");
					           //�����̷߳�����Ϣ
					           Message tomain=mainhand.obtainMessage();	
					           //Э��ʧ��
					           if(map2.get("is_success").equals("F"))
					           {
					        	   tomain.what=SETFAILPROCHILD;
								   tomain.obj=map2.get("error");
					           }
					           else
					           {
					        	   //ҵ����ʧ��
					        	   if(map2.get("result_code").equals("FAIL"))
						           {
						        	   tomain.what=SETFAILBUSCHILD;
									   tomain.obj=map2.get("detail_error_code")+map2.get("detail_error_des");
						           }
					        	   //ͨ��֧�����ṩ�Ķ���ֱ�����ɶ�ά��
					        	   else if(map2.get("result_code").equals("SUCCESS"))
						           {
						        	   tomain.what=SETMAIN;
									   tomain.obj=map2.get("qr_code");
						           }
					           }
					           ToolClass.Log(ToolClass.INFO,"EV_JNI","rec3="+tomain.obj,"log.txt");				           
							   mainhand.sendMessage(tomain); // ������Ϣ
							   
							   
	//				           //����ͼƬ
	//				           result=strpicString.substring(strpicString.indexOf("<pic_url>")+9, strpicString.indexOf("</pic_url>"));
	//						   //txt.setText(strpicString); // ������ݱ༭��	
	//						   ToolClass.Log(ToolClass.INFO,"EV_JNI","rec1="+result);		  		   
	//						   HttpClient httpClient=new DefaultHttpClient();
	//							HttpGet httprequest=new HttpGet(result);
	//							HttpResponse httpResponse;
	//							try {
	//								httpResponse=httpClient.execute(httprequest);
	//								if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){	//�������ɹ�
	//									//result = EntityUtils.toString(httpResponse.getEntity());	//��ȡ���ص��ַ���
	//									//ȡ�������Ϣ ȡ��HttpEntiy  
	//					                HttpEntity httpEntity = httpResponse.getEntity();  
	//					                //���һ��������  
	//					                InputStream is = httpEntity.getContent();  
	//					                bitmap = BitmapFactory.decodeStream(is);  
	//					                is.close();  
	//					                Message m = handler.obtainMessage(); // ��ȡһ��Message
	//					                m.what=1;
	//									handler.sendMessage(m); // ������Ϣ
	//					                 
	//								}else{
	//									result = "����ʧ�ܣ�";
	//								}
	//							} catch (ClientProtocolException e) {
	//								// TODO Auto-generated catch block
	//								e.printStackTrace();
	//							} catch (IOException e) {
	//								// TODO Auto-generated catch block
	//								e.printStackTrace();
	//							}
					       } catch (Exception e) {  
					           //e.printStackTrace();  
					    	   //�����̷߳�����Ϣ
					           Message tomain=mainhand.obtainMessage();	
					    	   tomain.what=SETFAILNETCHILD;
					    	   tomain.obj="netfail";
					    	   ToolClass.Log(ToolClass.INFO,"EV_JNI","rec="+tomain.obj,"log.txt");				           
							   mainhand.sendMessage(tomain); // ������Ϣ
					       }
				       }	
				    	//ʹ��2.0�汾��֧�����ڱ�
				       else if(feimatype==2)	
				       {				    	   
				    	   try {
				    		    String rsp=AlipayAPI.PostAliBuy(sPara);
					   			JSONObject response=new JSONObject(rsp);
					   			JSONObject obj=new JSONObject(response.get("alipay_trade_precreate_response").toString());
					   			ToolClass.Log(ToolClass.INFO,"EV_JNI","rec2="+obj.toString(),"log.txt");
					   			//�����̷߳�����Ϣ
					   			Message tomain=mainhand.obtainMessage();	
					   			String msg2=obj.getString("msg");
					   			if(msg2.toLowerCase().equals("success"))
					   			{
					   				String qr_code=obj.getString("qr_code");
					   				tomain.what=SETMAIN;
					   				tomain.obj=qr_code;
					   			}
					   			else
					   			{
					   				tomain.what=SETFAILBUSCHILD;
					   			    tomain.obj=msg2;					   				
					   			}
					   			ToolClass.Log(ToolClass.INFO,"EV_JNI","rec3="+tomain.obj,"log.txt");				           
					   			mainhand.sendMessage(tomain); // ������Ϣ
					   		} catch (Exception e) {
					   			// TODO Auto-generated catch block
					   			e.printStackTrace();
					   		   //�����̷߳�����Ϣ
					           Message tomain=mainhand.obtainMessage();	
					    	   tomain.what=SETFAILNETCHILD;
					    	   tomain.obj="netfail";
					    	   ToolClass.Log(ToolClass.INFO,"EV_JNI","rec="+tomain.obj,"log.txt");				           
							   mainhand.sendMessage(tomain); // ������Ϣ
					   		}
				       }
						
					break;
				case SETQUERYCHILD://���߳̽������߳���Ϣ
					ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIfeima>>��ѯ]["+Thread.currentThread().getId()+"]"+msg.obj.toString(),"log.txt");
					Map<String, String> sPara2 = new HashMap<String, String>();
					//1.��Ӷ�����Ϣ
					JSONObject ev2=null;
					try {
						ev2 = new JSONObject(msg.obj.toString());							
						sPara2.put("out_trade_no", ev2.getString("out_trade_no"));//�������
								
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}					
				    ToolClass.Log(ToolClass.INFO,"EV_JNI","Send0.2="+sPara2.toString(),"log.txt");
				    //ʹ��1.0�汾��֧����
			    	if(feimatype==1)
			    	{
					    //2.����֧��������Ϣ
						Map<String, String> map3 = AlipayConfigAPI.PostAliQuery(sPara2);
				        try {          	       	                    	       	
				           HttpRequester request = new HttpRequester();              
				           String url = "https://mapi.alipay.com/gateway.do?" + "_input_charset=" + AlipayConfig.getInput_charset();           
				           //3.����֧��������Ϣ
				           HttpRespons hr = request.sendPost(url, map3);
				           //4.�õ������ַ���
				           String strpicString=hr.getContent();	
				           ToolClass.Log(ToolClass.INFO,"EV_JNI","rec1="+strpicString,"log.txt");
				           //5.������ص���Ϣ
				           InputStream is = new ByteArrayInputStream(strpicString.getBytes());// ��ȡ��������
				           Map<String, String> map4=AlipayConfigAPI.PendAliQuery(is);
				           ToolClass.Log(ToolClass.INFO,"EV_JNI","rec2="+map4.toString(),"log.txt");
				           //�����̷߳�����Ϣ
				           Message tomain=mainhand.obtainMessage();	
				           //Э��ʧ��
				           if(map4.get("is_success").equals("F"))
				           {
				        	   tomain.what=SETFAILQUERYPROCHILD;
							   tomain.obj=map4.get("error");
				           }
				           else
				           {
				        	   //ҵ����ʧ��
				        	   if(map4.get("result_code").equals("FAIL"))
					           {
					        	   tomain.what=SETFAILQUERYBUSCHILD;
								   tomain.obj=map4.get("detail_error_code")+map4.get("detail_error_des");
					           }
				        	   //���׳ɹ�״̬
				        	   else if(map4.get("result_code").equals("SUCCESS"))
					           {
				        		   //���ڵȴ�֧��
				        		   if(map4.get("trade_status").equals("WAIT_BUYER_PAY"))
				        		   {
						        	   tomain.what=SETQUERYMAIN;
									   tomain.obj=map4.get("trade_status");
				        		   }
				        		   //ͨ��֧�����ṩ�Ķ���ֱ�����ɶ�ά��
				        		   else if(map4.get("trade_status").equals("TRADE_SUCCESS"))
				        		   {
						        	   tomain.what=SETQUERYMAINSUCC;
									   tomain.obj=map4.get("trade_status");
				        		   }
					           }
				           }
				           ToolClass.Log(ToolClass.INFO,"EV_JNI","rec3="+tomain.obj,"log.txt");				           
						   mainhand.sendMessage(tomain); // ������Ϣ
						   
				       } catch (Exception e) {  
				           e.printStackTrace();
				           //�����̷߳�����Ϣ
				           Message tomain=mainhand.obtainMessage();	
				    	   tomain.what=SETFAILNETCHILD;
				    	   tomain.obj="netfail";
				    	   ToolClass.Log(ToolClass.INFO,"EV_JNI","rec="+tomain.obj,"log.txt");				           
						   mainhand.sendMessage(tomain); // ������Ϣ
				       }
			       }
			       //ʹ��2.0�汾��֧�����ڱ�
			       else if(feimatype==2)	
			       {			    	   
			    	   try {
			    		    String rsp=AlipayAPI.PostAliQuery(sPara2);
				   			JSONObject response=new JSONObject(rsp);
				   			JSONObject obj=new JSONObject(response.get("alipay_trade_query_response").toString());
				   			ToolClass.Log(ToolClass.INFO,"EV_JNI","rec2="+obj.toString(),"log.txt");
				   			//�����̷߳�����Ϣ
				   			Message tomain=mainhand.obtainMessage();	
				   			String msg2=obj.getString("msg");
				   			if(msg2.toLowerCase().equals("success"))
				   			{
				   				//���ڵȴ�֧��
			        		   if(obj.getString("trade_status").equals("WAIT_BUYER_PAY"))
			        		   {
					        	   tomain.what=SETQUERYMAIN;
								   tomain.obj=obj.getString("trade_status");
			        		   }
			        		   //ͨ��֧�����ṩ�Ķ���ֱ�����ɶ�ά��
			        		   else if(obj.getString("trade_status").equals("TRADE_SUCCESS"))
			        		   {
					        	   tomain.what=SETQUERYMAINSUCC;
								   tomain.obj=obj.getString("trade_status");
			        		   }
				   			}
				   			else
				   			{
				   				tomain.what=SETFAILQUERYBUSCHILD;
				   			    tomain.obj=msg2;					   				
				   			}
				   			ToolClass.Log(ToolClass.INFO,"EV_JNI","rec3="+tomain.obj,"log.txt");				           
				   			mainhand.sendMessage(tomain); // ������Ϣ
				   		} catch (Exception e) {
				   			// TODO Auto-generated catch block
				   			e.printStackTrace();
				   			//�����̷߳�����Ϣ
				            Message tomain=mainhand.obtainMessage();	
				    	    tomain.what=SETFAILNETCHILD;
				    	    tomain.obj="netfail";
				    	    ToolClass.Log(ToolClass.INFO,"EV_JNI","rec="+tomain.obj,"log.txt");				           
						    mainhand.sendMessage(tomain); // ������Ϣ
				   		}
			       }
				break;
				case SETPAYOUTCHILD://���߳̽������߳���Ϣ
					ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIfeima>>�˿�]["+Thread.currentThread().getId()+"]"+msg.obj.toString(),"log.txt");
					Map<String, String> sPara3 = new HashMap<String, String>();
					//1.��Ӷ�����Ϣ
					JSONObject ev3=null;
					try {
						ev3 = new JSONObject(msg.obj.toString());							
						sPara3.put("out_trade_no", ev3.getString("out_trade_no"));//�������
						sPara3.put("refund_amount", ev3.getString("refund_amount"));
						sPara3.put("out_request_no", ev3.getString("out_request_no"));
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}					
				    ToolClass.Log(ToolClass.INFO,"EV_JNI","Send0.2="+sPara3.toString(),"log.txt");
				    //ʹ��1.0�汾��֧����
			    	if(feimatype==1)
			    	{
					    //2.����֧��������Ϣ
						Map<String, String> map5 = AlipayConfigAPI.PostAliPayout(sPara3);
				        try {          	       	                    	       	
				           HttpRequester request = new HttpRequester();              
				           String url = "https://mapi.alipay.com/gateway.do?" + "_input_charset=" + AlipayConfig.getInput_charset();           
				           //3.����֧��������Ϣ
				           HttpRespons hr = request.sendPost(url, map5);
				           //4.�õ������ַ���
				           String strpicString=hr.getContent();	
				           ToolClass.Log(ToolClass.INFO,"EV_JNI","rec1="+strpicString,"log.txt");
				           //5.������ص���Ϣ
				           InputStream is = new ByteArrayInputStream(strpicString.getBytes());// ��ȡ��������
				           Map<String, String> map6=AlipayConfigAPI.PendAliPayout(is);
				           ToolClass.Log(ToolClass.INFO,"EV_JNI","rec2="+map6.toString(),"log.txt");
				           //�����̷߳�����Ϣ
				           Message tomain=mainhand.obtainMessage();	
				           //Э��ʧ��
				           if(map6.get("is_success").equals("F"))
				           {
				        	   tomain.what=SETFAILPAYOUTPROCHILD;
							   tomain.obj=map6.get("error");
				           }
				           else
				           {
				        	   //ҵ����ʧ��
				        	   if(map6.get("result_code").equals("FAIL"))
					           {
					        	   tomain.what=SETFAILPAYOUTBUSCHILD;
								   tomain.obj=map6.get("detail_error_code")+map6.get("detail_error_des");
					           }
				        	   //ͨ��֧�����ṩ�Ķ���ֱ�����ɶ�ά��
				        	   else if(map6.get("result_code").equals("SUCCESS"))
					           {
					        	   tomain.what=SETPAYOUTMAIN;							   
					           }
				           }
				           ToolClass.Log(ToolClass.INFO,"EV_JNI","rec3="+tomain.obj,"log.txt");				           
						   mainhand.sendMessage(tomain); // ������Ϣ
						   
				       } catch (Exception e) {  
				           e.printStackTrace(); 
				           //�����̷߳�����Ϣ
				           Message tomain=mainhand.obtainMessage();	
				    	   tomain.what=SETFAILNETCHILD;
				    	   tomain.obj="netfail";
				    	   ToolClass.Log(ToolClass.INFO,"EV_JNI","rec="+tomain.obj,"log.txt");				           
						   mainhand.sendMessage(tomain); // ������Ϣ
				       }
			    	} 
			    	//ʹ��2.0�汾��֧�����ڱ�
			        else if(feimatype==2)	
			        {			    	   
			    	   try {
			    		    String rsp=AlipayAPI.PostAliPayout(sPara3);
				   			JSONObject response=new JSONObject(rsp);
				   			JSONObject obj=new JSONObject(response.get("alipay_trade_refund_response").toString());
				   			ToolClass.Log(ToolClass.INFO,"EV_JNI","rec2="+obj.toString(),"log.txt");
				   			//�����̷߳�����Ϣ
				   			Message tomain=mainhand.obtainMessage();	
				   			String msg2=obj.getString("msg");
				   			if(msg2.toLowerCase().equals("success"))
				   			{
				   				tomain.what=SETPAYOUTMAIN;
								tomain.obj="�˿�ɹ�";
				   			}
				   			else
				   			{
				   				tomain.what=SETFAILPAYOUTBUSCHILD;
				   			    tomain.obj=msg2;					   				
				   			}
				   			ToolClass.Log(ToolClass.INFO,"EV_JNI","rec3="+tomain.obj,"log.txt");				           
				   			mainhand.sendMessage(tomain); // ������Ϣ
				   		} catch (Exception e) {
				   			// TODO Auto-generated catch block
				   			e.printStackTrace();
				   			//�����̷߳�����Ϣ
				           Message tomain=mainhand.obtainMessage();	
				    	   tomain.what=SETFAILNETCHILD;
				    	   tomain.obj="netfail";
				    	   ToolClass.Log(ToolClass.INFO,"EV_JNI","rec="+tomain.obj,"log.txt");				           
						   mainhand.sendMessage(tomain); // ������Ϣ
				   		}
			    	   
			        }
				break;	
				case SETDELETECHILD://���߳̽������߳���Ϣ
					ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIfeima>>����]["+Thread.currentThread().getId()+"]"+msg.obj.toString(),"log.txt");
					Map<String, String> sPara4 = new HashMap<String, String>();
					//1.��Ӷ�����Ϣ
					JSONObject ev4=null;
					try {
						ev4 = new JSONObject(msg.obj.toString());							
						sPara4.put("out_trade_no", ev4.getString("out_trade_no"));//�������
								
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}					
				    ToolClass.Log(ToolClass.INFO,"EV_JNI","Send0.2="+sPara4.toString(),"log.txt");
				    //ʹ��1.0�汾��֧����
			    	if(feimatype==1)
			    	{
					    //2.����֧��������Ϣ
						Map<String, String> map7 = AlipayConfigAPI.PostAliDelete(sPara4);
				        try {          	       	                    	       	
				           HttpRequester request = new HttpRequester();              
				           String url = "https://mapi.alipay.com/gateway.do?" + "_input_charset=" + AlipayConfig.getInput_charset();           
				           //3.����֧��������Ϣ
				           HttpRespons hr = request.sendPost(url, map7);
				           //4.�õ������ַ���
				           String strpicString=hr.getContent();	
				           ToolClass.Log(ToolClass.INFO,"EV_JNI","rec1="+strpicString,"log.txt");
				           //5.������ص���Ϣ
				           InputStream is = new ByteArrayInputStream(strpicString.getBytes());// ��ȡ��������
				           Map<String, String> map8=AlipayConfigAPI.PendAliDelete(is);
				           ToolClass.Log(ToolClass.INFO,"EV_JNI","rec2="+map8.toString(),"log.txt");
				           //�����̷߳�����Ϣ
				           Message tomain=mainhand.obtainMessage();	
				           //Э��ʧ��
				           if(map8.get("is_success").equals("F"))
				           {
				        	   tomain.what=SETFAILDELETEPROCHILD;
							   tomain.obj=map8.get("error");
				           }
				           else
				           {
				        	   //ҵ����ʧ��
				        	   if(map8.get("result_code").equals("FAIL"))
					           {
					        	   tomain.what=SETFAILDELETEBUSCHILD;
								   tomain.obj=map8.get("detail_error_code")+map8.get("detail_error_des");
					           }
				        	   //ͨ��֧�����ṩ�Ķ���ֱ�����ɶ�ά��
				        	   else if(map8.get("result_code").equals("SUCCESS"))
					           {
					        	   tomain.what=SETDELETEMAIN;
								   tomain.obj=map8.get("trade_status");
					           }
				           }
				           ToolClass.Log(ToolClass.INFO,"EV_JNI","rec3="+tomain.obj,"log.txt");			           
						   mainhand.sendMessage(tomain); // ������Ϣ
						   
				       } catch (Exception e) {  
				           e.printStackTrace(); 
				           //�����̷߳�����Ϣ
				           Message tomain=mainhand.obtainMessage();	
				    	   tomain.what=SETFAILNETCHILD;
				    	   tomain.obj="netfail";
				    	   ToolClass.Log(ToolClass.INFO,"EV_JNI","rec="+tomain.obj,"log.txt");				           
						   mainhand.sendMessage(tomain); // ������Ϣ
				       }
			    	}
			    	//ʹ��2.0�汾��֧�����ڱ�
			        else if(feimatype==2)	
			        {			    	   
			    	   try {
			    		    String rsp=AlipayAPI.PostAliDelete(sPara4);
				   			JSONObject response=new JSONObject(rsp);
				   			JSONObject obj=new JSONObject(response.get("alipay_trade_cancel_response").toString());
				   			ToolClass.Log(ToolClass.INFO,"EV_JNI","rec2="+obj.toString(),"log.txt");
				   			//�����̷߳�����Ϣ
				   			Message tomain=mainhand.obtainMessage();	
				   			String msg2=obj.getString("msg");
				   			if(msg2.toLowerCase().equals("success"))
				   			{
				   				tomain.what=SETDELETEMAIN;
								tomain.obj="�����ɹ�";
				   			}
				   			else
				   			{
				   				tomain.what=SETFAILDELETEBUSCHILD;
				   			    tomain.obj=msg2;					   				
				   			}
				   			ToolClass.Log(ToolClass.INFO,"EV_JNI","rec3="+tomain.obj,"log.txt");				           
				   			mainhand.sendMessage(tomain); // ������Ϣ
				   		} catch (Exception e) {
				   			// TODO Auto-generated catch block
				   			e.printStackTrace();
				   			//�����̷߳�����Ϣ
				            Message tomain=mainhand.obtainMessage();	
				    	    tomain.what=SETFAILNETCHILD;
				    	    tomain.obj="netfail";
				    	    ToolClass.Log(ToolClass.INFO,"EV_JNI","rec="+tomain.obj,"log.txt");				           
						    mainhand.sendMessage(tomain); // ������Ϣ
				   		}
			    	   
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
