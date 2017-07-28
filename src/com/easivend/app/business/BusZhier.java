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

public class BusZhier extends Activity
{
	private final int SPLASH_DISPLAY_LENGHT = 1500; // �ӳ�1.5��
	//���ȶԻ���
	ProgressDialog dialog= null;
	public static BusZhier BusZhierAct=null;
	private final static int REQUEST_CODE=1;//���������ʶ
	TextView txtbuszhiercount=null,txtbuszhiamerount=null,txtbuszhierrst=null,txtbuszhiertime=null;
	ImageButton imgbtnbuszhierqxzf=null,imgbtnbuszhierqtzf=null;
	ImageView ivbuszhier=null;
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
    private String zhifutype = "3";//0�ֽ�1������2֧����������3֧������ά�룬4΢��ɨ��
    private float amount=0;
    //�߳̽���֧������ά�����
    private ExecutorService thread=null;
    private Handler mainhand=null,childhand=null;   
    private String out_trade_no=null;
    Zhifubaohttp zhifubaohttp=null;
    private int iszhier=0;//1�ɹ������˶�ά��,0û�гɹ����ɶ�ά�룬2���ν����Ѿ�����
    private boolean ercheck=false;//true���ڶ�ά����̲߳����У����Ժ�falseû�ж�ά����̲߳���
    private int ispayoutopt=0;//1���ڽ����˱Ҳ���
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// ��title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // ȫ��
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.buszhier);
		BusZhierAct = this;
		AudioSound.playbuszhier();
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
		txtbuszhiercount= (TextView) findViewById(R.id.txtbuszhiercount);
		txtbuszhiercount.setText(String.valueOf(OrderDetail.getShouldNo()));
		txtbuszhiamerount= (TextView) findViewById(R.id.txtbuszhiamerount);
		txtbuszhiamerount.setText(String.valueOf(amount));
		txtbuszhierrst= (TextView) findViewById(R.id.txtbuszhierrst);
		txtbuszhiertime= (TextView) findViewById(R.id.txtbuszhiertime);
		ivbuszhier= (ImageView) findViewById(R.id.ivbuszhier);
		timer.scheduleWithFixedDelay(task, 1, 1, TimeUnit.SECONDS);       // timeTask 
		imgbtnbuszhierqxzf = (ImageButton) findViewById(R.id.imgbtnbuszhierqxzf);
		imgbtnbuszhierqxzf.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {		    	 
		    	if(BusgoodsSelect.BusgoodsSelectAct!=null)
					BusgoodsSelect.BusgoodsSelectAct.finish(); 
		    	finishActivity();
		    }
		});
		imgbtnbuszhierqtzf = (ImageButton) findViewById(R.id.imgbtnbuszhierqtzf);
		imgbtnbuszhierqtzf.setOnClickListener(new OnClickListener() {
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
		//�߳̽���֧������ά�����
		//***********************
		mainhand=new Handler()
		{
			int con=0;
			@Override
			public void handleMessage(Message msg) {
				//barzhifubaotest.setVisibility(View.GONE);
				ercheck=false;
				// TODO Auto-generated method stub		 		
				switch (msg.what)
				{
					case Zhifubaohttp.SETMAIN://���߳̽������߳���Ϣ
						try {
							JSONObject zhuhe=new JSONObject(msg.obj.toString());
							String zhuheout_trade_no=zhuhe.getString("out_trade_no");
							String qr_code=zhuhe.getString("qr_code");
							ToolClass.Log(ToolClass.INFO,"EV_JNI","����֧����=out_trade_no="+out_trade_no+">>zhuheout_trade_no="+zhuheout_trade_no,"log.txt");
							if(zhuheout_trade_no.equals(out_trade_no))
							{
								ivbuszhier.setImageBitmap(ToolClass.createQRImage(qr_code));
								//txtbuszhierrst.setText("���׽��:"+msg.obj.toString());
								txtbuszhierrst.setText("���׽��:��ɨ���ά��");
								iszhier=1;
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					case Zhifubaohttp.SETFAILNETCHILD://���߳̽������߳���Ϣ
						txtbuszhierrst.setText("���׽��:����"+msg.obj.toString()+con);
						con++;
						if(ispayoutopt==1)
						{
							//��¼��־�˱����
							OrderDetail.setRealStatus(3);//��¼�˱�ʧ��
							OrderDetail.setRealCard(0);//��¼�˱ҽ��
							OrderDetail.setDebtAmount(amount);//Ƿ����
							OrderDetail.addLog(BusZhier.this);
							ispayoutopt=0;
							//��������ҳ��
							txtbuszhierrst.setText("���׽��:�˿�ʧ��");
							dialog.dismiss();
							finish();
						}
						break;		
					case Zhifubaohttp.SETPAYOUTMAIN://���߳̽������߳���Ϣ
						if(ispayoutopt==1)
						{
							//��¼��־�˱����
							OrderDetail.setRealStatus(1);//��¼�˱ҳɹ�
							OrderDetail.setRealCard(amount);//��¼�˱ҽ��
							OrderDetail.addLog(BusZhier.this);
							ispayoutopt=0;
							//��������ҳ��
							txtbuszhierrst.setText("���׽��:�˿�ɹ�");
							dialog.dismiss();
							finish();
						}
						break;
					case Zhifubaohttp.SETDELETEMAIN://���߳̽������߳���Ϣ
//						txtbuszhierrst.setText("���׽��:�����ɹ�");
//						timer.shutdown(); 
//						finish();
						break;	
					case Zhifubaohttp.SETQUERYMAINSUCC://���׳ɹ�
						txtbuszhierrst.setText("���׽��:���׳ɹ�");
						//reamin_amount=String.valueOf(amount);
						iszhier=2;
						timer.shutdown(); 
						tochuhuo();
						break;
					case Zhifubaohttp.SETQUERYMAIN://���߳̽������߳���Ϣ
					case Zhifubaohttp.SETFAILPROCHILD://���߳̽������߳���Ϣ
					case Zhifubaohttp.SETFAILBUSCHILD://���߳̽������߳���Ϣ	
					case Zhifubaohttp.SETFAILQUERYPROCHILD://���߳̽������߳���Ϣ
					case Zhifubaohttp.SETFAILQUERYBUSCHILD://���߳̽������߳���Ϣ	
					case Zhifubaohttp.SETFAILPAYOUTPROCHILD://���߳̽������߳���Ϣ		
					case Zhifubaohttp.SETFAILPAYOUTBUSCHILD://���߳̽������߳���Ϣ
					case Zhifubaohttp.SETFAILDELETEPROCHILD://���߳̽������߳���Ϣ		
					case Zhifubaohttp.SETFAILDELETEBUSCHILD://���߳̽������߳���Ϣ	
						//txtbuszhierrst.setText("���׽��:"+msg.obj.toString());
						if(ispayoutopt==1)
						{
							//��¼��־�˱����
							OrderDetail.setRealStatus(3);//��¼�˱�ʧ��
							OrderDetail.setRealCard(0);//��¼�˱ҽ��
							OrderDetail.setDebtAmount(amount);//Ƿ����
							OrderDetail.addLog(BusZhier.this);
							ispayoutopt=0;
							//��������ҳ��
							txtbuszhierrst.setText("���׽��:�˿�ʧ��");
							dialog.dismiss();
							finish();
						}
						break;	
				}				
			}
			
		};	
		//�����û��Լ��������
		zhifubaohttp=new Zhifubaohttp(mainhand);
		thread=Executors.newFixedThreadPool(3);
		thread.execute(zhifubaohttp);	
		//��ʱ
	    new Handler().postDelayed(new Runnable() 
		{
            @Override
            public void run() 
            {            	
        		//���Ͷ���
        		sendzhier();
            }

		}, SPLASH_DISPLAY_LENGHT);		
	}
	
	//���Ͷ���
	private void sendzhier()
	{	
		if(ercheckopt())
  		{
	    	// ����Ϣ���͵����߳���
	    	childhand=zhifubaohttp.obtainHandler();
			Message childmsg=childhand.obtainMessage();
			childmsg.what=Zhifubaohttp.SETCHILD;
			JSONObject ev=null;
			try {
				ev=new JSONObject();
				out_trade_no=ToolClass.out_trade_no(BusZhier.this);// ����InaccountDAO����;
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
	private void queryzhier()
	{
		if(ercheckopt())
  		{
			// ����Ϣ���͵����߳���
	    	childhand=zhifubaohttp.obtainHandler();
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
	//��������
	private void deletezhier()
	{
		//if(ercheckopt())
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<ercheck="+ercheck,"log.txt");
  		{
			// ����Ϣ���͵����߳���
	    	childhand=zhifubaohttp.obtainHandler();
			Message childmsg=childhand.obtainMessage();
			childmsg.what=Zhifubaohttp.SETDELETECHILD;
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
  		txtbuszhierrst.setText("���׽��:�����ɹ�");
  		finish();
	}
	//�˿�
	private void payoutzhier()
	{
		//if(ercheckopt())
		AudioSound.playbuspayout();
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<ercheck="+ercheck,"log.txt");
  		{
			// ����Ϣ���͵����߳���
	    	childhand=zhifubaohttp.obtainHandler();
			Message childmsg=childhand.obtainMessage();
			childmsg.what=Zhifubaohttp.SETPAYOUTCHILD;
			JSONObject ev=null;
			try {
				ev=new JSONObject();
				ev.put("out_trade_no", out_trade_no);		
				ev.put("refund_amount", String.valueOf(amount));
				ev.put("out_request_no", ToolClass.out_trade_no(BusZhier.this));
				Log.i("EV_JNI","Send0.1="+ev.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			childmsg.obj=ev;
			childhand.sendMessage(childmsg);
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
		            recLen--; 
		            txtbuszhiertime.setText("����ʱ:"+recLen); 
		            //�˳�ҳ��
		            if(recLen <= 0)
		            { 
		                timeoutfinishActivity();
		            } 
		            
		            
		            //���Ͳ�ѯ����ָ��
		            if(iszhier==1)
		            {
		                queryLen++;
		                if(queryLen>=2)
		                {
		                	queryLen=0;
		                	queryzhier();
		                }
		            }
		            //���Ͷ�������ָ��
		            else if(iszhier==0)
		            {
		                queryLen++;
		                if(queryLen>=5)
		                {
		                	queryLen=0;
		                	//���Ͷ���
		            		sendzhier();
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
    	if(iszhier==2)
    	{
    		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<zhier�˱Ұ�ť��Ч","log.txt");
    	}
    	else if(iszhier==1)
			deletezhier();
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
    	intent = new Intent(BusZhier.this, BusHuo.class);// ʹ��Accountflag���ڳ�ʼ��Intent
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
			if(resultCode==BusZhier.RESULT_CANCELED)
			{
				Bundle bundle=data.getExtras();
  				int status=bundle.getInt("status");//�������1�ɹ�,0ʧ��
  			    //1.
  				//�����ɹ�,��������
				if(status==1)
				{
					ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<���˿�","log.txt");
					ToolClass.setLAST_CHUHUO(true);
					OrderDetail.addLog(BusZhier.this);	
					AudioSound.playbusfinish();
					finish();
				}
				//����ʧ��,��Ǯ
				else
				{	
					ispayoutopt=1;
					ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<�˿�amount="+amount,"log.txt");
					dialog= ProgressDialog.show(BusZhier.this,"�����˿���","���Ժ�...");
					payoutzhier();//�˿����									
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
