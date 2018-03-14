package jdo.spc.bsm.bean;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import jdo.sys.SystemTool;
import jdo.util.FtpClient;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;

/**
 * 宁河住院发药机
 * @author Administrator
 *
 */
public class SPCOdiSendMachine {
	
	/**
	 * 创建txt文件
	 * @param parm
	 * @throws IOException 
	 */
	public static  void createTxt(TParm parm) throws IOException{
		
		TParm result = new TParm() ;
		for(int i=0;i<parm.getCount();i++){
			result.addData("1", parm.getValue("PAT_NAME", i)) ;//病患姓名
			result.addData("2", parm.getValue("MR_NO", i)) ;//病案号
			result.addData("3", parm.getValue("STATION_DESC", i)) ;//病区
			result.addData("4", parm.getValue("VS_DR_CODE", i)) ;//经治医生姓名
			result.addData("5", "") ;//默认不填
			result.addData("6", "99") ;//默认
			result.addData("7", parm.getDouble("DOSAGE_QTY", i)) ;//用量 片
			result.addData("8", parm.getValue("ORDER_CODE", i)) ;//医嘱代码
			result.addData("9", parm.getValue("FREQ_CODE", i)) ;//频次
			result.addData("10", parm.getValue("START_DTTM", i).substring(2, 8)) ;//开始时间
			result.addData("11", parm.getValue("END_DTTM", i).substring(2, 8)) ;//结束时间（开始时间与结束时间是一个值）
			result.addData("12", "") ;//默认
			result.addData("13", parm.getValue("DR_NOTE", i)) ;//医嘱备注
			result.addData("14", parm.getValue("IPD_NO", i)) ;//住院号
			result.addData("15", "") ;//默认
			result.addData("16", parm.getValue("BIRTH_DATE", i).substring(0, 10)) ;//出生年月
			result.addData("17", parm.getValue("SEX_TYPE", i)) ;//性别
			result.addData("18", "") ;//病房号
			result.addData("19", parm.getValue("BED_NO", i)) ;//病床号
			result.addData("20", "0") ;//默认值0
			result.addData("21", "宁河县医院") ;//医院名称
			result.addData("22", "") ;//默认不填
			result.addData("23", "") ;//默认不填
			result.addData("24", "") ;//默认不填
			result.addData("25", "") ;//默认不填
			result.addData("26", "") ;//默认不填
			result.addData("27", parm.getValue("MR_NO", i)) ;//默认不填     填写住院号
			result.addData("28", parm.getValue("ORDER_NO", i)) ;//默认不填    填写医嘱号
			result.addData("29", parm.getValue("ORDER_SEQ", i)) ;//默认不填    填写序号
			result.addData("30", "") ;//默认不填
			result.addData("31", "") ;//默认不填
			result.addData("32", "") ;//默认不填
			result.addData("33", "") ;//默认不填
			result.addData("34", "") ;//默认不填
			result.addData("35", "") ;//默认不填
			result.addData("36", "") ;//默认不填
		}
	
		result.setCount(parm.getCount()) ;
			//路径
		TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
		String url = config.getString("", "SRC");  //服务器名称
		String date = SystemTool.getInstance().getDate().toString().substring(0, 19).replace("-", "") ;
		String src = "C:\\SPC\\spc.txt" ;
//		System.out.println("src======"+src);
			   if (parm != null) {
			    File f = new File(src);
			    BufferedWriter bw = null;
			    bw = new BufferedWriter(new FileWriter(f));
			    for (int i = 0; i < result.getCount(); i++) {
			    	String str = txtFormat(result.getRow(i)) ;
			//写入文件
			   bw.write(str + "\r\n");
			    }
			    bw.flush();  
			    bw.close();
			   } 
		//====将文件传送到ftp指定路径
			   FtpClient   ftpClient = new FtpClient();
				FileInputStream in = null;
				try {
					in = new FileInputStream(new File(src));
					ftpClient.uploadFile(url, 21, "javahis", "javahis", "C://MPS//OCSData//",date + ".txt", in);
				} catch (FileNotFoundException e) {
					System.out.println("错误信息===="+e.toString());
					e.printStackTrace();
				}
			
			
	}
		
		


	/**
	 * 转换txt文件格式
	 * @param parm
	 * @return
	 */
	public static String txtFormat(TParm parm){
		
		int[] size = {20, 30, 50, 26, 3,2,5,50,20,6,6,8,50,50,50,10,6,20,20,1,30,30,30,
				      30,30,30,30,30,30,30,30,30,30,30,30,30} ;
		String[] column = {"1","2","3","4","5","6","7","8","9","10","11","12",
				"13","14","15","16","17","18","19","20","21","22","23","24",
				"25","26","27","28","29","30","31","32","33","34","35","36"} ;
		String str = "";
		for(int i=0;i<column.length;i++){
			if(i==6){
				String strQty= StringTool.round(parm.getDouble(column[i]),2)+"" ;
				str += strQty +String.format("%1$0"+(5-strQty.length())+"d",0) ;
			}
			else	{
				int n = BinaryString(parm.getValue(column[i])) ;
				int stringSize = size[i]-n ;
				str += String.format("%"+"-"+stringSize+"s", parm.getValue(column[i]));
			}
		}
		return str ;
	}
	/**
	 *计算汉字个数
	 * @param args
	 */
	public static int BinaryString(String str) {
		String len="";
		int j=0;
		char[] c=str.toCharArray();
		for(int i=0;i<c.length;i++){
			len=Integer.toBinaryString(c[i]);
			if(len.length()>8)
				j++;
		}
		return j ;
	}

}
