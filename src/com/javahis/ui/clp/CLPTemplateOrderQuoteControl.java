package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

import jdo.clp.BscInfoTool;
import jdo.odi.OdiMainTool;
import jdo.sys.Operator;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TCheckBox;
import com.javahis.system.textFormat.TextFormatCLPDuration;
import com.javahis.system.textFormat.TextFormatSYSCtz;
import com.javahis.util.OrderUtil;

/**
 * <p>Title: 临床路径模板</p>
 *
 * <p>Description: 临床路径模板 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author luhai
 * @version 1.0
 */
public class CLPTemplateOrderQuoteControl extends TControl {
    //临床路径项
    private String clncPathCode;
    //医嘱选择chk框的列数
    private int selectedColumnIndex=0;
    private String caseNo;//就诊号码
    private String ind_flg;//住院计价 过滤医生医嘱
    private boolean ind_clp_flg = false;//住院计价-取消页签对照弹出消息
    private String tag;//页签标记，抗菌药物传回
    private String statCode;
    public CLPTemplateOrderQuoteControl() {

    }

    public void onInit() {
        super.onInit();
        TParm inParm = (TParm)this.getParameter();
        this.clncPathCode = inParm.getValue("CLNCPATH_CODE");
        this.setValue("CLNCPATH_CODE",clncPathCode);
        caseNo=inParm.getValue("CASE_NO");//当前就诊号码
        ind_flg=inParm.getValue("IND_FLG");
        ind_clp_flg=inParm.getBoolean("IND_CLP_FLG");
        tag=inParm.getValue("PAGE_FLG");//页签标记  yuml 20141110
        TextFormatCLPDuration combo_schd = (TextFormatCLPDuration) this
		.getComponent("SCHD_CODE");
        statCode=getOdiSysParmData("ODI_STAT_CODE").toString();
        combo_schd.setCaseNo(caseNo);
        combo_schd.setClncpathCode(clncPathCode);
        combo_schd.onQuery();
        this.setValue("SCHD_CODE",inParm.getValue("SCHD_CODE")); //当前时程
        if (null!=ind_flg &&ind_flg.length()>0) {
        	this.setValue("FEE_TYPE", "4");
		}else{
			this.setValue("FEE_TYPE", "3");
		}
        //默认查询
        this.onQuery();
        addLisener();
    }
    /**
     * 初始化监听程序
     */
    private void addLisener(){
        addEventListener("TABLECLPTemplate->" + TTableEvent.CLICKED,
                         "onTableClick");

    }
    /**
     * 表格点击时间
     * @param row int
     */
    public void onTableClick(int row){
        TTable tmpTable=(TTable)this.getComponent("TABLECLPTemplate");
        TParm tableParm = tmpTable.getParmValue();
        int selectedColumn=tmpTable.getSelectedColumn();
        //点击选择框时执行check动作
        if(selectedColumn==selectedColumnIndex){
            TParm rowParm = tableParm.getRow(row);
            String status = rowParm.getValue("ISCHECK");
            if("Y".equals(status)){
                status="N";
            }else{
                status="Y";
            }
        }
        tmpTable.setParmValue(tableParm);
    }

