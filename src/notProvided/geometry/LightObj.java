package notProvided.geometry;

import windowing.graphics.Color;



public class LightObj {
	
	private Color lightColor;
	private double ac_A;
	private double ac_B;
	private double[] lightSourcePoint;

	public LightObj(Color lightColor, double ac_A, double ac_B, double[] lightSourcePoint){
		this.lightColor = lightColor;
		this.ac_A = ac_A;
		this.ac_B = ac_B;
		this.lightSourcePoint = lightSourcePoint;
	}
	public Color getLightColor(){
		return lightColor;
	}

	public double getAc_B(){
		return ac_B;
	}

	public double getAc_A(){
		return ac_A;
	}

	public double[] getLightSourcePoint(){
		return lightSourcePoint;
	}


};