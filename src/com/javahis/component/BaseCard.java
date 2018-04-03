package com.javahis.component;

import java.util.ArrayList;
import java.util.List;

import com.tiis.ui.TiMultiPanel;


/**
 *
 * 床头卡基类
 * @author whaosoft
 *
 */
public class BaseCard extends TiMultiPanel{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

    /** 床号 */
	public String sBed_no = "";
	/** 病案号 */
	public String sMr_no = "";

	/** 这个字段本来应该是 数据库中真正的 bed_no 但实际中但都把bed_no里放成了 bed_desc了 */
	public String sCode = "";

    /** 床位状态 */
	public String sBedStatus = "";

	/** 经治医师 */
	public String sVs_dr_code = null;
	/** 主治医师 */
	public String sAttend_dr_code = null;
	/** 科主任 */
	public String sDirector_dr_code = null;
	/** 主管护士 */
	public String sVs_nurse_code = null;
	/** 入院状态 */
	public String sPatient_condition = null;

	//
	public String sIpdNo = "";
	public String sCase_no = "";
	public String sNURSING_CLASS = "";
	public String sName = "";
	public String sStation_code = "";
	public String sDeptCode = "";
	public String occupy_bed_flg = "";
	public String sSex_code = "";// 性别代码
	public String sAge = "";
	public String sInDate = "";

	/** 新生儿 */
	public String new_born_flg = null;
	/** 婴儿床位 */
	public String baby_bed_flg = null;
	/** 新年龄显示方法 */
	public String new_age = "";


	/**
	 *
	 * @param array
	 * @return
	 */
	public List<String> getDataList(String[] array){

		List<String> list = new ArrayList<String>();
		for(  String str:array ){
			list.add(str);
		}
		array = null;

		return list;
	}

	/**
	 *
	 * @param value
	 * @return
	 */
	public int getColorValue( String value ){

		return Integer.parseInt( value.equals("") ? "255": value.trim() );
	}

	/**
	 *
	 * @param list
	 * @param index
	 * @return
	 */
	public String getData(List<String> list,int index){

		if( list.size()>index ){
			return list.get(index);
		}else{
			return "";
		}
	}
}
