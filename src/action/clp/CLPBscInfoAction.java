package action.clp;

import com.dongyang.action.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.clp.PackTool;
import jdo.clp.BscInfoTool;

import java.io.Serializable;
import java.util.Map;
import com.dongyang.jdo.TJDODBTool;
import jdo.clp.ThrpyschdmTool;

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
public class CLPBscInfoAction  extends TAction {
    public CLPBscInfoAction() {
    }
    /**
     * 删除临床路径类别
     * @param parm
     * @return
     */
    public TParm deleteBscInfo(TParm parm) {
        TParm result = new TParm();
        TConnection connection = getConnection();
        // 删除临床路径类别
        result = BscInfoTool.getInstance().delete(parm, connection);
        if (result.getErrCode() < 0) {
        	connection.rollback();
            connection.close();
            return result;
        }
        //将历史表中的当前使用的版本号的最新数据结束时间修改成当前时间，启用状态改为N==liling
        result = BscInfoTool.getInstance(). updateBscHis(parm, connection);
        if (result.getErrCode() < 0) {
        	connection.rollback();
            connection.close();
            return result;
        }
        //删除诊断
        result=this.deleteICD(parm.getValue("CLNCPATH_CODE"),connection);
        if (result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        // 删除此类别下面的时程
        result = new TParm(TJDODBTool.getInstance().update(
                " DELETE FROM CLP_THRPYSCHDM WHERE REGION_CODE = '" +
                parm.getValue("REGION_CODE") + "' AND CLNCPATH_CODE = '" +
                parm.getValue("CLNCPATH_CODE") + "'", connection));
        if (result.getErrCode() < 0) {
        	connection.rollback();
            connection.close();
            return result;
        }
        // 删除此类别下面的所有明细
        result = new TParm(TJDODBTool.getInstance().update(
                " DELETE FROM CLP_PACK WHERE REGION_CODE = '" +
                parm.getValue("REGION_CODE") + "' AND CLNCPATH_CODE = '" +
                parm.getValue("CLNCPATH_CODE") + "'", connection));
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
      //删除临床路径的时候医嘱历史表不删除，通过修改结束时间截版本==liling
       /* result = new TParm(TJDODBTool.getInstance().update(
                " DELETE FROM CLP_PACK_HISTORY WHERE REGION_CODE = '" +
                parm.getValue("REGION_CODE") + "' AND CLNCPATH_CODE = '" +
                parm.getValue("CLNCPATH_CODE") + "'", connection));*/
        result = new TParm(TJDODBTool.getInstance().update(
                " UPDATE CLP_PACK_HISTORY SET END_DATE= '" +
                parm.getValue("END_DATE") + "' WHERE REGION_CODE = '" +
                parm.getValue("REGION_CODE") + "' AND CLNCPATH_CODE = '" +
                parm.getValue("CLNCPATH_CODE") + "' AND VERSION ='"+ parm.getValue("VERSION") + "' ", connection));
        if (result.getErrCode() < 0) {
        	connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * 删除治疗时程
     * @param parm
     * @return
     */
    public TParm deleteThrpyschdm(TParm parm) {
        TParm result = new TParm();
        TConnection connection = getConnection();
        // 删除治疗时程
        result = ThrpyschdmTool.getInstance().delete(parm, connection);
        if (result.getErrCode() < 0) {
        	connection.rollback();
            connection.close();
            return result;
        }
        // 删除此类别下面的所有明细
        result = new TParm(TJDODBTool.getInstance().update(
                " DELETE FROM CLP_PACK WHERE REGION_CODE = '" +
                parm.getValue("REGION_CODE") + "' AND CLNCPATH_CODE = '" +
                parm.getValue("CLNCPATH_CODE") + "' AND SCHD_CODE = '" +
                parm.getValue("SCHD_CODE") + "'", connection));
        if (result.getErrCode() < 0) {
        	connection.rollback();
            connection.close();
            return result;
        }
        //删除临床路径的时候医嘱历史表不删除，通过修改结束时间截版本==liling
        /*
        result = new TParm(TJDODBTool.getInstance().update(
                " DELETE FROM CLP_PACK_HISTORY WHERE REGION_CODE = '" +
                parm.getValue("REGION_CODE") + "' AND CLNCPATH_CODE = '" +
                parm.getValue("CLNCPATH_CODE") + "' AND SCHD_CODE = '" +
                parm.getValue("SCHD_CODE") + "'", connection));*/
        result = new TParm(TJDODBTool.getInstance().update(
        		" UPDATE CLP_PACK_HISTORY SET END_DATE= '" +
                parm.getValue("END_DATE") + "'  WHERE REGION_CODE = '" +
                parm.getValue("REGION_CODE") + "' AND CLNCPATH_CODE = '" +
                parm.getValue("CLNCPATH_CODE") + "' AND SCHD_CODE = '" +
                parm.getValue("SCHD_CODE") + "' AND VERSION ='"+ parm.getValue("VERSION") + "' ", connection));
        if (result.getErrCode() < 0) {
        	connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * 加入临床路径
     * @param parm TParm
     * @return TParm
     */
    public TParm insertBSCInfo(TParm saveParm) {
        TParm basinfoParm = new TParm((Map) saveParm.getData("bscinfoParm"));
        TParm icdParm = new TParm((Map) saveParm.getData("icdParm"));
        TParm optParm = new TParm((Map) saveParm.getData("optParm"));
        Map basicInfo = (Map) saveParm.getData("basicInfo");
        TParm result = new TParm();
        TConnection connection = getConnection();
        //标准主表
        result = BscInfoTool.getInstance().insert(basinfoParm, connection);
        if (result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        result = insertICD(basicInfo, icdParm, basinfoParm, optParm, connection);
        if (result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        //标准历史表
        result = BscInfoTool.getInstance().insertHis(basinfoParm, connection);
        if (result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
   
    /**
     * 新增诊断
     * @param 
     * @param connection TConnection
     * @return TParm
     */
    private TParm insertICD(Map basicInfo, TParm icdParm, TParm basinfoParm,
                            TParm optParm, TConnection connection) {
        TParm result = new TParm();
        //diagnose
        StringBuffer sqlbf = new StringBuffer("");
        sqlbf.delete(0, sqlbf.length());
        sqlbf.append("SELECT CASE  (COUNT(MAX(SEQ))) WHEN 0 THEN '1' ELSE TO_CHAR((MAX(SEQ))) END AS  SEQ FROM CLP_CLNCPATHERDIAGICD GROUP BY SEQ ");
        result = new TParm(TJDODBTool.getInstance().select(sqlbf.toString()));
        int seq = result.getInt("SEQ", 0);
        for (int i = 0; i < icdParm.getCount("diagnose_icd_begin"); i++) {
            if ("".equals(icdParm.getValue("diagnose_icd_type_begin", i))) {
                continue;
            }
            sqlbf.delete(0, sqlbf.length());
            sqlbf.append("INSERT INTO CLP_CLNCPATHERDIAGICD VALUES( ");
            sqlbf.append("'" + basinfoParm.getValue("CLNCPATH_CODE") + "',");
            sqlbf.append("'" + icdParm.getValue("diagnose_icd_type_begin", i) +
                         "',");
            sqlbf.append("'" + icdParm.getValue("diagnose_icd_begin", i) + "',");
            sqlbf.append("'" + icdParm.getValue("diagnose_icd_type_end", i) +
                         "',");
            sqlbf.append("'" + icdParm.getValue("diagnose_icd_end", i) + "',");
            sqlbf.append((++seq) + ",");
            sqlbf.append("TO_DATE('" + basicInfo.get("OPT_DATE") +
                         "','YYYYMMDD'),");
            sqlbf.append("'" + basicInfo.get("OPT_TERM") + "',");
            sqlbf.append("'" + basicInfo.get("OPT_USER") + "'");
            sqlbf.append(")");
            result = new TParm(TJDODBTool.getInstance().update(sqlbf.toString(),
                    connection));
            if (result.getErrCode() < 0) {
            	connection.rollback();
                connection.close();
                return result;
            }
        }
        //opt diagnose
        sqlbf.delete(0, sqlbf.length());
        sqlbf.append("SELECT CASE  (COUNT(MAX(SEQ))) WHEN 0 THEN '1' ELSE TO_CHAR((MAX(SEQ))) END AS  SEQ FROM CLP_CLNCPATHEROPTICD GROUP BY SEQ");
        result = new TParm(TJDODBTool.getInstance().select(sqlbf.toString()));
        seq = result.getInt("SEQ", 0);
        for (int i = 0; i < optParm.getCount("operator_diagnose_icd_begin"); i++) {
            if ("".equals(optParm.getValue("operator_diagnose_icd_begin", i))) {
                continue;
            }
            sqlbf.delete(0, sqlbf.length());
            sqlbf.append("INSERT INTO CLP_CLNCPATHEROPTICD VALUES( ");
            sqlbf.append("'" + basinfoParm.getValue("CLNCPATH_CODE") + "',");
            sqlbf.append("'" +
                         optParm.getValue("operator_diagnose_icd_begin", i) +
                         "',");
            sqlbf.append("'" + optParm.getValue("operator_diagnose_icd_end", i) +
                         "',");
            sqlbf.append((++seq) + ",");
            sqlbf.append("TO_DATE('" + basicInfo.get("OPT_DATE") +
                         "','YYYYMMDD'),");
            sqlbf.append("'" + basicInfo.get("OPT_TERM") + "',");
            sqlbf.append("'" + basicInfo.get("OPT_USER") + "'");
            sqlbf.append(")");
            result = new TParm(TJDODBTool.getInstance().update(sqlbf.toString(),
                    connection));
            if (result.getErrCode() < 0) {
            	connection.rollback();
                connection.close();
                return result;
            }
        }
        return result;
    }
    /**
     * 更新临床路径
     * @param parm TParm
     * @return TParm
     */
    public TParm updateBSCInfo(TParm saveParm) {
        TParm basinfoParm = new TParm((Map) saveParm.getData("bscinfoParm"));
        TParm icdParm = new TParm((Map) saveParm.getData("icdParm"));
        TParm optParm = new TParm((Map) saveParm.getData("optParm"));
        Map basicInfo = (Map) saveParm.getData("basicInfo");
        TParm result = new TParm();
        TConnection connection = getConnection();
        //标准主表
        result = BscInfoTool.getInstance().update(basinfoParm, connection);
        if (result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        result = this.deleteICD(basinfoParm.getValue("CLNCPATH_CODE"),
                                connection);
        if (result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        result = insertICD(basicInfo, icdParm, basinfoParm, optParm, connection);
        if (result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * 删除诊断
     * @param clncPathCode String
     * @param connection TConnection
     * @return TParm
     */
    private TParm deleteICD(String clncPathCode, TConnection connection) {
        TParm result = new TParm();
        StringBuffer sqlbf = new StringBuffer();
        sqlbf.append("DELETE FROM CLP_CLNCPATHERDIAGICD WHERE CLNCPATH_CODE='" +
                     clncPathCode + "'");
        result = new TParm(TJDODBTool.getInstance().update(sqlbf.toString(),
                connection));
        if (result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        sqlbf.delete(0, sqlbf.length());
        sqlbf.append("DELETE FROM CLP_CLNCPATHEROPTICD WHERE CLNCPATH_CODE='" +
                     clncPathCode + "'");
        result = new TParm(TJDODBTool.getInstance().update(sqlbf.toString(),
                connection));
        if (result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        return result;
    }
    /**
     * 修订
     * liling 20140909 add
     */
   public TParm onRevise(TParm parm){
	   TParm result = new TParm();
       TConnection connection = getConnection();
     //1更新路径使用状态为修订中
       result = new TParm(TJDODBTool.getInstance().update(
    		   "UPDATE CLP_BSCINFO SET CLP_STATUS ='1'  WHERE CLNCPATH_CODE='"+parm.getData("CLNCPATH_CODE")+"' ",connection));
       if (result.getErrCode() < 0) {
          	connection.rollback();
              connection.close();
              return result;
          }
     //将当前使用的临床路径医嘱 添加到历史表,开始时间为当前时间，结束时间为99991231235959
       Map tableMap = (Map) parm.getData("CLP_PACK");
       TParm clpPack =  new TParm(tableMap);
//       TParm clpPack=(TParm) parm.getData("CLP_PACK");// java.util.HashMap cannot be cast to com.dongyang.data.TParm
       for(int i=0;i<clpPack.getCount();i++){
    	   result =  BscInfoTool.getInstance().insertPackHistory(clpPack.getRow(i),connection);
    	   if (result.getErrCode() < 0) {
           	connection.rollback();
            connection.close();
            return result;
           }
		}
     //3修改历史表中当前使用的临床路径医嘱结束时间
       result = new TParm(TJDODBTool.getInstance().update(
    		   "UPDATE CLP_PACK_HISTORY  SET END_DATE ='"+parm.getData("END_DATE")+"'  " +
    		   		"WHERE VERSION='"+parm.getData("VERSION") +"' AND CLNCPATH_CODE='"+parm.getData("CLNCPATH_CODE")+"'  ",connection));
       if (result.getErrCode() < 0) {
       	connection.rollback();
           connection.close();
           return result;
       }
       connection.commit();
       connection.close();
       return result;
   }
   /**
    * 发布
    * liling 20140909 add
    */
   public TParm onRelease(TParm parm){
	   TParm result = new TParm();
       TConnection connection = getConnection();
     //删除当前正在使用的医嘱数据
       result = new TParm(TJDODBTool.getInstance().update(
    		   "DELETE FROM CLP_PACK WHERE CLNCPATH_CODE='"+parm.getData("CLNCPATH_CODE")+"' AND VERSION='"+parm.getData("VERSION") +"' ",connection));
       if (result.getErrCode() < 0) {
          	connection.rollback();
              connection.close();
              return result;
          }
     //将已经修订的历史表医嘱替换正在使用的临床路径项目医嘱 将历史表中版本号码=0的医嘱添加到CLP_ PACK，
       Map tableMap = (Map) parm.getData("CLP_PACK");
       TParm clpPack =  new TParm(tableMap);
//       TParm clpPack=(TParm) parm.getData("CLP_PACK");// java.util.HashMap cannot be cast to com.dongyang.data.TParm
       for(int i=0;i<clpPack.getCount();i++){
    	   clpPack.setData("VERSION", i,parm.getData("VERSION_NEW"));
    	   result =  BscInfoTool.getInstance().insertPack(clpPack.getRow(i),connection);
    	   if (result.getErrCode() < 0) {
           	connection.rollback();
               connection.close();
               return result;
           }
		}
     //同时同步当前版本号码(CLP_PACK当前版本号码+1)parm.getData("VERSION_NEW")
       result = new TParm(TJDODBTool.getInstance().update(
    		   "UPDATE CLP_PACK_HISTORY SET VERSION='"+parm.getData("VERSION_NEW")+"' WHERE CLNCPATH_CODE='"+parm.getData("CLNCPATH_CODE")+"' AND VERSION='0' ",connection));
       if (result.getErrCode() < 0) {
          	connection.rollback();
              connection.close();
              return result;
          }
     //同时同步当前版本号码(CLP_MANAGEM当前版本号码+1)parm.getData("VERSION_NEW")
       result = new TParm(TJDODBTool.getInstance().update(
    		   "UPDATE CLP_MANAGEM SET VERSION='"+parm.getData("VERSION_NEW")+"' WHERE CLNCPATH_CODE='"+parm.getData("CLNCPATH_CODE")+"'  ",connection));
       if (result.getErrCode() < 0) {
          	connection.rollback();
              connection.close();
              return result;
          }
       result = new TParm(TJDODBTool.getInstance().update(
    		   "UPDATE CLP_BSCINFO SET VERSION='"+parm.getData("VERSION_NEW")+"',CLP_STATUS='2' WHERE CLNCPATH_CODE='"+parm.getData("CLNCPATH_CODE")+"'",connection));
       if (result.getErrCode() < 0) {
          	connection.rollback();
              connection.close();
              return result;
          }
       result = new TParm(TJDODBTool.getInstance().update(
    		   "UPDATE CLP_BSCINFO_HISTORY SET VERSION='"+parm.getData("VERSION_NEW")+"' WHERE CLNCPATH_CODE='"+parm.getData("CLNCPATH_CODE")+"' ",connection));
       if (result.getErrCode() < 0) {
          	connection.rollback();
              connection.close();
              return result;
          }
          connection.commit();
          connection.close();
          return result;
      }
   /**
    * 启用
    * liling 20140910 add
    * 再次启用以后，创建一条历史数据，开始时间为当前时间，结束时间为99991231235959
	* CLP_BSCINFO_HISTORY 表将当前使用的临床路径的结束时间修改为当前时间，再创建一条新的数据，版本号码不变
	* UPDATE CLP_BSCINFO_HISTORY  SET END_DATE =XXX WHERE 版本号=XXX AND XXX
    */
   public TParm onUse(TParm parm) {
       TParm result = new TParm();
       TConnection connection = getConnection();
     //标准历史表liling 20140827 add
       result = BscInfoTool.getInstance().insertHis(parm, connection);
       if (result.getErrCode() < 0) {
           connection.rollback();
           connection.close();
           return result;
       }
       //临床路径主表 更新ACTIVE_FLG为Y
       TParm useParm = new TParm((Map) parm.getData("USE_PARM"));
       result = BscInfoTool.getInstance().onUse(useParm,connection);
       if (result.getErrCode() < 0) {
       	connection.rollback();
           connection.close();
           return result;
       }
       connection.commit();
       connection.close();
       return result;
   }
   /**
    * 停用
    * liling 20140910 add
    * 启用勾选去掉，
    */
   public TParm onStop(TParm parm) {
       TParm result = new TParm();
       TConnection connection = getConnection();
       //临床路径主表 更新ACTIVE_FLG为N
       TParm useParm = new TParm((Map) parm.getData("USE_PARM"));
       result = BscInfoTool.getInstance().onUse(useParm,connection);
       if (result.getErrCode() < 0) {
          	connection.rollback();
              connection.close();
              return result;
          }
       //历史表中根据版本号查询数据，结束时间修改为当前时间，  更新ACTIVE_FLG为N？？
       result = BscInfoTool.getInstance(). updateBscHis(parm, connection);
//       result = new TParm(TJDODBTool.getInstance().update(
//    		   "UPDATE CLP_BSCINFO_HISTORY  SET END_DATE ='"+parm.getData("END_DATE")
//    		   +"',ACTIVE_FLG ='N'  WHERE CLNCPATH_CODE='"+parm.getData("CLNCPATH_CODE")+"'  AND VERSION='"+parm.getData("VERSION")
//    		   +"'  AND START_DATE='"+parm.getData("START_DATE")+"' ",connection));
       if (result.getErrCode() < 0) {
       	connection.rollback();
           connection.close();
           return result;
       }
       connection.commit();
       connection.close();
       return result;
   }
    /**
     * 医嘱套餐 保存操作 新增与更新 数据保存到历史表
     * liling 20140909 add
     */
    public TParm savePackHis(TParm parm) {
        TParm result = new TParm();
        TConnection connection = getConnection();
//        System.out.println("action parm++"+parm.getData());
        result = PackTool.getInstance().savePackHis(parm, connection);
        if (result.getErrCode() < 0) {
        	connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * 加入临床路径历史表
     * @param parm TParm
     * @return TParm
     * liling 20140829 add
     */
    public TParm insertBSCInfoHis(TParm parm) {
    	 TParm result = new TParm();
    	  TConnection connection = getConnection();
    	 //标准历史表liling 20140827 add
         result = BscInfoTool.getInstance().insertHis(parm, connection);
         if (result.getErrCode() < 0) {
             connection.rollback();
             connection.close();
             return result;
         }
         connection.commit();
         connection.close();
         return result;
    }
    /**
     * 医嘱套餐 删除操作  从历史表删除数据
     * liling 20140909 add
     */
    public TParm deletePackHis(TParm parm) {
    	TParm result = new TParm();
        TConnection connection = getConnection();
//        System.out.println("action delete++"+parm.getData());
        result = BscInfoTool.getInstance().deletePackHis(parm, connection);
        if (result.getErrCode() < 0) {
        	connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * 医嘱套餐 关键诊疗项目 护理计划
     * @param table TTable 列表
     * @param parm TParm 非列表数据
    
    public TParm savePack(TParm parm) {
        TParm result = new TParm();
        TConnection connection = getConnection();
        // 启用标志 版本
        Map bscInfoMap = (Map) parm.getData("CLP_BSCINFO");
        TParm bscInfoParm = new TParm(bscInfoMap);
        String activeFlg = bscInfoParm.getValue("ACTIVE_FLG");
        // 启用状态为Y时，将医嘱套餐插入历史表，更新当前医嘱套餐（版本号加1），当前临床路径版本号加1且禁用
        if ("Y".equals(activeFlg)) {
            // 2011-07-22 del将医嘱套餐插入历史表 2011-07-22 del
            // 删除时将clppack 对应的临床路径的所有信息全部存入历史表 2011-07-22 luhai 修改
            result = PackTool.getInstance().insertPackHistoryBatch(parm,
                    connection);
            if (result.getErrCode() < 0) {
                connection.close();
                return result;
            }
            // 更新当前医嘱套餐（版本号加1）
            int version = Integer.parseInt(bscInfoParm.getValue("VERSION")) + 1;
            parm.setData("VERSION", version);
            //删除时仅将编辑的时程和orderType 对应的数据删除，其他的不动，但是后期统一更新版本号
            //保证只要标准数据变动，就将一份完整的信息放入历史表中，便于展开时处理
            result = PackTool.getInstance().deletePackBatch(parm, connection);
            if (result.getErrCode() < 0) {
                connection.close();
                return result;
            }
            //插入新数据也是将编辑时程和orderFlg对应的数据插入，其他的orderFlg不进行插入
            result = PackTool.getInstance().insertPackBatch(parm, connection);
            if (result.getErrCode() < 0) {
                connection.close();
                return result;
            }
            // 版本同步升级
            result = PackTool.getInstance().upgradeVersionForPack(parm,
                    connection);
            if (result.getErrCode() < 0) {
                connection.close();
                return result;
            }
            // 当前临床路径版本号加1且禁用
            parm.setData("ACTIVE_FLG", "Y");
            result = BscInfoTool.getInstance().use(parm, connection);
            if (result.getErrCode() < 0) {
                connection.close();
                return result;
            }
        }
        // 启用状态为N时，更新当前医嘱套餐（版本号不变）
        if ("N".equals(activeFlg)) {
            // 更新当前医嘱套餐（版本号不变）
            int version = Integer.parseInt(bscInfoParm.getValue("VERSION"));
            parm.setData("VERSION", version);
            result = PackTool.getInstance().deletePackBatch(parm, connection);
            if (result.getErrCode() < 0) {
                connection.close();
                return result;
            }
            result = PackTool.getInstance().insertPackBatch(parm, connection);
            if (result.getErrCode() < 0) {
                connection.close();
                return result;
            }
        }
        connection.commit();
        connection.close();
        return result;
    } 
     public TParm deletePack(TParm sendParm) {
        TParm parm = new TParm((Map) sendParm.getData("saveMap"));
        // 启用标志 版本
        Map bscInfoMap = (Map) sendParm.getData("CLP_BSCINFO");
        TParm bscInfoParm = new TParm(bscInfoMap);
        TParm result = new TParm();
        TConnection connection = getConnection();
        String activeFlg = bscInfoParm.getValue("ACTIVE_FLG");
        // 启用状态为Y
        if ("Y".equals(activeFlg)) {
            //2011-07-22 加入begin
            // 删除时将clppack 对应的临床路径的所有信息全部存入历史表 2011-07-22 luhai 修改
            result = PackTool.getInstance().insertPackHistoryBatch(sendParm,
                    connection);
            if (result.getErrCode() < 0) {
                connection.close();
                return result;
            }
            // 更新当前医嘱套餐（版本号加1）
            int version = Integer.parseInt(bscInfoParm.getValue("VERSION")) + 1;
            sendParm.setData("VERSION", version);
            // 删除医嘱套餐
            result = PackTool.getInstance().delete(parm, connection);
            if (result.getErrCode() < 0) {
                connection.close();
                return result;
            }
            // 版本同步升级
            result = PackTool.getInstance().upgradeVersionForPack(sendParm,
                    connection);
            if (result.getErrCode() < 0) {
                connection.close();
                return result;
            }
            // 当前临床路径版本号加1且禁用
            sendParm.setData("ACTIVE_FLG", "Y");
            result = BscInfoTool.getInstance().use(sendParm, connection);
            if (result.getErrCode() < 0) {
                connection.close();
                return result;
            }
        } else {
            // 删除医嘱套餐
            result = PackTool.getInstance().delete(parm, connection);
            if (result.getErrCode() < 0) {
                connection.close();
                return result;
            }
        }
        //2011-07-22 加入end
        connection.commit();
        connection.close();
        return result;
    }

    */
}
