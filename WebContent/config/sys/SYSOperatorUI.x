 #  # Title: 用户管理  #  # Description: 用户管理  #  # Copyright: JavaHis (c) 2009  #  # @author zhangy 2009.04.22 # @version 1.0<Type=TFrame>UI.Title=用户管理UI.MenuConfig=%ROOT%\config\sys\SYSOperatorUI_Menu.xUI.Width=1142UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.sys.SYSOperatorControlUI.item=tPanel_4;tPanel_5;tTabbedPane_0;TABLEUI.layout=nullUI.Y=3UI.X=7UI.TopToolBar=YUI.TopMenu=YUI.ShowTitle=NUI.ShowMenu=NUI.FocusList=USER_ID;USER_NAME;PY1;PY2;USER_ENG_NAME;FOREIGNER_FLG;ID_NO;SEX_CODE;DESCRIPTION;REGION_CODE;POS_CODE;FULLTIME_FLG;CTRL_FLG;E_MAIL;USER_PASSWORD;UKEY_FLG;SEQ;ABNORMAL_TIMES;ROLE_ID;ACTIVE_DATE;END_DATE;PUB_FUNCTIONTABLE.Type=TTableTABLE.X=12TABLE.Y=460TABLE.Width=1130TABLE.Height=152TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoX=YTABLE.AutoWidth=NTABLE.Header=使用者,120;姓名,120;性别,60,SEX_CODE;区域,100,REGION_CODE;职称,100,POS_CODE;角色,100,ROLE_ID;操作人员,100;操作日期,100;操作IP,100TABLE.SQL=TABLE.ParmMap=USER_ID;USER_NAME;SEX_CODE;REGION_CODE;POS_CODE;ROLE_ID;OPT_USER;OPT_DATE;OPT_TERM;PWD_OBATEDATETABLE.LockColumns=0,1,2,3,4,5,6,7,8TABLE.Item=SEX_CODE;REGION_CODE;POS_CODE;ROLE_ID;EMR_RULE_CODETABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,leftTABLE.ModifyTag=TABLE.ClickedAction=onTableClickedTABLE.AutoModifyDataStore=YtTabbedPane_0.Type=TTabbedPanetTabbedPane_0.X=14tTabbedPane_0.Y=280tTabbedPane_0.Width=1132tTabbedPane_0.Height=175tTabbedPane_0.AutoSize=5tTabbedPane_0.AutoX=YtTabbedPane_0.AutoWidth=YtTabbedPane_0.Item=tPanel_6;tPanel_8;tPanel_7tTabbedPane_0.Name=tPanel_8.Type=TPaneltPanel_8.X=155tPanel_8.Y=7tPanel_8.Width=81tPanel_8.Height=81tPanel_8.Name=诊区/病区信息tPanel_8.Item=OPERATOR_CLINIC;tLabel_1;OPT_CLINICAREA_TABLE;Insert_CLINICAREA;Remove_CLINICAREA;tLabel_2;OPT_STATION_TABLE;Insert_STATION;Remove_STATION;OPERATOR_STATIONOPERATOR_STATION.Type=病区OPERATOR_STATION.X=464OPERATOR_STATION.Y=6OPERATOR_STATION.Width=135OPERATOR_STATION.Height=23OPERATOR_STATION.Text=OPERATOR_STATION.HorizontalAlignment=2OPERATOR_STATION.PopupMenuHeader=代码,100;名称,100OPERATOR_STATION.PopupMenuWidth=300OPERATOR_STATION.PopupMenuHeight=300OPERATOR_STATION.PopupMenuFilter=ID,1;NAME,1;PY1,1OPERATOR_STATION.FormatType=comboOPERATOR_STATION.ShowDownButton=YOPERATOR_STATION.Tip=病区OPERATOR_STATION.ShowColumnList=NAMEOPERATOR_STATION.HisOneNullRow=YRemove_STATION.Type=TButtonRemove_STATION.X=678Remove_STATION.Y=6Remove_STATION.Width=65Remove_STATION.Height=23Remove_STATION.Text=移除Remove_STATION.Action=onRemoveStationClickedInsert_STATION.Type=TButtonInsert_STATION.X=611Insert_STATION.Y=6Insert_STATION.Width=62Insert_STATION.Height=23Insert_STATION.Text=添加Insert_STATION.Action=onInsertStationClickedOPT_STATION_TABLE.Type=TTableOPT_STATION_TABLE.X=400OPT_STATION_TABLE.Y=37OPT_STATION_TABLE.Width=282OPT_STATION_TABLE.Height=118OPT_STATION_TABLE.SpacingRow=1OPT_STATION_TABLE.RowHeight=20OPT_STATION_TABLE.Header=主,50,boolean;名称,200,OPERATOR_STATIONOPT_STATION_TABLE.LockColumns=1OPT_STATION_TABLE.ColumnHorizontalAlignmentData=1,leftOPT_STATION_TABLE.Item=OPERATOR_STATIONOPT_STATION_TABLE.ParmMap=MAIN_FLG;STATION_CLINIC_CODEOPT_STATION_TABLE.AutoModifyDataStore=YtLabel_2.Type=TLabeltLabel_2.X=400tLabel_2.Y=10tLabel_2.Width=72tLabel_2.Height=15tLabel_2.Text=病  区:Remove_CLINICAREA.Type=TButtonRemove_CLINICAREA.X=286Remove_CLINICAREA.Y=6Remove_CLINICAREA.Width=64Remove_CLINICAREA.Height=23Remove_CLINICAREA.Text=移除Remove_CLINICAREA.Action=onRemoveClinicareaClickedInsert_CLINICAREA.Type=TButtonInsert_CLINICAREA.X=214Insert_CLINICAREA.Y=6Insert_CLINICAREA.Width=65Insert_CLINICAREA.Height=23Insert_CLINICAREA.Text=添加Insert_CLINICAREA.Action=onInsertClinicareaClickedOPT_CLINICAREA_TABLE.Type=TTableOPT_CLINICAREA_TABLE.X=18OPT_CLINICAREA_TABLE.Y=37OPT_CLINICAREA_TABLE.Width=273OPT_CLINICAREA_TABLE.Height=118OPT_CLINICAREA_TABLE.SpacingRow=1OPT_CLINICAREA_TABLE.RowHeight=20OPT_CLINICAREA_TABLE.Header=主,50,boolean;名称,200,OPERATOR_CLINICOPT_CLINICAREA_TABLE.Item=OPERATOR_CLINICOPT_CLINICAREA_TABLE.ColumnHorizontalAlignmentData=1,leftOPT_CLINICAREA_TABLE.ParmMap=MAIN_FLG;STATION_CLINIC_CODEOPT_CLINICAREA_TABLE.AutoModifyDataStore=YOPT_CLINICAREA_TABLE.LockColumns=1tLabel_1.Type=TLabeltLabel_1.X=21tLabel_1.Y=10tLabel_1.Width=58tLabel_1.Height=15tLabel_1.Text=诊  区:OPERATOR_CLINIC.Type=诊区OPERATOR_CLINIC.X=79OPERATOR_CLINIC.Y=6OPERATOR_CLINIC.Width=121OPERATOR_CLINIC.Height=23OPERATOR_CLINIC.Text=OPERATOR_CLINIC.HorizontalAlignment=2OPERATOR_CLINIC.PopupMenuHeader=代码,100;名称,100OPERATOR_CLINIC.PopupMenuWidth=300OPERATOR_CLINIC.PopupMenuHeight=300OPERATOR_CLINIC.PopupMenuFilter=ID,1;NAME,1;PY1,1OPERATOR_CLINIC.FormatType=comboOPERATOR_CLINIC.ShowDownButton=YOPERATOR_CLINIC.Tip=诊区OPERATOR_CLINIC.ShowColumnList=NAMEOPERATOR_CLINIC.HisOneNullRow=NOPERATOR_CLINIC.hasOperator=NtPanel_7.Type=TPaneltPanel_7.X=52tPanel_7.Y=11tPanel_7.Width=81tPanel_7.Height=81tPanel_7.Name=证照信息tPanel_7.Item=tLabel_6;tLabel_7;tLabel_19;tLabel_20;TABLELISCENSE;LCS_CLASS_CODE;ADD_LCS;DELETE_LCS;LCS_NO;EFF_LCS_DATE;END_LCS_DATE;UPDATE_LCStPanel_7.FocusList=LCS_CLASS_CODE;LCS_NO;EFF_LCS_DATE;END_LCS_DATEUPDATE_LCS.Type=TButtonUPDATE_LCS.X=101UPDATE_LCS.Y=128UPDATE_LCS.Width=60UPDATE_LCS.Height=23UPDATE_LCS.Text=修改UPDATE_LCS.Action=onUpdateLiscenseClickedEND_LCS_DATE.Type=TTextFormatEND_LCS_DATE.X=102END_LCS_DATE.Y=99END_LCS_DATE.Width=100END_LCS_DATE.Height=20END_LCS_DATE.Text=END_LCS_DATE.showDownButton=YEND_LCS_DATE.Format=yyyy/MM/ddEND_LCS_DATE.FormatType=dateEFF_LCS_DATE.Type=TTextFormatEFF_LCS_DATE.X=102EFF_LCS_DATE.Y=70EFF_LCS_DATE.Width=100EFF_LCS_DATE.Height=20EFF_LCS_DATE.Text=EFF_LCS_DATE.showDownButton=YEFF_LCS_DATE.FormatType=dateEFF_LCS_DATE.Format=yyyy/MM/ddEFF_LCS_DATE.Action=onEffLCSDateActionLCS_NO.Type=TTextFieldLCS_NO.X=102LCS_NO.Y=39LCS_NO.Width=140LCS_NO.Height=20LCS_NO.Text=DELETE_LCS.Type=TButtonDELETE_LCS.X=182DELETE_LCS.Y=128DELETE_LCS.Width=60DELETE_LCS.Height=23DELETE_LCS.Text=删除DELETE_LCS.Enabled=YDELETE_LCS.Action=onRemoveLiscenseClickedADD_LCS.Type=TButtonADD_LCS.X=19ADD_LCS.Y=128ADD_LCS.Width=60ADD_LCS.Height=23ADD_LCS.Text=添加ADD_LCS.Enabled=YADD_LCS.Action=onInsertLiscenseClickedLCS_CLASS_CODE.Type=证照类别下拉列表LCS_CLASS_CODE.X=102LCS_CLASS_CODE.Y=8LCS_CLASS_CODE.Width=140LCS_CLASS_CODE.Height=23LCS_CLASS_CODE.Text=TButtonLCS_CLASS_CODE.showID=YLCS_CLASS_CODE.showName=YLCS_CLASS_CODE.showText=NLCS_CLASS_CODE.showValue=NLCS_CLASS_CODE.showPy1=YLCS_CLASS_CODE.showPy2=YLCS_CLASS_CODE.Editable=YLCS_CLASS_CODE.Tip=证照类别下拉列表LCS_CLASS_CODE.TableShowList=nameLCS_CLASS_CODE.ModuleParmString=GROUP_ID:SYS_LICENSELCS_CLASS_CODE.ModuleParmTag=TABLELISCENSE.Type=TTableTABLELISCENSE.X=285TABLELISCENSE.Y=12TABLELISCENSE.Width=500TABLELISCENSE.Height=133TABLELISCENSE.SpacingRow=1TABLELISCENSE.RowHeight=20TABLELISCENSE.Header=证照类别,125,LCS_CLASS_CODE;证照号码,170;证照起日,100,Timestamp;证照讫日,100,TimestampTABLELISCENSE.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,leftTABLELISCENSE.Item=LCS_CLASS_CODETABLELISCENSE.ParmMap=LCS_CLASS_CODE;LCS_NO;EFF_LCS_DATE;END_LCS_DATETABLELISCENSE.ClickedAction=onTableLiscenseClickedTABLELISCENSE.LockColumns=0,1,2,3TABLELISCENSE.AutoModifyObject=NTABLELISCENSE.AutoModifyDataStore=YtLabel_20.Type=TLabeltLabel_20.X=19tLabel_20.Y=103tLabel_20.Width=72tLabel_20.Height=15tLabel_20.Text=证照讫日：tLabel_19.Type=TLabeltLabel_19.X=19tLabel_19.Y=73tLabel_19.Width=72tLabel_19.Height=15tLabel_19.Text=证照起日：tLabel_7.Type=TLabeltLabel_7.X=19tLabel_7.Y=42tLabel_7.Width=72tLabel_7.Height=15tLabel_7.Text=证照号码：tLabel_6.Type=TLabeltLabel_6.X=19tLabel_6.Y=13tLabel_6.Width=72tLabel_6.Height=15tLabel_6.Text=证照类别：tPanel_6.Type=TPaneltPanel_6.X=39tPanel_6.Y=22tPanel_6.Width=81tPanel_6.Height=81tPanel_6.Text=tPanel_6.Name=科室信息tPanel_6.Item=tLabel_5;TABLEDEPT;INSERT_DEPT;REMOVE_DEPT;DEPT_CODE;tLabel_8;COST_CENTER_CODECOST_CENTER_CODE.Type=成本中心下拉区域COST_CENTER_CODE.X=474COST_CENTER_CODE.Y=6COST_CENTER_CODE.Width=132COST_CENTER_CODE.Height=23COST_CENTER_CODE.Text=COST_CENTER_CODE.HorizontalAlignment=2COST_CENTER_CODE.PopupMenuHeader=代码,100;名称,100COST_CENTER_CODE.PopupMenuWidth=300COST_CENTER_CODE.PopupMenuHeight=300COST_CENTER_CODE.FormatType=comboCOST_CENTER_CODE.ShowDownButton=YCOST_CENTER_CODE.Tip=成本中心COST_CENTER_CODE.ShowColumnList=NAMECOST_CENTER_CODE.HisOneNullRow=YtLabel_8.Type=TLabeltLabel_8.X=399tLabel_8.Y=10tLabel_8.Width=72tLabel_8.Height=15tLabel_8.Text=成本中心:DEPT_CODE.Type=科室DEPT_CODE.X=68DEPT_CODE.Y=6DEPT_CODE.Width=145DEPT_CODE.Height=23DEPT_CODE.Text=DEPT_CODE.HorizontalAlignment=2DEPT_CODE.PopupMenuHeader=代码,100;名称,100DEPT_CODE.PopupMenuWidth=300DEPT_CODE.PopupMenuHeight=300DEPT_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1DEPT_CODE.FormatType=comboDEPT_CODE.ShowDownButton=YDEPT_CODE.Tip=科室DEPT_CODE.ShowColumnList=NAMEDEPT_CODE.HisOneNullRow=YDEPT_CODE.Action=onComboBoxSelectedREMOVE_DEPT.Type=TButtonREMOVE_DEPT.X=304REMOVE_DEPT.Y=6REMOVE_DEPT.Width=68REMOVE_DEPT.Height=23REMOVE_DEPT.Text=移除REMOVE_DEPT.Enabled=YREMOVE_DEPT.Action=onRemoveDeptClickedINSERT_DEPT.Type=TButtonINSERT_DEPT.X=226INSERT_DEPT.Y=6INSERT_DEPT.Width=68INSERT_DEPT.Height=23INSERT_DEPT.Text=添加INSERT_DEPT.Action=onInsertDeptClickedINSERT_DEPT.Enabled=YTABLEDEPT.Type=TTableTABLEDEPT.X=17TABLEDEPT.Y=37TABLEDEPT.Width=317TABLEDEPT.Height=106TABLEDEPT.SpacingRow=1TABLEDEPT.RowHeight=20TABLEDEPT.Header=主,50,boolean;科室,250,DEPT_CODETABLEDEPT.ParmMap=MAIN_FLG;DEPT_CODETABLEDEPT.Item=DEPT_CODETABLEDEPT.ColumnHorizontalAlignmentData=1,leftTABLEDEPT.LockColumns=1TABLEDEPT.AutoModifyObject=NTABLEDEPT.AutoModifyDataStore=YtLabel_5.Type=TLabeltLabel_5.X=19tLabel_5.Y=11tLabel_5.Width=45tLabel_5.Height=15tLabel_5.Text=科室：tLabel_5.Color=蓝tPanel_5.Type=TPaneltPanel_5.X=4tPanel_5.Y=171tPanel_5.Width=1132tPanel_5.Height=112tPanel_5.AutoX=YtPanel_5.AutoWidth=YtPanel_5.Border=组|权限信息tPanel_5.ControlClassName=tPanel_5.AutoSize=5tPanel_5.Item=tLabel_46;tLabel_47;tLabel_48;tLabel_49;PUB_FUNCTION;tLabel_50;tLabel_51;tLabel_52;RCNT_IP;RCNT_LOGIN_DATE;RCNT_LOGOUT_DATE;ACTIVE_DATE;ROLE_ID;tLabel_11;tLabel_12;START_DATE;tLabel_13;EMR_RULE_CODE;END_DATE_RULE;END_DATE;tLabel_14;tLabel_15;tLabel_16;T_DC;tLabel_17tLabel_17.Type=TLabeltLabel_17.X=696tLabel_17.Y=86tLabel_17.Width=98tLabel_17.Height=15tLabel_17.Text=教学医生类别：T_DC.Type=TComboBoxT_DC.X=793T_DC.Y=82T_DC.Width=146T_DC.Height=23T_DC.Text=TButtonT_DC.showID=YT_DC.Editable=YT_DC.SQL=SELECT ID, CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='T_DC'T_DC.TableShowList=TEXTT_DC.ExpandWidth=20tLabel_16.Type=TLabeltLabel_16.X=675tLabel_16.Y=89tLabel_16.Width=72tLabel_16.Height=15tLabel_16.Text=*tLabel_16.Color=红tLabel_16.Visible=NtLabel_16.Enabled=NtLabel_15.Type=TLabeltLabel_15.X=436tLabel_15.Y=89tLabel_15.Width=72tLabel_15.Height=15tLabel_15.Text=*tLabel_15.Color=红tLabel_15.Visible=NtLabel_15.Enabled=NtLabel_14.Type=TLabeltLabel_14.X=210tLabel_14.Y=89tLabel_14.Width=72tLabel_14.Height=15tLabel_14.Text=*tLabel_14.Color=红tLabel_14.Enabled=NtLabel_14.Visible=NEND_DATE.Type=TTextFormatEND_DATE.X=441END_DATE.Y=24END_DATE.Width=100END_DATE.Height=20END_DATE.Text=END_DATE.FormatType=dateEND_DATE.Format=yyyy/MM/ddEND_DATE.showDownButton=YEND_DATE_RULE.Type=TTextFormatEND_DATE_RULE.X=567END_DATE_RULE.Y=85END_DATE_RULE.Width=100END_DATE_RULE.Height=23END_DATE_RULE.Text=END_DATE_RULE.FormatType=dateEND_DATE_RULE.Format=yyyy/MM/ddEND_DATE_RULE.showDownButton=YEND_DATE_RULE.Visible=YEND_DATE_RULE.Enabled=YEMR_RULE_CODE.Type=TTextFormatEMR_RULE_CODE.X=103EMR_RULE_CODE.Y=84EMR_RULE_CODE.Width=100EMR_RULE_CODE.Height=23EMR_RULE_CODE.Text=EMR_RULE_CODE.PopupMenuSQL=SELECT ID AS ID,CHN_DESC AS NAME FROM SYS_DICTIONARY WHERE GROUP_ID='EMR_RULE'EMR_RULE_CODE.Tip=角色EMR_RULE_CODE.HorizontalAlignment=2EMR_RULE_CODE.FormatType=comboEMR_RULE_CODE.showDownButton=YEMR_RULE_CODE.PopupMenuHeader=ID,60;角色,150EMR_RULE_CODE.HisOneNullRow=YEMR_RULE_CODE.ShowColumnList=NAMEEMR_RULE_CODE.PopupMenuWidth=214EMR_RULE_CODE.PopupMenuHeight=110EMR_RULE_CODE.Visible=YEMR_RULE_CODE.Enabled=YtLabel_13.Type=TLabeltLabel_13.X=495tLabel_13.Y=90tLabel_13.Width=72tLabel_13.Height=15tLabel_13.Text=结束日期：tLabel_13.Visible=YtLabel_13.Enabled=YSTART_DATE.Type=TTextFormatSTART_DATE.X=329START_DATE.Y=85START_DATE.Width=100START_DATE.Height=23START_DATE.Text=START_DATE.FormatType=dateSTART_DATE.Format=yyyy/MM/ddSTART_DATE.showDownButton=YSTART_DATE.Visible=YSTART_DATE.Enabled=YtLabel_12.Type=TLabeltLabel_12.X=260tLabel_12.Y=90tLabel_12.Width=72tLabel_12.Height=15tLabel_12.Text=开始日期：tLabel_12.Visible=YtLabel_12.Enabled=YtLabel_11.Type=TLabeltLabel_11.X=19tLabel_11.Y=90tLabel_11.Width=72tLabel_11.Height=15tLabel_11.Text=角色权限：tLabel_11.Visible=YtLabel_11.Enabled=YROLE_ID.Type=权限ROLE_ID.X=86ROLE_ID.Y=24ROLE_ID.Width=101ROLE_ID.Height=23ROLE_ID.Text=ROLE_ID.HorizontalAlignment=2ROLE_ID.PopupMenuHeader=代码,100;名称,100ROLE_ID.PopupMenuWidth=300ROLE_ID.PopupMenuHeight=300ROLE_ID.PopupMenuFilter=ID,1;NAME,1;PY1,1ROLE_ID.FormatType=comboROLE_ID.ShowDownButton=YROLE_ID.Tip=权限ROLE_ID.ShowColumnList=NAMEROLE_ID.HisOneNullRow=YROLE_ID.Action=onRoleIdActionACTIVE_DATE.Type=TTextFormatACTIVE_DATE.X=268ACTIVE_DATE.Y=24ACTIVE_DATE.Width=100ACTIVE_DATE.Height=20ACTIVE_DATE.Text=ACTIVE_DATE.showDownButton=YACTIVE_DATE.FormatType=dateACTIVE_DATE.Format=yyyy/MM/ddACTIVE_DATE.Action=onActiveDateActionRCNT_LOGOUT_DATE.Type=TTextFormatRCNT_LOGOUT_DATE.X=467RCNT_LOGOUT_DATE.Y=57RCNT_LOGOUT_DATE.Width=160RCNT_LOGOUT_DATE.Height=20RCNT_LOGOUT_DATE.Text=RCNT_LOGOUT_DATE.showDownButton=YRCNT_LOGOUT_DATE.FormatType=dateRCNT_LOGOUT_DATE.Format=yyyy/MM/dd HH:mm:ssRCNT_LOGOUT_DATE.Enabled=NRCNT_LOGIN_DATE.Type=TTextFormatRCNT_LOGIN_DATE.X=153RCNT_LOGIN_DATE.Y=57RCNT_LOGIN_DATE.Width=160RCNT_LOGIN_DATE.Height=20RCNT_LOGIN_DATE.Text=RCNT_LOGIN_DATE.Format=yyyy/MM/dd HH:mm:ssRCNT_LOGIN_DATE.FormatType=dateRCNT_LOGIN_DATE.showDownButton=YRCNT_LOGIN_DATE.Enabled=NRCNT_IP.Type=TTextFieldRCNT_IP.X=746RCNT_IP.Y=56RCNT_IP.Width=150RCNT_IP.Height=20RCNT_IP.Text=RCNT_IP.Enabled=NtLabel_52.Type=TLabeltLabel_52.X=646tLabel_52.Y=59tLabel_52.Width=100tLabel_52.Height=15tLabel_52.Text=最后操作端末：tLabel_51.Type=TLabeltLabel_51.X=329tLabel_51.Y=60tLabel_51.Width=140tLabel_51.Height=15tLabel_51.Text=最后退出日期/时间：tLabel_50.Type=TLabeltLabel_50.X=19tLabel_50.Y=60tLabel_50.Width=140tLabel_50.Height=15tLabel_50.Text=最后登录日期/时间：PUB_FUNCTION.Type=TTextFieldPUB_FUNCTION.X=661PUB_FUNCTION.Y=24PUB_FUNCTION.Width=235PUB_FUNCTION.Height=20PUB_FUNCTION.Text=tLabel_49.Type=TLabeltLabel_49.X=553tLabel_49.Y=28tLabel_49.Width=100tLabel_49.Height=15tLabel_49.Text=默认进入程序：tLabel_48.Type=TLabeltLabel_48.X=374tLabel_48.Y=28tLabel_48.Width=70tLabel_48.Height=15tLabel_48.Text=失效日期：tLabel_47.Type=TLabeltLabel_47.X=200tLabel_47.Y=28tLabel_47.Width=70tLabel_47.Height=15tLabel_47.Text=生效日期：tLabel_46.Type=TLabeltLabel_46.X=19tLabel_46.Y=28tLabel_46.Width=60tLabel_46.Height=15tLabel_46.Text=角色：tLabel_46.Color=蓝tPanel_4.Type=TPaneltPanel_4.X=5tPanel_4.Y=5tPanel_4.Width=1132tPanel_4.Height=170tPanel_4.AutoX=YtPanel_4.AutoY=YtPanel_4.AutoWidth=YtPanel_4.AutoSize=5tPanel_4.Border=组|基本信息tPanel_4.Item=tLabel_33;USER_ID;tLabel_34;USER_NAME;tLabel_35;PY1;tLabel_36;PY2;tLabel_37;tLabel_38;ID_NO;FOREIGNER_FLG;tLabel_39;SEX_CODE;tLabel_40;DESCRIPTION;tLabel_41;USER_PASSWORD;tLabel_42;FULLTIME_FLG;CTRL_FLG;tLabel_43;E_MAIL;tLabel_45;REGION_CODE;SEQ;tLabel_0;ABNORMAL_TIMES;POS_CODE;tLabel_3;USER_ENG_NAME;pwd_LBL;tLabel_4;PWD_ENDDATE;PWD_POOFSTH;UKEY_FLG;tPanel_0;tLabel_9;TEL1;tLabel_10;TEL2;IS_OUT_FLGIS_OUT_FLG.Type=TCheckBoxIS_OUT_FLG.X=1014IS_OUT_FLG.Y=116IS_OUT_FLG.Width=110IS_OUT_FLG.Height=23IS_OUT_FLG.Text=是否外院医师IS_OUT_FLG.Visible=YTEL2.Type=TTextFieldTEL2.X=350TEL2.Y=142TEL2.Width=197TEL2.Height=20TEL2.Text=tLabel_10.Type=TLabeltLabel_10.X=296tLabel_10.Y=144tLabel_10.Width=72tLabel_10.Height=15tLabel_10.Text=电话二:TEL1.Type=TTextFieldTEL1.X=86TEL1.Y=143TEL1.Width=160TEL1.Height=20TEL1.Text=tLabel_9.Type=TLabeltLabel_9.X=14tLabel_9.Y=146tLabel_9.Width=72tLabel_9.Height=15tLabel_9.Text=电话一:tPanel_0.Type=TPaneltPanel_0.X=492tPanel_0.Y=79tPanel_0.Width=81tPanel_0.Height=81UKEY_FLG.Type=TCheckBoxUKEY_FLG.X=296UKEY_FLG.Y=117UKEY_FLG.Width=106UKEY_FLG.Height=22UKEY_FLG.Text=Ukey校验PWD_POOFSTH.Type=TLabelPWD_POOFSTH.X=199PWD_POOFSTH.Y=122PWD_POOFSTH.Width=72PWD_POOFSTH.Height=15PWD_POOFSTH.Text=强度校验:PWD_POOFSTH.Type=TLabelPWD_POOFSTH.X=201PWD_POOFSTH.Y=121PWD_POOFSTH.Width=72PWD_POOFSTH.Height=15PWD_POOFSTH.Text=强度校验:PWD_ENDDATE.Type=TTextFormatPWD_ENDDATE.X=898PWD_ENDDATE.Y=119PWD_ENDDATE.Width=100PWD_ENDDATE.Height=20PWD_ENDDATE.Text=PWD_ENDDATE.showDownButton=YPWD_ENDDATE.Format=yyyy/MM/ddPWD_ENDDATE.FormatType=datetLabel_4.Type=TLabeltLabel_4.X=795tLabel_4.Y=122tLabel_4.Width=98tLabel_4.Height=15tLabel_4.Text=密码失效日期：pwd_LBL.Type=TLabelpwd_LBL.X=268pwd_LBL.Y=122pwd_LBL.Width=18pwd_LBL.Height=14pwd_LBL.Text=低pwd_LBL.Color=红USER_ENG_NAME.Type=TTextFieldUSER_ENG_NAME.X=796USER_ENG_NAME.Y=24USER_ENG_NAME.Width=202USER_ENG_NAME.Height=20USER_ENG_NAME.Text=tLabel_3.Type=TLabeltLabel_3.X=736tLabel_3.Y=27tLabel_3.Width=61tLabel_3.Height=15tLabel_3.Text=英文名:POS_CODE.Type=职别下拉区域POS_CODE.X=262POS_CODE.Y=84POS_CODE.Width=102POS_CODE.Height=23POS_CODE.Text=POS_CODE.HorizontalAlignment=2POS_CODE.PopupMenuHeader=代码,100;名称,100POS_CODE.PopupMenuWidth=300POS_CODE.PopupMenuHeight=300POS_CODE.FormatType=comboPOS_CODE.ShowDownButton=YPOS_CODE.Tip=职别POS_CODE.ShowColumnList=NAMEPOS_CODE.HisOneNullRow=YPOS_CODE.Action=onPosCodeActionABNORMAL_TIMES.Type=TNumberTextFieldABNORMAL_TIMES.X=704ABNORMAL_TIMES.Y=119ABNORMAL_TIMES.Width=60ABNORMAL_TIMES.Height=20ABNORMAL_TIMES.Text=0ABNORMAL_TIMES.Format=#########0tLabel_0.Type=TLabeltLabel_0.X=591tLabel_0.Y=122tLabel_0.Width=100tLabel_0.Height=15tLabel_0.Text=异常登陆次数：SEQ.Type=TNumberTextFieldSEQ.X=473SEQ.Y=119SEQ.Width=100SEQ.Height=20SEQ.Text=0SEQ.Format=#########0REGION_CODE.Type=区域下拉列表REGION_CODE.X=86REGION_CODE.Y=84REGION_CODE.Width=100REGION_CODE.Height=23REGION_CODE.Text=TButtonREGION_CODE.showID=YREGION_CODE.showName=YREGION_CODE.showText=NREGION_CODE.showValue=NREGION_CODE.showPy1=NREGION_CODE.showPy2=NREGION_CODE.Editable=YREGION_CODE.Tip=区域REGION_CODE.TableShowList=nameREGION_CODE.ModuleParmString=REGION_CODE.ModuleParmTag=REGION_CODE.Action=onRegionCodeActionREGION_CODE.SelectedAction=REGION_CODE.ExpandWidth=80tLabel_45.Type=TLabeltLabel_45.X=19tLabel_45.Y=88tLabel_45.Width=70tLabel_45.Height=15tLabel_45.Text=区域：tLabel_45.Color=蓝E_MAIL.Type=TTextFieldE_MAIL.X=701E_MAIL.Y=87E_MAIL.Width=201E_MAIL.Height=20E_MAIL.Text=tLabel_43.Type=TLabeltLabel_43.X=634tLabel_43.Y=89tLabel_43.Width=60tLabel_43.Height=15tLabel_43.Text=E_Mail:CTRL_FLG.Type=TCheckBoxCTRL_FLG.X=498CTRL_FLG.Y=86CTRL_FLG.Width=120CTRL_FLG.Height=23CTRL_FLG.Text=管制药品药师FULLTIME_FLG.Type=TCheckBoxFULLTIME_FLG.X=380FULLTIME_FLG.Y=86FULLTIME_FLG.Width=100FULLTIME_FLG.Height=23FULLTIME_FLG.Text=专任医师tLabel_42.Type=TLabeltLabel_42.X=200tLabel_42.Y=88tLabel_42.Width=60tLabel_42.Height=15tLabel_42.Text=职称：tLabel_42.Color=蓝USER_PASSWORD.Type=TPasswordFieldUSER_PASSWORD.X=86USER_PASSWORD.Y=119USER_PASSWORD.Width=100USER_PASSWORD.Height=20USER_PASSWORD.Text=USER_PASSWORD.Action=pwdPoofSthUSER_PASSWORD.ControlClassName=onUserPasswordActiontLabel_41.Type=TLabeltLabel_41.X=19tLabel_41.Y=121tLabel_41.Width=60tLabel_41.Height=15tLabel_41.Text=口令：DESCRIPTION.Type=TTextFieldDESCRIPTION.X=619DESCRIPTION.Y=55DESCRIPTION.Width=285DESCRIPTION.Height=20DESCRIPTION.Text=tLabel_40.Type=TLabeltLabel_40.X=553tLabel_40.Y=58tLabel_40.Width=60tLabel_40.Height=15tLabel_40.Text=备注：SEX_CODE.Type=性别下拉列表SEX_CODE.X=435SEX_CODE.Y=55SEX_CODE.Width=100SEX_CODE.Height=23SEX_CODE.Text=TButtonSEX_CODE.showID=YSEX_CODE.showName=YSEX_CODE.showText=NSEX_CODE.showValue=NSEX_CODE.showPy1=YSEX_CODE.showPy2=YSEX_CODE.Editable=YSEX_CODE.Tip=性别SEX_CODE.TableShowList=nameSEX_CODE.ModuleParmString=GROUP_ID:SYS_SEXSEX_CODE.ModuleParmTag=SEX_CODE.Action=tLabel_39.Type=TLabeltLabel_39.X=374tLabel_39.Y=58tLabel_39.Width=60tLabel_39.Height=15tLabel_39.Text=性别：FOREIGNER_FLG.Type=TCheckBoxFOREIGNER_FLG.X=14FOREIGNER_FLG.Y=54FOREIGNER_FLG.Width=81FOREIGNER_FLG.Height=23FOREIGNER_FLG.Text=其他证件FOREIGNER_FLG.Action=ID_NO.Type=TTextFieldID_NO.X=186ID_NO.Y=55ID_NO.Width=177ID_NO.Height=20ID_NO.Text=ID_NO.Action=onIdNOActiontLabel_38.Type=TLabeltLabel_38.X=121tLabel_38.Y=58tLabel_38.Width=60tLabel_38.Height=15tLabel_38.Text=身份证：tLabel_37.Type=TLabeltLabel_37.X=412tLabel_37.Y=122tLabel_37.Width=60tLabel_37.Height=15tLabel_37.Text=顺序号：PY2.Type=TTextFieldPY2.X=618PY2.Y=24PY2.Width=100PY2.Height=20PY2.Text=tLabel_36.Type=TLabeltLabel_36.X=553tLabel_36.Y=27tLabel_36.Width=60tLabel_36.Height=15tLabel_36.Text=助记码：PY1.Type=TTextFieldPY1.X=435PY1.Y=24PY1.Width=100PY1.Height=20PY1.Text=tLabel_35.Type=TLabeltLabel_35.X=374tLabel_35.Y=27tLabel_35.Width=60tLabel_35.Height=15tLabel_35.Text=拼音：USER_NAME.Type=TTextFieldUSER_NAME.X=262USER_NAME.Y=24USER_NAME.Width=100USER_NAME.Height=20USER_NAME.Text=USER_NAME.Action=onUserNameActiontLabel_34.Type=TLabeltLabel_34.X=200tLabel_34.Y=27tLabel_34.Width=60tLabel_34.Height=15tLabel_34.Text=姓名：tLabel_34.Color=蓝USER_ID.Type=TTextFieldUSER_ID.X=86USER_ID.Y=24USER_ID.Width=100USER_ID.Height=20USER_ID.Text=USER_ID.Action=onUserIdActiontLabel_33.Type=TLabeltLabel_33.X=19tLabel_33.Y=27tLabel_33.Width=60tLabel_33.Height=15tLabel_33.Text=使用者：tLabel_33.AutoX=NtLabel_33.AutoY=NtLabel_33.Color=蓝TUSERNAME.Type=TTextFieldTUSERNAME.X=246TUSERNAME.Y=14TUSERNAME.Width=92TUSERNAME.Height=20TUSERNAME.Text=