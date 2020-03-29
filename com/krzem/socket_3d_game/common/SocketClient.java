package com.krzem.socket_3d_game.common;



import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.Exception;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;



public abstract class SocketClient{
	private Socket s;
	private BufferedReader inp;
	private PrintWriter otp;
	private List<Packet> _o_l;
	private List<Packet> _i_l;
	private boolean _dc=false;



	public SocketClient(Socket s){
		this.s=s;
		this._setup_io();
	}



	public void send(Packet p){
		synchronized (this._o_l){
			this._o_l.add(p);
		}
	}



	public Packet recive(){
		if (this._i_l.size()==0){
			return null;
		}
		return this._i_l.remove(0);
	}



	public Packet get(){
		if (this._i_l.size()==0){
			return null;
		}
		return this._i_l.get(0);
	}



	public void disconnect(){
		this._dc=true;
		try{
			this.s.close();
		}
		catch (Exception e){
			e.printStackTrace();
		}
		this.on_disconnect();
	}



	public abstract void on_connect();



	public abstract void on_disconnect();



	public abstract boolean on_message(Packet p);



	private void _setup_io(){
		try{
			this.inp=new BufferedReader(new InputStreamReader(this.s.getInputStream()));
			this.otp=new PrintWriter(this.s.getOutputStream(),true);
			this._o_l=new ArrayList<Packet>();
			this._i_l=new ArrayList<Packet>();
			SocketClient cls=this;
			new Thread(new Runnable(){
				public void run(){
					try{
						while (cls._dc==false){
							while (cls._o_l.size()==0){
								if (cls._dc==true){
									return;
								}
								try{
									Thread.sleep(1);
								}
								catch (Exception e){
									e.printStackTrace();
								}
							}
							synchronized (cls._o_l){
								cls.otp.println(cls._o_l.remove(0)._build());
							}
						}
					}
					catch (Exception e){
						if (e.getMessage()==null||(!e.getMessage().equals("Connection reset")&&!e.getMessage().equals("socket closed"))){
							e.printStackTrace();
							System.out.println(cls._o_l);
						}
					}
					if (cls._dc==false){
						cls._dc=true;
						cls.on_disconnect();
					}
					try{
						cls.s.close();
					}
					catch (Exception e){
						e.printStackTrace();
					}
				}
			}).start();
			new Thread(new Runnable(){
				public void run(){
					try{
						while (true){
							Packet p=Packet.decode(cls.inp.readLine());
							if (cls.on_message(p)==true){
								cls._i_l.add(p);
							}
						}
					}
					catch (Exception e){
						if (!e.getMessage().equals("Connection reset")&&!e.getMessage().equals("socket closed")){
							e.printStackTrace();
						}
					}
					if (cls._dc==false){
						cls._dc=true;
						cls.on_disconnect();
					}
					try{
						cls.s.close();
					}
					catch (Exception e){
						e.printStackTrace();
					}
				}
			}).start();
			this.on_connect();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
}