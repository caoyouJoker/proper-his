package com.javahis.component;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import com.tiis.ui.TiLabel;
import com.tiis.ui.TiPanel;


/**
 *
 * 床头卡 - 简卡
 * @author whaosoft
 *
 */
public class S_Card extends BaseCard {


	/**
	 *
	 */
	private static final long serialVersionUID = 6685653491843293933L;


	public TiLabel tiL_bedno = new TiLabel();
	public TiLabel tiL_name = new TiLabel();
	public TiLabel tiL_mrno = new TiLabel();
	public TiPanel tiPanel1 = new TiPanel();
	public TiLabel tiLabel1 = new TiLabel();
	public TiLabel tiLabel2 = new TiLabel();

	public int sNURSING_red = 255;
	public int sNURSING_green = 255;
	public int sNURSING_blue = 255;
	public int sPATIENT_red = 255;
	public int sPATIENT_green = 255;
	public int sPATIENT_blue = 255;

	public String sSex = "";
	public String sPATIENT_STATUS = "";
	public String sHosp_area = "";
	public String roomCode = "";
	public String bedDesc = "";
	public String sBIRTH_DATE = "";// 出生日期
	public String sIns_STATUS = "";
	public String sClp_code = "";
	public String sDanger = "";

	public TitledBorder titledBorder1;
	public TiLabel tiLabel3 = new TiLabel();// 性别代码
	public TiLabel tiLabel4 = new TiLabel();//


