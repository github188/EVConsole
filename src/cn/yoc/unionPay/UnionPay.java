package cn.yoc.unionPay;


import cn.yoc.unionPay.po.UnionOrder;
import cn.yoc.unionPay.sdk.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yocer
 * @version 2017/6/15
 * @description
 */
public class UnionPay {

    private static LogUtils logger = LogUtils.getLogger("UnionPay");


    //Ĭ�����õ���UTF-8
    public static String encoding = "UTF-8";
    //ȫ�����̶�ֵ
    public static String version = SDKConfig.getConfig().getVersion();


    /** ��������. */
    private static UnionPay unionPay = new UnionPay();


    private UnionPay() {
        super();
    }


    public static UnionPay getInstance() {
        return unionPay;
    }

    /**
     * ��ɨ�����ά�뽻��
     * @param order
     * @return
     * @throws IOException
     */
    public Map<String, String> precreate(UnionOrder order)
            throws IOException {

        Map<String, String> contentData = new HashMap<String, String>();

        /***����ȫ����ϵͳ����Ʒ����������encoding����ѡ�������������޸�***/
        contentData.put("version", version);            //�汾�� ȫ����Ĭ��ֵ
        contentData.put("encoding", encoding);     //�ַ������� ����ʹ��UTF-8,GBK���ַ�ʽ
        contentData.put("signMethod", SDKConfig.getConfig().getSignMethod()); //ǩ������
        contentData.put("txnType", "01");              		 	//�������� 01:����
        contentData.put("txnSubType", "07");           		 	//�������� 07���������Ѷ�ά��
        contentData.put("bizType", "000000");          		 	//��д000000
        contentData.put("channelType", "08");          		 	//�������� 08�ֻ�

        /***�̻��������***/
        contentData.put("merId", SDKConfig.getConfig().getMerId());   		 				//�̻����룬��ĳ��Լ�������̻��Ż���open��ע�������777�̻��Ų���
        contentData.put("accessType", "0");            		 	//�������ͣ��̻�������0 �������޸ģ�0��ֱ���̻��� 1�� �յ����� 2��ƽ̨�̻���
        contentData.put("orderId", order.getOrderId());        	 	    //�̻������ţ�8-40λ������ĸ�����ܺ���-����_�����������ж��ƹ���
        contentData.put("txnTime", order.getTxnTime());		 		    //��������ʱ�䣬ȡϵͳʱ�䣬��ʽΪYYYYMMDDhhmmss������ȡ��ǰʱ�䣬����ᱨtxnTime��Ч
        contentData.put("txnAmt", order.getTxnAmt());						//���׽�� ��λΪ�֣����ܴ�С����
        contentData.put("currencyCode", "156");                 //�����̻��̶� 156 �����

        // ���󷽱�����
        // ͸���ֶΣ���ѯ��֪ͨ�������ļ��о���ԭ�����֣�������Ҫ�����ò��޸��Լ�ϣ��͸�������ݡ�
        // ���ֲ��������ַ�ʱ����Ӱ��������밴���潨��ķ�ʽ��д��
        // 1. �����ȷ�����ݲ������&={}[]"'�ȷ���ʱ������ֱ����д���ݣ�����ķ������¡�
//		contentData.put("reqReserved", "͸����Ϣ1|͸����Ϣ2|͸����Ϣ3");
        // 2. ���ݿ��ܳ���&={}[]"'����ʱ��
        // 1) �����Ҫ�����ļ�������ʾ���ɽ��ַ��滻��ȫ�ǣ����������������ַ����Լ�д���룬�˴�����ʾ����
        // 2) ��������ļ�û����ʾҪ�󣬿���һ��base64�����£���
        //    ע��������ݳ��ȣ�ʵ�ʴ�������ݳ��Ȳ��ܳ���1024λ��
        //    ��ѯ��֪ͨ�Ƚӿڽ���ʱʹ��new String(Base64.decodeBase64(reqReserved), encoding);��base64���ٶ�����������������
//		contentData.put("reqReserved", Base64.encodeBase64String("�����ʽ����Ϣ������".toString().getBytes(encoding)));

        //��̨֪ͨ��ַ��������Ϊ�����ܷ��� http https���ɣ���֧���ɹ����������Զ����첽֪ͨ����post���̻����͵ĸõ�ַ����֧��ʧ�ܵĽ����������ᷢ�ͺ�̨֪ͨ��
        //��̨֪ͨ�������open.unionpay.com�������� ����  ��Ʒ�ӿڹ淶  ����֧����Ʒ�ӿڹ淶 ���ѽ��� �̻�֪ͨ
        //ע��:1.������Ϊ�����ܷ��ʣ������ղ���֪ͨ    2.http https����  3.�յ���̨֪ͨ����Ҫ10���ڷ���http200��302״̬��
        //    4.�������֪ͨ����������֪ͨ��10����δ�յ�����״̬�����Ӧ�����http200��302����ô��������һ��ʱ���ٴη��͡��ܹ�����5�Σ������������1��2��4��5 ���Ӻ���ٴ�֪ͨ��
        //    5.��̨֪ͨ��ַ��������˴��У��Ĳ��������磺http://abc/web?a=b&c=d �ں�̨֪ͨ���������֤ǩ��֮ǰ��Ҫ��д�߼�����Щ�ֶ�ȥ������ǩ�����򽫻���ǩʧ��
        contentData.put("backUrl", "http://127.0.0.1");

        /**�������������ǩ��������http post���󣬽���ͬ��Ӧ����**/
        Map<String, String> reqData = AcpService.sign(contentData, encoding);			 //������certId,signature��ֵ����signData�����л�ȡ���Զ���ֵ�ģ�ֻҪ֤��������ȷ���ɡ�
        String requestAppUrl = SDKConfig.getConfig().getBackRequestUrl();								 //��������url�������ļ���ȡ��Ӧ�����ļ�acp_sdk.properties�е� acpsdk.backTransUrl
        Map<String, String> rspData = AcpService.post(reqData,requestAppUrl,encoding);  //���������Ĳ�����ͬ��Ӧ��Ĭ�����ӳ�ʱʱ��30�룬��ȡ���ؽ����ʱʱ��30�룩;�������signData֮�󣬵���submitUrl֮ǰ���ܶ�submitFromData�еļ�ֵ�����κ��޸ģ�����޸Ļᵼ����ǩ��ͨ��

        /**��Ӧ����Ĵ������������ҵ���߼�����д����,����Ӧ���봦���߼������ο�------------->**/
        //Ӧ����淶�ο�open.unionpay.com�������� ����  ��Ʒ�ӿڹ淶  ��ƽ̨����ӿڹ淶-��5����-��¼��
        if(!rspData.isEmpty()){
            if(AcpService.validate(rspData, encoding)){
                LogUtil.writeLog("��֤ǩ���ɹ�");
                String respCode = rspData.get("respCode") ;
                if(("00").equals(respCode)){
                    //�ɹ�,��ȡtn��
                    //String tn = resmap.get("tn");
                    //TODO
                }else{
                    //����Ӧ����Ϊʧ�����Ų�ԭ�����ʧ�ܴ���
                    LogUtil.debug("����Ӧ����Ϊʧ�����Ų�ԭ�����ʧ�ܴ���");
                    //TODO
                }
            }else{
                LogUtil.writeErrorLog("��֤ǩ��ʧ��");
                //TODO �����֤ǩ��ʧ�ܵ�ԭ��
            }
        }else{
            //δ������ȷ��http״̬
            LogUtil.writeErrorLog("δ��ȡ�����ر��Ļ򷵻�http״̬���200");
        }
        //String reqMessage = genHtmlResult(reqData);
        //String rspMessage = genHtmlResult(rspData);
        logger.info("��������:" + reqData);
        logger.info("��Ӧ����:" + rspData);
        return rspData;

    }


