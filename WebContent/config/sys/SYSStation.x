#  # Title: 护士站管理  #  # Description:护士站,房间,床位管理  #  # Copyright: JavaHis (c) 2008  #  # @author fudw  # @version 1.0  <Type=TFrame>UI.Title=护士站管理UI.MenuConfig=%ROOT%\config\sys\SYSStationMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.sys.SYSStationControlUI.item=TREE;TABBEDPANLE;tPanel_3;tMovePane_0UI.layout=nullUI.FocusList=UI.TopMenu=YUI.TopToolBar=YtMovePane_0.Type=TMovePanetMovePane_0.X=181tMovePane_0.Y=9tMovePane_0.Width=5tMovePane_0.Height=585tMovePane_0.Text=tMovePane_0.MoveType=1tMovePane_0.Style=3tMovePane_0.DoubleClickType=1tMovePane_0.AutoY=YtMovePane_0.AutoHeight=YtMovePane_0.EntityData=tPanel_3,4;TREE,4;TABBEDPANLE,3tPanel_3.Type=TPaneltPanel_3.X=5tPanel_3.Y=5tPanel_3.Width=174tPanel_3.Height=52tPanel_3.Border=组tPanel_3.AutoY=YtPanel_3.AutoX=YtPanel_3.Item=tLabel_0;TREELOYARTREELOYAR.Type=TComboBoxTREELOYAR.X=65TREELOYAR.Y=16TREELOYAR.Width=81TREELOYAR.Height=23TREELOYAR.Text=TButtonTREELOYAR.showID=YTREELOYAR.Editable=YTREELOYAR.ShowText=NTREELOYAR.ShowName=YTREELOYAR.TableShowList=nameTREELOYAR.SelectedAction=onExtendToLoyarTREELOYAR.ParmMap=id:ID;name:NAMEtLabel_0.Type=TLabeltLabel_0.X=3tLabel_0.Y=19tLabel_0.Width=64tLabel_0.Height=15tLabel_0.Text=展开层级:TABBEDPANLE.Type=TTabbedPaneTABBEDPANLE.X=187TABBEDPANLE.Y=13TABBEDPANLE.Width=832TABBEDPANLE.Height=590TABBEDPANLE.AutoY=YTABBEDPANLE.AutoWidth=YTABBEDPANLE.Item=tPanel_0;tPanel_1;tPanel_2TABBEDPANLE.AutoHeight=YtPanel_2.Type=TPaneltPanel_2.X=50tPanel_2.Y=7tPanel_2.Width=825tPanel_2.Height=81tPanel_2.Name=床位tPanel_2.Text=床位tPanel_2.Tip=床位tPanel_2.Item=TABLEBED;BED_CLASS_CODE;SEX_CODE;BED_TYPE_CODEtPanel_2.AutoWidth=YBED_TYPE_CODE.Type=床位类别BED_TYPE_CODE.X=197BED_TYPE_CODE.Y=8BED_TYPE_CODE.Width=81BED_TYPE_CODE.Height=23BED_TYPE_CODE.Text=BED_TYPE_CODE.HorizontalAlignment=2BED_TYPE_CODE.PopupMenuHeader=代码,100;名称,100BED_TYPE_CODE.PopupMenuWidth=300BED_TYPE_CODE.PopupMenuHeight=300BED_TYPE_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1BED_TYPE_CODE.FormatType=comboBED_TYPE_CODE.ShowDownButton=YBED_TYPE_CODE.Tip=床位类别BED_TYPE_CODE.ShowColumnList=NAMEBED_TYPE_CODE.HisOneNullRow=YSEX_CODE.Type=性别下拉列表SEX_CODE.X=105SEX_CODE.Y=9SEX_CODE.Width=81SEX_CODE.Height=23SEX_CODE.Text=TButtonSEX_CODE.showID=YSEX_CODE.showName=YSEX_CODE.showText=NSEX_CODE.showValue=NSEX_CODE.showPy1=NSEX_CODE.showPy2=NSEX_CODE.Editable=YSEX_CODE.Tip=性别SEX_CODE.TableShowList=nameSEX_CODE.ModuleParmString=GROUP_ID:SYS_SEXSEX_CODE.ModuleParmTag=BED_CLASS_CODE.Type=床位等级下拉列表BED_CLASS_CODE.X=19BED_CLASS_CODE.Y=9BED_CLASS_CODE.Width=81BED_CLASS_CODE.Height=23BED_CLASS_CODE.Text=TButtonBED_CLASS_CODE.showID=YBED_CLASS_CODE.showName=YBED_CLASS_CODE.showText=NBED_CLASS_CODE.showValue=NBED_CLASS_CODE.showPy1=NBED_CLASS_CODE.showPy2=NBED_CLASS_CODE.Editable=YBED_CLASS_CODE.Tip=床位等级下拉列表BED_CLASS_CODE.TableShowList=nameBED_CLASS_CODE.ModuleParmString=GROUP_ID:SYS_BED_CLASSBED_CLASS_CODE.ModuleParmTag=BED_CLASS_CODE.ExpandWidth=50TABLEBED.Type=TTableTABLEBED.X=2TABLEBED.Y=5TABLEBED.Width=817TABLEBED.Height=702TABLEBED.SpacingRow=1TABLEBED.RowHeight=20TABLEBED.Header=床号,70;病床名称,80;拼音1,60;英文,150;归属病房,100,ROOM;护士站,80,STATION;归属区域,100,REGION_CODE;病床等级,120,BED_CLASS_CODE;床座类别,120,BED_TYPE_CODE;启用,40,boolean;预约,40,boolean;占床,40,boolean;包床,40,boolean;留床,40,boolean;性别,40,SEX_CODE;占床率,60,boolean;开预约,60,boolean;婴儿床,60,boolean;入酒店,60,boolean;拼音2,60;顺序,40;病床描述,80;操作人员,80;操作日期,100;操作端末,80TABLEBED.ParmMap=BED_NO;BED_NO_DESC;PY1;ENG_DESC;ROOM_CODE;STATION_CODE;REGION_CODE;BED_CLASS_CODE;BED_TYPE_CODE;ACTIVE_FLG;APPT_FLG;ALLO_FLG;BED_OCCU_FLG;RESERVE_BED_FLG;SEX_CODE;OCCU_RATE_FLG;DR_APPROVE_FLG;BABY_BED_FLG;HTL_FLG;PY2;SEQ;DESCRIPTION;OPT_USER;OPT_DATE;OPT_TERMTABLEBED.LockColumns=4,5,22,23,24TABLEBED.FocusType=2TABLEBED.SQL=TABLEBED.AutoModifyDataStore=YTABLEBED.AutoX=YTABLEBED.AutoY=YTABLEBED.AutoHeight=YTABLEBED.AutoWidth=YTABLEBED.ClickedAction=onTableBedDoubleClickedTABLEBED.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left;14,left;20,left;21,right;22,left;23,left;24,leftTABLEBED.Item=REGION_CODE;BED_CLASS_CODE;SEX_CODE;BED_TYPE_CODEtPanel_1.Type=TPaneltPanel_1.X=48tPanel_1.Y=14tPanel_1.Width=81tPanel_1.Height=81tPanel_1.Name=房间tPanel_1.Text=房间tPanel_1.Tip=房间tPanel_1.Item=TABLEROOMTABLEROOM.Type=TTableTABLEROOM.X=3TABLEROOM.Y=4TABLEROOM.Width=817TABLEROOM.Height=702TABLEROOM.SpacingRow=1TABLEROOM.RowHeight=20TABLEROOM.Header=病房号,65;病房描述,80;英文,150;拼音1,60;护士站,80,STATION;区域,100,REGION_CODE;性别管制,70,boolean;红色警戒,70,double;黄色警戒,70,double;拼音2,60;备注,80;操作人员,80,OPT_USER;操作日期,100;操作端末,100TABLEROOM.ParmMap=ROOM_CODE;ROOM_DESC;ENG_DESC;PY1;STATION_CODE;REGION_CODE;SEX_LIMIT_FLG;RED_SIGN;YELLOW_SIGN;PY2;DESCRIPT;OPT_USER;OPT_DATE;OPT_TERMTABLEROOM.SQL=TABLEROOM.AutoModifyDataStore=YTABLEROOM.AutoX=YTABLEROOM.AutoY=YTABLEROOM.AutoWidth=YTABLEROOM.AutoHeight=YTABLEROOM.LockColumns=4,11,12,13TABLEROOM.FocusType=2TABLEROOM.DoubleClickedAction=TABLEROOM.ClickedAction=onTableRoomDoubleClickedTABLEROOM.Item=PAHORG;REGION_CODE;OPT_USERTABLEROOM.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,left;7,right;8,right;9,left;10,left;11,left;12,left;13,lefttPanel_0.Type=TPaneltPanel_0.X=54tPanel_0.Y=31tPanel_0.Width=81tPanel_0.Height=81tPanel_0.Name=护士站tPanel_0.Text=护士站tPanel_0.Tip=护士站tPanel_0.Title=护士站tPanel_0.Item=TABLESTATION;REGION_CODE;DEPT_CODE;COST_CENTER_CODE;PAHORG;ATC_MACHINENO;ATC_TYPEATC_TYPE.Type=包药机类型下拉区域ATC_TYPE.X=56ATC_TYPE.Y=184ATC_TYPE.Width=81ATC_TYPE.Height=23ATC_TYPE.Text=ATC_TYPE.HorizontalAlignment=2ATC_TYPE.PopupMenuHeader=代码,100;名称,100ATC_TYPE.PopupMenuWidth=300ATC_TYPE.PopupMenuHeight=300ATC_TYPE.PopupMenuFilter=ID,1;NAME,1;PY1,1ATC_TYPE.FormatType=comboATC_TYPE.ShowDownButton=YATC_TYPE.Tip=包药机类型ATC_TYPE.ShowColumnList=NAMEATC_TYPE.HisOneNullRow=YATC_MACHINENO.Type=包药机台号下拉区域ATC_MACHINENO.X=66ATC_MACHINENO.Y=125ATC_MACHINENO.Width=81ATC_MACHINENO.Height=23ATC_MACHINENO.Text=ATC_MACHINENO.HorizontalAlignment=2ATC_MACHINENO.PopupMenuHeader=代码,100;名称,100ATC_MACHINENO.PopupMenuWidth=300ATC_MACHINENO.PopupMenuHeight=300ATC_MACHINENO.PopupMenuFilter=ID,1;NAME,1;PY1,1ATC_MACHINENO.FormatType=comboATC_MACHINENO.ShowDownButton=YATC_MACHINENO.Tip=包药机台号ATC_MACHINENO.ShowColumnList=NAMEATC_MACHINENO.HisOneNullRow=YPAHORG.Type=药房PAHORG.X=102PAHORG.Y=57PAHORG.Width=81PAHORG.Height=23PAHORG.Text=PAHORG.HorizontalAlignment=2PAHORG.PopupMenuHeader=代码,100;名称,100PAHORG.PopupMenuWidth=300PAHORG.PopupMenuHeight=300PAHORG.PopupMenuFilter=ID,1;NAME,1;PY1,1PAHORG.FormatType=comboPAHORG.ShowDownButton=YPAHORG.Tip=药房PAHORG.ShowColumnList=NAMEPAHORG.OrgType=BPAHORG.HisOneNullRow=YCOST_CENTER_CODE.Type=成本中心下拉区域COST_CENTER_CODE.X=247COST_CENTER_CODE.Y=12COST_CENTER_CODE.Width=81COST_CENTER_CODE.Height=23COST_CENTER_CODE.Text=COST_CENTER_CODE.HorizontalAlignment=2COST_CENTER_CODE.PopupMenuHeader=代码,100;名称,100COST_CENTER_CODE.PopupMenuWidth=300COST_CENTER_CODE.PopupMenuHeight=300COST_CENTER_CODE.FormatType=comboCOST_CENTER_CODE.ShowDownButton=YCOST_CENTER_CODE.Tip=成本中心COST_CENTER_CODE.ShowColumnList=NAMECOST_CENTER_CODE.HisOneNullRow=YDEPT_CODE.Type=科室DEPT_CODE.X=25DEPT_CODE.Y=9DEPT_CODE.Width=81DEPT_CODE.Height=23DEPT_CODE.Text=DEPT_CODE.HorizontalAlignment=2DEPT_CODE.PopupMenuHeader=代码,100;名称,100DEPT_CODE.PopupMenuWidth=300DEPT_CODE.PopupMenuHeight=300DEPT_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1DEPT_CODE.FormatType=comboDEPT_CODE.ShowDownButton=YDEPT_CODE.Tip=科室DEPT_CODE.ShowColumnList=NAMEDEPT_CODE.ClassIfy=0DEPT_CODE.ActiveFlg=YOPT_USER.Type=人员下拉列表OPT_USER.X=62OPT_USER.Y=68OPT_USER.Width=81OPT_USER.Height=23OPT_USER.Text=TButtonOPT_USER.showID=YOPT_USER.showName=YOPT_USER.showText=NOPT_USER.showValue=NOPT_USER.showPy1=YOPT_USER.showPy2=YOPT_USER.Editable=YOPT_USER.Tip=人员OPT_USER.TableShowList=nameOPT_USER.ModuleParmString=OPT_USER.ModuleParmTag=OPT_USER.ExpandWidth=10REGION_CODE.Type=区域下拉列表REGION_CODE.X=141REGION_CODE.Y=7REGION_CODE.Width=81REGION_CODE.Height=23REGION_CODE.Text=TButtonREGION_CODE.showID=YREGION_CODE.showName=YREGION_CODE.showText=NREGION_CODE.showValue=NREGION_CODE.showPy1=YREGION_CODE.showPy2=YREGION_CODE.Editable=YREGION_CODE.Tip=区域REGION_CODE.TableShowList=nameREGION_CODE.ModuleParmString=REGION_CODE.ModuleParmTag=REGION_CODE.ExpandWidth=10TABLESTATION.Type=TTableTABLESTATION.X=2TABLESTATION.Y=2TABLESTATION.Width=817TABLESTATION.Height=702TABLESTATION.SpacingRow=1TABLESTATION.RowHeight=20TABLESTATION.AutoX=YTABLESTATION.AutoY=YTABLESTATION.AutoWidth=YTABLESTATION.AutoHeight=YTABLESTATION.ParmMap=STATION_CODE;STATION_DESC;ENG_DESC;PY1;DEPT_CODE;COST_CENTER_CODE;REGION_CODE;ORG_CODE;LOC_CODE;PRINTER_NO;TEL_EXT;PY2;SEQ;OPT_USER;OPT_DATE;OPT_TERM;MACHINENO;ATC_TYPETABLESTATION.SQL=TABLESTATION.AutoModifyDataStore=YTABLESTATION.Header=护士站,60;名称,120;英文,250;拼音1,60;科室,70,DEPT_CODE;成本中心,100,COST_CENTER_CODE;区域,100,REGION_CODE;预设药房,100,PAHORG;位置,60;印表机代码,80;分机号,65;拼音2,50;顺序,40;操作人员,80,OPT_USER;操作日期,100;操作端末,110;包药机台号,100,ATC_MACHINENO;包药机类型,100,ATC_TYPETABLESTATION.FocusType=2TABLESTATION.DoubleClickedAction=onTableStationDoubleClickedTABLESTATION.ClickedAction=onTableStationDoubleClickedTABLESTATION.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left;9,right;10,left;11,left;12,left;13,left;14,left;15,left;16,left;17,leftTABLESTATION.Item=PAHORG;REGION_CODE;OPT_USER;DEPT_CODE;COST_CENTER_CODE;ATC_MACHINENO;ATC_TYPETABLESTATION.LockColumns=13,14,15TREE.Type=TTreeTREE.X=4TREE.Y=62TREE.Width=176TREE.Height=681TREE.SpacingRow=1TREE.RowHeight=20TREE.AutoY=NTREE.AutoHeight=YTREE.Pics=Path:dir1.gif