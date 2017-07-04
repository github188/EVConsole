/**
 *
 * Licensed Property to China UnionPay Co., Ltd.
 * 
 * (C) Copyright of China UnionPay Co., Ltd. 2010
 *     All Rights Reserved.
 *
 * 
 * Modification History:
 * =============================================================================
 *   Author         Date          Description
 *   ------------ ---------- ---------------------------------------------------
 *   xshu       2014-05-28       MPI��������������
 * =============================================================================
 */
package cn.yoc.unionPay.sdk;


import android.content.Context;

import java.io.*;
import java.util.Properties;

/**
 * 
 * @ClassName SDKConfig
 * @Description acpsdk�����ļ�acp_sdk.properties������Ϣ��
 * @date 2016-7-22 ����4:04:55
 *
 */
public class SDKConfig {
	public static final String FILE_NAME = "acp_sdk.properties";
	/** ǰ̨����URL. */
	private String frontRequestUrl;
	/** ��̨����URL. */
	private String backRequestUrl;
	/** ���ʲ�ѯ */
	private String singleQueryUrl;
	/** ������ѯ */
	private String batchQueryUrl;
	/** �������� */
	private String batchTransUrl;
	/** �ļ����� */
	private String fileTransUrl;
	/** ǩ��֤��·��. */
	private String signCertPath;
	/** ǩ��֤������. */
	private String signCertPwd;
	/** ǩ��֤������. */
	private String signCertType;
	/** ���ܹ�Կ֤��·��. */
	private String encryptCertPath;
	/** ��֤ǩ����Կ֤��Ŀ¼. */
	private String validateCertDir;
	/** �����̻������ȡָ��ǩ��֤��Ŀ¼. */
	private String signCertDir;
	/** �ŵ�����֤��·��. */
	private String encryptTrackCertPath;
	/** �ŵ����ܹ�Կģ��. */
	private String encryptTrackKeyModulus;
	/** �ŵ����ܹ�Կָ��. */
	private String encryptTrackKeyExponent;
	/** �п�����. */
	private String cardRequestUrl;
	/** app���� */
	private String appRequestUrl;
	/** ֤��ʹ��ģʽ(��֤��/��֤��) */
	private String singleMode;
	/** ��ȫ��Կ(SHA256��SM3����ʱʹ��) */
	private String secureKey;
	/** �м�֤��·��  */
	private String middleCertPath;
	/** ��֤��·��  */
	private String rootCertPath;
	/** �Ƿ���֤��ǩ֤��CN������false����  */
	private boolean ifValidateCNName = true;
	/** �Ƿ���֤https֤�飬Ĭ�϶�����  */
	private boolean ifValidateRemoteCert = false;
	/** signMethod��û�䰴01��  */
	private String signMethod = "01";
	/** version��û�䰴5.0.0  */
	private String version = "5.0.0";
	/** frontUrl  */
	private String frontUrl;
	/** backUrl  */
	private String backUrl;

	private String merId;
	private String keyPath;

	/*�ɷ���ص�ַ*/
	private String jfFrontRequestUrl;
	private String jfBackRequestUrl;
	private String jfSingleQueryUrl;
	private String jfCardRequestUrl;
	private String jfAppRequestUrl;
	
	private String qrcBackTransUrl;
	private String qrcB2cIssBackTransUrl;
	private String qrcB2cMerBackTransUrl;

	/** �����ļ��е�ǰ̨URL����. */
	public static final String SDK_FRONT_URL = "acpsdk.frontTransUrl";
	/** �����ļ��еĺ�̨URL����. */
	public static final String SDK_BACK_URL = "acpsdk.backTransUrl";
	/** �����ļ��еĵ��ʽ��ײ�ѯURL����. */
	public static final String SDK_SIGNQ_URL = "acpsdk.singleQueryUrl";
	/** �����ļ��е��������ײ�ѯURL����. */
	public static final String SDK_BATQ_URL = "acpsdk.batchQueryUrl";
	/** �����ļ��е���������URL����. */
	public static final String SDK_BATTRANS_URL = "acpsdk.batchTransUrl";
	/** �����ļ��е��ļ��ཻ��URL����. */
	public static final String SDK_FILETRANS_URL = "acpsdk.fileTransUrl";
	/** �����ļ��е��п�����URL����. */
	public static final String SDK_CARD_URL = "acpsdk.cardTransUrl";
	/** �����ļ��е�app����URL����. */
	public static final String SDK_APP_URL = "acpsdk.appTransUrl";

