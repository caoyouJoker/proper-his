package jdo.inv;

import jdo.inv.InvPackStockDTool;
import jdo.inv.InvPackStockMTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 *
 * <p>Title: 手术包库存处理</p>
 *
 * <p>Description: 手术包库存处理</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:javahis </p>
 *
 * @author fudw 2009-5-6
 * @version 1.0
 */
public class INVPackStockTool extends TJDOTool {
    /**
     * 实例
     */
    public static INVPackStockTool instanceObject;
    /**
     * 得到实例
     * @return INVVerifyinTool
     */
    public static INVPackStockTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INVPackStockTool();
        return instanceObject;
    }

    /**
     * 处理手术包库库存档的保存
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm dealSavePackStock(TParm parm, TConnection connection) {
        TParm result = new TParm();
        //无序号管理的更新手术包主档库存量
        TParm packStockMSaveParm = parm.getParm("PACKMSAVEPARM");
        if (packStockMSaveParm == null)
            return result.newErrParm( -1, "");
        String saveType = packStockMSaveParm.getValue("SAVETYPE");
        //说明是有序号管理的
        if (saveType.equals("I")) {
            //增加手术包库存总量
            result = insertValue(parm, connection);
            if (result == null || result.getErrCode() < 0)
                return result;
        }
        //无序号管理的
        if (saveType.equals("U")) {
            //手术包号
            String packCode = packStockMSaveParm.getValue("PACK_CODE");
            //检核是否已经存在此种手术包
            result = InvPackStockMTool.getInstance().checkPackCount(packCode);    
            if (result == null || result.getErrCode() < 0)
                return result;
            //如果没有则新增                                    
            if (result.getCount() == 0) {

                result = insertValue(parm, connection);
                if (result == null || result.getErrCode() < 0)
                    return result;
            }
            //如果已经存在同样新增（因为增加批次）
            if (result.getCount() > 0) {
            	result = insertValue(parm, connection);
//              result = updateValue(parm, connection);
                if (result == null || result.getErrCode() < 0)
                    return result;
            }
        }
        return result;
    }

    /**
     * 插入新手术包
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertValue(TParm parm, TConnection connection) {
        //无序号管理的更新手术包主档库存量
        TParm packStockMSaveParm = parm.getParm("PACKMSAVEPARM");
        //增加手术包库存总量
        TParm result = InvPackStockMTool.getInstance().insertPack(
                packStockMSaveParm,
                connection);     
        if (result == null || result.getErrCode() < 0)
            return result;
        String packType = packStockMSaveParm.getValue("SAVETYPE");
        
        if(!packType.equals("U")){	//无序号管理
        	//向手术包主历史记录表插入数据
            result = InvPackStockMDHistoryTool.getInstance().onInsertPackMHistory(packStockMSaveParm, connection);
            if (result == null || result.getErrCode() < 0)
                return result;
        }
        
        
        //入库明细
        TParm packStockDSaveParm = parm.getParm("PACKDSAVEPARM");

        if (packStockDSaveParm == null)
            return result.newErrParm( -1, "");
        //明细的个数
        int rowCount = packStockDSaveParm.getCount();
        for (int i = 0; i < rowCount; i++) {
            //插入明细
            result = InvPackStockDTool.getInstance().insertInv(
                    packStockDSaveParm.getRow(i), connection);  
            if (result == null || result.getErrCode() < 0){
            	return result;
            }
            if(!packType.equals("U")){	//无序号管理
            	//向手术包明细历史记录表插入数据
            	result = InvPackStockMDHistoryTool.getInstance().onInsertPackDHistory(packStockDSaveParm.getRow(i), connection);
            	if (result == null || result.getErrCode() < 0){
            		return result;
            	}
            }
        }
        return result;
    }

    /**
     * 更新手术包
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateValue(TParm parm, TConnection connection) {
        //无序号管理的更新手术包主档库存量
        TParm packStockMSaveParm = parm.getParm("PACKMSAVEPARM");
        //增加手术包库存总量
        TParm result = InvPackStockMTool.getInstance().updateQty(
                packStockMSaveParm,
                connection);   //wm2013-06-05			INVPackStockMTool.
        if (result.getErrCode() < 0)
            return result;
        //入库明细
        TParm packStockDSaveParm = parm.getParm("PACKDSAVEPARM");
        if (packStockDSaveParm == null)
            return result.newErrParm( -1, "");      
        //明细的个数
        int rowCount = packStockDSaveParm.getCount();
        for (int i = 0; i < rowCount; i++) {
            //插入明细
            result = InvPackStockDTool.getInstance().updateQty(
                    packStockDSaveParm.getRow(i), connection);   //wm2013-06-05  INVPackStockDTool.
            if (result.getErrCode() < 0)
                return result;
        }
        return result;
    }

//    /**
//     * 手术包出库方法
//     * @param parm TParm
//     * @param connection TConnection
//     * @return TParm
//     * 诊疗包调换出库不扣诊疗包的库存.只扣除明细物资
//       非诊疗包调换出库即扣除手术包数量.也扣除明细
//       如果有序号管理的手术包是可以回收的.
//       没有序号管理的非诊疗包手术包是无法回收的
//     */
//    public TParm outPackSave(TParm parm, TConnection connection) {
//        System.out.println("outPackSave");
//        TParm result = new TParm();
//        //保存方式
//        TParm typeParm = parm.getParm("TYPE");
//        //诊疗包注记
//        String typeFlg = typeParm.getValue("TYPE_FLG");
//        //借用注记
//        String borrowFlg = typeParm.getValue("BORROW_FLG");
//        //确认出库
//        String sure = typeParm.getValue("SURE");
//        //如果确认出库则不处理出库主档
//        if (sure.equals("N")) {
//            //处理出库档
//            String[] str = (String[]) parm.getData("SAVESQL");
//            if (str.length != 0) {
//                int rowCount = str.length;
//                //循环所有sql
//                for (int i = 0; i < rowCount; i++) {
//                    //执行一个sql
//                    result = new TParm(TJDODBTool.getInstance().update(str[i],
//                            connection));
//                    if (result.getErrCode() < 0)
//                        return result;
//                }
//            }
//        }
//
//        //如果是诊疗包
//        System.out.println("dealOutOncePack");
//        if (typeFlg.equals("Y")) {
//            //如果是确认出库直接处理诊疗包明细
//            if (sure.equals("N")) {
//                //如果是借用(借用不扣库)
//                if (borrowFlg.equals("Y"))
//                    //zhangyong20091208 诊疗包借用只扣主项库存
//                    return dealOutOncePack(parm, connection);
//                //return result;
//            }
//            //处理诊疗包调换出库(诊疗包调换出库不扣诊疗包的库存.只扣除明细物资)
//            //非诊疗包调换出库即扣除手术包数量.也扣除明细
//            //如果有序号管理的手术包是可以回收的.
//            //没有序号管理的非诊疗包手术包是无法回收的
//            return dealOutOncePack(parm, connection);
//        }
//        //取出保存全部数据
//        TParm packMParm = parm.getParm("STOCKPARM");
//        //手术包个数
//        int rowCount = packMParm.getCount();
//        //如果没有手术包返回错误信息
//        if (rowCount <= 0)
//            return result.newErrParm( -1, "参数为空");
//        TParm oneRowParm;
//        //循环所有数据
//        for (int i = 0; i < rowCount; i++) {
//            //一行数据
//            oneRowParm = packMParm.getRow(i);
//            //序号管理标记
//            String seqManFlg = oneRowParm.getValue("SEQMANFLG");
//            //有序号管理
//            if (seqManFlg.equals("Y")) {
//                //调用有序号管理出库处理方法
//                result = dealOutSeqManOutPack(oneRowParm, connection);
//                if (result.getErrCode() < 0)
//                    return result;
//            } else { //调用没有序号管理的出库处理方法
//                result = dealOutNoSeqManOutPack(oneRowParm, connection);
//                if (result.getErrCode() < 0)
//                    return result;
//            }
//        }
//        return result;
//    }
//
//    /**
//     * 处理诊疗包出库
//     * @param parm TParm
//     * @param connection TConnection
//     * @return TParm
//     */
//    public TParm dealOutOncePack(TParm parm, TConnection connection) {
//        TParm result = new TParm();
//        //取出保存全部数据
//        TParm packMParm = parm.getParm("STOCKPARM");
//        System.out.println("packMParm---" + packMParm);
//        //手术包个数
//        int rowCount = packMParm.getCount();
//        //如果没有手术包返回错误信息
//        if (rowCount <= 0)
//            return result.newErrParm( -1, "参数为空");
//        TParm oneRowParm;
//        //循环所有数据
//        for (int i = 0; i < rowCount; i++) {
//            //一行数据
//            oneRowParm = packMParm.getRow(i);
//            System.out.println("oneRowParm---" + oneRowParm);
//            //诊疗包出库都没有序号管理
//            result = dealOutTypeOncePack(oneRowParm, connection);
//            if (result.getErrCode() < 0)
//                return result;
//        }
//
//        return result;
//    }
//
//    /**
//     * 处理有序号管理的手术包出库
//     * @param parm TParm
//     * @param connection TConnection
//     * @return TParm
//     */
//    public TParm dealOutSeqManOutPack(TParm parm, TConnection connection) {
//        TParm result = new TParm();
//        if (parm == null)
//            return result.newErrParm( -1, "参数为空");
//        //调用处理手术包库存主档更新在库状态方法
//        result = INVPackStockMTool.getInstance().updatePackStatus(parm,
//                connection);
//        if (result.getErrCode() < 0)
//            return result;
//        //区明细
//        TParm packD = parm.getParm("PACKD");
//        int rowCount = packD.getCount();
//        TParm oneRowParm;
//        for (int i = 0; i < rowCount; i++) {
//            //取出一行数据
//            oneRowParm = packD.getRow(i);
//            //明细中插入一行数据
//            result = INVSupDispenseDDTool.getInstance().insertRow(oneRowParm,
//                    connection);
//            if (result.getErrCode() < 0)
//                return result;
//            String svaeType = oneRowParm.getValue("SAVETYPE");
//            //一次性物资要删除,非一次性物资不做任何修改
//            if (svaeType.equals("D")) {
//                //删除手术包中的一次性物资
//                result = INVPackStockDTool.getInstance().deleteOneRowInv(
//                        oneRowParm,
//                        connection);
//                if (result.getErrCode() < 0)
//                    return result;
//            }
//        }
//        return result;
//    }
//
//    /**
//     * 处理没有序号管理的手术包出库
//     * @param parm TParm
//     * @param connection TConnection
//     * @return TParm
//     */
//    public TParm dealOutNoSeqManOutPack(TParm parm, TConnection connection) {
//        TParm result = new TParm();
//        if (parm == null)
//            return result.newErrParm( -1, "参数为空");
//        //调用处理手术包库存主档更新库存总量的方法
//        parm.setData("QTY", parm.getDouble("QTY") * -1);
//        result = INVPackStockMTool.getInstance().updateQty(parm, connection);
//        if (result.getErrCode() < 0)
//            return result;
//        //区明细
//        TParm packD = parm.getParm("PACKD");
//        int rowCount = packD.getCount();
//        TParm oneRowParm;
//        for (int i = 0; i < rowCount; i++) {
//            //取出一行数据
//            oneRowParm = packD.getRow(i);
//            //出库明细中插入一行数据
//            result = INVSupDispenseDDTool.getInstance().insertRow(oneRowParm,
//                    connection);
//            if (result.getErrCode() < 0)
//                return result;
//            //更新手术包中物资库存
//            oneRowParm.setData("QTY", oneRowParm.getDouble("QTY") * -1);
//            result = INVPackStockDTool.getInstance().updateQty(oneRowParm,
//                    connection);
//            if (result.getErrCode() < 0)
//                return result;
//        }
//
//        return result;
//    }
//
//    //zhangyong20091125 begin
//    /**
//     * 修改：诊疗包出库时不扣库存==》诊疗包出库时扣库存
//     * @param parm TParm
//     * @param connection TConnection
//     * @return TParm
//     */
//    public TParm dealOutTypeOncePack(TParm parm, TConnection connection) {
//        System.out.println("---1----");
//        TParm result = new TParm();
//        if (parm == null)
//            return result.newErrParm( -1, "参数为空");
//        //zhangyong20091125 begin
//        //取主项数据
//        result = INVPackStockMTool.getInstance().onUpdatePackStockMQty(parm,
//                connection);
//        if (result.getErrCode() < 0) {
//            return result;
//        }
//        //zhangyong20091125 end
//
//        //区明细
//        TParm packD = parm.getParm("PACKD");
//        System.out.println("pack---" + packD);
//        int rowCount = packD.getCount();
//        TParm oneRowParm;
//        for (int i = 0; i < rowCount; i++) {
//            //取出一行数据
//            oneRowParm = packD.getRow(i);
//            System.out.println("oneRowParm---"+oneRowParm);
//            //出库明细中插入一行数据
//            result = INVSupDispenseDDTool.getInstance().insertRow(oneRowParm,
//                    connection);
//            if (result.getErrCode() < 0)
//                return result;
//            //更新手术包中物资库存
//            oneRowParm.setData("QTY", oneRowParm.getDouble("QTY") * -1);
//            result = INVPackStockDTool.getInstance().updateQty(oneRowParm,
//                    connection);
//            if (result.getErrCode() < 0)
//                return result;
//        }
//        return result;
//    }
//
//    //zhangyong20091125 end
//
//    /**
//     * 手术包退库方法
//     * @param parm TParm
//     * @param connection TConnection
//     * @return TParm
//     */
//    public TParm saveBackPack(TParm parm, TConnection connection) {
//        TParm result = new TParm();
//        //保存方式
//        TParm typeParm = parm.getParm("TYPE");
//        //诊疗包注记
//        String typeFlg = typeParm.getValue("TYPE_FLG");
//        //借用注记
//        String borrowFlg = typeParm.getValue("BORROW_FLG");
//
//        //退库保存更新退库单的退库标记
//        result = updateDispenMBackFlg(parm, connection);
//        if (result.getErrCode() < 0) {
//            System.out.println("更新出库单退库标记错误:" + result.getErrText());
//            return result;
//        }
//
//        //如果是诊疗包
//        if (typeFlg.equals("Y")) {
//            //如果是借用(因为借用出库时不扣库..雇退库不曾库)
//            if (borrowFlg.equals("Y"))
//                //就不扣除库存.....只写出库单
//                return result;
//        }
//        //取出保存全部数据
//        TParm packMParm = parm.getParm("STOCKPARM");
//        //手术包个数
//        int rowCount = packMParm.getCount();
//        //如果没有手术包返回错误信息
//        if (rowCount <= 0)
//            return result.newErrParm( -1, "参数为空");
//        TParm oneRowParm;
//        //循环所有数据
//        for (int i = 0; i < rowCount; i++) {
//            //一行数据
//            oneRowParm = packMParm.getRow(i);
//            //序号管理标记
//            String seqManFlg = oneRowParm.getValue("SEQMANFLG");
//            //有序号管理
//            if (seqManFlg.equals("Y")) {
//                //调用有序号管理退库处理方法
//                result = dealHaveSeqManPackBackStock(oneRowParm, connection);
//                if (result.getErrCode() < 0)
//                    return result;
//            } else { //调用没有序号管理的出库处理方法
//                result = noSeqManBackStock(oneRowParm, connection);
//                if (result.getErrCode() < 0)
//                    return result;
//            }
//        }
//        return result;
//    }
//
//    /**
//     * 处理有序号管理的手术包退库
//     * @param parm TParm
//     * @param connection TConnection
//     * @return TParm
//     */
//    public TParm dealHaveSeqManPackBackStock(TParm parm, TConnection connection) {
//        TParm result = new TParm();
//        if (parm == null)
//            return result.newErrParm( -1, "参数为空");
//        //调用处理手术包库存主档更新在库状态方法
//        result = INVPackStockMTool.getInstance().updatePackStatus(parm,
//                connection);
//        if (result.getErrCode() < 0)
//            return result;
//        //区明细
//        TParm packD = parm.getParm("PACKD");
//        int rowCount = packD.getCount();
//        TParm oneRowParm;
//        for (int i = 0; i < rowCount; i++) {
//            //取出一行数据
//            oneRowParm = packD.getRow(i);
//            String svaeType = oneRowParm.getValue("SAVETYPE");
//            //一次性物资要回复,非一次性物资不做任何修改
//            if (svaeType.equals("D")) {
////                System.out.println("sssssssssssssss>>>>>>>>>ssss");
//                //如果是出库删除的物资则回复
//                result = INVPackStockDTool.getInstance().insertInvForBackPack(
//                        oneRowParm, connection);
//                if (result.getErrCode() < 0)
//                    return result;
//            }
//        }
//        return result;
//    }
//
//    /**
//     * 处理没有序号管理的手术包退库
//     * @param parm TParm
//     * @param connection TConnection
//     * @return TParm
//     */
//    public TParm noSeqManBackStock(TParm parm, TConnection connection) {
//        TParm result = new TParm();
//        if (parm == null)
//            return result.newErrParm( -1, "参数为空");
//        //调用处理手术包库存主档更新库存总量的方法
////        parm.setData("QTY", parm.getDouble("QTY") * -1);
//        //出库时扣库..退库则曾库
//        parm.setData("QTY", parm.getDouble("QTY"));
//        result = INVPackStockMTool.getInstance().updateQty(parm, connection);
//        if (result.getErrCode() < 0)
//            return result;
//        //区明细
//        TParm packD = parm.getParm("PACKD");
//        int rowCount = packD.getCount();
//        TParm oneRowParm;
//        for (int i = 0; i < rowCount; i++) {
//            //取出一行数据
//            oneRowParm = packD.getRow(i);
//            //更新手术包中物资库存（曾库）
//            oneRowParm.setData("QTY", oneRowParm.getDouble("QTY"));
//            result = INVPackStockDTool.getInstance().updateQty(oneRowParm,
//                    connection);
//            if (result.getErrCode() < 0)
//                return result;
//        }
//
//        return result;
//    }
//
//    /**
//     * 手术包退库时更细出库单退库状态
//     * @param parm TParm
//     * @param connection TConnection
//     * @return TParm
//     */
//    public TParm updateDispenMBackFlg(TParm parm, TConnection connection) {
//        //退库保存更新退库单的退库标记
//        TParm dispenseBackFlgParm = parm.getParm("BACKFLG");
//        String dispenseNo = dispenseBackFlgParm.getValue("DISPENSE_NO");
//        //======================     chenxi =================
//        String requestNo =parm.getValue("REQUEST_NO", 0) ;
//        String sql1 =
//            "DELETE INV_SUP_DISPENSED  WHERE DISPENSE_NO='" +
//            dispenseNo + "'";
//        //执行第二个sql
//     TParm   result1 = new TParm(TJDODBTool.getInstance().update(sql1,
//            connection));
//        if (result1.getErrCode() < 0)
//            return result1;
//        String sql2 = "update INV_SUPREQUESTM set UPDATE_FLG = '0'" +
//        		"  where REQUEST_NO = '"+requestNo+"'" ;
//        TParm   result2 = new TParm(TJDODBTool.getInstance().update(sql2,
//                connection));
//            if (result2.getErrCode() < 0)
//                return result2;
//            String sql3 = "update INV_SUPREQUESTD set UPDATE_FLG = '0'" +
//            		" where REQUEST_NO = '"+requestNo+"'" ;
//            TParm   result3 = new TParm(TJDODBTool.getInstance().update(sql3,
//                    connection));
//                if (result3.getErrCode() < 0)
//                    return result3;
//           String sql4 =  "delete INV_SUP_DISPENSEDD WHERE DISPENSE_NO='" +dispenseNo + "'";
//           TParm result4 = new TParm(TJDODBTool.getInstance().update(sql4)) ;
//           if(result4.getErrCode()<0)
//        	   return result4 ;
//        //=================================   chenxi 
//        String sql =
//                "delete INV_SUP_DISPENSEM WHERE DISPENSE_NO='" +
//                dispenseNo + "'";
//        //执行一个sql
//        TParm result = new TParm(TJDODBTool.getInstance().update(sql,
//                connection));
//        if (result.getErrCode() < 0)
//            System.out.println("" + result.getErrText());
//        return result;
//    }
}
