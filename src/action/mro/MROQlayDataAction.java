package action.mro;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.mro.MROQlayControlMTool;
import jdo.mro.MROChrtvetrecTool;
import com.dongyang.data.TNull;
import com.sun.jmx.snmp.Timestamp;
import jdo.mro.MRORecordTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;
import jdo.sys.SystemTool;
import jdo.mro.MROChrtvetstdTool;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class MROQlayDataAction extends TAction {
    /**
     * 临时分数，累计查找一个项目的分数是否超出默认分数，如果超出将使用默认分值
     */
    private double [] priceTemp=null;
    
    public MROQlayDataAction() {
    }

    /**
     * 修改保存方法
     * @param parm TParm
     * @return TParm
     */
    public TParm saveQlayControlm(TParm parms) {
        TConnection conn = getConnection();
        double sumPrice = 0.0;//总分
        TParm result = null;
        TParm parmC = null; //扣分表数据
        TParm parm=parms.getParm("SCODEPARM");//记录病患的所有质控信息
        TParm selPrice=parms.getParm("selPrice");//记录每个分支的总分
        priceTemp= new double[selPrice.getCount("DESCRIPTION")];
        String type=parms.getValue("TYPE");//提交数据
        for (int i = 0; i < parm.getCount(); i++) {
            if (parm.getValue("ISTRUE",i).equals("1")) {//修改数据(ISTRUE=1)
                //扣分条件：已查状态，未通过的项目扣分
                if ("1".equals(parm.getValue("QUERYSTATUS",i)) &&
                    "0".equals(parm.getValue("STATUS",i))) {
                    priceStat(selPrice,parm.getValue("EXAMINE_CODE",i),parm,i);
                }
                //执行修改
                result = MROQlayControlMTool.getInstance().onUpdate(parm.getRow(i), conn);//更新MRO_QLAYCONTROLM表
                if (result.getErrCode() < 0) {
                    conn.close();
                    result.setErrText("质控记录主档记录错误！");
                    return result;
                }
                parmC = saveMROChrtvetrec(parm, i);
                result = MROChrtvetrecTool.getInstance().updatedata(parmC, conn); //修改扣分表数据:记录人工质控项目中多项的累计分数
                if (result.getErrCode() < 0) {
                    conn.close();
                    result.setErrText("记录病案审核档记录错误！");
                    return result;
                }
            } else { //添加数据(ISTRUE=0)
                //扣分条件：已查状态，未通过的项目扣分
                if ("1".equals(parm.getValue("QUERYSTATUS",i))) {//modify  by wanglong 20121106
                    priceStat(selPrice,parm.getValue("EXAMINE_CODE",i),parm,i);
                    result = MROQlayControlMTool.getInstance().onInsert(parm.getRow(i), conn);//新增MRO_QLAYCONTROLM表
                    if (result.getErrCode() < 0) {
                        conn.close();
                        result.setErrText("质控记录主档记录错误！");
                        return result;
                    }
                    parmC = saveMROChrtvetrec(parm, i);
                    result = MROChrtvetrecTool.getInstance().insertdata(parmC, conn);//新增扣分表数据
                    if (result.getErrCode() < 0) {
                        conn.close();
                        result.setErrText("记录病案审核档记录错误！");
                        return result;
                    }
                }
            }
        }
        conn.commit();
        conn.close();
        conn=getConnection();
        //修改病患质控分数，以及完成状态
        for(int i=0;i<priceTemp.length;i++){
            int price=selPrice.getInt("DESCRIPTION",i);
            if (price <= priceTemp[i]) { //分数超过默认最大分数
                sumPrice += price;
            } else {
                sumPrice += priceTemp[i];
            }
        }
        TParm parmR=new TParm();
        parmR.setData("MR_NO",parm.getValue("MR_NO",0));
        parmR.setData("CASE_NO",parm.getValue("CASE_NO",0));
        parmR.setData("IPD_NO",parm.getValue("IPD_NO",0));
        parmR.setData("TYPERESULT",type);
        parmR.setData("SUMSCODE", 100 - sumPrice);
   
        //提交完成最终修改
        if ("0".equals(type)) {//未完成
            // ==============add by wanglong 20131025
            parmR.setData("MRO_CHAT_FLG", "1");
            result = MROChrtvetrecTool.getInstance().updateMRO_CHAT_FLG(parmR, conn);
            if (result.getErrCode() < 0) {
                conn.close();
                result.setErrText("修改提交状态错误！");
                return result;
            }
            // ==============add end
            result = MRORecordTool.getInstance().updateTYPERESULT(parmR, conn); //修改分数和完成状态
            if (result.getErrCode() < 0 || !"".equals(result.getErrText())) {
                conn.close();
                result.setErrText("修改数据错误！");
                return result;
            }
        } else {// 已完成
            // ==============add by wanglong 20131025
            parmR.setData("MRO_CHAT_FLG", "2");
            result = MROChrtvetrecTool.getInstance().updateMRO_CHAT_FLG(parmR, conn);
            if (result.getErrCode() < 0) {
                conn.close();
                result.setErrText("修改提交状态错误！");
                return result;
            }
            // ==============add end
            result = MRORecordTool.getInstance().updateTYPERESULT(parmR, conn); //修改分数和完成状态
            if (result.getErrCode() < 0 || !"".equals(result.getErrText())) {
                conn.close();
                result.setErrText("修改数据错误！");
                return result;
            }
        }
        conn.commit();
        conn.close();
        result.setData("RESULTPRICE", 100 - sumPrice);
        result.setData("TYPERESULT", type);
        return result;
    }
    
    /**
     * 累计每一项的分值
     * @param parmNode TParm 子节点数据
     * @param EXAMINE_CODE String 当前的子项目id号
     * @param parm TParm //所有数据
     */
    private void priceStat(TParm selPrice,String EXAMINE_CODE,TParm parm,int index){
       for(int i=0;i<selPrice.getCount("CATEGORY_CODE");i++){
           if(EXAMINE_CODE.substring(0,3).equals(selPrice.getValue("CATEGORY_CODE",i))){
               priceTemp[i]+=parm.getDouble("SCORE", index)*parm.getInt("DEDUCT_SCORECOUNT", index);
               break;
           }
       }
    }
    
    /**
     * 记录病案审核档 参数添加
     * @param parm TParm
     * @param i int
     * @return TParm
     */
    private TParm saveMROChrtvetrec(TParm parm, int i) {
        TParm parmC = new TParm();
        parmC.setData("CASE_NO", parm.getValue("CASE_NO", i));
        parmC.setData("OLD_EXAMINE_CODE", parm.getValue("EXAMINE_CODE", i));
        parmC.setData("EXAMINE_CODE", parm.getValue("EXAMINE_CODE", i));
        parmC.setData("OLD_EXAMINE_DATE", parm.getValue("EXAMINE_DATE", i).substring(0, 8));
        String EXAMINE_DATE=StringTool.getString(SystemTool.getInstance().getDate(), "yyyyMMddHHmmss");
        parmC.setData("EXAMINE_DATE", EXAMINE_DATE.substring(0, 8));
        parmC.setData("IPD_NO", parm.getValue("IPD_NO", i));
        parmC.setData("MR_NO", parm.getValue("MR_NO", i));
        //parmC.setData("VS_CODE", parm.getValue("OPT_USER", i));
        parmC.setData("VS_CODE", parm.getValue("VS_CODE", i));//经治医生 modify by wanglong 20121105
        //parmC.setData("DEDUCT_SCORE", parm.getValue("SCORE", i));
        parmC.setData("DEDUCT_SCORE", Double.parseDouble(parm.getValue("SCORE", i)));//modify by wanglong 20121105
        parmC.setData("DEDUCT_NOTE", "");
        parmC.setData("URG_FLG", parm.getValue("URG_FLG", i));
        if (parm.getData("REPLY_DTTM", i) == null || parm.getValue("REPLY_DTTM", i).equals("")) {
            parmC.setData("REPLY_DTTM", "");// modify by wanglong 20121111
        } else {
            parmC.setData("REPLY_DTTM", parm.getData("REPLY_DTTM", i));
        }
        parmC.setData("REPLY_DR_CODE", parm.getData("REPLY_DR_CODE", i));
        parmC.setData("REPLY_REMK", parm.getData("REPLY_REMK", i)); 
        parmC.setData("OPT_USER", parm.getValue("OPT_USER", i));
        //parmC.setData("DEDUCT_SCORECOUNT", parm.getValue("DEDUCT_SCORECOUNT", i));
        parmC.setData("DEDUCT_SCORECOUNT", Integer.parseInt(parm.getValue("DEDUCT_SCORECOUNT", i)));//modify by wanglong 20121105
        parmC.setData("OPT_TERM", parm.getValue("OPT_TERM", i));
        return parmC;
    }
}
