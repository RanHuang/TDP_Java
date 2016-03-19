package com.nick.tdp.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Nick on 2015/7/3.
 */
public class SocketServer implements Runnable{

    public static final int PORT = 7748;

    private ServerSocket _serverSocket;

    public SocketServer(){
        _serverSocket = null;
    }

    @Override
    public void run(){
        //Create the listening socket.
        ServerSocket serverSocket = null;
        try{
            serverSocket = new ServerSocket(SocketServer.PORT);
        }catch (IOException e){
            e.printStackTrace();
        }

        _serverSocket = serverSocket;

        System.out.println("Socket Server is waiting for incoming connections.");
        //Listening
            Socket socket = null;
            try{
                socket = _serverSocket.accept();
            }catch (IOException e){
                e.printStackTrace();
            }

            if(socket != null && socket.isConnected()){
                //Start the top Application
                new Thread(new DevicePairing(socket, true)).start();
            }


        try{
            System.out.println("server thread is stopped");
            _serverSocket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    
    /**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Thread(new SocketServer()).start();
	}
}

