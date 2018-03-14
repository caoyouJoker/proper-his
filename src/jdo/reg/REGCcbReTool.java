package jdo.reg;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jdo.ekt.EKTIO;
import jdo.ins.INSMZConfirmTool;
import jdo.ins.INSOpdTJTool;
import jdo.ins.INSTJReg;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.StringTool;
/**
 * <p>
 * Title: 门诊建行医保退费
 * </p>
 * 		
 * <p>
 * Description: 门诊建行医保退费
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: blueocre
 * </p>
 * 
 * @author pangben 2012-9-06
 * @version 4.0
 */
public class REGCcbReTool extends TJDOTool{
	  /**
     * 实例
     */
    private static REGCcbReTool instanceObject;
    /**
     * 得到实例
     * @return PatAdmTool
     */
    public static REGCcbReTool getInstance() {
        if (instanceObject == null)
            instanceObject = new REGCcbReTool();
        return instanceObject;
    }

	/**
	 * 医保退费操作
	 */
	public boolean reSetInsSave(TParm opbParm,TControl control) {

		// 查询医保金额
		String sql = "SELECT PAY_INS_CARD,TOT_AMT,PAY_OTHER2 FROM BIL_OPB_RECP WHERE PRINT_NO='"
				+ opbParm.getValue("PRINT_NO") + "'";
		TParm bilParm = new TParm(TJDODBTool.getInstance().select(sql));

		TParm reSetInsParm = new TParm();
		TParm parm = new TParm();
		// System.out.println("---------------医保退费-------------:" + opbParm);
		parm.setData("CASE_NO", opbParm.getValue("CASE_NO"));
		parm.setData("INV_NO", opbParm.getValue("PRINT_NO"));
		parm.setData("RECP_TYPE", "OPB");// 收费类型

		// 查询是否医保 退费
		TParm result = INSOpdTJTool.getInstance().selectInsInvNo(parm);
		if (result.getErrCode() < 0) {
			return false;
		}
		if (result.getCount("CASE_NO") <= 0) {// 不是医保退费
			return true;
		}
		getInsValue(result,opbParm, bilParm,control);
		reSetInsParm.setData("CASE_NO",opbParm.getValue("CASE_NO"));// 就诊号
		reSetInsParm.setData("CONFIRM_NO", result.getValue("CONFIRM_NO", 0));// 医保就诊号
		reSetInsParm.setData("INS_TYPE", result.getValue("INS_TYPE"));// 医保就医类型
		reSetInsParm.setData("RECP_TYPE", "OPB");// 收费类型
		reSetInsParm.setData("UNRECP_TYPE", "OPBT");// 退费类型
		reSetInsParm.setData("OPT_USER", opbParm.getValue("OPT_USER"));// id
		reSetInsParm.setData("OPT_TERM", opbParm.getValue("OPT_TERM"));// ip
		reSetInsParm.setData("INV_NO", opbParm.getValue("PRINT_NO"));// 票据号
		reSetInsParm.setData("PAT_TYPE", opbParm.getValue("CTZ1_CODE"));// 身份
		reSetInsParm.setData("REGION_CODE", opbParm.getValue("NHI_NO"));// 身份

		result = INSTJReg.getInstance().insResetCommFunction(
				reSetInsParm.getData());

		if (result.getErrCode() < 0) {
			control.messageBox(result.getErrText());
			return false;
		}
		
		return true;
	}
	/**
	 * 医保获取数据
	 * 
	 * @return
	 */
	private void getInsValue(TParm resultParm, TParm opbParm,TParm sumParm,TControl control) {
		TParm insFeeParm = new TParm();
		insFeeParm.setData("CASE_NO", opbParm.getValue("CASE_NO"));// 退挂使用
		insFeeParm.setData("RECP_TYPE", "OPB");// 收费使用
		// insFeeParm.setData("CONFIRM_NO", resultParm.getValue("CONFIRM_NO",
		// 0));// 医保就诊号
		// ---写死了需要修改
		insFeeParm.setData("NAME", opbParm.getValue("PAT_NAME"));
		insFeeParm.setData("MR_NO", opbParm.getValue("MR_NO"));// 病患号
		TParm result = INSMZConfirmTool.getInstance().queryMZConfirmOne(
				insFeeParm);// 查询医保信息
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			return;
		}
		control.messageBox("票据号码:"+opbParm.getValue("PRINT_NO")+" 医保退费金额:"
				+ StringTool.round(sumParm.getDouble("PAY_INS_CARD", 0), 2)
				+ ","
				+ "建行退款:"
				+ (StringTool.round(sumParm.getDouble("TOT_AMT", 0)
						- sumParm.getDouble("PAY_INS_CARD", 0), 2)));
		if (result.getInt("INS_CROWD_TYPE", 0) == 1
				&& result.getInt("INS_PAT_TYPE", 0) == 1) {
			resultParm.setData("INS_TYPE", "1");// 医保就医类别
		} else if (result.getInt("INS_CROWD_TYPE", 0) == 1
				&& result.getInt("INS_PAT_TYPE", 0) == 2) {
			resultParm.setData("INS_TYPE", "2");// 医保就医类别
		} else if (result.getInt("INS_CROWD_TYPE", 0) == 2
				&& result.getInt("INS_PAT_TYPE", 0) == 2) {
			resultParm.setData("INS_TYPE", "3");// 医保就医类别
		}
	}
	/**
	 * 建行接口调用
	 * @param parm
	 * @param control
	 * @return
	 */
	public TParm getCcbRe(TParm parm){     		
		//socket通讯 ip写死127.0.0.1
		TSocket socket = new TSocket("127.0.0.1",8080,"web");
		TParm ektCcbParm=REGCcbTool.getInstance().getTradeInfo(parm);
		if (ektCcbParm.getErrCode()<0 || ektCcbParm.getCount()<=0) {
			ektCcbParm=new TParm();
			ektCcbParm.setErr(-1,"建行接口调用出现错误,请联系信息中心");
			return ektCcbParm;
		}
		//传参	
		TParm inParm = new TParm();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		inParm.setData("HIS_CODE", parm.getValue("NHI_NO")); //医院编码：000551
		inParm.setData("CARD_NO", ektCcbParm.getValue("CARD_NO",0)); //银行卡号 
		inParm.setData("PERSON_NO", ektCcbParm.getValue("PERSON_NO",0)); //个人编码  personal_no
		inParm.setData("O_TRAN_NO", parm.getValue("CASE_NO")); //原合同号 case_no	
		inParm.setData("O_STREAM_NO", ektCcbParm.getValue("BUSINESS_NO",0)); //原交易流水号 stream_no
		inParm.setData("O_PAY_SEQ", parm.getValue("RECEIPT_NO")); //原支付顺序号 RECEIPT_NO
		inParm.setData("TRAN_NO",parm.getValue("CASE_NO")); //合同号 与原合同号相同
		inParm.setData("AMOUNT", String.valueOf(parm.getDouble("AMT"))); //发生金额：自费病人为全部费用；医保病人为自费金额
		inParm.setData("DATE",  sf.format((Date)ektCcbParm.getData("OPT_DATE",0))); //原交易日期
		inParm.setData("RETURN_TYPE", "2"); //2-当日缴费退费、3-隔日缴费退费		
		inParm.setData("OPT_ID", parm.getValue("OPT_USER")); //操作员ID	
		inParm.setData("OPT_NAME", parm.getValue("OPT_NAME")); //操作员姓名
		inParm.setData("OPT_IP", parm.getValue("OPT_TERM")); //操作员IP
		inParm.setData("OPT_DATE", SystemTool.getInstance().getDate()); //操作时间
		inParm.setData("GUID", ektCcbParm.getValue("GUID",0)); //本次操作的唯一ID：用java生成一个40位伪随机数
		inParm.setData("TOKEN", ""); //空
		inParm.setData("EXECUTE_FLAG", "0");
		inParm.setData("EXECUTE_MESSAGE", ""); //空
		inParm.setData("ACCOUNT", ""); //空	
		//调用退费接口 DoReturn方法
		TParm resultData = TIOM_AppServer.executeAction(socket,"action.ccb.CBCClientAction",
				"DoReturn", inParm);
		if (resultData.getErrCode()<0) {
			return resultData;
		}
		Map<String, Object> m = (HashMap)resultData.getData();		 
		Map<String, Object> r = (HashMap)m.get("Result");		
		Map<String, Object> info = (HashMap)m.get("OperateInfo");	
		Map<String, Object> commit = (HashMap)m.get("ReturnData");							
		String streamNo = (String) commit.get("STREAM_NO");
		String flg = (String) r.get("EXECUTE_FLAG"); 
		String optName = (String) info.get("OPT_ID");
		String optIp = (String) info.get("OPT_IP");
		String guid = (String) info.get("GUID");							
		 //执行结果:0成功 1错误
		if (null!=resultData && "0".equals(flg)) {																					
			//回参							
			//resultData.getValue("STREAM_NO", 0); //此笔退费的stream_no
			//resultData.getValue("ACCOUNT", 0); //账期时间：回写ekt_ccb
			resultData.setData("CARD_NO",ektCcbParm.getValue("CARD_NO",0));
			resultData.setData("MR_NO",ektCcbParm.getValue("MR_NO",0));
			resultData.setData("CASE_NO",parm.getValue("CASE_NO"));
			resultData.setData("BUSINESS_NO",streamNo);					
			resultData.setData("PAT_NAME",ektCcbParm.getValue("PAT_NAME",0));
			resultData.setData("AMT",-parm.getDouble("AMT"));							
			resultData.setData("OPT_USER",optName);
			resultData.setData("OPT_TERM",optIp);
			resultData.setData("GUID",guid);
			resultData.setData("RECEIPT_NO",ektCcbParm.getValue("RECEIPT_NO",0));
		}else{
			//control.messageBox(resultData.getValue("EXECUTE_MESSAGE", 0));//错误信息
			resultData.setErr(-1,resultData.getValue("EXECUTE_MESSAGE", 0));
			return resultData;
		}
		return resultData;
	}
	/**
	 * 添加一条EKT_CCB_TREDE 退费数据
	 * @param parm
	 * @return
	 */
	public TParm saveEktCcbTrede(TParm parm){
		TParm ccbParm=new TParm();
		ccbParm.setData("TRADE_NO",SystemTool.getInstance().getNo("ALL",
				"EKT", "CCB_TRADE_NO", "CCB_TRADE_NO"));//流水号
		ccbParm.setData("CARD_NO",parm.getValue("CARD_NO"));//银行卡号
		ccbParm.setData("MR_NO",parm.getValue("MR_NO"));//病案号
		ccbParm.setData("CASE_NO",parm.getValue("CASE_NO"));//就诊号
		ccbParm.setData("BUSINESS_NO",parm.getValue("BUSINESS_NO"));//交易号
		ccbParm.setData("PAT_NAME",parm.getValue("PAT_NAME"));//病患名称
		ccbParm.setData("AMT",parm.getDouble("AMT"));//金额
		ccbParm.setData("STATE","2");//退费注记: 2
		ccbParm.setData("BUSINESS_TYPE",parm.getValue("BUSINESS_TYPE"));//退费类型
		ccbParm.setData("OPT_USER",parm.getValue("OPT_USER"));
		ccbParm.setData("OPT_DATE",SystemTool.getInstance().getDate());
		ccbParm.setData("OPT_TERM",parm.getValue("OPT_TERM"));
		ccbParm.setData("TOKEN","");
		ccbParm.setData("BANK_FLG","Y");								
		ccbParm.setData("GUID",parm.getValue("GUID"));//本次操作的唯一ID
		ccbParm.setData("RECEIPT_NO",parm.getValue("RECEIPT_NO"));//收据号码
		return REGCcbTool.getInstance().insertOpbCcbTrade(ccbParm);
	}
}

