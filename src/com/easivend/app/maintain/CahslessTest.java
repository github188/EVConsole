package com.easivend.app.maintain;

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
import com.landfoneapi.protocol.pkg._04_QueryReply;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CahslessTest extends Activity {
	public final static int OPENSUCCESS=1;//打开成功
	public final static int OPENFAIL=2;//打开失败
	public final static int CLOSESUCCESS=3;//关闭成功
	public final static int CLOSEFAIL=4;//关闭失败
	public final static int COSTSUCCESS=5;//扣款成功
	public final static int COSTFAIL=6;//扣款失败
	public final static int QUERYSUCCESS=7;//取交易信息成功
	public final static int QUERYFAIL=8;//取交易信息失败
	public final static int DELETESUCCESS=9;//撤销成功
	public final static int DELETEFAIL=10;//撤销失败
	public final static int PAYOUTSUCCESS=11;//退款成功
	public final static int PAYOUTFAIL=12;//退款失败
	public final static int FINDSUCCESS=13;//查询成功
	public final static int FINDFAIL=14;//查询失败
	public final static int READCARD=15;//已经读到卡
	private int isPossel=0;//0没有设置Pos，1有设置Pos有查询功能,其他值有设置pos没有查询功能
	private TextView txtcashlesstest=null;
	private EditText edtcashlesstest=null;
	private Button btncashlesstestopen=null,btncashlesstestok=null,btncashlesstestfind=null,
			btncashlesstestquery=null,btncashlesstestdelete=null,btncashlesstestpayout=null,
			btncashlesstestclose=null,btncashlesstestcancel=null;
	private Handler mainhand=null;
	private LfMISPOSApi mMyApi = new LfMISPOSApi();
	//查询参数
	private String cashbalance = "";
	//退款参数
	private String rfd_card_no = "";
	private String rfd_spec_tmp_serial = "";
	float amount=0;//商品需要支付金额
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cashlesstest);	
		//设置横屏还是竖屏的布局策略
		this.setRequestedOrientation(ToolClass.getOrientation());
		mainhand=new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub				
				switch (msg.what) 
				{
					case OPENSUCCESS:
						txtcashlesstest.setText(msg.obj.toString());
						break;
					case OPENFAIL:	
						txtcashlesstest.setText(msg.obj.toString());
						break;
					case CLOSESUCCESS:
						txtcashlesstest.setText(msg.obj.toString());
						break;
					case CLOSEFAIL:	
						txtcashlesstest.setText(msg.obj.toString());
						break;
					case CahslessTest.READCARD:
						txtcashlesstest.setText("已读到卡");
	  					break;	
					case COSTSUCCESS:
						txtcashlesstest.setText(msg.obj.toString());
						break;
					case COSTFAIL:	
						txtcashlesstest.setText(msg.obj.toString());
						break;
					case QUERYSUCCESS:
						txtcashlesstest.setText(msg.obj.toString());
						break;
					case QUERYFAIL:	
						txtcashlesstest.setText(msg.obj.toString());
						break;
					case DELETESUCCESS:
						txtcashlesstest.setText(msg.obj.toString());
						break;
					case DELETEFAIL:	
						txtcashlesstest.setText(msg.obj.toString());
						break;	
					case PAYOUTSUCCESS:
						txtcashlesstest.setText(msg.obj.toString());
						break;
					case PAYOUTFAIL:	
						txtcashlesstest.setText(msg.obj.toString());
						break;	
					case FINDSUCCESS:
						txtcashlesstest.setText(msg.obj.toString());
						break;
					case FINDFAIL:	
						txtcashlesstest.setText(msg.obj.toString());
						break;	
				}
			}
		};			
		//***********************
		//进行pos操作
		//***********************
		vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(CahslessTest.this);// 创建InaccountDAO对象
	    // 获取所有收入信息，并存储到List泛型集合中
    	Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
    	if(tb_inaccount!=null)
    	{
    		isPossel=tb_inaccount.getZhifubaofaca();    		
    	}	
		txtcashlesstest = (TextView)findViewById(R.id.txtcashlesstest);
		edtcashlesstest = (EditText)findViewById(R.id.edtcashlesstest);
		//打开
		btncashlesstestopen = (Button)findViewById(R.id.btncashlesstestopen);
		btncashlesstestopen.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {		    	
		    	ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 打开读卡器"+ToolClass.getCardcom(),"com.txt");
		    	txtcashlesstest.setText("打开读卡器.."+ToolClass.getCardcom());
		    	//打开串口
	    		int posipport=(ToolClass.getPosipport().equals(""))?0:Integer.parseInt(ToolClass.getPosipport());
		        //ip、端口、串口、波特率必须准确"121.40.30.62", 18080
				mMyApi.pos_init(ToolClass.getPosip(), posipport
						,ToolClass.getCardcom(), "9600", mIUserCallback); 
				if(ToolClass.getPosisssl()==1)
				{
					ToolClass.Log(ToolClass.INFO,"EV_COM","busport打开ssl加密","com.txt");
					mMyApi.pos_setKeyCert(ToolClass.getContext(), true, "CUP_cacert.pem");
				}				
		    }
		});
		//查询
		btncashlesstestfind = (Button)findViewById(R.id.btncashlesstestfind);		
		btncashlesstestfind.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {	
		    	ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 读卡器查询卡余额","com.txt");
		    	txtcashlesstest.setText("读卡器查询卡余额");
		    	mMyApi.pos_query(mIUserCallback);
		    }
		});
		//扣款
		btncashlesstestok = (Button)findViewById(R.id.btncashlesstestok);
		btncashlesstestok.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {	
		    	amount=Float.parseFloat(edtcashlesstest.getText().toString());
		    	ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 读卡器扣款="+amount,"com.txt");
		    	txtcashlesstest.setText("读卡器扣款="+amount);	
		    	if(isPossel==1)//会员卡
		    	{
		    		mMyApi.pos_purchase(ToolClass.MoneySend(amount), 0,mIUserCallback);
		    	}
		    	else if(isPossel>1)//银行卡
		    	{
		    		mMyApi.pos_purchase(ToolClass.MoneySend(amount), 1,mIUserCallback);
		    	}
		    }
		});
		//取交易信息
		btncashlesstestquery = (Button)findViewById(R.id.btncashlesstestquery);		
		btncashlesstestquery.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {	
		    	ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 读卡器获取交易信息","com.txt");
		    	txtcashlesstest.setText("读卡器获取交易信息");
		    	mMyApi.pos_getrecord("000000000000000", "00000000","000000", mIUserCallback);
		    }
		});
		//撤销交易
		btncashlesstestdelete = (Button)findViewById(R.id.btncashlesstestdelete);		
		btncashlesstestdelete.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {		
		    	ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 操作撤销（刷卡前）..","com.txt");
		    	txtcashlesstest.setText("POS 操作撤销（刷卡前）..");
		    	mMyApi.pos_cancel();
		    }
		});
		//退款
		btncashlesstestpayout = (Button)findViewById(R.id.btncashlesstestpayout);
		btncashlesstestpayout.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 读卡器退款="+amount,"com.txt");
		    	txtcashlesstest.setText("读卡器退款="+amount);
		    	if(isPossel==1)//会员卡
		    	{
		    		mMyApi.pos_refund("000000000000000", "00000000",rfd_card_no,ToolClass.MoneySend(amount),rfd_spec_tmp_serial,0, mIUserCallback);
		    	}
		    	else if(isPossel>1)//银行卡
		    	{
	                mMyApi.pos_refund("000000000000000", "00000000",rfd_card_no,1,"      ",1, mIUserCallback);
	            }
		    }
		});
		//关闭
		btncashlesstestclose = (Button)findViewById(R.id.btncashlesstestclose);
		btncashlesstestclose.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {		    
		    	ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 关闭读卡器","com.txt");
		    	txtcashlesstest.setText("POS 关闭读卡器");
				mMyApi.pos_release();
		    }
		});
		//退出
		btncashlesstestcancel = (Button)findViewById(R.id.btncashlesstestcancel);		
		btncashlesstestcancel.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {		
		    	mMyApi.pos_release();
		    	finish();
		    }
		});
	}
	
	//接口返回
	private IUserCallback mIUserCallback = new IUserCallback(){
		@Override
		public void onResult(REPLY rst) 
		{
			if(rst!=null) 
			{
				Message childmsg=mainhand.obtainMessage();
				//info(rst.op + ":" + rst.code + "," + rst.code_info);
				//【操作标识符】LfMISPOSApi下“OP_”开头的静态变量，如：LfMISPOSApi.OP_INIT、LfMISPOSApi.OP_PURCHASE等等
				//打开串口
				if(rst.op.equals(LfMISPOSApi.OP_INIT))
				{
					//【返回码和信息】code和code_info的返回/说明，见com.landfoneapi.mispos.ErrCode
					if(rst.code.equals(ErrCode._00.getCode())){//返回00，代表成功
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 打开成功"+ToolClass.getCardcom(),"com.txt");
						childmsg.what=OPENSUCCESS;
						childmsg.obj="打开成功"+ToolClass.getCardcom();
					}else{
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 打开失败"+ToolClass.getCardcom()+",code:"+rst.code+",info:"+rst.code_info,"com.txt");						
						childmsg.what=OPENFAIL;
						childmsg.obj="打开失败"+ToolClass.getCardcom()+",code:"+rst.code+",info:"+rst.code_info;
					}
				}
				//关闭串口
				else if(rst.op.equals(LfMISPOSApi.OP_RELEASE))
				{
					if(rst.code.equals(ErrCode._00.getCode())){//返回00，代表成功
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 关闭成功","com.txt");
						childmsg.what=CLOSESUCCESS;
						childmsg.obj="关闭成功";
					}else{
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 关闭失败,code:"+rst.code+",info:"+rst.code_info,"com.txt");						
						childmsg.what=CLOSEFAIL;
						childmsg.obj="关闭失败,code:"+rst.code+",info:"+rst.code_info;
					}
				}
				//签到
				else if (rst.op.equals(LfMISPOSApi.OP_SIGNIN)) 
				{
                    if (rst.code.equals(ErrCode._00.getCode())) {//返回00，代表成功
                    	ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 签到成功","com.txt");
                    } else {
                        ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 签到失败,code:"+rst.code+",info:"+rst.code_info,"com.txt");						
                    }
                }
				//结算【结算时间可能会非常长】
				else if (rst.op.equals(LfMISPOSApi.OP_SETTLE)) 
				{
                    if (rst.code.equals(ErrCode._00.getCode())) {//返回00，代表成功
                    	ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 结算成功","com.txt");                        
                    } else {
                    	ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 结算失败,code:"+rst.code+",info:"+rst.code_info,"com.txt");		
                    }
                } 
				//扣款
				else if(rst.op.equals(LfMISPOSApi.OP_PURCHASE))
				{
					if(rst.code.equals(ErrCode._00.getCode())){//返回00，代表成功
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 扣款成功","com.txt");
						childmsg.what=COSTSUCCESS;
						childmsg.obj="扣款成功";
					}
					else if(rst.code.equals(ErrCode._XY.getCode())){
  						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 撤销成功","com.txt");
  						childmsg.what=CahslessTest.DELETESUCCESS;
  						childmsg.obj="撤销成功";
  					}
					else{
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 扣款失败,code:"+rst.code+",info:"+rst.code_info,"com.txt");
						if(rst.code.equals("01"))
						{
							cashbalance=rst.code_info;
						}
						childmsg.what=COSTFAIL;
						childmsg.obj="扣款失败,code:"+rst.code+",info:"+rst.code_info;
					}
				}
				//查询
				else if (rst.op.equals(LfMISPOSApi.OP_QUERY)) 
				{
                    if (rst.code.equals(ErrCode._00.getCode()))
                    {
                        String tmp = "查询结果:";
                        tmp += "\n错误代码：" + ((_04_QueryReply) (rst)).getCode();
                        tmp += ",\n提示信息：" + ((_04_QueryReply) (rst)).getCode_info();
                        tmp += ",\n卡号:" + ((_04_QueryReply) (rst)).getCardNo();//卡号
                        tmp += ",\n现金余额:" + ((_04_QueryReply) (rst)).getCashBalance();//现金余额
                        tmp += ",\n积分余额:" + ((_04_QueryReply) (rst)).getPointBalance();//积分余额
                        ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 查询成功="+tmp,"com.txt");
                        //查询参数获取
                        cashbalance = ((_04_QueryReply) (rst)).getCashBalance();
                        childmsg.what=FINDSUCCESS;
						childmsg.obj="查询成功="+tmp;
                        
                    } 
                    else 
                    {
                    	ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 查询失败,code:"+rst.code+",info:"+rst.code_info,"com.txt");
                        childmsg.what=FINDFAIL;
						childmsg.obj="查询失败,code:"+rst.code+",info:"+rst.code_info;
                    }
                } 
				//退款
  				else if(rst.op.equals(LfMISPOSApi.OP_REFUND))
  				{
  					//返回00，代表成功
					if(rst.code.equals(ErrCode._00.getCode()))
					{
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 退款成功","com.txt");
						childmsg.what=CahslessTest.PAYOUTSUCCESS;
  						childmsg.obj="退款成功";
					}else
					{
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 退款失败,code:"+rst.code+",info:"+rst.code_info,"com.txt");
						childmsg.what=CahslessTest.PAYOUTFAIL;
						childmsg.obj="退款失败,code:"+rst.code+",info:"+rst.code_info;
					}
				}
				//返回结果
				else if(rst.op.equals(LfMISPOSApi.OP_GETRECORD))
				{
					//返回00，代表成功
					if(rst.code.equals(ErrCode._00.getCode()))
					{
						String tmp = "单据:特定信息=";
						tmp += "[" + ((_04_GetRecordReply) (rst)).getSpecInfoField();//特定信息【会员卡需要！！】
						/*特定信息说明
						+储值卡号(19)
						+终端流水号(6)
						+终端编号(8)
						+批次号(6)
						+商户号(15)
						+商户名称(60)
						+会员名称(60)
						+交易时间(6)
						+交易日期(8)
						+交易单号(14)
						+消费金额(12)
						+账户余额(12)
						+临时交易流水号（26）
						以上都是定长，金额都是定长12位，前补0，其他不足位数后补空格

						* */
						tmp += "],商户代码=[" + ((_04_GetRecordReply) (rst)).getMer();//商户代码
						tmp += "],终端号=[" + ((_04_GetRecordReply) (rst)).getTmn();//终端号
						tmp += "],卡号=[" + ((_04_GetRecordReply) (rst)).getCardNo();//卡号
						tmp += "],交易批次号=[" + ((_04_GetRecordReply) (rst)).getTransacionBatchNo();//交易批次号
						tmp += "],原交易类型=[" + ((_04_GetRecordReply) (rst)).getTransacionVoucherNo();//原交易类型
						tmp += "],交易日期和时间=[" + ((_04_GetRecordReply) (rst)).getTransacionDatetime();//交易日期和时间
						tmp += "],交易金额=[" + ((_04_GetRecordReply) (rst)).getTransacionAmount();//交易金额
						tmp +="]";
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 返回成功="+tmp,"com.txt");

  						//退款参数获取
						String tmp_spec = ((_04_GetRecordReply) (rst)).getSpecInfoField();
						int tmp_spec_len = tmp_spec!=null?tmp_spec.length():0;
						//【金额】
						//rfd_amt_fen = amount;//使用上次全额，测试金额都是1分
						//【退款卡号】
						if(tmp_spec!=null && tmp_spec_len>(2+19)){
							rfd_card_no = (((_04_GetRecordReply) (rst)).getSpecInfoField()).substring(0+2,2+19).trim();
						}
						//【剩余金额】
						if(tmp_spec!=null && tmp_spec_len>(2+19)){
							cashbalance = (((_04_GetRecordReply) (rst)).getSpecInfoField()).substring((tmp_spec_len-26-12),(tmp_spec_len-26)).trim();
						}
						//【临时交易流水号】
						if(tmp_spec!=null && tmp_spec_len>26){
							rfd_spec_tmp_serial = (((_04_GetRecordReply) (rst)).getSpecInfoField()).substring((tmp_spec_len-26),tmp_spec_len);
						}else{//使用空格时，表示上一次的【临时交易流水号】
							rfd_spec_tmp_serial = String.format("%1$-26s","");
						}
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 退款参数=金额"+amount+"卡号="+rfd_card_no+"流水号="+rfd_spec_tmp_serial+"剩余金额="+cashbalance,"com.txt");
						childmsg.what=QUERYSUCCESS;
						childmsg.obj="返回成功="+tmp;
					}
					else
					{
						ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 返回失败,code:"+rst.code+",info:"+rst.code_info,"com.txt");
						childmsg.what=QUERYFAIL;
						childmsg.obj="返回失败";
					}
				}
				mainhand.sendMessage(childmsg);
			}
		}

		@Override
		public void onProcess(Display dpl) {//过程和提示信息
			if(dpl!=null) {
				//lcd(dpl.getType() + "\n" + dpl.getMsg());

				//【提示信息类型】type的说明，见com.landfoneapi.mispos.DisplayType
				if(dpl.getType().equals(DisplayType._4.getType())){
					ToolClass.Log(ToolClass.INFO,"EV_COM","COMActivity 通讯提示<<"+dpl.getMsg(),"com.txt");
				}
				else if(dpl.getType().equals(DisplayType._h.getType()))
  				{
  					Message childmsg=mainhand.obtainMessage();
  					childmsg.what=CahslessTest.READCARD;
					childmsg.obj="已读到卡";  	
					mainhand.sendMessage(childmsg);
  				}
			}
		}
	};

}
