package com.krzem.socket_3d_game;



public class Main{
	public static void main(String[] args){
		new Main(args);
	}



	public Main(String[] args){
		new Server(args[0]);
	}
}