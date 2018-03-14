package com.javahis.ui.customquery;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import jdo.sys.CustomQueryTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TypeTool;

/**
 *
 * <p>Title: 条件选择对话框控制类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.5.18
 * @version 1.0
 */
public class SelectColumnDialogControl extends TControl{
    /**
     * 表名
     */
    private String tableName;
    private TTable allItem;
    private TTable selectItem;
    private TTable groupByTable;
    /**
     * 表信息
     */
    private TParm tableParm;
    /**
     * 初始化参数
     */
    public void onInitParameter()
    {
        Object parm = getParameter();
        if(parm == null || !(parm instanceof Object[]))
            return;
        Object pData[] = (Object[])parm;
        tableName = TypeTool.getString(pData[0]);
    }
    /**
     * 初始化
     */
    public void onInit()
    {
        allItem = (TTable)getComponent("ALL_ITEM");
        selectItem = (TTable)getComponent("SELECT_ITEM");
        selectItem.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
		"onSum");
        groupByTable=(TTable)getComponent("GROUPBY_TABLE");
        if(tableName == null || tableName.length() == 0)
            return;
        //得到表参数
        initTableParm();
        //初始化Table
        initTable();
    }
    /**
     * 得到表参数
     */
    public void initTableParm()
    {
        if (tableName == null || tableName.length() == 0)
            return;
        TParm parm = new TParm(getDBTool().getColumnsInf(tableName));
        if (parm.getErrCode() < 0) {
            messageBox_(parm.getErrText());
            return;
        }
        tableParm = new TParm();
        tableParm.setData("COLUMN_NAME",parm.getData("COLUMN_NAME"));
        tableParm.setData("NAME",parm.getData("COMMENTS"));
        tableParm.setCount(tableParm.getCount("COLUMN_NAME"));
    }
    /**
     * 初始化Table
     */
    public void initTable()
    {
        //保护检查
        if(tableName == null || tableName.length() == 0)
            return;
        if(tableParm == null || tableParm.getErrCode() < 0 || tableParm.getCount() == 0)
            return;
        //得到显示列
        String viewColumns[] = CustomQueryTool.getInstance().getViewTableColumn(tableName);
        //分割数据
        TParm parm = new TParm();
        Vector columnNames = (Vector)tableParm.getData("COLUMN_NAME");
        if(viewColumns != null)
            for(int i = 0;i < viewColumns.length;i++)
            {
                int index = columnNames.indexOf(viewColumns[i]);
                if(index < 0)
                    continue;
                parm.addData("COLUMN_NAME",viewColumns[i]);
                parm.addData("NAME",tableParm.getValue("NAME",index));
                parm.addData("SUM", "N");
                tableParm.removeRow(index);
            }
        parm.setCount(parm.getCount("COLUMN_NAME"));
        allItem.setParmValue(tableParm);
        if(allItem.getRowCount() > 0)
            allItem.setSelectedRow(0);
        else
        {
            callFunction("UI|ADD_BUTTON|setEnabled", false);
            callFunction("UI|ADD_ALL_BUTTON|setEnabled", false);
        }
        selectItem.setParmValue(parm);
        if(selectItem.getRowCount() > 0)
            selectItem.setSelectedRow(0);
        else
        {
            callFunction("UI|REMOVE_BUTTON|setEnabled", false);
            callFunction("UI|UP_BUTTON|setEnabled", false);
            callFunction("UI|DOWN_BUTTON|setEnabled", false);
            callFunction("UI|REMOVE_ALL_BUTTON|setEnabled", false);
        }
    }
    /**
     * 得到数据库对象
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool()
    {
        return TJDODBTool.getInstance();
    }
    /**
     * 添加
     */
    public void onAdd()
    {
        int row = allItem.getSelectedRow();
        if(row < 0)
            return;
        //给使用条件TABLE添加记录
        TParm parm = selectItem.getParmValue();
        if(parm == null)
            parm = new TParm();
        String columnId=allItem.getParmValue().getValue("COLUMN_NAME",row);
        Vector colVct=parm.getVectorValue("COLUMN_NAME");
        if(colVct!=null&&colVct.size()>0){
            if(colVct.indexOf(columnId)>-1){
            	this.messageBox_("不允许添加已有的列");
            	return;
            }
        }

        parm.addData("COLUMN_NAME",allItem.getParmValue().getValue("COLUMN_NAME",row));
        parm.addData("NAME",allItem.getParmValue().getValue("NAME",row));
        parm.addData("SUM", "N");
        parm.setCount(parm.getCount("COLUMN_NAME"));
        selectItem.setParmValue(parm);
        selectItem.setSelectedRow(selectItem.getRowCount() - 1);
        
        //判断使用条件TABLE中是否有合计的列，如果有，则要把新添加到使用条件TABLE的数据也添加到GROUPBY TABLE中
        int count=parm.getCount();
        TParm sumParm=this.groupByTable.getParmValue();
		if(sumParm==null){
			sumParm=new TParm();}
        for(int i=0;i<count;i++){
        	if(parm.getBoolean("SUM",i)){
        			sumParm.addData("COLUMN_NAME",allItem.getParmValue().getValue("COLUMN_NAME",row));
        			sumParm.addData("NAME",allItem.getParmValue().getValue("NAME",row));
        			sumParm.addData("SUM", "N");
        			sumParm.setCount(sumParm.getCount("COLUMN_NAME"));
        	}
        }
        groupByTable.setParmValue(sumParm);
        
//        allItem.removeRow(row);
        if(allItem.getRowCount() > 0)
            allItem.setSelectedRow(row < allItem.getRowCount()?row:allItem.getRowCount() - 1);
        else
        {
            callFunction("UI|ADD_BUTTON|setEnabled", false);
            callFunction("UI|ADD_ALL_BUTTON|setEnabled", false);
        }
        callFunction("UI|REMOVE_BUTTON|setEnabled",true);
        callFunction("UI|UP_BUTTON|setEnabled", true);
        callFunction("UI|DOWN_BUTTON|setEnabled", true);
        callFunction("UI|REMOVE_ALL_BUTTON|setEnabled", true);
    }
    /**
     * 删除
     */
    public void onRemove()
    {
        int row = selectItem.getSelectedRow();
        
        TParm parm=selectItem.getParmValue();
        String columnId=parm.getValue("COLUMN_ID",row);
        if(row < 0||parm.getCount()<0)
            return;
        
        //判断如果使用条件TABLE中有合计的列，则判断是删除合计列还是groupby的列
        List sumInt=new ArrayList();
        int count=parm.getCount();
        for(int i=0;i<count;i++){
        	if(parm.getBoolean("SUM",i)){
        		sumInt.add(i);
        	}
        }
        selectItem.removeRow(row);
        if(sumInt.size()>0){
        	//不是合计列
        	if(sumInt.indexOf(row)>-1){
        		groupByTable.removeRowAll();
        	}
        	//是合计列
        	else{
        		TParm sumParm=groupByTable.getParmValue();
        		int countSum=sumParm.getCount();
        		int sumRow=-1;
        		if(sumParm!=null&&countSum>0){
        			for(int i=0;i<countSum;i++){
        				if(columnId.equalsIgnoreCase(sumParm.getValue("COLUMN_ID",i))){
        					sumRow=i;
        				}
        			}
        			groupByTable.removeRow(sumRow);
        		}
        		
        		
        	}
        }
        if(selectItem.getRowCount() > 0)
            selectItem.setSelectedRow(row < selectItem.getRowCount()?row:selectItem.getRowCount() - 1);
        else
        {
            callFunction("UI|REMOVE_BUTTON|setEnabled", false);
            callFunction("UI|UP_BUTTON|setEnabled", false);
            callFunction("UI|DOWN_BUTTON|setEnabled", false);
            callFunction("UI|REMOVE_ALL_BUTTON|setEnabled", false);
        }
        
        
        
        callFunction("UI|ADD_BUTTON|setEnabled",true);
        callFunction("UI|ADD_ALL_BUTTON|setEnabled", true);
    }
    /**
     * 全选
     */
    public void onAddAll()
    {
        allItem.setSelectedRow(0);
        while(allItem.getRowCount() > 0)
            onAdd();
    }
    /**
     * 全清
     */
    public void onRemoveAll()
    {
        selectItem.setSelectedRow(0);
        while(selectItem.getRowCount() > 0)
            onRemove();
    }
    /**
     * 上移
     */
    public void onUP()
    {
        int row = selectItem.getSelectedRow();
        if(row <= 0)
            return;
        TParm parm = selectItem.getParmValue();
        if(parm == null)
            return;
        String temp = parm.getValue("COLUMN_NAME",row);
        parm.setData("COLUMN_NAME",row,parm.getValue("COLUMN_NAME",row - 1));
        parm.setData("COLUMN_NAME",row - 1,temp);
        temp = parm.getValue("NAME",row);
        parm.setData("NAME",row,parm.getValue("NAME",row - 1));
        parm.setData("NAME",row - 1,temp);
        selectItem.setParmValue(parm);
        selectItem.setSelectedRow(row - 1);
    }
    /**
     * 下移
     */
    public void onDown()
    {
        int row = selectItem.getSelectedRow();
        if(row >= selectItem.getRowCount() - 1)
            return;
        TParm parm = selectItem.getParmValue();
        if(parm == null)
            return;
        String temp = parm.getValue("COLUMN_NAME",row);
        parm.setData("COLUMN_NAME",row,parm.getValue("COLUMN_NAME",row + 1));
        parm.setData("COLUMN_NAME",row + 1,temp);
        temp = parm.getValue("NAME",row);
        parm.setData("NAME",row,parm.getValue("NAME",row + 1));
        parm.setData("NAME",row + 1,temp);
        selectItem.setParmValue(parm);
        selectItem.setSelectedRow(row + 1);
    }
    /**
     * 确定
     */
    public void onOK()
    {
        if(tableName == null || tableName.length() == 0)
            return;
        TParm parm = selectItem.getParmValue();
        if(parm == null)
            return;
        String columns[] = parm.getStringArray("COLUMN_NAME");
        TParm result = CustomQueryTool.getInstance().updateViewTableColumn(tableName,columns);
        if(result.getErrCode() < 0)
        {
            messageBox_(result.getErrText());
            return;
        }
        setReturnValue(columns);
        closeWindow();
    }
    /**
	 * 点选需要合计的字段，将没有被选中的字段写入groupby的table
	 * @param obj
	 */
	public boolean onSum(Object obj){
		TTable table=(TTable)obj;
		List sum=new ArrayList();
		TParm parm=table.getParmValue();
		int row=table.getSelectedRow();
		if(row<0){
			return true;
		}
		parm.setData("SUM", row, "Y");
		TParm sumParm=new TParm();
		String[] names=parm.getNames();
		
		int count=parm.getCount();
		
		if(parm==null||count<1){
			this.messageBox_("null");
			return true;
		}
		int sumParmCount=count;
		for(int i=0;i<count;i++){
			if(!parm.getBoolean("SUM",i)){
				for(int j=0;j<names.length;j++){
					sumParm.addData(names[j], parm.getValue(names[j],i));
				}
			}else{
				sumParmCount--;
			}
			
		}
		sumParm.setCount(sumParmCount);
		groupByTable.setParmValue(sumParm);
		
		
		return false;
	}
    /**
     * 取消
     */
    public void onCancel()
    {
        closeWindow();
    }
    public static void main(String args[])
    {
        Object parm = com.javahis.util.JavaHisDebug.runDialog("customquery\\SelectColumnDialog.x",new Object[]{"UDD_SHEET"});
        System.out.println("parm=" + parm);
    }
}
