package com.javahis.ui.med;

import com.dongyang.control.*;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import jdo.opb.OPB;
import java.sql.Timestamp;
import jdo.reg.PatAdmTool;
import com.dongyang.util.StringTool;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class MedChooseVisitAdmControl
    extends TControl {
    int selectrow = -1;
    OPB opb=new OPB();
    private String admType="O";
    String regionCode="";
    /**
     * 初始化界面
     */
    public void onInit() {
        super.onInit();
        //得到前台传来的数据并显示在界面上
        TParm recptype = (TParm) getParameter();
        if (!recptype.getData("count").equals("0")) {
            setValueForParm("MR_NO;PAT_NAME;SEX_CODE;AGE", recptype.getParm("PARM"),
                            -1);
            callFunction("UI|TABLE|setParmValue", recptype.getParm("RESULT"));
        }
        if (recptype.getData("count").equals("0")) {
            setValueForParm("MR_NO;PAT_NAME;SEX_CODE;AGE", recptype, -1);
        }
        //预设就诊时间段

        this.callFunction("UI|STARTTIME|setValue",recptype.getTimestamp("START_DATE"));
        this.callFunction("UI|ENDTIME|setValue",recptype.getTimestamp("END_DATE"));
        //table1的单击侦听事件
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");
        //table1的单击侦听事件
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.DOUBLE_CLICKED, this,
                     "onTableDoubleClicked");
        this.admType = recptype.getValue("ADM_TYPE");
        regionCode = recptype.getValue("REGION_CODE");
        //默认Table上显示当天挂号记录
//    onQuery();
    }

    /**
     *增加对Table的监听
     * @param row int
     */
    public void onTableClicked(int row) {
        //接收所有事件
        this.callFunction("UI|TABLE|acceptText");
//   TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
        selectrow = row;
    }

    public void onTableDoubleClicked(int row) {
        TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
        this.setReturnValue(data.getRow(selectrow));
        this.callFunction("UI|onClose");
    }

    /**
     * 查询
     */
    public void onQuery() {
        TParm parm = new TParm();
        if("O".equals(this.admType)||"E".equals(this.admType)){
            parm = PatAdmTool.getInstance().selDateByMrNoAdm(getValueString("MR_NO"),
            (Timestamp) getValue("STARTTIME"), (Timestamp) getValue("ENDTIME"),this.admType,regionCode);
        }
        if("I".equals(this.admType)){
            String sql = "SELECT CASE_NO,IN_DATE AS ADM_DATE,'' AS SESSION_CODE,DEPT_CODE,VS_DR_CODE AS DR_CODE "+
                "FROM ADM_INP "+
                "  WHERE MR_NO='"+this.getValueString("MR_NO")+"'"+
                " AND IN_DATE BETWEEN TO_DATE('"+StringTool.getString((Timestamp) getValue("STARTTIME"),"yyyyMMdd")+"','YYYYMMDD') AND TO_DATE('"+StringTool.getString((Timestamp) getValue("ENDTIME"),"yyyyMMdd")+"','YYYYMMDD')"+
                " ORDER BY CASE_NO";
            // System.out.println("SQL==="+sql);
            parm = new TParm(getDBTool().select(sql));
        }
        if (parm.getCount() < 0)
            return;
        if (parm.getCount() == 0)
            this.messageBox("无信息!");
        this.callFunction("UI|TABLE|setParmValue", parm);
    }
    /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

    /**
     *
     */
    public void onOK() {
        TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
        this.setReturnValue(data.getRow(selectrow));
        this.callFunction("UI|onClose");
    }

}
