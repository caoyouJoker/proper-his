## TBuilder Config File ## Title:## Company:JavaHis## Author:ehui 2009.04.29## version 1.0#<Type=TFrame>UI.Title=调用常用医嘱UI.MenuConfig=UI.Width=610UI.Height=300UI.toolbar=YUI.controlclassname=com.javahis.ui.opd.OPDCommOrderQuoteControlUI.item=TABLE1;FETCH;CANCEL;TABLE2;LBL_RX;RX_NO;SEL_ALLUI.layout=nullUI.X=600UI.Y=600UI.zhTitle=调用常用医嘱UI.enTitle=Common OrderSEL_ALL.Type=TCheckBoxSEL_ALL.X=411SEL_ALL.Y=6SEL_ALL.Width=81SEL_ALL.Height=23SEL_ALL.Text=全选SEL_ALL.Action=onSelectAllRX_NO.Type=TComboBoxRX_NO.X=261RX_NO.Y=6RX_NO.Width=120RX_NO.Height=23RX_NO.Text=TButtonRX_NO.showID=NRX_NO.Editable=YRX_NO.ShowName=YRX_NO.ShowPy1=NRX_NO.ShowPy2=NRX_NO.TableShowList=nameRX_NO.ParmMap=id:ID;name:NAMERX_NO.SelectedAction=onRxChangeLBL_RX.Type=TLabelLBL_RX.X=226LBL_RX.Y=10LBL_RX.Width=31LBL_RX.Height=15LBL_RX.Text=处方LBL_RX.zhText=处方LBL_RX.enText=RxTABLE2.Type=TTableTABLE2.X=304TABLE2.Y=39TABLE2.Width=299TABLE2.Height=256TABLE2.SpacingRow=1TABLE2.RowHeight=20TABLE2.Header=选,30,boolean;医嘱,220;启,30,booleanTABLE2.AutoHeight=YTABLE2.ParmMap=CHOOSE;ORDER_DESC;ACTIVE_FLGTABLE2.ColumnHorizontalAlignmentData=1,leftTABLE2.LockColumns=1,2TABLE2.enHeader=Pick;OrderTABLE2.LanguageMap=ORDER_DESC|TRADE_ENG_DESCCANCEL.Type=TButtonCANCEL.X=115CANCEL.Y=6CANCEL.Width=99CANCEL.Height=23CANCEL.Text=关闭CANCEL.Action=onCloseCANCEL.zhText=关闭CANCEL.enText=CloseFETCH.Type=TButtonFETCH.X=6FETCH.Y=6FETCH.Width=99FETCH.Height=23FETCH.Text=传回FETCH.Action=onOkFETCH.zhText=传回FETCH.enText=RetrieveTABLE1.Type=TTableTABLE1.X=6TABLE1.Y=39TABLE1.Width=291TABLE1.Height=256TABLE1.SpacingRow=1TABLE1.RowHeight=20TABLE1.AutoWidth=NTABLE1.AutoHeight=YTABLE1.Header=选,30,boolean;医嘱,220;启,30,booleanTABLE1.ColumnHorizontalAlignmentData=1,leftTABLE1.ParmMap=CHOOSE;ORDER_DESC;ACTIVE_FLGTABLE1.LockColumns=1,2TABLE1.enHeader=Pick;OrderTABLE1.LanguageMap=ORDER_DESC|TRADE_ENG_DESC