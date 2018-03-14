package jdo.emr;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

import jdo.sys.Operator;

/**
 * 介入室医生-胸痛中心病历
 * @author WangQing 20170307
 *
 */
public class EMROpeDrStationTool extends TJDOTool {
	private static EMROpeDrStationTool instanceObject;
	/**
	 * 构造器
	 */
	public EMROpeDrStationTool(){
		
	}
	
	/**
	 * 单例方法
	 * @return
	 */
	public static synchronized EMROpeDrStationTool getInstance() {
        if (instanceObject == null) {
            instanceObject = new EMROpeDrStationTool();
        }
        return instanceObject;
    }

	/**
     * 得到介入室医生模板
     * @return
     */
    public String[] getOpeDrStationEmrTemplet(){
    	// 模板代码
    	String subclassCode = TConfig.getSystemValue("AMI_IRDR_SUBCLASSCODE");    	
    	// 生成sql
    	String sql = "SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH," +
        		"SEQ,EMT_FILENAME FROM EMR_TEMPLET WHERE SUBCLASS_CODE='"+subclassCode+"'";
    	TParm result = new TParm();
    	result = new TParm(TJDODBTool.getInstance().select(sql));
    	String[] s = null;
        if (result.getCount("CLASS_CODE") > 0) {
            s = new String[] {
                result.getValue("TEMPLET_PATH", 0),
                result.getValue("SUBCLASS_DESC", 0),
                result.getValue("SUBCLASS_CODE", 0)};
        }
        return s;
    }
    
    
    /**
     * 保存介入室医生病历
     * @param caseNo String 就诊号
     * @param mrNo String 病案号
     * @param subclassCode String 病例模板代码
     * @param name String 病例模板名
     * @return TParm
     */
    public TParm saveOpeDrStationEmrFile(String caseNo,String mrNo,String subclassCode,String name)
    {
        TParm result = new TParm();
        TParm action = new TParm(TJDODBTool.getInstance().select("SELECT NVL(MAX(FILE_SEQ)+1,1) AS MAXFILENO FROM EMR_FILE_INDEX WHERE CASE_NO='"+caseNo+"'"));
        String indexStr = "01";
        String classCode = TConfig.getSystemValue("AMI_IRDR_CLASSCODE");
        int index = action.getInt("MAXFILENO",0);
        if(index<10){
           indexStr = "0"+index;
        }else{
            indexStr = ""+index;
        }
//        String mrNo="";
        String fileName = caseNo+"_"+name+"_"+indexStr;
        String filePath = "JHW"+"\\"+caseNo.substring(0,2)+"\\"+caseNo.substring(2,4)+"\\"+caseNo;
        TParm saveParm = new TParm( TJDODBTool.getInstance().update("INSERT INTO EMR_FILE_INDEX (CASE_NO, FILE_SEQ, MR_NO, IPD_NO, FILE_PATH,FILE_NAME, DESIGN_NAME, CLASS_CODE, SUBCLASS_CODE, DISPOSAC_FLG,OPT_USER, OPT_DATE, OPT_TERM,CREATOR_USER) VALUES "+
                                " ('"+caseNo+"', '"+indexStr+"', '"+mrNo+"', '', '"+filePath+"', "+
                                " '"+fileName+"', '"+fileName+"', '"+classCode+"', '"+subclassCode+"', 'N',"+
                                " '"+Operator.getID()+"', SYSDATE, '"+Operator.getIP()+"','"+Operator.getID()+"')"));
        if(saveParm.getErrCode()<0)
            return saveParm;
        result.setData("PATH",filePath);
        result.setData("FILENAME",fileName);
        return result;
    }
    
    /**
     * 拿到之前保存的介入室医生病历
     * @param caseNo String
     * @return String[]
     */
    public String[] getOpeDrStationEmrFile(String caseNo)
    {
        String classCode = TConfig.getSystemValue("AMI_IRDR_CLASSCODE");
        String subclassCode = TConfig.getSystemValue("AMI_IRDR_SUBCLASSCODE");

        TParm emrParm = new TParm(TJDODBTool.getInstance().select("SELECT FILE_PATH,FILE_NAME,SUBCLASS_CODE FROM EMR_FILE_INDEX WHERE CASE_NO='"+caseNo+"' AND CLASS_CODE='"+classCode+"' AND SUBCLASS_CODE='"+subclassCode+"'"));
        //System.out.println("======已看诊  getGSTempletSql========="+"SELECT FILE_PATH,FILE_NAME,SUBCLASS_CODE FROM EMR_FILE_INDEX WHERE CASE_NO='"+caseNo+"' AND CLASS_CODE='"+classCode+"' AND SUBCLASS_CODE='"+subclassCode+"'");

        String dir="";
        String file="";
        String subClassCode = "";
        if(emrParm.getCount()>0){
            dir = emrParm.getValue("FILE_PATH",0);
            file = emrParm.getValue("FILE_NAME",0);
            subClassCode = emrParm.getValue("SUBCLASS_CODE",0);
        }

        String s[] = {dir,file,subClassCode};
        return s;
    }
    
    
    
    
    
    
    
	
	
	
	
}
