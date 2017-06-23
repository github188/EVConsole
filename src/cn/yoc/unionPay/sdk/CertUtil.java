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
 *   xshu       2014-05-28       ֤�鹤����.
 * =============================================================================
 */
package cn.yoc.unionPay.sdk;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.*;
import java.security.spec.RSAPublicKeySpec;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static cn.yoc.unionPay.sdk.SDKConstants.UNIONPAY_CNNAME;
import static cn.yoc.unionPay.sdk.SDKUtil.isEmpty;

/**
 * @ClassName: CertUtil
 * @Description: acpsdk֤�鹤���࣬��Ҫ���ڶ�֤��ļ��غ�ʹ��
 * @date 2016-7-22 ����2:46:20
 *
 */
public class CertUtil {
	private static String keypath = null;
	/** ֤���������洢���̻�������ǩ��˽Կ֤��. */
	private static KeyStore keyStore = null;
	/** ������Ϣ���ܹ�Կ֤�� */
	private static X509Certificate encryptCert = null;
	/** �ŵ����ܹ�Կ */
	private static PublicKey encryptTrackKey = null;
	/** ��֤�������ر���ǩ��֤��. */
	private static X509Certificate validateCert = null;
	/** ��ǩ�м�֤�� */
	private static X509Certificate middleCert = null;
	/** ��ǩ��֤�� */
	private static X509Certificate rootCert = null;
	/** ��֤�������ر���ǩ���Ĺ�Կ֤��洢Map. */
	private static Map<String, X509Certificate> certMap = new HashMap<String, X509Certificate>();
	/** �̻�˽Կ�洢Map */
	private final static Map<String, KeyStore> keyStoreMap = new ConcurrentHashMap<String, KeyStore>();
	
	static {
		init();
	}
	
	/**
	 * ��ʼ������֤��.
	 */
	private static void init() {
		try {
			addProvider();//��ϵͳ���BC provider
			initSignCert();//��ʼ��ǩ��˽Կ֤��
			initMiddleCert();//��ʼ����ǩ֤����м�֤��
			initRootCert();//��ʼ����ǩ֤��ĸ�֤��
			initEncryptCert();//��ʼ�����ܹ�Կ
			initTrackKey();//�����ŵ����ܹ�Կ
			initValidateCertFromDir();//��ʼ�����е���ǩ֤��
		} catch (Exception e) {
			LogUtil.writeErrorLog("initʧ�ܡ���������öԳ���Կǩ���Ŀ����Ӵ��쳣����", e);
		}
	}
	
	/**
	 * ���ǩ������ǩ�������㷨�ṩ��
	 */
	private static void addProvider(){
		if (Security.getProvider("BC") == null) {
			LogUtil.writeLog("add BC provider");
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		} else {
			Security.removeProvider("BC"); //���eclipse����ʱtomcat�Զ����¼���ʱ��BC���ڲ���ԭ���쳣�����⡣
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			LogUtil.writeLog("re-add BC provider");
		}
		printSysInfo();
	}
	
	/**
	 * �������ļ�acp_sdk.properties�����õ�˽Կ·�������� ����ǩ��֤��
	 */
	private static void initSignCert() {
		if(!"01".equals(SDKConfig.getConfig().getSignMethod())){
			LogUtil.writeLog("��rsaǩ����ʽ��������ǩ��֤�顣");
			return;
		}
		if (SDKConfig.getConfig().getSignCertPath() == null 
				|| SDKConfig.getConfig().getSignCertPwd() == null
				|| SDKConfig.getConfig().getSignCertType() == null) {
			LogUtil.writeErrorLog("WARN: " + SDKConfig.SDK_SIGNCERT_PATH + "��" + SDKConfig.SDK_SIGNCERT_PWD 
					+ "��" + SDKConfig.SDK_SIGNCERT_TYPE + "Ϊ�ա� ֹͣ����ǩ��֤�顣");
			return;
		}
		if (null != keyStore) {
			keyStore = null;
		}
		try {
			keyStore = getKeyInfo(SDKConfig.getConfig().getSignCertPath(),
					SDKConfig.getConfig().getSignCertPwd(), SDKConfig
							.getConfig().getSignCertType());
			LogUtil.writeLog("InitSignCert Successful. CertId=["
					+ getSignCertId() + "]");
		} catch (IOException e) {
			LogUtil.writeErrorLog("InitSignCert Error", e);
		}
	}

