package com.easivend.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ClearCacheRequest;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.easivend.common.MediaFileAdapter;
import com.easivend.common.ToolClass;
import com.easivend.dao.vmc_columnDAO;
import com.easivend.dao.vmc_system_parameterDAO;
import com.easivend.model.Tb_vmc_system_parameter;
import com.easivend.view.XZip;
import com.google.gson.Gson;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;

public class EVServerhttp implements Runnable {
	RequestQueue mQueue = null; 
	String vmc_no="";
	String vmc_auth_code="";
	List<String> productimage=new ArrayList<String>();//������Ʒ��ϸ��Ϣ��ͼƬ
	public final static int SETNONE=0;//what���,���͸����߳���Ч��ʶ
	
	public final static int SETCHILD=2;//what���,���͸����߳�ǩ��
	public final static int SETMAIN=1;//what���,���͸����߳�ǩ����ɷ���	
	public final static int SETERRFAILMAIN=4;//what���,���͸����߳�ǩ������ʧ�ܷ���	
	
	public final static int SETHEARTCHILD=5;//what���,���͸����߳�����
	public final static int SETERRFAILHEARTMAIN=6;//what���,���͸����߳���������
	public final static int SETHEARTMAIN=7;//what���,���͸����̻߳�ȡ��������
	
	public final static int SETCLASSCHILD=8;//what���,���͸����̻߳�ȡ��Ʒ����
	public final static int SETERRFAILCLASSMAIN=9;//what���,���͸����̻߳�ȡ��Ʒ�������
	public final static int SETCLASSMAIN=10;//what���,���͸����̻߳�ȡ��Ʒ���෵��
	
	public final static int SETJOINCLASSCHILD=54;//what���,���͸����̻߳�ȡ��Ʒ�����Ӧ����Ʒid
	public final static int SETERRFAILJOINCLASSMAIN=55;//what���,���͸����̻߳�ȡ��Ʒ�����Ӧ����Ʒid����
	public final static int SETJOINCLASSMAIN=56;//what���,���͸����̻߳�ȡ��Ʒ�����Ӧ����Ʒid����
	
	public final static int SETPRODUCTCHILD=11;//what���,���͸����̻߳�ȡ��Ʒ��Ϣ
	public final static int SETERRFAILRODUCTMAIN=12;//what���,���͸����̻߳�ȡ��Ʒ����
	public final static int SETRODUCTMAIN=13;//what���,���͸����̻߳�ȡ��Ʒ����
	
	public final static int SETPRODUCTIMAGECHILD=60;//what���,���͸����̻߳�ȡ��Ʒ��ϸ�б���ͼƬ��Ϣ
	public final static int SETERRFAILRODUCTIMAGEMAIN=61;//what���,���͸����̻߳�ȡ��Ʒ��ϸ�б���ͼƬ����
	public final static int SETRODUCTIMAGEMAIN=62;//what���,���͸����̻߳�ȡ��Ʒ��ϸ�б���ͼƬ����
	
	public final static int SETHUODAOCHILD=14;//what���,���͸����̻߳�ȡ������Ϣ
	public final static int SETERRFAILHUODAOMAIN=15;//what���,���͸����̻߳�ȡ��������
	public final static int SETHUODAOMAIN=16;//what���,���͸����̻߳�ȡ��������
	
	public final static int SETHUODAOSTATUCHILD=17;//what���,���͸����߳��ϱ�������Ϣ
	public final static int SETERRFAILHUODAOSTATUMAIN=18;//what���,���͸����߳��ϱ�������Ϣ����
	public final static int SETHUODAOSTATUMAIN=19;//what���,���͸����߳��ϱ�������Ϣ
	
	public final static int SETDEVSTATUCHILD=20;//what���,���͸����߳��ϱ��豸״̬��Ϣ
	public final static int SETERRFAILDEVSTATUMAIN=21;//what���,���͸����̻߳�ȡ�豸����
	public final static int SETDEVSTATUMAIN=22;//what���,���͸����̻߳�ȡ�豸����	
	
	public final static int SETRECORDCHILD=23;//what���,���͸����߳��ϱ����׼�¼
	public final static int SETERRFAILRECORDMAIN=24;//what���,���͸����߳��ϱ����׹���
	public final static int SETRECORDMAIN=25;//what���,���͸����̻߳�ȡ�ϱ����׷���
	
	public final static int SETPVERSIONCHILD=27;//what���,���͸����̻߳�ȡ�汾��Ϣ
	public final static int SETERRFAILVERSIONMAIN=28;//what���,���͸����̻߳�ȡ�汾����
	public final static int SETVERSIONMAIN=29;//what���,���͸����̻߳�ȡ�汾����
	public final static int SETINSTALLMAIN=30;//what���,���͸����߳����°�װ����
	
	public final static int SETLOGCHILD=31;//what���,���͸����̻߳�ȡ��־��Ϣ
	public final static int SETERRFAILLOGMAIN=32;//what���,���͸����̻߳�ȡ��־����
	public final static int SETLOGMAIN=33;//what���,���͸����̻߳�ȡ��־����
	
	public final static int SETACCOUNTCHILD=34;//what���,���͸����̻߳�ȡ֧����΢���˺���Ϣ
	public final static int SETERRFAILACCOUNTMAIN=35;//what���,���͸����̻߳�ȡ֧����΢���˺Ź���
	public final static int SETACCOUNTMAIN=36;//what���,���͸����̻߳�ȡ֧����΢���˺ŷ���
	public final static int SETACCOUNTRESETMAIN=37;//what���,���͸����߳�֧����΢���˺���������
	
	public final static int SETADVCHILD=38;//what���,���͸����̻߳�ȡ�����Ϣ
	public final static int SETERRFAILADVMAIN=39;//what���,���͸����̻߳�ȡ������
	public final static int SETADVMAIN=40;//what���,���͸����̻߳�ȡ��淵��
	public final static int SETADVRESETMAIN=41;//what���,���͸����̹߳��������
	
	public final static int SETCLIENTCHILD=42;//what���,���͸����̻߳�ȡ�豸��Ϣ
	public final static int SETERRFAILCLIENTMAIN=43;//what���,���͸����̻߳�ȡ�豸����
	public final static int SETCLIENTMAIN=44;//what���,���͸����̻߳�ȡ�豸����
	
	public final static int SETEVENTINFOCHILD=45;//what���,���͸����̻߳�ȡ���Ϣ
	public final static int SETERRFAILEVENTINFOMAIN=46;//what���,���͸����̻߳�ȡ�����
	public final static int SETEVENTINFOMAIN=47;//what���,���͸����̻߳�ȡ�����
	
	public final static int SETDEMOINFOCHILD=48;//what���,���͸����̻߳�ȡ������ʾ��Ϣ
	public final static int SETERRFAILDEMOINFOMAIN=49;//what���,���͸����̻߳�ȡ������ʾ����
	public final static int SETDEMOINFOMAIN=50;//what���,���͸����̻߳�ȡ������ʾ����
	
	public final static int SETPICKUPCHILD=51;//what���,���͸�����ȡ������Ϣ
	public final static int SETERRFAILPICKUPMAIN=52;//what���,���͸����߳�ȡ������Ч
	public final static int SETPICKUPMAIN=53;//what���,���͸����߳�ȡ�������
	
	public final static int SETCHECKCMDCHILD=57;//what���,���͸����߳�ǩ����֤��Ϣ
	public final static int SETERRFAILDCHECKCMDMAIN=58;//what���,���͸����̻߳�ȡǩ����֤��Ϣ����
	public final static int SETCHECKCMDMAIN=59;//what���,���͸����̻߳�ȡǩ����֤��Ϣ����
	
	public final static int SETCHECKCHILD=26;//what���,���͸����̸߳���ǩ����Ϣ��	
	public final static int SETFAILMAIN=3;//what���,���͸����߳�����ʧ�ܷ���	
	String result = "";
	String Tok="";	
	String httpStr="http://easivend.net.cn/shj";
	private Handler mainhand=null,childhand=null;
	
	public EVServerhttp(Handler mainhand) {
		this.mainhand=mainhand;		
	}
	public Handler obtainHandler()
	{
		return this.childhand;
	}
	
