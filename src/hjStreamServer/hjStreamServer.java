package hjStreamServer;/*
* hjStreamServer.java 
* Streaming server: streams video frames in UDP packets
* for clients to play in real time the transmitted movies
*/

import security.MySRTSPDatagramSocket;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.*;
import java.util.Properties;

import static security.encryption.KeyManager.*;

class hjStreamServer {

	static public void main( String []args ) throws Exception {
	        if (args.length != 3)
	        {
                   System.out.println("Error, use: mySend <movie> <ip-multicast-address> <port>");
	           System.out.println("        or: mySend <movie> <ip-unicast-address> <port>");
	           System.exit(-1);
                }
      
		int size;
		int csize = 0;
		int count = 0;
 		long time;
		DataInputStream g = new DataInputStream( new FileInputStream(args[0]) );
		byte[] buff = new byte[4096];

		MySRTSPDatagramSocket s = new MySRTSPDatagramSocket();
		InetSocketAddress addr = new InetSocketAddress( args[1], Integer.parseInt(args[2]));
		DatagramPacket p = new DatagramPacket(buff, buff.length, addr );
		long t0 = System.nanoTime(); // Ref. time 
		long q0 = 0;

		//Get keys
		SecretKey[] key = getKeys();

		//Get algorithm parameter from configuration file
		String algorithm = getParameters()[0];

		while ( g.available() > 0 ) {
		    
		    size = g.readShort(); // size of the frame
		    csize=csize+size;
		    time = g.readLong();  // timestamp of the frame
			if ( count == 0 ) q0 = time; // ref. time in the stream
			count += 1;
			g.readFully(buff, 0, size );

			long t = System.nanoTime(); // what time is it?

			// Decision about the right time to transmit
			Thread.sleep( Math.max(0, ((time-q0)-(t-t0))/1000000));

			p.setSocketAddress( addr );

			//s.mySend(p,buff,size,key[0],key[1],properties.getProperty("algorithm"));
			s.mySend(p,buff,size,key[0],key[1],algorithm);

	           	// Just for awareness ... (debug)

			System.out.print( "." );
		}

		long tend = System.nanoTime(); // "The end" time 
                System.out.println();
		System.out.println("DONE! all frames sent: "+ count);

		long duration=(tend-t0)/1000000000;
		System.out.println("Movie duration "+ duration + " s");
		System.out.println("Throughput "+ count/duration + " fps");
     	        System.out.println("Throughput "+ (8*(csize)/duration)/1000 + " Kbps");

	}
}



