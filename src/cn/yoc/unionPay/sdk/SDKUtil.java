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
 *   xshu       2014-05-28      MPI������
 * =============================================================================
 */
package cn.yoc.unionPay.sdk;



import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.Map.Entry;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import static cn.yoc.unionPay.sdk.SDKConstants.*;

/**
 * 
 * @ClassName SDKUtil
 * @Description acpsdk������
 * @date 2016-7-22 ����4:06:18
 *
 */
public class SDKUtil {

	/**
	 * ����signMethod��ֵ���ṩ���ּ���ǩ���ķ���
	 * 
	 * @param data
	 *            ��ǩ������Map��ֵ����ʽ
	 * @param encoding
	 *            ����
	 * @return ǩ���Ƿ�ɹ�
	 */
	public static boolean sign(Map<String, String> data, String encoding) {
		
		if (isEmpty(encoding)) {
			encoding = "UTF-8";
		}
		String signMethod = data.get(param_signMethod);
		String version = data.get(SDKConstants.param_version);
		if (!VERSION_1_0_0.equals(version) && !VERSION_5_0_1.equals(version) && isEmpty(signMethod)) {
			LogUtil.writeErrorLog("signMethod must Not null");
			return false;
		}
		
		if (isEmpty(version)) {
			LogUtil.writeErrorLog("version must Not null");
			return false;
		}
		if (SIGNMETHOD_RSA.equals(signMethod)|| VERSION_1_0_0.equals(version) || VERSION_5_0_1.equals(version)) {
			if (VERSION_5_0_0.equals(version)|| VERSION_1_0_0.equals(version) || VERSION_5_0_1.equals(version)) {
				// ����ǩ��֤�����к�
				data.put(SDKConstants.param_certId, CertUtil.getSignCertId());
				// ��Map��Ϣת����key1=value1&key2=value2����ʽ
				String stringData = coverMap2String(data);
				LogUtil.writeLog("��ǩ�������Ĵ�:[" + stringData + "]");
				byte[] byteSign = null;
				String stringSign = null;
				try {
					// ͨ��SHA1����ժҪ��ת16����
					byte[] signDigest = SecureUtil
							.sha1X16(stringData, encoding);
					byteSign = SecureUtil.base64Encode(SecureUtil.signBySoft(
							CertUtil.getSignCertPrivateKey(), signDigest));
					stringSign = new String(byteSign);
					// ����ǩ����ֵ
					data.put(SDKConstants.param_signature, stringSign);
					return true;
				} catch (Exception e) {
					LogUtil.writeErrorLog("Sign Error", e);
					return false;
				}
			} else if (VERSION_5_1_0.equals(version)) {
				// ����ǩ��֤�����к�
				data.put(SDKConstants.param_certId, CertUtil.getSignCertId());
				// ��Map��Ϣת����key1=value1&key2=value2����ʽ
				String stringData = coverMap2String(data);
				LogUtil.writeLog("��ǩ�������Ĵ�:[" + stringData + "]");
				byte[] byteSign = null;
				String stringSign = null;
				try {
					// ͨ��SHA256����ժҪ��ת16����
					byte[] signDigest = SecureUtil
							.sha256X16(stringData, encoding);
					byteSign = SecureUtil.base64Encode(SecureUtil.signBySoft256(
							CertUtil.getSignCertPrivateKey(), signDigest));
					stringSign = new String(byteSign);
					// ����ǩ����ֵ
					data.put(SDKConstants.param_signature, stringSign);
					return true;
				} catch (Exception e) {
					LogUtil.writeErrorLog("Sign Error", e);
					return false;
				}
			}
		} else if (SIGNMETHOD_SHA256.equals(signMethod)) {
			return signBySecureKey(data, SDKConfig.getConfig()
					.getSecureKey(), encoding);
		} else if (SIGNMETHOD_SM3.equals(signMethod)) {
			return signBySecureKey(data, SDKConfig.getConfig()
					.getSecureKey(), encoding);
		}
		return false;
	}

	/**
	 * ͨ�������֤�����·����֤�������ȡǩ��֤�����ǩ��������ǩ��ֵ<br>
	 * 
	 * @param data
	 *            ��ǩ������Map��ֵ����ʽ
	 * @param encoding
	 *            ����
	 * @param certPath
	 *            ֤�����·��
	 * @param certPwd
	 *            ֤������
	 * @return ǩ��ֵ
	 */
	public static boolean signBySecureKey(Map<String, String> data, String secureKey,
			String encoding) {
		
		if (isEmpty(encoding)) {
			encoding = "UTF-8";
		}
		if (isEmpty(secureKey)) {
			LogUtil.writeErrorLog("secureKey is empty");
			return false;
		}
		String signMethod = data.get(param_signMethod);
		if (isEmpty(signMethod)) {
			LogUtil.writeErrorLog("signMethod must Not null");
			return false;
		}
		
		if (SIGNMETHOD_SHA256.equals(signMethod)) {
			// ��Map��Ϣת����key1=value1&key2=value2����ʽ
			String stringData = coverMap2String(data);
			LogUtil.writeLog("��ǩ�������Ĵ�:[" + stringData + "]");
			String strBeforeSha256 = stringData
					+ SDKConstants.AMPERSAND
					+ SecureUtil.sha256X16Str(secureKey, encoding);
			String strAfterSha256 = SecureUtil.sha256X16Str(strBeforeSha256,
					encoding);
			// ����ǩ����ֵ
			data.put(SDKConstants.param_signature, strAfterSha256);
			return true;
		} else if (SIGNMETHOD_SM3.equals(signMethod)) {
			String stringData = coverMap2String(data);
			LogUtil.writeLog("��ǩ�������Ĵ�:[" + stringData + "]");
			String strBeforeSM3 = stringData
					+ SDKConstants.AMPERSAND
					+ SecureUtil.sm3X16Str(secureKey, encoding);
			String strAfterSM3 = SecureUtil.sm3X16Str(strBeforeSM3, encoding);
			// ����ǩ����ֵ
			data.put(SDKConstants.param_signature, strAfterSM3);
			return true;
		}
		return false;
	}

