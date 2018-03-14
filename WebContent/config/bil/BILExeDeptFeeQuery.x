## TBuilder Config File ## Title:执行科室费用查询## Company:JavaHis## Author:fudw 2009.07.31## version 1.0#<Type=TFrame>UI.Title=执行科室查询UI.MenuConfig=%ROOT%\config\bil\BILExeDeptFeeQueryMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.bil.BILExeDeptFeeQueryControlUI.item=tPanel_0;TABLEUI.layout=nullUI.Name=执行科室查询UI.Text=执行科室查询UI.Tip=执行科室查询UI.TopMenu=YUI.TopToolBar=YTABLE.Type=TTableTABLE.X=6TABLE.Y=107TABLE.Width=1009TABLE.Height=636TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoY=NTABLE.AutoWidth=YTABLE.AutoHeight=YTABLE.AutoX=YTABLE.Header=护士站,100;病案号,100;就诊序号,100;住院号,100;病患姓名,80;收费代码,80;收费项目名称,200;执行科室,100;单价,70,double,########0.00;数量,70;金额,70,double,########0.00;收费人员,80;收费日期,100TABLE.AutoModifyDataStore=YTABLE.ParmMap=STATION_DESC;MR_NO;CASE_NO;IPD_NO;PAT_NAME;ITEM_CODE;ORDER_DESC;DEPT_DESC;OWN_PRICE;CHARGE_T;TOT_AMT;USER_NAME;OPT_DATETABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,right;9,right;10,right;11,left;12,left;TABLE.LockColumns=allTABLE.ClickedAction=tPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=5tPanel_0.Width=1014tPanel_0.Height=99tPanel_0.Border=组tPanel_0.AutoX=YtPanel_0.AutoY=YtPanel_0.AutoWidth=YtPanel_0.Item=tPanel_1;tLabel_0;BILL_DATE;tLabel_1;tLabel_2;BILL_DATEE;tLabel_3;EXE_DEPT_CODE;STATION_CODE;tLabel_6;ITEM_CODE;ORDER_DESCORDER_DESC.Type=TTextFieldORDER_DESC.X=167ORDER_DESC.Y=72ORDER_DESC.Width=119ORDER_DESC.Height=20ORDER_DESC.Text=ITEM_CODE.Type=TTextFieldITEM_CODE.X=69ITEM_CODE.Y=72ITEM_CODE.Width=94ITEM_CODE.Height=20ITEM_CODE.Text=tLabel_6.Type=TLabeltLabel_6.X=4tLabel_6.Y=75tLabel_6.Width=64tLabel_6.Height=15tLabel_6.Text=计费项目:tLabel_6.Color=蓝STATION_CODE.Type=TTextFormatSTATION_CODE.X=66STATION_CODE.Y=11STATION_CODE.Width=106STATION_CODE.Height=20STATION_CODE.Text=STATION_CODE.FormatType=comboSTATION_CODE.showDownButton=YSTATION_CODE.HorizontalAlignment=2STATION_CODE.PopupMenuHeight=200STATION_CODE.PopupMenuSQL=SELECT STATION_CODE,STATION_DESC FROM SYS_STATION ORDER BY STATION_CODESTATION_CODE.PopupMenuHeader=代码,80;名称,120STATION_CODE.ValueColumn=STATION_CODESTATION_CODE.ShowColumnList=STATION_DESCSTATION_CODE.PopupMenuFilter=STATION_CODE,1;STATION_DESC,1STATION_CODE.HisOneNullRow=YIPD_NO.Type=TTextFieldIPD_NO.X=318IPD_NO.Y=8IPD_NO.Width=104IPD_NO.Height=20IPD_NO.Text=IPD_NO.Action=onIpdNoAciontLabel_5.Type=TLabeltLabel_5.X=252tLabel_5.Y=11tLabel_5.Width=65tLabel_5.Height=15tLabel_5.Text=住 院 号:tLabel_5.Color=蓝MR_NO.Type=TTextFieldMR_NO.X=141MR_NO.Y=9MR_NO.Width=104MR_NO.Height=20MR_NO.Text=MR_NO.Action=onMrNoActiontLabel_4.Type=TLabeltLabel_4.X=78tLabel_4.Y=12tLabel_4.Width=65tLabel_4.Height=15tLabel_4.Text=病 案 号:tLabel_4.Color=蓝EXE_DEPT_CODE.Type=TTextFormatEXE_DEPT_CODE.X=245EXE_DEPT_CODE.Y=9EXE_DEPT_CODE.Width=103EXE_DEPT_CODE.Height=20EXE_DEPT_CODE.Text=EXE_DEPT_CODE.FormatType=comboEXE_DEPT_CODE.showDownButton=YEXE_DEPT_CODE.PopupMenuHeader=代码,80;名称120EXE_DEPT_CODE.PopupMenuHeight=200EXE_DEPT_CODE.PopupMenuSQL=SELECT DEPT_CODE,DEPT_DESC,DEPT_PYCODE FROM SYS_DEPT WHERE HOSP_AREA='HIS' AND IPD_FIT_FLG='Y' AND ACTIVE_FLG='Y' AND FINAL_FLG='Y' ORDER BY DEPT_SEQNO,DEPT_CODEEXE_DEPT_CODE.ValueColumn=DEPT_CODEEXE_DEPT_CODE.ShowColumnList=DEPT_DESCEXE_DEPT_CODE.PopupMenuFilter=DEPT_CODE,1;DEPT_DESC,1;DEPT_PYCODE,1EXE_DEPT_CODE.HorizontalAlignment=2EXE_DEPT_CODE.HisOneNullRow=YtLabel_3.Type=TLabeltLabel_3.X=180tLabel_3.Y=13tLabel_3.Width=63tLabel_3.Height=15tLabel_3.Text=执行科室:tLabel_3.Color=蓝BILL_DATEE.Type=TTextFormatBILL_DATEE.X=229BILL_DATEE.Y=41BILL_DATEE.Width=158BILL_DATEE.Height=20BILL_DATEE.Text=BILL_DATEE.HorizontalAlignment=2BILL_DATEE.FormatType=dateBILL_DATEE.Format=yyyy/MM/dd HH:mm:ssBILL_DATEE.showDownButton=YtLabel_2.Type=TLabeltLabel_2.X=4tLabel_2.Y=13tLabel_2.Width=64tLabel_2.Height=15tLabel_2.Text=护 士 站:tLabel_2.Color=蓝tLabel_1.Type=TLabeltLabel_1.X=220tLabel_1.Y=51tLabel_1.Width=12tLabel_1.Height=15tLabel_1.Text=~tLabel_1.Color=蓝BILL_DATE.Type=TTextFormatBILL_DATE.X=69BILL_DATE.Y=41BILL_DATE.Width=152BILL_DATE.Height=20BILL_DATE.Text=BILL_DATE.FormatType=dateBILL_DATE.Format=yyyy/MM/dd HH:mm:ssBILL_DATE.showDownButton=YBILL_DATE.HorizontalAlignment=2tLabel_0.Type=TLabeltLabel_0.X=5tLabel_0.Y=45tLabel_0.Width=65tLabel_0.Height=15tLabel_0.Text=计费起日:tLabel_0.Color=蓝tPanel_1.Type=TPaneltPanel_1.X=392tPanel_1.Y=3tPanel_1.Width=612tPanel_1.Height=93tPanel_1.Border=组tPanel_1.AutoX=NtPanel_1.AutoY=NtPanel_1.AutoSize=0tPanel_1.Item=IN;OUT;tLabel_4;MR_NO;tLabel_5;IPD_NO;tLabel_7;PAT_NAME;tLabel_8;SEX_CODE;tLabel_9;IN_DATE;tLabel_10;DS_DATE;tLabel_17;CASE_NOtPanel_1.AutoWidth=NCASE_NO.Type=TTextFieldCASE_NO.X=490CASE_NO.Y=6CASE_NO.Width=106CASE_NO.Height=20CASE_NO.Text=tLabel_17.Type=TLabeltLabel_17.X=427tLabel_17.Y=11tLabel_17.Width=64tLabel_17.Height=15tLabel_17.Text=就诊序号:tLabel_17.Color=蓝DS_DATE.Type=TTextFormatDS_DATE.X=321DS_DATE.Y=66DS_DATE.Width=102DS_DATE.Height=20DS_DATE.Text=DS_DATE.HorizontalAlignment=2DS_DATE.FormatType=dateDS_DATE.Format=yyyy/MM/ddDS_DATE.showDownButton=YDS_DATE.Enabled=NtLabel_10.Type=TLabeltLabel_10.X=256tLabel_10.Y=70tLabel_10.Width=65tLabel_10.Height=15tLabel_10.Text=出院日期:IN_DATE.Type=TTextFormatIN_DATE.X=144IN_DATE.Y=67IN_DATE.Width=101IN_DATE.Height=20IN_DATE.Text=IN_DATE.HorizontalAlignment=2IN_DATE.FormatType=dateIN_DATE.Format=yyyy/MM/ddIN_DATE.showDownButton=YIN_DATE.Enabled=NtLabel_9.Type=TLabeltLabel_9.X=76tLabel_9.Y=69tLabel_9.Width=66tLabel_9.Height=15tLabel_9.Text=入院日期:SEX_CODE.Type=TComboBoxSEX_CODE.X=320SEX_CODE.Y=36SEX_CODE.Width=102SEX_CODE.Height=23SEX_CODE.Text=TButtonSEX_CODE.showID=YSEX_CODE.Editable=YSEX_CODE.Enabled=NSEX_CODE.ShowName=YSEX_CODE.ShowText=NSEX_CODE.TableShowList=nameSEX_CODE.SQL=SELECT SEX_CODE,SEX_DESC FROM SYS_SEXSEX_CODE.ParmMap=id:SEX_CODE;name:SEX_DESCtLabel_8.Type=TLabeltLabel_8.X=255tLabel_8.Y=41tLabel_8.Width=64tLabel_8.Height=15tLabel_8.Text=性    别:PAT_NAME.Type=TTextFieldPAT_NAME.X=143PAT_NAME.Y=40PAT_NAME.Width=101PAT_NAME.Height=20PAT_NAME.Text=PAT_NAME.Enabled=NtLabel_7.Type=TLabeltLabel_7.X=77tLabel_7.Y=43tLabel_7.Width=65tLabel_7.Height=15tLabel_7.Text=病患姓名:OUT.Type=TRadioButtonOUT.X=3OUT.Y=51OUT.Width=56OUT.Height=23OUT.Text=出院OUT.Group=IN.Type=TRadioButtonIN.X=3IN.Y=21IN.Width=53IN.Height=23IN.Text=在院IN.Group=IN.Selected=YIN.Action=