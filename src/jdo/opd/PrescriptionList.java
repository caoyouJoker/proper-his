package jdo.opd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import jdo.reg.Reg;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import jdo.opb.OPBReceipt;

/**
 *
 * <p>
 * Title: 处方list
 * </p>
 *
 * <p>
 * Description:处方list
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company:Javahis
 * </p>
 *
 * @author ehui 200800911
 * @version 1.0
 */
public class PrescriptionList {
    /**
     * 处方
     */
    private Map prescription = new HashMap();

    /**
     * ODO对象
     */
    private ODO odo;
    /**
     * 挂号对象
     */
    private Reg reg;
    /**
     * 病患对象
     */
    /**
     * 设置挂号对象
     * @param reg Reg
     */
    public void setReg(Reg reg){
    	this.reg=reg;
    }
    /**
     * 得到挂号对象
     * @return Reg
     */
    public Reg getReg(){
    	return this.reg;
    }
    /**
     *
     */
    public PrescriptionList(){


    }
    public PrescriptionList(ODO odo){
    	setODO(odo);
    }
    public void setODO(ODO odo){
    	this.odo=odo;
    }
    public ODO getODO(){
    	return this.odo;
    }

    /**
     *
     * @param groupName String
     * @return Vector按照区名取Vector
     */
    public Vector getGroup(String groupName) {
        Vector data = (Vector) prescription.get(groupName);

        if (data == null) {

            data = new Vector();
            prescription.put(groupName, data);
        }
        return data;

    }

    /**
     * 组的个数
     * @return int
     */
    public int getGroupSize() {
        return prescription.size();
    }

    /**
     * 返回组名
     * @return String[]
     */

    public String[] getGroupNames() {
        String[] names = new String[getGroupSize()];
        Iterator it = prescription.keySet().iterator();
        int index = 0;
        while (it.hasNext()) {
            names[index] = (String) it.next();
            index++;
        }
        return names;
    }

    public boolean isExistGroup(String groupName) {
        if (groupName == null || groupName.trim().length() == 0)
            return false;
        return prescription.get(groupName) != null;
    }

    /**
     * 返回指定组的长度
     * @param groupName String
     * @return int
     */
    public int getGroupPrsptSize(String groupName) {
        if (!isExistGroup(groupName))
            return 0;
        return getGroup(groupName).size();
    }

    /**
     * 返回特定组的指定行的医嘱
     * @param groupName String
     * @param index int
     * @return OrderList
     */
    public OrderList getOrderList(String groupName, int index) {
        if (!isExistGroup(groupName))
            return null;
        if (index < 0 || index >= getGroupPrsptSize(groupName))
            return null;
        OrderList ol=(OrderList) getGroup(groupName).get(index);
        ol.setPat(odo.getPat());
        return ol;
    }
    /**
     * 返回特定组的指定行的医嘱opb用
     * @param groupName String
     * @param index int
     * @return OrderList
     */
    public OrderList getOrderListOob(String groupName, int index) {
       if (!isExistGroup(groupName))
           return null;
       if (index < 0 || index >= getGroupPrsptSize(groupName))
           return null;
       OrderList ol=(OrderList) getGroup(groupName).get(index);
       ol.setPat(reg.getPat());
       return ol;
   }


