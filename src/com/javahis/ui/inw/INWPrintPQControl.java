package com.javahis.ui.inw;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import java.util.Vector;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import com.dongyang.ui.TRadioButton;
import com.dongyang.jdo.TJDODBTool;

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
public class INWPrintPQControl extends TControl {
    //全局数据
    TParm parm = new TParm();
    //坐标
    Map optMap = new HashMap();
    //全局单位
    Map phaMap;
    //选中行
    int row = 0;
    //选中列
    int column=0;
    public void onInit(){
        Object obj = this.getParameter();
        if(obj!=null){
            parm = initPageData((Vector)obj);
            Object objPha = ((Vector)obj).get(((Vector)obj).size()-1);
            if(objPha!=null){
                phaMap = (Map)((Vector)obj).get(((Vector)obj).size()-1);
            }
        }
    }
    /**
     * 选中事件
     * @param row int
     * @param colomn int
     */
    public void onSelect(int row,int column){
        this.row = row;
        this.column = column;
    }
    /**
     * 打印
     */
    public void onPrint(){
        int count = parm.getCount("BED_NO");
        if (count <= 0) {
            this.messageBox_("没有要打印的医嘱！");
            return;
        }
        TParm actionParm = creatPrintData();
        int rowCount = actionParm.getCount("PRINT_DATAPQ");
        if(rowCount<=0){
            this.messageBox_("打印数据错误！");
            return;
        }
        TParm printDataPQParm = new TParm();
        int pRow = row;
        int pColumn = column;
        int cnt=0;
        int rowNull = 0;
        for(int i = 0; i < 15; i++){
            if (i % 3 == 0&&i!=0){
                cnt = 0 ;
                rowNull++;
            }
            if (i < pRow * 3 + pColumn) {
                printDataPQParm.addData("ORDER_DATE_"+(cnt+1),"");
                printDataPQParm.addData("BED_"+(cnt+1),"");
                printDataPQParm.addData("PAT_NAME_"+(cnt+1),"");
                printDataPQParm.addData("ORDER_1_"+(cnt+1),"");
                printDataPQParm.addData("QTY_1_"+(cnt+1),"");
                printDataPQParm.addData("TOT_QTY_1_"+(cnt+1),"");
                printDataPQParm.addData("ORDER_2_"+(cnt+1),"");
                printDataPQParm.addData("QTY_2_"+(cnt+1),"");
                printDataPQParm.addData("TOT_QTY_2_"+(cnt+1),"");
                printDataPQParm.addData("ORDER_3_"+(cnt+1),"");
                printDataPQParm.addData("QTY_3_"+(cnt+1),"");
                printDataPQParm.addData("TOT_QTY_3_"+(cnt+1),"");
                printDataPQParm.addData("ORDER_4_"+(cnt+1),"");
                printDataPQParm.addData("QTY_4_"+(cnt+1),"");
                printDataPQParm.addData("TOT_QTY_4_"+(cnt+1),"");
                printDataPQParm.addData("ORDER_5_"+(cnt+1),"");
                printDataPQParm.addData("QTY_5_"+(cnt+1),"");
                printDataPQParm.addData("TOT_QTY_5_"+(cnt+1),"");
                printDataPQParm.addData("STATION_DESC_"+(cnt+1),"");
                printDataPQParm.addData("MR_NO_"+(cnt+1),"");
                printDataPQParm.addData("SEX_"+(cnt+1),"");
                printDataPQParm.addData("AGE_"+(cnt+1),"");
                printDataPQParm.addData("RX_TYPE_"+(cnt+1),"");
                printDataPQParm.addData("FREQ_CODE_"+(cnt+1),"");
                printDataPQParm.addData("DOCTOR_"+(cnt+1),"");
                printDataPQParm.addData("ROUT_"+(cnt+1),"");
                printDataPQParm.addData("PAGE_"+(cnt+1),"");
                printDataPQParm.addData("TITLE_NAME_"+(cnt+1),"");
                printDataPQParm.addData("TITLE_QTY_"+(cnt+1),"");
                printDataPQParm.addData("TITLE_TOT_"+(cnt+1),"");
                printDataPQParm.addData("TITLE_DR_"+(cnt+1),"");
                printDataPQParm.addData("TITLE_CHECK_"+(cnt+1),"");
                printDataPQParm.addData("TITLE_EXE_"+(cnt+1),"");
                printDataPQParm.addData("TITLE_PAGEF_"+(cnt+1),"");
                printDataPQParm.addData("TITLE_PAGEB_"+(cnt+1),"");
                cnt++;
                continue;
            }else{
                break;
            }

        }
        for (int i = 0; i < rowCount; i++) {
            TParm temp = (TParm)actionParm.getData("PRINT_DATAPQ",i);
            printDataPQParm.addData("ORDER_DATE_"+(pColumn+1),temp.getData("DATETIME"));
            printDataPQParm.addData("BED_" + (pColumn + 1), temp.getData("BED_NO"));
            printDataPQParm.addData("PAT_NAME_" + (pColumn + 1), temp.getData("PAT_NAME"));
            printDataPQParm.addData("STATION_DESC_" + (pColumn + 1), temp.getData("STATION_DESC"));
            printDataPQParm.addData("MR_NO_" + (pColumn + 1), temp.getData("MR_NO"));
            printDataPQParm.addData("SEX_" + (pColumn + 1), temp.getData("SEX"));
            printDataPQParm.addData("AGE_" + (pColumn + 1), temp.getData("AGE"));
            printDataPQParm.addData("RX_TYPE_" + (pColumn + 1), temp.getData("RX_TYPE"));
            printDataPQParm.addData("FREQ_CODE_" + (pColumn + 1), temp.getData("FREQ"));
            printDataPQParm.addData("DOCTOR_" + (pColumn + 1), temp.getData("DOCTOR"));
            printDataPQParm.addData("ROUT_" + (pColumn + 1), temp.getData("ROUTE"));
            printDataPQParm.addData("PAGE_" + (pColumn + 1), temp.getData("PAGE"));

            printDataPQParm.addData("TITLE_NAME_" + (pColumn + 1),"药名");
            printDataPQParm.addData("TITLE_QTY_" + (pColumn + 1),"用量");
            printDataPQParm.addData("TITLE_TOT_" + (pColumn + 1),"数量");
            printDataPQParm.addData("TITLE_DR_" + (pColumn + 1),"医生:");
            printDataPQParm.addData("TITLE_CHECK_" + (pColumn + 1),"审核:");
            printDataPQParm.addData("TITLE_EXE_" + (pColumn + 1),"执行:");
            printDataPQParm.addData("TITLE_PAGEF_" + (pColumn + 1),"第");
            printDataPQParm.addData("TITLE_PAGEB_" + (pColumn + 1),"页");
            int rowOrderCount = temp.getCount("ORDER_DESC");
            for(int j=0;j<5;j++){
                if(j>rowOrderCount-1){
                    printDataPQParm.addData("ORDER_"+(j+1)+"_"+(pColumn+1),"");
                    printDataPQParm.addData("QTY_"+(j+1)+"_"+(pColumn+1),"");
                    printDataPQParm.addData("TOT_QTY_"+(j+1)+"_"+(pColumn+1),"");
                    continue;
                }
                printDataPQParm.addData("ORDER_"+(j+1)+"_"+(pColumn+1),temp.getData("ORDER_DESC",j));
                printDataPQParm.addData("QTY_"+(j+1)+"_"+(pColumn+1),numDot(temp.getDouble("QTY",j))+""+temp.getData("UNIT_CODE",j));
                printDataPQParm.addData("TOT_QTY_"+(j+1)+"_"+(pColumn+1),numDot(temp.getDouble("DOSAGE_QTY",j))+""+temp.getData("DOSAGE_UNIT",j));
            }
            pColumn++;
            if (pColumn == 3) {
                pColumn = 0;
                pRow++;
            }
        }
        printDataPQParm.setCount(pRow+rowNull+1);
        printDataPQParm.addData("SYSTEM", "COLUMNS", "A1");
        printDataPQParm.addData("SYSTEM", "COLUMNS", "A2");
        printDataPQParm.addData("SYSTEM", "COLUMNS", "A3");
        TParm parm = new TParm();
        parm.setData("PRINT_PQ", printDataPQParm.getData());
        openPrintWindow("%ROOT%\\config\\prt\\inw\\INWPasterNew.jhw",parm);
    }

