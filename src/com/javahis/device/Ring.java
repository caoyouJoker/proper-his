package com.javahis.device;

import java.applet.AudioClip;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JApplet;


public class Ring{
	
	public static final String SUCCESS = "C:/s.au";
	public static final String ERROR = "C:/e.au";
	public static final String REPEAT = "C:/r.au";
	
	AudioClip audio;
	
	public Ring(String path){
		// 加载声音文件
		URL mus=null;
		try {
			mus = new File(path).toURI().toURL();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		audio = JApplet.newAudioClip(mus);
	}
	
	/**
	 *<br>
	 * 方法说明：播放声音 <br>
	 * 输入参数： <br>
	 * 返回类型：
	 */
	public void play() {
		if (audio != null)
			stop();
		audio.play();
	}
	
	/**
	 *<br>
	 * 方法说明：循环播放声音 <br>
	 * 输入参数： <br>
	 * 返回类型：
	 */
	public void loop() {
		if (audio != null)
			audio.loop();
	}

	/**
	 *<br>
	 * 方法说明：停止播放声音 <br>
	 * 输入参数： <br>
	 * 返回类型：
	 */
	public void stop() {
		if (audio != null)
			audio.stop();
	}
}
