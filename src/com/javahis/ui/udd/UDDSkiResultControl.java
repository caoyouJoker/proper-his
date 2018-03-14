package com.javahis.ui.udd;

import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.javahis.ui.sys.SYSDeptControl;
/**
 * <p>
 * Title: 皮试批号修改
 * </p>
 * 
 * <p>
 * Description: 住院配药操作皮试药品可以修改皮试批号
 * </p>
 * 
 * <p>
 * Copyright: JAVAHIS
 * </p>
 * 
 * @author pangben 2015-4-13
 * @version 1.0
 */
public class UDDSkiResultControl extends TControl{
	private TParm parmmeter;//接受护士执行界面的参数
	private String caseNo = "";//就诊号
	private String orderCode = "";//药嘱代码
	//private String phl="";//add caoyong 2014/03/27静点执行
	private String batchNo;
	private String orderNo;
	private String orderSeq;
	private String orgCode; //add wukai on 20170412 执行科室
	/**
	 * 初始化参数
	 */
	public void onInit() {
		this.parmmeter = new TParm();
		Object obj = this.getParameter();
		if (obj.toString().length() != 0 || obj != null) {
			this.parmmeter = (TParm) obj;
			this.setValue("BATCH_NO", parmmeter.getValue("BATCH_NO"));//初始化批号
			//this.setValue("SKINTEST_NOTE", parmmeter.getValue("SKINTEST_FLG"));//初始化皮试结果
		}
		//phl=parmmeter.getValue("PHL");//add caoyong 2014/03/27静点执行
		caseNo = parmmeter.getValue("CASE_NO");
		orderCode = parmmeter.getValue("ORDER_CODE");
		batchNo= parmmeter.getValue("BATCH_NO");
		orderNo=parmmeter.getValue("ORDER_NO");
		orderSeq=parmmeter.getValue("ORDER_SEQ");
		orgCode = parmmeter.getValue("ORG_CODE");
		//皮试结果 add caoyong 2014/4/3 end 
	}
	/**
	 * 皮试批号修改
	 */
	public void onFecth() {
		TParm result = new TParm();
		String skiNo = this.getValueString("BATCH_NO");// 获取界面的皮试批号
		if (skiNo.equals(null) || "".equals(skiNo)) {
			this.messageBox("皮试批号不能为空。");
			return;
		}
		
		//add by wukai on 20170412 添加 批号校验 start
		String sql = "SELECT BATCH_NO FROM IND_STOCK WHERE ORG_CODE = '" + orgCode + "' AND ORDER_CODE = '" + orderCode + "'";
		
		String sql1 = sql + " AND BATCH_NO = '" + skiNo + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql1));
		
		if(parm == null || parm.getErrCode() < 0 || parm.getCount("BATCH_NO") <= 0) {
			parm = new TParm(TJDODBTool.getInstance().select(sql));
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < parm.getCount("BATCH_NO"); i ++) {
				sb.append(parm.getValue("BATCH_NO", i) + "  ");
			}
			this.messageBox_("此药品库存不存在批号：【" + skiNo + "】\n可用的批号有：" + sb.toString());
			return;
		}
		//add by wukai on 20170412 添加 批号校验 end
		
		if (!checkPW()) {
			return;
		}
		result.addData("BATCH_NO", skiNo);
		this.setReturnValue(result);
		TParm inParm = new TParm();
		inParm.setData("ORDER_CODE", orderCode);
		inParm.setData("BATCH_NO", skiNo);
		inParm.setData("CASE_NO", caseNo);
		inParm.setData("ORDER_NO", orderNo);//
		inParm.setData("ORDER_SEQ", orderSeq);//
		inParm.setData("OPT_USER", Operator.getID());//
		inParm.setData("OPT_TERM", Operator.getIP());//
		// 调用action执行事务
		TParm result1 = TIOM_AppServer.executeAction(
				"action.inw.InwOrderExecAction", "insterUddSkinNote", inParm);
		if (result1.getErrCode() < 0) {
			this.messageBox(result1.getErrText());
			return;
		}
		this.closeWindow();
	}
	/**
     * 调用密码验证
     * 
     * @return boolean
     */
    public boolean checkPW() {
        String inwExe = "inwExe";
        String value = (String) this.openDialog(
                "%ROOT%\\config\\inw\\passWordCheck.x", inwExe);
        if (value == null) {
            return false;
        }
        return value.equals("OK");
    }
}
