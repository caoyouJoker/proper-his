## TBuilder Config File ## Title:pangben## Company:JavaHis## Author:庞犇 2011.05.03## version 1.0#<Type=TFrame>UI.Title=变异字典查询UI.MenuConfig=%ROOT%/config/clp/CLPVarMoncatMenu.xUI.Width=989UI.Height=736UI.toolbar=YUI.controlclassname=com.javahis.ui.clp.ClpVarMoncatControlUI.item=tPanel_1;tPanel_2;tPanel_9UI.layout=nullUI.ShowMenu=YUI.TopToolBar=YUI.TopMenu=YtPanel_9.Type=TPaneltPanel_9.X=1tPanel_9.Y=431tPanel_9.Width=979tPanel_9.Height=300tPanel_9.Border=组|tPanel_9.MoveType=0tPanel_9.AutoX=YtPanel_9.AutoWidth=YtPanel_9.Item=TABLE_VARIANCEtPanel_9.AutoHeight=YTABLE_VARIANCE.Type=TTableTABLE_VARIANCE.X=7TABLE_VARIANCE.Y=5TABLE_VARIANCE.Width=956TABLE_VARIANCE.Height=283TABLE_VARIANCE.SpacingRow=1TABLE_VARIANCE.RowHeight=20TABLE_VARIANCE.AutoX=YTABLE_VARIANCE.AutoWidth=YTABLE_VARIANCE.Header=变异类别,80;原因代码,80;序号,30,;注记,30;原因说明,200;原因拼音,120;原因英文说明,200;路径项目,200,CLNCPATH_CODE;备注,200TABLE_VARIANCE.SQL=TABLE_VARIANCE.ColumnHorizontalAlignmentData=0,left;1,left;4,left;5,left;6,left;7,left;8,leftTABLE_VARIANCE.ParmMap=MONCAT_CODE;VARIANCE_CODE;SEQ;PY2;VARIANCE_CHN_DESC;PY1;VARIANCE_ENG_DESC;CLNCPATH_CODE;DESCRIPTIONTABLE_VARIANCE.LockColumns=0,5TABLE_VARIANCE.Item=CLNCPATH_CODETABLE_VARIANCE.AutoModifyDataStore=NTABLE_VARIANCE.AutoHeight=YTABLE_VARIANCE.ChangeAction=TABLE_VARIANCE.ColumnChangeAction=TABLE_VARIANCE.ClickedAction=tPanel_2.Type=TPaneltPanel_2.X=9tPanel_2.Y=130tPanel_2.Width=979tPanel_2.Height=292tPanel_2.Border=组|tPanel_2.AutoX=YtPanel_2.AutoWidth=YtPanel_2.Item=TABLE_VARMONCATTABLE_VARMONCAT.Type=TTableTABLE_VARMONCAT.X=8TABLE_VARMONCAT.Y=9TABLE_VARMONCAT.Width=957TABLE_VARMONCAT.Height=272TABLE_VARMONCAT.SpacingRow=1TABLE_VARMONCAT.RowHeight=20TABLE_VARMONCAT.AutoX=YTABLE_VARMONCAT.AutoWidth=YTABLE_VARMONCAT.Header=区域,120;变异类别代码,100;序号,30;注记,30;变异类别中文说明,200;变异类别拼音,160;变异类别英文说明,200;备注,160TABLE_VARMONCAT.ParmMap=REGION_CHN_DESC;MONCAT_CODE;SEQ;PY2;MONCAT_CHN_DESC;PY1;MONCAT_ENG_DESC;DESCRIPTION;REGION_CODETABLE_VARMONCAT.ColumnHorizontalAlignmentData=0,left;1,left;4,left;5,left;6,left;7,left;8,leftTABLE_VARMONCAT.ClickedAction=onTableVarMoncatClickedTABLE_VARMONCAT.LockColumns=allTABLE_VARMONCAT.AutoHeight=YtPanel_1.Type=TPaneltPanel_1.X=5tPanel_1.Y=11tPanel_1.Width=979tPanel_1.Height=112tPanel_1.Border=组|tPanel_1.AutoX=YtPanel_1.AutoWidth=YtPanel_1.Item=region_lbl;tLabel_8;tLabel_9;tLabel_15;tLabel_17;MONCAT_CODE;MONCAT_CHN_DESC;REGION_CODE;tLabel_1;tLabel_2;tLabel_0;MONCAT_ENG_DESC;PY1;SEQ;PY2;DESCRIPTION;CLNCPATH_CODEtPanel_1.FocusList=REGION_CODE;MONCAT_CODE;SEQ;PY2;MONCAT_CHN_DESC;PY1;MONCAT_ENG_DESC;DESCRIPTION;tPanel_1.MoveType=0CLNCPATH_CODE.Type=适用临床路径下拉区域CLNCPATH_CODE.X=687CLNCPATH_CODE.Y=83CLNCPATH_CODE.Width=81CLNCPATH_CODE.Height=18CLNCPATH_CODE.Text=CLNCPATH_CODE.HorizontalAlignment=2CLNCPATH_CODE.PopupMenuHeader=代码,100;名称,100CLNCPATH_CODE.PopupMenuWidth=300CLNCPATH_CODE.PopupMenuHeight=300CLNCPATH_CODE.FormatType=comboCLNCPATH_CODE.ShowDownButton=YCLNCPATH_CODE.Tip=适用临床路径CLNCPATH_CODE.ShowColumnList=NAMEDESCRIPTION.Type=TTextFieldDESCRIPTION.X=556DESCRIPTION.Y=81DESCRIPTION.Width=217DESCRIPTION.Height=22DESCRIPTION.Text=PY2.Type=TTextFieldPY2.X=696PY2.Y=16PY2.Width=77PY2.Height=22PY2.Text=SEQ.Type=TTextFieldSEQ.X=557SEQ.Y=16SEQ.Width=77SEQ.Height=22SEQ.Text=PY1.Type=TTextFieldPY1.X=556PY1.Y=49PY1.Width=217PY1.Height=22PY1.Text=MONCAT_ENG_DESC.Type=TTextFieldMONCAT_ENG_DESC.X=165MONCAT_ENG_DESC.Y=80MONCAT_ENG_DESC.Width=290MONCAT_ENG_DESC.Height=22MONCAT_ENG_DESC.Text=tLabel_0.Type=TLabeltLabel_0.X=34tLabel_0.Y=84tLabel_0.Width=113tLabel_0.Height=15tLabel_0.Text=变异类别英文说明tLabel_2.Type=TLabeltLabel_2.X=496tLabel_2.Y=85tLabel_2.Width=52tLabel_2.Height=15tLabel_2.Text=备   注tLabel_1.Type=TLabeltLabel_1.X=652tLabel_1.Y=19tLabel_1.Width=37tLabel_1.Height=15tLabel_1.Text=注记REGION_CODE.Type=区域下拉列表REGION_CODE.X=111REGION_CODE.Y=13REGION_CODE.Width=122REGION_CODE.Height=23REGION_CODE.Text=TButtonREGION_CODE.showID=YREGION_CODE.showName=YREGION_CODE.showText=NREGION_CODE.showValue=NREGION_CODE.showPy1=NREGION_CODE.showPy2=NREGION_CODE.Editable=YREGION_CODE.Tip=区域REGION_CODE.TableShowList=nameREGION_CODE.ModuleParmString=REGION_CODE.ModuleParmTag=REGION_CODE.ExpandWidth=80MONCAT_CHN_DESC.Type=TTextFieldMONCAT_CHN_DESC.X=165MONCAT_CHN_DESC.Y=48MONCAT_CHN_DESC.Width=290MONCAT_CHN_DESC.Height=22MONCAT_CHN_DESC.Text=MONCAT_CHN_DESC.PyTag=PY1MONCAT_CODE.Type=TTextFieldMONCAT_CODE.X=379MONCAT_CODE.Y=15MONCAT_CODE.Width=77MONCAT_CODE.Height=22MONCAT_CODE.Text=tLabel_17.Type=TLabeltLabel_17.X=281tLabel_17.Y=19tLabel_17.Width=107tLabel_17.Height=15tLabel_17.Text=变异类别代码tLabel_17.Color=蓝tLabel_15.Type=TLabeltLabel_15.X=462tLabel_15.Y=52tLabel_15.Width=101tLabel_15.Height=15tLabel_15.Text=变异类别拼音tLabel_9.Type=TLabeltLabel_9.X=34tLabel_9.Y=52tLabel_9.Width=133tLabel_9.Height=15tLabel_9.Text=变异类别中文说明tLabel_8.Type=TLabeltLabel_8.X=496tLabel_8.Y=19tLabel_8.Width=53tLabel_8.Height=15tLabel_8.Text=序   号region_lbl.Type=TLabelregion_lbl.X=35region_lbl.Y=18region_lbl.Width=53region_lbl.Height=17region_lbl.Text=区   域