## TBuilder Config File ## Title:批次处理程序维护## Company:JavaHis## Author:wangzhilei 2012.08.01## version 1.0#<Type=TFrame>UI.Title=批次执行删除UI.MenuConfig=%ROOT%\config\sys\SYSPatchUpMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.sys.SYSPatchUpControlUI.item=tPanel_1;TABLE_INFOUI.layout=nullUI.ShowTitle=NUI.ShowMenu=NUI.TopMenu=YUI.LoadFlg=NUI.TopToolBar=YTABLE_INFO.Type=TTableTABLE_INFO.X=5TABLE_INFO.Y=106TABLE_INFO.Width=664TABLE_INFO.Height=637TABLE_INFO.SpacingRow=1TABLE_INFO.RowHeight=20TABLE_INFO.AutoY=NTABLE_INFO.AutoWidth=YTABLE_INFO.AutoHeight=YTABLE_INFO.Header=选,25,boolean;批次名称,170;启动时间,150;结束时间,150;批次重送编号,100;运行状态,80;执行信息,200;服务器IP,120TABLE_INFO.AutoX=YTABLE_INFO.ParmMap=FLG;PATCH_DESC;PATCH_START_DATE;PATCH_END_DATE;PATCH_REOMIT_INDEX;PATCH_STATUS;PATCH_MESSAGE;SERVER_IP;PATCH_CODE;START_DATETABLE_INFO.LockColumns=1,2,3,4,5,6TABLE_INFO.ColumnHorizontalAlignmentData=1,left;2,left;3,left;4,right;5,left;6,left,7,lefttPanel_1.Type=TPaneltPanel_1.X=7tPanel_1.Y=5tPanel_1.Width=1034tPanel_1.Height=93tPanel_1.Border=组|批次处理程序tPanel_1.AutoX=YtPanel_1.AutoWidth=YtPanel_1.Item=TLabel;S_DATE;PATCH_A;PATCH_B;TLabel1;SELECT_ALL;PATCH_DESC;tl;E_DATEE_DATE.Type=TTextFormatE_DATE.X=519E_DATE.Y=40E_DATE.Width=160E_DATE.Height=20E_DATE.Text=E_DATE.FormatType=dateE_DATE.Format=yyyy/MM/dd HH:mm:ssE_DATE.showDownButton=Ytl.Type=TLabeltl.X=492tl.Y=44tl.Width=21tl.Height=15tl.Text=至PATCH_DESC.Type=TComboBoxPATCH_DESC.X=761PATCH_DESC.Y=39PATCH_DESC.Width=222PATCH_DESC.Height=23PATCH_DESC.Text=TButtonPATCH_DESC.showID=YPATCH_DESC.Editable=YPATCH_DESC.StringData=[[id,text],[],[101210000000,调价计划101210000000],[101213000000,调价计划101213000000],[120703000000,药库日结批次],[120329000000,固定费用批次过账],[120329000001,长期医嘱批次作业]]PATCH_DESC.CanEdit=YPATCH_DESC.ShowValue=YPATCH_DESC.TableShowList=textSELECT_ALL.Type=TCheckBoxSELECT_ALL.X=8SELECT_ALL.Y=39SELECT_ALL.Width=58SELECT_ALL.Height=23SELECT_ALL.Text=全选SELECT_ALL.Action=onSelectAllSELECT_ALL.Selected=YTLabel1.Type=TLabelTLabel1.X=687TLabel1.Y=44TLabel1.Width=70TLabel1.Height=15TLabel1.Text=批次名称：TLabel1.Color=蓝PATCH_B.Type=TRadioButtonPATCH_B.X=155PATCH_B.Y=40PATCH_B.Width=81PATCH_B.Height=23PATCH_B.Text=调价批次PATCH_B.Group=PATCH_B.Color=蓝PATCH_A.Type=TRadioButtonPATCH_A.X=71PATCH_A.Y=39PATCH_A.Width=81PATCH_A.Height=23PATCH_A.Text=业务批次PATCH_A.Group=PATCH_A.Selected=YPATCH_A.Color=蓝S_DATE.Type=TTextFormatS_DATE.X=318S_DATE.Y=40S_DATE.Width=160S_DATE.Height=20S_DATE.Text=TTextFormatS_DATE.FormatType=dateS_DATE.Format=yyyy/MM/dd HH:mm:ssS_DATE.showDownButton=YTLabel.Type=TLabelTLabel.X=246TLabel.Y=44TLabel.Width=72TLabel.Height=15TLabel.Text=启动时间：TLabel.Color=蓝