package security4j.util.coders;

import java.io.IOException;

import javax.crypto.SecretKey;


public class TestCoder {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {

			TestCoder coder = new TestCoder();
			coder.testPBE(PBECoder.PBEWithSHA1AndRC2_40);
 	        
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
        System.err.println("ԭ��:\t" + inputStr);  
        byte[] keyData = DESCoder.getKey("D:/des.dat");
        System.err.println("��Կ:\t" + keyData);  
  
        byte[] inputData = inputStr.getBytes();  
        inputData = DESCoder.encrypt(inputData, keyData, algorithm);  
  
        System.err.println("���ܺ�:\t" + BaseCoder.encryptBASE64(inputData));  
  
        byte[] outputData = DESCoder.decrypt(inputData, keyData, algorithm);  
        String outputStr = new String(outputData);  
  
        System.err.println("���ܺ�:\t" + outputStr); 		
	}
	
	//����PBE�㷨
	public void testPBE(String algorithm) throws Exception {

        String inputStr = "abc";  
        System.err.println("ԭ��: " + inputStr);  
        byte[] input = inputStr.getBytes();  
  
        String pwd = "efg";  
        System.err.println("����: " + pwd);  
  
        byte[] salt = PBECoder.initSalt();  
  
        byte[] data = PBECoder.encrypt(input, pwd, salt, algorithm);  
  
        System.err.println("���ܺ�: " + BaseCoder.encryptBASE64(data));  
  
        byte[] output = PBECoder.decrypt(data, pwd, salt,algorithm);  
        String outputStr = new String(output);  
  
        System.err.println("���ܺ�: " + outputStr); 		
	}
	
	//����RSA�㷨
	public void testRSA() throws Exception {
		RSACoder.saveKey(RSACoder.generateNewKeyPair(), "D/publicKey.dat", "D/privateKey.dat");
		testPri2Pub();
		testPub2Pri();
	}
	
	private void testPub2Pri() throws Exception {
	    System.err.println("\n��Կ���ܡ���˽Կ����");  
        String inputStr = "";
        for (int i = 0; i < 1024; i++) {
			inputStr += "a";
		}
        byte[] data = inputStr.getBytes();  
  
        byte[] encodedData = RSACoder.encryptByPublicKey(data, RSACoder.getPublicKey("D:/publicKey.dat"));  
  
        byte[] decodedData = RSACoder.decryptByPrivateKey(encodedData,  
                RSACoder.getPrivateKey("D:/privateKey.dat"));  
  
        String outputStr = new String(decodedData);  
        System.err.println("����ǰ: " + inputStr + "\n" + "���ܺ�: " + outputStr); 		
	}
	
	private void testPri2Pub() throws Exception {
        System.err.println("\n˽Կ���ܡ�����Կ����"); 
        String inputStr = ""; 
        for (int i = 0; i < 1024; i++) {
			inputStr += "a";
		}
        byte[] data = inputStr.getBytes();  
        byte[] encodedData = RSACoder.encryptByPrivateKey(data, RSACoder.getPrivateKey("D:/privateKey.dat"));  
        byte[] decodedData = RSACoder  
                .decryptByPublicKey(encodedData, RSACoder.getPublicKey("D:/publicKey.dat"));  
  
        String outputStr = new String(decodedData);  
        System.err.println("����ǰ: " + inputStr + "\n" + "���ܺ�: " + outputStr);  
  
        System.err.println("˽Կǩ��������Կ��֤ǩ��");  
        // ����ǩ��  
        String sign = new String(RSACoder.sign(encodedData, RSACoder.getPrivateKey("D:/privateKey.dat")));  
        System.err.println("ǩ��:\r" + sign);  
  
        // ��֤ǩ��  
        boolean status = RSACoder.verify(encodedData, RSACoder.getPublicKey("D:/publicKey.dat"), sign.getBytes());  
        System.err.println("״̬:\r" + status); 		
	}

}
