package com.krzem.socket_3d_game.client;



import com.jogamp.opengl.GL2;
import com.krzem.socket_3d_game.common.Packet;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.lang.Math;



public class Player extends Constants{
	public Main.Main_ cls;
	public Game g;
	public String id;
	public String nm;
	public double x;
	public double y;
	public double z;
	public int cy;



	public Player(Main.Main_ cls,Game g,String id,String nm,double x,double y,double z){
		this.cls=cls;
		this.g=g;
		this.id=id;
		this.nm=nm;
		this.x=x;
		this.y=y;
		this.z=z;
		this.cy=-1;
	}



	public void set_pos(double x,double y,double z){
		this.x=x;
		this.y=y;
		this.z=z;
		int cx=(int)(this.x-1)/32-(this.x-1<0?1:0);
		int cz=(int)(this.z-1)/32-(this.z-1<0?1:0);
		if ((int)this.x-cx*32==32){
			cx++;
		}
		if ((int)this.z-cz*32==32){
			cz++;
		}
		this.cy=(this.g.cm.get(String.format("%d,%d",cx,cz))==null?-1:this.g.cm.get(String.format("%d,%d",cx,cz)).ceiling_y((int)this.x-cx*32,(int)this.y,(int)this.z-cz*32));
	}



	public void draw(GL2 gl,Graphics2D g){
		gl.glBegin(GL2.GL_LINES);
		gl.glColor3d(1,1,0);
		gl.glVertex3d(this.x,this.y,this.z);
		gl.glVertex3d(this.x,this.y+5,this.z);
		gl.glEnd();
		gl.glEnable(GL2.GL_BLEND);
		gl.glBegin(GL2.GL_TRIANGLES);
		gl.glColor4d(0,0,0,PLAYER_SHADOW_OPCAITY/100);
		for (double a=0;a<Math.PI*2;a+=Math.PI*2/PLAYER_SHADOW_SEGMENTS){
			gl.glVertex3d(this.x,this.y+PLAYER_SHADOW_Y_OFFSET,this.z);
			gl.glVertex3d(this.x+Math.cos(a)*PLAYER_RADIUS,this.y+PLAYER_SHADOW_Y_OFFSET,this.z+Math.sin(a)*PLAYER_RADIUS);
			gl.glVertex3d(this.x+Math.cos(a+Math.PI*2/PLAYER_SHADOW_SEGMENTS)*PLAYER_RADIUS,this.y+PLAYER_SHADOW_Y_OFFSET,this.z+Math.sin(a+Math.PI*2/PLAYER_SHADOW_SEGMENTS)*PLAYER_RADIUS);
		}
		gl.glEnd();
		gl.glDisable(GL2.GL_BLEND);
		if (!this.id.equals(this.g.id)){
			double[] pp=this._project_2d(gl,new double[]{this.x,this.y+PLAYER_NAME_Y_OFFSET,this.z});
			if (pp[2]<=1&&(this.x-this.cls.cam.x)*(this.x-this.cls.cam.x)+(this.y+PLAYER_NAME_Y_OFFSET-this.cls.cam.y)*(this.y+PLAYER_NAME_Y_OFFSET-this.cls.cam.y)+(this.z-this.cls.cam.z)*(this.z-this.cls.cam.z)<=PLAYER_NAME_MAX_RENDER_DIST*PLAYER_NAME_MAX_RENDER_DIST){
				g.setColor(PLAYER_NAME_BG_COLOR);
				g.setFont(PLAYER_NAME_FONT);
				FontMetrics fm=g.getFontMetrics();
				int w=fm.stringWidth(this.nm);
				int h=fm.getHeight();
				g.fillRect((int)pp[0]-w/2,WINDOW_SIZE.height-(int)pp[1]-h/2,w,h);
				g.setColor(PLAYER_NAME_COLOR);
				g.drawString(this.nm,(int)pp[0]-w/2,WINDOW_SIZE.height-(int)pp[1]-h/2+fm.getAscent());
			}
		}
	}



	public static Player from_packet(Packet p,Main.Main_ cls,Game g){
		return new Player(cls,g,p.get("id"),p.get("name"),Double.parseDouble(p.get("x")),Double.parseDouble(p.get("y")),Double.parseDouble(p.get("z")));
	}



	private double[] _project_2d(GL2 gl,double[] p){
		float[] pp=new float[3];
		float[] mm=new float[16];
		float[] pm=new float[16];
		int[] vp=new int[]{0,0,WINDOW_SIZE.width,WINDOW_SIZE.height};
		gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX,mm,0);
		gl.glGetFloatv(GL2.GL_PROJECTION_MATRIX,pm,0);
		this.cls.glu.gluProject((float)p[0],(float)p[1],(float)p[2],mm,0,pm,0,vp,0,pp,0);
		return new double[]{pp[0],pp[1],pp[2]};
	}



	private double _map(double v,double aa,double ab,double ba,double bb){
		return (v-aa)/(ab-aa)*(bb-ba)+ba;
	}
}