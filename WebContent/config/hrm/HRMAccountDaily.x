## TBuilder Config File ## Title:健检日结## Company:JavaHis## Author:fudw 2009.07.21## version 1.0#<Type=TFrame>UI.Title=健检日结UI.MenuConfig=%ROOT%\config\hrm\HRMAccountDailyMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.hrm.HRMAccountDailyControlUI.item=tPanel_0;tPanel_1;TABLEUI.layout=nullUI.Name=健检日结UI.Text=健检日结UI.Tip=健检日结UI.TopMenu=YUI.TopToolBar=YTABLE.Type=TTableTABLE.X=4TABLE.Y=153TABLE.Width=1009TABLE.Height=590TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoY=NTABLE.AutoWidth=YTABLE.AutoHeight=YTABLE.AutoX=YTABLE.Header=选,30,boolean;区域,120;日结号,120;日结时间,140;日结人员,120,ACCOUNT_SEQ_T;日结金额,100,double,##########0.00;日结状态,100,STATUS;作废张数,80,intTABLE.ParmMap=S;REGION_CHN_DESC;ACCOUNT_SEQ;ACCOUNT_DATE;ACCOUNT_USER;AR_AMT;STATUS;INVALID_COUNTTABLE.LockColumns=1,2,3,4,5,6TABLE.HorizontalAlignmentData=TABLE.ColumnHorizontalAlignmentData=1,left;2,left;3,left;4,right;5,left;6,rightTABLE.Item=ACCOUNT_SEQ_TtPanel_1.Type=TPaneltPanel_1.X=5tPanel_1.Y=103tPanel_1.Width=1014tPanel_1.Height=48tPanel_1.Border=组tPanel_1.AutoX=YtPanel_1.AutoWidth=YtPanel_1.Item=tLabel_4;ACCOUNT_DATE;tLabel_6;ACCOUNT_DATEE;SELECT;TOGEDER_FLG;S_TIME;E_TIMEE_TIME.Type=TTextFieldE_TIME.X=710E_TIME.Y=6E_TIME.Width=67E_TIME.Height=20E_TIME.Text=23:59:59E_TIME.Enabled=NS_TIME.Type=TTextFieldS_TIME.X=449S_TIME.Y=6S_TIME.Width=67S_TIME.Height=20S_TIME.Text=00:00:00S_TIME.Enabled=NTOGEDER_FLG.Type=TCheckBoxTOGEDER_FLG.X=69TOGEDER_FLG.Y=21TOGEDER_FLG.Width=53TOGEDER_FLG.Height=23TOGEDER_FLG.Text=合并SELECT.Type=TCheckBoxSELECT.X=4SELECT.Y=21SELECT.Width=53SELECT.Height=23SELECT.Text=全选SELECT.Action=onSelectAllACCOUNT_DATEE.Type=TTextFormatACCOUNT_DATEE.X=594ACCOUNT_DATEE.Y=6ACCOUNT_DATEE.Width=110ACCOUNT_DATEE.Height=20ACCOUNT_DATEE.Text=ACCOUNT_DATEE.Format=yyyy/MM/ddACCOUNT_DATEE.FormatType=dateACCOUNT_DATEE.showDownButton=YACCOUNT_DATEE.HorizontalAlignment=2tLabel_6.Type=TLabeltLabel_6.X=529tLabel_6.Y=9tLabel_6.Width=64tLabel_6.Height=15tLabel_6.Text=查询迄日:tLabel_6.Color=蓝ACCOUNT_DATE.Type=TTextFormatACCOUNT_DATE.X=335ACCOUNT_DATE.Y=6ACCOUNT_DATE.Width=110ACCOUNT_DATE.Height=20ACCOUNT_DATE.Text=ACCOUNT_DATE.Format=yyyy/MM/ddACCOUNT_DATE.showDownButton=YACCOUNT_DATE.FormatType=dateACCOUNT_DATE.HorizontalAlignment=2tLabel_4.Type=TLabeltLabel_4.X=242tLabel_4.Y=9tLabel_4.Width=91tLabel_4.Height=15tLabel_4.Text=报表查询起日:tLabel_4.Color=蓝tPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=5tPanel_0.Width=1014tPanel_0.Height=97tPanel_0.AutoX=YtPanel_0.AutoY=YtPanel_0.AutoWidth=YtPanel_0.Border=组tPanel_0.Item=tLabel_0;tLabel_1;tLabel_7;REGION_CODE;ACCOUNT_USER;tLabel_55;ADM_TYPE;ACCOUNT_SEQ_T;DEPT;tLabel_2tPanel_0.AutoHeight=NtLabel_2.Type=TLabeltLabel_2.X=555tLabel_2.Y=63tLabel_2.Width=72tLabel_2.Height=15tLabel_2.Text=科 室:tLabel_2.Color=blueDEPT.Type=科室DEPT.X=607DEPT.Y=59DEPT.Width=101DEPT.Height=23DEPT.Text=DEPT.HorizontalAlignment=2DEPT.PopupMenuHeader=代码,100;名称,100DEPT.PopupMenuWidth=300DEPT.PopupMenuHeight=300DEPT.FormatType=comboDEPT.ShowDownButton=YDEPT.Tip=科室DEPT.ShowColumnList=NAMEDEPT.ClickedAction=ACCOUNT_USER|onQueryDEPT.HisOneNullRow=YDEPT.FocusLostAction=ACCOUNT_USER|onQueryDEPT.DoubleClickedAction=ACCOUNT_USER|onQueryACCOUNT_SEQ_T.Type=人员ACCOUNT_SEQ_T.X=106ACCOUNT_SEQ_T.Y=47ACCOUNT_SEQ_T.Width=119ACCOUNT_SEQ_T.Height=23ACCOUNT_SEQ_T.Text=ACCOUNT_SEQ_T.HorizontalAlignment=2ACCOUNT_SEQ_T.PopupMenuHeader=代码,100;名称,100ACCOUNT_SEQ_T.PopupMenuWidth=300ACCOUNT_SEQ_T.PopupMenuHeight=300ACCOUNT_SEQ_T.PopupMenuFilter=ID,1;NAME,1;PY1,1ACCOUNT_SEQ_T.FormatType=comboACCOUNT_SEQ_T.ShowDownButton=YACCOUNT_SEQ_T.Tip=人员ACCOUNT_SEQ_T.ShowColumnList=NAMEACCOUNT_SEQ_T.Visible=NADM_TYPE.Type=TComboBoxADM_TYPE.X=415ADM_TYPE.Y=60ADM_TYPE.Width=106ADM_TYPE.Height=23ADM_TYPE.Text=TButtonADM_TYPE.showID=YADM_TYPE.Editable=YADM_TYPE.StringData=[[id,text],[,],[O,门诊],[E,急诊],[H,健检]]ADM_TYPE.TableShowList=textADM_TYPE.SelectedAction=onAdmTypeClickADM_TYPE.Action=tLabel_55.Type=TLabeltLabel_55.X=356tLabel_55.Y=64tLabel_55.Width=54tLabel_55.Height=15tLabel_55.Text=门急别:tLabel_55.Color=蓝ACCOUNT_USER.Type=人员ACCOUNT_USER.X=808ACCOUNT_USER.Y=59ACCOUNT_USER.Width=97ACCOUNT_USER.Height=23ACCOUNT_USER.Text=ACCOUNT_USER.HorizontalAlignment=2ACCOUNT_USER.PopupMenuHeader=代码,100;名称,100ACCOUNT_USER.PopupMenuWidth=300ACCOUNT_USER.PopupMenuHeight=300ACCOUNT_USER.PopupMenuFilter=ID,1;NAME,1;PY1,1ACCOUNT_USER.FormatType=comboACCOUNT_USER.ShowDownButton=YACCOUNT_USER.Tip=人员ACCOUNT_USER.ShowColumnList=NAMEACCOUNT_USER.PosType=5ACCOUNT_USER.HisOneNullRow=YACCOUNT_USER.Dept=<DEPT>REGION_CODE.Type=区域下拉列表REGION_CODE.X=196REGION_CODE.Y=60REGION_CODE.Width=135REGION_CODE.Height=23REGION_CODE.Text=TButtonREGION_CODE.showID=YREGION_CODE.showName=YREGION_CODE.showText=NREGION_CODE.showValue=NREGION_CODE.showPy1=NREGION_CODE.showPy2=NREGION_CODE.Editable=YREGION_CODE.Tip=区域REGION_CODE.TableShowList=nameREGION_CODE.ModuleParmString=REGION_CODE.ModuleParmTag=REGION_CODE.Enabled=YREGION_CODE.ExpandWidth=80tLabel_7.Type=TLabeltLabel_7.X=144tLabel_7.Y=64tLabel_7.Width=67tLabel_7.Height=15tLabel_7.Text=区 域:tLabel_7.Color=蓝tLabel_1.Type=TLabeltLabel_1.X=736tLabel_1.Y=63tLabel_1.Width=65tLabel_1.Height=15tLabel_1.Text=收 费 员:tLabel_1.Color=蓝tLabel_0.Type=TLabeltLabel_0.X=369tLabel_0.Y=17tLabel_0.Width=279tLabel_0.Height=24tLabel_0.Text=健 检 收 费 日 结(报表)tLabel_0.FontSize=24tLabel_0.FontName=宋体tLabel_0.Color=蓝tLabel_0.VerticalAlignment=0