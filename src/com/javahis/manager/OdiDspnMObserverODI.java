package com.javahis.manager;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;

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
public class OdiDspnMObserverODI
    extends TObserverAdapter {
    /**
     * 对象新增行关系列表
     */
    Map newRowMap = new HashMap();
    /**
     * 插入一条数据
     * @param ds TDS
     * @param row int
     * @return int
     */
    public int insertRow(TDS dspdmDs, int row) {
        String type = dspdmDs.getAttributeString("RX_KIND");
        List list = new ArrayList();
        //临时
        if ("ST".equals(type)) {
            //拿到DSPNM中新增行在DATASTORE中的唯一ID
//            System.out.println("DSPNM进入行行数"+row);
            int dspnmNewRowD = (Integer) dspdmDs.getItemData(row, "#ID#");
//            System.out.println("DSPNMDataStore行数"+dspnmNewRowD);
            //向DSPNM中插入一条数据为新增数据
//            System.out.println("D表新增=====================");
            int newRow = this.getDS().insertRow();
            int newRowId = (Integer)this.getDS().getItemData(newRow,"#ID#");
            list.add(newRowId);
            //DSPNM表新增行和DSPND表新增行对照关系
            newRowMap.put(dspnmNewRowD,list);
//            this.getDS().setItem(newRow, "CASE_NO", dspdmDs.getAttribute("CASE_NO"));(0907)
            //设置对象之间新行的对应关系
            //保存标记
            this.getDS().setActive(newRow,false);
//            System.out.println("DSPND的数据=========");
//            this.getDS().showDebug();
            return newRow;
        }
        //长期
        if ("UD".equals(type)) {
            //拿到DSPNM中新增行在DATASTORE中的唯一ID
            int dspnmNewRowD = (Integer) dspdmDs.getItemData(row, "#ID#");
            //向DSPNM中插入一条数据为新增数据
            int newRow = this.getDS().insertRow();
            list.add(newRow);
            //DSPNM表新增行和DSPND表新增行对照关系
            newRowMap.put(dspnmNewRowD,list);
            this.getDS().setItem(newRow, "CASE_NO", dspdmDs.getAttribute("CASE_NO"));
            //设置对象之间新行的对应关系
            //保存标记
            this.getDS().setActive(newRow,false);
//            System.out.println("DSPND的数据=========");
//            this.getDS().showDebug();
            return newRow;
        }
        //出院带药
        if ("DS".equals(type)) {
            //拿到DSPNM中新增行在DATASTORE中的唯一ID
            int dspnmNewRowD = (Integer) dspdmDs.getItemData(row, "#ID#");
            //向DSPNM中插入一条数据为新增数据
            int newRow = this.getDS().insertRow();
            list.add(newRow);
            //DSPNM表新增行和DSPND表新增行对照关系
            newRowMap.put(dspnmNewRowD,list);
            this.getDS().setItem(newRow, "CASE_NO", dspdmDs.getAttribute("CASE_NO"));
            //设置对象之间新行的对应关系
            //保存标记
            this.getDS().setActive(newRow,false);
//            System.out.println("DSPND的数据=========");
//            this.getDS().showDebug();
            return newRow;
        }
        //中药饮片
        if ("IG".equals(type)) {
            //拿到DSPNM中新增行在DATASTORE中的唯一ID
            int dspnmNewRowD = (Integer) dspdmDs.getItemData(row, "#ID#");
            //向DSPNM中插入一条数据为新增数据
            int newRow = this.getDS().insertRow();
            list.add(newRow);
            //DSPNM表新增行和DSPND表新增行对照关系
            newRowMap.put(dspnmNewRowD,list);
            this.getDS().setItem(newRow, "CASE_NO", dspdmDs.getAttribute("CASE_NO"));
            //设置对象之间新行的对应关系
            //保存标记
            this.getDS().setActive(newRow,false);
//            System.out.println("DSPND的数据=========");
//            this.getDS().showDebug();
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
    public boolean setItem(TDS dspdmDs, int row, String column, Object value) {

        //医嘱类别
        String type = dspdmDs.getAttributeString("RX_KIND");
//        System.out.println("医嘱类别M表:"+type);
        //是否本对象要设置列值(Y表示需要N表示不需要防止递归)
        if(!(Boolean)dspdmDs.getAttribute("CHANGE_FLG"))
            return false;
        String buff = dspdmDs.isFilter()?dspdmDs.FILTER:dspdmDs.PRIMARY;
        //是临时医嘱的时候
        if("ST".equals(type)){
//            System.out.println("D对照:" + newRowMap);
            //设置医嘱类别和就诊序号
            if ("DSPN_KIND".equals(column) || "CASE_NO".equals(column)) {
//                System.out.println("M表column"+column);
                if ("DSPN_KIND".equals(column)) {
                    if (buff.equals(dspdmDs.PRIMARY))
                        return true;
                    else
                        return false;
                }
                if ("CASE_NO".equals(column)) {
                    //初始化ORDER数据到全局行对应中
                    if (newRowMap.isEmpty()) {
                        createDataStoreTable(type, dspdmDs, this.getDS());
                    }
                }
//                dspdmDs.setAttribute("CHANGE_FLG", false);
//                dspdmDs.setItem(row, column, value);
//                dspdmDs.setAttribute("CHANGE_FLG", true);
                if (buff.equals(dspdmDs.PRIMARY))
                    return true;
                else
                    return false;
            }
//            else {
//                int dspnmNewRowD = (Integer) dspdmDs.getItemData(row, "#ID#");
//                List rowList = (List) newRowMap.get(dspnmNewRowD);
//               System.out.println("rowList赋值对应表:"+rowList);
//                for (int i = 0; i < rowList.size(); i++) {
//                    int r = Integer.parseInt(rowList.get(i).toString());
//                    int rSetId = getDspnDSet(r, this.getDS());
//                    this.getDS().setItem(rSetId, column, value);
//                    this.getDS().setActive(rSetId, true);
//                }
//                if (buff.equals(dspdmDs.PRIMARY))
//                    return true;
//                else
//                    return false;
//            }
            if(value instanceof TParm){
                int dsRow = (Integer) dspdmDs.getItemData(row, "#ID#");
                List rowList = (List) newRowMap.get(dsRow);
                TParm parm = (TParm) value;
                //拿到列
                String columnList[] = dspdmDs.getColumns();
                String columnListD[] = this.getDS().getColumns();
                for (String temp : columnList) {
                    if (!"LINK_NO".equals(temp)&&parm.getValue(temp).length() == 0)
                        continue;
                    if ("DSPN_KIND".equals(temp) || "CASE_NO".equals(temp))
                        continue;
                    dspdmDs.setAttribute("CHANGE_FLG", false);
                    dspdmDs.setItem(row, temp, parm.getData(temp));
                    dspdmDs.setAttribute("CHANGE_FLG", true);
                }
                for(String temp:columnListD){
                    if (!"LINK_NO".equals(temp)&&parm.getValue(temp).length() == 0)
                        continue;
                    if ("DSPN_KIND".equals(temp) || "CASE_NO".equals(temp))
                        continue;
                    //查询在DSPNM中的位置
                    for (int i = 0; i < rowList.size(); i++) {
                        int r = Integer.parseInt(rowList.get(i).toString());
                        int rSetId = getDspnDSet(r, this.getDS());
                        if(rSetId<0){
                            break;
                        }
                        this.getDS().setItem(rSetId, temp, parm.getData(temp));
                        this.getDS().setActive(rSetId, true);
                    }
                }
                if (buff.equals(dspdmDs.PRIMARY))
                    return true;
                else
                    return false;
            }
        }
//        if(!newRowMap.isEmpty()){
//            //拿到DSPNM表中在DATASTORE的唯一ID号
//            int dspnmNewRowD = (Integer) dspdmDs.getItemData(row, "#ID#");
//            List rowList = (List) newRowMap.get(dspnmNewRowD);
////            int dspndNewRowD = (Integer) newRowMap.get(dspnmNewRowD);
////             int dspndNewRowD = StringTool.getInt(""+rowList.get(0));
////            this.getDS().setItem(dspndNewRowD, column, value);
//            if (value instanceof TParm && column.length() == 0) {
////                this.getDS().setActive(dspndNewRowD, true);
//                //转换成TPARM
//                TParm parm = (TParm) value;
//                parm.setData("CASE_NO",dspdmDs.getAttribute("CASE_NO"));
//                //拿到当前ODI_ORDER中需要设置的列名返回一个数组
//                String columnList[] = this.getDS().getColumns();
//                //循环给每个列赋值(根据不同的条件调用不同的计算方式并讲SYS_FEE中对应ODI_ORDER中的列明做转换)
//                Object obj = parm.getData("START_DTTM_LIST");
//                if (obj != null) {
//                    List dspndList = (List) parm.getData("START_DTTM_LIST");
//                    int listSize = dspndList.size();
//                    //删除
//                    for (int i = 0; i < rowList.size(); i++) {
//                        this.getDS().deleteRow(StringTool.getInt("" + rowList.get(i)));
//                        //设置行对照
//                        List temps = new ArrayList();
//                        newRowMap.put(dspnmNewRowD, temps);
//                    }
//                    //插入
//                    List tempRowList = new ArrayList();
//                    for (int i = 0; i < listSize; i++) {
//                        int rowNew = this.getDS().insertRow();
//                        tempRowList.add(rowNew);
//                        newRowMap.put(dspnmNewRowD, tempRowList);
//                        for (String temp : columnList) {
//                            if (parm.getData(temp) == null) {
//                                continue;
//                            }
//                            if("START_DTTM".equals(temp)){
//                                parm.setData(temp,dspndList.get(i));
//                            }
//                            dspdmDs.setAttribute("CHANGE_FLG",false);
//                            this.getDS().setItem(rowNew, temp,parm.getData(temp));
//                        }
//                    }
//                }else{
//                    for (int i = 0; i < rowList.size(); i++) {
//                        int rowModif = StringTool.getInt("" + rowList.get(i));
//                        this.getDS().setItem(rowModif, "CASE_NO", dspdmDs.getAttribute("CASE_NO"));
//                        this.getDS().setActive(rowModif, true);
//                        for (String temp : columnList) {
//                            if (parm.getData(temp) == null) {
//                                continue;
//                            }
//                            dspdmDs.setAttribute("CHANGE_FLG",false);
//                            this.getDS().setItem(rowModif, temp,parm.getData(temp));
//                        }
//                    }
//                }
//            }else {
//                System.out.println("====================================================");
//                for(int i=0;i<rowList.size();i++){
//                    int rowModif = StringTool.getInt(""+rowList.get(i));
//                    this.getDS().setItem(rowModif, "CASE_NO", dspdmDs.getAttribute("CASE_NO"));
//                    this.getDS().setActive(rowModif, true);
//                    this.getDS().setItem(rowModif, column, value);
//                }
//            }
//        }
        if (buff.equals(dspdmDs.PRIMARY))
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
    public int getDspnDSet(int id,TDS dspndDs){
        String buff = dspndDs.isFilter()?dspndDs.FILTER:dspndDs.PRIMARY;
        int rowCount = dspndDs.rowCount();
        int result = -1;
        for(int i=0;i<rowCount;i++){
            int setId = (Integer)dspndDs.getItemData(i,"#ID#",buff);
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
    public boolean deleteRow(TDS dspnmDs, int row) {
        //医嘱类型
       String type = dspnmDs.getAttributeString("RX_KIND");
//       System.out.println("医嘱类型"+type);
       String buff = dspnmDs.isFilter()?dspnmDs.FILTER:dspnmDs.PRIMARY;
       int delRowdspnm = dspnmDs.getAttributeInt("DELROW");
       if("ST".equals(type)){
           List delList = (List)newRowMap.get(delRowdspnm);
            for(int i=0;i<delList.size();i++){
                //查询在DSPNM中的位置
                int dspnmRowIdSet = getDspnDSet(Integer.parseInt(delList.get(i).toString()), this.getDS());
                if (dspnmRowIdSet < 0) {
                    if (buff.equals(dspnmDs.PRIMARY))
                        return true;
                    else
                        return false;
                }
                this.getDS().deleteRow(dspnmRowIdSet);
            }
           newRowMap.remove(delRowdspnm);
           if (buff.equals(dspnmDs.PRIMARY))
               return true;
           else
               return false;
       }

//        //拿新增行的ID
//        Object obj = dspnmDs.getItemData(row, "#ID#");
////        System.out.println("MObjc值"+obj);
//        if (obj != null) {
//            int dspndNewRow = (Integer) obj;
//            TParm parm = this.getDS().getRowParm((Integer)newRowMap.get(dspndNewRow));
////            System.out.println("D删除的行"+parm);
//            List delList = (List)newRowMap.get(dspndNewRow);
//            for(int i=0;i<delList.size();i++){
//                this.getDS().deleteRow(StringTool.getInt(""+delList.get(i)));
//            }
//            newRowMap.remove(dspndNewRow);
//            return true;
//        }
//        this.getDS().deleteRow(row);
       if (buff.equals(dspnmDs.PRIMARY))
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
//        System.out.println("得到未知行值");
//        ds.showDebug();
//        System.out.println("数据"+parm);
//        System.out.println("行"+row);
//        System.out.println("列"+column);
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
//        System.out.println("设置未知行值");
//        ds.showDebug();
//        System.out.println("数据"+parm);
//        System.out.println("行号"+row);
//        System.out.println("列"+column);
//        System.out.println("值"+value);
        return false;
    }
    /**
    * 初始化主表和观察表的行对应
    * @param type String
    * @param orderDs TDS
    * @param dspnmDs TDS
    */
   public void createDataStoreTable(String type,TDS dspnmDs,TDS dspndDs){
       if("ST".equals(type)){
           String buffDspnm = dspnmDs.isFilter()?dspnmDs.FILTER:dspnmDs.PRIMARY;
           String buffDspnd = dspndDs.isFilter()?dspndDs.FILTER:dspndDs.PRIMARY;
           //DSPNM表行号
           int rowCountDspnm = dspnmDs.isFilter()?dspnmDs.rowCountFilter():dspnmDs.rowCount();
           //DSPND表行号
           int rowCountDspnd = dspndDs.isFilter()?dspndDs.rowCountFilter():dspndDs.rowCount();
           for(int i=0;i<rowCountDspnm;i++){
               TParm temp = dspnmDs.getRowParm(i,buffDspnm);
               String orderDate = StringTool.getString(temp.getTimestamp("ORDER_DATE"),"yyyyMMddHHmmss");
               int dspnmID = (Integer)dspnmDs.getItemData(i,"#ID#",buffDspnm);
               List listM = new ArrayList();
               for(int j=0;j<rowCountDspnd;j++){
                   TParm tempDspnd = dspndDs.getRowParm(j,buffDspnd);
                   if(temp.getValue("CASE_NO").equals(tempDspnd.getValue("CASE_NO"))&&temp.getValue("ORDER_NO").equals(tempDspnd.getValue("ORDER_NO"))&&temp.getValue("ORDER_SEQ").equals(tempDspnd.getValue("ORDER_SEQ"))&&orderDate.equals(tempDspnd.getValue("ORDER_DATE"))){
                       int dspndID = (Integer)dspndDs.getItemData(j,"#ID#",buffDspnd);
                       listM.add(dspndID);
                       newRowMap.put(dspnmID,listM);
                       continue;
                   }
               }
           }
//           System.out.println("M对应行号:"+newRowMap);
       }
   }
   /**
    * 清空MAP
    */
   public void setMap(){
//       System.out.println("清空MAP(DSPNM)");
       this.newRowMap.clear();
   }
}
