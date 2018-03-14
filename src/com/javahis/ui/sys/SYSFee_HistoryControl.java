package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.javahis.util.JavaHisDebug;
import com.dongyang.ui.TFrame;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.MessageTool;
import com.dongyang.util.TSystem;
import com.dongyang.config.TConfigParm;
import javax.swing.UIManager;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TDS;

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
public class SYSFee_HistoryControl
    extends TControl {
    public SYSFee_HistoryControl() {
    }

    String orderCode="";
    String regionCode="";//======pangben modify 20110426
    TTable mainTbl;

    /**
     * 初始化函数
     */
    public void onInit() {
        super.onInit();
        initContrl();
        TParm inParm =(TParm) this.getParameter();
        orderCode=inParm.getValue("ORDER_CODE");
        //=========pangben modify 20110426 start
        regionCode=inParm.getValue("REGION_CODE");
        searchData(orderCode,regionCode);
         //=========pangben modify 20110426 stop
    }

    public void searchData(String code,String regionCode){
        //=========pangben modify 20110426 start
        String region="";
        if(null!=regionCode&&!"".equals(regionCode))
            region = " AND (REGION_CODE='" + regionCode + "' OR REGION_CODE IS NULL OR REGION_CODE = '') ";
        TDS data=new TDS();
        String sql = " SELECT * FROM SYS_FEE_HISTORY WHERE " +
                " ORDER_CODE = '" + code + "'" +region+
                " ORDER BY END_DATE DESC";
        //=========pangben modify 20110426 stop
        data.setSQL(sql);
        data.retrieve();
        mainTbl.setDataStore(data);
        mainTbl.setDSValue();
    }

    public void initContrl(){
        mainTbl=(TTable)this.getComponent("mainTable");
    }

    public void onDClick(){
        int selRow = mainTbl.getSelectedRow();
        //得到表选中行的parm
        TParm tableData = ( (TDS) mainTbl.getDataStore()).getBuffer(TDS.
            PRIMARY).getRow(selRow);

        this.setReturnValue(tableData);
        //关闭自己
        this.closeWindow();

    }


    //测试用例
       public static void main(String[] args) {
           JavaHisDebug.initClient();
           //JavaHisDebug.TBuilder();

//        JavaHisDebug.TBuilder();
           JavaHisDebug.runFrame("sys\\SYS_FEE\\SYSFEE_HISTORY.x");
    }


}