    /**
     * ����״̬��ѯ����
     * @param order
     * @return
     * @throws IOException
     */
    public Map<String, String> query(UnionOrder order)
            throws IOException {


        //String merId = order.getMerId();
        String orderId = order.getOrderId();
        String txnTime = order.getTxnTime();

        Map<String, String> data = new HashMap<String, String>();

        /***����ȫ����ϵͳ����Ʒ����������encoding����ѡ�������������޸�***/
        data.put("version", version);                 //�汾��
        data.put("encoding", encoding);          //�ַ������� ����ʹ��UTF-8,GBK���ַ�ʽ
        data.put("signMethod", SDKConfig.getConfig().getSignMethod()); //ǩ������
        data.put("txnType", "00");                             //�������� 00-Ĭ��
        data.put("txnSubType", "00");                          //����������  Ĭ��00
        data.put("bizType", "000201");                         //ҵ������

        /***�̻��������***/
        data.put("merId", SDKConfig.getConfig().getMerId());                  			   //�̻����룬��ĳ��Լ�������̻��Ż���open��ע�������777�̻��Ų���
        data.put("accessType", "0");                           //�������ͣ��̻�����̶���0�������޸�

        /***Ҫ��ͨ���������ֶα����޸�***/
        data.put("orderId", orderId);                 			//****�̻������ţ�ÿ�η����ײ������޸�Ϊ����ѯ�Ľ��׵Ķ�����
        data.put("txnTime", txnTime);                			//****��������ʱ�䣬ÿ�η����ײ������޸�Ϊ����ѯ�Ľ��׵Ķ�������ʱ��

        /**�������������ϣ����¶������������ǩ��������http post���󣬽���ͬ��Ӧ����------------->**/

        Map<String, String> reqData = AcpService.sign(data,encoding);			//������certId,signature��ֵ����signData�����л�ȡ���Զ���ֵ�ģ�ֻҪ֤��������ȷ���ɡ�
        String url = SDKConfig.getConfig().getSingleQueryUrl();								//��������url�������ļ���ȡ��Ӧ�����ļ�acp_sdk.properties�е� acpsdk.singleQueryUrl
        Map<String, String> rspData = AcpService.post(reqData, url,encoding); //���������Ĳ�����ͬ��Ӧ��Ĭ�����ӳ�ʱʱ��30�룬��ȡ���ؽ����ʱʱ��30�룩;�������signData֮�󣬵���submitUrl֮ǰ���ܶ�submitFromData�еļ�ֵ�����κ��޸ģ�����޸Ļᵼ����ǩ��ͨ��

        /**��Ӧ����Ĵ������������ҵ���߼�����д����,����Ӧ���봦���߼������ο�------------->**/
        //Ӧ����淶�ο�open.unionpay.com�������� ����  ��Ʒ�ӿڹ淶  ��ƽ̨����ӿڹ淶-��5����-��¼��
        if(!rspData.isEmpty()){
            if(AcpService.validate(rspData, encoding)){
                LogUtil.writeLog("��֤ǩ���ɹ�");
                if(("00").equals(rspData.get("respCode"))){//�����ѯ���׳ɹ�
                    String origRespCode = rspData.get("origRespCode");
                    if(("00").equals(origRespCode)){
                        //���׳ɹ��������̻�����״̬
                        //TODO
                    }else if(("03").equals(origRespCode)||
                            ("04").equals(origRespCode)||
                            ("05").equals(origRespCode)){
                        //���������л���״̬δ�������Ժ�����״̬��ѯ���� �����������δȷ�������Ƿ�ɹ����Զ����ļ�Ϊ׼��
                        //TODO
                    }else{
                        //����Ӧ����Ϊ����ʧ��
                        //TODO
                    }
                }else if(("34").equals(rspData.get("respCode"))){
                    //���������ڣ�����Ϊ����״̬δ������Ҫ�Ժ�����״̬��ѯ�������ݶ��˽��Ϊ׼

                }else{//��ѯ���ױ���ʧ�ܣ���Ӧ����10/11����ѯ�����Ƿ���ȷ
                    //TODO
                }
            }else{
                LogUtil.writeErrorLog("��֤ǩ��ʧ��");
                //TODO �����֤ǩ��ʧ�ܵ�ԭ��
            }
        }else{
            //δ������ȷ��http״̬
            LogUtil.writeErrorLog("δ��ȡ�����ر��Ļ򷵻�http״̬���200");
        }

        return rspData;


    }