    /**
     * 拿到选中打印起始位置
     * @return TParm
     */
    public TParm getPoint(){
        return null;
    }
    /**
     * 初始化页面打印数据
     * @param parm Vector
     * @return TParm
     */
    public TParm initPageData(Vector parm){
        TParm result = new TParm();
        int rowCount = ((Vector)parm.get(0)).size();
        for(int i=0;i<rowCount;i++){
            if(((Vector)parm.get(4)).get(i)!=null&&((Vector)parm.get(4)).get(i).toString().trim().length()!=0&&!"null".equals(((Vector)parm.get(4)).get(i))){
                //频次
                String freqCode = ((Vector) parm.get(12)).get(i).toString();
                TParm freqParm = new TParm(this.getDBTool().select("SELECT FREQ_TIMES FROM SYS_PHAFREQ WHERE FREQ_CODE='"+freqCode+"'"));
                int countFreq = freqParm.getInt("FREQ_TIMES", 0);
                //大于1次
                int seqNo = 1;
                 if(countFreq<=1){
                     result.addData("BED_NO",((Vector)parm.get(0)).get(i));
                     result.addData("MR_NO",((Vector)parm.get(1)).get(i));
                     result.addData("PAT_NAME",((Vector)parm.get(2)).get(i));
                     result.addData("LINK_MAIN_FLG",((Vector)parm.get(3)).get(i));
                     result.addData("LINK_NO",((Vector)parm.get(4)).get(i));
                     result.addData("ORDER_DESC",((Vector)parm.get(5)).get(i));
                     result.addData("QTY",((Vector)parm.get(6)).get(i));
                     result.addData("UNIT_CODE",((Vector)parm.get(7)).get(i));
                     result.addData("ORDER_CODE",((Vector)parm.get(8)).get(i));
                     result.addData("ORDER_NO",((Vector)parm.get(9)).get(i));
                     result.addData("ORDER_SEQ",((Vector)parm.get(10)).get(i));
                     result.addData("START_DTTM",((Vector)parm.get(11)).get(i));
                     result.addData("SEQ_NO",0);

                     result.addData("FREQ",((Vector)parm.get(12)).get(i));
                     result.addData("STATION_DESC",((Vector)parm.get(13)).get(i));
                     result.addData("SEX",((Vector)parm.get(14)).get(i));
                     result.addData("AGE",((Vector)parm.get(15)).get(i));
                     result.addData("RX_TYPE",((Vector)parm.get(16)).get(i));
                     result.addData("ROUTE",((Vector)parm.get(17)).get(i));
                     result.addData("DOCTOR",((Vector)parm.get(18)).get(i));
                     result.addData("DOSAGE_QTY",((Vector)parm.get(19)).get(i));
                     result.addData("DOSAGE_UNIT",((Vector)parm.get(20)).get(i));
                 }else{
                     for(int j=0;j<countFreq;j++){
                        result.addData("BED_NO",((Vector)parm.get(0)).get(i));
                        result.addData("MR_NO",((Vector)parm.get(1)).get(i));
                        result.addData("PAT_NAME",((Vector)parm.get(2)).get(i));
                        result.addData("LINK_MAIN_FLG",((Vector)parm.get(3)).get(i));
                        result.addData("LINK_NO",((Vector)parm.get(4)).get(i));
                        result.addData("ORDER_DESC",((Vector)parm.get(5)).get(i));
                        result.addData("QTY",((Vector)parm.get(6)).get(i));
                        result.addData("UNIT_CODE",((Vector)parm.get(7)).get(i));
                        result.addData("ORDER_CODE",((Vector)parm.get(8)).get(i));
                        result.addData("ORDER_NO",((Vector)parm.get(9)).get(i));
                        result.addData("ORDER_SEQ",((Vector)parm.get(10)).get(i));
                        result.addData("START_DTTM",((Vector)parm.get(11)).get(i));
                        result.addData("SEQ_NO",seqNo);

                        result.addData("FREQ",((Vector)parm.get(12)).get(i));
                        result.addData("STATION_DESC",((Vector)parm.get(13)).get(i));
                        result.addData("SEX",((Vector)parm.get(14)).get(i));
                        result.addData("AGE",((Vector)parm.get(15)).get(i));
                        result.addData("RX_TYPE",((Vector)parm.get(16)).get(i));
                        result.addData("ROUTE",((Vector)parm.get(17)).get(i));
                        result.addData("DOCTOR",((Vector)parm.get(18)).get(i));
                        result.addData("DOSAGE_QTY",((Vector)parm.get(19)).get(i));
                        result.addData("DOSAGE_UNIT",((Vector)parm.get(20)).get(i));
                        seqNo++;
                    }
                 }
            }
        }
        return result;
    }
    /**
    * 返回数据库操作工具
    * @return TJDODBTool
    */
   public TJDODBTool getDBTool(){
       return TJDODBTool.getInstance();
   }
    /**
     * 整理打印数据
     * @return TParm
     */
    public TParm creatPrintData(){
        TParm result = new TParm();
        Set linkSet = new HashSet();
        Map linkMap = new HashMap();
        int rowCount = parm.getCount("PAT_NAME");
        //打印多少个瓶签
        for(int i=0;i<rowCount;i++){
            TParm temp = parm.getRow(i);
            String tempStr = temp.getValue("MR_NO")+temp.getValue("LINK_NO")+temp.getValue("START_DTTM")+temp.getValue("SEQ_NO") + temp.getValue("ORDER_NO");
            linkSet.add(tempStr);
        }
        Iterator linkIterator = linkSet.iterator();
        //每个瓶签的基本信息
        while(linkIterator.hasNext()){
            String tempLinkStr = ""+linkIterator.next();
            for(int j=0;j<rowCount;j++){
                TParm tempParm = parm.getRow(j);
                if(tempLinkStr.equals(tempParm.getValue("MR_NO")+tempParm.getValue("LINK_NO")+tempParm.getValue("START_DTTM")+tempParm.getValue("SEQ_NO")+ tempParm.getValue("ORDER_NO"))){
                    TParm temp = new TParm();
                    String dateTime = tempParm.getValue("START_DTTM").substring(4,6)+"/"+tempParm.getValue("START_DTTM").substring(6,8);
                    temp.setData("DATETIME",dateTime);
                    temp.setData("BED_NO",tempParm.getValue("BED_NO"));
                    temp.setData("PAT_NAME",tempParm.getValue("PAT_NAME"));
                    temp.setData("MR_NO",tempParm.getValue("MR_NO"));
                    temp.setData("FREQ",tempParm.getValue("FREQ"));
                    temp.setData("STATION_DESC",tempParm.getValue("STATION_DESC"));
                    temp.setData("SEX",tempParm.getValue("SEX"));
                    temp.setData("AGE",tempParm.getValue("AGE"));
                    temp.setData("RX_TYPE",tempParm.getValue("RX_TYPE"));
                    temp.setData("ROUTE",tempParm.getValue("ROUTE"));
                    temp.setData("DOCTOR",tempParm.getValue("DOCTOR"));
                    linkMap.put(tempLinkStr,temp);
                    break;
                }
            }
            TParm temp = (TParm)linkMap.get(tempLinkStr);
            for(int j=0;j<rowCount;j++){
                TParm tempParm = parm.getRow(j);
                if(tempLinkStr.equals(tempParm.getValue("MR_NO")+
                                      tempParm.getValue("LINK_NO")+
                                      tempParm.getValue("START_DTTM")+
                                      tempParm.getValue("SEQ_NO")+
                                      tempParm.getValue("ORDER_NO"))){
                    String orderDesc = tempParm.getValue("ORDER_DESC");
                    String desc[] = breakNFixRow(orderDesc,28,1);
                    for(int k = 0;k<desc.length;k++){
                        if(k == 0){
                            temp.addData("ORDER_DESC",desc[k]);
                            //用量
                            temp.addData("QTY",tempParm.getValue("QTY"));
                            //单位
                            temp.addData("UNIT_CODE",phaMap.get(tempParm.getValue("UNIT_CODE")));
                            //总量
                            temp.addData("DOSAGE_QTY",tempParm.getValue("DOSAGE_QTY"));
                            //总量单位
                            temp.addData("DOSAGE_UNIT",phaMap.get(tempParm.getValue("DOSAGE_UNIT")));
                            //连接主
                            temp.addData("LINK_MAIN_FLG",tempParm.getValue("LINK_MAIN_FLG"));
                            continue;
                        }
                        temp.addData("ORDER_DESC",desc[k]);
                        //用量
                        temp.addData("QTY","");
                        //单位
                        temp.addData("UNIT_CODE","");
                        //总量
                        temp.addData("DOSAGE_QTY","");
                        //总量单位
                        temp.addData("DOSAGE_UNIT","");
                        //连接主
                        temp.addData("LINK_MAIN_FLG","");
                    }
                }
            }
            linkMap.put(tempLinkStr,temp);
            result.addData("PRINT_DATAPQ",linkMap.get(tempLinkStr));
        }
        Set onlySet = new HashSet();
        for(int i=0;i<result.getCount("PRINT_DATAPQ");i++){
            onlySet.add(((TParm)result.getRow(i).getData("PRINT_DATAPQ")).getValue("BED_NO"));
        }
        TParm resultTemp = new TParm();
        Iterator iter = onlySet.iterator();
        while(iter.hasNext()){
            String temp = iter.next().toString();
            for(int j=0;j<result.getCount("PRINT_DATAPQ");j++){
                if(temp.equals(((TParm)result.getRow(j).getData("PRINT_DATAPQ")).getValue("BED_NO"))){
                    resultTemp.addData("PRINT_DATAPQ",result.getRow(j).getData("PRINT_DATAPQ"));
                }
            }
        }

        return configParm(resultTemp);
    }

