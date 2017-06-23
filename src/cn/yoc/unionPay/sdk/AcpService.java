package cn.yoc.unionPay.sdk;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @ClassName AcpService
 * @Description acpsdk�ӿڷ����࣬�����̻����������ֱ�Ӳο�ʹ�ñ����еķ���
 * @date 2016-7-22 ����2:44:37
 */
public class AcpService {

	/**
	 * ������ǩ��(ʹ�������ļ������õ�˽Կ֤����߶Գ���Կǩ��)<br>
	 * ���ܣ��������Ľ���ǩ��,�����㸳ֵcertid,signature�ֶβ�����<br>
	 * @param reqData ������map<br>
	 * @param encoding ������������encoding�ֶε�ֵ<br>
	 * @return��ǩ�����map����<br>
	 */
	public static Map<String, String> sign(Map<String, String> reqData,String encoding) {
		reqData = SDKUtil.filterBlank(reqData);
		SDKUtil.sign(reqData, encoding);
		return reqData;
	}
	
	/**
	 * ͬsignByCertInfo<br>
	 * @param reqData
	 * @param certPath
	 * @param certPwd
	 * @param encoding
	 * @return
	 * @deprecated
	 */
	public static Map<String, String> sign(Map<String, String> reqData, String certPath,
			String certPwd,String encoding) {
		reqData = SDKUtil.filterBlank(reqData);
		SDKUtil.signByCertInfo(reqData,certPath,certPwd,encoding);
		return reqData;
	}
	
	/**
	 * ��֤��ǩ��(ͨ������˽Կ֤��·��������ǩ����<br>
	 * ���ܣ�����ж���̻��Ž�������,ÿ���̻��Ŷ�Ӧ��ͬ��֤�����ʹ�ô˷���:����˽Կ֤�������(������acp_sdk.properties�� ���� acpsdk.singleMode=false)<br>
	 * @param reqData ������map<br>
	 * @param certPath ǩ��˽Կ�ļ�����·����<br>
	 * @param certPwd ǩ��˽Կ����<br>
	 * @param encoding ������������encoding�ֶε�ֵ<br>
	 * @return��ǩ�����map����<br>
	 */
	public static Map<String, String> signByCertInfo(Map<String, String> reqData, String certPath,
			String certPwd,String encoding) {
		reqData = SDKUtil.filterBlank(reqData);
		SDKUtil.signByCertInfo(reqData,certPath,certPwd,encoding);
		return reqData;
	}
	
	/**
	 * ����Կǩ��(ͨ��������Կǩ��)<br>
	 * ���ܣ�����ж���̻��Ž�������,ÿ���̻��Ŷ�Ӧ��ͬ��֤�����ʹ�ô˷���:����˽Կ֤�������(������acp_sdk.properties�� ���� acpsdk.singleMode=false)<br>
	 * @param reqData ������map<br>
	 * @param secureKey ǩ���Գ���Կ<br>
	 * @param encoding ������������encoding�ֶε�ֵ<br>
	 * @return��ǩ�����map����<br>
	 */
	public static Map<String, String> signBySecureKey(Map<String, String> reqData, String secureKey, String encoding) {
		reqData = SDKUtil.filterBlank(reqData);
		SDKUtil.signBySecureKey(reqData, secureKey, encoding);
		return reqData;
	}
	
	/**
	 * ��֤ǩ��(SHA-1ժҪ�㷨)<br>
	 * @param resData ���ر�������<br>
	 * @param encoding ������������encoding�ֶε�ֵ<br>
	 * @return true ͨ�� false δͨ��<br>
	 */
	public static boolean validate(Map<String, String> rspData, String encoding) {
		return SDKUtil.validate(rspData, encoding);
	}
	
	/**
	 * ����Կ��ǩ(ͨ��������Կǩ��)<br>
	 * @param resData ���ر�������<br>
	 * @param encoding ������������encoding�ֶε�ֵ<br>
	 * @return true ͨ�� false δͨ��<br>
	 */
	public static boolean validateBySecureKey(Map<String, String> rspData, String secureKey, String encoding) {
		return SDKUtil.validateBySecureKey(rspData, secureKey, encoding);
	}
	
