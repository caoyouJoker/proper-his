## TBuilder Config File ## Title:手术室IP对照设置## Company: ProperSoft## Author:wanglong 2014.07.07## version 1.0#<Type=TFrame>UI.Title=手术室IP对照设置UI.MenuConfig=%ROOT%\config\ope\OPEIPtoRoomConfigMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.ope.OPEIPtoRoomConfigControlUI.item=tLabel_0;IP;tLabel_1;ROOM_NO;tPanel_0UI.layout=nullUI.TopMenu=YUI.TopToolBar=YtPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=61tPanel_0.Width=1014tPanel_0.Height=587tPanel_0.Border=组|已设置手术室tPanel_0.AutoWidth=YtPanel_0.AutoHeight=YtPanel_0.Item=TABLEtPanel_0.AutoX=YTABLE.Type=TTableTABLE.X=14TABLE.Y=24TABLE.Width=989TABLE.Height=644TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoWidth=YTABLE.AutoHeight=YTABLE.Header=终端机IP,180;手术室,100,ROOM_NO;操作人员,120,OPT_USER;操作日期,140;操作IP,120TABLE.Item=ROOM_NO;OPT_USERTABLE.ParmMap=IP;ROOM_NO;OPT_USER;OPT_DATE;OPT_TERMTABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,left;4,leftTABLE.LockColumns=AllOPT_USER.Type=人员下拉列表OPT_USER.X=262OPT_USER.Y=68OPT_USER.Width=81OPT_USER.Height=23OPT_USER.Text=TButtonOPT_USER.showID=YOPT_USER.showName=YOPT_USER.showText=NOPT_USER.showValue=NOPT_USER.showPy1=YOPT_USER.showPy2=YOPT_USER.Editable=YOPT_USER.Tip=人员OPT_USER.TableShowList=nameOPT_USER.ModuleParmString=OPT_USER.ModuleParmTag=ROOM_NO.Type=手术室列表ROOM_NO.X=361ROOM_NO.Y=22ROOM_NO.Width=143ROOM_NO.Height=23ROOM_NO.Text=TButtonROOM_NO.showID=YROOM_NO.showName=YROOM_NO.showText=NROOM_NO.showValue=NROOM_NO.showPy1=YROOM_NO.showPy2=YROOM_NO.Editable=YROOM_NO.Tip=手术室ROOM_NO.TableShowList=nameROOM_NO.ModuleParmString=GROUP_ID:OPE_OPROOMROOM_NO.ModuleParmTag=tLabel_1.Type=TLabeltLabel_1.X=296tLabel_1.Y=26tLabel_1.Width=71tLabel_1.Height=15tLabel_1.Text=手术间：IP.Type=TTextFieldIP.X=101IP.Y=22IP.Width=141IP.Height=23IP.Text=tLabel_0.Type=TLabeltLabel_0.X=24tLabel_0.Y=26tLabel_0.Width=72tLabel_0.Height=15tLabel_0.Text=终端机IP:tLabel_0.Color=蓝