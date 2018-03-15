package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTable;

/**
 * <p>Title: 周班转日班控制类(新)</p>
 *
 * <p>Description: 周班转日班控制类(新)</p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: ProperSoft</p>
 *
 * @author wangl 2012.03.29
 * @version 1.0
 */
public class REGSchWeekForDayNewControl extends TControl {
    TParm data;
    int selectRow = -1;
    //诊区选中数据
    TParm endMParm;
    //诊室选中数据
    TParm endDParm;
    String clinicAreaStr = "";
    String clinicRoomStr = "";
    public void onInit() {
        super.onInit();
        //初始化REGION登陆默认登录区域
        //===pangben modify 20110410
        callFunction("UI|REGION_CODE|setValue", Operator.getRegion());
        this.setValue("ADM_DATE_START", SystemTool.getInstance().getDate());
        this.setValue("ADM_DATE_END", SystemTool.getInstance().getDate());
        // 诊区table专用的监听
        getTTable("TableM").addEventListener(TTableEvent.CHECK_BOX_CLICKED,
                                                this, "onTableMComponent");
        // 诊室table专用的监听
        getTTable("TableD").addEventListener(
                TTableEvent.CHECK_BOX_CLICKED, this, "onTableDComponent");
        //初始化table数据
        initTable();
    }

    /**
     * 新增
     */
    public void onInsert() {
        getTTable("TableD").acceptText();
        TParm parm = getParmForTag(
                "REGION_CODE;ADM_TYPE;ADM_DATE_START:Timestamp;ADM_DATE_END:Timestamp");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        StringBuffer allSeq = new StringBuffer();
        String clinicRoomAll = "";
        String clinicRoom = "";
        TParm tableParm = getTTable("TableD").getParmValue();
        for (int i = 0; i < tableParm.getCount(); i++) {
            if (tableParm.getBoolean("FLG", i)) {
                clinicRoom = tableParm.getValue("CLINICROOM_NO", i);
                if (allSeq.length() > 0)
                    allSeq.append(",");
                allSeq.append("'" + clinicRoom + "'");
            }
        }
        clinicRoomAll = allSeq.toString();
        parm.setData("CLINICROOM_NO",clinicRoomAll);
        TParm result = TIOM_AppServer.executeAction("action.reg.REGAction",
                "schWeekForDayNew", parm);

        if (result.getErrCode() != 0) {
            messageBox(result.getErrText());
            return;
        }
        //提示信息“执行成功”
        this.messageBox("P0005");

    }

    /**
     * 保存
     */
    public void onSave() {
        onInsert();
    }

    /**
     *清空
     */
    public void onClear() {
        clearValue(
                "ADM_DATE_START;ADM_DATE_END;REGION_CODE;ADM_TYPE");


    }

    /**
     * 得到TTable
     *
     * @param tag
     *            String
     * @return TTable
     */
    public TTable getTTable(String tag) {
        return (TTable)this.getComponent(tag);
    }