	/**
	 * ͨ�������ǩ����Կ����ǩ��������ǩ��ֵ<br>
	 * 
	 * @param data
	 *            ��ǩ������Map��ֵ����ʽ
	 * @param encoding
	 *            ����
	 * @param certPath
	 *            ֤�����·��
	 * @param certPwd
	 *            ֤������
	 * @return ǩ��ֵ
	 */
	public static boolean signByCertInfo(Map<String, String> data,
			String certPath, String certPwd, String encoding) {
		
		if (isEmpty(encoding)) {
			encoding = "UTF-8";
		}
		if (isEmpty(certPath) || isEmpty(certPwd)) {
			LogUtil.writeErrorLog("CertPath or CertPwd is empty");
			return false;
		}
		String signMethod = data.get(param_signMethod);
		String version = data.get(SDKConstants.param_version);
		if (!VERSION_1_0_0.equals(version) && !VERSION_5_0_1.equals(version) && isEmpty(signMethod)) {
			LogUtil.writeErrorLog("signMethod must Not null");
			return false;
		}
		if (isEmpty(version)) {
			LogUtil.writeErrorLog("version must Not null");
			return false;
		}
		
		if (SIGNMETHOD_RSA.equals(signMethod) || VERSION_1_0_0.equals(version) || VERSION_5_0_1.equals(version)) {
			if (VERSION_5_0_0.equals(version) || VERSION_1_0_0.equals(version) || VERSION_5_0_1.equals(version)) {
				// ����ǩ��֤�����к�
				data.put(SDKConstants.param_certId, CertUtil.getCertIdByKeyStoreMap(certPath, certPwd));
				// ��Map��Ϣת����key1=value1&key2=value2����ʽ
				String stringData = coverMap2String(data);
				LogUtil.writeLog("��ǩ�������Ĵ�:[" + stringData + "]");
				byte[] byteSign = null;
				String stringSign = null;
				try {
					// ͨ��SHA1����ժҪ��ת16����
					byte[] signDigest = SecureUtil
							.sha1X16(stringData, encoding);
					byteSign = SecureUtil.base64Encode(SecureUtil.signBySoft(
							CertUtil.getSignCertPrivateKeyByStoreMap(certPath, certPwd), signDigest));
					stringSign = new String(byteSign);
					// ����ǩ����ֵ
					data.put(SDKConstants.param_signature, stringSign);
					return true;
				} catch (Exception e) {
					LogUtil.writeErrorLog("Sign Error", e);
					return false;
				}
			} else if (VERSION_5_1_0.equals(version)) {
				// ����ǩ��֤�����к�
				data.put(SDKConstants.param_certId, CertUtil.getCertIdByKeyStoreMap(certPath, certPwd));
				// ��Map��Ϣת����key1=value1&key2=value2����ʽ
				String stringData = coverMap2String(data);
				LogUtil.writeLog("��ǩ�������Ĵ�:[" + stringData + "]");
				byte[] byteSign = null;
				String stringSign = null;
				try {
					// ͨ��SHA256����ժҪ��ת16����
					byte[] signDigest = SecureUtil
							.sha256X16(stringData, encoding);
					byteSign = SecureUtil.base64Encode(SecureUtil.signBySoft256(
							CertUtil.getSignCertPrivateKeyByStoreMap(certPath, certPwd), signDigest));
					stringSign = new String(byteSign);
					// ����ǩ����ֵ
					data.put(SDKConstants.param_signature, stringSign);
					return true;
				} catch (Exception e) {
					LogUtil.writeErrorLog("Sign Error", e);
					return false;
				}
			}
			
		} 
		return false;
	}
	
	/**
	 * ��֤ǩ��
	 * 
	 * @param resData
	 *            ���ر�������
	 * @param encoding
	 *            �����ʽ
	 * @return
	 */
	public static boolean validateBySecureKey(Map<String, String> resData, String secureKey, String encoding) {
		LogUtil.writeLog("��ǩ����ʼ");
		if (isEmpty(encoding)) {
			encoding = "UTF-8";
		}
		String signMethod = resData.get(SDKConstants.param_signMethod);
		if (SIGNMETHOD_SHA256.equals(signMethod)) {
			// 1.����SHA256��֤
			String stringSign = resData.get(SDKConstants.param_signature);
			LogUtil.writeLog("ǩ��ԭ�ģ�["+stringSign+"]");
			// ��Map��Ϣת����key1=value1&key2=value2����ʽ
			String stringData = coverMap2String(resData);
			LogUtil.writeLog("����ǩ���ر��Ĵ���["+stringData+"]");
			String strBeforeSha256 = stringData
					+ SDKConstants.AMPERSAND
					+ SecureUtil.sha256X16Str(secureKey, encoding);
			String strAfterSha256 = SecureUtil.sha256X16Str(strBeforeSha256,
					encoding);
			return stringSign.equals(strAfterSha256);
		} else if (SIGNMETHOD_SM3.equals(signMethod)) {
			// 1.����SM3��֤
			String stringSign = resData.get(SDKConstants.param_signature);
			LogUtil.writeLog("ǩ��ԭ�ģ�["+stringSign+"]");
			// ��Map��Ϣת����key1=value1&key2=value2����ʽ
			String stringData = coverMap2String(resData);
			LogUtil.writeLog("����ǩ���ر��Ĵ���["+stringData+"]");
			String strBeforeSM3 = stringData
					+ SDKConstants.AMPERSAND
					+ SecureUtil.sm3X16Str(secureKey, encoding);
			String strAfterSM3 = SecureUtil
					.sm3X16Str(strBeforeSM3, encoding);
			return stringSign.equals(strAfterSM3);
		}
		return false;
	}
	
