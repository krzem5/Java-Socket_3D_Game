package com.krzem.socket_3d_game.client;



import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;



public class Keyboard extends Constants{
	public Main.Main_ cls;
	private Map<Integer,Integer> kl;
	private boolean clear=true;



	public Keyboard(Main.Main_ cls){
		this.cls=cls;
		this.kl=new HashMap<Integer,Integer>();
	}



	public void down(KeyEvent e){
		while (this.clear==false){

		}
		this.kl.put(e.getKeyCode(),1);
	}



	public void up(KeyEvent e){
		while (this.clear==false){

		}
		this.kl.remove(e.getKeyCode());
	}



	public void press(KeyEvent e){
		while (this.clear==false){

		}
		this.kl.put(e.getKeyCode(),2);
	}



	public void update(){
		this.clear=false;
		int[] r=new int[this.kl.size()];
		int idx=0;
		for (int i:this.kl.keySet()){
			if (this.kl.get(i)==2){
				r[idx]=i;
				idx++;
			}
		}
		for (int j=0;j<idx;j++){
			this.kl.remove(r[j]);
		}
		this.clear=true;
	}



	public boolean pressed(int k){
		if (this.kl.get(k)==null){
			return false;
		}
		return (this.kl.get(k)>0);
	}
}