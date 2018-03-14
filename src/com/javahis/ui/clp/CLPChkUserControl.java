
/**
 * <p>Title:临床路径执行人员 </p>
 *
 * <p>Description:临床路径执行人员 </p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: bluecore</p>
 *
 * @author fux 21020703
 * @version 4.0
 */

package com.javahis.ui.clp;
import java.sql.Timestamp;
import java.util.Vector;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TMessage;
import com.dongyang.util.TypeTool;



public class CLPChkUserControl extends TControl {
	    TTable TABLE;
	    TParm data;
	    int selectRow = -1;
	    /**
	     * 初始化
	     */
	    public void onInit() {
	        super.onInit();
	        // 添加诊区table单击事件
	        this.callFunction("UI|TABLE|addEventListener",
	                     "TABLE->" + TTableEvent.CLICKED, this,
	                     "onTableClicked");
	        //获得TABLE对象
	        TABLE=(	TTable)this.getComponent("TABLE");
	        onClear();
	        this.onQuery();
	    }

	    /**
	     * 增加对TABLE的监听,单击表格一行数据
	     */
	    public void onTableClicked() {
	        //如果修改方法将主键锁起来
	    	 ( (TTextField) getComponent("CHKUSER_CODE")).setEnabled(false);
	        //删除按钮可以选择
	        ((TMenuItem)this.getComponent("delete")).setEnabled(true);
	        //当前行
	         int row = TABLE.getSelectedRow();
	        //获得表格所有属性值
	         TParm parm = TABLE.getParmValue();
	         setValueForParm("CHKUSER_CODE;CHKUSER_CHN_DESC;PY1;PY2;CHKUSER_ENG_DESC;" +
	                 "SEQ;DESCRIPTION",
	                parm, row);
	         this.setValue("SEQ",parm.getValue("SEQ",row));
	    }
	   

