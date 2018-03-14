package com.javahis.ui.hrm;

import java.sql.Timestamp;

import jdo.hrm.HRMOrder;
import jdo.hrm.HRMPatAdm;
import jdo.hrm.HRMSchdayDr;
import jdo.reg.SessionTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.EmrUtil;
import com.javahis.util.StringUtil;
/**
 * <p>Title: 健康检查问诊控制类</p>
 *
 * <p>Description: 健康检查问诊控制类</p>
 *
 * <p>Copyright: javahis 20090922</p>
 *
 * <p>Company:JavaHis</p>
 *
 * @author ehui
 * @version 1.0
 */
public class HRMCheckControl extends TControl {
	//挂号对象
	private HRMPatAdm patAdm;
	//医嘱对象
	private HRMOrder order;
	//TABLE
	private TTable table,orderTable;
	//结构化病历模板名称
	private String tempName;
	//班表对象
	private HRMSchdayDr sch;
	//CASE_NO
	private String caseNo;
	//deptAttribute
	private String deptAttribute;
	/**
	 * 初始化事件
	 */
	public void onInit() {
		super.onInit();
		//初始化控件
		initComponent();
		//初始化数据
		initData();
	}
	/**
	 * 初始化数据
	 */
	private void initData() {
		//初始化数据对象
		patAdm=new HRMPatAdm();
		order=new HRMOrder();
		//取得当前SESSION_CODE
		String sessionCode = SessionTool.getInstance().getDrSessionNow("O",Operator.getRegion());
		if(StringUtil.isNullString(sessionCode)){
			this.messageBox_("取得时段错误");
			return;
		}
		//取得当前诊室、医师的科别属性
		deptAttribute=HRMSchdayDr.getDeptAttribute();
		if(StringUtil.isNullString(deptAttribute)){
			this.messageBox_("取得科别属性错误");
			return;
		}
		// System.out.println("Check.attribute="+deptAttribute);
		// System.out.println("Check.session="+sessionCode);
		if(deptAttribute==null){
			this.messageBox_("取得科别属性失败");
			return;
		}
		TJDODBTool g;
		Timestamp now=TJDODBTool.getInstance().getDBTime();
		Timestamp tomorrow=StringTool.rollDate(now, 1L);
		this.setValue("START_DATE", now);
		this.setValue("END_DATE", tomorrow);
		String startDate=StringTool.getString(now,"yyyyMMdd");
		String endDate=StringTool.getString(tomorrow,"yyyyMMdd");
		//取得当前医师应该问诊的病患列表
		TParm patParm=order.onQueryByDeptAttribute( deptAttribute,false,startDate,endDate);
		table.setParmValue(patParm);
	}
	/**
	 * 初始化控件
	 */
	private void initComponent() {
		table=(TTable)this.getComponent("TABLE");
		orderTable=(TTable)this.getComponent("ORDER_TABLE");
	}
	/**
	 * 病患TABLE单击事件，根据选中行的CASE_NO初始化医嘱对象
	 */
	public void onTableClick(){
		int row=table.getSelectedRow();
		if(row<0){
			return;
		}
		if(order.onQueryByCaseNo(patAdm.getItemString(row, "CASE_NO"))==-1){
			this.messageBox_("取得数据失败");
			return;
		}
		orderTable.setDataStore(order);
		orderTable.setDSValue();

	}
	/**
	 * TABLE双击事件
	 */
	public void onPatChoose(){
		TParm parm = new TParm();
		//数据检核
		int row=table.getSelectedRow();
		if(row<0){
			return;
		}
		TParm emrParm=new TParm();
		TParm patParm=table.getParmValue();
		if(patParm==null){
			return;
		}
		int count=patParm.getCount();
		if(count<1){
			return;
		}
		//配置打开结构化病历的参数，打开结构化病历的数据
		caseNo=patParm.getValue("CASE_NO",row);
		order.filt(caseNo);
		patAdm.onQueryByCaseNo(caseNo);
		tempName=patParm.getValue("MR_CODE",row);
		emrParm.setData("MR_CODE",tempName);
		emrParm.setData("CASE_NO",caseNo);
		// System.out.println("emrParm="+emrParm);
		emrParm=EmrUtil.getInstance().getEmrFilePath(emrParm);
		// System.out.println("emrParm========="+emrParm);

        parm.setData("SYSTEM_TYPE", "HRM");
        parm.setData("ADM_TYPE","H");
        parm.setData("CASE_NO",caseNo);
        parm.setData("PAT_NAME",patParm.getValue("PAT_NAME",row));
        parm.setData("MR_NO",patParm.getValue("MR_NO",row));
        parm.setData("IPD_NO","");
        parm.setData("ADM_DATE",patParm.getData("REPORT_DATE",row));
        parm.setData("DEPT_CODE",patParm.getData("DEPT_CODE",row));
        parm.setData("EMR_FILE_DATA",emrParm);
        parm.setData("STYLETYPE","1");
        parm.setData("RULETYPE","2");
        parm.addListener("EMR_LISTENER",this,"emrListener");
        parm.addListener("EMR_SAVE_LISTENER", this, "emrSaveListener");
        this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x",parm);
	}
	/**
	 * 调用结构化病例的接口需要的方法(扩充用)
	 * @param parm
	 */
	public void emrListener(TParm parm){
	}
	/**
	 * 调用结构化病历的保存接口需要的方法
	 * @param parm
	 */
	public void emrSaveListener(TParm parm){
                //TEMPWANGM20100609
		TParm result=order.saveByCheck(Operator.getRegion(), Operator.getDept(), caseNo,deptAttribute,"",false);
		if(result.getErrCode()!=0){
//			this.messageBox_(result.getErrText());
			return;
		}
		this.messageBox("P0001");
		order.resetModify();
		initData();
	}
	/**
	 * 根据MR_NO查询
	 */
	public void onMrNo(){
		String mrNo=this.getValueString("MR_NO");
		if(StringUtil.isNullString(mrNo)){
			return;
		}
		mrNo=StringTool.fill0(mrNo, PatTool.getInstance().getMrNoLength()); //===========  chenxi 
		this.setValue("MR_NO", mrNo);

	}
}
