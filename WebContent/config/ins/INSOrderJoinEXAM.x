## TBuilder Config File ## Title:## Company:JavaHis## Author:庞犇 2011.11.07## version 1.0#<Type=TFrame>UI.Title=三目字典药品UI.MenuConfig=%ROOT%\config\ins\INSOrderJoinPHAMenu.xUI.Width=1173UI.Height=591UI.toolbar=YUI.controlclassname=com.javahis.ui.ins.INSOrderJoinPHAControlUI.item=tPanel_1;tPanel_3UI.layout=nullUI.FocusList=UI.Opaque=NtPanel_3.Type=TPaneltPanel_3.X=8tPanel_3.Y=77tPanel_3.Width=1154tPanel_3.Height=509tPanel_3.Border=组tPanel_3.AutoWidth=YtPanel_3.Item=TABLE_FEE;TABLE_RULE;tMovePane_0tPanel_3.AutoHeight=YtMovePane_0.Type=TMovePanetMovePane_0.X=525tMovePane_0.Y=12tMovePane_0.Width=26tMovePane_0.Height=47tMovePane_0.Text=TMovePanetMovePane_0.MoveType=1tMovePane_0.AutoHeight=YtMovePane_0.EntityData=TABLE_FEE,4;TABLE_RULE,3TABLE_RULE.Type=TTableTABLE_RULE.X=536TABLE_RULE.Y=10TABLE_RULE.Width=607TABLE_RULE.Height=488TABLE_RULE.SpacingRow=1TABLE_RULE.RowHeight=20TABLE_RULE.AutoHeight=YTABLE_RULE.AutoWidth=YTABLE_RULE.Header=选,25,boolean;三目编码,100;医保名称,170;最高限价,60;剂型,80;规格,80;批准文号,120;生产厂商,200;开始时间,170;结束时间,170TABLE_RULE.ParmMap=FLG;XMBM;XMMC;ZGXJ;JX;GG;PZWH;SCQY;KSSJ;JSSJ;XMBZ;XMLBTABLE_RULE.ColumnHorizontalAlignmentData=1,left;2,left;3,right;4,left;5,left;6,left;7,left;8,left;9,leftTABLE_RULE.LockRows=TABLE_RULE.LockColumns=1,2,3,4,5,6,7,8,9TABLE_FEE.Type=TTableTABLE_FEE.X=9TABLE_FEE.Y=10TABLE_FEE.Width=523TABLE_FEE.Height=488TABLE_FEE.SpacingRow=1TABLE_FEE.RowHeight=20TABLE_FEE.AutoWidth=NTABLE_FEE.AutoHeight=YTABLE_FEE.Header=选,25,boolean;医嘱码,60;医嘱名称,220;住院医保码,80;门诊医保码,80;急诊医保码,80;医保名称,100;医保价格,55,double;医保给付类别,80;价格,45;剂型,80,DOSE_CODE;规格,120;生产厂商,170,MAN_CODETABLE_FEE.ParmMap=FLG;ORDER_CODE;ORDER_DESC;NHI_CODE_I;NHI_CODE_O;NHI_CODE_E;NHI_FEE_DESC;NHI_PRICE;INSPAY_TYPE;OWN_PRICE;DOSE_CODE;SPECIFICATION;MAN_CODETABLE_FEE.LockColumns=1,2,3,4,5,6,7,8,9,10,11,12,13TABLE_FEE.Item=MAN_CODE;DOSE_CODETABLE_FEE.ClickedAction=TABLE_FEE.DoubleClickedAction=onTableDoubleClickTABLE_FEE.ColumnHorizontalAlignmentData=1,left;2,left;3,left;4,left;5,left;6,left;7,right;8,left;9,right;10,left;11,left;12,left;13,lefttPanel_1.Type=TPaneltPanel_1.X=8tPanel_1.Y=9tPanel_1.Width=1153tPanel_1.Height=63tPanel_1.Border=组tPanel_1.AutoWidth=YtPanel_1.Item=tPanel_2;RDO_MATE;RDO_NO_MATE;MAN_CODE;DOSE_CODE;SELECT_ALL;tPanel_4tPanel_4.Type=TPaneltPanel_4.X=730tPanel_4.Y=4tPanel_4.Width=251tPanel_4.Height=52tPanel_4.Border=组|保存医保码tPanel_4.Item=NHI_CODE_O;NHI_CODE_E;NHI_CODE_INHI_CODE_I.Type=TCheckBoxNHI_CODE_I.X=172NHI_CODE_I.Y=18NHI_CODE_I.Width=58NHI_CODE_I.Height=23NHI_CODE_I.Text=住院NHI_CODE_I.Selected=YNHI_CODE_E.Type=TCheckBoxNHI_CODE_E.X=101NHI_CODE_E.Y=17NHI_CODE_E.Width=69NHI_CODE_E.Height=23NHI_CODE_E.Text=急诊NHI_CODE_E.Selected=YNHI_CODE_O.Type=TCheckBoxNHI_CODE_O.X=35NHI_CODE_O.Y=18NHI_CODE_O.Width=59NHI_CODE_O.Height=23NHI_CODE_O.Text=门诊NHI_CODE_O.Selected=YSELECT_ALL.Type=TCheckBoxSELECT_ALL.X=9SELECT_ALL.Y=24SELECT_ALL.Width=56SELECT_ALL.Height=23SELECT_ALL.Text=全选SELECT_ALL.Action=onSelectAllDOSE_CODE.Type=剂型下拉区域DOSE_CODE.X=30DOSE_CODE.Y=65DOSE_CODE.Width=81DOSE_CODE.Height=23DOSE_CODE.Text=DOSE_CODE.HorizontalAlignment=2DOSE_CODE.PopupMenuHeader=代码,100;名称,100DOSE_CODE.PopupMenuWidth=300DOSE_CODE.PopupMenuHeight=300DOSE_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1DOSE_CODE.FormatType=comboDOSE_CODE.ShowDownButton=YDOSE_CODE.Tip=剂型DOSE_CODE.ShowColumnList=NAMEMAN_CODE.Type=生产厂商下拉区域MAN_CODE.X=46MAN_CODE.Y=64MAN_CODE.Width=81MAN_CODE.Height=23MAN_CODE.Text=MAN_CODE.HorizontalAlignment=2MAN_CODE.PopupMenuHeader=代码,100;名称,100MAN_CODE.PopupMenuWidth=300MAN_CODE.PopupMenuHeight=300MAN_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1MAN_CODE.FormatType=comboMAN_CODE.ShowDownButton=YMAN_CODE.Tip=生产厂商MAN_CODE.ShowColumnList=NAMERDO_NO_MATE.Type=TRadioButtonRDO_NO_MATE.X=189RDO_NO_MATE.Y=23RDO_NO_MATE.Width=69RDO_NO_MATE.Height=23RDO_NO_MATE.Text=未匹配RDO_NO_MATE.Group=groupRDO_NO_MATE.Action=onClickNoMateRDO_MATE.Type=TRadioButtonRDO_MATE.X=105RDO_MATE.Y=23RDO_MATE.Width=81RDO_MATE.Height=23RDO_MATE.Text=已匹配RDO_MATE.Group=groupRDO_MATE.Selected=YRDO_MATE.Action=onClickNoMatetPanel_2.Type=TPaneltPanel_2.X=325tPanel_2.Y=4tPanel_2.Width=393tPanel_2.Height=52tPanel_2.Border=组|条件tPanel_2.Item=SAME_DATA;SELECT_DATA;CMB_FACTORtPanel_2.Enabled=YCMB_FACTOR.Type=TComboBoxCMB_FACTOR.X=250CMB_FACTOR.Y=17CMB_FACTOR.Width=115CMB_FACTOR.Height=23CMB_FACTOR.Text=TButtonCMB_FACTOR.showID=YCMB_FACTOR.Editable=YCMB_FACTOR.CanEdit=YCMB_FACTOR.StringData=[[id,text],[,],[1,100%],[2,80%],[3,50%],[4,50%以下]]CMB_FACTOR.TableShowList=textCMB_FACTOR.Enabled=NCMB_FACTOR.Tip=条件匹配列表SELECT_DATA.Type=TRadioButtonSELECT_DATA.X=130SELECT_DATA.Y=18SELECT_DATA.Width=113SELECT_DATA.Height=23SELECT_DATA.Text=条件匹配数据SELECT_DATA.Group=group1SELECT_DATA.Action=onFactorSELECT_DATA.Enabled=NSAME_DATA.Type=TRadioButtonSAME_DATA.X=7SAME_DATA.Y=19SAME_DATA.Width=116SAME_DATA.Height=23SAME_DATA.Text=完全匹配数据SAME_DATA.Group=group1SAME_DATA.Selected=YSAME_DATA.Enabled=NSAME_DATA.Action=onFactorLBL.Type=TLabelLBL.X=27LBL.Y=37LBL.Width=69LBL.Height=15LBL.Text=读入信息:tas.Type=TLabeltas.X=29tas.Y=35tas.Width=72tas.Height=15tas.Text=读入信息: