package security.encryption;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.security.*;



public class EncryptPayload {
    public byte[] encrypt(byte[] data, int ptSize, SecretKey keyFound, SecretKey hMacKeyFound, String algorithm) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);

        //Initialization vector generation
        SecureRandom randomSecureRandom = new SecureRandom();
        byte[] iv = new byte[cipher.getBlockSize()];
        randomSecureRandom.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        //HMac generation
        Mac hMac = Mac.getInstance("HmacSHA512");

        //Cyphering
        cipher.init(Cipher.ENCRYPT_MODE, keyFound, ivSpec);

        byte[] cipherText = new byte[cipher.getOutputSize(ptSize+hMac.getMacLength())];
        int ctSize = cipher.update(data, 0, ptSize, cipherText, 0);
        hMac.init(hMacKeyFound);
        hMac.update(data, 0, ptSize);
        ctSize += cipher.doFinal(hMac.doFinal(), 0, hMac.getMacLength(), cipherText, ctSize);

        //Merging ciphertext size, iv and ciphertext
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );

        outputStream.write(ByteBuffer.allocate(Short.BYTES).putShort((short)ctSize).array());
        outputStream.write(iv);
        outputStream.write(cipherText);

        //Returning the iv+encrypted byte array
        return outputStream.toByteArray();
    }
    public byte[] decrypt(int ctSize, byte[] data, SecretKey keyFound, SecretKey hMacKeyFound, String algorithm) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);

        //Splitting array into iv and ciphertext
        byte[] iv = java.util.Arrays.copyOfRange(data, 0, cipher.getBlockSize());
        byte[] cipherText = java.util.Arrays.copyOfRange(data, cipher.getBlockSize(), cipher.getBlockSize() + ctSize);

        //Setting the ivSpec
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        //HMac generation
        Mac hMac = Mac.getInstance("HmacSHA512");

        //Decrypt the ciphertext
        cipher.init(Cipher.DECRYPT_MODE, keyFound, ivSpec);
        byte[] plainText = cipher.doFinal(cipherText, 0, ctSize);

        int messageLength = plainText.length - hMac.getMacLength();
        hMac.init(hMacKeyFound);
        hMac.update(plainText, 0, messageLength);

        byte[] messageHash = new byte[hMac.getMacLength()];
        System.arraycopy(plainText, messageLength, messageHash, 0, messageHash.length);

        //Send the plainText back, checking the integrity with the Mac
        if(MessageDigest.isEqual(hMac.doFinal(),messageHash))
            return java.util.Arrays.copyOfRange(plainText, 0, messageLength);
        else
            return new byte[0];
    }
}
