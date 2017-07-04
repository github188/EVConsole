package com.easivend.app.maintain;

import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;

import com.easivend.common.ToolClass;
import com.easivend.http.Weixinghttp;
import com.easivend.http.Yinlianhttp;
import com.example.evconsole.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class YinlianTest extends Activity {
	private ImageView imgweixingtest=null;
	private TextView txtweixingtest=null;
	private EditText edtweixingtest=null;
	private Button btnweixingtestok=null,btnweixingtestcancel=null,btnweixingtestquery=null
			,btnweixingtestdelete=null,btnweixingtestpayout=null;
	private ProgressBar barweixingtest=null;
	
	private Handler mainhand=null,childhand=null;
	private String out_trade_no=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yinliantest);	
		//���ú������������Ĳ��ֲ���
		this.setRequestedOrientation(ToolClass.getOrientation());
		mainhand=new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				barweixingtest.setVisibility(View.GONE);
				// TODO Auto-generated method stub				
				switch (msg.what)
				{
					case Yinlianhttp.SETMAIN://���߳̽������߳���Ϣ
						try {
							JSONObject zhuhe=new JSONObject(msg.obj.toString());
							imgweixingtest.setImageBitmap(ToolClass.createQRImage(zhuhe.getString("code_url")));
							txtweixingtest.setText("������ά����:"+zhuhe.toString());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}						
						break;
					case Yinlianhttp.SETFAILNETCHILD://���߳̽������߳���Ϣ
						txtweixingtest.setText("������ά����:"+msg.obj.toString());
						break;	
					case Yinlianhttp.SETPAYOUTMAIN://���߳̽������߳���Ϣ
						txtweixingtest.setText("������ά����:�˿�ɹ�");
						break;
					case Yinlianhttp.SETDELETEMAIN://���߳̽������߳���Ϣ
						txtweixingtest.setText("������ά����:�����ɹ�");
						break;	
					case Yinlianhttp.SETQUERYMAINSUCC://���߳̽������߳���Ϣ	
						txtweixingtest.setText("������ά����:���׳ɹ�");
						break;		
					case Yinlianhttp.SETFAILPROCHILD://���߳̽������߳���Ϣ
					case Yinlianhttp.SETFAILBUSCHILD://���߳̽������߳���Ϣ	
					case Yinlianhttp.SETFAILQUERYPROCHILD://���߳̽������߳���Ϣ
					case Yinlianhttp.SETFAILQUERYBUSCHILD://���߳̽������߳���Ϣ		
					case Yinlianhttp.SETQUERYMAIN://���߳̽������߳���Ϣ	
					case Yinlianhttp.SETFAILPAYOUTPROCHILD://���߳̽������߳���Ϣ		
					case Yinlianhttp.SETFAILPAYOUTBUSCHILD://���߳̽������߳���Ϣ
					case Yinlianhttp.SETFAILDELETEPROCHILD://���߳̽������߳���Ϣ		
					case Yinlianhttp.SETFAILDELETEBUSCHILD://���߳̽������߳���Ϣ	
						txtweixingtest.setText("������ά����:"+msg.obj.toString());
						break;		
				}				
			}
			
		};	
		//�����û��Լ��������
		final Yinlianhttp weixinghttp=new Yinlianhttp(mainhand);
		ExecutorService thread=Executors.newFixedThreadPool(3);
		thread.execute(weixinghttp);
		imgweixingtest = (ImageView) findViewById(R.id.imgweixingtest);
		edtweixingtest = (EditText) findViewById(R.id.edtweixingtest);
		txtweixingtest = (TextView) findViewById(R.id.txtweixingtest);
		barweixingtest= (ProgressBar) findViewById(R.id.barweixingtest);
		//���id��Ϣ
		Intent intent=getIntent();
		final String id=intent.getStringExtra("id");
		Log.i("EV_JNI","Send0.0="+id);
		//���Ͷ���
		btnweixingtestok = (Button)findViewById(R.id.btnweixingtestok);
		btnweixingtestok.setOnClickListener(new OnClickListener() {			
		    @Override
		    public void onClick(View arg0) {
		    	barweixingtest.setVisibility(View.VISIBLE);
		    	// ����Ϣ���͵����߳���
		    	childhand=weixinghttp.obtainHandler();
				Message childmsg=childhand.obtainMessage();
				childmsg.what=Weixinghttp.SETCHILD;
				JSONObject ev=null;
				try {
					ev=new JSONObject();
					SimpleDateFormat tempDate = new SimpleDateFormat("yyyyMMddHHmmssSSS"); //��ȷ������ 
			        String datetime = tempDate.format(new java.util.Date()).toString(); 					
			        out_trade_no=id+datetime;
			        ev.put("out_trade_no", out_trade_no);
					ev.put("total_fee", edtweixingtest.getText());
					Log.i("EV_JNI","Send0.1="+ev.toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				childmsg.obj=ev;
				childhand.sendMessage(childmsg);
		    }
		});
		//��ѯ
		btnweixingtestquery = (Button)findViewById(R.id.btnweixingtestquery);	
		btnweixingtestquery.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	barweixingtest.setVisibility(View.VISIBLE);
		    	// ����Ϣ���͵����߳���
		    	childhand=weixinghttp.obtainHandler();
				Message childmsg=childhand.obtainMessage();
				childmsg.what=Weixinghttp.SETQUERYCHILD;
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
		});
		//�˿�
		btnweixingtestpayout = (Button)findViewById(R.id.btnweixingtestpayout);	
		btnweixingtestpayout.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	barweixingtest.setVisibility(View.VISIBLE);
		    	// ����Ϣ���͵����߳���
		    	childhand=weixinghttp.obtainHandler();
				Message childmsg=childhand.obtainMessage();
				childmsg.what=Weixinghttp.SETPAYOUTCHILD;
				JSONObject ev=null;
				try {
					ev=new JSONObject();
					ev.put("out_trade_no", out_trade_no);		
					//ev.put("out_trade_no", "000120150301113215800");
					ev.put("total_fee", edtweixingtest.getText());
					ev.put("refund_fee", edtweixingtest.getText());
					SimpleDateFormat tempDate = new SimpleDateFormat("yyyyMMddHHmmssSSS"); //��ȷ������ 
			        String datetime = tempDate.format(new java.util.Date()).toString(); 					
			        ev.put("out_refund_no", id+datetime);
					Log.i("EV_JNI","Send0.1="+ev.toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				childmsg.obj=ev;
				childhand.sendMessage(childmsg);
		    }
		});	
		//�˳�
		btnweixingtestcancel = (Button)findViewById(R.id.btnweixingtestcancel);		
		btnweixingtestcancel.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	finish();
		    }
		});	
	}

}
