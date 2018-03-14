package jdo.clp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.dongyang.jdo.*;
import com.dongyang.ui.TWord;
import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 单病种工具类</p> 
 *
 * <p>Description: 单病种工具类</p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: BlueCore </p>
 *
 * @author WangLong 20120926
 * @version 1.0
 */
public class CLPSingleDiseTool extends TJDOTool {

    private static CLPSingleDiseTool instance = null;//实例
    private TWord word;// WORD控件
    
    /**
     * 返回实例
     */
    public static CLPSingleDiseTool getInstance() {
        if (instance == null) {
            instance = new CLPSingleDiseTool();
        }
        return instance;
    }
    
    private CLPSingleDiseTool() {
        this.setModuleName("clp\\CLPSingleDiseModule.x");
        onInit();
    }

	/**
	 * 获取急诊病患历次单病种信息（用于“合并”窗口）
	 */
	public TParm queryEMRHistoryForMerge(TParm parm) {
		TParm result = query("queryEMRHistoryForMerge", parm);//parm中加入MR_NO
		if (result.getErrCode() < 0)
			err(parm.getErrCode() + " " + parm.getErrText());
		return result;
	}
	
	/**
	 * 获取急诊病患基本信息
	 */
	public TParm queryPatInfoFromE(TParm parm) {
		TParm result=new TParm();
		if(parm.getData("CASE_NO")!=null){
			TParm diagResult = query("queryPatDiagFromE", parm);
            result = query("querySinglePatInfoFromE", parm);
            result.addData("ICD_CODE", diagResult.getValue("ICD_CODE", 0));
            result.addData("ICD_CHN_DESC", diagResult.getValue("ICD_CHN_DESC", 0));
		}else{
			 result = query("queryPatInfoFromE", parm);//parm中加入CASE_NO,MR_NO,START_DATE,END_DATE,DEPT_CODE
		}
		if (result.getErrCode() < 0)
			err(parm.getErrCode() + " " + parm.getErrText());
		return result;
	}
	
	/**
	 * 获取住院病患基本信息
	 */
	public TParm queryPatInfoFromI(TParm parm) {
		TParm result=new TParm();
		if(parm.getData("CASE_NO")!=null){
			TParm diagResult = query("queryPatDiagFromI", parm);
            result = query("querySinglePatInfoFromI", parm);
            result.addData("ICD_CODE", diagResult.getValue("ICD_CODE", 0));
            result.addData("ICD_CHN_DESC", diagResult.getValue("ICD_CHN_DESC", 0));
		}else{
			result = query("queryPatInfoFromI", parm);//parm中加入CASE_NO,MR_NO,START_DATE,END_DATE,DEPT_CODE
		}
		if (result.getErrCode() < 0)
			err(parm.getErrCode() + " " + parm.getErrText());
		return result;
	}
	
	/**
	 * 获取住院病患的来源ADM_SOURCE和入院状态
	 */
	public TParm queryASandPCFromI(TParm parm) {
		TParm result = query("queryASandPCFromI", parm);//parm中加入CASE_NO
		if (result.getErrCode() < 0)
			err(parm.getErrCode() + " " + parm.getErrText());
		return result;
	}
	
	/**
	 * 查询挂号表中的单病种种类
	 */
	public TParm queryREGAdmSDInfo(TParm parm) {
		TParm result = query("queryREGAdmSDInfo", parm);//parm中加入CASE_NO
		if (result.getErrCode() < 0)
			err(parm.getErrCode() + " " + parm.getErrText());
		return result;
	}
	
	/**
	 * 查询预约住院表中的单病种种类
	 */
	public TParm queryADMResvSDInfo(TParm parm) {
		TParm result = query("queryADMResvSDInfo", parm);//parm中加入CASE_NO
		if (result.getErrCode() < 0)
			err(parm.getErrCode() + " " + parm.getErrText());
		return result;
	}
	
	/**
	 * 查询住院主档中的单病种种类
	 */
	public TParm queryADMInpSDInfo(TParm parm) {
		TParm result = query("queryADMInpSDInfo", parm);//parm中加入CASE_NO
		if (result.getErrCode() < 0)
			err(parm.getErrCode() + " " + parm.getErrText());
		return result;
	}
	
