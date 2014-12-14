package security4j.util.coders;

import java.security.MessageDigest;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class BaseCoder {

	/**
	 * MAC�㷨��ѡ���¶����㷨
	 * 
	 * <pre>
	 * HmacMD5 
	 * HmacSHA1 
	 * HmacSHA256 
	 * HmacSHA384 
	 * HmacSHA512
	 * </pre>
	 */
	public static final String HMAC_MD5 	= "HmacMD5";
	public static final String HMAC_SHA1 	= "HmacSHA1";
	public static final String HMAC_SHA256 	= "HmacSHA256";
	public static final String HMAC_SHA384 	= "HmacSHA384";
	public static final String HMAC_SHA512 	= "HmacSHA512";

	/**
	 * BASE64����
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptBASE64(String key) throws Exception {
		return Base64.getDecoder().decode(key);
	}

	/**
	 * BASE64����
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptBASE64(byte[] key) throws Exception {
		return Base64.getDecoder().decode(key);
	}

	/**
	 * BASE64����
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptBASE64(byte[] key) throws Exception {
		return Base64.getEncoder().encode(key);
	}

	/**
	 * MD5(Message Digest algorithm
	 * 5����ϢժҪ�㷨)���ǵ�����ܣ��κ����ݼ��ܺ�ֻ�����Ψһ��һ�����ܴ���ͨ������У�������ڴ���������Ƿ��޸ġ�
	 * 
	 * @param data
	 *            ����֤��Ϣ
	 * @return ��MD5���ܺ�Ľ��
	 * @throws Exception
	 */
	public static byte[] encryptMD5(byte[] data) throws Exception {

		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(data);

		return md5.digest();

	}

	/**
	 * SHA(Secure Hash
	 * Algorithm����ȫɢ���㷨)���ǵ�����ܣ��κ����ݼ��ܺ�ֻ�����Ψһ��һ�����ܴ���ͨ������У�������ڴ���������Ƿ��޸ġ�
	 * 
	 * @param data
	 *            ����֤��Ϣ
	 * @return ��SHA���ܺ�Ľ��
	 * @throws Exception
	 */
	public static byte[] encryptSHA(byte[] data) throws Exception {

		MessageDigest sha = MessageDigest.getInstance("SHA");
		sha.update(data);

		return sha.digest();

	}

	/**
	 * ��ʼ��HMAC��Կ
	 * 
	 * @param keyType
	 *            MAC�㷨��ѡ���¶����㷨
	 * <pre>
	 * HmacMD5 
	 * HmacSHA1 
	 * HmacSHA256 
	 * HmacSHA384 
	 * HmacSHA512  
	 * </pre> 
	 * @return
	 * @throws Exception
	 */
	public static byte[] initMacKey(String algorithm) throws Exception {
		KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);

		SecretKey secretKey = keyGenerator.generateKey();
		return encryptBASE64(secretKey.getEncoded());
	}

	/**
	 * HMAC(Hash Message Authentication
	 * Code��ɢ����Ϣ������)���ǵ�����ܣ��κ����ݼ��ܺ�ֻ�����Ψһ��һ�����ܴ���ͨ������У�������ڴ���������Ƿ��޸ġ�
	 *
	 * @param keyType
	 *            MAC�㷨��ѡ���¶����㷨
	 * <pre>
	 * HmacMD5 
	 * HmacSHA1 
	 * HmacSHA256 
	 * HmacSHA384 
	 * HmacSHA512  
	 * </pre>      
	 * @param data
	 *            ��֤����
	 * @param key
	 *            δ��BASE64�������Կ��Ϣ
	 * @return byte[] ��HMAC���ܺ�Ľ��
	 * @throws Exception
	 */
	public static byte[] encryptHMAC(byte[] data, byte[] key, String algorithm) throws Exception {
		SecretKey secretKey = new SecretKeySpec(decryptBASE64(key), algorithm);
		Mac mac = Mac.getInstance(secretKey.getAlgorithm());
		mac.init(secretKey);

		return mac.doFinal(data);
	}

}
