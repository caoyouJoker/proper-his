## TBuilder Config File ## Title:## Company:JavaHis## Author:Yuanxm 2012.10.11## version 1.0#<Type=TFrame>UI.Title=医嘱出智能柜UI.MenuConfig=%ROOT%\config\spc\SPCErOutCabinetMenu.xUI.Width=800UI.Height=600UI.toolbar=YUI.controlclassname=com.javahis.ui.spc.SPCErOutCabinetControlUI.item=tPanel_1;tTabbedPane_0;TABLE_LISTUI.layout=nullUI.TopMenu=YUI.TopToolBar=YUI.Name=UI.Text=医嘱出智能柜UI.Tip=医嘱出智能柜UI.AutoWidth=NUI.AutoHeight=NTABLE_LIST.Type=TTableTABLE_LIST.X=7TABLE_LIST.Y=77TABLE_LIST.Width=762TABLE_LIST.Height=92TABLE_LIST.SpacingRow=1TABLE_LIST.RowHeight=20TABLE_LIST.Header=处方签号,100;病案号,100;病患姓名,150;开立日期,120;开立医师,100TABLE_LIST.ParmMap=RX_NO;MR_NO;PAT_NAME;ORDER_DATE;USER_NAMETABLE_LIST.ClickedAction=onTableListClickTABLE_LIST.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,leftTABLE_LIST.LockColumns=0,1,2,3tTabbedPane_0.Type=TTabbedPanetTabbedPane_0.X=7tTabbedPane_0.Y=174tTabbedPane_0.Width=788tTabbedPane_0.Height=421tTabbedPane_0.AutoX=NtTabbedPane_0.AutoY=NtTabbedPane_0.AutoWidth=YtTabbedPane_0.AutoHeight=YtTabbedPane_0.Item=PANEL_MtTabbedPane_0.ChangedAction=onQueryPANEL_M.Type=TPanelPANEL_M.X=56PANEL_M.Y=7PANEL_M.Width=81PANEL_M.Height=81PANEL_M.Name=麻精PANEL_M.Text=麻精PANEL_M.Item=tLabel_15;CONTAINER_ID;TOXIC_QTY;CONTAINER_DESC;TABLE_M;TABLE_DTABLE_D.Type=TTableTABLE_D.X=2TABLE_D.Y=3TABLE_D.Width=759TABLE_D.Height=81TABLE_D.SpacingRow=1TABLE_D.RowHeight=20TABLE_D.Header=药品名称,150;用量,100;用法,80;频次,100;总量,80;单位,60;已出库数量,100TABLE_D.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,right;5,left;6,rightTABLE_D.ParmMap=ORDER_DESC;MEDI_QTY;ROUTE_CHN_DESC;FREQ_CHN_DESC;DISPENSE_QTY;DISPENSE_UNIT;ACUM_OUTBOUND_QTY;ORDER_CODE;ORDER_SEQ;CASE_NO;ORDER_NO;START_DTTMTABLE_D.ClickedAction=onTableDClickedTABLE_M.Type=TTableTABLE_M.X=2TABLE_M.Y=116TABLE_M.Width=760TABLE_M.Height=84TABLE_M.SpacingRow=1TABLE_M.RowHeight=20TABLE_M.Header=序号,50;药品名称,150;麻精流水号,100;批号,100;效期,100;来源容器名称,200TABLE_M.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,left;6,rightTABLE_M.ParmMap=ROW_NUM;ORDER_DESC;TOXIC_ID;BATCH_NO;VALID_DATE;CONTAINER_DESC;VERIFYIN_PRICE;CONTAINER_IDTABLE_M.ClickedAction=onTableDClickedCONTAINER_DESC.Type=TTextFieldCONTAINER_DESC.X=216CONTAINER_DESC.Y=88CONTAINER_DESC.Width=215CONTAINER_DESC.Height=24CONTAINER_DESC.Text=CONTAINER_DESC.Enabled=NTOXIC_QTY.Type=TNumberTextFieldTOXIC_QTY.X=166TOXIC_QTY.Y=88TOXIC_QTY.Width=45TOXIC_QTY.Height=24TOXIC_QTY.Text=0TOXIC_QTY.Format=#########0TOXIC_QTY.Visible=YTOXIC_QTY.Enabled=NCONTAINER_ID.Type=TTextFieldCONTAINER_ID.X=47CONTAINER_ID.Y=87CONTAINER_ID.Width=116CONTAINER_ID.Height=26CONTAINER_ID.Text=CONTAINER_ID.Action=onCodetLabel_15.Type=TLabeltLabel_15.X=6tLabel_15.Y=89tLabel_15.Width=42tLabel_15.Height=22tLabel_15.Text=条码:tPanel_1.Type=TPaneltPanel_1.X=5tPanel_1.Y=-2tPanel_1.Width=797tPanel_1.Height=78tPanel_1.Border=组|操作信息tPanel_1.Item=tLabel_12;RX_NO;tLabel_18;CABINET_ID;tLabel_19;CABINET_DESC;tLabel_21;ORG_CHN_DESC;tLabel_9;UPDATE_N;UPDATE_Y;tLabel_10;START_DATE;tLabel_14;END_DATEtPanel_1.Title=tPanel_1.AutoWidth=NtPanel_1.AutoHeight=NEND_DATE.Type=TTextFormatEND_DATE.X=640END_DATE.Y=45END_DATE.Width=127END_DATE.Height=26END_DATE.Text=TTextFormatEND_DATE.Format=yyyy/MM/dd HH:mm:ssEND_DATE.FormatType=dateEND_DATE.HisOneNullRow=NEND_DATE.showDownButton=YtLabel_14.Type=TLabeltLabel_14.X=624tLabel_14.Y=50tLabel_14.Width=14tLabel_14.Height=15tLabel_14.Text=～START_DATE.Type=TTextFormatSTART_DATE.X=492START_DATE.Y=45START_DATE.Width=129START_DATE.Height=26START_DATE.Text=TTextFormatSTART_DATE.Format=yyyy/MM/dd HH:mm:ssSTART_DATE.FormatType=dateSTART_DATE.HisOneNullRow=NSTART_DATE.showDownButton=YtLabel_10.Type=TLabeltLabel_10.X=425tLabel_10.Y=51tLabel_10.Width=64tLabel_10.Height=15tLabel_10.Text=日期区间:tLabel_10.Color=blueUPDATE_Y.Type=TRadioButtonUPDATE_Y.X=355UPDATE_Y.Y=46UPDATE_Y.Width=70UPDATE_Y.Height=23UPDATE_Y.Text=已出库UPDATE_Y.Group=group1UPDATE_N.Type=TRadioButtonUPDATE_N.X=291UPDATE_N.Y=47UPDATE_N.Width=67UPDATE_N.Height=23UPDATE_N.Text=未出库UPDATE_N.Group=group1UPDATE_N.Selected=YtLabel_9.Type=TLabeltLabel_9.X=229tLabel_9.Y=50tLabel_9.Width=72tLabel_9.Height=15tLabel_9.Text=出库状态:tLabel_9.Color=blueORG_CHN_DESC.Type=TTextFieldORG_CHN_DESC.X=491ORG_CHN_DESC.Y=17ORG_CHN_DESC.Width=126ORG_CHN_DESC.Height=26ORG_CHN_DESC.Text=ORG_CHN_DESC.Enabled=NtLabel_21.Type=TLabeltLabel_21.X=423tLabel_21.Y=19tLabel_21.Width=74tLabel_21.Height=18tLabel_21.Text=所属部门：tLabel_21.FontSize=14CABINET_DESC.Type=TTextFieldCABINET_DESC.X=296CABINET_DESC.Y=17CABINET_DESC.Width=121CABINET_DESC.Height=27CABINET_DESC.Text=CABINET_DESC.Enabled=NtLabel_19.Type=TLabeltLabel_19.X=227tLabel_19.Y=16tLabel_19.Width=64tLabel_19.Height=22tLabel_19.Text=名    称:tLabel_19.FontSize=14CABINET_ID.Type=TTextFieldCABINET_ID.X=86CABINET_ID.Y=15CABINET_ID.Width=135CABINET_ID.Height=26CABINET_ID.Text=CABINET_ID.Enabled=NtLabel_18.Type=TLabeltLabel_18.X=7tLabel_18.Y=18tLabel_18.Width=84tLabel_18.Height=25tLabel_18.Text=智能柜编号：tLabel_18.FontSize=14RX_NO.Type=TTextFieldRX_NO.X=85RX_NO.Y=45RX_NO.Width=136RX_NO.Height=26RX_NO.Text=RX_NO.Enabled=YRX_NO.Action=onTaskmedNoClickedtLabel_12.Type=TLabeltLabel_12.X=19tLabel_12.Y=49tLabel_12.Width=71tLabel_12.Height=15tLabel_12.Text=处方签号：tLabel_12.Color=蓝tLabel_12.FontSize=14