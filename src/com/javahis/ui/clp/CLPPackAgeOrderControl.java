package com.javahis.ui.clp;

import jdo.clp.BscInfoTool;
import jdo.clp.CLPManagemTool;
import jdo.clp.CLPTool;
import jdo.odi.OdiMainTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TTable;
import com.javahis.system.textFormat.TextFormatCLPDuration;
import com.javahis.system.textFormat.TextFormatClpPackAge;
/**
 * <p>Title: 临床路径套餐传回</p>
 *
 * <p>Description: 医生站使用 </p>
 *
 * <p>Copyright: Copyright (c) 2015</p>
 *
 * <p>Company: ProperSoft </p>
 *
 * @author pangben 2015-8-12
 * @version 1.0
 */
public class CLPPackAgeOrderControl  extends TControl {
	 //临床路径项
    private String clncPathCode;
    //医嘱选择chk框的列数
    private int selectedColumnIndex=0;
    private TTable table;
    private String caseNo;
    private String mrNo;
    //private String statCode;
	public void onInit() {
        super.onInit();
        TParm inParm = (TParm)this.getParameter();
        this.clncPathCode = inParm.getValue("CLNCPATH_CODE");
        this.setValue("CLNCPATH_CODE",clncPathCode);
        caseNo= inParm.getValue("CASE_NO");
        mrNo= inParm.getValue("MR_NO");
        selectedColumnIndex=inParm.getInt("SELECT_INDEX");//页签标记 
        TextFormatCLPDuration combo_schd = (TextFormatCLPDuration) this
		.getComponent("SCHD_CODE");
        combo_schd.setClncpathCode(clncPathCode);
        combo_schd.onQuery();
        TextFormatClpPackAge combo_packAge=(TextFormatClpPackAge) this
		.getComponent("PACKAGE_CODE");
        combo_packAge.setClncpathCode(clncPathCode);
        //combo_packAge.setSchdCode(inParm.getValue("SCHD_CODE"));
        combo_packAge.onQuery();
        this.setValue("SCHD_CODE",inParm.getValue("SCHD_CODE")); //当前时程
        this.setValue("FEE_TYPE", "3");
        table=(TTable)this.getComponent("TABLE");
      //住院医生站校验第一次进入路径操作
		TParm result = CLPTool.getInstance().checkOdiClpExe(getClpOdiStationParm(), this);
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			this.closeWindow();
			return;
		}else{
			String sql="SELECT CLNCPATH_CODE,SCHD_CODE FROM ADM_INP WHERE CASE_NO='"+caseNo+"'";
		    result=new TParm(TJDODBTool.getInstance().select(sql));
	    	if (result.getErrCode()<0) {
	    		messageBox(result.getErrText());
	    		this.setReturnValue(new TParm());
	    		this.closeWindow();
				return;
			}
	    	if (null==result.getValue("CLNCPATH_CODE",0) ||
	    			result.getValue("CLNCPATH_CODE",0).length()<=0) {
				this.messageBox("没有进入临床路径不可以操作");
				this.setReturnValue(new TParm());
				this.closeWindow();
				return;
			}
			this.clncPathCode = result.getValue("CLNCPATH_CODE",0);
	        this.setValue("CLNCPATH_CODE",clncPathCode);
	        combo_schd = (TextFormatCLPDuration) this
			.getComponent("SCHD_CODE");
	        combo_schd.setClncpathCode(clncPathCode);
	        combo_schd.onQuery();
	        combo_packAge=(TextFormatClpPackAge) this
			.getComponent("PACKAGE_CODE");
	        combo_packAge.setClncpathCode(clncPathCode);
	        //combo_packAge.setSchdCode(inParm.getValue("SCHD_CODE"));
	        combo_packAge.onQuery();
	        this.setValue("SCHD_CODE",result.getValue("SCHD_CODE",0)); //当前时程
		}
        //statCode=getOdiSysParmData("ODI_STAT_CODE").toString();
        //默认查询
        this.onQuery();
    }
	private TParm getClpOdiStationParm(){
		TParm parm=new TParm();
		parm.setData("CASE_NO",caseNo);
        parm.setData("MR_NO", mrNo);
        String sql="SELECT A.CTZ1_CODE,A.DEPT_CODE,B.PAT_NAME FROM ADM_INP A,SYS_PATINFO B WHERE A.MR_NO=B.MR_NO AND A.CASE_NO='"+caseNo+"'";
        TParm result=new TParm(TJDODBTool.getInstance().select(sql));
        parm.setData("PAT_NAME", result.getValue("PAT_NAME",0));
        parm.setData("CTZ_CODE", result.getValue("CTZ1_CODE",0));
        parm.setData("DEPT_CODE",result.getValue("DEPT_CODE",0));
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        parm.setData("REGION_CODE", Operator.getRegion());
        return parm;
	}
	/**
	 * 
	* @Title: onQuery
	* @Description: TODO(查询套餐数据)
	* @author pangben
	* @throws
	 */
	
	public void onQuery(){
		String where="";
		if (this.getValueString("SCHD_CODE").length()>0) {
			where+=" AND SCHD_CODE='"+this.getValueString("SCHD_CODE")+"'";
		}
		if (this.getValueString("PACKAGE_CODE").length()>0) {
			where+=" AND PACK_CODE='"+this.getValueString("PACKAGE_CODE")+"'";
		}
		String sql="SELECT A.PACKAGE_CODE ,A.CLP_PACK_FLG,A.PACK_NOTE,B.SCHD_CODE,A.PACKAGE_DESC FROM CLP_PACKAGE A,(SELECT PACK_CODE,SCHD_CODE FROM CLP_PACK WHERE CLNCPATH_CODE='"
			+this.getValueString("CLNCPATH_CODE")+"' "+where+
			" GROUP BY PACK_CODE,SCHD_CODE) B WHERE A.PACKAGE_CODE=B.PACK_CODE ORDER BY A.SEQ_NO";
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount()<=0) {
			this.messageBox("没有需要查询的套餐数据");
			table.setParmValue(new TParm());
			this.callFunction("UI|TABLECLPTemplate|setParmValue",new TParm());
			return;
		}
		table.setParmValue(result);
		this.callFunction("UI|TABLECLPTemplate|setParmValue",new TParm());
	}
	
	 /**
     * 单击上面表格事件
     */
    public void onTableClick() {
    	int row=table.getSelectedRow();
    	if (row<0) {
			return;
		}
    	TParm tableParm=table.getParmValue();
    	String packCode=tableParm.getValue("PACKAGE_CODE",row);
        String regionCode=Operator.getRegion();
        StringBuffer sqlbf = new StringBuffer();
        sqlbf.append(" SELECT 'Y' AS ISCHECK,A.CLNCPATH_CODE,A.SCHD_CODE,A.ORDER_TYPE,A.ORDER_CODE, A.NOTE, ");
        sqlbf.append(" A.CHKTYPE_CODE,A.ORDER_SEQ_NO,B.ORDER_DESC,A.START_DAY,A.DOSE AS MEDI_QTY, ");
        sqlbf.append(" A.DOSE_UNIT AS MEDI_UNIT,A.FREQ_CODE,A.RBORDER_DEPT_CODE AS DEPTORDR_CODE ");
        sqlbf.append(" FROM  CLP_PACK A ,SYS_FEE B,CLP_CHKUSER C ");
        sqlbf.append(" WHERE A.ORDER_CODE=B.ORDER_CODE AND A.CHKUSER_CODE=C.CHKUSER_CODE AND A.ORDER_FLG='Y' ");
        //===============pangben 2012-5-18
        sqlbf.append(" AND (A.REGION_CODE IS NULL OR A.REGION_CODE='"+regionCode+"' OR A.REGION_CODE='' ) ");
        //===============pangben 2012-5-18 stop
        sqlbf.append(" AND (B.REGION_CODE IS NULL OR B.REGION_CODE='"+regionCode+"') ");
        if(this.getValueString("FEE_TYPE").equals("3")){
        	 sqlbf.append(" AND A.CLNCPATH_CODE='"+this.clncPathCode+"' AND C.CHKUSER_FLG='Y' ");
        }else if(this.getValueString("FEE_TYPE").equals("4")){
        	sqlbf.append(" AND A.CLNCPATH_CODE='"+this.clncPathCode+"' AND C.CHKUSER_FLG='N' ");
		}else{
			sqlbf.append(" AND A.CLNCPATH_CODE='"+this.clncPathCode+"' ");
		}
        String schdCode = tableParm.getValue("SCHD_CODE",row); 
        if(checkNullAndEmpty(schdCode)){//时程代码
            sqlbf.append(" AND A.SCHD_CODE='"+schdCode+"' ");
        }
        sqlbf.append(" AND A.PACK_CODE='"+packCode+"' ");//套餐代码
        String orderType=getOrderType(selectedColumnIndex);
        if(checkNullAndEmpty(orderType)){
            sqlbf.append(" AND A.ORDER_TYPE='"+orderType+"' ");
        }
        if(this.getValueString("FEE_TYPE").equals("1")){
        	 sqlbf.append(" AND B.ORDER_CAT1_CODE = 'PHA_W' ");
    	}else if(this.getValueString("FEE_TYPE").equals("2")){
    		sqlbf.append(" AND B.ORDER_CAT1_CODE != 'PHA_W' ");
    	}
        TParm result=new TParm(TJDODBTool.getInstance().select(sqlbf.append(" ORDER BY A.SEQ ").toString()));
        if (result.getCount()<=0) {
        	this.callFunction("UI|TABLECLPTemplate|setParmValue",new TParm());
		}else{
			this.callFunction("UI|TABLECLPTemplate|setParmValue",result);
		}
    }
    /**
     * 得到医嘱类型
     * @return String
     */
    private String getOrderType(int selectIndex){
        String orderType="";
        switch (selectIndex) {
		case 0:
			orderType = "ST";
			break;
		case 1:
			orderType = "UD";
			break;
		case 2:
			orderType = "DS";
			break;
		default:
			break;
		}
        return orderType;
    }
    /**
     * 检查是否为空或空串
     * @return boolean
     */
    private boolean checkNullAndEmpty(String checkstr) {
        if (checkstr == null) {
            return false;
        }
        if ("".equals(checkstr)) {
            return false;
        }
        return true;
    }
    /**
     * 全选按钮
     */
    public void chkAll(){
        TTable table = (TTable)this.getComponent("TABLECLPTemplate");
        TParm tableParm = table.getParmValue();
        for(int i=0;i<tableParm.getCount();i++){
            if(tableParm.getBoolean("ISCHECK",i)){
            	tableParm.setData("ISCHECK",i,false);
            }else{
            	tableParm.setData("ISCHECK",i,true);
            }
        }
        table.setParmValue(tableParm);
    }
    /**
     * 传回
     */
	public void onOk() {
		int row=table.getSelectedRow();
		TParm tablePackParm=table.getParmValue();
		if (row<0) {
			this.messageBox("请选择套餐");
			return;
		}
		TParm returnParm = new TParm();
		TTable table = (TTable) this.getComponent("TABLECLPTemplate");
		table.acceptText();
		TParm tableParm = table.getParmValue();
		String orderType=getOrderType(selectedColumnIndex);
		for (int i = 0; i < tableParm.getCount(); i++) {
			TParm tableRow = tableParm.getRow(i);
			if (tableRow.getValue("ISCHECK").equals("Y")) {
				// 要回传的医嘱是否停用
				String sql = "SELECT ACTIVE_FLG FROM SYS_FEE WHERE ORDER_CODE = '"
						+ tableRow.getValue("ORDER_CODE") + "'";
				TParm activeParm = new TParm(TJDODBTool.getInstance().select(
						sql));
				if (activeParm.getValue("ACTIVE_FLG", 0).equals("N")) {
					this.messageBox(tableRow.getValue("ORDER_DESC") + ",已停用。");
					return;
				}
				//String orderType = detail.getValue("ORDER_TYPE");
				TParm parm = new TParm();
				parm.setData("FREQ_CODE",tableRow.getValue("FREQ_CODE"));
				parm.setData("STAT_FLG", orderType.equalsIgnoreCase("ST") ? "Y"
						: "N");
				TParm result = BscInfoTool.getInstance().checkFreq(parm);
				if (result.getCount() <= 0) {
					this.messageBox(tableRow.getValue("ORDER_DESC")+",频次错误不可以传回");
        			return ;// 回滚 不操作
				}
				returnParm.addData("ORDER_CODE", tableRow
						.getValue("ORDER_CODE"));
				returnParm.addData("CLNCPATH_CODE", tableRow
						.getValue("CLNCPATH_CODE"));
				returnParm.addData("SCHD_CODE", tableRow.getValue("SCHD_CODE"));
				returnParm.addData("CHKTYPE_CODE", tableRow
						.getValue("CHKTYPE_CODE"));
				returnParm.addData("ORDER_SEQ_NO", tableRow
						.getValue("ORDER_SEQ_NO"));
				returnParm.addData("ORDER_TYPE", tableRow
						.getValue("ORDER_TYPE"));
			}
		}
		if (returnParm.getCount("ORDER_CODE")<=0) {
			this.messageBox("请勾选需要传回的医嘱");
			return;
		}
		if (tablePackParm.getValue("CLP_PACK_FLG",row).equals("Y")) {//关键项目注记
			if (this.messageBox("提示","使用关键套餐,是否需要变更路径或时程",2)==0) {
				
				TParm sendParm = new TParm();
				sendParm.setData("OLD_CLNCPATH_CODE", clncPathCode);
				sendParm.setData("MR_NO", mrNo);
				sendParm.setData("EVL_CODE","");
				sendParm.setData("CASE_NO", caseNo);
				String resultstr = (String) this.openDialog(
						"%ROOT%\\config\\clp\\CLPManageChangeExe.x", sendParm);
				if (null!=resultstr && resultstr.toLowerCase().indexOf("success") >= 0) {
					this.setReturnValue(returnParm);
					this.closeWindow();
				}
				return;
			}
		}
		this.setReturnValue(returnParm);
		this.closeWindow();
	}
	 public void onWindowClose() {
	        this.closeWindow();
	 }
    /**
	 * 得到住院参数
	 * 
	 * @param key
	 *            String
	 * @return Object
	 */
	public Object getOdiSysParmData(String key) {
		return OdiMainTool.getInstance().getOdiSysParmData(key);
	}
}