	/**
	 * �������ļ�acp_sdk.properties����·�� ����������Ϣ����֤��
	 */
	private static void initMiddleCert() {
		LogUtil.writeLog("�����м�֤��==>"+SDKConfig.getConfig().getMiddleCertPath());
		if (!isEmpty(SDKConfig.getConfig().getMiddleCertPath())) {
			middleCert = initCert(SDKConfig.getConfig().getMiddleCertPath());
			LogUtil.writeLog("Load MiddleCert Successful");
		} else {
			LogUtil.writeLog("WARN: acpsdk.middle.path is empty");
		}
	}

	/**
	 * �������ļ�acp_sdk.properties����·�� ����������Ϣ����֤��
	 */
	private static void initRootCert() {
		LogUtil.writeLog("���ظ�֤��==>"+SDKConfig.getConfig().getRootCertPath());
		if (!isEmpty(SDKConfig.getConfig().getRootCertPath())) {
			rootCert = initCert(SDKConfig.getConfig().getRootCertPath());
			LogUtil.writeLog("Load RootCert Successful");
		} else {
			LogUtil.writeLog("WARN: acpsdk.rootCert.path is empty");
		}
	}
	
	/**
	 * �������ļ�acp_sdk.properties����·�� ����������Կ�ϼ�֤�飨�м�֤�飩
	 */
	private static void initEncryptCert() {
		LogUtil.writeLog("����������Ϣ����֤��==>"+SDKConfig.getConfig().getEncryptCertPath());
		if (!isEmpty(SDKConfig.getConfig().getEncryptCertPath())) {
			encryptCert = initCert(SDKConfig.getConfig().getEncryptCertPath());
			LogUtil.writeLog("Load EncryptCert Successful");
		} else {
			LogUtil.writeLog("WARN: acpsdk.encryptCert.path is empty");
		}
	}
	
	/**
	 * �������ļ�acp_sdk.properties����·�� ���شŵ���Կ
	 */
	private static void initTrackKey() {
		if (!isEmpty(SDKConfig.getConfig().getEncryptTrackKeyModulus())
				&& !isEmpty(SDKConfig.getConfig().getEncryptTrackKeyExponent())) {
			encryptTrackKey = getPublicKey(SDKConfig.getConfig().getEncryptTrackKeyModulus(), 
					SDKConfig.getConfig().getEncryptTrackKeyExponent());
			LogUtil.writeLog("LoadEncryptTrackKey Successful");
		} else {
			LogUtil.writeLog("WARN: acpsdk.encryptTrackKey.modulus or acpsdk.encryptTrackKey.exponent is empty");
		}
	}

