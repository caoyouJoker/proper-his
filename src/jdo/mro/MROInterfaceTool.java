package jdo.mro;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import java.sql.Connection;
import com.dongyang.db.TConnection;

/**
 * <p>Title:MRO接口方法(后台Tool，前后台都要部署) </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangk 2009-4-28
 * @version 1.0
 */
public class MROInterfaceTool
    extends TJDODBTool {
    public MROInterfaceTool() {
    }
    /**
    * 实例
    */
   public static MROInterfaceTool instanceObject;

   /**
    * 得到实例
    * @return SYSRegionTool
    */
   public static MROInterfaceTool getInstance() {
       if (instanceObject == null)
           instanceObject = new MROInterfaceTool();
       return instanceObject;
   }
   /**
    * 转床、转出科
    * @param parm TParm  参数信息： CASE_NO,OPT_USER,OPT_TERM为必须参数，其他 诊断参数自选 ==>
    * TRANS_DEPT     转科科别
    * OUT_DATE       出院日期
    * OUT_DEPT       出院科别
    * OUT_STATION    出院病区
    * OUT_ROOM_NO    出院病室
    * REAL_STAY_DAYS 实际住院天数
    * @param conn TConnection
    * @return TParm
    */
   public TParm updateTransDept(TParm parm, TConnection conn) {
       TParm result = new TParm();
       String sql = "UPDATE MRO_RECORD SET ";
       if (parm.getData("TRANS_DEPT") != null) //转科科别
           sql += " TRANS_DEPT='" + parm.getValue("TRANS_DEPT") + "',";
       if (parm.getData("OUT_DATE") != null) //出院日期
           sql += " OUT_DATE=TO_DATE('" + parm.getValue("OUT_DATE") +
               "','YYYYMMDD'),";
       if (parm.getData("OUT_DEPT") != null) //出院科别
           sql += " OUT_DEPT='" + parm.getValue("OUT_DEPT") + "',";
       if (parm.getData("OUT_STATION") != null) //出院病区
           sql += " OUT_STATION='" + parm.getValue("OUT_STATION") + "',";
       if (parm.getData("OUT_ROOM_NO") != null) //出院病室
           sql += " OUT_ROOM_NO='" + parm.getValue("OUT_ROOM_NO") + "',";
       if (parm.getData("REAL_STAY_DAYS") != null) //实际住院天数
           sql += " REAL_STAY_DAYS='" + parm.getValue("REAL_STAY_DAYS") +
               "',";
       sql += " OPT_USER='"+parm.getValue("OPT_USER")+"',OPT_TERM='"+parm.getValue("OPT_TERM")+"',OPT_DATE=SYSDATE ";
       if (parm.getData("CASE_NO") != null)
           sql += " WHERE CASE_NO='" +
               parm.getValue("CASE_NO") + "'";
       result.setData(this.update(sql, conn));
       if (result.getErrCode() < 0) {
           err("jdo.mro.MROInterfaceTool.updateTransDept==>ERR:" +
               result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * 修改诊断信息
    * @param parm TParm 参数信息： CASE_NO,OPT_USER,OPT_TERM为必须参数，其他 诊断参数自选 ==>
    * OE_DIAG_CODE      门急诊诊断
    * IN_DIAG_CODE      入院诊断
    * OUT_DIAG_CODE1    出院主诊断
    * OUT_DIAG_CODE2    出院第二诊断
    * OUT_DIAG_CODE3    出院第三诊断
    * OUT_DIAG_CODE4    出院第四诊断
    * OUT_DIAG_CODE5    出院第五诊断
    * OUT_DIAG_CODE6    出院第六诊断
    * INTE_DIAG_CODE    院内感染诊断
    * @return TParm
    */
   public TParm updateDiag(TParm parm,TConnection conn){
       TParm result = new TParm();
       String sql = "UPDATE MRO_RECORD SET ";
       if(parm.getData("OE_DIAG_CODE")!=null)//门急诊诊断
           sql += " OE_DIAG_CODE = '" + parm.getValue("OE_DIAG_CODE") + "',";
       if(parm.getData("IN_DIAG_CODE")!=null)//入院诊断
           sql += " IN_DIAG_CODE = '" + parm.getValue("IN_DIAG_CODE") + "',";
       if(parm.getData("OUT_DIAG_CODE1")!=null)//出院主诊断
           sql += " OUT_DIAG_CODE1 = '" + parm.getValue("OUT_DIAG_CODE1") + "',";
       if(parm.getData("OUT_DIAG_CODE2")!=null)//出院第二诊断
           sql += " OUT_DIAG_CODE2 = '" + parm.getValue("OUT_DIAG_CODE2") + "',";
       if(parm.getData("OUT_DIAG_CODE3")!=null)//出院第三诊断
           sql += " OUT_DIAG_CODE3 = '" + parm.getValue("OUT_DIAG_CODE3") + "',";
       if(parm.getData("OUT_DIAG_CODE4")!=null)//出院第四诊断
           sql += " OUT_DIAG_CODE4 = '" + parm.getValue("OUT_DIAG_CODE4") + "',";
       if(parm.getData("OUT_DIAG_CODE5")!=null)//出院第五诊断
           sql += " OUT_DIAG_CODE5 = '" + parm.getValue("OUT_DIAG_CODE5") + "',";
       if(parm.getData("OUT_DIAG_CODE6")!=null)//出院第六诊断
           sql += " OUT_DIAG_CODE6 = '" + parm.getValue("OUT_DIAG_CODE6") + "',";
       if(parm.getData("INTE_DIAG_CODE")!=null)//院内感染诊断
           sql += " INTE_DIAG_CODE = '" + parm.getValue("INTE_DIAG_CODE") + "',";

       sql += " OPT_USER='"+parm.getValue("OPT_USER")+"',OPT_TERM='"+parm.getValue("OPT_TERM")+"',OPT_DATE=SYSDATE ";
       sql += "  WHERE CASE_NO = '"+ parm.getValue("CASE_NO") +"'";
       result.setData(this.update(sql,conn));
       if (result.getErrCode() < 0) {
           err("jdo.mro.MROInterfaceTool.updateDiag==>ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * 修改 药品过敏
    * @param parm TParm 参数信息： CASE_NO,ALLEGIC,OPT_USER,OPT_TERM为必须参数
    * @param conn TConnection
    * @return TParm
    */
   public TParm updateALLEGIC(TParm parm,TConnection conn){
       TParm result = new TParm();
       String sql = "UPDATE MRO_RECORD SET ";
       if(parm.getData("ALLEGIC")!=null)//过敏记录
           sql += " ALLEGIC='"+ parm.getValue("ALLEGIC") +"',";

       sql += " OPT_USER='"+parm.getValue("OPT_USER")+"',OPT_TERM='"+parm.getValue("OPT_TERM")+"',OPT_DATE=SYSDATE ";
       sql += " WHERE CASE_NO='"+ parm.getValue("CASE_NO") +"'";
       result.setData(this.update(sql,conn));
       if (result.getErrCode() < 0) {
          err("jdo.mro.MROInterfaceTool.updateALLEGIC==>ERR:" + result.getErrCode() + result.getErrText() +
              result.getErrName());
          return result;
      }
      return result;
   }
   /**
    * 插入 手术信息
    * @param <any> TParm
    * @return TParm
    */
   public TParm insertOP(TParm parm,TConnection conn){
       TParm result;
       result = MRORecordTool.getInstance().insertOP(parm,conn);
       return result;
   }
   /**
    * 修改 手术信息
    * @param parm TParm 参数信息： CASE_NO,SEQ_NO,OPT_USER,OPT_TERM为必须参数
    * @param conn TConnection
    * @return TParm
    */
   public TParm updateOP(TParm parm,TConnection conn){
       TParm result = new TParm();
       if(parm.getData("CASE_NO")==null||parm.getData("SEQ_NO")==null){
           result.setErr(-1,"必要条件参数不存在！");
           return result;
       }
       String sql = "UPDATE MRO_RECORD_OP SET ";
       if(parm.getData("IPD_NO")!=null)//住院号
           sql += " IPD_NO='"+ parm.getValue("IPD_NO") +"',";
       if(parm.getData("MR_NO")!=null)//病历号
           sql += " MR_NO='"+ parm.getValue("MR_NO") +"',";
       if(parm.getData("OP_CODE")!=null)//手术代码
           sql += " OP_CODE='"+ parm.getValue("OP_CODE") +"',";
       if(parm.getData("OP_DESC")!=null)//手术名称
           sql += " OP_DESC='"+ parm.getValue("OP_DESC") +"',";
       if(parm.getData("OP_REMARK")!=null)//手术备注
           sql += " OP_REMARK='"+ parm.getValue("OP_REMARK") +"',";
       if(parm.getData("OP_DATE")!=null)//手术时间
           sql += " OP_DATE=TO_DATE('"+ parm.getValue("OP_DATE") +"','YYYYMMDDHH24MISS'),";
       if(parm.getData("ANA_WAY")!=null)//麻醉方式
           sql += " ANA_WAY='"+ parm.getValue("ANA_WAY") +"',";
       if(parm.getData("ANA_DR")!=null)//麻醉医师
           sql += " ANA_DR='"+ parm.getValue("ANA_DR") +"',";
       if(parm.getData("MAIN_SUGEON")!=null)//术者
           sql += " MAIN_SUGEON='"+ parm.getValue("MAIN_SUGEON") +"',";
       if(parm.getData("AST_DR1")!=null)//助刀一
           sql += " AST_DR1='"+ parm.getValue("AST_DR1") +"',";
       if(parm.getData("AST_DR2")!=null)//助刀二
           sql += " AST_DR2='"+ parm.getValue("AST_DR2") +"',";
       if(parm.getData("HEALTH_LEVEL")!=null)//切口愈合等级
           sql += " HEALTH_LEVEL='"+ parm.getValue("HEALTH_LEVEL") +"',";
       if(parm.getData("OP_LEVEL")!=null)//手术等级
           sql += " OP_LEVEL='"+ parm.getValue("OP_LEVEL") +"',";
       if(parm.getData("OPT_USER")!=null)
           sql += " OPT_USER='"+ parm.getValue("OPT_USER") +"',";
       if(parm.getData("OPT_TERM")!=null)
           sql += " OPT_TERM='"+ parm.getValue("OPT_TERM") +"',";
       sql += " OPT_DATE=SYSDATE "; //修改时间
       sql += " WHERE CASE_NO = '" + parm.getValue("CASE_NO") +
           "' AND SEQ_NO = '" + parm.getValue("SEQ_NO") + "'";
       result.setData(this.update(sql, conn));
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * 修改 输血信息
    * @param parm TParm CASE_NO,OPT_USER,OPT_TERM为必须参数
    * @param conn TConnection
    * @return TParm
    */
   public TParm updateBlood(TParm parm,TConnection conn){
       TParm result = new TParm();
       String sql = "UPDATE MRO_RECORD SET ";
       if(parm.getData("BLOOD_TYPE")!=null)//血型
           sql += " BLOOD_TYPE='"+ parm.getValue("BLOOD_TYPE") +"',";
       if(parm.getData("RH_TYPE")!=null)//RH
           sql += " RH_TYPE='"+ parm.getValue("RH_TYPE") +"',";
       if(parm.getData("TRANS_REACTION")!=null)//输血反应
           sql += " TRANS_REACTION='"+ parm.getValue("TRANS_REACTION") +"',";
       if(parm.getData("RBC")!=null)//红血球
           sql += " RBC='"+ parm.getValue("RBC") +"',";
       if(parm.getData("PLATE")!=null)//血小板
           sql += " PLATE='"+ parm.getValue("PLATE") +"',";
       if(parm.getData("PLASMA")!=null)//血浆
           sql += " PLASMA='"+ parm.getValue("PLASMA") +"',";
       if(parm.getData("WHOLE_BLOOD")!=null)//全血
           sql += " WHOLE_BLOOD='"+ parm.getValue("WHOLE_BLOOD") +"',";
       if(parm.getData("OTH_BLOOD")!=null)//其它血品种类
           sql += " OTH_BLOOD='"+ parm.getValue("OTH_BLOOD") +"',";
       sql += " OPT_USER='"+parm.getValue("OPT_USER")+"',OPT_TERM='"+parm.getValue("OPT_TERM")+"',OPT_DATE=SYSDATE ";
       sql += " WHERE CASE_NO='"+ parm.getValue("CASE_NO") +"'";
       result.setData(this.update(sql, conn));
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * 修改账务信息
    * @param parm TParm  CASE_NO,OPT_USER,OPT_TERM为必须参数  其他参数自选==>
    * CHARGE_01 床位费(住);CHARGE_02 护理费(住);CHARGE_03 西药费(住);CHARGE_04 中成药费(住);CHARGE_05 中草药费(住);
    * CHARGE_06 放射费(住);CHARGE_07 化验费(住);CHARGE_08 输氧费(住);
    * CHARGE_09 输血费(住);CHARGE_10 诊疗费(住);CHARGE_11 手术费(住);
    * CHARGE_12 接生费(住);CHARGE_13 检查费(住);CHARGE_14 家床费(住);
    * CHARGE_15 麻醉费(住);CHARGE_16 婴儿费(住);CHARGE_17 其他;
    * CHARGE_18 （暂无）;CHARGE_19 （暂无）;CHARGE_20 （暂无）; 费用名称是根据 SYS_DICTIONARY 表来的 GROUP_ID='MRO_CHARGE'
    * @param conn TConnection
    * @return TParm
    */
   public TParm updateCharge(TParm parm,TConnection conn){
       TParm result = new TParm();
       String sql = "UPDATE MRO_RECORD SET ";
       if(parm.getData("CHARGE_01")!=null)//床位费(住)
           sql += " CHARGE_01='"+ parm.getValue("CHARGE_01") +"',";
       if(parm.getData("CHARGE_02")!=null)//护理费(住)
           sql += " CHARGE_02='"+ parm.getValue("CHARGE_02") +"',";
       if(parm.getData("CHARGE_03")!=null)//西药费(住)
           sql += " CHARGE_03='"+ parm.getValue("CHARGE_03") +"',";
       if(parm.getData("CHARGE_04")!=null)//中成药费(住)
           sql += " CHARGE_04='"+ parm.getValue("CHARGE_04") +"',";
       if(parm.getData("CHARGE_05")!=null)//中草药费(住)
           sql += " CHARGE_05='"+ parm.getValue("CHARGE_05") +"',";
       if(parm.getData("CHARGE_06")!=null)//放射费(住)
           sql += " CHARGE_06='"+ parm.getValue("CHARGE_06") +"',";
       if(parm.getData("CHARGE_07")!=null)//化验费(住)
           sql += " CHARGE_07='"+ parm.getValue("CHARGE_07") +"',";
       if(parm.getData("CHARGE_08")!=null)//输氧费(住)
           sql += " CHARGE_08='"+ parm.getValue("CHARGE_08") +"',";
       if(parm.getData("CHARGE_09")!=null)//输血费(住)
           sql += " CHARGE_09='"+ parm.getValue("CHARGE_09") +"',";
       if(parm.getData("CHARGE_10")!=null)//诊疗费(住)
           sql += " CHARGE_10='"+ parm.getValue("CHARGE_10") +"',";
       if(parm.getData("CHARGE_11")!=null)//手术费(住)
           sql += " CHARGE_11='"+ parm.getValue("CHARGE_11") +"',";
       if(parm.getData("CHARGE_12")!=null)//接生费(住)
           sql += " CHARGE_12='"+ parm.getValue("CHARGE_12") +"',";
       if(parm.getData("CHARGE_13")!=null)//检查费(住)
           sql += " CHARGE_13='"+ parm.getValue("CHARGE_13") +"',";
       if(parm.getData("CHARGE_14")!=null)//家床费(住)
           sql += " CHARGE_14='"+ parm.getValue("CHARGE_14") +"',";
       if(parm.getData("CHARGE_15")!=null)//麻醉费(住)
           sql += " CHARGE_15='"+ parm.getValue("CHARGE_15") +"',";
       if(parm.getData("CHARGE_16")!=null)//婴儿费(住)
           sql += " CHARGE_16='"+ parm.getValue("CHARGE_16") +"',";
       if(parm.getData("CHARGE_17")!=null)//其他
           sql += " CHARGE_17='"+ parm.getValue("CHARGE_17") +"',";
       if(parm.getData("CHARGE_18")!=null)
           sql += " CHARGE_18='"+ parm.getValue("CHARGE_18") +"',";
       if(parm.getData("CHARGE_19")!=null)
           sql += " CHARGE_19='"+ parm.getValue("CHARGE_19") +"',";
       if(parm.getData("CHARGE_20")!=null)
           sql += " CHARGE_20='"+ parm.getValue("CHARGE_20") +"',";

       sql += " OPT_USER='"+parm.getValue("OPT_USER")+"',OPT_TERM='"+parm.getValue("OPT_TERM")+"',OPT_DATE=SYSDATE ";
       sql += " WHERE CASE_NO='"+ parm.getValue("CASE_NO") +"'";

       result.setData(this.update(sql, conn));
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
}