	/** ���½ɷѲ�Ʒʹ�ã������Ʒ�ò��������Ӽ��� */
	// ǰ̨�����ַ
	public static final String JF_SDK_FRONT_TRANS_URL= "acpsdk.jfFrontTransUrl";
	// ��̨�����ַ
	public static final String JF_SDK_BACK_TRANS_URL="acpsdk.jfBackTransUrl";
	// ���ʲ�ѯ�����ַ
	public static final String JF_SDK_SINGLE_QUERY_URL="acpsdk.jfSingleQueryUrl";
	// �п����׵�ַ
	public static final String JF_SDK_CARD_TRANS_URL="acpsdk.jfCardTransUrl";
	// App���׵�ַ
	public static final String JF_SDK_APP_TRANS_URL="acpsdk.jfAppTransUrl";
	// �˵���
	public static final String QRC_BACK_TRANS_URL="acpsdk.qrcBackTransUrl";
	// �˵���
	public static final String QRC_B2C_ISS_BACK_TRANS_URL="acpsdk.qrcB2cIssBackTransUrl";
	// �˵���
	public static final String QRC_B2C_MER_BACK_TRANS_URL="acpsdk.qrcB2cMerBackTransUrl";
	
	
	/** �����ļ���ǩ��֤��·������. */
	public static final String SDK_SIGNCERT_PATH = "acpsdk.signCert.path";
	/** �����ļ���ǩ��֤�����볣��. */
	public static final String SDK_SIGNCERT_PWD = "acpsdk.signCert.pwd";
	/** �����ļ���ǩ��֤�����ͳ���. */
	public static final String SDK_SIGNCERT_TYPE = "acpsdk.signCert.type";
	/** �����ļ����������֤��·������. */
	public static final String SDK_ENCRYPTCERT_PATH = "acpsdk.encryptCert.path";
	/** �����ļ��дŵ�����֤��·������. */
	public static final String SDK_ENCRYPTTRACKCERT_PATH = "acpsdk.encryptTrackCert.path";
	/** �����ļ��дŵ����ܹ�Կģ������. */
	public static final String SDK_ENCRYPTTRACKKEY_MODULUS = "acpsdk.encryptTrackKey.modulus";
	/** �����ļ��дŵ����ܹ�Կָ������. */
	public static final String SDK_ENCRYPTTRACKKEY_EXPONENT = "acpsdk.encryptTrackKey.exponent";
	/** �����ļ�����֤ǩ��֤��Ŀ¼����. */
	public static final String SDK_VALIDATECERT_DIR = "acpsdk.validateCert.dir";


