package com.javahis.component;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;

import javax.swing.BorderFactory;
import com.tiis.ui.TiLabel;
import com.tiis.ui.TiPanel;
import com.tiis.util.TiString;



/**
 *
 * 床头卡 - 细卡
 * @author whaosoft
 *
 */
public class T_Card extends BaseCard {


	/**
	 *
	 */
	private static final long serialVersionUID = -6267546297516623036L;


	public TiLabel tiL_bedno = new TiLabel();
	public TiLabel tiL_name = new TiLabel();
	public TiLabel tiL_mrno = new TiLabel();
	public TiLabel tiL_ipdno = new TiLabel();
	public TiLabel sex = new TiLabel();
	public TiLabel age = new TiLabel();
	public TiLabel ctz = new TiLabel();
	public TiLabel DANGER = new TiLabel();
	public TiLabel nursing_class = new TiLabel();
	public TiLabel VS_dr = new TiLabel();
	public TiLabel OPERATION = new TiLabel();
	public TiPanel tiPanel1 = new TiPanel();


	public String sSex = "";
	public String sCtz = "";
	public String sDanger = "";
	public String sUserName = "";
	public String sOp = "";
	public String sICD = "";

	public int sNURSING_red = 255;
	public int sNURSING_green = 255;
	public int sNURSING_blue = 255;
	public int sPATIENT_red = 255;
	public int sPATIENT_green = 255;
	public int sPATIENT_blue = 255;

	public String sHosp_area = "";
	public String sBIRTH_DATE = "";// 出生日期
	public String sIns_STATUS = "";
	public String sClp_code = ""; // lur 修改 2006-04-06


	public TiLabel icd_1 = new TiLabel();
	public TiLabel tiLabel1 = new TiLabel();
	public TiLabel tiLabel2 = new TiLabel();
	public TiLabel tiLabel3 = new TiLabel();
	public TiLabel tiLabel4 = new TiLabel();

