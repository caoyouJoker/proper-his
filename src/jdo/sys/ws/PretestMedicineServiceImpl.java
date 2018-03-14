package jdo.sys.ws;

import java.sql.Timestamp;

import javax.jws.WebService;

import jdo.ind.INDSQL;
import jdo.ind.IndSysParmTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.StringTool;
import com.javahis.bsm.XmlUtils;

/**
 * <p>Title: 供盒装包药机呼叫接口实现301</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS</p>
 *
 * <p>Company:  </p>
 *
 * @author chenx  2013-05-22
 * @version 4.0
 */
@WebService
public class PretestMedicineServiceImpl implements IPretestMedicineService{


	/**
	 * 301方法的实现
	 */
	public String onPrepareFinish(String xml) {
		TParm parm = XmlUtils.createSendxmltoParm(xml);
		String message = "" ;
		int    status  = 0 ;
		if(parm.getValue("PRESC_NO").length()<0 || parm == null){
			message = "传送的处方号没有收到" ;
			return messageReturn(message, status) ;
		}
		String sql =  "      UPDATE OPD_ORDER SET PICK_TMIE = SYSDATE,LOCATION = '"+parm.getValue("LOCATION")+"' " +
				      "     WHERE RX_NO='"+parm.getValue("PRESC_NO")+"'" ;
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if(result.getErrCode()<0){
			message="配药失败" ;
			return messageReturn(message, status) ;
		}
		message ="配药已完成" ;
		status  = 1;
		return messageReturn(message, status);
	}

	/**
	 * 305方法实现，往IND_REQUESTM，IND_REQUESTD插入字段
	 */
	public String onRequestBox(String xml) {
		TParm result = new TParm() ;
		TParm parm = XmlUtils.createSendxml305toParm(xml) ;
		String returnxml = ""  ;
		System.out.println("parm========"+parm);
        TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
        String request = config.getString("", "BOXDEPT_CODE");  //申请部门
		//新增主表
		TParm parmMain =  new TParm() ;
		String requestNo= SystemTool.getInstance().getNo("ALL", "IND",
                "IND_REQUEST", "No");
		parmMain.setData("REQUEST_NO", requestNo) ; //申请单号
		Timestamp date = SystemTool.getInstance().getDate();
		parmMain.setData("REQTYPE_CODE", "DEP");//单据类别
		parmMain.setData("APP_ORG_CODE", request);//申请部门
		parmMain.setData("TO_ORG_CODE", this.onGetsupDept(request));//接受申请部门
		parmMain.setData("REQUEST_DATE",date);//申请日期
		parmMain.setData("REQUEST_USER", request);//申请人员
		parmMain.setData("REASON_CHN_DESC", "");//申请原因
		parmMain.setData("DESCRIPTION", "");//备注DRUG_CATEGORY
		parmMain.setData("DRUG_CATEGORY", "1");//普通药品
        String unit_type = "0";
            unit_type = IndSysParmTool.getInstance().onQuery().getValue(
                "UNIT_TYPE",
                0);
        parmMain.setData("UNIT_TYPE", unit_type);//申请单使用单位1:库存单位 2:配药单位 
        parmMain.setData("URGENT_FLG", "N");//紧急注记
        parmMain.setData("REGION_CODE", "H01");//区域代码
        parmMain.setData("OPT_USER", request);
        parmMain.setData("OPT_DATE", date);
        parmMain.setData("OPT_TERM", "172.20.11.89");
        TSocket socket = new TSocket("127.0.0.1", 8080, "web");//webgy->web
        result = TIOM_AppServer.executeAction(socket,
                "action.ind.INDRequestAction", "onBoxInsertM", parmMain);     
		if(result.getErrCode()<0){
			returnxml = this.messageReturn(result.getErrName()+result.getErrText(), 0) ;
			 return  returnxml ;
				}
		//============================保存细表
		TParm parmDetail =  new TParm() ;
		for(int i=0;i<parm.getCount("ORDER_CODE");i++){
			parmDetail.addData("REQUEST_NO", requestNo) ;//申请单号
			parmDetail.addData("SEQ_NO", i+1) ;//序号
			parmDetail.addData("ORDER_CODE", parm.getValue("ORDER_CODE", i)) ;//药品代码
			parmDetail.addData("QTY", parm.getValue("APPQUANTITY", i)) ;//数量APPQUANTITY
			parmDetail.addData("UNIT_CODE", "150") ;//单位代码，都是盒装药
			 TParm resultorderCode = new TParm(TJDODBTool.getInstance().select(INDSQL.
			            getPHAInfoByOrder(parm.getValue("ORDER_CODE", i))));
			        if (resultorderCode.getErrCode() < 0) {
			            return  this.messageReturn("药品信息错误", 0) ;
			        }
			        double stock_price = 0;
			        double retail_price = 0;
			  stock_price = StringTool.round(resultorderCode.getDouble("STOCK_PRICE", 0)
                            * resultorderCode.getDouble("DOSAGE_QTY", 0),
                            2);
               retail_price = StringTool.round(resultorderCode.getDouble("RETAIL_PRICE", 0)
                             * resultorderCode.getDouble("DOSAGE_QTY", 0),
                             2);
			parmDetail.addData("RETAIL_PRICE", stock_price) ;//零售价
			parmDetail.addData("STOCK_PRICE", retail_price) ;//成本价VALID_DATE
			parmDetail.addData("UPDATE_FLG", "0") ;//单据状态 0:已申请
			parmDetail.addData("BATCH_NO", "") ;//
			parmDetail.addData("ACTUAL_QTY", 0) ;//实际接收数量 (累计完成量)
			parmDetail.addData("OPT_USER", "040102") ;//
			parmDetail.addData("OPT_DATE", date) ;//
			parmDetail.addData("OPT_TERM", "172.20.11.89") ;//
		}
		result = TIOM_AppServer.executeAction(socket,
                "action.ind.INDRequestAction", "onInsertD", parmDetail);  
		if(result.getErrCode()<0){
			returnxml = this.messageReturn(result.getErrName()+result.getErrText(), 0) ;
			 return  returnxml ;
				}
		returnxml = this.messageReturn("成功", 1) ;
		return  returnxml ;
	}
	/**
	 * 返回信息
	 * @param status
	 * @return
	 */
	private String messageReturn(String message,int status) {
		String xml = XmlUtils.createParmtoXml(message, status).toString() ;
		return xml ;
	}
	/**
	 * 获取供应部门
	 */
	public String onGetsupDept(String request){
		String supdept= "" ;
		String sql = "SELECT SUP_ORG_CODE FROM IND_ORG WHERE ORG_CODE = '"+request+"'"   ;
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql)) ;
        if(parm.getErrCode()<0){
        	return supdept  ;
        }
        supdept = parm.getValue("SUP_ORG_CODE", 0) ;
		return supdept ;
	}
}
