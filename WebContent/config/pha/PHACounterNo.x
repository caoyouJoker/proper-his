## TBuilder Config File ## Title:## Company:JavaHis## Author:ehui 2010.03.15## version 1.0#<Type=TFrame>UI.Title=领药号设置UI.MenuConfig=%ROOT%\config\pha\PHACounterNoMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.pha.PhaCounterNoControlUI.item=tPanel_0UI.layout=nullUI.zhTitle=领药号设置UI.enTitle=Set Counter NoUI.TopMenu=YUI.TopToolBar=YUI.ShowMenu=YtPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=5tPanel_0.Width=1014tPanel_0.Height=738tPanel_0.AutoX=YtPanel_0.AutoY=YtPanel_0.AutoWidth=YtPanel_0.AutoHeight=YtPanel_0.Title=tPanel_0.zhTitle=领药号设置tPanel_0.enTitle=tPanel_0.MenuConfig=tPanel_0.TopMenu=YtPanel_0.TopToolBar=YtPanel_0.ShowMenu=YtPanel_0.Border=组tPanel_0.Item=tLabel_0;ORG_CODE;TABLE;ATC_TYPEATC_TYPE.Type=TComboBoxATC_TYPE.X=282ATC_TYPE.Y=67ATC_TYPE.Width=81ATC_TYPE.Height=23ATC_TYPE.Text=TButtonATC_TYPE.showID=YATC_TYPE.Editable=YATC_TYPE.StringData=[[ID,TEXT],[,],[1,文件交换],[2,数据插入]]ATC_TYPE.TableShowList=textDISPENSE_OPERATOR.Type=人员DISPENSE_OPERATOR.X=523DISPENSE_OPERATOR.Y=9DISPENSE_OPERATOR.Width=81DISPENSE_OPERATOR.Height=23DISPENSE_OPERATOR.Text=DISPENSE_OPERATOR.HorizontalAlignment=2DISPENSE_OPERATOR.PopupMenuHeader=代码,100;名称,100DISPENSE_OPERATOR.PopupMenuWidth=300DISPENSE_OPERATOR.PopupMenuHeight=300DISPENSE_OPERATOR.PopupMenuFilter=ID,1;NAME,1;PY1,1DISPENSE_OPERATOR.FormatType=comboDISPENSE_OPERATOR.ShowDownButton=YDISPENSE_OPERATOR.Tip=人员DISPENSE_OPERATOR.ShowColumnList=NAMEDISPENSE_OPERATOR.PosType=2DOSAGE_OPERATOR.Type=人员DOSAGE_OPERATOR.X=430DOSAGE_OPERATOR.Y=10DOSAGE_OPERATOR.Width=81DOSAGE_OPERATOR.Height=23DOSAGE_OPERATOR.Text=DOSAGE_OPERATOR.HorizontalAlignment=2DOSAGE_OPERATOR.PopupMenuHeader=代码,100;名称,100DOSAGE_OPERATOR.PopupMenuWidth=300DOSAGE_OPERATOR.PopupMenuHeight=300DOSAGE_OPERATOR.PopupMenuFilter=ID,1;NAME,1;PY1,1DOSAGE_OPERATOR.FormatType=comboDOSAGE_OPERATOR.ShowDownButton=YDOSAGE_OPERATOR.Tip=人员DOSAGE_OPERATOR.ShowColumnList=NAMEDOSAGE_OPERATOR.PosType=2TABLE.Type=TTableTABLE.X=8TABLE.Y=41TABLE.Width=81TABLE.Height=81TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoWidth=YTABLE.AutoHeight=YTABLE.Header=药房,120,ORG_CODE;窗口号,80;中文名,100;英文名,150;简拼,80;启,30,boolean;普,30,boolean;管,30,boolean;配药药师,120,DOSAGE_OPERATOR;发药药师,120,DISPENSE_OPERATOR;所在IP,120;包药机台号,120;包药机类型,120,ATC_TYPETABLE.Item=ORG_CODE;DOSAGE_OPERATOR;DISPENSE_OPERATOR;ATC_TYPETABLE.ParmMap=ORG_CODE;COUNTER_NO;COUNTER_DESC;COUNTER_ENG_DESC;PY1;CHOSEN_FLG;COMMON_FLG;CTRL_FLG;DOSAGE_USER;DISPENSE_USER;IP;MACHINENO;ATC_TYPETABLE.AutoModifyDataStore=YTABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;8,left;9,left;10,leftTABLE.LockColumns=0,1TABLE.FocusIndexList=2,3,5,6,7,8,9,10TABLE.FocusType=2ORG_CODE.Type=药房ORG_CODE.X=47ORG_CODE.Y=10ORG_CODE.Width=173ORG_CODE.Height=23ORG_CODE.Text=ORG_CODE.HorizontalAlignment=2ORG_CODE.PopupMenuHeader=代码,100;名称,100ORG_CODE.PopupMenuWidth=300ORG_CODE.PopupMenuHeight=300ORG_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1ORG_CODE.FormatType=comboORG_CODE.ShowDownButton=YORG_CODE.Tip=药房ORG_CODE.ShowColumnList=NAMEORG_CODE.Action=onQueryForDataORG_CODE.HisOneNullRow=YORG_CODE.OrgType=BtLabel_0.Type=TLabeltLabel_0.X=11tLabel_0.Y=16tLabel_0.Width=35tLabel_0.Height=15tLabel_0.Text=药房