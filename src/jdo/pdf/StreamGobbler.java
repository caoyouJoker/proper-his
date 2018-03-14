package jdo.pdf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

/**
 * 
 * @author lixiang
 *
 */
public class StreamGobbler extends Thread {
	private InputStream is;
	private String type;
	private Vector<String> errors = new Vector<String>();
	
	public StreamGobbler(InputStream is, String type) {
		this.is = is;
		this.type = type;
	}
	

	public Vector<String> getErrors() {
		return errors;
	}


	public void setErrors(Vector<String> errors) {
		this.errors = errors;
	}


	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null){
				//System.out.println(type + ">" + line);
				if(type.equals("Error")){
					this.getErrors().add(line);
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

}
