package com.javahis.ui.sys;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

import jdo.sys.Operator;
import jdo.sys.OperatorTool;
import jdo.sys.SYSOperatorTool;
import jdo.sys.SYSSQL;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:用户管理
 * </p>
 *
 * <p>
 * Description:用户管理
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: javahis
 * </p>
 *
 * @author zhangy 2009.6.08
 * @version 1.0
 */
public class SYSOperatorControl extends TControl {
    /** 
     * 初始化方法
     */
   // private boolean pwdMessageShow = false; //密码消息框显示状态===pangben modify 20110526
    public void onInit() {
        //============pangben modify 20110429 start
        //查询此区域密码是否显示校验强度 。如果数据库PWD_STRENGTH字段为'N'将不显示校验，否则显示
        TParm parm = null;
        if (null != Operator.getRegion() && !"".equals(Operator.getRegion())) {

            String pwdSQL =
                    "SELECT PWD_STRENGTH FROM SYS_REGION WHERE REGION_CODE='" +
                    Operator.getRegion() + "'";
            parm = new TParm(TJDODBTool.getInstance().select(pwdSQL));
        } else {
            parm = new TParm();
            parm.setData("PWD_STRENGTH",0, "N");
        }
        if ("N".equals(parm.getValue("PWD_STRENGTH", 0))) {
            callFunction("UI|pwd_LBL|setVisible", false);
            callFunction("UI|PWD_POOFSTH|setVisible", false);
        } else {
            callFunction("UI|pwd_LBL|setVisible", true);
            callFunction("UI|PWD_POOFSTH|setVisible", true);
        }
        this.setValue("pwd_LBL", ""); //出师界面不显示
        //============pangben modify 20110429 stop
        // 初始化权限
        if (!getPopedem("delPopedem")) {
            ((TMenuItem) getComponent("delete")).setVisible(false);
        }
        // 给TABLEDEPT中的CHECKBOX添加侦听事件
        callFunction("UI|TABLEDEPT|addEventListener",
                     TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTableDeptCheckBoxClicked");
        // 给OPT_CLINICAREA_TABLE中的CHECKBOX添加侦听事件
        callFunction("UI|OPT_CLINICAREA_TABLE|addEventListener",
                     TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTableOPT_ClinicareaCheckBoxClicked");
        // 给OPT_STATION_TABLE中的CHECKBOX添加侦听事件
        callFunction("UI|OPT_STATION_TABLE|addEventListener",
                     TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTableOPT_StationCheckBoxClicked");
        callFunction("UI|OPT_STATION_TABLE|addEventListener",
                TTableEvent.CHECK_BOX_CLICKED, this,
                "onTableOPT_StationCheckBoxClicked");
        // 初始化使用者所属科室列表
        showOperatorDept();
        // 初始化使用者所属诊区列表
        showOperatorClinicarea();
        // 初始化使用者所属病区列表
        showOperatorStation();
        // 设置删除按钮状态
        callFunction("UI|delete|setEnabled", false);
        // 设置保存按钮状态
        callFunction("UI|save|setEnabled", false);
        // 设置新增按钮状态
        callFunction("UI|new|setEnabled", true);
        // 初始化当前时间
        Timestamp date = StringTool.getTimestamp(new Date());
        this.setValue("ACTIVE_DATE", date);
        this.setValue("END_DATE", "9999/12/31");
        //角色权限日期设定
        this.setValue("START_DATE", date);
        this.setValue("END_DATE_RULE", "9999/12/31");
        //============pangben modify 20110328 start
        //密码失效日期设定
        this.setValue("PWD_ENDDATE", "9999/12/31");
        //============pangben modify 20110328 stop
        
        TTable table = getTable("TABLE"); 
        table.removeRowAll();
        TDataStore dataStore = new TDataStore();
        /** modified by WangQing 20170427 start */
        String region = "";
        if(Operator.getRegion() != null && Operator.getRegion().length() != 0){
        	region = " WHERE REGION_CODE='" + Operator.getRegion() + "' ";
        }
        String sql = "SELECT * FROM SYS_OPERATOR "+region;
        /** modified by WangQing 20170427 end */
        dataStore.setSQL(sql);
//        dataStore.setSQL(SYSSQL.getSYSOperator(Operator.getRegion())); //============pangben modify 20110524
        dataStore.retrieve();
        table.setDataStore(dataStore);
        table.setDSValue();
        int seq = getMaxSeq(table.getDataStore(), "SEQ", table.getDataStore()
                            .isFilter() ? table.getDataStore().FILTER : table
                            .getDataStore().PRIMARY);
        setValue("SEQ", seq);
        TTextField a=(TTextField)this.getComponent("USER_ID");
//      System.out.println("aseeee:"+a.getFocusLostAction());
        onClear();//wanglong add 20140529
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        if (getValueString("DEPT_CODE").length() == 0) {
            onFilter();
            TTable table = getTable("TABLE");
            if (table.getRowCount() > 0) {
                table.setSelectedRow(0);
                this.onTableClicked();
            }
            return;
        }
        onComboBoxSelected();
      //  pwdMessageShow = false; //=====pangben modify 20110526
    }

    /**
     * 清空方法
     */
    public void onClear() {
        //============pangben modify 20110328 start
        //设置强度标签不显示
        this.setValue("pwd_LBL", "");
        //============pangben modify 20110328 stop
        String clearObj =
                "USER_ID;USER_NAME;DEPT_CODE;REGION_CODE;USER_ENG_NAME;"
                + "LCS_CLASS_CODE;LCS_NO;EFF_LCS_DATE;END_LCS_DATE;"
                + "POS_CODE;ROLE_ID;USER_PASSWORD;ACTIVE_DATE;END_DATE;"
                + "RCNT_LOGIN_DATE;RCNT_LOGOUT_DATE;RCNT_IP;PUB_FUNCTION;"
                + "PY1;PY2;SEQ;FOREIGNER_FLG;ID_NO;DESCRIPTION;FULLTIME_FLG;UKEY_FLG;"//xueyf add UKEY_FLG
                +
                "CTRL_FLG;E_MAIL;SEX_CODE;ABNORMAL_TIMES;OPERATOR_STATION;OPT_CLINICAREA_TABLE;COST_CENTER_CODE;TEL1;TEL2;IS_OUT_FLG;EMR_RULE_CODE"
                // modified by WangQing 20170427
                + ";T_DC";
        clearValue(clearObj);
        // 清空科室信息表
        getTable("TABLEDEPT").removeRowAll();
        // 清空证照信息表
        getTable("TABLELISCENSE").removeRowAll();
        // 清空使用者所属诊区信息表
        getTable("OPT_CLINICAREA_TABLE").removeRowAll();
        // 清空使用者所属病区信息表
        getTable("OPT_STATION_TABLE").removeRowAll();
        // 初始化使用者所属科室列表
        showOperatorDept();
        // 显示所有操作人员列表
        showOperator();
        // 初始化使用者所属诊区列表
        showOperatorClinicarea();
        // 初始化使用者所属病区列表
        showOperatorStation();
        // 初始化页面状态
        callFunction("UI|USER_ID|setEnabled", true);
        callFunction("UI|new|setEnabled", true);
        callFunction("UI|delete|setEnabled", false);
        callFunction("UI|save|setEnabled", false);
        // 初始化当前时间
        Timestamp date = StringTool.getTimestamp(new Date());
        this.setValue("ACTIVE_DATE", date);
        this.setValue("END_DATE", "9999/12/31");
        //角色权限日期设定
        this.setValue("START_DATE", date);
        this.setValue("END_DATE_RULE", "9999/12/31");
        TTable table = getTable("TABLE");
        int seq = getMaxSeq(table.getDataStore(), "SEQ", table.getDataStore()
                            .isFilter() ? table.getDataStore().FILTER : table
                            .getDataStore().PRIMARY);
        setValue("SEQ", seq);
    //    pwdMessageShow = false; //=====pangben modify 20110526
    }

    /**
     * 添加一行新的操作者信息数据事件
     */
    public void onNew() {
        String userId = getValueString("USER_ID").trim();
        if (userId.length() == 0) {
            messageBox("请先输入使用者");
            return;
        }
        TTable table = getTable("TABLE");
        //============pangben modify 20110607 start
        String userSQL = "SELECT * FROM SYS_OPERATOR WHERE USER_ID='" + userId +
                         "'";
        TParm selParm = new TParm(TJDODBTool.getInstance().select(userSQL));
        //============pangben modify 20110607 stop
        if (selParm.getCount("USER_ID")>0) {
            messageBox("使用者已存在");
            return;
        }
        int row = table.addRow();
        Timestamp date = StringTool.getTimestamp(new Date());
        table.setItem(row, "USER_ID", userId);
        table.setSelectedRow(row);
        callFunction("UI|USER_ID|setEnabled", false);
        // 清空科室信息表
        getTable("TABLEDEPT").removeRowAll();
        // 清空证照信息表
        getTable("TABLELISCENSE").removeRowAll();
        // 清空使用者所属诊区信息表
        getTable("OPT_CLINICAREA_TABLE").removeRowAll();
        // 清空使用者所属病区信息表
        getTable("OPT_STATION_TABLE").removeRowAll();
        // 初始化使用者所属科室列表
        showOperatorDept();
        // 初始化使用者所属诊区列表
        showOperatorClinicarea();
        // 初始化使用者所属病区列表
        showOperatorStation();
        onTableClicked();
        int seq = getMaxSeq(table.getDataStore(), "SEQ", table.getDataStore()
                            .isFilter() ? table.getDataStore().FILTER : table
                            .getDataStore().PRIMARY);
        setValue("SEQ", seq);
        // 设置页面状态
        callFunction("UI|new|setEnabled", false);
        callFunction("UI|save|setEnabled", true);
        // 设置光标
        ((TTextField) getComponent("USER_NAME")).grabFocus();
        // 初始化当前时间
        this.setValue("ACTIVE_DATE", date);
        this.setValue("END_DATE", "9999/12/31");
        //角色权限日期设定
        this.setValue("START_DATE", date);
        this.setValue("END_DATE_RULE", "9999/12/31");
        
    }
    /**
     * 保存方法
     * @return boolean
     */
    public boolean onSave() {

        TParm parm = new TParm();
        if (!checkNewData()) {
            return false;
        }
        pwdPoofSth();
        //=======pangben modify 20110631 start
        //验证密码强度，若强度过低不可以保存
        TLabel pwdLbl = (TLabel)this.getComponent("PWD_POOFSTH");
        TLabel lbl = (TLabel)this.getComponent("pwd_LBL");
        if (pwdLbl.isVisible() && lbl.getValue().equals("低")) {
            this.messageBox("密码强度太低,不可以保存!");
            return false;
        }
        //=======pangben modify 20110631 stop
        TTable table = getTable("TABLE");
        int row = table.getSelectedRow();
        TDataStore dataStore = table.getDataStore();
        // 保存科室
        TTable tabledept = getTable("TABLEDEPT");
        if (tabledept.getDataStore().isModified()) {
            tabledept.acceptText();
            if (tabledept.getDataStore().rowCount() > 0) {
                Vector main_flg = new Vector();
                main_flg = tabledept.getDataStore().getVector("MAIN_FLG");
                boolean flg = false;
                for (int i = 0; i < main_flg.size(); i++) {
                    if (((Vector) main_flg.get(i)).get(0).toString()
                        .equals("Y"))
                        flg = true;
                }
                if (!flg) {
                    this.messageBox("请选择主科室");
                    return false;
                }
            }
            String[] dept_sql = tabledept.getDataStore().getUpdateSQL();
            parm.setData("DEPT", dept_sql);
            tabledept.setDSValue();
        }

        // 保存证照
        String userId = (String) table.getItemData(table.getSelectedRow(),
                "USER_ID");
        //============pangben modify 20110428 start
        //==========获得密码起始日期、有效日期、加密密码
        TParm resultPwd = OperatorTool.getInstance().getUserInfo(userId);

        /*
         * ZhenQin - 2011-05-06更改实现,
         * 记录更改用户的角色及变更日志,当用户权限，权限组，角色发生变化时,写变更信息至SYS_OPERATORLOG表
         *
         */

        //旧的角色
        String user_RoleId_old = null;
        try {
            //当时第一次时是新增,可能没有选中行
            user_RoleId_old = (String) table.getItemData(table.getSelectedRow(),
                    "ROLE_ID");
        } catch (Exception e) {
            user_RoleId_old = "";
        } finally {
            //保证user_RoleId_old != null;
            if (user_RoleId_old == null) {
                user_RoleId_old = "";
            }
            user_RoleId_old = user_RoleId_old.trim();
        }
        //新的角色
        String user_RoleId_new = this.getValueString("ROLE_ID");
        //这里显式的赋null值,在tool中,用Tparm的existData("")的方式判断是否存在此数据,存在表示需要记录日志
        TParm userInfoChange = null;
        
        //当user_RoleId_old和user_RoleId_new的值不相等,表示操作者更改了用户角色
        if (!user_RoleId_new.equals(user_RoleId_old)) {
            userInfoChange = new TParm();
            
            /*
             * 这里用了add的方式添加数据,目前支持记录用户角色时保存日志,
             * 以后肯能记录其他信息,用add的方式添加数据即可
             */
            userInfoChange.addData("MODI_ITEM",
                                   user_RoleId_old.equals("") ? "I" : "R");
            userInfoChange.addData("MODI_ITEM_CHN",
                                   user_RoleId_old.equals("") ? "新增用户角色" :
                                   "改变角色");
            userInfoChange.addData("ITEM_OLD",
                                   user_RoleId_old.equals("") ? "" : user_RoleId_old);
            userInfoChange.addData("ITEM_NEW", user_RoleId_new);
            userInfoChange.addData("DESCRIPTION",
                                   user_RoleId_old.equals("") ? "新增用户角色" :
                                   "改变角色");

            /*
             * 操作者每次提交保存的永远是单个用户的数据,下面这些数据再一次事务中有一条即可
             */
            userInfoChange.setData("USER_ID", this.getValue("USER_ID"));
            userInfoChange.setData("REGION_CODE", Operator.getRegion());
            userInfoChange.setData("OPT_USER", Operator.getID());
            userInfoChange.setData("OPT_TERM", Operator.getIP());
            userInfoChange.setData("OPT_DATE", SystemTool.getInstance().getDate());
            
            
        }
        // add start machao

	String sql ="SELECT ID FROM EMR_RULE_USER WHERE USER_EMR_ID = '"+ userId+"'" ;
	TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql));
	System.out.println("mmm::"+parm1.getCount());
	if(parm1.getCount()>0){
		String startDate = this.getValueString("START_DATE");
		String start = "";
		if(StringUtil.isNullString(startDate)){
			
		}else{
			start = startDate.substring(0,19);
		}
		
		String endDate = this.getValueString("END_DATE_RULE");
		String endEmrDate = "";
		if(StringUtil.isNullString(endDate)){
			
		}else{
			endEmrDate = endDate.substring(0,19);
		}
		
		String sql2 = "UPDATE EMR_RULE_USER SET EMR_RULE_CODE='"+this.getValue("EMR_RULE_CODE")+"' ";
		
    	if(StringUtil.isNullString(start)){
    		
    	}else{
    		sql2+=" , START_DATE = TO_DATE('"+start+"','yyyy-MM-dd HH24:MI:ss') ";
    	}
    	if(StringUtil.isNullString(endEmrDate)){
    		
    	}else{
    		sql2+="  , END_EMR_DATE = TO_DATE('"+endEmrDate+"','yyyy-MM-dd HH24:MI:ss')";
    	}
    	sql2 +=  " WHERE USER_EMR_ID='"+userId+"'"; 
    	System.out.println("ma:::"+sql2);
    	TJDODBTool.getInstance().update(sql2);
	}else{
		TParm emrRuleUser = new TParm();
        TNull tnull = new TNull(Timestamp.class);
        emrRuleUser.setData("ID1", this.getValue("USER_ID"));
        if(this.getValueString("EMR_RULE_CODE").length()>0){
        	emrRuleUser.setData("EMR_RULE_CODE1", this.getValue("EMR_RULE_CODE"));
        }else{
        	emrRuleUser.setData("EMR_RULE_CODE1", "");
        }
        emrRuleUser.setData("USER_ID1", this.getValue("USER_ID"));
        Timestamp beginTime = (Timestamp) getValue("START_DATE");
        Timestamp endTime = (Timestamp) getValue("END_DATE_RULE");
        if(beginTime == null){
        	emrRuleUser.setData("START_DATE1", tnull);
        }else{
        	emrRuleUser.setData("START_DATE1", beginTime);
        }
        if(endTime == null){
        	emrRuleUser.setData("END_DATE1", tnull);
        }else{
        	emrRuleUser.setData("END_DATE1", endTime);
        }

        parm.setData("emrRuleUser", emrRuleUser.getData());
	}

        //System.out.println("machao::"+emrRuleUser);
        // add end machao
        //解密
        String oldPWD = OperatorTool.getInstance().decrypt(
                resultPwd.getValue("USER_PASSWORD", 0));
        //============pangben modify 20110428 start
        Timestamp date = StringTool.getTimestamp(new Date());
        TTable tableliscense = getTable("TABLELISCENSE");
        tableliscense.acceptText();
        TDataStore dataStore2 = tableliscense.getDataStore();
        int rows[] = dataStore2.getModifiedRows(dataStore2.PRIMARY);
        if (dataStore2.isModified()) {
            for (int i = 0; i < rows.length; i++) {
                dataStore2.setItem(rows[i], "USER_ID", userId);
                dataStore2.setItem(rows[i], "OPT_USER", Operator.getID());
                dataStore2.setItem(rows[i], "OPT_DATE", date);
                dataStore2.setItem(rows[i], "OPT_TERM", Operator.getIP());
            }
            String[] liscense_sql = tableliscense.getDataStore().getUpdateSQL();
            parm.setData("LISCENSE", liscense_sql);
            tableliscense.setDSValue();
        }

        TTable clinicarea = getTable("OPT_CLINICAREA_TABLE");
        TDataStore clinicareaDS = clinicarea.getDataStore();
        TTable station = getTable("OPT_STATION_TABLE");
        TDataStore stationDS = station.getDataStore();

        // 判断是否存在多个主诊区或多个主病区
        if (clinicareaDS.rowCount() > 0 || stationDS.rowCount() > 0) {
            int countNum = 0;
            for (int i = 0; i < clinicareaDS.rowCount(); i++) {
                Vector main_flg = new Vector();
                main_flg = clinicareaDS.getVector("MAIN_FLG");
                if (((Vector) main_flg.get(i)).get(0).toString().equals("Y"))
                    countNum++;
            }
            for (int i = 0; i < stationDS.rowCount(); i++) {
                Vector main_flg = new Vector();
                main_flg = stationDS.getVector("MAIN_FLG");
                if (((Vector) main_flg.get(i)).get(0).toString().equals("Y"))
                    countNum++;
            }
            if (countNum == 0) {
                this.messageBox("请选择主诊区或主病区");
                return false;
            }
            if (countNum > 1) {
                this.messageBox("只能选择一个主诊区或主病区");
                return false;
            }
        }

        // 保存诊区
        if (clinicareaDS.isModified()) {
            clinicarea.acceptText();
            String[] clinicarea_sql = clinicarea.getDataStore().getUpdateSQL();
            parm.setData("CLINICAREA", clinicarea_sql);
            clinicarea.setDSValue();
        }

        // 保存病区
        if (stationDS.isModified()) {
            station.acceptText();
            String[] station_sql = station.getDataStore().getUpdateSQL();
            parm.setData("STATION", station_sql);
            station.setDSValue();
        }

        // 向DataStore填充数据
        dataStore.setItem(row, "USER_NAME", getValueString("USER_NAME"));
        dataStore.setItem(row, "PY1", getValueString("PY1"));
        dataStore.setItem(row, "PY2", getValueString("PY2"));
        dataStore.setItem(row, "SEQ", getValueInt("SEQ"));
        dataStore.setItem(row, "USER_ENG_NAME", getValueString("USER_ENG_NAME"));
        dataStore.setItem(row, "FOREIGNER_FLG", getValue("FOREIGNER_FLG"));
        dataStore.setItem(row, "ID_NO", getValueString("ID_NO"));
        dataStore.setItem(row, "SEX_CODE", getValueString("SEX_CODE"));
        dataStore.setItem(row, "DESCRIPTION", getValueString("DESCRIPTION"));
        dataStore.setItem(row, "POS_CODE", getValueString("POS_CODE"));
        String password = OperatorTool.getInstance().encrypt(
                getValueString("USER_PASSWORD"));
        //===============pangben modify 20110428 start
        //判段密码是否相同,如果相同提示消息框,判断密码更新日期是否是当前时间
        // if(pwdMessageShow){//判断密码是否修改
        Timestamp loginTime = resultPwd.getTimestamp("PWD_STARTDATE", 0);
        if (null != loginTime && !loginTime.equals("")) {
           // if(){//判断密码是否修改
           if (!oldPWD.equals(getValueString("USER_PASSWORD"))) {
              dataStore.setItem(row, "PWD_STARTDATE", date);
           }
       }
        // }
       //  pwdMessageShow = false; //密码消息框显示状态
        //===============pangben modify 20110428 start
        dataStore.setItem(row, "USER_PASSWORD", password);
        dataStore.setItem(row, "FULLTIME_FLG", getValue("FULLTIME_FLG"));
        dataStore.setItem(row, "CTRL_FLG", getValue("CTRL_FLG"));
        //xueyf modify 2011-12-28 start
        dataStore.setItem(row, "UKEY_FLG", getValue("UKEY_FLG"));
        //xueyf modify 2011-12-28 end
        dataStore.setItem(row, "E_MAIL", getValue("E_MAIL"));
        dataStore.setItem(row, "REGION_CODE", getValueString("REGION_CODE"));
        dataStore.setItem(row, "ROLE_ID", getValueString("ROLE_ID"));
        Timestamp begin = (Timestamp) getValue("ACTIVE_DATE");
        Timestamp end = (Timestamp) getValue("END_DATE");

        dataStore.setItem(row, "ACTIVE_DATE", begin);
        dataStore.setItem(row, "END_DATE", end);
        dataStore.setItem(row, "IS_OUT_FLG", getValue("IS_OUT_FLG"));
        //增加功能添加密碼有效時間
        //===============pangben modify 20110324 start
        Timestamp PWD_ENDDATE = (Timestamp) getValue("PWD_ENDDATE"); 
        dataStore.setItem(row, "PWD_ENDDATE", PWD_ENDDATE);
        //===============pangben modify 20110324 stop
        dataStore.setItem(row, "ABNORMAL_TIMES", getValueInt("ABNORMAL_TIMES"));
        dataStore.setItem(row, "PUB_FUNCTION", getValueString("PUB_FUNCTION"));
        dataStore.setItem(row, "OPT_USER", Operator.getID());
        dataStore.setItem(row, "OPT_DATE", date);
        dataStore.setItem(row, "OPT_TERM", Operator.getIP());
        //add shibaoliu 20120315
        dataStore.setItem(row, "COST_CENTER_CODE", this.getValueString("COST_CENTER_CODE"));
        dataStore.setItem(row, "TEL1", this.getValueString("TEL1"));
        dataStore.setItem(row, "TEL2", this.getValueString("TEL2"));
        // 向SYS_OPERATOR表中添加证照信息
        if (tableliscense.getDataStore().rowCount() > 0) {
            String lsc_no = tableliscense.getItemString(0, "LCS_NO");
            Timestamp eff_date = tableliscense.getItemTimestamp(0,
                    "EFF_LCS_DATE");
            Timestamp end_date = tableliscense.getItemTimestamp(0,
                    "END_LCS_DATE");
            dataStore.setItem(row, "LCS_NO", lsc_no);
            dataStore.setItem(row, "EFF_LCS_DATE", eff_date);
            dataStore.setItem(row, "END_LCS_DATE", end_date);
        } else {
            dataStore.setItem(row, "LCS_NO", "");
            dataStore.setItem(row, "EFF_LCS_DATE", "");
            dataStore.setItem(row, "END_LCS_DATE", "");
        }
        /** modified by WangQing 20170427 start */
        dataStore.setItem(row, "T_DC", this.getValueString("T_DC"));
        /** modified by WangQing 20170427 end */
        // 保存使用者数据
        if (table.getDataStore().isModified()) {
            table.acceptText();
            String[] operator_sql = table.getDataStore().getUpdateSQL();
//            int count = operator_sql.length;
            for(int i=0; i<operator_sql.length; i++){
            	System.out.println("***operator_sql["+i+"]:"+operator_sql[i]);
            }            
            parm.setData("OPERATOR", operator_sql);
            table.setDSValue(); 
        }
        // 调用保存动作
        TParm result = TIOM_AppServer.executeAction(
                "action.sys.SYSOperatorAction",
                "onSaveOperator", parm);
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return false;
        }
       
//        String sql1 = "SELECT USER_ID FROM SYS_OPERATOR WHERE USER_ID ='"+userId+"'";
//        TParm resultParm = new TParm(TJDODBTool.getInstance().select(sql1));
//        if(resultParm.getCount()>0){
//        	String sql ="SELECT ID FROM EMR_RULE_USER WHERE USER_EMR_ID = '"+ userId+"'" ;
//        	TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql));
//        	String startDate = this.getValueString("START_DATE");
//        	String start = "";
//        	if(startDate == null){
//        		
//        	}else{
//        		start = startDate.substring(0,19);
//        	}
//        	
//        	String endDate = this.getValueString("END_DATE_RULE");
//        	String endEmrDate = "";
//        	if(endDate == null){
//        		
//        	}else{
//        		endEmrDate = endDate.substring(0,19);
//        	}
//        	
//        	
//        	if(parm1.getCount()>0){//更改方法
//            	String sql2 = "UPDATE EMR_RULE_USER SET EMR_RULE_CODE='"+this.getValue("EMR_RULE_CODE");
//		        		
//            	if(StringUtil.isNullString(start)){
//            		
//            	}else{
//            		sql2+="' , START_DATE = TO_DATE('"+start+"','yyyy-MM-dd HH24:MI:ss') ";
//            	}
//            	if(StringUtil.isNullString(endEmrDate)){
//            		
//            	}else{
//            		sql2+="  , END_EMR_DATE = TO_DATE('"+endEmrDate+"','yyyy-MM-dd HH24:MI:ss')";
//            	}
//            	sql2 +=  " WHERE USER_EMR_ID='"+userId+"'"; 
//            	System.out.println("mac::"+sql2);
//            	TJDODBTool.getInstance().update(sql2);
//        	}else{//插入方法
//        		String emrId = this.getValueString("USER_ID");
//        		String emrRuleCode = this.getValueString("EMR_RULE_CODE");
//        		String emrUserId = this.getValueString("USER_ID");
//        		TParm parm2 = new TParm();
//        		parm2.setData("ID", emrId);
//        		parm2.setData("EMR_RULE_CODE", emrRuleCode);
//        		parm2.setData("USER_EMR_ID", emrUserId);
//        		
//        		parm2.setData("START_DATE", (Timestamp) getValue("START_DATE"));
//        		parm2.setData("END_EMR_DATE", (Timestamp) getValue("END_DATE_RULE"));
//        		Boolean flag = SYSOperatorTool.getInstance().onInsertEmr(parm2);
//        		if(!flag){
//        			this.messageBox("保存角色权限失败");
//        		}
//        	}
//        }
        
        table.setSelectedRow(row);
        messageBox("P0001");
        // 设置页面状态
        callFunction("UI|new|setEnabled", true);
        callFunction("UI|save|setEnabled", false);
        return true;
    }

    /**
     * 删除方法
     */
    public void onDelete() {
        TTable table = getTable("TABLE");
        int row = table.getTable().getSelectedRow();
        if (row < 0)
            return;
        // 操作者证照
        int count1 = this.getTable("TABLELISCENSE").getRowCount();
        if (count1 > 0) {
            this.messageBox("该操作者存在证照信息，请删除");
            return;
        }
        // 操作者所属科室
        int count2 = this.getTable("TABLEDEPT").getRowCount();
        if (count2 > 0) {
            this.messageBox("该操作者存在所属科室，请删除");
            return;
        }
        // 删除操作确认
        switch (messageBox("提示信息", "是否删除该操作者?", 0)) {
        case 0:
            table.removeRow(row);

            // 删除科室
            TTable tabledept = getTable("TABLEDEPT");
            if (tabledept.getDataStore().isModified()) {
                tabledept.acceptText();
            }
            if (!tabledept.update()) {
                return;
            }
            tabledept.setDSValue();

            // 删除证照
            TTable tableliscense = getTable("TABLELISCENSE");
            tableliscense.acceptText();
            TDataStore dataStore2 = tableliscense.getDataStore();
            if (dataStore2.isModified()) {
                if (!tableliscense.update()) {
                    return;
                }
                tableliscense.setDSValue();
            }

            TTable clinicarea = getTable("OPT_CLINICAREA_TABLE");
            TDataStore clinicareaDS = clinicarea.getDataStore();
            TTable station = getTable("OPT_STATION_TABLE");
            TDataStore stationDS = station.getDataStore();

            // 删除诊区
            if (clinicareaDS.isModified()) {
                clinicarea.acceptText();
                if (!clinicarea.update()) {
                    return;
                }
                clinicarea.setDSValue();
            }

            // 删除病区
            if (stationDS.isModified()) {
                station.acceptText();
                if (!station.update()) {
                    return;
                }
                station.setDSValue();
            }

            // 删除使用者数据
            if (table.getDataStore().isModified()) {
                table.acceptText();
                if (!table.update()) {
                    messageBox("删除失败");
                    return;
                }
                table.setDSValue();
            }
            messageBox("删除成功");

            break;
        case 1:
            return;

        }
        // 删除按钮状态设置
        if (table.getRowCount() > 0) {
            table.setSelectedRow(0);
            onTableClicked();
        } else {
            callFunction("UI|delete|setEnabled", false);
        }

    }

    /**
     * 用户制卡
     */
    public void onCreatEKT() {
        openDialog("%ROOT%\\config\\ekt\\SaveUseIDDialog.x");
    }

    /**
     * 过滤条件
     */
    public void onFilter() {
        boolean flg = ((TTextField) getComponent("USER_ID")).isEnabled();
        // 如果为不可使用状态，则进行数据更新
        if (!flg) {
            TTable table = getTable("TABLE");
            int row = table.getSelectedRow();
            // 使用者名称
            String userName = getValueString("USER_NAME");
            table.setItem(row, "USER_NAME", userName);
            // 拼音
            String py = TMessage.getPy(userName);
            setValue("PY1", py);
            // 性别
            String sex = getValueString("SEX_CODE");
            table.setItem(row, "SEX_CODE", sex);
            // 区域
            String region = getValueString("REGION_CODE");
            table.setItem(row, "REGION_CODE", region);
            // 职称
            String pos = getValueString("POS_CODE");
            table.setItem(row, "POS_CODE", pos);
            // 角色 ROLE_ID
            String role = getValueString("ROLE_ID");
            table.setItem(row, "ROLE_ID", role);
            return;
        }
        // 查询操作者
        String value = "";
        String str = getValueString("USER_NAME");
        if (str.length() != 0)
            value += "USER_NAME like '" + str + "%'";
        str = getValueString("USER_ID");
        if (str.length() != 0) {
            if (value.length() != 0)
                value += " AND ";
            value += "USER_ID like '" + str + "%'";
        }
        str = getValueString("REGION_CODE");
        if (str.length() != 0) {
            if (value.length() != 0)
                value += " AND ";
            value += "REGION_CODE = '" + str + "'";
        }
        str = getValueString("POS_CODE");
        if (str.length() != 0) {
            if (value.length() != 0)
                value += " AND ";
            value += "POS_CODE = '" + str + "'";
        }
        str = getValueString("ROLE_ID");
        if (str.length() != 0) {
            if (value.length() != 0)
                value += " AND ";
            value += "ROLE_ID = '" + str + "'";
        }
        TTable table1 = getTable("TABLE");
        if (value.length() > 0) {
            table1.setFilter(value);
            table1.filter();
            return;
        }
        table1.setFilter("");
        table1.filter();
    }

    /**
     * UserId回车事件
     */
    public void onUserIdAction() {
        ((TTextField) getComponent("USER_NAME")).grabFocus();
    }

    /**
     * UserName回车事件
     */
    public void onUserNameAction() {
        boolean flg = ((TTextField) getComponent("USER_ID")).isEnabled();
        if (!flg) {
            TTable table = getTable("TABLE");
            int row = table.getSelectedRow();
            String userName = getValueString("USER_NAME");
            table.setItem(row, "USER_NAME", userName);
            String py = TMessage.getPy(userName);
            setValue("PY1", py);
            setValue("USER_ENG_NAME", py);
        }
        ((TTextField) getComponent("PY1")).grabFocus();
    }

    /**
     * POS_CODE回车事件
     */
    public void onPosCodeAction() {
        ((TCheckBox) getComponent("FULLTIME_FLG")).grabFocus();
    }


    /**
     * REGION_CODE回车事件
     */
    public void onRegionCodeAction() {
        ((TTextFormat) getComponent("POS_CODE")).grabFocus();
    }

    /**
     * ROLE_ID回车事件
     */
    public void onRoleIdAction() {
        ((TTextFormat) getComponent("ACTIVE_DATE")).grabFocus();
    }

    /**
     * ACTIVE_DATE回车事件
     */
    public void onActiveDateAction() {
        ((TTextFormat) getComponent("END_DATE")).grabFocus();
    }

    /**
     * END_DATE回车事件
     */
    public void onEndDateAction() {
        ((TTextField) getComponent("PUB_FUNCTION")).grabFocus();
    }

    /**
     * ID_NO回车事件
     */
    public void onIdNOAction() {
        String flg = getValueString("FOREIGNER_FLG");
        String id_no = getValueString("ID_NO");
        if (flg.equals("N") && StringTool.isId(id_no)) {
            String sex = StringTool.isMaleFromID(id_no);
            this.setValue("SEX_CODE", sex);
        }
        ((TComboBox) getComponent("SEX_CODE")).grabFocus();
    }

    /**
     * EFF_LCS_DATE回车事件
     */
    public void onEffLCSDateAction() {
        ((TTextFormat) getComponent("END_LCS_DATE")).grabFocus();
    }

    /**
     * 科室ComboBox选中事件
     */
    public void onComboBoxSelected() {
        String dept = getValueString("DEPT_CODE");
        if (((TMenuItem) getComponent("new")).isEnabled()) {
            // 根据科室查询人员
            String sql = "SELECT USER_ID,DEPT_CODE FROM SYS_OPERATOR_DEPT";
            if (dept.length() > 0) {
                sql += " WHERE DEPT_CODE = '" + dept + "'";
            }
            TDataStore st = new TDataStore();
            st.setSQL(sql);
            st.retrieve();
            int count = st.rowCount();

            /** modified by WangQing 20170427 start */
            String region = "";
            if(Operator.getRegion() != null && Operator.getRegion().length() != 0){
            	region = " WHERE REGION_CODE='" + Operator.getRegion() + "' ";
            }
            String sql2 = "SELECT * FROM SYS_OPERATOR "+region+
                    " AND USER_ID IN ("
                    + "'" + st.getItemData(0, "USER_ID") + "'";
//            String sql2 = SYSSQL.getSYSOperator(Operator.getRegion()) +
//                          " AND USER_ID IN ("
//                          + "'" + st.getItemData(0, "USER_ID") + "'";
            for (int i = 1; i < count; i++) {
                sql2 = sql2 + ",'" + st.getItemData(i, "USER_ID") + "'";
            }
            sql2 += ")";
            /** modified by WangQing 20170427 end */
            TDataStore dataStroe = new TDataStore();
            dataStroe.setSQL(sql2);
            dataStroe.retrieve();
            TTable table1 = getTable("TABLE");
            table1.setDataStore(dataStroe);
            table1.setDSValue();
            onFilter();
        }
    }

    /**
     * 操作者信息表格(TABLE)单击事件
     */
    public void onTableClicked() {
        //====pangben modify 20110429 start 清空强度校验
    //    pwdMessageShow = false; //不显示密码修改消息框
        this.setValue("pwd_LBL", "");
        //====pangben modify 20110429 stop
        // 清空科室
        this.setValue("DEPT_CODE", "");
        // 设置新增、删除按钮状态
        if (getTable("TABLE").getSelectedRow() != -1) {
            callFunction("UI|delete|setEnabled", true);
            callFunction("UI|new|setEnabled", false);
        }
        // 得到被选中行的数据
        TParm parm = getTable("TABLE").getDataStore().getRowParm(
                getTable("TABLE").getSelectedRow());
        System.out.println(parm);
        // 在UI中显示选中行的数据
        setValue("USER_ID", parm.getData("USER_ID"));
        setValue("USER_NAME", parm.getData("USER_NAME"));
        setValue("USER_ENG_NAME", parm.getData("USER_ENG_NAME"));
        setValue("PY1", parm.getData("PY1"));
        setValue("PY2", parm.getData("PY2"));
        setValue("SEQ", parm.getData("SEQ"));
        setValue("FOREIGNER_FLG", parm.getData("FOREIGNER_FLG"));
        setValue("ID_NO", parm.getData("ID_NO"));
        setValue("SEX_CODE", parm.getData("SEX_CODE"));
        setValue("DESCRIPTION", parm.getData("DESCRIPTION"));
        setValue("POS_CODE", parm.getData("POS_CODE"));
        String pwd = OperatorTool.getInstance().decrypt(
                (String) parm.getData("USER_PASSWORD"));
        setValue("USER_PASSWORD", pwd);
        setValue("FULLTIME_FLG", parm.getData("FULLTIME_FLG"));
        setValue("CTRL_FLG", parm.getData("CTRL_FLG"));
        //xueyf add UKEY_FLG
        setValue("UKEY_FLG", parm.getData("UKEY_FLG"));
        setValue("E_MAIL", parm.getData("E_MAIL"));
        setValue("REGION_CODE", parm.getData("REGION_CODE"));
        setValue("ROLE_ID", parm.getData("ROLE_ID"));
        setValue("ACTIVE_DATE", parm.getData("ACTIVE_DATE"));
        setValue("END_DATE", parm.getData("END_DATE"));
        setValue("PUB_FUNCTION", parm.getData("PUB_FUNCTION"));
        setValue("RCNT_LOGIN_DATE", parm.getData("RCNT_LOGIN_DATE"));
        setValue("RCNT_LOGOUT_DATE", parm.getData("RCNT_LOGOUT_DATE"));
        setValue("RCNT_IP", parm.getData("RCNT_IP"));
        setValue("ABNORMAL_TIMES", parm.getData("ABNORMAL_TIMES"));
        setValue("COST_CENTER_CODE", parm.getData("COST_CENTER_CODE"));
        setValue("TEL1", parm.getData("TEL1"));
        setValue("TEL2", parm.getData("TEL2"));
        setValue("IS_OUT_FLG", parm.getData("IS_OUT_FLG"));
        /** modified by WangQing 20170427 start */
        setValue("T_DC", parm.getData("T_DC"));
        /** modified by WangQing 20170427 end */
        
        String sql1 = "SELECT EMR_RULE_CODE,START_DATE,END_EMR_DATE FROM EMR_RULE_USER WHERE USER_EMR_ID = '"+parm.getData("USER_ID")+"'";
        TParm emrRuleParm = new TParm(TJDODBTool.getInstance().select(sql1));
        System.out.println(emrRuleParm);
        setValue("EMR_RULE_CODE", emrRuleParm.getData("EMR_RULE_CODE",0));
        setValue("START_DATE", emrRuleParm.getData("START_DATE",0));
        setValue("END_DATE_RULE", emrRuleParm.getData("END_EMR_DATE",0));
        
        //===========pangben modify 20110324 start
        if (null != parm.getData("PWD_ENDDATE"))
            setValue("PWD_ENDDATE", parm.getData("PWD_ENDDATE"));
        else
            setValue("PWD_ENDDATE", "9999/12/31");
        //===========pangben modify 20110324 stop
        
        // 显示指定操作者的所属科室
        String userId = parm.getValue("USER_ID");
        TTable table = getTable("TABLEDEPT");
        String sql = SYSSQL.getOperatorDeptByUserId(userId);
        TDataStore dataStroe = new TDataStore();
        dataStroe.setSQL(sql);
        dataStroe.retrieve();
        table.setDataStore(dataStroe);
        table.setDSValue();
        // 显示指定操作者的所属诊区
        TTable clinicarea_table = getTable("OPT_CLINICAREA_TABLE");
        sql = SYSSQL.getSYSOperatorStation("1", userId);
        TDataStore dataStroeClinic = new TDataStore();
        dataStroeClinic.setSQL(sql);
        dataStroeClinic.retrieve();
        clinicarea_table.setDataStore(dataStroeClinic);
        clinicarea_table.setDSValue();
        // 显示指定操作者的所属病区
        TTable station_table = getTable("OPT_STATION_TABLE");
        sql = SYSSQL.getSYSOperatorStation("2", userId);
        TDataStore dataStroeStation = new TDataStore();
        dataStroeStation.setSQL(sql);
        dataStroeStation.retrieve();
        station_table.setDataStore(dataStroeStation);
        station_table.setDSValue();
        // 查询指定用户的证照信息
        showOperatorLicense(userId);
        // 改变输入框状态
        callFunction("UI|USER_ID|setEnabled", false);
        // 设置保存按钮状态
        callFunction("UI|save|setEnabled", true);
    }

    /**
     * 证照信息表格(TableLiscense)单击事件
     */
    public void onTableLiscenseClicked() {
        TParm parm = getTable("TABLELISCENSE").getDataStore().getRowParm(
                getTable("TABLELISCENSE").getSelectedRow());
        setValue("LCS_CLASS_CODE", parm.getData("LCS_CLASS_CODE"));
        setValue("LCS_NO", parm.getData("LCS_NO"));
        setValue("EFF_LCS_DATE", parm.getData("EFF_LCS_DATE"));
        setValue("END_LCS_DATE", parm.getData("END_LCS_DATE"));
    }

    /**
     * 添加科室按钮(INSERT_DEPT)
     */
    public void onInsertDeptClicked() {
        Timestamp date = StringTool.getTimestamp(new Date());
        String dept_code = this.getValueString("DEPT_CODE");
        if ("".equals(dept_code)) {
            this.messageBox("请选择添加科室");
            return;
        }
        TTable table = getTable("TABLEDEPT");
        for (int i = 0; i < table.getRowCount(); i++) {
            if (dept_code.equals(table.getDataStore().getItemString(i,
                    "DEPT_CODE"))) {
                this.messageBox("科室已存在，不可添加");
                return;
            }
        }
        int count = table.getRowCount();
        int newRow = table.addRow();
        String userId = getTable("TABLE").getDataStore().getItemString(
                getTable("TABLE").getSelectedRow(), "USER_ID");
        if (count == 0)
            table.setItem(newRow, "MAIN_FLG", "Y");
        else
            table.setItem(newRow, "MAIN_FLG", "N");
        table.setItem(newRow, "DEPT_CODE", dept_code);
        table.setItem(newRow, "USER_ID", userId);
        table.setItem(newRow, "OPT_USER", Operator.getID());
        table.setItem(newRow, "OPT_DATE", date);
        table.setItem(newRow, "OPT_TERM", Operator.getIP());
    }

    /**
     * 移除科室单击按钮(REMOVE_DEPT)
     */
    public void onRemoveDeptClicked() {
        if (getTable("TABLEDEPT").getSelectedRow() == -1)
            return;
        getTable("TABLEDEPT").removeRow(getTable("TABLEDEPT").getSelectedRow());
    }

    /**
     * 单击Insert_CLINICAREA按钮
     */
    public void onInsertClinicareaClicked() {
        Timestamp date = StringTool.getTimestamp(new Date());
        String clinic_area = this.getValueString("OPERATOR_CLINIC");
        if ("".equals(clinic_area)) {
            this.messageBox("请选择添加诊区");
            return;
        }
        TTable table = getTable("OPT_CLINICAREA_TABLE");
        for (int i = 0; i < table.getRowCount(); i++) {
            if (clinic_area.equals(table.getDataStore().getItemString(i,
                    "STATION_CLINIC_CODE"))) {
                this.messageBox("诊区已存在，不可添加");
                return;
            }
        }
        int newRow = table.addRow();
        String userId = getTable("TABLE").getDataStore().getItemString(
                getTable("TABLE").getSelectedRow(), "USER_ID");
        table.setItem(newRow, "MAIN_FLG", "N");
        table.setItem(newRow, "STATION_CLINIC_CODE", clinic_area);
        table.setItem(newRow, "USER_ID", userId);
        table.setItem(newRow, "TYPE", 1);
        table.setItem(newRow, "OPT_USER", Operator.getID());
        table.setItem(newRow, "OPT_DATE", date);
        table.setItem(newRow, "OPT_TERM", Operator.getIP());
    }

    /**
     * 单击Remove_CLINICAREA按钮
     */
    public void onRemoveClinicareaClicked() {
        if (getTable("OPT_CLINICAREA_TABLE").getSelectedRow() == -1)
            return;
        getTable("OPT_CLINICAREA_TABLE").removeRow(getTable(
                "OPT_CLINICAREA_TABLE").getSelectedRow());
    }

    /**
     * 单击Insert_Station按钮
     */
    public void onInsertStationClicked() {
        Timestamp date = StringTool.getTimestamp(new Date());
        String station_area = this.getValueString("OPERATOR_STATION");
        if ("".equals(station_area)) {
            this.messageBox("请选择添加病区");
            return;
        }
        TTable table = getTable("OPT_STATION_TABLE");
        for (int i = 0; i < table.getRowCount(); i++) {
            if (station_area.equals(table.getDataStore().getItemString(i,
                    "STATION_CLINIC_CODE"))) {
                this.messageBox("诊区已存在，不可添加");
                return;
            }
        }
        int newRow = table.addRow();
        String userId = getTable("TABLE").getDataStore().getItemString(
                getTable("TABLE").getSelectedRow(), "USER_ID");
        table.setItem(newRow, "MAIN_FLG", "N");
        table.setItem(newRow, "STATION_CLINIC_CODE", station_area);
        table.setItem(newRow, "USER_ID", userId);
        table.setItem(newRow, "TYPE", 2);
        table.setItem(newRow, "OPT_USER", Operator.getID());
        table.setItem(newRow, "OPT_DATE", date);
        table.setItem(newRow, "OPT_TERM", Operator.getIP());
    }

    /**
     * 单击Remove_STATION按钮
     */
    public void onRemoveStationClicked() {
        if (getTable("OPT_STATION_TABLE").getSelectedRow() == -1)
            return;
        getTable("OPT_STATION_TABLE").removeRow(getTable(
                "OPT_STATION_TABLE").getSelectedRow());
    }
    /**
     * 操作者所属科室表格(TABLEDEPT)复选框改变事件
     * @param obj Object
     */
    public void onTableDeptCheckBoxClicked(Object obj) {
        // 获得点击的table对象
        TTable tableDown = (TTable) obj;
        // 只有执行该方法后才可以在光标移动前接受动作效果（框架需要）
        tableDown.acceptText();
        // 获得选中的行
        int row = tableDown.getSelectedRow();
        // 取消所有选中
        for (int i = 0; i < tableDown.getRowCount(); i++) {
            tableDown.setItem(i, "MAIN_FLG", "N");
            tableDown.getDataStore().setItem(i, "MAIN_FLG", "N");
        }
        // 选择被选中行
        tableDown.setItem(row, "MAIN_FLG", "Y");
        tableDown.getDataStore().setItem(row, "MAIN_FLG", "Y");
    }
    /**
     * 操作者所属诊区表格(OPT_CLINICAREA_TABLE)复选框改变事件
     * @param obj Object
     */
    public void onTableOPT_ClinicareaCheckBoxClicked(Object obj) {
        // 获得点击的table对象
        TTable tableDown = (TTable) obj;
        // 只有执行该方法后才可以在光标移动前接受动作效果（框架需要）
        tableDown.acceptText();
        // 获得选中的行
        int row = tableDown.getSelectedRow();
        // 取消所有选中
        for (int i = 0; i < tableDown.getRowCount(); i++) {
            if (row == i)
                continue;
            tableDown.setItem(i, "MAIN_FLG", "N");
            tableDown.getDataStore().setItem(i, "MAIN_FLG", "N");
        }
    }
    /**
     * 操作者所属病区表格(OPT_Station_TABLE)复选框改变事件
     * @param obj Object
     */
    public void onTableOPT_StationCheckBoxClicked(Object obj) {
        // 获得点击的table对象
        TTable tableDown = (TTable) obj;
        // 只有执行该方法后才可以在光标移动前接受动作效果（框架需要）
        tableDown.acceptText();
        // 获得选中的行
        int row = tableDown.getSelectedRow();
        // 取消所有选中
        for (int i = 0; i < tableDown.getRowCount(); i++) {
            if (row == i)
                continue;
            tableDown.setItem(i, "MAIN_FLG", "N");
            tableDown.getDataStore().setItem(i, "MAIN_FLG", "N");
        }
    }

    /**
     * 添加新的证照
     */
    public void onInsertLiscenseClicked() {
        if (getTable("TABLE").getSelectedRow() == -1) {
            messageBox("请选择使用者");
            return;
        }
        String code = getValueString("LCS_CLASS_CODE").trim();
        TTable table = getTable("TABLELISCENSE");
        if (table.getDataStore().exist("LCS_CLASS_CODE='" + code + "'")) {
            messageBox("证照已存在");
            return;
        }
        if (getValueString("LCS_CLASS_CODE").trim().length() == 0) {
            messageBox("证照类别不能为空");
            return;
        }
        if (getValueString("LCS_NO").trim().length() == 0) {
            messageBox("证照号码不能为空");
            return;
        }
        Timestamp begin = (Timestamp) getValue("EFF_LCS_DATE");
        if (begin == null) {
            messageBox("证照起日不能为空");
            return;
        }
        Timestamp end = (Timestamp) getValue("END_LCS_DATE");
        if (end == null) {
            messageBox("证照讫日不能为空");
            return;
        }
        if (end.compareTo(begin) <= 0) {
            messageBox("证照起日不能大于证照讫日");
            return;
        }
        int row = table.addRow();
        table.setItem(row, "LCS_CLASS_CODE", code);
        table.setItem(row, "LCS_NO", getValueString("LCS_NO").trim());
        table.setItem(row, "EFF_LCS_DATE", begin);
        table.setItem(row, "END_LCS_DATE", end);
    }


    /**
     * 更新证照
     */
    public void onUpdateLiscenseClicked() {
        String code = getValueString("LCS_CLASS_CODE").trim();
        TTable table = getTable("TABLELISCENSE");
        int row = table.getSelectedRow();
        if (row < 0) {
            this.messageBox("请选择修改证照");
            return;
        }
        if (getValueString("LCS_CLASS_CODE").trim().length() == 0) {
            messageBox("证照类别不能为空");
            return;
        }
        if (getValueString("LCS_NO").trim().length() == 0) {
            messageBox("证照号码不能为空");
            return;
        }
        Timestamp begin = (Timestamp) getValue("EFF_LCS_DATE");
        if (begin == null) {
            messageBox("证照起日不能为空");
            return;
        }
        Timestamp end = (Timestamp) getValue("END_LCS_DATE");
        if (end == null) {
            messageBox("证照讫日不能为空");
            return;
        }
        if (end.compareTo(begin) <= 0) {
            messageBox("证照起日不能大于证照讫日");
            return;
        }
        table.setItem(row, "LCS_CLASS_CODE", code);
        table.setItem(row, "LCS_NO", getValueString("LCS_NO").trim());
        table.setItem(row, "EFF_LCS_DATE", begin);
        table.setItem(row, "END_LCS_DATE", end);
    }


    /**
     * 移除证照
     */
    public void onRemoveLiscenseClicked() {
        TTable table = getTable("TABLELISCENSE");
        int row = table.getSelectedRow();
        if (row == -1) {
            messageBox("请选择证照");
            return;
        }
        table.removeRow(row);
    }
    
    /**
     * 设置个人权限
     * 
     * 2017 zhanglei
     */
    public void onAuth(){
    	
    	TTable tb = getTable("TABLE");
    	int i = tb.getSelectedRow();
    	//this.messageBox("1.进入control方法");
    	if( i==-1 ){
    		this.messageBox("请从用户列表中选择一个用户!");
    		return;
    	}

    	TParm parm = tb.getDataStore().getRowParm(i);

		this.openDialog("%ROOT%\\config\\sys\\SYSUserAuth.x", parm);
    }

    /**
     * 显示使用者所属科室列表
     */
    public void showOperatorDept() {
        TTable table = getTable("TABLEDEPT");
        String sql = SYSSQL.getOperatorDeptByUserId(getValueString("USER_ID"));
        TDataStore dataStroe = new TDataStore();
        dataStroe.setSQL(sql);
        dataStroe.retrieve();
        table.setDataStore(dataStroe);
    }
    /**
     * 查询指定用户的证照信息
     * @param userId String
     */
    public void showOperatorLicense(String userId) {
        TDataStore dataStroe2 = new TDataStore();
        dataStroe2.setSQL(SYSSQL.getSYSLicenseDetail(userId));
        dataStroe2.retrieve();
        TTable table2 = getTable("TABLELISCENSE");
        table2.setDataStore(dataStroe2);
        table2.setDSValue();
    }

    /**
     * 显示使用者所属诊区列表
     */
    public void showOperatorClinicarea() {
        TTable table = getTable("OPT_CLINICAREA_TABLE");
        TDataStore dataStroe = new TDataStore();
        String sql = SYSSQL.getSYSOperatorStation("1",
                                                  this.getValueString("USER_ID"));
        dataStroe.setSQL(sql);
        dataStroe.retrieve();
        table.setDataStore(dataStroe);
    }

    /**
     * 显示使用者所属病区列表
     */
    public void showOperatorStation() {
        TTable table = getTable("OPT_STATION_TABLE");
        TDataStore dataStroe = new TDataStore();
        dataStroe.setSQL(SYSSQL.getSYSOperatorStation("2",
                this.getValueString("USER_ID")));
        dataStroe.retrieve();
        table.setDataStore(dataStroe);
    }

    /**
     * 显示所有操作人员列表
     */
    public void showOperator() {
        TDataStore dataStore = new TDataStore();
        //=======pangben modify 20110524 start
        /** modified by WangQing 20170427 start */
        String region = "";
        if(Operator.getRegion() != null && Operator.getRegion().length() != 0){
        	region = " WHERE REGION_CODE='" + Operator.getRegion() + "' ";
        }
        String sql = "SELECT * FROM SYS_OPERATOR "+region;
        /** modified by WangQing 20170427 end */
        dataStore.setSQL(sql);
//        dataStore.setSQL(SYSSQL.getSYSOperator(Operator.getRegion()));
        //=======pangben modify 20110524 stop
        dataStore.retrieve();
        TTable table = getTable("TABLE");
        table.setDataStore(dataStore);
        table.setDSValue();
    }
    /**
     * 检查数据的完整性和准确性
     * @return boolean
     */
    public boolean checkNewData() {
    	/** modified by WangQing 20170427 start */
    	if((this.getValueString("ROLE_ID").equals("T")) && (this.getValueString("T_DC").trim().length() == 0)){
    		this.messageBox("教学医生类别不能为空");
    		return false;
    	}
    	/** modified by WangQing 20170427 end */
    	
        if (getValueString("USER_NAME").equals("")) {
            this.messageBox("用户姓名不能为空");
            return false;
        }
        if (this.getValue("FOREIGNER_FLG").equals("N")) {
            String id_no = getValueString("ID_NO");
            if (!StringTool.isId(id_no)) {
                this.messageBox("身份证不正确");
                return false;
            }
        }
        if (getValueString("SEX_CODE").equals("")) {
            this.messageBox("性别不能为空");
            return false;
        }
        if (getValueString("POS_CODE").equals("")) {
            this.messageBox("职称不能为空");
            return false;
        }
        if (getValueString("USER_PASSWORD").equals("")) {
            this.messageBox("口令不能为空");
            return false;
        }
        // E_MAIL
        String e_mail = getValueString("E_MAIL");
        if (!e_mail.equals("")) {
            // 邮箱验证
            if (!StringTool.isEmail(e_mail)) {
                this.messageBox("邮箱输入不合法");
                return false;
            }
        }
//        if (getValueString("REGION_CODE").equals("")) {
//            this.messageBox("区域不能为空");
//            return false;
//        }
        if (getValueString("ROLE_ID").equals("")) {
            this.messageBox("角色不能为空");
            return false;
        }
        Timestamp begin = (Timestamp) getValue("ACTIVE_DATE");
        if (begin == null) {
            messageBox("生效日期不能为空");
            return false;
        }
        Timestamp end = (Timestamp) getValue("END_DATE");
        if (end == null) {
            messageBox("失效日期不能为空");
            return false;
        }
        if (end.compareTo(begin) <= 0) {
            this.messageBox("生效日期不能大于失效日期");
            return false;
        }
        int count = this.getValueInt("ABNORMAL_TIMES");
        if (count < 0) {
            this.messageBox("异常登陆次数不能小于0");
            return false;
        }
        
        /*TTextFormat ttEmrCode = (TTextFormat) this.getComponent("EMR_RULE_CODE");
    	if(ttEmrCode.getValue()==null || "".equals(ttEmrCode.getValue())){
    		this.messageBox("权限角色不能为空");
    		return false;
    	}
    	
    	TTextFormat ttStartDate = (TTextFormat) this.getComponent("START_DATE");
    	if(ttStartDate.getValue()==null || "".equals(ttStartDate.getValue())){
    		this.messageBox("开始时间不能为空");
    		return false;
    	}
    	
    	TTextFormat ttEndDate = (TTextFormat) this.getComponent("END_DATE_RULE");
    	if(ttEndDate.getValue()==null || "".equals(ttEndDate.getValue())){
    		this.messageBox("结束时间不能为空");
    		return false;
    	}*/
        return true;
    }
    /**
     * 得到页面中Table对象
     * @param tag String
     * @return TTable
     */
    private TTable getTable(String tag) {
        return (TTable) callFunction("UI|" + tag + "|getThis");
    }
    /**
     * 得到最大的编号 +1
     * @param dataStore TDataStore
     * @param columnName String
     * @param dbBuffer String
     * @return int
     */
    public int getMaxSeq(TDataStore dataStore, String columnName,
                         String dbBuffer) {
        if (dataStore == null)
            return 0;
        // 保存数据量
        int count = dataStore.getBuffer(dbBuffer).getCount();
        // 保存最大号
        int max = 0;
        for (int i = 0; i < count; i++) {
            int value = TCM_Transform.getInt(dataStore.getItemData(i,
                    columnName, dbBuffer));
            // 保存最大值
            if (max < value) {
                max = value;
                continue;
            }
        }
        // 最大号加1
        max++;
        return max;
    }

    /**
     * 密码文本框显示密码强度方法
     * pangben modify 20110324
     */
    public void pwdPoofSth() {
        //pwdMessageShow = true;
        //只有存在用户才能显示校验强度
        TTextField txtUserID = (TTextField)this.getComponent("USER_ID");
        if (null != txtUserID.getValue() && !"".equals(txtUserID.getValue()) &&
            !txtUserID.isEnabled()) {
            String sPW = this.getValueString("USER_PASSWORD");
            TLabel lbl = (TLabel)this.getComponent("pwd_LBL");
            if (sPW.length() <= 4) {
                this.setValue("pwd_LBL", "低");
                lbl.setColor("RED");
                return; //密码太短
            }
            String modes = "";
            for (int i = 0; i < sPW.length(); i++) {
                //测试每一个字符的类别并统计一共有多少种模式.
                modes = modes + getCharCode(sPW.charAt(i));
            }
            int result = bitTotal(modes);
            switch (result) {
            case 0:
                this.setValue("pwd_LBL", "低");
                lbl.setColor("RED");
                break;
            case 1:
                this.setValue("pwd_LBL", "中");
                lbl.setColor("绿");
                break;
            case 2:
                this.setValue("pwd_LBL", "强");
                lbl.setColor("BLUE");
                break;
            default:
                this.setValue("pwd_LBL", "低");
                lbl.setColor("RED");
                break;
            }
        }
    }

    /**
     * 验证密码强度返回显示强度的数字：0 低，1 中，2 强
     * @param modes String
     * @return int
     * pangben modify 20110324
     */
    public int bitTotal(String modes) {
        int ans = 0;
        if (modes.contains("1") && modes.contains("2") && modes.contains("3"))
            ans = 2;
        else if (modes.contains("1") && modes.contains("2"))
            ans = 1;
        else if (modes.contains("2") && modes.contains("3"))
            ans = 1;
        else if (modes.contains("1") && modes.contains("3"))
            ans = 1;
        else
            ans = 0;
        return ans;
    }

    /**
     * 通过验证每一个字符的ASCII码值获得需要的数值
     * @param iN char
     * @return String
     * pangben modify 20110324
     */
    public String getCharCode(char iN) {
        if (iN >= 48 && iN <= 57) //数字
            return "1";
        if (iN >= 65 && iN <= 90) //大写字母
            return "2";
        if (iN >= 97 && iN <= 122) //小写
            return "2";
        else
            return "3"; //特殊字符
    }
    /**
     * 校验密码强度
     * @return boolean
     */
    public boolean checkStrong(){
        String value = this.getValueString("pwd_LBL");
        if("低".equals(value)){
            this.messageBox("密码强度过低,请修改密码");
            return true;
        }
        return false;
    }
    public static void main(String[] args) {
        com.javahis.util.JavaHisDebug.initClient();
        //  pwdPoofSth();

    }
}
