package jdo.inv;

import com.dongyang.data.TParm;
/**
 *
 * <p>Title: 物资处理数据</p>
 *
 * <p>Description:物资处理数据 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:javahis </p>
 *
 * @author fudw 2009-09-21
 * @version 1.0
 */
public class INV {
    /**
     * 引入库存主档
     * @return SYSFeeTool
     */
    public static InvStockMTool InvStockMTool() {
        return InvStockMTool.getInstance();
    }
    /**
     * 引入库存明细档
     * @return SYSFeeTool
     */
    public static InvStockDTool InvStockDTool() {
        return InvStockDTool.getInstance();
    }

    /**
     * 查找库存量（有批次序号的物资明细档中某一科室的库存总量）
     * @param orgCode String(科室代码)
     * @param invCode String(物资代码)
     * @param batchSeq int(批次序号)
     * @return double(库存量)
     */
    public static double getStockQty(String orgCode, String invCode,int batchSeq) {
        double qty = 0.0;
        return qty;
    }

    /**
     *  查找所有库存物资（有批次序号的某一科室的库存物资）
     * @param orgCode String(科室)(科室)
     * @param invCode String(物资代码)
     * @param batchSeq int
     * @return TParm
     */
    public static TParm getAllStockQty(String orgCode, String invCode,
                                       int batchSeq) {
        TParm result = new TParm();
        return result;
    }
    /**
     *  查找库存总量（库存主档中某科室某物资总量）
     * @param orgCode String(科室代码)
     * @param invCode String(物资代码)
     * @return double
     */
    public static double getStockQty(String orgCode, String invCode) {
        if(invCode == null ||invCode.length() == 0)
              return -1;
          TParm parm=new TParm();
          parm.setData("INV_CODE",invCode);
          parm.setData("ORG_CODE",orgCode);
          parm=InvStockMTool().getStockQty(parm);   //wm2013-06-04  INVStockMTool()
          if (parm.getErrCode() < 0){
              System.out.println(parm.getErrCode() + " " + parm.getErrText());
              return -1;
          }
          return parm.getDouble("SUM(STOCK_QTY)",0);
    }
    /**
     * 查找所有库存物资（库存主档中某科室的全部批号的库存）
     * @param orgCode String
     * @param invCode String
     * @return TParm
     */
    public static TParm getAllStockQty(String orgCode, String invCode) {
    	System.out.println("INV.java");
        if (orgCode == null || orgCode.length() == 0 || invCode == null ||
            invCode.length() == 0)
            return null;
        TParm parm = new TParm();
        parm.setData("INV_CODE", invCode);
        parm.setData("ORG_CODE",orgCode);
        parm=InvStockDTool().getStockBatchSeq(parm);			//wm2013-06-03
        if (parm.getErrCode() < 0)
           System.out.println(parm.getErrCode() + " " + parm.getErrText());
        return parm;
    }
    /**
     * 查找库存总量（库存主档中某物资全院总量）
     * @param invCode String
     * @return double
     */
    public static double getStockQty(String invCode){
        if(invCode == null ||invCode.length() == 0)
            return -1;
        TParm parm=new TParm();
        parm.setData("INV_CODE",invCode);
        parm=InvStockMTool().getStockQty(parm);
        if (parm.getErrCode() < 0){
            System.out.println(parm.getErrCode() + " " + parm.getErrText());
            return -1;
        }
        return parm.getDouble("STOCK_QTY",0);
    }
    /**
     * 自动补充打包
     * @param parm TParm
     * @return String
     */
    public String autoRePack(TParm parm){
        return "";
    }




}