    private String numDot(double medQty){
        if(medQty == 0)
            return "";
        if((int)medQty == medQty)
            return "" + (int)medQty;
        else
            return "" + medQty;
    }

    private TParm configParm(TParm parm){
        TParm result = new TParm();
        for(int i = 0;i<parm.getCount("PRINT_DATAPQ");i++){
            TParm parmI = (TParm)parm.getData("PRINT_DATAPQ",i);;
            int rowCount = parmI.getCount("ORDER_DESC");
            int pageCount = 1;
            if(rowCount % 5 == 0)
                pageCount = rowCount/5;
            else
                pageCount = rowCount/5 + 1;
            int page = 1;
            for(int j = 0 ; j < rowCount ; j++ ){
                if((j + 1) % 5 ==0){
                    result.addData("PRINT_DATAPQ", cloneParm(parmI, j - 4, j));
                    ((TParm)result.getData("PRINT_DATAPQ",result.getCount("PRINT_DATAPQ") - 1)).setData("PAGE",page + "/" + pageCount);
                    page++;
                }
                else if((j + 1) == rowCount ){
                    result.addData("PRINT_DATAPQ",
                                   cloneParm(parmI, rowCount - rowCount % 5, j));
                    ((TParm)result.getData("PRINT_DATAPQ",result.getCount("PRINT_DATAPQ") - 1)).setData("PAGE",page + "/" + pageCount);
                    page++;
                }
            }
        }
        return result;
    }

