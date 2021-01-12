package com.krzem.socket_3d_game.client;



import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import com.krzem.socket_3d_game.common.Packet;
import com.krzem.socket_3d_game.common.VXLFileLoader;
import com.krzem.socket_3d_game.common.VXLObject;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.Exception;
import java.lang.Math;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Game extends Constants{
	public Main.Main_ cls;
	public Client c;
	public String id;
	public Map<String,VXLObject> cm;
	public Map<String,Player> pm;
	public Map<String,List<double[][]>> tl;
	public List<int[]> db;
	public Hotbar hb;
	private Texture _tx=null;
	private BufferedImage _img=null;



	public Game(Main.Main_ cls){
		this.cls=cls;
		this.c=new Client(this.cls,this);
		this.cm=new HashMap<String,VXLObject>();
		this.pm=new HashMap<String,Player>();
		this.tl=new HashMap<String,List<double[][]>>();
		this.db=new ArrayList<int[]>();
		this.hb=new Hotbar(this.cls,this);
	}



	public void update(GL2 gl){
		Packet p=null;
		while ((p=this.c.recive())!=null){
			switch (p.id){
				case Packet.PACKET.SERVER.SHA256_REQUEST_PACKET:
					try{
						this.id=p.get("id");
						Packet p1=new Packet(1);
						p1.set("sha256",Cryptography._sha256_file(new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath()));
						this.c.send(p1);
					}
					catch (Exception e){
						e.printStackTrace();
					}
					break;
				case Packet.PACKET.SERVER.PLAYER_DATA_PACKET:
					Player pl=Player.from_packet(p,this.cls,this);
					this.pm.put(pl.id,pl);
					break;
				case Packet.PACKET.SERVER.PLAYER_LEAVE_PACKET:
					this.pm.remove(p.get("id"));
					break;
				case Packet.PACKET.SERVER.GAME_BOARD_DATA_PACKET:
					VXLObject o=VXLFileLoader.from_packet(p);
					this.cm.put(String.format("%d,%d",o.ox,o.oz),o);
					this.tl.put(String.format("%d,%d",o.ox,o.oz),this._get_triangles(o));
					break;
				case Packet.PACKET.SERVER.GAME_BOARD_DOOR_REMOVE_PACKET:
					for (String b:p.get("l").split(";")){
						int[] bp=new int[]{Integer.parseInt(b.split(",")[0]),Integer.parseInt(b.split(",")[1]),Integer.parseInt(b.split(",")[2])};
						int cx=(int)bp[0]/32-(bp[0]<0?1:0);
						int cz=(int)bp[2]/32-(bp[2]<0?1:0);
						this.db.add(new int[]{bp[0],bp[1],bp[2],100,
						this.cm.get(String.format("%d,%d",cx,cz)).remove(bp[0]-cx*32,bp[1],bp[2]-cz*32)});
					}
					break;
				case Packet.PACKET.SERVER.PLAYER_POSITION_CHANGE_PACKET:
					this.pm.get(p.get("id")).set_pos(Double.parseDouble(p.get("x")),Double.parseDouble(p.get("y")),Double.parseDouble(p.get("z")));
					break;
				case Packet.PACKET.SERVER.PLAYER_HOTBAR_DATA_PACKET:
					this.hb.from_packet(p);
					break;
				default:
					System.out.printf("UNKNOWN PACKET: %d\n",p.id);
			}
		}
		this.hb.update();
	}



	public void draw(GL2 gl){
		if (this._img==null){
			this._img=new BufferedImage(WINDOW_SIZE.width,WINDOW_SIZE.height,BufferedImage.TRANSLUCENT);
		}
		Graphics2D g=(Graphics2D)this._img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g.setBackground(new Color(255,255,255,0));
		g.clearRect(0,0,WINDOW_SIZE.width,WINDOW_SIZE.height);
		this._draw_chunks(gl);
		for (Map.Entry<String,Player> e:this.pm.entrySet()){
			e.getValue().draw(gl,g);
		}
		this.hb.draw(g);
		g.dispose();
		gl.glPushMatrix();
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		this.cls.glu.gluOrtho2D(0,WINDOW_SIZE.width,0,WINDOW_SIZE.height);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glEnable(GL2.GL_BLEND);
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA,GL2.GL_ONE_MINUS_SRC_ALPHA);
		if (this._tx==null){
			this._tx=AWTTextureIO.newTexture(gl.getGLProfile(),this._img,false);
		}
		else{
			this._tx.updateImage(gl,AWTTextureIO.newTextureData(gl.getGLProfile(),this._img,false));
		}
		this._tx.enable(gl);
		this._tx.bind(gl);
		gl.glBegin(GL2.GL_QUADS);
		gl.glColor4d(1,1,1,1);
		gl.glTexCoord2d(0,1);
		gl.glVertex2d(0,0);
		gl.glTexCoord2d(1,1);
		gl.glVertex2d(WINDOW_SIZE.width,0);
		gl.glTexCoord2d(1,0);
		gl.glVertex2d(WINDOW_SIZE.width,WINDOW_SIZE.height);
		gl.glTexCoord2d(0,0);
		gl.glVertex2d(0,WINDOW_SIZE.height);
		gl.glEnd();
		gl.glPopMatrix();
		gl.glDisable(GL2.GL_BLEND);
		gl.glDisable(GL2.GL_TEXTURE_2D);
	}



	private List<double[][]> _get_triangles(VXLObject o){
		List<double[][]> l=new ArrayList<double[][]>();
		for (int[] b:o.dt){
			if ((b[7]&1)==1){
				l.add(new double[][]{{b[0]+o.ox*32+1,b[1],b[2]+o.oz*32},{b[0]+o.ox*32+1,b[1]+1,b[2]+o.oz*32},{b[0]+o.ox*32+1,b[1],b[2]+o.oz*32+1}});
				l.add(new double[][]{{b[0]+o.ox*32+1,b[1]+1,b[2]+o.oz*32},{b[0]+o.ox*32+1,b[1]+1,b[2]+o.oz*32+1},{b[0]+o.ox*32+1,b[1],b[2]+o.oz*32+1}});
			}
			if ((b[7]&2)==2){
				l.add(new double[][]{{b[0]+o.ox*32,b[1]+1,b[2]+o.oz*32},{b[0]+o.ox*32+1,b[1]+1,b[2]+o.oz*32},{b[0]+o.ox*32,b[1]+1,b[2]+o.oz*32+1}});
				l.add(new double[][]{{b[0]+o.ox*32+1,b[1]+1,b[2]+o.oz*32+1},{b[0]+o.ox*32+1,b[1]+1,b[2]+o.oz*32},{b[0]+o.ox*32,b[1]+1,b[2]+o.oz*32+1}});
			}
			if ((b[7]&4)==4){
				l.add(new double[][]{{b[0]+o.ox*32,b[1],b[2]+o.oz*32+1},{b[0]+o.ox*32+1,b[1],b[2]+o.oz*32+1},{b[0]+o.ox*32,b[1]+1,b[2]+o.oz*32+1}});
				l.add(new double[][]{{b[0]+o.ox*32+1,b[1]+1,b[2]+o.oz*32+1},{b[0]+o.ox*32+1,b[1],b[2]+o.oz*32+1},{b[0]+o.ox*32,b[1]+1,b[2]+o.oz*32+1}});
			}
			if ((b[7]&8)==8){
				l.add(new double[][]{{b[0]+o.ox*32,b[1],b[2]+o.oz*32},{b[0]+o.ox*32,b[1]+1,b[2]+o.oz*32},{b[0]+o.ox*32,b[1],b[2]+o.oz*32+1}});
				l.add(new double[][]{{b[0]+o.ox*32,b[1]+1,b[2]+o.oz*32},{b[0]+o.ox*32,b[1]+1,b[2]+o.oz*32+1},{b[0]+o.ox*32,b[1],b[2]+o.oz*32+1}});
			}
			if ((b[7]&16)==16){
				l.add(new double[][]{{b[0]+o.ox*32,b[1],b[2]+o.oz*32},{b[0]+o.ox*32+1,b[1],b[2]+o.oz*32},{b[0]+o.ox*32,b[1],b[2]+o.oz*32+1}});
				l.add(new double[][]{{b[0]+o.ox*32+1,b[1],b[2]+o.oz*32+1},{b[0]+o.ox*32+1,b[1],b[2]+o.oz*32},{b[0]+o.ox*32,b[1],b[2]+o.oz*32+1}});
			}
			if ((b[7]&32)==32){
				l.add(new double[][]{{b[0]+o.ox*32,b[1],b[2]+o.oz*32},{b[0]+o.ox*32+1,b[1],b[2]+o.oz*32},{b[0]+o.ox*32,b[1]+1,b[2]+o.oz*32}});
				l.add(new double[][]{{b[0]+o.ox*32+1,b[1]+1,b[2]+o.oz*32},{b[0]+o.ox*32+1,b[1],b[2]+o.oz*32},{b[0]+o.ox*32,b[1]+1,b[2]+o.oz*32}});
			}
		}
		return l;
	}



	private void _draw_chunks(GL2 gl){
		gl.glBegin(GL2.GL_TRIANGLES);
		double[][] t=new double[][]{{this.cls.cam.x,this.cls.cam.z},{this.cls.cam.x+CAMERA_CAM_FAR*Math.cos((-this.cls.cam.ry-90-this.cls.cam.fov)/180*Math.PI),this.cls.cam.z+CAMERA_CAM_FAR*Math.sin((-this.cls.cam.ry-90-this.cls.cam.fov)/180*Math.PI)},{this.cls.cam.x+CAMERA_CAM_FAR*Math.cos((-this.cls.cam.ry-90+this.cls.cam.fov)/180*Math.PI),this.cls.cam.z+CAMERA_CAM_FAR*Math.sin((-this.cls.cam.ry-90+this.cls.cam.fov)/180*Math.PI)}};
		for (Map.Entry<String,VXLObject> e:this.cm.entrySet()){
			if (this._collision_poly_rect(t,e.getValue().ox*32,e.getValue().oz*32,32,32)==true){
				for (int[] b:e.getValue().dt){
					this._draw_box(gl,b[0]+e.getValue().ox*32,b[1],b[2]+e.getValue().oz*32,1,b[7],e.getValue().cl_dt.get(b[6]));
				}
			}
		}
		for (int i=this.db.size()-1;i>=0;i--){
			int[] db=this.db.get(i);
			db[3]-=3;
			if (db[3]<=0){
				this.db.remove(i);
				continue;
			}
			int cx=(int)db[0]/32-(db[0]<0?1:0);
			int cz=(int)db[2]/32-(db[2]<0?1:0);
			this._draw_box(gl,db[0]+(0.5-db[3]/200d),db[1]+(0.5-db[3]/200d),db[2]+(0.5-db[3]/200d),db[3]/100d,63,this.cm.get(String.format("%d,%d",cx,cz)).cl_dt.get(db[4]));
		}
		gl.glEnd();
	}



	private void _draw_box(GL2 gl,double x,double y,double z,double sz,int v,int[][] c_dt){
		if ((v&1)==1){
			gl.glColor3d(c_dt[0][0]/255d,c_dt[0][1]/255d,c_dt[0][2]/255d);
			gl.glVertex3d(x+sz,y,z);
			gl.glVertex3d(x+sz,y+sz,z);
			gl.glVertex3d(x+sz,y,z+sz);
			gl.glVertex3d(x+sz,y+sz,z);
			gl.glVertex3d(x+sz,y+sz,z+sz);
			gl.glVertex3d(x+sz,y,z+sz);
		}
		if ((v&2)==2){
			gl.glColor3d(c_dt[1][0]/255d,c_dt[1][1]/255d,c_dt[1][2]/255d);
			gl.glVertex3d(x,y+sz,z);
			gl.glVertex3d(x+sz,y+sz,z);
			gl.glVertex3d(x,y+sz,z+sz);
			gl.glVertex3d(x+sz,y+sz,z+sz);
			gl.glVertex3d(x+sz,y+sz,z);
			gl.glVertex3d(x,y+sz,z+sz);
		}
		if ((v&4)==4){
			gl.glColor3d(c_dt[2][0]/255d,c_dt[2][1]/255d,c_dt[2][2]/255d);
			gl.glVertex3d(x,y,z+sz);
			gl.glVertex3d(x+sz,y,z+sz);
			gl.glVertex3d(x,y+sz,z+sz);
			gl.glVertex3d(x+sz,y+sz,z+sz);
			gl.glVertex3d(x+sz,y,z+sz);
			gl.glVertex3d(x,y+sz,z+sz);
		}
		if ((v&8)==8){
			gl.glColor3d(c_dt[3][0]/255d,c_dt[3][1]/255d,c_dt[3][2]/255d);
			gl.glVertex3d(x,y,z);
			gl.glVertex3d(x,y+sz,z);
			gl.glVertex3d(x,y,z+sz);
			gl.glVertex3d(x,y+sz,z);
			gl.glVertex3d(x,y+sz,z+sz);
			gl.glVertex3d(x,y,z+sz);
		}
		if ((v&16)==16){
			gl.glColor3d(c_dt[4][0]/255d,c_dt[4][1]/255d,c_dt[4][2]/255d);
			gl.glVertex3d(x,y,z);
			gl.glVertex3d(x+sz,y,z);
			gl.glVertex3d(x,y,z+sz);
			gl.glVertex3d(x+sz,y,z+sz);
			gl.glVertex3d(x+sz,y,z);
			gl.glVertex3d(x,y,z+sz);
		}
		if ((v&32)==32){
			gl.glColor3d(c_dt[5][0]/255d,c_dt[5][1]/255d,c_dt[5][2]/255d);
			gl.glVertex3d(x,y,z);
			gl.glVertex3d(x+sz,y,z);
			gl.glVertex3d(x,y+sz,z);
			gl.glVertex3d(x+sz,y+sz,z);
			gl.glVertex3d(x+sz,y,z);
			gl.glVertex3d(x,y+sz,z);
		}
	}



	private boolean _collision_poly_rect(double[][] p,double rx,double ry,double rw,double rh){
		for (int i=0;i<p.length;i++){
			if (this._collision_line_rect(p[i][0],p[i][1],p[(i+1)%p.length][0],p[(i+1)%p.length][1],rx,ry,rw,rh)==true){
				return true;
			}
		}
		if (this._collision_poly_point(p,rx,ry)==true){
			return true;
		}
		return false;
	}



	private boolean _collision_poly_point(double[][] p,double px,double py){
		boolean c=false;
		for (int i=0;i<p.length;i++){
			if (px<(p[(i+1)%p.length][0]-p[i][0])*(py-p[i][1])/(p[(i+1)%p.length][1]-p[i][1])+p[i][0]&&((p[i][1]>py&&p[(i+1)%p.length][1]<py)||(p[i][1]<py&&p[(i+1)%p.length][1]>py))){
				c=!c;
			}
		}
		return c;
	}



	private boolean _collision_line_rect(double sx,double sy,double ex,double ey,double rx,double ry,double rw,double rh){
		return (this._collision_or_parallel_line_line(sx,sy,ex,ey,rx,ry,rx+rw,ry)==true||this._collision_or_parallel_line_line(sx,sy,ex,ey,rx+rw,ry,rx+rw,ry+rh)==true||this._collision_or_parallel_line_line(sx,sy,ex,ey,rx+rw,ry+rh,rx,ry+rh)==true||this._collision_or_parallel_line_line(sx,sy,ex,ey,rx,ry+rh,rx,ry)==true);
	}



	private boolean _collision_or_parallel_line_line(double asx,double asy,double aex,double aey,double bsx,double bsy,double bex,double bey){
		if (((asx-aex)*(bsy-bey)-(asy-aey)*(bsx-bex))<=LINE_PARALLEL_BUFFER){
			return true;
		}
		double t=((asx-bsx)*(bsy-bey)-(asy-bsy)*(bsx-bex))/((asx-aex)*(bsy-bey)-(asy-aey)*(bsx-bex));
		double u=-((asx-asx)*(asy-bsy)-(asy-aey)*(asx-bsx))/((asx-aex)*(bsy-bey)-(asy-aey)*(bsx-bex));
		return ((0<=t&&t<=1&&0<=u&&u<=1));
	}
}