## TBuilder Config File ## Title:## Company:JavaHis## Author:caoy 2013.09.06## version 1.0#<Type=TFrame>UI.Title=抗菌药品UI.MenuConfig=UI.Width=788UI.Height=480UI.toolbar=YUI.controlclassname=com.javahis.ui.odi.ODIInfecPackSheetControlUI.item=tPanel_0;tPanel_1;tMovePane_0UI.layout=nullUI.zhTitle=抗菌药品UI.enTitle=ExamQuoteUI.TopMenu=NUI.TopToolBar=NUI.LoadFlg=NUI.ShowMenu=NtMovePane_0.Type=TMovePanetMovePane_0.X=178tMovePane_0.Y=65tMovePane_0.Width=19tMovePane_0.Height=331tMovePane_0.Text=TMovePanetMovePane_0.MoveType=1tMovePane_0.EntityData=tPanel_2,4;tPanel_1,3tPanel_1.Type=TPaneltPanel_1.X=1tPanel_1.Y=82tPanel_1.Width=782tPanel_1.Height=390tPanel_1.AutoWidth=YtPanel_1.AutoHeight=YtPanel_1.Border=组|抗菌药品tPanel_1.Item=TABLE1;FREQ_CODE;ROUTE_CODE;MEDI_UNITMEDI_UNIT.Type=计量单位MEDI_UNIT.X=19MEDI_UNIT.Y=47MEDI_UNIT.Width=81MEDI_UNIT.Height=23MEDI_UNIT.Text=MEDI_UNIT.HorizontalAlignment=2MEDI_UNIT.PopupMenuHeader=代码,100;名称,100MEDI_UNIT.PopupMenuWidth=300MEDI_UNIT.PopupMenuHeight=300MEDI_UNIT.PopupMenuFilter=ID,1;NAME,1;PY1,1MEDI_UNIT.FormatType=comboMEDI_UNIT.ShowDownButton=YMEDI_UNIT.Tip=计量单位MEDI_UNIT.ShowColumnList=NAMEROUTE_CODE.Type=用法下拉区域ROUTE_CODE.X=232ROUTE_CODE.Y=47ROUTE_CODE.Width=81ROUTE_CODE.Height=23ROUTE_CODE.Text=ROUTE_CODE.HorizontalAlignment=2ROUTE_CODE.PopupMenuHeader=代码,100;名称,100ROUTE_CODE.PopupMenuWidth=300ROUTE_CODE.PopupMenuHeight=300ROUTE_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1ROUTE_CODE.FormatType=comboROUTE_CODE.ShowDownButton=YROUTE_CODE.Tip=用法ROUTE_CODE.ShowColumnList=NAMEFREQ_CODE.Type=频次FREQ_CODE.X=116FREQ_CODE.Y=47FREQ_CODE.Width=81FREQ_CODE.Height=23FREQ_CODE.Text=FREQ_CODE.HorizontalAlignment=2FREQ_CODE.PopupMenuHeader=代码,100;名称,100FREQ_CODE.PopupMenuWidth=300FREQ_CODE.PopupMenuHeight=300FREQ_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1FREQ_CODE.FormatType=comboFREQ_CODE.ShowDownButton=YFREQ_CODE.Tip=频次FREQ_CODE.ShowColumnList=NAMETABLE1.Type=TTableTABLE1.X=8TABLE1.Y=18TABLE1.Width=763TABLE1.Height=364TABLE1.SpacingRow=1TABLE1.RowHeight=20TABLE1.AutoWidth=YTABLE1.AutoHeight=YTABLE1.Header=传,30,boolean;连,30,boolean;组,30,int;医嘱名称,200;规格,80;用量,60,double,#########0.000;单位,60,MEDI_UNIT,MEDI_UNIT;用法,60,ROUTE_CODE,ROUTE_CODE;频次,90,FREQ_CODE,FREQ_CODE;备注,200TABLE1.LockColumns=1,2,3,4,5,6,7,8,9,10,11,12TABLE1.ColumnHorizontalAlignmentData=1,rigth;2,left;3,left;4,left;5,left;6,left;7,left;8,left;9,leftTABLE1.ParmMap=FLG;LINKMAIN_FLG;LINK_NO;ORDER_DESC;SPECIFICATION;MEDI_QTY;MEDI_UNIT;ROUTE_CODE;FREQ_CODE;DESCRIPTION;APPROVE_FLGTABLE1.Item=MEDI_UNIT;FREQ_CODE;ROUTE_CODEtPanel_0.Type=TPaneltPanel_0.X=4tPanel_0.Y=5tPanel_0.Width=779tPanel_0.Height=82tPanel_0.Border=组|tPanel_0.AutoWidth=YtPanel_0.Item=LBL;DESC;tButton_2;tButton_3;tButton_8;WRDO;OPRDO;tLabel_1;CAT_TYPE;ORDOORDO.Type=TRadioButtonORDO.X=529ORDO.Y=15ORDO.Width=58ORDO.Height=23ORDO.Text=治疗ORDO.Group=groupORDO.Action=onChang1CAT_TYPE.Type=TTextFieldCAT_TYPE.X=387CAT_TYPE.Y=16CAT_TYPE.Width=124CAT_TYPE.Height=20CAT_TYPE.Text=CAT_TYPE.Enabled=NtLabel_1.Type=TLabeltLabel_1.X=315tLabel_1.Y=17tLabel_1.Width=72tLabel_1.Height=15tLabel_1.Text=切口类型：OPRDO.Type=TRadioButtonOPRDO.X=592OPRDO.Y=15OPRDO.Width=81OPRDO.Height=23OPRDO.Text=术前预防OPRDO.Group=groupOPRDO.Action=onChang1OPRDO.Selected=YWRDO.Type=TRadioButtonWRDO.X=679WRDO.Y=16WRDO.Width=81WRDO.Height=23WRDO.Text=会诊结果WRDO.Group=groupWRDO.Action=onChang1tButton_8.Type=TButtontButton_8.X=488tButton_8.Y=52tButton_8.Width=61tButton_8.Height=23tButton_8.Text=关闭tButton_8.Action=onClosetButton_3.Type=TButtontButton_3.X=153tButton_3.Y=52tButton_3.Width=61tButton_3.Height=23tButton_3.Text=全选tButton_3.Action=onSelAlltButton_2.Type=TButtontButton_2.X=321tButton_2.Y=50tButton_2.Width=60tButton_2.Height=23tButton_2.Text=传回tButton_2.Action=onSendDESC.Type=TTextFieldDESC.X=56DESC.Y=15DESC.Width=239DESC.Height=20DESC.Text=DESC.Enabled=NLBL.Type=TLabelLBL.X=13LBL.Y=18LBL.Width=48LBL.Height=15LBL.Text=手术: