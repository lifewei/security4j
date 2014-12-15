package security4j.util.coders;

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
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 *  ����ǩ���㷨
 *
 */
public class DSACoder { 
    /** 
     * Ĭ����Կ�ֽ��� 
     *  
     * <pre> 
     * DSA  
     * Default Keysize 1024   
     * Keysize must be a multiple of 64, ranging from 512 to 1024 (inclusive). 
     * </pre> 
     */  
    private static final int KEY_SIZE = 1024;  
  
    /** 
     * ��˽Կ����Ϣ��������ǩ�� 
     *  
     * @param data 
     *            �������� 
     * @param privateKey 
     *            ˽Կ 
     *  
     * @return 
     * @throws Exception 
     */  
    public static byte[] sign(byte[] data, byte[] privateKey) throws Exception {  
        // ������base64�����˽Կ  
        byte[] keyBytes = BaseCoder.decryptBASE64(privateKey);  
  
        // ����PKCS8EncodedKeySpec����  
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);  
  
        // KEY_"DSA" ָ���ļ����㷨  
        KeyFactory keyFactory = KeyFactory.getInstance("DSA");  
  
        // ȡ˽Կ�׶���  
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);  
  
        // ��˽Կ����Ϣ��������ǩ��  
        Signature signature = Signature.getInstance(keyFactory.getAlgorithm());  
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
     *            ��Կ 
     * @param sign 
     *            ����ǩ�� 
     *  
     * @return У��ɹ�����true ʧ�ܷ���false 
     * @throws Exception 
     *  
     */  
    public static boolean verify(byte[] data, byte[] publicKey, String sign)  
            throws Exception {  
  
        // ������base64����Ĺ�Կ  
        byte[] keyBytes = BaseCoder.decryptBASE64(publicKey);  
  
        // ����X509EncodedKeySpec����  
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);  
  
        // "DSA" ָ���ļ����㷨  
        KeyFactory keyFactory = KeyFactory.getInstance("DSA");  
  
        // ȡ��Կ�׶���  
        PublicKey pubKey = keyFactory.generatePublic(keySpec);  
  
        Signature signature = Signature.getInstance(keyFactory.getAlgorithm());  
        signature.initVerify(pubKey);  
        signature.update(data);  
  
        // ��֤ǩ���Ƿ�����  
        return signature.verify(BaseCoder.decryptBASE64(sign));  
    }  
  
    /** 
     * ������Կ 
     *  
     * @param seed 
     *            ���� 
     * @return ��Կ�� 
     * @throws Exception 
     */  
    public static KeyPair generateKeyPair() throws Exception {  
        KeyPairGenerator keygen = KeyPairGenerator.getInstance("DSA");  
        // ��ʼ�����������  
        String seed = "" + System.currentTimeMillis();
        SecureRandom secureRandom = new SecureRandom();  
        secureRandom.setSeed(seed.getBytes());  
        keygen.initialize(KEY_SIZE, secureRandom);  
  
        KeyPair keys = keygen.genKeyPair();  
  
//        DSAPublicKey publicKey = (DSAPublicKey) keys.getPublic();  
//        DSAPrivateKey privateKey = (DSAPrivateKey) keys.getPrivate();   
  
        return keys;  
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
