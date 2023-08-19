package security;

import security.encryption.EncryptPayload;

import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.net.*;
import java.nio.ByteBuffer;

import static java.util.Arrays.copyOfRange;

public class MySRTSPDatagramSocket extends DatagramSocket {
    public MySRTSPDatagramSocket() throws SocketException {
        super();
    }
    public MySRTSPDatagramSocket(SocketAddress bindaddr) throws SocketException {
        super(bindaddr);
    }
    public void mySend(DatagramPacket p, byte[] buff, int ptSize, SecretKey key, SecretKey hMacKey, String algorithm) throws Exception {
        byte[] fixedheader = {0b00010000};

        //Encryption here
        EncryptPayload encryptPayload = new EncryptPayload();
        byte[] encryptedPayload = encryptPayload.encrypt(buff,ptSize,key,hMacKey,algorithm);

        //Merging everything
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write(fixedheader);
        outputStream.write(encryptedPayload);
        byte[] frame = outputStream.toByteArray();

        p.setData(frame, 0, frame.length);
        super.send(p);
    }
    public byte[] myReceive(DatagramPacket inPacket, SecretKey key, SecretKey hMacKey, String algorithm) throws Exception {
        super.receive(inPacket);

        //Get the full packet
        byte[] packet = inPacket.getData();
        byte[] ctSizeHeader = copyOfRange(packet,1,3);
        int ctSize = ByteBuffer.wrap(ctSizeHeader).getShort();

        byte[] encryptedBuff = copyOfRange(packet,3,packet.length);

        //Decription here
        EncryptPayload encryptPayload = new EncryptPayload();
        return encryptPayload.decrypt(ctSize,encryptedBuff,key,hMacKey,algorithm);
    }
}
