package com.easivend.app.business;

import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.easivend.app.maintain.CahslessTest;
import com.easivend.common.AudioSound;
import com.easivend.common.OrderDetail;
import com.easivend.common.ToolClass;
import com.easivend.dao.vmc_system_parameterDAO;
import com.easivend.model.Tb_vmc_system_parameter;
import com.example.evconsole.R;
import com.landfone.common.utils.IUserCallback;
import com.landfoneapi.mispos.Display;
import com.landfoneapi.mispos.DisplayType;
import com.landfoneapi.mispos.ErrCode;
import com.landfoneapi.mispos.LfMISPOSApi;
import com.landfoneapi.protocol.pkg.REPLY;
import com.landfoneapi.protocol.pkg._04_GetRecordReply;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class BusZhipos extends Activity 
{
	private final int SPLASH_DISPLAY_LENGHT = 200; // �ӳ�1.5��
	//���ȶԻ���
	ProgressDialog dialog= null;
	public static BusZhipos BusZhiposAct=null;
	private final static int REQUEST_CODE=1;//���������ʶ
	TextView txtbuszhiposcount=null,txtbuszhiposAmount=null,txtbuszhipostime=null,
			txtbuszhipostsxx=null;
	ImageButton imgbtnbuszhiposqxzf=null,imgbtnbuszhiposqtzf=null;
	ImageView imgbtnbusgoodsback=null;
	private final int SPLASH_TIMEOUT_LENGHT = 5*60; //  5*60�ӳ�5����
	private int recLen = SPLASH_TIMEOUT_LENGHT; 
	private int queryLen = 0; 
	private int ispayoutopt=0;//1���ڽ����˱Ҳ���,0δ�����˱Ҳ���
    ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);
    
