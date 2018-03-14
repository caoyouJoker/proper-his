package jdo.sid;

import java.io.File;

import jdo.ekt.EKTIO;
import jdo.sys.PatTool;

import com.bluecore.cardreader.CardInfoBO;
import com.bluecore.cardreader.ICallBack;
import com.bluecore.cardreader.IdCardReaderUtil;
import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

public class IdCardO {
	/**
	 * 实例
	 */
	private static IdCardO instanceObject;
	/**
	 * 得到实例
	 * 
	 * @return EKTIO
	 */
	public static IdCardO getInstance() {
		if (instanceObject == null)
			instanceObject = new IdCardO();
		return instanceObject;
	}
	private String path = getEktPort();// 泰心医疗卡参数，默认病案号键值名称
	/**
	 * 读取身份证操作
	 * 参数:1.身份证图片放的位置 2.读卡信息
	 * @return
	 */
	public TParm readIdCard() {
		TParm parm = new TParm();
		CardInfoBO bo=null;
		try {
			bo=IdCardReaderUtil.getCardInfo(path);
		} catch (Exception e) {
			parm.setErr(-1,"重新获取信息");
			System.out.println("重新获取信息:"+e.getMessage());
			return parm;
			// TODO: handle exception
		}
        //CardInfoBO cardInfo = IdCardReaderUtil.getCardInfo(dir);
        if(bo ==null){
        	parm.setErr(-1,"未获得身份证信息,请重新操作");
        	return parm;
        }   
		parm.setData("PAT_NAME",bo.getName().trim());//姓名
		parm.setData("SEX_CODE",bo.getSex().trim().equals("男")?"1":"2");//性别
		parm.setData("BRITHPLACE",bo.getFolk().trim());//籍贯
		parm.setData("BIRTH_DATE",bo.getBirth().trim().substring(0,4)+"/"+bo.getBirth().trim().substring(4,6)+"/"+
				bo.getBirth().trim().substring(6,bo.getBirth().trim().length()));//生日
		parm.setData("IDNO",bo.getCode().trim());//身份证号
		parm.setData("RESID_ADDRESS",bo.getAdd().trim());//住址
		//通过身份证号查询病患信息
		TParm infoParm=PatTool.getInstance().getInfoForIdNo(parm);
		if(infoParm.getCount()>0){
			parm=infoParm;
			//parm.setData("MESSAGE","已存在此就诊病患信息");
		}else{
			parm.setData("MESSAGE","不存在此就诊病患信息");
		}
		delFolder(path);
		return parm;
	}
	/**
	 * 得到debug标记
	 * 
	 * @return
	 */
	public String getEktPort() {
		String com = "";
		com = getProp().getString("", "sid.path");
		if (com == null || com.trim().length() <= 0) {
			//System.out.println("配置文件医疗卡com标记错误！");
		}
		return com;
	}

	/**
	 * 读取 TConfig.x
	 * 
	 * @return TConfig
	 */
	public static TConfig getProp() {
		TConfig config = TConfig
				.getConfig("WEB-INF\\config\\system\\TConfig.x");
		if (config == null) {
			//System.out.println("TConfig.x 文件没有找到！");
		}
		return config;
	}
	// 删除文件夹
	// param folderPath 文件夹完整绝对路径

	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 删除指定文件夹下所有文件
	// param path 文件夹完整绝对路径
	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
				flag = true;
			}
		}
		return flag;
	}
}
