## TBuilder Config File ## Title:体检综合报告## Company: ProperSoft## Author:WangLong 2013.02.16## version 1.0#<Type=TFrame>UI.Title=体检综合报告UI.MenuConfig=%ROOT%\config\hrm\HRMCheckReportMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.hrm.HRMCheckReportControlUI.item=tPanel_0;TABLEUI.layout=nullUI.TopMenu=YUI.TopToolBar=YUI.ShowMenu=YUI.ShowTitle=NtPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=0tPanel_0.Width=1014tPanel_0.Height=62tPanel_0.AutoWidth=YtPanel_0.Border=组|查询tPanel_0.Item=tLabel_1;COMPANY_CODE;tLabel_2;CONTRACT_CODE;tLabel_0;START_DATE;tLabel_3;END_DATEEND_DATE.Type=TTextFormatEND_DATE.X=223END_DATE.Y=22END_DATE.Width=99END_DATE.Height=23END_DATE.Text=END_DATE.Format=yyyy/MM/ddEND_DATE.FormatType=dateEND_DATE.HisOneNullRow=YEND_DATE.showDownButton=YEND_DATE.HorizontalAlignment=0tLabel_3.Type=TLabeltLabel_3.X=202tLabel_3.Y=27tLabel_3.Width=21tLabel_3.Height=15tLabel_3.Text=～START_DATE.Type=TTextFormatSTART_DATE.X=94START_DATE.Y=22START_DATE.Width=99START_DATE.Height=23START_DATE.Text=START_DATE.FormatType=dateSTART_DATE.showDownButton=YSTART_DATE.Format=yyyy/MM/ddSTART_DATE.HisOneNullRow=YSTART_DATE.HorizontalAlignment=0tLabel_0.Type=TLabeltLabel_0.X=27tLabel_0.Y=26tLabel_0.Width=62tLabel_0.Height=15tLabel_0.Text=报到日期tLabel_0.Color=blueCONTRACT_CODE.Type=健康检查合同下拉区域CONTRACT_CODE.X=705CONTRACT_CODE.Y=22CONTRACT_CODE.Width=190CONTRACT_CODE.Height=23CONTRACT_CODE.Text=CONTRACT_CODE.HorizontalAlignment=2CONTRACT_CODE.PopupMenuHeader=代码,100;名称,100CONTRACT_CODE.PopupMenuWidth=300CONTRACT_CODE.PopupMenuHeight=300CONTRACT_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1CONTRACT_CODE.FormatType=comboCONTRACT_CODE.ShowDownButton=YCONTRACT_CODE.Tip=健康检查合同CONTRACT_CODE.ShowColumnList=NAMECONTRACT_CODE.Action=CONTRACT_CODE.HisOneNullRow=YtLabel_2.Type=TLabeltLabel_2.X=640tLabel_2.Y=26tLabel_2.Width=62tLabel_2.Height=15tLabel_2.Text=合同名称tLabel_2.Color=蓝COMPANY_CODE.Type=TTextFormatCOMPANY_CODE.X=454COMPANY_CODE.Y=22COMPANY_CODE.Width=160COMPANY_CODE.Height=23COMPANY_CODE.Text=COMPANY_CODE.HorizontalAlignment=2COMPANY_CODE.FormatType=COMBOCOMPANY_CODE.showDownButton=YCOMPANY_CODE.HisOneNullRow=YCOMPANY_CODE.PopupMenuHeader=代码,100;名称,200COMPANY_CODE.InputPopupMenu=YCOMPANY_CODE.PopupMenuFilter=ID,1;NAME,3;PY1,1COMPANY_CODE.ShowColumnList=NAMECOMPANY_CODE.ValueColumn=IDCOMPANY_CODE.Action=onCompanyChooseCOMPANY_CODE.PopupMenuWidth=325COMPANY_CODE.PopupMenuHeight=300tLabel_1.Type=TLabeltLabel_1.X=388tLabel_1.Y=26tLabel_1.Width=62tLabel_1.Height=15tLabel_1.Text=团体名称tLabel_1.Color=蓝TABLE.Type=TTableTABLE.X=8TABLE.Y=66TABLE.Width=1011TABLE.Height=677TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoWidth=YTABLE.Header=序号,30;报到日期,90;部门,100;工号,60;姓名,90;性别,35,SEX_CODE;年龄,35;婚否,60,MARRIAGE_CODE;病案号,100;联系方式,120;既往史,70;吸烟史,60;饮酒,50;家族史,70;收缩压,100;舒张压,100;身高,50;体重,50;体重指数,70;外阴,70;阴道,70;宫颈,70;其他,100;裂隙灯,70;眼底,70;视力,100;矫正,100;色觉检查,100;鼻窦,70;咽部,70;外耳道,70;听力检查,100TABLE.Item=SEX_CODE;MARRIAGE_CODETABLE.ParmMap=SEQ_NO;REPORT_DATE;PAT_DEPT;STAFF_NO;PAT_NAME;SEX_CODE;AGE;MARRIAGE_CODE;MR_NO;TEL;IN_JWS;IN_XYS;IN_YJS;IN_JZS;IN_SSY;IN_SZY;OUT_SG;OUT_TZ;OUT_TZZS;WOMAN_WY;WOMAN_YD;WOMAN_GJ;WOMAN_QT;EYE_LXD;EYE_YD;EYE_SL;EYE_JZ;EYE_SJJC;FACE_BD;FACE_YB;FACE_WED;FACE_TLJCTABLE.LockColumns=allTABLE.ColumnHorizontalAlignmentData=0,right;2,left;3,left;4,left;6,right;7,left;8,left;9,left;10,left;11,left;12,left;13,left;14,left;15,left;16,left;17,left;18,left;19,left;20,left;21,left;22,left;23,left;24,left;25,left;26,left;27,left;28,left;29,left;30,left;31,leftTABLE.AutoHeight=YSEX_CODE.Type=性别下拉列表SEX_CODE.X=150SEX_CODE.Y=150SEX_CODE.Width=80SEX_CODE.Height=23SEX_CODE.Text=TButtonSEX_CODE.showID=YSEX_CODE.showName=YSEX_CODE.showText=NSEX_CODE.showValue=NSEX_CODE.showPy1=NSEX_CODE.showPy2=NSEX_CODE.Editable=YSEX_CODE.Tip=性别SEX_CODE.TableShowList=nameSEX_CODE.ModuleParmString=GROUP_ID:SYS_SEXSEX_CODE.ModuleParmTag=SEX_CODE.Enabled=YMARRIAGE_CODE.Type=TComboBoxMARRIAGE_CODE.X=230MARRIAGE_CODE.Y=180MARRIAGE_CODE.Width=80MARRIAGE_CODE.Height=23MARRIAGE_CODE.Text=TButtonMARRIAGE_CODE.showID=YMARRIAGE_CODE.Editable=YMARRIAGE_CODE.StringData=[[id,text],[1,未婚],[2,已婚],[3,已婚],[4,已婚],[9,]]MARRIAGE_CODE.ExpandWidth=40MARRIAGE_CODE.TableShowList=text