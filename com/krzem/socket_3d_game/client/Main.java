package com.krzem.socket_3d_game.client;



import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;



public class Main{
	public static void main(String[] args) {
		new Main.Main_(args);
	}



	public static class Main_ extends Constants implements GLEventListener{
		public int MOUSE=0;
		public int MOUSE_COUNT=0;
		public int MOUSE_BUTTON=0;
		public Vector MOUSE_POS=new Vector(0,0);
		public int SCROLL_D=0;
		public Keyboard KEYBOARD;
		public Camera cam;
		public JFrame frame;
		public GLCanvas canvas;
		public Game g;
		public GLU glu=new GLU();
		private int _mouse;
		private int _mouseC;
		private int _mouseB;
		private MouseEvent _mouseM;
		private int _sc;
		private boolean _break=false;



		public Main_(String[] args){
			this.init();
			this.frame_init();
			this.run();
		}



		public void init(){
			this.cam=new Camera(this);
			this.KEYBOARD=new Keyboard(this);
			this.g=new Game(this);
		}



		public void frame_init(){
			Main.Main_ cls=this;
			this.frame=new JFrame("Socket 3D Game Client");
			this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.frame.setUndecorated(true);
			this.frame.setResizable(false);
			this.frame.addWindowListener(new WindowAdapter(){
				public void windowClosing(WindowEvent e){
					cls._quit();
				}
			});
			this.frame.addWindowFocusListener(new WindowAdapter(){
				public void windowGainedFocus(WindowEvent e){
					cls.cam._center=true;
				}
				public void windowLostFocus(WindowEvent e){
					cls.cam._center=false;
				}
			});
			this.canvas=new GLCanvas(new GLCapabilities(GLProfile.get(GLProfile.GL2)));
			this.canvas.addGLEventListener(this);
			this.canvas.setSize(WINDOW_SIZE.width,WINDOW_SIZE.height);
			this.canvas.setPreferredSize(new Dimension(WINDOW_SIZE.width,WINDOW_SIZE.height));
			this.canvas.addMouseListener(new MouseAdapter(){
				public void mousePressed(MouseEvent e){
					cls._mouse=1;
					cls._mouseC=e.getClickCount();
					cls._mouseB=e.getButton();
				}
				public void mouseReleased(MouseEvent e){
					cls._mouse=2;
					cls._mouseC=e.getClickCount();
					cls._mouseB=e.getButton();
				}
				public void mouseClicked(MouseEvent e){
					cls._mouse=3;
					cls._mouseC=e.getClickCount();
					cls._mouseB=e.getButton();
				}
			});
			this.canvas.addMouseMotionListener(new MouseMotionAdapter(){
				public void mouseMoved(MouseEvent e){
					cls._mouseM=e;
				}
				public void mouseDragged(MouseEvent e){
					cls._mouseM=e;
				}
			});
			this.canvas.addMouseWheelListener(new MouseWheelListener(){
				public void mouseWheelMoved(MouseWheelEvent e){
					if (e.getWheelRotation()<0){
						cls._sc=1;
					}
					else{
						cls._sc=-1;
					}
				}
			});
			this.canvas.addKeyListener(new KeyListener(){
				public void keyPressed(KeyEvent e){
					if (cls.KEYBOARD==null){
						return;
					}
					cls.KEYBOARD.down(e);
				}
				public void keyReleased(KeyEvent e){
					if (cls.KEYBOARD==null){
						return;
					}
					cls.KEYBOARD.up(e);
				}
				public void keyTyped(KeyEvent e){
					if (cls.KEYBOARD==null){
						return;
					}
					cls.KEYBOARD.press(e);
				}
			});
			this.frame.add(this.canvas);
			this.frame.setVisible(true);
			this.canvas.requestFocus();
			SCREEN.setFullScreenWindow(this.frame);
			this.canvas.repaint();
		}



		public void run(){
			this.cam._center=true;
			new FPSAnimator(this.canvas,1000,true).start();
		}



		public void update(GL2 gl){
			this.g.update(gl);
			this.cam.update();
			this.KEYBOARD.update();
		}



		public void draw(GL2 gl){
			gl.glClear(GL2.GL_COLOR_BUFFER_BIT|GL2.GL_DEPTH_BUFFER_BIT);
			gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT,GL2.GL_NICEST);
			gl.glEnable(GL2.GL_DEPTH_TEST);
			gl.glBlendFunc(GL2.GL_SRC_ALPHA,GL2.GL_ONE_MINUS_SRC_ALPHA);
			gl.glEnable(GL2.GL_COLOR);
			gl.glDepthFunc(GL2.GL_LEQUAL);
			gl.glClearColor(1,1,1,1);
			gl.setSwapInterval(1);
			this.cam.draw(gl);
			this.g.draw(gl);
			gl.glFlush();
		}



		@Override
		public void display(GLAutoDrawable drawable){
			try{
				this._update_events();
				this.update(drawable.getGL().getGL2());
				this.draw(drawable.getGL().getGL2());
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}



		@Override
		public void dispose(GLAutoDrawable drawable){

		}



		@Override
		public void init(GLAutoDrawable drawable){
			this.cam.setup(drawable.getGL().getGL2());
		}



		@Override
		public void reshape(GLAutoDrawable drawable,int x,int y,int w,int h){
			this.cam.setup(drawable.getGL().getGL2());
		}



		private void _update_events(){
			this.MOUSE=this._mouse+0;
			this.MOUSE_COUNT=this._mouseC+0;
			this.MOUSE_BUTTON=this._mouseB+0;
			if (this._mouse!=1){
				this._mouse=0;
				this._mouseC=0;
				this._mouseB=0;
			}
			if (this._mouseM!=null){
				this.MOUSE_POS=new Vector(this._mouseM.getPoint().x,this._mouseM.getPoint().y);
				this._mouseM=null;
			}
			this.SCROLL_D=this._sc+0;
			this._sc=0;
		}



		public void _quit(){
			if (this._break==true){
				return;
			}
			this._break=true;
			this.frame.dispose();
			this.frame.dispatchEvent(new WindowEvent(this.frame,WindowEvent.WINDOW_CLOSING));
		}
	}
}