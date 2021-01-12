package com.krzem.socket_3d_game.client;



import com.krzem.socket_3d_game.common.ItemObject;
import com.krzem.socket_3d_game.common.Packet;
import java.awt.Graphics2D;



public class Hotbar extends Constants{
	public Main.Main_ cls;
	public Game g;
	public int c;
	public ItemObject[] dt;
	private boolean _sd=false;



	public Hotbar(Main.Main_ cls,Game g){
		this.cls=cls;
		this.g=g;
		this.c=0;
		this.dt=new ItemObject[5];
	}



	public void update(){
		if (this.cls.KEYBOARD.pressed(32)==true){
			if (this._sd==false){
				this.g.c.send(new Packet(Packet.PACKET.CLIENT.PLAYER_ITEM_ACTION_REQUEST_PACKET));
			}
			this._sd=true;
		}
		else{
			this._sd=false;
		}
		for (int i=4;i>=0;i--){
			if (this.cls.KEYBOARD.pressed(49+i)==true&&this.c!=i){
				this.c=i;
				Packet ph=new Packet(Packet.PACKET.CLIENT.PLAYER_HOTBAR_CURSOR_MOVEMENT_REQUEST_PACKET);
				ph.set("c",Integer.toString(i));
				this.g.c.send(ph);
				break;
			}
		}
	}



	public void draw(Graphics2D g){

	}



	public void from_packet(Packet p){
		this.c=Integer.parseInt(p.get("c"));
		for (int i=0;i<5;i++){
			this.dt[i]=ItemObject.from_packet(p.get(String.format("s%d",i)));
		}
	}
}