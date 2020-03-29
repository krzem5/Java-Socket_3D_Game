package com.krzem.socket_3d_game;



import com.krzem.socket_3d_game.common.Packet;
import com.krzem.socket_3d_game.common.SocketClient;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;



public class Client extends Constants{
	public Server sc;
	public SocketClient s;
	public Game g;
	public String id;



	public Client(Server sc,Socket s){
		this.sc=sc;
		this.g=null;
		this.id=this._gen_id();
		Client cls=this;
		this.s=new SocketClient(s){
			@Override
			public void on_connect(){
				System.out.printf("Client#%s connected\n",cls.id);
				Packet p=new Packet(Packet.PACKET.SERVER.SHA256_REQUEST_PACKET);
				p.set("id",cls.id);
				this.send(p);
			}



			@Override
			public void on_disconnect(){
				System.out.printf("Client#%s disconnected\n",cls.id);
				if (cls.g!=null){
					cls.g.remove(cls);
				}
			}



			@Override
			public boolean on_message(Packet p){
				return cls._msg(p);
			}
		};
	}



	private boolean _msg(Packet p){
		switch (p.id){
			case Packet.PACKET.CLIENT.SHA256_RESPONSE_PACKET:
				// if (!p.get("sha256").equals(CLIENT_CHECKSUM)){
				// 	this.s.disconnect();
				// }
				System.out.println("CLIENT HASH: "+p.get("sha256"));
				Game.add(this);
				return false;
		}
		return true;
	}



	private String _gen_id(){
		return Cryptography._md5(Cryptography._gen_rand_string(CLIENT_RANDOM_ID_LENGTH)+Long.toString(System.nanoTime()));
	}
}