	/**
	 * ��֤ǩ��
	 * 
	 * @param resData
	 *            ���ر�������
	 * @param encoding
	 *            �����ʽ
	 * @return
	 */
	public static boolean validate(Map<String, String> resData, String encoding) {
		LogUtil.writeLog("��ǩ����ʼ");
		if (isEmpty(encoding)) {
			encoding = "UTF-8";
		}
		String signMethod = resData.get(SDKConstants.param_signMethod);
		String version = resData.get(SDKConstants.param_version);
		if (SIGNMETHOD_RSA.equals(signMethod) || VERSION_1_0_0.equals(version) || VERSION_5_0_1.equals(version)) {
			// ��ȡ���ر��ĵİ汾��
			if (VERSION_5_0_0.equals(version) || VERSION_1_0_0.equals(version) || VERSION_5_0_1.equals(version)) {
				String stringSign = resData.get(SDKConstants.param_signature);
				LogUtil.writeLog("ǩ��ԭ�ģ�["+stringSign+"]");
				// �ӷ��ر����л�ȡcertId ��Ȼ��ȥ֤�龲̬Map�в�ѯ��Ӧ��ǩ֤�����
				String certId = resData.get(SDKConstants.param_certId);
				LogUtil.writeLog("�Է��ر��Ĵ���ǩʹ�õ���ǩ��Կ���кţ�["+certId+"]");
				// ��Map��Ϣת����key1=value1&key2=value2����ʽ
				String stringData = coverMap2String(resData);
				LogUtil.writeLog("����ǩ���ر��Ĵ���["+stringData+"]");
				try {
					// ��֤ǩ����Ҫ�����������̻��Ĺ�Կ֤��.
					return SecureUtil.validateSignBySoft(CertUtil
							.getValidatePublicKey(certId), SecureUtil
							.base64Decode(stringSign.getBytes(encoding)),
							SecureUtil.sha1X16(stringData, encoding));
				} catch (UnsupportedEncodingException e) {
					LogUtil.writeErrorLog(e.getMessage(), e);
				} catch (Exception e) {
					LogUtil.writeErrorLog(e.getMessage(), e);
				}
			} else if (VERSION_5_1_0.equals(version)) {
				// 1.�ӷ��ر����л�ȡ��Կ��Ϣת���ɹ�Կ����
				String strCert = resData.get(SDKConstants.param_signPubKeyCert);
//				LogUtil.writeLog("��ǩ��Կ֤�飺["+strCert+"]");
				X509Certificate x509Cert = CertUtil.genCertificateByStr(strCert);
				if(x509Cert == null) {
					LogUtil.writeErrorLog("convert signPubKeyCert failed");
					return false;
				}
				// 2.��֤֤����
				if (!CertUtil.verifyCertificate(x509Cert)) {
					LogUtil.writeErrorLog("��֤��Կ֤��ʧ�ܣ�֤����Ϣ��["+strCert+"]");
					return false;
				}
				
				// 3.��ǩ
				String stringSign = resData.get(SDKConstants.param_signature);
				LogUtil.writeLog("ǩ��ԭ�ģ�["+stringSign+"]");
				// ��Map��Ϣת����key1=value1&key2=value2����ʽ
				String stringData = coverMap2String(resData);
				LogUtil.writeLog("����ǩ���ر��Ĵ���["+stringData+"]");
				try {
					// ��֤ǩ����Ҫ�����������̻��Ĺ�Կ֤��.
					boolean result = SecureUtil.validateSignBySoft256(x509Cert
							.getPublicKey(), SecureUtil.base64Decode(stringSign
							.getBytes(encoding)), SecureUtil.sha256X16(
							stringData, encoding));
					LogUtil.writeLog("��֤ǩ��" + (result? "�ɹ�":"ʧ��"));
					return result;
				} catch (UnsupportedEncodingException e) {
					LogUtil.writeErrorLog(e.getMessage(), e);
				} catch (Exception e) {
					LogUtil.writeErrorLog(e.getMessage(), e);
				}
			}

		} else if (SIGNMETHOD_SHA256.equals(signMethod)) {
			// 1.����SHA256��֤
			String stringSign = resData.get(SDKConstants.param_signature);
			LogUtil.writeLog("ǩ��ԭ�ģ�["+stringSign+"]");
			// ��Map��Ϣת����key1=value1&key2=value2����ʽ
			String stringData = coverMap2String(resData);
			LogUtil.writeLog("����ǩ���ر��Ĵ���["+stringData+"]");
			String strBeforeSha256 = stringData
					+ SDKConstants.AMPERSAND
					+ SecureUtil.sha256X16Str(SDKConfig.getConfig()
							.getSecureKey(), encoding);
			String strAfterSha256 = SecureUtil.sha256X16Str(strBeforeSha256,
					encoding);
			boolean result =  stringSign.equals(strAfterSha256);
			LogUtil.writeLog("��֤ǩ��" + (result? "�ɹ�":"ʧ��"));
			return result;
		} else if (SIGNMETHOD_SM3.equals(signMethod)) {
			// 1.����SM3��֤
			String stringSign = resData.get(SDKConstants.param_signature);
			LogUtil.writeLog("ǩ��ԭ�ģ�["+stringSign+"]");
			// ��Map��Ϣת����key1=value1&key2=value2����ʽ
			String stringData = coverMap2String(resData);
			LogUtil.writeLog("����ǩ���ر��Ĵ���["+stringData+"]");
			String strBeforeSM3 = stringData
					+ SDKConstants.AMPERSAND
					+ SecureUtil.sm3X16Str(SDKConfig.getConfig()
							.getSecureKey(), encoding);
			String strAfterSM3 = SecureUtil
					.sm3X16Str(strBeforeSM3, encoding);
			boolean result =  stringSign.equals(strAfterSM3);
			LogUtil.writeLog("��֤ǩ��" + (result? "�ɹ�":"ʧ��"));
			return result;
		}
		return false;
	}

