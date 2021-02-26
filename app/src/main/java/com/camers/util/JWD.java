package com.camers.util;

public class JWD {
	private String X;//����
	private String Y;//γ��
	/**
	 * @return the x
	 */
	public JWD(){}
	public JWD(String x,String y){
		this.X = x;
		this.Y = y;
	}
	
	public JWD( double x,double y)
	{
		this.X = String.valueOf(x);
		this.Y = String.valueOf(y);
	}
	public String getX() {
		return X;
	}
	/**
	 * @param x the x to set
	 */
	public void setX(String x) {
		X = x;
	}
	/**
	 * @return the y
	 */
	public String getY() {
		return Y;
	}
	/**
	 * @param y the y to set
	 */
	public void setY(String y) {
		Y = y;
	}
	
}
