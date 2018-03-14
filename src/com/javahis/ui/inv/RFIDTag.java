package com.javahis.ui.inv;

import java.io.Serializable;

import com.javahis.device.Uitltool;
/**
 *   
 * @author lixiang
 *
 */
public class RFIDTag implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 419882076939852969L;
	/**
	 * epc的值
	 */
	private String epc;
	/**
	 * rfid值
	 */
	private String readerid;
	/**
	 * 扫描到的天线
	 */
	private String antenna;
	/**
	 * 方向  + 入，  -为出
	 */
	private String direction;
	/**
	 *  发生时间
	 */
	private String time;
	
	private String textContent;
	/**
	 * 
	 * @return
	 */
	public String getEpc() {
		return Uitltool.decode(epc);
	}

	public void setEpc(String epc) {
		this.epc = epc;
	}

	public String getReaderid() {
		return readerid;
	}

	public void setReaderid(String readerid) {
		this.readerid = readerid;
	}

	public String getAntenna() {
		return antenna;
	}

	public void setAntenna(String antenna) {
		this.antenna = antenna;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTextContent() {
		return textContent;
	}

	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}
	
	
}
