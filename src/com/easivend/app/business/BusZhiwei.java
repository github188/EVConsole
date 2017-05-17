package com.easivend.app.business;

import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import com.easivend.common.AudioSound;
import com.easivend.common.OrderDetail;
import com.easivend.common.ToolClass;
import com.easivend.http.Weixinghttp;
import com.easivend.http.Zhifubaohttp;
import com.example.evconsole.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class BusZhiwei extends Activity 
{
	private final int SPLASH_DISPLAY_LENGHT = 1500; // �ӳ�1.5��
	//���ȶԻ���
	ProgressDialog dialog= null;
	public static BusZhiwei BusZhiweiAct=null;
	private final static int REQUEST_CODE=1;//���������ʶ
	TextView txtbuszhiweicount=null,txtbuszhiweirount=null,txtbuszhiweirst=null,txtbuszhiweitime=null;
	ImageButton imgbtnbuszhiweiqxzf=null,imgbtnbuszhiweiqtzf=null;
	ImageView ivbuszhiwei=null;
	ImageView imgbtnbusgoodsback=null;
	private final int SPLASH_TIMEOUT_LENGHT = 5*60; //  5*60�ӳ�5����
	private int recLen = SPLASH_TIMEOUT_LENGHT; 
	private int queryLen = 0; 
    ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);
//	private String proID = null;
//	private String productID = null;
//	private String proType = null;
//	private String cabID = null;
//	private String huoID = null;
//    private String prosales = null;
//    private String count = null;
//    private String reamin_amount = null;
    private String zhifutype = "4";//0�ֽ�1������2֧����������3֧������ά�룬4΢��ɨ��
    private float amount=0;
    //�߳̽���΢�Ŷ�ά�����
    private ExecutorService thread=null;
    private Handler mainhand=null,childhand=null;   
    private String out_trade_no=null;
    Weixinghttp weixinghttp=null;
    private int iszhiwei=0;//1�ɹ������˶�ά��,0û�гɹ����ɶ�ά�룬2���ν����Ѿ�����
    private boolean ercheck=false;//true���ڶ�ά����̲߳����У����Ժ�falseû�ж�ά����̲߳���
    private int ispayoutopt=0;//1���ڽ����˱Ҳ���,0δ�����˱Ҳ���
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// ��title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // ȫ��
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.buszhiwei);
		BusZhiweiAct = this;
		AudioSound.playbuszhiwei();
		//����Ʒҳ����ȡ����ѡ�е���Ʒ