   /**
    * 初始化List
     * 要求：parm中只有一列名为“GROUP”的四行数据
    * @param parm TParm
    * @return 真：成功，假：失败
    */
   public boolean initParm(TParm parm) {
        if (parm == null)
            return false;
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            TParm group = new TParm();

            group.setData( (Map) parm.getData("GROUP", i));

            if (!initGroup(group))
                return false;
        }
        return true;
    }
    /**
     * 装载组信息
     * @param parm TParm
     * @return boolean
     */
    private boolean initGroup(TParm parm) {
        String groupName = parm.getValue("NAME");
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            TParm list = new TParm();
            list.setData( (Map) parm.getData("LIST", i));
            OrderList orderlist = new OrderList(this);
            if (!orderlist.initParm(list))
                return false;
            getGroup(groupName).add(orderlist);
        }
        return true;
    }
    /**
     * 得到数据
     * @return TParm
     */
    public TParm getParmShow()
    {
        return getParmShow(new TParm());
    }
    /**
     * 得到数据
     * @param result TParm
     * @return TParm
     */
    public TParm getParmShow(TParm result)
    {
        return getParmShow(OrderList.PRIMARY,result);
    }
    /**
     * 得到数据
     * @param storeName String 缓冲区
     * @param result TParm
     * @return TParm
     */
    public TParm getParmShow(String storeName, TParm result)
    {
        //得到全部组名
        String groupNames[] = getGroupNames();
        for(int i = 0;i < groupNames.length;i++)
        {
            //得到处方签列表
            getParmShow(groupNames[i],storeName,result);
        }
        return result;
    }
    /**
     * 得到数据
     * @param groupName String 处方签组
     * @return TParm
     */
    public TParm getParmShow(String groupName)
    {
        return getParmShow(groupName,OrderList.PRIMARY);
    }
    /**
     * 得到数据
     * @param groupName String 处方签组
     * @param storeName String 缓冲区
     * @return TParm
     */
    public TParm getParmShow(String groupName,String storeName)
    {
        return getParmShow(groupName,storeName,new TParm());
    }
    /**
     * 得到数据
     * @param groupName String 处方签组
     * @param storeName String 缓冲区
     * @param result TParm 数据
     * @return TParm
     */
    public TParm getParmShow(String groupName,String storeName, TParm result)
    {
        Vector group = getGroup(groupName);
        for(int j = 0;j < group.size();j++)
        {
            //得到单一处方签
            OrderList orderlist = (OrderList)group.get(j);
            orderlist.getParm(storeName,result,true);
        }
        result.setData("ACTION","COUNT",result.getCount("CASE_NO"));
        return result;
    }
    public List getOrder()
    {
        List list = new ArrayList();
        getOrder(list);
        return list;
    }
    public void getOrder(List list)
    {
        //得到全部组名
        String groupNames[] = getGroupNames();
        for(int i = 0;i < groupNames.length;i++)
        {
            //得到处方签列表
            getOrder(groupNames[i],list);
        }
    }
    public void getOrder(String groupName,List list)
    {
        Vector group = getGroup(groupName);
        for(int i = 0;i < group.size();i++)
        {
            //得到单一处方签
            OrderList orderlist = (OrderList)group.get(i);
            for(int j = 0;j < orderlist.size();j++)
                list.add(orderlist.get(j));
        }
    }
    public TParm getParm() {
        TParm parm = new TParm();
        TParm newParm = new TParm();
        TParm modifiedParm = new TParm();
        TParm deletedParm = new TParm();
        //得到全部组名
        String groupNames[] = getGroupNames();
        for(int i = 0;i < groupNames.length;i++)
        {
            //得到处方签列表
            Vector group = getGroup(groupNames[i]);
            for(int j = 0;j < group.size();j++)
            {
                //得到单一处方签
                OrderList orderlist = (OrderList)group.get(j);
                //装载新增医嘱
                orderlist.getParm(OrderList.NEW,newParm);
                //装载修改医嘱
                orderlist.getParm(OrderList.MODIFIED,modifiedParm);
                //装载删除医嘱
                orderlist.getParm(OrderList.DELETED,deletedParm);
            }
        }
        newParm.setData("ACTION","COUNT",newParm.getCount("CASE_NO"));
        modifiedParm.setData("ACTION","COUNT",modifiedParm.getCount("CASE_NO"));
        deletedParm.setData("ACTION","COUNT",deletedParm.getCount("CASE_NO"));
        //组合完整数据包
        parm.setData(OrderList.NEW,newParm.getData());
        parm.setData(OrderList.MODIFIED,modifiedParm.getData());
        parm.setData(OrderList.DELETED,deletedParm.getData());
        return parm;
    }
    /**
     * 新建处方
     * @param groupName String
     * @return OrderList
     */
    public OrderList newOrderList(String groupName){
    	Vector v=this.getGroup(groupName);
    	OrderList ol=new OrderList(this);
    	OrderList oldol=this.getOrderList(groupName, this.getGroupPrsptSize(groupName)-1);
    	//System.out.println("prescriptionlist---------------"+(this.getGroupPrsptSize(groupName)-1));
    	if(oldol==null){
    		ol.setPresrtNo(1);
    	}else{
    		ol.setPresrtNo(this.getGroupPrsptSize(groupName)+1);
    	}
    	String rxNo=SystemTool.getInstance().getNo("ALL", "ODO", "RX_NO", "RX_NO");
    	if(rxNo.length()==0)
    	{
    		//err("取处方号失败");
    		//System.out.println("取处方号失败");
    		return null;
    	}
    	ol.setPat(reg.getPat());
    	ol.setRxNo(rxNo);
    	ol.setRxType(groupName);
    	ol.setDrCode(Operator.getID());
    	ol.setDeptCode(Operator.getDept());
//    	ol.setRbOrderDeptCode(Operator.getDept());
    	ol.setAdmType(odo.getAdmType());
    	if(odo.getPat()==null)
    		//System.out.println("odo's pat=null");
    	ol.setPat(odo.getPat());

    	v.add(ol);
    	return ol;
    }
    /**
     * 新建处方
     * @param groupName String
     * @param admType String
     * @param pat Pat
     * @return OrderList
     */
    public OrderList newOrderList(String groupName,String admType,Pat pat){
    	Vector v=this.getGroup(groupName);
    	OrderList ol=new OrderList(this);
    	OrderList oldol=this.getOrderListOob(groupName, this.getGroupPrsptSize(groupName)-1);
    	//System.out.println("prescriptionlist---------------"+(this.getGroupPrsptSize(groupName)-1));
    	if(oldol==null){
    		ol.setPresrtNo(1);
    	}else{
    		ol.setPresrtNo(this.getGroupPrsptSize(groupName)+1);
    	}
    	String rxNo=SystemTool.getInstance().getNo("ALL", "ODO", "RX_NO", "RX_NO");
    	if(rxNo.length()==0)
    	{
    		//err("取处方号失败");
    		//System.out.println("取处方号失败");
    		return null;
    	}

    	ol.setRxNo(rxNo);
    	ol.setRxType(groupName);
    	ol.setDrCode(Operator.getID());
    	ol.setDeptCode(Operator.getDept());
//    	ol.setRbOrderDeptCode(Operator.getDept());
    	ol.setAdmType(admType);
    	ol.setPat(pat);
    	ol.setChargeFlg(true);
    	v.add(ol);
    	return ol;
    }
    public boolean removeOrderList(String groupName,int index){
    	Vector v=this.getGroup(groupName);
    	if(v.size()==0||index>=v.size()){
    		return false;
    	}
    	v.remove(index);
    	return true;
    }
