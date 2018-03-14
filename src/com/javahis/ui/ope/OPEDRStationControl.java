package com.javahis.ui.ope;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.StringUtils;

import jdo.bil.BILComparator;
import jdo.ope.OPETool;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSBedTool;
import jdo.sys.SYSHzpyTool;
import jdo.sys.SYSNewRegionTool;
import jdo.sys.SystemTool;
import jdo.sys.Operator;
import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TComponent;
import com.dongyang.ui.TDialog;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.system.textFormat.TextFormatCLPDuration;
import com.javahis.util.AMIUtil;
import com.javahis.util.SelectResult;
import com.javahis.util.StringUtil;

import device.PassDriver;


/**
 * <p>Title: 手术工作站 </p>
 *
 * <p>Description: 手术工作站 </p>
 *
 * <p>Copyright: Copyright (c) 2014 </p>
 *
 * <p>Company: Bluecore </p>
 *
 * @author wanglong 2014-7-1
 * @version 1.0
 */
public class OPEDRStationControl extends TControl {
    /**
     * 当前子页面TAG
     */
    public String workPanelTag = "";
    /**
     * 合理用药
     */
    private boolean passIsReady = false;

    private boolean enforcementFlg = false;

    private int warnFlg;
    
    private TTable table ;
    private BILComparator compare = new BILComparator();//比较器（排序功能）
	private boolean ascending = false; //正序，反序（排序功能）
	private int sortColumn = -1;//操作列（排序功能）
	String interVen = "";
	TParm param = new TParm();
	 /**
     * 初始化
     */
    public void onInit() {
    	
    	
    	
        super.onInit();
        if (this.getParameter() != null) {
        	if(this.getParameter() instanceof String){//介入护理平台或者手术医生站界面 直接 进入
	            String[] paramStr = this.getParameter().toString().split(";");
	            if ("OEI".indexOf(paramStr[0]) == -1) {
	            	paramStr[0] = "I";
	            }
	            if(paramStr.length == 2){
	            	interVen = paramStr[1];
	            }
	            this.setValue("ADM_TYPE", paramStr[0]);
	            initUI();
	            initData();
	            SwingUtilities.invokeLater(new Runnable() {

	                public void run() {
	                    try {
	                        onQuery();
	                    }
	                    catch (Exception e) {}
	                }
	            });
        	}
        	if(this.getParameter() instanceof TParm){//介入护理平台界面调用手术医生站
//        		TDialog dialog = (TDialog) getComponent("UI");
//            	//dialog.setHeight(480);
//            	dialog.setWidth(Toolkit.getDefaultToolkit().getScreenSize().width);
        		TParm parm = (TParm) this.getParameter();
        		TParm result = parm.getParm("INITUI");
        		this.setValueForParm("ADM_TYPE;ROOM_NO;MAIN_SURGEON;OP_START_DATE;OP_END_DATE;MR_NO;ANA_USER;PAT_NAME;OP_DEPT_CODE;SCHD_CODE;tCheckBox_0;OPE_STA_N;OPE_STA_Y;OPE_STA_ALL",
        				result);
        		try{
        			this.setValue("OP_START_DATE", result.getValue("OP_START_DATE").toString().substring(0, 19).replaceAll("-", "/"));
        			this.setValue("OP_END_DATE", result.getValue("OP_END_DATE").toString().substring(0, 19).replaceAll("-", "/"));
        		}catch(Exception e){
        			System.out.println(e);
        		}
        		initUI();
        		this.callFunction("UI|SCHD_CODE|setEnabled", false);
	            onQuery();
	            int count = 0;
	            TParm tableParm = table.getParmValue();
	            for(int i = 0; i < tableParm.getCount("OPBOOK_SEQ"); i++){
	            	if(tableParm.getValue("OPBOOK_SEQ",i).equals(result.getValue("OPBOOK_SEQ"))){
	            		count = i;
	            		break;
	            	}
	            }
        		table.setSelectedRow(count);
        		this.onTableDoubleClicked(count);
        		
        		
        	}
        }
        if(!"".equals(interVen)){
        	this.setTitle("介入护理平台");
        }
        
    }

    /**
     * 界面初始化
     */
    private void initUI() {
        table = (TTable) this.getComponent("Table");
        addSortListener(table);// table加排序监听
        OpList opList = new OpList();
        DiagList diagList = new DiagList();
        table.addItem("OpList", opList);
        table.addItem("DiagList", diagList);
        callFunction("UI|Table|addEventListener", "Table->" + TTableEvent.DOUBLE_CLICKED, this,
                     "onTableDoubleClicked");
    }

    /**
     * 数据初始化
     */
    private void initData() {
    	this.setValue("tCheckBox_0", "Y");
        // 获取当前时间
        Timestamp now = SystemTool.getInstance().getDate();
        String date = StringTool.getString(now, "yyyyMMdd");
        this.setValue("OP_START_DATE", StringTool.getTimestamp(date + "000000", "yyyyMMddhhmmss"));
        this.setValue("OP_END_DATE", StringTool.getTimestamp(date + "235959", "yyyyMMddhhmmss"));
        String roomSql = "SELECT * FROM OPE_IPROOM WHERE IP = '#'".replaceFirst("#", Operator.getIP());
        TParm roomParm = new TParm(TJDODBTool.getInstance().select(roomSql));
        if (roomParm.getCount() > 0) {
            this.setValue("ROOM_NO", roomParm.getValue("ROOM_NO", 0));
        }
        if(interVen.equals("")){
        	this.setValue("MAIN_SURGEON", Operator.getID());
        }
        this.setValue("SCHD_CODE", "");
        this.callFunction("UI|SCHD_CODE|setEnabled", false);
        
    }

