#  # Title: 门诊处方点评  #  # Description:门诊处方点评  #  # Copyright: Bluecore (c) 2012  #  # @author zhangp  # @version 1.0<Type=TFrame>UI.Title=门诊处方点评UI.MenuConfig=%ROOT%\config\pes\PESMainMenu.xUI.Width=1490UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.pes.PESMainControlUI.item=Panel1;tPanel_2;tPanel_3UI.layout=nullUI.AutoWidth=YUI.TopMenu=YUI.TopToolBar=YUI.X=5UI.AutoX=YtPanel_3.Type=TPaneltPanel_3.X=5tPanel_3.Y=416tPanel_3.Width=1480tPanel_3.Height=327tPanel_3.Border=组|药品列表tPanel_3.item=TABLE2tPanel_3.AutoWidth=YtPanel_3.AutoH=NtPanel_3.AutoHeight=YtPanel_3.AutoX=YtPanel_2.Type=TPaneltPanel_2.X=5tPanel_2.Y=64tPanel_2.Width=1480tPanel_2.Height=355tPanel_2.Border=组|处方列表tPanel_2.item=TABLE1tPanel_2.AutoW=NtPanel_2.AutoHeight=NtPanel_2.AutoWidth=YtPanel_2.AutoX=YPanel1.Type=TPanelPanel1.X=5Panel1.Y=5Panel1.Width=1480Panel1.Height=61Panel1.AutoX=YPanel1.AutoY=YPanel1.AutoWidth=YPanel1.Border=组|查询条件Panel1.Item=PES_TYPE;tLabel_0;tLabel_1;REGION_CODE;tLabel_2;EVAL_CODE;tLabel_10;tLabel_13;tLabel_16;点评期间下拉区域_0;PES_NO;DR_CODE;QUESTION_CODE;ROUTE_CODE;FREQ_CODE;tLabel_3tLabel_3.Type=TLabeltLabel_3.X=405tLabel_3.Y=24tLabel_3.Width=15tLabel_3.Height=15tLabel_3.Text=*tLabel_3.Color=redFREQ_CODE.Type=频次FREQ_CODE.X=1004FREQ_CODE.Y=5FREQ_CODE.Width=81FREQ_CODE.Height=23FREQ_CODE.Text=FREQ_CODE.HorizontalAlignment=2FREQ_CODE.PopupMenuHeader=代码,100;名称,100FREQ_CODE.PopupMenuWidth=300FREQ_CODE.PopupMenuHeight=300FREQ_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1FREQ_CODE.FormatType=comboFREQ_CODE.ShowDownButton=YFREQ_CODE.Tip=频次FREQ_CODE.ShowColumnList=NAMEFREQ_CODE.Visible=NROUTE_CODE.Type=用法下拉区域ROUTE_CODE.X=901ROUTE_CODE.Y=6ROUTE_CODE.Width=81ROUTE_CODE.Height=23ROUTE_CODE.Text=ROUTE_CODE.HorizontalAlignment=2ROUTE_CODE.PopupMenuHeader=代码,100;名称,100ROUTE_CODE.PopupMenuWidth=300ROUTE_CODE.PopupMenuHeight=300ROUTE_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1ROUTE_CODE.FormatType=comboROUTE_CODE.ShowDownButton=YROUTE_CODE.Tip=用法ROUTE_CODE.ShowColumnList=NAMEROUTE_CODE.Visible=NQUESTION_CODE.Type=处方点评问题代码下拉区域QUESTION_CODE.X=1033QUESTION_CODE.Y=94QUESTION_CODE.Width=81QUESTION_CODE.Height=23QUESTION_CODE.Text=QUESTION_CODE.HorizontalAlignment=2QUESTION_CODE.PopupMenuHeader=代码,50;名称,400;拼音,100QUESTION_CODE.PopupMenuWidth=450QUESTION_CODE.PopupMenuHeight=300QUESTION_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1QUESTION_CODE.FormatType=comboQUESTION_CODE.ShowDownButton=YQUESTION_CODE.Tip=问题代码QUESTION_CODE.ShowColumnList=NAMEQUESTION_CODE.HisOneNullRow=YQUESTION_CODE.Visible=YDR_CODE.Type=人员DR_CODE.X=901DR_CODE.Y=21DR_CODE.Width=81DR_CODE.Height=23DR_CODE.Text=DR_CODE.HorizontalAlignment=2DR_CODE.PopupMenuHeader=代码,100;名称,100DR_CODE.PopupMenuWidth=300DR_CODE.PopupMenuHeight=300DR_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1DR_CODE.FormatType=comboDR_CODE.ShowDownButton=YDR_CODE.Tip=人员DR_CODE.ShowColumnList=NAMEDR_CODE.ValueColumn=IDDR_CODE.Visible=NPES_NO.Type=点评期间下拉区域PES_NO.X=502PES_NO.Y=19PES_NO.Width=114PES_NO.Height=23PES_NO.Text=PES_NO.HorizontalAlignment=2PES_NO.PopupMenuHeader=点评期间,120PES_NO.PopupMenuWidth=300PES_NO.PopupMenuHeight=300PES_NO.PopupMenuFilter=ID,1PES_NO.FormatType=comboPES_NO.ShowDownButton=YPES_NO.Tip=点评期间PES_NO.ShowColumnList=ID;NAME;PY1PES_NO.HisOneNullRow=Y点评期间下拉区域_0.Type=点评期间下拉区域点评期间下拉区域_0.X=529点评期间下拉区域_0.Y=27tLabel_16.Type=TLabeltLabel_16.X=835tLabel_16.Y=22tLabel_16.Width=72tLabel_16.Height=15tLabel_16.Text=*tLabel_16.Color=redtLabel_13.Type=TLabeltLabel_13.X=620tLabel_13.Y=23tLabel_13.Width=15tLabel_13.Height=15tLabel_13.Text=*tLabel_13.Color=redtLabel_10.Type=TLabeltLabel_10.X=433tLabel_10.Y=24tLabel_10.Width=72tLabel_10.Height=15tLabel_10.Text=点评期间:tLabel_10.Color=蓝EVAL_CODE.Type=人员EVAL_CODE.X=719EVAL_CODE.Y=18EVAL_CODE.Width=110EVAL_CODE.Height=23EVAL_CODE.Text=EVAL_CODE.HorizontalAlignment=2EVAL_CODE.PopupMenuHeader=代码,100;名称,100EVAL_CODE.PopupMenuWidth=300EVAL_CODE.PopupMenuHeight=300EVAL_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1EVAL_CODE.FormatType=comboEVAL_CODE.ShowDownButton=YEVAL_CODE.Tip=人员EVAL_CODE.ShowColumnList=NAMEEVAL_CODE.HisOneNullRow=YEVAL_CODE.PosType=EVAL_CODE.Enabled=NtLabel_2.Type=TLabeltLabel_2.X=647tLabel_2.Y=23tLabel_2.Width=72tLabel_2.Height=15tLabel_2.Text=点评人员:tLabel_2.Color=blackREGION_CODE.Type=区域下拉列表REGION_CODE.X=81REGION_CODE.Y=21REGION_CODE.Width=112REGION_CODE.Height=23REGION_CODE.Text=TButtonREGION_CODE.showID=YREGION_CODE.showName=YREGION_CODE.showText=NREGION_CODE.showValue=NREGION_CODE.showPy1=YREGION_CODE.showPy2=YREGION_CODE.Editable=YREGION_CODE.Tip=区域REGION_CODE.TableShowList=nameREGION_CODE.ModuleParmString=REGION_CODE.ModuleParmTag=REGION_CODE.Enabled=NtLabel_1.Type=TLabeltLabel_1.X=16tLabel_1.Y=25tLabel_1.Width=72tLabel_1.Height=15tLabel_1.Text=区  域:tLabel_1.Color=bluetLabel_0.Type=TLabeltLabel_0.X=216tLabel_0.Y=24tLabel_0.Width=72tLabel_0.Height=15tLabel_0.Text=点评类别:tLabel_0.Color=bluePES_TYPE.Type=点评类别下拉列表PES_TYPE.X=287PES_TYPE.Y=20PES_TYPE.Width=113PES_TYPE.Height=23PES_TYPE.Text=TButtonPES_TYPE.showID=YPES_TYPE.showName=YPES_TYPE.showText=NPES_TYPE.showValue=NPES_TYPE.showPy1=YPES_TYPE.showPy2=YPES_TYPE.Editable=YPES_TYPE.Tip=点评类别PES_TYPE.TableShowList=namePES_TYPE.ModuleParmString=GROUP_ID:PES_EVA_TYPEPES_TYPE.ModuleParmTag=PES_TYPE.Visible=YPES_TYPE.Action=PES_TYPE.SelectedAction=STATUS.Type=票据状态下拉列表STATUS.X=236STATUS.Y=52STATUS.Width=81STATUS.Height=23STATUS.Text=TButtonSTATUS.showID=NSTATUS.showName=YSTATUS.showText=NSTATUS.showValue=NSTATUS.showPy1=YSTATUS.showPy2=YSTATUS.Editable=YSTATUS.Tip=票据状态下拉列表STATUS.TableShowList=nameSTATUS.ModuleParmString=GROUP_ID:BIL_INVOICE_STATUSSTATUS.ModuleParmTag=TABLE2.Type=TTableTABLE2.X=9TABLE2.Y=24TABLE2.Width=1458TABLE2.Height=292TABLE2.SpacingRow=1TABLE2.RowHeight=20TABLE2.Header=序号,30;组号,30;医嘱名称,200;用量,60;途径,80,ROUTE_CODE;频次,90,FREQ_CODE;天数,50;总量,50;总金额,60,double,########0.00;不合理,60,boolean;不合理代码,400,QUESTION_CODE;备注,200TABLE2.ParmMap=SEQ_NO;LINK_NO;ORDER_DESC;MEDI_QTY;ROUTE_CODE;FREQ_CODE;TAKE_DAYS;DOSAGE_QTY;AR_AMT;REASON_FLG;QUESTION_CODE;REMARK;PES_RX_NO;RX_NO;CASE_NOTABLE2.Enabled=YTABLE2.LockColumns=0,1,2,3,4,5,6,7,8TABLE2.HorizontalAlignmentData=TABLE2.ColumnHorizontalAlignmentData=2,left;3,right;4,left;5,left;6,left;7,left;8,right;10,left;11,leftTABLE2.Item=QUESTION_CODE;ROUTE_CODE;FREQ_CODETABLE2.AutoWidth=YTABLE2.AutoHeight=YTABLE2.AutoX=YTABLE2.AutoY=YTABLE1.Type=TTableTABLE1.X=20TABLE1.Y=24TABLE1.Width=1458TABLE1.Height=320TABLE1.SpacingRow=1TABLE1.RowHeight=20TABLE1.Header=序号,30;病案号,95;姓名,55;处方日期,100,Timestamp,yyyy/MM/dd HH:mm:ss;处方号,90;年龄,40;诊断,150;处方药品数量,80;抗菌药品数量,80;注射药品数量,80;国家基本药品,80;药品通用名数量,90;处方金额,80,double,########0.00;医生,60,DR_CODE;配药药师,60,DR_CODE;发药药师,60,DR_CODE;不合理,60,boolean;不合理代码,400,QUESTION_CODE;备注,200TABLE1.AutoWidth=YTABLE1.ParmMap=SEQ;MR_NO;PAT_NAME;ORDER_DATE;RX_NO;AGE;ICD_CHN_DESC;ORDER_QTY;ANTIBIOTIC_QTY;INJECT_QTY;BASE_QTY;GOODS_QTY;RX_TOTAL;DR_CODE;PHA_DOSAGE_CODE;PHA_DISPENSE_CODE;REASON_FLG;QUESTION_CODE;REMARKTABLE1.Item=DR_CODE;QUESTION_CODETABLE1.LockColumns=0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15TABLE1.ColumnHorizontalAlignmentData=1,left;2,left;3,left;4,left;5,left;6,left;7,right;8,right;9,right;10,right;11,right;12,right;13,left;14,left;15,left;17,left;18,left;TABLE1.AutoHeight=YTABLE1.ClickedAction=onClickTableMTABLE1.AutoX=YTABLE1.AutoY=YTABLE1.ColumnChangeAction=