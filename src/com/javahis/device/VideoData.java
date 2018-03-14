package com.javahis.device;


import java.io.*;

public class VideoData
    implements Serializable
{
  private int data[];
  private int red;
  private int green;
  private int blue;
  private int width;
  private int height;
  public VideoData()
  {

  }
  public VideoData(int red,int green,int blue,int width,int height)
  {
    this.red = red;
    this.green = green;
    this.blue = blue;
    this.width = width;
    this.height = height;
  }
  public int[] getData()
  {
    return data;
  }
  public void setData(int[] data)
  {
    this.data = data;
  }
  public int getRed()
  {
    return red;
  }
  public void setRed(int i)
  {
    red = i;
  }
  public int getGreen()
  {
    return green;
  }
  public void setGreen(int i)
  {
    green = i;
  }
  public int getBlue()
  {
    return blue;
  }
  public void setBlue(int i)
  {
    blue = i;
  }
  public int getWidth()
  {
    return width;
  }
  public void setWidth(int i)
  {
    width = i;
  }
  public int getHeight()
  {
    return height;
  }
  public void setHeight(int i)
  {
    height = i;
  }
  public String toString()
  {
    String s =
        "red = " + red + "," +
        "green = " + green + "," +
        "blue = " + blue + "," +
        "width = " + width + "," +
        "height = " + height + "," +
        "count = " + data.length;
    return s;

  }
}
