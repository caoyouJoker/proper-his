package com.javahis.ui.emr;

import java.util.List;
import java.util.UUID;
import com.dongyang.control.TControl;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.wcomponent.util.TiString;
import com.javahis.ui.sys.orm.bean.EmrTypePO;
import com.javahis.ui.sys.orm.ext.QueryToolExt;
import com.javahis.ui.sys.orm.ext.TableToolExt;
import com.javahis.ui.sys.orm.tools.SqlTool;
import com.javahis.ui.sys.orm.tools.Type;



/**
 *
 * @author whaosoft
 *
 */
public class TEmrWordTypeControl extends TControl{


	/**  */
	private final static String TAG_TABEL = "tTable_0";
	/**  */
	private final static String TAG_ETNAME = "ET_NAME";
	private TableToolExt tableTool;
	private QueryToolExt qt = QueryToolExt.getInstance();
	private TTable table;


	/**
	 *
	 */
	public void onInit() {

		super.onInit();

		table = (TTable) getComponent(TAG_TABEL);

		tableTool = new TableToolExt(table);

		//
        this.showTable();
	}

	/**
	 *
	 */
	private void showTable(){

		List<EmrTypePO> list = qt.queryBySql("select * from EMR_NEW_TYPE", EmrTypePO.class);

		tableTool.show(list);
	}

	/**
	 *
	 */
	public void onSelectItem(){

		//this.messageBox("~~~");

		EmrTypePO et = tableTool.getSelectedData();

        this.setValue(TAG_ETNAME, et.name);
	}

	/**
	 *
	 */
	public void onAdd(){
		table.clearSelection();
		this.setValue(TAG_ETNAME,null);
	}

	/**
	 *
	 */
	public void onSave(){

	    String str = this.getText(TAG_ETNAME);
        if( TiString.isEmpty(str) ){
        	this.messageBox("名称不可以为空!");
        	return;
        }

	    //
		EmrTypePO et = tableTool.getSelectedData();

        if( null!= et ){

        	et.modifyState = Type.UPDATE;
        	et.name = str;
        }else{

        	et = new EmrTypePO();
        	et.modifyState = Type.INSERT;
        	et.name = str;
        	et.id = UUID.randomUUID().toString();
        }

        //
		try {
			String sql = SqlTool.getInstance().getSql(et);

			TJDODBTool.getInstance().update(sql);

		} catch (Exception e) {
			this.messageBox("保存失败!");
			throw new RuntimeException(e);
		}

		//
		this.showTable();
		this.setValue(TAG_ETNAME,null);

		//
		this.messageBox("保存成功!");
	}


}