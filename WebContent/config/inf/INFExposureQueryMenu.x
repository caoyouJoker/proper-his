#############################################
# <p>Title:��¶�Ǽǲ�ѯ</p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author zhanglei 20170512
# @version 5.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;excel;|;regDetail;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;excel;|;regDetail;|;clear;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

regDetail.Type=TMenuItem
regDetail.Text=����
regDetail.Tip=��������(Ctrl+R)
regDetail.M=R
regDetail.key=Ctrl+R
regDetail.Action=onRegDetail
regDetail.pic=search-2.gif


clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

excel.Type=TMenuItem
excel.Text=��Excel
excel.Tip=��Excel(Ctrl+E)
excel.M=E
excel.key=Ctrl+E
excel.Action=onExcel
excel.pic=exportexcel.gif