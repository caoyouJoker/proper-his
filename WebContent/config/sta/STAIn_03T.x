# #  Title:STA_IN_03医院住院病患动态及疗效台帐# #  Description:STA_IN_03医院住院病患动态及疗效台帐# #  Copyright: Copyright (c) Javahis 2008# #  author zhangk 2009.6.30#  version 1.0#<Type=TFrame>UI.Title=医院住院病患动态及疗效台帐UI.MenuConfig=%ROOT%\config\sta\STAIn_03TMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.sta.STAIn_03TControlUI.Item=tPanel_0;TableUI.TopMenu=YUI.TopToolBar=YUI.ShowTitle=NUI.ShowMenu=NTable.Type=TTableTable.X=66Table.Y=150Table.Width=81Table.Height=593Table.SpacingRow=1Table.RowHeight=20Table.AutoX=YTable.AutoWidth=YTable.AutoHeight=YTable.Header=tPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=5tPanel_0.Width=1014tPanel_0.Height=140tPanel_0.Border=凸tPanel_0.AutoX=YtPanel_0.AutoY=YtPanel_0.AutoWidth=YtPanel_0.Item=tPanel_1;tPanel_2tPanel_2.Type=TPaneltPanel_2.X=510tPanel_2.Y=7tPanel_2.Width=497tPanel_2.Height=126tPanel_2.Border=组|趋势tPanel_2.AutoY=YtPanel_2.AutoWidth=YtPanel_2.AutoHeight=YtPanel_2.Item=Check2;QS_1;QS_2;QY1;Q_Month1;tLabel_1;QY2;tLabel_2;tLabel_3;Q_Month2;STA_DEPT2STA_DEPT2.Type=TTextFormatSTA_DEPT2.X=144STA_DEPT2.Y=86STA_DEPT2.Width=120STA_DEPT2.Height=20STA_DEPT2.Text=STA_DEPT2.showDownButton=YSTA_DEPT2.FormatType=comboSTA_DEPT2.PopupMenuWidth=300STA_DEPT2.PopupMenuHeight=300STA_DEPT2.HisOneNullRow=YSTA_DEPT2.PopupMenuHeader=部门编号,100;部门名称,200;部门等级,80STA_DEPT2.PopupMenuSQL=SELECT DISTINCT DEPT_CODE, DEPT_DESC, DEPT_LEVEL, OE_DEPT_CODE, IPD_DEPT_CODE, PY1 FROM STA_OEI_DEPT_LISTSTA_DEPT2.ShowColumnList=DEPT_DESCSTA_DEPT2.PopupMenuFilter=PY1,1;DEPT_CODE,1STA_DEPT2.ValueColumn=DEPT_CODESTA_DEPT2.DynamicDownload=YSTA_DEPT2.HorizontalAlignment=2Q_Month2.Type=TTextFormatQ_Month2.X=260Q_Month2.Y=53Q_Month2.Width=77Q_Month2.Height=20Q_Month2.Text=TTextFormatQ_Month2.FormatType=dateQ_Month2.Format=yyyy/MMQ_Month2.HorizontalAlignment=4tLabel_3.Type=TLabeltLabel_3.X=230tLabel_3.Y=56tLabel_3.Width=19tLabel_3.Height=15tLabel_3.Text=至tLabel_2.Type=TLabeltLabel_2.X=230tLabel_2.Y=25tLabel_2.Width=21tLabel_2.Height=15tLabel_2.Text=至QY2.Type=TTextFormatQY2.X=260QY2.Y=22QY2.Width=77QY2.Height=20QY2.Text=TTextFormatQY2.FormatType=dateQY2.Format=yyyyQY2.HorizontalAlignment=4tLabel_1.Type=TLabeltLabel_1.X=85tLabel_1.Y=89tLabel_1.Width=46tLabel_1.Height=15tLabel_1.Text=科  室Q_Month1.Type=TTextFormatQ_Month1.X=144Q_Month1.Y=53Q_Month1.Width=77Q_Month1.Height=20Q_Month1.Text=TTextFormatQ_Month1.FormatType=dateQ_Month1.Format=yyyy/MMQ_Month1.HorizontalAlignment=4QY1.Type=TTextFormatQY1.X=144QY1.Y=22QY1.Width=77QY1.Height=20QY1.Text=TTextFormatQY1.Format=yyyyQY1.FormatType=dateQY1.HorizontalAlignment=4QS_2.Type=TRadioButtonQS_2.X=92QS_2.Y=52QS_2.Width=41QS_2.Height=23QS_2.Text=月QS_2.Group=QuShiQS_1.Type=TRadioButtonQS_1.X=92QS_1.Y=21QS_1.Width=41QS_1.Height=23QS_1.Text=年QS_1.Group=QuShiQS_1.Enabled=YQS_1.Selected=YCheck2.Type=TCheckBoxCheck2.X=19Check2.Y=21Check2.Width=54Check2.Height=23Check2.Text=选择Check2.Action=check2SelectedtPanel_1.Type=TPaneltPanel_1.X=7tPanel_1.Y=7tPanel_1.Width=500tPanel_1.Height=126tPanel_1.Border=组|本期tPanel_1.AutoY=YtPanel_1.AutoX=YtPanel_1.AutoHeight=YtPanel_1.Item=Check1;B_Radio1;B_Radio2;B_MONTH;B_YEAR;tLabel_0;STA_DEPT1STA_DEPT1.Type=TTextFormatSTA_DEPT1.X=130STA_DEPT1.Y=87STA_DEPT1.Width=120STA_DEPT1.Height=20STA_DEPT1.Text=STA_DEPT1.PopupMenuHeader=部门编号,100;部门名称,200;部门等级,80STA_DEPT1.FormatType=comboSTA_DEPT1.showDownButton=YSTA_DEPT1.PopupMenuWidth=300STA_DEPT1.PopupMenuHeight=300STA_DEPT1.PopupMenuSQL=SELECT DISTINCT DEPT_CODE, DEPT_DESC, DEPT_LEVEL, OE_DEPT_CODE, IPD_DEPT_CODE, PY1 FROM STA_OEI_DEPT_LISTSTA_DEPT1.PopupMenuFilter=PY1,1;DEPT_CODE,1STA_DEPT1.ShowColumnList=DEPT_DESCSTA_DEPT1.ValueColumn=DEPT_CODESTA_DEPT1.DynamicDownload=YSTA_DEPT1.HorizontalAlignment=2STA_DEPT1.HisOneNullRow=YtLabel_0.Type=TLabeltLabel_0.X=69tLabel_0.Y=90tLabel_0.Width=48tLabel_0.Height=15tLabel_0.Text=科  室B_YEAR.Type=TTextFormatB_YEAR.X=130B_YEAR.Y=21B_YEAR.Width=77B_YEAR.Height=20B_YEAR.Text=TTextFormatB_YEAR.FormatType=dateB_YEAR.Format=yyyyB_YEAR.HorizontalAlignment=4B_MONTH.Type=TTextFormatB_MONTH.X=130B_MONTH.Y=52B_MONTH.Width=77B_MONTH.Height=20B_MONTH.Text=B_MONTH.FormatType=dateB_MONTH.Format=yyyy/MMB_MONTH.HorizontalAlignment=4B_Radio2.Type=TRadioButtonB_Radio2.X=76B_Radio2.Y=52B_Radio2.Width=44B_Radio2.Height=23B_Radio2.Text=月B_Radio2.Group=BenQiB_Radio1.Type=TRadioButtonB_Radio1.X=76B_Radio1.Y=21B_Radio1.Width=40B_Radio1.Height=23B_Radio1.Text=年B_Radio1.Group=BenQiB_Radio1.Selected=YCheck1.Type=TCheckBoxCheck1.X=15Check1.Y=22Check1.Width=53Check1.Height=23Check1.Text=选择Check1.Action=check1SelectedCheck1.Selected=Y