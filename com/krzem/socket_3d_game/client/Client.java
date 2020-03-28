package com.krzem.socket_3d_game.client;



import com.krzem.socket_3d_game.common.Packet;
import com.krzem.socket_3d_game.common.SocketClient;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.Exception;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;



public class Client extends Constants{
	public Main.Main_ cls;
	public Game g;
	private SocketClient s;



	public Client(Main.Main_ cls,Game g){
		this.cls=cls;
		this.g=g;
		try{
			this.s=new SocketClient(new Socket(SERVER_URL.split(":")[0],Integer.parseInt(SERVER_URL.split(":")[1]))){
				@Override
				public void on_connect(){
					System.out.println("Connected!");
				}



				@Override
				public void on_disconnect(){
					System.out.println("Disconnected :(");
					System.exit(1);
				}



				@Override
				public boolean on_message(Packet p){
					return true;
				}
			};
		}
		catch (Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}



	public void send(Packet p){
		this.s.send(p);
	}



	public Packet recive(){
		return this.s.recive();
	}
}