	/**
	 * 更新挂号表，将单病种信息插入
	 */
	public TParm updateREGAdmSDInfo(TParm parm) {
		//System.out.println("=====插入数据========"+parm);
		TParm result = update("updateREGAdmSDInfo", parm);//parm中加入CASE_NO,DISE_CODE
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * 更新预约住院表，将单病种信息插入
	 */
	public TParm updateADMResvSDInfo(TParm parm) {
		//System.out.println("=====插入数据========"+parm);
		TParm result = update("updateADMResvSDInfo", parm);//parm中加入CASE_NO,DISE_CODE
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * 更新住院主档，将单病种信息插入
	 */
	public TParm updateADMInpSDInfo(TParm parm) {
		//System.out.println("=====插入数据========"+parm);
		TParm result = update("updateADMInpSDInfo", parm);//parm中加入CASE_NO,DISE_CODE
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * 删除挂号表中的单病种信息
	 */
	public TParm deleteREGAdmSDInfo(TParm parm) {
		//System.out.println("=====插入数据========"+parm);
		TParm result = update("deleteREGAdmSDInfo", parm);//parm中加入CASE_NO
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * 删除预约住院表中的单病种信息
	 */
	public TParm deleteADMResvSDInfo(TParm parm) {
		//System.out.println("=====插入数据========"+parm);
		TParm result = update("deleteADMResvSDInfo", parm);//parm中加入CASE_NO
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * 删除住院主档中的单病种信息
	 */
	public TParm deleteADMInpSDInfo(TParm parm) {
		//System.out.println("=====插入数据========"+parm);
		TParm result = update("deleteADMInpSDInfo", parm);//parm中加入CASE_NO
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	
	/**
	 * 查询单病种病历信息
	 */
	public TParm querySDData(TParm parm) {
		TParm result =new TParm();
		if (parm.getData("ADM_TYPE") == null) {
			result = query("querySDDataFromE", parm);// ADM_TYPE、DISE_CODE
			TParm eResult = query("querySDDataFromI", parm);// START_DATE、END_DATE、ADM_TYPE、DISE_CODE
			for (int i = 0; i < eResult.getCount("CASE_NO"); i++) {
				result.addRowData(eResult, i);
			}
		} else if (parm.getData("ADM_TYPE").equals("E")) {
			result = query("querySDDataFromE", parm);// ADM_TYPE、DISE_CODE
		} else if (parm.getData("ADM_TYPE").equals("I")) {
			result = query("querySDDataFromI", parm);// ADM_TYPE、DISE_CODE
		}
		if (result.getErrCode() < 0)
			err(parm.getErrCode() + " " + parm.getErrText());
		return result;
	}
	

	/**
	 * 删除单病种病历信息
	 */
	public TParm deleteSDData(TParm parm,TConnection conn) {//CASE_NO
		TParm result = update("deleteSDData", parm, conn);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * 插入单病种病历信息
	 */
	public TParm insertSDData(TParm parm,TConnection conn) {
		//System.out.println("=====插入数据========"+parm);
		TParm result = update("insertSDData", parm, conn);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * 单病种病历文件记录清除
	 */
	public TParm clearSDFileHistory(TParm parm,TConnection conn) {
		//System.out.println("=====插入数据========"+parm);
		TParm result = update("clearSDFileHistory", parm, conn);//parm中加入CASE_NO
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * 单病种病历文件记录合并
	 */
	public TParm copySDFileHistory(TParm parm,TConnection conn) {
		TParm actionParm=new TParm();
		actionParm.setData("CASE_NO",parm.getData("CASE_NO"));
		actionParm.setData("CASE_NO_OLD",parm.getData("CASE_NO_OLD"));
		//System.out.println("=====插入数据========"+parm);
		TParm result = update("copySDFileHistory", actionParm, conn);//parm中加入CASE_NO_OLD,CASE_NO
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * 单病种主档记录清除
	 */
	public TParm clearSDDBHistory(TParm parm,TConnection conn) {
		//System.out.println("=====插入数据========"+parm);
		TParm result = update("clearSDDBHistory", parm, conn);//parm中加入CASE_NO
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 单病种主档记录合并
	 */
	public TParm copySDDBHistory(TParm parm,TConnection conn) {
		TParm actionParm=new TParm();
		actionParm.setData("CASE_NO",parm.getData("CASE_NO"));
		actionParm.setData("CASE_NO_OLD",parm.getData("CASE_NO_OLD"));
		//System.out.println("=====插入数据========"+parm);
		TParm result = update("copySDDBHistory", actionParm, conn);//parm中加入CASE_NO_OLD,CASE_NO
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * 复制单病种病历文件
	 */
	public boolean copySDEMRFile(TParm parm){
		TParm result = query("querySDEMRFile", parm);//查询单病种的病历文件信息，用于文件复制操作
		if ((result.getErrCode() < 0)||(result.getCount()<1)) {
			return false;
		}
		int i = 0;
		String newFilePath="";
		String newFileName="";
		for (; i < result.getCount("FILE_PATH"); i++) {
			newFilePath="JHW\\"+parm.getValue("CASE_NO").substring(0, 2)+ File.separator+parm.getValue("CASE_NO").substring(2, 4)+File.separator +result.getValue("MR_NO",i);
			newFileName=parm.getValue("CASE_NO")+result.getValue("FILE_NAME", i).substring(12);
			String srcFilePath = TConfig.getSystemValue("FileServer.Main.Root") + File.separator  + TConfig.getSystemValue("EmrData") + File.separator + result.getValue("FILE_PATH", i);
			String srcFileName = result.getValue("FILE_NAME", i) + ".jhw";
			//word.onOpen(result.getValue("FILE_PATH", i), result.getValue("FILE_NAME", i), 3, true);
			result.setData("FILE_PATH", i, newFilePath);
			result.setData("FILE_NAME", i, newFileName);
			String destFilePath=TConfig.getSystemValue("FileServer.Main.Root") + File.separator  + TConfig.getSystemValue("EmrData") + File.separator + newFilePath;
			String destFileName=newFileName + ".jhw";
			if(!fileCopy(srcFilePath+File.separator +srcFileName, destFilePath+File.separator +destFileName)){//复制jhw文件
				break;
			}
            if (!fileCopy(srcFilePath.replaceFirst("JHW", "XML") + File.separator
                                  + srcFileName.replaceFirst("\\.jhw", "\\.xml"),
                          destFilePath.replaceFirst("JHW", "XML") + File.separator
                                  + destFileName.replaceFirst("\\.jhw", "\\.xml"))) {//复制xml文件
                break;
            }
            if (!fileCopy(srcFilePath.replaceFirst("JHW", "XML") + File.separator
                                  + srcFileName.replaceFirst("\\.jhw", "\\_CDA.xml"),
                          destFilePath.replaceFirst("JHW", "XML") + File.separator
                                  + destFileName.replaceFirst("\\.jhw", "\\_CDA.xml"))) {//复制后缀为_CDA的xml文件
                break;
            }
		}
		if (i != result.getCount("FILE_PATH")) {//出错时rollback
			for (int j=0; j < i; j++) {
				String filePath = TConfig.getSystemValue("FileServer.Main.Root") + File.separator  + TConfig.getSystemValue("EmrData") + result.getValue("FILE_PATH", i);
				String fileName = result.getValue("FILE_NAME", i) + ".jhw";
                new File(filePath + File.separator + fileName).delete();//删除复制过来的jhw文件
                new File(filePath.replaceFirst("JHW", "XML") + File.separator
                        + fileName.replaceFirst("\\.jhw", "\\.xml")).delete();//删除复制过来的xml文件
                new File(filePath.replaceFirst("JHW", "XML") + File.separator
                        + fileName.replaceFirst("\\.jhw", "\\_CDA.xml")).delete();//删除复制过来后缀为_CDA的xml文件
			}
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * 复制文件(目标文件存在则覆盖内容)
	 * @param srcFileName
	 * @param destFileName
	 * @return
	 */
	public boolean fileCopy(String srcFileName, String destFileName) {
		File srcFile = new File(srcFileName);
		File targetFile = new File(destFileName);
		try {
			InputStream in = new FileInputStream(srcFile);
			OutputStream out = new FileOutputStream(targetFile);
			byte[] buffer = new byte[1024];
			int byteread = 0; // 读取文件的字节数
			while ((byteread = in.read(buffer)) != -1) {
				out.write(buffer, 0, byteread);
			}
			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	



}
