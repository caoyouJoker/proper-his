package action.dev;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.dev.DEVStockCheckTool;
import jdo.sys.SystemTool;
import java.text.SimpleDateFormat;
import jdo.dev.DEVTool;

public class DEV_StockCheckAction extends TAction {
    SimpleDateFormat date = new SimpleDateFormat("yyyyMMddHHmmss");
    public DEV_StockCheckAction() {
    }
  
    /**
     * 更新盘点保存
     *
     * @param parm
     * TParm
     * @return TParm
     */
    public TParm onUpdate(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        TParm condition = parm.getParm("Parm");  
        TParm inparm = new TParm();  
        for (int i = 0; i < parm.getCount(); i++) { 
            inparm.setData("DEPT_CODE", condition.getValue("DEPT_CODE", 0));
            inparm.setData("DEV_CODE", parm.getValue("DEV_CODE", i));
            inparm.setData("BATCH_SEQ", parm.getInt("BATCH_SEQ", i));    
            inparm.setData("REGION_CODE", DEVTool.getInstance().getArea());
            inparm.setData("CHECK_TYPE", condition.getValue("CHECK_TYPE", 0));
            inparm.setData("STOCK_QTY", parm.getInt("STOCK_QTY", i));
            inparm.setData("CHECK_OPT_CODE", condition.getValue("OPT_USER", 0));
            inparm.setData("ACTUAL_CHECK_QTY", 
                           parm.getInt("ACTUAL_CHECK_QTY", i));
            inparm.setData("ACTUAL_CHECKQTY_DATE",
                           condition.getValue("ACTUAL_CHECKQTY_DATE",0));
            inparm.setData("ACTUAL_CHECKQTY_USER", 
                           condition.getValue("OPT_USER", 0));
            inparm.setData("CHECK_PHASE_QTY", parm.getInt("CHECK_PHASE_QTY", i));
            inparm.setData("CHECK_PHASE_AMT", 
                           parm.getDouble("CHECK_PHASE_AMT", i));
            inparm.setData("MODI_QTY", parm.getInt("MODI_QTY", i)); 
            inparm.setData("MODI_AMT", parm.getDouble("MODI_AMT", i));
            inparm.setData("MODIQTY_OPT_CODE", condition.getValue("OPT_USER", 0));
            inparm.setData("MODI_DATE", condition.getValue("OPT_DATE",0));
            inparm.setData("OPT_USER", condition.getValue("OPT_USER", 0));
            inparm.setData("OPT_DATE", condition.getValue("OPT_DATE",0));
            inparm.setData("OPT_TERM", condition.getValue("OPT_TERM", 0));
            result = DEVStockCheckTool.getInstance().onUpdate(inparm, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                conn.close();
                return result;   
            } 
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * 新增盘点
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onInsert(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();  
        TParm condition = parm.getParm("Parm");
        //入参
        TParm inparm = new TParm();
        for (int i = 0; i < parm.getCount("DEV_CODE"); i++) {
            inparm.setData("DEPT_CODE", condition.getValue("DEPT_CODE", 0));
            inparm.setData("DEV_CODE", parm.getValue("DEV_CODE", i));
            inparm.setData("BATCH_SEQ", parm.getInt("BATCH_SEQ", i));
            inparm.setData("REGION_CODE", condition.getValue("REGION_CODE", 0));
            inparm.setData("CHECK_TYPE", condition.getValue("CHECK_TYPE", 0));
            inparm.setData("STOCK_QTY", parm.getInt("QTY", i));
            inparm.setData("CHECK_OPT_CODE", condition.getValue("OPT_USER", 0));
            inparm.setData("ACTUAL_CHECK_QTY",
                           parm.getInt("ACTUAL_CHECK_QTY", i));
            inparm.setData("ACTUAL_CHECKQTY_DATE",             
                           condition.getValue("OPT_DATE",0));
            inparm.setData("ACTUAL_CHECKQTY_USER",    
                           condition.getValue("OPT_USER", 0));
            inparm.setData("CHECK_PHASE_QTY", parm.getInt("CHECK_PHASE_QTY", i));
            inparm.setData("CHECK_PHASE_AMT",
                           parm.getDouble("CHECK_PHASE_AMT", i));
            inparm.setData("MODI_QTY", parm.getInt("MODI_QTY", i));    
            inparm.setData("MODI_AMT", parm.getDouble("MODI_AMT", i));
            inparm.setData("MODIQTY_OPT_CODE", condition.getValue("OPT_USER", 0));
            inparm.setData("MODI_DATE", condition.getValue("OPT_DATE",0));
            inparm.setData("OPT_USER", condition.getValue("OPT_USER", 0));
            inparm.setData("OPT_DATE", condition.getValue("OPT_DATE",0));
            inparm.setData("OPT_TERM", condition.getValue("OPT_TERM", 0));
            result = DEVStockCheckTool.getInstance().onInsert(inparm);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                conn.close();
                return result;
            }  
        }  
        conn.commit();
        conn.close();
        return result;
    }
    /**
     * 更新库存
     * @param parm TParm 
     * @param conn TConnection
     * @return TParm
     */
    public TParm onConfrimCheck(TParm parm) {
        TConnection conn = getConnection();
        TParm result=new TParm();
        TParm inparm=new TParm(); 
        TParm condition = parm.getParm("Parm");
        for(int i=0;i<parm.getCount();i++){
            inparm.setData("DEPT_CODE", condition.getValue("DEPT_CODE", 0));
            inparm.setData("DEV_CODE", parm.getValue("DEV_CODE", i));
            inparm.setData("BATCH_SEQ", parm.getInt("BATCH_SEQ", i));
            inparm.setData("REGION_CODE",DEVTool.getInstance().getArea());
            //库存量   
            inparm.setData("QTY", parm.getInt("QTY", i)+parm.getInt("CHECK_PHASE_QTY", i));
            inparm.setData("OPT_USER", condition.getValue("OPT_USER", 0));
            inparm.setData("OPT_DATE", condition.getValue("OPT_DATE",0));
            inparm.setData("OPT_TERM", condition.getValue("OPT_TERM", 0));
            result = DEVTool.getInstance().onUpdate(inparm, conn);
            if (result.getErrCode() < 0) {  
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                conn.close();
                return result;
            }
        }
        conn.commit();
        conn.close();  
        return result;
    }

}
