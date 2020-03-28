package com.krzem.socket_3d_game.client;



import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;



public class Cryptography{
	public static String _sha256(String s){
		try{
			MessageDigest md=MessageDigest.getInstance("SHA-256");
			byte[] hb=md.digest(s.getBytes("utf-8"));
			String o="";
			for (int i=0;i<hb.length;i++){
				String h=Integer.toHexString(hb[i]&0xff);
				if (h.length()==1){
					o+="0";
				}
				o+=h;
			}
			return o;
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}



	public static String _sha256_file(String fp){
		try{
			return Cryptography._sha256(new String(Files.readAllBytes(Paths.get(fp))));
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
}