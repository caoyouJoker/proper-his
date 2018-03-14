package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.data.TParm;
import com.dongyang.ui.edit.TAttributeList;

import jdo.sys.SystemTool;
/**
 *
 * <p>Title: 职业下拉列表</p>
 *
 * <p>Description: 职业下拉列表</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.01.06
 * @version 1.0
 */
public class TComboSYSOccupation extends TComboBox{
    /**
     * 新建对象的初始值
     * @param object TObject
     */
    public void createInit(TObject object)
    {
        if(object == null)
            return;
        object.setValue("Width","81");
        object.setValue("Height","23");
        object.setValue("Text","TButton");
        object.setValue("showID","Y");
        object.setValue("showName","Y");
        object.setValue("showText","N");
        object.setValue("showValue","N");
        object.setValue("showPy1","Y");
        object.setValue("showPy2","Y");
        object.setValue("Editable","Y");
        object.setValue("Tip","职业下拉列表");
        object.setValue("TableShowList","id,name");
        object.setValue("ModuleParmString","GROUP_ID:SYS_OCCUPATION");
        object.setValue("ModuleParmTag","");
    }
    public String getModuleName()
    {
        return "sys\\SYSDictionaryModule.x";
    }
    public String getModuleMethodName()
    {
		
		// modified by WangQing 20170315
      //  return "getGroupList";
		
		return "getGroupListNew";
    }
    public String getParmMap()
    {
        return "id:ID;name:NAME;enname:ENNAME;Py1:PY1;py2:PY2";
    }
    /**
     * 增加扩展属性
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data){
    }
	
	
	// modified by WangQing 20170315 
	
	/**
     * 执行Module动作
     */
    public void onQuery()
    {
        TParm parm = new TParm();
        parm.setDataN("GROUP_ID","SYS_OCCUPATION");
        parm.setDataN("NOW", this.dateToString((Date) SystemTool.getInstance().getDate()));
        parm.setDataN("NOW", this.dateToString((Date) SystemTool.getInstance().getDate()));
        setModuleParm(parm);
        super.onQuery();
    }
    
    // modified by WangQing 20170315 
	
	/**
	 * Date->String
	 * @param date
	 * @return
	 */
	public String dateToString(Date date){
		//		Date date = new Date();
		String dateStr = "";
		//format的格式可以任意
		//		DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH/mm/ss");
		DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			//			dateStr = sdf.format(date);
			//			System.out.println(dateStr);
			dateStr = sdf2.format(date);
			System.out.println(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateStr;
	}
}