//	private String proID = null;
//	private String productID = null;
//	private String proType = null;
//	private String cabID = null;
//	private String huoID = null;
//    private String prosales = null;
//    private String count = null;
//    private String reamin_amount = null;
    private int isPossel=0;//0û������Pos��1������Pos�в�ѯ����,����ֵ������posû�в�ѯ����
    private String zhifutype = "1";//0�ֽ�1������2֧����������3֧������ά�룬4΢��ɨ��
	float amount=0;//��Ʒ��Ҫ֧�����
	private LfMISPOSApi mMyApi = new LfMISPOSApi();
    private Handler posmainhand=null;
    private int iszhipos=0;//1�ɹ������˿ۿ�����,0û�з��ͳɹ��ۿ�����2ˢ���ۿ��Ѿ���ɲ��ҽ���㹻
    private String out_trade_no=null;
    //�˿����
	private String rfd_card_no = "";
	private String rfd_spec_tmp_serial = "";
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// ��title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // ȫ��
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.buszhipos);
		BusZhiposAct = this;
		AudioSound.playbuszhipos();
		timer.scheduleWithFixedDelay(task, 1, 1, TimeUnit.SECONDS);       // timeTask 
		amount=OrderDetail.getShouldPay()*OrderDetail.getShouldNo();
		txtbuszhiposcount= (TextView) findViewById(R.id.txtbuszhiposcount);
		txtbuszhiposcount.setText(String.valueOf(OrderDetail.getShouldNo()));
		txtbuszhiposAmount= (TextView) findViewById(R.id.txtbuszhiposAmount);
		txtbuszhiposAmount.setText(String.valueOf(amount));
		txtbuszhipostime = (TextView) findViewById(R.id.txtbuszhipostime);
		txtbuszhipostsxx = (TextView) findViewById(R.id.txtbuszhipostsxx);
		out_trade_no=ToolClass.out_trade_no(BusZhipos.this);// ����InaccountDAO����;
		imgbtnbuszhiposqxzf = (ImageButton) findViewById(R.id.imgbtnbuszhiposqxzf);
		imgbtnbuszhiposqxzf.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {		    	
		    	if(BusgoodsSelect.BusgoodsSelectAct!=null)
					BusgoodsSelect.BusgoodsSelectAct.finish(); 
		    	finishActivity();
		    }
		});
		imgbtnbuszhiposqtzf = (ImageButton) findViewById(R.id.imgbtnbuszhiposqtzf);
		imgbtnbuszhiposqtzf.setOnClickListener(new OnClickListener() {
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
		posmainhand=new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub				
				switch (msg.what) 
				{
					case CahslessTest.OPENSUCCESS:
						break;
					case CahslessTest.OPENFAIL:	
						break;
					case CahslessTest.CLOSESUCCESS:
						break;
					case CahslessTest.CLOSEFAIL:	
						break;
					case CahslessTest.COSTSUCCESS:
						txtbuszhipostsxx.setText("��ʾ��Ϣ���������");
						iszhipos=2;
						//��ʱ
					    new Handler().postDelayed(new Runnable() 
						{
				            @Override
				            public void run() 
				            {         
				            	//��������ȡ������Ϣ
								mMyApi.pos_getrecord("000000000000000", "00000000","000000", mIUserCallback);
							}

						}, 300);
						break;
					case CahslessTest.COSTFAIL:	
						txtbuszhipostsxx.setText("��ʾ��Ϣ���ۿ�ʧ��");
						iszhipos=0;
						break;
					case CahslessTest.QUERYSUCCESS:
					case CahslessTest.QUERYFAIL:	
						//txtbuszhipostsxx.setText("������Ϣ��"+SpecInfoField);
						tochuhuo();						
						break;
					case CahslessTest.DELETESUCCESS:
					case CahslessTest.DELETEFAIL:	
						//��ʱ
					    new Handler().postDelayed(new Runnable() 
						{
				            @Override
				            public void run() 
				            {         
				            	ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity �رն�����","com.txt");
								finish();
							}

						}, 300);						
						break;						
					case CahslessTest.PAYOUTSUCCESS:
						if(ispayoutopt==1)
						{
							//��¼��־�˱����
							OrderDetail.setRealStatus(1);//��¼�˱ҳɹ�
							OrderDetail.setRealCard(amount);//��¼�˱ҽ��
							OrderDetail.addLog(BusZhipos.this);
							ispayoutopt=0;
							//��������ҳ��
							txtbuszhipostsxx.setText("��ʾ��Ϣ���˿�ɹ�");
							//��ʱ
						    new Handler().postDelayed(new Runnable() 
							{
					            @Override
					            public void run() 
					            {         
					            	ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity �رն�����","com.txt");
					            	dialog.dismiss();
									finish();
								}

							}, 300);							
						}
						break;
					case CahslessTest.PAYOUTFAIL:	
						if(ispayoutopt==1)
						{
							//��¼��־�˱����
							OrderDetail.setRealStatus(3);//��¼�˱�ʧ��
							OrderDetail.setRealCard(0);//��¼�˱ҽ��
							OrderDetail.setDebtAmount(amount);//Ƿ����
							OrderDetail.addLog(BusZhipos.this);
							ispayoutopt=0;
							//��������ҳ��
							txtbuszhipostsxx.setText("��ʾ��Ϣ���˿�ɹ�");
							//��ʱ
						    new Handler().postDelayed(new Runnable() 
							{
					            @Override
					            public void run() 
					            {         
					            	ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity �رն�����","com.txt");
					            	dialog.dismiss();
									finish();
								}

							}, 300);
						}
						break;		
				}
			}
		};
		//�򿪴���
		ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity �򿪶�����"+ToolClass.getCardcom(),"com.txt");
		vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(BusZhipos.this);// ����InaccountDAO����
	    // ��ȡ����������Ϣ�����洢��List���ͼ�����
    	Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
    	if(tb_inaccount!=null)
    	{    		
    		if(tb_inaccount.getZhifubaofaca()>0)
    		{
    			if(
                        (ToolClass.isEmptynull(ToolClass.getCardcom())==false)
                        &&(ToolClass.getCardcom().equals("null")==false)
                        )			    
    			{
    				isPossel=tb_inaccount.getZhifubaofaca();
	    			ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity �򿪶�����"+ToolClass.getCardcom(),"com.txt");
	    			//�򿪴���
    	    		int posipport=(ToolClass.getPosipport().equals(""))?0:Integer.parseInt(ToolClass.getPosipport());
    		        //ip���˿ڡ����ڡ������ʱ���׼ȷ"121.40.30.62", 18080
    				mMyApi.pos_init(ToolClass.getPosip(), posipport
    						,ToolClass.getCardcom(), "9600", mIUserCallback); 	   			
    			}
    		}
    	}				
		//��ʱ
	    new Handler().postDelayed(new Runnable() 
		{
            @Override
            public void run() 
            {         
            	txtbuszhipostsxx.setText("��ʾ��Ϣ����ˢ��");
            	if(isPossel==1)//��Ա��
                {
            		ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity �������ۿ�="+amount+"[pos_purchase]<<amount_fen="+ToolClass.MoneySend(amount)
            				+"type="+0,"com.txt");
                    mMyApi.pos_purchase(ToolClass.MoneySend(amount), 0,mIUserCallback);
                }
                else if(isPossel>1)//���п�
                {
                	ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity �������ۿ�="+amount+"[pos_purchase]<<amount_fen="+ToolClass.MoneySend(amount)
                			+"type="+1,"com.txt");
                    mMyApi.pos_purchase(ToolClass.MoneySend(amount), 1,mIUserCallback);
                }
		    	iszhipos=1;
            }

		}, SPLASH_DISPLAY_LENGHT);
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
		            txtbuszhipostime.setText("����ʱ:"+recLen); 
		            //�˳�ҳ��
		            if(recLen <= 0)
		            { 
		            	timeoutfinishActivity();
		            } 
		            
		            
		            //���Ͳ�ѯ����ָ��
		            if(iszhipos==1)
		            {		                
		            }
		            //���Ͷ�������ָ��
		            else if(iszhipos==0)
		            {		              
		            }
		        } 
	          });
	      }      
	  };
	//��������
	private void finishActivity()
	{
		//�������ɨ���Ѿ����������Թ����򲻽����˿����
    	if(iszhipos==2)
    	{
    		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<zhier�˱Ұ�ť��Ч","log.txt");
    	}
    	else if(iszhipos==1)
    		deletezhipos();
		else 
		{			
			ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity �رն�����","com.txt");
			finish();
		}
	}
	//���ڳ�ʱ�Ľ�������
	private void timeoutfinishActivity()
	{
		finishActivity();
	}
	
	//��������
  	private void deletezhipos()
  	{
  		ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity ����������ˢ��ǰ��..","com.txt");
    	mMyApi.pos_cancel();
  	}
  	
    //�˿��
  	private void payoutzhipos()
  	{
  		ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity �������˿�="+amount,"com.txt");
  		AudioSound.playbuspayout();
  	    if(isPossel==1)//��Ա��
  		{
  			mMyApi.pos_refund("000000000000000", "00000000",rfd_card_no,ToolClass.MoneySend(amount),rfd_spec_tmp_serial,0, mIUserCallback);
  		}
  		else if(isPossel>1)//���п�
  		{
            mMyApi.pos_refund("000000000000000", "00000000",rfd_card_no,1,"      ",1, mIUserCallback);
        }
  	}
	
	//�ӿڷ���
  	private IUserCallback mIUserCallback = new IUserCallback(){
  		@Override
  		public void onResult(REPLY rst) 
  		{
  			if(rst!=null) 
  			{
  				Message childmsg=posmainhand.obtainMessage();
  				//info(rst.op + ":" + rst.code + "," + rst.code_info);
  				//��������ʶ����LfMISPOSApi�¡�OP_����ͷ�ľ�̬�������磺LfMISPOSApi.OP_INIT��LfMISPOSApi.OP_PURCHASE�ȵ�
  				//�򿪴���
  				if(rst.op.equals(LfMISPOSApi.OP_INIT))
  				{
  					//�����������Ϣ��code��code_info�ķ���/˵������com.landfoneapi.mispos.ErrCode
  					if(rst.code.equals(ErrCode._00.getCode())){//����00������ɹ�
  						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity �򿪳ɹ�"+ToolClass.getCardcom(),"com.txt");
  						childmsg.what=CahslessTest.OPENSUCCESS;
  						childmsg.obj="�򿪳ɹ�"+ToolClass.getCardcom();
  					}else{
  						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity ��ʧ��"+ToolClass.getCardcom()+",code:"+rst.code+",info:"+rst.code_info,"com.txt");						
  						childmsg.what=CahslessTest.OPENFAIL;
  						childmsg.obj="��ʧ��"+ToolClass.getCardcom()+",code:"+rst.code+",info:"+rst.code_info;
  					}
  				}
  				//�رմ���
  				else if(rst.op.equals(LfMISPOSApi.OP_RELEASE))
  				{
  					if(rst.code.equals(ErrCode._00.getCode())){//����00������ɹ�
  						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity �رճɹ�","com.txt");
  						childmsg.what=CahslessTest.CLOSESUCCESS;
  						childmsg.obj="�رճɹ�";
  					}else{
  						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity �ر�ʧ��,code:"+rst.code+",info:"+rst.code_info,"com.txt");						
  						childmsg.what=CahslessTest.CLOSEFAIL;
  						childmsg.obj="�ر�ʧ��,code:"+rst.code+",info:"+rst.code_info;
  					}
  				}
  				//�ۿ�
  				else if(rst.op.equals(LfMISPOSApi.OP_PURCHASE))
  				{
  					if(rst.code.equals(ErrCode._00.getCode())){//����00������ɹ�
  						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity �ۿ�ɹ�","com.txt");
  						childmsg.what=CahslessTest.COSTSUCCESS;
  						childmsg.obj="�ۿ�ɹ�";
  					}
  					else if(rst.code.equals(ErrCode._XY.getCode())){
  						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity �����ɹ�","com.txt");
  						childmsg.what=CahslessTest.DELETESUCCESS;
  						childmsg.obj="�����ɹ�";
  					}
  					else
  					{
  						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity �ۿ�ʧ��,code:"+rst.code+",info:"+rst.code_info,"com.txt");
  						childmsg.what=CahslessTest.COSTFAIL;
  						childmsg.obj="�ۿ�ʧ��,code:"+rst.code+",info:"+rst.code_info;
  					}
  				}
  		    	//�˿�
  				else if(rst.op.equals(LfMISPOSApi.OP_REFUND))
  				{
  					//����00������ɹ�
					if(rst.code.equals(ErrCode._00.getCode()))
					{
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity �˿�ɹ�","com.txt");
						childmsg.what=CahslessTest.PAYOUTSUCCESS;
  						childmsg.obj="�˿�ɹ�";
					}else
					{
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity �˿�ʧ��,code:"+rst.code+",info:"+rst.code_info,"com.txt");
						childmsg.what=CahslessTest.PAYOUTFAIL;
						childmsg.obj="�˿�ʧ��,code:"+rst.code+",info:"+rst.code_info;
					}
				}
  				//���ؽ��
  				else if(rst.op.equals(LfMISPOSApi.OP_GETRECORD))
  				{
  					//����00������ɹ�
  					if(rst.code.equals(ErrCode._00.getCode()))
  					{  						
  						String tmp = "����:�ض���Ϣ=";
  						tmp += "[" + ((_04_GetRecordReply) (rst)).getSpecInfoField();//�ض���Ϣ����Ա����Ҫ������
  						/*�ض���Ϣ˵��
  						+��ֵ����(19)
  						+�ն���ˮ��(6)
  						+�ն˱��(8)
  						+���κ�(6)
  						+�̻���(15)
  						+�̻�����(60)
  						+��Ա����(60)
  						+����ʱ��(6)
  						+��������(8)
  						+���׵���(14)
  						+���ѽ��(12)
  						+�˻����(12)
  						+��ʱ������ˮ�ţ�26��
  						���϶��Ƕ��������Ƕ���12λ��ǰ��0����������λ���󲹿ո�

  						* */
  						tmp += "],�̻�����=[" + ((_04_GetRecordReply) (rst)).getMer();//�̻�����
  						tmp += "],�ն˺�=[" + ((_04_GetRecordReply) (rst)).getTmn();//�ն˺�
  						tmp += "],����=[" + ((_04_GetRecordReply) (rst)).getCardNo();//����
  						tmp += "],�������κ�=[" + ((_04_GetRecordReply) (rst)).getTransacionBatchNo();//�������κ�
  						tmp += "],ԭ��������=[" + ((_04_GetRecordReply) (rst)).getTransacionVoucherNo();//ԭ��������
  						tmp += "],�������ں�ʱ��=[" + ((_04_GetRecordReply) (rst)).getTransacionDatetime();//�������ں�ʱ��
  						tmp += "],���׽��=[" + ((_04_GetRecordReply) (rst)).getTransacionAmount();//���׽��
  						tmp +="]";
  						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity ��ѯ�ɹ�="+tmp,"com.txt");
  					
  						//�˿������ȡ
						String tmp_spec = ((_04_GetRecordReply) (rst)).getSpecInfoField();
						int tmp_spec_len = tmp_spec!=null?tmp_spec.length():0;
						//����
						//rfd_amt_fen = amount;//ʹ���ϴ�ȫ����Խ���1��
						//���˿�š�
						if(tmp_spec!=null && tmp_spec_len>(2+19)){
							rfd_card_no = (((_04_GetRecordReply) (rst)).getSpecInfoField()).substring(0+2,2+19).trim();
						}
						//����ʱ������ˮ�š�
						if(tmp_spec!=null && tmp_spec_len>26){
							rfd_spec_tmp_serial = (((_04_GetRecordReply) (rst)).getSpecInfoField()).substring((tmp_spec_len-26),tmp_spec_len);
						}else{//ʹ�ÿո�ʱ����ʾ��һ�εġ���ʱ������ˮ�š�
							rfd_spec_tmp_serial = String.format("%1$-26s","");
						}
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity �˿����=���"+amount+"����="+rfd_card_no+"��ˮ��="+rfd_spec_tmp_serial,"com.txt");
						childmsg.what=CahslessTest.QUERYSUCCESS;
  						childmsg.obj=((_04_GetRecordReply) (rst)).getSpecInfoField();
  					}
  					else
  					{
  						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity ��ѯʧ��,code:"+rst.code+",info:"+rst.code_info,"com.txt");
  						childmsg.what=CahslessTest.QUERYFAIL;
  						childmsg.obj="";
  					}
  				}
  				posmainhand.sendMessage(childmsg);
  			}
  		}

  		@Override
  		public void onProcess(Display dpl) {//���̺���ʾ��Ϣ
  			if(dpl!=null) {
  				//lcd(dpl.getType() + "\n" + dpl.getMsg());

  				//����ʾ��Ϣ���͡�type��˵������com.landfoneapi.mispos.DisplayType
  				if(dpl.getType().equals(DisplayType._4.getType())){
  					ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity ͨѶ��ʾ<<"+dpl.getMsg(),"com.txt");
  				}

  			}
  		}
  	};
  	
    //��������ҳ��
  	private void tochuhuo()
  	{
  		Intent intent = null;// ����Intent����                
      	intent = new Intent(BusZhipos.this, BusHuo.class);// ʹ��Accountflag���ڳ�ʼ��Intent
//      	intent.putExtra("out_trade_no", out_trade_no);
//      	intent.putExtra("proID", proID);
//      	intent.putExtra("productID", productID);
//      	intent.putExtra("proType", proType);
//      	intent.putExtra("cabID", cabID);
//      	intent.putExtra("huoID", huoID);
//      	intent.putExtra("prosales", prosales);
//      	intent.putExtra("count", count);
//      	intent.putExtra("reamin_amount", reamin_amount);
//      	intent.putExtra("zhifutype", zhifutype);
      	OrderDetail.setOrdereID(out_trade_no);
      	OrderDetail.setPayType(Integer.parseInt(zhifutype));
      	OrderDetail.setSmallCard(amount);
      	OrderDetail.setRfd_card_no(rfd_card_no);
      	startActivityForResult(intent, REQUEST_CODE);// ��Accountflag
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
  					ToolClass.Log(ToolClass.INFO,"EV_COM","APP<<���˿�","com.txt");
  					ToolClass.setLAST_CHUHUO(true);
  					OrderDetail.addLog(BusZhipos.this); 
  					AudioSound.playbusfinish();
  					finish();
  				}
  				//����ʧ��,��Ǯ
  				else
  				{	
  					ispayoutopt=1;
  					ToolClass.Log(ToolClass.INFO,"EV_COM","APP<<�˿�amount="+amount,"com.txt");
  					dialog= ProgressDialog.show(BusZhipos.this,"�����˿���","���Ժ�...");
  					payoutzhipos();//�˿����									
  				}				
  			}			
  		}
  	}
  	
  	@Override
	protected void onDestroy() {
  		timer.shutdown(); 
		mMyApi.pos_release();		
		super.onDestroy();		
	}

}
