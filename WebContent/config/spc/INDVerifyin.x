## TBuilder Config File ## Title:验收入库管理## Company:JavaHis## Author:zhangy 2009.05.06## version 1.0#<Type=TFrame>UI.Title=导入装箱单UI.MenuConfig=%ROOT%\config\spc\INDVerifyinMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.spc.INDVerifyinControlUI.item=tPanel_1;tPanel_2;tMovePane_0;tPanel_3UI.layout=nullUI.Text=导入装箱单信息UI.Tip=导入装箱单信息UI.ShowMenu=NUI.ShowTitle=NUI.TopToolBar=YUI.TopMenu=YUI.FocusList=UPDATE_FLG_A;UPDATE_FLG_B;VERIFYIN_DATE;ORG_CODE;VERIFYIN_NO;SUP_CODE;PLAN_NO;REASON_CHN_DESC;CHECK_FLG;DESCRIPTIONtPanel_3.Type=TPaneltPanel_3.X=5tPanel_3.Y=103tPanel_3.Width=1014tPanel_3.Height=175tPanel_3.Border=凹tPanel_3.AutoX=YtPanel_3.AutoWidth=YtPanel_3.AutoHeight=NtPanel_3.Item=TABLE_MTABLE_M.Type=TTableTABLE_M.X=2TABLE_M.Y=0TABLE_M.Width=81TABLE_M.Height=81TABLE_M.SpacingRow=1TABLE_M.RowHeight=20TABLE_M.AutoX=YTABLE_M.AutoY=YTABLE_M.AutoWidth=YTABLE_M.AutoHeight=YTABLE_M.AutoSize=0TABLE_M.Header=入库单号,100;供应厂商,180,SUP_CODE_2;验收部门,120,ORG_CODE;验收人员,80,USER_ID;验收时间,100;验收结果,80,REASON_CODE;审核人员,80,USER_ID;审核时间,100;计划单号,100;付款计划日期,120;欠款打印,80,boolean;备注,150TABLE_M.ParmMap=VERIFYIN_NO;SUP_CODE;ORG_CODE;VERIFYIN_USER;VERIFYIN_DATE;REASON_CHN_DESC;CHECK_USER;CHECK_DATE;PLAN_NO;BILL_DATE;BILLPRINT_FLG;DESCRIPTIONTABLE_M.LockColumns=0,1,2,3,4,5,6,7,8,9,10,11TABLE_M.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left;9,left;11,leftTABLE_M.Item=SUP_CODE_2;ORG_CODE;USER_ID;REASON_CODETABLE_M.ClickedAction=onTableMClickedtMovePane_0.Type=TMovePanetMovePane_0.X=1tMovePane_0.Y=279tMovePane_0.Width=100tMovePane_0.Height=5tMovePane_0.Text=tMovePane_0.MoveType=2tMovePane_0.AutoX=YtMovePane_0.AutoWidth=YtMovePane_0.Style=3tMovePane_0.Border=凸tMovePane_0.AutoSize=3tMovePane_0.EntityData=tPanel_3,2;tPanel_2,1tPanel_2.Type=TPaneltPanel_2.X=5tPanel_2.Y=284tPanel_2.Width=1014tPanel_2.Height=446tPanel_2.AutoX=YtPanel_2.AutoWidth=YtPanel_2.AutoHeight=YtPanel_2.Border=组|验收细项tPanel_2.Item=tPanel_4;tPanel_5tPanel_5.Type=TPaneltPanel_5.X=9tPanel_5.Y=56tPanel_5.Width=996tPanel_5.Height=368tPanel_5.AutoX=YtPanel_5.AutoWidth=YtPanel_5.AutoSize=3tPanel_5.AutoHeight=YtPanel_5.Border=凹tPanel_5.Item=TABLE_DTABLE_D.Type=TTableTABLE_D.X=13TABLE_D.Y=-3TABLE_D.Width=81TABLE_D.Height=363TABLE_D.SpacingRow=1TABLE_D.RowHeight=20TABLE_D.AutoX=YTABLE_D.AutoY=YTABLE_D.AutoWidth=YTABLE_D.AutoHeight=YTABLE_D.AutoSize=0TABLE_D.Header=选,30,boolean;药品名称,180;规格,120;验收数,60,double,#####0.000;赠与数,60,double,#####0.000;验收单位,80,UNIT;验收单价,80,double,#####0.0000;验收金额,80,double,#####0.00;零售价,80,double,#####0.0000;零售金额,80,double,#####0.00;生产厂商,180;发票号码,80;发票日期,100,Timestamp,yyyy/MM/dd;批号,80;效期,100,Timestamp,yyyy/MM/dd;验收结果,100,REASON_CODE;品质扣款,80,double,#####0.00;申请单号,120;申请单号序号,120TABLE_D.ParmMap=SELECT_FLG;ORDER_DESC;SPECIFICATION;VERIFYIN_QTY;GIFT_QTY;BILL_UNIT;VERIFYIN_PRICE;INVOICE_AMT;RETAIL_PRICE;SELL_SUM;MAN_CODE;INVOICE_NO;INVOICE_DATE;BATCH_NO;VALID_DATE;REASON_CHN_DESC;QUALITY_DEDUCT_AMT;PURORDER_NO;PURSEQ_NO;SUP_ORDER_CODE;ACTUAL;PRCTABLE_D.AutoModifyDataStore=YTABLE_D.LockColumns=1,2,5,7,9,17,18,19TABLE_D.ColumnHorizontalAlignmentData=1,left;2,left;3,right;4,right;5,left;6,right;7,right;8,right;9,right;10,left;11,left;12,left;13,left;14,left;15,left;16,left;17,right;18,left;19,left;20,rightTABLE_D.Item=UNIT;REASON_CODETABLE_D.ClickedAction=onTableDClickedTABLE_D.AutoResizeMode=TABLE_D.FocusType=2TABLE_D.FocusIndexList=3,4,6,11,12,13,14,15,16,17TABLE_D.RowSelectionAllowed=NtPanel_4.Type=TPaneltPanel_4.X=9tPanel_4.Y=21tPanel_4.Width=996tPanel_4.Height=33tPanel_4.Border=组tPanel_4.AutoX=YtPanel_4.AutoY=YtPanel_4.AutoWidth=YtPanel_4.AutoSize=3tPanel_4.Item=SELECT_ALL;tLabel_19;SUM_RETAIL_PRICE;tLabel_20;SUM_VERIFYIN_PRICE;tLabel_21;PRICE_DIFFERENCE;UNIT;MAN_CODEMAN_CODE.Type=生产厂商下拉区域MAN_CODE.X=128MAN_CODE.Y=42MAN_CODE.Width=127MAN_CODE.Height=23MAN_CODE.Text=MAN_CODE.HorizontalAlignment=2MAN_CODE.PopupMenuHeader=代码,100;名称,100MAN_CODE.PopupMenuWidth=300MAN_CODE.PopupMenuHeight=300MAN_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1MAN_CODE.FormatType=comboMAN_CODE.ShowDownButton=YMAN_CODE.Tip=生产厂商MAN_CODE.ShowColumnList=NAMEMAN_CODE.HisOneNullRow=YMAN_CODE.PhaFlg=YUNIT.Type=计量单位下拉列表UNIT.X=133UNIT.Y=44UNIT.Width=10UNIT.Height=23UNIT.Text=TButtonUNIT.showID=YUNIT.showName=YUNIT.showText=NUNIT.showValue=NUNIT.showPy1=NUNIT.showPy2=NUNIT.Editable=YUNIT.Tip=计量单位UNIT.TableShowList=namePRICE_DIFFERENCE.Type=TNumberTextFieldPRICE_DIFFERENCE.X=907PRICE_DIFFERENCE.Y=6PRICE_DIFFERENCE.Width=80PRICE_DIFFERENCE.Height=20PRICE_DIFFERENCE.Text=0PRICE_DIFFERENCE.Format=#########0.00PRICE_DIFFERENCE.Enabled=NPRICE_DIFFERENCE.Visible=NtLabel_21.Type=TLabeltLabel_21.X=831tLabel_21.Y=10tLabel_21.Width=72tLabel_21.Height=15tLabel_21.Text=进销差价:tLabel_21.Visible=NSUM_VERIFYIN_PRICE.Type=TNumberTextFieldSUM_VERIFYIN_PRICE.X=726SUM_VERIFYIN_PRICE.Y=6SUM_VERIFYIN_PRICE.Width=80SUM_VERIFYIN_PRICE.Height=20SUM_VERIFYIN_PRICE.Text=0SUM_VERIFYIN_PRICE.Format=#########0.00SUM_VERIFYIN_PRICE.Enabled=NtLabel_20.Type=TLabeltLabel_20.X=642tLabel_20.Y=10tLabel_20.Width=80tLabel_20.Height=15tLabel_20.Text=进货总金额:SUM_RETAIL_PRICE.Type=TNumberTextFieldSUM_RETAIL_PRICE.X=530SUM_RETAIL_PRICE.Y=6SUM_RETAIL_PRICE.Width=80SUM_RETAIL_PRICE.Height=20SUM_RETAIL_PRICE.Text=0SUM_RETAIL_PRICE.Format=#########0.00SUM_RETAIL_PRICE.Enabled=NtLabel_19.Type=TLabeltLabel_19.X=440tLabel_19.Y=10tLabel_19.Width=80tLabel_19.Height=15tLabel_19.Text=零售总金额:tLabel_19.HorizontalAlignment=SELECT_ALL.Type=TCheckBoxSELECT_ALL.X=14SELECT_ALL.Y=5SELECT_ALL.Width=81SELECT_ALL.Height=23SELECT_ALL.Text=全部勾选SELECT_ALL.Action=onCheckSelectAlltPanel_1.Type=TPaneltPanel_1.X=5tPanel_1.Y=5tPanel_1.Width=1014tPanel_1.Height=95tPanel_1.Border=组tPanel_1.AutoX=YtPanel_1.AutoY=YtPanel_1.AutoWidth=YtPanel_1.Item=tLabel_6;UPDATE_FLG_A;UPDATE_FLG_B;tLabel_7;VERIFYIN_DATE;tLabel_8;ORG_CODE;tLabel_14;VERIFYIN_NO;tLabel_15;SUP_CODE_2;tLabel_16;tLabel_17;tLabel_18;PLAN_NO;DESCRIPTION;CHECK_FLG;tLabel_22;START_DATE;tLabel_23;END_DATE;REASON_CODE;SUP_CODE;USER_ID;tLabel_3;TOXIC_DRUG;GEN_DRUG;SPC_BOX_BARCODE;tLabel_0;DRUG_TO_ORG_CODEDRUG_TO_ORG_CODE.Type=物联网药库请领部门B下拉列表DRUG_TO_ORG_CODE.X=673DRUG_TO_ORG_CODE.Y=63DRUG_TO_ORG_CODE.Width=102DRUG_TO_ORG_CODE.Height=23DRUG_TO_ORG_CODE.Text=DRUG_TO_ORG_CODE.HorizontalAlignment=2DRUG_TO_ORG_CODE.PopupMenuHeader=编码,100;名称,100DRUG_TO_ORG_CODE.PopupMenuWidth=300DRUG_TO_ORG_CODE.PopupMenuHeight=300DRUG_TO_ORG_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1DRUG_TO_ORG_CODE.FormatType=comboDRUG_TO_ORG_CODE.ShowDownButton=YDRUG_TO_ORG_CODE.Tip=药库DRUG_TO_ORG_CODE.ShowColumnList=NAMEtLabel_0.Type=TLabeltLabel_0.X=578tLabel_0.Y=68tLabel_0.Width=96tLabel_0.Height=15tLabel_0.Text=麻精入库部门:SPC_BOX_BARCODE.Type=TTextFieldSPC_BOX_BARCODE.X=547SPC_BOX_BARCODE.Y=59SPC_BOX_BARCODE.Width=77SPC_BOX_BARCODE.Height=20SPC_BOX_BARCODE.Text=123SPC_BOX_BARCODE.Visible=NGEN_DRUG.Type=TRadioButtonGEN_DRUG.X=749GEN_DRUG.Y=6GEN_DRUG.Width=67GEN_DRUG.Height=23GEN_DRUG.Text=普药GEN_DRUG.Group=group2GEN_DRUG.Selected=YTOXIC_DRUG.Type=TRadioButtonTOXIC_DRUG.X=823TOXIC_DRUG.Y=6TOXIC_DRUG.Width=53TOXIC_DRUG.Height=23TOXIC_DRUG.Text=麻精TOXIC_DRUG.Group=group2TOXIC_DRUG.Selected=NtLabel_3.Type=TLabeltLabel_3.X=681tLabel_3.Y=10tLabel_3.Width=72tLabel_3.Height=15tLabel_3.Text=药品种类:tLabel_3.Color=blueUSER_ID.Type=人员下拉列表USER_ID.X=735USER_ID.Y=107USER_ID.Width=10USER_ID.Height=23USER_ID.Text=TButtonUSER_ID.showID=YUSER_ID.showName=YUSER_ID.showText=NUSER_ID.showValue=NUSER_ID.showPy1=NUSER_ID.showPy2=NUSER_ID.Editable=YUSER_ID.Tip=人员USER_ID.TableShowList=nameUSER_ID.ModuleParmString=USER_ID.ModuleParmTag=USER_ID.Classify=SUP_CODE.Type=供应厂商SUP_CODE.X=751SUP_CODE.Y=35SUP_CODE.Width=174SUP_CODE.Height=23SUP_CODE.Text=SUP_CODE.HorizontalAlignment=2SUP_CODE.PopupMenuHeader=ID,100;NAME,100SUP_CODE.PopupMenuWidth=300SUP_CODE.PopupMenuHeight=300SUP_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1SUP_CODE.FormatType=comboSUP_CODE.ShowDownButton=YSUP_CODE.Tip=供应厂商SUP_CODE.ShowColumnList=NAMESUP_CODE.HisOneNullRow=YSUP_CODE.PhaFlg=YREASON_CODE.Type=药库原因REASON_CODE.X=330REASON_CODE.Y=66REASON_CODE.Width=120REASON_CODE.Height=23REASON_CODE.Text=TButtonREASON_CODE.showID=NREASON_CODE.showName=YREASON_CODE.showText=NREASON_CODE.showValue=NREASON_CODE.showPy1=NREASON_CODE.showPy2=NREASON_CODE.Editable=YREASON_CODE.Tip=药库原因REASON_CODE.TableShowList=nameREASON_CODE.ReasonType=VERREASON_CODE.ExpandWidth=0END_DATE.Type=TTextFormatEND_DATE.X=511END_DATE.Y=9END_DATE.Width=160END_DATE.Height=20END_DATE.Text=END_DATE.showDownButton=YEND_DATE.FormatType=dateEND_DATE.Format=yyyy/MM/dd HH:mm:sstLabel_23.Type=TLabeltLabel_23.X=489tLabel_23.Y=11tLabel_23.Width=25tLabel_23.Height=15tLabel_23.Text=～tLabel_23.HorizontalAlignment=0START_DATE.Type=TTextFormatSTART_DATE.X=330START_DATE.Y=9START_DATE.Width=160START_DATE.Height=20START_DATE.Text=START_DATE.showDownButton=YSTART_DATE.FormatType=dateSTART_DATE.Format=yyyy/MM/dd HH:mm:sstLabel_22.Type=TLabeltLabel_22.X=265tLabel_22.Y=13tLabel_22.Width=72tLabel_22.Height=15tLabel_22.Text=查询区间:tLabel_22.Color=blueCHECK_FLG.Type=TCheckBoxCHECK_FLG.X=465CHECK_FLG.Y=66CHECK_FLG.Width=81CHECK_FLG.Height=23CHECK_FLG.Text=审核入库CHECK_FLG.Enabled=NDESCRIPTION.Type=TTextFieldDESCRIPTION.X=836DESCRIPTION.Y=67DESCRIPTION.Width=89DESCRIPTION.Height=20DESCRIPTION.Text=PLAN_NO.Type=TTextFieldPLAN_NO.X=91PLAN_NO.Y=68PLAN_NO.Width=160PLAN_NO.Height=20PLAN_NO.Text=PLAN_NO.Enabled=NtLabel_18.Type=TLabeltLabel_18.X=799tLabel_18.Y=67tLabel_18.Width=72tLabel_18.Height=15tLabel_18.Text=备注:tLabel_17.Type=TLabeltLabel_17.X=265tLabel_17.Y=70tLabel_17.Width=72tLabel_17.Height=15tLabel_17.Text=验收结果:tLabel_16.Type=TLabeltLabel_16.X=17tLabel_16.Y=70tLabel_16.Width=72tLabel_16.Height=15tLabel_16.Text=计划单号:tLabel_16.Color=黑SUP_CODE_2.Type=供应厂商下拉列表SUP_CODE_2.X=817SUP_CODE_2.Y=105SUP_CODE_2.Width=10SUP_CODE_2.Height=23SUP_CODE_2.Text=TButtonSUP_CODE_2.showID=YSUP_CODE_2.showName=YSUP_CODE_2.showText=NSUP_CODE_2.showValue=NSUP_CODE_2.showPy1=NSUP_CODE_2.showPy2=NSUP_CODE_2.Editable=YSUP_CODE_2.Tip=供应厂商SUP_CODE_2.TableShowList=nameSUP_CODE_2.ModuleParmString=SUP_CODE_2.ModuleParmTag=SUP_CODE_2.ExpandWidth=30tLabel_15.Type=TLabeltLabel_15.X=685tLabel_15.Y=40tLabel_15.Width=72tLabel_15.Height=15tLabel_15.Text=供应厂商:tLabel_15.Color=blueVERIFYIN_NO.Type=TTextFieldVERIFYIN_NO.X=543VERIFYIN_NO.Y=36VERIFYIN_NO.Width=105VERIFYIN_NO.Height=20VERIFYIN_NO.Text=tLabel_14.Type=TLabeltLabel_14.X=468tLabel_14.Y=40tLabel_14.Width=72tLabel_14.Height=15tLabel_14.Text=验收单号:tLabel_14.Color=blueORG_CODE.Type=药房下拉列表ORG_CODE.X=330ORG_CODE.Y=35ORG_CODE.Width=120ORG_CODE.Height=23ORG_CODE.Text=TButtonORG_CODE.showID=YORG_CODE.showName=YORG_CODE.showText=NORG_CODE.showValue=NORG_CODE.showPy1=NORG_CODE.showPy2=NORG_CODE.Editable=YORG_CODE.Tip=药房ORG_CODE.TableShowList=nameORG_CODE.ModuleParmTag=ORG_CODE.OrgType=AORG_CODE.ExpandWidth=30tLabel_8.Type=TLabeltLabel_8.X=265tLabel_8.Y=40tLabel_8.Width=72tLabel_8.Height=15tLabel_8.Text=验收部门:tLabel_8.Color=blueVERIFYIN_DATE.Type=TTextFormatVERIFYIN_DATE.X=91VERIFYIN_DATE.Y=37VERIFYIN_DATE.Width=160VERIFYIN_DATE.Height=20VERIFYIN_DATE.Text=VERIFYIN_DATE.FormatType=dateVERIFYIN_DATE.Format=yyyy/MM/dd HH:mm:ssVERIFYIN_DATE.showDownButton=YVERIFYIN_DATE.HorizontalAlignment=2tLabel_7.Type=TLabeltLabel_7.X=17tLabel_7.Y=40tLabel_7.Width=72tLabel_7.Height=15tLabel_7.Text=验收时间:tLabel_7.Color=黑UPDATE_FLG_B.Type=TRadioButtonUPDATE_FLG_B.X=169UPDATE_FLG_B.Y=8UPDATE_FLG_B.Width=81UPDATE_FLG_B.Height=23UPDATE_FLG_B.Text=未审核UPDATE_FLG_B.Group=group1UPDATE_FLG_B.Selected=YUPDATE_FLG_B.Action=onChangeRadioButtonUPDATE_FLG_A.Type=TRadioButtonUPDATE_FLG_A.X=88UPDATE_FLG_A.Y=8UPDATE_FLG_A.Width=81UPDATE_FLG_A.Height=23UPDATE_FLG_A.Text=已审核UPDATE_FLG_A.Group=group1UPDATE_FLG_A.Action=onChangeRadioButtontLabel_6.Type=TLabeltLabel_6.X=17tLabel_6.Y=13tLabel_6.Width=72tLabel_6.Height=15tLabel_6.Text=验收状态:tLabel_6.AutoX=NtLabel_6.AutoY=NtLabel_6.AutoSize=10tLabel_6.Color=blue