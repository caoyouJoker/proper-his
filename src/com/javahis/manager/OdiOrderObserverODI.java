package com.javahis.manager;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import java.util.Map;
import java.util.HashMap;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import java.util.Vector;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
/**
 * <p>Title: 住院管理=>住院医生站系统=>住院医生站管理者=ODI_ORDER观察者</p>
 *
 * <p>Description: ODI_ORDER观察者</p>
 *
 * <p>Copyright: Copyright JavaHis (c) 2009年1月</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author Miracle
 * @version JavaHis 1.0
 */
public class OdiOrderObserverODI
    extends TObserverAdapter {
    /**
     * 对象新增行关系列表
     */
    Map newRowMap = new HashMap();
    /**
    * 是否有未编辑行
    * @return boolean
    */
   public boolean isNewRow(){
       Boolean falg = false;
       TParm parmBuff = this.getDS().getBuffer(this.getDS().PRIMARY);
//       System.out.println("是否有未编辑行parmBuff"+parmBuff);
       int lastRow = parmBuff.getCount("#ACTIVE#");
       Object obj = parmBuff.getData("#ACTIVE#",lastRow-1);
       if(obj!=null){
           falg = (Boolean)parmBuff.getData("#ACTIVE#",lastRow-1);
       }else{
           falg=true;
       }
//       System.out.println("是否有未编辑行**********"+falg);
       return falg;
   }
   /**
    * 得到和M表的行对应
    * @return int
    */
   public int getRowIdDspnm(){
       int row = -1;
       TParm parmBuff = this.getDS().getBuffer(this.getDS().PRIMARY);
       int lastRow = parmBuff.getCount("#ACTIVE#");
       Object obj = parmBuff.getData("#ACTIVE#",lastRow-1);
       if(obj!=null){
           boolean falg = parmBuff.getBoolean("#ACTIVE#",lastRow-1);
           if(!falg){
               row = (Integer)this.getDS().getItemData(lastRow-1,"#ID#");
//               System.out.println("OOOOOOOOOOOOOO========"+row);
               Iterator iter = newRowMap.keySet().iterator();
               List mapList = new ArrayList();
               while(iter.hasNext()){
                   int objIndex = (Integer)iter.next();
                   if(row == Integer.parseInt(newRowMap.get(objIndex).toString())){
//                       System.out.println("MMMMMMMMMMMMM========"+objIndex);
                       /**
                        * Iterator 是工作在一个独立的线程中，并且拥有一个 mutex 锁。
                        * Iterator 被创建之后会建立一个指向原来对象的单链索引表，
                        * 当原来的对象数量发生变化时，这个索引表的内容不会同步改变，
                        * 所以当索引指针往后移动的时候就找不到要迭代的对象，
                        * 所以按照 fail-fast 原则 Iterator
                        * 会马上抛出 java.util.ConcurrentModificationException 异常
                        * 所以 Iterator 在工作的时候是不允许被迭代的对象被改变的。
                        * 但你可以使用 Iterator 本身的方法 remove() 来删除对象，
                        * Iterator.remove() 方法会在删除当前迭代对象的同时维护索引的一致性。
                        * 如果你的 Collection / Map 对象实际只有一个元素的时候，
                        * ConcurrentModificationException 异常并不会被抛出
                        */
                       mapList.add(objIndex);
                   }
               }
               for(int j=0;j<mapList.size();j++){
                   newRowMap.remove(mapList.get(j));
               }
           }else{
               row = -1;
           }
       }
       return row;
   }
    /**
     * 插入一条数据
     * @param ds TDS
     * @param row int
     * @return int 为正数表示正确插入
     */
    public int insertRow(TDS odiOrderDs, int row) {
        String type = odiOrderDs.getAttributeString("RX_KIND");
//        System.out.println("?????????????????   "+type);
        //临时
        if("ST".equals(type)){
            //拿到ORDER对象中新增行的唯一ID在DATESTORE(TDS)中的
            int dsRow = (Integer) odiOrderDs.getItemData(row, "#ID#");
//            System.out.println("INSERT传入行行号:"+row);
//            System.out.println("INSERTDATESTORE行号:"+dsRow);
            //查看M表是否有未编辑行(删除时使用)并且不是集合医嘱
            if(!this.isNewRow()&&!odiOrderDs.getAttributeBoolean("ORDERJH_FLG")){
//                System.out.println("进来了====");
                int rowDspnm = getRowIdDspnm();
                if(rowDspnm<0){
                    return row;
                }else{
                    newRowMap.put(dsRow,rowDspnm);
                    odiOrderDs.setItem(row,"RX_KIND", type);
                }
                return row;
            }
//            System.out.println("M表新增:====");
            //向DSPNM中插入一条数据为新增数据
            this.getDS().setAttribute("RX_KIND",type);
            int newRow = this.getDS().insertRow();
            int newRowId = (Integer)this.getDS().getItemData(newRow,"#ID#");
//            System.out.println("DSPNM新增行" + newRow);
            //设置对象之间新行的对应关系
            newRowMap.put(dsRow,newRowId);
            //设置ORDER中的类型
            odiOrderDs.setItem(row,"RX_KIND", type);
            //设置DSPNM中的类型
//            this.getDS().setItem(newRow, "DSPN_KIND", type);(0907)
//            //设置看诊号
//            this.getDS().setItem(newRow, "CASE_NO", odiOrderDs.getAttribute("CASE_NO"));(0907)
            //保存标记
            this.getDS().setActive(newRow,false);
//            System.out.println("=======================M表新增");
//            this.getDS().showDebug();
            return newRow;
        }
        //长期
        if("UD".equals(type)){
            //拿到ORDER对象中新增行的唯一ID在DATESTORE(TDS)中的
            int dsRow = (Integer) odiOrderDs.getItemData(row, "#ID#");
            //向DSPNM中插入一条数据为新增数据
            this.getDS().setAttribute("RX_KIND",odiOrderDs.getAttribute("RX_KIND"));
            int newRow = this.getDS().insertRow();
//            System.out.println("DSPNM新增行" + newRow);
            //设置对象之间新行的对应关系
            newRowMap.put(dsRow, newRow);
            //设置ORDER中的类型
            odiOrderDs.setItem(row, "RX_KIND", odiOrderDs.getAttribute("RX_KIND"));
            //设置DSPNM中的类型
            this.getDS().setItem(newRow, "DSPN_KIND", odiOrderDs.getAttribute("RX_KIND"));
            //设置看诊号
            this.getDS().setItem(newRow, "CASE_NO", odiOrderDs.getAttribute("CASE_NO"));
            //保存标记
            this.getDS().setActive(newRow,false);
//            System.out.println("=======================M表新增");
//            this.getDS().showDebug();
            return newRow;
        }
        //出院带药
        if("DS".equals(type)){
            //拿到ORDER对象中新增行的唯一ID在DATESTORE(TDS)中的
             int dsRow = (Integer) odiOrderDs.getItemData(row, "#ID#");
//             System.out.println("拿到ORDER对象中新增行的唯一ID在DATESTORE(TDS)中的"+dsRow);
             //向DSPNM中插入一条数据为新增数据
             this.getDS().setAttribute("RX_KIND",odiOrderDs.getAttribute("RX_KIND"));
             int newRow = this.getDS().insertRow();
//             System.out.println("DSPNM新增行向DSPNM中插入一条数据为新增数据" + newRow);
             //设置对象之间新行的对应关系
             newRowMap.put(dsRow, newRow);
             //设置ORDER中的类型
             odiOrderDs.setItem(row, "RX_KIND", odiOrderDs.getAttribute("RX_KIND"));
             //设置DSPNM中的类型
             this.getDS().setItem(newRow, "DSPN_KIND", odiOrderDs.getAttribute("RX_KIND"));
             //设置看诊号
             this.getDS().setItem(newRow, "CASE_NO", odiOrderDs.getAttribute("CASE_NO"));
             //保存标记
             this.getDS().setActive(newRow,false);
//             System.out.println("=======================M表新增");
//             this.getDS().showDebug();
             return newRow;
        }
        //中药饮片
        if("IG".equals(type)){
            //拿到ORDER对象中新增行的唯一ID在DATESTORE(TDS)中的
             int dsRow = (Integer) odiOrderDs.getItemData(row, "#ID#");
             //向DSPNM中插入一条数据为新增数据
             this.getDS().setAttribute("RX_KIND",odiOrderDs.getAttribute("RX_KIND"));
             int newRow = this.getDS().insertRow();
//             System.out.println("DSPNM新增行" + newRow);
             //设置对象之间新行的对应关系
             newRowMap.put(dsRow, newRow);
             //设置ORDER中的类型
             odiOrderDs.setItem(row, "RX_KIND", odiOrderDs.getAttribute("RX_KIND"));
             //设置DSPNM中的类型
             this.getDS().setItem(newRow, "DSPN_KIND", odiOrderDs.getAttribute("RX_KIND"));
             //设置看诊号
             this.getDS().setItem(newRow, "CASE_NO", odiOrderDs.getAttribute("CASE_NO"));
             //保存标记
             this.getDS().setActive(newRow,false);
//             System.out.println("=======================M表新增");
//             this.getDS().showDebug();
             return newRow;
        }
        return -1;
    }
    /**
     * 设置项
     * @param ds TDS
     * @param row int
     * @param column String
     * @param value Object
     * @return boolean
     */
    public boolean setItem(TDS odiOrderDs, int row, String column, Object value) {
        //医嘱类型
        String type = odiOrderDs.getAttributeString("RX_KIND");
        //是否本对象要设置列值(Y表示需要N表示不需要防止递归)
        if(!(Boolean)odiOrderDs.getAttribute("CHANGE_FLG"))
            return false;
//        System.out.println("传入值:"+value+"列值:"+column+"医嘱类型:"+type);
//        System.out.println("setItem传入行行号:"+row);
        String buff = odiOrderDs.isFilter()?odiOrderDs.FILTER:odiOrderDs.PRIMARY;
        //临时医嘱
        if("ST".equals(type)){
//            System.out.println("M对照:"+newRowMap);
            //设置医嘱类别
            if ("RX_KIND".equals(column)||"CASE_NO".equals(column)) {
//                System.out.println("Order表column"+column);
                if("RX_KIND".equals(column)){
                    int dsRow = (Integer) odiOrderDs.getItemData(row, "#ID#");
                    odiOrderDs.setAttribute("CHANGE_FLG", false);
                    odiOrderDs.setItem(row, column, value);
                    odiOrderDs.setAttribute("CHANGE_FLG", true);
                    int odiM = (Integer)newRowMap.get(dsRow);
//                    System.out.println("M表的行号:"+odiM);
                    //查询在DSPNM中的位置
                    int dspnmRowIdSet = getDspnmSet(odiM,this.getDS());
                    if(dspnmRowIdSet<0){
                        if(buff.equals(odiOrderDs.PRIMARY))
                            return true;
                        else
                            return false;
                    }
                    return this.getDS().setItem(dspnmRowIdSet,"DSPN_KIND",value);
                }
                if("CASE_NO".equals(column)){
                    //初始化ORDER数据到全局行对应中
                    if(newRowMap.isEmpty()){
                        createDataStoreTable(type,odiOrderDs,this.getDS());
                    }
                }
//                odiOrderDs.setAttribute("CHANGE_FLG", false);
//                odiOrderDs.setItem(row, column, value);
//                odiOrderDs.setAttribute("CHANGE_FLG", true);
                if (buff.equals(odiOrderDs.PRIMARY))
                    return true;
                else
                    return false;
            }
            if(value instanceof TParm){
                int dsRow = (Integer) odiOrderDs.getItemData(row, "#ID#");
                int odiM = (Integer) newRowMap.get(dsRow);
                TParm parm = (TParm)value;
                //拿到列
                String columnList[] = odiOrderDs.getColumns();
                for(String temp:columnList){
                    if(!"LINK_NO".equals(temp)&&parm.getValue(temp).length()==0)
                        continue;
                    if("RX_KIND".equals(temp)||"CASE_NO".equals(temp))
                        continue;
                    odiOrderDs.setAttribute("CHANGE_FLG", false);
                    odiOrderDs.setItem(row,temp,parm.getData(temp));
                    odiOrderDs.setAttribute("CHANGE_FLG", true);
                    //插入M表时对此字段的处理
                    if("START_DTTM".equals(temp)){
//                        System.out.println("START_DTTM"+parm.getData(temp));
                        if(parm.getData(temp) instanceof Timestamp){
                            String startDttm = StringTool.getString(parm.getTimestamp(temp),"yyyyMMddHHmmss");
                            parm.setData(temp,startDttm);
                        }
                    }
                    //查询在DSPNM中的位置
                    int dspnmRowIdSet = getDspnmSet(odiM, this.getDS());
                    if (dspnmRowIdSet < 0) {
                        if (buff.equals(odiOrderDs.PRIMARY))
                            return true;
                        else
                            return false;
                    }

                    System.out.println("M表行:"+dspnmRowIdSet);
//                    this.getDS().setItem(dspnmRowIdSet,temp,parm.getData(temp));
                    this.getDS().setItem(dspnmRowIdSet,"",parm);
                    this.getDS().setActive(dspnmRowIdSet,true);
                }
                if (buff.equals(odiOrderDs.PRIMARY))
                    return true;
                else
                    return false;
            }

        }
//        //判断是否是TPARM类型并且不是空指向的情况
//        if (value instanceof TParm && column.length()==0) {
//            int id = (Integer) odiOrderDs.getItemData(row, "#ID#");
//            System.out.println("setItemDATESTORE行号:"+id);
//            //拿新增行的行号
//            int dspnmNewRow = (Integer) newRowMap.get(id);
//            //转换成TPARM
//            TParm parm = (TParm) value;
//            //初始化行赋值
//            Object obj = parm.getData("ACTIVE");
//            if(obj!=null){
//                Boolean falg = (Boolean)parm.getData("ACTIVE");
//                if(!falg){
//                    odiOrderDs.setAttribute("CHANGE_FLG", false);
//                    odiOrderDs.setItem(row, "RX_KIND", parm.getData("RX_KIND"));
//                    return true;
//                }
//            }
//            //保存标记
//            this.getDS().setActive(dspnmNewRow,true);
//            //拿到当前ODI_ORDER中需要设置的列名返回一个数组
////            System.out.println("M表中要赋值的数据"+value);
//            String columnList[] = odiOrderDs.getColumns();
//            //循环给每个列赋值(根据不同的条件调用不同的计算方式并讲SYS_FEE中对应ODI_ORDER中的列明做转换)
//            for (String temp : columnList) {
//                if (parm.getData(temp) == null) {
//                    continue;
//                }
//                odiOrderDs.setAttribute("CHANGE_FLG",false);
//                //付值F时首日量
//                if("Y".equals(parm.getData("RX_FLG"))){
//                    if (temp.equals("RX_KIND")) {
//                        this.getDS().setItem(dspnmNewRow, "DSPN_KIND",
//                                             parm.getData(temp));
//                        continue;
//                    }
//                }
//                odiOrderDs.setItem(row,temp,parm.getData(temp));
//                //找到ODI_DSPNM关联的新增行
////                System.out.println("行对照======================>>>>>"+newRowMap+"新增行=======>>"+row);
//                if(temp.equals("START_DTTM")){
////                    System.out.println("M表主键"+parm.getData(temp).getClass()+"==="+parm.getData(temp));
//                    if(parm.getData(temp) instanceof Timestamp){
//                        String startDttm = StringTool.getString(parm.getTimestamp(temp),"yyyyMMddHHmmss");
////                        System.out.println("处理后的数据:"+startDttm);
//                        parm.setData(temp,startDttm);
//                    }
//                }
//                this.getDS().setItem(dspnmNewRow, temp, parm.getData(temp));
//            }
//            //设置值
//            this.getDS().setItem(dspnmNewRow,"", parm);
//        }
//
//        //设置医嘱类别
//        if("RX_KIND".equals(column)){
//            odiOrderDs.setAttribute("CHANGE_FLG",false);
//            odiOrderDs.setItem(row,column,value);
//        }
        if (buff.equals(odiOrderDs.PRIMARY))
            return true;
        else
            return false;
    }
    /**
     * 找到DSPNM中的位置
     * @param id int
     * @param dspnmDs TDS
     * @return int
     */
    public int getDspnmSet(int id,TDS dspnmDs){
        String buff = dspnmDs.isFilter()?dspnmDs.FILTER:dspnmDs.PRIMARY;
        int rowCount = dspnmDs.rowCount();
        int result = -1;
        for(int i=0;i<rowCount;i++){
            int setId = (Integer)dspnmDs.getItemData(i,"#ID#",buff);
            if(id==setId){
                result = i;
                break;
            }
        }
        return result;
    }
    /**
     * 删除一行
     * @param ds TDS
     * @param row int
     * @return boolean
     */
    public boolean deleteRow(TDS odiOrderDs, int row) {
        //医嘱类型
        String type = odiOrderDs.getAttributeString("RX_KIND");
//        System.out.println("DEL医嘱类型"+type);
        String buff = odiOrderDs.isFilter()?odiOrderDs.FILTER:odiOrderDs.PRIMARY;
        int delRowOrder = odiOrderDs.getAttributeInt("DELROW");
        if("ST".equals(type)){
            int odiM = (Integer) newRowMap.get(delRowOrder);
//            System.out.println("对应M表删除行ID:"+odiM);
            //查询在DSPNM中的位置
            int dspnmRowIdSet = getDspnmSet(odiM, this.getDS());
//            System.out.println("查询在DSPNM中的位置:"+dspnmRowIdSet);
            if (dspnmRowIdSet < 0) {
                if (buff.equals(odiOrderDs.PRIMARY))
                    return true;
                else
                    return false;
            }
//            System.out.println("DEL行:"+dspnmRowIdSet);
            this.getDS().setAttribute("DELROW",odiM);
            this.getDS().deleteRow(dspnmRowIdSet);
            newRowMap.remove(delRowOrder);
//            System.out.println("M表对应行:"+newRowMap);
            if (buff.equals(odiOrderDs.PRIMARY))
                return true;
            else
                return false;
        }
//        System.out.println("删除一行"+row);
//        //拿新增行的ID
//        Object obj = odiOrderDs.getItemData(row,"#ID#");
////        System.out.println("得到obj值"+obj);
//        Object obj1 = odiOrderDs.getAttribute("DEL_FLG");
////        System.out.println("得到obj1的值"+obj1);
//        if(obj1!=null){
//            if (! (Boolean) odiOrderDs.getAttribute("DEL_FLG")) {
//                if (obj != null) {
//                    int OrderNewRow = (Integer) obj;
//                    TParm parm = this.getDS().getRowParm( (Integer) newRowMap.
//                        get(OrderNewRow));
////                    System.out.println("M删除的行" + parm);
//                    this.getDS().deleteRow( (Integer) newRowMap.get(OrderNewRow));
//                    newRowMap.remove(OrderNewRow);
//                    return true;
//                }
//                this.getDS().deleteRow(row);
//                return false;
//            }
//            odiOrderDs.setAttribute("DEL_FLG", false);
//            odiOrderDs.deleteRow(row);
//        }else{
//           this.getDS().deleteRow(row);
//        }
        if (buff.equals(odiOrderDs.PRIMARY))
            return true;
        else
            return false;

    }
    /**
     * 得到未知行值
     * @param ds TDS
     * @param parm TParm
     * @param row int
     * @param column String
     * @return Object
     */
    public Object getOtherColumnValue(TDS ds, TParm parm, int row,String column) {
        if("EFF_DATEDAY".equals(column)){
            TParm action = parm.getRow(row);
            String effDate = StringTool.getString(action.getTimestamp("EFF_DATE"),"yyyy/MM/dd HH:mm:ss");
            return effDate;
        }
        if("ORDER_DESCCHN".equals(column)){
            TParm action = parm.getRow(row);
            String orderDescChn = action.getValue("ORDER_DESC")+action.getValue("GOODS_DESC")+action.getValue("DESCRIPTION")+action.getValue("SPECIFICATION");
            return orderDescChn;
        }
        return null;
    }
    /**
     * 设置未知行值
     * @param ds TDS
     * @param parm TParm
     * @param row int
     * @param column String
     * @param value Object
     * @return boolean
     */
    public boolean setOtherColumnValue(TDS ds, TParm parm, int row,String column, Object value) {
        if("EFF_DATEDAY".equals(column)){
            ds.setAttribute("CHANGE_FLG",false);
            ds.setItem(row,"EFF_DATE",StringTool.getTimestamp(""+value,"yyyy/MM/dd HH:mm:ss"));
        }
        return false;
    }
    /**
     * 初始化主表和观察表的行对应
     * @param type String
     * @param orderDs TDS
     * @param dspnmDs TDS
     */
    public void createDataStoreTable(String type,TDS orderDs,TDS dspnmDs){
        if("ST".equals(type)){
//            String filterStr = orderDs.getFilter();
//            String sortStr = orderDs.getSort();
//            System.out.println("过滤语句"+filterStr);
//            orderDs.setFilter("");
//            orderDs.filter();
//            System.out.println("过滤后:");
//            orderDs.showDebug();
            String buffOrder = orderDs.isFilter()?orderDs.FILTER:orderDs.PRIMARY;
            String buffDspnm = dspnmDs.isFilter()?dspnmDs.FILTER:dspnmDs.PRIMARY;
            //ORDER表行号
            int rowCountOrder = orderDs.isFilter()?orderDs.rowCountFilter():orderDs.rowCount();
            //DSPNM表行号
            int rowCountDspnm = dspnmDs.isFilter()?dspnmDs.rowCountFilter():dspnmDs.rowCount();
            for(int i=0;i<rowCountOrder;i++){
                TParm temp = orderDs.getRowParm(i,buffOrder);
//                String startDttm = StringTool.getString(temp.getTimestamp("START_DTTM"),"yyyyMMddHHmmss");
                int orderID = (Integer)orderDs.getItemData(i,"#ID#",buffOrder);
                for(int j=0;j<rowCountDspnm;j++){
                    TParm tempDspnm = dspnmDs.getRowParm(j,buffDspnm);
                    if(temp.getValue("CASE_NO").equals(tempDspnm.getValue("CASE_NO"))&&temp.getValue("ORDER_NO").equals(tempDspnm.getValue("ORDER_NO"))&&temp.getValue("ORDER_SEQ").equals(tempDspnm.getValue("ORDER_SEQ"))){
                        int dspnmID = (Integer)dspnmDs.getItemData(j,"#ID#",buffDspnm);
                        newRowMap.put(orderID,dspnmID);
                        continue;
                    }
                }
            }
//            orderDs.setFilter(filterStr);
//            orderDs.setSort(sortStr);
//            orderDs.filter();
//            System.out.println("ORDER对应行号:"+newRowMap);
        }
    }
    /**
     * 清空MAP
     */
    public void setMap(){
//        System.out.println("清空MAP(ORDER)");
        this.newRowMap.clear();
    }
}
