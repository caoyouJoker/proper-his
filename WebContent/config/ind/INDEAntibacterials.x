############################################### <p>Title:门（急）诊明细、汇总报表 </p>## <p>Description:门（急）诊明细、汇总报表 </p>## <p>Copyright: Copyright (c) 2013</p>## <p>Company:Javahis </p>## @author wangm  2013-3-15# @version 1.0##############################################<Type=TFrame>UI.Title=院内急诊抗菌药物明细统计UI.MenuConfig=%ROOT%\config\ind\INDEAntibacterials_Menu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.ind.INDEAntibacterialsControlUI.item=tPanel_0;tab_StatisticsUI.layout=nullUI.FocusList=UI.X=0UI.Y=0UI.TopMenu=YUI.TopToolBar=YUI.ShowTitle=NUI.AutoX=NUI.AutoY=NUI.AutoWidth=NUI.AutoHeight=NUI.AutoSize=0UI.Border=tab_Statistics.Type=TTabletab_Statistics.X=4tab_Statistics.Y=49tab_Statistics.Width=1015tab_Statistics.Height=694tab_Statistics.SpacingRow=1tab_Statistics.RowHeight=20tab_Statistics.LockColumns=alltab_Statistics.AutoX=Ytab_Statistics.AutoWidth=Ytab_Statistics.AutoHeight=YTAB_STA.Type=TTableTAB_STA.X=4TAB_STA.Y=88TAB_STA.Width=90TAB_STA.Height=81TAB_STA.SpacingRow=1TAB_STA.RowHeight=20TAB_STA.AutoX=YTAB_STA.AutoWidth=YTAB_STA.AutoHeight=YTAB_STA.LockColumns=alltPanel_0.Type=TPaneltPanel_0.X=7tPanel_0.Y=7tPanel_0.Width=1010tPanel_0.Height=41tPanel_0.Border=组|查询条件tPanel_0.AutoX=YtPanel_0.AutoY=YtPanel_0.AutoWidth=YtPanel_0.Item=tLabel_0;txt_StartDate;tLabel_1;txt_EndDate;txt_StartTime;txt_EndTimetxt_EndTime.Type=TTextFormattxt_EndTime.X=480txt_EndTime.Y=12txt_EndTime.Width=77txt_EndTime.Height=22txt_EndTime.Text=txt_EndTime.FormatType=datetxt_EndTime.Format=HH:mm:sstxt_StartTime.Type=TTextFormattxt_StartTime.X=244txt_StartTime.Y=12txt_StartTime.Width=80txt_StartTime.Height=22txt_StartTime.Text=txt_StartTime.FormatType=datetxt_StartTime.Format=HH:mm:sstxt_StartTime.showDownButton=Ntxt_EndDate.Type=TTextFormattxt_EndDate.X=355txt_EndDate.Y=12txt_EndDate.Width=120txt_EndDate.Height=22txt_EndDate.Text=TTextFormattxt_EndDate.FormatType=datetxt_EndDate.Format=yyyy/MM/ddtxt_EndDate.showDownButton=Ytxt_EndDate.HorizontalAlignment=2tLabel_1.Type=TLabeltLabel_1.X=334tLabel_1.Y=16tLabel_1.Width=20tLabel_1.Height=15tLabel_1.Text=至txt_StartDate.Type=TTextFormattxt_StartDate.X=119txt_StartDate.Y=12txt_StartDate.Width=120txt_StartDate.Height=22txt_StartDate.Text=TTextFormattxt_StartDate.FormatType=datetxt_StartDate.Format=yyyy/MM/ddtxt_StartDate.showDownButton=Ytxt_StartDate.HorizontalAlignment=2tLabel_0.Type=TLabeltLabel_0.X=44tLabel_0.Y=17tLabel_0.Width=71tLabel_0.Height=15tLabel_0.Text=起讫日期：tLabel_0.Color=蓝