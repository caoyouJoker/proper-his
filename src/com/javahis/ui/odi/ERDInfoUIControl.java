package com.javahis.ui.odi;

import jdo.erd.ERDLevelTool;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TWord;
import com.dongyang.ui.event.TTableEvent;
import com.javahis.util.OdoUtil;

/**
 * <p>Title: </p>
 *
 * <p>Description:�����������鿴 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: ProperSoft </p>
 *
 * @author huangtt 20151030
 * @version 1.0
 */
public class ERDInfoUIControl extends TControl{
	/**
     * TABLE1����
     */
    private static String TABLE = "TABLE1";
    /**
     * WORD�ؼ�
     */
    private static String WORD = "WORD";

    private TWord word;
    TTable table;
    public void onInit() {
        super.onInit();
        word = this.getTWord(WORD);
        table = this.getTTable(TABLE);
        //ע��Table����¼�
        callFunction("UI|" + TABLE + "|addEventListener",
                     TABLE + "->" + TTableEvent.CLICKED, this, "onTABLEClicked");
        Object obj = this.getParameter();
        if (obj != null) {
            TParm parm = (TParm) obj;
            this.getTTable(TABLE).setParmValue(parm);
        }
    }
    
    public void onTABLEClicked(int row) {
        if (row < 0) {
            return;
        }
        word.onNewFile();
		word.update();
		TParm talbeParm = table.getParmValue();		
        String triageNo = talbeParm.getValue("TRIAGE_NO", row);
        String mrNo = talbeParm.getValue("MR_NO", row);
        String[] saveFiles = ERDLevelTool.getInstance().getELFile(triageNo);
        TParm allParm = new TParm();
		word.onOpen(saveFiles[0], saveFiles[1], 3, false);
		allParm.addListener("onDoubleClicked", this, "onDoubleClicked");
		allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", mrNo);
		word.setWordParameter(allParm);
		word.setCanEdit(false);
		Pat pat = Pat.onQueryByMrNo(mrNo);
		word.setMicroField("����", pat.getName());
		word.setMicroField("�Ա�", pat.getSexString());
		word.setMicroField("����", OdoUtil.showAge(pat.getBirthday(),
 				SystemTool.getInstance().getDate()));
 }
    
    /**
     * �õ�WORD
     * @param tag String
     * @return TWord
     */
    public TWord getTWord(String tag) {
        return (TWord)this.getComponent(tag);
    }
    
    /**
     * TABLE����
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag) {
        return (TTable)this.getComponent(tag);
    }
}
