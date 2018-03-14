package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;

public class INDStoreControl extends TControl {

	private Timer timer;

	private TimerTask task;

	private long period = 5 * 1000;

	private long delay = 5 * 1000;

	private static int i = 0;			

	public void onInit() {
		initTimer();
		schedule();			
	}

	public void initTimer() {
		this.timer = new Timer();
		// setPeriod();
		this.task = new TimerTask() {
			public void run() {
				onQuery();
			}
		};
	}

	public INDStoreControl() {
		super();
	}

	public void schedule() {									
		this.task = new TimerTask() {
			public void run() {
				onQuery();				
			}
		};
		this.timer.schedule(this.task, this.delay, this.period);
	}

	/**																						
	 * ²éÑ¯·½·¨
	 */
	public void onQuery() {			
		TParm ccbParm = new TParm();
		List flgList = new ArrayList();
		flgList.add(true);
	//	flgList.add(false);						              								
	//	List cIDList = new ArrayList();
	//	cIDList.add("001");
	//	cIDList.add("002");					   				
		List cNameList = new ArrayList();   
		cNameList.add("Âð·ÈÈÝÆ÷Ò»");
	//	cNameList.add("ÈÝÆ÷ËÄ");
	//	List cCodeList = new ArrayList();				
	//	cCodeList.add("A03");
	//	cCodeList.add("A04");			
		List durgList = new ArrayList();
	//	durgList.add("ÑÎËáÂð·È×¢ÉäÒº");
		durgList.add("èÛéÚËá·ÒÌ«Äá×¢ÉäÒº");
		List speList = new ArrayList();
		speList.add("10mg");
	//	speList.add("2ml:0.1mg");
		List numList = new ArrayList();
		numList.add("2");
	//	numList.add("2");
		List unitList = new ArrayList();
		unitList.add("Ö§");
	//	unitList.add("Ö§");
		List qlList = new ArrayList();
		qlList.add("2");
	//	qlList.add("5");
		List sjList = new ArrayList();
		sjList.add("2");
	//	sjList.add("5");
		List pcList = new ArrayList();
		pcList.add("10");
	//	pcList.add("1110694");
		List xqList = new ArrayList();
		xqList.add("10");
	//	xqList.add("2013-01-01");
		List serList = new ArrayList();
		serList.add("000001~000010");
	//	serList.add("000011~000020");
		for (int i = 0; i < flgList.size(); i++) {
		//	ccbParm.addData("FLG", flgList.get(i));
		//	ccbParm.addData("CONTAINER_ID", cIDList.get(i));
		//	ccbParm.addData("CONTAINER_CODE", cCodeList.get(i));
			ccbParm.addData("CONTAINER_NAME", cNameList.get(i));
			ccbParm.addData("DURG_NAME", durgList.get(i));
			ccbParm.addData("SPECIFICATION", speList.get(i));
			ccbParm.addData("NUM", numList.get(i));
			ccbParm.addData("UNIT", unitList.get(i));
			ccbParm.addData("QLNUM", qlList.get(i));
			ccbParm.addData("SJNUM", sjList.get(i));
			ccbParm.addData("SER", serList.get(i));
			ccbParm.addData("VAILDATE", xqList.get(i));
			ccbParm.addData("PC", pcList.get(i));
		}
		this.setValue("tTextField_4", "¸¶ÎÄ¿¡");
		this.setValue("tTextFormat_0", SystemTool.getInstance().getDate());
		this.setValue("tTextFormat_2", SystemTool.getInstance().getDate());					
		this.callFunction("UI|tTable_0|setParmValue", ccbParm);

	}
	
