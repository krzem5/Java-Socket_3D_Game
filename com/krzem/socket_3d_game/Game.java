package com.krzem.socket_3d_game;



import com.krzem.socket_3d_game.common.Packet;
import com.krzem.socket_3d_game.common.VXLFileLoader;
import com.krzem.socket_3d_game.common.VXLObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.Exception;
import java.lang.Math;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;



public class Game extends Constants{
	private static List<Game> gl=null;
	private static Map<String,List<String>> tfl=null;
	public Map<String,Player> pl;
	public Map<String,VXLObject> m;
	public Map<String,List<double[][]>> co;
	public Map<String,Boolean> dos;
	public Map<String,int[]> doa;
	public Map<String,List<int[]>> dol;
	public Map<String,List<double[][]>> dco;



	static{
		Game.gl=new ArrayList<Game>();
		Game.tfl=new HashMap<String,List<String>>();
		try{
			ZipInputStream zf=new ZipInputStream(Game.class.getProtectionDomain().getCodeSource().getLocation().openStream());
			while(true) {
				ZipEntry e=zf.getNextEntry();
				if (e==null){
					break;
				}
				if (e.getName().startsWith(MAP_CHUNK_FILE_PATH.replaceAll("^/",""))&&e.getName().replace(MAP_CHUNK_FILE_PATH.replaceAll("^/",""),"").split("\\.")[0].split(",").length==3&&e.getName().endsWith(".vxl")){
					String f=e.getName().replace(MAP_CHUNK_FILE_PATH.replaceAll("^/",""),"").split("\\.")[0];
					if (tfl.get(f.split(",")[0])==null){
						tfl.put(f.split(",")[0],new ArrayList<String>());
					}
					tfl.get(f.split(",")[0]).add("/"+e.getName());
				}
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}



	public Game(){
		Game cls=this;
		this._gen_map();
		this.pl=new HashMap<String,Player>();
		new Thread(new Runnable(){
			@Override
			public void run(){
				while (true){
					cls.update();
					try{
						Thread.sleep(1000/60);
					}
					catch (Exception e){
						e.printStackTrace();
					}
				}
			}
		}).start();
	}



	public void send_all(Packet p){
		for (Map.Entry<String,Player> e:this.pl.entrySet()){
			e.getValue().c.s.send(p);
		}
	}



	public void remove(Client c){
		this.pl.remove(c.id);
		Packet p=new Packet(Packet.PACKET.SERVER.PLAYER_LEAVE_PACKET);
		p.set("id",c.id);
		this.send_all(p);
	}



	public static void add(Client c){
		if (Game.gl.size()==0){
			Game.gl.add(new Game());
		}
		Game.gl.get(Game.gl.size()-1)._init(c);
	}



	private void update(){
		for (Map.Entry<String,Player> e:this.pl.entrySet()){
			e.getValue().update();
		}
	}



	private void _init(Client c){
		c.g=this;
		Player p=new Player(this,c);
		this.pl.put(p.id,p);
		this._send_map(p);
		for (Map.Entry<String,Player> e:this.pl.entrySet()){
			c.s.send(e.getValue().to_packet());
			e.getValue().c.s.send(p.to_packet());
		}
	}



	private void _send_map(Player p){
		for (Map.Entry<String,VXLObject> e:this.m.entrySet()){
			Packet wp=new Packet(Packet.PACKET.SERVER.GAME_BOARD_DATA_PACKET);
			wp.set("off",String.format("%d,%d",e.getValue().ox,e.getValue().oz));
			VXLFileLoader.to_packet(e.getValue().src,wp);
			p.c.s.send(wp);
		}
	}



	private void _gen_map(){
		try{
			this.m=new HashMap<String,VXLObject>();
			int i=(int)(RANDOM_GENERATOR.nextDouble()*tfl.size());
			int j=0;
			String nm=null;
			for (Map.Entry<String,List<String>> e:tfl.entrySet()){
				if (j==i){
					nm=e.getKey();
					for (String fp:e.getValue()){
						String fn=fp.replace(MAP_CHUNK_FILE_PATH.replaceAll("^/",""),"").split("\\.")[0];
						this.m.put(fn.substring(fn.split(",")[0].length()+1),VXLFileLoader.to_object(fp,Integer.parseInt(fn.split(",")[1]),Integer.parseInt(fn.split(",")[2])));
					}
					break;
				}
				j++;
			}
			this.co=new HashMap<String,List<double[][]>>();
			for (Map.Entry<String,VXLObject> e:this.m.entrySet()){
				this.co.put(e.getKey(),e.getValue().collision(PLAYER_Y));
			}
			BufferedReader r=new BufferedReader(new InputStreamReader(Game.class.getResourceAsStream(String.format("%s%s.dt",MAP_CHUNK_FILE_PATH,nm))));
			this.dos=new HashMap<String,Boolean>();
			this.doa=new HashMap<String,int[]>();
			this.dol=new HashMap<String,List<int[]>>();
			this.dco=new HashMap<String,List<double[][]>>();
			Map<String,List<Integer>> rm=new HashMap<String,List<Integer>>();
			String ln=null;
			while ((ln=r.readLine())!=null){
				ln=ln.replace("\r","").toLowerCase();
				if (ln.length()==0){
					continue;
				}
				String[] s_ln=ln.split(" ");
				String d_nm=s_ln[0];
				this.dos.put(d_nm,false);
				this.doa.put(d_nm,new int[]{Integer.parseInt(s_ln[1].split(",")[0]),Integer.parseInt(s_ln[1].split(",")[1]),Integer.parseInt(s_ln[1].split(",")[2])});
				this.dol.put(d_nm,new ArrayList<int[]>());
				this.dco.put(d_nm,new ArrayList<double[][]>());
				for (int k=2;k<s_ln.length;k++){
					int[] p=new int[]{Integer.parseInt(s_ln[k].split(",")[0]),Integer.parseInt(s_ln[k].split(",")[1]),Integer.parseInt(s_ln[k].split(",")[2])};
					this.dol.get(d_nm).add(p);
					int ox=p[0]/32-(p[0]<0?1:0);
					int oz=p[2]/32-(p[2]<0?1:0);
					if (p[1]==PLAYER_Y){
						if (rm.containsKey(String.format("%d,%d",ox,oz))==false){
							rm.put(String.format("%d,%d",ox,oz),new ArrayList<Integer>());
						}
						int l=this.m.get(String.format("%d,%d",ox,oz)).index(p[0]-ox*32,p[1],p[2]-oz*32);
						if (l!=-1){
							rm.get(String.format("%d,%d",ox,oz)).add(l);
							this.dco.get(d_nm).add(new double[][]{{p[0],p[2]},{p[0]+1,p[2]},{p[0]+1,p[2]+1},{p[0],p[2]+1}});
						}
					}
				}
			}
			for (Map.Entry<String,List<Integer>> e:rm.entrySet()){
				while (e.getValue().size()>0){
					int mx=-1;
					int idx_i=-1;
					int k=0;
					for (int idx:e.getValue()){
						mx=Math.max(mx,idx);
						if (mx==idx){
							idx_i=k+0;
						}
						k++;
					}
					this.co.get(e.getKey()).remove(mx);
					e.getValue().remove(idx_i);
				}
			}
			r.close();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
}