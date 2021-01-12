package com.krzem.socket_3d_game.client;



import com.jogamp.opengl.GL2;
import com.krzem.socket_3d_game.common.Packet;
import java.awt.Cursor;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.lang.Exception;
import java.lang.Math;
import java.util.ArrayList;
import java.util.List;



public class Camera extends Constants{
	public Main.Main_ cls;
	public double fov;
	public double x;
	public double y;
	public double z;
	public double rx;
	public double ry;
	public double drx;
	public double dry;
	public double _rx;
	public double _ry;
	public double[] mp=null;
	private Robot rb;
	private boolean _l=true;
	public boolean _center=true;
	private boolean _init=false;
	private int _s_c=0;
	private int _ls=0;



	public Camera(Main.Main_ cls){
		this.cls=cls;
		try{
			this.rb=new Robot(SCREEN);
		}
		catch (Exception e){
			e.printStackTrace();
		}
		this.fov=70;
		this.x=0;
		this.y=0;
		this.z=0;
		this.rx=0;
		this.ry=0;
		this.drx=0;
		this.dry=0;
		this._rx=0;
		this._ry=0;
	}



	public void setup(GL2 gl){
		gl.glViewport(0,0,WINDOW_SIZE.width,WINDOW_SIZE.height);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		this.cls.glu.gluPerspective((float)this.fov,(float)WINDOW_SIZE.width/(float)WINDOW_SIZE.height,CAMERA_CAM_NEAR,CAMERA_CAM_FAR);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}