    public void onWindowClose() {
        this.closeWindow();
    }
    /**
     * combo 的action直接设置方法onQuery冲突，故使用onSchdChange替换
     */
    public void onSchdChange(){
        onQuery();
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
    /**
     * 数据查询
     */
    public void onQuery() {
        String regionCode=Operator.getRegion();
        StringBuffer sqlbf = new StringBuffer();
        sqlbf.append(" SELECT '' AS ISCHECK,A.CLNCPATH_CODE,A.SCHD_CODE,A.ORDER_TYPE,A.ORDER_CODE, A.NOTE, ");
        sqlbf.append(" A.CHKTYPE_CODE,A.ORDER_SEQ_NO, ");
        sqlbf.append(" B.ORDER_DESC || CASE WHEN B.SPECIFICATION IS NOT NULL THEN '(' || B.SPECIFICATION || ')' END AS ORDER_DESC, ");
        sqlbf.append(" A.START_DAY,A.DOSE AS MEDI_QTY, ");
        sqlbf.append(" A.DOSE_UNIT AS MEDI_UNIT,A.FREQ_CODE,A.RBORDER_DEPT_CODE AS DEPTORDR_CODE ");
        sqlbf.append(" FROM  CLP_PACK A ,SYS_FEE B,CLP_CHKUSER C ");
        sqlbf.append(" WHERE A.ORDER_CODE=B.ORDER_CODE AND A.CHKUSER_CODE=C.CHKUSER_CODE AND A.ORDER_FLG='Y' ");
        //===============pangben 2012-5-18
        sqlbf.append(" AND (A.REGION_CODE IS NULL OR A.REGION_CODE='"+regionCode+"' OR A.REGION_CODE='' ) ");
        //===============pangben 2012-5-18 stop
        sqlbf.append(" AND (B.REGION_CODE IS NULL OR B.REGION_CODE='"+regionCode+"') ");
        if (null!=ind_flg&&ind_flg.equals("Y")) {
        	sqlbf.append(" AND A.CLNCPATH_CODE='"+this.clncPathCode+"' AND C.CHKUSER_FLG='N' ");
        //===============yuml s   2014-10-30
		}else if(null!=ind_flg&&ind_flg.equals("N")){
			sqlbf.append(" AND A.CLNCPATH_CODE='"+this.clncPathCode+"' AND C.CHKUSER_FLG='Y' ");
		}else{
			sqlbf.append(" AND A.CLNCPATH_CODE='"+this.clncPathCode+"' ");
		}
      //===============yuml   e    2014-10-30  
       
        String schdCode = this.getValueString("SCHD_CODE"); 
        int startDay=this.getValueInt("START_DAY");//第几天
        if(checkNullAndEmpty(schdCode)){
            sqlbf.append(" AND A.SCHD_CODE='"+schdCode+"' ");
        }
        //==========pangben 2012-6-19 start
        if (startDay!=0) {
        	  sqlbf.append(" AND A.START_DAY='"+startDay+"' ");
		}
      //==========pangben 2012-6-19 stop
        String orderType=getOrderType();
        if(checkNullAndEmpty(orderType)){
            sqlbf.append(" AND A.ORDER_TYPE='"+orderType+"' ");
        }
        if(this.getValueString("FEE_TYPE").equals("1")){
        	 sqlbf.append(" AND B.ORDER_CAT1_CODE = 'PHA_W' ");
    	}else if(this.getValueString("FEE_TYPE").equals("2")){
    		sqlbf.append(" AND B.ORDER_CAT1_CODE != 'PHA_W' ");
    	}
//        System.out.println("sqlbf::::"+sqlbf);
        TParm result=new TParm(TJDODBTool.getInstance().select(sqlbf.append(" ORDER BY A.SEQ ").toString()));
        this.callFunction("UI|TABLECLPTemplate|setParmValue",result);
    }
    /**
     * 传回
     */
    public void onOk(){
            TParm returnParm = new TParm();
            TTable table = (TTable)this.getComponent("TABLECLPTemplate");
            table.acceptText();
            TParm tableParm=table.getParmValue();
            for(int i=0;i<tableParm.getCount();i++){
                TParm tableRow=tableParm.getRow(i);
                if(tableRow.getValue("ISCHECK").equals("Y")){
                	//要回传的医嘱是否停用
            		String sql = "SELECT ACTIVE_FLG FROM SYS_FEE WHERE ORDER_CODE = '"+tableRow.getValue("ORDER_CODE")+"'";
            		TParm activeParm = new TParm(TJDODBTool.getInstance().select(sql));
            		if(activeParm.getValue("ACTIVE_FLG",0).equals("N")){
            			this.messageBox(tableRow.getValue("ORDER_DESC")+",已停用。");
            			return ;
            		}
            		
            		if(!ind_clp_flg){//住院计价取消弹出
            			TParm parm = new TParm();
        				parm.setData("FREQ_CODE",tableRow.getValue("FREQ_CODE"));
        				parm.setData("STAT_FLG", tag.equalsIgnoreCase("ST") ? "Y"
        						: "N");
        				TParm result = BscInfoTool.getInstance().checkFreq(parm);
        				if (result.getCount() <= 0) {
        					this.messageBox(tableRow.getValue("ORDER_DESC")+",频次错误不可以传回");
                			return ;// 回滚 不操作
        				}
                		if(!tag.equals(tableRow.getValue("ORDER_TYPE"))){
                			this.messageBox(tableRow.getValue("ORDER_DESC")+",非"+this.getTagName(tag)+"。");
                			return ;
                		}
            		}           		
            		//yuml   e   20141110   判断引入的页面是否正确
//                	System.out.println("123455========"+tableRow);
//                 tableRow.setData("PAGE_FLG",tag);
//                 TParm result=OrderUtil.getInstance().checkPhaAntiOnFech(tableRow);
//                 if (result.getErrCode()<0) {
//						this.messageBox(result.getErrText());
//						return;
//                 }
	                 returnParm.addData("ORDER_CODE",tableRow.getValue("ORDER_CODE"));
	                 returnParm.addData("CLNCPATH_CODE",tableRow.getValue("CLNCPATH_CODE"));
	                 returnParm.addData("SCHD_CODE",tableRow.getValue("SCHD_CODE"));
	                 returnParm.addData("CHKTYPE_CODE",tableRow.getValue("CHKTYPE_CODE"));
	                 returnParm.addData("ORDER_SEQ_NO",tableRow.getValue("ORDER_SEQ_NO"));
	                 returnParm.addData("ORDER_TYPE",tableRow.getValue("ORDER_TYPE"));
                }
            }
            this.setReturnValue(returnParm);
            this.closeWindow();
    }
    /**
     * 全选按钮
     */
    public void chkAll(){
        TTable table = (TTable)this.getComponent("TABLECLPTemplate");
        TCheckBox tbtn = (TCheckBox)this.getComponent("chkAll");
        String isCheck=tbtn.isSelected()?"Y":"";
        TParm tableParm = table.getParmValue();
        for(int i=0;i<tableParm.getCount();i++){
            tableParm.setData("ISCHECK",i,isCheck);
        }
        table.setParmValue(tableParm);
    }
    /**
     * 得到医嘱类型
     * @return String
     */
    private String getOrderType(){
        String orderType="";
        TRadioButton tbAll=(TRadioButton)this.getComponent("rbAll");
        if(tbAll.isSelected()){
            orderType="";
        }
        TRadioButton rbUD=(TRadioButton)this.getComponent("rbUD");//长期
        if (rbUD.isSelected()) {
            orderType = "UD";
        }
        TRadioButton rbST=(TRadioButton)this.getComponent("rbST");//临时
        if (rbST.isSelected()) {
            orderType = "ST";
        }
        TRadioButton rbDS=(TRadioButton)this.getComponent("rbDS");//临时
        if (rbDS.isSelected()) {
            orderType = "DS";
        }
        return orderType;
    }
    /**
     * 检查控件是否为空
     * @param componentName String
     * @return boolean
     */
    private boolean checkComponentNullOrEmpty(String componentName) {
        if (componentName == null || "".equals(componentName)) {
            return false;
        }
        String valueStr = this.getValueString(componentName);
        if (valueStr == null || "".equals(valueStr)) {
            return false;
        }
        return true;
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
     * 查询天数操作
     */
    public void onStartDay(){
    	if (this.getValue("FEE_TYPE").equals("3")) {
    		ind_flg="N";
		}else if (this.getValue("FEE_TYPE").equals("4")) {
			ind_flg="Y";
		}else{
			ind_flg=null;
		}
    	
    	 onQuery();
    }
    /**
     * 获取页面标签名字
       yuml   20141110
     */
    public String getTagName(String tag){
    	String tagName = "";
    	if("ST".equals(tag)){
    		tagName="临时医嘱";
    	}else if("UD".equals(tag)){
    		tagName="长期医嘱";
    	}else if("DS".equals(tag)){
    		tagName="出院带药医嘱";
    	}else if("IG".equals(tag)){
    		tagName="中药饮片医嘱";
    	}
    	return tagName;
    }
}
