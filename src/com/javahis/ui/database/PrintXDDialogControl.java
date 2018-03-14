package com.javahis.ui.database;

import com.dongyang.ui.TComboBox;
import com.dongyang.control.TControl;
import javax.print.PrintService;
import java.util.List;
import java.util.ArrayList;
import java.awt.print.PrinterJob;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;

public class PrintXDDialogControl extends TControl{
    private TComboBox printlist;
    /**
     * 初始化
     */
    public void onInit()
    {
        printlist = (TComboBox) getComponent("PRINT_LIST");
        initPrintList();
    }
    /**
     * 初始化打印机列表
     */
    public void initPrintList()
    {
        List list = new ArrayList();
        PrintService[] services = PrinterJob.lookupPrintServices();
        for(int i=0;i<services.length;i++){
            list.add(services[i].getName().trim());
        }
        String s[] = (String[])list.toArray(new String[]{});
        printlist.setData(s);
        PrintService ps = PrinterJob.getPrinterJob().getPrintService();
        String defaultPrint = ps.getName();
        printlist.setSelectedID(defaultPrint);
    }
    /**
     * 得到选中打印机
     * @return PrintService
     */
    public PrintService getSelectPrint()
    {
        String name = printlist.getSelectedID();
        PrintService[] services = PrinterJob.lookupPrintServices();
        for(int i=0;i<services.length;i++){
            if(services[i].getName().trim().equals(name))
                return services[i];
        }
        return null;
    }
    /**
     * 确定
     */
    public void onOK()
    {
        PrintService print = getSelectPrint();
        if (print == null) {
            messageBox_("请选择打印机!");
            return;
        }
        TParm parm = new TParm();
        parm.setData("PRINT", print);
        parm.setData("START_ROW",getValue("START_ROW"));
        parm.setData("END_ROW",getValue("END_ROW"));
        parm.setData("PRINT_BAK",StringTool.getBoolean("" + getValue("PRINT_BAK")));
        setReturnValue(parm);
        closeWindow();
    }
    /**
     * 取消
     */
    public void onCancel()
    {
        closeWindow();
    }
}
