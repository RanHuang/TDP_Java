package com.nick.tdp.test;

public class Server {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Thread(new SocketServer()).start();
	}

}