//		Intent intent=getIntent();
//		Bundle bundle=intent.getExtras();
//		proID=bundle.getString("proID");
//		productID=bundle.getString("productID");
//		proType=bundle.getString("proType");
//		cabID=bundle.getString("cabID");
//		huoID=bundle.getString("huoID");
//		prosales=bundle.getString("prosales");
//		count=bundle.getString("count");
//		reamin_amount=bundle.getString("reamin_amount");
		amount=OrderDetail.getShouldPay()*OrderDetail.getShouldNo();
		txtbuszhiweicount= (TextView) findViewById(R.id.txtbuszhiweicount);
		txtbuszhiweicount.setText(String.valueOf(OrderDetail.getShouldNo()));
		txtbuszhiweirount= (TextView) findViewById(R.id.txtbuszhiweirount);
		txtbuszhiweirount.setText(String.valueOf(amount));
		txtbuszhiweirst= (TextView) findViewById(R.id.txtbuszhiweirst);
		txtbuszhiweitime= (TextView) findViewById(R.id.txtbuszhiweitime);
		ivbuszhiwei= (ImageView) findViewById(R.id.ivbuszhiwei);
		timer.scheduleWithFixedDelay(task, 1, 1, TimeUnit.SECONDS);       // timeTask 
		imgbtnbuszhiweiqxzf = (ImageButton) findViewById(R.id.imgbtnbuszhiweiqxzf);
		imgbtnbuszhiweiqxzf.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {		    	 
		    	if(BusgoodsSelect.BusgoodsSelectAct!=null)
					BusgoodsSelect.BusgoodsSelectAct.finish(); 
		    	finishActivity();		    	
		    }
		});
		imgbtnbuszhiweiqtzf = (ImageButton) findViewById(R.id.imgbtnbuszhiweiqtzf);
		imgbtnbuszhiweiqtzf.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	finishActivity();		    	
		    }
		});
		this.imgbtnbusgoodsback=(ImageView)findViewById(R.id.imgbtnbusgoodsback);
		imgbtnbusgoodsback.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {		    	
		    	if(BusgoodsSelect.BusgoodsSelectAct!=null)
					BusgoodsSelect.BusgoodsSelectAct.finish(); 
		    	finishActivity();
		    }
		});
		//***********************
		//�߳̽���΢�Ŷ�ά�����
		//***********************
		mainhand=new Handler()
		{
			int con=0;
			@Override
			public void handleMessage(Message msg) {
				//barweixingtest.setVisibility(View.GONE);
				ercheck=false;
				// TODO Auto-generated method stub				
				switch (msg.what)
				{
					case Weixinghttp.SETMAIN://���߳̽������߳���Ϣ
						try {
							JSONObject zhuhe=new JSONObject(msg.obj.toString());
							String zhuheout_trade_no=zhuhe.getString("out_trade_no");
							String code_url=zhuhe.getString("code_url");
							ToolClass.Log(ToolClass.INFO,"EV_JNI","����΢��=out_trade_no="+out_trade_no+">>zhuheout_trade_no="+zhuheout_trade_no,"log.txt");						       
							if(zhuheout_trade_no.equals(out_trade_no))
							{
								ivbuszhiwei.setImageBitmap(ToolClass.createQRImage(code_url));
								//txtbuszhiweirst.setText("���׽��:"+msg.obj.toString());
								txtbuszhiweirst.setText("���׽��:��ɨ���ά��");
								iszhiwei=1;
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					case Weixinghttp.SETFAILNETCHILD://���߳̽������߳���Ϣ
						txtbuszhiweirst.setText("���׽��:����"+msg.obj.toString()+con);
						con++;
						if(ispayoutopt==1)
						{
							//��¼��־�˱����
							OrderDetail.setRealStatus(3);//��¼�˱�ʧ��
							OrderDetail.setRealCard(0);//��¼�˱ҽ��
							OrderDetail.setDebtAmount(amount);//Ƿ����
							OrderDetail.addLog(BusZhiwei.this);
							ispayoutopt=0;
							//��������ҳ��
							txtbuszhiweirst.setText("���׽��:�˿�ʧ��");
							dialog.dismiss();
							finish();
						}
						break;	
					case Weixinghttp.SETPAYOUTMAIN://���߳̽������߳���Ϣ
						if(ispayoutopt==1)
						{
							//��¼��־�˱����
							OrderDetail.setRealStatus(1);//��¼�˱ҳɹ�
							OrderDetail.setRealCard(amount);//��¼�˱ҽ��
							OrderDetail.addLog(BusZhiwei.this);
							ispayoutopt=0;
							//��������ҳ��
							txtbuszhiweirst.setText("���׽��:�˿�ɹ�");
							dialog.dismiss();
							finish();
						}
						break;
					case Weixinghttp.SETDELETEMAIN://���߳̽������߳���Ϣ
//						txtbuszhiweirst.setText("���׽��:�����ɹ�");
//						timer.shutdown(); 
//						finish();
						break;	
					case Weixinghttp.SETQUERYMAINSUCC://���߳̽������߳���Ϣ		
						txtbuszhiweirst.setText("���׽��:���׳ɹ�");
						//reamin_amount=String.valueOf(amount);
						iszhiwei=2;
						timer.shutdown(); 
						tochuhuo();
						break;
					case Weixinghttp.SETFAILPROCHILD://���߳̽������߳���Ϣ
					case Weixinghttp.SETFAILBUSCHILD://���߳̽������߳���Ϣ	
					case Weixinghttp.SETFAILQUERYPROCHILD://���߳̽������߳���Ϣ
					case Weixinghttp.SETFAILQUERYBUSCHILD://���߳̽������߳���Ϣ		
					case Weixinghttp.SETQUERYMAIN://���߳̽������߳���Ϣ	
					case Weixinghttp.SETFAILPAYOUTPROCHILD://���߳̽������߳���Ϣ		
					case Weixinghttp.SETFAILPAYOUTBUSCHILD://���߳̽������߳���Ϣ
					case Weixinghttp.SETFAILDELETEPROCHILD://���߳̽������߳���Ϣ		
					case Weixinghttp.SETFAILDELETEBUSCHILD://���߳̽������߳���Ϣ		
						//txtbuszhiweirst.setText("���׽��:"+msg.obj.toString());
						if(ispayoutopt==1)
						{
							//��¼��־�˱����
							OrderDetail.setRealStatus(3);//��¼�˱�ʧ��
							OrderDetail.setRealCard(0);//��¼�˱ҽ��
							OrderDetail.setDebtAmount(amount);//Ƿ����
							OrderDetail.addLog(BusZhiwei.this);
							ispayoutopt=0;
							//��������ҳ��
							txtbuszhiweirst.setText("���׽��:�˿�ʧ��");
							dialog.dismiss();
							finish();
						}
						break;		
				}				
			}
			
		};
		//�����û��Լ��������
		weixinghttp=new Weixinghttp(mainhand);
		thread=Executors.newFixedThreadPool(3);
		thread.execute(weixinghttp);
		//��ʱ
	    new Handler().postDelayed(new Runnable() 
		{
            @Override
            public void run() 
            {            	
            	//���Ͷ���
        		sendzhiwei();
            }

		}, SPLASH_DISPLAY_LENGHT);			
	}
	//���Ͷ���
	private void sendzhiwei()
	{	
		if(ercheckopt())
  		{
	    	// ����Ϣ���͵����߳���
	    	childhand=weixinghttp.obtainHandler();
			Message childmsg=childhand.obtainMessage();
			childmsg.what=Weixinghttp.SETCHILD;
			JSONObject ev=null;
			try {
				ev=new JSONObject();
				out_trade_no=ToolClass.out_trade_no(BusZhiwei.this);
		        ev.put("out_trade_no", out_trade_no);
				ev.put("total_fee", String.valueOf(amount));
				Log.i("EV_JNI","Send0.1="+ev.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			childmsg.obj=ev;
			childhand.sendMessage(childmsg);
  		}
	}
	//��ѯ����
	private void queryzhiwei()
	{
		if(ercheckopt())
  		{
			// ����Ϣ���͵����߳���
	    	childhand=weixinghttp.obtainHandler();
			Message childmsg=childhand.obtainMessage();
			childmsg.what=Zhifubaohttp.SETQUERYCHILD;
			JSONObject ev=null;
			try {
				ev=new JSONObject();
				ev.put("out_trade_no", out_trade_no);		
				//ev.put("out_trade_no", "000120150301113215800");	
				Log.i("EV_JNI","Send0.1="+ev.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			childmsg.obj=ev;
			childhand.sendMessage(childmsg);
  		}
	}
	//�˿��
	private void payoutzhiwei()
	{
		//if(ercheckopt())
		AudioSound.playbuspayout();
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<ercheck="+ercheck,"log.txt");
  		{
			// ����Ϣ���͵����߳���
	    	childhand=weixinghttp.obtainHandler();
			Message childmsg=childhand.obtainMessage();
			childmsg.what=Zhifubaohttp.SETPAYOUTCHILD;
			JSONObject ev=null;
			try {
				ev=new JSONObject();
				ev.put("out_trade_no", out_trade_no);		
				ev.put("total_fee", String.valueOf(amount));
				ev.put("refund_fee", String.valueOf(amount));
				ev.put("out_refund_no", ToolClass.out_trade_no(BusZhiwei.this));
				Log.i("EV_JNI","Send0.1="+ev.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			childmsg.obj=ev;
			childhand.sendMessage(childmsg);
  		}  		
	}
	//��������
	private void deletezhiwei()
	{
		//if(ercheckopt())
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<ercheck="+ercheck,"log.txt");
  		{
			// ����Ϣ���͵����߳���
			childhand=weixinghttp.obtainHandler();
			Message childmsg=childhand.obtainMessage();
			childmsg.what=Weixinghttp.SETDELETECHILD;
			JSONObject ev=null;
			try {
				ev=new JSONObject();
				ev.put("out_trade_no", out_trade_no);		
				//ev.put("out_trade_no", "000120150301092857698");	
				Log.i("EV_JNI","Send0.1="+ev.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			childmsg.obj=ev;
			childhand.sendMessage(childmsg);
  		}
  		txtbuszhiweirst.setText("���׽��:�����ɹ�");
		finish();
	}
	
	//���õ���ʱ��ʱ��
    TimerTask task = new TimerTask() { 
        @Override 
        public void run() { 
  
            runOnUiThread(new Runnable() {      // UI thread 

		         @Override 
		        public void run() 
		        { 
		            recLen--; 
		            txtbuszhiweitime.setText("����ʱ:"+recLen); 
		            //�˳�ҳ��
		            if(recLen <= 0)
		            { 
		                timeoutfinishActivity();
		            } 
		            //���Ͳ�ѯ����ָ��
		            if(iszhiwei==1)
		            {
		                queryLen++;
		                if(queryLen>=4)
		                {
		                	queryLen=0;
		                	queryzhiwei();
		                }
		            }
		            //���Ͷ�������ָ��
		            else if(iszhiwei==0)
		            {
		                queryLen++;
		                if(queryLen>=10)
		                {
		                	queryLen=0;
		                	//���Ͷ���
		            		sendzhiwei();
		                }
		            }
		        }  
            });
        }
    };
	//��������
	private void finishActivity()
	{	
		//�������ɨ���Ѿ����������Թ����򲻽����˿����
    	if(iszhiwei==2)
    	{
    		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<zhiwei�˱Ұ�ť��Ч","log.txt");
    	}
    	else if(iszhiwei==1)
			deletezhiwei();
		else 
		{
			finish();	
		}
	}
	//���ڳ�ʱ�Ľ�������
	private void timeoutfinishActivity()
	{
		finishActivity();
	}
	
	//��������ҳ��
	private void tochuhuo()
	{
		Intent intent = null;// ����Intent����                
    	intent = new Intent(BusZhiwei.this, BusHuo.class);// ʹ��Accountflag���ڳ�ʼ��Intent    	
//    	intent.putExtra("out_trade_no", out_trade_no);
//    	intent.putExtra("proID", proID);
//    	intent.putExtra("productID", productID);
//    	intent.putExtra("proType", proType);
//    	intent.putExtra("cabID", cabID);
//    	intent.putExtra("huoID", huoID);
//    	intent.putExtra("prosales", prosales);
//    	intent.putExtra("count", count);
//    	intent.putExtra("reamin_amount", reamin_amount);
//    	intent.putExtra("zhifutype", zhifutype);
    	OrderDetail.setOrdereID(out_trade_no);
    	OrderDetail.setPayType(Integer.parseInt(zhifutype));
    	OrderDetail.setSmallCard(amount);
    	startActivityForResult(intent, REQUEST_CODE);// ��Accountflag
	}
	//�ж��Ƿ��ڶ�ά����̲߳�����,true��ʾ���Բ�����,false���ܲ���
  	private boolean ercheckopt()
  	{
  		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<ercheck="+ercheck,"log.txt");
  		if(ercheck==false)
  		{
  			ercheck=true;
  			return true;
  		}
  		else
  		{
  			return false;
		}
  	}
    //����BusHuo������Ϣ
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode==REQUEST_CODE)
		{
			if(resultCode==BusZhiwei.RESULT_CANCELED)
			{
				Bundle bundle=data.getExtras();
  				int status=bundle.getInt("status");//�������1�ɹ�,0ʧ��  				
  				//1.
  				//�����ɹ�,��������
				if(status==1)
				{
					ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<���˿�","log.txt");
					ToolClass.setLAST_CHUHUO(true);
					OrderDetail.addLog(BusZhiwei.this);	
					AudioSound.playbusfinish();
					finish();
				}
				//����ʧ��,��Ǯ
				else
				{
					ispayoutopt=1;
					ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<�˿�amount="+amount,"log.txt");
					dialog= ProgressDialog.show(BusZhiwei.this,"�����˿���","���Ժ�...");
					////�˿�
					payoutzhiwei();										
				}
			}			
		}
	}
	
	@Override
	protected void onDestroy() {
  		timer.shutdown(); 
		super.onDestroy();		
	}
}
