package com.easivend.app.maintain;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.easivend.app.business.Busgoods;
import com.easivend.app.business.BusgoodsSelect;
import com.easivend.chart.Line;
import com.easivend.chart.Stack;
import com.easivend.common.ToolClass;
import com.easivend.common.Vmc_OrderAdapter;
import com.easivend.dao.vmc_cabinetDAO;
import com.easivend.dao.vmc_orderDAO;
import com.easivend.evprotocol.EVprotocolAPI;
import com.easivend.model.Tb_vmc_cabinet;
import com.easivend.model.Tb_vmc_order_pay;
import com.example.evconsole.R;

import android.R.integer;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TabHost.TabSpec;

public class Order extends TabActivity
{
	private TabHost mytabhost = null;
	private int[] layres=new int[]{R.id.tab_grid,R.id.tab_picture};//内嵌布局文件的id
	private TextView txtpayTypeValue=null,txtpayStatusValue=null,txtRealStatusValue=null,txtsmallNoteValue=null,txtsmallConiValue=null,
			txtsmallAmountValue=null,txtsmallCardValue=null,txtrealNoteValue=null,txtrealCoinValue=null,txtrealAmountValue=null,
			txtdebtAmountValue=null,txtrealCardValue=null,txtpayTimeValue=null,txtproductNameValue=null,txtsalesPriceValue=null,
			txtcabIDValue=null,txtcolumnIDValue=null;
	private EditText edtordergridstart=null,edtordergridend=null;
	private Button btnordergridquery=null,btnordergriddel=null,btnordergridexit=null,btnordergridStack=null,btnordergridLine=null;	
	private Spinner spinordergridtongji=null;
	private ListView lvorder=null;
	private SimpleDateFormat df;
	private String date=null;
	private int datetype=0;//1开始时间,2结束时间
	private static final int DATE_DIALOG_IDSTART = 1;// 创建开始时间对话框常量	
	private int mYear=0,mMon=0,mDay=0;
	private static final int DATE_DIALOG_IDEND = 2;// 创建结束时间对话框常量
	private int eYear=0,eMon=0,eDay=0;
	//总支付订单
	private	String[] ordereID;// 订单ID[pk]
	private	String[] payType;// 支付方式0现金，1银联，2支付宝声波，3支付宝二维码，4微信扫描
	private	String[] payStatus;// 订单状态0出货成功，1出货失败，2支付失败，3未支付
	private	String[] RealStatus;// 退款状态，0不显示未发生退款动作，1退款完成，2部分退款，3退款失败
	private	String[] smallNote;// 纸币金额
	private	String[] smallConi;// 硬币金额
	private	String[] smallAmount;// 现金投入金额
	private	String[] smallCard;// 非现金支付金额
	private	String[] shouldPay;// 商品总金额
	private	String[] shouldNo;// 商品总数量
	private	String[] realNote;// 纸币退币金额
	private	String[] realCoin;// 硬币退币金额
	private	String[] realAmount;// 现金退币金额
	private	String[] debtAmount;// 欠款金额
	private	String[] realCard;// 非现金退币金额
	private	String[] payTime;//支付时间
		//详细支付订单
	private	String[] productID;//商品id
	private	String[] cabID;//货柜号
	private String[] columnID;//货道号
	    //商品信息
	private String[] productName;// 商品全名
	private String[] salesPrice;// 优惠价,如”20.00”
	
	//数字类型订单信息
    //总支付订单
	private double[] smallNotevalue;// 纸币金额
	private double[] smallConivalue;// 硬币金额
	private double[] smallAmountvalue;// 现金投入金额
	private double[] smallCardvalue;// 非现金支付金额
	private double[] shouldPayvalue;// 商品总金额
	private double[] shouldNovalue;// 商品总数量
	private double[] realNotevalue;// 纸币退币金额
	private double[] realCoinvalue;// 硬币退币金额
	private double[] realAmountvalue;// 现金退币金额
	private double[] debtAmountvalue;// 欠款金额
	private double[] realCardvalue;// 非现金退币金额
  	//商品信息
	private double[] salesPricevalue;// 优惠价,如”20.00”
	private int ourdercount=0;//记录的数量
	
	private SimpleAdapter simpleada = null;//进行数据的转换操作
	//定义显示的内容包装
    private List<Map<String,String>> listMap = new ArrayList<Map<String,String>>();
    