    /**
     * 初始化table数据
     */
    public void initTable() {
        //登录人员归属的所有诊区
        String userAreaSql =
                " SELECT USER_ID, TYPE, STATION_CLINIC_CODE, MAIN_FLG, OPT_USER," +
                "         OPT_DATE,OPT_TERM" +
                "   FROM SYS_OPERATOR_STATION " +
                "  WHERE TYPE = '1' " + //1.诊区 2.病区
                "    AND USER_ID = '" + Operator.getID() + "' " +
                "  ORDER BY USER_ID ";
        TParm userAreaParm = new TParm(TJDODBTool.getInstance().select(
                userAreaSql));
        int count = userAreaParm.getCount();

        StringBuffer allSeq = new StringBuffer();
        String clinicAreaAll = "";
        String clinicArea = "";
        for (int i = 0; i < count; i++) {
            clinicArea = userAreaParm.getValue("STATION_CLINIC_CODE", i);
            if (allSeq.length() > 0)
                allSeq.append(",");
            allSeq.append("'"+clinicArea+"'");
        }
        clinicAreaAll = allSeq.toString();
        //诊区代码,诊区说明
        String clinicAreaSql =
                " SELECT 'N' AS FLG,CLINICAREA_CODE,CLINIC_DESC " +
                "   FROM REG_CLINICAREA " +
                "  WHERE CLINICAREA_CODE IN (" + clinicAreaAll + ") ";
        TParm clinicAreaParm = new TParm(TJDODBTool.getInstance().select(
                clinicAreaSql));
        if (clinicAreaParm.getErrCode() < 0) {
            messageBox(clinicAreaParm.getErrText());
            return;
        }
        getTTable("TableM").setParmValue(clinicAreaParm);

    }
    /**
     * 诊区table监听事件
     *
     * @param obj
     *            Object
     * @return boolean
     */
    public boolean onTableMComponent(Object obj) {
        TTable TableM = (TTable) obj;
        TableM.acceptText();
        TParm tableParm = TableM.getParmValue();
        endMParm = new TParm();
        int count = tableParm.getCount("CLINICAREA_CODE");
        for (int i = 0; i < count; i++) {
            if (tableParm.getBoolean("FLG", i)) {
                endMParm.addData("CLINICAREA_CODE", tableParm.getValue("CLINICAREA_CODE",
                        i));

            }
        }
        StringBuffer allSeq = new StringBuffer();
        String clinicAreaAll = "";
        String clinicArea = "";
        for (int j = 0; j < count; j++) {
            clinicArea = endMParm.getValue("CLINICAREA_CODE", j);
            if (allSeq.length() > 0)
                allSeq.append(",");
            allSeq.append("'"+clinicArea+"'");
        }
        clinicAreaAll = allSeq.toString();
        if(clinicAreaAll.length()==0)
            return true;
        String clinicRoomSql =
                " SELECT 'Y' AS FLG, CLINICROOM_NO, CLINICROOM_DESC "+
                "   FROM REG_CLINICROOM "+
                "  WHERE CLINICAREA_CODE IN ("+clinicAreaAll+") ";
        TParm clinicRoomParm = new TParm(TJDODBTool.getInstance().select(
                clinicRoomSql));
        getTTable("TableD").removeRow();
        getTTable("TableD").setParmValue(clinicRoomParm);
        return true;
    }
    /**
     * 诊室table监听事件
     * @param obj Object
     * @return boolean
     */
    public boolean onTableDComponent(Object obj) {
        TTable TableD = (TTable) obj;
        TableD.acceptText();
        TParm tableParm = TableD.getParmValue();
        endDParm = new TParm();
        int count = tableParm.getCount("CLINICROOM_NO");
        for (int i = 0; i < count; i++) {
            if (tableParm.getBoolean("FLG", i)) {
                endDParm.addData("CLINICROOM_NO", tableParm.getValue("CLINICROOM_NO",
                        i));
            }
        }
        return true;
    }
    /**
     * 诊区table全选事件
     */
    public void onSelectAllM() {
        clinicAreaStr = new String();
        StringBuffer allArea = new StringBuffer();
        String select = getValueString("SELECTM");
        TTable table = (TTable)this.getComponent("TableM");
        table.acceptText();
        TParm parm = table.getParmValue();
        int countM = parm.getCount();
        for (int i = 0; i < countM; i++) {
            String area = "";
            parm.setData("FLG", i, select);
            area = parm.getValue("CLINICAREA_CODE", i);
               if (allArea.length() > 0)
                   allArea.append(",");
               allArea.append(area);
        }
        clinicAreaStr = allArea.toString();
        table.setParmValue(parm);

        table.acceptText();
        TParm tableParm = table.getParmValue();
        endMParm = new TParm();
        int count = tableParm.getCount("CLINICAREA_CODE");
        for (int i = 0; i < count; i++) {
            if (tableParm.getBoolean("FLG", i)) {
                endMParm.addData("CLINICAREA_CODE", tableParm.getValue("CLINICAREA_CODE",
                        i));

            }
        }
        StringBuffer allSeq = new StringBuffer();
        String clinicAreaAll = "";
        String clinicArea = "";
        for (int j = 0; j < count; j++) {
            clinicArea = endMParm.getValue("CLINICAREA_CODE", j);
            if (allSeq.length() > 0)
                allSeq.append(",");
            allSeq.append("'"+clinicArea+"'");
        }
        clinicAreaAll = allSeq.toString();
        if(clinicAreaAll.length()==0)
            return ;
        String clinicRoomSql =
                " SELECT 'Y' AS FLG, CLINICROOM_NO, CLINICROOM_DESC "+
                "   FROM REG_CLINICROOM "+
                "  WHERE CLINICAREA_CODE IN ("+clinicAreaAll+") ";
        TParm clinicRoomParm = new TParm(TJDODBTool.getInstance().select(
                clinicRoomSql));
        getTTable("TableD").removeRow();
        getTTable("TableD").setParmValue(clinicRoomParm);
   }
   public void onSelectAllD() {
       clinicRoomStr = new String();
       StringBuffer allRoom= new StringBuffer();
       String select = getValueString("SELECTD");
       TTable table = (TTable)this.getComponent("TableD");
       table.acceptText();
       TParm parm = table.getParmValue();
       int count = parm.getCount();
       for (int i = 0; i < count; i++) {
           String room = "";
           parm.setData("FLG", i, select);
           room = parm.getValue("CLINICROOM_NO", i);
              if (allRoom.length() > 0)
                  allRoom.append(",");
              allRoom.append(room);
       }
       clinicRoomStr = allRoom.toString();
       table.setParmValue(parm);
  }
}
