package com.krzem.socket_3d_game.common;



import java.util.Base64;
import java.util.HashMap;
import java.util.Map;



public class ItemObject{
	public static final class ITEM{
		public static final int EMPTY=0;
		public static final int BASIC_PISTOL=1;
	}



	public byte id;
	private Map<String,String> _dt;



	public ItemObject(int id){
		this.id=(byte)id;
		this._dt=new HashMap<String,String>();
	}



	public void set(String k,String v){
		this._dt.put(k,v);
	}



	public String get(String k){
		return this._dt.get(k);
	}



	public String to_packet(){
		String o="";
		for (Map.Entry<String,String> e:this._dt.entrySet()){
			o+=String.format(",%s:%s",this._base64_encode(e.getKey()),this._base64_encode(e.getValue()));
		}
		return this._base64_encode(new String(new byte[]{this.id})+(o.length()>0?o.substring(1):""));
	}



	public static ItemObject from_packet(String dt_s){
		byte[] dt=ItemObject._base64_decode(dt_s).getBytes();
		ItemObject o=new ItemObject(dt[0]);
		String dt_m=new String(dt).substring(1);
		for (String e:dt_m.split(",")){
			if (e.split(":").length!=2){
				continue;
			}
			o.set(ItemObject._base64_decode(e.split(":")[0]),ItemObject._base64_decode(e.split(":")[1]));
		}
		return o;
	}



	public static String _base64_decode(String s){
		return new String(Base64.getDecoder().decode(s));
	}



	private String _base64_encode(String s){
		return Base64.getEncoder().encodeToString(s.getBytes());
	}
}