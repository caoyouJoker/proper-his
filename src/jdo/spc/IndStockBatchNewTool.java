package jdo.spc;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.dongyang.util.StringTool;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import java.sql.Timestamp;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 重新日结过帐</p>
 *
 * <p>Description: 重新日结过帐</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangy 2011.2.18
 * @version 1.0
 */
public class IndStockBatchNewTool 
    extends TJDOTool {
    /**
     * 实例
     */
    public static IndStockBatchNewTool instanceObject;

    /**
     * 得到实例
     *
     * @return IndReStockBatchTool
     */
    public static IndStockBatchNewTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IndStockBatchNewTool();
        return instanceObject;
    }

    /**
     * 日结过帐
     * @param org_code String
     * @param date String
     * @return boolean
     */
    public boolean onReStockBatch(String org_code, String date,String lastDate) {
//    	 System.out.println("批次执行开始32332=================");
        // 1.删除当天的IND_DDSTOCK数据
        boolean flg = deleteIndDDstock(org_code, date);
        if (!flg) {
            return false; 
        }
//        System.out.println("批次执行开始bbb=================");
        // 2.取得当前IND_STOCK数据
        TParm ind_stock = getIndStock(org_code,lastDate);
        if (ind_stock == null) {
            return false;
        }
//        System.out.println("批次执行开始aaa=================");
        // 3.创建IND_DDSTOCK数据集
        TDataStore ind_ddstock = getIndDDStock(org_code, date);
        if (ind_ddstock == null) {
            return false;
        }
        // 4.从IND_STOCK向IND_DDSTOCK赋值数据
        ind_ddstock = setDataStoreValue(ind_ddstock, ind_stock, date);
        if (ind_ddstock == null) {
            return false;
        }
//        System.out.println("批次执行开始111=================");
        // 5.取得上期库存量及金额LAST_TOTSTOCK_QTY,LAST_TOTSTOCK_AMT
        ind_ddstock = setLastTotStock(ind_ddstock, org_code, date);
        if (ind_ddstock == null) {
            return false;
        }
        // 6.取得本期盘点库存量及金额CHECKMODI_QTY,CHECKMODI_AMT
        ind_ddstock = setCheckModi(ind_ddstock, org_code, date);
        if (ind_ddstock == null) {
            return false;
        }
        // 7.取得验收入库量及金额VERIFYIN_QTY,VERIFYIN_AMT
        ind_ddstock = setVerifyin(ind_ddstock, org_code, date,lastDate);
        if (ind_ddstock == null) {
            return false;
        }
//        System.out.println("批次执行开始222=================");
        // 8.取得请领入库量及金额REQUEST_IN_QTY,REQUEST_IN_AMT
        ind_ddstock = setDispenseIn(ind_ddstock, org_code, date, "DEP");    //二级药房入
        if (ind_ddstock == null) {
            return false;
        }
        // 9.取得备药入库量及金额REQUEST_IN_QTY,REQUEST_IN_AMT
        ind_ddstock = setDispenseIn(ind_ddstock, org_code, date, "TEC");    //二级药房入
        if (ind_ddstock == null) {
            return false;
        }
        // 10.取得拨补入库量及金额GIF_IN_QTY,GIF_IN_AMT
        ind_ddstock = setDispenseIn(ind_ddstock, org_code, date, "GIF");    //二级药房入
        if (ind_ddstock == null) {
            return false;
        }
        // 11.取得退库入库量及金额RET_IN_QTY,RET_IN_AMT
        ind_ddstock = setDispenseIn(ind_ddstock, org_code, date, "RET");
        if (ind_ddstock == null) {
            return false;
        }
        // 12.取得其他入库量及金额THI_IN_QTY,THI_IN_AMT
        ind_ddstock = setDispenseIn(ind_ddstock, org_code, date, "THI");    //二级药房入
        if (ind_ddstock == null) {
            return false;
        }
        // 8.取得退货出库量及金额REGRESSGOODS_QTY,REGRESSGOODS_AMT
        ind_ddstock = setRegerssgoods(ind_ddstock, org_code, date);
        if (ind_ddstock == null) {
            return false;
        }
        // 14.取得请领出库量及金额REQUEST_OUT_QTY,REQUEST_OUT_AMT
        ind_ddstock = setDispenseOut(ind_ddstock, org_code, date, "DEP");      
        if (ind_ddstock == null) {
            return false;
        }
        // 15.取得备药出库量及金额REQUEST_OUT_QTY,REQUEST_OUT_AMT
        ind_ddstock = setDispenseOut(ind_ddstock, org_code, date, "TEC");
        if (ind_ddstock == null) {
            return false;
        }
        // 16.取得补充计费出库量及金额REQUEST_OUT_QTY,REQUEST_OUT_AMT
        ind_ddstock = setDispenseOut(ind_ddstock, org_code, date, "EXM");
        if (ind_ddstock == null) {
            return false;
        }
        // 17.取得拨补出库量及金额GIF_OUT_QTY,GIF_OUT_AMT
        ind_ddstock = setDispenseOut(ind_ddstock, org_code, date, "GIF");
        if (ind_ddstock == null) {
            return false;
        }
        // 18.取得退库出库量及金额RET_OUT_QTY,RET_OUT_AMT
        ind_ddstock = setDispenseOut(ind_ddstock, org_code, date, "RET");
        if (ind_ddstock == null) {
            return false;
        }
        // 19.取得耗损出库量及金额WAS_OUT_QTY,WAS_OUT_AMT
        ind_ddstock = setDispenseOut(ind_ddstock, org_code, date, "WAS");
        if (ind_ddstock == null) {
            return false;
        }
        // 20.取得其他出库量及金额THO_OUT_QTY,THO_OUT_AMT
        ind_ddstock = setDispenseOut(ind_ddstock, org_code, date, "THO");
        if (ind_ddstock == null) {
            return false;
        }
        // 21.取得卫耗材出库量及金额COS_OUT_QTY,COS_OUT_AMT
        ind_ddstock = setDispenseOut(ind_ddstock, org_code, date, "COS");
        if (ind_ddstock == null) {
            return false;
        }
        // 22.计算当前库存量及金额STOCK_QTY,STOCK_AMT
        ind_ddstock = setStock(ind_ddstock);
        if (ind_ddstock == null) {
            return false;
        }
        // 23.取得退药量及金额REGRESSDRUG_QTY,REGRESSDRUG_AMT
        ind_ddstock = setRegeessDrug(ind_ddstock, org_code, date);         //二级药房入
        if (ind_ddstock == null) {
            return false;
        }
//        System.out.println("批次执行开始333=================");
        // 24.取得发药量及金额DOSAGE_QTY,DOSAGE_AMT
        ind_ddstock = setDosage(ind_ddstock, org_code, date);               //二级药房出
        if (ind_ddstock == null) {
            return false;
        }
        // 25.计算调价损益金额PROFIT_LOSS_AMT
        ind_ddstock = setProfitLoss(ind_ddstock);
        if (ind_ddstock == null) {
            return false;
        }
//        System.out.println("批次执行开始444=================");
        String[] sql = ind_ddstock.getUpdateSQL();
        TParm parm = new TParm();
        for (int i = 0; i < sql.length; i++) {
         //   System.out.println(sql[i]);
        //    System.out.println("sql[" + i + "]---" + sql[i]);				
            parm = new TParm(TJDODBTool.getInstance().update(sql[i]));
            if (parm == null || parm.getErrCode() < 0) {
            	System.out.println(sql.length+"=="+i+"=="+"IND_DDSTOCK数据插入失败！！！"+sql[i]);
                return false;
            }
        }
        /*begin delete by guoyi 20120517 for 将删除库存为零的代码注释掉，解决盘点等功能连表查询少数据*/
        //加入清空库存为0数据功能
        //luhai add 2012-2-10 begin
        /*StringBuffer sqlbf = new StringBuffer();
        sqlbf.append(" DELETE  FROM IND_STOCK WHERE (LAST_TOTSTOCK_QTY+IN_QTY-OUT_QTY-CHECKMODI_QTY)<=0 ");
        parm = new TParm();
        parm = new TParm(TJDODBTool.getInstance().update(sqlbf.toString()));
        if (parm == null || parm.getErrCode() < 0) {
        	System.out.println("IND_STOCK清空库存为0数据失败！！！");
            return false;
        }*/
        //加入清空库存为0数据功能end
        /*end delete by guoyi 20120517 for 将删除库存为零的代码注释掉，解决盘点等功能连表查询少数据*/
        return true;
    }

    /**
     * 日结过帐
     * @param org_code String
     * @param date String
     * @param conn TConnection
     * @return boolean
     */
    public boolean onReStockBatch(String org_code, String date,String lastdate,
                                  TConnection conn) {
        // 1.删除当天的IND_DDSTOCK数据
        boolean flg = deleteIndDDstock(org_code, date);
        if (!flg) {
            return false;
        }
        // 2.取得当前IND_STOCK数据
        TParm ind_stock = getIndStock(org_code,lastdate);
        if (ind_stock == null) {
            return false;
        }
        // 3.创建IND_DDSTOCK数据集
        TDataStore ind_ddstock = getIndDDStock(org_code, date);
        if (ind_ddstock == null) {
            return false;
        }
        // 4.从IND_STOCK向IND_DDSTOCK赋值数据
        ind_ddstock = setDataStoreValue(ind_ddstock, ind_stock, date);
        if (ind_ddstock == null) {
            return false;
        }
        // 5.取得上期库存量及金额LAST_TOTSTOCK_QTY,LAST_TOTSTOCK_AMT
        ind_ddstock = setLastTotStock(ind_ddstock, org_code, date);
        if (ind_ddstock == null) {
            return false;
        }
        // 6.取得本期盘点库存量及金额CHECKMODI_QTY,CHECKMODI_AMT
        //del by lx 2012-06-26应该不需要以下方法，解除冻结后已经写入ind_stock
        ind_ddstock = setCheckModi(ind_ddstock, org_code, date);
        if (ind_ddstock == null) {
            return false;
        }
        
        // 7.取得验收入库量及金额VERIFYIN_QTY,VERIFYIN_AMT
        //lx未看
        ind_ddstock = setVerifyin(ind_ddstock, org_code, date,lastdate);
        if (ind_ddstock == null) {
            return false;
        }
        // 8.取得退货出库量及金额REGRESSGOODS_QTY,REGRESSGOODS_AMT
        ind_ddstock = setRegerssgoods(ind_ddstock, org_code, date);
        if (ind_ddstock == null) {
            return false;
        }
        // 9.取得请领入库量及金额REQUEST_IN_QTY,REQUEST_IN_AMT
        ind_ddstock = setDispenseIn(ind_ddstock, org_code, date, "DEP");
        if (ind_ddstock == null) {
            return false;
        }
        // 10.取得备药入库量及金额REQUEST_IN_QTY,REQUEST_IN_AMT
        ind_ddstock = setDispenseIn(ind_ddstock, org_code, date, "TEC");
        if (ind_ddstock == null) {
            return false;
        }
        // 11.取得拨补入库量及金额GIF_IN_QTY,GIF_IN_AMT
        ind_ddstock = setDispenseIn(ind_ddstock, org_code, date, "GIF");
        if (ind_ddstock == null) {
            return false;
        }
        // 12.取得退库入库量及金额RET_IN_QTY,RET_IN_AMT
        ind_ddstock = setDispenseIn(ind_ddstock, org_code, date, "RET");
        if (ind_ddstock == null) {
            return false;
        }
        // 13.取得其他入库量及金额THI_IN_QTY,THI_IN_AMT
        ind_ddstock = setDispenseIn(ind_ddstock, org_code, date, "THI");
        if (ind_ddstock == null) {
            return false;
        }
        // 14.取得请领出库量及金额REQUEST_OUT_QTY,REQUEST_OUT_AMT
        ind_ddstock = setDispenseOut(ind_ddstock, org_code, date, "DEP");
        if (ind_ddstock == null) {
            return false;
        }
        // 15.取得备药出库量及金额REQUEST_OUT_QTY,REQUEST_OUT_AMT
        ind_ddstock = setDispenseOut(ind_ddstock, org_code, date, "TEC");
        if (ind_ddstock == null) {
            return false;
        }
        // 16.取得补充计费出库量及金额REQUEST_OUT_QTY,REQUEST_OUT_AMT
        ind_ddstock = setDispenseOut(ind_ddstock, org_code, date, "EXM");
        if (ind_ddstock == null) {
            return false;
        }
        // 17.取得拨补出库量及金额GIF_OUT_QTY,GIF_OUT_AMT
        ind_ddstock = setDispenseOut(ind_ddstock, org_code, date, "GIF");
        if (ind_ddstock == null) {
            return false;
        }
        // 18.取得退库出库量及金额RET_OUT_QTY,RET_OUT_AMT
        ind_ddstock = setDispenseOut(ind_ddstock, org_code, date, "RET");
        if (ind_ddstock == null) {
            return false;
        }
        // 19.取得耗损出库量及金额WAS_OUT_QTY,WAS_OUT_AMT
        ind_ddstock = setDispenseOut(ind_ddstock, org_code, date, "WAS");
        if (ind_ddstock == null) {
            return false;
        }
        // 20.取得其他出库量及金额THO_OUT_QTY,THO_OUT_AMT
        ind_ddstock = setDispenseOut(ind_ddstock, org_code, date, "THO");
        if (ind_ddstock == null) {
            return false;
        }
        // 21.取得卫耗材出库量及金额COS_OUT_QTY,COS_OUT_AMT
        ind_ddstock = setDispenseOut(ind_ddstock, org_code, date, "COS");
        if (ind_ddstock == null) {
            return false;
        }
        // 22.计算当前库存量及金额STOCK_QTY,STOCK_AMT
        ind_ddstock = setStock(ind_ddstock);
        if (ind_ddstock == null) {
            return false;
        }
        // 23.取得发药量及金额DOSAGE_QTY,DOSAGE_AMT
        ind_ddstock = setDosage(ind_ddstock, org_code, date);
        if (ind_ddstock == null) {
            return false;
        }
        // 24.取得退药量及金额REGRESSDRUG_QTY,REGRESSDRUG_AMT
        ind_ddstock = setRegeessDrug(ind_ddstock, org_code, date);
        if (ind_ddstock == null) {
            return false;
        }
        // 25.计算调价损益金额PROFIT_LOSS_AMT
        ind_ddstock = setProfitLoss(ind_ddstock);
        if (ind_ddstock == null) {
            return false;
        }

        String[] sql = ind_ddstock.getUpdateSQL();
        TParm parm = new TParm();
        for (int i = 0; i < sql.length; i++) {
            //System.out.println("sql=================[" + i + "]---" + sql[i]);
            parm = new TParm(TJDODBTool.getInstance().update(sql[i], conn));
            if (parm == null || parm.getErrCode() < 0) {
                System.out.println("IND_DDSTOCK数据插入失败！！！");
                return false;
            }
        }
        /*begin delete by guoyi 20120517 for 将删除库存为零的代码注释掉，解决盘点等功能连表查询少数据*/
        //加入清空库存为0数据功能
        //luhai add 2012-2-10 begin
        /*StringBuffer sqlbf = new StringBuffer();
        sqlbf.append(" DELETE  FROM IND_STOCK WHERE (LAST_TOTSTOCK_QTY+IN_QTY-OUT_QTY+CHECKMODI_QTY)<=0   ");
        sqlbf.append(" AND ORG_CODE IN ( SELECT ORG_CODE FROM IND_ORG WHERE ORG_TYPE <> 'A' )  ");
        parm = new TParm();
        parm = new TParm(TJDODBTool.getInstance().update(sqlbf.toString()));
        if (parm == null || parm.getErrCode() < 0) {
        	System.out.println("IND_STOCK清空库存为0数据失败！！！");
            return false;
        }*/
        //加入清空库存为0数据功能end
        /*end delete by guoyi 20120517 for 将删除库存为零的代码注释掉，解决盘点等功能连表查询少数据*/
        //luhai add 2012-2-10 end
        return true;
    }


    /**
     * 1.删除当天的IND_DDSTOCK数据
     * @param org_code String
     * @param date String
     */
    private boolean deleteIndDDstock(String org_code, String tran_date) {
        String DELETE_IND_DDSTOCK_SQL =
            "DELETE FROM IND_DDSTOCK WHERE TRANDATE = '" + tran_date +
            "' AND ORG_CODE = '" + org_code + "'";
        //System.out.println("DELETE_IND_DDSTOCK_SQL---"+DELETE_IND_DDSTOCK_SQL);
        TParm parm = new TParm(TJDODBTool.getInstance().update(
            DELETE_IND_DDSTOCK_SQL));
        //System.out.println("parm---"+parm);
        if (parm == null || parm.getErrCode() < 0) {
            System.out.println("部门："+org_code+", IND_DDSTOCK数据删除失败！！！");
            return false;
        }
        return true;  
    }

    /**
     * 1.删除当天的IND_DDSTOCK数据
     * @param org_code String
     * @param tran_date String
     * @param conn TConnection
     * @return boolean
     */
    private boolean deleteIndDDstock(String org_code, String tran_date,
                                     TConnection conn) {
        String DELETE_IND_DDSTOCK_SQL =
            "DELETE FROM IND_DDSTOCK WHERE TRANDATE = '" + tran_date +
            "' AND ORG_CODE = '" + org_code + "'";
//        System.out.println("DELETE_IND_DDSTOCK_SQL---"+DELETE_IND_DDSTOCK_SQL);
        TParm parm = new TParm(TJDODBTool.getInstance().update(
            DELETE_IND_DDSTOCK_SQL, conn));
//        System.out.println("parm---"+parm);
        if (parm == null || parm.getErrCode() < 0) {
            System.out.println("部门："+org_code+", IND_DDSTOCK数据删除失败！！！");
            return false;
        }
        return true;
    }


    /**
     * 2.取得当前IND_STOCK数据
     * @param org_code String
     */
    private TParm getIndStock(String org_code,String lastdate) {
        /*String SELECT_IND_STOCK_SQL =
            "SELECT * FROM IND_STOCK WHERE ORG_CODE = '" + org_code +
            "' ORDER BY ORDER_CODE, VALID_DATE, BATCH_SEQ";// lx 2012-06-26 可能只有BATCH_SEQ
*///        System.out.println("SELECT_IND_STOCK_SQL---"+SELECT_IND_STOCK_SQL);
    	String SELECT_IND_STOCK_SQL = "SELECT * FROM IND_DDSTOCK WHERE ORG_CODE='"+org_code+"'"
    	+" AND TRANDATE='"+lastdate+"' ORDER BY ORDER_CODE, VALID_DATE, BATCH_SEQ";
        TParm parm = new TParm(TJDODBTool.getInstance().select(
            SELECT_IND_STOCK_SQL));
        if (parm == null || parm.getErrCode() < 0 ||
            parm.getCount("ORDER_CODE") <= 0) {
            System.out.println("部门："+org_code+", IND_DDSTOCK数据取得失败！！！");
            return null;
        }     
        return parm;
    }

    /**
     * 3.创建IND_DDSTOCK数据集
     * @param org_code String
     * @param tran_date String
     * @return TDataStore
     */
    private TDataStore getIndDDStock(String org_code, String tran_date) {
        String SELECT_IND_DDSTOCK_SQL =
            "SELECT * FROM IND_DDSTOCK WHERE ORG_CODE = '" + org_code +
            "' AND TRANDATE = '" + tran_date +
            "' ORDER BY ORDER_CODE, BATCH_SEQ";
        //System.out.println("SELECT_IND_DDSTOCK_SQL---"+SELECT_IND_DDSTOCK_SQL);
        TDataStore dateStore = new TDataStore();
        dateStore.setSQL(SELECT_IND_DDSTOCK_SQL);
        dateStore.retrieve();
        if (dateStore.rowCount() > 0) {
            System.out.println("部门："+org_code+", IND_DDSTOCK数据未完全删除！！！");
            return null;
        }
        return dateStore;
    }

    /**
     * 4.从IND_STOCK向IND_DDSTOCK赋值数据
     * @param dataStore TDataStore
     * @param parm TParm
     * @param trandate String
     * @return TDataStore
     */
    private TDataStore setDataStoreValue(TDataStore dataStore, TParm parm,
                                         String trandate) {
        String order_code = "";
        Timestamp date = StringTool.getTimestamp(new Date());
        //del by lx 2012-06-26 这段可能没用
        for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
            order_code = parm.getValue("ORDER_CODE", i);
            String sql = "SELECT A.TRADE_PRICE, B.OWN_PRICE "
                + " FROM PHA_BASE A, SYS_FEE_HISTORY B "
                + " WHERE A.ORDER_CODE = B.ORDER_CODE "
                + " AND TO_DATE ('" + trandate +
                "235959', 'YYYYMMDDHH24MISS') "
                + " BETWEEN TO_DATE (B.START_DATE, 'YYYYMMDDHH24MISS') "
                + " AND TO_DATE (B.END_DATE, 'YYYYMMDDHH24MISS') "
                + " AND A.ORDER_CODE = ";
            String order_by = "  ORDER BY B.END_DATE DESC ";
            sql = sql + "'" + order_code + "'" + order_by;

            TParm pha_base = new TParm(TJDODBTool.getInstance().select(sql)); 
            /* begin update by guoyi 20120523 for 采用药品零差价后不判断PHA_BASE中的TRADE_PRICE*/
//            if (pha_base == null || pha_base.getCount("TRADE_PRICE") <= 0) {
//            	System.out.println("过账期间禁用的药品sql----" + sql);
//            	continue;
//            }
            if (pha_base == null || pha_base.getErrCode() < 0 ) {
            	System.out.println("过账期间禁用的药品sql----========================" + sql);
            	continue;
            }
            /* end update by guoyi 20120523 for 采用药品零差价后不判断PHA_BASE中的TRADE_PRICE*/

            int row = dataStore.insertRow();
            // 1-5
            dataStore.setItem(row, "TRANDATE", trandate);
            dataStore.setItem(row, "ORG_CODE", parm.getValue("ORG_CODE", i));
            dataStore.setItem(row, "ORDER_CODE", order_code);
            dataStore.setItem(row, "BATCH_SEQ", parm.getInt("BATCH_SEQ", i));
            dataStore.setItem(row, "BATCH_NO", parm.getValue("BATCH_NO", i));
            // 6-10
            dataStore.setItem(row, "VALID_DATE",
                              parm.getTimestamp("VALID_DATE", i));
            dataStore.setItem(row, "REGION_CODE",
                              parm.getValue("REGION_CODE", i));
            dataStore.setItem(row, "STOCK_QTY", 0);
            dataStore.setItem(row, "STOCK_AMT", 0);
            dataStore.setItem(row, "LAST_TOTSTOCK_QTY", 0);
            // 11-15
            dataStore.setItem(row, "LAST_TOTSTOCK_AMT", 0);
            dataStore.setItem(row, "IN_QTY", 0);
            dataStore.setItem(row, "IN_AMT", 0);
            dataStore.setItem(row, "OUT_QTY", 0);
            dataStore.setItem(row, "OUT_AMT", 0);
            // 16-20
            dataStore.setItem(row, "CHECKMODI_QTY", 0);
            dataStore.setItem(row, "CHECKMODI_AMT", 0);
            dataStore.setItem(row, "VERIFYIN_QTY", 0);
            dataStore.setItem(row, "VERIFYIN_AMT", 0);
            dataStore.setItem(row, "FAVOR_QTY", 0);
            // 21-25
            dataStore.setItem(row, "REGRESSGOODS_QTY", 0);
            dataStore.setItem(row, "REGRESSGOODS_AMT", 0);
            dataStore.setItem(row, "DOSAGE_QTY", 0);
            dataStore.setItem(row, "DOSAGE_AMT", 0);
            dataStore.setItem(row, "REGRESSDRUG_QTY", 0);
            // 26-30
            dataStore.setItem(row, "REGRESSDRUG_AMT", 0);
            dataStore.setItem(row, "PROFIT_LOSS_AMT", 0);
            dataStore.setItem(row, "VERIFYIN_PRICE",
                              parm.getDouble("VERIFYIN_PRICE", i));
            dataStore.setItem(row, "STOCK_PRICE",
                              parm.getDouble("VERIFYIN_PRICE", i));
            dataStore.setItem(row, "RETAIL_PRICE",
                              parm.getDouble("RETAIL_PRICE", i));//Modified by lx 2012-06-26 INDSTock->RETAIL_PRICE ?????
            // 31-35
            dataStore.setItem(row, "TRADE_PRICE",			
                              parm.getDouble("TRADE_PRICE", i));//Modified by lx 2012-06-26 INDSTock->VERIFYIN_PRICE ????
            dataStore.setItem(row, "STOCKIN_QTY", 0);		
            dataStore.setItem(row, "STOCKIN_AMT", 0);
            dataStore.setItem(row, "STOCKOUT_QTY", 0);     
            dataStore.setItem(row, "STOCKOUT_AMT", 0);
            // 36-40
            dataStore.setItem(row, "OPT_USER", "IND_RE");
            dataStore.setItem(row, "OPT_DATE", date);
            dataStore.setItem(row, "OPT_TERM", "127.0.0.1");
            dataStore.setItem(row, "REQUEST_IN_QTY", 0);
            dataStore.setItem(row, "REQUEST_IN_AMT", 0);
            // 41-45
            dataStore.setItem(row, "REQUEST_OUT_QTY", 0);
            dataStore.setItem(row, "REQUEST_OUT_AMT", 0);
            dataStore.setItem(row, "GIF_IN_QTY", 0);
            dataStore.setItem(row, "GIF_IN_AMT", 0);
            dataStore.setItem(row, "GIF_OUT_QTY", 0);
            // 46-50
            dataStore.setItem(row, "GIF_OUT_AMT", 0);
            dataStore.setItem(row, "RET_IN_QTY", 0);
            dataStore.setItem(row, "RET_IN_AMT", 0);
            dataStore.setItem(row, "RET_OUT_QTY", 0);
            dataStore.setItem(row, "RET_OUT_AMT", 0);
            // 51-55
            dataStore.setItem(row, "WAS_OUT_QTY", 0);
            dataStore.setItem(row, "WAS_OUT_AMT", 0);
            dataStore.setItem(row, "THO_OUT_QTY", 0);
            dataStore.setItem(row, "THO_OUT_AMT", 0);
            dataStore.setItem(row, "THI_IN_QTY", 0);
            // 56-58
            dataStore.setItem(row, "THI_IN_AMT", 0);
            dataStore.setItem(row, "COS_OUT_QTY", 0);
            dataStore.setItem(row, "COS_OUT_AMT", 0);
        }
        return dataStore;
    }

    /**
     * 5.取得上期库存量及金额LAST_TOTSTOCK_QTY,LAST_TOTSTOCK_AMT
     * @param dataStore TDataStore
     * @param org_code String
     * @param date String
     */
    private TDataStore setLastTotStock(TDataStore dataStore, String org_code,
                                       String date) {
        Timestamp datetime = StringTool.getTimestamp(date, "yyyyMMdd");
        Timestamp lastdate = StringTool.rollDate(datetime, -1); //modified by lx 2012-06-26 如果前一天未过账，应该取最近一次过账的时间;
        String last_date = StringTool.getString(lastdate, "yyyyMMdd");
        String SELECT_IND_DDSTOCK_SQL =
            "SELECT ORDER_CODE, BATCH_SEQ, STOCK_QTY, STOCK_AMT " +
            "FROM IND_DDSTOCK WHERE ORG_CODE = '" +
            org_code + "' AND TRANDATE = '" + last_date +
            "' ORDER BY ORDER_CODE, BATCH_SEQ";
        //System.out.println("SELECT_IND_DDSTOCK_SQL---"+SELECT_IND_DDSTOCK_SQL);
        TParm parm = new TParm(TJDODBTool.getInstance().select(
            SELECT_IND_DDSTOCK_SQL));
        String order_code = "";
        int batch_seq = 0;
        for (int i = 0; i < dataStore.rowCount(); i++) {
            order_code = dataStore.getItemString(i, "ORDER_CODE");
            batch_seq = dataStore.getItemInt(i, "BATCH_SEQ");
            for (int j = 0; j < parm.getCount("ORDER_CODE"); j++) {
                if (order_code.equals(parm.getValue("ORDER_CODE", j)) &&
                    batch_seq == parm.getInt("BATCH_SEQ", j)) {
                    dataStore.setItem(i, "LAST_TOTSTOCK_QTY",
                                      parm.getDouble("STOCK_QTY", j));
                    dataStore.setItem(i, "LAST_TOTSTOCK_AMT",
                                      parm.getDouble("STOCK_AMT", j));
                    continue;
                }
            }
        }
        return dataStore;
    }

    /**
     * 6.取得本期盘点库存量及金额CHECKMODI_QTY,CHECKMODI_AMT
     * @param dataStore TDataStore
     * @param org_code String
     * @param date String
     * @return TDataStore
     */
    private TDataStore setCheckModi(TDataStore dataStore, String org_code,
                                    String date) {
        String start_date = date + "000000";
        String end_date = date + "235959";
        //
        String SELECT_IND_QTYCHECK_SQL =
            "SELECT ORDER_CODE, BATCH_SEQ, RETAIL_PRICE * MODI_QTY AS MODI_AMT, " +
            "MODI_QTY FROM IND_QTYCHECK WHERE ORG_CODE = '" +
            org_code +
            "' AND TO_DATE(UNFREEZE_DATE, 'YYYYMMDDHH24MISS') BETWEEN " +
            "TO_DATE('" + start_date + "', 'YYYYMMDDHH24MISS') AND TO_DATE('" +
            end_date + "', 'YYYYMMDDHH24MISS')" +
            " ORDER BY ORDER_CODE, BATCH_SEQ";
        //System.out.println("SELECT_IND_QTYCHECK_SQL---"+SELECT_IND_QTYCHECK_SQL);
        TParm parm = new TParm(TJDODBTool.getInstance().select(
            SELECT_IND_QTYCHECK_SQL));
        String order_code = "";
        int batch_seq = 0;
        for (int i = 0; i < dataStore.rowCount(); i++) {
            order_code = dataStore.getItemString(i, "ORDER_CODE");
            batch_seq = dataStore.getItemInt(i, "BATCH_SEQ");
            for (int j = 0; j < parm.getCount("ORDER_CODE"); j++) {
                if (order_code.equals(parm.getValue("ORDER_CODE", j)) &&
                    batch_seq == parm.getInt("BATCH_SEQ", j)) {
                    dataStore.setItem(i, "CHECKMODI_QTY",
                                      parm.getDouble("MODI_QTY", j));
                    dataStore.setItem(i, "CHECKMODI_AMT",
                                      parm.getDouble("MODI_AMT", j));
                    	
                    continue;
                }
            }
        }
        return dataStore;
    }

    /**
     * 7.取得验收入库量及金额VERIFYIN_QTY,VERIFYIN_AMT
     * @param dataStore TDataStore
     * @param org_code String
     * @param date String
     * @return TDataStore
     */
    private TDataStore setVerifyin(TDataStore dataStore, String org_code,
                                   String date,String lastdate) {
    	Timestamp opt_date = StringTool.getTimestamp(new Date());
        String start_date = date + "000000";
        String end_date = date + "235959";
        String SELECT_IND_VERIFYIN_SQL =
            "SELECT A.ORDER_CODE, A.BATCH_NO, A.VALID_DATE,B.SUP_CODE, " +
            " A.VERIFYIN_QTY * C.DOSAGE_QTY * C.STOCK_QTY  AS QTY, " +
            " A.VERIFYIN_QTY * A.RETAIL_PRICE AS AMT, " +
            "  CASE WHEN A.BILL_UNIT=C.STOCK_UNIT THEN ROUND(A.VERIFYIN_PRICE/C.DOSAGE_QTY/C.STOCK_QTY,2) ELSE  A.VERIFYIN_PRICE END AS VERIFYIN_PRICE, "+
            "  CASE WHEN A.BILL_UNIT=C.STOCK_UNIT THEN ROUND(A.RETAIL_PRICE/C.DOSAGE_QTY/C.STOCK_QTY,2) ELSE A.RETAIL_PRICE  END AS RETAIL_PRICE  " +
            " FROM IND_VERIFYIND A, IND_VERIFYINM B, PHA_TRANSUNIT C " +
            " WHERE A.VERIFYIN_NO = B.VERIFYIN_NO " +
            " AND A.ORDER_CODE = C.ORDER_CODE " +			
            " AND B.ORG_CODE = '" + org_code + "' " +					  
            " AND B.CHECK_DATE BETWEEN TO_DATE ('" + start_date +
            "', 'YYYYMMDDHH24MISS') AND TO_DATE ('" + end_date +
            "', 'YYYYMMDDHH24MISS') ORDER BY A.ORDER_CODE ";
        TParm parm = new TParm(TJDODBTool.getInstance().select(
            SELECT_IND_VERIFYIN_SQL));
     	Map<String, Integer> batchSeqMap = new HashMap<String, Integer>();
        String order_code = "";
        String batch_no = "";
        String valid_date = "";
        int batchSEQ=0;
        for (int i = 0; i < parm.getCount("ORDER_CODE");i++) {
        	for (int j = 0; j < dataStore.rowCount(); j++) {
        		order_code = dataStore.getItemString(j, "ORDER_CODE");
                batch_no = dataStore.getItemString(j, "BATCH_NO");
                valid_date = StringTool.getString(dataStore.getItemTimestamp(j,
                    "VALID_DATE"), "yyyyMMdd");
                if (order_code.equals(parm.getValue("ORDER_CODE", i)) &&
                        batch_no.equals(parm.getValue("BATCH_NO", i)) &&
                        valid_date.equals(StringTool.getString(parm.
                        getTimestamp("VALID_DATE", i), "yyyyMMdd"))) {
                		/*dataStore.setItem(j, "VERIFYIN_QTY",
                             parm.getDouble("QTY", i) +   
                             dataStore.getItemDouble(i, "VERIFYIN_QTY"));
			           dataStore.setItem(j, "VERIFYIN_AMT",
			                             parm.getDouble("AMT", i) +
			                             dataStore.getItemDouble(j, "VERIFYIN_AMT"));
			           dataStore.setItem(j, "IN_QTY",
			                             parm.getDouble("QTY", i) +
			                             dataStore.getItemDouble(j, "IN_QTY"));
			           dataStore.setItem(j, "IN_AMT",
			                             parm.getDouble("AMT", i) +
			                             dataStore.getItemDouble(j, "IN_AMT"));
			           dataStore.setItem(j, "STOCKIN_QTY",
			                             parm.getDouble("QTY", i) +
			                             dataStore.getItemDouble(j, "STOCKIN_QTY"));
			           dataStore.setItem(j, "STOCKIN_AMT",
			                             parm.getDouble("AMT", i) +
			                             dataStore.getItemDouble(j, "STOCKIN_AMT"));*/
                	   dataStore.setItem(j, "VERIFYIN_QTY",
                            parm.getDouble("QTY", i) );
			           dataStore.setItem(j, "VERIFYIN_AMT",
			                             parm.getDouble("AMT", i) );
			           dataStore.setItem(j, "IN_QTY",
			                             parm.getDouble("QTY", i) );
			           dataStore.setItem(j, "IN_AMT",
			                             parm.getDouble("AMT", i) );
			           dataStore.setItem(j, "STOCKIN_QTY",
			                             parm.getDouble("QTY", i) );
			           dataStore.setItem(j, "STOCKIN_AMT",
			                             parm.getDouble("AMT", i) );
			           break;					 								
                } else {    
                if(j==(dataStore.rowCount()-1)) {
                	 String SELECT_BATCH_SEQ = "SELECT MAX(BATCH_SEQ) AS BATCH_SEQ FROM IND_DDSTOCK WHERE TRANDATE='"+lastdate+"'" +
             		" AND ORG_CODE='"+org_code+"' AND ORDER_CODE='"+parm.getValue("ORDER_CODE", i)+"'";
                	 TParm result = new TParm(TJDODBTool.getInstance().select(
            		 SELECT_BATCH_SEQ));	
                		//找到当前的最大批次号加1 ，没有找到默认为1
						if(result.getCount("BATCH_SEQ") > 0){
							// 最大+1批次序号
							batchSEQ = result.getInt("BATCH_SEQ", 0) + 1;
						}else{
							batchSEQ = 1 ;				
						}			   				
						//同一品种药品重复入库，BATCH_SEQ生成问题解决
					Set<String> keySet = batchSeqMap.keySet();
					if (keySet.contains(parm.getValue("ORDER_CODE", i))) {
						Integer batchSeqValue = batchSeqMap.get(parm.getValue("ORDER_CODE", i));
						batchSeqValue += 1;
						batchSeqMap.put(parm.getValue("ORDER_CODE", i), batchSeqValue);
						batchSEQ = batchSeqValue ;	
					}else{
						batchSeqMap.put(parm.getValue("ORDER_CODE", i), batchSEQ);
					}
     				int row = dataStore.insertRow();			
                	//如果不存在新添加批次  modify by fuwj			
                	dataStore.setItem(row, "TRANDATE",
                			date);							
                	dataStore.setItem(row, "ORG_CODE",
        			org_code);				
        			dataStore.setItem(row, "ORDER_CODE",
        					parm.getValue("ORDER_CODE", i));
        			dataStore.setItem(row, "BATCH_NO",				
        					parm.getValue("BATCH_NO", i));
 System.out.println(parm.getValue("ORDER_CODE", i)+"=="+parm.getValue("BATCH_NO", i));
        			dataStore.setItem(row, "VALID_DATE",
        					StringTool.getString(parm.			
        	                        getTimestamp("VALID_DATE", i), "yyyyMMdd"));
                	dataStore.setItem(row, "BATCH_SEQ",
                			batchSEQ);
                	
                	dataStore.setItem(row, "VERIFYIN_PRICE",
                            parm.getDouble("VERIFYIN_PRICE", i));
                	dataStore.setItem(row, "RETAIL_PRICE",
                            parm.getDouble("RETAIL_PRICE", i));
                	dataStore.setItem(row, "SUP_CODE",	
                            parm.getValue("SUP_CODE", i));
                			
                	dataStore.setItem(row, "VERIFYIN_QTY",
                            parm.getDouble("QTY", i));
                	dataStore.setItem(row, "VERIFYIN_AMT",				
                            parm.getDouble("AMT", i) );
                	dataStore.setItem(row, "IN_QTY", 
                            parm.getDouble("QTY", i));
                	dataStore.setItem(row, "IN_AMT",
                            parm.getDouble("AMT", i) );
                	dataStore.setItem(row, "STOCKIN_QTY",
                            parm.getDouble("QTY", i));
                	dataStore.setItem(row, "STOCKIN_AMT",
                            parm.getDouble("AMT", i) );		
                	dataStore.setItem(row, "REGION_CODE",
                            "H01" );	
                    dataStore.setItem(row, "OPT_USER", "IND_RE");
                    dataStore.setItem(row, "OPT_DATE", date);			
                    dataStore.setItem(row, "OPT_TERM", "127.0.0.1");
                	j++;
                	continue;
                }
                }
        	}
        }
        return dataStore;
    }

    /**
     * 8.取得退货出库量及金额REGRESSGOODS_QTY,REGRESSGOODS_AMT
     * @param dataStore TDataStore
     * @param org_code String
     * @param date String
     * @return TDataStore
     */
    private TDataStore setRegerssgoods(TDataStore dataStore, String org_code,
                                       String date) {
        String start_date = date + "000000";
        String end_date = date + "235959";
        String SELECT_IND_REGRESSGOODS_SQL =
            "SELECT   A.ORDER_CODE, A.BATCH_NO, A.VALID_DATE, " +
            " A.QTY * C.DOSAGE_QTY * C.STOCK_QTY AS QTY, " +
            " A.QTY * A.RETAIL_PRICE AS AMT " +
            " FROM IND_REGRESSGOODSD A, IND_REGRESSGOODSM B, PHA_TRANSUNIT C " +
            " WHERE A.REGRESSGOODS_NO = B.REGRESSGOODS_NO " +
            " AND A.ORDER_CODE = C.ORDER_CODE " +
            " AND B.ORG_CODE = '" + org_code + "' " +
            " AND B.CHECK_DATE BETWEEN TO_DATE ('" + start_date +
            "', 'YYYYMMDDHH24MISS') " +
            " AND TO_DATE ('" + end_date + "', 'YYYYMMDDHH24MISS') " +
            " ORDER BY A.ORDER_CODE ";
        //System.out.println("SELECT_IND_REGRESSGOODS_SQL---"+SELECT_IND_REGRESSGOODS_SQL);
        TParm parm = new TParm(TJDODBTool.getInstance().select(
            SELECT_IND_REGRESSGOODS_SQL));
        String order_code = "";
        String batch_no = "";
        String valid_date = "";
        for (int i = 0; i < dataStore.rowCount(); i++) {
            order_code = dataStore.getItemString(i, "ORDER_CODE");
            batch_no = dataStore.getItemString(i, "BATCH_NO");
            valid_date = StringTool.getString(dataStore.getItemTimestamp(i,
                "VALID_DATE"), "yyyyMMdd");
            for (int j = 0; j < parm.getCount("ORDER_CODE"); j++) {
                if (order_code.equals(parm.getValue("ORDER_CODE", j)) &&
                    batch_no.equals(parm.getValue("BATCH_NO", j)) &&
                    valid_date.equals(StringTool.getString(parm.
                    getTimestamp("VALID_DATE", j), "yyyyMMdd"))) {
                    dataStore.setItem(i, "REGRESSGOODS_QTY",
                                      parm.getDouble("QTY", j) +
                                      dataStore.getItemDouble(i,
                        "REGRESSGOODS_QTY"));
                    dataStore.setItem(i, "REGRESSGOODS_AMT",
                                      parm.getDouble("AMT", j) +
                                      dataStore.getItemDouble(i,
                        "REGRESSGOODS_AMT"));
                    dataStore.setItem(i, "OUT_QTY",
                                      parm.getDouble("QTY", j) +
                                      dataStore.getItemDouble(i, "OUT_QTY"));
                    dataStore.setItem(i, "OUT_AMT",
                                      parm.getDouble("AMT", j) +
                                      dataStore.getItemDouble(i, "OUT_AMT"));
                    dataStore.setItem(i, "STOCKOUT_QTY",
                                      parm.getDouble("QTY", j) +
                                      dataStore.getItemDouble(i, "STOCKOUT_QTY"));
                    dataStore.setItem(i, "STOCKOUT_AMT",
                                      parm.getDouble("AMT", j) +
                                      dataStore.getItemDouble(i, "STOCKOUT_AMT"));
                    continue;
                }
            }
        }
        return dataStore;
    }

    /**
     * 8-12.取得入库量及金额IN_QTY,IN_AMT
     * @param dataStore TDataStore
     * @param org_code String
     * @param date String
     * @param request_type String
     * @return TDataStore
     */
    private TDataStore setDispenseIn(TDataStore dataStore, String org_code,
                                     String date, String request_type) {
        String start_date = date + "000000";
        String end_date = date + "235959";
        String org_where = "";
        String qty_where = "";
        if ("DEP".equals(request_type) || "THI".equals(request_type)) {
            org_where = " AND B.APP_ORG_CODE = '" + org_code + "' ";
            qty_where = " A.QTY * C.DOSAGE_QTY * C.STOCK_QTY AS QTY, ";
        }
        else if ("GIF".equals(request_type) || "RET".equals(request_type)) {
            org_where = " AND B.TO_ORG_CODE = '" + org_code + "' ";
            qty_where = " A.QTY * C.DOSAGE_QTY * C.STOCK_QTY AS QTY, ";
        }
        else if ("TEC".equals(request_type)) {
            org_where = " AND B.APP_ORG_CODE = '" + org_code + "' ";
            qty_where = " A.QTY , ";
        }
        else {
            org_where = " AND B.TO_ORG_CODE = '" + org_code + "' ";
            qty_where = " A.QTY , ";
        }
        String SELECT_IND_DISPENSE_IN_SQL =
            "SELECT   A.ORDER_CODE, A.BATCH_NO, A.VALID_DATE, " + qty_where +
       //     " A.QTY * A.RETAIL_PRICE AS AMT " +					
            " CASE WHEN A.UNIT_CODE = C.STOCK_UNIT  THEN A.RETAIL_PRICE * C.DOSAGE_QTY * C.STOCK_QTY*A.QTY ELSE A.RETAIL_PRICE*A.QTY  END AS AMT " +
            " FROM IND_DISPENSED A, IND_DISPENSEM B, PHA_TRANSUNIT C " +
            " WHERE A.DISPENSE_NO = B.DISPENSE_NO " +
            " AND A.ORDER_CODE = C.ORDER_CODE " + org_where +
            " AND B.WAREHOUSING_DATE BETWEEN TO_DATE ('" + start_date +
            "', 'YYYYMMDDHH24MISS') " +
            " AND TO_DATE ('" + end_date + "', 'YYYYMMDDHH24MISS') " +
            " AND B.REQTYPE_CODE = '" + request_type + "'" +
            " AND B.UPDATE_FLG <> '2' " +
            " ORDER BY A.ORDER_CODE, A.BATCH_SEQ ";   
        //System.out.println("SELECT_IND_DISPENSE_IN_SQL---"+SELECT_IND_DISPENSE_IN_SQL);
        TParm parm = new TParm(TJDODBTool.getInstance().select(
            SELECT_IND_DISPENSE_IN_SQL));
        String order_code = "";
        String batch_no = "";
        String valid_date = "";
        String type = "";
        if ("DEP".equals(request_type) || "TEC".equals(request_type)) {
            type = "REQUEST_IN_";
        }
        else if ("GIF".equals(request_type)) {
            type = "GIF_IN_";
        }
        else if ("RET".equals(request_type)) {
            type = "RET_IN_";
        }
        else {
            type = "THI_IN_";
        }
        for (int i = 0; i < dataStore.rowCount(); i++) {
            order_code = dataStore.getItemString(i, "ORDER_CODE");
            batch_no = dataStore.getItemString(i, "BATCH_NO");
            valid_date = StringTool.getString(dataStore.getItemTimestamp(i,
                "VALID_DATE"), "yyyyMMdd");
            for (int j = 0; j < parm.getCount("ORDER_CODE"); j++) {
                if (order_code.equals(parm.getValue("ORDER_CODE", j)) &&
                    batch_no.equals(parm.getValue("BATCH_NO", j)) &&
                    valid_date.equals(StringTool.getString(parm.
                    getTimestamp("VALID_DATE", j), "yyyyMMdd"))) {
                	
                    dataStore.setItem(i, type + "QTY",
                                      parm.getDouble("QTY", j) +
                                      dataStore.getItemDouble(i, type + "QTY"));
                    dataStore.setItem(i, type + "AMT",
                                      parm.getDouble("AMT", j) +
                                      dataStore.getItemDouble(i, type + "AMT"));
                    dataStore.setItem(i, "IN_QTY",
                                      parm.getDouble("QTY", j) +
                                      dataStore.getItemDouble(i, "IN_QTY"));
                    dataStore.setItem(i, "IN_AMT",
                                      parm.getDouble("AMT", j) +
                                      dataStore.getItemDouble(i, "IN_AMT"));
                    dataStore.setItem(i, "STOCKIN_QTY",
                                      parm.getDouble("QTY", j) +
                                      dataStore.getItemDouble(i, "STOCKIN_QTY"));
                    dataStore.setItem(i, "STOCKIN_AMT",
                                      parm.getDouble("AMT", j) +
                                      dataStore.getItemDouble(i, "STOCKIN_AMT"));																					
                    continue;
                }  
            }
        }
        return dataStore;
    }

    /**
     * 14-21.取得出库量及金额OUT_QTY,OUT_AMT
     * @param dataStore TDataStore
     * @param org_code String
     * @param date String
     * @param request_type String
     * @return TDataStore
     */
    private TDataStore setDispenseOut(TDataStore dataStore, String org_code,
                                      String date, String request_type) {
        String start_date = date + "000000";
        String end_date = date + "235959";
        String org_where = "";
        String qty_where = "";
        if ("DEP".equals(request_type)) {
            org_where = " AND B.TO_ORG_CODE = '" + org_code + "' ";
            qty_where = " A.QTY * C.DOSAGE_QTY * C.STOCK_QTY AS QTY, ";
        }
        else if ("GIF".equals(request_type) || "RET".equals(request_type) ||
                 "WAS".equals(request_type) || "THO".equals(request_type)) {
            org_where = " AND B.APP_ORG_CODE = '" + org_code + "' ";
            qty_where = " A.QTY * C.DOSAGE_QTY * C.STOCK_QTY AS QTY, ";
        }
        else if ("TEC".equals(request_type) || "EXM".equals(request_type) ||
                 "COS".equals(request_type)) {
            org_where = " AND B.TO_ORG_CODE = '" + org_code + "' ";
            qty_where = " A.QTY , ";
        }

        String SELECT_IND_DISPENSE_OUT_SQL =
            "SELECT   A.ORDER_CODE, A.BATCH_NO, A.VALID_DATE, " + qty_where +
            " CASE WHEN A.UNIT_CODE = C.STOCK_UNIT  THEN A.RETAIL_PRICE * C.DOSAGE_QTY * C.STOCK_QTY*A.QTY ELSE A.RETAIL_PRICE*A.QTY  END AS AMT " +
            " FROM IND_DISPENSED A, IND_DISPENSEM B, PHA_TRANSUNIT C " +
            " WHERE A.DISPENSE_NO = B.DISPENSE_NO " +
            " AND A.ORDER_CODE = C.ORDER_CODE " + org_where +
            " AND B.DISPENSE_DATE BETWEEN TO_DATE ('" + start_date +
            "', 'YYYYMMDDHH24MISS') " +
            " AND TO_DATE ('" + end_date + "', 'YYYYMMDDHH24MISS') " +
            " AND B.REQTYPE_CODE = '" + request_type + "'" +
            " AND B.UPDATE_FLG <> '2' " +
            " ORDER BY A.ORDER_CODE, A.BATCH_SEQ ";
        //System.out.println("SELECT_IND_DISPENSE_OUT_SQL---"+SELECT_IND_DISPENSE_OUT_SQL);
        TParm parm = new TParm(TJDODBTool.getInstance().select(
            SELECT_IND_DISPENSE_OUT_SQL));
        String order_code = "";
        String batch_no = "";
        String valid_date = "";
        String type = "";
        if ("DEP".equals(request_type) || "TEC".equals(request_type) ||
            "EXM".equals(request_type)) {
            type = "REQUEST_OUT_";
        }
        else if ("GIF".equals(request_type)) {
            type = "GIF_OUT_";
        }
        else if ("RET".equals(request_type)) {
            type = "RET_OUT_";
        }
        else if ("WAS".equals(request_type)) {
            type = "WAS_OUT_";
        }
        else if ("THO".equals(request_type)) {
            type = "THO_OUT_";
        }
        else if ("COS".equals(request_type)) {
            type = "COS_OUT_";
        }

        for (int i = 0; i < dataStore.rowCount(); i++) {
            order_code = dataStore.getItemString(i, "ORDER_CODE");
            batch_no = dataStore.getItemString(i, "BATCH_NO");
            valid_date = StringTool.getString(dataStore.getItemTimestamp(i,
                "VALID_DATE"), "yyyyMMdd");
            for (int j = 0; j < parm.getCount("ORDER_CODE"); j++) {
                if (order_code.equals(parm.getValue("ORDER_CODE", j)) &&
                    batch_no.equals(parm.getValue("BATCH_NO", j)) &&
                    valid_date.equals(StringTool.getString(parm.
                    getTimestamp("VALID_DATE", j), "yyyyMMdd"))) {
                    dataStore.setItem(i, type + "QTY",
                                      parm.getDouble("QTY", j) +
                                      dataStore.getItemDouble(i, type + "QTY"));
                    dataStore.setItem(i, type + "AMT",
                                      parm.getDouble("AMT", j) +
                                      dataStore.getItemDouble(i, type + "AMT"));
                    dataStore.setItem(i, "OUT_QTY",
                                      parm.getDouble("QTY", j) +
                                      dataStore.getItemDouble(i, "OUT_QTY"));
                    dataStore.setItem(i, "OUT_AMT",
                                      parm.getDouble("AMT", j) +
                                      dataStore.getItemDouble(i, "OUT_AMT"));
                    dataStore.setItem(i, "STOCKOUT_QTY",
                                      parm.getDouble("QTY", j) +
                                      dataStore.getItemDouble(i, "STOCKOUT_QTY"));
                    dataStore.setItem(i, "STOCKOUT_AMT",
                                      parm.getDouble("AMT", j) +
                                      dataStore.getItemDouble(i, "STOCKOUT_AMT"));
                    continue;
                }
            }
        }
        return dataStore;
    }

    /**
     * 22.计算当前库存量及金额STOCK_QTY,STOCK_AMT
     * @param dataStore TDataStore
     * @return TDataStore
     */
    private TDataStore setStock(TDataStore dataStore) {
        double qty = 0;
        double own_price = 0;
        for (int i = 0; i < dataStore.rowCount(); i++) {
            qty = dataStore.getItemDouble(i, "LAST_TOTSTOCK_QTY")
                + dataStore.getItemDouble(i, "IN_QTY")
                - dataStore.getItemDouble(i, "OUT_QTY")
                + dataStore.getItemDouble(i, "CHECKMODI_QTY");
            own_price = dataStore.getItemDouble(i, "RETAIL_PRICE");
            dataStore.setItem(i, "STOCK_QTY", qty);
            dataStore.setItem(i, "STOCK_AMT", qty * own_price);
        }
        return dataStore;
    }

    /**
     * 24.取得发药量及金额DOSAGE_QTY,DOSAGE_AMT
     * @param dataStore TDataStore
     * @param org_code String
     * @param date String
     * @return TDataStore
     */
    private TDataStore setDosage(TDataStore dataStore, String org_code,
                                 String date) {
        String start_date = date + "000000";
        String end_date = date + "235959";
        String order_code = "";
        //根据实际的交易档回写门诊发药
//        // 门诊部分发药
        String SELECT_IND_DOSAGE_OE_SQL =
            "SELECT   A.ORDER_CODE, SUM (A.DISPENSE_QTY1) AS QTY1, " +
            " SUM (A.DISPENSE_QTY2) AS QTY2,SUM (A.DISPENSE_QTY3) AS QTY3, "+
            " BATCH_SEQ1,BATCH_SEQ2,BATCH_SEQ3 "+
            " FROM OPD_ORDER A , PHA_BASE B " +
            " WHERE A.ORDER_CODE = B.ORDER_CODE " +
            " AND A.EXEC_DEPT_CODE = '" + org_code + "' " +
            " AND A.PHA_RETN_DATE IS NULL " +
            " AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE ('" + start_date +
            "', 'YYYYMMDDHH24MISS') " +
            " AND TO_DATE ('" + end_date + "', 'YYYYMMDDHH24MISS') " +
            " GROUP BY A.ORDER_CODE,batch_seq1, batch_seq2, batch_seq3  ORDER BY A.ORDER_CODE,batch_seq1, batch_seq2, batch_seq3 ";//,BATCH_SEQ1,BATCH_SEQ2,BATCH_SEQ3
    //    System.out.println("门诊发药sql:"+SELECT_IND_DOSAGE_OE_SQL);
        TParm parmOE = new TParm(TJDODBTool.getInstance().select(
            SELECT_IND_DOSAGE_OE_SQL));
        parmOE=getParmFromDispenseDetail(parmOE);
        double stock_qty = 0;
        double dosage_qty = 0;
        double own_price = 0;
        for (int i = 0; i < parmOE.getCount("ORDER_CODE"); i++) {
            order_code = parmOE.getValue("ORDER_CODE", i);
            dosage_qty = parmOE.getDouble("QTY", i);
            for (int j = 0; j < dataStore.rowCount(); j++) {
            if (order_code.equals(dataStore.getItemString(j, "ORDER_CODE"))) {
                    stock_qty = dataStore.getItemDouble(j, "STOCK_QTY");
                    own_price = dataStore.getItemDouble(j, "RETAIL_PRICE");
                    if (stock_qty >= dosage_qty) {
                        dataStore.setItem(j, "STOCK_QTY", dataStore.
                                          getItemDouble(j, "STOCK_QTY") -
                                          dosage_qty);
                        dataStore.setItem(j, "STOCK_AMT", dataStore.
                                          getItemDouble(j, "STOCK_AMT") -
                                          dosage_qty * own_price);
                        dataStore.setItem(j, "OUT_QTY", dataStore.
                                          getItemDouble(j, "OUT_QTY") +
                                          dosage_qty);
                        dataStore.setItem(j, "OUT_AMT", dataStore.
                                          getItemDouble(j, "OUT_AMT") +
                                          dosage_qty * own_price);
                        dataStore.setItem(j, "STOCKOUT_QTY", dataStore.
                                          getItemDouble(j, "STOCKOUT_QTY") +
                                          dosage_qty);
                        dataStore.setItem(j, "STOCKOUT_AMT", dataStore.
                                          getItemDouble(j, "STOCKOUT_AMT") +
                                          dosage_qty * own_price);
                        dataStore.setItem(j, "DOSAGE_QTY", dataStore.
                                          getItemDouble(j, "DOSAGE_QTY") +
                                          dosage_qty);
                        dataStore.setItem(j, "DOSAGE_AMT", dataStore.
                                          getItemDouble(j, "DOSAGE_AMT") +
                                          dosage_qty * own_price);
                        break;
                    }
                    else {
                        dataStore.setItem(j, "STOCK_QTY", dataStore.
                                          getItemDouble(j, "STOCK_QTY") -
                                          stock_qty);
                        dataStore.setItem(j, "STOCK_AMT", dataStore.
                                          getItemDouble(j, "STOCK_AMT") -
                                          stock_qty * own_price);
                        dataStore.setItem(j, "OUT_QTY", dataStore.
                                          getItemDouble(j, "OUT_QTY") +
                                          stock_qty);
                        dataStore.setItem(j, "OUT_AMT", dataStore.
                                          getItemDouble(j, "OUT_AMT") +
                                          stock_qty * own_price);
                        dataStore.setItem(j, "STOCKOUT_QTY", dataStore.
                                          getItemDouble(j, "STOCKOUT_QTY") +
                                          stock_qty);
                        dataStore.setItem(j, "STOCKOUT_AMT", dataStore.
                                          getItemDouble(j, "STOCKOUT_AMT") +
                                          stock_qty * own_price);
                        dataStore.setItem(j, "DOSAGE_QTY", dataStore.
                                          getItemDouble(j, "DOSAGE_QTY") +
                                          stock_qty);
                        dataStore.setItem(j, "DOSAGE_AMT", dataStore.
                                          getItemDouble(j, "DOSAGE_AMT") +
                                          stock_qty * own_price);
                        dosage_qty = dosage_qty - stock_qty;
                    }
                }
            	if(dosage_qty>0) {
            		//记录到日志 哪些药品未被扣库
            	}
            }
        }

        // 住院发药部分
        String SELECT_IND_DOSAGE_I_SQL =
            "SELECT   A.ORDER_CODE, SUM (A.DOSAGE_QTY) AS QTY " +
            " FROM ODI_DSPNM A, PHA_BASE B " +
            " WHERE A.ORDER_CODE = B.ORDER_CODE " +
            " AND A.EXEC_DEPT_CODE = '" + org_code + "' " +
            " AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE ('" + start_date + "', " +
            " 'YYYYMMDDHH24MISS' ) AND TO_DATE ('" + end_date +
            "', 'YYYYMMDDHH24MISS' )" +
            " GROUP BY A.ORDER_CODE ORDER BY A.ORDER_CODE";
        TParm parmI = new TParm(TJDODBTool.getInstance().select(
                SELECT_IND_DOSAGE_I_SQL));
         parmI=getParmFromDispenseDetail(parmI);
        stock_qty = 0;
        dosage_qty = 0;
        own_price = 0;
        for (int i = 0; i < parmI.getCount("ORDER_CODE"); i++) {
            order_code = parmI.getValue("ORDER_CODE", i);
            dosage_qty = parmI.getDouble("QTY", i);
            for (int j = 0; j < dataStore.rowCount(); j++) {
                if (order_code.equals(dataStore.getItemString(j, "ORDER_CODE"))) {        
                    stock_qty = dataStore.getItemDouble(j, "STOCK_QTY");
                    own_price = dataStore.getItemDouble(j, "RETAIL_PRICE");
                    if (stock_qty >= dosage_qty) {
                        dataStore.setItem(j, "STOCK_QTY", dataStore.
                                          getItemDouble(j, "STOCK_QTY") -
                                          dosage_qty);
                        dataStore.setItem(j, "STOCK_AMT", dataStore.
                                          getItemDouble(j, "STOCK_AMT") -
                                          dosage_qty * own_price);
                        dataStore.setItem(j, "OUT_QTY", dataStore.
                                          getItemDouble(j, "OUT_QTY") +
                                          dosage_qty);
                        dataStore.setItem(j, "OUT_AMT", dataStore.
                                          getItemDouble(j, "OUT_AMT") +
                                          dosage_qty * own_price);
                        dataStore.setItem(j, "STOCKOUT_QTY", dataStore.
                                          getItemDouble(j, "STOCKOUT_QTY") +
                                          dosage_qty);
                        dataStore.setItem(j, "STOCKOUT_AMT", dataStore.
                                          getItemDouble(j, "STOCKOUT_AMT") +
                                          dosage_qty * own_price);
                        dataStore.setItem(j, "DOSAGE_QTY", dataStore.
                                          getItemDouble(j, "DOSAGE_QTY") +
                                          dosage_qty);
                        dataStore.setItem(j, "DOSAGE_AMT", dataStore.
                                          getItemDouble(j, "DOSAGE_AMT") +
                                          dosage_qty * own_price);
                        break;
                    }
                    else {
                        dataStore.setItem(j, "STOCK_QTY", dataStore.
                                          getItemDouble(j, "STOCK_QTY") -
                                          stock_qty);
                        dataStore.setItem(j, "STOCK_AMT", dataStore.
                                          getItemDouble(j, "STOCK_AMT") -
                                          stock_qty * own_price);
                        dataStore.setItem(j, "OUT_QTY", dataStore.
                                          getItemDouble(j, "OUT_QTY") +
                                          stock_qty);
                        dataStore.setItem(j, "OUT_AMT", dataStore.
                                          getItemDouble(j, "OUT_AMT") +
                                          stock_qty * own_price);
                        dataStore.setItem(j, "STOCKOUT_QTY", dataStore.
                                          getItemDouble(j, "STOCKOUT_QTY") +
                                          stock_qty);
                        dataStore.setItem(j, "STOCKOUT_AMT", dataStore.
                                          getItemDouble(j, "STOCKOUT_AMT") +
                                          stock_qty * own_price);
                        dataStore.setItem(j, "DOSAGE_QTY", dataStore.
                                          getItemDouble(j, "DOSAGE_QTY") +
                                          stock_qty);
                        dataStore.setItem(j, "DOSAGE_AMT", dataStore.
                                          getItemDouble(j, "DOSAGE_AMT") +
                                          stock_qty * own_price);
                        dosage_qty = dosage_qty - stock_qty;
                    }
                }
            }
        }

        return dataStore;
    }

    /**
     * 23.取得退药量及金额REGRESSDRUG_QTY,REGRESSDRUG_AMT
     * @param dataStore TDataStore
     * @param org_code String
     * @param date String
     * @return TDataStore
     */
    private TDataStore setRegeessDrug(TDataStore dataStore, String org_code,
                                      String date) {
        String start_date = date + "000000";
        String end_date = date + "235959";
        String order_code = "";

        // 住院退药部分
        String SELECT_IND_REGRESSDRUG_I_SQL = "SELECT A.ORDER_CODE, " +
            "SUM (A.RTN_DOSAGE_QTY) AS QTY " +
            " FROM ODI_DSPNM A, PHA_BASE B " +
            " WHERE A.ORDER_CODE = B.ORDER_CODE " +
            " AND A.EXEC_DEPT_CODE = '" + org_code + "' " +
            " AND A.DSPN_KIND = 'RT' " +
            " AND A.PHA_RETN_DATE BETWEEN TO_DATE ('" + start_date +
            "', 'YYYYMMDDHH24MISS') AND TO_DATE ('" + end_date +
            "', 'YYYYMMDDHH24MISS') GROUP BY A.ORDER_CODE ORDER BY A.ORDER_CODE";

        TParm parmI = new TParm(TJDODBTool.getInstance().select(
            SELECT_IND_REGRESSDRUG_I_SQL));

        double ret_qty = 0;
        double own_price = 0;
        for (int i = 0; i < parmI.getCount("ORDER_CODE"); i++) {
            order_code = parmI.getValue("ORDER_CODE", i);
            ret_qty = parmI.getDouble("QTY", i);
            for (int j = dataStore.rowCount() - 1; j >= 0; j--) {
                	if (order_code.equals(dataStore.getItemString(j, "ORDER_CODE"))) {
                    own_price = dataStore.getItemDouble(j, "RETAIL_PRICE");
                    dataStore.setItem(j, "STOCK_QTY", dataStore.
                                      getItemDouble(j, "STOCK_QTY") +
                                      ret_qty);
                    dataStore.setItem(j, "STOCK_AMT", dataStore.
                                      getItemDouble(j, "STOCK_AMT") +
                                      ret_qty * own_price);
                    dataStore.setItem(j, "IN_QTY", dataStore.
                                      getItemDouble(j, "IN_QTY") +
                                      ret_qty);
                    dataStore.setItem(j, "OUT_AMT", dataStore.
                                      getItemDouble(j, "IN_AMT") +
                                      ret_qty * own_price);
                    dataStore.setItem(j, "STOCKIN_QTY", dataStore.
                                      getItemDouble(j, "STOCKIN_QTY") +
                                      ret_qty);
                    dataStore.setItem(j, "STOCKIN_AMT", dataStore.
                                      getItemDouble(j, "STOCKIN_AMT") +
                                      ret_qty * own_price);
                    dataStore.setItem(j, "REGRESSDRUG_QTY", dataStore.
                                      getItemDouble(j, "REGRESSDRUG_QTY") +
                                      ret_qty);
                    dataStore.setItem(j, "REGRESSDRUG_AMT", dataStore.
                                      getItemDouble(j, "REGRESSDRUG_AMT") +
                                      ret_qty * own_price);
                    break;
                }
            }
        }

        //门诊退药部分
        //************************************************************************************************
        //luhai 2012-2-10 处理退药，退药时写入表因每笔退药单仅仅记录一笔退药的batchSeq，故仅取dispense_qty1
        //防止 A.CANCEL_DOSAGE_QTY 不能准确对应到batchSeq 故仅仅启用batchSeq1
        //************************************************************************************************
//        String SELECT_IND_REGRESSDRUG_O_SQL =
//            " SELECT A.ORDER_CODE, SUM (A.DOSAGE_QTY) AS QTY "
//            + " FROM PHA_ORDER_HISTORY A , OPD_ORDER B "
//            + " WHERE A.CASE_NO = B.CASE_NO "
//            + " AND A.RX_NO = B.RX_NO "
//            + " AND A.SEQ_NO = B.SEQ_NO "
//            + " AND A.EXEC_DEPT_CODE = '" + org_code + "' "
//            + " AND A.PHA_RETN_DATE >= '" + start_date + "' "
//            + " AND A.PHA_RETN_DATE <= '" + end_date + "' "
//            + " GROUP BY A.ORDER_CODE ORDER BY A.ORDER_CODE";
        String SELECT_IND_REGRESSDRUG_O_SQL =
            " SELECT A.ORDER_CODE, SUM (A.DISPENSE_QTY) AS QTY "
            + " FROM  OPD_ORDER A "
            + " WHERE  "
            + "  A.EXEC_DEPT_CODE = '" + org_code + "' "
            + " AND A.PHA_RETN_DATE >= '" + start_date + "' "
            + " AND A.PHA_RETN_DATE <= '" + end_date + "' "
            + " GROUP BY A.ORDER_CODE ORDER BY A.ORDER_CODE  ";
        //************************************************************************************************
        //luhai 2012-2-10 处理退药，退药时写入表因每笔退药单仅仅记录一笔退药的batchSeq，故仅取dispense_qty1
        //防止 A.CANCEL_DOSAGE_QTY 不能准确对应到batchSeq 故仅仅启用batchSeq1 END
        //************************************************************************************************
//        System.out.println("门诊退药语句："+SELECT_IND_REGRESSDRUG_O_SQL);
        TParm parmO = new TParm(TJDODBTool.getInstance().select(
            SELECT_IND_REGRESSDRUG_O_SQL));
        parmO=getParmFromDispenseDetail(parmO);
        ret_qty = 0;
        own_price = 0;
        for (int i = 0; i < parmO.getCount("ORDER_CODE"); i++) {
            order_code = parmO.getValue("ORDER_CODE", i);
            ret_qty = parmO.getDouble("QTY", i);
            for (int j = dataStore.rowCount() - 1; j >= 0; j--) {
            	//luhai modify 加入seq的限定 begin
//                if (order_code.equals(dataStore.getItemString(j, "ORDER_CODE"))) {
                if (order_code.equals(dataStore.getItemString(j, "ORDER_CODE"))) {
                	//luhai modify 加入seq的限定 end
                    own_price = dataStore.getItemDouble(j, "RETAIL_PRICE");
                    dataStore.setItem(j, "STOCK_QTY", dataStore.
                                      getItemDouble(j, "STOCK_QTY") +
                                      ret_qty);
                    dataStore.setItem(j, "STOCK_AMT", dataStore.
                                      getItemDouble(j, "STOCK_AMT") +
                                      ret_qty * own_price);
                    dataStore.setItem(j, "IN_QTY", dataStore.
                                      getItemDouble(j, "IN_QTY") +
                                      ret_qty);
                    dataStore.setItem(j, "OUT_AMT", dataStore.
                                      getItemDouble(j, "IN_AMT") +
                                      ret_qty * own_price);
                    dataStore.setItem(j, "STOCKIN_QTY", dataStore.
                                      getItemDouble(j, "STOCKIN_QTY") +
                                      ret_qty);
                    dataStore.setItem(j, "STOCKIN_AMT", dataStore.
                                      getItemDouble(j, "STOCKIN_AMT") +
                                      ret_qty * own_price);
                    dataStore.setItem(j, "REGRESSDRUG_QTY", dataStore.
                                      getItemDouble(j, "REGRESSDRUG_QTY") +
                                      ret_qty);
                    dataStore.setItem(j, "REGRESSDRUG_AMT", dataStore.
                                      getItemDouble(j, "REGRESSDRUG_AMT") +
                                      ret_qty * own_price);
                    break;
                }
            }
        }
        			
        return dataStore;
    }

    /**
     * 25.计算调价损益金额PROFIT_LOSS_AMT
     * @param dataStore TDataStore
     * @return TDataStore
     */
    private TDataStore setProfitLoss(TDataStore dataStore) {
        double profot_loss_amt = 0;
        double stock_amt = 0;
        for (int i = 0; i < dataStore.rowCount(); i++) {
            stock_amt = dataStore.getItemDouble(i, "LAST_TOTSTOCK_AMT")
                + dataStore.getItemDouble(i, "IN_AMT")
                - dataStore.getItemDouble(i, "OUT_AMT")
                + dataStore.getItemDouble(i, "CHECKMODI_AMT");
            profot_loss_amt = dataStore.getItemDouble(i, "STOCK_AMT") -
                stock_amt;
            dataStore.setItem(i, "PROFIT_LOSS_AMT", profot_loss_amt);
        }

        return dataStore;
    }
    /**
     *
     * 从带有batchSeq信息的parm中拆分药品
     * @param detailParm
     * @return
     */
    private TParm getParmFromDispenseDetail(TParm detailParm){
    	TParm returnParm = new TParm();
    	for(int i = 0;i<detailParm.getCount("ORDER_CODE");i++){
    		//batchSeq 1
    		returnParm.addData("ORDER_CODE", detailParm.getValue("ORDER_CODE",i));
    		returnParm.addData("BATCH_SEQ", detailParm.getValue("BATCH_SEQ1",i));
    		returnParm.addData("QTY", detailParm.getValue("QTY1",i));
    		//batchSeq 2
    		returnParm.addData("ORDER_CODE", detailParm.getValue("ORDER_CODE",i));
    		returnParm.addData("QTY", detailParm.getValue("QTY2",i));
    		returnParm.addData("BATCH_SEQ", detailParm.getValue("BATCH_SEQ2",i));
    		//batchSeq 3
    		returnParm.addData("ORDER_CODE", detailParm.getValue("ORDER_CODE",i));
    		returnParm.addData("QTY", detailParm.getValue("QTY3",i));
    		returnParm.addData("BATCH_SEQ", detailParm.getValue("BATCH_SEQ3",i));
    	}
    	return returnParm;
    }
    
    /**
     * 根据部门编码查询最近的重新日结 日期
     * @param orgCode 部门编码
     * @author liyh
     * @date 20120629
     * @return str 
     */
    public  String getLastTranDateByOrgCode(String orgCode){
    	String resultStr = "-1";
    	String sql = " SELECT MAX(TRANDATE) AS TRANDATE FROM IND_DDSTOCK WHERE ORG_CODE='" + orgCode + "' ";
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	if(null != parm && parm.getCount("TRANDATE") >0 ){
    		resultStr = parm.getValue("TRANDATE", 0);
    	}else{
    		resultStr = "-1";
    	}
    	return resultStr;
    }

}
