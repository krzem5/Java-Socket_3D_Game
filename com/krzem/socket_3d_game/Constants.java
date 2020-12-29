package com.krzem.socket_3d_game;



import java.lang.Math;
import java.util.Random;



public class Constants{
	public static final String CLIENT_CHECKSUM="ecb9c959b0573a750c5dbbc11311acef38dd98510a6d774edaa98704a2e0c534";

	public static final int CLIENT_RANDOM_ID_LENGTH=1024;

	public static final Random RANDOM_GENERATOR=new Random();
	public static final double EPSILON=Math.ulp(1d);

	public static final String MAP_CHUNK_FILE_PATH="/com/krzem/socket_3d_game/data/scene/";
	public static final double MAP_PLAYER_SPAWN_OFFEST_RANGE=5;

	public static final double PLAYER_SPEED=10;
	public static final double PLAYER_SPRINT_SPEED=15.5;
	public static final double PLAYER_VEL_MIN_EASE_DIFF=0.05;
	public static final double PLAYER_VEL_EASE_PROC=0.45;
	public static final double PLAYER_RADIUS=0.85;
	public static final int PLAYER_Y=1;
	public static final double PLAYER_MAX_ADD_SPEED=3;
	public static final double PLAYER_MIN_MOVEMENT_UPDATE=0.0001;
}
