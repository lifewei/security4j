package security4j.util.coders;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;


public class RSACoder {
	private static final String KEY_ALGORITHM = "RSA";
	private static final String SIGNATURE_ALGORITHM = "MD5withRSA";
	//�������ĳ��Ȳ��ܳ���117
	private static final int MAX_ENCRYPT_BLOCK_SIZE = 117;
	public static final int KEY_LENGTH_MIN = 512;
	public static final int KEY_LENGTH_1024 = 1024;
	public static final int KEY_LENGTH_2048 = 2048;
	public static final int KEY_LENGTH_MAX = KEY_LENGTH_2048;

	/**
	 * ��˽Կ����Ϣ��������ǩ��
	 * 
	 * @param data
	 *            ��������
	 * @param privateKey
	 *            δ��BASE64�����˽Կ����
	 * 
	 * @return byte[]
	 * 				ǩ���������
	 * @throws Exception
	 */
	public static byte[] sign(byte[] data, byte[] privateKey) throws Exception {
		// ������base64�����˽Կ
		byte[] keyBytes = BaseCoder.decryptBASE64(privateKey);

		// ����PKCS8EncodedKeySpec����
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);

		// KEY_ALGORITHM ָ���ļ����㷨
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

		// ȡ˽Կ�׶���
		PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);

		// ��˽Կ����Ϣ��������ǩ��
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initSign(priKey);
		signature.update(data);

		return BaseCoder.encryptBASE64(signature.sign());
	}

	/**
	 * У������ǩ��
	 * 
	 * @param data
	 *            ��������
	 * @param publicKey
	 *            δ��BASE64�����빫Կ����
	 * @param sign
	 *            ������ǩ���������
	 * 
	 * @return У��ɹ�����true ʧ�ܷ���false
	 * @throws Exception
	 * 
	 */
	public static boolean verify(byte[] data, byte[] publicKey, byte[] sign)
			throws Exception {

		// ������base64����Ĺ�Կ
		byte[] keyBytes = BaseCoder.decryptBASE64(publicKey);

		// ����X509EncodedKeySpec����
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);

		// KEY_ALGORITHM ָ���ļ����㷨
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

		// ȡ��Կ�׶���
		PublicKey pubKey = keyFactory.generatePublic(keySpec);

		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initVerify(pubKey);
		signature.update(data);

		// ��֤ǩ���Ƿ�����
		return signature.verify(BaseCoder.decryptBASE64(sign));
	}

	
	/**
	 * ����<br>
	 * ��˽Կ����
	 * 
	 * @param data
	 * 			���������
	 * @param key
	 * 			δ����BASE64�����˽Կ����
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptByPrivateKey(byte[] data, byte[] key)
			throws Exception {
		// ����Կ����
		byte[] keyBytes = BaseCoder.decryptBASE64(key);

		// ȡ��˽Կ
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		RSAPrivateKey privateKey = (RSAPrivateKey)keyFactory.generatePrivate(pkcs8KeySpec);

		// �����ݽ���
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		int datalength = data.length;
		//��ȡ��Կ�����ټ����Ӧ��������ĳ��ȣ�����ȡ��
		int subLength = (int) Math.ceil((double)privateKey.getModulus().toString(2).length() / 8);

		ByteArrayOutputStream decryptData = new ByteArrayOutputStream();
		for (int i = 0; i < datalength; i += subLength) {
			if (datalength - i > subLength) {
				decryptData.write(cipher.doFinal(data, i, subLength));
			} else {
				decryptData.write(cipher.doFinal(data, i, datalength - i));
			}
			decryptData.flush();
		}
		
		return decryptData.toByteArray();
	}

	/**
	 * ����<br>
	 * �ù�Կ����
	 * 
	 * @param data
	 * 			���������
	 * @param key
	 * 			δ����BASE64����Ĺ�Կ����
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptByPublicKey(byte[] data, byte[] key)
			throws Exception {
		// ����Կ����
		byte[] keyBytes = BaseCoder.decryptBASE64(key);

		// ȡ�ù�Կ
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		RSAPublicKey publicKey = (RSAPublicKey)keyFactory.generatePublic(x509KeySpec);
		// �����ݽ���
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, publicKey);
		//ÿһ��������ݲ��ܳ�����Կ���ȸ��ֽ�
		int datalength = data.length;
		//��ȡ��Կ�����ټ����Ӧ��������ĳ��ȣ�����ȡ��
		int subLength = (int) Math.ceil((double)publicKey.getModulus().toString(2).length() / 8);

		ByteArrayOutputStream decryptData = new ByteArrayOutputStream();
		for (int i = 0; i < datalength; i += subLength) {
			if (datalength - i > subLength) {
				decryptData.write(cipher.doFinal(data, i, subLength));
			} else {
				decryptData.write(cipher.doFinal(data, i, datalength - i));
			}
			decryptData.flush();
		}
		return decryptData.toByteArray();
	}

	/**
	 * ����<br>
	 * �ù�Կ����
	 * 
	 * @param data
	 * 			���������
	 * @param key
	 * 			δ��BASE64���빫Կ����
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptByPublicKey(byte[] data, byte[] key)
			throws Exception {
		// �Թ�Կ����
		byte[] keyBytes = BaseCoder.decryptBASE64(key);

		// ȡ�ù�Կ
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		RSAPublicKey publicKey = (RSAPublicKey)keyFactory.generatePublic(x509KeySpec);
		// �����ݷֶμ���
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);		
		
		int datalength = data.length;
		//��ȡ��Կ�����ټ����Ӧ��������ĳ��ȣ�ÿ�����ݵĳ��Ȳ��ܳ���117���ߣ���Կ����-11���ֽ�
		int subLength = publicKey.getModulus().toString(2).length() / 8 - 11;

		if (subLength > MAX_ENCRYPT_BLOCK_SIZE) {
			subLength = MAX_ENCRYPT_BLOCK_SIZE;
		}
		ByteArrayOutputStream encryptData = new ByteArrayOutputStream();
		for (int i = 0; i < datalength; i += subLength) {
			if (datalength - i > subLength) {
				encryptData.write(cipher.doFinal(data, i, subLength));
			} else {
				encryptData.write(cipher.doFinal(data, i, datalength - i));
			}
			encryptData.flush();
		}
		return encryptData.toByteArray();
	}

	/**
	 * ����<br>
	 * ��˽Կ����
	 * 
	 * @param data
	 * 			���������
	 * @param key
	 * 			δ��BASE64�����˽Կ����
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptByPrivateKey(byte[] data, byte[] key)
			throws Exception {
		// ����Կ����
		byte[] keyBytes = BaseCoder.decryptBASE64(key);

		// ȡ��˽Կ
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		RSAPrivateKey privateKey = (RSAPrivateKey)keyFactory.generatePrivate(pkcs8KeySpec);
		// �����ݷֶμ���
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);		
		//ÿ�����ݵĳ��Ȳ��ܳ�����Կ����-11���ֽ�
		int datalength = data.length;
		//��ȡ��Կ�����ټ����Ӧ��������ĳ���
		int subLength = privateKey.getModulus().toString(2).length() / 8 - 11;
		if (subLength > MAX_ENCRYPT_BLOCK_SIZE) {
			subLength = MAX_ENCRYPT_BLOCK_SIZE;
		}

		ByteArrayOutputStream encryptData = new ByteArrayOutputStream();
		for (int i = 0; i < datalength; i += subLength) {
			if (datalength - i > subLength) {
				encryptData.write(cipher.doFinal(data, i, subLength));
			} else {
				encryptData.write(cipher.doFinal(data, i, datalength - i));
			}
			encryptData.flush();
		}

		return encryptData.toByteArray();
	}

	/**
	 * ȡ��˽Կ
	 * 
	 * @param keyPath
	 * 				��Կ�ļ�·��
	 * @return byte[]
	 * 				δ��BASE64������Կ����
	 * @throws Exception
	 */
	public static byte[] getPrivateKey(String keyPath)
			throws Exception {
		File file = new File(keyPath);
		FileInputStream fis = new FileInputStream(file);
		byte[] key = new byte[fis.available()];
		fis.read(key);
		fis.close();
		return key;
	}

	/**
	 * ȡ�ù�Կ
	 * 
	 * @param keyPath
	 * 				��Կ�ļ�·��
	 * @return byte[]
	 * 				δ��BASE64���빫Կ����
	 * @throws Exception
	 */
	public static byte[] getPublicKey(String keyPath)
			throws Exception {
		File file = new File(keyPath);
		FileInputStream fis = new FileInputStream(file);
		byte[] key = new byte[fis.available()];
		fis.read(key);
		fis.close();
		return key;
	}

	/**
	 * ��������Կ�ԣ�ʹ��Ĭ�ϳ���1024λ
	 * 
	 * @return KeyPair ��Կ��
	 * @throws Exception
	 */
	public static KeyPair generateNewKeyPair() throws Exception {
		return generateNewKeyPair(KEY_LENGTH_1024);
	}
	
	/**
	 * ��������Կ�ԣ���ָ����Կ����
	 * 
	 * @param keyLength
	 * 				 ��Կ����96-2048
	 * @return KeyPair 
	 * 				 ��Կ��
	 * @throws Exception
	 */
	public static KeyPair generateNewKeyPair(int keyLength) throws Exception {

		KeyPairGenerator keyPairGen = KeyPairGenerator
				.getInstance(KEY_ALGORITHM);
		String random = "" + System.currentTimeMillis();
		keyPairGen.initialize(keyLength,  new SecureRandom(random.getBytes()));
		KeyPair keyPair = keyPairGen.generateKeyPair();

//		// ��Կ
//		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
//
//		// ˽Կ
//		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

		return keyPair;
	}
	
	/**
	 * ����Կ�Ա��浽ָ���ļ���
	 * 
	 * @param keyPair 
	 * 				Ҫ�������Կ��
	 * @param publicKeyPath 
	 * 				��Կ����·��
	 * @param privateKeyPath 
	 * 				˽Կ����·��
	 * @throws IOException
	 * @throws Exception
	 */
	
	public static void saveKey(KeyPair keyPair, String publicKeyPath,   
            String privateKeyPath) throws IOException, Exception {   
        PublicKey pubkey = keyPair.getPublic();   
        PrivateKey prikey = keyPair.getPrivate();   
  
        // save public key   
        File pubFile  = new File(publicKeyPath);
        FileOutputStream pubs = new FileOutputStream(pubFile);
        pubs.write(BaseCoder.encryptBASE64(pubkey.getEncoded()));
        pubs.flush();
        pubs.close();
        // save private key   
        File prifile  = new File(privateKeyPath);
        FileOutputStream pris = new FileOutputStream(prifile);
        pris.write(BaseCoder.encryptBASE64(prikey.getEncoded()));
        pris.flush();
        pris.close();  
    }   
	
}