	public RequestQueue getRequestQueue() {
        if (mQueue == null) {
            // getApplicationContext()�ǹؼ�, �������
            // Activity����BroadcastReceiver������ȱ��.
        	mQueue = Volley.newRequestQueue(ToolClass.getContext());
        }
        //ǿ���������
        File cacheDir = new File(ToolClass.getContext().getCacheDir(), "volley");
        DiskBasedCache cache = new DiskBasedCache(cacheDir);
        mQueue.start();

        // clear all volley caches.
        mQueue.add(new ClearCacheRequest(cache, null));
        return mQueue;
    }
	
	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		Looper.prepare();//�û��Լ�������࣬�����߳���Ҫ�Լ�׼��loop
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread start["+Thread.currentThread().getId()+"]","server.txt");
		childhand=new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what)
				{
				case SETCHILD://���߳̽������߳�ǩ����Ϣ					
					//1.�õ����������Ϣ
					JSONObject ev=null;
					try {
						ev = new JSONObject(msg.obj.toString());
						vmc_no=ev.getString("vmc_no");
						vmc_auth_code=ev.getString("vmc_auth_code");
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send0.2=vmc_no="+vmc_no+"vmc_auth_code="+vmc_auth_code,"server.txt");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//�õ�����˵�ַ��Ϣ
					//httpStr="http://easivend.net.cn/shj";
					//�½�Volley 
					mQueue = getRequestQueue();
					//2.���ò���,���÷�������ַ
					String target = httpStr+"/api/vmcCheckin";	//Ҫ�ύ��Ŀ���ַ
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread ǩ��["+Thread.currentThread().getId()+"]="+target,"server.txt");
					//1.��ӵ��༯�У�����key,value����ΪString
					Map<String,Object> parammap = new TreeMap<String,Object>() ;
					parammap.put("vmc_no",vmc_no);
					parammap.put("vmc_auth_code",vmc_auth_code);			
					//ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+parammap.toString(),"server.txt");
					//��2.map�༯תΪjson��ʽ
					Gson gson=new Gson();
					final String param=gson.toJson(parammap);		
					//ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send2="+param.toString(),"server.txt");
					
					//�����̷߳�����Ϣ
					final Message tomain1=mainhand.obtainMessage();
					tomain1.what=SETNONE;
					//4.�豸ǩ��
					StringRequest stringRequest = new StringRequest(Method.POST, target,  new Response.Listener<String>() {  
                        @Override  
                        public void onResponse(String response) {  
                           
                          //�������ɹ�
							result = response;	//��ȡ���ص��ַ���
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","һ�ڵĺ�̨","server.txt");
							JSONObject object;
							try {
								object = new JSONObject(result);
								int errType =  object.getInt("Error");
								//�����й���
								if(errType>0)
								{
									tomain1.what=SETERRFAILMAIN;
									tomain1.obj=object.getString("Message");
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail1]SETERRFAILMAIN","server.txt");
									if(object.has("CLIENT_STATUS_SERVICE"))
									{
										int CLIENT_STATUS_SERVICE=object.getInt("CLIENT_STATUS_SERVICE");
										//���ñ����Ƿ����ʹ��
						    			if(CLIENT_STATUS_SERVICE==0)
						    				ToolClass.setCLIENT_STATUS_SERVICE(true);
						    			else
						    				ToolClass.setCLIENT_STATUS_SERVICE(false);
									}									
								}
								else
								{
									tomain1.what=SETMAIN;
									Tok=object.getString("Token");
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok1]","server.txt");
									if(object.has("CLIENT_STATUS_SERVICE"))
									{
										int CLIENT_STATUS_SERVICE=object.getInt("CLIENT_STATUS_SERVICE");
										//���ñ����Ƿ����ʹ��
						    			if(CLIENT_STATUS_SERVICE==0)
						    				ToolClass.setCLIENT_STATUS_SERVICE(true);
						    			else
						    				ToolClass.setCLIENT_STATUS_SERVICE(false);
									}
									else
									{
										ToolClass.setCLIENT_STATUS_SERVICE(true);
									}
								}	
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}										    	    
				    	    mainhand.sendMessage(tomain1); // ������Ϣ
                        }  
                    }, new Response.ErrorListener() {  
                        @Override  
                        public void onErrorResponse(VolleyError error) {  
                        	result = "����ʧ�ܣ�";
							tomain1.what=SETFAILMAIN;
							mainhand.sendMessage(tomain1); // ������Ϣ
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail1]SETFAILMAIN"+result,"server.txt");  
                        }  
                    }) 
					{  
					    @Override  
					    protected Map<String, String> getParams() throws AuthFailureError {  
					    	//3.���params
					    	Map<String, String> map = new HashMap<String, String>();  
					        map.put("param", param);  
					        //params.add(new BasicNameValuePair("param", param));
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+param.toString(),"server.txt");
					        return map;  
					    }  
					}; 	
					//5.������Ϣ�����͵�������
					mQueue.add(stringRequest);
					break;
				case SETHEARTCHILD://���߳̽������߳�������Ϣ
					//����
					String target2 = httpStr+"/api/vmcPoll";	//Ҫ�ύ��Ŀ���ַ
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread ����["+Thread.currentThread().getId()+"]="+target2,"server.txt");
					//�½�Volley 
					mQueue = getRequestQueue();
					//�����̷߳�����Ϣ
					final Message tomain2=mainhand.obtainMessage();
					tomain2.what=SETNONE;
					//4.׼��������Ϣ����
					StringRequest stringRequest2 = new StringRequest(Method.POST, target2,  new Response.Listener<String>() {  
						@Override  
						public void onResponse(String response) {  
						   
						  //�������ɹ�
							result = response;	//��ȡ���ص��ַ���
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
							JSONObject object;
							try {
								object = new JSONObject(result);
								int errType =  object.getInt("Error");
								//�����й���
								if(errType>0)
								{
									tomain2.what=SETERRFAILHEARTMAIN;
									tomain2.obj=object.getString("Message");
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail2]SETERRFAILHEARTMAIN","server.txt");
								}
								else
								{
									tomain2.what=SETHEARTMAIN;
									tomain2.obj=result;
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok2]","server.txt");
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}										    	    
							mainhand.sendMessage(tomain2); // ������Ϣ
						}  
					}, new Response.ErrorListener() {  
						@Override  
						public void onErrorResponse(VolleyError error) {  
							result = "����ʧ�ܣ�";
							tomain2.what=SETFAILMAIN;
							mainhand.sendMessage(tomain2); // ������Ϣ
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail2]SETFAILMAIN"+result,"server.txt");
						}  
					}) 
					{  
						@Override  
						protected Map<String, String> getParams() throws AuthFailureError {  
							//3.���params
							Map<String, String> map = new HashMap<String, String>();  
							map.put("Token", Tok);  
							map.put("LastPollTime", ToolClass.getLasttime());
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+map.toString(),"server.txt");
							return map;  
					   }  
					}; 	
					//5.������Ϣ�����͵�������
					mQueue.add(stringRequest2);				
						
					break;
				case SETCLASSCHILD://��ȡ��Ʒ����
					String target3 = httpStr+"/api/productClass";	//Ҫ�ύ��Ŀ���ַ
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread ��ȡ��Ʒ����["+Thread.currentThread().getId()+"]="+target3,"server.txt");
					final String LAST_EDIT_TIME3=msg.obj.toString();
					//�½�Volley 
					mQueue = getRequestQueue();
					//�����̷߳�����Ϣ
					final Message tomain3=mainhand.obtainMessage();
					tomain3.what=SETNONE;
					//4.׼��������Ϣ����
					StringRequest stringRequest3 = new StringRequest(Method.POST, target3,  new Response.Listener<String>() {  
                        @Override  
                        public void onResponse(String response) {  
                           
                          //�������ɹ�
                        	result = response;	//��ȡ���ص��ַ���
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
							JSONObject object;
							try {
								object = new JSONObject(result);
								int errType =  object.getInt("Error");
								//�����й���
								if(errType>0)
								{
									tomain3.what=SETERRFAILCLASSMAIN;
									tomain3.obj=object.getString("Message");
									mainhand.sendMessage(tomain3); // ������Ϣ
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail3]SETERRFAILCLASSMAIN","server.txt");
								}
								else
								{
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok3]","server.txt");
									JSONObject jsonObject = new JSONObject(result); 
									if(jsonObject.has("ProductClassList")==true)
									{										
										tomain3.what=SETCLASSMAIN;
										tomain3.obj=result;	
										mainhand.sendMessage(tomain3); // ������Ϣ
									}
									else if(jsonObject.has("List")==true)
									{										
										ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok4]׼��������Ʒ����...","server.txt");
										classArray(result);
										if(classarr.length()>0)
										{
											updateclass(0);
										}
									}
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}									    	    
				    	    
                        }  
                    }, new Response.ErrorListener() {  
                        @Override  
                        public void onErrorResponse(VolleyError error) {  
                        	result = "����ʧ�ܣ�";
							tomain3.what=SETFAILMAIN;
				    	    mainhand.sendMessage(tomain3); // ������Ϣ
				    	    ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail3]SETFAILMAIN"+result,"server.txt");
                        }  
                    }) 
					{  
					    @Override  
					    protected Map<String, String> getParams() throws AuthFailureError {  
					    	//3.���params
					    	Map<String, String> map = new HashMap<String, String>();  
					        map.put("Token", Tok);  
					        map.put("LAST_EDIT_TIME", LAST_EDIT_TIME3);
					        map.put("vmc_no", vmc_no); 
					        ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+map.toString(),"server.txt");
					        return map;  
					   }  
					}; 	
					//5.������Ϣ�����͵�������
					mQueue.add(stringRequest3);									
					break;
				case SETJOINCLASSCHILD://��ȡ��Ʒ�����Ӧ����Ʒ��Ϣ
					String target20 = httpStr+"/api/productJoinClass";	//Ҫ�ύ��Ŀ���ַ
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread ��ȡ��Ʒ������Ʒ��Ӧ��ϵ["+Thread.currentThread().getId()+"]="+target20,"server.txt");
					final String LAST_EDIT_TIME20=msg.obj.toString();
					//�½�Volley 
					mQueue = getRequestQueue();
					//�����̷߳�����Ϣ
					final Message tomain20=mainhand.obtainMessage();
					tomain20.what=SETNONE;
					//4.׼��������Ϣ����
					StringRequest stringRequest20 = new StringRequest(Method.POST, target20,  new Response.Listener<String>() {  
                        @Override  
                        public void onResponse(String response) {  
                           
                          //�������ɹ�
                        	result = response;	//��ȡ���ص��ַ���
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
							JSONObject object;
							try {
								object = new JSONObject(result);
								int errType =  object.getInt("Error");
								//�����й���
								if(errType>0)
								{
									tomain20.what=SETERRFAILJOINCLASSMAIN;
									tomain20.obj=object.getString("Message");
									mainhand.sendMessage(tomain20); // ������Ϣ
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail20]SETERRFAILCLASSMAIN","server.txt");
								}
								else
								{
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok20]","server.txt");
									JSONObject jsonObject = new JSONObject(result); 
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok4]׼��������Ʒ�����Ӧ����Ʒ��Ϣ...","server.txt");
									classjoinArray(result);
									if(classjoinarr.length()>0)
									{
										updateclassjoin(0);
									}
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}									    	    
				    	    
                        }  
                    }, new Response.ErrorListener() {  
                        @Override  
                        public void onErrorResponse(VolleyError error) {  
                        	result = "����ʧ�ܣ�";
							tomain20.what=SETFAILMAIN;
				    	    mainhand.sendMessage(tomain20); // ������Ϣ
				    	    ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail20]SETFAILMAIN"+result,"server.txt");
                        }  
                    }) 
					{  
					    @Override  
					    protected Map<String, String> getParams() throws AuthFailureError {  
					    	//3.���params
					    	Map<String, String> map = new HashMap<String, String>();  
					        map.put("Token", Tok);  
					        map.put("LastEditTime", LAST_EDIT_TIME20);
					        map.put("CLIENT_NO", vmc_no); 
					        ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+map.toString(),"server.txt");
					        return map;  
					   }  
					}; 	
					//5.������Ϣ�����͵�������
					mQueue.add(stringRequest20);	
					break;
				case SETPRODUCTCHILD://��ȡ��Ʒ��Ϣ
					String target4 = httpStr+"/api/productData";	//Ҫ�ύ��Ŀ���ַ
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread ��ȡ��Ʒ��Ϣ["+Thread.currentThread().getId()+"]="+target4,"server.txt");
					final String LAST_EDIT_TIME4=msg.obj.toString();
					//�½�Volley 
					mQueue = getRequestQueue();
					//�����̷߳�����Ϣ
					final Message tomain4=mainhand.obtainMessage();
					tomain4.what=SETNONE;
					//4.׼��������Ϣ����
					StringRequest stringRequest4 = new StringRequest(Method.POST, target4,  new Response.Listener<String>() {  
						@Override  
						public void onResponse(String response) {  
						   
						  //�������ɹ�
							result = response;	//��ȡ���ص��ַ���
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
							JSONObject object;
							try {
								object = new JSONObject(result);
								int errType =  object.getInt("Error");
								//�����й���
								if(errType>0)
								{
									tomain4.what=SETERRFAILRODUCTMAIN;
									tomain4.obj=object.getString("Message");
									mainhand.sendMessage(tomain4); // ������Ϣ
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail4]SETERRFAILRODUCTMAIN","server.txt");
								}
								else
								{
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok4]׼��������Ʒ��Ϣ...","server.txt");
									productArray(result);
									if(productarr.length()>0)
									{
										updateproduct(0);
									}
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}										  
						}  
					}, new Response.ErrorListener() {  
						@Override  
						public void onErrorResponse(VolleyError error) {  
							result = "����ʧ�ܣ�";
							tomain4.what=SETFAILMAIN;
				    	    mainhand.sendMessage(tomain4); // ������Ϣ
				    	    ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail4]SETFAILMAIN"+result,"server.txt");
						}  
					}) 
					{  
						@Override  
						protected Map<String, String> getParams() throws AuthFailureError {  
							//3.���params
							Map<String, String> map = new HashMap<String, String>();  
							map.put("Token", Tok);
							map.put("VMC_NO", vmc_no);
							map.put("PAGE_INDEX", "");
							map.put("PAGE_SIZE", "");
							map.put("LAST_EDIT_TIME", LAST_EDIT_TIME4);
							map.put("PRODUCT_NO", "");
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+map.toString(),"server.txt");
							return map;  
					   }  
					}; 	
					//5.������Ϣ�����͵�������
					mQueue.add(stringRequest4);	
					break;
				case SETPRODUCTIMAGECHILD://��ȡ��Ʒ��ϸ�б���ͼƬ��Ϣ
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread ��ȡ��Ʒ��ϸ�б���ͼƬ��Ϣ["+Thread.currentThread().getId()+"]","server.txt");
					try {
						productimageArray();
						if(productimage.size()>0)
						{
							updateproductimage(0);
						}
					} catch (Exception e3) {
						// TODO Auto-generated catch block
						e3.printStackTrace();
					}					
					break;	
				case SETHUODAOCHILD://��ȡ������Ϣ
					String target5 = httpStr+"/api/vmcPathConfigDownload";	//Ҫ�ύ��Ŀ���ַ
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread ��ȡ������Ϣ["+Thread.currentThread().getId()+"]="+target5,"server.txt");
					final String LAST_EDIT_TIME5=msg.obj.toString();
					//�½�Volley 
					mQueue = getRequestQueue();
					//�����̷߳�����Ϣ
					final Message tomain5=mainhand.obtainMessage();
					tomain5.what=SETNONE;
					//4.׼��������Ϣ����
					StringRequest stringRequest5 = new StringRequest(Method.POST, target5,  new Response.Listener<String>() {  
						@Override  
						public void onResponse(String response) {  
						   
						  //�������ɹ�
							result = response;	//��ȡ���ص��ַ���
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
							JSONObject object;
							try {
								object = new JSONObject(result);
								int errType =  object.getInt("Error");
								//�����й���
								if(errType>0)
								{
									tomain5.what=SETERRFAILHUODAOMAIN;
									tomain5.obj=object.getString("Message");
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail5]SETERRFAILHUODAOMAIN","server.txt");
								}
								else
								{
									tomain5.what=SETHUODAOMAIN;
									tomain5.obj=result;
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok5]","server.txt");
								}			    	    
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}										    	    
							mainhand.sendMessage(tomain5); // ������Ϣ
						}  
					}, new Response.ErrorListener() {  
						@Override  
						public void onErrorResponse(VolleyError error) {  
							result = "����ʧ�ܣ�";
							tomain5.what=SETFAILMAIN;
				    	    mainhand.sendMessage(tomain5); // ������Ϣ
				    	    ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail5]SETFAILMAIN"+result,"server.txt");
						}  
					}) 
					{  
						@Override  
						protected Map<String, String> getParams() throws AuthFailureError {  
							//3.���params
							Map<String, String> map = new HashMap<String, String>();  
							map.put("Token", Tok);  
							map.put("LAST_EDIT_TIME", LAST_EDIT_TIME5);
							map.put("VMC_NO", vmc_no);
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+map.toString(),"server.txt");
							return map;  
					   }  
					}; 	
					//5.������Ϣ�����͵�������
					mQueue.add(stringRequest5);					
					break;										
				case EVServerhttp.SETDEVSTATUCHILD://�豸״̬�ϱ�
					int bill_err=0;
	    			int coin_err=0;
					//1.�õ����������Ϣ
					JSONObject ev7=null;
					try {
						ev7 = new JSONObject(msg.obj.toString());
						bill_err=ev7.getInt("bill_err");
						coin_err=ev7.getInt("coin_err");
		    			ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send0.2=bill_err="+bill_err+"coin_err="+coin_err
						,"server.txt");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//�豸״̬�ϱ�
					String target7 = httpStr+"/api/vmcStatus";	//Ҫ�ύ��Ŀ���ַ
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread �豸״̬�ϱ�["+Thread.currentThread().getId()+"]="+target7,"server.txt");
					int isPrinter=0,zhifubaofaca=0;
					vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(ToolClass.getContext());// ����InaccountDAO����
				    // �õ��豸ID��
			    	Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
			    	if(tb_inaccount!=null)
			    	{
			    		isPrinter=tb_inaccount.getIsNet();
			    		zhifubaofaca=tb_inaccount.getZhifubaofaca();
			    	}
					//1.��ӵ��༯�У�����key,value����ΪString
					Map<String,Object> parammap7 = new TreeMap<String,Object>() ;
					parammap7.put("VMC_NO",vmc_no);
					parammap7.put("CLIENT_STATUS","0");
					parammap7.put("COINS_STATUS",coin_err);
					parammap7.put("NOTE_STATUS",bill_err);	
					parammap7.put("DOOR_STATUS","0");	
					parammap7.put("WAREHOUSE_TEMPERATURE","0");		
					parammap7.put("POS_STATUS",zhifubaofaca);	
					parammap7.put("PRINT_STATUS",isPrinter);
					if(ToolClass.getServerVer()==1)//һ�ں�̨
					{
						String temp="";
						//����
						if(ToolClass.getOrientation()==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
						{
							temp="��׿����";
						}
						//����
						else
						{
							temp="��׿����";
						}
						parammap7.put("CLIENT_VERSION",temp+ToolClass.getVersion());	
						parammap7.put("CLIENT_DESC","�����汾��");	
					}
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+parammap7.toString(),"server.txt");
					//��2.map�༯תΪjson��ʽ
					Gson gson7=new Gson();
					final String param7=gson7.toJson(parammap7);		
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send2="+param7.toString(),"server.txt");
					//�½�Volley 
					mQueue = getRequestQueue();
					//�����̷߳�����Ϣ
					final Message tomain7=mainhand.obtainMessage();
					tomain7.what=SETNONE;
					//4.׼��������Ϣ����
					StringRequest stringRequest7 = new StringRequest(Method.POST, target7,  new Response.Listener<String>() {  
						@Override  
						public void onResponse(String response) {  
						   
						  //�������ɹ�
							result = response;	//��ȡ���ص��ַ���
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
							JSONObject object;
							try {
								object = new JSONObject(result);
								int errType =  object.getInt("Error");
								//�����й���
								if(errType>0)
								{
									tomain7.what=SETERRFAILDEVSTATUMAIN;
									tomain7.obj=object.getString("Message");
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail6]SETERRFAILDEVSTATUMAIN","server.txt");
								}
								else
								{
									tomain7.what=SETDEVSTATUMAIN;
									tomain7.obj=result;
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok6]","server.txt");
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}										    	    
							mainhand.sendMessage(tomain7); // ������Ϣ
						}  
					}, new Response.ErrorListener() {  
						@Override  
						public void onErrorResponse(VolleyError error) {  
							result = "����ʧ�ܣ�";
							tomain7.what=SETFAILMAIN;
				    	    mainhand.sendMessage(tomain7); // ������Ϣ
				    	    ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=Net[fail6]SETFAILMAIN","server.txt");
						}  
					}) 
					{  
						@Override  
						protected Map<String, String> getParams() throws AuthFailureError {  
							//3.���params
							Map<String, String> map = new HashMap<String, String>();  
							map.put("Token", Tok);  
							map.put("param", param7);
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send3="+map.toString(),"server.txt");
							return map;  
					   }  
					}; 	
					//5.������Ϣ�����͵�������
					mQueue.add(stringRequest7);
					
					break;
				case EVServerhttp.SETRECORDCHILD://���׼�¼�ϱ�	
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread ���׼�¼�ϱ�["+Thread.currentThread().getId()+"]","server.txt");
					//1.�õ����׼�¼�����Ϣ
					try {
						recordArray(msg.obj.toString());
						if(recordarr.length()>0)
						{
							updaterecord(0);
						}
					} catch (JSONException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					break;
				case SETHUODAOSTATUCHILD://����״̬�ϱ���Ϣ	
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread ����״̬�ϱ�["+Thread.currentThread().getId()+"]","server.txt");
					//1.�õ����׼�¼�����Ϣ
					try {
						columnArray(msg.obj.toString());
						if(columnarr.length()>0)
						{
							updatecolumn(0);
						}
					} catch (JSONException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					break;	
				case SETCHECKCHILD://���߳̽������߳�ǩ����Ϣ					
					//1.�õ����������Ϣ
					JSONObject ev10=null;
					try {
						ev10 = new JSONObject(msg.obj.toString());
						vmc_no=ev10.getString("vmc_no");
						vmc_auth_code=ev10.getString("vmc_auth_code");
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send0.2=vmc_no="+vmc_no+"vmc_auth_code="+vmc_auth_code,"server.txt");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}	
					//�豸ǩ��
					String target11 = httpStr+"/api/vmcCheckin";	//Ҫ�ύ��Ŀ���ַ
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread �豸ǩ��["+Thread.currentThread().getId()+"]="+target11,"server.txt");
					//1.��ӵ��༯�У�����key,value����ΪString
					Map<String,Object> parammap11 = new TreeMap<String,Object>() ;
					parammap11.put("vmc_no",vmc_no);
					parammap11.put("vmc_auth_code",vmc_auth_code);			
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+parammap11.toString(),"server.txt");
					//��2.map�༯תΪjson��ʽ
					Gson gson11=new Gson();
					final String param11=gson11.toJson(parammap11);		
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send2="+param11.toString(),"server.txt");
					//�½�Volley 
					mQueue = getRequestQueue();
					//�����̷߳�����Ϣ
					final Message tomain11=mainhand.obtainMessage();
					tomain11.what=SETNONE;
					//4.�豸ǩ��
					StringRequest stringRequest11 = new StringRequest(Method.POST, target11,  new Response.Listener<String>() {  
                        @Override  
                        public void onResponse(String response) {  
                           
                            //�������ɹ�
							result = response;	//��ȡ���ص��ַ���
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
							JSONObject object;
							try {
								object = new JSONObject(result);
								int errType =  object.getInt("Error");
								//�����й���
								if(errType>0)
								{	
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail9]SETERRFAILMAIN","server.txt");	
									if(object.has("CLIENT_STATUS_SERVICE"))
									{
										int CLIENT_STATUS_SERVICE=object.getInt("CLIENT_STATUS_SERVICE");
										//���ñ����Ƿ����ʹ��
										if(CLIENT_STATUS_SERVICE==0)
											ToolClass.setCLIENT_STATUS_SERVICE(true);
										else
											ToolClass.setCLIENT_STATUS_SERVICE(false);
									}
								}
								else
								{
									Tok=object.getString("Token");	
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok9]","server.txt");
									if(object.has("CLIENT_STATUS_SERVICE"))
									{
										int CLIENT_STATUS_SERVICE=object.getInt("CLIENT_STATUS_SERVICE");
										//���ñ����Ƿ����ʹ��
										if(CLIENT_STATUS_SERVICE==0)
											ToolClass.setCLIENT_STATUS_SERVICE(true);
										else
											ToolClass.setCLIENT_STATUS_SERVICE(false);
									}
									else
									{
										ToolClass.setCLIENT_STATUS_SERVICE(true);
									}
								}	
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}										    	    
				    	    mainhand.sendMessage(tomain11); // ������Ϣ
                        }  
                    }, new Response.ErrorListener() {  
                        @Override  
                        public void onErrorResponse(VolleyError error) {  
                        	result = "����ʧ�ܣ�";
                        	tomain11.what=SETFAILMAIN;
    						mainhand.sendMessage(tomain11); // ������Ϣ	
    						ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=Net[fail9]SETFAILMAIN","server.txt");
                        }  
                    }) 
					{  
					    @Override  
					    protected Map<String, String> getParams() throws AuthFailureError {  
					    	//3.���params
					    	Map<String, String> map = new HashMap<String, String>();  
					        map.put("param", param11);  					        
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send3="+map.toString(),"server.txt");
					        return map;  
					    }  
					}; 	
					//5.������Ϣ�����͵�������
					mQueue.add(stringRequest11);						
					break;
				case SETCHECKCMDCHILD://���߳̽������߳�ǩ��������Ϣ					
					//1.�õ����������Ϣ
					JSONObject ev21=null;
					try {
						ev21 = new JSONObject(msg.obj.toString());
						vmc_no=ev21.getString("vmc_no");
						vmc_auth_code=ev21.getString("vmc_auth_code");
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send0.2=vmc_no="+vmc_no+"vmc_auth_code="+vmc_auth_code,"server.txt");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}	
					//�豸ǩ��
					String target21 = httpStr+"/api/vmcCheckin";	//Ҫ�ύ��Ŀ���ַ
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread �豸ǩ��["+Thread.currentThread().getId()+"]="+target21,"server.txt");
					//1.��ӵ��༯�У�����key,value����ΪString
					Map<String,Object> parammap21 = new TreeMap<String,Object>() ;
					parammap21.put("vmc_no",vmc_no);
					parammap21.put("vmc_auth_code",vmc_auth_code);			
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+parammap21.toString(),"server.txt");
					//��2.map�༯תΪjson��ʽ
					Gson gson21=new Gson();
					final String param21=gson21.toJson(parammap21);		
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send2="+param21.toString(),"server.txt");
					//�½�Volley 
					mQueue = getRequestQueue();
					//�����̷߳�����Ϣ
					final Message tomain21=mainhand.obtainMessage();
					tomain21.what=SETNONE;
					//4.�豸ǩ��
					StringRequest stringRequest21 = new StringRequest(Method.POST, target21,  new Response.Listener<String>() {  
                        @Override  
                        public void onResponse(String response) {  
                           
                            //�������ɹ�
							result = response;	//��ȡ���ص��ַ���
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
							JSONObject object;
							try {
								object = new JSONObject(result);
								int errType =  object.getInt("Error");
								//�����й���
								if(errType>0)
								{	
									tomain21.what=SETERRFAILDCHECKCMDMAIN;
									tomain21.obj=object.getString("Message");
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail9]SETERRFAILMAIN","server.txt");	
									if(object.has("CLIENT_STATUS_SERVICE"))
									{
										int CLIENT_STATUS_SERVICE=object.getInt("CLIENT_STATUS_SERVICE");
										//���ñ����Ƿ����ʹ��
										if(CLIENT_STATUS_SERVICE==0)
											ToolClass.setCLIENT_STATUS_SERVICE(true);
										else
											ToolClass.setCLIENT_STATUS_SERVICE(false);
									}
								}
								else
								{
									tomain21.what=SETCHECKCMDMAIN;
									Tok=object.getString("Token");	
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok9]","server.txt");
									if(object.has("CLIENT_STATUS_SERVICE"))
									{
										int CLIENT_STATUS_SERVICE=object.getInt("CLIENT_STATUS_SERVICE");
										//���ñ����Ƿ����ʹ��
										if(CLIENT_STATUS_SERVICE==0)
											ToolClass.setCLIENT_STATUS_SERVICE(true);
										else
											ToolClass.setCLIENT_STATUS_SERVICE(false);
									}
									else
									{
										ToolClass.setCLIENT_STATUS_SERVICE(true);
									}
								}	
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}										    	    
				    	    mainhand.sendMessage(tomain21); // ������Ϣ
                        }  
                    }, new Response.ErrorListener() {  
                        @Override  
                        public void onErrorResponse(VolleyError error) {  
                        	result = "����ʧ�ܣ�";
                        	tomain21.what=SETERRFAILDCHECKCMDMAIN;
                        	tomain21.obj="Error net";
    						mainhand.sendMessage(tomain21); // ������Ϣ	
    						ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=Net[fail9]SETFAILMAIN","server.txt");
                        }  
                    }) 
					{  
					    @Override  
					    protected Map<String, String> getParams() throws AuthFailureError {  
					    	//3.���params
					    	Map<String, String> map = new HashMap<String, String>();  
					        map.put("param", param21);  					        
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send3="+map.toString(),"server.txt");
					        return map;  
					    }  
					}; 	
					//5.������Ϣ�����͵�������
					mQueue.add(stringRequest21);						
					break;	
				case SETPVERSIONCHILD://��ȡ�汾��Ϣ
					String target12 = httpStr+"/api/clientVersion";	//Ҫ�ύ��Ŀ���ַ
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread ��ȡ�汾��Ϣ["+Thread.currentThread().getId()+"]="+target12,"server.txt");
					final String LAST_EDIT_TIME12=msg.obj.toString();
					//�½�Volley 
					mQueue = getRequestQueue();
					//�����̷߳�����Ϣ
					final Message tomain12=mainhand.obtainMessage();
					tomain12.what=SETNONE;
					//4.׼��������Ϣ����
					StringRequest stringRequest12 = new StringRequest(Method.POST, target12,  new Response.Listener<String>() {  
						@Override  
						public void onResponse(String response) {  
						   
						    //�������ɹ�
							result = response;	//��ȡ���ص��ַ���
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
							JSONObject object;
							try {
								object = new JSONObject(result);
								int errType =  object.getInt("Error");
								//�����й���
								if(errType>0)
								{
									tomain12.what=SETERRFAILVERSIONMAIN;
									tomain12.obj=object.getString("Message");							 	    
									mainhand.sendMessage(tomain12); // ������Ϣ
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail12]SETERRFAILHUODAOMAIN","server.txt");
								}
								else
								{
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok12]","server.txt");
									versionArray(result);
									if(versionarr.length()>0)
									{
										updateversion(0);
									}
								}			    	    
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}										   
						}  
					}, new Response.ErrorListener() {  
						@Override  
						public void onErrorResponse(VolleyError error) {  
							result = "����ʧ�ܣ�";
							tomain12.what=SETFAILMAIN;
				    	    mainhand.sendMessage(tomain12); // ������Ϣ
				    	    ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail12]SETFAILMAIN"+result,"server.txt");
						}  
					}) 
					{  
						@Override  
						protected Map<String, String> getParams() throws AuthFailureError {  
							//3.���params
							Map<String, String> map = new HashMap<String, String>();  
							map.put("Token", Tok);  
							map.put("LAST_EDIT_TIME", LAST_EDIT_TIME12);
							map.put("VMC_NO", vmc_no);
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+map.toString(),"server.txt");
							return map;  
					   }  
					}; 	
					//5.������Ϣ�����͵�������
					mQueue.add(stringRequest12);					
					break;
				case SETLOGCHILD://��ȡ��־��Ϣ
					String target13 = httpStr+"/api/clientLogInfo";	//Ҫ�ύ��Ŀ���ַ
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread ��ȡ��־��Ϣ["+Thread.currentThread().getId()+"]="+target13,"server.txt");
					final String LAST_EDIT_TIME13=msg.obj.toString();
					//�½�Volley 
					mQueue = getRequestQueue();
					//�����̷߳�����Ϣ
					final Message tomain13=mainhand.obtainMessage();
					tomain13.what=SETNONE;
					//4.׼��������Ϣ����
					StringRequest stringRequest13 = new StringRequest(Method.POST, target13,  new Response.Listener<String>() {  
						@Override  
						public void onResponse(String response) {  
						   
						    //�������ɹ�
							result = response;	//��ȡ���ص��ַ���
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
							JSONObject object;
							try {
								object = new JSONObject(result);
								int errType =  object.getInt("Error");
								//�����й���
								if(errType>0)
								{
									tomain13.what=SETERRFAILLOGMAIN;
									tomain13.obj=object.getString("Message");							   	    
									mainhand.sendMessage(tomain13); // ������Ϣ
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail13]SETERRFAILHUODAOMAIN","server.txt");
								}
								else
								{
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok13]","server.txt");
									logArray(result);
									if(logarr.length()>0)
									{
										updatelog(0);
									}
								}			    	    
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}										 
						}  
					}, new Response.ErrorListener() {  
						@Override  
						public void onErrorResponse(VolleyError error) {  
							result = "����ʧ�ܣ�";
							tomain13.what=SETFAILMAIN;
				    	    mainhand.sendMessage(tomain13); // ������Ϣ
				    	    ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail13]SETFAILMAIN"+result,"server.txt");
						}  
					}) 
					{  
						@Override  
						protected Map<String, String> getParams() throws AuthFailureError {  
							//3.���params
							Map<String, String> map = new HashMap<String, String>();  
							map.put("Token", Tok);  
							map.put("LAST_EDIT_TIME", LAST_EDIT_TIME13);
							map.put("VMC_NO", vmc_no);
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+map.toString(),"server.txt");
							return map;  
					   }  
					}; 	
					//5.������Ϣ�����͵�������
					mQueue.add(stringRequest13);					
					break;
				case SETACCOUNTCHILD://��ȡ֧����΢����Ϣ					
					String target14 = httpStr+"/api/selectAccount";	//Ҫ�ύ��Ŀ���ַ
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread ��ȡ֧����΢����Ϣ["+target14+"]","server.txt");
					final String LAST_EDIT_TIME14=msg.obj.toString();
					//�½�Volley 
					mQueue = getRequestQueue();
					//�����̷߳�����Ϣ
					final Message tomain14=mainhand.obtainMessage();
					tomain14.what=SETNONE;
					//4.׼��������Ϣ����
					StringRequest stringRequest14 = new StringRequest(Method.POST, target14,  new Response.Listener<String>() {  
						@Override  
						public void onResponse(String response) {  
						   
						    //�������ɹ�
							result = response;	//��ȡ���ص��ַ���
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
							JSONObject object;
							try {
								object = new JSONObject(result);
								int errType =  object.getInt("Error");
								//�����й���
								if(errType>0)
								{
									tomain14.what=SETERRFAILACCOUNTMAIN;
									tomain14.obj=object.getString("Message");							   	    
									mainhand.sendMessage(tomain14); // ������Ϣ
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail14]SETERRFAILHUODAOMAIN","server.txt");
								}
								else
								{
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok14]","server.txt");
									AccountArray(result);
									if(Accountarr.length()>0)
									{
										updateAccount(0);
									}
								}			    	    
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}										 
						}  
					}, new Response.ErrorListener() {  
						@Override  
						public void onErrorResponse(VolleyError error) {  
							result = "����ʧ�ܣ�";
							tomain14.what=SETFAILMAIN;
				    	    mainhand.sendMessage(tomain14); // ������Ϣ
				    	    ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail14]SETFAILMAIN"+result,"server.txt");
						}  
					}) 
					{  
						@Override  
						protected Map<String, String> getParams() throws AuthFailureError {  
							//3.���params
							Map<String, String> map = new HashMap<String, String>();  
							map.put("Token", Tok);  
							map.put("LAST_EDIT_TIME", LAST_EDIT_TIME14);
							map.put("vmc_no",vmc_no);
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+map.toString(),"server.txt");
							return map;  
					   }  
					}; 	
					//5.������Ϣ�����͵�������
					mQueue.add(stringRequest14);					
					break;
				case SETADVCHILD://��ȡ�����Ϣ
					String target15 = httpStr+"/api/advInfo";	//Ҫ�ύ��Ŀ���ַ
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread ��ȡ�����Ϣ["+Thread.currentThread().getId()+"]="+target15,"server.txt");
					final String LAST_EDIT_TIME15=msg.obj.toString();
					//�½�Volley 
					mQueue = getRequestQueue();
					//�����̷߳�����Ϣ
					final Message tomain15=mainhand.obtainMessage();
					tomain15.what=SETNONE;
					//4.׼��������Ϣ����
					StringRequest stringRequest15 = new StringRequest(Method.POST, target15,  new Response.Listener<String>() {  
						@Override  
						public void onResponse(String response) {  
						   
						    //�������ɹ�
							result = response;	//��ȡ���ص��ַ���
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
							JSONObject object;
							try {
								object = new JSONObject(result);
								int errType =  object.getInt("Error");
								//�����й���
								if(errType>0)
								{
									tomain15.what=SETERRFAILADVMAIN;
									tomain15.obj=object.getString("Message");							   	    
									mainhand.sendMessage(tomain15); // ������Ϣ
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail15]SETERRFAILHUODAOMAIN","server.txt");
								}
								else
								{
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok15]","server.txt");
									advArray(result);
									if(advarr.length()>0)
									{
										updateadv(0);
									}
								}			    	    
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}										 
						}  
					}, new Response.ErrorListener() {  
						@Override  
						public void onErrorResponse(VolleyError error) {  
							result = "����ʧ�ܣ�";
							tomain15.what=SETFAILMAIN;
				    	    mainhand.sendMessage(tomain15); // ������Ϣ
				    	    ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail15]SETFAILMAIN"+result,"server.txt");
						}  
					}) 
					{  
						@Override  
						protected Map<String, String> getParams() throws AuthFailureError {  
							//3.���params
							Map<String, String> map = new HashMap<String, String>(); 
							map.put("vmc_no",vmc_no);
							map.put("Token", Tok);  
							map.put("LAST_EDIT_TIME", LAST_EDIT_TIME15);							
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+map.toString(),"server.txt");
							return map;  
					   }  
					}; 	
					//5.������Ϣ�����͵�������
					mQueue.add(stringRequest15);					
					break;
				case SETCLIENTCHILD://��ȡ�豸��Ϣ
					String target16 = httpStr+"/api/selectClientSetting";	//Ҫ�ύ��Ŀ���ַ
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread ��ȡ�豸��Ϣ["+Thread.currentThread().getId()+"]="+target16,"server.txt");
					final String LAST_EDIT_TIME16=msg.obj.toString();
					//�½�Volley 
					mQueue = getRequestQueue();
					//�����̷߳�����Ϣ
					final Message tomain16=mainhand.obtainMessage();
					tomain16.what=SETNONE;
					//4.׼��������Ϣ����
					StringRequest stringRequest16 = new StringRequest(Method.POST, target16,  new Response.Listener<String>() {  
						@Override  
						public void onResponse(String response) {  
						   
						    //�������ɹ�
							result = response;	//��ȡ���ص��ַ���
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
							JSONObject object;
							try {
								object = new JSONObject(result);
								int errType =  object.getInt("Error");
								//�����й���
								if(errType>0)
								{
									tomain16.what=SETERRFAILCLIENTMAIN;
									tomain16.obj=object.getString("Message");							   	    
									mainhand.sendMessage(tomain16); // ������Ϣ
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail16]SETERRFAILHUODAOMAIN","server.txt");
								}
								else
								{
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok16]","server.txt");
									clientArray(result);
									if(clientarr.length()>0)
									{
										updateclient(0);
									}
								}			    	    
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}										 
						}  
					}, new Response.ErrorListener() {  
						@Override  
						public void onErrorResponse(VolleyError error) {  
							result = "����ʧ�ܣ�";
							tomain16.what=SETFAILMAIN;
				    	    mainhand.sendMessage(tomain16); // ������Ϣ
				    	    ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail16]SETFAILMAIN"+result,"server.txt");
						}  
					}) 
					{  
						@Override  
						protected Map<String, String> getParams() throws AuthFailureError {  
							//3.���params
							Map<String, String> map = new HashMap<String, String>();  
							map.put("Token", Tok);  
							map.put("LAST_EDIT_TIME", LAST_EDIT_TIME16);	
							map.put("VMC_NO",vmc_no);
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+map.toString(),"server.txt");
							return map;  
					   }  
					}; 	
					//5.������Ϣ�����͵�������
					mQueue.add(stringRequest16);					
					break;
				case SETEVENTINFOCHILD://��ȡ���Ϣ
					String target18 = httpStr+"/api/eventInfo";	//Ҫ�ύ��Ŀ���ַ
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread ��ȡ���Ϣ["+Thread.currentThread().getId()+"]="+target18,"server.txt");
					final String LAST_EDIT_TIME18=msg.obj.toString();
					//�½�Volley 
					mQueue = getRequestQueue();
					//�����̷߳�����Ϣ
					final Message tomain18=mainhand.obtainMessage();
					tomain18.what=SETNONE;
					//4.׼��������Ϣ����
					StringRequest stringRequest18 = new StringRequest(Method.POST, target18,  new Response.Listener<String>() {  
						@Override  
						public void onResponse(String response) {  
						   
						    //�������ɹ�
							result = response;	//��ȡ���ص��ַ���
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
							JSONObject object;
							try {
								object = new JSONObject(result);
								int errType =  object.getInt("Error");
								//�����й���
								if(errType>0)
								{
									tomain18.what=SETERRFAILEVENTINFOMAIN;
									tomain18.obj=object.getString("Message");							   	    
									mainhand.sendMessage(tomain18); // ������Ϣ
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail18]SETERRFAILEVENTINFOMAIN","server.txt");
								}
								else
								{
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok18]","server.txt");
									eventArray(result);
									if(eventarr.length()>0)
									{
										updateevent(0);
									}									
								}			    	    
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}										 
						}  
					}, new Response.ErrorListener() {  
						@Override  
						public void onErrorResponse(VolleyError error) {  
							result = "����ʧ�ܣ�";
							tomain18.what=SETFAILMAIN;
				    	    mainhand.sendMessage(tomain18); // ������Ϣ
				    	    ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail18]SETFAILMAIN"+result,"server.txt");
						}  
					}) 
					{  
						@Override  
						protected Map<String, String> getParams() throws AuthFailureError {  
							//3.���params
							Map<String, String> map = new HashMap<String, String>();  
							map.put("Token", Tok);  
							map.put("LAST_EDIT_TIME", LAST_EDIT_TIME18);	
							map.put("vmc_no",vmc_no);
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+map.toString(),"server.txt");
							return map;  
					   }  
					}; 	
					//5.������Ϣ�����͵�������
					mQueue.add(stringRequest18);					
					break;	
				case SETDEMOINFOCHILD://��ȡ������ʾ��Ϣ
					String target19 = httpStr+"/api/demoInfo";	//Ҫ�ύ��Ŀ���ַ
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread ��ȡ������ʾ��Ϣ["+Thread.currentThread().getId()+"]="+target19,"server.txt");
					final String LAST_EDIT_TIME19=msg.obj.toString();
					//�½�Volley 
					mQueue = getRequestQueue();
					//�����̷߳�����Ϣ
					final Message tomain19=mainhand.obtainMessage();
					tomain19.what=SETNONE;
					//4.׼��������Ϣ����
					StringRequest stringRequest19 = new StringRequest(Method.POST, target19,  new Response.Listener<String>() {  
						@Override  
						public void onResponse(String response) {  
						   
						    //�������ɹ�
							result = response;	//��ȡ���ص��ַ���
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
							JSONObject object;
							try {
								object = new JSONObject(result);
								int errType =  object.getInt("Error");
								//�����й���
								if(errType>0)
								{
									tomain19.what=SETERRFAILDEMOINFOMAIN;
									tomain19.obj=object.getString("Message");							   	    
									mainhand.sendMessage(tomain19); // ������Ϣ
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail19]SETERRFAILEVENTINFOMAIN","server.txt");
								}
								else
								{
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok19]","server.txt");
									demoArray(result);
									if(demoarr.length()>0)
									{
										updatedemo(0);
									}
								}			    	    
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}										 
						}  
					}, new Response.ErrorListener() {  
						@Override  
						public void onErrorResponse(VolleyError error) {  
							result = "����ʧ�ܣ�";
							tomain19.what=SETFAILMAIN;
				    	    mainhand.sendMessage(tomain19); // ������Ϣ
				    	    ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail19]SETFAILMAIN"+result,"server.txt");
						}  
					}) 
					{  
						@Override  
						protected Map<String, String> getParams() throws AuthFailureError {  
							//3.���params
							Map<String, String> map = new HashMap<String, String>();  
							map.put("Token", Tok);  
							map.put("LAST_EDIT_TIME", LAST_EDIT_TIME19);
							map.put("vmc_no",vmc_no);
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+map.toString(),"server.txt");
							return map;  
					   }  
					}; 	
					//5.������Ϣ�����͵�������
					mQueue.add(stringRequest19);					
					break;	
				//ȡ����Ƚ����⣬�����������Ƶ�����
				case SETPICKUPCHILD://��ȡȡ����
					String target17 = httpStr+"/api/getPickupCodeStatus";	//Ҫ�ύ��Ŀ���ַ
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Thread ��ȡȡ������Ϣ["+Thread.currentThread().getId()+"]="+target17,"server.txt");
					final String PICKUP_CODE=msg.obj.toString();
					final String LAST_EDIT_TIME17=ToolClass.getLasttime();
					//�½�Volley 
					mQueue = getRequestQueue();
					//�����̷߳�����Ϣ
					final Message tomain17=mainhand.obtainMessage();
					tomain17.what=SETNONE;
					//4.׼��������Ϣ����
					StringRequest stringRequest17 = new StringRequest(Method.POST, target17,  new Response.Listener<String>() {  
						@Override  
						public void onResponse(String response) {  
						   
						    //�������ɹ�
							result = response;	//��ȡ���ص��ַ���
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
							JSONObject object;
							try {
								object = new JSONObject(result);
								int errType =  object.getInt("Error");
								//�����й���
								if(errType>0)
								{
									tomain17.what=SETERRFAILPICKUPMAIN;
									tomain17.obj=object.getString("Message");							   	    
									mainhand.sendMessage(tomain17); // ������Ϣ
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail17]SETERRFAILHUODAOMAIN","server.txt");
								}
								else
								{
									ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok17]","server.txt");
									pickupArray(result);
									if(pickuparr.length()>0)
									{
										updatepickup(0,PICKUP_CODE);
									}
								}			    	    
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}										 
						}  
					}, new Response.ErrorListener() {  
						@Override  
						public void onErrorResponse(VolleyError error) {  
							result = "����ʧ�ܣ�";
							tomain17.what=SETERRFAILPICKUPMAIN;
				    	    mainhand.sendMessage(tomain17); // ������Ϣ
				    	    ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail17]SETFAILMAIN"+result,"server.txt");
						}  
					}) 
					{  
						@Override  
						protected Map<String, String> getParams() throws AuthFailureError {  
							//3.���params
							Map<String, String> map = new HashMap<String, String>();  
							map.put("Token", Tok);  
							map.put("LAST_EDIT_TIME", LAST_EDIT_TIME17);	
							map.put("PICKUP_CODE", PICKUP_CODE);
							map.put("vmc_no",vmc_no);
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1="+map.toString(),"server.txt");
							return map;  
					   }  
					}; 	
					//5.������Ϣ�����͵�������
					mQueue.add(stringRequest17);					
					break;	
				default:
					break;
				}
			}
			
		};
		Looper.loop();//�û��Լ�������࣬�����߳���Ҫ�Լ�׼��loop
	}
	
	//==============
	//==��Ʒ�������ģ��
	//==============
	JSONArray classarr=null;
	JSONArray zhuheclassArray=null;
	JSONObject zhuheclassjson = null; 
	int classint=0;
	//�ֽ������Ϣ
	private void classArray(String classrst) throws JSONException
	{
		JSONObject jsonObject = new JSONObject(classrst); 
		classarr=jsonObject.getJSONArray("List");
		classint=0;
		zhuheclassArray=new JSONArray();
		zhuheclassjson = new JSONObject(); 
		if(classarr.length()==0)
		{
			//�����̷߳�����Ϣ
			Message tomain=mainhand.obtainMessage();
			tomain.what=SETCLASSMAIN;
			tomain.obj=zhuheclassjson.toString();
			mainhand.sendMessage(tomain); // ������Ϣ	
		}
	}
	
	//���·����ͼƬ��Ϣ
	private String updateclass(int i) throws JSONException
	{
		final JSONObject object2=classarr.getJSONObject(i);
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","���·����ͼƬ="+object2.toString(),"server.txt");										
		final JSONObject zhuheobj=object2;
		//��һ������ȡͼƬ����ATTID
		final String CLS_URL=object2.getString("CLS_URL");
		String ATT_ID="";
		if(CLS_URL.equals("null")!=true)
		{
			String a[] = CLS_URL.split("/");  
			ATT_ID=a[a.length-1];
			ATT_ID=ATT_ID.substring(0,ATT_ID.lastIndexOf("."));
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","ͼƬATT_ID="+ATT_ID,"server.txt");										
			zhuheobj.put("AttImg", ToolClass.getImgFile(ATT_ID));
		}
		else
		{
			zhuheobj.put("AttImg", "");
		}
		
		
		try
		{	
			if(ATT_ID.equals("")==true)
			{
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","����["+object2.getString("CLASS_NAME")+"]��ͼƬ","server.txt");
			}
			else
			{
				if(ToolClass.isImgFile(ATT_ID))
				{
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","����["+object2.getString("CLASS_NAME")+"]ͼƬ�Ѵ���","server.txt");
				}
				else 
				{
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","����["+object2.getString("CLASS_NAME")+"]ͼƬ,����ͼƬ...","server.txt");
					//�ڶ���.׼������	
					String serip=httpStr.substring(0,httpStr.lastIndexOf("/shj"));
					String url= serip+CLS_URL;	//Ҫ�ύ��Ŀ���ַ
					final String ATTIDS=ATT_ID;
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","ATTID=["+ATTIDS+"]url["+url+"]","server.txt");
					ImageRequest imageRequest = new ImageRequest(  
							url,  
					        new Response.Listener<Bitmap>() {  
					            @Override  
					            public void onResponse(Bitmap response) {  
					            	ToolClass.saveBitmaptofile(response,ATTIDS);
					            	try {
										ToolClass.Log(ToolClass.INFO,"EV_SERVER","����["+object2.getString("CLASS_NAME")+"]ͼƬ,����ͼƬ���","server.txt");
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
					            }  
					        }, 0, 0, Config.RGB_565, new Response.ErrorListener() {  
					            @Override  
					            public void onErrorResponse(VolleyError error) {  
									result = "����ʧ�ܣ�";
									try {
										ToolClass.Log(ToolClass.INFO,"EV_SERVER","����["+object2.getString("CLASS_NAME")+"]ͼƬ,����ͼƬʧ��","server.txt");
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
					            }  
					        });
					mQueue.add(imageRequest); 
				}
				
			}
		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec2=[fail10-1]","server.txt");
		}
		
		//����������ͼƬ���ֱ��浽json��		
		zhuheclassArray.put(zhuheobj);
		
		
		//���Ĳ���������һ��������Ϣ
		classint++;
		if(classint<classarr.length())
		{
			try {
				updateclass(classint);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			try {
				zhuheclassjson.put("List", zhuheclassArray);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","reczhuhe="+zhuheclassjson.toString(),"server.txt");

			//�ϴ���server
			//�����̷߳�����Ϣ
			Message tomain4=mainhand.obtainMessage();
			tomain4.what=SETCLASSMAIN;
			tomain4.obj=zhuheclassjson.toString();
			mainhand.sendMessage(tomain4); // ������Ϣ
		}		
		return "";
	}
	
	//=======================
	//==��Ʒ�����Ӧ����Ʒ��Ϣ����ģ��
	//=======================
	JSONArray classjoinarr=null;
	JSONArray zhuheclassjoinArray=null;
	JSONObject zhuheclassjoinjson = null; 
	int classjoinint=0;
	//�ֽ������Ϣ
	private void classjoinArray(String classjoinrst) throws JSONException
	{
		JSONObject jsonObject = new JSONObject(classjoinrst); 
		classjoinarr=jsonObject.getJSONArray("List");
		classjoinint=0;
		zhuheclassjoinArray=new JSONArray();
		zhuheclassjoinjson = new JSONObject(); 
		if(classjoinarr.length()==0)
		{
			//�����̷߳�����Ϣ
			Message tomain=mainhand.obtainMessage();
			tomain.what=SETJOINCLASSMAIN;
			tomain.obj=zhuheclassjoinjson.toString();
			mainhand.sendMessage(tomain); // ������Ϣ	
		}
	}
	
	//���·����ͼƬ��Ϣ
	private String updateclassjoin(int i) throws JSONException
	{
		final JSONObject object2=classjoinarr.getJSONObject(i);
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","���·����Ӧ����Ʒ��Ϣ="+object2.toString(),"server.txt");										
		final JSONObject zhuheobj=new JSONObject();
		//��һ������ȡͼƬ���õ���Ʒid
		int IS_DELETE=object2.getInt("IS_DELETE");
		if(IS_DELETE==0)
		{
			zhuheobj.put("PRODUCT_NO", object2.getString("PRODUCT_NO"));
			zhuheobj.put("CLASS_CODE", object2.getString("CLASS_CODE"));
			//����������ͼƬ���ֱ��浽json��		
			zhuheclassjoinArray.put(zhuheobj);
		}
				
		//���Ĳ���������һ��������Ϣ
		classjoinint++;
		if(classjoinint<classjoinarr.length())
		{
			try {
				updateclassjoin(classjoinint);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			try {
				zhuheclassjoinjson.put("List", zhuheclassjoinArray);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","reczhuhe="+zhuheclassjoinjson.toString(),"server.txt");

			//�ϴ���server
			//�����̷߳�����Ϣ
			Message tomain4=mainhand.obtainMessage();
			tomain4.what=SETJOINCLASSMAIN;
			tomain4.obj=zhuheclassjoinjson.toString();
			mainhand.sendMessage(tomain4); // ������Ϣ
		}		
		return "";
	}
	
	
	//==========
	//==��Ʒ����ģ��
	//==========
	JSONArray productarr=null;
	JSONArray zhuheproductArray=null;
	JSONObject zhuheproductjson = null; 
	int productint=0;
	//�ֽ���Ʒ��Ϣ
	private void productArray(String classrst) throws JSONException
	{
		JSONObject jsonObject = new JSONObject(classrst);
		if(ToolClass.getServerVer()==0)//�ɵĺ�̨
		{
			productarr=jsonObject.getJSONArray("ProductList");
			productint=0;
			zhuheproductArray=new JSONArray();
			zhuheproductjson = new JSONObject(); 
			if(productarr.length()==0)
			{
				//�����̷߳�����Ϣ
				Message tomain=mainhand.obtainMessage();
				tomain.what=SETRODUCTMAIN;
				tomain.obj=zhuheproductjson.toString();
				mainhand.sendMessage(tomain); // ������Ϣ	
			}
		}
		else if(ToolClass.getServerVer()==1)//һ�ں�̨
		{
			productarr=jsonObject.getJSONArray("List");
			productint=0;
			zhuheproductArray=new JSONArray();
			zhuheproductjson = new JSONObject(); 
			if(productarr.length()==0)
			{
				//�����̷߳�����Ϣ
				Message tomain=mainhand.obtainMessage();
				tomain.what=SETRODUCTMAIN;
				tomain.obj=zhuheproductjson.toString();
				mainhand.sendMessage(tomain); // ������Ϣ	
			}
		}
	}
	//������Ʒ��ͼƬ��Ϣ
	private String updateproduct(int i) throws JSONException
	{
		final JSONObject object2=productarr.getJSONObject(i);
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","������Ʒ��ͼƬ="+object2.toString(),"server.txt");										
		final JSONObject zhuheobj=object2;
		String product_TXT="";
		if(ToolClass.getServerVer()==0)//�ɵĺ�̨
		{
			//��һ��.��ȡ��ƷͼƬ����
			String target6 = httpStr+"/api/productImage";	//Ҫ�ύ��Ŀ���ַ
			JSONObject json=new JSONObject();
			try {
				json.put("VmcNo", vmc_no);
				json.put("attId", object2.getString("att_batch_id"));				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}					
			final String param6=json.toString();		
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send2="+param6.toString(),"server.txt");
			//�½�Volley 
			mQueue = getRequestQueue();
			//4.׼��������Ϣ����
			StringRequest stringRequest6 = new StringRequest(Method.POST, target6,  new Response.Listener<String>() {  
				@Override  
				public void onResponse(String response) {  				   
					//�������ɹ�
					result = response;	//��ȡ���ص��ַ���
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1[ok10]="+result,"server.txt");
					try
					{
						//�ڶ�������ȡͼƬ����ATTID
						JSONObject jsonObject3 = new JSONObject(result); 
						JSONArray arr3=null;
						if(ToolClass.getServerVer()==0)//�ɵĺ�̨
						{
							arr3=jsonObject3.getJSONArray("ProductImageList");
						}
						else if(ToolClass.getServerVer()==1)//һ�ں�̨
						{
							arr3=jsonObject3.getJSONArray("List");
						}
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec2[ok10]1","server.txt");
						JSONObject object3=arr3.getJSONObject(0);
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec2[ok10]2","server.txt");
						final String ATT_ID=object3.getString("ATT_ID");
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec2[ok10]3","server.txt");
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec2[ok10]ATT_ID="+ATT_ID,"server.txt");
						//ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec2[ok10]zhuheobj="+zhuheobj+"zhuheproductArray="+zhuheproductArray,"server.txt");
						//����������ͼƬ���ֱ��浽json��
						zhuheobj.put("AttImg", ToolClass.getImgFile(ATT_ID));
						zhuheproductArray.put(zhuheobj);
						if(ToolClass.isEmptynull(ATT_ID))
						{
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","��Ʒ["+object2.getString("product_Name")+"]��ͼƬ","server.txt");
						}
						else
						{
							if(ToolClass.isImgFile(ATT_ID))
							{
								ToolClass.Log(ToolClass.INFO,"EV_SERVER","��Ʒ["+object2.getString("product_Name")+"]ͼƬ�Ѵ���","server.txt");
							}
							else 
							{
								ToolClass.Log(ToolClass.INFO,"EV_SERVER","��Ʒ["+object2.getString("product_Name")+"]ͼƬ,����ͼƬ...","server.txt");
								//���Ĳ�.׼������	
								String url= httpStr+"/topic/getFile/"+ATT_ID + ".jpg";	//Ҫ�ύ��Ŀ���ַ
								ImageRequest imageRequest = new ImageRequest(  
										url,  
								        new Response.Listener<Bitmap>() {  
								            @Override  
								            public void onResponse(Bitmap response) {  
								            	ToolClass.saveBitmaptofile(response,ATT_ID);
								            	try {
													ToolClass.Log(ToolClass.INFO,"EV_SERVER","��Ʒ["+object2.getString("product_Name")+"]ͼƬ,����ͼƬ���","server.txt");
												} catch (JSONException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
								            }  
								        }, 0, 0, Config.RGB_565, new Response.ErrorListener() {  
								            @Override  
								            public void onErrorResponse(VolleyError error) {  
												result = "����ʧ�ܣ�";
												try {
													ToolClass.Log(ToolClass.INFO,"EV_SERVER","��Ʒ["+object2.getString("product_Name")+"]ͼƬ,����ͼƬʧ��","server.txt");
												} catch (JSONException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
								            }  
								        });
								mQueue.add(imageRequest); 
							}
							
						}
					}
					catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec2=[fail10-1]","server.txt");
							try {
								zhuheobj.put("AttImg", "");
								zhuheproductArray.put(zhuheobj);
							} catch (JSONException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}	
						}
					
					
					//���岽��������һ����Ʒ��Ϣ
					productint++;
					if(productint<productarr.length())
					{
						try {
							updateproduct(productint);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else
					{
						try {
							zhuheproductjson.put("ProductList", zhuheproductArray);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","reczhuhe="+zhuheproductjson.toString(),"server.txt");
	
						//�ϴ���server
						//�����̷߳�����Ϣ
						Message tomain4=mainhand.obtainMessage();
						tomain4.what=SETRODUCTMAIN;
						tomain4.obj=zhuheproductjson.toString();
						mainhand.sendMessage(tomain4); // ������Ϣ
					}
				}  
			}, new Response.ErrorListener() {  
				@Override  
				public void onErrorResponse(VolleyError error) {  
					result = "����ʧ�ܣ�";
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec2=[fail10]"+result,"server.txt");
					//����������ͼƬ���ֱ��浽json��
					try {
						zhuheobj.put("AttImg", "");
						zhuheproductArray.put(zhuheobj);					
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
					
					//���岽��������һ����Ʒ��Ϣ
					productint++;
					if(productint<productarr.length())
					{
						try {
							updateproduct(productint);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else
					{
						try {
							zhuheproductjson.put("ProductList", zhuheproductArray);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","reczhuhe="+zhuheproductjson.toString(),"server.txt");
	
						//�ϴ���server
						//�����̷߳�����Ϣ
						Message tomain4=mainhand.obtainMessage();
						tomain4.what=SETRODUCTMAIN;
						tomain4.obj=zhuheproductjson.toString();
						mainhand.sendMessage(tomain4); // ������Ϣ
					}
				}  
			}) 
			{  
				@Override  
				protected Map<String, String> getParams() throws AuthFailureError {  
					//3.���params
					Map<String, String> map = new HashMap<String, String>();  
					map.put("Token", Tok);  
					map.put("param", param6);
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send3="+map.toString(),"server.txt");
					return map;  
			   }  
			}; 	
			//5.������Ϣ�����͵�������
			mQueue.add(stringRequest6);	
		}
		else if(ToolClass.getServerVer()==1)//һ�ں�̨
		{
			//��һ������ȡͼƬ����ATTID
			final String CLS_URL=object2.getString("product_Images");
			String ATT_ID="";
			if(CLS_URL.equals("")!=true)
			{
				String a[] = CLS_URL.split("/");  
				ATT_ID=a[a.length-1];
				ATT_ID=ATT_ID.substring(0,ATT_ID.lastIndexOf("."));
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","ͼƬATT_ID="+ATT_ID,"server.txt");										
				zhuheobj.put("AttImg", ToolClass.getImgFile(ATT_ID));
			}
			else
			{
				zhuheobj.put("AttImg", "");
			}
			
			//��1.1������ȡ��ϸ��Ϣ�����ͼƬ
			product_TXT=object2.getString("product_TXT");
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","oldproduct_TXT="+product_TXT,"server.txt");										
			if(product_TXT.equals("")!=true)
			{
			    Vector<String> imageList = ToolClass.parseImageURL(product_TXT);
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","imageList="+imageList,"server.txt");										
				if(imageList!=null)
				{					 
					 for(int v = 0;v < imageList.size();v++)
					 {
						 ToolClass.Log(ToolClass.INFO,"EV_SERVER","imageͼƬ����="+imageList.get(v).indexOf("/shj"),"server.txt");										
						 //ֻ��ͼƬ���Ӳ���׼�����ص�����
						 if(imageList.get(v).indexOf("/shj")>-1)
						 {
							 productimage.add(imageList.get(v));
							 product_TXT=ToolClass.repImageURL(product_TXT,imageList.get(v));
						 }
					 }	
					 ToolClass.Log(ToolClass.INFO,"EV_SERVER","newproduct_TXT="+product_TXT,"server.txt");										
				}				
			}
			zhuheobj.put("product_TXT", product_TXT);
			
			try
			{	
				if(ATT_ID.equals("")==true)
				{
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","��Ʒ["+object2.getString("product_Name")+"]��ͼƬ","server.txt");
				}
				else
				{
					if(ToolClass.isImgFile(ATT_ID))
					{
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","��Ʒ["+object2.getString("product_Name")+"]ͼƬ�Ѵ���","server.txt");
					}
					else 
					{
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","��Ʒ["+object2.getString("product_Name")+"]ͼƬ,����ͼƬ...","server.txt");
						//�ڶ���.׼������	
						String serip=httpStr.substring(0,httpStr.lastIndexOf("/shj"));
						String url= serip+CLS_URL;	//Ҫ�ύ��Ŀ���ַ
						final String ATTIDS=ATT_ID;
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","ATTID=["+ATTIDS+"]url["+url+"]","server.txt");
						ImageRequest imageRequest = new ImageRequest(  
								url,  
								new Response.Listener<Bitmap>() {  
									@Override  
									public void onResponse(Bitmap response) {  
										ToolClass.saveBitmaptofile(response,ATTIDS);
										try {
											ToolClass.Log(ToolClass.INFO,"EV_SERVER","��Ʒ["+object2.getString("product_Name")+"]ͼƬ,����ͼƬ���","server.txt");
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}  
								}, 0, 0, Config.RGB_565, new Response.ErrorListener() {  
									@Override  
									public void onErrorResponse(VolleyError error) {  
										result = "����ʧ�ܣ�";
										try {
											ToolClass.Log(ToolClass.INFO,"EV_SERVER","��Ʒ["+object2.getString("product_Name")+"]ͼƬ,����ͼƬʧ��","server.txt");
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}  
								});
						mQueue.add(imageRequest); 
					}
					
				}
			}
			catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec2=[fail10-1]","server.txt");
			}
			
			//����������ͼƬ���ֱ��浽json��		
			zhuheproductArray.put(zhuheobj);
			
			
			//���岽��������һ����Ʒ��Ϣ
			productint++;
			if(productint<productarr.length())
			{
				try {
					updateproduct(productint);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				try {
					zhuheproductjson.put("ProductList", zhuheproductArray);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","recproductimage="+productimage.toString(),"server.txt");
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","reczhuhe="+zhuheproductjson.toString(),"server.txt");

				//�ϴ���server
				//�����̷߳�����Ϣ
				Message tomain4=mainhand.obtainMessage();
				tomain4.what=SETRODUCTMAIN;
				tomain4.obj=zhuheproductjson.toString();
				mainhand.sendMessage(tomain4); // ������Ϣ
			}
		}
		return "";
	}
		
	//====================
	//==��Ʒ��ϸ�б���ͼƬ��Ϣģ��
	//====================
	JSONArray zhuheproductimageArray=null;
	JSONObject zhuheproductimagejson = null; 
	int productimageint=0;
	//�ֽ���Ʒ��Ϣ
	private void productimageArray() throws Exception
	{
		productimageint=0;
		zhuheproductimageArray=new JSONArray();
		zhuheproductimagejson = new JSONObject(); 
		if(productimage.size()==0)
		{
			//�����̷߳�����Ϣ
			Message tomain=mainhand.obtainMessage();
			tomain.what=SETRODUCTIMAGEMAIN;
			tomain.obj=zhuheproductimagejson.toString();
			mainhand.sendMessage(tomain); // ������Ϣ	
		}
	}
	//������Ʒ��ͼƬ��Ϣ
	private String updateproductimage(int i) throws JSONException
	{
		final String object2=productimage.get(i);
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","��Ʒ��ϸ��Ϣ��Ʒ��ͼƬ="+object2.toString(),"server.txt");										
				
		//��һ������ȡͼƬ����ATTID
		String ATT_ID="";
		String a[] = object2.split("/");  
		ATT_ID=a[a.length-1];
		ATT_ID=ATT_ID.substring(0,ATT_ID.lastIndexOf("."));
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","��Ʒ��ϸ��ϢͼƬATT_ID="+ATT_ID,"server.txt");	

		try
		{	
			if(ATT_ID.equals("")==true)
			{
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","��Ʒ��ϸ��Ϣ["+ATT_ID+"]��ͼƬ","server.txt");
			}
			else
			{
				if(ToolClass.isImgFile(ATT_ID))
				{
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","��Ʒ��ϸ��Ϣ["+ATT_ID+"]ͼƬ�Ѵ���","server.txt");
				}
				else 
				{
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","��Ʒ��ϸ��Ϣ["+ATT_ID+"]ͼƬ,����ͼƬ...","server.txt");
					//�ڶ���.׼������	
					String serip=httpStr.substring(0,httpStr.lastIndexOf("/shj"));
					String url= serip+object2;	//Ҫ�ύ��Ŀ���ַ
					final String ATTIDS=ATT_ID;
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","ATTID=["+ATTIDS+"]url["+url+"]","server.txt");
					ImageRequest imageRequest = new ImageRequest(  
							url,  
							new Response.Listener<Bitmap>() {  
								@Override  
								public void onResponse(Bitmap response) {  
									ToolClass.saveBitproductmaptofile(response,ATTIDS);
									try {
										ToolClass.Log(ToolClass.INFO,"EV_SERVER","��Ʒ��ϸ��Ϣ["+ATTIDS+"]ͼƬ,����ͼƬ���","server.txt");
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}  
							}, 0, 0, Config.RGB_565, new Response.ErrorListener() {  
								@Override  
								public void onErrorResponse(VolleyError error) {  
									result = "����ʧ�ܣ�";
									try {
										ToolClass.Log(ToolClass.INFO,"EV_SERVER","��Ʒ��ϸ��Ϣ["+ATTIDS+"]ͼƬ,����ͼƬʧ��","server.txt");
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}  
							});
					mQueue.add(imageRequest); 
				}
				
			}
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec2=[fail10-1]","server.txt");
		}

		//���岽��������һ����Ʒ��Ϣ
		productimageint++;
		if(productimageint<productimage.size())
		{
			try {
				updateproductimage(productimageint);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			productimage.clear();
			//�ϴ���server
			//�����̷߳�����Ϣ
			Message tomain4=mainhand.obtainMessage();
			tomain4.what=SETRODUCTIMAGEMAIN;
			tomain4.obj=zhuheproductimagejson.toString();
			mainhand.sendMessage(tomain4); // ������Ϣ
		}		
		return "";
	}
	//==========
	//==���׼�¼ģ��
	//==========
	JSONArray recordarr=null;
	JSONArray retjson=null;	
	int retint=0;
	//�ֽ⽻�׼�¼��Ϣ
	private void recordArray(String classrst) throws JSONException
	{
		retint=0;
		recordarr=new JSONArray(classrst);
		retjson=new JSONArray();
		if(recordarr.length()==0)
		{
			//�����̷߳�����Ϣ
			Message tomain=mainhand.obtainMessage();
			tomain.what=SETRECORDMAIN;
			tomain.obj=retjson;
			mainhand.sendMessage(tomain); // ������Ϣ	
		}
	}
	//���½��׼�¼��Ϣ
	private void updaterecord(int i) 
	{
		final String ret[]={null};
		JSONObject jsonObject = null; 
		String productNo="";
		String shouldPay="";
		String orderNo="";
		final String orderNoVal[]={null};
		int payStatus=0;
		String customerPrice="";
		int payType=0;
		int actualQuantity=0;
		int quantity=0;
		String orderTime="";
		int orderStatus=0;
		String productName="";
		String RefundAmount="";
		String rfd_card_no="";
		//һ�ں�̨
		String NOTE_AMOUNT="",COIN_AMOUNT="",CASH_AMOUNT="",REFUND_NOTE_AMOUNT="",
				REFUND_COIN_AMOUNT="",REFUND_CASH_AMOUNT="",AMOUNT_OWED="",Amount="",
				Cab="",PATH_NO="";
		int Status=0;
		try {
			jsonObject =recordarr.getJSONObject(i);
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send0.1="+jsonObject.toString()
			,"server.txt");
			productNo=jsonObject.getString("productNo");
			shouldPay=jsonObject.getString("shouldPay");
			orderNo=jsonObject.getString("orderNo");
			orderNoVal[0]=orderNo;
			payStatus=jsonObject.getInt("payStatus");
			customerPrice=jsonObject.getString("customerPrice");
			payType=jsonObject.getInt("payType");
			actualQuantity=jsonObject.getInt("actualQuantity");
			quantity=jsonObject.getInt("quantity");
			orderTime=jsonObject.getString("orderTime");
			orderStatus=jsonObject.getInt("orderStatus");
			productName=jsonObject.getString("productName");
			RefundAmount=jsonObject.getString("RefundAmount");
			Status=jsonObject.getInt("Status");
			rfd_card_no=jsonObject.getString("rfd_card_no");
			if(ToolClass.getServerVer()==0)//�ɵĺ�̨
			{
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send0.2=orderNo="+orderNo+"orderTime="+orderTime+"orderStatus="+orderStatus+"payStatus="
					+payStatus+"payType="+payType+"shouldPay="+shouldPay+"RefundAmount="+RefundAmount+"Status="+Status+"productNo="+productNo+"quantity="+quantity+
					"actualQuantity="+actualQuantity+"customerPrice="+customerPrice+"productName="+productName,"server.txt");			
			}
			else if(ToolClass.getServerVer()==1)//һ�ں�̨
			{
				NOTE_AMOUNT=jsonObject.getString("NOTE_AMOUNT");
				COIN_AMOUNT=jsonObject.getString("COIN_AMOUNT");
				CASH_AMOUNT=jsonObject.getString("CASH_AMOUNT");
				REFUND_NOTE_AMOUNT=jsonObject.getString("REFUND_NOTE_AMOUNT");
				REFUND_COIN_AMOUNT=jsonObject.getString("REFUND_COIN_AMOUNT");
				REFUND_CASH_AMOUNT=jsonObject.getString("REFUND_CASH_AMOUNT");
				AMOUNT_OWED=jsonObject.getString("AMOUNT_OWED");
				Amount=jsonObject.getString("Amount");
				Cab=jsonObject.getString("Cab");
				PATH_NO=jsonObject.getString("PATH_NO");
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send0.2=orderNo="+orderNo+"orderTime="+orderTime+"orderStatus="+orderStatus+"payStatus="
						+payStatus+"payType="+payType+"shouldPay="+shouldPay+"RefundAmount="+RefundAmount+"Status="+Status+"productNo="+productNo+"quantity="+quantity+
						"actualQuantity="+actualQuantity+"customerPrice="+customerPrice+"productName="+productName+"rfd_card_no="+rfd_card_no+"NOTE_AMOUNT="+NOTE_AMOUNT+"COIN_AMOUNT="+COIN_AMOUNT
						+"CASH_AMOUNT="+CASH_AMOUNT+"REFUND_NOTE_AMOUNT="+REFUND_NOTE_AMOUNT+"REFUND_COIN_AMOUNT="+REFUND_COIN_AMOUNT+"REFUND_CASH_AMOUNT="+REFUND_CASH_AMOUNT
						+"AMOUNT_OWED="+AMOUNT_OWED+"Amount="+Amount+"Cab="+Cab+"PATH_NO="+PATH_NO,"server.txt");			
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//��װjson��
		//���׼�¼��Ϣ
		String target3 = httpStr+"/api/vmcTransactionRecords";	//Ҫ�ύ��Ŀ���ַ
				
		JSONObject param=null;
		try {
			JSONObject order=new JSONObject();
			order.put("orderID", 121);
			order.put("orderNo", orderNo);//����id
			order.put("orderType", 1);
			order.put("orderTime", orderTime);//֧��ʱ��
			order.put("productCount", quantity);//��������
			order.put("customerName", "test");
			order.put("proCode", "test");
			order.put("cityCode", "test");
			order.put("areaCode", "test");
			order.put("address", "test");
			order.put("addressDetail", "test");
			order.put("consTel", "test");
			order.put("consName", "test");
			order.put("freight", 3);
			order.put("insurance", 2);
			order.put("consPost", "test");
			order.put("shipType", 3);
			order.put("orderStatus", orderStatus);//1δ֧��,2�����ɹ�,3����δ���
			order.put("shipTime", orderTime);//֧��ʱ��
			order.put("isNoFreight", 3);
			order.put("lastUpdateTime", orderTime);//֧��ʱ��
			order.put("orderDesc", "test");
			order.put("shouldPay", 2);
			order.put("integre", 3);
			order.put("sendStatus", 2);
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","order="+order.toString(),"server.txt");
			
			JSONObject orderpay=new JSONObject();
			orderpay.put("payID", 120);
			orderpay.put("orderID", 121);
			orderpay.put("payStatus", payStatus);//0δ����,1���ڸ���,2�������,3����ʧ�ܣ�4����ȡ����5֧�������¼�--������֧��)
			orderpay.put("payType", payType);//0�ֽ�,1֧��������,2����,3֧������ά��,4΢��
			orderpay.put("shouldPay", shouldPay);//���׽��,��2.5Ԫ
			orderpay.put("realPay", 2);
			if(ToolClass.getServerVer()==0)//�ɵĺ�̨
			{
				orderpay.put("RefundAmount", RefundAmount);//�˿���,��1.5Ԫ
			}
			else if(ToolClass.getServerVer()==1)//һ�ں�̨
			{
				orderpay.put("RefundAmount", 0);//�˿���,��1.5Ԫ
			}
			orderpay.put("smallChange", 0);
			orderpay.put("realNote", 1);
			orderpay.put("realCoins", 0);
			orderpay.put("smallNote", 1);
			orderpay.put("smallConis", 0);
			orderpay.put("integre", 0);
			orderpay.put("payDesc", "test");
			orderpay.put("payTime", orderTime);	//֧��ʱ��
			if(ToolClass.getServerVer()==1)//һ�ں�̨
			{
				orderpay.put("NOTE_AMOUNT", NOTE_AMOUNT);
				orderpay.put("COIN_AMOUNT", COIN_AMOUNT);
				orderpay.put("CASH_AMOUNT", CASH_AMOUNT);
				orderpay.put("REFUND_NOTE_AMOUNT", REFUND_NOTE_AMOUNT);
				orderpay.put("REFUND_COIN_AMOUNT", REFUND_COIN_AMOUNT);
				orderpay.put("REFUND_CASH_AMOUNT", REFUND_CASH_AMOUNT);
				orderpay.put("AMOUNT_OWED", AMOUNT_OWED);
				orderpay.put("Status", Status);//0��δ�˿1�������˿2���˿�ɹ���3���˿�ʧ��'
				orderpay.put("ONE_CARD_ID", rfd_card_no);//����
			}			
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","orderpay="+orderpay.toString(),"server.txt");
			
			JSONObject orderrefund=new JSONObject();
			orderrefund.put("RefundId", 122);
			orderrefund.put("orderNo", orderNo);//��Ʒid
			orderrefund.put("Reason", "test");
			if(ToolClass.getServerVer()==0)//�ɵĺ�̨
			{
				orderrefund.put("Amount", 0);
				orderrefund.put("Debt", 0);
			}
			else if(ToolClass.getServerVer()==1)//һ�ں�̨			
			{
				orderrefund.put("Amount", Amount);
				orderrefund.put("Debt", AMOUNT_OWED);
			}
			orderrefund.put("Refund", 0);			
			orderrefund.put("ResultCode", "test");
			orderrefund.put("TradeNo", "test");
			orderrefund.put("Description", "test");
			orderrefund.put("Status", Status);//0��δ�˿1�������˿2���˿�ɹ���3���˿�ʧ��'
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","orderrefund="+orderrefund.toString(),"server.txt");
			
			JSONObject orderproduct=new JSONObject();
			orderproduct.put("opID", 123);
			orderproduct.put("orderID", 121);
			orderproduct.put("productID", 120);
			orderproduct.put("productNo", productNo);//��Ʒ���
			orderproduct.put("productType", "0001");
			orderproduct.put("quantity", quantity);//Ԥ�Ƴ���
			orderproduct.put("actualQuantity", actualQuantity);//ʵ�ʳ���
			orderproduct.put("productPrice", 1);			
			orderproduct.put("customerPrice", customerPrice);//��Ʒ����
			orderproduct.put("productName", productName);//��Ʒ����
			orderproduct.put("moneyAmount", 1);
			orderproduct.put("productIntegre", 1);
			orderproduct.put("IntegreAmount", 1);
			orderproduct.put("firstpurchaseprice", 1);
			if(ToolClass.getServerVer()==1)//һ�ں�̨
			{
				orderproduct.put("Cab", Cab);
				orderproduct.put("PATH_NO", PATH_NO);
			}
			JSONArray orderpro=new JSONArray();
			orderpro.put(orderproduct);
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","orderproduct="+orderpro.toString(),"server.txt");
			
			
			
			JSONObject trans=new JSONObject();
			trans.put("Order", order);
			trans.put("OrderPay", orderpay);
			trans.put("OrderProducts", orderpro);
			trans.put("OrderRefund", orderrefund);			
			JSONArray TRANSACTION=new JSONArray();
			TRANSACTION.put(trans);
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","TRANSACTION="+TRANSACTION.toString(),"server.txt");
			param=new JSONObject();
			param.put("VMC_NO", vmc_no);
			param.put("TOTAL",1);
			param.put("ACTUAL_TOTAL",1);
			param.put("TRANSACTION", TRANSACTION);
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","param="+param.toString(),"server.txt");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final JSONObject paramVal[]={null};
		paramVal[0]=param;
		
		//�����̷߳�����Ϣ
		final Message tomain3=mainhand.obtainMessage();
		//�½�Volley 
		mQueue = getRequestQueue();
		//4.׼��������Ϣ����
		StringRequest stringRequest3 = new StringRequest(Method.POST, target3,  new Response.Listener<String>() {  
			@Override  
			public void onResponse(String response) {  
			   
			  //�������ɹ�
				result = response;	//��ȡ���ص��ַ���
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
				JSONObject object;
				try {
					object = new JSONObject(result);
					int errType =  object.getInt("Error");
					//�����й���
					if(errType>0)
					{
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail7]Records","server.txt");
					}
					else
					{
						ret[0]=orderNoVal[0];						
						JSONObject retj=new JSONObject();
						retj.put("orderno", ret[0]);
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok7]["+retj.toString()+"]","server.txt");
						retjson.put(retj);
					}			
					
				} catch (JSONException e) {
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail7-1]","server.txt");
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//���岽��������һ����¼��Ϣ
				retint++;
				if(retint<recordarr.length())
				{
					updaterecord(retint);
				}
				else
				{					
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec2="+retjson.toString(),"server.txt");
					//�ϴ���server
					//�����̷߳�����Ϣ
					tomain3.what=SETRECORDMAIN;
					tomain3.obj=retjson;
					mainhand.sendMessage(tomain3); // ������Ϣ
				}
			}  
		}, new Response.ErrorListener() {  
			@Override  
			public void onErrorResponse(VolleyError error) {  
				result = "����ʧ�ܣ�";
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail7]"+result,"server.txt");
				//���岽��������һ����¼��Ϣ
				retint++;
				if(retint<recordarr.length())
				{
					updaterecord(retint);
				}
				else
				{						
					//�ϴ���server
					//�����̷߳�����Ϣ
					tomain3.what=SETRECORDMAIN;
					tomain3.obj=retjson;
					mainhand.sendMessage(tomain3); // ������Ϣ
				}
			}  
		}) 
		{  
			@Override  
			protected Map<String, String> getParams() throws AuthFailureError {  
				//3.���params
				Map<String, String> map = new HashMap<String, String>();  
				map.put("Token", Tok);  
				map.put("param", paramVal[0].toString());
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","Records="+map.toString(),"server.txt");
				return map;  
		   }  
		}; 	
		//5.������Ϣ�����͵�������
		mQueue.add(stringRequest3);				
	}
	
	//==========
	//==��������ģ��
	//==========
	JSONArray columnarr=null;
	JSONArray columnjson=null;	
	int columnint=0;
	//�ֽ⽻�׼�¼��Ϣ
	private void columnArray(String classrst) throws JSONException
	{
		columnint=0;
		columnarr=new JSONArray(classrst);
		columnjson=new JSONArray();
		if(columnarr.length()==0)
		{
			//�����̷߳�����Ϣ
			Message tomain=mainhand.obtainMessage();
			tomain.what=SETHUODAOSTATUMAIN;
			tomain.obj=columnjson;
			mainhand.sendMessage(tomain); // ������Ϣ	
		}
	}
	//���»���״̬��Ϣ
	private void updatecolumn(int i) 
	{
		JSONObject jsonObject = null; 
		String pathID="";
		String cabinetNumber="";
		String pathName="";
		final String cabinetNumberVal[]={null};
		final String pathNameVal[]={null};
		String productID="";
		String productNum="";
		String pathCount="";
		String pathStatus="";
		String pathRemaining="";
		String lastedittime="";
		String isdisable="";
		try {
			jsonObject =columnarr.getJSONObject(i);
			pathID=jsonObject.getString("pathID");
			cabinetNumber=jsonObject.getString("cabinetNumber");
			cabinetNumberVal[0]=cabinetNumber;
			pathName=jsonObject.getString("pathName");
			pathNameVal[0]=pathName;
			productID=jsonObject.getString("productID");
			productNum=jsonObject.getString("productNum");
			pathCount=jsonObject.getString("pathCount");
			pathStatus=jsonObject.getString("pathStatus");
			pathRemaining=jsonObject.getString("pathRemaining");
			lastedittime=jsonObject.getString("lastedittime");
			isdisable=jsonObject.getString("isdisable");
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send0.2=pathID="+pathID+"cabinetNumber="+cabinetNumber+"pathName="+pathName+"productID="
				+productID+"productNum="+productNum+"pathCount="+pathCount+"pathStatus="+pathStatus+"pathRemaining="+pathRemaining+"lastedittime="+lastedittime+
				"isdisable="+isdisable,"server.txt");			
			  	
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		//��װjson��
		//���׼�¼��Ϣ
		String target3 = httpStr+"/api/vmcPathStatus";	//Ҫ�ύ��Ŀ���ַ
				
		JSONObject param=null;
		try {
			JSONObject trans=new JSONObject();
			trans.put("pathID", pathID);
			trans.put("cabinetNumber", cabinetNumber);
			trans.put("pathName", pathName);
			trans.put("productID", productID);	
			trans.put("productNum", productNum);	
			trans.put("pathCount", pathCount);	
			trans.put("pathStatus", pathStatus);	
			trans.put("pathRemaining", pathRemaining);
			trans.put("lastedittime", lastedittime);	
			trans.put("isdisable", isdisable);
			JSONArray PATHLIST=new JSONArray();
			PATHLIST.put(trans);
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","PATHLIST="+PATHLIST.toString(),"server.txt");
			param=new JSONObject();
			param.put("VMC_NO", vmc_no);
			param.put("PATHLIST", PATHLIST);
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","param="+param.toString(),"server.txt");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final JSONObject paramVal[]={null};
		paramVal[0]=param;

			
		//�����̷߳�����Ϣ
		final Message tomain3=mainhand.obtainMessage();
		//�½�Volley 
		mQueue = getRequestQueue();
		//4.׼��������Ϣ����
		StringRequest stringRequest3 = new StringRequest(Method.POST, target3,  new Response.Listener<String>() {  
			@Override  
			public void onResponse(String response) {  
			   
			    //�������ɹ�
				result = response;	//��ȡ���ص��ַ���
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
				JSONObject object;
				try {
					object = new JSONObject(result);
					int errType =  object.getInt("Error");
					//�����й���
					if(errType>0)
					{
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail8]Records","server.txt");
					}
					else
					{
						JSONObject retj=new JSONObject();
						retj.put("cabinetNumber", cabinetNumberVal[0]);
						retj.put("pathName", pathNameVal[0]);
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[ok8]["+retj.toString()+"]","server.txt");
						columnjson.put(retj);
					}				
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail8-1]","server.txt");
					e.printStackTrace();
				}	
				
				//���岽��������һ��������Ϣ
				columnint++;
				if(columnint<columnarr.length())
				{
					updatecolumn(columnint);
				}
				else
				{						
					//�ϴ���server
					//�����̷߳�����Ϣ
					tomain3.what=SETHUODAOSTATUMAIN;
					tomain3.obj=columnjson;
					mainhand.sendMessage(tomain3); // ������Ϣ
				}
			}  
		}, new Response.ErrorListener() {  
			@Override  
			public void onErrorResponse(VolleyError error) {  
				result = "����ʧ�ܣ�";
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail8]"+result,"server.txt");
				//���岽��������һ��������Ϣ
				columnint++;
				if(columnint<columnarr.length())
				{
					updatecolumn(columnint);
				}
				else
				{						
					//�ϴ���server
					//�����̷߳�����Ϣ
					tomain3.what=SETHUODAOSTATUMAIN;
					tomain3.obj=columnjson;
					mainhand.sendMessage(tomain3); // ������Ϣ
				}
			}  
		}) 
		{  
			@Override  
			protected Map<String, String> getParams() throws AuthFailureError {  
				//3.���params
				Map<String, String> map = new HashMap<String, String>();  
				map.put("Token", Tok);  
				map.put("param", paramVal[0].toString());
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","columns="+map.toString(),"server.txt");
				return map;  
		   }  
		}; 	
		//5.������Ϣ�����͵�������
		mQueue.add(stringRequest3);				
	}
	
	
	//==========
	//==�汾����ģ��
	//==========
	JSONArray versionarr=null;
	JSONArray zhuheversionArray=null;
	JSONObject zhuheversionjson = null; 
	int versionint=0;
	//�ֽ���Ʒ��Ϣ
	private void versionArray(String classrst) throws JSONException
	{
		JSONObject jsonObject = new JSONObject(classrst);
		if(ToolClass.getServerVer()==1)//һ�ں�̨
		{
			versionarr=jsonObject.getJSONArray("List");
			versionint=0;
			zhuheversionArray=new JSONArray();
			zhuheversionjson = new JSONObject(); 
			if(versionarr.length()==0)
			{
				ToolClass.deleteAPKFile();
				//�����̷߳�����Ϣ
				Message tomain=mainhand.obtainMessage();
				tomain.what=SETVERSIONMAIN;
				tomain.obj=zhuheversionjson.toString();
				mainhand.sendMessage(tomain); // ������Ϣ	
			}			
		}
	}
	//���³�����Ϣ
	private String updateversion(int i) throws JSONException
	{
		final JSONObject object2=versionarr.getJSONObject(i);
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","���°汾="+object2.toString(),"server.txt");										
		final JSONObject zhuheobj=object2;
		//��һ��.��ȡ��ƷͼƬ����
		final String FILE_URL=object2.getString("FILE_URL");
		String ATT_ID="";
		if(FILE_URL.equals("null")!=true)
		{
			String a[] = FILE_URL.split("/");  
			ATT_ID=a[a.length-1];
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","����ATT_ID="+ATT_ID,"server.txt");										
			zhuheobj.put("AttImg", ToolClass.getImgFile(ATT_ID));
		}
		else
		{
			zhuheobj.put("AttImg", "");
		}
		
		try
		{	
			if(ATT_ID.equals("")==true)
			{
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","����["+ATT_ID+"]������","server.txt");
			}
			else
			{
				if(ToolClass.isAPKFile(ATT_ID))
				{
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","����["+ATT_ID+"]�Ѵ���","server.txt");
				}
				else 
				{
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","����["+ATT_ID+"]��ʼ����...","server.txt");
					//�ڶ���.׼������	
					String serip=httpStr.substring(0,httpStr.lastIndexOf('/'));
					String url= serip+FILE_URL;	//Ҫ�ύ��Ŀ���ַ
					final String ATTIDS=ATT_ID;
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","ATTID=["+ATTIDS+"]url["+url+"]","server.txt");
					downloadApk(ATTIDS,url);//���س���
										
				}
				
			}
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec2=[fail10-1]"+e,"server.txt");
		}
		
		//����������ͼƬ���ֱ��浽json��		
		zhuheversionArray.put(zhuheobj);
		
		
		//���Ĳ���������һ��������Ϣ
		versionint++;
		if(versionint<versionarr.length())
		{
			try {
				updateversion(versionint);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			try {
				zhuheversionjson.put("List", zhuheversionArray);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","reczhuhe="+zhuheversionjson.toString(),"server.txt");

			//�ϴ���server
			//�����̷߳�����Ϣ
			Message tomain4=mainhand.obtainMessage();
			tomain4.what=SETVERSIONMAIN;
			tomain4.obj=zhuheversionjson.toString();
			mainhand.sendMessage(tomain4); // ������Ϣ
		}		
		return "";
		
	}
	
	private String url=null;
	private String ATTIDS=null;
	/**
     * ����apk�ļ�
     */
    private void downloadApk(String ATT,String str)
    {    	
    	ATTIDS=ATT;
    	url=str;
        // �������߳��������
        new downloadApkThread().start();
    }

    /**
     * �����ļ��߳�
     * 
     * @author coolszy
     *@date 2012-4-26
     *@blog http://blog.92coding.com
     */
    private class downloadApkThread extends Thread
    {
        @Override
        public void run()
        {
            try
            {
            	//1.����
            	HttpClient client = new DefaultHttpClient();  
                HttpGet get = new HttpGet(url);  
                HttpResponse response = client.execute(get);  
                HttpEntity entity = response.getEntity();  
                long length = entity.getContentLength();  
                InputStream is = entity.getContent();  
                FileOutputStream fileOutputStream = null;  
                if (is != null)
                {  
                    File file = ToolClass.setAPKFile(ATTIDS);  
                    fileOutputStream = new FileOutputStream(file);  
                    byte[] buf = new byte[1024];  
                    int ch = -1;  
//                    int count = 0;  
                    while ((ch = is.read(buf)) != -1) {  
                        fileOutputStream.write(buf, 0, ch);  
//                        count += ch;  
                        if (length > 0) {  
                        }  
                    }  
                }  
                fileOutputStream.flush();  
                if (fileOutputStream != null) {  
                    fileOutputStream.close();  
                } 
                //2.�ϴ����°汾״̬
                String target3 = httpStr+"/api/updateClientVersion";	//Ҫ�ύ��Ŀ���ַ
                //�½�Volley 
				mQueue = getRequestQueue();
                StringRequest stringRequest3 = new StringRequest(Method.POST, target3,  new Response.Listener<String>() {  
        			@Override  
        			public void onResponse(String response) {              			   
        			    //�������ɹ�
        				result = response;	//��ȡ���ص��ַ���
        				ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
        				
        			}  
        		}, new Response.ErrorListener() {  
        			@Override  
        			public void onErrorResponse(VolleyError error) {  
        				result = "����ʧ�ܣ�";
        				ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail8]"+result,"server.txt");            				
        			}  
        		}) 
        		{  
        			@Override  
        			protected Map<String, String> getParams() throws AuthFailureError {  
        				//3.���params
        				Map<String, String> map = new HashMap<String, String>();  
        				map.put("Token", Tok);  
        				map.put("VMC_NO", vmc_no);
        				map.put("OPEN_DOOR_ID", "");
        				map.put("EXEC_RESULT", "9");
        				map.put("EXEC_TIME", ToolClass.getLasttime());
        				map.put("VERSION_STATUS", "2");
        				ToolClass.Log(ToolClass.INFO,"EV_SERVER","�������version="+map.toString(),"server.txt");
        				return map;  
        		   }  
        		}; 	
        		//3.������Ϣ�����͵�������
        		mQueue.add(stringRequest3);
               
        		
        		//4.�ϴ���server
    			//�����̷߳�����Ϣ
    			Message tomain4=mainhand.obtainMessage();
    			tomain4.what=SETINSTALLMAIN;
    			tomain4.obj=ATTIDS;
    			mainhand.sendMessage(tomain4); // ������Ϣ                
            } catch (Exception e)
            {
                e.printStackTrace();
                //2.�ϴ����°汾״̬
                String target3 = httpStr+"/api/updateClientVersion";	//Ҫ�ύ��Ŀ���ַ
                //�½�Volley 
				mQueue = getRequestQueue();
                StringRequest stringRequest3 = new StringRequest(Method.POST, target3,  new Response.Listener<String>() {  
        			@Override  
        			public void onResponse(String response) {              			   
        			    //�������ɹ�
        				result = response;	//��ȡ���ص��ַ���
        				ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
        				
        			}  
        		}, new Response.ErrorListener() {  
        			@Override  
        			public void onErrorResponse(VolleyError error) {  
        				result = "����ʧ�ܣ�";
        				ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail8]"+result,"server.txt");            				
        			}  
        		}) 
        		{  
        			@Override  
        			protected Map<String, String> getParams() throws AuthFailureError {  
        				//3.���params
        				Map<String, String> map = new HashMap<String, String>();  
        				map.put("Token", Tok);  
        				map.put("VMC_NO", vmc_no);
        				map.put("OPEN_DOOR_ID", "");
        				map.put("EXEC_RESULT", "2");
        				map.put("EXEC_TIME", ToolClass.getLasttime());
        				map.put("VERSION_STATUS", "1");
        				ToolClass.Log(ToolClass.INFO,"EV_SERVER","����ʧ��version="+map.toString(),"server.txt");
        				return map;  
        		   }  
        		}; 	
        		//3.������Ϣ�����͵�������
        		mQueue.add(stringRequest3);
            }
        }
    };
	
    
    //==========
  	//==��־����ģ��
  	//==========
  	JSONArray logarr=null;
  	JSONArray zhuhelogArray=null;
  	JSONObject zhuhelogjson = null; 
  	int logint=0;
  	//�ֽ���Ʒ��Ϣ
  	private void logArray(String classrst) throws JSONException
  	{
  		JSONObject jsonObject = new JSONObject(classrst);
  		if(ToolClass.getServerVer()==1)//һ�ں�̨
  		{
  			logarr=jsonObject.getJSONArray("List");
  			logint=0;
  			zhuhelogArray=new JSONArray();
  			zhuhelogjson = new JSONObject(); 
  			if(logarr.length()==0)
  			{
  				//�����̷߳�����Ϣ
  				Message tomain=mainhand.obtainMessage();
  				tomain.what=SETLOGMAIN;
  				tomain.obj=zhuhelogjson.toString();
  				mainhand.sendMessage(tomain); // ������Ϣ	
  			}			
  		}
  	}
  	//������־��Ϣ
  	private String updatelog(int i) throws JSONException
  	{
  		final JSONObject object2=logarr.getJSONObject(i);
  		ToolClass.Log(ToolClass.INFO,"EV_SERVER","������־="+object2.toString(),"server.txt");										
  		final JSONObject zhuheobj=object2;
  		//��һ��.��ȡ��־ID
  		final int LOG_ID=object2.getInt("CLIENT_LOG_ID");
  		String START_LOG_TIME=object2.getString("START_LOG_TIME");
  		String END_LOG_TIME=object2.getString("END_LOG_TIME");
  		ToolClass.Log(ToolClass.INFO,"EV_SERVER","��Ҫ�ϴ�LOG_ID="+LOG_ID+"START_LOG_TIME="+START_LOG_TIME+"END_LOG_TIME"+END_LOG_TIME,"server.txt");										
		zhuheobj.put("AttImg", LOG_ID);
		//�ڶ���.ѹ����־��
		final String f=ToolClass.logFileInterval(START_LOG_TIME, END_LOG_TIME);
		
		if(f==null)
		{
			ToolClass.Log(ToolClass.INFO,"EV_SERVER","��־["+LOG_ID+"]����־��Ҫ�ϴ�","server.txt");
		}
		else
		{
	  		try
	  		{	
	  			ToolClass.Log(ToolClass.INFO,"EV_SERVER","��־["+LOG_ID+"]��ʼ�ϴ�...","server.txt");
				//�ڶ���.׼���ϴ�	
	  			uploadLog(String.valueOf(LOG_ID),f);
	  		}
	  		catch (Exception e) {
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  			ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec2=[fail10-1]"+e,"server.txt");
	  		}
		}
  		
  		//����������ͼƬ���ֱ��浽json��		
  		zhuhelogArray.put(zhuheobj);
  		
  		
  		//���Ĳ���������һ��������Ϣ
  		logint++;
  		if(logint<logarr.length())
  		{
  			try {
  				updatelog(logint);
  			} catch (JSONException e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  			}
  		}
  		else
  		{
  			try {
  				zhuhelogjson.put("List", zhuhelogArray);
  			} catch (JSONException e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  			}
  			ToolClass.Log(ToolClass.INFO,"EV_SERVER","reczhuhe="+zhuhelogjson.toString(),"server.txt");

  			//�ϴ���server
  			//�����̷߳�����Ϣ
  			Message tomain4=mainhand.obtainMessage();
  			tomain4.what=SETLOGMAIN;
  			tomain4.obj=zhuhelogjson.toString();
  			mainhand.sendMessage(tomain4); // ������Ϣ
  		}		
  		return "";
  		
  	}
  	
  	private String LOG_IDS=null;
  	private File file=null;
  	/**
     * �ϴ���־�ļ�
     */
    private void uploadLog(String LOG_ID,String f)
    {    	
    	LOG_IDS=LOG_ID;
    	file=new File(f);
        // �������߳��������
        new uploadLogThread().start();
    }
  	
    /**
     * �ϴ��ļ��߳�
     * 
     * @author coolszy
     *@date 2012-4-26
     *@blog http://blog.92coding.com
     */
    private class uploadLogThread extends Thread
    {
        @Override
        public void run()
        {
//        	String target = httpStr+"/api/uploadClientLog";	//Ҫ�ύ��Ŀ���ַ
//			HttpClient httpclient = new DefaultHttpClient();	//����HttpClient����
//			httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);//����ʱ
//			httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);//��ȡ��ʱ
//			HttpPost httppost = new HttpPost(target);	//����HttpPost����
//			//1.���params
//			List<NameValuePair> params = new ArrayList<NameValuePair>();
//			params.add(new BasicNameValuePair("LAST_EDIT_TIME", ToolClass.getLasttime()));
//			params.add(new BasicNameValuePair("Token", Tok));			
//			params.add(new BasicNameValuePair("CLIENT_LOG_ID", LOG_IDS));
//			String bal=null;
//			try {
//				//�����ļ���
//				FileInputStream inputFile = new FileInputStream(file);
//				//ץΪbyte�ֽ�
//				byte[] buffer = new byte[(int)file.length()];
//				inputFile.read(buffer);
//				inputFile.close();
//				//ѹ��ΪBase64��ʽ
//				bal= Base64.encodeToString(buffer,Base64.DEFAULT);
//			} catch (FileNotFoundException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			params.add(new BasicNameValuePair("FILE_CONTENT", bal));
//			//ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send3="+params.toString(),"server.txt");
//			try {
//				httppost.setEntity(new UrlEncodedFormEntity(params, "utf-8")); //���ñ��뷽ʽ
//				HttpResponse httpResponse = httpclient.execute(httppost);	//ִ��HttpClient����
//				//�����̷߳�����Ϣ
//				Message tomain=mainhand.obtainMessage();
//				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){	//�������ɹ�
//					//�������ɹ�
//					result = EntityUtils.toString(httpResponse.getEntity());	//��ȡ���ص��ַ���
//					ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=��־�ϴ����"+result,"server.txt");					
//				}else{
//					result = "����ʧ�ܣ�";
//					tomain.what=SETFAILMAIN;
//					mainhand.sendMessage(tomain); // ������Ϣ
//					ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail1]��־�ϴ�"+result,"server.txt");
//				}
//				
//				
//			} 
//	       catch (Exception e) 
//	       {  
//	           //e.printStackTrace();  
//	    	   //�����̷߳�������ʧ����Ϣ
//				Message tomain=mainhand.obtainMessage();
//	    	    tomain.what=SETFAILMAIN;
//	    	    mainhand.sendMessage(tomain); // ������Ϣ
//	    	    ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=Net[fail1]SETFAILMAIN","server.txt");
//	       }
        	
        	//�ڶ������ϴ���server
  			String target17 = httpStr+"/api/uploadClientLog";	//Ҫ�ύ��Ŀ���ַ
			final String LAST_EDIT_TIME17=ToolClass.getLasttime();
        	//�����̷߳�����Ϣ
			final Message tomain17=mainhand.obtainMessage();
			tomain17.what=SETNONE;
			//�½�Volley 
			mQueue = getRequestQueue();
			//4.׼��������Ϣ����
			StringRequest stringRequest17 = new StringRequest(Method.POST, target17,  new Response.Listener<String>() {  
				@Override  
				public void onResponse(String response) {  
				   
				    //�������ɹ�
					result = response;	//��ȡ���ص��ַ���
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=��־�ϴ����"+result,"server.txt");
															 
				}  
			}, new Response.ErrorListener() {  
				@Override  
				public void onErrorResponse(VolleyError error) {  
					result = "����ʧ�ܣ�";
					tomain17.what=SETFAILMAIN;
		    	    mainhand.sendMessage(tomain17); // ������Ϣ
		    	    ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail17]SETFAILMAIN"+result,"server.txt");
				}  
			}) 
			{  
				@Override  
				protected Map<String, String> getParams() throws AuthFailureError {  
					String bal=null;
					try {
						//�����ļ���
						FileInputStream inputFile = new FileInputStream(file);
						//ץΪbyte�ֽ�
						byte[] buffer = new byte[(int)file.length()];
						inputFile.read(buffer);
						inputFile.close();
						//ѹ��ΪBase64��ʽ
						bal= Base64.encodeToString(buffer,Base64.DEFAULT);
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//3.���params
					Map<String, String> map = new HashMap<String, String>();  
					map.put("Token", Tok);  
					map.put("LAST_EDIT_TIME", LAST_EDIT_TIME17);	
					map.put("CLIENT_LOG_ID", LOG_IDS);
					map.put("FILE_CONTENT", bal);
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1=��־�ϴ�...","server.txt");
					return map;  
			   }  
			}; 	
			//5.������Ϣ�����͵�������
			mQueue.add(stringRequest17);
        }
    };
    
    //==============
  	//==֧����΢�Ÿ���ģ��
  	//==============
  	JSONArray Accountarr=null;
  	JSONArray zhuheAccountArray=null;
  	JSONObject zhuheAccountjson = null; 
  	int Accountint=0;
  	//�ֽ���Ʒ��Ϣ
  	private void AccountArray(String classrst) throws JSONException
  	{
  		JSONObject jsonObject = new JSONObject(classrst);
  		if(ToolClass.getServerVer()==1)//һ�ں�̨
  		{
  			Accountarr=jsonObject.getJSONArray("List");
  			Accountint=0;
  			zhuheAccountArray=new JSONArray();
  			zhuheAccountjson = new JSONObject(); 
  			if(Accountarr.length()==0)
  			{
  				//�����̷߳�����Ϣ
  				Message tomain=mainhand.obtainMessage();
  				tomain.what=SETACCOUNTMAIN;
  				tomain.obj=zhuheAccountjson.toString();
  				mainhand.sendMessage(tomain); // ������Ϣ	
  			}			
  		}
  	}
  	//����֧����΢����Ϣ
  	private String updateAccount(int i) throws JSONException
  	{
  		final JSONObject object2=Accountarr.getJSONObject(i);
  		Message tomain14=mainhand.obtainMessage();
  		tomain14.what=SETACCOUNTMAIN;
		tomain14.obj="";							   	    
		mainhand.sendMessage(tomain14); // ������Ϣ
		
		
		ToolClass.Log(ToolClass.INFO,"EV_SERVER","����֧����΢����Ϣ="+object2.toString(),"server.txt");										
		//��ʱ3s
	    new Handler().postDelayed(new Runnable() 
		{
            @Override
            public void run() 
            { 
            	String vmcno="";
            	//����VMC_NO
          		vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(ToolClass.getContext());// ����InaccountDAO����
        	    // ��ȡ����������Ϣ�����洢��List���ͼ�����
            	Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
            	if(tb_inaccount!=null)
            	{
            		vmcno=tb_inaccount.getDevID().toString();            		
            	}
            	try {
					String WX_URL=object2.getString("WX_URL");
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","΢��֤��="+WX_URL,"server.txt");
					//��Ҫ����֤��
					if((ToolClass.isEmptynull(object2.get("WX_URL").toString())==false)
				        	&&(object2.get("WX_URL").toString().equals("null")==false)
				        )
					{
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","����΢��֤��...","server.txt");
						//1.ɾ��ԭ����ѹ���ļ�
						ToolClass.deleteCertFile();							
						//2.����
						String serip=httpStr.substring(0,httpStr.lastIndexOf("/shj"));
	  					String url= serip+WX_URL;	//Ҫ�ύ��Ŀ���ַ
	  					String a[] = WX_URL.split("/");  
	  		  			final String ATTIDS=a[a.length-1]; 
	  					ToolClass.Log(ToolClass.INFO,"EV_SERVER","ATTID=["+ATTIDS+"]url["+url+"]","server.txt");
	  					downloadCert(url,ATTIDS,object2,vmcno);//����	  				
					}
					else
					{
						ToolClass.Log(ToolClass.INFO,"EV_SERVER","ֱ�Ӵ���֧����΢��","server.txt");
						ToolClass.ResetConfigFileServer(object2,vmcno);
					}	
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            					
				Message tomain=mainhand.obtainMessage();
		  		tomain.what=SETACCOUNTRESETMAIN;
				tomain.obj="";							   	    
				mainhand.sendMessage(tomain); // ������Ϣ
            }

		}, 1000);
  		return "";
  		
  	}
  	
  	private String certurl=null;
  	private String CERTATTAD=null;
  	private JSONObject certobj=null;
  	private String certvmcno=null;
	/**
     * ����cert΢���ļ�
     */
    private void downloadCert(String str,String ATT_ID,JSONObject object2,String VMC_NO)
    {    	
    	certurl=str;
    	CERTATTAD=ATT_ID;
    	certobj=object2;
    	certvmcno=VMC_NO;
        // �������߳��������
        new downloadCertThread().start();
    }
  	
    /**
     * �����ļ��߳�
     * 
     * @author coolszy
     *@date 2012-4-26
     *@blog http://blog.92coding.com
     */
    private class downloadCertThread extends Thread
    {
        @Override
        public void run()
        {
            try
            {
            	//1.����
            	HttpClient client = new DefaultHttpClient();  
                HttpGet get = new HttpGet(certurl);  
                HttpResponse response = client.execute(get);  
                HttpEntity entity = response.getEntity();  
                long length = entity.getContentLength();  
                InputStream is = entity.getContent();  
                FileOutputStream fileOutputStream = null;  
                if (is != null)
                {  
                    File file = ToolClass.savetoCert(CERTATTAD);  
                    fileOutputStream = new FileOutputStream(file);  
                    byte[] buf = new byte[1024];  
                    int ch = -1;  
//                    int count = 0;  
                    while ((ch = is.read(buf)) != -1) {  
                        fileOutputStream.write(buf, 0, ch);  
//                        count += ch;  
                        if (length > 0) {  
                        }  
                    }  
                }  
                fileOutputStream.flush();  
                if (fileOutputStream != null) {  
                    fileOutputStream.close();  
                }   
                ToolClass.Log(ToolClass.INFO,"EV_SERVER","΢��֤��["+CERTATTAD+"],�������","server.txt");
                //2.��ѹ
                String attimg=CERTATTAD.substring(CERTATTAD.lastIndexOf(".") + 1).toUpperCase();
                ToolClass.Log(ToolClass.INFO,"EV_SERVER","֤���ʽ["+attimg+"]","server.txt");
                //��ѹ����,��ѹ��
                if(attimg.equals("RAR")||attimg.equals("ZIP"))
                {
                	//��ѹ��
                	String zipFile = ToolClass.setCertFile(CERTATTAD).toString();
                	String upzipFile=ToolClass.getEV_DIR()+File.separator+"cert";
                	ToolClass.deleteCertFolder();//ɾ��certĿ¼
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
                }
                //3.��������
                ToolClass.ResetConfigFileServer(certobj,certvmcno);
            } catch (Exception e)
            {
            	ToolClass.Log(ToolClass.INFO,"EV_SERVER","΢��֤��["+CERTATTAD+"],����ʧ��","server.txt");
                e.printStackTrace();                
            }
        }
    };
  	
    //==============
  	//==���ģ��
  	//==============
  	JSONArray advarr=null;
  	JSONArray zhuheadvArray=null;
  	JSONObject zhuheadvjson = null; 
  	int advint=0;
  	//�ֽ�����Ϣ
  	private void advArray(String advrst) throws JSONException
  	{
  		JSONObject jsonObject = new JSONObject(advrst); 
  		advarr=jsonObject.getJSONArray("List");
  		advint=0;
  		zhuheadvArray=new JSONArray();
  		zhuheadvjson = new JSONObject(); 
  		if(advarr.length()==0)
  		{
  			//�����̷߳�����Ϣ
  			Message tomain=mainhand.obtainMessage();
  			tomain.what=SETADVMAIN;
  			tomain.obj=zhuheadvjson.toString();
  			mainhand.sendMessage(tomain); // ������Ϣ	
  		}
  	}
  	
  	//���¹����Ϣ
  	private String updateadv(int i) throws JSONException
  	{
  		final JSONObject object2=advarr.getJSONObject(i);
  		ToolClass.Log(ToolClass.INFO,"EV_SERVER","���¹��="+object2.toString(),"server.txt");										
  		final JSONObject zhuheobj=object2;
  		//��һ������ȡͼƬ����ATTID
  		final String CLS_URL=object2.getString("ADV_URL");
  		String ATT_ID="",TypeStr="";
  		int FileType=0;//1ͼƬ,2��Ƶ
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
		    	FileType=2;
		        ToolClass.Log(ToolClass.INFO,"EV_SERVER","�����ƵATT_ID="+ATT_ID+"."+TypeStr,"server.txt");										
		    }
		    //�Ƿ�ͼƬ�ļ�
		    else if(MediaFileAdapter.isImgFileType(tmp)==true)
		    {
		    	FileType=1;
	  			ToolClass.Log(ToolClass.INFO,"EV_SERVER","���ͼƬATT_ID="+ATT_ID+"."+TypeStr,"server.txt");										
	  		}
  			
  			zhuheobj.put("AttImg", ToolClass.getImgFile(ATT_ID));
  		}
  		else
  		{
  			zhuheobj.put("AttImg", "");
  		}
  		
  		
  		try
  		{	
  			if(ATT_ID.equals("")==true)
  			{
  				ToolClass.Log(ToolClass.INFO,"EV_SERVER","���["+object2.getString("ADV_TITLE")+"]��","server.txt");
  			}
  			else
  			{
  				int IS_DELETE=object2.getInt("IS_DELETE");
  				int ALERT_TYPE=object2.getInt("ALERT_TYPE");//0��棬1����
				ToolClass.Log(ToolClass.INFO,"EV_SERVER","���["+object2.getString("ADV_TITLE")+"],ALERT_TYPE="+ALERT_TYPE+",IS_DELETE="+IS_DELETE,"server.txt");
				final String ads;
		    	if(ALERT_TYPE==1)
		    		ads="adshuo";
		    	else
		    		ads="ads";
		    	
				if(ToolClass.isAdsFile(ATT_ID,TypeStr,ads))
  				{
  					ToolClass.Log(ToolClass.INFO,"EV_SERVER","���["+object2.getString("ADV_TITLE")+"]�Ѵ���","server.txt");
  					if(IS_DELETE==1)
  					{
  						ToolClass.Log(ToolClass.INFO,"EV_SERVER","���["+object2.getString("ADV_TITLE")+"]ɾ��","server.txt");
  						ToolClass.delAds(ATT_ID,TypeStr,ads);
  					}
  				}
  				else 
  				{
  					ToolClass.Log(ToolClass.INFO,"EV_SERVER","���["+object2.getString("ADV_TITLE")+"],������","server.txt");
  					if(IS_DELETE==0)
  					{
	  					ToolClass.Log(ToolClass.INFO,"EV_SERVER","���["+object2.getString("ADV_TITLE")+"],����...","server.txt");
	  					//�ڶ���.׼������	
	  					String serip=httpStr.substring(0,httpStr.lastIndexOf("/shj"));
	  					String url= serip+CLS_URL;	//Ҫ�ύ��Ŀ���ַ
	  					final String ATTIDS=ATT_ID;
	  					final String TypeStrs=TypeStr;
	  					ToolClass.Log(ToolClass.INFO,"EV_SERVER","ATTID=["+ATTIDS+"."+TypeStrs+"]url["+url+"]","server.txt");
	  					//����ͼƬ
	  					if(FileType==1)
	  					{
		  					ImageRequest imageRequest = new ImageRequest(  
		  							url,  
		  					        new Response.Listener<Bitmap>() {  
		  					            @Override  
		  					            public void onResponse(Bitmap response) {  
		  					            	ToolClass.saveBitmaptoads(response,TypeStrs,ATTIDS,ads);
		  					            	try {
		  										ToolClass.Log(ToolClass.INFO,"EV_SERVER","���ͼƬ["+object2.getString("ADV_TITLE")+"],�������","server.txt");
		  									} catch (JSONException e) {
		  										// TODO Auto-generated catch block
		  										e.printStackTrace();
		  									}
		  					            }  
		  					        }, 0, 0, Config.RGB_565, new Response.ErrorListener() {  
		  					            @Override  
		  					            public void onErrorResponse(VolleyError error) {  
		  									result = "����ʧ�ܣ�";
		  									try {
		  										ToolClass.Log(ToolClass.INFO,"EV_SERVER","���ͼƬ["+object2.getString("ADV_TITLE")+"],����ʧ��","server.txt");
		  									} catch (JSONException e) {
		  										// TODO Auto-generated catch block
		  										e.printStackTrace();
		  									}
		  					            }  
		  					        });
		  					mQueue.add(imageRequest); 
	  					}
	  				    //������Ƶ
	  					else if(FileType==2)
	  					{
	  						downloadAds(url,ATTIDS,TypeStr,ads);//���س���
	  					}
  					}
  				}
  				
  			}
  		}
  		catch (JSONException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  			ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec2=[fail10-1]","server.txt");
  		}
  		
  		//�������������ֱ��浽json��		
  		zhuheadvArray.put(zhuheobj);
  		
  		
  		//���Ĳ���������һ��������Ϣ
  		advint++;
  		if(advint<advarr.length())
  		{
  			try {
  				updateadv(advint);
  			} catch (JSONException e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  			}
  		}
  		else
  		{
  			try {
  				zhuheadvjson.put("List", zhuheadvArray);
  			} catch (JSONException e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  			}
  			ToolClass.Log(ToolClass.INFO,"EV_SERVER","reczhuhe="+zhuheadvjson.toString(),"server.txt");

  			//�ϴ���server
  			//�����̷߳�����Ϣ
  			Message tomain4=mainhand.obtainMessage();
  			tomain4.what=SETADVMAIN;
  			tomain4.obj=zhuheclassjson.toString();
  			mainhand.sendMessage(tomain4); // ������Ϣ
  			
  			//���ܳɹ���ʧ�ܣ��������ù��ʱ��
  		    //��ʱ3s
  		    new Handler().postDelayed(new Runnable() 
  			{
  	            @Override
  	            public void run() 
  	            { 
  				Message tomain=mainhand.obtainMessage();
  		  		tomain.what=SETADVRESETMAIN;
  				tomain.obj="";							   	    
  				mainhand.sendMessage(tomain); // ������Ϣ
  	            }

  			}, 1000);
  		}		
  		return "";
  	}
  	
  	private String adsurl=null;
  	private String ATTADS=null;
  	private String TypeStr=null;
  	private String adsstr=null;
	/**
     * ����apk�����Ƶ�ļ�
     */
    private void downloadAds(String str,String ATT_ID,String Type,String ads)
    {    	
    	adsurl=str;
    	ATTADS=ATT_ID;
    	TypeStr=Type;
    	adsstr=ads;
        // �������߳��������
        new downloadAdsThread().start();
    }
  	
    /**
     * �����ļ��߳�
     * 
     * @author coolszy
     *@date 2012-4-26
     *@blog http://blog.92coding.com
     */
    private class downloadAdsThread extends Thread
    {
        @Override
        public void run()
        {
            try
            {
            	//1.����
            	HttpClient client = new DefaultHttpClient();  
                HttpGet get = new HttpGet(adsurl);  
                HttpResponse response = client.execute(get);  
                HttpEntity entity = response.getEntity();  
                long length = entity.getContentLength();  
                InputStream is = entity.getContent();  
                FileOutputStream fileOutputStream = null;  
                if (is != null)
                {  
                    File file = ToolClass.saveAvitoads(ATTADS,TypeStr,adsstr);  
                    fileOutputStream = new FileOutputStream(file);  
                    byte[] buf = new byte[1024];  
                    int ch = -1;  
//                    int count = 0;  
                    while ((ch = is.read(buf)) != -1) {  
                        fileOutputStream.write(buf, 0, ch);  
//                        count += ch;  
                        if (length > 0) {  
                        }  
                    }  
                }  
                fileOutputStream.flush();  
                if (fileOutputStream != null) {  
                    fileOutputStream.close();  
                }   
                ToolClass.Log(ToolClass.INFO,"EV_SERVER","�����Ƶ["+ATTADS+"."+TypeStr+"],�������","server.txt");
            } catch (Exception e)
            {
            	ToolClass.Log(ToolClass.INFO,"EV_SERVER","�����Ƶ["+ATTADS+"."+TypeStr+"],����ʧ��","server.txt");
                e.printStackTrace();                
            }
        }
    };
    
    //==============
  	//==�豸״̬ģ��
  	//==============
  	JSONArray clientarr=null;
  	JSONArray zhuheclientArray=null;
  	JSONObject zhuheclientjson = null; 
  	int clientint=0;
  	//�ֽ��豸��Ϣ
  	private void clientArray(String clientrst) throws JSONException
  	{
  		JSONObject jsonObject = new JSONObject(clientrst); 
  		clientarr=jsonObject.getJSONArray("List");
  		clientint=0;
  		zhuheclientArray=new JSONArray();
  		zhuheclientjson = new JSONObject(); 
  		if(clientarr.length()==0)
  		{
  			//�����̷߳�����Ϣ
  			Message tomain=mainhand.obtainMessage();
  			tomain.what=SETCLIENTMAIN;
  			tomain.obj=zhuheclientjson.toString();
  			mainhand.sendMessage(tomain); // ������Ϣ	
  		}
  	    //����VMC_NO������
  		vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(ToolClass.getContext());// ����InaccountDAO����
	    // ��ȡ����������Ϣ�����洢��List���ͼ�����
    	Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
    	if(tb_inaccount!=null)
    	{
    		devID=tb_inaccount.getDevID().toString();
    		mainPwd=tb_inaccount.getMainPwd();
    		ToolClass.Log(ToolClass.INFO,"EV_SERVER","����VMC_NO="+devID+",MANAGER_PASSWORD="+mainPwd+",CLIENT_STATUS_SERVICE="+ToolClass.isCLIENT_STATUS_SERVICE(),"server.txt");	
    	}
  	}
  	
  	String devID="";
  	String mainPwd="";
  	//�����豸��Ϣ
  	private String updateclient(int i) throws JSONException
  	{  	
  		final JSONObject object2=clientarr.getJSONObject(i);
  		ToolClass.Log(ToolClass.INFO,"EV_SERVER","�����豸="+object2.toString(),"server.txt");										
  		final JSONObject zhuheobj=object2;
  		//��һ������ȡVMC_NO������
  		final String VMC_NO=object2.getString("VMC_NO");
  		final String MANAGER_PASSWORD=object2.getString("MANAGER_PASSWORD");
  		int CLIENT_STATUS_SERVICE=object2.getInt("CLIENT_STATUS_SERVICE");//����״̬
  		//����ʱ��
  		int RESTART_SKIP=object2.getInt("RESTART_SKIP");
  		String RESTART_TIME=object2.getString("RESTART_TIME");
  		
  		ToolClass.Log(ToolClass.INFO,"EV_SERVER","�豸VMC_NO="+VMC_NO+",MANAGER_PASSWORD="+MANAGER_PASSWORD+"CLIENT_STATUS_SERVICE="+CLIENT_STATUS_SERVICE
  				+"RESTART_SKIP="+RESTART_SKIP+"RESTART_TIME="+RESTART_TIME,"server.txt");	
  		zhuheobj.put("AttImg", "");
  		  		
  		try
  		{	   
  			if((ToolClass.isEmptynull(devID)==false)&&(devID.equals(VMC_NO)))
  			{  				
  				vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(ToolClass.getContext());// ����InaccountDAO����
  			    //����Tb_inaccount���� 
    			Tb_vmc_system_parameter tb_vmc_system_parameter = new Tb_vmc_system_parameter(VMC_NO, "", 0,0, 
    					0,0,MANAGER_PASSWORD,0,0,0,0,0,0,0,"",0,
    					0,0, 0,0,0,"","");
    			ToolClass.Log(ToolClass.INFO,"EV_SERVER","�����豸VMC_NO="+tb_vmc_system_parameter.getDevID()+",MANAGER_PASSWORD="+tb_vmc_system_parameter.getMainPwd(),"server.txt");	
    			parameterDAO.updatepwd(tb_vmc_system_parameter); 
    			//���ñ����Ƿ����ʹ��
    			if(CLIENT_STATUS_SERVICE==0)
    				ToolClass.setCLIENT_STATUS_SERVICE(true);
    			else
    				ToolClass.setCLIENT_STATUS_SERVICE(false);	
  			}
  		}
  		catch (Exception e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  			ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec2=[fail10-1]","server.txt");
  		}
  		
  		//�������������ֱ��浽json��		
  		zhuheclientArray.put(zhuheobj);
  		
  		
  		//���Ĳ���������һ��������Ϣ
  		clientint++;
  		if(clientint<clientarr.length())
  		{
  			try {
  				updateclient(clientint);
  			} catch (JSONException e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  			}
  		}
  		else
  		{
  			try {
  				zhuheclientjson.put("List", zhuheclientArray);
  			} catch (JSONException e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  			}
  			ToolClass.Log(ToolClass.INFO,"EV_SERVER","reczhuhe="+zhuheclientjson.toString(),"server.txt");

  			//�ϴ���server
  			//�����̷߳�����Ϣ
  			Message tomain4=mainhand.obtainMessage();
  			tomain4.what=SETCLIENTMAIN;
  			tomain4.obj=zhuheclientjson.toString();
  			mainhand.sendMessage(tomain4); // ������Ϣ  			
  		}		
  		return "";
  	}
  	
    //==============
  	//==���Ϣģ��
  	//==============
  	JSONArray eventarr=null;
  	JSONArray zhuheeventArray=null;
  	JSONObject zhuheeventjson = null; 
  	int eventint=0;
  	//�ֽ��豸��Ϣ
  	private void eventArray(String eventrst) throws JSONException
  	{
  		JSONObject jsonObject = new JSONObject(eventrst); 
  		eventarr=jsonObject.getJSONArray("List");
  		eventint=0;
  		zhuheeventArray=new JSONArray();
  		zhuheeventjson = new JSONObject(); 
  		if(eventarr.length()==0)
  		{
  			//�����̷߳�����Ϣ
  			Message tomain=mainhand.obtainMessage();
  			tomain.what=SETEVENTINFOMAIN;
  			tomain.obj=zhuheeventjson.toString();
  			mainhand.sendMessage(tomain); // ������Ϣ	
  		}  	    
  	}
  	
  	//���»��Ϣ
  	private String updateevent(int i) throws JSONException
  	{  	
  		final JSONObject object2=eventarr.getJSONObject(i);
  		ToolClass.Log(ToolClass.INFO,"EV_SERVER","���»��Ϣ="+object2.toString(),"server.txt");										
  		final JSONObject zhuheobj=object2;
  		//��һ������ȡVMC_NO������
  		final String EVENT_CONTENT=object2.getString("EVENT_CONTENT");
  		ToolClass.Log(ToolClass.INFO,"EV_SERVER","EVENT_CONTENT="+EVENT_CONTENT,"server.txt");	
  		zhuheobj.put("AttImg", "");
  		  
  		try
  		{	
  			{  				
  				vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(ToolClass.getContext());// ����InaccountDAO����
  			    //����Tb_inaccount���� 
    			Tb_vmc_system_parameter tb_vmc_system_parameter = new Tb_vmc_system_parameter(devID, "", 0,0, 
    					0,0,"",0,0,0,0,0,0,0,"",0,
    					0,0, 0,0,0,EVENT_CONTENT,"");
    			ToolClass.Log(ToolClass.INFO,"EV_SERVER","����EVENT_CONTENT","server.txt");	
    			parameterDAO.updateevent(tb_vmc_system_parameter); 
  			}
  		}
  		catch (Exception e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  			ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec2=[fail10-1]","server.txt");
  		}
  		
  		//�������������ֱ��浽json��		
  		zhuheeventArray.put(zhuheobj);
  		
  		
  		//���Ĳ���������һ��������Ϣ
  		eventint++;
  		if(eventint<eventarr.length())
  		{
  			try {
  				updateevent(eventint);
  			} catch (JSONException e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  			}
  		}
  		else
  		{
  			try {
  				zhuheeventjson.put("List", zhuheeventArray);
  			} catch (JSONException e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  			}
  			ToolClass.Log(ToolClass.INFO,"EV_SERVER","reczhuhe="+zhuheeventjson.toString(),"server.txt");

  			//�ϴ���server
  		    //�����̷߳�����Ϣ
			Message tomain=mainhand.obtainMessage();
			tomain.what=SETEVENTINFOMAIN;
			tomain.obj=zhuheeventjson.toString();
			mainhand.sendMessage(tomain); // ������Ϣ 			
  		}		
  		return "";
  	}
  	
    //==============
  	//==������ʾģ��
  	//==============
  	JSONArray demoarr=null;
  	JSONArray zhuhedemoArray=null;
  	JSONObject zhuhedemojson = null; 
  	int demoint=0;
  	//�ֽ��豸��Ϣ
  	private void demoArray(String demorst) throws JSONException
  	{
  		JSONObject jsonObject = new JSONObject(demorst); 
  		demoarr=jsonObject.getJSONArray("List");
  		demoint=0;
  		zhuhedemoArray=new JSONArray();
  		zhuhedemojson = new JSONObject(); 
  		if(demoarr.length()==0)
  		{
  			//�����̷߳�����Ϣ
  			Message tomain=mainhand.obtainMessage();
  			tomain.what=SETDEMOINFOMAIN;
  			tomain.obj=zhuhedemojson.toString();
  			mainhand.sendMessage(tomain); // ������Ϣ	
  		}  	    
  	}
  	
  	//���¹�����ʾ
  	private String updatedemo(int i) throws JSONException
  	{  	
  		final JSONObject object2=demoarr.getJSONObject(i);
  		ToolClass.Log(ToolClass.INFO,"EV_SERVER","���¹�����ʾ="+object2.toString(),"server.txt");										
  		final JSONObject zhuheobj=object2;
  		//��һ������ȡVMC_NO������
  		final String DEMO_CONTENT=object2.getString("DEMO_CONTENT");
  		ToolClass.Log(ToolClass.INFO,"EV_SERVER","DEMO_CONTENT="+DEMO_CONTENT,"server.txt");	
  		zhuheobj.put("AttImg", "");
  		  
  		try
  		{	
  			{  				
  				vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(ToolClass.getContext());// ����InaccountDAO����
  			    //����Tb_inaccount���� 
    			Tb_vmc_system_parameter tb_vmc_system_parameter = new Tb_vmc_system_parameter(devID, "", 0,0, 
    					0,0,"",0,0,0,0,0,0,0,"",0,
    					0,0, 0,0,0,"",DEMO_CONTENT);
    			ToolClass.Log(ToolClass.INFO,"EV_SERVER","����DEMO_CONTENT","server.txt");	
    			parameterDAO.updatedemo(tb_vmc_system_parameter); 
  			}
  		}
  		catch (Exception e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  			ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec2=[fail10-1]","server.txt");
  		}
  		
  		//�������������ֱ��浽json��		
  		zhuhedemoArray.put(zhuheobj);
  		
  		
  		//���Ĳ���������һ��������Ϣ
  		demoint++;
  		if(demoint<demoarr.length())
  		{
  			try {
  				updatedemo(demoint);
  			} catch (JSONException e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  			}
  		}
  		else
  		{
  			try {
  				zhuhedemojson.put("List", zhuhedemoArray);
  			} catch (JSONException e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  			}
  			ToolClass.Log(ToolClass.INFO,"EV_SERVER","reczhuhe="+zhuhedemojson.toString(),"server.txt");

  			//�ϴ���server
  		    //�����̷߳�����Ϣ
			Message tomain=mainhand.obtainMessage();
			tomain.what=SETDEMOINFOMAIN;
			tomain.obj=zhuhedemojson.toString();
			mainhand.sendMessage(tomain); // ������Ϣ 			
  		}		
  		return "";
  	}
  	
    //==============
  	//==ȡ����ģ��
    //ȡ����Ƚ����⣬�����������Ƶ�����
  	//==============
  	JSONArray pickuparr=null;
  	JSONArray zhuhepickupArray=null;
  	JSONObject zhuhepickupjson = null; 
  	int pickupint=0;
  	//�ֽ��豸��Ϣ
  	private void pickupArray(String pickuprst) throws JSONException
  	{
  		JSONObject jsonObject = new JSONObject(pickuprst); 
  		pickuparr=jsonObject.getJSONArray("List");
  		pickupint=0;
  		zhuhepickupArray=new JSONArray();
  		zhuhepickupjson = new JSONObject(); 
  		if(pickuparr.length()==0)
  		{
  			//�����̷߳�����Ϣ
  			Message tomain=mainhand.obtainMessage();
  			tomain.what=SETERRFAILPICKUPMAIN;
  			tomain.obj=zhuhepickupjson.toString();
  			mainhand.sendMessage(tomain); // ������Ϣ	
  		}  	   
  	}
  	String PICKUP_CODE="";
  	//����ȡ������Ϣ
  	private String updatepickup(int i,String pick) throws JSONException
  	{  	  		
  		boolean quhuo=false; 
  		PICKUP_CODE=pick;
  		final JSONObject object2=pickuparr.getJSONObject(i);
  		ToolClass.Log(ToolClass.INFO,"EV_SERVER","ȡ�������="+object2.toString(),"server.txt");										
  		final JSONObject zhuheobj=new JSONObject();
  		//��һ������ȡPRODUCT_NO��STAUTS
  		final String PRODUCT_NO=object2.getString("PRODUCT_NO");
  		final int STAUTS=object2.getInt("STAUTS");
  		ToolClass.Log(ToolClass.INFO,"EV_SERVER","ȡ����PRODUCT_NO="+PRODUCT_NO+",STAUTS="+STAUTS,"server.txt");	
  		//����ֵ��ʾ������ȡ��
  		if(STAUTS==0)
  		{
  			//�����л���
  			vmc_columnDAO columnDAO = new vmc_columnDAO(ToolClass.getContext());// ����InaccountDAO����
  			if(columnDAO.getproductCount(PRODUCT_NO)>0)
  			{
  				quhuo=true;
  			}  			 
  		}
  		//��ȡ�ɹ�
  		if(quhuo)
  		{
  			//��һ����ȷ�ϲ���
  			final String out_trade_no=ToolClass.out_trade_no(ToolClass.getContext());	
  			zhuheobj.put("PRODUCT_NO", PRODUCT_NO);
  			zhuheobj.put("out_trade_no", out_trade_no);
  			
  			//�ڶ������ϴ���serverȡ����ʹ�õ�
  			String target17 = httpStr+"/api/savePickupCode";	//Ҫ�ύ��Ŀ���ַ
			final String LAST_EDIT_TIME17=ToolClass.getLasttime();
			//�����̷߳�����Ϣ
			final Message tomain17=mainhand.obtainMessage();
			tomain17.what=SETNONE;
			//�½�Volley 
			mQueue = getRequestQueue();
			//4.׼��������Ϣ����
			StringRequest stringRequest17 = new StringRequest(Method.POST, target17,  new Response.Listener<String>() {  
				@Override  
				public void onResponse(String response) {  
				   
				    //�������ɹ�
					result = response;	//��ȡ���ص��ַ���
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1="+result,"server.txt");
															 
				}  
			}, new Response.ErrorListener() {  
				@Override  
				public void onErrorResponse(VolleyError error) {  
					result = "����ʧ�ܣ�";
					tomain17.what=SETFAILMAIN;
		    	    mainhand.sendMessage(tomain17); // ������Ϣ
		    	    ToolClass.Log(ToolClass.INFO,"EV_SERVER","rec1=[fail17]SETFAILMAIN"+result,"server.txt");
				}  
			}) 
			{  
				@Override  
				protected Map<String, String> getParams() throws AuthFailureError {  
					//3.���params
					Map<String, String> map = new HashMap<String, String>();  
					map.put("Token", Tok);  
					map.put("LAST_EDIT_TIME", LAST_EDIT_TIME17);	
					map.put("PICKUP_CODE", PICKUP_CODE);
					map.put("ORDER_NO", out_trade_no);
					map.put("VMC_NO", vmc_no);
					ToolClass.Log(ToolClass.INFO,"EV_SERVER","Send1=�ϴ�ȡ�������"+map.toString(),"server.txt");
					return map;  
			   }  
			}; 	
			//5.������Ϣ�����͵�������
			mQueue.add(stringRequest17);
			
			
  			//�������������̷߳�����Ϣ
  			Message tomain4=mainhand.obtainMessage();
  			tomain4.what=SETPICKUPMAIN;
  			tomain4.obj=zhuheobj;
  			mainhand.sendMessage(tomain4); // ������Ϣ 
  		}
  		else
  		{
  			//�����̷߳�����Ϣ
  			Message tomain=mainhand.obtainMessage();
  			tomain.what=SETERRFAILPICKUPMAIN;
  			tomain.obj=zhuhepickupjson.toString();
  			mainhand.sendMessage(tomain); // ������Ϣ	
  		}	
  		return "";
  	}
  	
  	
	
}
