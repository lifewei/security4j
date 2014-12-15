package security4j.util.coders;

import java.io.IOException;
import java.security.KeyPair;

import javax.crypto.SecretKey;


public class TestCoder {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {

			TestCoder coder = new TestCoder();
			coder.testDSA();
 	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// ����DES�㷨
	public void testDES(String algorithm) throws Exception {

		SecretKey key = DESCoder.generateKey(algorithm);
		DESCoder.saveKey(key, "D:/des.dat");
        String inputStr = "";
        for (int i = 0; i < 1024; i++) {
			inputStr += "a";
		}
        System.out.println("ԭ��:\t" + inputStr);  
        byte[] keyData = DESCoder.getKey("D:/des.dat");
        System.out.println("��Կ:\t" + keyData);  
  
        byte[] inputData = inputStr.getBytes();  
        inputData = DESCoder.encrypt(inputData, keyData, algorithm);  
  
        System.out.println("���ܺ�:\t" + BaseCoder.encryptBASE64(inputData));  
  
        byte[] outputData = DESCoder.decrypt(inputData, keyData, algorithm);  
        String outputStr = new String(outputData);  
  
        System.out.println("���ܺ�:\t" + outputStr); 		
	}
	
	//����PBE�㷨
	public void testPBE(String algorithm) throws Exception {

        String inputStr = "abc";  
        System.out.println("ԭ��: " + inputStr);  
        byte[] input = inputStr.getBytes();  
  
        String pwd = "efg";  
        System.out.println("����: " + pwd);  
  
        byte[] salt = PBECoder.initSalt();  
  
        byte[] data = PBECoder.encrypt(input, pwd, salt, algorithm);  
  
        System.out.println("���ܺ�: " + BaseCoder.encryptBASE64(data));  
  
        byte[] output = PBECoder.decrypt(data, pwd, salt,algorithm);  
        String outputStr = new String(output);  
  
        System.out.println("���ܺ�: " + outputStr); 		
	}
	
	//����RSA�㷨
	public void testRSA() throws Exception {
		RSACoder.saveKey(RSACoder.generateNewKeyPair(), "D/publicKey.dat", "D/privateKey.dat");
		testPri2Pub();
		testPub2Pri();
	}
	
	private void testPub2Pri() throws Exception {
	    System.out.println("\n��Կ���ܡ���˽Կ����");  
        String inputStr = "";
        for (int i = 0; i < 1024; i++) {
			inputStr += "a";
		}
        byte[] data = inputStr.getBytes();  
  
        byte[] encodedData = RSACoder.encryptByPublicKey(data, RSACoder.getPublicKey("D:/publicKey.dat"));  
  
        byte[] decodedData = RSACoder.decryptByPrivateKey(encodedData,  
                RSACoder.getPrivateKey("D:/privateKey.dat"));  
  
        String outputStr = new String(decodedData);  
        System.out.println("����ǰ: " + inputStr + "\n" + "���ܺ�: " + outputStr); 		
	}
	
	private void testPri2Pub() throws Exception {
        System.out.println("\n˽Կ���ܡ�����Կ����"); 
        String inputStr = ""; 
        for (int i = 0; i < 1024; i++) {
			inputStr += "a";
		}
        byte[] data = inputStr.getBytes();  
        byte[] encodedData = RSACoder.encryptByPrivateKey(data, RSACoder.getPrivateKey("D:/privateKey.dat"));  
        byte[] decodedData = RSACoder  
                .decryptByPublicKey(encodedData, RSACoder.getPublicKey("D:/publicKey.dat"));  
  
        String outputStr = new String(decodedData);  
        System.out.println("����ǰ: " + inputStr + "\n" + "���ܺ�: " + outputStr);  
  
        System.out.println("˽Կǩ��������Կ��֤ǩ��");  
        // ����ǩ��  
        String sign = new String(RSACoder.sign(encodedData, RSACoder.getPrivateKey("D:/privateKey.dat")));  
        System.out.println("ǩ��:\r" + sign);  
  
        // ��֤ǩ��  
        boolean status = RSACoder.verify(encodedData, RSACoder.getPublicKey("D:/publicKey.dat"), sign.getBytes());  
        System.out.println("״̬:\r" + status); 		
	}

	public void testDH() throws Exception {
        // ���ɼ׷���Կ�Զ�  
        KeyPair aKeys = DHCoder.generateKeyPairA();  
        String aPublicKey = new String(BaseCoder.encryptBASE64(aKeys.getPublic().getEncoded())); 
        String aPrivateKey = new String(BaseCoder.encryptBASE64(aKeys.getPrivate().getEncoded()));  
  
        System.out.println("�׷���Կ:\r" + aPublicKey);  
        System.out.println("�׷�˽Կ:\r" + aPrivateKey);  
          
        // �ɼ׷���Կ����������Կ�Զ�  
        KeyPair bKeys = DHCoder.generateKeyPairB(aPublicKey.getBytes());  
        String bPublicKey = new String(BaseCoder.encryptBASE64(bKeys.getPublic().getEncoded()));  
        String bPrivateKey = new String(BaseCoder.encryptBASE64(bKeys.getPrivate().getEncoded()));  
          
        System.out.println("�ҷ���Կ:\r" + bPublicKey);  
        System.out.println("�ҷ�˽Կ:\r" + bPrivateKey);  
          
        String aInput = "abc ";  
        System.out.println("ԭ��: " + aInput);  
  
        // �ɼ׷���Կ���ҷ�˽Կ��������  
        byte[] aCode = DHCoder.encrypt(aInput.getBytes(), aPublicKey.getBytes(),  
                bPrivateKey.getBytes());  
  
        // ���ҷ���Կ���׷�˽Կ����  
        byte[] aDecode = DHCoder.decrypt(aCode, bPublicKey.getBytes(), aPrivateKey.getBytes());  
        String aOutput = (new String(aDecode));  
  
        System.out.println("����: " + aOutput);  
  
        System.out.println(" ===============���������ܽ���================== ");  
        String bInput = "def ";  
        System.out.println("ԭ��: " + bInput);  
  
        // ���ҷ���Կ���׷�˽Կ��������  
        byte[] bCode = DHCoder.encrypt(bInput.getBytes(), bPublicKey.getBytes(),  
                aPrivateKey.getBytes());  
  
        // �ɼ׷���Կ���ҷ�˽Կ����  
        byte[] bDecode = DHCoder.decrypt(bCode, aPublicKey.getBytes(), bPrivateKey.getBytes());  
        String bOutput = (new String(bDecode));  
  
        System.out.println("����: " + bOutput);		
	}
	
	public void testDSA() throws Exception {
        String inputStr = "abc";  
        byte[] data = inputStr.getBytes();  
  
        // ������Կ  
        KeyPair keyPair = DSACoder.generateKeyPair();  
  
        // �����Կ  
        String publicKey = new String(BaseCoder.encryptBASE64(keyPair.getPublic().getEncoded()));  
        String privateKey = new String(BaseCoder.encryptBASE64(keyPair.getPrivate().getEncoded()));
  
        System.out.println("��Կ:\r" + publicKey);  
        System.out.println("˽Կ:\r" + privateKey);  
  
        // ����ǩ��  
        String sign = new String(DSACoder.sign(data, privateKey.getBytes()));  
        System.out.println("ǩ��:\r" + sign);  
  
        // ��֤ǩ��  
        boolean status = DSACoder.verify(data, publicKey.getBytes(), sign);  
        System.out.println("״̬:\r" + status);		
	}
}
