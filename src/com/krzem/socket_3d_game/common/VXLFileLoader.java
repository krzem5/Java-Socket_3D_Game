package com.krzem.socket_3d_game.common;



import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.Exception;
import java.util.HashMap;
import java.util.Map;



public class VXLFileLoader{
	private static Map<String,String> _cache=new HashMap<String,String>();



	public static void to_packet(String fp,Packet p){
		if (VXLFileLoader._cache.get(fp)!=null){
			p.set("w",VXLFileLoader._cache.get(fp));
			return;
		}
		try{
			BufferedReader r=new BufferedReader(new InputStreamReader(VXLFileLoader.class.getResourceAsStream(fp)));
			String o="";
			String o_cl="";
			String ln=null;
			while ((ln=r.readLine())!=null){
				ln=ln.replace("\r","").toLowerCase();
				if (ln.length()==0){
					continue;
				}
				switch (ln.charAt(0)){
					case '#':
						continue;
					case 'c':
						String c_dt=ln.substring(2).replace("#","").replace(" ","");
						byte[] o_cl_dt=new byte[c_dt.length()/2];
						for (int i=0;i<c_dt.length();i+=2){
							o_cl_dt[i/2]=(byte)(Integer.parseInt(c_dt.substring(i,i+2),16));
						}
						o_cl+=new String(o_cl_dt);
						break;
					case 'o':
						String[] _ol=ln.substring(2).split(" ");
						o+=new String(new byte[]{(byte)Integer.parseInt(_ol[0]),(byte)Integer.parseInt(_ol[1]),(byte)Integer.parseInt(_ol[2]),(byte)Integer.parseInt(_ol[3]),(byte)Integer.parseInt(_ol[4]),(byte)Integer.parseInt(_ol[5]),(byte)Integer.parseInt(_ol[6]),(byte)Integer.parseInt(_ol[7])});
						break;
				}
			}
			r.close();
			o=new String(new byte[]{(byte)(o_cl.length()/18)})+o_cl+o;
			VXLFileLoader._cache.put(fp,o);
			p.set("w",o);
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}



	public static VXLObject to_object(String fp,int ox,int oy){
		Packet p=new Packet(-1);
		p.set("off",String.format("%d,%d",ox,oy));
		VXLFileLoader.to_packet(fp,p);
		VXLObject o=VXLFileLoader.from_packet(p);
		o.src=fp;
		return o;
	}



	public static VXLObject from_packet(Packet p){
		VXLObject o=new VXLObject(Integer.parseInt(p.get("off").split(",")[0]),Integer.parseInt(p.get("off").split(",")[1]));
		byte[] dt=p.get("w").getBytes();
		int cl_l=dt[0]*18;
		for (int i=1;i<cl_l+1;i+=18){
			o.add_cl(dt[i]&0xff,dt[i+1]&0xff,dt[i+2]&0xff,dt[i+3]&0xff,dt[i+4]&0xff,dt[i+5]&0xff,dt[i+6]&0xff,dt[i+7]&0xff,dt[i+8]&0xff,dt[i+9]&0xff,dt[i+10]&0xff,dt[i+11]&0xff,dt[i+12]&0xff,dt[i+13]&0xff,dt[i+14]&0xff,dt[i+15]&0xff,dt[i+16]&0xff,dt[i+17]&0xff);
		}
		for (int i=cl_l+1;i<dt.length;i+=8){
			o.add(dt[i]&0xff,dt[i+1]&0xff,dt[i+2]&0xff,dt[i+3]&0xff,dt[i+4]&0xff,dt[i+5]&0xff,dt[i+6]&0xff,dt[i+7]&0xff);
		}
		return o;
	}
}