	/**
	 * ²éÑ¯·½·¨
	 */
	public void onQuery1() {
		TParm ccbParm = new TParm();
		List flgList = new ArrayList();
		flgList.add(true);
		flgList.add(false);
		List cIDList = new ArrayList();
		cIDList.add("001_" + i);
		cIDList.add("002_" + i);
		List cNameList = new ArrayList();
		cNameList.add("Âð·ÈÈÝÆ÷Ò»" + i);
		cNameList.add("Âð·ÈÈÝÆ÷Ò»" + i);
		List cCodeList = new ArrayList();
		cCodeList.add("A03");
		cCodeList.add("A04");
		List durgList = new ArrayList();
		durgList.add("ÑÎËáÂð·È×¢ÉäÒº");
		durgList.add("èÛéÚËá·ÒÌ«Äá×¢ÉäÒº");
		List speList = new ArrayList();
		speList.add("10mg");
		speList.add("2ml:0.1mg");
		List numList = new ArrayList();
		numList.add("2");
		numList.add("2");
		List unitList = new ArrayList();
		unitList.add("Ö§");
		unitList.add("Ö§");
		List qlList = new ArrayList();
		qlList.add("2");
		qlList.add("5");
		List sjList = new ArrayList();
		sjList.add("2");
		sjList.add("5");
		List pcList = new ArrayList();
		pcList.add("1110694");
		pcList.add("1110694");
		List xqList = new ArrayList();
		xqList.add("2013-01-01");
		xqList.add("2013-01-01");
		List serList = new ArrayList();
		serList.add("000001~000010");
		serList.add("000011~000020");
		for (int i = 0; i < flgList.size(); i++) {
			ccbParm.addData("FLG", flgList.get(i));
			ccbParm.addData("CONTAINER_ID", cIDList.get(i));
			ccbParm.addData("CONTAINER_CODE", cCodeList.get(i));
			ccbParm.addData("CONTAINER_NAME", cNameList.get(i));
			ccbParm.addData("DURG_NAME", durgList.get(i));
			ccbParm.addData("SPECIFICATION", speList.get(i));
			ccbParm.addData("NUM", numList.get(i));
			ccbParm.addData("UNIT", unitList.get(i));
			ccbParm.addData("QLNUM", qlList.get(i));
			ccbParm.addData("SJNUM", sjList.get(i));
			ccbParm.addData("SER", serList.get(i));
			ccbParm.addData("VAILDATE", xqList.get(i));
			ccbParm.addData("PC", pcList.get(i));
		}
		i++;													
		this.callFunction("UI|tTable_1|setParmValue", ccbParm);

	}

	public void onClick() {
		TTextField t = (TTextField) this.getComponent("tTextField_2");
		t.setValue("20121016001");
		onQuery();
		((TTextField) getComponent("tTextField_3")).grabFocus();
	}

	public void conClick() {
		TTextField t = (TTextField) this.getComponent("tTextField_3");
		t.setValue("ÈÝÆ÷Èý");
		TTable table = this.getTTable("tTable_0");
		table.removeRow(0);
		TTable table1 = this.getTTable("tTable_0");
		TParm ccbParm = new TParm();
		List flgList = new ArrayList();
		flgList.add(true);
		flgList.add(false);
		List cIDList = new ArrayList();
		cIDList.add("001");
		// cIDList.add("002");
		List cNameList = new ArrayList();
		cNameList.add("Âð·ÈÈÝÆ÷Ò»");						
		// cNameList.add("ÈÝÆ÷ËÄ");
		List cCodeList = new ArrayList();
		cCodeList.add("A03");
		// cCodeList.add("A04");
		List durgList = new ArrayList();
		durgList.add("ÑÎËáÂð·È×¢ÉäÒº");
		// durgList.add("èÛéÚËá·ÒÌ«Äá×¢ÉäÒº");
		List speList = new ArrayList();
		speList.add("10mg");
		// speList.add("2ml:0.1mg");
		List numList = new ArrayList();
		numList.add("2");
		// numList.add("2");
		List unitList = new ArrayList();
		unitList.add("Ö§");
		// unitList.add("Ö§");
		List qlList = new ArrayList();
		qlList.add("2");
		// qlList.add("5");
		List sjList = new ArrayList();
		sjList.add("2");
		// sjList.add("5");
		List pcList = new ArrayList();
		pcList.add("1110694");
		// pcList.add("1110694");
		List xqList = new ArrayList();
		xqList.add("2013-01-01");
		// xqList.add("2013-01-01");
		List serList = new ArrayList();
		serList.add("000001~000010");
		// serList.add("000011~000020");
		for (int i = 0; i < cNameList.size(); i++) {
			ccbParm.addData("CONTAINER_NAME", cNameList.get(i));
			ccbParm.addData("DURG_NAME", durgList.get(i));
			ccbParm.addData("SPECIFICATION", speList.get(i));
			ccbParm.addData("NUM", numList.get(i));
			ccbParm.addData("UNIT", unitList.get(i));
			ccbParm.addData("QLNUM", qlList.get(i));
			ccbParm.addData("SJNUM", sjList.get(i));
			ccbParm.addData("SER", serList.get(i));
			ccbParm.addData("VAILDATE", xqList.get(i));
			ccbParm.addData("PC", pcList.get(i));
		}
		callFunction("UI|save|setEnabled", true);
		this.callFunction("UI|tTable_1|setParmValue", ccbParm);
		this.getTTable("tTable_3").removeRowAll();
	}

	/**
	 * »ñÈ¡table¶ÔÏó
	 * 
	 * @param tableName
	 * @return
	 */
	public TTable getTable(String tableName) {
		return (TTable) this.getComponent(tableName);
	}

	/**
	 * Çå¿Õ
	 */
	public void onClear() {
		this.getTTable("tTable_0").removeRowAll();
		this.getTTable("tTable_1").removeRowAll();
	}

