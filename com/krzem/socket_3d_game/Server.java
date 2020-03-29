package com.krzem.socket_3d_game;



import java.lang.Exception;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;



public class Server{
	public int p;
	public ServerSocket ss;
	public List<Client> cl;



	public Server(String p){
		this.p=Integer.parseInt(p);
		this.cl=new ArrayList<Client>();
		try{
			this.ss=new ServerSocket(this.p);
			System.out.println(String.format("Server started on port %d.",this.p));
			while (true){
				this.cl.add(new Client(this,ss.accept()));
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
}