    /**
     * �˻����ף���̨�ʽ��ཻ��
     * @param order
     * @return
     * @throws IOException
     */
    public Map<String, String> refund(UnionOrder order)
            throws IOException {
        //String origQryId = order.getOrderId();
        String txnAmt = order.getRefundAmt();
        //String merId = order.getMerId();

        Map<String, String> data = new HashMap<String, String>();

        /***����ȫ����ϵͳ����Ʒ����������encoding����ѡ�������������޸�***/
        data.put("version", version);               //�汾��
        data.put("encoding", encoding);             //�ַ������� ����ʹ��UTF-8,GBK���ַ�ʽ
        data.put("signMethod", SDKConfig.getConfig().getSignMethod()); //ǩ������
        data.put("txnType", "04");                           //�������� 04-�˻�
        data.put("txnSubType", "00");                        //����������  Ĭ��00
        data.put("bizType", "000000");          		 	//��д000000
        data.put("channelType", "08");                       //�������ͣ�07-PC��08-�ֻ�

        /***�̻��������***/
        data.put("merId", SDKConfig.getConfig().getMerId());                //�̻����룬��ĳ��Լ�������̻��Ż���open��ע�������777�̻��Ų���
        data.put("accessType", "0");                         //�������ͣ��̻�����̶���0�������޸�
        data.put("orderId", DateUtils.makeOrderNo("refund"));          //�̻������ţ�8-40λ������ĸ�����ܺ���-����_�����������ж��ƹ������²�������ͬ��ԭ����
        data.put("txnTime", DateUtils.currentDateTime("yyyyMMddHHmmss"));      //��������ʱ�䣬��ʽΪYYYYMMDDhhmmss������ȡ��ǰʱ�䣬����ᱨtxnTime��Ч
        data.put("currencyCode", "156");                     //���ױ��֣������̻�һ����156 ����ң�
        data.put("txnAmt", txnAmt);                          //****�˻�����λ�֣���Ҫ��С���㡣�˻����С�ڵ���ԭ���ѽ���С�ڵ�ʱ����Զ���˻����˻��ۼƽ�����ԭ���ѽ��
        data.put("backUrl", SDKConfig.getConfig().getBackUrl());               //��̨֪ͨ��ַ����̨֪ͨ�������open.unionpay.com�������� ����  ��Ʒ�ӿڹ淶  ����֧����Ʒ�ӿڹ淶 �˻����� �̻�֪ͨ,����˵��ͬ���ѽ��׵ĺ�̨֪ͨ

        /***Ҫ��ͨ���������ֶα����޸�***/
        //data.put("origQryId", origQryId);      //****ԭ���ѽ��׷��صĵ�queryId�����Դ����ѽ��׺�̨֪ͨ�ӿ��л��߽���״̬��ѯ�ӿ��л�ȡ
        data.put("origOrderId", order.getOrderId());
        data.put("origTxnTime", order.getTxnTime());
        // ���󷽱�����
        // ͸���ֶΣ���ѯ��֪ͨ�������ļ��о���ԭ�����֣�������Ҫ�����ò��޸��Լ�ϣ��͸�������ݡ�
        // ���ֲ��������ַ�ʱ����Ӱ��������밴���潨��ķ�ʽ��д��
        // 1. �����ȷ�����ݲ������&={}[]"'�ȷ���ʱ������ֱ����д���ݣ�����ķ������¡�
//		data.put("reqReserved", "͸����Ϣ1|͸����Ϣ2|͸����Ϣ3");
        // 2. ���ݿ��ܳ���&={}[]"'����ʱ��
        // 1) �����Ҫ�����ļ�������ʾ���ɽ��ַ��滻��ȫ�ǣ����������������ַ����Լ�д���룬�˴�����ʾ����
        // 2) ��������ļ�û����ʾҪ�󣬿���һ��base64�����£���
        //    ע��������ݳ��ȣ�ʵ�ʴ�������ݳ��Ȳ��ܳ���1024λ��
        //    ��ѯ��֪ͨ�Ƚӿڽ���ʱʹ��new String(Base64.decodeBase64(reqReserved), encoding);��base64���ٶ�����������������
//		data.put("reqReserved", Base64.encodeBase64String("�����ʽ����Ϣ������".toString().getBytes(encoding)));

        /**�������������ϣ����¶������������ǩ��������http post���󣬽���ͬ��Ӧ����------------->**/
        Map<String, String> reqData  = AcpService.sign(data,encoding);		//������certId,signature��ֵ����signData�����л�ȡ���Զ���ֵ�ģ�ֻҪ֤��������ȷ���ɡ�
        String url = SDKConfig.getConfig().getBackRequestUrl();									//��������url�������ļ���ȡ��Ӧ�����ļ�acp_sdk.properties�е� acpsdk.backTransUrl
        Map<String, String> rspData = AcpService.post(reqData, url,encoding);//�������signData֮�󣬵���submitUrl֮ǰ���ܶ�submitFromData�еļ�ֵ�����κ��޸ģ�����޸Ļᵼ����ǩ��ͨ��

        /**��Ӧ����Ĵ������������ҵ���߼�����д����,����Ӧ���봦���߼������ο�------------->**/
        //Ӧ����淶�ο�open.unionpay.com�������� ����  ��Ʒ�ӿڹ淶  ��ƽ̨����ӿڹ淶-��5����-��¼��
        if(!rspData.isEmpty()){
            if(AcpService.validate(rspData, encoding)){
                LogUtil.writeLog("��֤ǩ���ɹ�");
                String respCode = rspData.get("respCode") ;
                if(("00").equals(respCode)){
                    //����������(���������ѳɹ������ȴ����պ�̨֪ͨ���¶���״̬,Ҳ������������ ��ѯ����ȷ������״̬��
                    //TODO
                }else if(("03").equals(respCode)||
                        ("04").equals(respCode)||
                        ("05").equals(respCode)){
                    //�����跢����״̬��ѯ����ȷ������״̬
                    //TODO
                }else{
                    //����Ӧ����Ϊʧ�����Ų�ԭ��
                    //TODO
                }
            }else{
                LogUtil.writeErrorLog("��֤ǩ��ʧ��");
                //TODO �����֤ǩ��ʧ�ܵ�ԭ��
            }
        }else{
            //δ������ȷ��http״̬
            LogUtil.writeErrorLog("δ��ȡ�����ر��Ļ򷵻�http״̬���200");
        }
        return rspData;
    }

    /**
     * ���ѳ�������̨�ʽ��ཻ�ף�
     * @param order
     * @return
     */
    public Map<String, String> consumeUndo(UnionOrder order) throws Exception{


        //String merId = order.getMerId();
        String orderId = order.getOrderId();
        String txnTime = order.getTxnTime();
        String txnAmt = order.getTxnAmt();

        Map<String, String> data = new HashMap<String, String>();

        /***����ȫ����ϵͳ����Ʒ����������encoding����ѡ�������������޸�***/
        data.put("version", version);            //�汾��
        data.put("encoding", encoding);     //�ַ������� ����ʹ��UTF-8,GBK���ַ�ʽ
        data.put("signMethod", SDKConfig.getConfig().getSignMethod()); //ǩ������
        data.put("txnType", "31");                        //�������� 31-���ѳ���
        data.put("txnSubType", "00");                     //����������  Ĭ��00
        data.put("bizType", "000000");          		  //��д000000
        data.put("channelType", "08");                    //�������ͣ�07-PC��08-�ֻ�

        /***�̻��������***/
        data.put("merId", SDKConfig.getConfig().getMerId());             			  //�̻����룬��ĳ��Լ�������̻��Ż���open��ע�������777�̻��Ų���
        data.put("accessType", "0");                      //�������ͣ��̻�����̶���0�������޸�
        data.put("orderId", DateUtils.makeOrderNo("consume"));       			  //�̻������ţ�8-40λ������ĸ�����ܺ���-����_�����������ж��ƹ������²�������ͬ��ԭ����
        data.put("txnTime", txnTime);   				  //��������ʱ�䣬��ʽΪYYYYMMDDhhmmss������ȡ��ǰʱ�䣬����ᱨtxnTime��Ч
        data.put("txnAmt", txnAmt);                       //�������������ѳ���ʱ�����ԭ���ѽ����ͬ
        data.put("currencyCode", "156");                  //���ױ���(�����̻�һ����156 �����)
        data.put("backUrl", SDKConfig.getConfig().getBackUrl());            //��̨֪ͨ��ַ����̨֪ͨ�������open.unionpay.com�������� ����  ��Ʒ�ӿڹ淶  ����֧����Ʒ�ӿڹ淶 ���ѳ������� �̻�֪ͨ,����˵��ͬ���ѽ��׵��̻�֪ͨ

        /***Ҫ��ͨ���������ֶα����޸�***/
        //data.put("origQryId", origQryId);   			  //��ԭʼ������ˮ�š���ԭ���ѽ��׷��صĵ�queryId�����Դ����ѽ��׺�̨֪ͨ�ӿ��л��߽���״̬��ѯ�ӿ��л�ȡ
        data.put("origOrderId", orderId);
        data.put("origTxnTime", txnTime);
        // ���󷽱�����
        // ͸���ֶΣ���ѯ��֪ͨ�������ļ��о���ԭ�����֣�������Ҫ�����ò��޸��Լ�ϣ��͸�������ݡ�
        // ���ֲ��������ַ�ʱ����Ӱ��������밴���潨��ķ�ʽ��д��
        // 1. �����ȷ�����ݲ������&={}[]"'�ȷ���ʱ������ֱ����д���ݣ�����ķ������¡�
//		data.put("reqReserved", "͸����Ϣ1|͸����Ϣ2|͸����Ϣ3");
        // 2. ���ݿ��ܳ���&={}[]"'����ʱ��
        // 1) �����Ҫ�����ļ�������ʾ���ɽ��ַ��滻��ȫ�ǣ����������������ַ����Լ�д���룬�˴�����ʾ����
        // 2) ��������ļ�û����ʾҪ�󣬿���һ��base64�����£���
        //    ע��������ݳ��ȣ�ʵ�ʴ�������ݳ��Ȳ��ܳ���1024λ��
        //    ��ѯ��֪ͨ�Ƚӿڽ���ʱʹ��new String(Base64.decodeBase64(reqReserved), encoding);��base64���ٶ�����������������
//		data.put("reqReserved", Base64.encodeBase64String("�����ʽ����Ϣ������".toString().getBytes(encoding)));

        /**�������������ϣ����¶������������ǩ��������http post���󣬽���ͬ��Ӧ����**/
        Map<String, String> reqData  = AcpService.sign(data,encoding);     //������certId,signature��ֵ����signData�����л�ȡ���Զ���ֵ�ģ�ֻҪ֤��������ȷ���ɡ�
        String url = SDKConfig.getConfig().getBackRequestUrl();						     //��������url�������ļ���ȡ��Ӧ�����ļ�acp_sdk.properties�е� acpsdk.backTransUrl
        Map<String,String> rspData = AcpService.post(reqData,url,encoding); //���������Ĳ�����ͬ��Ӧ��Ĭ�����ӳ�ʱʱ��30�룬��ȡ���ؽ����ʱʱ��30�룩;�������signData֮�󣬵���submitUrl֮ǰ���ܶ�submitFromData�еļ�ֵ�����κ��޸ģ�����޸Ļᵼ����ǩ��ͨ��

        //Ӧ����淶�ο�open.unionpay.com�������� ����  ��Ʒ�ӿڹ淶  ��ƽ̨����ӿڹ淶-��5����-��¼��
        if(!rspData.isEmpty()){
            if(AcpService.validate(rspData, encoding)){
                LogUtil.writeLog("��֤ǩ���ɹ�");
                String respCode = rspData.get("respCode") ;
                if(("00").equals(respCode)){
                    //����������(���������ѳɹ������ȴ����պ�̨֪ͨ���¶���״̬,Ҳ������������ ��ѯ����ȷ������״̬��
                    //TODO
                }else if(("03").equals(respCode)||
                        ("04").equals(respCode)||
                        ("05").equals(respCode)){
                    //�����跢����״̬��ѯ����ȷ������״̬
                    //TODO
                }else{
                    //����Ӧ����Ϊʧ�����Ų�ԭ��
                    //TODO
                }
            }else{
                LogUtil.writeErrorLog("��֤ǩ��ʧ��");
                //TODO �����֤ǩ��ʧ�ܵ�ԭ��
            }
        }else{
            //δ������ȷ��http״̬
            LogUtil.writeErrorLog("δ��ȡ�����ر��Ļ򷵻�http״̬���200");
        }

        return rspData;
    }


}
