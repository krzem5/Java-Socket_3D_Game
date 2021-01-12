package com.krzem.socket_3d_game;



import com.krzem.socket_3d_game.common.ItemObject;
import com.krzem.socket_3d_game.common.Packet;
import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



public class Player extends Constants{
	public Game g;
	public Client c;
	public String id;
	public String nm;
	public double x;
	public double y;
	public double z;
	public double dx;
	public double dz;
	public double ddx;
	public double ddz;
	public double a;
	public int sp;
	public Item[] hb;
	public int hb_c;
	public List<int[]> cb;
	private double _d=0;;
	private long _l_tm=-1;
	private boolean _hb_u=true;



	public Player(Game g,Client c){
		this.g=g;
		this.c=c;
		this.id=c.id;
		this.nm=String.format("Client#%s",this.id).substring(0,16);
		this.x=16+(RANDOM_GENERATOR.nextDouble()*2-1)*MAP_PLAYER_SPAWN_OFFEST_RANGE;
		this.y=PLAYER_Y+0;
		this.z=16+(RANDOM_GENERATOR.nextDouble()*2-1)*MAP_PLAYER_SPAWN_OFFEST_RANGE;
		this.dx=0;
		this.dz=0;
		this.ddx=0;
		this.ddz=0;
		this.a=0;
		this.sp=0;
		this.hb=new Item[5];
		this.hb_c=0;
		this.cb=new ArrayList<int[]>();
		this.hb[0]=new Item(this.g,this,ItemObject.ITEM.BASIC_PISTOL);
		for (int i=1;i<5;i++){
			this.hb[i]=new Item(this.g,this,ItemObject.ITEM.EMPTY);
		}
	}



	public void update(){
		if (this._l_tm==-1){
			this._l_tm=System.nanoTime();
		}
		double df=((double)(System.nanoTime()-this._l_tm))*1e-9;
		this._l_tm=System.nanoTime();
		Packet p=null;
		while ((p=this.c.s.recive())!=null){
			switch (p.id){
				case Packet.PACKET.CLIENT.PLAYER_MOVEMENT_REQUEST_PACKET:
					this._d=-Math.min(Integer.parseInt(p.get("s")),1);
					this.sp=Math.max(Integer.parseInt(p.get("s"))-1,0);
					break;
				case Packet.PACKET.CLIENT.PLAYER_ROTATION_REQUEST_PACKET:
					this.a=-Double.parseDouble(p.get("a"))/180*Math.PI;
					break;
				case Packet.PACKET.CLIENT.PLAYER_ITEM_ACTION_REQUEST_PACKET:
					this._hb_u=(this.hb[this.hb_c].use()==true?true:this._hb_u);
					break;
				case Packet.PACKET.CLIENT.PLAYER_HOTBAR_CURSOR_MOVEMENT_REQUEST_PACKET:
					this.hb_c=Integer.parseInt(p.get("c"));
					this._hb_u=true;
					break;
				default:
					System.out.printf("UNKNOWN PACKET: %d\n",p.id);
			}
		}
		double ox=this.x+0;
		double oz=this.z+0;
		this.dx=0;
		this.dz=0;
		double _dx=this._round((this._d*Math.cos(this.a)));
		double _dz=this._round((this._d*Math.sin(this.a)));
		double s=(this.sp==0?PLAYER_SPEED:PLAYER_SPRINT_SPEED);
		double ts=0;
		this.cb=new ArrayList<int[]>();
		while (ts<s){
			double sa=(ts+PLAYER_MAX_ADD_SPEED>=s?s-ts:PLAYER_MAX_ADD_SPEED);
			this.dx+=_dx*df*sa;
			this.dz+=_dz*df*sa;
			this.x+=_dx*df*sa;
			this.z+=_dz*df*sa;
			this._collision_wr();
			ts+=sa;
		}
		if ((ox-this.x)*(ox-this.x)+(oz-this.z)*(oz-this.z)>=PLAYER_MIN_MOVEMENT_UPDATE){
			Packet mp=new Packet(Packet.PACKET.SERVER.PLAYER_POSITION_CHANGE_PACKET);
			mp.set("id",this.id);
			mp.set("x",Double.toString(this.x));
			mp.set("y",Double.toString(this.y));
			mp.set("z",Double.toString(this.z));
			this.g.send_all(mp);
		}
		for (int i=0;i<5;i++){
			if (this.hb[i].update()==true){
				this._hb_u=true;
			}
		}
		if (this._hb_u==true){
			Packet hp=new Packet(Packet.PACKET.SERVER.PLAYER_HOTBAR_DATA_PACKET);
			hp.set("c",Integer.toString(this.hb_c));
			for (int i=0;i<5;i++){
				hp.set(String.format("s%d",i),this.hb[i].to_packet());
			}
			this.c.s.send(hp);
		}
		this._hb_u=false;
	}



