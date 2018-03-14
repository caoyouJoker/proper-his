package action.sta;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import jdo.sta.StaGenMroDataTool;
import jdo.sta.StaGenMroDataTran;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.db.TDBPoolManager;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.patch.Patch;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: 医疗监管指标批次类
 * </p>
 * 
 * <p>
 * Description: 医疗监管指标批次类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author shibl
 * @version 1.0
 * 
 * 
 */
public class STAGenMroDataBatchPatch extends Patch {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

	public synchronized boolean run() {
		System.out.println("================= 医疗监管指标上传 run==========================");
		TConfig config = TConfig.getConfig("WEB-INF/config/system/TConfig.x");
		String SendFilePath = config.getString("ServerStaSendFilePath");
		TConnection conn =  TDBPoolManager.getInstance().getConnection();;
		String[] sourceFiles = TIOM_FileServer.listFile(TIOM_FileServer
				.getSocket(), SendFilePath);
		for (int i = 0; i < sourceFiles.length; i++) {// 循环
			String fileName=sourceFiles[i];
			byte[] data = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
					SendFilePath + fileName);
			if (data == null) {
				return true;
			}
			InputStream is = null;
			is = new ByteArrayInputStream(data);
			// 获取token串
			String token = null;
			//try {
				//token = StaGenMroDataTran.getInstance().getToken();
			//} catch (IOException e) {
				// TODO Auto-generated catch block
				//System.out.println("医疗监管获取token出错");
				//return false;
			//}
			// 发送zip文件
			if (!token.equalsIgnoreCase("") && is != null) {
				boolean flg = StaGenMroDataTran.getInstance().sendData(token,
						is);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
				if (flg) {
					TIOM_FileServer.deleteFile(TIOM_FileServer.getSocket(),
							SendFilePath + fileName);
					 if(!updateSendDate(fileName.substring(0, fileName.lastIndexOf(".")),conn)){
						 conn.rollback();
						 conn.close();
						 return false;
					 }else{
						 conn.commit();
						 conn.close();
					 }
					}
				} else {
					System.out.print("医疗监管上传发送失败");
					return false;
				}
				// 查看返回信息
				StaGenMroDataTran.getInstance().findResult(token);
			}
		return true;
	}
	/**
	 * 
	 * @param zipTime
	 * @param conn
	 * @return
	 */
	public boolean updateSendDate(String zipTime,TConnection conn){
		boolean flg=true;
		String DateStr = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyyMMddHHmmss");
		TParm result = StaGenMroDataTool.getInstance().updateSTASendFlg(zipTime, DateStr, "4", conn);
		if (result.getErrCode() < 0) {
			flg=false;
			conn.rollback();
			conn.close();
			return flg;
		}
		return flg;
	}
}
