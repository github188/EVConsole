package com.easivend.http;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import com.easivend.common.ToolClass;
import com.easivend.weixing.WeiConfigAPI;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class Weixinghttp implements Runnable 
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
	public final static int SETPAYOUTCHILDSERVER=16;//what���,server���͸����߳�֧�����˿�
	//��������
	public final static int SETDELETECHILD=17;//what���,���͸����߳�֧������������
	public final static int SETDELETEMAIN=18;//what���,���͸����߳��˿���
	public final static int SETFAILDELETEPROCHILD=19;//what���,���͸����߳̽���Э��ʧ��
	public final static int SETFAILDELETEBUSCHILD=20;//what���,���͸����߳̽�����Ϣʧ��
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
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<weixinghttp="+Thread.currentThread().getId(),"log.txt");
		Looper.prepare();//�û��Լ�������࣬�����߳���Ҫ�Լ�׼��loop
		childhand=new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what)
				{
					case SETCHILD://���߳̽������߳���Ϣ
						ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIweixing>>��ά��]["+Thread.currentThread().getId()+"]"+msg.obj.toString(),"log.txt");
						Map<String, String> sPara = new HashMap<String, String>();
						//1.��Ӷ�����Ϣ
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
						ToolClass.Log(ToolClass.INFO,"EV_JNI","Send0.2="+sPara.toString(),"log.txt");
						//2.����֧��������Ϣ
						Map<String, String> map1 = WeiConfigAPI.PostWeiBuy(sPara);						
						try {
							//3.����֧��������Ϣ
							String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";			            
							String content= WeiConfigAPI.sendPost(url, map1);
				            //4.������ص���Ϣ
				            InputStream is = new ByteArrayInputStream(content.getBytes());// ��ȡ��������
					           
				           Map<String, String> map2=WeiConfigAPI.PendWeiBuy(is);
					       ToolClass.Log(ToolClass.INFO,"EV_JNI","rec2="+map2.toString(),"log.txt");
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
					        	   JSONObject zhuhe=new JSONObject();
					        	   zhuhe.put("code_url", map2.get("code_url"));
					        	   zhuhe.put("out_trade_no", ev.getString("out_trade_no"));
								   tomain.obj=zhuhe;
					           }
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
						ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIweixing>>��ѯ]["+Thread.currentThread().getId()+"]"+msg.obj.toString(),"log.txt");
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
						//2.����������Ϣ
						Map<String, String> map3 = WeiConfigAPI.PostWeiQuery(sPara2);
						try {
							//3.���Ͷ�����Ϣ
							String url2 = "https://api.mch.weixin.qq.com/pay/orderquery";			            
							String content2= WeiConfigAPI.sendPost(url2, map3);
							
				            //4.������ص���Ϣ
				            InputStream is = new ByteArrayInputStream(content2.getBytes());// ��ȡ��������
					           
				           Map<String, String> map4=WeiConfigAPI.PendWeiQuery(is);
					       ToolClass.Log(ToolClass.INFO,"EV_JNI","rec2="+map4.toString(),"log.txt");
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
				        	   //���׳ɹ�״̬
				        	   else if(map4.get("result_code").equals("SUCCESS"))
					           {
				        		  //���ڵȴ�֧��
				        		   if(map4.get("trade_state").equals("NOTPAY"))
				        		   {
				        			   tomain.what=SETQUERYMAIN;
									   tomain.obj=map4.get("trade_state");
				        		   }				        		   
				        		   //֧���ɹ�
				        		   else if(map4.get("trade_state").equals("SUCCESS"))
				        		   {
						        	   tomain.what=SETQUERYMAINSUCC;
									   tomain.obj=map4.get("trade_state");
				        		   }
				        		   //�������
				        		   else 
				        		   {
				        			   tomain.what=SETQUERYMAIN;
									   tomain.obj=map4.get("trade_state");
				        		   }
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
						ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIweixing>>�˿�]["+Thread.currentThread().getId()+"]"+msg.obj.toString(),"log.txt");
						Map<String, String> sPara3 = new HashMap<String, String>();
						//1.��Ӷ�����Ϣ
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
						ToolClass.Log(ToolClass.INFO,"EV_JNI","Send0.2="+sPara3.toString(),"log.txt");
						//2.����֧��������Ϣ
						Map<String, String> map5 = WeiConfigAPI.PostWeiPayout(sPara3);
						try {
							//3.����֧��������Ϣ
							String url3 = "https://api.mch.weixin.qq.com/secapi/pay/refund";			            
							String content3= WeiConfigAPI.sendPost(url3, map5);
							
				            //4.������ص���Ϣ
				            InputStream is = new ByteArrayInputStream(content3.getBytes());// ��ȡ��������
					           
				           Map<String, String> map6=WeiConfigAPI.PendWeiPayout(is);
					       ToolClass.Log(ToolClass.INFO,"EV_JNI","rec2="+map6.toString(),"log.txt");
					       //�����̷߳�����Ϣ
					        Message tomain=mainhand.obtainMessage();
					      //Э��ʧ��
				           if(map6.get("return_code").equals("FAIL"))
				           {
				        	   tomain.what=SETFAILPAYOUTPROCHILD;
							   tomain.obj=map6.get("return_msg");
				           }
				           else
				           {
				        	   //ҵ����ʧ��
				        	   if(map6.get("result_code").equals("FAIL"))
					           {
					        	   tomain.what=SETFAILPAYOUTBUSCHILD;
								   tomain.obj=map6.get("err_code")+map6.get("err_code_des");
					           }
				        	   //ͨ��֧�����ṩ�Ķ���ֱ�����ɶ�ά��
				        	   else if(map6.get("result_code").equals("SUCCESS"))
					           {
					        	   tomain.what=SETPAYOUTMAIN;
								   tomain.obj=map6.get("trade_state");
					           }
				           }
				           ToolClass.Log(ToolClass.INFO,"EV_JNI","rec3="+tomain.obj,"log.txt");	
					       mainhand.sendMessage(tomain); // ������Ϣ	
				           
				        } catch (Exception e) {
				            // TODO Auto-generated catch block
				            System.out.println(e);
				          //�����̷߳�����Ϣ
				           Message tomain=mainhand.obtainMessage();	
				    	   tomain.what=SETFAILPAYOUTPROCHILD;
				    	   tomain.obj="netfail";
				    	   ToolClass.Log(ToolClass.INFO,"EV_JNI","rec="+tomain.obj,"log.txt");				           
						   mainhand.sendMessage(tomain); // ������Ϣ
				        } 
						
					break;
					case SETDELETECHILD://���߳̽������߳���Ϣ
						ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIweixing>>����]["+Thread.currentThread().getId()+"]"+msg.obj.toString(),"log.txt");
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
					    //2.����������Ϣ
						Map<String, String> map7 = WeiConfigAPI.PostWeiDelete(sPara4);
						try {
							//3.���Ͷ�����Ϣ
							String url4 = "https://api.mch.weixin.qq.com/pay/closeorder";			            
							String content4= WeiConfigAPI.sendPost(url4, map7);
							
				            //4.������ص���Ϣ
				            InputStream is = new ByteArrayInputStream(content4.getBytes());// ��ȡ��������
					           
				           Map<String, String> map8=WeiConfigAPI.PendWeiDelete(is);
					       ToolClass.Log(ToolClass.INFO,"EV_JNI","rec2="+map8.toString(),"log.txt");
					       //�����̷߳�����Ϣ
					        Message tomain=mainhand.obtainMessage();
					      //Э��ʧ��
				           if(map8.get("return_code").equals("FAIL"))
				           {
				        	   tomain.what=SETFAILDELETEPROCHILD;
							   tomain.obj=map8.get("return_msg");
				           }
				           else
				           {
				        	   //ҵ����ʧ��
				        	   if(map8.get("result_code").equals("FAIL"))
					           {
					        	   tomain.what=SETFAILDELETEBUSCHILD;
								   tomain.obj=map8.get("err_code")+map8.get("err_code_des");
								   //֧�����ͬʱ�����³�����ť�����Ĺ���"�ۿ�ͳ���������10������"
								   if(map8.get("err_code").equals("USERPAYING")
									||map8.get("err_code").equals("ORDERPAID")
									||map8.get("err_code").equals("SYSTEMERROR"))
								   {
								      ToolClass.Log(ToolClass.INFO,"EV_JNI","׼���˿�...","log.txt");
								      Payoutind(ev4.getString("out_trade_no"),ev4.getString("out_refund_no"),ev4.getString("total_fee"),ev4.getString("refund_fee"));
								   }
					           }
				        	   //���׳ɹ�״̬
				        	   else if(map8.get("result_code").equals("SUCCESS"))
					           {
				        		  //���ڵȴ�֧��
				        		   tomain.what=SETDELETEMAIN;
								   tomain.obj=map8.get("trade_state");
					        	   
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
					case SETPAYOUTCHILDSERVER://���߳̽������߳���Ϣ
						ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIweixing>>�˿�]["+Thread.currentThread().getId()+"]"+msg.obj.toString(),"log.txt");
						Map<String, String> sPara5 = new HashMap<String, String>();
						//1.��Ӷ�����Ϣ
						JSONObject ev5=null;
						try {
							ev5 = new JSONObject(msg.obj.toString());							
							sPara5.put("out_trade_no", ev5.getString("out_trade_no"));//�������
							sPara5.put("out_refund_no", ev5.getString("out_refund_no"));//�˿���
							long fee3=ToolClass.MoneySend(Float.parseFloat(ev5.getString("total_fee")));
							String total_fee=String.valueOf(fee3);
							sPara5.put("total_fee",total_fee);//�����ܽ��
							
							fee3=ToolClass.MoneySend(Float.parseFloat(ev5.getString("refund_fee")));
							String refund_fee=String.valueOf(fee3);
							sPara5.put("refund_fee",refund_fee);//�����ܽ��	
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						ToolClass.Log(ToolClass.INFO,"EV_JNI","Send0.2="+sPara5.toString(),"log.txt");
						//2.����֧��������Ϣ
						Map<String, String> map6 = WeiConfigAPI.PostWeiPayout(sPara5);
						try {
							//3.����֧��������Ϣ
							String url3 = "https://api.mch.weixin.qq.com/secapi/pay/refund";			            
							String content3= WeiConfigAPI.sendPost(url3, map6);
							
				            //4.������ص���Ϣ
				            InputStream is = new ByteArrayInputStream(content3.getBytes());// ��ȡ��������
					           
				           Map<String, String> map8=WeiConfigAPI.PendWeiPayout(is);
					       ToolClass.Log(ToolClass.INFO,"EV_JNI","rec2="+map8.toString(),"log.txt");
					       //Э��ʧ��
				           if(map8.get("return_code").equals("FAIL"))
				           {
				        	   ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<WeiPayoutSend=΢���˿�ʧ��","log.txt");
				           }
				           else
				           {
				        	   //ҵ����ʧ��
				        	   if(map8.get("result_code").equals("FAIL"))
					           {
				        		   ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<WeiPayoutSend=΢���˿�ʧ��","log.txt");
					           }
				        	   //ͨ��֧�����ṩ�Ķ���ֱ�����ɶ�ά��
				        	   else if(map8.get("result_code").equals("SUCCESS"))
					           {
				        		   ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<WeiPayoutSend=΢���˿�ɹ�","log.txt");
				        		   ToolClass.WriteSharedPreferencesWeipayDel(msg.obj.toString());
					           }
				           }
				           
				        } catch (Exception e) {
				            // TODO Auto-generated catch block
				            System.out.println(e);
				            ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<WeiPayoutSend=΢���˿�ʧ��","log.txt");
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
	
	private void Payoutind(final String out_trade_no,final String out_refund_no,final String total_feetmp,final String refund_feetmp)
    {
        ToolClass.Log(ToolClass.INFO,"EV_JNI","[APIweixing>>�˿�]["+Thread.currentThread().getId()+"]","log.txt");
        new Thread()
        {
        	public void run(){
        		Map<String, String> sPara3 = new HashMap<String, String>();
                //1.��Ӷ�����Ϣ
                JSONObject ev3=null;
                ev3 = new JSONObject();                            
                sPara3.put("out_trade_no", out_trade_no);//�������
                sPara3.put("out_refund_no", out_refund_no);//�˿���
                long fee3=ToolClass.MoneySend(Float.parseFloat(total_feetmp));
                String total_fee=String.valueOf(fee3);
                sPara3.put("total_fee",total_fee);//�����ܽ��
                
                fee3=ToolClass.MoneySend(Float.parseFloat(refund_feetmp));
                String refund_fee=String.valueOf(fee3);
                sPara3.put("refund_fee",refund_fee);//�����ܽ��    
                ToolClass.Log(ToolClass.INFO,"EV_JNI","Send0.2="+sPara3.toString(),"log.txt");
                //2.����֧��������Ϣ
                Map<String, String> map5 = WeiConfigAPI.PostWeiPayout(sPara3);
                try {
                    //3.����֧��������Ϣ
                    String url3 = "https://api.mch.weixin.qq.com/secapi/pay/refund";                        
                    String content3= WeiConfigAPI.sendPost(url3, map5);
                    
                    //4.������ص���Ϣ
                    InputStream is = new ByteArrayInputStream(content3.getBytes());// ��ȡ��������
                       
                   Map<String, String> map6=WeiConfigAPI.PendWeiPayout(is);
                   ToolClass.Log(ToolClass.INFO,"EV_JNI","rec2="+map6.toString(),"log.txt");
                   //�����̷߳�����Ϣ
                    Message tomain=mainhand.obtainMessage();
                  //Э��ʧ��
                   if(map6.get("return_code").equals("FAIL"))
                   {
                       tomain.what=SETFAILPAYOUTPROCHILD;
                       tomain.obj=map6.get("return_msg");
                   }
                   else
                   {
                       //ҵ����ʧ��
                       if(map6.get("result_code").equals("FAIL"))
                       {
                           tomain.what=SETFAILPAYOUTBUSCHILD;
                           tomain.obj=map6.get("err_code")+map6.get("err_code_des");
                       }
                       //ͨ��֧�����ṩ�Ķ���ֱ�����ɶ�ά��
                       else if(map6.get("result_code").equals("SUCCESS"))
                       {
                           tomain.what=SETPAYOUTMAIN;
                           tomain.obj=map6.get("trade_state");
                       }
                   }
                   ToolClass.Log(ToolClass.INFO,"EV_JNI","rec3="+tomain.obj,"log.txt");    
                   mainhand.sendMessage(tomain); // ������Ϣ    
                   
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    System.out.println(e);
                  //�����̷߳�����Ϣ
                   Message tomain=mainhand.obtainMessage();    
                   tomain.what=SETFAILPAYOUTPROCHILD;
                   tomain.obj="netfail";
                   ToolClass.Log(ToolClass.INFO,"EV_JNI","rec="+tomain.obj,"log.txt");                           
                   mainhand.sendMessage(tomain); // ������Ϣ
                }
            }
        }.start();         
    }

}
