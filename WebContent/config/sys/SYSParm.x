# #  Title:健检参数# #  Description:健检参数# #  Copyright: Copyright (c) BlueCore 2014# #  author wangl 2014.02.14#  version 1.0#<Type=TFrame>UI.Title=其他报告医嘱设定UI.MenuConfig=%ROOT%\config\sys\SYSParmMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.sys.SYSParmControlUI.item=tPanel_0UI.layout=nullUI.TopMenu=YUI.TopToolBar=YtPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=5tPanel_0.Width=1014tPanel_0.Height=738tPanel_0.Item=tPanel_1tPanel_0.Enabled=YtPanel_0.Border=凸tPanel_0.AutoX=YtPanel_0.AutoWidth=YtPanel_0.AutoHeight=YtPanel_0.AutoSize=5tPanel_1.Type=TPaneltPanel_1.X=7tPanel_1.Y=7tPanel_1.Width=1000tPanel_1.Height=724tPanel_1.Item=RIS_PRINT_N;RIS_PRINT_Y;tPanel_3tPanel_1.Border=组|tPanel_1.AutoHeight=YtPanel_1.AutoWidth=YtPanel_1.AutoX=YtPanel_1.AutoY=YRIS_PRINT_N.Type=TRadioButtonRIS_PRINT_N.X=27RIS_PRINT_N.Y=185RIS_PRINT_N.Width=81RIS_PRINT_N.Height=23RIS_PRINT_N.Text=不打印RIS_PRINT_N.Group=1RIS_PRINT_N.Selected=YRIS_PRINT_N.Visible=NRIS_PRINT_Y.Type=TRadioButtonRIS_PRINT_Y.X=27RIS_PRINT_Y.Y=216RIS_PRINT_Y.Width=81RIS_PRINT_Y.Height=23RIS_PRINT_Y.Text=全部打印RIS_PRINT_Y.Group=1RIS_PRINT_Y.Enabled=YRIS_PRINT_Y.Visible=NtPanel_3.Type=TPaneltPanel_3.X=9tPanel_3.Y=8tPanel_3.Width=980tPanel_3.Height=705tPanel_3.Border=组|添加医嘱tPanel_3.AutoX=NtPanel_3.Item=tLabel_0;ORDER_CODE;ORDER_DESC;ADD_ORDER;DEL_ORDER;ORDER_TABLEtPanel_3.AutoWidth=YtPanel_3.AutoHeight=YtLabel_0.Type=TLabeltLabel_0.X=13tLabel_0.Y=24tLabel_0.Width=72tLabel_0.Height=15tLabel_0.Text=医嘱名称ORDER_CODE.Type=TTextFieldORDER_CODE.X=76ORDER_CODE.Y=21ORDER_CODE.Width=77ORDER_CODE.Height=20ORDER_CODE.Text=ORDER_DESC.Type=TTextFieldORDER_DESC.X=156ORDER_DESC.Y=21ORDER_DESC.Width=222ORDER_DESC.Height=20ORDER_DESC.Text=ADD_ORDER.Type=TButtonADD_ORDER.X=297ADD_ORDER.Y=49ADD_ORDER.Width=85ADD_ORDER.Height=23ADD_ORDER.Text=<--增加ADD_ORDER.Action=onAddOrderDEL_ORDER.Type=TButtonDEL_ORDER.X=297DEL_ORDER.Y=80DEL_ORDER.Width=85DEL_ORDER.Height=23DEL_ORDER.Text=-->删除DEL_ORDER.Action=onDelOrderORDER_TABLE.Type=TTableORDER_TABLE.X=9ORDER_TABLE.Y=48ORDER_TABLE.Width=280ORDER_TABLE.Height=646ORDER_TABLE.SpacingRow=1ORDER_TABLE.RowHeight=20ORDER_TABLE.AutoX=YORDER_TABLE.AutoY=NORDER_TABLE.AutoHeight=YORDER_TABLE.Header=医嘱代码,80;医嘱名称,190ORDER_TABLE.Item=ORDER_TABLE.LockColumns=allORDER_TABLE.ColumnHorizontalAlignmentData=1,leftORDER_TABLE.AutoWidth=NORDER_TABLE.ParmMap=ORDER_CODE;ORDER_DESC