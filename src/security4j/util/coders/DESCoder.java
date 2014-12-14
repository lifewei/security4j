package security4j.util.coders;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


/**
 * DES��ȫ�������
 * 
 * <pre>
 * ֧�� DES��DESede(TripleDES,����3DES)��AES��Blowfish��RC2��RC4(ARCFOUR)
 * DES          		key size must be equal to 56
 * DESede(TripleDES) 	key size must be equal to 112 or 168
 * AES          		key size must be equal to 128, 192 or 256,but 192 and 256 bits may not be available
 * Blowfish     		key size must be multiple of 8, and can only range from 32 to 448 (inclusive)
 * RC2          		key size must be between 40 and 1024 bits
 * RC4(ARCFOUR) 		key size must be between 40 and 1024 bits
 * �������� ��Ҫ��ע JDK Document http://.../docs/technotes/guides/security/SunProviders.html
 * </pre>
 * 
 */
public class DESCoder {
	/**
	 * ALGORITHM �㷨 <br>
	 * ���滻Ϊ��������һ���㷨��ͬʱkeyֵ��size��Ӧ�ı䡣
	 * 
	 * <pre>
	 * DES          		key size must be equal to 56
	 * DESede(TripleDES) 	key size must be equal to 112 or 168
	 * AES          		key size must be equal to 128, 192 or 256,but 192 and 256 bits may not be available
	 * Blowfish     		key size must be multiple of 8, and can only range from 32 to 448 (inclusive)
	 * RC2          		key size must be between 40 and 1024 bits
	 * RC4(ARCFOUR) 		key size must be between 40 and 1024 bits
	 * </pre>
	 * 
	 * ��Key toKey(byte[] key)������ʹ����������
	 * <code>SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);</code> �滻
	 * <code>
	 * DESKeySpec dks = new DESKeySpec(key);
	 * SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
	 * SecretKey secretKey = keyFactory.generateSecret(dks);
	 * </code>
	 */
	public static final String DES = "DES";
	public static final String DESede = "DESede";
	public static final String AES = "AES";
	public static final String Blowfish = "Blowfish";
	public static final String RC2 = "RC2";
	public static final String RC4 = "RC4";
	/**
	 * ת����Կ<br>
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	private static Key toKey(byte[] key, String algorithm) throws Exception {
		SecretKey secretKey = new SecretKeySpec(key, algorithm);
		return secretKey;
	}

	/**
	 * ����
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] data, byte[] key, String algorithm) throws Exception {
		Key k = toKey(BaseCoder.decryptBASE64(key), algorithm);

		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.DECRYPT_MODE, k);

		return cipher.doFinal(data);
	}

	/**
	 * ����
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] data, byte[] key, String algorithm) throws Exception {
		Key k = toKey(BaseCoder.decryptBASE64(key), algorithm);
		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.ENCRYPT_MODE, k);

		return cipher.doFinal(data);
	}


	/**
	 * ������Կ
	 * 
	 * @return
	 * @throws Exception
	 */
	public static SecretKey generateKey(String algorithm) throws Exception {

		KeyGenerator kg = KeyGenerator.getInstance(algorithm);
		String random = "" + System.currentTimeMillis();
		kg.init(new SecureRandom(random.getBytes()));
		SecretKey secretKey = kg.generateKey();
		return secretKey;
	}
	
	/**
	 * ȡ����Կ
	 * 
	 * @param keyPath
	 * 				��Կ�ļ�·��
	 * @return byte[]
	 * 				δ��BASE64���빫Կ����
	 * @throws Exception
	 */
	public static byte[] getKey(String keyPath)
			throws Exception {
		File file = new File(keyPath);
		FileInputStream fis = new FileInputStream(file);
		byte[] key = new byte[fis.available()];
		fis.read(key);
		fis.close();
		return key;
	}
	
	/**
	 * 
	 * @param key
	 * 			Ҫ�������Կ
	 * @param keyPath
	 * 			����·��
	 * @throws IOException
	 * @throws Exception
	 */
	public static void saveKey(SecretKey key, String keyPath)  throws IOException, Exception {
        // save key   
        File file  = new File(keyPath);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(BaseCoder.encryptBASE64(key.getEncoded()));
        fos.flush();
        fos.close();
	}
}
