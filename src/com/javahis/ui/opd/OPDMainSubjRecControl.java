package com.javahis.ui.opd;

import java.awt.Component;

import jdo.opd.DiagRec;
import jdo.opd.DiagRecList;
import jdo.opd.ODO;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;

/**
 *
 * <p>
 * Title: 主诉Panel
 * </p>
 *
 * <p>
 * Description:主诉Panel
 * </p>
 *
 * <p>
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * </p>
 *
 * <p>
 * Company:Javahis
 * </p>
 *
 * @author ehui 20080924
 * @version 1.0
 */
public class OPDMainSubjRecControl extends TControl {
	ODO odo;
	int selectRow = -1;
	boolean mainflg = false;
	int mainint = 0;
	String nullStr="";
	public void onInit() {
		super.onInit();
		mainflg = false;
		mainint = 0;
		callFunction("UI|TABLEDIAGNOSIS|addEventListener", "TABLEDIAGNOSIS->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		this.addEventListener("TABLEDIAGNOSIS->" + TTableEvent.CHANGE_VALUE,
				"onTableChangeValue");
		TTable table = (TTable) this.callFunction("UI|TABLEDIAGNOSIS|getThis");
		table
				.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
						"onMainFlg");
		// table专用的监听
		callFunction("UI|TABLEDIAGNOSIS|addEventListener",
				TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreateEditComponent");
		initDiagRec();
		initSubjRec();
	}

