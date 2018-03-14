package jdo.pha;

import java.sql.*;
import com.dongyang.jdo.TJDOObject;
import java.util.Vector;
import jdo.opd.OrderList;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.data.TModifiedList;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.Operator;
import jdo.opd.Order;



/**
 *
 * <p>Title: 门诊药房主结构</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS (c)</p>
 *
 * @author ZangJH 2008.09.26
 *
 * @version 1.0
 */

public class Pha
    extends TJDOObject {

    /**
     * 所在界面的标记：审配发退
     */
    private String type = "";
    /**
     * 是否是取消退药界面
     */
    private boolean isUnDisp=false;
    /**
     * 所在界面的状态：Y完成/N未完成
     */
    private String finishFlg = "";

    /**
     *当前选中的某个OrderList的行号
     */
    private int ordListRow = -1;

    /**
     * 存放OrderList(可以不分等级的存放)
     */
    private Vector orderListVector = new Vector();

    /**
     * OrderList对象，管理所有的Order(当前的OrderList)
     */
    private OrderList nowOrdList = new OrderList();

    private PhaUtil phaUtil = new PhaUtil();

    /**
     * 初始化
     */
    public Pha() {

    }

    /**
     * 利用parm初始化pha
     * @param parm TParm
     * @return boolean 真：成功，假：失败
     */
    public boolean initParm(TParm parm) {

        if (parm == null)
            return false;

        //调用管理类的初始化
        if (parm.existData("LIST")) {
            orderListVector = phaUtil.initParm(parm);
        }

        return true;
    }

    /**
     * 根据动态where 条件查询Pha，参数：DR_CODE,DEPT_CODE,DSCHECK_DATE,DSDGT_DATE,DSDLVRY_Date,DSRETURN_DATE,RX_NO,CASE_NO,MR_NO,SEQ_NO
     * @param parm
     * @return Pha
     */
    public static Pha onQueryByTParm(TParm parm) {
        //调用PHAAction查询
        TParm result = TIOM_AppServer.executeAction("action.pha.PHAAction",
            "onQuery", parm);
        if (result.getErrCode() < 0) {
            return null;
        }
        Pha pha = new Pha();
        //初始化pha
        if (!pha.initParm(result)) {
            return null;
        }
        return pha;
    }


    /**
     * 保存
     * @return TParm
     */
    public TParm onSave() {

        //创建一个TParm，用于从jdo传到action层的传输对象
        TParm saveDate = new TParm();

        //得到当前选中的orderList
        nowOrdList = phaUtil.getCertainOrdListByRow(this.getOrdListRow(),
            orderListVector);

        //传入执行方法，向Parm中EXE_FLG为真的数据写入对应于界面的数据
        this.onExecute(nowOrdList);
        String viewFlg = this.getType();
        //当在退药界面的时候会产生一个orderHistoryList（其他界面不会）
        if (viewFlg.equals("Return")) {
            //获得当前orderList的parm
            TParm parmOrd = nowOrdList.getParm();
            //获得当前orderHistoryList的parm
            TParm parmHis = phaUtil.orderParmTransPhaHistoryParm(parmOrd.
                getParm(
                    TModifiedList.MODIFIED));

            saveDate.setData("ORDER", parmOrd.getData());
            //只有当“未完成”状态的时候保存才向HISTORY中插入数据
            if(!"Y".equals(getFinishFlag()))
                saveDate.setData("HISTORY", parmHis.getData());

        }
        else {
            //获得当前orderList的parm
            TParm parm = nowOrdList.getParm();

            //注意：通过parm.getData()，把一个TParm对象衣服脱掉，成为一个MAP对象，然后再把这个map对象set进另一个TParm中（传到action中）
            saveDate.setData("ORDER", parm.getData());
            //是否是取消配药（扣库反操作标记==退药）--->配药界面且已完成被选中
            if("Dispense".equals(getType())&&"Y".equals(getFinishFlag()))
                this.setIsUnDisp(true);
            saveDate.setData("ISUNDISP_FLG",this.isIsUnDisp());

        }

        //是否需要调用库存接口
        if("Examine".equals(getType())||"Send".equals(getType()))
            //审核、发药不需要调用库存接口
            saveDate.setData("forIND",false);
        else//配药、退药需要调用库存接口
            saveDate.setData("forIND",true);

        saveDate.setData("finishFlag",getFinishFlag());
        //调用后台的action
        TParm result = TIOM_AppServer.executeAction("action.pha.PHAAction",
            "onSave", saveDate);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }


    /**
     * 在onSave前调用的一个方法，向每个order(的TParm)中写对应的‘审配发退’时间
     * ordListParm 需要保存饿orderList的TParm
     * @param String 所在该界面的标记
     */

    public void onExecute(OrderList ordList) {

        //获得系统当前时间
        Timestamp optTime = TJDODBTool.getInstance().getDBTime();
        String optUser = Operator.getID();
        //获得当前页面的类型：审配发退
        String viewFlg = this.getType();
        //得到order条数
        int count = ordList.size();
        //修改该order的审核时间
        if (viewFlg.equals("Examine")) {
            //遍历每一个order
            for (int i = 0; i < count; i++) {
                Order ord = ordList.getOrder(i);
                //依次取出该orderList的每一个order的执行标记,如果执行的标记有“√”
                if (ord.getExeFlg()) {
                    //未完成的时候加上时间和人员
                    if (finishFlg.equals("N")) {
                        ord.modifyPhaCheckCode(optUser);
                        ord.modifyPhaCheckDate(optTime);
                    }
                    else { //完成的时候清除时间和人员
                        ord.modifyPhaCheckCode(null);
                        ord.modifyPhaCheckDate(null);
                    }
                }
            }
        }
        //修改该order的配药时间
        if (viewFlg.equals("Dispense")) {
            //遍历每一个order
            for (int i = 0; i < count; i++) {
                Order ord = ordList.getOrder(i);
                //依次取出该orderList的每一个order,如果执行的标记有“√”
                if (ord.getExeFlg()) {
                    if (finishFlg.equals("N")) {
                        ord.modifyPhaDosageCode(optUser);
                        ord.modifyPhaDosageDate(optTime);
                        ord.modifyExecDrCode(optUser);
                    }
                    else { //完成的时候清除时间和人员
                        ord.modifyPhaDosageCode(null);
                        ord.modifyPhaDosageDate(null);
                        ord.modifyExecDrCode(optUser);
                    }
                }
            }
        }
        //修改该order的发药时间
        if (viewFlg.equals("Send")) {
            //遍历每一个order
            for (int i = 0; i < count; i++) {
                Order ord = ordList.getOrder(i);
                //依次取出该orderList的每一个order,如果执行的标记有“√”
                if (ord.getExeFlg()) {
                    if (finishFlg.equals("N")) {
                        ord.modifyPhaDispenseCode(optUser);
                        ord.modifyPhaDispenseDate(optTime);
                        ord.modifyExecFlg("Y");
                    }
                    else { //完成的时候清除时间和人员
                        ord.modifyPhaDispenseCode(null);
                        ord.modifyPhaDispenseDate(null);
                        ord.modifyExecFlg("N");
                    }
                }
            }
        }
        //退药的时候不清空DSCHECK_DATE DSDGT_DATE DSDLVRY_DATE，并且要在历史档中插入数据
        if (viewFlg.equals("Return")) {
            for (int i = 0; i < count; i++) {
                Order ord = ordList.getOrder(i);
                //依次取出该orderList的每一个order,如果执行的标记有“√”
                if (ord.getExeFlg()) {
                    if (finishFlg.equals("N")) {
                        ord.modifyPhaRetnCode(optUser);
                        ord.modifyPhaRetnDate(optTime);
                        ord.modifyExecFlg("N");
                    }
                    else { //完成的时候清除时间和人员
                        ord.modifyPhaRetnCode(null);
                        ord.modifyPhaRetnDate(null);
                        ord.modifyExecFlg("Y");
                    }
                    //然后再清空审配发的时间（不需要清空OPD_ORDER中的审，配，发资料）
//                    ord.modifyPhaCheckCode(null);
//                    ord.modifyPhaCheckDate(null);
//                    ord.modifyPhaDosageCode(null);
//                    ord.modifyPhaDosageDate(null);
//                    ord.modifyPhaDispenseCode(null);
//                    ord.modifyPhaDispenseDate(null);
                }
            }
        }
    }

    //得到被选择的那个orderList
    public OrderList getCertainOrdListByRow(int row) {

        OrderList ordList = new OrderList();
        ordList = phaUtil.getCertainOrdListByRow(row, orderListVector);
        return ordList;

    }

    public TParm getAllOrderListParm() {

        TParm result = new TParm();
        result = phaUtil.getAllOrderListParm(orderListVector);
        return result;
    }


    /**
     * 得到全部数据
     * 注意：一个大的TParm里面要装着3个小parm（newParm，modifiedParm，deletedParm），然后传给后台action
     * @return TParm
     */
    public TParm getParm() {
        TParm result = new TParm();
        if (orderListVector == null || orderListVector.size() == 0)
            return result;
        int count = phaUtil.getOrderListCount(orderListVector);
        //规则：3个小parm
        TParm newParm, modifiedParm, deletedParm;
        newParm = new TParm();
        modifiedParm = new TParm();
        deletedParm = new TParm();
        //加载医嘱数据
        for (int i = 0; i < count; i++) {
            OrderList ol = phaUtil.getCertainOrdListByRow(i, orderListVector);

            newParm = new TParm();
            //装载新增医嘱
            ol.getParm(OrderList.NEW, newParm);

            modifiedParm = new TParm();
            //装载修改医嘱
            ol.getParm(OrderList.MODIFIED, modifiedParm);

            deletedParm = new TParm();
            //装载删除医嘱
            ol.getParm(OrderList.DELETED, deletedParm);

        }
        newParm.setData("ACTION", "COUNT", newParm.getCount("CASE_NO"));
        modifiedParm.setData("ACTION", "COUNT", modifiedParm.getCount("CASE_NO"));
        deletedParm.setData("ACTION", "COUNT", deletedParm.getCount("CASE_NO"));
        //组合完整数据包
        result.setData(OrderList.NEW, newParm.getData());
        result.setData(OrderList.MODIFIED, modifiedParm.getData());
        result.setData(OrderList.DELETED, deletedParm.getData());
        result.setData("ACTION", "COUNT", count);
        return result;
    }


    /**
     * 界面属性标记
     */
    public void setType(String pageType) {
        //设置当前页面的类型
        this.type = pageType;
    }

    public String getType() {
        //返回当前页面的类型
        return this.type;
    }

    /**
     * 界面查询状态标记
     */
    public void setFinishFlag(String finishFlg) {
        //设置当前页面的查询状态标记
        this.finishFlg = finishFlg;
    }

    public String getFinishFlag() {
        //返回当前页面的查询状态标记
        return this.finishFlg;
    }

    /**
     * 当前选中的某个orderList
     */
    public void setOrdListRow(int row) {
        //设置当前选中的某个orderList行号
        this.ordListRow = row;
    }

    public void setIsUnDisp(boolean isUnDisp) {
        this.isUnDisp = isUnDisp;
    }

    public int getOrdListRow() {
        //返回当前选中的某个orderList行号
        return this.ordListRow;
    }

    public boolean isIsUnDisp() {
        return isUnDisp;
    }


}
