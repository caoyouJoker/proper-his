## TBuilder Config File ## Title:## Company:JavaHis## Author:sdr 2013.04.22## version 1.0#<Type=TFrame>UI.Title=返洗率登记UI.MenuConfig=%ROOT%\config\inv\INVBackwashingCountMenu.xUI.Width=1104UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.inv.INVBackwashingCountControlUI.item=tPanel_0;tableBS;USERUI.layout=nullUI.ShowMenu=NUI.TopToolBar=YUI.TopMenu=YUI.Name=返洗率登记UI.Text=返洗率登记USER.Type=人员USER.X=23USER.Y=152USER.Width=81USER.Height=23USER.Text=USER.HorizontalAlignment=2USER.PopupMenuHeader=代码,100;名称,100USER.PopupMenuWidth=300USER.PopupMenuHeight=300USER.PopupMenuFilter=ID,1;NAME,1;PY1,1USER.FormatType=comboUSER.ShowDownButton=YUSER.Tip=人员USER.ShowColumnList=NAMEtableBS.Type=TTabletableBS.X=7tableBS.Y=76tableBS.Width=1092tableBS.Height=667tableBS.SpacingRow=1tableBS.RowHeight=20tableBS.Item=USERtableBS.Header=手术包名称,200;手术包代码,100;反洗次数,80tableBS.ParmMap=PACK_DESC;PACK_CODE;PCOUNTtableBS.ColumnHorizontalAlignmentData=0,left;1,left;2,lefttableBS.AutoWidth=YtableBS.AutoHeight=YtableBS.AutoY=NtableBS.ClickedAction=onTableClickedtableBS.LockColumns=ALLtPanel_0.Type=TPaneltPanel_0.X=7tPanel_0.Y=4tPanel_0.Width=1092tPanel_0.Height=69tPanel_0.Border=组tPanel_0.Item=tLabel_1;tLabel_8;BACKWASHING_DATE_START;PACK_CODE;tLabel_5;PACK_DESC;tLabel_0;BACKWASHING_DATE_ENDtPanel_0.AutoX=YtPanel_0.AutoWidth=YtPanel_0.TopToolBar=NtPanel_0.ShowTitle=NBACKWASHING_DATE_END.Type=TTextFormatBACKWASHING_DATE_END.X=245BACKWASHING_DATE_END.Y=38BACKWASHING_DATE_END.Width=138BACKWASHING_DATE_END.Height=20BACKWASHING_DATE_END.Text=BACKWASHING_DATE_END.Format=yyyy/MM/dd HH:mm:ssBACKWASHING_DATE_END.showDownButton=YBACKWASHING_DATE_END.FormatType=datetLabel_0.Type=TLabeltLabel_0.X=231tLabel_0.Y=40tLabel_0.Width=15tLabel_0.Height=15tLabel_0.Text=-PACK_DESC.Type=TTextFieldPACK_DESC.X=313PACK_DESC.Y=9PACK_DESC.Width=122PACK_DESC.Height=20PACK_DESC.Text=PACK_DESC.Enabled=NtLabel_5.Type=TLabeltLabel_5.X=232tLabel_5.Y=11tLabel_5.Width=92tLabel_5.Height=15tLabel_5.Text=手术包名称：PACK_CODE.Type=TTextFieldPACK_CODE.X=84PACK_CODE.Y=9PACK_CODE.Width=122PACK_CODE.Height=20PACK_CODE.Text=BACKWASHING_DATE_START.Type=TTextFormatBACKWASHING_DATE_START.X=84BACKWASHING_DATE_START.Y=38BACKWASHING_DATE_START.Width=138BACKWASHING_DATE_START.Height=20BACKWASHING_DATE_START.Text=BACKWASHING_DATE_START.showDownButton=YBACKWASHING_DATE_START.FormatType=dateBACKWASHING_DATE_START.Format=yyyy/MM/dd HH:mm:sstLabel_8.Type=TLabeltLabel_8.X=18tLabel_8.Y=40tLabel_8.Width=72tLabel_8.Height=15tLabel_8.Text=返洗日期:tLabel_1.Type=TLabeltLabel_1.X=5tLabel_1.Y=11tLabel_1.Width=84tLabel_1.Height=15tLabel_1.Text=手术包代码:tLabel_1.Color=black