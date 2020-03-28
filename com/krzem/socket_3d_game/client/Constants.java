package com.krzem.socket_3d_game.client;



import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.lang.Math;
import java.util.Random;



public class Constants{
	public static final int DISPLAY_ID=0;
	public static final GraphicsDevice SCREEN=GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[DISPLAY_ID];
	public static final Rectangle WINDOW_SIZE=SCREEN.getDefaultConfiguration().getBounds();

	public static final double EPSILON=Math.ulp(1d);
	public static final Random RANDOM_GENERATOR=new Random();
	public static final double LINE_PARALLEL_BUFFER=0.01;

	public static final String SERVER_URL="192.168.178.73:7999";

	public static final double CAMERA_ROT_SPEED=0.075;
	public static final double CAMERA_MIN_EASE_DIFF=0.05;
	public static final double CAMERA_EASE_PROC=0.45;
	public static final double CAMERA_CAM_NEAR=0.1;
	public static final double CAMERA_CAM_FAR=1000;
	public static final double CAMERA_PLAYER_MAX_DIST=7;
	public static final double CAMERA_PLAYER_MAX_LOW_CEILING_DIST=2.5;
	public static final double CAMERA_PLAYER_Y_ADD=2.25;
	public static final double CAMERA_MIN_ANGLE=75;
	public static final double CAMERA_MAX_ANGLE=135;

	public static final double PLAYER_RADIUS=0.85;
	public static final int PLAYER_SHADOW_SEGMENTS=50;
	public static final double PLAYER_SHADOW_OPCAITY=25;
	public static final double PLAYER_SHADOW_Y_OFFSET=0.001;
	public static final double PLAYER_NAME_Y_OFFSET=2.5;
	public static final Color PLAYER_NAME_COLOR=new Color(238,238,238);
	public static final Color PLAYER_NAME_BG_COLOR=new Color(34,34,34,60);
	public static final double PLAYER_NAME_MAX_RENDER_DIST=100;
	public static final Font PLAYER_NAME_FONT=new Font("Consolas",Font.PLAIN,50);
}