	public void update(){
		if (this._l==true&&this._center==true){
			this.mp=null;
			if (this._init==false){
				this.rb.mouseMove(WINDOW_SIZE.width/2,WINDOW_SIZE.height/2);
				this.cls.canvas.setCursor(this.cls.canvas.getToolkit().createCustomCursor(new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB),new Point(),null));
				this._init=true;
			}
			if (this._s_c<4){
				this._s_c++;
				this.rb.mouseMove(WINDOW_SIZE.width/2,WINDOW_SIZE.height/2);
			}
			else{
				int sp=0;
				if (this.cls.KEYBOARD.pressed(87)){
					sp=1;
					if (this.cls.KEYBOARD.pressed(17)){
						sp=2;
					}
				}
				if (this._ls!=sp){
					this._ls=sp;
					Packet p=new Packet(Packet.PACKET.CLIENT.PLAYER_MOVEMENT_REQUEST_PACKET);
					p.set("s",Integer.toString(sp));
					this.cls.g.c.send(p);
				}
				this._rx+=(WINDOW_SIZE.height/2-this.cls.MOUSE_POS.y)*CAMERA_ROT_SPEED;
				this._ry+=(WINDOW_SIZE.width/2-this.cls.MOUSE_POS.x)*CAMERA_ROT_SPEED;
				this._rx=Math.max(Math.min(this._rx,180-CAMERA_MIN_ANGLE),180-CAMERA_MAX_ANGLE);
				this.rx=this._rx+270;
				this.ry=this._ry+90;
				this.drx=this._ease(this.drx,this.rx);
				this.dry=this._ease(this.dry,this.ry);
				if (Math.abs(this.dry-this.ry)>=CAMERA_MIN_EASE_DIFF){
					Packet p=new Packet(Packet.PACKET.CLIENT.PLAYER_ROTATION_REQUEST_PACKET);
					p.set("a",Double.toString(this.dry-90));
					this.cls.g.c.send(p);
				}
				Player cp=this.cls.g.pm.get(this.cls.g.id);
				if (cp!=null){
					double[] p=this._constrain_triangles_wr(new double[]{cp.x,cp.y+CAMERA_PLAYER_Y_ADD,cp.z},new double[]{Math.sin((this.drx-270)/180*Math.PI)*Math.cos(-(this.dry-90)/180*Math.PI),Math.cos((this.drx-270)/180*Math.PI),Math.sin((this.drx-270)/180*Math.PI)*Math.sin(-(this.dry-90)/180*Math.PI)},(cp.cy!=-1?CAMERA_PLAYER_MAX_LOW_CEILING_DIST:CAMERA_PLAYER_MAX_DIST));
					this.x=p[0];
					this.y=p[1];
					this.z=p[2];
				}
				this.rb.mouseMove(WINDOW_SIZE.width/2,WINDOW_SIZE.height/2);
			}
		}
		else{
			this.mp=new double[]{this.cls.MOUSE_POS.x,WINDOW_SIZE.height-this.cls.MOUSE_POS.y};
		}
	}


	public void draw(GL2 gl){
		gl.glViewport(0,0,WINDOW_SIZE.width,WINDOW_SIZE.height);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		this.cls.glu.gluPerspective((float)this.fov,(float)WINDOW_SIZE.width/(float)WINDOW_SIZE.height,CAMERA_CAM_NEAR,CAMERA_CAM_FAR);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glRotatef((float)-this.drx,1.0f,0.0f,0.0f);
		gl.glRotatef((float)-this.dry,0.0f,1.0f,0.0f);
		gl.glTranslatef((float)-this.x,(float)-this.y,(float)-this.z);
	}



	private double _ease(double c,double t){
		if (Math.abs(t-c)<CAMERA_MIN_EASE_DIFF){
			return t+0;
		}
		else{
			return c*CAMERA_EASE_PROC+(1-CAMERA_EASE_PROC)*t;
		}
	}



	private double _map(double v,double aa,double ab,double ba,double bb){
		return (v-aa)/(ab-aa)*(bb-ba)+ba;
	}



	private double[] _constrain_triangles_wr(double[] ro,double[] rd,double rl){
		int cx=(int)ro[0]/32-(ro[0]<0?1:0);
		int cz=(int)ro[2]/32-(ro[2]<0?1:0);
		if ((int)ro[0]-cx*32==32){
			cx++;
		}
		if ((int)ro[2]-cz*32==32){
			cz++;
		}
		for (int i=-1;i<=1;i++){
			for (int j=-1;j<=1;j++){
				if (this.cls.g.tl.get(String.format("%d,%d",cx-i,cz-j))!=null){
					rl=Math.min(rl,this._constrain_triangles(ro,rd,rl,this.cls.g.tl.get(String.format("%d,%d",cx-i,cz-j))));
				}
			}
		}
		return new double[]{ro[0]+rd[0]*rl,ro[1]+rd[1]*rl,ro[2]+rd[2]*rl};
	}



	private double _constrain_triangles(double[] ro,double[] rd,double rl,List<double[][]> tl){
		double[] e1;
		double[] e2;
		double[] h;
		double[] s;
		double[] q;
		for (double[][] t:tl){
			e1=new double[]{t[1][0]-t[0][0],t[1][1]-t[0][1],t[1][2]-t[0][2]};
			e2=new double[]{t[2][0]-t[0][0],t[2][1]-t[0][1],t[2][2]-t[0][2]};
			h=new double[]{rd[1]*e2[2]-rd[2]*e2[1],rd[2]*e2[0]-rd[0]*e2[2],rd[0]*e2[1]-rd[1]*e2[0]};
			double a=e1[0]*h[0]+e1[1]*h[1]+e1[2]*h[2];
			if (a>-EPSILON&&a<EPSILON){
				continue;
			}
			double f=1/a;
			s=new double[]{ro[0]-t[0][0],ro[1]-t[0][1],ro[2]-t[0][2]};
			double u=f*(s[0]*h[0]+s[1]*h[1]+s[2]*h[2]);
			if (u<0||u>1){
				continue;
			}
			q=new double[]{s[1]*e1[2]-s[2]*e1[1],s[2]*e1[0]-s[0]*e1[2],s[0]*e1[1]-s[1]*e1[0]};
			double v=f*(rd[0]*q[0]+rd[1]*q[1]+rd[2]*q[2]);
			if (v<0||u+v>1){
				continue;
			}
			double ln=f*(e2[0]*q[0]+e2[1]*q[1]+e2[2]*q[2])-CAMERA_CAM_NEAR*2;
			if (ln>EPSILON){
				rl=Math.min(rl,ln);
			}
		}
		return rl;
	}
}