	public T_Card() {
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public T_Card(String sBed_no, String sNURSING_CLASS,String baby_bed_flg) {// 空床
		try {
			this.sBed_no = sBed_no;
			this.sNURSING_CLASS = sNURSING_CLASS;
			this.baby_bed_flg = baby_bed_flg;
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public T_Card(String sBed_no, String sNURSING_CLASS,String code,String baby_bed_flg) {// 空床
		try {
			this.sBed_no = sBed_no;
			this.sNURSING_CLASS = sNURSING_CLASS;
			this.sCode = code;
			this.baby_bed_flg = baby_bed_flg;
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public T_Card(String sBed_no, String sName, String sMr_no,
			String sNURSING_CLASS, String sStation_code, String array[]) {//
		try {
			this.sBed_no = sBed_no;
			this.sName = sName;
			this.sMr_no = sMr_no;
			this.sNURSING_CLASS = sNURSING_CLASS;
			this.sStation_code = sStation_code;

			//
			List<String> list = this.getDataList(array);

			//System.out.println("array[0]"+array[0]);
			this.sSex = this.getData(list,0);
			this.sAge = this.getData(list,5);
			this.sCtz = this.getData(list,1);
			this.sDanger = this.getData(list,2);
			this.sUserName = this.getData(list,3);
			this.sOp = this.getData(list,4);
			this.sInDate = this.getData(list,6);
			this.sICD = this.getData(list,7);
			this.sNURSING_red = this.getColorValue( this.getData(list,8) );
			this.sNURSING_green = this.getColorValue( this.getData(list,9) );
			this.sNURSING_blue = this.getColorValue( this.getData(list,10) );
			this.sPATIENT_red = this.getColorValue( this.getData(list,11) );
			this.sPATIENT_green = this.getColorValue( this.getData(list,12) );
			this.sPATIENT_blue = this.getColorValue( this.getData(list,13) );
			this.occupy_bed_flg = this.getData(list,14);
			this.sHosp_area = this.getData(list,15);
			this.sCase_no = this.getData(list,16);
			this.sBIRTH_DATE = this.getData(list,17);// 出生日期
			this.sSex_code = this.getData(list,18);// 性别代码
			this.sIns_STATUS = this.getData(list,19);
			this.sClp_code = this.getData(list,20);// lur 修改 2006-04-06
			this.sDeptCode = this.getData(list,21);
			this.sIpdNo = this.getData(list,22);

			//
			this.sCode = this.getData(list,23);
			this.sBedStatus = this.getData(list,24);
			this.sVs_dr_code = this.getData(list,25);
			this.sAttend_dr_code = this.getData(list,26);
			this.sDirector_dr_code = this.getData(list,27);
			this.sVs_nurse_code = this.getData(list,28);
			this.sPatient_condition = this.getData(list,29);
			this.new_born_flg = this.getData(list,30);
			this.new_age = this.getData(list,31);
			this.baby_bed_flg = this.getData(list,32);

			//
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void jbInit() throws Exception {

		tiL_bedno.setBackground(Color.black);
		tiL_bedno.setFont(new java.awt.Font("宋体", 1, 12));
		tiL_bedno.setForeground(Color.black);
		tiL_bedno.setText(this.sBed_no);
		tiL_bedno.setBounds(new Rectangle(4, 5, 43, 14));
		//性别颜色不同
		if (this.sSex.equals("女")) {
			this.setBackground(new Color(255, 174, 176));
			this.setForeground(new Color(255, 174, 176));
		} else {
			this.setBackground(Color.white);
			this.setForeground(Color.white);
		}
		//床位号部分颜色(护理级别有关)
		if (this.sNURSING_red == 0 && this.sNURSING_green == 0
				&& this.sNURSING_blue == 0) {
			this.sNURSING_red = 255;
			this.sNURSING_green = 255;
			this.sNURSING_blue = 255;
		}
		tiPanel1.setBackground(new Color(this.sNURSING_red,
				this.sNURSING_green, this.sNURSING_blue));

		//tiPanel1.setBackground(new Color(255,255,255))
		tiPanel1.setForeground(new Color(this.sNURSING_red,
				this.sNURSING_green, this.sNURSING_blue));
		//tiPanel1.setForeground(new Color(255,255,255));

		this.setFont(new java.awt.Font("Dialog", 0, 11));
		this.setBorder(BorderFactory.createEtchedBorder());

		this.setLayout(null);
		tiL_name.setText(this.sName);
		tiL_name.setBounds(new Rectangle(62, 4, 65, 24));
		tiL_mrno.setText("病案号：" + this.sMr_no);
		tiL_mrno.setBounds(new Rectangle(5, 76, 148, 15));

		/** this.addMouseListener(this); */
		tiPanel1.setBounds(new Rectangle(5, 6, 50, 21));
		tiPanel1.setLayout(null);
		sex.setText("性别：" + this.sSex);
		sex.setBounds(new Rectangle(5, 34, 60, 19));

		//旧年龄显示方式-停用
		//age.setText("年龄：" + this.sAge);
		//age.setBounds(new Rectangle(70, 33, 62, 19));

		ctz.setText("付款方式：" + sCtz);
		ctz.setRequirement(false);
		ctz.setBounds(new Rectangle(5, 93, 150, 20));

		String str[] = TiString.fixRow(TiString.breakRow(this.sICD, 20), 1);
		DANGER.setText("诊断：" + str[0]);
		//		if (str.length > 1) {
		//			icd_1.setText(str[1]);
		//		}

		DANGER.setBounds(new Rectangle(5, 153, 159, 20));

		tiL_ipdno.setText("住院号：" + this.sIpdNo);
		tiL_ipdno.setBounds(new Rectangle(5, 150, 159, 20));

		nursing_class.setText("护理级别：" + this.sNURSING_CLASS);
		nursing_class.setBounds(new Rectangle(6, 123, 123, 22));
		VS_dr.setText("经治医师：" + this.sUserName);
		//111
		VS_dr.setBounds(new Rectangle(5, 133, 140, 21));
		//##
		String l9_sInDate = null;
		if( this.sInDate.length()<10 ){
			l9_sInDate = "";
		}else{
			l9_sInDate = this.sInDate.substring(0,10);
		}
		OPERATION.setText("入院日期：" + l9_sInDate);
		//126
		OPERATION.setBounds(new Rectangle(5, 114, 156, 18));

		icd_1.setBounds(new Rectangle(37, 155, 133, 18));
		// this.add(nursing_class, null);

		//病情
		if( null!=this.sDanger && !this.sDanger.equals("") ){
			if( !this.sDanger.equals("S2") ){
				tiLabel1.setText("※");
				tiLabel1.setForeground(new Color(this.sPATIENT_red,
						this.sPATIENT_green, this.sPATIENT_blue));
				tiLabel1.setBounds(new Rectangle(122, 9, 16, 17));
				this.add(tiLabel1, null);
			}
		}

		tiLabel2.setText("包");
		tiLabel2.setBounds(new Rectangle(136, 9, 17, 15));
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
		tiLabel3.setBounds(new Rectangle(152, 10, 15, 15));
		tiLabel4.setBounds(new Rectangle(106, 10, 48, 15));

		if (!this.sClp_code.equals(""))
			tiLabel4.setText("△");
		tiLabel4.setForeground(Color.GREEN);
		this.add(tiPanel1, null);
		tiPanel1.add(tiL_bedno, null);
		this.add(sex, null);
		this.add(age, null);
		this.add(tiL_mrno, null);
		this.add(ctz, null);
		this.add(OPERATION, null);
		this.add(VS_dr, null);
		this.add(DANGER, null);
		this.add(icd_1, null);
		this.add(tiL_name, null);
		this.add(tiLabel3, null);
		this.add(tiLabel4, null);
		this.add(tiL_ipdno, null);
		if (this.occupy_bed_flg.equals("Y")) {
			this.add(tiLabel2, null);
		}

		//婴儿床
        if( null!=this.baby_bed_flg && "Y".equals(this.baby_bed_flg) ){
        	TiLabel tiL_baby_bed_flg = new TiLabel();
        	tiL_baby_bed_flg.setText("婴");
        	tiL_baby_bed_flg.setBounds(new Rectangle(130, 34, 14, 14));
    		this.add(tiL_baby_bed_flg, null);
        }

        //新年龄
    	TiLabel tiL_new_age = new TiLabel();
    	tiL_new_age.setText("年龄：" + this.new_age);
    	tiL_new_age.setBounds(new Rectangle(5, 57, 140, 14));
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

	public String getSAge() {
		return sAge;
	}

	public void setSAge(String sAge) {
		this.sAge = sAge;
	}

	public String getSCtz() {
		return sCtz;
	}

	public void setSCtz(String sCtz) {
		this.sCtz = sCtz;
	}

	public String getSDanger() {
		return sDanger;
	}

	public void setSDanger(String sDanger) {
		this.sDanger = sDanger;
	}

	public String getSUserName() {
		return sUserName;
	}

	public void setSUserName(String sUserName) {
		this.sUserName = sUserName;
	}

	public String getSOp() {
		return sOp;
	}

	public void setSOp(String sOp) {
		this.sOp = sOp;
	}

	public String getSInDate() {
		return sInDate;
	}

	public void setSInDate(String sInDate) {
		this.sInDate = sInDate;
	}

	public String getSICD() {
		return sICD;
	}

	public void setSICD(String sICD) {
		this.sICD = sICD;
	}

}
