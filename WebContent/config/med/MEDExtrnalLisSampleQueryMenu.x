# 
#  Title:����������ѯ�˵�
# 
#  Description:Menu
# 
#  Copyright: Copyright (c) Javahis 2017
# 
#  author wangb
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;clear;|;upload;|;export;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;clear;close


Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

upload.Type=TMenuItem
upload.Text=�ϴ�CSV
upload.Tip=�ϴ�CSV(Ctrl+U)
upload.M=U
upload.key=Ctrl+U
upload.Action=onUpLoad
upload.pic=008.gif

export.Type=TMenuItem
export.Text=����Excel
export.Tip=����(Ctrl+O)
export.M=O
export.key=Ctrl+O
export.Action=onExport
export.pic=exportexcel.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