	public S_Card() {
		try {
			jbInit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public S_Card(String sBed_no, String sNURSING_CLASS) {// 空床 （泰心显示）
		try {
			this.sBed_no = sBed_no;
			this.sNURSING_CLASS = sNURSING_CLASS;
			jbInit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//爱育华空床显示  zhangh -- 多显示roomcode
	public S_Card(String sBed_no, String sNURSING_CLASS, String roomCode, String bedDesc,String baby_bed_flg) {
		try {
			this.sBed_no = sBed_no;
			this.roomCode = roomCode;
			this.bedDesc = bedDesc;
			this.sNURSING_CLASS = sNURSING_CLASS;
			this.baby_bed_flg = baby_bed_flg;
			jbInit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public S_Card(String sBed_no, String sNURSING_CLASS,String code,String baby_bed_flg) {// 空床
		try {
			this.sBed_no = sBed_no;
			this.sNURSING_CLASS = sNURSING_CLASS;
			this.sCode = code;
			this.baby_bed_flg = baby_bed_flg;
			jbInit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param sBed_no
	 * @param sName
	 * @param sMr_no
	 * @param sNURSING_CLASS
	 * @param sStation_code
	 * @param sSex
	 * @param array
	 */
	public S_Card(String sBed_no, String sName, String sMr_no,
			String sNURSING_CLASS, String sStation_code, String sSex,
			String[] array) {//
		try {
			this.sBed_no = sBed_no;
			this.sName = sName;
			this.sMr_no = sMr_no;
			this.sNURSING_CLASS = sNURSING_CLASS;
			this.sStation_code = sStation_code;
			this.sSex = sSex;

			//
			List<String> list = this.getDataList(array);

			//System.out.println("array[0]"+array[0]);
			this.sNURSING_red = this.getColorValue( this.getData(list,0) );
			this.sNURSING_green = this.getColorValue( this.getData(list,1) );
			this.sNURSING_blue = this.getColorValue( this.getData(list,2) );
			this.sPATIENT_red = this.getColorValue( this.getData(list,3) );
			this.sPATIENT_green = this.getColorValue( this.getData(list,4) );
			this.sPATIENT_blue = this.getColorValue( this.getData(list,5) );

			this.sPATIENT_STATUS = this.getData(list,6);
			this.occupy_bed_flg = this.getData(list,7);
			this.sHosp_area = this.getData(list,8);
			this.sCase_no = this.getData(list,9);
			this.sBIRTH_DATE = this.getData(list,10);// 出生日期
			this.sSex_code = this.getData(list,11);// 性别代码
			this.sIns_STATUS = this.getData(list,12);
			this.sClp_code = this.getData(list,13);
			this.sDeptCode = this.getData(list,14);
			this.sIpdNo = this.getData(list,15);
			this.roomCode = this.getData(list,16);
			this.bedDesc = this.getData(list,17);

			//
			this.sInDate = this.getData(list,19);
			this.sAge = this.getData(list,18);

			//
			this.sCode = this.getData(list,20);
			this.sBedStatus = this.getData(list,21);
			this.sVs_dr_code = this.getData(list,22);
			this.sAttend_dr_code = this.getData(list,23);
			this.sDirector_dr_code = this.getData(list,24);
			this.sVs_nurse_code = this.getData(list,25);
			this.sPatient_condition = this.getData(list,26);
			this.new_born_flg = this.getData(list,27);
			this.sDanger = this.getData(list,28);
			this.new_age = this.getData(list,29);
			this.baby_bed_flg = this.getData(list,30);

			//
			if( null!=this.bedDesc && !this.bedDesc.trim().equals("") ){
				jbInit(true);
			}else{
				jbInit(false);
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void jbInit(boolean isLong) throws Exception {

		//
		int bedno_width=0;
		//
		if( isLong ){
			bedno_width = 180;
		}else{
			bedno_width = 150;
		}

		//
		titledBorder1 = new TitledBorder("");
		tiL_bedno.setBackground(Color.black);
		tiL_bedno.setFont(new java.awt.Font("宋体", 1, 11));
		tiL_bedno.setForeground(Color.black);

		if(isLong)
			tiL_bedno.setText(roomCode + "-" + this.sBed_no + " " + bedDesc);
		else
			tiL_bedno.setText(this.sBed_no + " " + bedDesc);


		tiL_bedno.setBounds(new Rectangle(0, 0, bedno_width, 21));

		if (this.sSex.equals("女")) {
			this.setBackground(new Color(255, 174, 176));
			this.setForeground(new Color(255, 174, 176));
		} else {
			this.setBackground(Color.white);
			this.setForeground(Color.white);
		}
		//
		if (this.sNURSING_red == 0 && this.sNURSING_green == 0
				&& this.sNURSING_blue == 0) {
			this.sNURSING_red = 255;
			this.sNURSING_green = 255;
			this.sNURSING_blue = 255;
		}
		tiPanel1.setBackground(new Color(this.sNURSING_red,
				this.sNURSING_green, this.sNURSING_blue));
		tiPanel1.setForeground(new Color(this.sNURSING_red,
				this.sNURSING_green, this.sNURSING_blue));
		this.setFont(new java.awt.Font("Dialog", 0, 11));
		this.setBorder(BorderFactory.createEtchedBorder());

		this.setLayout(null);
		tiL_name.setText(sName+"  "+this.sSex);
		tiL_name.setBounds(new Rectangle(2, 23, 73, 19));
		tiL_mrno.setText(sMr_no);
		tiL_mrno.setBounds(new Rectangle(3, 54, 110, 15));
		/** this.addMouseListener(this); */
		tiPanel1.setBounds(new Rectangle(2, 2, 48, 20));
		tiPanel1.setSize(bedno_width-1, 19);
		tiPanel1.setLayout(null);

		//病情
		if( null!=this.sDanger && !this.sDanger.equals("") ){
			if( !this.sDanger.equals("S2") ){
				tiLabel1.setText("※");
				tiLabel1.setForeground(new Color(this.sPATIENT_red,
						this.sPATIENT_green, this.sPATIENT_blue));
				tiLabel1.setBounds(new Rectangle(95, 25, 18, 14));
				this.add(tiLabel1, null);
			}
		}

		tiLabel2.setText("包");
		tiLabel2.setBounds(new Rectangle(128, 24, 18, 15));

		//		//医保确认书
		if (this.sIns_STATUS.equals("0") || this.sIns_STATUS.equals("5")
				|| this.sIns_STATUS.equals("6")) {
			tiLabel3.setText("☆");
		} else if (this.sIns_STATUS.equals("1") || this.sIns_STATUS.equals("2")
				|| this.sIns_STATUS.equals("3") || this.sIns_STATUS.equals("4")
				|| this.sIns_STATUS.equals("7")) {
			tiLabel3.setText("★");
			tiLabel3.setForeground(Color.GREEN);
		} else {
			tiLabel3.setText("");
		}

		tiLabel3.setBounds(new Rectangle(113, 24, 33, 15));

		tiLabel4.setBounds(new Rectangle(80, 25, 48, 15));
		//包含监床路径的
		if (!this.sClp_code.equals(""))
			tiLabel4.setText("△");
		tiLabel4.setForeground(Color.GREEN);

		this.add(tiPanel1, null);
		tiPanel1.add(tiL_bedno, null);

		this.add(tiL_mrno, null);

		if (this.occupy_bed_flg.equals("Y")) {
			this.add(tiLabel2, null);
		}


		this.add(tiL_name, null);
		this.add(tiLabel3, null);
		this.add(tiLabel4, null);

		//婴儿床
        if( null!=this.baby_bed_flg && "Y".equals(this.baby_bed_flg) ){
        	TiLabel tiL_baby_bed_flg = new TiLabel();
        	tiL_baby_bed_flg.setText("婴");
        	tiL_baby_bed_flg.setBounds(new Rectangle(130, 54, 14, 14));
    		this.add(tiL_baby_bed_flg, null);
        }

        //新年龄
    	TiLabel tiL_new_age = new TiLabel();
    	tiL_new_age.setText(this.new_age);
    	tiL_new_age.setBounds(new Rectangle(2, 40, 140, 14));
		this.add(tiL_new_age, null);

	}

	public String getSBed_no() {
		return sBed_no;
	}

	public void setSBed_no(String sBed_no) {
		this.sBed_no = sBed_no;
	}

	public String getSName() {
		return sName;
	}

	public void setSName(String sName) {
		this.sName = sName;
	}

	public String getSMr_no() {
		return sMr_no;
	}

	public void setSMr_no(String sMr_no) {
		this.sMr_no = sMr_no;
	}

	public String getSNURSING_CLASS() {
		return sNURSING_CLASS;
	}

	public void setSNURSING_CLASS(String sNURSING_CLASS) {
		this.sNURSING_CLASS = sNURSING_CLASS;
	}

	public String getSStation_code() {
		return sStation_code;
	}

	public void setSStation_code(String sStation_code) {
		this.sStation_code = sStation_code;
	}

	public String getSSex() {
		return sSex;
	}

	public void setSSex(String sSex) {
		this.sSex = sSex;
	}

}
