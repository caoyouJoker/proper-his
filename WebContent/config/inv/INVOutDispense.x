<Type=TFrame>UI.Title=物资出库单报表UI.MenuConfig=%ROOT%\config\inv\INVOutDispenseMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.inv.INVOutDispenseControlUI.item=tPanel_2;TABLEPANEUI.layout=nullUI.FocusList=UI.ShowMenu=YUI.ShowTitle=NUI.TopMenu=YUI.TopToolBar=YTABLEPANE.Type=TTabbedPaneTABLEPANE.X=9TABLEPANE.Y=86TABLEPANE.Width=992TABLEPANE.Height=657TABLEPANE.Item=tPanel_1;tPanel_4;tPanel_5TABLEPANE.AutoX=NTABLEPANE.AutoY=NTABLEPANE.AutoWidth=YTABLEPANE.AutoHeight=YTABLEPANE.ChangedAction=onTablePaneClickedtPanel_5.Type=TPaneltPanel_5.X=154tPanel_5.Y=7tPanel_5.Width=81tPanel_5.Height=81tPanel_5.Name=出库明细tPanel_5.Item=Table_OUT_DETAILTable_OUT_DETAIL.Type=TTableTable_OUT_DETAIL.X=3Table_OUT_DETAIL.Y=5Table_OUT_DETAIL.Width=979Table_OUT_DETAIL.Height=551Table_OUT_DETAIL.SpacingRow=1Table_OUT_DETAIL.RowHeight=20Table_OUT_DETAIL.Header=物资编码,150;物资名称,150;规格型号,120;出库单号,100;日期,150;数量,100;入库部门,100;出库部门,100Table_OUT_DETAIL.ParmMap=INV_CODE;INV_CHN_DESC;DESCRIPTION;DISPENSE_NO;VALID_DATE;QTY;FROM_ORG_DESC;TO_ORG_DESCtPanel_4.Type=TPaneltPanel_4.X=43tPanel_4.Y=9tPanel_4.Width=81tPanel_4.Height=81tPanel_4.Text=tPanel_4.Name=入库明细tPanel_4.Item=Table_IN_DETAILTable_IN_DETAIL.Type=TTableTable_IN_DETAIL.X=3Table_IN_DETAIL.Y=5Table_IN_DETAIL.Width=979Table_IN_DETAIL.Height=574Table_IN_DETAIL.SpacingRow=1Table_IN_DETAIL.RowHeight=20Table_IN_DETAIL.Header=物资编码,150;物资名称,150;规格型号,120;入库单号,100;日期,150;数量,100Table_IN_DETAIL.ParmMap=INV_CODE;INV_CHN_DESC;DESCRIPTION;DISPENSE_NO;VALID_DATE;QTYtPanel_1.Type=TPaneltPanel_1.X=34tPanel_1.Y=40tPanel_1.Width=714tPanel_1.Height=397tPanel_1.AutoWidth=YtPanel_1.AutoHeight=YtPanel_1.Item=Table_IN_ALLtPanel_1.Text=tPanel_1.Name=入库汇总Table_IN_ALL.Type=TTableTable_IN_ALL.X=6Table_IN_ALL.Y=4Table_IN_ALL.Width=994Table_IN_ALL.Height=619Table_IN_ALL.SpacingRow=1Table_IN_ALL.RowHeight=20Table_IN_ALL.AutoWidth=YTable_IN_ALL.AutoHeight=YTable_IN_ALL.AutoX=NTable_IN_ALL.AutoY=NTable_IN_ALL.Header=物资编码,180;物资名称,450;规格型号,200;验收数,100Table_IN_ALL.ParmMap=INV_CODE;INV_CHN_DESC;DESCRIPTION;IN_QTYTable_IN_ALL.LockRows=Table_IN_ALL.LockColumns=0,1,2,3Table_IN_ALL.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,rightTable_IN_ALL.ClickedAction=onTableClickedtPanel_2.Type=TPaneltPanel_2.X=5tPanel_2.Y=5tPanel_2.Width=1014tPanel_2.Height=73tPanel_2.Border=组|查询条件tPanel_2.AutoY=YtPanel_2.AutoWidth=YtPanel_2.AutoX=YtPanel_2.Item=tLabel_1;START_DATE;tTextField_2;tLabel_2;END_DATE;tTextField_3;INV_CODEINV_CODE.Type=TTextFieldINV_CODE.X=539INV_CODE.Y=28INV_CODE.Width=77INV_CODE.Height=20INV_CODE.Text=INV_CODE.Visible=NtTextField_3.Type=TTextFieldtTextField_3.X=423tTextField_3.Y=28tTextField_3.Width=69tTextField_3.Height=20tTextField_3.Text=23:59:59tTextField_3.Enabled=NEND_DATE.Type=TTextFormatEND_DATE.X=307END_DATE.Y=28END_DATE.Width=111END_DATE.Height=20END_DATE.Text=TTextFormatEND_DATE.showDownButton=YEND_DATE.FormatType=dateEND_DATE.Format=yyyy/MM/ddtLabel_2.Type=TLabeltLabel_2.X=280tLabel_2.Y=31tLabel_2.Width=20tLabel_2.Height=15tLabel_2.Text=～tTextField_2.Type=TTextFieldtTextField_2.X=205tTextField_2.Y=28tTextField_2.Width=68tTextField_2.Height=20tTextField_2.Text=00:00:00tTextField_2.Enabled=NSTART_DATE.Type=TTextFormatSTART_DATE.X=90START_DATE.Y=28START_DATE.Width=109START_DATE.Height=20START_DATE.Text=TTextFormatSTART_DATE.showDownButton=YSTART_DATE.FormatType=dateSTART_DATE.Format=yyyy/MM/ddtLabel_1.Type=TLabeltLabel_1.X=24tLabel_1.Y=31tLabel_1.Width=64tLabel_1.Height=15tLabel_1.Text=统计时间:tLabel_1.Color=bluetLabel_1.FontName=