# #  Title: STA_IN_08手术医师工作量# #  Description: STA_IN_08手术医师工作量# #  Copyright: Copyright (c) Javahis 2008# #  author zhangk 2009.6.3#  version 1.0#<Type=TFrame>UI.Title=手术医师工作量UI.MenuConfig=%ROOT%\config\sta\STAIn_08Menu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.sta.STAIn_08ControlUI.Item=tPanel_0;TableUI.TopMenu=YUI.TopToolBar=YUI.ShowTitle=NUI.ShowMenu=NTable.Type=TTableTable.X=112Table.Y=90Table.Width=81Table.Height=653Table.SpacingRow=1Table.RowHeight=20Table.AutoX=YTable.AutoWidth=YTable.AutoHeight=YTable.Header=手术医师,80;术者例数,80;一助例数,80;I类切口甲级愈合例数,130;I类切口甲级愈合率,125;I类切口感染例数,120;I类切口感染率,110;平均住院天数,100;术前平均住院天数,120;术后平均住院天数,120;术后10日死亡例数,120;平均总费用,100;平均住院费,100;平均药费,80;平均手术费,100;平均检查治疗费,110Table.ParmMap=DR_NAME;DATA_01;DATA_02;DATA_03;DATA_04;DATA_05;DATA_06;DATA_07;DATA_08;DATA_09;DATA_10;DATA_11;DATA_12;DATA_13;DATA_14;DATA_15Table.ColumnHorizontalAlignmentData=0,left;1,right;2,right;3,right;4,right;5,right;6,right;7,right;8,right;9,right;10,right;11,right;12,right;13,right;14,right;15,rightTable.LockColumns=alltPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=5tPanel_0.Width=1014tPanel_0.Height=80tPanel_0.Border=凸tPanel_0.AutoX=YtPanel_0.AutoY=YtPanel_0.AutoWidth=YtPanel_0.Item=tLabel_0;DATE_S;DATE_E;tLabel_1;tLabel_2;tLabel_3;Dept_Combo;DR_ComboDR_Combo.Type=人员DR_Combo.X=699DR_Combo.Y=28DR_Combo.Width=120DR_Combo.Height=20DR_Combo.Text=DR_Combo.HorizontalAlignment=2DR_Combo.PopupMenuHeader=代码,100;名称,100DR_Combo.PopupMenuWidth=300DR_Combo.PopupMenuHeight=300DR_Combo.PopupMenuFilter=ID,1;NAME,1;PY1,1DR_Combo.FormatType=comboDR_Combo.ShowDownButton=YDR_Combo.Tip=人员DR_Combo.ShowColumnList=NAMEDR_Combo.HisOneNullRow=YDR_Combo.PosType=1DR_Combo.Dept=<Dept_Combo>Dept_Combo.Type=科室Dept_Combo.X=485Dept_Combo.Y=27Dept_Combo.Width=120Dept_Combo.Height=20Dept_Combo.Text=Dept_Combo.HorizontalAlignment=2Dept_Combo.PopupMenuHeader=代码,100;名称,100Dept_Combo.PopupMenuWidth=300Dept_Combo.PopupMenuHeight=300Dept_Combo.FormatType=comboDept_Combo.ShowDownButton=YDept_Combo.Tip=科室Dept_Combo.ShowColumnList=NAMEDept_Combo.Action=DR_Combo|onQuery;onDeptChangeDept_Combo.HisOneNullRow=YDept_Combo.DeptGrade=Dept_Combo.FinalFlg=YDept_Combo.DeptCat1=Dept_Combo.ClassIfy=0Dept_Combo.IpdFitFlg=YtLabel_3.Type=TLabeltLabel_3.X=421tLabel_3.Y=31tLabel_3.Width=61tLabel_3.Height=15tLabel_3.Text=统计科室tLabel_3.Color=蓝tLabel_2.Type=TLabeltLabel_2.X=632tLabel_2.Y=31tLabel_2.Width=65tLabel_2.Height=15tLabel_2.Text=医    师tLabel_2.Color=蓝tLabel_1.Type=TLabeltLabel_1.X=248tLabel_1.Y=29tLabel_1.Width=20tLabel_1.Height=15tLabel_1.Text=至DATE_E.Type=TTextFormatDATE_E.X=272DATE_E.Y=26DATE_E.Width=120DATE_E.Height=20DATE_E.Text=TTextFormatDATE_E.FormatType=dateDATE_E.Format=yyyy/MM/ddDATE_E.showDownButton=YDATE_E.HorizontalAlignment=4DATE_S.Type=TTextFormatDATE_S.X=118DATE_S.Y=26DATE_S.Width=120DATE_S.Height=20DATE_S.Text=TTextFormatDATE_S.FormatType=dateDATE_S.Format=yyyy/MM/ddDATE_S.showDownButton=YDATE_S.HorizontalAlignment=4tLabel_0.Type=TLabeltLabel_0.X=46tLabel_0.Y=30tLabel_0.Width=66tLabel_0.Height=15tLabel_0.Text=统计日期tLabel_0.Color=蓝