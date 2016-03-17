package com.nick.tdp.test;

public class Client {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Thread(new SocketClient("localhost")).start();
	}

}