    private TParm cloneParm(TParm parm,int startIndex,int endIndex){
        TParm result = new TParm();
        String[] names = parm.getNames();
        for(int i = 0 ;i < names.length;i++){
            if(parm.getData(names[i]) instanceof String)
                result.setData(names[i],parm.getData(names[i]));
            else if(parm.getData(names[i]) instanceof Vector){
                for(int j = startIndex;j <= endIndex;j++ )
                    result.addData(names[i], parm.getData(names[i],j));
            }
        }
        return result;
    }

    /**
     * 返回单选控件
     * @param tag String
     * @return TRadioButton
     */
    public TRadioButton getRadioButton(String tag){
        return (TRadioButton)this.getComponent(tag);
    }
    public  String[] breakNFixRow(String src, int bre, int fix)
    {
        return fixRow(breakRow(src, bre), fix);
    }
    public String[] fixRow(String string, int size)
    {
        Vector splitVector = new Vector();
        int index = 0;
        int separatorCount = 0;
        for(int i = 0; i < string.length(); i++)
        {
            char c = string.charAt(i);
            if("\n".equals(String.valueOf(c)))
            {
                if(++separatorCount >= size)
                {
                    splitVector.add(string.substring(index, i));
                    index = i + 1;
                    separatorCount = 0;
                }
            }
        }

        splitVector.add(string.substring(index, string.length()));
        String splitArray[] = new String[splitVector.size()];
        for(int j = 0; j < splitVector.size(); j++)
            splitArray[j] = (String)splitVector.get(j);

        return splitArray;
    }

    public String breakRow(String src, int size)
    {
        return breakRow(src, size, 0);
    }
    public String breakRow(String src, int size, int shift)
    {
        StringBuffer tmp = new StringBuffer("");
        tmp.append(space(shift));
        int i = 0;
        int len = 0;
        for(; i < src.length(); i++)
        {
            char c = src.charAt(i);
            len += getCharSize(c);
            if("\n".equals(String.valueOf(c)))
            {
                tmp.append(c);
                tmp.append(space(shift));
                len = 0;
            } else
            if(size >= len)
            {
                tmp.append(c);
            } else
            {
                tmp.append("\n");
                tmp.append(space(shift));
                tmp.append(c);
                len = getCharSize(c);
            }
        }

        return tmp.toString();
    }

    public int getCharSize(char c)
    {
        return (new String(new char[] {
            c
        })).getBytes().length;
    }

    public String space(int n)
    {
        StringBuffer tmp = new StringBuffer("");
        for(int i = 0; i < n; i++)
            tmp.append(' ');

        return tmp.toString();
    }

    public static void main(String[] args) {
        int pRow = 0;
        int pColumn = 2;
        for(int i = 0; i < 20; i++){
            if (i % 4 == 0&&i!=0)
            if (i < pRow * 4 + pColumn) {
                System.out.print(" ");
                continue;
            }else{
                break;
            }
        }
        for (int i = 0; i < 20; i++) {
            System.out.print("*");
            pColumn++;
            if (pColumn == 4) {
                pColumn = 0;
                pRow++;
            }
        }
    }
}