	    /**
	     * 清空
	     */
	    public void onClear() {
	    	
	        data = new TParm();
	        clearValue("CHKUSER_CODE;CHKUSER_CHN_DESC;PY1;PY2;" +
	        		"SEQ;DESCRIPTION;CHKUSER_ENG_DESC");
	        this.callFunction("UI|TABLE|removeRowAll");
	        TTable Table = (TTable)this.getComponent("TABLE");
	        selectRow = -1;
	        this.onQuery();
	        data = Table.getParmValue();
	        long seq = 0;
	        // 取SEQ最大值
	        if (data.existData("SEQ")) {
	            Vector vct = data.getVectorValue("SEQ");
	            for (int i = 0; i < vct.size(); i++) {
	                long a = Long.parseLong( (vct.get(i)).toString().trim());
	                if (a > seq)
	                    seq = a;
	            }
	            this.setValue("SEQ", seq + 1);
	        }
	      
	    }
	    /**
	     * 查询
	     */
	    public void onQuery(){  
	    	String chkusercode = this.getValueString("CHKUSER_CODE");
	        String chkuserdesc = this.getValueString("CHKUSER_CHN_DESC");
	        StringBuffer sb = new StringBuffer();
	        if (chkusercode != null && chkusercode.length() > 0)
	            sb.append(" CHKUSER_CODE like '" + chkusercode + "%' ");
	        if (chkuserdesc != null && chkuserdesc.length() > 0) {
	            if (sb.length() > 0)
	                sb.append(" AND ");
	            sb.append(" CHKUSER_CHN_DESC like '" + chkuserdesc + "%' ");
	        }
	    	   String sql1 = " SELECT  CHKUSER_FLG," +
	    	   		         " CHKUSER_CODE,CHKUSER_CHN_DESC," +
  			                 " CHKUSER_ENG_DESC,PY1,PY2,SEQ,DESCRIPTION," +
  			                 " OPT_USER,OPT_TERM,OPT_DATE " +
  			                 " FROM CLP_CHKUSER" ;
	           String sql2 = " ORDER BY SEQ,CHKUSER_CODE ";
	        if (sb.length() > 0)
	            sql1 += " WHERE " + sb.toString()+sql2 ;
	        else
	        	sql1 += sql2;
	        System.out.println("sql::"+sql1);
	        TParm result = new TParm(TJDODBTool.getInstance().select(sql1));
	        System.out.println("sqlParm::"+result);
	        if (result.getErrCode() < 0) {
	            messageBox(result.getErrText());
	            return;
	        }
	        System.out.println("sqlParm::"+result);
	        //诊区table赋值
	        this.callFunction("UI|TABLE|setParmValue", result);
	    	
	    
	    }
	    /**
	     * 保存
	     */
	    public void onSave() {
//	    	 if (!this.emptyTextCheck("CHKUSER_CODE"))
//	    		 return ;
	         String chkusercode= getValue("CHKUSER_CODE").toString(); //代码
	         String chkuserdesc = getValue("CHKUSER_CHN_DESC").toString(); //说明
	         String py1 = getValue("PY1").toString(); //拼音码
	         String py2 = getValue("PY2").toString(); //注记码
	         int seq = TypeTool.getInt(getValue("SEQ")); //顺序号
	         String description = getValue("DESCRIPTION").toString(); //备注
	         String enddesc = getValue("CHKUSER_ENG_DESC").toString(); //英文
	         String optUser = Operator.getID(); //操作人员
	         Timestamp optDate = SystemTool.getInstance().getDate(); //操作日期
	         String optTerm = Operator.getIP(); //操作终端
	         TParm parm = new TParm();
	         parm.setData("CHKUSER_CODE", chkusercode);
	         parm.setData("CHKUSER_CHN_DESC", chkuserdesc);
	         parm.setData("PY1", py1);
	         parm.setData("PY2", py2);
	         parm.setData("SEQ", seq);
	         parm.setData("DESCRIPTION", description);
	         parm.setData("CHKUSER_ENG_DESC", enddesc);
	         parm.setData("OPT_USER", optUser);
	         parm.setData("OPT_DATE", optDate);
	         parm.setData("OPT_TERM", optTerm);
	        if (!emptyTextCheck("CHKUSER_CODE,CHKUSER_CHN_DESC")) {
	            return;
	        }
	        String selSql =
	            " SELECT CHKUSER_CODE FROM CLP_CHKUSER WHERE CHKUSER_CODE = '" +
	            chkusercode + "'";
	        TParm selParm = new TParm(TJDODBTool.getInstance().select(selSql));
	        if (selParm.getCount("CLINICAREA_CODE") > 0) {
	            String updateSql =
	            	 " UPDATE CLP_CHKUSER " +
	                 "    SET CHKUSER_CODE='" + chkusercode + "',CHKUSER_CHN_DESC='" + chkuserdesc + "',PY1='" + py1 +
	                 "',PY2='" + py2 + "',SEQ='" + seq + "',DESCRIPTION='" +
	                 description + "'," +
	                 "        CHKUSER_ENG_DESC='" + enddesc + "',OPT_USER='" + optUser +
	                 "',OPT_DATE=SYSDATE,OPT_TERM='" + optTerm + "' " +
	                 "  WHERE CHKUSER_CODE='" + chkusercode + "'";
	            TParm updateParm = new TParm(TJDODBTool.getInstance().update(
	                updateSql));
	            //刷新，设置末行某列的值
	            int row = (Integer) callFunction("UI|TABLE|getSelectedRow");
	            if (row < 0){}
	            else {
	                TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
	                data.setRowData(row, parm);
	                callFunction("UI|AREATABLE|setRowParmValue", row, data);
	            }

	        }
	        else {
	            String sql =
	            	 " INSERT INTO CLP_CHKUSER " +
	                 "             (CHKUSER_CODE,CHKUSER_CHN_DESC,PY1,PY2,SEQ," +
	                 "             DESCRIPTION,CHKUSER_ENG_DESC,OPT_USER,OPT_DATE,OPT_TERM) " +
	                 "      VALUES ('" + chkusercode + "','" + chkuserdesc + "','" + py1 +
	                 "','" + py2 + "','" + seq + "'," +
	                 "             '" + description + "','" + enddesc + "','" +
	                 optUser + "',SYSDATE,'" + optTerm + "') ";
	            TParm insertParm = new TParm(TJDODBTool.getInstance().update(sql));
	            if (insertParm.getErrCode() < 0) {
	                messageBox(insertParm.getErrText());
	                return ;
	            }
	            //table上加入新增的数据显示
	            callFunction("UI|TABLE|addRow", parm,
	                         "CHKUSER_CODE;CHKUSER_CHN_DESC;PY1;PY2;SEQ;" +
	                         "DESCRIPTION;CHKUSER_ENG_DESC;OPT_USER;OPT_DATE;OPT_TERM");
	        }
	        messageBox("P0001");
	        this.onClear();
	        return ;
	    }
	    /**
	     * 删除
	     */
	    public void onDelete()
	    {   
	    	TTable table = (TTable)this.getComponent("TABLE");
	    	if (this.messageBox("提示", "是否删除", 2) == 0)
	    	{
	            String chkuserCode = getValue("CHKUSER_CODE").toString();
	            String deletesql =
	            	      " DELETE FROM CLP_CHKUSER WHERE CHKUSER_CODE = '" +
	            	      chkuserCode + "' ";
	            //System.out.println("SQL=="+deletesql);
	            TParm delParm = new TParm(TJDODBTool.getInstance().update(
	            	   deletesql));
	            //System.out.println("delParm=="+delParm);
	            if (delParm.getErrCode() < 0) {
	                messageBox(delParm.getErrText());
	                return;
	            }
	            if (delParm != null) {
	                this.messageBox("删除成功");
	            } else
	                this.messageBox("删除失败");
	            onClear();
	        }
	        else 
	        {
	            return;
	        }
	    }
	    /**
	     * 名称回车事件
	     */
	    public void onUserNameAction(){
	    	String Chkuser = getValueString("CHKUSER_CHN_DESC");
	    	String py = TMessage.getPy(Chkuser);
	    	setValue("PY1", py);
	    	((TTextField) getComponent("PY1")).grabFocus();
	    }
	    /**
	     * 根据汉字输出拼音首字母
	     *
	     * @return Object
	     */
	    public Object onCode() {
	        if (TCM_Transform.getString(this.getValue("REGMETHOD_DESC")).length() <
	            1) {
	            return null;
	        }
	        String value = TMessage.getPy(this.getValueString("REGMETHOD_DESC"));
	        if (null == value || value.length() < 1) {
	            return null;
	        }
	        this.setValue("PY1", value);
	        // 光标下移
	        ( (TTextField) getComponent("PY1")).grabFocus();
	        return null;
	    }
	    /**
	     * 得到最大的编号
	     *
	     * @param dataStore
	     *            TDataStore
	     * @param columnName
	     *            String
	     * @return String
	     */
	    public int getMaxSeq(TDataStore dataStore, String columnName) {
	        if (dataStore == null)
	            return 0;
	        // 保存数据量
	        int count = dataStore.rowCount();
	        // 保存最大号
	        int s = 0;
	        for (int i = 0; i < count; i++) {
	            int value = dataStore.getItemInt(i, columnName);
	            // 保存最大值
	            if (s < value) {
	                s = value;
	                continue;
	            }
	        }
	        // 最大号加1
	        s++;
	        return s;
	    }

}