	/**
	 * µÃµ½TABLE
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	public void onTableDClicked() {
		TParm ccbParm = new TParm();
		List flgList = new ArrayList();
		flgList.add(true);
		flgList.add(false);
		List cIDList = new ArrayList();
		cIDList.add("001");
		cIDList.add("002");
		List cNameList = new ArrayList();
		cNameList.add("ÈÝÆ÷Èý");
		cNameList.add("ÈÝÆ÷Èý");
		List cCodeList = new ArrayList();
		cCodeList.add("A03");
		cCodeList.add("A03");
		List durgList = new ArrayList();
		durgList.add("ÑÎËáÂð·È×¢ÉäÒº");
		durgList.add("ÑÎËáÂð·È×¢ÉäÒº");
		List speList = new ArrayList();
		speList.add("10mg");
		speList.add("10mg");
		List numList = new ArrayList();
		numList.add("1");
		numList.add("1");
		List unitList = new ArrayList();
		unitList.add("Ö§");
		unitList.add("Ö§");
		List qlList = new ArrayList();
		qlList.add("2");
		qlList.add("2");
		List sjList = new ArrayList();
		sjList.add("2");
		sjList.add("2");
		List pcList = new ArrayList();
		pcList.add("1110694");
		pcList.add("1110694");
		List xqList = new ArrayList();
		xqList.add("2013-01-01");
		xqList.add("2013-01-01");
		List serList = new ArrayList();
		serList.add("000001");
		serList.add("000002");
		for (int i = 0; i < flgList.size(); i++) {
			ccbParm.addData("FLG", flgList.get(i));
			ccbParm.addData("CONTAINER_ID", cIDList.get(i));
			ccbParm.addData("CONTAINER_CODE", cCodeList.get(i));
			ccbParm.addData("CONTAINER_NAME", cNameList.get(i));
			ccbParm.addData("DURG_NAME", durgList.get(i));
			ccbParm.addData("SPECIFICATION", speList.get(i));
			ccbParm.addData("NUM", numList.get(i));
			ccbParm.addData("UNIT", unitList.get(i));
			ccbParm.addData("QLNUM", qlList.get(i));
			ccbParm.addData("SJNUM", sjList.get(i));
			ccbParm.addData("SER", serList.get(i));
			ccbParm.addData("VAILDATE", xqList.get(i));
			ccbParm.addData("PC", pcList.get(i));
		}
		this.callFunction("UI|tTable_3|setParmValue", ccbParm);
	}

	public void onTableClicked() {
		TParm ccbParm = new TParm();
		List flgList = new ArrayList();
		flgList.add(true);
		flgList.add(false);
		List cIDList = new ArrayList();
		cIDList.add("001");
		cIDList.add("002");
		List cNameList = new ArrayList();
		cNameList.add("ÈÝÆ÷Èý");
		cNameList.add("ÈÝÆ÷Èý");
		List cCodeList = new ArrayList();
		cCodeList.add("A03");
		cCodeList.add("A03");
		List durgList = new ArrayList();
		durgList.add("ÑÎËáÂð·È×¢ÉäÒº");
		durgList.add("ÑÎËáÂð·È×¢ÉäÒº");
		List speList = new ArrayList();
		speList.add("10mg");
		speList.add("10mg");
		List numList = new ArrayList();
		numList.add("1");
		numList.add("1");
		List unitList = new ArrayList();
		unitList.add("Ö§");
		unitList.add("Ö§");
		List qlList = new ArrayList();
		qlList.add("2");
		qlList.add("2");
		List sjList = new ArrayList();
		sjList.add("2");
		sjList.add("2");
		List pcList = new ArrayList();
		pcList.add("1110694");
		pcList.add("1110694");
		List xqList = new ArrayList();
		xqList.add("2013-01-01");
		xqList.add("2013-01-01");
		List serList = new ArrayList();
		serList.add("000001");
		serList.add("000002");
		for (int i = 0; i < flgList.size(); i++) {
			ccbParm.addData("FLG", flgList.get(i));
			ccbParm.addData("CONTAINER_ID", cIDList.get(i));
			ccbParm.addData("CONTAINER_CODE", cCodeList.get(i));
			ccbParm.addData("CONTAINER_NAME", cNameList.get(i));
			ccbParm.addData("DURG_NAME", durgList.get(i));
			ccbParm.addData("SPECIFICATION", speList.get(i));
			ccbParm.addData("NUM", numList.get(i));
			ccbParm.addData("UNIT", unitList.get(i));
			ccbParm.addData("QLNUM", qlList.get(i));
			ccbParm.addData("SJNUM", sjList.get(i));
			ccbParm.addData("SER", serList.get(i));
			ccbParm.addData("VAILDATE", xqList.get(i));
			ccbParm.addData("PC", pcList.get(i));
		}
		this.callFunction("UI|tTable_2|setParmValue", ccbParm);
	}

}
