## TBuilder Config File ## Title:## Company:JavaHis## Author:cwl 2012.07.03## version 1.0#<Type=TFrame>UI.Title=门诊收入统计UI.MenuConfig=%ROOT%\config\customquery\OPBReceiptOfOutPatientMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.opb.OPBReceiptOfOutPatientControlUI.item=tPanel_2;tPanel_3UI.layout=nullUI.TopMenu=YUI.TopToolBar=YtPanel_3.Type=TPaneltPanel_3.X=5tPanel_3.Y=93tPanel_3.Width=1014tPanel_3.Height=650tPanel_3.Border=组|tPanel_3.AutoX=YtPanel_3.AutoWidth=YtPanel_3.Item=TabletPanel_3.AutoHeight=YTable.Type=TTableTable.X=5Table.Y=12Table.Width=992Table.Height=623Table.SpacingRow=1Table.RowHeight=20Table.Header=挂号费,80;诊查费,80;合计,80;挂号费,80;诊查费,80;抗生素,80;非抗生素,80;中成药,80;中草药,80;检查费,80;治疗费,80;放射费,80;手术费,80;输血费,80;化验费,80;体检费,80;社区医疗,80;观察床费,80;CT,80;MR,80;自费部分,80;材料费,80;输氧费,80Table.AutoX=YTable.AutoWidth=YTable.AutoHeight=YTable.ParmMap=挂号费;诊查费;合计;挂号费;诊查费;抗生素;非抗生素;中成药;中草药;检查费;治疗费;放射费;手术费;输血费;化验费;体检费;社区医疗;观察床费;CT;MR;自费部分;材料费;输氧费Table.LockColumns=allTable.ColumnHorizontalAlignmentData=0,right;1,right;2,right;3,right;4,right;5,right;6,right;7,right;8,right;9,right;10,right;11,right;12,ri ght;13,right;14,right;15,right;16,right;17,right;18,right;19,right;20,right;21,right;22,righttPanel_2.Type=TPaneltPanel_2.X=5tPanel_2.Y=7tPanel_2.Width=1014tPanel_2.Height=81tPanel_2.Border=组|查询条件tPanel_2.Item=tLabel_7;S_DATE;tLabel_8;E_DATE;REALDEPT_CODE;tLabel_10tPanel_2.AutoX=YtPanel_2.AutoWidth=YtLabel_10.Type=TLabeltLabel_10.X=554tLabel_10.Y=33tLabel_10.Width=43tLabel_10.Height=15tLabel_10.Text=门诊：REALDEPT_CODE.Type=TComboBoxREALDEPT_CODE.X=596REALDEPT_CODE.Y=30REALDEPT_CODE.Width=93REALDEPT_CODE.Height=23REALDEPT_CODE.Text=TButtonREALDEPT_CODE.showID=NREALDEPT_CODE.Editable=YREALDEPT_CODE.StringData=[[id,text],[,],[1,市内门诊],[2,院内门诊],[3,院内急诊]]E_DATE.Type=TTextFormatE_DATE.X=348E_DATE.Y=32E_DATE.Width=158E_DATE.Height=20E_DATE.Text=TTextFormatE_DATE.FormatType=dateE_DATE.showDownButton=YE_DATE.Format=yyyy/MM/dd HH:mm:sstLabel_8.Type=TLabeltLabel_8.X=276tLabel_8.Y=33tLabel_8.Width=72tLabel_8.Height=15tLabel_8.Text=结束时间：S_DATE.Type=TTextFormatS_DATE.X=91S_DATE.Y=32S_DATE.Width=158S_DATE.Height=20S_DATE.Text=TTextFormatS_DATE.FormatType=dateS_DATE.Format=yyyy/MM/dd HH:mm:ssS_DATE.showDownButton=YtLabel_7.Type=TLabeltLabel_7.X=19tLabel_7.Y=36tLabel_7.Width=72tLabel_7.Height=15tLabel_7.Text=开始时间：