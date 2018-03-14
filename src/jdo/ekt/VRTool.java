package jdo.ekt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import com.dongyang.data.TParm;


/**
 * 
 * @author zhangp
 *
 */
public class VRTool {
	
	private final String URL = "C:\\JavaHis\\";
	private final String NAME = "VR.txt";
	private final String ENCODING = "GBK";
	private final String SWITCHNAME = "SWITCH.txt";
	
	public void write(String seq, String mrNo, String cType,
			double balance){
		try {
			File file = new File(URL);
			if (!file.exists()) {
				file.mkdir();
			}
			String filename = URL + NAME;
			file = new File(filename);
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
				}
			} else {
				file.delete();
				file = new File(filename);
				file.createNewFile();
			}
			FileWriter fr = new FileWriter(filename);
			BufferedWriter bw = new BufferedWriter(fr);
			DecimalFormat decimalFormat = new DecimalFormat("########0.00");
			bw.write(seq + "#" + mrNo + "#" + cType + "#" + decimalFormat.format(balance));
			bw.flush();
			bw.close();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
	}
	
	public TParm read() {
		TParm p = new TParm();
		try {
			File file = new File(URL);
			if (!file.exists()) {
				file.mkdir();
			}
			String filename = URL + NAME;
			file = new File(filename);
			InputStreamReader read = new InputStreamReader(
					new FileInputStream(file), ENCODING);// 考虑到编码格式
			BufferedReader bufferedReader = new BufferedReader(read);
			String lineTxt = null;
			if ((lineTxt = bufferedReader.readLine()) != null) {
				String[] ls = lineTxt.split("#");
//				cardDTO = new CardDTO(ls[0], ls[1], ls[2], ls[3]);
				p.setData("SEQ", ls[0]);
				p.setData("MR_NO", ls[1]);
			}
			read.close();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
		return p;
	}
	
	public boolean getVrSwitch(){
		try {
			File file = new File(URL);
			if (!file.exists()) {
				file.mkdir();
			}
			String filename = URL + SWITCHNAME;
			file = new File(filename);
			if (!file.exists()) {
				return false;
			}
			InputStreamReader read = new InputStreamReader(
					new FileInputStream(file), ENCODING);// 考虑到编码格式
			BufferedReader bufferedReader = new BufferedReader(read);
			String lineTxt = null;
			while ((lineTxt = bufferedReader.readLine()) != null) {
				if("Y".equals(lineTxt)){
					return true;
				}else{
					return false;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}
}
