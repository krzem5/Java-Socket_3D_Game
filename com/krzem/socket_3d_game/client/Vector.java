package com.krzem.socket_3d_game.client;



import com.jogamp.opengl.GL2;
import java.lang.Math;



public class Vector extends Constants{
	public double x;
	public double y;



	public Vector(double x,double y){
		this.x=x;
		this.y=y;
	}



	public Vector clone(){
		return new Vector(this.x,this.y);
	}



	public void vertex(GL2 gl){
		gl.glVertex2f((float)this.x,(float)this.y);
	}



	public Vector add(double x,double y){
		return new Vector(this.x+x,this.y+y);
	}



	public Vector rotate(Vector o,double a){
		double x=(this.x-o.x)*Math.cos(a)-(this.y-o.y)*Math.sin(a)+o.x;
		double y=(this.x-o.x)*Math.sin(a)+(this.y-o.y)*Math.cos(a)+o.y;
		return new Vector(x,y);
	}



	public double angle(){
		return Math.atan2(this.y,this.x);
	}



	@Override public String toString(){
		return String.format("Vector(x=%f, y=%f)",this.x,this.y);
	}
}