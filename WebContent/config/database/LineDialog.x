## TBuilder Config File ## Title:图层对话框## Company:JavaHis## Author:lzk 2009.05.21## version 1.0#<Type=TFrame>UI.Title=UI.MenuConfig=UI.Width=335UI.Height=393UI.toolbar=YUI.controlclassname=com.javahis.ui.database.LineDialogControlUI.item=tRootPanel_0UI.layout=nullUI.FocusList=NAME;DX1;DY1;DX2;DY2tRootPanel_0.Type=TRootPaneltRootPanel_0.X=0tRootPanel_0.Y=0tRootPanel_0.Width=335tRootPanel_0.Height=393tRootPanel_0.AutoSize=0tRootPanel_0.AutoX=YtRootPanel_0.AutoY=YtRootPanel_0.AutoHeight=YtRootPanel_0.AutoWidth=YtRootPanel_0.Title=直线属性tRootPanel_0.Item=tTabbedPane_0tTabbedPane_0.Type=TTabbedPanetTabbedPane_0.X=10tTabbedPane_0.Y=38tTabbedPane_0.Width=312tTabbedPane_0.Height=346tTabbedPane_0.AutoSize=0tTabbedPane_0.AutoX=YtTabbedPane_0.AutoY=YtTabbedPane_0.AutoWidth=YtTabbedPane_0.AutoHeight=YtTabbedPane_0.Item=tPanel_0;tPanel_1tPanel_1.Type=TPaneltPanel_1.X=48tPanel_1.Y=9tPanel_1.Width=81tPanel_1.Height=81tPanel_1.Text=tPanel_1.Name=成员tPanel_1.Item=tPanel_0.Type=TPaneltPanel_0.X=27tPanel_0.Y=9tPanel_0.Width=81tPanel_0.Height=81tPanel_0.Name=属性tPanel_0.Item=tLabel_2;NAME;tLabel_3;DX1;tLabel_4;DY1;tLabel_5;DY2;tLabel_6;DX2;V_CB;tLabel_0;COLOR;COLOR_D;tLabel_1;LINE_WIDTH;tLabel_7;LINE_TYPE;tLabel_8;LINE_WIDTH1;tLabel_9;DX0;tLabel_10;DY0;tLabel_11;DCOUNT;DX1B;DX2B;DY1B;DY2B;LINE_TLINE_T.Type=TCheckBoxLINE_T.X=264LINE_T.Y=211LINE_T.Width=37LINE_T.Height=23LINE_T.Text=TLINE_T.Action=onLineTDY2B.Type=TCheckBoxDY2B.X=280DY2B.Y=66DY2B.Width=21DY2B.Height=23DY2B.Text=DY2B.Action=onDY2BDY1B.Type=TCheckBoxDY1B.X=280DY1B.Y=35DY1B.Width=21DY1B.Height=23DY1B.Text=DY1B.Action=onDY1BDX2B.Type=TCheckBoxDX2B.X=130DX2B.Y=66DX2B.Width=21DX2B.Height=23DX2B.Text=DX2B.Action=onDX2BDX1B.Type=TCheckBoxDX1B.X=130DX1B.Y=35DX1B.Width=21DX1B.Height=23DX1B.Text=DX1B.Action=onDX1BDCOUNT.Type=TTextFormatDCOUNT.X=52DCOUNT.Y=207DCOUNT.Width=77DCOUNT.Height=20DCOUNT.Text=0DCOUNT.HorizontalAlignment=4DCOUNT.Format=intDCOUNT.FocusLostAction=onDCountDCOUNT.ClickedAction=DCOUNT.Action=onDCounttLabel_11.Type=TLabeltLabel_11.X=7tLabel_11.Y=209tLabel_11.Width=41tLabel_11.Height=15tLabel_11.Text=个数:DY0.Type=TTextFormatDY0.X=168DY0.Y=180DY0.Width=77DY0.Height=20DY0.Text=0DY0.HorizontalAlignment=4DY0.FormatType=intDY0.Action=onDY0DY0.FocusLostAction=onDY0tLabel_10.Type=TLabeltLabel_10.X=137tLabel_10.Y=183tLabel_10.Width=25tLabel_10.Height=15tLabel_10.Text=y0:DX0.Type=TTextFormatDX0.X=52DX0.Y=180DX0.Width=77DX0.Height=20DX0.Text=0DX0.FormatType=intDX0.HorizontalAlignment=4DX0.Action=onDX0DX0.FocusLostAction=onDX0tLabel_9.Type=TLabeltLabel_9.X=23tLabel_9.Y=183tLabel_9.Width=29tLabel_9.Height=15tLabel_9.Text=x0:LINE_WIDTH1.Type=TTextFormatLINE_WIDTH1.X=168LINE_WIDTH1.Y=124LINE_WIDTH1.Width=77LINE_WIDTH1.Height=20LINE_WIDTH1.Text=LINE_WIDTH1.FormatType=doubleLINE_WIDTH1.Format=####0.0LINE_WIDTH1.HorizontalAlignment=4LINE_WIDTH1.Action=onLineWidth1LINE_WIDTH1.FocusLostAction=onLineWidth1tLabel_8.Type=TLabeltLabel_8.X=133tLabel_8.Y=127tLabel_8.Width=38tLabel_8.Height=15tLabel_8.Text=虚线:LINE_TYPE.Type=TComboBoxLINE_TYPE.X=51LINE_TYPE.Y=150LINE_TYPE.Width=95LINE_TYPE.Height=23LINE_TYPE.Text=TButtonLINE_TYPE.showID=YLINE_TYPE.Editable=YLINE_TYPE.StringData=[[id,name],[0,直线],[1,虚线],[2,点线]]LINE_TYPE.ShowName=YLINE_TYPE.ShowText=NLINE_TYPE.TableShowList=NAMELINE_TYPE.SelectedAction=onLineTypetLabel_7.Type=TLabeltLabel_7.X=7tLabel_7.Y=154tLabel_7.Width=41tLabel_7.Height=15tLabel_7.Text=类型:LINE_WIDTH.Type=TTextFormatLINE_WIDTH.X=52LINE_WIDTH.Y=124LINE_WIDTH.Width=77LINE_WIDTH.Height=20LINE_WIDTH.Text=LINE_WIDTH.HorizontalAlignment=4LINE_WIDTH.FormatType=doubleLINE_WIDTH.Format=####0.0LINE_WIDTH.Action=onLineWidthLINE_WIDTH.FocusLostAction=onLineWidthtLabel_1.Type=TLabeltLabel_1.X=7tLabel_1.Y=128tLabel_1.Width=41tLabel_1.Height=15tLabel_1.Text=线高:COLOR_D.Type=TLabelCOLOR_D.X=247COLOR_D.Y=98COLOR_D.Width=49COLOR_D.Height=19COLOR_D.Text=调色板COLOR_D.Color=蓝COLOR_D.HorizontalAlignment=0COLOR_D.CursorType=12COLOR_D.Action=onColorDialogCOLOR_D.Border=凸COLOR_D.VerticalAlignment=0COLOR.Type=TTextFieldCOLOR.X=52COLOR.Y=97COLOR.Width=193COLOR.Height=20COLOR.Text=COLOR.Action=onColorCOLOR.FocusLostAction=onColortLabel_0.Type=TLabeltLabel_0.X=7tLabel_0.Y=101tLabel_0.Width=41tLabel_0.Height=15tLabel_0.Text=颜色:V_CB.Type=TCheckBoxV_CB.X=257V_CB.Y=5V_CB.Width=59V_CB.Height=23V_CB.Text=显示V_CB.Action=onVCBDY2.Type=TTextFormatDY2.X=201DY2.Y=68DY2.Width=77DY2.Height=20DY2.Text=0DY2.FormatType=intDY2.HorizontalAlignment=4DY2.Action=onDY2DY2.FocusLostAction=onDY2tLabel_6.Type=TLabeltLabel_6.X=170tLabel_6.Y=72tLabel_6.Width=25tLabel_6.Height=15tLabel_6.Text=y2:DX2.Type=TTextFormatDX2.X=52DX2.Y=68DX2.Width=77DX2.Height=20DX2.Text=0DX2.HorizontalAlignment=4DX2.FormatType=intDX2.Action=onDX2DX2.FocusLostAction=onDX2tLabel_5.Type=TLabeltLabel_5.X=23tLabel_5.Y=72tLabel_5.Width=29tLabel_5.Height=15tLabel_5.Text=x2:DY1.Type=TTextFormatDY1.X=201DY1.Y=37DY1.Width=77DY1.Height=20DY1.Text=0DY1.HorizontalAlignment=4DY1.FormatType=intDY1.Action=onDY1DY1.FocusLostAction=onDY1tLabel_4.Type=TLabeltLabel_4.X=170tLabel_4.Y=39tLabel_4.Width=26tLabel_4.Height=15tLabel_4.Text=y1:DX1.Type=TTextFormatDX1.X=52DX1.Y=37DX1.Width=77DX1.Height=20DX1.Text=0DX1.FormatType=intDX1.HorizontalAlignment=4DX1.Action=onDX1DX1.FocusLostAction=onDX1tLabel_3.Type=TLabeltLabel_3.X=23tLabel_3.Y=39tLabel_3.Width=26tLabel_3.Height=15tLabel_3.Text=x1:NAME.Type=TTextFieldNAME.X=52NAME.Y=5NAME.Width=193NAME.Height=20NAME.Text=NAME.Action=onNameNAME.FocusLostAction=onNametLabel_2.Type=TLabeltLabel_2.X=7tLabel_2.Y=8tLabel_2.Width=41tLabel_2.Height=15tLabel_2.Text=名称: