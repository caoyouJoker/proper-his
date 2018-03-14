package com.javahis.ui.mro;

import jdo.mro.MROQlayControlMTool;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.util.StringUtil;
import jdo.sys.Pat;
import jdo.sys.PatTool;

import com.dongyang.control.TControl;
import com.javahis.system.textFormat.TextFormatSYSOperator;
import com.javahis.system.textFormat.TextFormatDept;
import com.javahis.system.textFormat.TextFormatSYSStation;
import com.dongyang.data.TParm;
import java.sql.Timestamp;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTextFormat;

/**
 * <p>Title: 自动质控查询</p>
 *
 * <p>Description: 自动质控查询</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy 2011.6.24
 * @version 1.0
 */
public class MROQlayQueryControl
    extends TControl {

    private TTable table_m;

    private TTable table_d;

    private TTable table_dd;

    public MROQlayQueryControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        table_m = getTable("TABLE_M");
        table_d = getTable("TABLE_D");
        table_dd = getTable("TABLE_DD");
        this.setValue("REGION_CODE", Operator.getRegion());
        getTextFormat("START_DATE").setEnabled(false);
        getTextFormat("END_DATE").setEnabled(false);

        Timestamp date = SystemTool.getInstance().getDate();
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/'));
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/'));
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        if ("".equals(getValueString("REGION_CODE"))) {
            this.messageBox("请选择区域！");
            return;
        }
        TParm parm = new TParm();
        parm.setData("REGION_CODE", getValueString("REGION_CODE"));
        if (!"".equals(this.getValueString("DEPT_CODE"))) {
            parm.setData("DEPT_CODE", getValueString("DEPT_CODE"));
        }
        if (!"".equals(this.getValueString("STATION_CODE"))) {
            parm.setData("STATION_CODE", getValueString("STATION_CODE"));
        }
        if (!"".equals(this.getValueString("VS_DR_CODE"))) {
            parm.setData("VS_DR_CODE", getValueString("VS_DR_CODE"));//modify by wanglong 20121127
        }
        if (!"".equals(this.getValueString("STATUS"))) {
            parm.setData("STATUS", getValueString("STATUS"));
        }
        if (!"".equals(this.getValueString("MR_NO"))) {
            parm.setData("MR_NO", getValueString("MR_NO"));
        }
        if (!"".equals(this.getValueString("IPD_NO"))) {
            parm.setData("IPD_NO", getValueString("IPD_NO"));
        }
        if (this.getRadioButton("TYPE_IN").isSelected()) {
            parm.setData("TYPE_IN", "TYPE_IN");
        }
        else {
            parm.setData("TYPE_OUT", "TYPE_OUT");
			Timestamp startDate = (Timestamp) this.getValue("START_DATE");//modify by wanglong 20121127
			Timestamp endDate = (Timestamp) this.getValue("END_DATE");
			endDate = new Timestamp(endDate.getTime() + 24 * 60 * 60 * 1000 - 1);
            parm.setData("START_DATE", startDate);//modify by wanglong 20121127
            parm.setData("END_DATE", endDate);//modify by wanglong 20121127
        }

        /**
         * 查询质控成绩
         */
        TParm result = MROQlayControlMTool.getInstance().
            onQueryQlayControlScore(parm);
        if (result == null || result.getCount("CASE_NO") <= 0) {
            this.messageBox("没有查询结果");
        }
        else {
            table_m.setParmValue(result);
        }
    }

    /**
     * 清空方法
     */
    public void onClear() {
        this.setValue("REGION_CODE", "");
        this.setValue("DEPT_CODE", "");
        this.setValue("STATION_CODE", "");
        this.setValue("USER_ID", "");
        this.setValue("STATUS", "");
        this.setValue("MR_NO", "");
        this.setValue("IPD_NO", "");
        this.setValue("PAT_NAME", "");
        this.setValue("AGE", "");
        this.setValue("REGION_CODE", Operator.getRegion());
        getTable("TABLE_M").removeRowAll();
        getTable("TABLE_D").removeRowAll();
        getTable("TABLE_DD").removeRowAll();
        onChangeRegion();
        onChangeDept();
        this.getRadioButton("TYPE_IN").setSelected(true);
        getTextFormat("START_DATE").setEnabled(false);
        getTextFormat("END_DATE").setEnabled(false);
        Timestamp date = SystemTool.getInstance().getDate();
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/'));
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/'));
    }

    /**
     *
     */
    public void onChangeType(){
        if (this.getRadioButton("TYPE_IN").isSelected()) {
            getTextFormat("START_DATE").setEnabled(false);
            getTextFormat("END_DATE").setEnabled(false);
        }
        else {
            getTextFormat("START_DATE").setEnabled(true);
            getTextFormat("END_DATE").setEnabled(true);
        }
    }

    /**
     * 表格单击事件
     */
    public void onTableClicked() {
        int row = table_m.getSelectedRow();
        TParm parm = table_m.getParmValue().getRow(row);
        /** modified by WangQing 20170413 start*/
		String sql = "SELECT A.CASE_NO, A.EXAMINE_CODE, (D.TYPE_DESC||' / '||B.EXAMINE_DESC)AS EXAMINE_DESC "
				+ "FROM MRO_QLAYCONTROLM A, MRO_CHRTVETSTD B, MRO_TYPE D "
				+ "WHERE A.CASE_NO = '" + parm.getValue("CASE_NO")+"' "
				+ "AND A.EXAMINE_CODE = B.EXAMINE_CODE "
				+ "AND B.TYPE_CODE = D.TYPE_CODE "
				+ "ORDER BY A.CHECK_DATE";
		System.out.println("===sql -----onTableClicked: "+ sql);
		/** modified by WangQing 20170413 end */
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        table_d.setParmValue(result);
    }

    /**
     * 表格单击事件
     */
    public void onTableDClicked() {
        int row = table_d.getSelectedRow();
        TParm parm = table_d.getParmValue().getRow(row);
        /** modified by WangQing 20170413 start*/
		String sql = "SELECT TO_DATE (A.EXAMINE_DATE, 'YYYYMMDDHH24MISS') "
				+ " AS EXAMINE_DATE, (D.TYPE_DESC||' / '||B.EXAMINE_DESC)AS EXAMINE_DESC, B.SCORE, C.USER_NAME, "
				+ " A.CHECK_DATE, A.EXAMINE_CODE, A.STATUS"
				+ " FROM MRO_QLAYCONTROLM A, MRO_CHRTVETSTD B, SYS_OPERATOR C ,MRO_TYPE D"
				+ " WHERE A.EXAMINE_CODE = B.EXAMINE_CODE "
				+ " AND A.CHECK_USER = C.USER_ID "
				+ " AND A.CASE_NO = '" + parm.getValue("CASE_NO")
				+ "' AND A.EXAMINE_CODE = '" + parm.getValue("EXAMINE_CODE")+"' "
				+ "AND B.TYPE_CODE = D.TYPE_CODE "
				+ "ORDER BY A.EXAMINE_DATE";
		System.out.println("===sql: "+sql);
		/** modified by WangQing 20170413 end*/
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        table_dd.setParmValue(result);
    }

    /**
     * 区域变更事件
     */
    public void onChangeRegion() {
        String region_code = this.getValueString("REGION_CODE");
        // 过滤科室
        TextFormatDept dept_code = (TextFormatDept)this.getComponent(
            "DEPT_CODE");
        dept_code.setRegionCode(region_code);
        dept_code.onQuery();
        // 过滤病区
        TextFormatSYSStation station_code = (TextFormatSYSStation)this.
            getComponent("STATION_CODE");
        station_code.setRegionCode(region_code);
        station_code.onQuery();
        // 过滤医生
        TextFormatSYSOperator user_id = (TextFormatSYSOperator)this.
            getComponent("USER_ID");
        user_id.setRegionCode(region_code);
        user_id.onQuery();
    }

    /**
     * 科室变更事件
     */
    public void onChangeDept() {
        String dept_code = this.getValueString("DEPT_CODE");
        // 过滤医生
        TextFormatSYSOperator user_id = (TextFormatSYSOperator)this.
            getComponent("USER_ID");
        user_id.setDept(dept_code);
        user_id.onQuery();
    }

    /**
     * 病案号回车事件
     */
    public void onMrNo() {
        String mr_no = this.getValueString("MR_NO");
        Pat pat = Pat.onQueryByMrNo(mr_no);
        if (pat == null) {
            this.messageBox("没有查询数据");
            this.setValue("MR_NO", "");
            this.setValue("IPD_NO", "");
            this.setValue("PAT_NAME", "");
            this.setValue("AGE", "");
            return;
        }
        
        // modify by huangtt 20160929 EMPI患者查重提示 start
        mr_no=PatTool.getInstance().checkMrno(getValueString("mr_no")); 
        this.setValue("MR_NO",mr_no);
        if (!StringUtil.isNullString(mr_no) && !mr_no.equals(pat.getMrNo())) {
			this.messageBox("病案号" + mr_no + " 已合并至 " + "" + pat.getMrNo());
			
		}	
        // modify by huangtt 20160929 EMPI患者查重提示 end 
        
        Timestamp date = SystemTool.getInstance().getDate();
        this.setValue("MR_NO", pat.getMrNo());
        this.setValue("IPD_NO", pat.getIpdNo());
        this.setValue("PAT_NAME", pat.getName());
        this.setValue("AGE", StringUtil.showAge(pat.getBirthday(), date));
    }

    /**
     * 住院号回车事件
     */
    public void onIpdNo() {
        String ipd_no = this.getValueString("IPD_NO");
        this.setValue("IPD_NO", StringTool.fill0(ipd_no, PatTool.getInstance().getIpdNoLength())); //==========chenxi 
    }

    /**
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * 得到RadioButton对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

    /**
     * 得到TextFormat对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }

}
