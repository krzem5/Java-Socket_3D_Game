package com.krzem.socket_3d_game.common;



import java.util.Base64;
import java.util.HashMap;
import java.util.Map;



public class Packet{
	public static final class PACKET{
		public static final class SERVER{
			public static final int SHA256_REQUEST_PACKET=0;
			public static final int PLAYER_DATA_PACKET=2;
			public static final int PLAYER_LEAVE_PACKET=3;
			public static final int GAME_BOARD_DATA_PACKET=4;
			public static final int GAME_BOARD_DOOR_REMOVE_PACKET=5;
			public static final int PLAYER_POSITION_CHANGE_PACKET=10;
			public static final int PLAYER_HOTBAR_DATA_PACKET=11;
		}
		public static final class CLIENT{
			public static final int SHA256_RESPONSE_PACKET=1;
			public static final int PLAYER_MOVEMENT_REQUEST_PACKET=6;
			public static final int PLAYER_ROTATION_REQUEST_PACKET=7;
			public static final int PLAYER_ITEM_ACTION_REQUEST_PACKET=8;
			public static final int PLAYER_HOTBAR_CURSOR_MOVEMENT_REQUEST_PACKET=9;
		}
	}
	private static Map<Integer,String[]> PACKET_PROPERTY_MAP=new HashMap<Integer,String[]>(){{
		this.put(0,new String[]{"id"});
		this.put(1,new String[]{"sha256"});
		this.put(2,new String[]{"id","name","x","y","z"});
		this.put(3,new String[]{"id"});
		this.put(4,new String[]{"off","w"});
		this.put(5,new String[]{"l"});
		this.put(6,new String[]{"s"});
		this.put(7,new String[]{"a"});
		this.put(8,new String[]{});
		this.put(9,new String[]{"c"});
		this.put(10,new String[]{"id","x","y","z"});
		this.put(11,new String[]{"c","s0","s1","s2","s3","s4"});
	}};



	public byte id=-1;
	private Map<String,String> _m=null;



	public Packet(int id){
		this.id=(byte)id;
		this._m=new HashMap<String,String>();
	}



	public void set(String k,String v){
		this._m.put(k,v);
	}



	public String get(String k){
		return this._m.get(k);
	}



	public String _build(){
		for (String k:Packet.PACKET_PROPERTY_MAP.get((int)this.id)){
			if (this._m.containsKey(k)==false){
				throw new RuntimeException(String.format("Missing packet value '%s' (Id=%d)",k,this.id));
			}
		}
		for (String k:this._m.keySet()){
			boolean c=false;
			for (String k2:Packet.PACKET_PROPERTY_MAP.get((int)this.id)){
				if (k.equals(k2)){
					c=true;
					break;
				}
			}
			if (c==false){
				throw new RuntimeException(String.format("Extra packet value '%s' (Id=%d)",k,this.id));
			}
		}
		String o="";
		for (Map.Entry<String,String> e:this._m.entrySet()){
			o+=String.format(",%s:%s",this._base64_encode(e.getKey()),this._base64_encode(e.getValue()));
		}
		return this._base64_encode(new String(new byte[]{this.id})+(o.length()>0?o.substring(1):""));
	}



	@Override
	public String toString(){
		return String.format("Packet(id=%d, data=%s)",this.id,this._m.toString());
	}



	public static Packet decode(String dt_s){
		byte[] dt=Packet._base64_decode(dt_s).getBytes();
		Packet p=new Packet(dt[0]);
		String mp=new String(dt).substring(1);
		for (String e:mp.split(",")){
			if (e.split(":").length!=2){
				continue;
			}
			p.set(Packet._base64_decode(e.split(":")[0]),Packet._base64_decode(e.split(":")[1]));
		}
		return p;
	}



	public static String _base64_decode(String s){
		return new String(Base64.getDecoder().decode(s));
	}



	private String _base64_encode(String s){
		return Base64.getEncoder().encodeToString(s.getBytes());
	}
}