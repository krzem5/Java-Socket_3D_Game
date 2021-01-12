package com.krzem.socket_3d_game;



import java.security.MessageDigest;
import java.security.SecureRandom;



public class Cryptography{
	private static SecureRandom _sr=new SecureRandom();



	public static String _md5(String s){
		try{
			MessageDigest md=MessageDigest.getInstance("MD5");
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



	public static String _gen_rand_string(int l){
		byte[] bl=new byte[l];
		Cryptography._sr.nextBytes(bl);
		return new String(bl);
	}
}