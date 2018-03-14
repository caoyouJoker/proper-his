package com.javahis.ui.inw;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TDialog;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.tiis.ui.TiLabel;
import com.tiis.ui.TiMultiPanel;
import com.tiis.ui.TiPanel;
import com.tiis.util.TiString;

/**
 * 
 * 床头卡功能
 * 
 * @author lixiang
 * 
 */
public class INWBedCardControl extends TControl  {

	private TDialog mainFrame;
	TPanel tiPanel1;
	TiPanel tiPanel2 = new TiPanel();
	JScrollPane jScrollPane1 = new JScrollPane();
	//TiPanel tiPanel3 = new TiPanel();
	
	TPanel tiPanel3 = new TPanel();
	TitledBorder testBorder3;
	GridLayout gridLayout1 = new GridLayout();
	// 病区
	private TTextFormat stationCode;
	//隐藏人员、
	private TTextFormat userId;
	/**
	 * 简卡单选
	 */
	private TRadioButton sCard;
	/**
	 * 细卡单选
	 */
	private TRadioButton tCard;
	/**
	 * 床位总数
	 */
	private TTextField bedsCount;

	/**
     * 
     */
	public INWBedCardControl() {

	}

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		mainFrame = ((TDialog) getComponent("UI"));
		mainFrame.setResizable(true);
		//mainFrame.setState(0);
		// mainFrame.setUndecorated(true);
		tiPanel1 = ((TPanel) getComponent("tPanel_0"));
		//testBorder3 = new TitledBorder(BorderFactory.createEtchedBorder(new Color(228, 255, 255),new Color(112, 154, 161)),"测试重画");
		tiPanel2.setBounds(new Rectangle(0, 1, 1020, 630));
		tiPanel2.setSize(1010, 680);
		tiPanel2.setLayout(null);
		jScrollPane1.setBounds(new Rectangle(3, 12, 1000, 620));
		tiPanel1.add(tiPanel2, null);
		tiPanel2.add(jScrollPane1, null);
		//test
		//tiPanel3.setBorder(testBorder3);
		tiPanel3.setBorder("");
		jScrollPane1.getViewport().add(tiPanel3, null);
		sCard = (TRadioButton) this.getComponent("S_CARD");
		tCard = (TRadioButton) this.getComponent("T_CARD");
		bedsCount= (TTextField) this.getComponent("tTextField_0");

		this.stationCode = (TTextFormat) this.getComponent("STATION_CODE");
		this.userId = (TTextFormat) this.getComponent("USER_ID");
		String userid = Operator.getID();
		// this.messageBox("==currentStation=="+currentStation);
		this.userId.setValue(userid);
		stationCode.setValue(Operator.getStation());
		onQueryData();
	}

	/**
     * 
     */
	public void onQueryData() {
		//this.messageBox("执行查询");
		this.buildBedData();
	}

	//

	/**
	 * 动态构造床位数据
	 */
	public void buildBedData() {
		tiPanel3.removeAll();
		int j = 0;
		// this.messageBox("sCard"+sCard);
		// 判断 该病区在系统中未设床位
		String bedsSql = " SELECT BED_NO_DESC,ALLO_FLG  " + " FROM SYS_BED  "
				+ " WHERE REGION_CODE='" + Operator.getRegion() + "' "
				+ " AND STATION_CODE='" + this.stationCode.getValue() + "' "
				+ " AND ACTIVE_FLG='Y' " + " ORDER BY REGION_CODE,BED_NO ";
		//System.out.println("==bedsSql=="+bedsSql);
		TParm bedParm = new TParm(TJDODBTool.getInstance().select(bedsSql));
		int count=0;
		int occuBedCount = 0;
		//this.messageBox("bedParm" + bedParm.getCount());
		if (bedParm.getCount() == 0 || bedParm.getCount() == -1) {
			this.messageBox("该病区在系统中未设床位");
		} else {
			// 简卡
			if (sCard.isSelected()) {//
				occuBedCount = 0;
				//this.messageBox("显示简卡");
				// 查询住院病人资料
				String sPatSql = "Select A.bed_no_desc,C.pat_name,B.mr_no,B.nursing_class,";
				sPatSql += "D.CHN_DESC,E.colour_red,E.colour_green,E.colour_blue,F.colour_red as colour_red1,";
				sPatSql += "F.colour_green as colour_green1,F.colour_blue as colour_blue1,B.PATIENT_STATUS,A.BED_OCCU_FLG,";
				sPatSql += "B.REGION_CODE,B.case_no,C.BIRTH_DATE,C.SEX_CODE,'' ins_status,B.CLNCPATH_CODE,B.DEPT_CODE,B.IPD_NO,A.BED_STATUS ";
				sPatSql += "from SYS_BED A,ADM_INP B,SYS_PATINFO C,(select * from sys_dictionary where group_id='SYS_SEX')  D,";
				sPatSql += "ADM_NURSING_CLASS E,ADM_PATIENT_STATUS F ";
				sPatSql += "Where A.REGION_CODE='"+Operator.getRegion()+"' ";
				sPatSql += "and A.station_code='"+this.stationCode.getValue()+"' ";
				sPatSql += "and A.REGION_CODE=B.REGION_CODE ";
				sPatSql += "and A.case_no=B.case_no ";
				sPatSql += "and B.mr_no=C.mr_no ";
				sPatSql += "and C.SEX_CODE=D.ID ";
				sPatSql += "and E.NURSING_CLASS_Code(+)=B.NURSING_CLASS ";
				sPatSql += "and F.PATIENT_STATUS_code(+)=B.PATIENT_STATUS ";
//                sPatSql +="AND A.ALLO_FLG='Y' AND B.CANCEL_FLG<>'Y' AND A.BED_STATUS='1' "; //与下面的一致
                sPatSql +="AND A.ALLO_FLG='Y' AND B.CANCEL_FLG<>'Y' "; //如不去掉床位状态条件 有的包床显示不出来
				sPatSql += "ORDER BY A.REGION_CODE,A.BED_NO";  

//				System.out.println("=====sPatSql====="+sPatSql);
				TParm sPatParm = new TParm(TJDODBTool.getInstance().select(
						sPatSql));
				//this.messageBox("++sPatParm+"+sPatParm);
				int result_count = sPatParm.getCount();  
				// 床位总数
				int row = Integer.parseInt(String.valueOf(bedParm.getCount()));
				//(int) TiMath.ceil(row / 5.0, 0)
				gridLayout1 = new GridLayout(0,
						5, 10, 10);
				tiPanel3.setLayout(gridLayout1);
				
				//System.out.println("=============简卡长度================"+row);
				
				
				for (int i = 0; i < row; i++) {
					S_card s_card[] = new S_card[row];
					//this.messageBox("==i=="+i);
					// 床号相同，说明有人使用床位
					// String.valueOf(((Vector)Sta_bed.get(1)).get(i)).equals(String.valueOf(((Vector)result.get(0)).get(j)))
					if (result_count != 0
							&& bedParm.getValue("BED_NO_DESC", i).equals(
									sPatParm.getValue("BED_NO_DESC", j))) {

						String s[] = new String[16];
						//this.messageBox("COLOUR_RED====="+sPatParm.getValue("COLOUR_RED", j));
						s[0] = sPatParm.getValue("COLOUR_RED", j).equals("")? "255"
								: sPatParm.getValue("COLOUR_RED", j);
						s[1] = sPatParm.getValue("COLOUR_GREEN", j).equals("")? "255"
								: sPatParm.getValue("COLOUR_GREEN", j);
						s[2] = sPatParm.getValue("COLOUR_BLUE", j).equals("")? "255"
								: sPatParm.getValue("COLOUR_BLUE", j);
						s[3] = sPatParm.getValue("COLOUR_RED1", j).equals("")? "255"
								: sPatParm.getValue("COLOUR_RED1", j);
						s[4] = sPatParm.getValue("COLOUR_GREEN1", j).equals("")? "255"
								: sPatParm.getValue("COLOUR_GREEN1", j);
						s[5] = sPatParm.getValue("COLOUR_BLUE1", j) .equals("")? "255"
								: sPatParm.getValue("COLOUR_BLUE1", j);
						// PATIENT_STATUS
						s[6] = sPatParm.getValue("PATIENT_STATUS", j);
						// BED_OCCU_FLG
						for (int k = 0; k < sPatParm.getCount("MR_NO"); k++) {
							if(sPatParm.getValue("MR_NO", j).equals(sPatParm.getValue("MR_NO", k))){
								if(sPatParm.getValue("BED_OCCU_FLG", j).equals("Y")){
									s[7] = "Y";
									occuBedCount++;
									break;
								}
							}
						}
						if(s[7] == null || s[7].length() <= 0)
							s[7] = sPatParm.getValue("BED_OCCU_FLG", j);
						// REGION_CODE
						s[8] = sPatParm.getValue("REGION_CODE", j);
						// case_no
						s[9] = sPatParm.getValue("CASE_NO", j);
						// BIRTH_DATE
						s[10] = sPatParm.getValue("BIRTH_DATE", j);
						// SEX_CODE
						s[11] = sPatParm.getValue("SEX_CODE", j);
						// ins_status
						s[12] = sPatParm.getValue("INS_STATUS", j);
						// CLNCPATH_CODE
						s[13] =sPatParm.getValue("CLNCPATH_CODE", j);
						s[14] =sPatParm.getValue("DEPT_CODE", j);
						s[15] =sPatParm.getValue("IPD_NO", j);
//						s[16] =sPatParm.getValue("BED_STATUS", j);
						//
						//this.messageBox("BED_NO"+sPatParm.getValue("BED_NO", j));
						s_card[i] = new S_card(
								sPatParm.getValue("BED_NO_DESC", j),
								sPatParm.getValue("PAT_NAME", j),
								sPatParm.getValue("MR_NO", j),
								sPatParm.getValue("NURSING_CLASS", j) == null ? ""
										: sPatParm.getValue("NURSING_CLASS", j),
								(String) stationCode.getValue(), sPatParm
										.getValue("CHN_DESC", j) == null ? ""
										: sPatParm.getValue("CHN_DESC", j), s);

						if (j < result_count - 1) {
							j++;
						}
						count++;
						// 是空床的情况
					} else {
						// String.valueOf(((Vector)Sta_bed.get(1)).get(i))
						s_card[i] = new S_card(bedParm.getValue("BED_NO_DESC", i),
								"");
					}
					//s_card[i].setPreferredSize(new Dimension(150, 60));
					s_card[i].setPreferredSize(new Dimension(150, 60));
					//this.messageBox("=====s_card["+i+"]"+s_card[i].sBed_no);
					// s_card[i].addMouseListener(this) ;
					//TButton btest=new TButton();
					//btest.setLabel("ok");
					//tiPanel3.add(btest,null);
					tiPanel3.add(s_card[i], null);
				}
				count -= occuBedCount;
				// 细卡
			} else if (tCard.isSelected()) {
				occuBedCount = 0;
				//this.messageBox("显示细卡");
				// 查询住院病人资料
				String tPatSql = "Select A.bed_no_desc,C.pat_name,B.mr_no,B.nursing_class,D.CHN_DESC,";
				tPatSql +="case NHI_CTZ_FLG when 'Y' then '医保' when 'N' then '自费' end CTZ,B.PATIENT_STATUS,G.USER_NAME,'' BLANK,";
				//修改age计算公式  wanglong add 20141022
				tPatSql +="floor(months_between(B.IN_DATE,C.BIRTH_DATE)/12) as age,TO_CHAR(B.in_date,'YYYY/MM/DD') IN_DATE,A.case_no,";
				tPatSql +="I.colour_red,I.colour_green,I.colour_blue,J.colour_red  as colour_red1,J.colour_green as colour_green1,J.colour_blue as colour_blue1,A.BED_OCCU_FLG,";
				tPatSql +="B.REGION_CODE,B.case_no patCaseNo,C.BIRTH_DATE,C.SEX_CODE,'' ins_status,B.CLNCPATH_CODE,B.DEPT_CODE,B.IPD_NO ";
				tPatSql +="from SYS_BED A,ADM_INP B,SYS_PATINFO C,(select * from sys_dictionary where group_id='SYS_SEX') D,";
				tPatSql +="SYS_CTZ E,SYS_OPERATOR G,ADM_NURSING_CLASS I,ADM_PATIENT_STATUS J ";
				tPatSql +="Where A.REGION_CODE='"+Operator.getRegion()+"' ";
				tPatSql +="and A.station_code='"+this.stationCode.getValue()+"' ";
				tPatSql +="and A.REGION_CODE=B.REGION_CODE ";
				tPatSql +="and A.case_no=B.case_no ";
				tPatSql +="and B.mr_no=C.mr_no ";
				tPatSql +="and C.SEX_CODE=D.ID ";
				tPatSql +="and B.ctz1_code=E.ctz_code ";
				tPatSql +="and B.VS_DR_CODE=G.user_id ";
				tPatSql +="AND b.nursing_class=i.nursing_class_code(+) ";
				tPatSql +="AND b.patient_status=j.patient_status_code(+) ";
//				tPatSql +="AND A.ALLO_FLG='Y' AND B.CANCEL_FLG<>'Y' AND A.BED_STATUS='1' ";    
				tPatSql +="AND A.ALLO_FLG='Y' AND B.CANCEL_FLG<>'Y'";    
				
				//tPatSql +="AND A.ALLO_FLG='Y' AND B.CANCEL_FLG<>'Y' AND A.BED_STATUS='1' ";
				tPatSql +="ORDER BY A.REGION_CODE,A.BED_NO";
				
//				System.out.println("=====tPatSql====="+tPatSql);
				//
				TParm tPatParm = new TParm(TJDODBTool.getInstance().select(
						tPatSql));
				int result_count=tPatParm.getCount();
		        int row=bedParm.getCount();
		        //(int)TiMath.ceil(row/5.0,0)
		        gridLayout1=new GridLayout(0,5,10,10);
		        tiPanel3.setLayout(gridLayout1);

		        T_card t_card[] = new T_card[row];
		        for (int i = 0; i < row; i++) {
		        	String s[]=new String[23];
		        	//已占用床位
		        	if(result_count!=0&&result_count != 0
							&& bedParm.getValue("BED_NO_DESC", i).equals(
									tPatParm.getValue("BED_NO_DESC", j))){
		        		
		        		//this.messageBox("case no"+tPatParm.getValue("CASE_NO",j));
		        		//构造细卡数据
		        		s[0]=tPatParm.getValue("CHN_DESC", j);
		                s[1]=tPatParm.getValue("CTZ", j);
		                s[2]=tPatParm.getValue("PATIENT_STATUS", j);
		                s[3]=tPatParm.getValue("USER_NAME", j);
		                s[4]=tPatParm.getValue("BLANK", j);
		                s[5]=tPatParm.getValue("AGE", j);
		                s[6]=tPatParm.getValue("IN_DATE", j);
		                //取诊断内容  ADM_INP ADM_INPDIAG
		                String diagSql="Select b.icd_chn_desc ";
		                diagSql+="from ADM_INP a ,SYS_DIAGNOSIS b ";
		                diagSql+="where a.CASE_NO='"+tPatParm.getValue("CASE_NO",j)+"' ";
		                //diagSql+="and a.maindiag_flg = 'Y' ";
		                diagSql+="and b.icd_code = a.MAINDIAG ";
		                //diagSql+="order by a.io_type desc";
		                
//		                System.out.println("=====diagSql====="+diagSql);
		                TParm diagParm = new TParm(TJDODBTool.getInstance().select(
		                		diagSql));	                
		                if(diagParm.getCount()==0||diagParm.getCount()==-1){
		                  s[7]="";
		                }else{
		                  s[7]=diagParm.getValue("ICD_CHN_DESC", 0);
		                }
		                //colour_red
		                //this.messageBox("colour_red====="+tPatParm.getValue("colour_red", j));
		                //System.out.println("++++colour red++++"+tPatParm.getValue("COLOUR_RED", j));
		                s[8]=tPatParm.getValue("COLOUR_RED", j).equals("")?"255":tPatParm.getValue("COLOUR_RED", j);
		                s[9]=tPatParm.getValue("COLOUR_GREEN", j).equals("")?"255":tPatParm.getValue("COLOUR_GREEN", j);
		                s[10]=tPatParm.getValue("COLOUR_BLUE", j).equals("")?"255":tPatParm.getValue("COLOUR_BLUE", j);
		                
		                s[11]=tPatParm.getValue("COLOUR_RED1", j).equals("")?"255":tPatParm.getValue("COLOUR_RED1", j);
		                s[12]=tPatParm.getValue("COLOUR_GREEN1", j).equals("")?"255":tPatParm.getValue("COLOUR_GREEN1", j);
		                s[13]=tPatParm.getValue("COLOUR_BLUE1", j).equals("")?"255":tPatParm.getValue("COLOUR_BLUE1", j);
		                //BED_OCCU_FLG
		                for (int k = 0; k < tPatParm.getCount("MR_NO"); k++) {
							if(tPatParm.getValue("MR_NO", j).equals(tPatParm.getValue("MR_NO", k))){
								if(tPatParm.getValue("BED_OCCU_FLG", j).equals("Y")){
										s[14] = "Y";
										occuBedCount++;
										break;
								}
							}
						}
						if(s[14] == null || s[14].length() <= 0)
							s[14]=tPatParm.getValue("BED_OCCU_FLG", j);
		                s[15]=tPatParm.getValue("REGION_CODE", j);
		                s[16]=tPatParm.getValue("CASE_NO", j);
		                s[17]=tPatParm.getValue("BIRTH_DATE", j);
		                s[18]=tPatParm.getValue("SEX_CODE", j);
		                s[19]=tPatParm.getValue("INS_STATUS", j);
		                s[20]=tPatParm.getValue("CLNCPATH_CODE", j);
		                s[21] =tPatParm.getValue("DEPT_CODE", j);
						s[22] =tPatParm.getValue("IPD_NO", j);
		        		
		                t_card[i] = new T_card(tPatParm.getValue("BED_NO_DESC", j), tPatParm.getValue("PAT_NAME", j),tPatParm.getValue("MR_NO", j),tPatParm.getValue("NURSING_CLASS", j), (String)stationCode.getValue(),s);

		                if(j<result_count-1){
		                  j++;
		                }
                      count++ ;
		              }else{
		            	//空床位
		                t_card[i] = new T_card(bedParm.getValue("BED_NO_DESC", i), "");
		              }
		        	  t_card[i].setPreferredSize(new Dimension(150, 180));
		              //t_card[i].addMouseListener(this) ;
		              tiPanel3.add(t_card[i], null);	        
		        }
		        count -= occuBedCount;
			}
		}
	    //this.messageBox("重画panel3");
		//重新载入面版数据
	    jScrollPane1.setViewportView(tiPanel3);
	  //设置床位总数;
		bedsCount.setText(String.valueOf(count));
		//tiPanel3.repaint();

	}
	/**
	 * 打开住院医生站窗口
	 * @param parm
	 */
	public void openOdiStationWindow(TParm parm) {
		this.setReturnValue(parm);
		this.closeWindow();		
	}

	/**
	 * 床头卡－－简卡
	 * 
	 * @author lixiang
	 * 
	 */
	public class S_card extends TiMultiPanel implements MouseListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6685653491843293933L;
		TiLabel tiL_bedno = new TiLabel();
		TiLabel tiL_name = new TiLabel();
		TiLabel tiL_mrno = new TiLabel();
		
		private String sBed_no = "";
		private String sName = "";
		private String sMr_no = "";
		private String sNURSING_CLASS = "";
		TiPanel tiPanel1 = new TiPanel();
		private String sStation_code = "";
		private String sSex = "";
		private int sNURSING_red = 255;
		private int sNURSING_green = 255;
		private int sNURSING_blue = 255;
		private int sPATIENT_red = 255;
		private int sPATIENT_green = 255;
		private int sPATIENT_blue = 255;
		TiLabel tiLabel1 = new TiLabel();
		TiLabel tiLabel2 = new TiLabel();
		private String occupy_bed_flg = "";
		private String sPATIENT_STATUS = "";
		private String sHosp_area = "";
		private String sCase_no = "";
		private String sDeptCode="";
		private String sIpdNo="";
