## TBuilder Config File ## Title:卫耗材领用作业## Company:JavaHis## Author:zhangy 2009.05.06## version 1.0#<Type=TFrame>UI.Title=卫耗材领用UI.MenuConfig=%ROOT%\config\spc\INDRequestCosMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.spc.INDRequestCosControlUI.item=tPanel_0;tPanel_6;tPanel_7;tMovePane_1UI.layout=nullUI.Text=卫耗材领用作业UI.Tip=卫耗材领用作业UI.TopMenu=YUI.TopToolBar=YUI.FocusList=UPDATE_FLG_A;UPDATE_FLG_B;URGENT_FLG;REQTYPE_CODE;APP_ORG_CODE;TO_ORG_CODE;REASON_CHN_DESC;DESCRIPTIONUI.ShowTitle=NUI.ShowMenu=NtMovePane_1.Type=TMovePanetMovePane_1.X=6tMovePane_1.Y=271tMovePane_1.Width=100tMovePane_1.Height=5tMovePane_1.Text=tMovePane_1.MoveType=2tMovePane_1.Border=凸tMovePane_1.Style=3tMovePane_1.AutoX=YtMovePane_1.AutoWidth=YtMovePane_1.EntityData=tPanel_6,2;tPanel_7,1tPanel_7.Type=TPaneltPanel_7.X=2tPanel_7.Y=276tPanel_7.Width=1014tPanel_7.Height=445tPanel_7.Border=组|申请明细tPanel_7.AutoX=YtPanel_7.AutoWidth=YtPanel_7.AutoHeight=YtPanel_7.Item=tPanel_8;tPanel_9tPanel_9.Type=TPaneltPanel_9.X=11tPanel_9.Y=56tPanel_9.Width=992tPanel_9.Height=378tPanel_9.AutoX=YtPanel_9.AutoWidth=YtPanel_9.AutoHeight=YtPanel_9.Border=凹tPanel_9.Item=TABLE_DTABLE_D.Type=TTableTABLE_D.X=101TABLE_D.Y=2TABLE_D.Width=889TABLE_D.Height=374TABLE_D.SpacingRow=1TABLE_D.RowHeight=20TABLE_D.AutoWidth=YTABLE_D.AutoHeight=YTABLE_D.AutoY=YTABLE_D.AutoX=YTABLE_D.AutoSize=0TABLE_D.Header=药品名称,200;规格,150;请领数量,80,double,#####0.000;单位,40,UNIT;零售价,100,double,#####0.0000;零售金额,120,double,#####0.00;累计完成量,100,double,#####0.000;中止,40,booleanTABLE_D.LockColumns=1,3,4,5,6,7TABLE_D.ColumnHorizontalAlignmentData=0,left;1,left;2,right;3,left;4,right;5,right;6,rightTABLE_D.ClickedAction=onTableDClickedTABLE_D.ParmMap=ORDER;SPECIFICATION;QTY;UNIT_CODE;RETAIL_PRICE;SUM_RETAIL_PRICE;ACTUAL_QTY;END_FLGTABLE_D.AutoModifyDataStore=YTABLE_D.Item=UNITTABLE_D.FocusType=2TABLE_D.FocusIndexList=0,2TABLE_D.Enabled=YTABLE_D.Visible=YtPanel_8.Type=TPaneltPanel_8.X=11tPanel_8.Y=20tPanel_8.Width=992tPanel_8.Height=31tPanel_8.Border=组tPanel_8.AutoX=YtPanel_8.AutoY=YtPanel_8.AutoWidth=YtPanel_8.Item=tLabel_0;SUM_RETAIL_PRICE;UNIT_TYPESUM_RETAIL_PRICE.Type=TNumberTextFieldSUM_RETAIL_PRICE.X=859SUM_RETAIL_PRICE.Y=6SUM_RETAIL_PRICE.Width=111SUM_RETAIL_PRICE.Height=20SUM_RETAIL_PRICE.Text=0SUM_RETAIL_PRICE.Format=#########0.00SUM_RETAIL_PRICE.Enabled=NtLabel_0.Type=TLabeltLabel_0.X=758tLabel_0.Y=9tLabel_0.Width=100tLabel_0.Height=15tLabel_0.Text=零售总金额:tPanel_6.Type=TPaneltPanel_6.X=5tPanel_6.Y=98tPanel_6.Width=1014tPanel_6.Height=173tPanel_6.Border=凹tPanel_6.AutoX=YtPanel_6.AutoWidth=YtPanel_6.Item=TABLE_MTABLE_M.Type=TTableTABLE_M.X=51TABLE_M.Y=2TABLE_M.Width=81TABLE_M.Height=169TABLE_M.SpacingRow=1TABLE_M.RowHeight=20TABLE_M.AutoX=YTABLE_M.AutoY=YTABLE_M.AutoWidth=YTABLE_M.AutoHeight=YTABLE_M.AutoSize=0TABLE_M.Header=申请日期,100;申请单号,100;单据类别,100,REQTYPE_CODE;申请部门,100,ORG_CODE;申请人员,100,REQUEST_USER;出库部门,100,TO_ORG_CODE;申请原因,100,REASON_CHN_DESC;单位,80,UNIT_TYPE;备注,150;急,30,booleanTABLE_M.ParmMap=REQUEST_DATE;REQUEST_NO;REQTYPE_CODE;APP_ORG_CODE;REQUEST_USER;TO_ORG_CODE;REASON_CHN_DESC;UNIT_TYPE;DESCRIPTION;URGENT_FLGTABLE_M.Item=REQTYPE_CODE;ORG_CODE;TO_ORG_CODE;REASON_CHN_DESC;UNIT_TYPE;REQUEST_USERTABLE_M.LockColumns=0,1,2,3,4,5,6,7,8,9TABLE_M.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,leftTABLE_M.ClickedAction=onTableMClickedtPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=5tPanel_0.Width=1014tPanel_0.Height=91tPanel_0.AutoX=YtPanel_0.AutoY=YtPanel_0.AutoWidth=YtPanel_0.Border=组tPanel_0.Item=tLabel_1;UPDATE_FLG_A;UPDATE_FLG_B;tLabel_2;REQUEST_DATE;tLabel_3;tLabel_4;REQUEST_NO;tLabel_5;REQTYPE_CODE;tLabel_9;tLabel_11;tLabel_12;DESCRIPTION;URGENT_FLG;tLabel_8;START_DATE;tLabel_14;END_DATE;REASON_CHN_DESC;UNIT_TYPE;药品类别_0;TO_ORG_CODE;UNIT;REQUEST_USER;APP_ORG_CODE;ORG_CODEORG_CODE.Type=科室下拉列表ORG_CODE.X=759ORG_CODE.Y=98ORG_CODE.Width=81ORG_CODE.Height=23ORG_CODE.Text=TButtonORG_CODE.showID=YORG_CODE.showName=YORG_CODE.showText=NORG_CODE.showValue=NORG_CODE.showPy1=NORG_CODE.showPy2=YORG_CODE.Editable=YORG_CODE.Tip=科室ORG_CODE.TableShowList=nameORG_CODE.Grade=3APP_ORG_CODE.Type=TTextFormatAPP_ORG_CODE.X=92APP_ORG_CODE.Y=61APP_ORG_CODE.Width=160APP_ORG_CODE.Height=20APP_ORG_CODE.Text=APP_ORG_CODE.showDownButton=YAPP_ORG_CODE.FormatType=comboAPP_ORG_CODE.PopupMenuHeader=ID,100;NAME,150APP_ORG_CODE.HisOneNullRow=YAPP_ORG_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1APP_ORG_CODE.ShowColumnList=NAMEAPP_ORG_CODE.ValueColumn=IDAPP_ORG_CODE.Action=onChangeDeptAPP_ORG_CODE.HorizontalAlignment=2APP_ORG_CODE.PopupMenuHeight=150APP_ORG_CODE.PopupMenuWidth=260REQUEST_USER.Type=人员下拉列表REQUEST_USER.X=765REQUEST_USER.Y=104REQUEST_USER.Width=10REQUEST_USER.Height=23REQUEST_USER.Text=TButtonREQUEST_USER.showID=YREQUEST_USER.showName=YREQUEST_USER.showText=NREQUEST_USER.showValue=NREQUEST_USER.showPy1=NREQUEST_USER.showPy2=NREQUEST_USER.Editable=YREQUEST_USER.Tip=人员REQUEST_USER.TableShowList=nameREQUEST_USER.ModuleParmString=REQUEST_USER.ModuleParmTag=REQUEST_USER.Classify=UNIT.Type=计量单位下拉列表UNIT.X=647UNIT.Y=101UNIT.Width=10UNIT.Height=23UNIT.Text=TButtonUNIT.showID=YUNIT.showName=YUNIT.showText=NUNIT.showValue=NUNIT.showPy1=NUNIT.showPy2=NUNIT.Editable=YUNIT.Tip=计量单位UNIT.TableShowList=nameTO_ORG_CODE.Type=药房下拉列表TO_ORG_CODE.X=356TO_ORG_CODE.Y=59TO_ORG_CODE.Width=121TO_ORG_CODE.Height=23TO_ORG_CODE.Text=TButtonTO_ORG_CODE.showID=YTO_ORG_CODE.showName=YTO_ORG_CODE.showText=NTO_ORG_CODE.showValue=NTO_ORG_CODE.showPy1=NTO_ORG_CODE.showPy2=NTO_ORG_CODE.Editable=YTO_ORG_CODE.Tip=药房TO_ORG_CODE.TableShowList=nameTO_ORG_CODE.ModuleParmTag=TO_ORG_CODE.ExpandWidth=80药品类别_0.Type=药品类别药品类别_0.X=266药品类别_0.Y=20UNIT_TYPE.Type=TComboBoxUNIT_TYPE.X=819UNIT_TYPE.Y=129UNIT_TYPE.Width=10UNIT_TYPE.Height=23UNIT_TYPE.Text=TButtonUNIT_TYPE.showID=YUNIT_TYPE.Editable=YUNIT_TYPE.ShowText=NUNIT_TYPE.ShowName=YUNIT_TYPE.TableShowList=nameUNIT_TYPE.StringData=[[id,name],[,],[0,库存单位],[1,配药单位]]REASON_CHN_DESC.Type=药库原因REASON_CHN_DESC.X=616REASON_CHN_DESC.Y=59REASON_CHN_DESC.Width=120REASON_CHN_DESC.Height=23REASON_CHN_DESC.Text=TButtonREASON_CHN_DESC.showID=YREASON_CHN_DESC.showName=YREASON_CHN_DESC.showText=NREASON_CHN_DESC.showValue=NREASON_CHN_DESC.showPy1=NREASON_CHN_DESC.showPy2=NREASON_CHN_DESC.Editable=YREASON_CHN_DESC.Tip=药库原因REASON_CHN_DESC.TableShowList=nameREASON_CHN_DESC.ReasonType=DEPEND_DATE.Type=TTextFormatEND_DATE.X=543END_DATE.Y=7END_DATE.Width=160END_DATE.Height=20END_DATE.Text=END_DATE.HorizontalAlignment=2END_DATE.showDownButton=YEND_DATE.Format=yyyy/MM/dd HH:mm:ssEND_DATE.FormatType=datetLabel_14.Type=TLabeltLabel_14.X=519tLabel_14.Y=10tLabel_14.Width=25tLabel_14.Height=15tLabel_14.Text=～tLabel_14.HorizontalAlignment=0START_DATE.Type=TTextFormatSTART_DATE.X=356START_DATE.Y=7START_DATE.Width=160START_DATE.Height=20START_DATE.Text=START_DATE.showDownButton=YSTART_DATE.FormatType=dateSTART_DATE.Format=yyyy/MM/dd HH:mm:ssSTART_DATE.HorizontalAlignment=2tLabel_8.Type=TLabeltLabel_8.X=283tLabel_8.Y=10tLabel_8.Width=72tLabel_8.Height=15tLabel_8.Text=查询区间:tLabel_8.Color=blueURGENT_FLG.Type=TCheckBoxURGENT_FLG.X=757URGENT_FLG.Y=5URGENT_FLG.Width=100URGENT_FLG.Height=23URGENT_FLG.Text=急件注记DESCRIPTION.Type=TTextFieldDESCRIPTION.X=835DESCRIPTION.Y=60DESCRIPTION.Width=140DESCRIPTION.Height=20DESCRIPTION.Text=tLabel_12.Type=TLabeltLabel_12.X=762tLabel_12.Y=64tLabel_12.Width=72tLabel_12.Height=15tLabel_12.Text=备    注:tLabel_11.Type=TLabeltLabel_11.X=545tLabel_11.Y=64tLabel_11.Width=72tLabel_11.Height=15tLabel_11.Text=申请原因:tLabel_9.Type=TLabeltLabel_9.X=283tLabel_9.Y=64tLabel_9.Width=72tLabel_9.Height=15tLabel_9.Text=出库部门:tLabel_9.Color=蓝REQTYPE_CODE.Type=TComboBox  REQTYPE_CODE.X=356REQTYPE_CODE.Y=32REQTYPE_CODE.Width=120REQTYPE_CODE.Height=23REQTYPE_CODE.Text=TButtonREQTYPE_CODE.showID=YREQTYPE_CODE.Editable=YREQTYPE_CODE.ShowName=YREQTYPE_CODE.ShowText=NREQTYPE_CODE.TableShowList=nameREQTYPE_CODE.ExpandWidth=30REQTYPE_CODE.StringData=[[id,name],[COS,卫耗材领用],[SRD,科研用药],[NMA,核医学用药]]REQTYPE_CODE.SelectedAction=REQTYPE_CODE.Action=tLabel_5.Type=TLabeltLabel_5.X=283tLabel_5.Y=37tLabel_5.Width=72tLabel_5.Height=15tLabel_5.Text=单据类别:tLabel_5.Color=blueREQUEST_NO.Type=TTextFieldREQUEST_NO.X=616REQUEST_NO.Y=33REQUEST_NO.Width=119REQUEST_NO.Height=20REQUEST_NO.Text=REQUEST_NO.Enabled=YtLabel_4.Type=TLabeltLabel_4.X=545tLabel_4.Y=37tLabel_4.Width=72tLabel_4.Height=15tLabel_4.Text=申请单号:tLabel_4.Color=bluetLabel_3.Type=TLabeltLabel_3.X=15tLabel_3.Y=64tLabel_3.Width=72tLabel_3.Height=15tLabel_3.Text=申请部门:tLabel_3.Color=blueREQUEST_DATE.Type=TTextFormatREQUEST_DATE.X=92REQUEST_DATE.Y=34REQUEST_DATE.Width=160REQUEST_DATE.Height=20REQUEST_DATE.Text=REQUEST_DATE.showDownButton=YREQUEST_DATE.Format=yyyy/MM/dd HH:mm:ssREQUEST_DATE.FormatType=dateREQUEST_DATE.HorizontalAlignment=2tLabel_2.Type=TLabeltLabel_2.X=15tLabel_2.Y=37tLabel_2.Width=72tLabel_2.Height=15tLabel_2.Text=申请时间:tLabel_2.Color=黑UPDATE_FLG_B.Type=TRadioButtonUPDATE_FLG_B.X=181UPDATE_FLG_B.Y=6UPDATE_FLG_B.Width=81UPDATE_FLG_B.Height=23UPDATE_FLG_B.Text=完成UPDATE_FLG_B.Group=group1UPDATE_FLG_B.Action=onChangeRadioButtonUPDATE_FLG_A.Type=TRadioButtonUPDATE_FLG_A.X=89UPDATE_FLG_A.Y=6UPDATE_FLG_A.Width=81UPDATE_FLG_A.Height=23UPDATE_FLG_A.Text=未完成UPDATE_FLG_A.Group=group1UPDATE_FLG_A.Selected=YUPDATE_FLG_A.Action=onChangeRadioButtontLabel_1.Type=TLabeltLabel_1.X=15tLabel_1.Y=10tLabel_1.Width=72tLabel_1.Height=15tLabel_1.Text=申请状态:tLabel_1.Color=blue