	public Packet to_packet(){
		Packet p=new Packet(Packet.PACKET.SERVER.PLAYER_DATA_PACKET);
		p.set("id",this.id);
		p.set("name",this.nm);
		p.set("x",Double.toString(this.x));
		p.set("y",Double.toString(this.y));
		p.set("z",Double.toString(this.z));
		return p;
	}



	private double _round(double v){
		return Math.floor(v*100000)/100000;
	}



	private void _collision_wr(){
		int cx=(int)this.x/32-(this.x<0?1:0);
		int cz=(int)this.z/32-(this.z<0?1:0);
		for (int i=-1;i<=1;i++){
			for (int j=-1;j<=1;j++){
				if (this.g.co.get(String.format("%d,%d",cx+i,cz+j))!=null){
					this._collision(this.g.co.get(String.format("%d,%d",cx+i,cz+j)));
				}
			}
		}
		for (Map.Entry<String,List<double[][]>> e:this.g.dco.entrySet()){
			if (this.g.dos.get(e.getKey())==true){
				continue;
			}
			this._collision(e.getValue());
		}
		for (int[] p:this.cb){
			for (Map.Entry<String,int[]> e:this.g.doa.entrySet()){
				if (this.g.dos.get(e.getKey())==false){
					if (e.getValue()[0]==p[0]&&e.getValue()[2]==p[1]){
						this.g.dos.put(e.getKey(),true);
						Packet drp=new Packet(Packet.PACKET.SERVER.GAME_BOARD_DOOR_REMOVE_PACKET);
						String s="";
						for (int[] db:this.g.dol.get(e.getKey())){
							s+=String.format(";%d,%d,%d",db[0],db[1],db[2]);
						}
						drp.set("l",(s.length()==0?"":s.substring(1)));
						this.c.s.send(drp);
					}
				}
			}
		}
	}



	private void _collision(List<double[][]> cl){
		for (double[][] c:cl){
			double cx=this.x+0;
			double cy=this.z+0;
			double cr=PLAYER_RADIUS;
			double ov=-1;
			double[] ovN=null;
			outer:
			for (int i=0;i<c.length;i++){
				int ni=(i+1==c.length?0:i+1);
				int nni=(ni+1==c.length?0:ni+1);
				int pi=(i==0?c.length-1:i-1);
				double cov=-1;
				double[] covN=null;
				double[] e=new double[]{c[ni][0]-c[i][0],c[ni][1]-c[i][1]};
				double[] p=new double[]{cx-c[i][0],cy-c[i][1]};
				if (p[0]*e[0]+p[1]*e[1]<0){
					e=new double[]{c[i][0]-c[pi][0],c[i][1]-c[pi][1]};
					double[] p2=new double[]{cx-c[pi][0],cy-c[pi][1]};
					if (p2[0]*e[0]+p2[1]*e[1]>e[0]*e[0]+e[1]*e[1]){
						if (p[0]*p[0]+p[1]*p[1]>cr*cr){
							continue outer;
						}
						double l=Math.sqrt(p[0]*p[0]+p[1]*p[1]);
						covN=new double[]{p[0]/l,p[1]/l};
						cov=cr-l;
					}
				}
				else if (p[0]*e[0]+p[1]*e[1]>e[0]*e[0]+e[1]*e[1]){
					e=new double[]{c[nni][0]-c[ni][0],c[nni][1]-c[ni][1]};
					p=new double[]{cx-c[ni][0],cy-c[ni][1]};
					if (p[0]*e[0]+p[1]*e[1]<0){
						if (p[0]*p[0]+p[1]*p[1]>cr*cr){
							continue outer;
						}
						double l=Math.sqrt(p[0]*p[0]+p[1]*p[1]);
						covN=new double[]{p[0]/l,p[1]/l};
						cov=cr-l;
					}
				}
				else{
					double l=Math.sqrt(e[0]*e[0]+e[1]*e[1]);
					double[] n=new double[]{e[1]/l,-e[0]/l};
					if (p[0]*n[0]+p[1]*n[1]<0||Math.abs(p[0]*n[0]+p[1]*n[1])>cr){
						continue outer;
					}
					covN=n;
					cov=cr-(p[0]*n[0]+p[1]*n[1]);
				}
				if (covN!=null&&(Math.abs(cov)<ov||ov==-1)){
					ov=cov+0;
					ovN=new double[]{covN[0]*cov,covN[1]*cov};
				}
			}
			if (ovN!=null){
				this.x+=ovN[0]+EPSILON*(ov<0?-1:1);
				this.z+=ovN[1]+EPSILON*(ov<0?-1:1);
				if (this.cb.contains(new int[]{(int)c[0][0],(int)c[0][1]})==false){
					this.cb.add(new int[]{(int)c[0][0],(int)c[0][1]});
				}
			}
		}
	}
}