	/**
	 * ��Map�е�����ת����key1=value1&key2=value2����ʽ ������ǩ����signature
	 * 
	 * @param data
	 *            ��ƴ�ӵ�Map����
	 * @return ƴ�Ӻú���ַ���
	 */
	public static String coverMap2String(Map<String, String> data) {
		TreeMap<String, String> tree = new TreeMap<String, String>();
		Iterator<Entry<String, String>> it = data.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> en = it.next();
			if (SDKConstants.param_signature.equals(en.getKey().trim())) {
				continue;
			}
			tree.put(en.getKey(), en.getValue());
		}
		it = tree.entrySet().iterator();
		StringBuffer sf = new StringBuffer();
		while (it.hasNext()) {
			Entry<String, String> en = it.next();
			sf.append(en.getKey() + SDKConstants.EQUAL + en.getValue()
					+ SDKConstants.AMPERSAND);
		}
		return sf.substring(0, sf.length() - 1);
	}


	/**
	 * �����Ϸ��� ������key=value&key=value���ַ���ת��Ϊ��Ӧ��Map����
	 * 
	 * @param result
	 * @return
	 */
	public static Map<String, String> coverResultString2Map(String result) {
		return convertResultStringToMap(result);
	}

	/**
	 * ������key=value&key=value���ַ���ת��Ϊ��Ӧ��Map����
	 * 
	 * @param result
	 * @return
	 */
	public static Map<String, String> convertResultStringToMap(String result) {
		Map<String, String> map =null;
		try {

			if (StringUtils.isNotBlank(result)) {
				if (result.startsWith("{") && result.endsWith("}")) {
					result = result.substring(1, result.length() - 1);
				}
				map = parseQString(result);
			}

		} catch (UnsupportedEncodingException e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
		}
		return map;
	}

	
	/**
	 * ����Ӧ���ַ���������Ӧ��Ҫ��
	 * 
	 * @param str
	 *            ��Ҫ�������ַ���
	 * @return �����Ľ��map
	 * @throws UnsupportedEncodingException
	 */
	public static Map<String, String> parseQString(String str)
			throws UnsupportedEncodingException {

		Map<String, String> map = new HashMap<String, String>();
		int len = str.length();
		StringBuilder temp = new StringBuilder();
		char curChar;
		String key = null;
		boolean isKey = true;
		boolean isOpen = false;//ֵ����Ƕ��
		char openName = 0;
		if(len>0){
			for (int i = 0; i < len; i++) {// �����������������ַ���
				curChar = str.charAt(i);// ȡ��ǰ�ַ�
				if (isKey) {// �����ǰ���ɵ���key
					
					if (curChar == '=') {// �����ȡ��=�ָ��� 
						key = temp.toString();
						temp.setLength(0);
						isKey = false;
					} else {
						temp.append(curChar);
					}
				} else  {// �����ǰ���ɵ���value
					if(isOpen){
						if(curChar == openName){
							isOpen = false;
						}
						
					}else{//���û����Ƕ��
						if(curChar == '{'){//����������Ϳ���Ƕ��
							isOpen = true;
							openName ='}';
						}
						if(curChar == '['){
							isOpen = true;
							openName =']';
						}
					}
					if (curChar == '&' && !isOpen) {// �����ȡ��&�ָ��,ͬʱ����ָ������ֵ����ʱ��map�����
						putKeyValueToMap(temp, isKey, key, map);
						temp.setLength(0);
						isKey = true;
					}else{
						temp.append(curChar);
					}
				}
				
			}
			putKeyValueToMap(temp, isKey, key, map);
		}
		return map;
	}

	private static void putKeyValueToMap(StringBuilder temp, boolean isKey,
			String key, Map<String, String> map)
			throws UnsupportedEncodingException {
		if (isKey) {
			key = temp.toString();
			if (key.length() == 0) {
				throw new RuntimeException("QString format illegal");
			}
			map.put(key, "");
		} else {
			if (key.length() == 0) {
				throw new RuntimeException("QString format illegal");
			}
			map.put(key, temp.toString());
		}
	}

	/**
	 * 
	 * ��ȡӦ�����еļ��ܹ�Կ֤��,���洢������,������ԭʼ֤��<br>
	 * ���³ɹ��򷵻�1���޸��·���0��ʧ���쳣����-1��
	 * 
	 * @param resData
	 * @param encoding
	 * @return
	 */
	public static int getEncryptCert(Map<String, String> resData,
			String encoding) {
		String strCert = resData.get(SDKConstants.param_encryptPubKeyCert);
		String certType = resData.get(SDKConstants.param_certType);
		if (isEmpty(strCert) || isEmpty(certType))
			return -1;
		X509Certificate x509Cert = CertUtil.genCertificateByStr(strCert);
		if (CERTTYPE_01.equals(certType)) {
			// ����������Ϣ���ܹ�Կ
			if (!CertUtil.getEncryptCertId().equals(
					x509Cert.getSerialNumber().toString())) {
				// ID��ͬʱ���б���֤����²���
				String localCertPath = SDKConfig.getConfig().getEncryptCertPath();
				String newLocalCertPath = genBackupName(localCertPath);
				// 1.������֤����б��ݴ洢
				if (!copyFile(localCertPath, newLocalCertPath))
					return -1;
				// 2.���ݳɹ�,������֤��Ĵ洢
				if (!writeFile(localCertPath, strCert, encoding))
					return -1;
				LogUtil.writeLog("save new encryptPubKeyCert success");
				CertUtil.resetEncryptCertPublicKey();
				return 1;
			}else {
				return 0;
			}

		} else if (CERTTYPE_02.equals(certType)) {
//			// ���´ŵ����ܹ�Կ
//			if (!CertUtil.getEncryptTrackCertId().equals(
//					x509Cert.getSerialNumber().toString())) {
//				// ID��ͬʱ���б���֤����²���
//				String localCertPath = SDKConfig.getConfig().getEncryptTrackCertPath();
//				String newLocalCertPath = genBackupName(localCertPath);
//				// 1.������֤����б��ݴ洢
//				if (!copyFile(localCertPath, newLocalCertPath))
//					return -1;
//				// 2.���ݳɹ�,������֤��Ĵ洢
//				if (!writeFile(localCertPath, strCert, encoding))
//					return -1;
//				LogUtil.writeLog("save new encryptPubKeyCert success");
//				CertUtil.resetEncryptTrackCertPublicKey();
//				return 1;
//			}else {
				return 0;
//			}
		}else {
			LogUtil.writeLog("unknown cerType:"+certType);
			return -1;
		}
	}
	
	/**
	 * �ļ���������
	 * 
	 * @param srcFile
	 *            Դ�ļ�
	 * @param destFile
	 *            Ŀ���ļ�
	 * @return
	 * @throws IOException
	 */
	public static boolean copyFile(String srcFile, String destFile) {
		boolean flag = false;
		FileInputStream fin = null;
		FileOutputStream fout = null;
		FileChannel fcin = null;
		FileChannel fcout = null;
		try {
			// ��ȡԴ�ļ���Ŀ���ļ������������
			fin = new FileInputStream(srcFile);
			fout = new FileOutputStream(destFile);
			// ��ȡ�������ͨ��
			fcin = fin.getChannel();
			fcout = fout.getChannel();
			// ����������
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			while (true) {
				// clear�������軺������ʹ�����Խ��ܶ��������
				buffer.clear();
				// ������ͨ���н����ݶ���������
				int r = fcin.read(buffer);
				// read�������ض�ȡ���ֽ���������Ϊ�㣬�����ͨ���ѵ�������ĩβ���򷵻�-1
				if (r == -1) {
					flag = true;
					break;
				}
				// flip�����û��������Խ��¶��������д����һ��ͨ��
				buffer.flip();
				// �����ͨ���н�����д�뻺����
				fcout.write(buffer);
			}
			fout.flush();
		} catch (IOException e) {
			LogUtil.writeErrorLog("CopyFile fail", e);
		} finally {
			try {
				if (null != fin)
					fin.close();
				if (null != fout)
					fout.close();
				if (null != fcin)
					fcin.close();
				if (null != fcout)
					fcout.close();
			} catch (IOException ex) {
				LogUtil.writeErrorLog("Releases any system resources fail", ex);
			}
		}
		return flag;
	}
	
	/**
	 * д�ļ�����
	 * 
	 * @param filePath
	 *            �ļ�·��
	 * @param fileContent
	 *            �ļ�����
	 * @param encoding
	 *            ����
	 * @return
	 */
	public static boolean writeFile(String filePath, String fileContent,
			String encoding) {
		FileOutputStream fout = null;
		FileChannel fcout = null;
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		}
		
		try {
			fout = new FileOutputStream(filePath);
			// ��ȡ���ͨ��
			fcout = fout.getChannel();
			// ����������
			// ByteBuffer buffer = ByteBuffer.allocate(1024);
			ByteBuffer buffer = ByteBuffer.wrap(fileContent.getBytes(encoding));
			fcout.write(buffer);
			fout.flush();
		} catch (FileNotFoundException e) {
			LogUtil.writeErrorLog("WriteFile fail", e);
			return false;
		} catch (IOException ex) {
			LogUtil.writeErrorLog("WriteFile fail", ex);
			return false;
		} finally {
			try {
				if (null != fout)
					fout.close();
				if (null != fcout)
					fcout.close();
			} catch (IOException ex) {
				LogUtil.writeErrorLog("Releases any system resources fail", ex);
				return false;
			}
		}
		return true;
	}
	
	/**
	 * ��������ļ���(xxx)���� <br>
	 * ���Ϊ�� xxx_backup.cer
	 * 
	 * @param fileName
	 * @return
	 */
	public static String genBackupName(String fileName) {
		if (isEmpty(fileName))
			return "";
		int i = fileName.lastIndexOf(POINT);
		String leftFileName = fileName.substring(0, i);
		String rightFileName = fileName.substring(i + 1);
		String newFileName = leftFileName + "_backup" + POINT + rightFileName;
		return newFileName;
	}
	

	public static byte[] readFileByNIO(String filePath) {
		FileInputStream in = null;
		FileChannel fc = null;
		ByteBuffer bf = null;
		try {
			in = new FileInputStream(filePath);
			fc = in.getChannel();
			bf = ByteBuffer.allocate((int) fc.size());
			fc.read(bf);
			return bf.array();
		} catch (Exception e) {
			LogUtil.writeErrorLog(e.getMessage());
			return null;
		} finally {
			try {
				if (null != fc) {
					fc.close();
				}
				if (null != in) {
					in.close();
				}
			} catch (Exception e) {
				LogUtil.writeErrorLog(e.getMessage());
				return null;
			}
		}
	}
	
	/**
	 * �����������еĿ��ַ������߿��ַ���
	 * @param contentData
	 * @return
	 */
	public static Map<String, String> filterBlank(Map<String, String> contentData){
		LogUtil.writeLog("��ӡ�������� :");
		Map<String, String> submitFromData = new HashMap<String, String>();
		Set<String> keyset = contentData.keySet();
		
		for(String key:keyset){
			String value = contentData.get(key);
			if (StringUtils.isNotBlank(value)) {
				// ��valueֵ����ȥ��ǰ��մ���
				submitFromData.put(key, value.trim());
				LogUtil.writeLog(key + "-->" + String.valueOf(value));
			}
		}
		return submitFromData;
	}
	
	/**
	 * ��ѹ��.
	 * 
	 * @param inputByte
	 *            byte[]�������͵�����
	 * @return ��ѹ���������
	 * @throws IOException
	 */
	public static byte[] inflater(final byte[] inputByte) throws IOException {
		int compressedDataLength = 0;
		Inflater compresser = new Inflater(false);
		compresser.setInput(inputByte, 0, inputByte.length);
		ByteArrayOutputStream o = new ByteArrayOutputStream(inputByte.length);
		byte[] result = new byte[1024];
		try {
			while (!compresser.finished()) {
				compressedDataLength = compresser.inflate(result);
				if (compressedDataLength == 0) {
					break;
				}
				o.write(result, 0, compressedDataLength);
			}
		} catch (Exception ex) {
			System.err.println("Data format error!\n");
			ex.printStackTrace();
		} finally {
			o.close();
		}
		compresser.end();
		return o.toByteArray();
	}

	/**
	 * ѹ��.
	 * 
	 * @param inputByte
	 *            ��Ҫ��ѹ����byte[]����
	 * @return ѹ���������
	 * @throws IOException
	 */
	public static byte[] deflater(final byte[] inputByte) throws IOException {
		int compressedDataLength = 0;
		Deflater compresser = new Deflater();
		compresser.setInput(inputByte);
		compresser.finish();
		ByteArrayOutputStream o = new ByteArrayOutputStream(inputByte.length);
		byte[] result = new byte[1024];
		try {
			while (!compresser.finished()) {
				compressedDataLength = compresser.deflate(result);
				o.write(result, 0, compressedDataLength);
			}
		} finally {
			o.close();
		}
		compresser.end();
		return o.toByteArray();
	}
	
	/**
	 * �ж��ַ����Ƿ�ΪNULL���
	 * 
	 * @param s
	 *            ���жϵ��ַ�������
	 * @return �жϽ�� true-�� false-��
	 */
	public static boolean isEmpty(String s) {
		return null == s || "".equals(s.trim());
	}
	
}
