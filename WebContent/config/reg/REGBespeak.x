############################################## <p>Title:预约挂号 </p>## <p>Description:预约挂号 </p>## <p>Copyright: Copyright (c) 2011</p>## <p>Company: bluecore</p>## @author pangben 2012-03-27# @version 4.0#############################################<Type=TFrame>UI.Title=预约挂号UI.MenuConfig=%ROOT%\config\reg\REGBespeakMenu.xUI.Width=800UI.Height=519UI.toolbar=YUI.controlclassname=com.javahis.ui.reg.REGBespeakControlUI.Item=tPanel_0UI.ShowMenu=YUI.TopMenu=YUI.TopToolBar=YtPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=5tPanel_0.Width=790tPanel_0.Height=509tPanel_0.AutoX=YtPanel_0.AutoY=YtPanel_0.AutoWidth=YtPanel_0.AutoHeight=YtPanel_0.Border=凸tPanel_0.Item=tLabel_0;ADM_DATE;tLabel_1;SESSION_CODE;tLabel_2;tLabel_3;tLabel_4;tLabel_5;MR_NO;tLabel_6;PAT_NAME;tLabel_7;SEX_CODE;tLabel_9;CTZ1_CODE;SERVICE_LEVEL;tLabel_10;tLabel_13;CTZ2_CODE;tLabel_14;BIRTHDAY;ERD_LEVEL_TITLE;ERD_LEVEL;tLabel_64;REGION_CODE;DEPT_CODE;DR_CODE;tLabel_63;CTZ3_CODE;CLINICROOM_NO;tLabel_66;REGMETHOD_CODE;TABLE;HB;ZS;YS;KSKS.Type=科室下拉列表KS.X=31KS.Y=186KS.Width=81KS.Height=23KS.Text=TButtonKS.showID=YKS.showName=YKS.showText=NKS.showValue=NKS.showPy1=NKS.showPy2=NKS.Editable=YKS.Tip=科室KS.TableShowList=nameYS.Type=门诊适用人员YS.X=226YS.Y=203YS.Width=81YS.Height=23YS.Text=YS.HorizontalAlignment=2YS.PopupMenuHeader=代码,100;名称,100YS.PopupMenuWidth=300YS.PopupMenuHeight=300YS.PopupMenuFilter=ID,1;NAME,1;PY1,1YS.FormatType=comboYS.ShowDownButton=YYS.Tip=门诊适用人员YS.ShowColumnList=NAMEZS.Type=门诊适用诊室ZS.X=41ZS.Y=178ZS.Width=81ZS.Height=23ZS.Text=ZS.HorizontalAlignment=2ZS.PopupMenuHeader=代码,100;名称,100ZS.PopupMenuWidth=300ZS.PopupMenuHeight=300ZS.PopupMenuFilter=ID,1;NAME,1;PY1,1ZS.FormatType=comboZS.ShowDownButton=YZS.Tip=门诊适用诊室ZS.ShowColumnList=NAMEZS.HisOneNullRow=YHB.Type=号别HB.X=22HB.Y=188HB.Width=81HB.Height=23HB.Text=HB.HorizontalAlignment=2HB.PopupMenuHeader=代码,100;名称,100HB.PopupMenuWidth=300HB.PopupMenuHeight=300HB.PopupMenuFilter=ID,1;NAME,1;PY1,1HB.FormatType=comboHB.ShowDownButton=YHB.Tip=号别HB.ShowColumnList=NAMEHB.HisOneNullRow=YTABLE.Type=TTableTABLE.X=8TABLE.Y=176TABLE.Width=770TABLE.Height=326TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoWidth=YTABLE.AutoHeight=YTABLE.Header=类别,80;号别,80,HB;科别,100,KS;诊室,100,ZS;医生,80,YS;已挂人数,70;限挂人数,70TABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,right;6,rightTABLE.ParmMap=TYPE;CLINICTYPE_CODE;DEPT_CODE;CLINICROOM_NO;DR_CODE;QUE_NO;MAX_QUETABLE.Item=HB;KS;ZS;YSTABLE.ClickedAction=onTableClickedTABLE.LockColumns=allREGMETHOD_CODE.Type=挂号方式下拉列表REGMETHOD_CODE.X=642REGMETHOD_CODE.Y=141REGMETHOD_CODE.Width=121REGMETHOD_CODE.Height=23REGMETHOD_CODE.Text=TButtonREGMETHOD_CODE.showID=YREGMETHOD_CODE.showName=YREGMETHOD_CODE.showText=NREGMETHOD_CODE.showValue=NREGMETHOD_CODE.showPy1=NREGMETHOD_CODE.showPy2=NREGMETHOD_CODE.Editable=YREGMETHOD_CODE.Tip=挂号方式REGMETHOD_CODE.TableShowList=nameREGMETHOD_CODE.ModuleParmTag=tLabel_66.Type=TLabeltLabel_66.X=580tLabel_66.Y=145tLabel_66.Width=59tLabel_66.Height=15tLabel_66.Text=挂号方式CLINICROOM_NO.Type=门诊适用诊室CLINICROOM_NO.X=451CLINICROOM_NO.Y=104CLINICROOM_NO.Width=118CLINICROOM_NO.Height=23CLINICROOM_NO.Text=CLINICROOM_NO.HorizontalAlignment=2CLINICROOM_NO.PopupMenuHeader=代码,100;名称,100CLINICROOM_NO.PopupMenuWidth=300CLINICROOM_NO.PopupMenuHeight=300CLINICROOM_NO.PopupMenuFilter=ID,1;NAME,1;PY1,1CLINICROOM_NO.FormatType=comboCLINICROOM_NO.ShowDownButton=YCLINICROOM_NO.Tip=门诊适用诊室CLINICROOM_NO.ShowColumnList=NAMECLINICROOM_NO.RegionCode=<REGION_CODE>CLINICROOM_NO.AdmType=<ADM_TYPE>CLINICROOM_NO.AdmDate=<ADM_DATE>CLINICROOM_NO.SessionCode=<SESSION_CODE>CLINICROOM_NO.HisOneNullRow=YCLINICROOM_NO.Enabled=NCTZ3_CODE.Type=身份折扣下拉列表CTZ3_CODE.X=450CTZ3_CODE.Y=141CTZ3_CODE.Width=119CTZ3_CODE.Height=23CTZ3_CODE.Text=TButtonCTZ3_CODE.showID=YCTZ3_CODE.showName=YCTZ3_CODE.showText=NCTZ3_CODE.showValue=NCTZ3_CODE.showPy1=NCTZ3_CODE.showPy2=NCTZ3_CODE.Editable=YCTZ3_CODE.Tip=身份CTZ3_CODE.TableShowList=nametLabel_63.Type=TLabeltLabel_63.X=400tLabel_63.Y=146tLabel_63.Width=47tLabel_63.Height=15tLabel_63.Text=身份三DR_CODE.Type=门诊适用人员DR_CODE.X=259DR_CODE.Y=105DR_CODE.Width=119DR_CODE.Height=23DR_CODE.Text=DR_CODE.HorizontalAlignment=2DR_CODE.PopupMenuHeader=代码,100;名称,100DR_CODE.PopupMenuWidth=300DR_CODE.PopupMenuHeight=300DR_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1DR_CODE.FormatType=comboDR_CODE.ShowDownButton=YDR_CODE.Tip=门诊适用人员DR_CODE.ShowColumnList=NAMEDR_CODE.HisOneNullRow=YDR_CODE.RegionCode=<REGION_CODE>DR_CODE.AdmType=<ADM_TYPE>DR_CODE.AdmDate=<ADM_DATE>DR_CODE.SessionCode=<SESSION_CODE>DR_CODE.DeptCode=<DEPT_CODE>DR_CODE.Enabled=NDEPT_CODE.Type=门诊适用科室DEPT_CODE.X=71DEPT_CODE.Y=105DEPT_CODE.Width=117DEPT_CODE.Height=23DEPT_CODE.Text=DEPT_CODE.HorizontalAlignment=2DEPT_CODE.PopupMenuHeader=代码,100;名称,100DEPT_CODE.PopupMenuWidth=300DEPT_CODE.PopupMenuHeight=300DEPT_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1DEPT_CODE.FormatType=comboDEPT_CODE.ShowDownButton=YDEPT_CODE.Tip=门诊适用科室DEPT_CODE.ShowColumnList=NAMEDEPT_CODE.HisOneNullRow=YDEPT_CODE.RegionCode=<REGION_CODE>DEPT_CODE.AdmType=<ADM_TYPE>DEPT_CODE.AdmDate=<ADM_DATE>DEPT_CODE.SessionCode=<SESSION_CODE>DEPT_CODE.Action=DR_CODE|onQuery;CLINICROOM_NO|onQueryDEPT_CODE.Enabled=NREGION_CODE.Type=区域下拉列表REGION_CODE.X=71REGION_CODE.Y=28REGION_CODE.Width=118REGION_CODE.Height=23REGION_CODE.Text=TButtonREGION_CODE.showID=YREGION_CODE.showName=YREGION_CODE.showText=NREGION_CODE.showValue=NREGION_CODE.showPy1=NREGION_CODE.showPy2=NREGION_CODE.Editable=YREGION_CODE.Tip=区域REGION_CODE.TableShowList=nameREGION_CODE.ModuleParmString=REGION_CODE.ModuleParmTag=REGION_CODE.ExpandWidth=80tLabel_64.Type=TLabeltLabel_64.X=19tLabel_64.Y=32tLabel_64.Width=47tLabel_64.Height=15tLabel_64.Text=院  区ERD_LEVEL.Type=检伤等级下拉列表ERD_LEVEL.X=643ERD_LEVEL.Y=28ERD_LEVEL.Width=120ERD_LEVEL.Height=23ERD_LEVEL.Text=TButtonERD_LEVEL.showID=YERD_LEVEL.showName=YERD_LEVEL.showText=NERD_LEVEL.showValue=NERD_LEVEL.showPy1=NERD_LEVEL.showPy2=NERD_LEVEL.Editable=YERD_LEVEL.Tip=检伤等级ERD_LEVEL.TableShowList=nameERD_LEVEL.Visible=NERD_LEVEL_TITLE.Type=TLabelERD_LEVEL_TITLE.X=581ERD_LEVEL_TITLE.Y=32ERD_LEVEL_TITLE.Width=59ERD_LEVEL_TITLE.Height=15ERD_LEVEL_TITLE.Text=急诊检伤ERD_LEVEL_TITLE.Visible=NBIRTHDAY.Type=TTextFormatBIRTHDAY.X=643BIRTHDAY.Y=67BIRTHDAY.Width=120BIRTHDAY.Height=22BIRTHDAY.Text=BIRTHDAY.FormatType=dateBIRTHDAY.Format=yyyy/MM/ddBIRTHDAY.showDownButton=YBIRTHDAY.Enabled=NtLabel_14.Type=TLabeltLabel_14.X=583tLabel_14.Y=71tLabel_14.Width=63tLabel_14.Height=15tLabel_14.Text=出生日期CTZ2_CODE.Type=身份折扣下拉列表CTZ2_CODE.X=259CTZ2_CODE.Y=142CTZ2_CODE.Width=120CTZ2_CODE.Height=22CTZ2_CODE.Text=TButtonCTZ2_CODE.showID=YCTZ2_CODE.showName=YCTZ2_CODE.showText=NCTZ2_CODE.showValue=NCTZ2_CODE.showPy1=NCTZ2_CODE.showPy2=NCTZ2_CODE.Editable=YCTZ2_CODE.Tip=主身份CTZ2_CODE.TableShowList=nametLabel_13.Type=TLabeltLabel_13.X=209tLabel_13.Y=146tLabel_13.Width=46tLabel_13.Height=15tLabel_13.Text=身份二tLabel_10.Type=TLabeltLabel_10.X=580tLabel_10.Y=110tLabel_10.Width=60tLabel_10.Height=15tLabel_10.Text=服务等级SERVICE_LEVEL.Type=服务等级下拉区域SERVICE_LEVEL.X=643SERVICE_LEVEL.Y=106SERVICE_LEVEL.Width=120SERVICE_LEVEL.Height=22SERVICE_LEVEL.Text=SERVICE_LEVEL.HorizontalAlignment=2SERVICE_LEVEL.PopupMenuHeader=代码,100;名称,100SERVICE_LEVEL.PopupMenuWidth=300SERVICE_LEVEL.PopupMenuHeight=300SERVICE_LEVEL.PopupMenuFilter=ID,1;NAME,1;PY1,1SERVICE_LEVEL.FormatType=comboSERVICE_LEVEL.ShowDownButton=YSERVICE_LEVEL.Tip=服务等级SERVICE_LEVEL.ShowColumnList=NAMESERVICE_LEVEL.HisOneNullRow=YSERVICE_LEVEL.Action=onClickClinicTypeCTZ1_CODE.Type=身份折扣下拉列表CTZ1_CODE.X=71CTZ1_CODE.Y=142CTZ1_CODE.Width=116CTZ1_CODE.Height=22CTZ1_CODE.Text=TButtonCTZ1_CODE.showID=YCTZ1_CODE.showName=YCTZ1_CODE.showText=NCTZ1_CODE.showValue=NCTZ1_CODE.showPy1=YCTZ1_CODE.showPy2=YCTZ1_CODE.Editable=YCTZ1_CODE.Tip=主身份CTZ1_CODE.TableShowList=nameCTZ1_CODE.Action=CTZ1_CODE.SelectedAction=onClickClinicTypetLabel_9.Type=TLabeltLabel_9.X=19tLabel_9.Y=146tLabel_9.Width=48tLabel_9.Height=15tLabel_9.Text=身份一SEX_CODE.Type=性别下拉列表SEX_CODE.X=450SEX_CODE.Y=67SEX_CODE.Width=120SEX_CODE.Height=22SEX_CODE.Text=TButtonSEX_CODE.showID=YSEX_CODE.showName=YSEX_CODE.showText=NSEX_CODE.showValue=NSEX_CODE.showPy1=NSEX_CODE.showPy2=NSEX_CODE.Editable=YSEX_CODE.Tip=性别SEX_CODE.TableShowList=nameSEX_CODE.ModuleParmString=GROUP_ID:SYS_SEXSEX_CODE.ModuleParmTag=SEX_CODE.Enabled=NtLabel_7.Type=TLabeltLabel_7.X=401tLabel_7.Y=71tLabel_7.Width=46tLabel_7.Height=15tLabel_7.Text=性  别PAT_NAME.Type=TTextFieldPAT_NAME.X=259PAT_NAME.Y=67PAT_NAME.Width=120PAT_NAME.Height=22PAT_NAME.Text=PAT_NAME.Enabled=NtLabel_6.Type=TLabeltLabel_6.X=209tLabel_6.Y=71tLabel_6.Width=47tLabel_6.Height=15tLabel_6.Text=姓  名MR_NO.Type=TTextFieldMR_NO.X=71MR_NO.Y=67MR_NO.Width=117MR_NO.Height=22MR_NO.Text=MR_NO.Enabled=NMR_NO.Action=onMrNotLabel_5.Type=TLabeltLabel_5.X=19tLabel_5.Y=71tLabel_5.Width=48tLabel_5.Height=15tLabel_5.Text=病案号tLabel_4.Type=TLabeltLabel_4.X=401tLabel_4.Y=109tLabel_4.Width=46tLabel_4.Height=15tLabel_4.Text=诊  室tLabel_3.Type=TLabeltLabel_3.X=209tLabel_3.Y=109tLabel_3.Width=46tLabel_3.Height=15tLabel_3.Text=医  生tLabel_2.Type=TLabeltLabel_2.X=19tLabel_2.Y=109tLabel_2.Width=45tLabel_2.Height=15tLabel_2.Text=科  别SESSION_CODE.Type=时段下拉列表SESSION_CODE.X=450SESSION_CODE.Y=28SESSION_CODE.Width=120SESSION_CODE.Height=22SESSION_CODE.Text=TButtonSESSION_CODE.showID=YSESSION_CODE.showName=YSESSION_CODE.showText=NSESSION_CODE.showValue=NSESSION_CODE.showPy1=NSESSION_CODE.showPy2=NSESSION_CODE.Editable=YSESSION_CODE.Tip=时段SESSION_CODE.TableShowList=nameSESSION_CODE.ModuleParmString=SESSION_CODE.ModuleParmTag=SESSION_CODE.AdmType=SESSION_CODE.Enabled=NtLabel_1.Type=TLabeltLabel_1.X=401tLabel_1.Y=32tLabel_1.Width=49tLabel_1.Height=15tLabel_1.Text=时  段ADM_DATE.Type=TTextFormatADM_DATE.X=259ADM_DATE.Y=28ADM_DATE.Width=120ADM_DATE.Height=22ADM_DATE.Text=ADM_DATE.FormatType=dateADM_DATE.showDownButton=YADM_DATE.Format=yyyy/MM/ddADM_DATE.HorizontalAlignment=2ADM_DATE.Enabled=NtLabel_0.Type=TLabeltLabel_0.X=209tLabel_0.Y=32tLabel_0.Width=49tLabel_0.Height=15tLabel_0.Text=日  期