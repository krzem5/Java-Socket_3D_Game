package com.krzem.socket_3d_game;



import com.krzem.socket_3d_game.common.ItemObject;
import java.util.HashMap;
import java.util.Map;



public class Item extends Constants{
	public static final Map<Integer,int[]> GUN_DATA=new HashMap<Integer,int[]>(){{
		this.put(1,new int[]{5,2000});
	}};



	public Game g;
	public Player p;
	public ItemObject o;
	public int bc;
	public long rt;
	private int _lp=-1;



	public Item(Game g,Player p,int id){
		this.g=g;
		this.p=p;
		this.o=new ItemObject(id);
		this.bc=-1;
		this.rt=-1;
		if (Item.GUN_DATA.get((int)this.o.id)!=null){
			this.bc=Item.GUN_DATA.get((int)this.o.id)[0]+0;
		}
		this.o.set("bc",Integer.toString(this.bc));
		this.o.set("p","-1");
	}



	public boolean update(){
		if (this.rt!=-1){
			if (System.nanoTime()>=this.rt){
				this.rt=-1;
				this._lp=-1;
				this.bc=Item.GUN_DATA.get((int)this.o.id)[0]+0;
				this.o.set("bc",Integer.toString(this.bc));
				this.o.set("p","-1");
				return true;
			}
			if (100-(int)((double)(this.rt-System.nanoTime())*1e-6/(double)Item.GUN_DATA.get((int)this.o.id)[1]*100d)>this._lp){
				this._lp=100-(int)((double)(this.rt-System.nanoTime())*1e-6/(double)Item.GUN_DATA.get((int)this.o.id)[1]*100d);
				this.o.set("p",Integer.toString(this._lp));
				return true;
			}
		}
		return false;
	}



	public boolean use(){
		if (this.o.id==ItemObject.ITEM.EMPTY||this.bc==-1){
			return false;
		}
		if (this.bc>0){
			this.bc--;
			this.o.set("bc",Integer.toString(this.bc));
			if (this.bc==0){
				this.rt=System.nanoTime()+(long)(Item.GUN_DATA.get((int)this.o.id)[1]*1e6);
			}
			return true;
		}
		return false;
	}



	public String to_packet(){
		return this.o.to_packet();
	}
}