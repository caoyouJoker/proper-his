package jdo.inv;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;

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
public class InvPackStockMTool
    extends TJDOTool {

    /**
     * 实例
     */
    public static InvPackStockMTool instanceObject;

    /**
     * 构造器
     */
    public InvPackStockMTool() {
        setModuleName("inv\\INVPackStockMModule.x");
        onInit();
    }

    /**
     * 得到实例
     *
     * @return IndPurPlanMTool
     */
    public static InvPackStockMTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvPackStockMTool();
        return instanceObject;
    }

    /**
     * 手术包打包新增序号管理细项的库存量
     *
     * @param parm
     * @return
     */
    public TParm onInsertStockQtyByPack(TParm parm, TConnection conn) {
        TParm result = this.update("insertStockQtyByPack", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 手术包打包更新序号管理细项的库存量
     *
     * @param parm
     * @return
     */
    public TParm onUpdateStockQtyByPack(TParm parm, TConnection conn) {
        TParm result = this.update("updateStockQtyByPack", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询手术包主档
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryStockM(TParm parm) {
        TParm result = this.query("queryStockM", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 没有序号管理的手术包,扣除主库中手术包的库存量
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateQtyBySupReq(TParm parm, TConnection conn) {
        TParm result = this.update("updateQtyBySupReq", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 有序号管理的手术包,更新手术包的状态为出库
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateStatusBySupReq(TParm parm, TConnection conn) {
        TParm result = this.update("updateStatusBySupReq", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 增加主库中手术包的库存量及变更状态
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateQtyAndStatus(TParm parm, TConnection conn) {
        TParm result = this.update("updateQtyAndStatus", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除主项
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDelete(TParm parm, TConnection conn) {
        TParm result = this.update("delete", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
//------------------供应室新增start-------------------------
    
    
    /**
     * 查找最大手术包序号
     * @param packCode String
     * @return TParm
     */
    public TParm getStockSeq(String packCode) {
        if (packCode == null || packCode.length() == 0)
            return null;
        TParm parm = new TParm();
        parm.setData("PACK_CODE", packCode);
        TParm result = query("getStockSeq", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
    /**
     * 查找手术包的消毒日期和效期
     * @param packCode String
     * @param packSeqNo int
     * @return TParm
     */
    public TParm getPackDate(String packCode,int packSeqNo){
        TParm result=new TParm();
        if(packCode==null||packCode.length()==0||packSeqNo<=0)
            return result.newErrParm(-1,"参数不能为空!");
        result.setData("PACK_CODE",packCode);
        result.setData("PACK_SEQ_NO",packSeqNo);
        result = query("getPackDate",result);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
    
    /**
     * 检核手术包是否存在
     * @param packCode String
     * @return TParm
     */
    public TParm checkPackCount(String packCode) {
        if (packCode == null || packCode.length() == 0)
            return null;
        TParm parm = new TParm();
        parm.setData("PACK_CODE", packCode);
        TParm result = query("checkPackCount", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
    /**
     * 新增手术包
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertPack(TParm parm, TConnection connection) {
        TParm result = this.update("insertPack", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
    
    /**
     * 更新手术包库存量
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateQty(TParm parm, TConnection connection) {
        TParm result = this.update("updateQty", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
    
    public TParm queryPackBatch(TParm parm){
    	TParm result = this.query("queryPackBatch", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
    /**
     * 查找手术包的消毒日期和效期（消毒包）
     * @param packCode String
     * @param packSeqNo int
     * @return TParm
     */
    public TParm getPackDateBatch(String packCode,int packSeqNo,int packBatchNo){
        TParm result=new TParm();
        result.setData("PACK_CODE",packCode);
        result.setData("PACK_SEQ_NO",packSeqNo);
        result.setData("PACK_BATCH_NO",packBatchNo);
        result = query("getPackDateBatch",result);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
  
    //------------------供应室新增end-------------------------


}
