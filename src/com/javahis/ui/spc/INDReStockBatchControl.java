package com.javahis.ui.spc;

import com.dongyang.control.TControl;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.util.StringTool;
import java.util.Date;
import java.sql.Timestamp;
import jdo.ind.IndReStockBatchTool;

/**
 * <p>Title: 重新日结过帐</p>
 *
 * <p>Description: 重新日结过帐</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy 2011.1.5
 * @version 1.0
 */
public class INDReStockBatchControl
    extends TControl {
    public INDReStockBatchControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        Timestamp date = TJDODBTool.getInstance().getDBTime();
        // 初始化查询区间
        this.setValue("DATE", date.toString().substring(0, 10).replace('-', '/'));
        //this.setValue("DATE", "2010/10/31");
    }

    /**
     * 重新日结过帐保存
     */
    public void onSave() {
        String org_code = this.getValueString("ORG_CODE");
        if ("".equals(org_code)) {
            this.messageBox("请选择过账药房");
            return;
        }
        String date = this.getValueString("DATE");
        date = date.substring(0, 4) + date.substring(5, 7) +
            date.substring(8, 10);
        //查询最近日结过账 日期
        Timestamp datetime = StringTool.getTimestamp(date, "yyyyMMdd");
        Timestamp lastdate = StringTool.rollDate(datetime, -1); 
        String last_date = StringTool.getString(lastdate, "yyyyMMdd");
        
        String resultString = IndReStockBatchTool.getInstance().getLastTranDateByOrgCode(org_code);
        if(!"".equals(resultString) &&  !"-1".equals(resultString)){//-1表示没有日结信息
        	int lastDateInt = Integer.parseInt(last_date);
        	int resultInt = Integer.parseInt(resultString);
        	resultInt += 1;
        	if(resultInt < lastDateInt){
        		this.messageBox("请先日结"+resultInt);
        		return ;
        	}
        }
        if (IndReStockBatchTool.getInstance().onReStockBatch(org_code, date)) {
            this.messageBox("执行成功！");
        }
        else {
            this.messageBox("执行失败！");
        }

//        if (onReStockBatch(org_code, date)) {
//            this.messageBox("执行成功！");
//        }
//        else {
//            this.messageBox("执行失败！");
//        }


    }

    /**
     * 日结过帐
     * @param org_code String
     * @param date String
     * @return boolean
     */
    public boolean onReStockBatch(String org_code, String date) {
        // 1.删除当天的IND_DDSTOCK数据
        boolean flg = deleteIndDDstock(org_code, date);
        if (!flg) {
            return false;
        }
        // 2.取得当前IND_STOCK数据
        TParm ind_stock = getIndStock(org_code);
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
       /* // 6.取得本期盘点库存量及金额CHECKMODI_QTY,CHECKMODI_AMT
        ind_ddstock = setCheckModi(ind_ddstock, org_code, date);
        if (ind_ddstock == null) {
            return false;
        }*/
        // 7.取得验收入库量及金额VERIFYIN_QTY,VERIFYIN_AMT
        ind_ddstock = setVerifyin(ind_ddstock, org_code, date);
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
            //System.out.println("sql[" + i + "]---" + sql[i]);
            parm = new TParm(TJDODBTool.getInstance().update(sql[i]));
            if (parm == null || parm.getErrCode() < 0) {
                this.messageBox("IND_DDSTOCK数据插入失败！！！");
                return false;
            }
        }
        //加入清空库存为0数据功能
        //luhai add 2012-2-10 begin
        StringBuffer sqlbf = new StringBuffer();
        sqlbf.append(" DELETE  FROM IND_STOCK WHERE (LAST_TOTSTOCK_QTY+IN_QTY-OUT_QTY-CHECKMODI_QTY)>0 ");
        parm = new TParm();
        parm = new TParm(TJDODBTool.getInstance().update(sqlbf.toString()));
        if (parm == null || parm.getErrCode() < 0) {
            this.messageBox("IND_STOCK清空库存为0数据失败！！！");
            return false;
        }
        //加入清空库存为0数据功能end
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
        if (parm == null || parm.getErrCode() < 0) {
            this.messageBox("IND_DDSTOCK数据删除失败！！！");
            return false;
        }
        return true;
    }

    /**
     * 2.取得当前IND_STOCK数据
     * @param org_code String
     */
    private TParm getIndStock(String org_code) {
        String SELECT_IND_STOCK_SQL =
            "SELECT * FROM IND_STOCK WHERE ORG_CODE = '" + org_code +
            "' ORDER BY ORDER_CODE, VALID_DATE, BATCH_SEQ";
        //System.out.println("SELECT_IND_STOCK_SQL---"+SELECT_IND_STOCK_SQL);
        TParm parm = new TParm(TJDODBTool.getInstance().select(
            SELECT_IND_STOCK_SQL));
        if (parm == null || parm.getErrCode() < 0 ||
            parm.getCount("ORDER_CODE") <= 0) {
            this.messageBox("IND_STOCK数据取得失败！！！");
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
            this.messageBox("IND_DDSTOCK数据未完全删除！！！");
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
        for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
            order_code = parm.getValue("ORDER_CODE", i);
            String sql = "SELECT A.TRADE_PRICE, B.OWN_PRICE "
                + " FROM PHA_BASE A, SYS_FEE_HISTORY B "
                + " WHERE A.ORDER_CODE = B.ORDER_CODE "
                + " AND TO_DATE ('" + trandate + "235959', 'YYYYMMDDHH24MISS') "
                + " BETWEEN TO_DATE (B.START_DATE, 'YYYYMMDDHH24MISS') "
                + " AND TO_DATE (B.END_DATE, 'YYYYMMDDHH24MISS') "
                + " AND A.ORDER_CODE = ";
            String order_by = "  ORDER BY B.END_DATE DESC ";
            sql = sql + "'" + order_code + "'" + order_by;

            TParm pha_base = new TParm(TJDODBTool.getInstance().select(sql));
            if (pha_base == null || pha_base.getCount("TRADE_PRICE") <= 0) {
                //System.out.println("sql----" + sql);
                continue;
            }

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
                              pha_base.getDouble("OWN_PRICE", 0));
            // 31-35
            dataStore.setItem(row, "TRADE_PRICE",
                              pha_base.getDouble("TRADE_PRICE", 0));
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
        Timestamp lastdate = StringTool.rollDate(datetime, -1);
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
        String SELECT_IND_QTYCHECK_SQL =
            "SELECT ORDER_CODE, BATCH_SEQ, RETAIL_PRICE * MODI_QTY AS MODI_AMT, " +
            "MODI_QTY FROM IND_QTYCHECK WHERE ORG_CODE = '" +
            org_code + "' AND TO_DATE(FROZEN_DATE, 'YYYYMMDDHH24MISS') BETWEEN " +
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
                                     0);
                    dataStore.setItem(i, "CHECKMODI_AMT",
                                      0);
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
                                    String date) {
        String start_date = date + "000000";
        String end_date = date + "235959";
        String SELECT_IND_VERIFYIN_SQL =
            "SELECT A.ORDER_CODE, A.BATCH_NO, A.VALID_DATE, " +
            " A.VERIFYIN_QTY * C.DOSAGE_QTY * C.STOCK_QTY  AS QTY, " +
            " A.VERIFYIN_QTY * A.RETAIL_PRICE AS AMT " +
            " FROM IND_VERIFYIND A, IND_VERIFYINM B, PHA_TRANSUNIT C " +
            " WHERE A.VERIFYIN_NO = B.VERIFYIN_NO " +
            " AND A.ORDER_CODE = C.ORDER_CODE " +
            " AND B.ORG_CODE = '" + org_code + "' " +
            " AND B.CHECK_DATE BETWEEN TO_DATE ('" + start_date +
            "', 'YYYYMMDDHH24MISS') AND TO_DATE ('" + end_date +
            "', 'YYYYMMDDHH24MISS') ORDER BY A.ORDER_CODE ";
        //System.out.println("SELECT_IND_VERIFYIN_SQL---"+SELECT_IND_VERIFYIN_SQL);
        TParm parm = new TParm(TJDODBTool.getInstance().select(
            SELECT_IND_VERIFYIN_SQL));
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
                    dataStore.setItem(i, "VERIFYIN_QTY",
                                      parm.getDouble("QTY", j) +
                                      dataStore.getItemDouble(i, "VERIFYIN_QTY"));
                    dataStore.setItem(i, "VERIFYIN_AMT",
                                      parm.getDouble("AMT", j) +
                                      dataStore.getItemDouble(i, "VERIFYIN_AMT"));
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
     * 9-13.取得入库量及金额IN_QTY,IN_AMT
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
            " A.QTY * A.RETAIL_PRICE AS AMT " +
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
            " A.QTY * A.RETAIL_PRICE AS AMT " +
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
     * 23.取得发药量及金额DOSAGE_QTY,DOSAGE_AMT
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
        // 门诊部分发药
        //luhai modify 2012-2-9 begin 根据实际的seq回写 begin
//        String SELECT_IND_DOSAGE_OE_SQL =
//        	"SELECT   A.ORDER_CODE, SUM (A.DOSAGE_QTY) AS QTY " +
//        	" FROM OPD_ORDER A , PHA_BASE B " +
//        	" WHERE A.ORDER_CODE = B.ORDER_CODE " +
//        	" AND A.EXEC_DEPT_CODE = '" + org_code + "' " +
//        	" AND A.PHA_RETN_DATE IS NULL " +
//        	" AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE ('" + start_date +
//        	"', 'YYYYMMDDHH24MISS') " +
//        	" AND TO_DATE ('" + end_date + "', 'YYYYMMDDHH24MISS') " +
//        	" GROUP BY A.ORDER_CODE ORDER BY A.ORDER_CODE ";
//        //System.out.println("SELECT_IND_DOSAGE_OE_SQL---"+SELECT_IND_DOSAGE_OE_SQL);
//        TParm parmOE = new TParm(TJDODBTool.getInstance().select(
//            SELECT_IND_DOSAGE_OE_SQL));
//        //System.out.println("SELECT_IND_DOSAGE_OE_SQL---"+SELECT_IND_DOSAGE_OE_SQL);
//        TParm parmOE = new TParm(TJDODBTool.getInstance().select(
//            SELECT_IND_DOSAGE_OE_SQL));
        String SELECT_IND_DOSAGE_OE_SQL =
            "SELECT   A.ORDER_CODE, SUM (A.DISPENSE_QTY1) AS QTY1, " +
            " SUM (A.DISPENSE_QTY2) AS QTY2,SUM (A.DISPENSE_QTY3) AS QTY3, "+
            " BATCH_SEQ1,BATCH_SEQ2,BATCH_SEQ3 "+
            " FROM OPD_ORDER A , PHA_BASE B " +
            " WHERE A.ORDER_CODE = B.ORDER_CODE " +
            " AND A.EXEC_DEPT_CODE = '" + org_code + "' " +
//            " AND A.PHA_RETN_DATE IS NULL " +
            " AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE ('" + start_date +
            "', 'YYYYMMDDHH24MISS') " +
            " AND TO_DATE ('" + end_date + "', 'YYYYMMDDHH24MISS') " +
            " GROUP BY A.ORDER_CODE ORDER BY A.ORDER_CODE,BATCH_SEQ1,BATCH_SEQ2,BATCH_SEQ3 ";
        TParm parmOE = new TParm(TJDODBTool.getInstance().select(
            SELECT_IND_DOSAGE_OE_SQL));
        parmOE=getParmFromDispenseDetail(parmOE);
      //luhai modify 2012-2-9 begin 根据实际的seq回写  end
        double stock_qty = 0;
        double dosage_qty = 0;
        double own_price = 0;
        String batchSeq="-1";
        for (int i = 0; i < parmOE.getCount("ORDER_CODE"); i++) {
            order_code = parmOE.getValue("ORDER_CODE", i);
            dosage_qty = parmOE.getDouble("QTY", i);
            //luhai modify 判断时加入batchSeq begin
            batchSeq=parmOE.getValue("BATCH_SEQ",i);
            //luhai modify 判断时加入batchSeq end
            for (int j = 0; j < dataStore.rowCount(); j++) {
            	//luhai 2012-2-10 add 批次发药时加入batchSeq 的判断 begin
//                if (order_code.equals(dataStore.getItemString(j, "ORDER_CODE"))) {
                 if (order_code.equals(dataStore.getItemString(j, "ORDER_CODE"))&&
                     batchSeq.equals(dataStore.getItemString(j, "BATCH_SEQ"))
                    ) {
                //luhai 2012-2-10 add 批次发药时加入batchSeq 的判断 end
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

        // 住院发药部分
        //luhai 2012-2-10 加入根据实际发药详细信息回写ddstock 发药数据 begin
//        String SELECT_IND_DOSAGE_I_SQL =
//            "SELECT   A.ORDER_CODE, SUM (A.DOSAGE_QTY) AS QTY " +
//            " FROM ODI_DSPNM A, PHA_BASE B " +
//            " WHERE A.ORDER_CODE = B.ORDER_CODE " +
//            " AND A.EXEC_DEPT_CODE = '" + org_code + "' " +
//            " AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE ('" + start_date + "', " +
//            " 'YYYYMMDDHH24MISS' ) AND TO_DATE ('" + end_date +
//            "', 'YYYYMMDDHH24MISS' )" +
//            " GROUP BY A.ORDER_CODE ORDER BY A.ORDER_CODE";
//        //System.out.println("SELECT_IND_DOSAGE_I_SQL---"+SELECT_IND_DOSAGE_I_SQL);
//        TParm parmI = new TParm(TJDODBTool.getInstance().select(
//            SELECT_IND_DOSAGE_I_SQL));
        String SELECT_IND_DOSAGE_I_SQL =
            "SELECT   A.ORDER_CODE, SUM (A.DISPENSE_QTY1) AS QTY1, " +
            " SUM (A.DISPENSE_QTY2) AS QTY2,SUM (A.DISPENSE_QTY3) AS QTY3, "+
            " BATCH_SEQ1,BATCH_SEQ2,BATCH_SEQ3 "+
            " FROM ODI_DSPNM A, PHA_BASE B " +
            " WHERE A.ORDER_CODE = B.ORDER_CODE " +
            " AND A.EXEC_DEPT_CODE = '" + org_code + "' " +
            " AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE ('" + start_date + "', " +
            " 'YYYYMMDDHH24MISS' ) AND TO_DATE ('" + end_date +
            "', 'YYYYMMDDHH24MISS' )" +
            " GROUP BY A.ORDER_CODE ORDER BY A.ORDER_CODE,BATCH_SEQ1,BATCH_SEQ2,BATCH_SEQ3 ";
        //System.out.println("SELECT_IND_DOSAGE_I_SQL---"+SELECT_IND_DOSAGE_I_SQL);
        TParm parmI = new TParm(TJDODBTool.getInstance().select(
            SELECT_IND_DOSAGE_I_SQL));
        parmI=getParmFromDispenseDetail(parmI);
      //luhai 2012-2-10 加入根据实际发药详细信息回写ddstock 发药数据 end
        stock_qty = 0;
        dosage_qty = 0;
        own_price = 0;
        batchSeq="";
        for (int i = 0; i < parmI.getCount("ORDER_CODE"); i++) {
            order_code = parmI.getValue("ORDER_CODE", i);
            dosage_qty = parmI.getDouble("QTY", i);
            //luhai modify 判断时加入batchSeq begin
            batchSeq=parmI.getValue("BATCH_SEQ",i);
            //luhai modify 判断时加入batchSeq end
            for (int j = 0; j < dataStore.rowCount(); j++) {
            //luhai 2012-2-10 add 批次发药时加入batchSeq 的判断 begin
//                if (order_code.equals(dataStore.getItemString(j, "ORDER_CODE"))) {
//            	System.out.println(batchSeq+"+===="+dataStore.getItemString(j, "BATCH_SEQ"));
                  if (order_code.equals(dataStore.getItemString(j, "ORDER_CODE"))&&
                       batchSeq.equals(dataStore.getItemString(j, "BATCH_SEQ"))
                      ) {
            //luhai 2012-2-10 add 批次发药时加入batchSeq 的判断 end
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
     * 24.取得退药量及金额REGRESSDRUG_QTY,REGRESSDRUG_AMT
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
        //luhai 2012-2-10 退药逻辑根据实际的发药详细信息修改begin
        // 住院退药部分
//        String SELECT_IND_REGRESSDRUG_I_SQL = "SELECT A.ORDER_CODE, " +
//            "SUM (A.RTN_DOSAGE_QTY - A.CANCEL_DOSAGE_QTY) AS QTY " +
//            " FROM ODI_DSPNM A, PHA_BASE B " +
//            " WHERE A.ORDER_CODE = B.ORDER_CODE " +
//            " AND A.EXEC_DEPT_CODE = '" + org_code + "' " +
//            " AND A.DSPN_KIND = 'RT' " +
//            " AND A.PHA_RETN_DATE BETWEEN TO_DATE ('" + start_date +
//            "', 'YYYYMMDDHH24MISS') AND TO_DATE ('" + end_date +
//            "', 'YYYYMMDDHH24MISS') GROUP BY A.ORDER_CODE ORDER BY A.ORDER_CODE";
//        //System.out.println("SELECT_IND_REGRESSDRUG_I_SQL---" + SELECT_IND_REGRESSDRUG_I_SQL);
//        TParm parmI = new TParm(TJDODBTool.getInstance().select(
//            SELECT_IND_REGRESSDRUG_I_SQL));
        String SELECT_IND_REGRESSDRUG_I_SQL = "SELECT A.ORDER_CODE, " +
            "SUM (A.RTN_DOSAGE_QTY - A.CANCEL_DOSAGE_QTY) AS QTY1 " +
            " SUM (A.DISPENSE_QTY2) AS QTY2,SUM (A.DISPENSE_QTY3) AS QTY3, "+
            " BATCH_SEQ1,BATCH_SEQ2,BATCH_SEQ3 "+
            " FROM ODI_DSPNM A, PHA_BASE B " +
            " WHERE A.ORDER_CODE = B.ORDER_CODE " +
            " AND A.EXEC_DEPT_CODE = '" + org_code + "' " +
            " AND A.DSPN_KIND = 'RT' " +
            " AND A.PHA_RETN_DATE BETWEEN TO_DATE ('" + start_date +
            "', 'YYYYMMDDHH24MISS') AND TO_DATE ('" + end_date +
            "', 'YYYYMMDDHH24MISS') GROUP BY A.ORDER_CODE,BATCH_SEQ1,BATCH_SEQ2,BATCH_SEQ3 ORDER BY A.ORDER_CODE";
        //System.out.println("SELECT_IND_REGRESSDRUG_I_SQL---" + SELECT_IND_REGRESSDRUG_I_SQL);
        TParm parmI = new TParm(TJDODBTool.getInstance().select(
            SELECT_IND_REGRESSDRUG_I_SQL));
        parmI=getParmFromDispenseDetail(parmI);
      //luhai 2012-2-10 退药逻辑根据实际的发药详细信息修改begin
        double ret_qty = 0;
        double own_price = 0;
        String batchSeq="";
        //luhai modify 2012-2-10 begin 根据实际的发药相关信息回写
//        for (int i = 0; i < parmI.getCount("ORDER_CODE"); i++) {
//            order_code = parmI.getValue("ORDER_CODE", i);
//            ret_qty = parmI.getDouble("QTY", i);
//            for (int j = dataStore.rowCount() - 1; j >= 0; j--) {
//                if (order_code.equals(dataStore.getItemString(j, "ORDER_CODE"))) {
//                    own_price = dataStore.getItemDouble(j, "RETAIL_PRICE");
//        System.out.println("退药----------------------------------------------------------------------");
        for (int i = 0; i < parmI.getCount("ORDER_CODE"); i++) {
            order_code = parmI.getValue("ORDER_CODE", i);
            ret_qty = parmI.getDouble("QTY", i);
            batchSeq=parmI.getValue("BATCH_SEQ",i);
            for (int j = dataStore.rowCount() - 1; j >= 0; j--) {
//                if (order_code.equals(dataStore.getItemString(j, "ORDER_CODE"))) {
                if (order_code.equals(dataStore.getItemString(j, "ORDER_CODE"))&&
                        batchSeq.equals(dataStore.getItemString(j, "BATCH_SEQ"))){
                    own_price = dataStore.getItemDouble(j, "RETAIL_PRICE");
        //luhai modify 2012-2-10 end
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
     * 测试方式
     * @param args String[]
     */
    public static void main(String[] args) {
        INDReStockBatchControl ind = new INDReStockBatchControl();
        ind.onReStockBatch("", "");
    }
}
