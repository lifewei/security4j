package security4j.util.coders;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
  
/** 
 * DH��ȫ������� 
 *  
 */  
public abstract class DHCoder {
  
    /** 
     * Ĭ����Կ�ֽ��� 
     *  
     * <pre> 
     * DH 
     * Default Keysize 1024   
     * Keysize must be a multiple of 64, ranging from 512 to 1024 (inclusive). 
     * </pre> 
     */  
    private static final int KEY_SIZE = 1024;  
  
    /** 
     * DH��������Ҫһ�ֶԳƼ����㷨�����ݼ��ܣ���������ʹ��DES��Ҳ����ʹ�������ԳƼ����㷨�� 
     */  
    public static final String SECRET_ALGORITHM = "DES";  
  
    /** 
     * ��ʼ���׷���Կ 
     *  
     * @return 
     * @throws Exception 
     */  
    public static KeyPair generateKeyPairA() throws Exception {  
        KeyPairGenerator keyPairGenerator = KeyPairGenerator  
                .getInstance("DH");  
        keyPairGenerator.initialize(KEY_SIZE);  
  
        KeyPair keyPair = keyPairGenerator.generateKeyPair();  
//        // �׷���Կ  
//        DHPublicKey publicKey = (DHPublicKey) keyPair.getPublic();  
//        // �׷�˽Կ  
//        DHPrivateKey privateKey = (DHPrivateKey) keyPair.getPrivate();  
        return keyPair;  
    }  
  
    /** 
     * ��ʼ���ҷ���Կ 
     *  
     * @param key 
     *            �׷���Կ 
     * @return 
     * @throws Exception 
     */  
    public static KeyPair generateKeyPairB(byte[] key) throws Exception {  
        // �����׷���Կ  
        byte[] keyBytes = BaseCoder.decryptBASE64(key);  
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance("DH");  
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);  
  
        // �ɼ׷���Կ�����ҷ���Կ  
        DHParameterSpec dhParamSpec = ((DHPublicKey) pubKey).getParams();  
  
        KeyPairGenerator keyPairGenerator = KeyPairGenerator  
                .getInstance(keyFactory.getAlgorithm());  
        keyPairGenerator.initialize(dhParamSpec);  
  
        KeyPair keyPair = keyPairGenerator.generateKeyPair();  
  
//        // �ҷ���Կ  
//        DHPublicKey publicKey = (DHPublicKey) keyPair.getPublic();  
//        // �ҷ�˽Կ  
//        DHPrivateKey privateKey = (DHPrivateKey) keyPair.getPrivate();   
  
        return keyPair;  
    }  
  
    /** 
     * ����<br> 
     *  
     * @param data 
     *            ���������� 
     * @param publicKey 
     *            ��(��)����Կ 
     * @param privateKey 
     *            �ң��ף���˽Կ 
     * @return 
     * @throws Exception 
     */  
    public static byte[] encrypt(byte[] data, byte[] publicKey,  
    		byte[] privateKey) throws Exception {  
  
        // ���ɱ�����Կ  
        SecretKey secretKey = getSecretKey(publicKey, privateKey);  
  
        // ���ݼ���  
        Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());  
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);  
  
        return cipher.doFinal(data);  
    }  
  
    /** 
     * ����<br> 
     *  
     * @param data 
     *            ���������� 
     * @param publicKey 
     *            �ң��ף�����Կ 
     * @param privateKey 
     *            �ף��ң���˽Կ 
     * @return 
     * @throws Exception 
     */  
    public static byte[] decrypt(byte[] data, byte[] publicKey,  
    		byte[] privateKey) throws Exception {  
  
        // ���ɱ�����Կ  
        SecretKey secretKey = getSecretKey(publicKey, privateKey);  
        // ���ݽ���  
        Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());  
        cipher.init(Cipher.DECRYPT_MODE, secretKey);  
  
        return cipher.doFinal(data);  
    }  
  
    /** 
     * ������Կ 
     *  
     * @param publicKey 
     *            ��Կ 
     * @param privateKey 
     *            ˽Կ 
     * @return 
     * @throws Exception 
     */  
    private static SecretKey getSecretKey(byte[] publicKey, byte[] privateKey)  
            throws Exception {  
        // ��ʼ����Կ  
        byte[] pubKeyBytes = BaseCoder.decryptBASE64(publicKey);  
  
        KeyFactory keyFactory = KeyFactory.getInstance("DH");  
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pubKeyBytes);  
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);  
  
        // ��ʼ��˽Կ  
        byte[] priKeyBytes = BaseCoder.decryptBASE64(privateKey);  
  
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(priKeyBytes);  
        Key priKey = keyFactory.generatePrivate(pkcs8KeySpec);  
  
        KeyAgreement keyAgree = KeyAgreement.getInstance(keyFactory  
                .getAlgorithm());  
        keyAgree.init(priKey);  
        keyAgree.doPhase(pubKey, true);  
  
        // ���ɱ�����Կ  
        SecretKey secretKey = keyAgree.generateSecret(SECRET_ALGORITHM);  
  
        return secretKey;  
    }  
  
    /** 
     * ȡ��δ��BASE64�������Կ��Ϣ 
     *  
     * @param keyPath 
     * @return 
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
