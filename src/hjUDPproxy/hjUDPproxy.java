package hjUDPproxy;

import security.MySRTSPDatagramSocket;

import javax.crypto.SecretKey;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import static security.encryption.KeyManager.*;

class hjUDPproxy {
    public static void main(String[] args) throws Exception {
        InputStream inputStream = new FileInputStream("src/hjUDPproxy/config.properties");
        if (inputStream == null) {
            System.err.println("Configuration file not found!");
            System.exit(1);
        }
        Properties properties = new Properties();
        properties.load(inputStream);
	String remote = properties.getProperty("remote");
        String destinations = properties.getProperty("localdelivery");

        //Load crypto properties
        inputStream = new FileInputStream("src/security/configSecurity.properties");
        if (inputStream == null) {
            System.err.println("Configuration file not found!");
            System.exit(1);}
        properties = new Properties();
        properties.load(inputStream);

        SocketAddress inSocketAddress = parseSocketAddress(remote);
        Set<SocketAddress> outSocketAddressSet = Arrays.stream(destinations.split(",")).map(s -> parseSocketAddress(s)).collect(Collectors.toSet());
        MySRTSPDatagramSocket inSocket = new MySRTSPDatagramSocket(inSocketAddress);
        DatagramSocket outSocket = new DatagramSocket();
        byte[] buffer = new byte[4 * 1024];

        //Get keys
        SecretKey[] key = getKeys(properties.getProperty("keyStorePath"),properties.getProperty("keyStorePass"),properties.getProperty("algorithm"));
       
        while (true) {
          DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
 	  byte[] ptData = inSocket.myReceive(inPacket,key[0],key[1],properties.getProperty("algorithm"));

          System.out.print("*");
          for (SocketAddress outSocketAddress : outSocketAddressSet) 
            {
                outSocket.send(new DatagramPacket(ptData, ptData.length, outSocketAddress));
            }
        }
    }

    private static InetSocketAddress parseSocketAddress(String socketAddress) 
    {
        String[] split = socketAddress.split(":");
        String host = split[0];
        int port = Integer.parseInt(split[1]);
        return new InetSocketAddress(host, port);
    }
}