	/**
	 * ���ܣ���̨�����ύ�����Ĳ�����ͬ��Ӧ����<br>
	 * @param reqData ������<br>
	 * @param rspData Ӧ����<br>
	 * @param reqUrl  �����ַ<br>
	 * @param encoding<br>
	 * @return Ӧ��http 200����true ,����false<br>
	 */
	public static Map<String,String> post(
			Map<String, String> reqData,String reqUrl,String encoding) {
		Map<String, String> rspData = new HashMap<String,String>();
		LogUtil.writeLog("����������ַ:" + reqUrl);
		//���ͺ�̨��������
		HttpClient hc = new HttpClient(reqUrl, 30000, 30000);
		try {
			int status = hc.send(reqData, encoding);
			if (200 == status) {
				String resultString = hc.getResult();
				if (null != resultString && !"".equals(resultString)) {
					// �����ؽ��ת��Ϊmap
					Map<String,String> tmpRspData  = SDKUtil.convertResultStringToMap(resultString);
					rspData.putAll(tmpRspData);
				}
			}else{
				LogUtil.writeLog("����http״̬��["+status+"]�����������Ļ��������ַ�Ƿ���ȷ");
			}
		} catch (Exception e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
		}
		return rspData;
	}
	
	/**
	 * ���ܣ�http Get���� ����ɷѲ�Ʒ��ʹ��<br>
	 * @param reqUrl �����ַ<br>
	 * @param encoding<br>
	 * @return
	 */
	public static String get(String reqUrl,String encoding) {
		
		LogUtil.writeLog("����������ַ:" + reqUrl);
		//���ͺ�̨��������
		HttpClient hc = new HttpClient(reqUrl, 30000, 30000);
		try {
			int status = hc.sendGet(encoding);
			if (200 == status) {
				String resultString = hc.getResult();
				if (null != resultString && !"".equals(resultString)) {
					return resultString;
				}
			}else{
				LogUtil.writeLog("����http״̬��["+status+"]�����������Ļ��������ַ�Ƿ���ȷ");
			}
		} catch (Exception e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
		}
		return null;
	}
	
	
	/**
	 * ���ܣ�ǰ̨���׹���HTTP POST�Զ��ύ��<br>
	 * @param action ���ύ��ַ<br>
	 * @param hiddens ��MAP��ʽ�洢�ı���ֵ<br>
	 * @param encoding ������������encoding�ֶε�ֵ<br>
	 * @return ����õ�HTTP POST���ױ�<br>
	 */
	public static String createAutoFormHtml(String reqUrl, Map<String, String> hiddens,String encoding) {
		StringBuffer sf = new StringBuffer();
		sf.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset="+encoding+"\"/></head><body>");
		sf.append("<form id = \"pay_form\" action=\"" + reqUrl
				+ "\" method=\"post\">");
		if (null != hiddens && 0 != hiddens.size()) {
			Set<Entry<String, String>> set = hiddens.entrySet();
			Iterator<Entry<String, String>> it = set.iterator();
			while (it.hasNext()) {
				Entry<String, String> ey = it.next();
				String key = ey.getKey();
				String value = ey.getValue();
				sf.append("<input type=\"hidden\" name=\"" + key + "\" id=\""
						+ key + "\" value=\"" + value + "\"/>");
			}
		}
		sf.append("</form>");
		sf.append("</body>");
		sf.append("<script type=\"text/javascript\">");
		sf.append("document.all.pay_form.submit();");
		sf.append("</script>");
		sf.append("</html>");
		return sf.toString();
	}

	
	/**
	 * ���ܣ��������ļ�����ʹ��DEFLATEѹ���㷨ѹ����Base64���������ַ���������<br>
	 * ���õ��Ľ��ף������������������գ������˻�<br>
	 * @param filePath �����ļ�-ȫ·���ļ���<br>
	 * @return
	 */
	public static String enCodeFileContent(String filePath,String encoding){
		String baseFileContent = "";
		
		File file = new File(filePath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				LogUtil.writeErrorLog(e.getMessage(), e);
			}
		}
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			int fl = in.available();
			if (null != in) {
				byte[] s = new byte[fl];
				in.read(s, 0, fl);
				// ѹ������.
				baseFileContent = new String(SecureUtil.base64Encode(SDKUtil.deflater(s)),encoding);
			}
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
		return baseFileContent;
	}
	
	/**
	 * ���ܣ��������׷��ص�fileContent�ַ�������� �� ��base64����DEFLATEѹ������أ�<br>
	 * ���õ��Ľ��ף������ļ����أ���������״̬��ѯ<br>
	 * @param resData ���ر���map<br>
	 * @param fileDirectory ��ص��ļ�Ŀ¼������·����
	 * @param encoding ������������encoding�ֶε�ֵ<br>	
	 */
	public static String deCodeFileContent(Map<String, String> resData,String fileDirectory,String encoding) {
		// ���������ļ�
		String filePath = null;
		String fileContent = resData.get(SDKConstants.param_fileContent);
		if (null != fileContent && !"".equals(fileContent)) {
			FileOutputStream out = null;
			try {
				byte[] fileArray = SDKUtil.inflater(SecureUtil
						.base64Decode(fileContent.getBytes(encoding)));
				if (SDKUtil.isEmpty(resData.get("fileName"))) {
					filePath = fileDirectory + File.separator + resData.get("merId")
							+ "_" + resData.get("batchNo") + "_"
							+ resData.get("txnTime") + ".txt";
				} else {
					filePath = fileDirectory + File.separator + resData.get("fileName");
				}
				File file = new File(filePath);
				if (file.exists()) {
					file.delete();
				}
				file.createNewFile();
			    out = new FileOutputStream(file);
				out.write(fileArray, 0, fileArray.length);
				out.flush();
			} catch (UnsupportedEncodingException e) {
				LogUtil.writeErrorLog(e.getMessage(), e);
			} catch (IOException e) {
				LogUtil.writeErrorLog(e.getMessage(), e);
			}finally{
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return filePath;
	}

	/**
	 * ���ܣ�������ļ����� ת���������ַ�������base64,��ѹ��<br>
	 * ���õ��Ľ��ף���������״̬��ѯ<br>
	 * @param fileContent ��������״̬��ѯ���ص��ļ�����<br>
	 * @return ��������<br>
	 */
	public static String getFileContent(String fileContent,String encoding){
		String fc = "";
		try {
			fc = new String(SDKUtil.inflater(SecureUtil.base64Decode(fileContent.getBytes())),encoding);
		} catch (UnsupportedEncodingException e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
		} catch (IOException e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
		}
		return fc;
	}
	
	
	/**
	 * ���ܣ��ֿ�����Ϣ��customerInfo����<br>
	 * ˵��������ѡ��������Ϣ����Ȩ��ʹ�þɵĹ���customerInfo��ʽ������������Ϣ���м��ܣ��� phoneNo��cvn2�� expired�����ܣ����������pin�Ļ������<br>
	 * @param customerInfoMap ��Ϣ��������� key������value��ֵ,����<br>
	 *        ���磺customerInfoMap.put("certifTp", "01");					//֤������<br>
				  customerInfoMap.put("certifId", "341126197709218366");	//֤������<br>
				  customerInfoMap.put("customerNm", "������");				//����<br>
				  customerInfoMap.put("phoneNo", "13552535506");			//�ֻ���<br>
				  customerInfoMap.put("smsCode", "123456");					//������֤��<br>
				  customerInfoMap.put("pin", "111111");						//���루���ܣ�<br>
				  customerInfoMap.put("cvn2", "123");           			//�������cvn2��λ���֣������ܣ�<br>
				  customerInfoMap.put("expired", "1711");  				    //��Ч�� ����ǰ���ں󣨲�����)<br>
	 * @param accNo  customerInfoMap����������ô���ű���,���customerInfoMapδ������pin�����ֶο��Բ���<br>
	 * @param encoding ������������encoding�ֶε�ֵ<br>				  
	 * @return base64��ĳֿ�����Ϣ���ֶ�<br>
	 */
	public static String getCustomerInfo(Map<String,String> customerInfoMap,String accNo,String encoding) {
		
		if(customerInfoMap.isEmpty())
			return "{}";
		StringBuffer sf = new StringBuffer("{");
		for(Iterator<String> it = customerInfoMap.keySet().iterator(); it.hasNext();){
			String key = it.next();
			String value = customerInfoMap.get(key);
			if(key.equals("pin")){
				if(null == accNo || "".equals(accNo.trim())){
					LogUtil.writeLog("�������루PIN����������getCustomerInfo�������ϴ�����");
					throw new RuntimeException("����PINû�Ϳ����޷���������");
				}else{
					value = encryptPin(accNo,value,encoding);
				}
			}
			sf.append(key).append(SDKConstants.EQUAL).append(value);
			if(it.hasNext())
				sf.append(SDKConstants.AMPERSAND);
		}
		String customerInfo = sf.append("}").toString();
		LogUtil.writeLog("��װ��customerInfo���ģ�"+customerInfo);
		try {
			return new String(SecureUtil.base64Encode(sf.toString().getBytes(
					encoding)),encoding);
		} catch (UnsupportedEncodingException e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
		} catch (IOException e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
		}
		return customerInfo;
	}
	
	/**
	 * ���ܣ��ֿ�����Ϣ��customerInfo���죬��ѡ��������Ϣ����Ȩ�� �����¼��ܹ淶����pin��phoneNo��cvn2��expired���� <br>
	 * ���õ��Ľ��ף� <br>
	 * @param customerInfoMap ��Ϣ��������� key������value��ֵ,���� <br>
	 *        ���磺customerInfoMap.put("certifTp", "01");					//֤������ <br>
				  customerInfoMap.put("certifId", "341126197709218366");	//֤������ <br>
				  customerInfoMap.put("customerNm", "������");				//���� <br>
				  customerInfoMap.put("smsCode", "123456");					//������֤�� <br>
				  customerInfoMap.put("pin", "111111");						//���루���ܣ� <br>
				  customerInfoMap.put("phoneNo", "13552535506");			//�ֻ��ţ����ܣ� <br>
				  customerInfoMap.put("cvn2", "123");           			//�������cvn2��λ���֣����ܣ� <br>
				  customerInfoMap.put("expired", "1711");  				    //��Ч�� ����ǰ���ں󣨼��ܣ� <br>
	 * @param accNo  customerInfoMap����������ô���ű���,���customerInfoMapδ������PIN�����ֶο��Բ���<br>
	 * @param encoding ������������encoding�ֶε�ֵ
	 * @return base64��ĳֿ�����Ϣ���ֶ� <br>
	 */
	public static String getCustomerInfoWithEncrypt(Map<String,String> customerInfoMap,String accNo,String encoding) {
		if(customerInfoMap.isEmpty())
			return "{}";
		StringBuffer sf = new StringBuffer("{");
		//������Ϣ������
		StringBuffer encryptedInfoSb = new StringBuffer("");
		
		for(Iterator<String> it = customerInfoMap.keySet().iterator(); it.hasNext();){
			String key = it.next();
			String value = customerInfoMap.get(key);
			if(key.equals("phoneNo") || key.equals("cvn2") || key.equals("expired")){
				encryptedInfoSb.append(key).append(SDKConstants.EQUAL).append(value).append(SDKConstants.AMPERSAND);
			}else{
				if(key.equals("pin")){
					if(null == accNo || "".equals(accNo.trim())){
						LogUtil.writeLog("�������루PIN����������getCustomerInfoWithEncrypt�������ϴ�����");
						throw new RuntimeException("����PINû�Ϳ����޷���������");
					}else{
						value = encryptPin(accNo,value,encoding);
					}
				}
				sf.append(key).append(SDKConstants.EQUAL).append(value).append(SDKConstants.AMPERSAND);
			}
		}
		
		if(!encryptedInfoSb.toString().equals("")){
			encryptedInfoSb.setLength(encryptedInfoSb.length()-1);//ȥ�����һ��&����
			LogUtil.writeLog("��װ��customerInfo encryptedInfo���ģ�"+ encryptedInfoSb.toString());
			sf.append("encryptedInfo").append(SDKConstants.EQUAL).append(encryptData(encryptedInfoSb.toString(), encoding));
		}else{
			sf.setLength(sf.length()-1);
		}
		
		String customerInfo = sf.append("}").toString();
		LogUtil.writeLog("��װ��customerInfo���ģ�"+customerInfo);
		try {
			return new String(SecureUtil.base64Encode(sf.toString().getBytes(encoding)),encoding);
		} catch (UnsupportedEncodingException e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
		} catch (IOException e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
		}
		return customerInfo;
	}
	
	/**
	 * �������ر��ģ���̨֪ͨ���е�customerInfo��<br>
	 * ��base64,�����������Ϣ���� encryptedInfo ������ܲ��� encryptedInfo�е���ŵ�customerInfoMap����<br>
	 * @param customerInfo<br>
	 * @param encoding<br>
	 * @return
	 */
	public static Map<String,String> parseCustomerInfo(String customerInfo,String encoding){
		Map<String,String> customerInfoMap = null;
		try {
				byte[] b = SecureUtil.base64Decode(customerInfo.getBytes(encoding));
				String customerInfoNoBase64 = new String(b,encoding);
				LogUtil.writeLog("��base64��===>" +customerInfoNoBase64);
				//ȥ��ǰ���{}
				customerInfoNoBase64 = customerInfoNoBase64.substring(1, customerInfoNoBase64.length()-1);
				customerInfoMap = SDKUtil.parseQString(customerInfoNoBase64);
				if(customerInfoMap.containsKey("encryptedInfo")){
					String encInfoStr = customerInfoMap.get("encryptedInfo");
					customerInfoMap.remove("encryptedInfo");
					String encryptedInfoStr = decryptData(encInfoStr, encoding);
					Map<String,String> encryptedInfoMap = SDKUtil.parseQString(encryptedInfoStr);
					customerInfoMap.putAll(encryptedInfoMap);
				}
			} catch (UnsupportedEncodingException e) {
				LogUtil.writeErrorLog(e.getMessage(), e);
			} catch (IOException e) {
				LogUtil.writeErrorLog(e.getMessage(), e);
			}
		return customerInfoMap;
	}
	
	/**
	 * �������ر��ģ���̨֪ͨ���е�customerInfo��<br>
	 * ��base64,�����������Ϣ���� encryptedInfo ������ܲ��� encryptedInfo�е���ŵ�customerInfoMap����<br>
	 * @param customerInfo<br>
	 * @param encoding<br>
	 * @return
	 */
	public static Map<String,String> parseCustomerInfo(String customerInfo, String certPath,
			String certPwd, String encoding){
		Map<String,String> customerInfoMap = null;
		try {
				byte[] b = SecureUtil.base64Decode(customerInfo.getBytes(encoding));
				String customerInfoNoBase64 = new String(b,encoding);
				LogUtil.writeLog("��base64��===>" +customerInfoNoBase64);
				//ȥ��ǰ���{}
				customerInfoNoBase64 = customerInfoNoBase64.substring(1, customerInfoNoBase64.length()-1);
				customerInfoMap = SDKUtil.parseQString(customerInfoNoBase64);
				if(customerInfoMap.containsKey("encryptedInfo")){
					String encInfoStr = customerInfoMap.get("encryptedInfo");
					customerInfoMap.remove("encryptedInfo");
					String encryptedInfoStr = decryptData(encInfoStr, certPath, certPwd, encoding);
					Map<String,String> encryptedInfoMap = SDKUtil.parseQString(encryptedInfoStr);
					customerInfoMap.putAll(encryptedInfoMap);
				}
			} catch (UnsupportedEncodingException e) {
				LogUtil.writeErrorLog(e.getMessage(), e);
			} catch (IOException e) {
				LogUtil.writeErrorLog(e.getMessage(), e);
			}
		return customerInfoMap;
	}

	/**
	 * ������ܲ���base64<br>
	 * @param accNo ����<br>
	 * @param pwd ����<br>
	 * @param encoding<br>
	 * @return ���ܵ�����<br>
	 */
	public static String encryptPin(String accNo, String pin, String encoding) {
		return SecureUtil.encryptPin(accNo, pin, encoding, CertUtil
				.getEncryptCertPublicKey());
	}
	
	/**
	 * ������Ϣ���ܲ���base64(���ţ��ֻ��ţ�cvn2,��Ч�ڣ�<br>
	 * @param data �� phoneNo,cvn2,��Ч��<br>
	 * @param encoding<br>
	 * @return ���ܵ�����<br>
	 */
	public static String encryptData(String data, String encoding) {
		return SecureUtil.encryptData(data, encoding, CertUtil
				.getEncryptCertPublicKey());
	}
	
	/**
	 * ������Ϣ���ܣ�ʹ�������ļ�acp_sdk.properties����<br>
	 * @param base64EncryptedInfo ������Ϣ<br>
	 * @param encoding<br>
	 * @return ���ܺ������<br>
	 */
	public static String decryptData(String base64EncryptedInfo, String encoding) {
		return SecureUtil.decryptData(base64EncryptedInfo, encoding, CertUtil
				.getSignCertPrivateKey());
	}
	
	/**
	 * ������Ϣ����,ͨ�������˽Կ����<br>
	 * @param base64EncryptedInfo ������Ϣ<br>
	 * @param certPath ˽Կ�ļ�����ȫ·����<br>
	 * @param certPwd ˽Կ����<br>
	 * @param encoding<br>
	 * @return
	 */
	public static String decryptData(String base64EncryptedInfo, String certPath,
			String certPwd, String encoding) {
		return SecureUtil.decryptData(base64EncryptedInfo, encoding, CertUtil
				.getSignCertPrivateKeyByStoreMap(certPath, certPwd));
	}

	/**
	 * 5.0.0���ܴŵ���Ϣ��5.1.0�ӿ�����ʹ��<br>
	 * @param trackData �����ܴŵ�����<br>
	 * @param encoding �����ʽ<br>
	 * @return ���ܵ�����<br>
	 * @deprecated
	 */
	public static String encryptTrack(String trackData, String encoding) {
		return SecureUtil.encryptData(trackData, encoding,
				CertUtil.getEncryptTrackPublicKey());
	}
	
	/**
	 * ��ȡ������Ϣ����֤����������к�<br>
	 * @return
	 */
	public static String getEncryptCertId(){
		return CertUtil.getEncryptCertId();
	}
	
	/**
	 * ���ַ�����base64<br>
	 * @param rawStr<br>
	 * @param encoding<br>
	 * @return<br>
	 * @throws IOException
	 */
	public static String base64Encode(String rawStr,String encoding) throws IOException {
		byte [] rawByte = rawStr.getBytes(encoding);
		return new String(SecureUtil.base64Encode(rawByte),encoding);
	}
	/**
	 * ��base64���ַ�����base64<br>
	 * @param base64Str<br>
	 * @param encoding<br>
	 * @return<br>
	 * @throws IOException
	 */
	public static String base64Decode(String base64Str,String encoding) throws IOException {
		byte [] rawByte = base64Str.getBytes(encoding);
		return new String(SecureUtil.base64Decode(rawByte),encoding);
	}


	/**
	 * 
	 * �п�������Ϣ��(cardTransData)����<br>
	 * �����������á�{}��������������ԡ�&���������ӡ���ʽ���£�{������1=ֵ&������2=ֵ&������3=ֵ}<br>
	 * ˵������ʾ�������ο�������ʱ����ݽӿ��ĵ��еı���Ҫ����װ<br>
	 * 
	 * @param cardTransDataMap cardTransData������<br>
	 * @param requestData �������merId��orderId��txnTime��txnAmt���ŵ�����ʱ��Ҫʹ��<br>
	 * @param encoding ����<br>
	 * @return
	 */
	public static String getCardTransData(Map<String, String> cardTransDataMap,
			Map<String, String> requestData,
			String encoding) { {

		StringBuffer cardTransDataBuffer = new StringBuffer();
		
		if(cardTransDataMap.containsKey("track2Data")){
			StringBuffer track2Buffer = new StringBuffer();
			track2Buffer.append(requestData.get("merId"))
					.append(SDKConstants.COLON).append(requestData.get("orderId"))
					.append(SDKConstants.COLON).append(requestData.get("txnTime"))
					.append(SDKConstants.COLON).append(requestData.get("txnAmt")==null?0:requestData.get("txnAmt"))
					.append(SDKConstants.COLON).append(cardTransDataMap.get("track2Data"));
			cardTransDataMap.put("track2Data", 
					AcpService.encryptData(track2Buffer.toString(),	encoding));
		}
		
		if(cardTransDataMap.containsKey("track3Data")){
			StringBuffer track3Buffer = new StringBuffer();
			track3Buffer.append(requestData.get("merId"))
				.append(SDKConstants.COLON).append(requestData.get("orderId"))
				.append(SDKConstants.COLON).append(requestData.get("txnTime"))
				.append(SDKConstants.COLON).append(requestData.get("txnAmt")==null?0:requestData.get("txnAmt"))
				.append(SDKConstants.COLON).append(cardTransDataMap.get("track3Data"));
			cardTransDataMap.put("track3Data", 
					AcpService.encryptData(track3Buffer.toString(),	encoding));
		}

		return cardTransDataBuffer.append(SDKConstants.LEFT_BRACE)
				.append(SDKUtil.coverMap2String(cardTransDataMap))
				.append(SDKConstants.RIGHT_BRACE).toString();
		}
	
	}
	
	/**
	 * ��ȡӦ�����еļ��ܹ�Կ֤��,���洢������,����ԭʼ֤��,���Զ��滻֤��<br>
	 * ���³ɹ��򷵻�1���޸��·���0��ʧ���쳣����-1<br>
	 * @param resData ���ر���
	 * @param encoding
	 * @return
	 */
	public static int updateEncryptCert(Map<String, String> resData,
			String encoding) {
		return SDKUtil.getEncryptCert(resData, encoding);
	}
	
	/**
	 * ��ȡ
	 * @param number
	 * @return
	 */
	public static int genLuhn(String number){
		return SecureUtil.genLuhn(number);
	}
}
