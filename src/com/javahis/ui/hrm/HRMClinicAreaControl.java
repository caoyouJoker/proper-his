package com.javahis.ui.hrm;

import jdo.hrm.HRMClinicArea;
import jdo.hrm.HRMClinicRoom;
import jdo.sys.MessageTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TTableEvent;
import com.javahis.util.StringUtil;
/**
 * <p> Title: 健康检查诊区诊间对应控制类 </p>
 *
 * <p> Description: 健康检查诊区诊间对应控制类 </p>
 *
 * <p> Copyright: javahis 20090922 </p>
 *
 * <p> Company:JavaHis </p>
 *
 * @author ehui
 * @version 1.0
 */
public class HRMClinicAreaControl extends TControl {
	//诊区TABLE，诊间TABLE
	private TTable areaTable,roomTable;
	//诊区对象
	private HRMClinicArea area;
	//诊间对象
	private HRMClinicRoom room;
	//字符工具类
	private StringUtil util;
	//TABLE的TAG名，TABLE单击时记录其TAG名，删除按钮动作时使用
	private String tableName;
	/**
	 * 初始化事件
	 */
	public void onInit() {
		super.onInit();
		//清空
		onClear();
	}
	/**
	 * 清空
	 */
	private void onClear() {
		//初始化控件
		initComponent();
		//初始化数据
		initData();
	}
	/**
	 * 初始化数据
	 */
	private void initData() {
		//初始化诊区对象
		area=new HRMClinicArea();
		area.onQuery();
		area.newRow(-1);
		//将诊区对象赋值到诊区TABLE上
		areaTable.setDataStore(area);
		areaTable.setDSValue();
		room=new HRMClinicRoom();
		roomTable.removeRowAll();
	}
	/**
	 * 初始化控件
	 */
	private void initComponent() {
		areaTable=(TTable)this.getComponent("AREA_TABLE");
		roomTable=(TTable)this.getComponent("ROOM_TABLE");
		//诊区TABLE值改变事件
		areaTable.addEventListener("AREA_TABLE->"+ TTableEvent.CHANGE_VALUE,this, "onAreaValueChange");
		//诊间TABLE值改变事件
		roomTable.addEventListener("ROOM_TABLE->"+ TTableEvent.CHANGE_VALUE,this, "onRoomValueChange");
		//套餐主项CHECK_BOX事件
		roomTable.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
		"onCheckClicked");
	}
	/**
	 * 套餐主项的CHECK_BOX事件
	 * @param obj
	 * @return
	 */
	public boolean onCheckClicked(Object obj){
		TTable table=(TTable)obj;
		table.acceptText();
		table.setDSValue();
		return false;
	}
	/**
	 * 诊区TABLE单击事件
	 */
	public void onAreaClick(){
		int row=areaTable.getSelectedRow();
		if(row<0){
			return;
		}
		String areaCode=area.getItemString(row, "CLINICAREA_CODE");
		if(util.isNullString(areaCode)){
			return;
		}
		if("ROOM_TABLE".equalsIgnoreCase(tableName)&&room.isModified()){
			int yesNo=this.messageBox("提示信息", "是否保存诊间信息", this.YES_NO_OPTION);
			if(yesNo==0){
				onNewRoom();
			}
		}
		//根据诊区TABLE中选择的诊区代码，初始化诊间对象并赋值给诊间TABLE
		room.onQuery(areaCode,area.getItemString(row, "REGION_CODE"));
		room.newRow(-1);
		roomTable.setDataStore(room);
		roomTable.setDSValue();
		tableName="AREA_TABLE";
	}
	/**
	 * 诊间TABLE单击事件
	 */
	public void onRoomClick(){
		int row=roomTable.getSelectedRow();
		if(row<0){
			return;
		}
		//如果上一次操作的TABLE是诊区TABLE而诊区对象又有数据改变，则提示是否保存，选是则保存
		if("AREA_TABLE".equalsIgnoreCase(tableName)&&area.isModified()){
			int yesNo=this.messageBox("提示信息", "是否保存诊区信息", this.YES_NO_OPTION);
			if(yesNo==0){
				//保存诊区对象的数据
				onNewArea();
			}
		}
		tableName="ROOM_TABLE";
	}
	/**
	 * 删除
	 */
	public void onDelete(){
		//根据操作的TABLE的TAG名，实例化TABLE
		TTable table=(TTable)this.getComponent(tableName);
		int row=table.getSelectedRow();
		TParm result;
		//如果是诊间TABLE删除选中行并返回
		if("ROOM_TABLE".equalsIgnoreCase(tableName)){
			room.deleteRow(row);
			result=room.onSave();
			if(result.getErrCode()!=0){
				this.messageBox("E0001");
			}else{
				this.messageBox("P0001");
			}
			onClear();
			return;
		}
		//验证输出结果
		result=room.deleteByAreaCode();
		if(result.getErrCode()!=0){
			this.messageBox("E0001");
			return;
		}
		//删除诊区，验证删除数据并返回
		area.deleteRow(row);
		result=area.onSave();
		if(result.getErrCode()!=0){
			this.messageBox("E0001");
			return;
		}
		this.messageBox("P0001");
		onClear();
	}
	/**
	 * 保存
	 */
	public void onSave(){
            areaTable.acceptText();
            TParm result=new TParm();
            if(area.isModified()){
                result=area.onSave();
            }
            if(result.getErrCode()!=0){
                this.messageBox_("诊区保存失败");
                return;
            }else{
                area.resetModify();
            }
            //		this.messageBox_(room.isModified());
            if(room.isModified()){
                // System.out.println("room is modify");
               // room.showDebug();
                result=room.onSave();
            }
            if(result.getErrCode()!=0){
                this.messageBox_("诊间保存失败");

                return;
            }else{
                    room.resetModify();
            }
            this.messageBox("P0001");
	}
	/**
	 * 保存诊区信息
	 */
	public void onNewArea(){
		if(area.isModified()){
			TParm result=area.onSave();
			if(result.getErrCode()!=0){
				this.messageBox("E0002");
			}else{
				this.messageBox("P0002");
			}
		}else{
			this.messageBox_("没有数据可保存");
		}
	}
	/**
	 * 保存诊间信息
	 */
	public void onNewRoom(){
		if(room.isModified()){
			TParm result=room.onSave();
			if(result.getErrCode()!=0){
				this.messageBox("E0002");
			}else{
				this.messageBox("P0002");
			}
		}else{
			this.messageBox_("没有数据可保存");
		}
	}
	/**
	 * 诊区值改变事件，当代码不空，填写诊区名称时新增一行
	 * @param tNode
	 */
	public boolean onAreaValueChange(TTableNode tNode){
		int row=tNode.getRow();
		int column=tNode.getColumn();
		String colName=areaTable.getParmMap(column);
		if(!"CLINICAREA_CODE".equalsIgnoreCase(colName)){
			if(util.isNullString(area.getItemString(row, "CLINICAREA_CODE"))){
				this.messageBox_("请先填写诊区代码");
				return true;
			}
		}
		if("CLINIC_DESC".equalsIgnoreCase(colName)){
			String value=tNode.getValue()+"";
			if(util.isNullString(value)){
				return true;
			}
			String oldValue=tNode.getOldValue()+"";
			if(value.equalsIgnoreCase(oldValue)){
				return true;
			}
			MessageTool d=new MessageTool();
			String py=d.getPy(value);
			area.setActive(row,true);
			area.setItem(row, "PY1", py);
			if(row>0&&!util.isNullString(area.getItemString(area.rowCount()-1, "CLINIC_DESC"))){
				area.newRow(-1);
			}else if(row==0){
				area.newRow(-1);
			}

			areaTable.setDSValue();
			areaTable.getTable().grabFocus();
			areaTable.setSelectedRow(row);
			areaTable.setSelectedColumn(column+1);

		}
		return false;
	}
	/**
	 * 诊间值改变事件，当代码不空，填写诊间名称时新增一行
	 * @param tNode
	 */
	public boolean onRoomValueChange(TTableNode tNode){
		int row=tNode.getRow();
		int column=tNode.getColumn();
		String colName=roomTable.getParmMap(column);
		if(!"CLINICROOM_NO".equalsIgnoreCase(colName)){
			if(util.isNullString(room.getItemString(row, "CLINICROOM_NO"))){
				this.messageBox_("请先填写诊室代码");
				return true;
			}
		}
		if("CLINICROOM_DESC".equalsIgnoreCase(colName)){
			String value=tNode.getValue()+"";
			if(util.isNullString(value)){
				return true;
			}
			String oldValue=tNode.getOldValue()+"";
			if(value.equalsIgnoreCase(oldValue)){
				return true;
			}
			MessageTool d=new MessageTool();
			String py=d.getPy(value);
			room.setActive(row,true);
			room.setItem(row, "PY1", py);
			if(row>0&&!util.isNullString(room.getItemString(room.rowCount()-1, "CLINICROOM_NO"))){
				room.newRow(-1);
			}else if(row==0){
				room.newRow(-1);
			}

			roomTable.setDSValue();
			roomTable.getTable().grabFocus();
			roomTable.setSelectedRow(row);
			roomTable.setSelectedColumn(column+1);
		}
		return false;
	}

}

