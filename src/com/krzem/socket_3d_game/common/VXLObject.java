package com.krzem.socket_3d_game.common;



import java.util.ArrayList;
import java.util.List;



public class VXLObject{
	public int ox;
	public int oz;
	public List<int[]> dt;
	public List<int[][]> cl_dt;
	public String src=null;



	public VXLObject(int ox,int oz){
		this.ox=ox;
		this.oz=oz;
		this.dt=new ArrayList<int[]>();
		this.cl_dt=new ArrayList<int[][]>();
	}



	public void add(int x,int y,int z,int w,int h,int d,int ci,int dr){
		this.dt.add(new int[]{x,y,z,w,h,d,ci,dr});
	}



	public void add_cl(int r0,int g0,int b0,int r1,int g1,int b1,int r2,int g2,int b2,int r3,int g3,int b3,int r4,int g4,int b4,int r5,int g5,int b5){
		this.cl_dt.add(new int[][]{{r0,g0,b0},{r1,g1,b1},{r2,g2,b2},{r3,g3,b3},{r4,g4,b4},{r5,g5,b5}});
	}



	public int index(int x,int cy,int z){
		int i=0;
		for (int[] b:this.dt){
			if (b[1]==cy){
				if (b[0]==x&&b[2]==z){
					return i;
				}
				i++;
			}
		}
		return -1;
	}



	public int remove(int x,int y,int z){
		int i=0;
		for (int[] b:this.dt){
			if (b[0]==x&&b[1]==y&&b[2]==z){
				return this.dt.remove(i)[6];
			}
			i++;
		}
		return -1;
	}



	public int ceiling_y(int x,int my,int z){
		int ch=-1;
		for (int[] b:this.dt){
			if (b[1]>=my&&b[0]==x&&b[2]==z){
				if (ch==-1||b[1]<ch){
					ch=b[1]+0;
				}
			}
		}
		return ch;
	}



	public List<double[][]> collision(int y){
		List<double[][]> o=new ArrayList<double[][]>();
		for (int[] b:this.dt){
			if (b[1]==y){
				o.add(new double[][]{{this.ox*32+b[0],this.oz*32+b[2]},{this.ox*32+b[0]+b[3],this.oz*32+b[2]},{this.ox*32+b[0]+b[3],this.oz*32+b[2]+b[5]},{this.ox*32+b[0],this.oz*32+b[2]+b[5]}});
			}
		}
		return o;
	}
}