	public static final String SDK_SIGNCERT_MERID="acpsdk.signCert.merId";
	/** �����ļ����Ƿ����cvn2����. */
	public static final String SDK_CVN_ENC = "acpsdk.cvn2.enc";
	/** �����ļ����Ƿ����cvn2��Ч�ڳ���. */
	public static final String SDK_DATE_ENC = "acpsdk.date.enc";
	/** �����ļ����Ƿ���ܿ��ų���. */
	public static final String SDK_PAN_ENC = "acpsdk.pan.enc";
	/** �����ļ���֤��ʹ��ģʽ */
	public static final String SDK_SINGLEMODE = "acpsdk.singleMode";
	/** �����ļ��а�ȫ��Կ */
	public static final String SDK_SECURITYKEY = "acpsdk.secureKey";
	/** �����ļ��и�֤��·������  */
	public static final String SDK_ROOTCERT_PATH = "acpsdk.rootCert.path";
	/** �����ļ��и�֤��·������  */
	public static final String SDK_MIDDLECERT_PATH = "acpsdk.middleCert.path";
	/** �����Ƿ���Ҫ��֤��ǩ֤��CN������false֮���ֵ����true���� */
	public static final String SDK_IF_VALIDATE_CN_NAME = "acpsdk.ifValidateCNName";
	/** �����Ƿ���Ҫ��֤https֤�飬����true֮���ֵ����false���� */
	public static final String SDK_IF_VALIDATE_REMOTE_CERT = "acpsdk.ifValidateRemoteCert";
	/** signmethod */
	public static final String SDK_SIGN_METHOD ="acpsdk.signMethod";
	/** version */
	public static final String SDK_VERSION = "acpsdk.version";
	/** ��̨֪ͨ��ַ  */
	public static final String SDK_BACKURL = "acpsdk.backUrl";
	/** ǰ̨֪ͨ��ַ  */
	public static final String SDK_FRONTURL = "acpsdk.frontUrl";
	/** ��������. */
	private static SDKConfig config = new SDKConfig();
	/** �����ļ�����. */
	private Properties properties;

	private SDKConfig() {
		super();
	}

	/**
	 * ��ȡconfig����.
	 * @return
	 */
	public static SDKConfig getConfig() {
		return config;
	}

	/**
	 * ��properties�ļ�����
	 * 
	 * @param rootPath
	 *            �������ļ�����Ŀ¼.
	 */
	public void loadPropertiesFromPath(String rootPath) {
		if (StringUtils.isNotBlank(rootPath)) {
			LogUtil.writeLog("��·����ȡ�����ļ�: " + rootPath+ File.separator+FILE_NAME);
			File file = new File(rootPath + File.separator + FILE_NAME);
			InputStream in = null;
			if (file.exists()) {
				try {
                    this.keyPath = rootPath;
					in = new FileInputStream(file);
					properties = new Properties();
					properties.load(in);
					loadProperties(properties);
				} catch (FileNotFoundException e) {
					LogUtil.writeErrorLog(e.getMessage(), e);
				} catch (IOException e) {
					LogUtil.writeErrorLog(e.getMessage(), e);
				} finally {
					if (null != in) {
						try {
							in.close();
						} catch (IOException e) {
							LogUtil.writeErrorLog(e.getMessage(), e);
						}
					}
				}
			} else {
				// ���ڴ�ʱ���ܻ�û�����LOG�ļ��أ���˲��ñ�׼�������ӡ��־��Ϣ
				LogUtil.writeErrorLog(rootPath + FILE_NAME + "������,���ز���ʧ��");
			}
		} else {
			loadPropertiesFromSrc();
		}

	}