    /**
     * 手术间选择查询
     */
    public void onOpRoomQuery() {
        TParm parm = new TParm();
        if (this.getValueString("ADM_TYPE").equals("")) {
            this.messageBox("门急住别不能为空");
            return;
        }
        parm.setData("ADM_TYPE", this.getValueString("ADM_TYPE"));
        if (this.getValue("OP_START_DATE") == null || this.getValue("OP_END_DATE") == null) {
            this.messageBox("时间不能为空");
            return;
        }
        parm.setData("OP_START_DATE", StringTool.getString((Timestamp) this
                .getValue("OP_START_DATE"), "yyyyMMddHHmmss"));
        parm.setData("OP_END_DATE", StringTool.getString((Timestamp) this.getValue("OP_END_DATE"),
                                                         "yyyyMMddHHmmss"));
        if (!this.getValueString("ROOM_NO").equals("")) {
            parm.setData("ROOM_NO", this.getValueString("ROOM_NO"));
        }
        if (!this.getValueString("MR_NO").equals("")) {
            String mrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO"));
            parm.setData("MR_NO", mrNo);
        }
        exeQuery(parm);
    }

    /**
     * 病案号回车查询
     */
    public void onMrNoQuery() {
        String mrNo = this.getValueString("MR_NO").trim();
        if (!mrNo.equals("")) {
            mrNo = PatTool.getInstance().checkMrno(mrNo);
            
         // modify by huangtt 20160928 EMPI患者查重提示 start
            this.setValue("MR_NO", mrNo);
    		Pat pat = Pat.onQueryByMrNo(mrNo);
    		if(pat == null){
    			this.messageBox("无此病案号!");
    			return;
    		}
    		if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
    			messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
    			mrNo = pat.getMrNo();
    			this.setValue("MR_NO", mrNo);
    		}
    		 this.setValue("PAT_NAME",pat.getName());
    		// modify by huangtt 20160928 EMPI患者查重提示 end
            
            
//            TParm sysPatInfo = PatTool.getInstance().getInfoForMrno(mrNo);
//            if (sysPatInfo.getCount() > 0) {
//                this.setValue("MR_NO", mrNo);// 病案号
//                this.setValue("PAT_NAME", sysPatInfo.getValue("PAT_NAME", 0));// 姓名
//            } else {
//                return;
//            }
    		 
        } else {
            return;
        }
        TParm parm = new TParm();
        if (this.getValueString("ADM_TYPE").equals("")) {
            this.messageBox("门急住别不能为空");
            return;
        }
        parm.setData("ADM_TYPE", this.getValueString("ADM_TYPE"));
        if (this.getValue("OP_START_DATE") == null || this.getValue("OP_END_DATE") == null) {
            this.messageBox("时间不能为空");
            return;
        }
        parm.setData("OP_START_DATE", StringTool.getString((Timestamp) this
                .getValue("OP_START_DATE"), "yyyyMMddHHmmss"));
        parm.setData("OP_END_DATE", StringTool.getString((Timestamp) this.getValue("OP_END_DATE"),
                                                         "yyyyMMddHHmmss"));
        if (!this.getValueString("ROOM_NO").equals("")) {
            parm.setData("ROOM_NO", this.getValueString("ROOM_NO"));
        }
        if (!this.getValueString("MR_NO").equals("")) {
            parm.setData("MR_NO", this.getValueString("MR_NO"));
        }
        exeQuery(parm);
    }
    
    /**
     * 查询
     */
    public void onQuery() {
        TParm parm=new TParm();
        if (this.getValueString("ADM_TYPE").equals("")) {
            this.messageBox("门急住别不能为空");
            return;
        }
        parm.setData("ADM_TYPE", this.getValueString("ADM_TYPE"));
        if (this.getValue("OP_START_DATE") == null || this.getValue("OP_END_DATE") == null) {
            this.messageBox("时间不能为空");
            return;
        }
        parm.setData("OP_START_DATE", StringTool.getString((Timestamp) this
                .getValue("OP_START_DATE"), "yyyyMMddHHmmss"));
        parm.setData("OP_END_DATE", StringTool.getString((Timestamp) this.getValue("OP_END_DATE"),
                                                         "yyyyMMddHHmmss"));
        if (!this.getValueString("ROOM_NO").equals("")) {
            parm.setData("ROOM_NO", this.getValueString("ROOM_NO"));
        }
        if (!this.getValueString("MR_NO").trim().equals("")) {
            String mrNo = this.getValueString("MR_NO").trim();
            mrNo = PatTool.getInstance().checkMrno(mrNo);
            TParm sysPatInfo = PatTool.getInstance().getInfoForMrno(mrNo);
            if (sysPatInfo.getCount() > 0) {
                this.setValue("MR_NO", mrNo);// 病案号
                this.setValue("PAT_NAME", sysPatInfo.getValue("PAT_NAME", 0));// 姓名
            }
            parm.setData("MR_NO", mrNo);
        }
        if (!this.getValueString("MAIN_SURGEON").equals("")) {
            parm.setData("MAIN_SURGEON", this.getValueString("MAIN_SURGEON"));
        }
        if (!this.getValueString("ANA_USER").equals("")) {
            parm.setData("ANA_USER", this.getValueString("ANA_USER"));
        }
        if (!this.getValueString("OP_DEPT_CODE").equals("")) {
            parm.setData("OP_DEPT_CODE", this.getValueString("OP_DEPT_CODE"));
        }
        exeQuery(parm);
    }

    /**
     * 执行查询
     */
    public void exeQuery(TParm parm) {
    	//记录查询时的参数
    	param = this.getParmForTag("ADM_TYPE;ROOM_NO;MAIN_SURGEON;OP_START_DATE;OP_END_DATE;MR_NO;ANA_USER;PAT_NAME;OP_DEPT_CODE;SCHD_CODE;tCheckBox_0;OPE_STA_N;OPE_STA_Y;OPE_STA_ALL");
        String sql =
				// modified by WangQing 20170316
				// add AMI_FLG 胸痛病患标记
//				"SELECT A.OP_DATE, A.ROOM_NO, A.MR_NO, A.CASE_NO, A.IPD_NO, B.PAT_NAME, "
				"SELECT C.AMI_FLG, A.OP_DATE, A.ROOM_NO, A.MR_NO, A.CASE_NO, A.IPD_NO, B.PAT_NAME, "
			
                        + "       A.OP_CODE1 OP_CODE, A.DIAG_CODE1 DIAG_CODE, A.MAIN_SURGEON, A.ANA_USER1 ANA_USER, "
                        + "       A.URGBLADE_FLG, A.TF_FLG, A.TIME_NEED, A.STATE, A.APROVE_DATE, A.OPBOOK_SEQ, A.ADM_TYPE, A.OP_DEPT_CODE,A.TYPE_CODE,REG.ENTER_ROUTE,REG.PATH_KIND "
                        + "  FROM OPE_OPBOOK A, SYS_PATINFO B, ADM_INP C,ADM_RESV AR,REG_PATADM REG  &   "
                        + " WHERE A.MR_NO = B.MR_NO                  "
                        + "   AND A.CANCEL_FLG <> 'Y'      "
                        + "   @ "//0,申请;1,排程完毕;2,接患者;3,手术室交接;4,手术等待;5,手术开始;6,关胸;7,手术结束;8,返回病房
                        + "   AND A.ADM_TYPE = '#'                   "
                        + "   AND A.CASE_NO = C.CASE_NO              "// wanglong add 20150408
                        + "   AND C.CASE_NO=AR.IN_CASE_NO(+) "
                        + "   AND AR.OPD_CASE_NO=REG.CASE_NO(+)"
                        + "   AND C.DS_DATE IS NULL                  "
                        + "   AND (C.CANCEL_FLG <> 'Y' OR C.CANCEL_FLG IS NULL) "
                        + "   AND A.OP_DATE BETWEEN TO_DATE( '#', 'YYYYMMDDHH24MISS') "
                        + "                     AND TO_DATE( '#', 'YYYYMMDDHH24MISS') "
                        + "  #  #  #   #   #  &  ORDER BY A.OP_DATE";
        sql = sql.replaceFirst("#", parm.getValue("ADM_TYPE"));
        sql = sql.replaceFirst("#", parm.getValue("OP_START_DATE"));
        sql = sql.replaceFirst("#", parm.getValue("OP_END_DATE"));//
        
        String opeStatus = "";
        if (getTRadioButton("OPE_STA_N").isSelected()) {
        	opeStatus = "'1', '2', '3', '4', '5', '6'";
        } else if (getTRadioButton("OPE_STA_Y").isSelected()) {
        	opeStatus = "'7'";
        } else if (getTRadioButton("OPE_STA_ALL").isSelected()) {
        	opeStatus = "'1', '2', '3', '4', '5', '6','7'";
        }
        
        if (StringUtils.isNotEmpty(opeStatus)) {
        	// 0,申请;1,排程完毕;2,接患者;3,手术室交接;4,手术等待;5,手术开始;6,关胸;7,手术结束;8,返回病房
        	sql = sql.replaceFirst("@", " AND A.STATE IN (" + opeStatus + ") ");
			
        }
        
        
        if (!parm.getValue("ROOM_NO").equals("")) {
            sql =
                    sql.replaceFirst("#",
                                     " AND A.ROOM_NO='@' ".replaceFirst("@",
                                                                        parm.getValue("ROOM_NO")));
        } else {
            sql = sql.replaceFirst("#", " ");
        }
        if (!parm.getValue("MR_NO").equals("")) {
            sql =
                    sql.replaceFirst("#",
                                     " AND A.MR_NO='@' ".replaceFirst("@", parm.getValue("MR_NO")));
        } else {
            sql = sql.replaceFirst("#", " ");
        }
        if (!parm.getValue("MAIN_SURGEON").equals("")) {
            sql =
                    sql.replaceFirst("#", " AND A.MAIN_SURGEON='@' ".replaceFirst("@", parm
                            .getValue("MAIN_SURGEON")));
        } else {
            sql = sql.replaceFirst("#", " ");
        }
        if (!parm.getValue("ANA_USER").equals("")) {
            sql =
                    sql.replaceFirst("#", " AND A.ANA_USER1='@' ".replaceFirst("@", parm
                            .getValue("ANA_USER")));
        } else {
            sql = sql.replaceFirst("#", " ");
        }
        if (!parm.getValue("OP_DEPT_CODE").equals("")) {
            sql =
                    sql.replaceFirst("#", " AND A.OP_DEPT_CODE='@' ".replaceFirst("@", parm
                            .getValue("OP_DEPT_CODE")));
        } else {
            sql = sql.replaceFirst("#", " ");
        }
        
        if(this.getValue("tCheckBox_0").equals("Y")){
        	sql = sql.replaceFirst("&", " ,OPE_IPROOM D ");
        	sql = sql.replaceFirst("&", " AND A.OPBOOK_SEQ = D.OPBOOK_SEQ ");
        }else{
        	sql = sql.replaceFirst("&", " ");
        	sql = sql.replaceFirst("&", " ");
        }
        
        //System.out.println("11111::"+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            this.messageBox("E0005");//执行失败
            return;
        }
        if (result.getCount() < 1) {
            this.messageBox("E0008");// 查无资料
        }
        Color red = new Color(255, 0, 0);
		Color pink =new Color(255,170,255);
		HashMap map = new HashMap();
		HashMap wmap = new HashMap();
		//TTable table = this.getTTable("table");
		SelectResult sr = new SelectResult(result);
		int cnt = sr.size();
		for(int i=0;i<cnt;i++){
			Object er = sr.getRowField(i,"ENTER_ROUTE");
			Object pk = sr.getRowField(i,"PATH_KIND");
			if(er !=null && !"E01".equals(er) && !"".equals(er)){
				map.put(i, pink);
				
			}
			if("P01".equals(pk)){
				wmap.put(i, red);
			}
		}
		if (map.size() > 0) {
			table.setRowColorMap(map);
			
		}
		if(wmap.size()>0){
			table.setRowTextColorMap(wmap);
		}
        table.setParmValue(result);
        
    }
    
    /**
     * 手术排程
     */
    public void onAsg() {
        int row = table.getSelectedRow();// 选中行
        if (row < 0) {
            return;
        }
        TParm data = table.getParmValue();
        String OPBOOK_SEQ = data.getValue("OPBOOK_SEQ", row);
        this.openDialog("%ROOT%/config/ope/OPEPersonnel.x", OPBOOK_SEQ);
    }

    /**
     * 手术信息
     */
    public void onOpInfo() {
        int row = table.getSelectedRow();// 选中行
        TParm parmValue = table.getParmValue();
        String OPBOOK_SEQ = parmValue.getValue("OPBOOK_SEQ", row);
        TParm parm = new TParm();
        parm.setData("FLG", "update");
        parm.setData("OPBOOK_SEQ", OPBOOK_SEQ);
        parm.setData("ADM_TYPE", parmValue.getValue("ADM_TYPE", row));
        this.openDialog("%ROOT%/config/ope/OPEOpBook.x", parm);
    }

    /**
     * 手术记录
     */
    public void onOpRecord() {
        int row = table.getSelectedRow();// 选中行
        if (row < 0) {
            return;
        }
        TParm parm = new TParm();
        TParm parmValue = table.getParmValue();
        String OPBOOK_SEQ = parmValue.getValue("OPBOOK_SEQ", row);
        parm.setData("OPBOOK_SEQ", OPBOOK_SEQ);
        parm.setData("MR_NO", parmValue.getValue("MR_NO", row));
        parm.setData("ADM_TYPE", parmValue.getValue("ADM_TYPE", row));
        this.openDialog("%ROOT%/config/ope/OPEOpDetail.x", parm);
    }
    
    
    /**
	 * 临时解锁 
	 */
	public void onUnlock(){
		 int row = table.getSelectedRow();// 选中行
	        if (row < 0) {
	        	this.messageBox("请选中一行数据");
				return;
	        }
	      //  TParm parmValue = table.getParmValue();
	        TParm parmRow = table.getParmValue().getRow(row);
	      this.openDialog("%ROOT%\\config\\adm\\AdmGreenChannelUnlock.x", parmRow);
		
	}


    /**
     * 双击事件
     * 
     * @param row
     *            int
     */
    public void onTableDoubleClicked(int row) {
        if (row < 0) {
            return;
        }
        //this.messageBox(table.getParmValue().getValue("OPBOOK_SEQ",row));
        //if("".equals(interVen)){
	        if (!OPETool.getInstance().isOpDept(Operator.getDept())) {
	            this.messageBox("非手术科室登录，不允许进行操作");
	            return;
	        }
        //}
        TParm parmRow = table.getParmValue().getRow(row);
		// modified by WangQing 20170316
		// 胸痛病人提示
//		TLabel amiFlgLable = (TLabel) this.getComponent("AMI_FLG");
//		if(parmRow.getValue("AMI_FLG").equals("Y")){	
//			amiFlgLable.setVisible(true);
//		}else{
//			amiFlgLable.setVisible(false);
//		}
        this.setValue("ROOM_NO", parmRow.getValue("ROOM_NO"));
        this.setValue("MR_NO", parmRow.getValue("MR_NO"));
        this.setValue("PAT_NAME", parmRow.getValue("PAT_NAME"));
        this.setValue("MAIN_SURGEON", parmRow.getValue("MAIN_SURGEON"));
        this.setValue("ANA_USER", parmRow.getValue("ANA_USER"));
        this.setValue("OP_DEPT_CODE", parmRow.getValue("OP_DEPT_CODE"));
        
        //胸痛提示
//        TLabel amiR = (TLabel) this.getComponent("tLabel_66");
//		String erDesc = "";
//		String pkDesc = "";
//		TParm ami = AMIUtil.getE02byInCaseNo(parmRow.getValue("CASE_NO"));
//		if(ami !=null){
//			int count = ami.getData("ACTION","COUNT")==null?0:ami.getInt("ACTION","COUNT");
//			if(count>0){
//				erDesc = ami.getValue("ER_DESC",0);
//				pkDesc = ami.getValue("PK_DESC",0);
//				
//				if(!"E01".equals(ami.getValue("ENTER_ROUTE",0))){
//					amiR.setForeground(Color.RED);
//				}
//			}			
//			amiR.setText(erDesc);
//		}
        
        String sql =
                "SELECT B.CLNCPATH_CODE, A.BED_NO_DESC, C.PAT_NAME, C.SEX_CODE, C.BIRTH_DATE, B.IN_DATE, B.DS_DATE, "
                        + "       D.ICD_CHN_DESC AS MAINDIAG, B.CTZ1_CODE, B.MR_NO, B.IPD_NO, B.TOTAL_AMT, B.TOTAL_BILPAY, "
                        + "       B.GREENPATH_VALUE, B.STATION_CODE, B.RED_SIGN, B.YELLOW_SIGN, B.STOP_BILL_FLG, A.BED_NO, "
                        + "       B.CTZ2_CODE, B.CTZ3_CODE, B.VS_DR_CODE, B.DEPT_CODE, B.HEIGHT, B.WEIGHT, B.CASE_NO, "
                        + "       B.CUR_AMT, C.POST_CODE, C.ADDRESS, C.COMPANY_DESC, C.CELL_PHONE, C.TEL_HOME, C.IDNO, C.PAT_NAME1, "
                        + "       B.NURSING_CLASS, B.PATIENT_STATUS, D.ICD_CODE, E.CHECK_FLG AS MRO_CHAT_FLG, A.ENG_DESC, "
                        + "       B.SERVICE_LEVEL, B.BILL_STATUS, B.DISE_CODE "
                        + "  FROM SYS_BED A, ADM_INP B, SYS_PATINFO C, SYS_DIAGNOSIS D, MRO_MRV_TECH E "
                        + " WHERE A.BED_NO = B.BED_NO(+)   "
                        + "   AND A.CASE_NO = B.CASE_NO(+) "
                        + "   AND A.MR_NO = B.MR_NO(+)     "
                        + "   AND A.MR_NO = C.MR_NO(+)     "
                        + "   AND A.ACTIVE_FLG = 'Y'       "
                        + "   AND A.CASE_NO = E.CASE_NO(+) "
                        + "   AND A.MR_NO = E.MR_NO(+)     "
                        + "   AND B.DS_DATE IS NULL        "// 在院
                        + "   AND A.ALLO_FLG = 'Y'         "
                        + "   AND B.CANCEL_FLG <> 'Y'      "
                        + "   AND A.BED_STATUS = '1'       "
                        + "   AND B.REGION_CODE = 'H01'    "
                        + "   AND B.MAINDIAG = D.ICD_CODE(+) "
                        + "   AND B.CASE_NO = '#'              "
                        + "ORDER BY B.CASE_NO DESC             ";
        sql = sql.replaceFirst("#", parmRow.getValue("CASE_NO"));
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0 || result.getCount() < 1) {
            this.messageBox("查询用户信息失败");
            return;
        }
        // 拿到预交金余额如果不足给提示不强制
        double rPrice = result.getDouble("CUR_AMT");
        // 黄色警戒
        double yellowPrice = result.getDouble("YELLOW_SIGN");
        
        if(!(this.getParameter() instanceof TParm)){
        	if (rPrice <= yellowPrice) {
                if (this.messageBox("提示信息 Tips", "预交金余额不足！\n Paying insufficient balance gold!",
                                    this.YES_NO_OPTION) != 0) return;
            }
	        // 判断是否加锁
	        if (PatTool.getInstance().isLockPat(result.getValue("MR_NO", 0))) {
	            if (this.messageBox("是否解锁 Whether to unlock",
	                                PatTool.getInstance()
	                                        .getLockParmString(result.getValue("MR_NO", 0)), 0) == 0) {
	                PatTool.getInstance().unLockPat(result.getValue("MR_NO", 0));
	                PatTool.getInstance().lockPat(result.getValue("MR_NO", 0), "ODI");
	            } else {
	                return;
	            }
	        } else {
	            // 加锁
	            PatTool.getInstance().lockPat(result.getValue("MR_NO", 0), "ODI");
	        }
	        passcheck();//初始化合理用药组件
        }
        
        table.setVisible(false);
        TParm action = new TParm();
        action.setData("ODI","OPBOOK_SEQ",parmRow.getValue("OPBOOK_SEQ"));
        action.setData("ODI", "CASE_NO", result.getValue("CASE_NO", 0));
        action.setData("ODI", "BED_NO", result.getData("BED_NO", 0));
        action.setData("ODI", "IPD_NO", result.getValue("IPD_NO", 0));
        action.setData("ODI", "MR_NO", result.getValue("MR_NO", 0));
        action.setData("ODI", "PAT_NAME", result.getValue("PAT_NAME", 0));
        action.setData("ODI", "PAT_NAME1",
                       SYSHzpyTool.getInstance().charToAllPy(result.getValue("PAT_NAME", 0)));
        action.setData("ODI", "SEX_CODE", result.getData("SEX_CODE", 0));
        action.setData("ODI", "BIRTH_DATE", result.getData("BIRTH_DATE", 0));
        action.setData("ODI", "ADDRESS", result.getData("ADDRESS", 0));
        action.setData("ODI", "POST_CODE", result.getData("POST_CODE", 0));
        action.setData("ODI", "COMPANY_DESC", result.getData("COMPANY_DESC", 0));
        action.setData("ODI", "TEL", result.getData("CELL_PHONE", 0));
        action.setData("ODI", "TEL1", result.getData("TEL_HOME", 0));
        action.setData("ODI", "IDNO", result.getData("IDNO", 0));
        action.setData("ODI", "CTZ_CODE", result.getData("CTZ1_CODE", 0));
        action.setData("ODI", "ADM_DATE", result.getData("IN_DATE", 0));
        action.setData("ODI", "DEPT_CODE", result.getValue("DEPT_CODE", 0));
        action.setData("ODI", "STATION_CODE", result.getValue("STATION_CODE", 0));
        action.setData("ODI","ORDER_DR_CODE",this.getValue("MAIN_SURGEON"));
        String orgCode =
                this.getOrgCode(result.getValue("STATION_CODE", 0), result.getValue("DEPT_CODE", 0));
        action.setData("ODI", "ORG_CODE", orgCode); // 拿到对应药房
        action.setData("ODI", "VS_DR_CODE", result.getValue("VS_DR_CODE", 0));
        action.setData("ODI", "STOP_BILL_FLG", result.getData("STOP_BILL_FLG", 0)); // 停止划价
        action.setData("ODI", "MAINDIAG", result.getData("MAINDIAG", 0)); // 主诊断
        action.setData("ODI", "ICD_CODE", result.getData("ICD_CODE", 0));
        action.setData("ODI", "ICD_DESC", result.getData("MAINDIAG", 0));
        action.setData("ODI", "SAVE_FLG", true); // 保存权限注记
        // =============合理用药
        boolean passIsReady = SYSNewRegionTool.getInstance().isIREASONABLEMED(Operator.getRegion());
        int warnFlg = Integer.parseInt(TConfig.getSystemValue("WarnFlg")); // 预警等级
        boolean enforcementFlg = "Y".equals(TConfig.getSystemValue("EnforcementFlg")); // 是否强制
        action.setData("ODI", "PASS", passIsReady);
        action.setData("ODI", "FORCE", enforcementFlg);
        action.setData("ODI", "WARN", warnFlg);
        if (passIsReady) {
            action.setData("ODI", "passflg", initReasonbledMed());
        } else {
            action.setData("ODI", "passflg", false);
        }
        action.setData("ODI", "OIDRFLG", false); // 会诊注记
        boolean isICU = SYSBedTool.getInstance().checkIsICU(parmRow.getValue("CASE_NO"));
        action.setData("ODI", "ICU_FLG", isICU); // ICU病患注记
        action.setData("ODI", "OPE_FLG", true); // 手术医生站注记/////////////////
        action.setData("ODI", "OP_DEPT_CODE", parmRow.getValue("OP_DEPT_CODE"));// 手术科室/////////////////
        action.setData("ODI", "OPBOOK_SEQ", parmRow.getValue("OPBOOK_SEQ"));// 手术申请单号/////////////////
        //=====pangben 2015-8-14 临床路径时程查询
        sql="SELECT CLNCPATH_CODE,SCHD_CODE FROM ADM_INP WHERE CASE_NO='"+result.getValue("CASE_NO", 0)+"'";
        TParm parm = new TParm( TJDODBTool.getInstance().select(sql));
        if (null!=parm.getValue("CLNCPATH_CODE",0)&&parm.getValue("CLNCPATH_CODE",0).length()>0) {
        	TextFormatCLPDuration combo_schd = (TextFormatCLPDuration) this.getComponent("SCHD_CODE");
    		combo_schd.setClncpathCode(parm.getValue("CLNCPATH_CODE",0));
    	    combo_schd.onQuery();
        	String schdCode=parm.getValue("SCHD_CODE",0);
        	this.setValue("SCHD_CODE", schdCode);
            this.callFunction("UI|SCHD_CODE|setEnabled", true);
            action.setData("ODI", "OPECLP_FLG", true);//====pangben 2015-8-14 医生站操作路径病患，临时医嘱时程操作
		}else{
			action.setData("ODI", "OPECLP_FLG", false);//====pangben 2015-8-14 医生站操作路径病患，临时医嘱时程操作
			this.setValue("SCHD_CODE", "");
	        this.callFunction("UI|SCHD_CODE|setEnabled", false);
		}
        if("".equals(interVen)){
        	if(this.getParameter() instanceof TParm){
        		action.setData("ODI","OPERATOR","OPE_N");//表示住院护士操作
        	}else{
        		action.setData("ODI","OPERATOR","DR");
        	}
	        ((TPanel) getComponent("PANEL")).addItem("STATIONMAIN",
	                                                 "%ROOT%\\config\\odi\\ODIStationUI.x", action,
	                                                 false);
        }else{
        	//param = this.getParmForTag("ADM_TYPE;ROOM_NO;MAIN_SURGEON;OP_START_DATE;OP_END_DATE;MR_NO;ANA_USER;PAT_NAME;OP_DEPT_CODE;SCHD_CODE;tCheckBox_0");
        	param.setData("OPBOOK_SEQ", table.getParmValue().getValue("OPBOOK_SEQ",row));
        	action.setData("INITUI",param.getData());
        	action.setData("OP_ROOM",getTcomBox("ROOM_NO").getSelectedName());
        	action.setData("OP_DATE",table.getParmValue().getValue("OP_DATE",table.getSelectedRow()).toString().substring(0,19).replaceAll("-", "/"));
        	action.setData("OP_CODE",parmRow.getValue("OP_CODE"));
        	action.setData("TYPE_CODE",parmRow.getValue("TYPE_CODE"));
        	action.setData("OPBOOK_SEQ",parmRow.getValue("OPBOOK_SEQ"));
    		((TPanel) getComponent("PANEL")).addItem("STATIONMAIN",
                    "%ROOT%\\config\\ope\\OPEIntervenNurPlat.x", action,
                    false);
        }
        workPanelTag = "STATIONMAIN";
        this.setUIEnabled(false);
        String incaseno=result.getValue("CASE_NO", 0);
//        setInPateintPath(incaseno);//add for ami
                
    }
    
    public void setInPateintPath(String incaseno){
    	TLabel amiR = (TLabel) this.getComponent("tLabel_11");
		TLabel amiP = (TLabel) this.getComponent("tLabel_12");
		//String incaseno=this.getValueString("CASE_NO");
		TParm ami = AMIUtil.getE02byInCaseNo(incaseno);
		if(ami!=null){
			String enterroute="";
			String erdesc="";
			String pathkind="";
			String pkdesc="";
			if(((Vector)ami.getData("ENTER_ROUTE"))!=null&&((Vector)ami.getData("ENTER_ROUTE")).size()>0){
			     enterroute=String.valueOf(((Vector)ami.getData("ENTER_ROUTE")).get(0));
			}
			if(((Vector)ami.getData("ER_DESC"))!=null&&((Vector)ami.getData("ER_DESC")).size()>0){
			    
			    erdesc=String.valueOf(((Vector)ami.getData("ER_DESC")).get(0));
			}
			if(((Vector)ami.getData("PATH_KIND"))!=null&&((Vector)ami.getData("PATH_KIND")).size()>0){
				 
			    pathkind=String.valueOf(((Vector)ami.getData("PATH_KIND")).get(0));
			}
			if(((Vector)ami.getData("PK_DESC"))!=null&&((Vector)ami.getData("PK_DESC")).size()>0){
				
			    pkdesc=String.valueOf(((Vector)ami.getData("PK_DESC")).get(0));
			}
			if(!enterroute.equals("null")||!enterroute.trim().equals("")){
				if(enterroute.trim().equals("E01")){
					amiR.setForeground(Color.BLACK);
					amiP.setForeground(Color.BLACK);
				}else{
					amiR.setForeground(Color.RED);
					amiP.setForeground(Color.RED);					
				}
				amiR.setText(erdesc);
				amiP.setText(pkdesc);
			}else{
				amiR.setText("");
				amiP.setText("");
			}
			
		}else{
			amiR.setText("");
			amiP.setText("");
		}
    }

    /**
     * 启用/禁用界面组件
     * 
     * @param flag
     */
    public void setUIEnabled(boolean flag) {
        this.callFunction("UI|OP_START_DATE|setEnabled", flag);
        this.callFunction("UI|OP_END_DATE|setEnabled", flag);
        this.callFunction("UI|ROOM_NO|setEnabled", flag);
        this.callFunction("UI|MR_NO|setEnabled", flag);
        this.callFunction("UI|MAIN_SURGEON|setEnabled", flag);
        this.callFunction("UI|ANA_USER|setEnabled", flag);
        this.callFunction("UI|OP_DEPT_CODE|setEnabled", flag);
    }

    /**
     * 显示当前TOOLBAR
     */
    public void onShowWindowsEvent() {
        if (workPanelTag == null || workPanelTag.length() == 0) {
            // 显示UIshowTopMenu
            callFunction("UI|showTopMenu");
            return;
        }
        TPanel p = (TPanel) getComponent(workPanelTag);
        p.getControl().callFunction("onShowWindowsFunction");
    }
    
    /**
     * 关闭工作页面
     * 
     * @return boolean
     */
    public boolean onClosePanel() {
        TPanel p = (TPanel) getComponent("STATIONMAIN");
        if (!p.getControl().onClosing()) return false;
        // 移除当前子UI
        callFunction("UI|PANEL|removeItem", "STATIONMAIN");
        // 移除子UIMenuBar
        callFunction("UI|removeChildMenuBar");
        // 移除子UIToolBar
        callFunction("UI|removeChildToolBar");
        // 显示UIshowTopMenu
        callFunction("UI|showTopMenu");
        // 得到TabbedPane控件
        // TTabbedPane tabPane = (TTabbedPane) this.callFunction("UI|TablePane|getThis");
        // 可以编辑
        // tabPane.setEnabled(true);
        this.setUIEnabled(true);
        // 显示TABLE
        table.setVisible(true);
        return true;
    }

    /**
     * 清空
     */
    public void onClear() {
        this.clearValue("ROOM_NO;MR_NO;PAT_NAME;MAIN_SURGEON;ANA_USER;OP_DEPT_CODE");
        table.setDSValue();
        getTRadioButton("OPE_STA_N").setSelected(true);
        initData();
    }

    /**
     * 拿到对应药房
     * 
     * @param stationCode
     *            String
     * @param deptCode
     *            String
     * @return String
     */
    public String getOrgCode(String stationCode, String deptCode) {
        TParm parm =
                new TParm(TJDODBTool.getInstance()
                        .select("SELECT ORG_CODE FROM SYS_STATION WHERE STATION_CODE='"
                                        + stationCode + "'"));
        if (parm.getCount() == 0) {
            return "";
        }
        return parm.getValue("ORG_CODE", 0);
    }

    /**
     * 初始化合理用药
     * 
     * @return boolean
     */
    public boolean initReasonbledMed() {
        try {
            if (PassDriver.init() != 1) {
                return false;
            }
            // 合理用药初始化
            if (PassDriver.PassInit(Operator.getName(), Operator.getDept(), 10) != 1) {
                return false;
            }
            // 合理用药控制参数
            if (PassDriver.PassSetControlParam(1, 2, 0, 2, 1) != 1) {
                return false;
            }
        }
        catch (UnsatisfiedLinkError e1) {
            e1.printStackTrace();
            return false;
        }
        catch (NoClassDefFoundError e2) {
            e2.printStackTrace();
            return false;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    
    /**
     * 初始化合理用药参数
     */
    public void passcheck() {
        // 合理用药
        passIsReady = SYSNewRegionTool.getInstance().isIREASONABLEMED(Operator.getRegion());
        // 预警等级
        warnFlg = Integer.parseInt(TConfig.getSystemValue("WarnFlg"));
        // 是否强制
        enforcementFlg = "Y".equals(TConfig.getSystemValue("EnforcementFlg"));
        // 合理用药
        if (passIsReady) {
            if (!initReasonbledMed()) {
                this.messageBox("合理用药初始化失败！");
            }
        }
    }
    
    /**
     * 诊断CODE替换中文 模糊查询（内部类）
     */
    public class DiagList extends TLabel {
        TDataStore dataStore = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");
        public String getTableShowValue(String s) {
            if (dataStore == null)
                return s;
            String bufferString = dataStore.isFilter() ? dataStore.FILTER :
                dataStore.PRIMARY;
            TParm parm = dataStore.getBuffer(bufferString);
            Vector v = (Vector) parm.getData("ICD_CODE");
            Vector d = (Vector) parm.getData("ICD_CHN_DESC");
            int count = v.size();
            for (int i = 0; i < count; i++) {
                if (s.equals(v.get(i)))
                    return "" + d.get(i);
            }
            return s;
        }
    }
    
    /**
     * 手术CODE替换中文 模糊查询（内部类）
     */
    public class OpList extends TLabel {
        TDataStore dataStore = new TDataStore();
        public OpList(){
            dataStore.setSQL("SELECT * FROM SYS_OPERATIONICD");
            dataStore.retrieve();
        }
        public String getTableShowValue(String s) {
            if (dataStore == null)
                return s;
            String bufferString = dataStore.isFilter() ? dataStore.FILTER :
                dataStore.PRIMARY;
            TParm parm = dataStore.getBuffer(bufferString);
            Vector v = (Vector) parm.getData("OPERATION_ICD");
            Vector d = (Vector) parm.getData("OPT_CHN_DESC");
            int count = v.size();
            for (int i = 0; i < count; i++) {
                if (s.equals(v.get(i)))
                    return "" + d.get(i);
            }
            return s;
        }
    }
    
	// ====================排序功能begin======================
	/**
	 * 加入表格排序监听方法
	 * @param table
	 */
	public void addSortListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				if (j == sortColumn) {
					ascending = !ascending;// 点击相同列，翻转排序
				} else {
					ascending = true;
					sortColumn = j;
				}
				TParm tableData = table.getParmValue();// 取得表单中的数据
				String columnName[] = tableData.getNames("Data");// 获得列名
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				String tblColumnName = table.getParmMap(sortColumn); // 表格排序的列名;
				int col = tranParmColIndex(columnName, tblColumnName); // 列名转成parm中的列索引
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames, table);
			}
		});
	}

	/**
	 * 根据列名数据，将TParm转为Vector
	 * @param parm
	 * @param group
	 * @param names
	 * @param size
	 * @return
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}

	/**
	 * 返回指定列在列名数组中的index
	 * @param columnName
	 * @param tblColumnName
	 * @return int
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {
			if (tmp.equalsIgnoreCase(tblColumnName)) {
				return index;
			}
			index++;
		}
		return index;
	}

	/**
	 * 根据列名数据，将Vector转成Parm
	 * @param vectorTable
	 * @param parmTable
	 * @param columnNames
	 * @param table
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames, final TTable table) {
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		// add by wangqing 20170629 start
		// 胸痛病患排序后显示错误
		Color red = new Color(255, 0, 0);
		Color pink =new Color(255,170,255);
		HashMap map = new HashMap();
		HashMap wmap = new HashMap();
		SelectResult sr = new SelectResult(parmTable);
		int cnt = sr.size();
		for(int i=0;i<cnt;i++){
			Object er = sr.getRowField(i,"ENTER_ROUTE");
			Object pk = sr.getRowField(i,"PATH_KIND");
			if(er !=null && !"E01".equals(er) && !"".equals(er)){
				map.put(i, pink);

			}
			if("P01".equals(pk)){
				wmap.put(i, red);
			}
		}
		if (map.size() > 0) {
			table.setRowColorMap(map);

		}
		if(wmap.size()>0){
			table.setRowTextColorMap(wmap);
		}
		// add by wangqing 20170629 end
		table.setParmValue(parmTable);
	}
	// ====================排序功能end======================
	/**
     * CDR
     */
    public void onQuerySummaryInfo() {
        TParm parm = new TParm();
        TTable table = (TTable)this.getComponent("Table");
        
        int selRow = table.getSelectedRow();
        if (selRow < 0) {
            this.messageBox("请选中要查看的病人信息");
            return;
        }    
        Container container = (Container) callFunction("UI|getThis");
        while (!(container instanceof TTabbedPane)) {
            container = container.getParent();
        }
        TTabbedPane tabbedPane = (TTabbedPane) container;

        parm.setData("MR_NO", table.getParmValue().getRow(selRow).getValue("MR_NO"));
        // 打开综合查询界面
        tabbedPane.openPanel("CDR_SUMMARY_UI",
                "%ROOT%\\config\\emr\\EMRCdrSummaryInfo.x", parm);
        TComponent component = (TComponent) callFunction(
                "UI|SYSTEM_TAB|findObject", "CDR_SUMMARY_UI");
        if (component != null) {
            tabbedPane.setSelectedComponent((Component) component);
            return;
        }
    }
    /**
     * 病历浏览
     */
    public void onCxShow(){  
    	TTable table =(TTable)this.getComponent("Table");
     	TParm parm = table.getParmValue();
    	String mrNo = parm.getValue("MR_NO", table.getSelectedRow());
    	String caseNo = parm.getValue("CASE_NO", table.getSelectedRow());
        TParm result = queryPassword();
        String user_password = result.getValue("USER_PASSWORD",0);
        String url = "http://"+getWebServicesIp()+"?userId="+Operator.getID()+"&password="+user_password+"&mrNo="+mrNo+"&caseNo="+caseNo;
        try {
            Runtime.getRuntime().exec(String.valueOf(String.valueOf((new
                    StringBuffer("cmd.exe /c start iexplore \"")).append(
                            url).append("\""))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private TParm queryPassword(){
        String sql = "SELECT USER_PASSWORD FROM SYS_OPERATOR WHERE USER_ID = '"+Operator.getID()+"' AND REGION_CODE = '"+Operator.getRegion()+"'";
        return new TParm(TJDODBTool.getInstance().select(sql));
    }
    
    /**
     * 获取配置文件中的电子病历服务器IP
     * @return
     */
    public static String getWebServicesIp() {
        TConfig config = getProp();
        String url = config.getString("", "EMRIP");
        return url;
    }
    
    /**
     * 获取配置文件
     * @author shendr
     */
    public static TConfig getProp() {
        TConfig config=null;
        try{
         config = TConfig
                .getConfig("WEB-INF\\config\\system\\TConfig.x");
        }catch(Exception e){
            e.printStackTrace();
        }
        return config;
    }
	
    
   
    
	/**
	 * 处理当前TOOLBAR
	 */
	public void onShowWindowsFunction() {
		// 显示UIshowTopMenu
		callFunction("UI|showTopMenu");
	}
	
	public TComboBox getTcomBox(String tagName){
		return (TComboBox) this.getComponent(tagName);
	}
	/**
	 * 得到RadioButton
	 * 
	 * @param tag
	 *            String
	 * @return TRadioButton
	 */
	public TRadioButton getTRadioButton(String tag) {    
		return (TRadioButton) this.getComponent(tag);
	}
	/**
	 * 解除绑定
	 */
	public void onRemove(){
		int row = table.getSelectedRow();
		if(row < 0){
			this.messageBox("请选中数据");
			return;
		}
		String opBookSeq = table.getParmValue().getValue("OPBOOK_SEQ",row);
		String sql = "SELECT * FROM OPE_IPROOM WHERE OPBOOK_SEQ = '"+opBookSeq+"'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getCount() < 0){
			this.messageBox("该病患没有绑定术间");
			return;
		}
		sql = "UPDATE OPE_IPROOM SET OPBOOK_SEQ = '' WHERE  OPBOOK_SEQ = '"+opBookSeq+"'";
		result = new TParm(TJDODBTool.getInstance().update(sql));
		String date = TJDODBTool.getInstance().getDBTime().toString().substring(0,19).replaceAll("-", "/");
		sql = "UPDATE OPE_OPBOOK SET REMOVE_DATE = TO_DATE('"+date+"','yyyy/MM/dd HH24:mi:ss') WHERE  OPBOOK_SEQ = '"+opBookSeq+"'";
		result = new TParm(TJDODBTool.getInstance().update(sql));
		this.messageBox("解除成功");
		this.onClear();
	}
}
