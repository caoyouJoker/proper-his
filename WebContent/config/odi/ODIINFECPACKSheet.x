## TBuilder Config File ## Title:## Company:JavaHis## Author:caoy 2013.09.06## version 1.0#<Type=TFrame>UI.Title=引用表单UI.MenuConfig=UI.Width=963UI.Height=588UI.toolbar=YUI.controlclassname=com.javahis.ui.odi.ODIInfecPackSheetControlUI.item=TABLEPANEUI.layout=nullUI.zhTitle=引用表单UI.enTitle=ExamQuoteTABLEPANE.Type=TTabbedPaneTABLEPANE.X=3TABLEPANE.Y=3TABLEPANE.Width=956TABLEPANE.Height=580TABLEPANE.Item=tPanel_4;tPanel_5TABLEPANE.ChangedAction=onChangeStarttPanel_5.Type=TPaneltPanel_5.X=33tPanel_5.Y=13tPanel_5.Width=81tPanel_5.Height=81tPanel_5.Name=预防tPanel_5.Item=tRootPanel_0;tPanel_9;tPanel_10;tPanel_13tPanel_13.Type=TPaneltPanel_13.X=177tPanel_13.Y=470tPanel_13.Width=771tPanel_13.Height=78tPanel_13.Border=组|tPanel_13.Item=tButton_1;tButton_5;tButton_7tButton_7.Type=TButtontButton_7.X=594tButton_7.Y=26tButton_7.Width=81tButton_7.Height=23tButton_7.Text=关闭tButton_7.zhText=关闭tButton_7.Action=onClosetButton_7.enText=ClosetButton_5.Type=TButtontButton_5.X=324tButton_5.Y=28tButton_5.Width=81tButton_5.Height=23tButton_5.Text=传回tButton_5.Action=onSendtButton_1.Type=TButtontButton_1.X=89tButton_1.Y=28tButton_1.Width=81tButton_1.Height=23tButton_1.Text=全选tButton_1.Action=onSelAlltPanel_10.Type=TPaneltPanel_10.X=178tPanel_10.Y=28tPanel_10.Width=769tPanel_10.Height=440tPanel_10.Border=组|数据窗口|Data WindowtPanel_10.Item=TABLE3TABLE3.Type=TTableTABLE3.X=8TABLE3.Y=17TABLE3.Width=755TABLE3.Height=414TABLE3.SpacingRow=1TABLE3.RowHeight=20TABLE3.Header=传,30,boolean;连,30,boolean;组,50,int;医嘱名称,260;用量,60,double,#########0.000;单位,60,UNIT_COMBO;用法,60,ROUTE_CODE;频次,90,FREQ_CODE;规格,180;备注,200TABLE3.LockColumns=1,2,3,4,5,6,7,8,9,10,11,12TABLE3.HorizontalAlignmentData=TABLE3.ParmMap=FLG;LINKMAIN_FLG;LINK_NO;ORDER_DESC;MEDI_QTY;MEDI_UNIT;ROUTE_CODE;FREQ_CODE;SPECIFICATION;DESCRIPTIONTABLE3.AutoX=YTABLE3.AutoY=YTABLE3.AutoWidth=YTABLE3.AutoHeight=YTABLE3.Item=TABLE3.ColumnHorizontalAlignmentData=1,rigth;2,left;3,left;5,left;6,lefttPanel_9.Type=TPaneltPanel_9.X=4tPanel_9.Y=28tPanel_9.Width=169tPanel_9.Height=521tPanel_9.Border=组|tPanel_9.Item=TABLE2TABLE2.Type=TTableTABLE2.X=6TABLE2.Y=5TABLE2.Width=157TABLE2.Height=506TABLE2.SpacingRow=1TABLE2.RowHeight=20TABLE2.Header=套餐名称,160TABLE2.LockColumns=ALLTABLE2.HorizontalAlignmentData=0,leftTABLE2.ClickedAction=onTableClickTABLE2.ParmMap=PACK_DESCtRootPanel_0.Type=TRootPaneltRootPanel_0.X=2tRootPanel_0.Y=2tRootPanel_0.Width=946tRootPanel_0.Height=21tPanel_4.Type=TPaneltPanel_4.X=140tPanel_4.Y=143tPanel_4.Width=81tPanel_4.Height=81tPanel_4.Name=治疗tPanel_4.Item=tRootPanel_2;tPanel_6;tPanel_11;tPanel_12;tRadioButton_0;tRadioButton_1tRadioButton_1.Type=TRadioButtontRadioButton_1.X=96tRadioButton_1.Y=27tRadioButton_1.Width=81tRadioButton_1.Height=23tRadioButton_1.Text=术后预防tRadioButton_1.Group=tRadioButton_0.Type=TRadioButtontRadioButton_0.X=29tRadioButton_0.Y=29tRadioButton_0.Width=57tRadioButton_0.Height=23tRadioButton_0.Text=治疗tRadioButton_0.Group=tRadioButton_0.Selected=YtPanel_12.Type=TPaneltPanel_12.X=181tPanel_12.Y=470tPanel_12.Width=768tPanel_12.Height=76tPanel_12.Border=组|tPanel_12.Item=tButton_0;tButton_4;tButton_6tButton_6.Type=TButtontButton_6.X=594tButton_6.Y=27tButton_6.Width=81tButton_6.Height=23tButton_6.Text=关闭tButton_6.zhText=关闭tButton_6.Action=onClosetButton_6.enText=ClosetButton_4.Type=TButtontButton_4.X=334tButton_4.Y=26tButton_4.Width=81tButton_4.Height=23tButton_4.Text=传回tButton_4.Action=onSendtButton_0.Type=TButtontButton_0.X=79tButton_0.Y=26tButton_0.Width=81tButton_0.Height=23tButton_0.Text=全选tButton_0.Action=onSelAlltPanel_11.Type=TPaneltPanel_11.X=181tPanel_11.Y=53tPanel_11.Width=768tPanel_11.Height=415tPanel_11.Border=组|数据窗口|Data WindowtPanel_11.Item=TABLE1TABLE1.Type=TTableTABLE1.X=9TABLE1.Y=18TABLE1.Width=751TABLE1.Height=386TABLE1.SpacingRow=1TABLE1.RowHeight=20TABLE1.Header=传,30,boolean;连,30,boolean;组,50,int;医嘱名称,260;用量,60,double,#########0.000;单位,60,UNIT_COMBO;用法,60,ROUTE_CODE;频次,90,FREQ_CODE;规格,180;备注,200TABLE1.LockColumns=1,2,3,4,5,6,7,8,9,10,11,12TABLE1.HorizontalAlignmentData=TABLE1.ParmMap=FLG;LINKMAIN_FLG;LINK_NO;ORDER_DESC;MEDI_QTY;MEDI_UNIT;ROUTE_CODE;FREQ_CODE;SPECIFICATION;DESCRIPTIONTABLE1.AutoX=YTABLE1.AutoY=YTABLE1.AutoWidth=YTABLE1.AutoHeight=YTABLE1.Item=TABLE1.ColumnHorizontalAlignmentData=1,rigth;2,left;3,left;5,left;6,lefttPanel_6.Type=TPaneltPanel_6.X=5tPanel_6.Y=53tPanel_6.Width=174tPanel_6.Height=493tPanel_6.Border=组|tPanel_6.Item=TABLETABLE.Type=TTableTABLE.X=6TABLE.Y=6TABLE.Width=163TABLE.Height=479TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.Header=套餐名称,180TABLE.LockColumns=ALLTABLE.HorizontalAlignmentData=TABLE.ClickedAction=onTableClickTABLE.ParmMap=PACK_DESCTABLE.ColumnHorizontalAlignmentData=0,lefttRootPanel_2.Type=TRootPaneltRootPanel_2.X=0tRootPanel_2.Y=2tRootPanel_2.Width=950tRootPanel_2.Height=23