//		private String bedStatus="";
		
		public/* static */String sBIRTH_DATE = "";// 出生日期 del static by dengyh
		// 2008-08-06
		public/* static */String sSex_code = ""; // del static by dengyh
		// 2008-08-06
		TitledBorder titledBorder1;
		TiLabel tiLabel3 = new TiLabel();// 性别代码
		private String sIns_STATUS = "";
		private String sClp_code = "";
		TiLabel tiLabel4 = new TiLabel();// lur 修改 2006-04-06

		public S_card() {
			try {
				jbInit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public S_card(String sBed_no, String sNURSING_CLASS) {// 空床
			try {
				this.sBed_no = sBed_no;
				this.sNURSING_CLASS = sNURSING_CLASS;
				jbInit();
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
		 * @param Color
		 */
		public S_card(String sBed_no, String sName, String sMr_no,
				String sNURSING_CLASS, String sStation_code, String sSex,
				String[] Color) {//
			try {
				this.sBed_no = sBed_no;
				this.sName = sName;
				this.sMr_no = sMr_no;
				this.sNURSING_CLASS = sNURSING_CLASS;
				this.sStation_code = sStation_code;
				this.sSex = sSex;

				//System.out.println("Color[0]"+Color[0]);
				this.sNURSING_red = Integer
						.parseInt(Color[0].equals("") ? "255" : Color[0]
								.trim());
				this.sNURSING_green = Integer
						.parseInt(Color[1].equals("") ? "255" : Color[1]
								.trim());
				this.sNURSING_blue = Integer
						.parseInt(Color[2].equals("") ? "255" : Color[2]
								.trim());
				
				this.sPATIENT_red = Integer
						.parseInt(Color[3].equals("") ? "255" : Color[3]
								.trim());
				this.sPATIENT_green = Integer
						.parseInt(Color[4].equals("") ? "255" : Color[4]
								.trim());
				this.sPATIENT_blue = Integer
						.parseInt(Color[5].equals("") ? "255" : Color[5]
								.trim());
				
				this.sPATIENT_STATUS = Color[6];
				this.occupy_bed_flg = Color[7];
				this.sHosp_area = Color[8];
				this.sCase_no = Color[9];
				this.sBIRTH_DATE = Color[10];// 出生日期
				this.sSex_code = Color[11];// 性别代码
				this.sIns_STATUS = Color[12];
				this.sClp_code = Color[13];// lur 修改 2006-04-06
				this.sDeptCode=Color[14];
				this.sIpdNo=Color[15];
//				this.bedStatus=Color[16];

				jbInit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void jbInit() throws Exception {
			titledBorder1 = new TitledBorder("");
			tiL_bedno.setBackground(Color.black);
			tiL_bedno.setFont(new java.awt.Font("宋体", 1, 12));
			tiL_bedno.setForeground(Color.black);
			tiL_bedno.setText(this.sBed_no);
			tiL_bedno.setBounds(new Rectangle(3, 0, 42, 21));
			
			if (this.sSex.equals("女")) {
				this.setBackground(new Color(255, 240, 255));
				this.setForeground(new Color(255, 240, 255));
			} else {
				this.setBackground(Color.white);
				this.setForeground(Color.white);
			}
			//
			if(this.sNURSING_red==0&&this.sNURSING_green==0&& this.sNURSING_blue==0){
				this.sNURSING_red=255;
				this.sNURSING_green=255;
				this.sNURSING_blue=255;
			}
			tiPanel1.setBackground(new Color(this.sNURSING_red,
					this.sNURSING_green, this.sNURSING_blue));
			
			tiPanel1.setForeground(new Color(this.sNURSING_red,
					this.sNURSING_green, this.sNURSING_blue));

			this.setFont(new java.awt.Font("Dialog", 0, 11));
			this.setBorder(BorderFactory.createEtchedBorder());

			this.setLayout(null);
			tiL_name.setText(sName);
			tiL_name.setBounds(new Rectangle(59, 5, 59, 19));
			tiL_mrno.setText(sMr_no);
			tiL_mrno.setBounds(new Rectangle(5, 36, 110, 15));
			this.addMouseListener(this);
			tiPanel1.setBounds(new Rectangle(4, 4, 48, 19));
			tiPanel1.setSize(48, 19);
			tiPanel1.setLayout(null);
			tiLabel1.setText("※");

			tiLabel1.setForeground(new Color(this.sPATIENT_red,
					this.sPATIENT_green, this.sPATIENT_blue));
			tiLabel1.setBounds(new Rectangle(118, 5, 18, 14));
			tiLabel2.setText("包");
			tiLabel2.setBounds(new Rectangle(139, 5, 18, 15));
			//医保确认书
			if (this.sIns_STATUS.equals("0") || this.sIns_STATUS.equals("5")
					|| this.sIns_STATUS.equals("6")) {
				tiLabel3.setText("☆");
			} else if (this.sIns_STATUS.equals("1")
					|| this.sIns_STATUS.equals("2")
					|| this.sIns_STATUS.equals("3")
					|| this.sIns_STATUS.equals("4")
					|| this.sIns_STATUS.equals("7")) {
				tiLabel3.setText("★");
				tiLabel3.setForeground(Color.GREEN);
			} else {
				tiLabel3.setText("");
			}
			tiLabel3.setBounds(new Rectangle(155, 2, 13, 15));

			tiLabel4.setBounds(new Rectangle(100, 6, 48, 15));
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
			this.add(tiLabel1, null);
			this.add(tiL_name, null);
			this.add(tiLabel3, null);
			this.add(tiLabel4, null);
		}

		public void mouseClicked(MouseEvent e) {
			
			if(this.sMr_no.equals("")){
			      JOptionPane.showMessageDialog(this, "此床没有病人");
			      return;
			 }
			//构造参数;
			//打开 .x 文件
			TParm parm=new TParm();
			parm.setData("CASE_NO", this.sCase_no);
			parm.setData("STATION_CODE", this.sStation_code);
			parm.setData("DEPT_CODE", this.sDeptCode);
			parm.setData("IPD_NO", this.sIpdNo);
			parm.setData("MR_NO", this.sMr_no);
			openOdiStationWindow(parm);

		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
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

	/**
	 * 
	 * 细卡
	 * 
	 * @author lixiang
	 * 
	 */
	public class T_card extends TiMultiPanel implements MouseListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6267546297516623036L;
		TiLabel tiL_bedno = new TiLabel();
		TiLabel tiL_name = new TiLabel();
		TiLabel tiL_mrno = new TiLabel();
		TiLabel tiL_ipdno = new TiLabel();
		
		private String sBed_no = "";
		private String sName = "";
		private String sMr_no = "";

		private String sNURSING_CLASS = "";
		TiPanel tiPanel1 = new TiPanel();
		private String sStation_code;
		TiLabel sex = new TiLabel();
		TiLabel age = new TiLabel();
		TiLabel ctz = new TiLabel();
		TiLabel DANGER = new TiLabel();
		TiLabel nursing_class = new TiLabel();
		TiLabel VS_dr = new TiLabel();
		TiLabel OPERATION = new TiLabel();
		private String sSex = "";
		private String sAge = "";
		private String sCtz = "";
		private String sDanger = "";
		private String sUserName = "";
		private String sOp = "";
		private String sInDate = "";
		private String sICD = "";
		private int sNURSING_red = 255;
		private int sNURSING_green = 255;
		private int sNURSING_blue = 255;
		private int sPATIENT_red = 255;
		private int sPATIENT_green = 255;
		private int sPATIENT_blue = 255;
		private String occupy_bed_flg = "";
		private String sHosp_area = "";
		private String sCase_no = "";
		public String sBIRTH_DATE = "";// 出生日期
		public String sSex_code = "";// 性别代码
		private String sIns_STATUS = "";
		private String sClp_code = ""; // lur 修改 2006-04-06
		
		private String sDeptCode="";
		private String sIpdNo="";

		TiLabel icd_1 = new TiLabel();
		TiLabel tiLabel1 = new TiLabel();
		TiLabel tiLabel2 = new TiLabel();
		TiLabel tiLabel3 = new TiLabel();
		TiLabel tiLabel4 = new TiLabel();

		public T_card() {
			try {
				jbInit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public T_card(String sBed_no, String sNURSING_CLASS) {// 空床
			try {
				this.sBed_no = sBed_no;
				this.sNURSING_CLASS = sNURSING_CLASS;
				jbInit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public T_card(String sBed_no, String sName, String sMr_no,
				String sNURSING_CLASS, String sStation_code, String array[]) {//
			try {
				this.sBed_no = sBed_no;
				this.sName = sName;
				this.sMr_no = sMr_no;
				this.sNURSING_CLASS = sNURSING_CLASS;
				this.sStation_code = sStation_code;
				this.sSex = array[0];
				this.sAge = array[5];
				this.sCtz = array[1];
				this.sDanger = array[2];
				this.sUserName = array[3];
				this.sOp = array[4];
				this.sInDate = array[6];
				this.sICD = array[7];
				this.sNURSING_red = Integer
						.parseInt(array[8].equals("") ? "255" : array[8]
								.trim());
				this.sNURSING_green = Integer
						.parseInt(array[9].equals("") ? "255" : array[9]
								.trim());
				this.sNURSING_blue = Integer
						.parseInt(array[10].equals("") ? "255" : array[10]
								.trim());
				this.sPATIENT_red = Integer
						.parseInt(array[11].equals("") ? "255" : array[11]
								.trim());
				this.sPATIENT_green = Integer
						.parseInt(array[12].equals("") ? "255" : array[12]
								.trim());
				this.sPATIENT_blue = Integer
						.parseInt(array[13].equals("") ? "255" : array[13]
								.trim());
				this.occupy_bed_flg = array[14];
				this.sHosp_area = array[15];
				this.sCase_no = array[16];
				this.sBIRTH_DATE = array[17];// 出生日期
				this.sSex_code = array[18];// 性别代码
				this.sIns_STATUS = array[19];
				this.sClp_code = array[20];// lur 修改 2006-04-06
				
				this.sDeptCode= array[21];
				this.sIpdNo= array[22];
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
				this.setBackground(new Color(255, 240, 255));
				this.setForeground(new Color(255, 240, 255));
			} else {
				this.setBackground(Color.white);
				this.setForeground(Color.white);
			}
			//床位号部分颜色(护理级别有关)
			if(this.sNURSING_red==0&&this.sNURSING_green==0&& this.sNURSING_blue==0){
				this.sNURSING_red=255;
				this.sNURSING_green=255;
				this.sNURSING_blue=255;
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
			tiL_mrno.setBounds(new Rectangle(7, 57, 148, 15));
			
			
			
			this.addMouseListener(this);
			tiPanel1.setBounds(new Rectangle(5, 6, 50, 21));
			tiPanel1.setLayout(null);
			sex.setText("性别：" + this.sSex);
			sex.setBounds(new Rectangle(5, 34, 60, 19));
			age.setText("年龄：" + this.sAge);
			age.setBounds(new Rectangle(70, 33, 62, 19));
			ctz.setText("付款方式：" + sCtz);
			ctz.setRequirement(false);
			ctz.setBounds(new Rectangle(5, 73, 114, 20));

			String str[] = TiString.fixRow(TiString.breakRow(this.sICD, 20), 1);
			DANGER.setText("诊断：" + str[0]);
//			if (str.length > 1) {
//				icd_1.setText(str[1]);
//			}
	
			DANGER.setBounds(new Rectangle(5, 133, 159, 20));
			
			tiL_ipdno.setText("住院号：" + this.sIpdNo);
			tiL_ipdno.setBounds(new Rectangle(5,150, 159, 20));
			
			nursing_class.setText("护理级别：" + this.sNURSING_CLASS);
			nursing_class.setBounds(new Rectangle(6, 123, 123, 22));
			VS_dr.setText("经治医师：" + this.sUserName);
			//111
			VS_dr.setBounds(new Rectangle(5, 112, 140, 21));
			OPERATION.setText("入院日期：" + this.sInDate);
			//126
			OPERATION.setBounds(new Rectangle(5, 93, 156, 18));

			icd_1.setBounds(new Rectangle(37, 155, 133, 18));
			// this.add(nursing_class, null);
			tiLabel1.setText("※");

			tiLabel1.setForeground(new Color(this.sPATIENT_red,
					this.sPATIENT_green, this.sPATIENT_blue));
			tiLabel1.setBounds(new Rectangle(126, 9, 16, 17));
			tiLabel2.setText("包");
			tiLabel2.setBounds(new Rectangle(143, 12, 17, 15));
			if (this.sIns_STATUS.equals("0") || this.sIns_STATUS.equals("5")
					|| this.sIns_STATUS.equals("6")) {
				tiLabel3.setText("☆");
			} else if (this.sIns_STATUS.equals("1")
					|| this.sIns_STATUS.equals("2")
					|| this.sIns_STATUS.equals("3")
					|| this.sIns_STATUS.equals("4")
					|| this.sIns_STATUS.equals("7")) {
				tiLabel3.setText("★");
				tiLabel3.setForeground(Color.GREEN);
			} else {
				tiLabel3.setText("");
			}
			tiLabel3.setBounds(new Rectangle(154, 10, 15, 15));
			tiLabel4.setBounds(new Rectangle(109, 10, 48, 15));

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
			this.add(tiLabel1, null);
			this.add(tiL_name, null);
			this.add(tiLabel3, null);
			this.add(tiLabel4, null);
			this.add(tiL_ipdno, null);
			if (this.occupy_bed_flg.equals("Y")) {
				this.add(tiLabel2, null);
			}

		}

		public void mouseClicked(MouseEvent e) {
			 if(this.sMr_no.equals("")){
			      JOptionPane.showMessageDialog(this, "此床没有病人");
			      return;
			 }
			 //构造参数；
			 
			 //打开新
			 TParm parm=new TParm();
			    parm.setData("CASE_NO", this.sCase_no);
				parm.setData("STATION_CODE", this.sStation_code);
				parm.setData("DEPT_CODE", this.sDeptCode);
				parm.setData("IPD_NO", this.sIpdNo);
				openOdiStationWindow(parm);
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
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
}