	/**
	 *
	 * @param com
	 * @param row
	 * @param column
	 */
	public void onCreateEditComponent(Component com, int row, int column) {
		selectRow = row;
		if (column != 1)
			return;
		if (!(com instanceof TTextField))
			return;

		TTextField textfield = (TTextField) com;
		textfield.onInit();
		// 给table上的新text增加sys_fee弹出窗口
		textfield.setPopupMenuParameter("bbb", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSICDPopup.x"));
		// 给新text增加接受sys_fee弹出窗口的回传值
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popReturn");

	}

	/**
	 *
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		this.callFunction("UI|TABLEDIAGNOSIS|acceptText");
		DiagRecList drl=odo.getDiagRecList();
		DiagRec dr;
		if(drl==null||drl.size()<1){
			dr = odo.getDiagRecList().newDiagRec();
		}else{
			dr = odo.getDiagRecList().getDiagRec(selectRow);
		}

		dr.modifyIcdCode(parm.getValue("ICD_CODE"));
		dr.modifyCaseNo(odo.getCaseNo());
//		this.messageBox_(parm.getValue("MAIN_DIAG_FLG"));
//		dr.modifyMainDiagFlg(parm.getValue("MAIN_DIAG_FLG"));
		dr.modifyIcdType(parm.getValue("ICD_TYPE"));
		if (!mainflg) {
			dr.modifyMainDiagFlg("Y");
			mainflg = true;
			mainint = selectRow;
		}
		TParm tableparm = dr.getParm();
//		// System.out.println("diag parm==="+tableparm);
		// tableparm.setData("")
		// dr.initParm(parm);
		this.callFunction("UI|TABLEDIAGNOSIS|setRowParmValue", selectRow,
				tableparm);
		onNew();
	}

	/**
	 * 初始化界面
	 */
	public void initSubjRec() {
		if (odo == null) {
			return;
		}
		this.setValue("SUBJ_TEXT", odo.getSubjText());
		this.setValue("OBJ_TEXT", odo.getObjText());
		this.setValue("PHYSEXAM_REC", odo.getPhysexamRec());

	}

	/**
	 * 表格中的主诊断点击事件
	 *
	 * @param o
	 *            TTable
	 */
	public void onMainFlg(Object o) {
		TTable table = (TTable) o;
		table.acceptText();
		int row = table.getSelectedRow();
		int column = table.getSelectedColumn();
		Object mainobj = table.getValueAt(row, column);
		DiagRec dr = odo.getDiagRecList().getDiagRec(row);
		DiagRec olddr = odo.getDiagRecList().getDiagRec(mainint);
		if (dr == null
				|| (dr.getIcdCode() == null || dr.getIcdCode().length() < 1)) {
			return;
		}

		if ("N".equalsIgnoreCase(mainobj.toString())) {
			this.messageBox_("必须有一项主诊断");
			table.setValueAt(true, row, 0);
			return;
		}
		dr.modifyMainDiagFlg("Y");
		olddr.modifyMainDiagFlg("N");
		table.setValueAt(false, mainint, 0);
		mainint = row;

	}

	/**
	 * 主诉点击事件
	 */
	public void onSubjText() {
		TParm inParm=new TParm();
		inParm.setData("subject",this);
		inParm.setData("TAG","SUBJ_TEXT");
		TParm msg=(TParm)this.openDialog("%ROOT%\\config\\opd\\OPDComPhraseQuote.x",inParm,false);
		this.setValue("SUBJ_TEXT", msg.getValue("CONTENT"));
	}
	/**
	 * 客诉点击事件
	 */
	public void onObjText(){
		TParm inParm=new TParm();
		inParm.setData("subject",this);
		inParm.setData("TAG","OBJ_TEXT");
		TParm msg=(TParm)this.openDialog("%ROOT%\\config\\opd\\OPDComPhraseQuote.x",inParm,false);
		this.setValue("OBJ_TEXT", msg.getValue("CONTENT"));
	}
	/**
	 * 客诉点击事件
	 */
	public void onPhyText(){
		TParm inParm=new TParm();
		inParm.setData("subject",this);
		inParm.setData("TAG","PHYSEXAM_REC");
		TParm msg=(TParm)this.openDialog("%ROOT%\\config\\opd\\OPDComPhraseQuote.x",inParm,false);
		this.setValue("PHYSEXAM_REC", msg.getValue("CONTENT"));
	}
	/**
	 * 初始化界面
	 */
	public void initDiagRec() {
		if (odo == null) {
			return;
		}
		this.setValue("ICD_TYPE", "W");
		callFunction("UI|TABLEDIAGNOSIS|setParmValue",new TParm());
		this.callFunction("UI|TABLEDIAGNOSIS|removeRowAll");
		TParm temp = odo.getDiagRecList().getParm(DiagRecList.PRIMARY);
		if (temp.getCount() > 0) {
			// 主,40;疾病诊断,80;名称,150;备注,150;开立医生,100;开立时间,100;报告卡序号,80
			callFunction("UI|TABLEDIAGNOSIS|setParmValue", temp);
			DiagRecList drl = odo.getDiagRecList();
			for (int i = 0; i < temp.getCount(); i++) {
				if (drl.getDiagRec(i) != null
						&& "Y".equalsIgnoreCase(drl.getDiagRec(i)
								.getMainDiagFlg())) {
					mainint = i;
					mainflg = true;
					break;
				}
			}
			onNew();
			// ,"MAINDIAG_FLG;ICD_CODE;ICD_CODE;DIAG_NOTE;DR_CODE;ORDER_DATE;FILE_NO");
			return;
		}

		onNew();
	}

	/**
	 * TABLE点击事件
	 *
	 * @param row
	 */
	public void onTableClicked(int row) {
		 String message = (String)callFunction("UI|delete|getActionMessage");
		 String s[] = StringTool.getHead(message, "|");
		 if(s!=null&&!callFunction("UI|getTag").equals(s[0])){
			 callFunction("UI|" + s[0] + "|clearTableSelection");
		 }
		 callFunction("UI|delete|setActionMessage", callFunction("UI|getTag")
		 + "|onDelete");
		selectRow = row;
	}

	/**
	 * TABLE值改变事件
	 *
	 * @param tableNode
	 */
	public void onTableChangeValue(Object tableNode) {
		this.callFunction("UI|TABLEDIAGNOSIS|acceptText");
		TTableNode mytableNode = (TTableNode) tableNode;
		// 该CELL的值
		String nodevalue = TCM_Transform.getString(mytableNode.getValue());
		// TABLE上第几行
		int c = mytableNode.getRow();
		// 第几列
		int column = mytableNode.getColumn();
		DiagRec dr;
		if(odo.getDiagRecList()!=null&&odo.getDiagRecList().size()>1){
			dr = odo.getDiagRecList().getDiagRec(c);
		}else{
			return;
		}

		switch (column) {
		// //MAINDIAGFLG
		// case 0:
		// dr.modifyMainDiagFlg(nodevalue);
		// DiagRec oldmain=odo.getDiagRecList().getDiagRec(mainint);
		// oldmain.modifyMainDiagFlg("N");
		//
		// this.callFunction("UI|TABLEDIAGNOSIS|setRowParmValue",mainint,
		// oldmain.getParm());
		// mainint=c;
		// break;
		// ICD_TYPE,ICD_CODE
		// case 1:
		// if(nodevalue==null||nodevalue.length()==0){
		// this.messageBox_("请设定诊断类别");
		// return;
		// }
		// dr.modifyIcdCode(nodevalue);
		// dr.modifyIcdType(TCM_Transform.getString(this.getValue("ICD_TYPE")));
		// DiagRec oldmain=odo.getDiagRecList().getDiagRec(mainint);
		// if(!mainflg){
		// dr.modifyMainDiagFlg("Y");
		// mainflg=true;
		// mainint=c;
		// }
		// this.callFunction("UI|TABLEDIAGNOSIS|setRowParmValue",c,
		// dr.getParm());
		// break;
		// ICD_CODE
//		case 2:
//			break;
		case 3:
			dr.modifyDiagNote(nodevalue);
			break;
		case 4:
			dr.modifyDrCode(nodevalue);
			break;
		case 6:
			dr.modifyDrCode(nodevalue);
			break;
		}
		dr.modifyOptUser(Operator.getID());
		dr.modifyOptTerm(Operator.getIP());
		if (dr.getCaseNo() == null || "".equalsIgnoreCase(dr.getCaseNo())) {
			dr.modifyCaseNo(odo.getCaseNo());
		}

	}

	/**
	 * TABLE新增一行数据
	 */
	public void onNew() {
		if (odo == null)
			return;
		String admDate = StringTool.getString(StringTool
				.getTimestamp(SystemTool.getInstance().getDate()),
				"yyyyMMddHHmmss");
		DiagRec dr = odo.getDiagRecList().newDiagRec();
		TParm parm = dr.getParm();
		dr.modifyDrCode(Operator.getID());
		dr.modifyOptUser(Operator.getID());
		dr.modifyOptTerm(Operator.getIP());
		dr.modifyAdmType(odo.getAdmType());
		dr.modifyIcdType(this.getValueString("ICD_TYPE"));
		dr.modifyOrderDate(StringTool.getTimestamp(SystemTool.getInstance()
				.getDate()));
		dr.modifyCaseNo(odo.getCaseNo());
		parm.setData("ORDER_DATE", admDate.substring(0,8));
		parm.setData("DR_CODE", Operator.getID());
		int rownow = TCM_Transform
				.getInt(callFunction("UI|TABLEDIAGNOSIS|getRowCount"));
		// addRow(int row, TParm parm) 新增一行数据
		this.callFunction("UI|TABLEDIAGNOSIS|addRow", parm);
//		// System.out.println("odo .getdiagreclist is null"
//				+ odo.getDiagRecList().getParm());
	}

	/**
	 * TABLE删除一行数据
	 */
	public void onDelete() {
		if (selectRow == -1 || odo == null)
			return;
		this.callFunction("UI|TABLEDIAGNOSIS|removeRow", selectRow);
		odo.getDiagRecList().removeData(selectRow);
	}

	/**
	 * 设置ODO对象
	 *
	 * @param odo
	 */
	public void setOdo(Object odo) {

		if (odo == null)
			return;
		this.odo = (ODO) odo;
		initSubjRec();
		initDiagRec();
	}
	public void onClear(){
		this.setValue("SUBJ_TEXT", nullStr);
		this.setValue("OBJ_TEXT", nullStr);
		this.setValue("PHYSEXAM_REC", nullStr);
		this.setValue("ICD_TYPE", nullStr);
		this.setValue("CHN_DSNAME", false);
		 selectRow = -1;
		 mainflg = false;
		 mainint = 0;
		 callFunction("UI|TABLEDIAGNOSIS|setParmValue",new TParm());
		callFunction("UI|TABLEDIAGNOSIS|removeRowAll");

	}
	public Object callMessage(String message, Object parm) {
		return super.callMessage(message, parm);
	}
}