	/**
	 * �������ļ�acp_sdk.properties����·�� ������֤ǩ��֤��
	 */
	private static void initValidateCertFromDir() {
		if(!"01".equals(SDKConfig.getConfig().getSignMethod())){
			LogUtil.writeLog("��rsaǩ����ʽ����������ǩ֤�顣");
			return;
		}
		certMap.clear();
		String dir = SDKConfig.getConfig().getValidateCertDir();
		LogUtil.writeLog("������֤ǩ��֤��Ŀ¼==>" + dir +" ע�������������version=5.1.0��ô����ǩ֤��Ŀ¼ʹ�ò��������Բ���Ҫ���ã�version=5.0.0�������ã���");
		if (isEmpty(dir)) {
			LogUtil.writeErrorLog("WARN: acpsdk.validateCert.dir is empty");
			return;
		}
		CertificateFactory cf = null;
		FileInputStream in = null;
		try {
			cf = CertificateFactory.getInstance("X.509", "BC");
			File fileDir = new File(dir);
			File[] files = fileDir.listFiles(new CerFilter());
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				in = new FileInputStream(file.getAbsolutePath());
				validateCert = (X509Certificate) cf.generateCertificate(in);
				certMap.put(validateCert.getSerialNumber().toString(),
						validateCert);
				// ��ӡ֤�������Ϣ,�����Խ׶ε���
				LogUtil.writeLog("[" + file.getAbsolutePath() + "][CertId="
						+ validateCert.getSerialNumber().toString() + "]");
			}
			LogUtil.writeLog("LoadVerifyCert Successful");
		} catch (CertificateException e) {
			LogUtil.writeErrorLog("LoadVerifyCert Error", e);
		} catch (FileNotFoundException e) {
			LogUtil.writeErrorLog("LoadVerifyCert Error File Not Found", e);
		} catch (NoSuchProviderException e) {
			LogUtil.writeErrorLog("LoadVerifyCert Error No BC Provider", e);
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					LogUtil.writeErrorLog(e.toString());
				}
			}
		}
	}

	/**
	 * �ø�����·�������� ����ǩ��֤�飬�����浽certKeyStoreMap
	 * 
	 * @param certFilePath
	 * @param certPwd
	 */
	private static void loadSignCert(String certFilePath, String certPwd) {
		KeyStore keyStore = null;
		try {
			keyStore = getKeyInfo(certFilePath, certPwd, "PKCS12");
			keyStoreMap.put(certFilePath, keyStore);
			LogUtil.writeLog("LoadRsaCert Successful");
		} catch (IOException e) {
			LogUtil.writeErrorLog("LoadRsaCert Error", e);
		}
	}

	/**
	 * ͨ��֤��·����ʼ��Ϊ��Կ֤��
	 * @param path
	 * @return
	 */
	private static X509Certificate initCert(String path) {
		X509Certificate encryptCertTemp = null;
		CertificateFactory cf = null;
		FileInputStream in = null;
		try {
			cf = CertificateFactory.getInstance("X.509", "BC");
			in = new FileInputStream(path);
			encryptCertTemp = (X509Certificate) cf.generateCertificate(in);
			// ��ӡ֤�������Ϣ,�����Խ׶ε���
			LogUtil.writeLog("[" + path + "][CertId="
					+ encryptCertTemp.getSerialNumber().toString() + "]");
		} catch (CertificateException e) {
			LogUtil.writeErrorLog("InitCert Error", e);
		} catch (FileNotFoundException e) {
			LogUtil.writeErrorLog("InitCert Error File Not Found", e);
		} catch (NoSuchProviderException e) {
			LogUtil.writeErrorLog("LoadVerifyCert Error No BC Provider", e);
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					LogUtil.writeErrorLog(e.toString());
				}
			}
		}
		return encryptCertTemp;
	}
	
	/**
	 * ͨ��keyStore ��ȡ˽Կǩ��֤��PrivateKey����
	 * 
	 * @return
	 */
	public static PrivateKey getSignCertPrivateKey() {
		try {
			Enumeration<String> aliasenum = keyStore.aliases();
			String keyAlias = null;
			if (aliasenum.hasMoreElements()) {
				keyAlias = aliasenum.nextElement();
			}
			PrivateKey privateKey = (PrivateKey) keyStore.getKey(keyAlias,
					SDKConfig.getConfig().getSignCertPwd().toCharArray());
			return privateKey;
		} catch (KeyStoreException e) {
			LogUtil.writeErrorLog("getSignCertPrivateKey Error", e);
			return null;
		} catch (UnrecoverableKeyException e) {
			LogUtil.writeErrorLog("getSignCertPrivateKey Error", e);
			return null;
		} catch (NoSuchAlgorithmException e) {
			LogUtil.writeErrorLog("getSignCertPrivateKey Error", e);
			return null;
		}
	}
	/**
	 * ͨ��ָ��·����˽Կ֤��  ��ȡPrivateKey����
	 * 
	 * @return
	 */
	public static PrivateKey getSignCertPrivateKeyByStoreMap(String certPath,
			String certPwd) {
		if (!keyStoreMap.containsKey(certPath)) {
			loadSignCert(certPath, certPwd);
		}
		try {
			Enumeration<String> aliasenum = keyStoreMap.get(certPath)
					.aliases();
			String keyAlias = null;
			if (aliasenum.hasMoreElements()) {
				keyAlias = aliasenum.nextElement();
			}
			PrivateKey privateKey = (PrivateKey) keyStoreMap.get(certPath)
					.getKey(keyAlias, certPwd.toCharArray());
			return privateKey;
		} catch (KeyStoreException e) {
			LogUtil.writeErrorLog("getSignCertPrivateKeyByStoreMap Error", e);
			return null;
		} catch (UnrecoverableKeyException e) {
			LogUtil.writeErrorLog("getSignCertPrivateKeyByStoreMap Error", e);
			return null;
		} catch (NoSuchAlgorithmException e) {
			LogUtil.writeErrorLog("getSignCertPrivateKeyByStoreMap Error", e);
			return null;
		}
	}

	/**
	 * ��ȡ������Ϣ����֤��PublicKey
	 * 
	 * @return
	 */
	public static PublicKey getEncryptCertPublicKey() {
		if (null == encryptCert) {
			String path = SDKConfig.getConfig().getEncryptCertPath();
			if (!isEmpty(path)) {
				encryptCert = initCert(path);
				return encryptCert.getPublicKey();
			} else {
				LogUtil.writeErrorLog("acpsdk.encryptCert.path is empty");
				return null;
			}
		} else {
			return encryptCert.getPublicKey();
		}
	}
	
	/**
	 * ����������Ϣ����֤�鹫Կ
	 */
	public static void resetEncryptCertPublicKey() {
		encryptCert = null;
	}
	
	/**
	 * ��ȡ�ŵ�����֤��PublicKey
	 * 
	 * @return
	 */
	public static PublicKey getEncryptTrackPublicKey() {
		if (null == encryptTrackKey) {
			initTrackKey();
		}
		return encryptTrackKey;
	}
	
	/**
	 * ͨ��certId��ȡ��ǩ֤��Map�ж�Ӧ֤��PublicKey
	 * 
	 * @param certId ֤���������
	 * @return ͨ��֤���Ż�ȡ���Ĺ�Կ
	 */
	public static PublicKey getValidatePublicKey(String certId) {
		X509Certificate cf = null;
		if (certMap.containsKey(certId)) {
			// ����certId��Ӧ��֤�����
			cf = certMap.get(certId);
			return cf.getPublicKey();
		} else {
			// ������������Load֤���ļ�Ŀ¼
			initValidateCertFromDir();
			if (certMap.containsKey(certId)) {
				// ����certId��Ӧ��֤�����
				cf = certMap.get(certId);
				return cf.getPublicKey();
			} else {
				LogUtil.writeErrorLog("ȱ��certId=[" + certId + "]��Ӧ����ǩ֤��.");
				return null;
			}
		}
	}
	
	/**
	 * ��ȡ�����ļ�acp_sdk.properties�����õ�ǩ��˽Կ֤��certId
	 * 
	 * @return ֤���������
	 */
	public static String getSignCertId() {
		try {
			Enumeration<String> aliasenum = keyStore.aliases();
			String keyAlias = null;
			if (aliasenum.hasMoreElements()) {
				keyAlias = aliasenum.nextElement();
			}
			X509Certificate cert = (X509Certificate) keyStore
					.getCertificate(keyAlias);
			return cert.getSerialNumber().toString();
		} catch (Exception e) {
			LogUtil.writeErrorLog("getSignCertId Error", e);
			return null;
		}
	}

	/**
	 * ��ȡ������Ϣ����֤���certId
	 * 
	 * @return
	 */
	public static String getEncryptCertId() {
		if (null == encryptCert) {
			String path = SDKConfig.getConfig().getEncryptCertPath();
			if (!isEmpty(path)) {
				encryptCert = initCert(path);
				return encryptCert.getSerialNumber().toString();
			} else {
				LogUtil.writeErrorLog("acpsdk.encryptCert.path is empty");
				return null;
			}
		} else {
			return encryptCert.getSerialNumber().toString();
		}
	}

	/**
	 * ��ǩ��˽Կ֤���ļ���ȡΪ֤��洢����
	 * 
	 * @param pfxkeyfile
	 *            ֤���ļ���
	 * @param keypwd
	 *            ֤������
	 * @param type
	 *            ֤������
	 * @return ֤�����
	 * @throws IOException
	 */
	private static KeyStore getKeyInfo(String pfxkeyfile, String keypwd,
			String type) throws IOException {
		LogUtil.writeLog("����ǩ��֤��==>" + pfxkeyfile);
		FileInputStream fis = null;
		try {
			KeyStore ks = KeyStore.getInstance(type, "BC");
			LogUtil.writeLog("Load RSA CertPath=[" + pfxkeyfile + "],Pwd=["+ keypwd + "],type=["+type+"]");
			fis = new FileInputStream(pfxkeyfile);
			char[] nPassword = null;
			nPassword = null == keypwd || "".equals(keypwd.trim()) ? null: keypwd.toCharArray();
			if (null != ks) {
				ks.load(fis, nPassword);
			}
			return ks;
		} catch (Exception e) {
			LogUtil.writeErrorLog("getKeyInfo Error", e);
			return null;
		} finally {
			if(null!=fis)
				fis.close();
		}
	}
	
	/**
	 * ͨ��ǩ��˽Կ֤��·���������ȡ˽Կ֤��certId
	 * @param certPath
	 * @param certPwd
	 * @return
	 */
	public static String getCertIdByKeyStoreMap(String certPath, String certPwd) {
		if (!keyStoreMap.containsKey(certPath)) {
			// ������δ��ѯ��,�����RSA֤��
			loadSignCert(certPath, certPwd);
		}
		return getCertIdIdByStore(keyStoreMap.get(certPath));
	}
	
	/**
	 * ͨ��keystore��ȡ˽Կ֤���certIdֵ
	 * @param keyStore
	 * @return
	 */
	private static String getCertIdIdByStore(KeyStore keyStore) {
		Enumeration<String> aliasenum = null;
		try {
			aliasenum = keyStore.aliases();
			String keyAlias = null;
			if (aliasenum.hasMoreElements()) {
				keyAlias = aliasenum.nextElement();
			}
			X509Certificate cert = (X509Certificate) keyStore
					.getCertificate(keyAlias);
			return cert.getSerialNumber().toString();
		} catch (KeyStoreException e) {
			LogUtil.writeErrorLog("getCertIdIdByStore Error", e);
			return null;
		}
	}
	
	/**
	 * ʹ��ģ��ָ������RSA��Կ ע�⣺�˴�������Ĭ�ϲ�λ��ʽ��ΪRSA/None/PKCS1Padding����ͬJDKĬ�ϵĲ�λ��ʽ���ܲ�ͬ
	 * 
	 * @param modulus
	 *            ģ
	 * @param exponent
	 *            ָ��
	 * @return
	 */
	private static PublicKey getPublicKey(String modulus, String exponent) {
		try {
			BigInteger b1 = new BigInteger(modulus);
			BigInteger b2 = new BigInteger(exponent);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
			RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2);
			return keyFactory.generatePublic(keySpec);
		} catch (Exception e) {
			LogUtil.writeErrorLog("����RSA��Կʧ�ܣ�" + e);
			return null;
		}
	}
	
	/**
	 * ���ַ���ת��ΪX509Certificate����.
	 * 
	 * @param x509CertString
	 * @return
	 */
	public static X509Certificate genCertificateByStr(String x509CertString) {
		X509Certificate x509Cert = null;
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509", "BC");
			InputStream tIn = new ByteArrayInputStream(
					x509CertString.getBytes("ISO-8859-1"));
			x509Cert = (X509Certificate) cf.generateCertificate(tIn);
		} catch (Exception e) {
			LogUtil.writeErrorLog("gen certificate error", e);			
		}
		return x509Cert;
	}
	
	/**
	 * �������ļ�acp_sdk.properties�л�ȡ��ǩ��Կʹ�õ��м�֤��
	 * @return
	 */
	public static X509Certificate getMiddleCert() {
		if (null == middleCert) {
			String path = SDKConfig.getConfig().getMiddleCertPath();
			if (!isEmpty(path)) {
				initMiddleCert();
			} else {
				LogUtil.writeErrorLog(SDKConfig.SDK_MIDDLECERT_PATH + " not set in " + SDKConfig.FILE_NAME);
				return null;
			}
		}
		return middleCert;
	}
	
	/**
	 * �������ļ�acp_sdk.properties�л�ȡ��ǩ��Կʹ�õĸ�֤��
	 * @return
	 */
	public static X509Certificate getRootCert() {
		if (null == rootCert) {
			String path = SDKConfig.getConfig().getRootCertPath();
			if (!isEmpty(path)) {
				initRootCert();
			} else {
				LogUtil.writeErrorLog(SDKConfig.SDK_ROOTCERT_PATH + " not set in " + SDKConfig.FILE_NAME);
				return null;
			}
		}
		return rootCert;
	}

	/**
	 * ��ȡ֤���CN
	 * @param aCert
	 * @return
	 */
	private static String getIdentitiesFromCertficate(X509Certificate aCert) {
		String tDN = aCert.getSubjectDN().toString();
		String tPart = "";
		if ((tDN != null)) {
			String tSplitStr[] = tDN.substring(tDN.indexOf("CN=")).split("@");
			if (tSplitStr != null && tSplitStr.length > 2
					&& tSplitStr[2] != null)
				tPart = tSplitStr[2];
		}
		return tPart;
	}
	
	/**
	 * ��֤������
	 * @param cert
	 * @return
	 */
	private static boolean verifyCertificateChain(X509Certificate cert){
		
		if ( null == cert) {
			LogUtil.writeErrorLog("cert must Not null");
			return false;
		}
		
		X509Certificate middleCert = CertUtil.getMiddleCert();
		if (null == middleCert) {
			LogUtil.writeErrorLog("middleCert must Not null");
			return false;
		}
		
		X509Certificate rootCert = CertUtil.getRootCert();
		if (null == rootCert) {
			LogUtil.writeErrorLog("rootCert or cert must Not null");
			return false;
		}
		
		try {
		
	        X509CertSelector selector = new X509CertSelector();
	        selector.setCertificate(cert);
	        
	        Set<TrustAnchor> trustAnchors = new HashSet<TrustAnchor>();
	        trustAnchors.add(new TrustAnchor(rootCert, null));
	        PKIXBuilderParameters pkixParams = new PKIXBuilderParameters(
			        trustAnchors, selector);
	
	        Set<X509Certificate> intermediateCerts = new HashSet<X509Certificate>();
	        intermediateCerts.add(rootCert);
	        intermediateCerts.add(middleCert);
	        intermediateCerts.add(cert);
	        
	        pkixParams.setRevocationEnabled(false);
	
	        CertStore intermediateCertStore = CertStore.getInstance("Collection",
					new CollectionCertStoreParameters(intermediateCerts), "BC");
	        pkixParams.addCertStore(intermediateCertStore);
	
	        CertPathBuilder builder = CertPathBuilder.getInstance("PKIX", "BC");
	        
        	@SuppressWarnings("unused")
			PKIXCertPathBuilderResult result = (PKIXCertPathBuilderResult) builder
                .build(pkixParams);
			LogUtil.writeLog("verify certificate chain succeed.");
			return true;
        } catch (java.security.cert.CertPathBuilderException e){
			LogUtil.writeErrorLog("verify certificate chain fail.", e);
		} catch (Exception e) {
			LogUtil.writeErrorLog("verify certificate chain exception: ", e);
		}
		return false;
	}
	
	/**
	 * ���֤����
	 * 
	 * @param rootCerts
	 *            ��֤��
	 * @param cert
	 *            ����֤��֤��
	 * @return
	 */
	public static boolean verifyCertificate(X509Certificate cert) {
		
		if ( null == cert) {
			LogUtil.writeErrorLog("cert must Not null");
			return false;
		}
		try {
			cert.checkValidity();//��֤��Ч��
//			cert.verify(middleCert.getPublicKey());
			if(!verifyCertificateChain(cert)){
				return false;
			}
		} catch (Exception e) {
			LogUtil.writeErrorLog("verifyCertificate fail", e);
			return false;
		}
		
		if(SDKConfig.getConfig().isIfValidateCNName()){
			// ��֤��Կ�Ƿ���������
			if(!UNIONPAY_CNNAME.equals(CertUtil.getIdentitiesFromCertficate(cert))) {
				LogUtil.writeErrorLog("cer owner is not CUP:" + CertUtil.getIdentitiesFromCertficate(cert));
				return false;
			}
		} else {
			// ��֤��Կ�Ƿ���������
			if(!UNIONPAY_CNNAME.equals(CertUtil.getIdentitiesFromCertficate(cert)) 
					&& !"00040000:SIGN".equals(CertUtil.getIdentitiesFromCertficate(cert))) {
				LogUtil.writeErrorLog("cer owner is not CUP:" + CertUtil.getIdentitiesFromCertficate(cert));
				return false;
			}
		}
		return true;		
	}

	/**
	 * ��ӡϵͳ������Ϣ
	 */
	private static void printSysInfo() {
		LogUtil.writeLog("================= SYS INFO begin====================");
		LogUtil.writeLog("os_name:" + System.getProperty("os.name"));
		LogUtil.writeLog("os_arch:" + System.getProperty("os.arch"));
		LogUtil.writeLog("os_version:" + System.getProperty("os.version"));
		LogUtil.writeLog("java_vm_specification_version:"
				+ System.getProperty("java.vm.specification.version"));
		LogUtil.writeLog("java_vm_specification_vendor:"
				+ System.getProperty("java.vm.specification.vendor"));
		LogUtil.writeLog("java_vm_specification_name:"
				+ System.getProperty("java.vm.specification.name"));
		LogUtil.writeLog("java_vm_version:"
				+ System.getProperty("java.vm.version"));
		LogUtil.writeLog("java_vm_name:" + System.getProperty("java.vm.name"));
		LogUtil.writeLog("java.version:" + System.getProperty("java.version"));
		LogUtil.writeLog("java.vm.vendor=[" + System.getProperty("java.vm.vendor") + "]");
		LogUtil.writeLog("java.version=[" + System.getProperty("java.version") + "]");
		printProviders();
		LogUtil.writeLog("================= SYS INFO end=====================");
	}
	
	/**
	 * ��jre��ӡ�㷨�ṩ���б�
	 */
	private static void printProviders() {
		LogUtil.writeLog("Providers List:");
		Provider[] providers = Security.getProviders();
		for (int i = 0; i < providers.length; i++) {
			LogUtil.writeLog(i + 1 + "." + providers[i].getName());
		}
	}

	/**
	 * ֤���ļ�������
	 * 
	 */
	static class CerFilter implements FilenameFilter {
		public boolean isCer(String name) {
			if (name.toLowerCase().endsWith(".cer")) {
				return true;
			} else {
				return false;
			}
		}
		public boolean accept(File dir, String name) {
			return isCer(name);
		}
	}

	public static String getKeypath() {
		return keypath;
	}

	public static void setKeypath(String keypath) {
		CertUtil.keypath = keypath;
	}
}
