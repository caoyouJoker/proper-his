## TBuilder Config File ## Title:## Company:JavaHis## Author:sundx 2009.10.27## version 1.0#<Type=TFrame>UI.Title=设备入库UI.MenuConfig=%ROOT%\config\dev\DEVInStorageMenu.xUI.Width=1288UI.Height=682UI.toolbar=YUI.controlclassname=com.javahis.ui.dev.DevInStorageControlUI.item=tPanel_0;tPanel_2;tMovePane_0UI.layout=nullUI.TopMenu=YUI.TopToolBar=YtMovePane_0.Type=TMovePanetMovePane_0.X=356tMovePane_0.Y=0tMovePane_0.Width=5tMovePane_0.Height=677tMovePane_0.Text=tMovePane_0.MoveType=1tMovePane_0.Border=凸tMovePane_0.AutoHeight=YtMovePane_0.EntityData=tPanel_0,4;tPanel_2,3tPanel_2.Type=TPaneltPanel_2.X=365tPanel_2.Y=5tPanel_2.Width=918tPanel_2.Height=672tPanel_2.Border=组tPanel_2.Item=tPanel_5;DEV_INWAREHOUSED;DEV_INWAREHOUSEDD;MANtPanel_2.AutoHeight=YtPanel_2.AutoWidth=YMAN.Type=生产厂商下拉区域MAN.X=73MAN.Y=377MAN.Width=81MAN.Height=23MAN.Text=MAN.HorizontalAlignment=2MAN.PopupMenuHeader=代码,100;名称,100MAN.PopupMenuWidth=300MAN.PopupMenuHeight=300MAN.PopupMenuFilter=ID,1;NAME,1;PY1,1MAN.FormatType=comboMAN.ShowDownButton=YMAN.Tip=生产厂商MAN.ShowColumnList=NAMEDEVKIND_CODE.Type=设备种类下拉区域DEVKIND_CODE.X=188DEVKIND_CODE.Y=132DEVKIND_CODE.Width=81DEVKIND_CODE.Height=23DEVKIND_CODE.Text=DEVKIND_CODE.HorizontalAlignment=2DEVKIND_CODE.PopupMenuHeader=ID,100;NAME,100DEVKIND_CODE.PopupMenuWidth=300DEVKIND_CODE.PopupMenuHeight=300DEVKIND_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1DEVKIND_CODE.FormatType=comboDEVKIND_CODE.ShowDownButton=YDEVKIND_CODE.Tip=设备种类DEVKIND_CODE.ShowColumnList=NAMEDEV_PRO.Type=设备属性下拉区域DEV_PRO.X=121DEV_PRO.Y=125DEV_PRO.Width=81DEV_PRO.Height=23DEV_PRO.Text=DEV_PRO.HorizontalAlignment=2DEV_PRO.PopupMenuHeader=ID,100;NAME,100DEV_PRO.PopupMenuWidth=300DEV_PRO.PopupMenuHeight=300DEV_PRO.PopupMenuFilter=ID,1;NAME,1;PY1,1DEV_PRO.FormatType=comboDEV_PRO.ShowDownButton=YDEV_PRO.Tip=设备属性DEV_PRO.ShowColumnList=NAMEDEP_METHOD.Type=TComboBoxDEP_METHOD.X=66DEP_METHOD.Y=137DEP_METHOD.Width=81DEP_METHOD.Height=23DEP_METHOD.Text=TButtonDEP_METHOD.showID=YDEP_METHOD.Editable=YDEP_METHOD.StringData=[[id,text],["",""],[1,不折旧],[2,原值直线法],[3,双倍余额递减法],[4,综合折旧法]]DEP_METHOD.TableShowList=textMAN_TF.Type=生产厂商下拉区域MAN_TF.X=182MAN_TF.Y=148MAN_TF.Width=81MAN_TF.Height=23MAN_TF.Text=MAN_TF.HorizontalAlignment=2MAN_TF.PopupMenuHeader=ID,100;NAME,100MAN_TF.PopupMenuWidth=300MAN_TF.PopupMenuHeight=300MAN_TF.PopupMenuFilter=ID,1;NAME,1;PY1,1MAN_TF.FormatType=comboMAN_TF.ShowDownButton=YMAN_TF.Tip=生产厂商MAN_TF.ShowColumnList=NAMEMAN_NATION_TF.Type=国籍MAN_NATION_TF.X=65MAN_NATION_TF.Y=165MAN_NATION_TF.Width=81MAN_NATION_TF.Height=23MAN_NATION_TF.Text=MAN_NATION_TF.HorizontalAlignment=2MAN_NATION_TF.PopupMenuHeader=ID,100;NAME,100MAN_NATION_TF.PopupMenuWidth=300MAN_NATION_TF.PopupMenuHeight=300MAN_NATION_TF.PopupMenuFilter=ID,1;NAME,1;PY1,1MAN_NATION_TF.FormatType=comboMAN_NATION_TF.ShowDownButton=YMAN_NATION_TF.Tip=国籍MAN_NATION_TF.ShowColumnList=NAMEDEV_UNIT.Type=计量单位DEV_UNIT.X=122DEV_UNIT.Y=135DEV_UNIT.Width=81DEV_UNIT.Height=23DEV_UNIT.Text=DEV_UNIT.HorizontalAlignment=2DEV_UNIT.PopupMenuHeader=ID,100;NAME,100DEV_UNIT.PopupMenuWidth=300DEV_UNIT.PopupMenuHeight=300DEV_UNIT.PopupMenuFilter=ID,1;NAME,1;PY1,1DEV_UNIT.FormatType=comboDEV_UNIT.ShowDownButton=YDEV_UNIT.Tip=计量单位DEV_UNIT.ShowColumnList=NAMEDEV_INWAREHOUSEDD.Type=TTableDEV_INWAREHOUSEDD.X=10DEV_INWAREHOUSEDD.Y=361DEV_INWAREHOUSEDD.Width=900DEV_INWAREHOUSEDD.Height=299DEV_INWAREHOUSEDD.SpacingRow=1DEV_INWAREHOUSEDD.RowHeight=20DEV_INWAREHOUSEDD.Header=删,30,BOOLEAN;选,30,BOOLEAN;印,30,BOOLEAN;属性,100,DEV_PRO;资产编号,150;设备编号,120;设备序号,100;设备名称,200;条码号,200;依附主设备,100;规格,200;型号,100;生产厂商,100;品牌,100;出厂日期,100,Timestamp;出厂序号,100;残值,100;保修终止日期,100,Timestamp;折旧终止日期,100,Timestamp;财产价值,100;入库单号,100;入库单序号,100;设备流水号,100;序列号(SN),100;IP地址;机器名,100;存放地点,100;使用人,100;无线IP,100DEV_INWAREHOUSEDD.AutoWidth=YDEV_INWAREHOUSEDD.ParmMap=DEL_FLG;SELECT_FLG;PRINT_FLG;DEVPRO_CODE;DEV_CODE_DETAIL;DEV_CODE;DEVSEQ_NO;DEV_CHN_DESC;BARCODE;MAIN_DEV;SPECIFICATION;MODEL;MAN_CODE;BRAND;MAN_DATE;MAN_SEQ;LAST_PRICE;GUAREP_DATE;DEP_DATE;TOT_VALUE;INWAREHOUSE_NO;SEQ_NO;DEVSEQ_NO;SERIAL_NUM;IP;TERM;LOC_CODE;USE_USER;WIRELESS_IPDEV_INWAREHOUSEDD.Item=DEV_PRODEV_INWAREHOUSEDD.ColumnHorizontalAlignmentData=DEV_INWAREHOUSEDD.FocusType=2DEV_INWAREHOUSEDD.AutoHeight=YDEV_INWAREHOUSEDD.LockColumns=3,4,5,6,7,8,9DEV_INWAREHOUSEDD.DoubleClickedAction=onDoubleClickedDEV_INWAREHOUSED.Type=TTableDEV_INWAREHOUSED.X=11DEV_INWAREHOUSED.Y=114DEV_INWAREHOUSED.Width=898DEV_INWAREHOUSED.Height=237DEV_INWAREHOUSED.SpacingRow=1DEV_INWAREHOUSED.RowHeight=20DEV_INWAREHOUSED.AutoWidth=YDEV_INWAREHOUSED.Header=删,30,BOOLEAN;属性,100,DEV_PRO;设备编号,120;设备名称,200;规格,100;型号,100;生产厂商,200,MAN_TF;品牌,100;入库数量,100,INT;累计入库数,100;验收数,100;单位,100,DEV_UNIT;单价,100;财产价值,100;出场日期,100,Timestamp;残值,100;保修终止日期,100,Timestamp;折旧,60,DEP_METHOD;使用年限,100;折旧终止日期,100,Timestamp;生产国,100,MAN_NATION_TF;序号管理,100,BOOLEAN;计量设备,100,BOOLEAN;效益评估,100,BOOLEAN;技术文件,100;验收单号,100;验收单明细序号,100;入库单号,100;入库单序号,100;设备类别,100,DEVKIND_CODEDEV_INWAREHOUSED.ParmMap=DEL_FLG;DEVPRO_CODE;DEV_CODE;DEV_CHN_DESC;SPECIFICATION;MODEL;MAN_CODE;BRAND;QTY;SUM_QTY;RECEIPT_QTY;UNIT_CODE;UNIT_PRICE;TOT_VALUE;MAN_DATE;LAST_PRICE;GUAREP_DATE;DEPR_METHOD;USE_DEADLINE;DEP_DATE;MAN_NATION;SEQMAN_FLG;MEASURE_FLG;BENEFIT_FLG;FILES_WAY;VERIFY_NO;VERIFY_NO_SEQ;INWAREHOUSE_NO;SEQ_NO;DEVKIND_CODEDEV_INWAREHOUSED.Item=DEV_PRO;DEV_UNIT;MAN_NATION_TF;MAN_TF;DEP_METHOD;DEVKIND_CODEDEV_INWAREHOUSED.ColumnHorizontalAlignmentData=1,left;2,left;3,right;4,left;5,left;6,left;7,right;8,right;9,right;10,left;11,right;12,right;13,left;14,right;15,left;16,left;17,right;18,left;19,left;23,left;24,left;25,right;26,left;27,right;28,leftDEV_INWAREHOUSED.FocusType=2DEV_INWAREHOUSED.LockColumns=2,3,4,5,6,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22tPanel_5.Type=TPaneltPanel_5.X=10tPanel_5.Y=4tPanel_5.Width=897tPanel_5.Height=108tPanel_5.Border=组tPanel_5.Item=tLabel_11;tLabel_20;INWAREHOUSE_DATE;tLabel_21;VERIFY_NO;tLabel_22;tLabel_23;SUP_BOSSNAME;tLabel_24;SUP_TEL;tLabel_25;tLabel_26;SUP_CODE;OPERATOR;INWAREHOUSE_NO_QUARY;DEPT;CHECK_INtPanel_5.AutoWidth=YCHECK_IN.Type=TCheckBoxCHECK_IN.X=430CHECK_IN.Y=68CHECK_IN.Width=89CHECK_IN.Height=23CHECK_IN.Text=审核入库DEPT.Type=成本中心下拉区域DEPT.X=88DEPT.Y=70DEPT.Width=116DEPT.Height=23DEPT.Text=DEPT.HorizontalAlignment=2DEPT.PopupMenuHeader=代码,100;名称,100DEPT.PopupMenuWidth=300DEPT.PopupMenuHeight=300DEPT.FormatType=comboDEPT.ShowDownButton=YDEPT.Tip=成本中心DEPT.ShowColumnList=NAMEDEPT.Action=OPERATOR|onQueryINWAREHOUSE_NO_QUARY.Type=TTextFieldINWAREHOUSE_NO_QUARY.X=89INWAREHOUSE_NO_QUARY.Y=10INWAREHOUSE_NO_QUARY.Width=104INWAREHOUSE_NO_QUARY.Height=20INWAREHOUSE_NO_QUARY.Text=INWAREHOUSE_NO_QUARY.Enabled=NOPERATOR.Type=人员OPERATOR.X=276OPERATOR.Y=69OPERATOR.Width=120OPERATOR.Height=23OPERATOR.Text=OPERATOR.HorizontalAlignment=2OPERATOR.PopupMenuHeader=ID,100;NAME,100OPERATOR.PopupMenuWidth=300OPERATOR.PopupMenuHeight=300OPERATOR.PopupMenuFilter=ID,1;NAME,1;PY1,1OPERATOR.FormatType=comboOPERATOR.ShowDownButton=YOPERATOR.Tip=人员OPERATOR.ShowColumnList=NAMEOPERATOR.Dept=<DEPT>SUP_CODE.Type=供应厂商SUP_CODE.X=88SUP_CODE.Y=38SUP_CODE.Width=308SUP_CODE.Height=23SUP_CODE.Text=SUP_CODE.HorizontalAlignment=2SUP_CODE.PopupMenuHeader=ID,100;NAME,100SUP_CODE.PopupMenuWidth=300SUP_CODE.PopupMenuHeight=300SUP_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1SUP_CODE.FormatType=comboSUP_CODE.ShowDownButton=YSUP_CODE.Tip=供应厂商SUP_CODE.ShowColumnList=NAMEtLabel_26.Type=TLabeltLabel_26.X=215tLabel_26.Y=73tLabel_26.Width=58tLabel_26.Height=15tLabel_26.Text=入库人员tLabel_25.Type=TLabeltLabel_25.X=14tLabel_25.Y=73tLabel_25.Width=72tLabel_25.Height=15tLabel_25.Text=入库科室SUP_TEL.Type=TTextFieldSUP_TEL.X=665SUP_TEL.Y=40SUP_TEL.Width=111SUP_TEL.Height=20SUP_TEL.Text=SUP_TEL.Enabled=NtLabel_24.Type=TLabeltLabel_24.X=604tLabel_24.Y=43tLabel_24.Width=64tLabel_24.Height=15tLabel_24.Text=联系电话SUP_BOSSNAME.Type=TTextFieldSUP_BOSSNAME.X=480SUP_BOSSNAME.Y=40SUP_BOSSNAME.Width=116SUP_BOSSNAME.Height=20SUP_BOSSNAME.Text=SUP_BOSSNAME.Enabled=NtLabel_23.Type=TLabeltLabel_23.X=412tLabel_23.Y=43tLabel_23.Width=66tLabel_23.Height=15tLabel_23.Text=联 络 人tLabel_22.Type=TLabeltLabel_22.X=14tLabel_22.Y=41tLabel_22.Width=64tLabel_22.Height=15tLabel_22.Text=供应厂商VERIFY_NO.Type=TTextFieldVERIFY_NO.X=480VERIFY_NO.Y=9VERIFY_NO.Width=116VERIFY_NO.Height=20VERIFY_NO.Text=VERIFY_NO.Enabled=NtLabel_21.Type=TLabeltLabel_21.X=411tLabel_21.Y=12tLabel_21.Width=72tLabel_21.Height=15tLabel_21.Text=验收单号INWAREHOUSE_DATE.Type=TTextFormatINWAREHOUSE_DATE.X=277INWAREHOUSE_DATE.Y=9INWAREHOUSE_DATE.Width=119INWAREHOUSE_DATE.Height=20INWAREHOUSE_DATE.Text=TTextFormatINWAREHOUSE_DATE.FormatType=dateINWAREHOUSE_DATE.Format=yyyy/MM/ddINWAREHOUSE_DATE.showDownButton=YtLabel_20.Type=TLabeltLabel_20.X=209tLabel_20.Y=11tLabel_20.Width=59tLabel_20.Height=15tLabel_20.Text=入库日期tLabel_11.Type=TLabeltLabel_11.X=14tLabel_11.Y=13tLabel_11.Width=72tLabel_11.Height=15tLabel_11.Text=入库单号tPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=6tPanel_0.Width=349tPanel_0.Height=669tPanel_0.Border=组tPanel_0.Item=tPanel_3;tPanel_4tPanel_4.Type=TPaneltPanel_4.X=7tPanel_4.Y=225tPanel_4.Width=331tPanel_4.Height=436tPanel_4.Border=组|入库单tPanel_4.Item=RECEIPT;OPERATOR_COM;DEPT_COMtPanel_4.AutoWidth=YDEPT_COM.Type=成本中心下拉区域DEPT_COM.X=58DEPT_COM.Y=47DEPT_COM.Width=81DEPT_COM.Height=23DEPT_COM.Text=DEPT_COM.HorizontalAlignment=2DEPT_COM.PopupMenuHeader=代码,100;名称,100DEPT_COM.PopupMenuWidth=300DEPT_COM.PopupMenuHeight=300DEPT_COM.FormatType=comboDEPT_COM.ShowDownButton=YDEPT_COM.Tip=成本中心DEPT_COM.ShowColumnList=NAMEOPERATOR_COM.Type=人员OPERATOR_COM.X=179OPERATOR_COM.Y=47OPERATOR_COM.Width=81OPERATOR_COM.Height=23OPERATOR_COM.Text=OPERATOR_COM.HorizontalAlignment=2OPERATOR_COM.PopupMenuHeader=代码,100;名称,100OPERATOR_COM.PopupMenuWidth=300OPERATOR_COM.PopupMenuHeight=300OPERATOR_COM.PopupMenuFilter=ID,1;NAME,1;PY1,1OPERATOR_COM.FormatType=comboOPERATOR_COM.ShowDownButton=YOPERATOR_COM.Tip=人员OPERATOR_COM.ShowColumnList=NAMERECEIPT.Type=TTableRECEIPT.X=11RECEIPT.Y=21RECEIPT.Width=311RECEIPT.Height=397RECEIPT.SpacingRow=1RECEIPT.RowHeight=20RECEIPT.Header=入库日期,100;入库单号,100;入库科室,100,DEPT_COM;验收单号,100;验收日期,100;入库人员,100,OPERATOR_COMRECEIPT.ParmMap=INWAREHOUSE_DATE;INWAREHOUSE_NO;INWAREHOUSE_DEPT;VERIFY_NO;RECEIPT_DATE;INWAREHOUSE_USERRECEIPT.Item=OPERATOR_COM;DEPT_COMRECEIPT.LockColumns=allRECEIPT.ClickedAction=onDevInwarehousetPanel_3.Type=TPaneltPanel_3.X=8tPanel_3.Y=9tPanel_3.Width=330tPanel_3.Height=209tPanel_3.Border=组|查询条件tPanel_3.Item=tLabel_12;tLabel_13;INWARE_START_DATE;tLabel_14;INWARE_END_DATE;tLabel_15;tLabel_16;tLabel_17;RECEIPT_NO;tLabel_18;RECEIPT_START_DATE;tLabel_19;RECEIPT_END_DATE;INWAREHOUSE_USER;INWAREHOUSE_NO;INWAREHOUSE_DEPT;UPDATE_FLG_A;UPDATE_FLG_CtPanel_3.Title=tPanel_3.AutoWidth=YUPDATE_FLG_C.Type=TRadioButtonUPDATE_FLG_C.X=101UPDATE_FLG_C.Y=15UPDATE_FLG_C.Width=81UPDATE_FLG_C.Height=23UPDATE_FLG_C.Text=已完成UPDATE_FLG_C.Group=group1UPDATE_FLG_A.Type=TRadioButtonUPDATE_FLG_A.X=21UPDATE_FLG_A.Y=16UPDATE_FLG_A.Width=70UPDATE_FLG_A.Height=23UPDATE_FLG_A.Text=未完成UPDATE_FLG_A.Group=group1UPDATE_FLG_A.Selected=YINWAREHOUSE_DEPT.Type=成本中心下拉区域INWAREHOUSE_DEPT.X=90INWAREHOUSE_DEPT.Y=97INWAREHOUSE_DEPT.Width=101INWAREHOUSE_DEPT.Height=23INWAREHOUSE_DEPT.Text=INWAREHOUSE_DEPT.HorizontalAlignment=2INWAREHOUSE_DEPT.PopupMenuHeader=代码,100;名称,100INWAREHOUSE_DEPT.PopupMenuWidth=300INWAREHOUSE_DEPT.PopupMenuHeight=300INWAREHOUSE_DEPT.FormatType=comboINWAREHOUSE_DEPT.ShowDownButton=YINWAREHOUSE_DEPT.Tip=成本中心INWAREHOUSE_DEPT.ShowColumnList=NAMEINWAREHOUSE_DEPT.Action=INWAREHOUSE_USER|onQueryINWAREHOUSE_NO.Type=TTextFieldINWAREHOUSE_NO.X=88INWAREHOUSE_NO.Y=43INWAREHOUSE_NO.Width=101INWAREHOUSE_NO.Height=20INWAREHOUSE_NO.Text=INWAREHOUSE_NO.Enabled=YINWAREHOUSE_NO.Action=onQueryINWAREHOUSE_USER.Type=人员INWAREHOUSE_USER.X=88INWAREHOUSE_USER.Y=124INWAREHOUSE_USER.Width=103INWAREHOUSE_USER.Height=23INWAREHOUSE_USER.Text=INWAREHOUSE_USER.HorizontalAlignment=2INWAREHOUSE_USER.PopupMenuHeader=ID,100;NAME,100INWAREHOUSE_USER.PopupMenuWidth=300INWAREHOUSE_USER.PopupMenuHeight=300INWAREHOUSE_USER.PopupMenuFilter=ID,1;NAME,1;PY1,1INWAREHOUSE_USER.FormatType=comboINWAREHOUSE_USER.ShowDownButton=YINWAREHOUSE_USER.Tip=人员INWAREHOUSE_USER.ShowColumnList=NAMEINWAREHOUSE_USER.Dept=<INWAREHOUSE_DEPT>RECEIPT_END_DATE.Type=TTextFormatRECEIPT_END_DATE.X=220RECEIPT_END_DATE.Y=178RECEIPT_END_DATE.Width=99RECEIPT_END_DATE.Height=20RECEIPT_END_DATE.Text=TTextFormatRECEIPT_END_DATE.FormatType=dateRECEIPT_END_DATE.Format=yyyy/MM/ddRECEIPT_END_DATE.showDownButton=YtLabel_19.Type=TLabeltLabel_19.X=198tLabel_19.Y=180tLabel_19.Width=22tLabel_19.Height=15tLabel_19.Text=至RECEIPT_START_DATE.Type=TTextFormatRECEIPT_START_DATE.X=88RECEIPT_START_DATE.Y=179RECEIPT_START_DATE.Width=107RECEIPT_START_DATE.Height=20RECEIPT_START_DATE.Text=TTextFormatRECEIPT_START_DATE.FormatType=dateRECEIPT_START_DATE.Format=yyyy/MM/ddRECEIPT_START_DATE.showDownButton=YtLabel_18.Type=TLabeltLabel_18.X=25tLabel_18.Y=183tLabel_18.Width=60tLabel_18.Height=15tLabel_18.Text=验收日期RECEIPT_NO.Type=TTextFieldRECEIPT_NO.X=88RECEIPT_NO.Y=152RECEIPT_NO.Width=103RECEIPT_NO.Height=20RECEIPT_NO.Text=tLabel_17.Type=TLabeltLabel_17.X=25tLabel_17.Y=154tLabel_17.Width=59tLabel_17.Height=15tLabel_17.Text=验收单号tLabel_16.Type=TLabeltLabel_16.X=22tLabel_16.Y=126tLabel_16.Width=72tLabel_16.Height=15tLabel_16.Text=入库人员tLabel_15.Type=TLabeltLabel_15.X=23tLabel_15.Y=99tLabel_15.Width=65tLabel_15.Height=15tLabel_15.Text=入库科室INWARE_END_DATE.Type=TTextFormatINWARE_END_DATE.X=220INWARE_END_DATE.Y=69INWARE_END_DATE.Width=95INWARE_END_DATE.Height=20INWARE_END_DATE.Text=TTextFormatINWARE_END_DATE.FormatType=dateINWARE_END_DATE.Format=yyyy/MM/ddINWARE_END_DATE.showDownButton=YtLabel_14.Type=TLabeltLabel_14.X=198tLabel_14.Y=72tLabel_14.Width=20tLabel_14.Height=15tLabel_14.Text=至INWARE_START_DATE.Type=TTextFormatINWARE_START_DATE.X=88INWARE_START_DATE.Y=70INWARE_START_DATE.Width=102INWARE_START_DATE.Height=20INWARE_START_DATE.Text=TTextFormatINWARE_START_DATE.FormatType=dateINWARE_START_DATE.Format=yyyy/MM/ddINWARE_START_DATE.showDownButton=YtLabel_13.Type=TLabeltLabel_13.X=23tLabel_13.Y=73tLabel_13.Width=61tLabel_13.Height=15tLabel_13.Text=入库日期tLabel_12.Type=TLabeltLabel_12.X=23tLabel_12.Y=48tLabel_12.Width=72tLabel_12.Height=15tLabel_12.Text=入库单号