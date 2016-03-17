package com.nick.tdp.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketClient implements Runnable {

	
	private static final String TAG = "Socket Client";

    private String _serverHost;
    private int _serverPort;

    public SocketClient(String  serverHost_){
        _serverHost = serverHost_;
        _serverPort = SocketServer.PORT;
    }

    @Override
    public void run(){
    	System.err.println(TAG + ":" + "Start Client Socket.");
        try {
            /**
             * The client thread sleep for a while before connecting to the remote server, 
             * waiting for the server to get ready.
             */
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        Socket socket = new Socket();
        try{
            /**
             * Create a client socket.
             */
            socket.bind(null);
            socket.connect(new InetSocketAddress(_serverHost, _serverPort), 0);
            System.err.println(TAG + ":" + "Connect to server");
        }catch (IOException e){
            System.err.println(TAG + ":" + "Exception when sending message...");
            e.printStackTrace();
        }catch (NumberFormatException e){
        	System.err.println(TAG + ":" + "Exception when sending message...");
        	e.printStackTrace();
        }
        //Start the top Application
        System.err.println(TAG + ":" + "Start the EncryptionController Thread.");
        
        
        new Thread(new DevicePairing(socket, false)).start(); 

    }
}

