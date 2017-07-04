package com.easivend.fragment;

import com.easivend.app.business.BusPort;
import com.easivend.app.business.BusPort.BusPortFragInteraction;
import com.easivend.common.OrderDetail;
import com.easivend.common.ToolClass;
import com.easivend.dao.vmc_system_parameterDAO;
import com.easivend.model.Tb_vmc_system_parameter;
import com.example.evconsole.R;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class BuszhiamountFragment extends Fragment 
{
	TextView txtbuszhiamountcount=null,txtbuszhiamountAmount=null,txtbuszhiamountbillAmount=null,txtbuszhiamounttime=null,
			txtbuszhiamounttsxx=null;
	ImageButton imgbtnbuszhiamountqxzf=null,imgbtnbuszhiamountqtzf=null;
	ImageView imgbtnbusgoodsback=null;
	float amount=0;//��Ʒ��Ҫ֧�����	
	ImageView ivbuszhiselamount=null,ivbuszhiselzhier=null,ivbuszhiselweixing=null,ivbuszhiselpos=null;
	ImageView ivbuszhier=null,ivbuszhiwei=null,ivbusyinlian=null;
//	private String proID = null;
//	private String productID = null;
//	private String proType = null;
//	private String cabID = null;
//	private String huoID = null;
//    private String prosales = null;
//    private String count = null;
//    private String reamin_amount = null;    
//    private String id="";
    private String out_trade_no=null;    
	private Context context;
	//=========================
    //fragment��activity�ص����
    //=========================
    /**
     * �������ⲿactivity������
     */
    private BuszhiamountFragInteraction listterner;
    /**
     * �����ġ���ContentFragment�����ص�activity��ʱ������ע��ص���Ϣ
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(activity instanceof BuszhiamountFragInteraction)
        {
            listterner = (BuszhiamountFragInteraction)activity;
        }
        else{
            throw new IllegalArgumentException("activity must implements BuszhiamountFragInteraction");
        }

    }
    /**
     * ����һ������������activity����ʵ�ֵĽӿ�
     */
    public interface BuszhiamountFragInteraction
    {
        /**
         * Fragment ��Activity����ָ�����������Ը�������������
         * @param str
         */
        //void BusgoodsselectSwitch(int buslevel);//�л���BusZhixxҳ��
        void BuszhiamountFinish();      //�л���businessҳ��
    }
    @Override
    public void onDetach() {
        super.onDetach();

        listterner = null;
    }
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_buszhiamount, container, false);  
		context=this.getActivity();//��ȡactivity��context
		amount=OrderDetail.getShouldPay()*OrderDetail.getShouldNo();		
		txtbuszhiamountcount= (TextView) view.findViewById(R.id.txtbuszhiamountcount);
		txtbuszhiamountcount.setText(String.valueOf(OrderDetail.getShouldNo()));
		txtbuszhiamountAmount= (TextView) view.findViewById(R.id.txtbuszhiamountAmount);
		txtbuszhiamountAmount.setText(String.valueOf(amount));
		txtbuszhiamountbillAmount= (TextView) view.findViewById(R.id.txtbuszhiamountbillAmount);		
		txtbuszhiamounttime = (TextView) view.findViewById(R.id.txtbuszhiamounttime);
		txtbuszhiamounttsxx = (TextView) view.findViewById(R.id.txtbuszhiamounttsxx);
		imgbtnbuszhiamountqxzf = (ImageButton) view.findViewById(R.id.imgbtnbuszhiamountqxzf);
		imgbtnbuszhiamountqxzf.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {		    	
		    	listterner.BuszhiamountFinish();//�������fragment��activity���ͻص���Ϣ
		    }
		});
		this.imgbtnbusgoodsback=(ImageView)view.findViewById(R.id.imgbtnbusgoodsback);
		imgbtnbusgoodsback.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {		    	
		    	listterner.BuszhiamountFinish();//�������fragment��activity���ͻص���Ϣ
		    }
		});
		/**
	     * ����������fragment������,
	     * �����塢��Fragment�����ص�activity��ʱ��ע��ص���Ϣ
	     * @param activity
	     */
		BusPort.setCallBack(new buportInterfaceImp());
		ivbuszhiselamount = (ImageView) view.findViewById(R.id.ivbuszhiselamount);
		ivbuszhiselzhier = (ImageView) view.findViewById(R.id.ivbuszhiselzhier);
		ivbuszhiselweixing = (ImageView) view.findViewById(R.id.ivbuszhiselweixing);	
		ivbuszhiselpos = (ImageView) view.findViewById(R.id.ivbuszhiselpos);
		ivbuszhier = (ImageView) view.findViewById(R.id.ivbuszhier);
		ivbusyinlian = (ImageView) view.findViewById(R.id.ivbusyinlian);
		ivbuszhiwei = (ImageView) view.findViewById(R.id.ivbuszhiwei);
		//*********************
		//�������Եõ���֧����ʽ
		//*********************
		vmc_system_parameterDAO parameterDAO = new vmc_system_parameterDAO(context);// ����InaccountDAO����
	    // ��ȡ����������Ϣ�����洢��List���ͼ�����
    	Tb_vmc_system_parameter tb_inaccount = parameterDAO.find();
    	if(tb_inaccount!=null)
    	{
    		if(tb_inaccount.getAmount()==0)
    		{
    			ivbuszhiselamount.setVisibility(View.GONE);//�ر�
    		}
    		else
    		{
    			ivbuszhiselamount.setVisibility(View.VISIBLE);//��
    		}	
    		if(tb_inaccount.getZhifubaoer()==0)
    		{
    			ivbuszhiselzhier.setVisibility(View.GONE);//�ر�
    			ivbuszhier.setVisibility(View.GONE);//�ر�
    		}
    		else
    		{
    			ivbuszhiselzhier.setVisibility(View.VISIBLE);//��
    			ivbuszhier.setVisibility(View.VISIBLE);//��
    		}
    		if(tb_inaccount.getWeixing()==0)
    		{
    			ivbuszhiselweixing.setVisibility(View.GONE);//�ر�
    			ivbuszhiwei.setVisibility(View.GONE);//�ر�
    		}
    		else
    		{
    			ivbuszhiselweixing.setVisibility(View.VISIBLE);//��
    			ivbuszhiwei.setVisibility(View.VISIBLE);//��
    		}
    		//pos��                                                                             ������ά��
    		if((tb_inaccount.getZhifubaofaca()==0)&&(tb_inaccount.getPrinter()==0))
    		{
    			ivbuszhiselpos.setVisibility(View.GONE);//�ر�
    			ivbusyinlian.setVisibility(View.GONE);//�ر�
    		}
    		//pos����
    		else if(tb_inaccount.getZhifubaofaca()==1)
    		{
    			ivbuszhiselpos.setVisibility(View.VISIBLE);//��
    			ivbusyinlian.setVisibility(View.GONE);//�ر�
    		}    		
    		//������ά���
    		else if(tb_inaccount.getPrinter()==1)
    		{
    			ivbuszhiselpos.setVisibility(View.VISIBLE);//��
    			ivbusyinlian.setVisibility(View.VISIBLE);//��
    		} 
    	}
		return view;
	}
	
	private class buportInterfaceImp implements BusPortFragInteraction//���ؽӿ�
	{
		/**
	     * ����������fragment������,
	     * ��������ʵ��BusPortFragInteraction�ӿ�
	     * @param activity
	     */
		@Override
		public void BusportTsxx(String str) {
			// TODO Auto-generated method stub
			txtbuszhiamounttsxx.setText(str);
		}

		@Override
		public void BusportTbje(String str) {
			// TODO Auto-generated method stub
			txtbuszhiamountbillAmount.setText(str);
		}

		@Override
		public void BusportChjg(int sta) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void BusportSend(String str) {
			// TODO Auto-generated method stub
			ivbuszhier.setImageBitmap(ToolClass.createQRImage(str));
		}

		@Override
		public void BusportSendWei(String str) {
			// TODO Auto-generated method stub
			ivbuszhiwei.setImageBitmap(ToolClass.createQRImage(str));
		}
		
		@Override
		public void BusportSendYinlian(String str) {
			// TODO Auto-generated method stub
			ivbusyinlian.setImageBitmap(ToolClass.createQRImage(str));
		}

		
	}
		
}