    //生成图表
    private String title="";//标题
    //金额统计
	private double[] Amountvalue=new double[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	//数量统计
	private double[] Countvalue=new double[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	private double Amountmax=0,Countmax=0;//最大值
	private int tongjitype=0;//0统计金额,1统计数量
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.order);// 设置布局文件
		this.mytabhost = super.getTabHost();//取得TabHost对象
        LayoutInflater.from(this).inflate(R.layout.order, this.mytabhost.getTabContentView(),true);
        //增加Tab的组件
        TabSpec myTabhuodaomana=this.mytabhost.newTabSpec("tab0");
        myTabhuodaomana.setIndicator("报表信息");
        myTabhuodaomana.setContent(this.layres[0]);
    	this.mytabhost.addTab(myTabhuodaomana); 
    	
    	TabSpec myTabhuodaotest=this.mytabhost.newTabSpec("tab1");
    	myTabhuodaotest.setIndicator("图表信息");
    	myTabhuodaotest.setContent(this.layres[1]);
    	this.mytabhost.addTab(myTabhuodaotest); 
    	
    	//===============
    	//报表查询页面
    	//===============   
		df = new SimpleDateFormat("yyyy");//设置日期格式
    	date=df.format(new Date()); 
    	mYear=Integer.parseInt(date);
    	eYear=Integer.parseInt(date);
    	df = new SimpleDateFormat("MM");//设置日期格式
    	date=df.format(new Date()); 
    	mMon=Integer.parseInt(date);
    	eMon=Integer.parseInt(date);
    	df = new SimpleDateFormat("dd");//设置日期格式
    	date=df.format(new Date()); 
    	mDay=Integer.parseInt(date);
    	eDay=Integer.parseInt(date);
    	//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<现在时刻是"+y+"-"+m+"-"+d);    
    	txtpayTypeValue = (TextView) findViewById(R.id.txtpayTypeValue);
    	txtpayStatusValue = (TextView) findViewById(R.id.txtpayStatusValue);
    	txtRealStatusValue = (TextView) findViewById(R.id.txtRealStatusValue);
    	txtsmallNoteValue = (TextView) findViewById(R.id.txtsmallNoteValue);
    	txtsmallConiValue = (TextView) findViewById(R.id.txtsmallConiValue);
    	txtsmallAmountValue = (TextView) findViewById(R.id.txtsmallAmountValue);
    	txtsmallCardValue = (TextView) findViewById(R.id.txtsmallCardValue);
    	txtrealNoteValue = (TextView) findViewById(R.id.txtrealNoteValue);
    	txtrealCoinValue = (TextView) findViewById(R.id.txtrealCoinValue);
    	txtrealAmountValue = (TextView) findViewById(R.id.txtrealAmountValue);
    	txtdebtAmountValue = (TextView) findViewById(R.id.txtdebtAmountValue);
    	txtrealCardValue = (TextView) findViewById(R.id.txtrealCardValue);
    	txtpayTimeValue = (TextView) findViewById(R.id.txtpayTimeValue);
    	txtproductNameValue = (TextView) findViewById(R.id.txtproductNameValue);
    	txtsalesPriceValue = (TextView) findViewById(R.id.txtsalesPriceValue);
    	txtcabIDValue = (TextView) findViewById(R.id.txtcabIDValue);
    	txtcolumnIDValue = (TextView) findViewById(R.id.txtcolumnIDValue);
    	
    	
    	lvorder = (ListView) findViewById(R.id.lvorder);
    	lvorder.setOnItemClickListener(new OnItemClickListener() 
    	{
            // 覆写onItemClick方法
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	txtpayTypeValue.setText(payType[position]);
            	txtpayStatusValue.setText(payStatus[position]);
            	txtRealStatusValue.setText(RealStatus[position]);
            	txtsmallNoteValue.setText(smallNote[position]);
            	txtsmallConiValue.setText(smallConi[position]);
            	txtsmallAmountValue.setText(smallAmount[position]);
            	txtsmallCardValue.setText(smallCard[position]);
            	txtrealNoteValue.setText(realNote[position]);
            	txtrealCoinValue.setText(realCoin[position]);
            	txtrealAmountValue.setText(realAmount[position]);
            	txtdebtAmountValue.setText(debtAmount[position]);
            	txtrealCardValue.setText(realCard[position]);
            	txtpayTimeValue.setText(payTime[position]);
            	txtproductNameValue.setText(productName[position]);
            	txtsalesPriceValue.setText(salesPrice[position]);
            	txtcabIDValue.setText(cabID[position]);
            	txtcolumnIDValue.setText(columnID[position]);
            }
        });
    	edtordergridstart = (EditText) findViewById(R.id.edtordergridstart);// 获取时间文本框
    	edtordergridstart.setOnClickListener(new OnClickListener() {// 为时间文本框设置单击监听事件
            @Override
            public void onClick(View arg0) {
            	datetype=DATE_DIALOG_IDSTART;
                showDialog(DATE_DIALOG_IDSTART);// 显示日期选择对话框                
            }
        });
    	edtordergridend = (EditText) findViewById(R.id.edtordergridend);// 获取时间文本框
    	edtordergridend.setOnClickListener(new OnClickListener() {// 为时间文本框设置单击监听事件
            @Override
            public void onClick(View arg0) {
            	datetype=DATE_DIALOG_IDEND;
                showDialog(DATE_DIALOG_IDEND);// 显示日期选择对话框                
            }
        });
    	//查询
    	btnordergridquery = (Button) findViewById(R.id.btnordergridquery);
    	btnordergridquery.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	grid();
		    }
		});
    	//退出
    	btnordergridexit = (Button) findViewById(R.id.btnordergridexit);
    	btnordergridexit.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	finish();
		    }
		});
    	spinordergridtongji= (Spinner) findViewById(R.id.spinordergridtongji); 
    	this.spinordergridtongji.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			//当选项改变时触发
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				tongjitype=arg2;				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
    	//生成柱状图
    	btnordergridStack = (Button) findViewById(R.id.btnordergridStack);
    	btnordergridStack.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	Intent intent = null;// 创建Intent对象                
            	intent = new Intent(Order.this, Stack.class);// 使用Accountflag窗口初始化Intent
            	intent.putExtra("title", edtordergridstart.getText().toString()+"到"+edtordergridend.getText().toString());
            	//0统计金额,1统计数量
            	if(tongjitype==0)
            	{
	            	intent.putExtra("maxvalue", Amountmax);
	            	intent.putExtra("value", Amountvalue);
            	}
            	else if(tongjitype==1)
            	{
	            	intent.putExtra("maxvalue", Countmax);
	            	intent.putExtra("value", Countvalue);
            	}
            	startActivity(intent);// 打开Accountflag
		    }
		});
    	//生成折线图
    	btnordergridLine = (Button) findViewById(R.id.btnordergridLine);
    	btnordergridLine.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	Intent intent = null;// 创建Intent对象                
            	intent = new Intent(Order.this, Line.class);// 使用Accountflag窗口初始化Intent
            	intent.putExtra("title", edtordergridstart.getText().toString()+"到"+edtordergridend.getText().toString());
            	//0统计金额,1统计数量
            	if(tongjitype==0)
            	{
	            	intent.putExtra("maxvalue", Amountmax);
	            	intent.putExtra("value", Amountvalue);
            	}
            	else if(tongjitype==1)
            	{
	            	intent.putExtra("maxvalue", Countmax);
	            	intent.putExtra("value", Countvalue);
            	}
            	startActivity(intent);// 打开Accountflag
		    }
		});
	}
	//===============
	//报表查询页面
	//===============
	@Override
    protected Dialog onCreateDialog(int id) {// 重写onCreateDialog方法	
        switch (id)
        {
	        case DATE_DIALOG_IDSTART:// 弹出日期选择对话框
	            return new DatePickerDialog(this, mDateSetListener, mYear, mMon-1, mDay);            
	        case DATE_DIALOG_IDEND:// 弹出日期选择对话框
	            return new DatePickerDialog(this, mDateSetListener, eYear, eMon-1, eDay);    
        }
        return null;
    }
	
	private DatePickerDialog.OnDateSetListener mDateSetListener= new DatePickerDialog.OnDateSetListener()
	{

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			switch (datetype)
	        {
		        case DATE_DIALOG_IDSTART:// 弹出日期选择对话框
		        	mYear=year;
					mMon=(monthOfYear+1);
					mDay=dayOfMonth;
					edtordergridstart.setText(mYear+"-"+mMon+"-"+mDay);
					break;
	            //edtordergridstart.setText(mYear+"-"+mMon+"-"+mDay);
		        case DATE_DIALOG_IDEND:// 弹出日期选择对话框
		        	eYear=year;
					eMon=(monthOfYear+1);
					eDay=dayOfMonth; 
					edtordergridend.setText(eYear+"-"+eMon+"-"+eDay);
					break;
	        }
			
			
		}
		
	};
	//查询报表
	private void grid()
	{
		if((mYear>0)&&(eYear>0))
		{
			Vmc_OrderAdapter vmc_OrderAdapter=new Vmc_OrderAdapter();
			vmc_OrderAdapter.grid(Order.this, mYear, mMon, mDay, eYear, eMon, eDay);
			//总支付订单
			ordereID = vmc_OrderAdapter.getOrdereID();// 订单ID[pk]
			payType = vmc_OrderAdapter.getPayType();// 支付方式0现金，1银联，2支付宝声波，3支付宝二维码，4微信扫描
			payStatus = vmc_OrderAdapter.getPayStatus();// 订单状态0出货成功，1出货失败，2支付失败，3未支付
			RealStatus = vmc_OrderAdapter.getRealStatus();// 退款状态，0不显示未发生退款动作，1退款完成，2部分退款，3退款失败
			smallNote = vmc_OrderAdapter.getSmallNote();// 纸币金额
			smallConi = vmc_OrderAdapter.getSmallConi();// 硬币金额
			smallAmount = vmc_OrderAdapter.getSmallAmount();// 现金投入金额
			smallCard = vmc_OrderAdapter.getSmallCard();// 非现金支付金额
			shouldPay = vmc_OrderAdapter.getShouldPay();// 商品总金额
			shouldNo = vmc_OrderAdapter.getShouldNo();// 商品总数量
			realNote = vmc_OrderAdapter.getRealNote();// 纸币退币金额
			realCoin = vmc_OrderAdapter.getRealCoin();// 硬币退币金额
			realAmount = vmc_OrderAdapter.getRealAmount();// 现金退币金额
			debtAmount = vmc_OrderAdapter.getDebtAmount();// 欠款金额
			realCard = vmc_OrderAdapter.getRealCard();// 非现金退币金额
			payTime = vmc_OrderAdapter.getPayTime();//支付时间
			//详细支付订单
			productID = vmc_OrderAdapter.getProductID();//商品id
			cabID = vmc_OrderAdapter.getCabID();//货柜号
		    columnID = vmc_OrderAdapter.getColumnID();//货道号
		    //商品信息
		    productName = vmc_OrderAdapter.getProductName();// 商品全名
		    salesPrice = vmc_OrderAdapter.getSalesPrice();// 优惠价,如”20.00”
		    
		    //数字类型订单信息
		    smallNotevalue= vmc_OrderAdapter.getSmallNotevalue();// 纸币金额
		    smallConivalue= vmc_OrderAdapter.getSmallConivalue();// 硬币金额
		    smallAmountvalue= vmc_OrderAdapter.getSmallAmountvalue();// 现金投入金额
		    smallCardvalue= vmc_OrderAdapter.getSmallCardvalue();// 非现金支付金额
		    shouldPayvalue= vmc_OrderAdapter.getShouldPayvalue();// 商品总金额
		    shouldNovalue= vmc_OrderAdapter.getShouldNovalue();// 商品总数量
		    realNotevalue= vmc_OrderAdapter.getRealNotevalue();// 纸币退币金额
		    realCoinvalue= vmc_OrderAdapter.getRealCoinvalue();// 硬币退币金额
		    realAmountvalue= vmc_OrderAdapter.getRealAmountvalue();// 现金退币金额
		    debtAmountvalue= vmc_OrderAdapter.getDebtAmountvalue();// 欠款金额
		    realCardvalue= vmc_OrderAdapter.getRealCardvalue();// 非现金退币金额
		    //商品信息
		    salesPricevalue= vmc_OrderAdapter.getSalesPricevalue();// 优惠价,如”20.00”
		    ourdercount=vmc_OrderAdapter.getCount();
		    
			int x=0;
			this.listMap.clear();
			for(x=0;x<vmc_OrderAdapter.getCount();x++)
			{
			  	Map<String,String> map = new HashMap<String,String>();//定义Map集合，保存每一行数据
			   	map.put("ordereID", ordereID[x]);
		    	map.put("payType", payType[x]);
		    	map.put("payStatus", payStatus[x]);
		    	map.put("RealStatus", RealStatus[x]);
		    	map.put("productName", productName[x]);
		    	map.put("salesPrice", salesPrice[x]);
		    	map.put("cabID", cabID[x]);
		    	map.put("columnID", columnID[x]);
		    	map.put("payTime", payTime[x]);
		    	this.listMap.add(map);//保存数据行
			}
			//将这个构架加载到data_list中
			this.simpleada = new SimpleAdapter(this,this.listMap,R.layout.orderlist,
			    		new String[]{"ordereID","payType","payStatus","RealStatus","productName","salesPrice","cabID","columnID","payTime"},//Map中的key名称
			    		new int[]{R.id.txtordereID,R.id.txtpayType,R.id.txtpayStatus,R.id.txtRealStatus,R.id.txtproductName,R.id.txtsalesPrice,R.id.txtcabID,R.id.txtcolumnID,R.id.txtpayTime});
			this.lvorder.setAdapter(this.simpleada);
			
			//作图表统计信息
			chartcount();
			
		}
		else
		{
			Toast.makeText(Order.this, "请输入正确查询时间！", Toast.LENGTH_SHORT).show();
		}
	}
	
	//作图表统计信息
	private void chartcount()
	{	
		int j=0;
		//清空数据
		Arrays.fill(Amountvalue, 0); 
		Arrays.fill(Countvalue, 0); 
		Amountmax=0;
		Countmax=0;
		
		for(int i=0,mon=0;i<24;i++)	
		{
			//上半月
			if(i%2==0)
			{
				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<"+(mon+1)+"月上：="+getLastDayOfMonth(eYear,(mon+1),0)+"到"+getLastDayOfMonth(eYear,(mon+1),1));
				for(j=0;j<ourdercount;j++)
				{
					if(isdatein(getLastDayOfMonth(eYear,(mon+1),0),getLastDayOfMonth(eYear,(mon+1),1),payTime[j])==true)
					{
						ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<"+payTime[j]+"是在");
						Amountvalue[i]+=salesPricevalue[j];
						Countvalue[i]+=1;
					}
					else
					{
						//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<"+payTime[j]+"不是在");
					}	
				}
			}
			//下半月
			else
			{
				ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<"+(mon+1)+"月下：="+getLastDayOfMonth(eYear,(mon+1),1)+"到"+getLastDayOfMonth(eYear,(mon+1),2));
				for(j=0;j<ourdercount;j++)
				{
					if(isdatein(getLastDayOfMonth(eYear,(mon+1),1),getLastDayOfMonth(eYear,(mon+1),2),payTime[j])==true)
					{
						ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<"+payTime[j]+"是在");
						Amountvalue[i]+=salesPricevalue[j];
						Countvalue[i]+=1;
						
					}
					else
					{
						//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<"+payTime[j]+"不是在");
					}	
				}
				mon++;
			}
		}
		//取得最高的值
		for(int i=0;i<24;i++)	
		{
			Amountmax=(Amountvalue[i]>Amountmax)?Amountvalue[i]:Amountmax;
			Countmax=(Countvalue[i]>Countmax)?Countvalue[i]:Countmax;			
		}
		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<amount="+Amountmax+",count="+Countmax);
	}
	
	/**
	 * 获取某月的最后一天或者中旬
	 * @Title:getLastDayOfMonth
	 * @Description:
	 * @param:@param year
	 * @param:@param month
	 * @param:@param type=0月初,1月中旬,2月下旬
	 * @param:@return
	 * @return:String
	 * @throws
	 */
	private String getLastDayOfMonth(int year,int month,int type)
	{
		Calendar cal = Calendar.getInstance();
		//设置年份
		cal.set(Calendar.YEAR,year);
		//设置月份
		cal.set(Calendar.MONTH, month-1);
		if(type==0)
		{
			//设置日历中月份的中旬
			cal.set(Calendar.DAY_OF_MONTH, 1);
		}
		else if(type==1)
		{
			//设置日历中月份的中旬
			cal.set(Calendar.DAY_OF_MONTH, 15);
		}
		else if(type==2)
		{
			//获取某月最大天数
			int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			//设置日历中月份的最大天数
			cal.set(Calendar.DAY_OF_MONTH, lastDay);
		}
		//格式化日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String lastDayOfMonth = sdf.format(cal.getTime());
		
		return lastDayOfMonth;
	}
	//是否在时间区间中,s是需要比较的时间,begin,end是时间区间
	//是返回true
	private boolean isdatein(String begin,String end,String s)
	{
		//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<"+s+"在"+begin+"="+dateCompare(s,begin));
		//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<"+s+"在"+end+"="+dateCompare(end,s));
		if((dateCompare(s,begin)>=0)&&(dateCompare(end,s)>=0))
		{
			return true;
		}
		return false;
	}
	//时间比较,返回值result==0s1相等s2,result<0s1小于s2,result:>0s1大于s2,
	private int dateCompare(String s1,String s2)
	{
		//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<"+s1);
		//ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<"+s2);
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Calendar c1=java.util.Calendar.getInstance();
		java.util.Calendar c2=java.util.Calendar.getInstance();
		try
		{
			c1.setTime(df.parse(s1));
			c2.setTime(df.parse(s2));
		}catch(java.text.ParseException e){
			System.err.println("格式不正确");
		}
		return c1.compareTo(c2);
	}
}