	public void loadPropertiesFromPath(Context context, String pathName) {

		String pf = "unionCert" + File.separator+FILE_NAME;
		this.keyPath = pathName + File.separator + "unionCert";
		LogUtil.writeLog("��·����ȡ�����ļ�: " + pf);
		InputStream in = null;
		try {
			in = context.getAssets().open(pf);
			properties = new Properties();
			properties.load(in);
			copyFilesFassets(context, "unionCert",this.keyPath);
			loadProperties(properties);
		} catch (Exception e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					LogUtil.writeErrorLog(e.getMessage(), e);
				}
			}
		}

	}


	/**
	 *  ��assetsĿ¼�и��������ļ�������
	 *  @param  context  Context ʹ��CopyFiles���Activity
	 *  @param  oldPath  String  ԭ�ļ�·��  �磺/aa
	 *  @param  newPath  String  ���ƺ�·��  �磺xx:/bb/cc
	 */
	public static int copyFilesFassets(Context context,String oldPath,String newPath) {
		try {
			String fileNames[] = context.getAssets().list(oldPath);//��ȡassetsĿ¼�µ������ļ���Ŀ¼��
			if (fileNames.length > 0) {//�����Ŀ¼
				File file = new File(newPath);
				file.mkdirs();//����ļ��в����ڣ���ݹ�
				for (String fileName : fileNames) {
					copyFilesFassets(context,oldPath + "/" + fileName,newPath+"/"+fileName);
				}
			} else {//������ļ�
				InputStream is = context.getAssets().open(oldPath);
				FileOutputStream fos = new FileOutputStream(new File(newPath));
				byte[] buffer = new byte[1024];
				int byteCount= 0;
				while((byteCount=is.read(buffer))!=-1) {//ѭ������������ȡ buffer�ֽ�
					fos.write(buffer, 0, byteCount);//����ȡ��������д�뵽�����
				}
				fos.flush();//ˢ�»�����
				is.close();
				fos.close();
			}
			return 1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogUtil.writeErrorLog("��������֤���ļ�����" + e.getMessage());
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * ��classpath·���¼������ò���
	 */
	public void loadPropertiesFromSrc() {
		InputStream in = null;
		try {
			LogUtil.writeLog("��classpath: " +SDKConfig.class.getClassLoader().getResource("").getPath()+" ��ȡ�����ļ�"+FILE_NAME);
			in = SDKConfig.class.getClassLoader().getResourceAsStream(FILE_NAME);
			if (null != in) {
				properties = new Properties();
				try {
					properties.load(in);
				} catch (IOException e) {
					throw e;
				}
			} else {
				LogUtil.writeErrorLog(FILE_NAME + "�����ļ�δ����classpathָ����Ŀ¼�� "+SDKConfig.class.getClassLoader().getResource("").getPath()+" �ҵ�!");
				return;
			}


			loadProperties(properties);
		} catch (IOException e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					LogUtil.writeErrorLog(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * ���ݴ���� {@link #load(Properties)}�����������ò���
	 * 
	 * @param pro
	 */
	public void loadProperties(Properties pro) {
		LogUtil.writeLog("��ʼ�������ļ��м���������");
		String value = null;

		value = pro.getProperty(SDK_SIGNCERT_MERID);
		if (!SDKUtil.isEmpty(value)) {
			this.merId = value.trim();
			LogUtil.writeLog("����������̻���==>"+SDK_SIGNCERT_MERID +"==>"+ value+" �Ѽ���");
		}

		value = pro.getProperty(SDK_SIGNCERT_PATH);
		if (!SDKUtil.isEmpty(value)) {
			this.signCertPath = this.keyPath == null ? value.trim() : this.keyPath + File.separator + value.trim();
			LogUtil.writeLog("�����˽Կǩ��֤��·��==>"+SDK_SIGNCERT_PATH +"==>"+ value+" �Ѽ���");
		}
		value = pro.getProperty(SDK_SIGNCERT_PWD);
		if (!SDKUtil.isEmpty(value)) {
			this.signCertPwd = value.trim();
			LogUtil.writeLog("�����˽Կǩ��֤������==>"+SDK_SIGNCERT_PWD +" �Ѽ���");
		}
		value = pro.getProperty(SDK_SIGNCERT_TYPE);
		if (!SDKUtil.isEmpty(value)) {
			this.signCertType = value.trim();
			LogUtil.writeLog("�����˽Կǩ��֤������==>"+SDK_SIGNCERT_TYPE +"==>"+ value+" �Ѽ���");
		}
		value = pro.getProperty(SDK_ENCRYPTCERT_PATH);
		if (!SDKUtil.isEmpty(value)) {
			this.encryptCertPath = this.keyPath == null ? value.trim() : this.keyPath + File.separator + value.trim();
			LogUtil.writeLog("�����������Ϣ����֤��==>"+SDK_ENCRYPTCERT_PATH +"==>"+ value+" �Ѽ���");
		}
		value = pro.getProperty(SDK_VALIDATECERT_DIR);
		if (!SDKUtil.isEmpty(value)) {
			this.validateCertDir = this.keyPath == null ? value.trim() : this.keyPath + File.separator + value.trim();
			LogUtil.writeLog("�������֤ǩ��֤��·��(�������õ���Ŀ¼����Ҫָ������Կ�ļ�)==>"+SDK_VALIDATECERT_DIR +"==>"+ value+" �Ѽ���");
		}
		value = pro.getProperty(SDK_FRONT_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.frontRequestUrl = value.trim();
		}
		value = pro.getProperty(SDK_BACK_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.backRequestUrl = value.trim();
		}
		value = pro.getProperty(SDK_BATQ_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.batchQueryUrl = value.trim();
		}
		value = pro.getProperty(SDK_BATTRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.batchTransUrl = value.trim();
		}
		value = pro.getProperty(SDK_FILETRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.fileTransUrl = value.trim();
		}
		value = pro.getProperty(SDK_SIGNQ_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.singleQueryUrl = value.trim();
		}
		value = pro.getProperty(SDK_CARD_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.cardRequestUrl = value.trim();
		}
		value = pro.getProperty(SDK_APP_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.appRequestUrl = value.trim();
		}
		value = pro.getProperty(SDK_ENCRYPTTRACKCERT_PATH);
		if (!SDKUtil.isEmpty(value)) {
			this.encryptTrackCertPath = this.keyPath == null ? value.trim() : this.keyPath + File.separator + value.trim();
		}

		value = pro.getProperty(SDK_SECURITYKEY);
		if (!SDKUtil.isEmpty(value)) {
			this.secureKey = value.trim();
		}
		value = pro.getProperty(SDK_ROOTCERT_PATH);
		if (!SDKUtil.isEmpty(value)) {
			this.rootCertPath = this.keyPath == null ? value.trim() : this.keyPath + File.separator + value.trim();
		}
		value = pro.getProperty(SDK_MIDDLECERT_PATH);
		if (!SDKUtil.isEmpty(value)) {
			this.middleCertPath = this.keyPath == null ? value.trim() : this.keyPath + File.separator + value.trim();
		}

		/**�ɷѲ���**/
		value = pro.getProperty(JF_SDK_FRONT_TRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.jfFrontRequestUrl = value.trim();
		}

		value = pro.getProperty(JF_SDK_BACK_TRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.jfBackRequestUrl = value.trim();
		}
		
		value = pro.getProperty(JF_SDK_SINGLE_QUERY_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.jfSingleQueryUrl = value.trim();
		}
		
		value = pro.getProperty(JF_SDK_CARD_TRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.jfCardRequestUrl = value.trim();
		}
		
		value = pro.getProperty(JF_SDK_APP_TRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.jfAppRequestUrl = value.trim();
		}
		
		value = pro.getProperty(QRC_BACK_TRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.qrcBackTransUrl = value.trim();
		}
		
		value = pro.getProperty(QRC_B2C_ISS_BACK_TRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.qrcB2cIssBackTransUrl = value.trim();
		}
		
		value = pro.getProperty(QRC_B2C_MER_BACK_TRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.qrcB2cMerBackTransUrl = value.trim();
		}

		value = pro.getProperty(SDK_ENCRYPTTRACKKEY_EXPONENT);
		if (!SDKUtil.isEmpty(value)) {
			this.encryptTrackKeyExponent = value.trim();
		}

		value = pro.getProperty(SDK_ENCRYPTTRACKKEY_MODULUS);
		if (!SDKUtil.isEmpty(value)) {
			this.encryptTrackKeyModulus = value.trim();
		}
		
		value = pro.getProperty(SDK_IF_VALIDATE_CN_NAME);
		if (!SDKUtil.isEmpty(value)) {
			if( SDKConstants.FALSE_STRING.equals(value.trim()))
					this.ifValidateCNName = false;
		}
		
		value = pro.getProperty(SDK_IF_VALIDATE_REMOTE_CERT);
		if (!SDKUtil.isEmpty(value)) {
			if( SDKConstants.TRUE_STRING.equals(value.trim()))
					this.ifValidateRemoteCert = true;
		}
		
		value = pro.getProperty(SDK_SIGN_METHOD);
		if (!SDKUtil.isEmpty(value)) {
			this.signMethod = value.trim();
		}
		
		value = pro.getProperty(SDK_SIGN_METHOD);
		if (!SDKUtil.isEmpty(value)) {
			this.signMethod = value.trim();
		}
		value = pro.getProperty(SDK_VERSION);
		if (!SDKUtil.isEmpty(value)) {
			this.version = value.trim();
		}
		value = pro.getProperty(SDK_FRONTURL);
		if (!SDKUtil.isEmpty(value)) {
			this.frontUrl = value.trim();
		}
		value = pro.getProperty(SDK_BACKURL);
		if (!SDKUtil.isEmpty(value)) {
			this.backUrl = value.trim();
		}
	}


	public String getFrontRequestUrl() {
		return frontRequestUrl;
	}

	public void setFrontRequestUrl(String frontRequestUrl) {
		this.frontRequestUrl = frontRequestUrl;
	}

	public String getBackRequestUrl() {
		return backRequestUrl;
	}

	public void setBackRequestUrl(String backRequestUrl) {
		this.backRequestUrl = backRequestUrl;
	}

	public String getSignCertPath() {
		return signCertPath;
	}

	public void setSignCertPath(String signCertPath) {
		this.signCertPath = signCertPath;
	}

	public String getSignCertPwd() {
		return signCertPwd;
	}

	public void setSignCertPwd(String signCertPwd) {
		this.signCertPwd = signCertPwd;
	}

	public String getSignCertType() {
		return signCertType;
	}

	public void setSignCertType(String signCertType) {
		this.signCertType = signCertType;
	}

	public String getEncryptCertPath() {
		return encryptCertPath;
	}

	public void setEncryptCertPath(String encryptCertPath) {
		this.encryptCertPath = encryptCertPath;
	}
	
	public String getValidateCertDir() {
		return validateCertDir;
	}

	public void setValidateCertDir(String validateCertDir) {
		this.validateCertDir = validateCertDir;
	}

	public String getSingleQueryUrl() {
		return singleQueryUrl;
	}

	public void setSingleQueryUrl(String singleQueryUrl) {
		this.singleQueryUrl = singleQueryUrl;
	}

	public String getBatchQueryUrl() {
		return batchQueryUrl;
	}

	public void setBatchQueryUrl(String batchQueryUrl) {
		this.batchQueryUrl = batchQueryUrl;
	}

	public String getBatchTransUrl() {
		return batchTransUrl;
	}

	public void setBatchTransUrl(String batchTransUrl) {
		this.batchTransUrl = batchTransUrl;
	}

	public String getFileTransUrl() {
		return fileTransUrl;
	}

	public void setFileTransUrl(String fileTransUrl) {
		this.fileTransUrl = fileTransUrl;
	}

	public String getSignCertDir() {
		return signCertDir;
	}

	public void setSignCertDir(String signCertDir) {
		this.signCertDir = signCertDir;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public String getCardRequestUrl() {
		return cardRequestUrl;
	}

	public void setCardRequestUrl(String cardRequestUrl) {
		this.cardRequestUrl = cardRequestUrl;
	}

	public String getAppRequestUrl() {
		return appRequestUrl;
	}

	public void setAppRequestUrl(String appRequestUrl) {
		this.appRequestUrl = appRequestUrl;
	}
	
	public String getEncryptTrackCertPath() {
		return encryptTrackCertPath;
	}

	public void setEncryptTrackCertPath(String encryptTrackCertPath) {
		this.encryptTrackCertPath = encryptTrackCertPath;
	}
	
	public String getJfFrontRequestUrl() {
		return jfFrontRequestUrl;
	}

	public void setJfFrontRequestUrl(String jfFrontRequestUrl) {
		this.jfFrontRequestUrl = jfFrontRequestUrl;
	}

	public String getJfBackRequestUrl() {
		return jfBackRequestUrl;
	}

	public void setJfBackRequestUrl(String jfBackRequestUrl) {
		this.jfBackRequestUrl = jfBackRequestUrl;
	}

	public String getJfSingleQueryUrl() {
		return jfSingleQueryUrl;
	}

	public void setJfSingleQueryUrl(String jfSingleQueryUrl) {
		this.jfSingleQueryUrl = jfSingleQueryUrl;
	}

	public String getJfCardRequestUrl() {
		return jfCardRequestUrl;
	}

	public void setJfCardRequestUrl(String jfCardRequestUrl) {
		this.jfCardRequestUrl = jfCardRequestUrl;
	}

	public String getJfAppRequestUrl() {
		return jfAppRequestUrl;
	}

	public void setJfAppRequestUrl(String jfAppRequestUrl) {
		this.jfAppRequestUrl = jfAppRequestUrl;
	}

	public String getSingleMode() {
		return singleMode;
	}

	public void setSingleMode(String singleMode) {
		this.singleMode = singleMode;
	}

	public String getEncryptTrackKeyExponent() {
		return encryptTrackKeyExponent;
	}

	public void setEncryptTrackKeyExponent(String encryptTrackKeyExponent) {
		this.encryptTrackKeyExponent = encryptTrackKeyExponent;
	}

	public String getEncryptTrackKeyModulus() {
		return encryptTrackKeyModulus;
	}

	public void setEncryptTrackKeyModulus(String encryptTrackKeyModulus) {
		this.encryptTrackKeyModulus = encryptTrackKeyModulus;
	}
	
	public String getSecureKey() {
		return secureKey;
	}

	public void setSecureKey(String securityKey) {
		this.secureKey = securityKey;
	}
	
	public String getMiddleCertPath() {
		return middleCertPath;
	}

	public void setMiddleCertPath(String middleCertPath) {
		this.middleCertPath = middleCertPath;
	}
	
	public boolean isIfValidateCNName() {
		return ifValidateCNName;
	}

	public void setIfValidateCNName(boolean ifValidateCNName) {
		this.ifValidateCNName = ifValidateCNName;
	}

	public boolean isIfValidateRemoteCert() {
		return ifValidateRemoteCert;
	}

	public void setIfValidateRemoteCert(boolean ifValidateRemoteCert) {
		this.ifValidateRemoteCert = ifValidateRemoteCert;
	}

	public String getSignMethod() {
		return signMethod;
	}

	public void setSignMethod(String signMethod) {
		this.signMethod = signMethod;
	}
	public String getQrcBackTransUrl() {
		return qrcBackTransUrl;
	}

	public void setQrcBackTransUrl(String qrcBackTransUrl) {
		this.qrcBackTransUrl = qrcBackTransUrl;
	}

	public String getQrcB2cIssBackTransUrl() {
		return qrcB2cIssBackTransUrl;
	}

	public void setQrcB2cIssBackTransUrl(String qrcB2cIssBackTransUrl) {
		this.qrcB2cIssBackTransUrl = qrcB2cIssBackTransUrl;
	}

	public String getQrcB2cMerBackTransUrl() {
		return qrcB2cMerBackTransUrl;
	}

	public void setQrcB2cMerBackTransUrl(String qrcB2cMerBackTransUrl) {
		this.qrcB2cMerBackTransUrl = qrcB2cMerBackTransUrl;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getFrontUrl() {
		return frontUrl;
	}

	public void setFrontUrl(String frontUrl) {
		this.frontUrl = frontUrl;
	}

	public String getBackUrl() {
		return backUrl;
	}

	public void setBackUrl(String backUrl) {
		this.backUrl = backUrl;
	}

	public String getRootCertPath() {
		return rootCertPath;
	}

	public void setRootCertPath(String rootCertPath) {
		this.rootCertPath = rootCertPath;
	}

	public String getMerId() {
		return merId;
	}

	public void setMerId(String merId) {
		this.merId = merId;
	}
}
