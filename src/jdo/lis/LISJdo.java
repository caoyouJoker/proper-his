package jdo.lis;

import com.dongyang.jdo.TJDOTool;
import java.sql.Timestamp;
import com.dongyang.data.TParm;
import com.dongyang.util.Log;

public class LISJdo extends TJDOTool{
    /**
     * 实例
     */
    public static LISJdo instanceObject;
    /**
     * 得到实例
     * @return TestJDO
     */
    public static LISJdo getInstance()
    {
        if(instanceObject == null)
            instanceObject = new LISJdo();
        return instanceObject;
    }
    /**
     * 构造器
     */
    public LISJdo()
    {
        setModuleName("lis\\LisTQL.x");
        onInit();
    }
    /**
     * 得到系统时间
     * @return Timestamp 生效日期
     */
    public Timestamp getDate()
    {
        TParm parm = new TParm();
        return getResultTimestamp(query("getDate",parm),"SYSDATE");
    }
    /**
     * 更新LIS新医嘱发送状态
     * @param labnumberNo String 条码号
     * @return boolean
     */
    public synchronized boolean getUpDateNewLabStust(String labnumberNo){
        TParm parm = new TParm();
        parm.setData("LAB_NUMBER",labnumberNo);
        //Log.DEBUG = true;
        TParm action = this.update("getUpDateNewLabStust",parm);
        if(action.getErrCode()<0){
            return false;
        }
        return true;
    }
    /**
     * LIS到检检体核收
     * @param labnumberNo String
     * @return boolean
     */
    public synchronized boolean upDateLisDJ(String labnumberNo){
        TParm parm = new TParm();
        parm.setData("LAB_NUMBER",labnumberNo);
        TParm action = this.update("upDateLisDJ",parm);
        if(action.getErrCode()<0){
           return false;
       }
       return true;
    }
    /**
     * LIS取消检体核收
     * @param labnumberNo String
     * @return boolean
     */
    public synchronized boolean upDateLisQXDJ(String labnumberNo){
        TParm parm = new TParm();
        parm.setData("LAB_NUMBER",labnumberNo);
        TParm action = this.update("upDateLisQXDJ",parm);
        if(action.getErrCode()<0){
           return false;
       }
       return true;
    }
    /**
     * LIS检体拒收
     * @param labnumberNo String
     * @return boolean
     */
    public synchronized boolean upDateLisJSDJ(String labnumberNo){
        TParm parm = new TParm();
        parm.setData("LAB_NUMBER",labnumberNo);
        TParm action = this.update("upDateLisJSDJ",parm);
        if(action.getErrCode()<0){
           return false;
       }
       return true;
    }
    /**
     * 更新护士工作站状态
     * @param labnumberNo String
     * @return TParm
     */
    public synchronized TParm selectNBWStat(String labnumberNo){
        TParm parm = new TParm();
        parm.setData("LAB_NUMBER",labnumberNo);
        TParm action = this.query("selectNBWStat",parm);
        return action;
    }
    /**
     * 审核完成
     * @param labnumberNo String
     * @return boolean
     */
    public synchronized boolean upDateSHWC(String labnumberNo){
        TParm parm = new TParm();
        parm.setData("LAB_NUMBER", labnumberNo);
        TParm action = this.update("upDateSHWC", parm);
        if (action.getErrCode() < 0) {
            return false;
        }
        return true;
    }
    /**
     * 删除临检表数据
     * @param labnumberNo String
     * @return boolean
     */
    public synchronized boolean delLAB_GENRPTDTLData(String labnumberNo){
        TParm parm = new TParm();
        parm.setData("LAB_NUMBER", labnumberNo);
        TParm action = this.update("delLAB_GENRPTDTLData", parm);
        if (action.getErrCode() < 0) {
            return false;
        }
        return true;
    }
    /**
     * 删除微免表的LIS报告数据
     * @param labnumberNo String
     * @return boolean
     */
    public synchronized boolean delLAB_ANTISENSTESTData(String labnumberNo){
        TParm parm = new TParm();
        parm.setData("LAB_NUMBER", labnumberNo);
        TParm action = this.update("delLAB_ANTISENSTESTData", parm);
        if (action.getErrCode() < 0) {
            return false;
        }
        return true;
    }
    /**
     * 删除细菌表的LIS报告数据
     * @param labnumberNo String
     * @return boolean
     */
    public synchronized boolean delLAB_CULRPTDTLData(String labnumberNo){
        TParm parm = new TParm();
        parm.setData("LAB_NUMBER", labnumberNo);
        TParm action = this.update("delLAB_CULRPTDTLData", parm);
        if (action.getErrCode() < 0) {
            return false;
        }
        return true;
    }
    /**
     * 插入临检表数据
     * @param parm TParm
     * @return boolean
     */
    public synchronized boolean insertLAB_GENRPTDTLData(TParm parm){
        TParm action = this.update("insertLAB_GENRPTDTLData",parm);
        if(action.getErrCode()<0){
            return false;
        }
        return true;
    }
    /**
     * 报告完成
     * @return boolean
     */
    public synchronized boolean upDateBGEND(String labNumberNo,String rptDttm,String drUser,String testSeq){
        TParm parm = new TParm();
        parm.setData("LAB_NUMBER",labNumberNo);
        parm.setData("RPT_DTTM",rptDttm);
        parm.setData("DLVRYRPT_USER",drUser);
        parm.setData("TESTSET_SEQ",testSeq);
        TParm action = this.update("upDateBGEND",parm);
        if(action.getErrCode()<0){
            return false;
        }
        return true;
    }
}