//    /**
//     * 新建处方，通过参数确定是否新取得处方号
//     * @param groupName
//     * @param takeNewRxNo boolean 是否新取得处方号 Y:新取得处方号 N:使用旧处方号
//     * @param rxNo String 如不取得处方号则传入处方号
//     * @return OrderList 返回新建的OrderList
//     */
//    public OrderList newOrderList(String groupName,boolean takeNewRxNo,String rxNo){
//    	//参数校验，如果不用新取得处方号且传入处方号为空
//    	if((!takeNewRxNo)&&(rxNo==null||rxNo.length()==0)){
//    		return new OrderList();
//    	}
//    	Vector v=this.getGroup(groupName);
//    	OrderList ol=new OrderList(this);
//    	if(takeNewRxNo){
//    		rxNo=SystemTool.getInstance().getNo("ALL", "ODO", "RX_NO", "RX_NO");
//    	}
//    	if(rxNo.length()==0)
//    	{
//    		//err("取处方号失败");
//    		//System.out.println("取处方号失败");
//    		return null;
//    	}
//    	ol.setRxNo(rxNo);
//    	ol.setRxType(groupName);
//    	v.add(ol);
//    	return ol;
//    }
    public String getCaseNo(){
    	if(this.getODO()!=null)
    		return this.getODO().getCaseNo();
    	return "";
    }
    public String getAdmType(){
    	if(this.getODO()!=null)
    		return this.getODO().getAdmType();
    	return "";
    }
    public String getDrCode(){
    	if(this.getODO()!=null)
    		return this.getODO().getDrCode();
    	return "";
    }

    public void reset()
    {
    	String groupNames[] = this.getGroupNames();
    	for(int i = 0;i < groupNames.length;i++)
    	{
    		int count = getGroupPrsptSize(groupNames[i]);
    		for(int j = 0;j < count;j++)
    			this.getOrderList(groupNames[i], j).reset();

    	}
    }
    /**
     * 转换字符串型
     * @return String
     */
    public String toString() {
        return prescription.toString();
    }

    public static void main(String[] args) {
        PrescriptionList p = new PrescriptionList();
        TParm parm = new TParm();
        parm.setData("ACTION", "COUNT", 1);
        TParm xiyaozu = new TParm();

        xiyaozu.setData("NAME", "西药");
        xiyaozu.setData("ACTION", "COUNT", 1);
        parm.addData("GROUP", xiyaozu.getData());

        TParm order = new TParm();
        //icdCode:ICD_CODE;description:DESCRIPTION;icdCode:OLD_CODE:old
        order.addData("CASE_NO", "01");
        order.addData("RX_NO", "01");
        order.setData("ACTION", "COUNT", 1);

        xiyaozu.addData("LIST", order.getData());
        p.initParm(parm);

        OrderList list = p.getOrderList("西药",0);
        Order order1 = list.newOrder();
        order1.setCaseNo("AAA");
        //System.out.println(p);
        //System.out.println(p.getParm());
    }
    /**
   * 得到处方签的序列comb
   * @return TParm
   * add by fudw
   */
  public Vector getPrescriptionComb(String cat1Type) {
      String rxType = "";
      if("PHA".equals(cat1Type))
          rxType = "1,2,3";
      if("LIS".equals(cat1Type)||"RIS".equals(cat1Type))
          rxType = "5";
      if("TRT".equals(cat1Type)||"PLN".equals(cat1Type))
          rxType = "4";
      if("OTH".equals(cat1Type))
          rxType = "7";
      //处方签comb的数据
      Vector prescriptionComb=new Vector();
      //一行数据
      Vector combOneLine=new Vector();
      //显示方式
      combOneLine.add("id");
      combOneLine.add("name");
      //添加一行
      prescriptionComb.add(combOneLine);
      combOneLine=new Vector();
      //空行
      combOneLine.add("");
      combOneLine.add("");
      //添加空行
      prescriptionComb.add(combOneLine);
      //得到所有处方签号
     String[] prescript = this.getGroupNames();
     //
      for (int i = 0; i < prescript.length; i++) {
          //得到处方签长度
         int prescriptSize=  this.getGroup(prescript[i]).size();
         for(int j=0;j<prescriptSize;j++){
             OrderList orderList=this.getOrderListOob(prescript[i], j);
//             System.out.println("处方签医嘱类型"+orderList.getRxType());
//             System.out.println("传入参"+rxType);
//             System.out.println("比较结果"+rxType.indexOf(orderList.getRxType()));
             if(cat1Type==null||cat1Type.length()==0){
             }else{
                 if (rxType.indexOf(orderList.getRxType())<0)
                     continue;
             }
             combOneLine=new Vector();
//             System.out.println("处方签号"+orderList.getRxNo());
             combOneLine.add(orderList.getRxNo());
             combOneLine.add(orderList.getDrCode());
             prescriptionComb.add(combOneLine);
         }
      }
      return prescriptionComb;
  }

  public Vector getPrescriptionComb() {

      //处方签comb的数据
      Vector prescriptionComb = new Vector();
      //一行数据
      Vector combOneLine = new Vector();
      //显示方式
      combOneLine.add("id");
      combOneLine.add("name");
      //添加一行
      prescriptionComb.add(combOneLine);
      combOneLine = new Vector();
      //空行
      combOneLine.add("");
      combOneLine.add("");
      //添加空行
      prescriptionComb.add(combOneLine);
      //得到所有处方签号
      String[] prescript = this.getGroupNames();
      //
      for (int i = 0; i < prescript.length; i++) {
          //得到处方签长度
          int prescriptSize = this.getGroup(prescript[i]).size();
          for (int j = 0; j < prescriptSize; j++) {
              OrderList orderList = this.getOrderListOob(prescript[i], j);

              combOneLine = new Vector();

              combOneLine.add(orderList.getRxNo());
              combOneLine.add(orderList.getDrCode());
              prescriptionComb.add(combOneLine);
          }
      }
      return prescriptionComb;
  }
  /**
   * 得到所有的医嘱orderList
   * @param opbReceipt OPBReceipt
   * @return OrderList
   */
  public OrderList getOrderList(OPBReceipt opbReceipt) {
      OrderList orderList = new OrderList();
      //得到所有处方签号
      String[] prescript = this.getGroupNames();
      //
      for (int i = 0; i < prescript.length; i++) {
          //得到处方签长度
          int prescriptSize = this.getGroup(prescript[i]).size();
          for (int j = 0; j < prescriptSize; j++) {
              //拿出一张处方签中的order
              OrderList odList = this.getOrderListOob(prescript[i], j);
              int count=odList.size();
              for(int t=0;t<count;t++){
                  //取出一条order
                  Order order = (Order) odList.getOrder(t);
                  //根据条件添加到票据上
                  dealReceipt(orderList,order,opbReceipt);
              }
          }
      }
      return orderList;
  }

  /**
   * 分票
   * @param orderList OrderList
   * @param order Order
   * @param opbReceipt OPBReceipt
   */
  public void dealReceipt(OrderList orderList, Order order,
                          OPBReceipt opbReceipt) {
      if(opbReceipt.getReceiptNo().equals(order.getReceiptNo()))
          orderList.add(order);
  }
  /**
   * 取得集合医嘱的parm
   * @param groupNo int
   * @param orderCode String
   * @return TParm
   */
  public TParm  getOrderSetParm(int groupNo,String orderCode){
      OrderList orderList = new OrderList();
     //得到所有处方签号
     String[] prescript = this.getGroupNames();
     for (int i = 0; i < prescript.length; i++) {
         //得到处方签长度
         int prescriptSize = this.getGroup(prescript[i]).size();
         for (int j = 0; j < prescriptSize; j++) {
             //拿出一张处方签中的order
             OrderList odList = this.getOrderListOob(prescript[i], j);
             int count=odList.size();
             for(int t=0;t<count;t++){
                 //取出一条order
                 Order order = (Order) odList.getOrder(t);
                 int oneGroupNo=order.getOrderSetGroupNo();
                 String oneOrderSetCode = order.getOrdersetCode();
                 if(oneOrderSetCode==null||oneOrderSetCode.length()==0)
                     continue;
                 if (groupNo == oneGroupNo && orderCode.equals(oneOrderSetCode))
                     orderList.add(order);
             }
         }
     }
      return orderList.getParm(OrderList.PRIMARY);
  }
}
