## TBuilder Config File ## Title:## Company:JavaHis## Author:wuxy 2017.07.06## version 1.0#<Type=TFrame>UI.Title=保健办工作站UI.MenuConfig=%ROOT%\config\bms\HOStationMenu.xUI.Width=1024UI.Height=764UI.toolbar=YUI.controlclassname=com.javahis.ui.bms.HOStationControlUI.item=tPanel_1;tPanel_7UI.layout=nullUI.TopMenu=YUI.ShowMenu=YUI.TopToolBar=YtPanel_7.Type=TPaneltPanel_7.X=5tPanel_7.Y=344tPanel_7.Width=1014tPanel_7.Height=294tPanel_7.Border=组|记录清单tPanel_7.Item=tTable_1tPanel_7.AutoWidth=YtTable_1.Type=TTabletTable_1.X=10tTable_1.Y=20tTable_1.Width=989tTable_1.Height=264tTable_1.SpacingRow=1tTable_1.RowHeight=20tTable_1.Header=医嘱代码,100;医嘱名称,150;用量,80,double,######0.000;单位,80;频次,100;用法,80;天数,80;总量,80,double,######0.00;单位,80,DOSAGE_UNIT;自费价,80,double,######0.0000;应收小计,100,double,######0.00;就诊科别,100,DEPT_CODE;医师,100,DR_CODE;执行科室,100,DEPT_CODE;执行医师,100,EXEC_DR_CODEtTable_1.Item=DEPT_CODE;DR_CODE;EXEC_DR_CODE;DOSAGE_UNITtTable_1.ParmMap=ORDER_CODE;ORDER_DESC;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;DOSAGE_QTY;DOSAGE_UNIT;OWN_PRICE;AR_AMT;DEPT_CODE;DR_CODE;EXEC_DEPT_CODE;EXEC_DR_CODEtTable_1.LockColumns=0,1,2,3,4,5,6,7,8,9,10,11,12,13tTable_1.AutoWidth=YtTable_1.ColumnHorizontalAlignmentData=0,right;1,left;2,right;3,right;4,left;5,left;6,right;7,right;8,left;9,right;10,right;11,left;12,left;13,left;14,lefttPanel_1.Type=TPaneltPanel_1.X=4tPanel_1.Y=10tPanel_1.Width=1015tPanel_1.Height=335tPanel_1.Border=组|就诊记录tPanel_1.Item=tLabel_10;tLabel_11;S_DATE;E_DATE;tTable_0;SELECT_ALL;系统管理_0;DEPT_CODE;DR_CODE;tTable_2;DOSAGE_UNIT;EXEC_DR_CODEtPanel_1.Visible=YtPanel_1.AutoWidth=YtPanel_1.TopToolBar=YtPanel_1.TopMenu=YtPanel_1.ShowMenu=YEXEC_DR_CODE.Type=门诊适用人员EXEC_DR_CODE.X=783EXEC_DR_CODE.Y=20EXEC_DR_CODE.Width=81EXEC_DR_CODE.Height=23EXEC_DR_CODE.Text=EXEC_DR_CODE.HorizontalAlignment=2EXEC_DR_CODE.PopupMenuHeader=代码,100;名称,100EXEC_DR_CODE.PopupMenuWidth=300EXEC_DR_CODE.PopupMenuHeight=300EXEC_DR_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1EXEC_DR_CODE.FormatType=comboEXEC_DR_CODE.ShowDownButton=YEXEC_DR_CODE.Tip=门诊适用人员EXEC_DR_CODE.ShowColumnList=NAMEEXEC_DR_CODE.Visible=NDOSAGE_UNIT.Type=计量单位下拉列表DOSAGE_UNIT.X=457DOSAGE_UNIT.Y=20DOSAGE_UNIT.Width=81DOSAGE_UNIT.Height=23DOSAGE_UNIT.Text=单位DOSAGE_UNIT.showID=YDOSAGE_UNIT.showName=YDOSAGE_UNIT.showText=NDOSAGE_UNIT.showValue=NDOSAGE_UNIT.showPy1=YDOSAGE_UNIT.showPy2=YDOSAGE_UNIT.Editable=YDOSAGE_UNIT.Tip=计量单位DOSAGE_UNIT.TableShowList=nameDOSAGE_UNIT.Visible=NtTable_2.Type=TTabletTable_2.X=504tTable_2.Y=50tTable_2.Width=486tTable_2.Height=273tTable_2.SpacingRow=1tTable_2.RowHeight=20tTable_2.Header=选,30,boolean;检验日期,100,timestamp,yyyy/MM/dd;时段,80;科别,100,DEPT_CODE;号别,100;诊室,100;看诊医生,100,DR_CODE;病案号,100;姓名,100;就诊状态,100tTable_2.Item=DEPT_CODE;DR_CODEtTable_2.ParmMap=SELECT_FLG;ADM_DATE;SESSION_DESC;DEPT_CODE;CLINICTYPE_DESC;CLINICROOM_DESC;DR_CODE;MR_NO;PAT_NAME;ADM_STATUStTable_2.Visible=NDR_CODE.Type=门诊适用人员DR_CODE.X=681DR_CODE.Y=19DR_CODE.Width=81DR_CODE.Height=23DR_CODE.Text=DR_CODE.HorizontalAlignment=2DR_CODE.PopupMenuHeader=代码,100;名称,100DR_CODE.PopupMenuWidth=300DR_CODE.PopupMenuHeight=300DR_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1DR_CODE.FormatType=comboDR_CODE.ShowDownButton=YDR_CODE.Tip=门诊适用人员DR_CODE.ShowColumnList=NAMEDR_CODE.Visible=NDEPT_CODE.Type=科室DEPT_CODE.X=600DEPT_CODE.Y=20DEPT_CODE.Width=81DEPT_CODE.Height=23DEPT_CODE.Text=DEPT_CODE.HorizontalAlignment=2DEPT_CODE.PopupMenuHeader=代码,100;名称,100DEPT_CODE.PopupMenuWidth=300DEPT_CODE.PopupMenuHeight=300DEPT_CODE.FormatType=comboDEPT_CODE.ShowDownButton=YDEPT_CODE.Tip=科室DEPT_CODE.ShowColumnList=NAMEDEPT_CODE.Visible=N系统管理_0.Type=系统管理系统管理_0.X=456系统管理_0.Y=25SELECT_ALL.Type=TCheckBoxSELECT_ALL.X=350SELECT_ALL.Y=20SELECT_ALL.Width=81SELECT_ALL.Height=23SELECT_ALL.Text=全选SELECT_ALL.Action=onCheckSelectAlltTable_0.Type=TTabletTable_0.X=11tTable_0.Y=49tTable_0.Width=996tTable_0.Height=274tTable_0.SpacingRow=1tTable_0.RowHeight=20tTable_0.Header=选,30,boolean;病案号,100;姓名,100;检验日期,100,timestamp,yyyy/MM/dd;时段,80;诊室,100;科别,100,DEPT_CODE;号别,100;看诊医生,100,DR_CODE;就诊状态,100tTable_0.Item=DEPT_CODE;DR_CODEtTable_0.ParmMap=SELECT_FLG;MR_NO;PAT_NAME;ADM_DATE;SESSION_DESC;CLINICROOM_DESC;DEPT_CODE;CLINICTYPE_DESC;DR_CODE;ADM_STATUStTable_0.LockColumns=1,2,3,4,5,6,7,8,9tTable_0.ClickedAction=onTableClickedtTable_0.RightClickedAction=tTable_0.AutoWidth=YtTable_0.ColumnHorizontalAlignmentData=1;2,left;3;4,left;5,left;6,left;7,left;8,left;9,leftE_DATE.Type=TTextFormatE_DATE.X=191E_DATE.Y=22E_DATE.Width=110E_DATE.Height=20E_DATE.Text=E_DATE.FormatType=dateE_DATE.Format=yyyy/MM/ddE_DATE.showDownButton=YS_DATE.Type=TTextFormatS_DATE.X=59S_DATE.Y=22S_DATE.Width=110S_DATE.Height=20S_DATE.Text=S_DATE.FormatType=dateS_DATE.Format=yyyy/MM/ddS_DATE.showDownButton=YtLabel_11.Type=TLabeltLabel_11.X=174tLabel_11.Y=30tLabel_11.Width=16tLabel_11.Height=14tLabel_11.Text=~tLabel_11.Color=蓝tLabel_10.Type=TLabeltLabel_10.X=20tLabel_10.Y=25tLabel_10.Width=40tLabel_10.Height=15tLabel_10.Text=日期:tLabel_10.Color=蓝