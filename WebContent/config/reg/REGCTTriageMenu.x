# 
#  Title:CT����
# 
#  Description:CT����
# 
#  Copyright: Copyright (c) Javahis 2017
# 
#  author WangQing 2017.2.22
#  version 1.0
#
<Type=TMenuBar>

UI.Item=File;Window
UI.button=query;save;clear;close

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;save;refresh;clear;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=refresh

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=X
query.key=Alt+F4
query.Action=onQuery
query.pic=Query.gif

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=E
clear.key=Ctrl+E
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�ر�
close.Tip=�ر�
close.M=X
close.key=Alt+F4

close.Action=onClose
close.pic=close.gif

refresh.Type=TMenuItem
refresh.Text=ˢ��
refresh.Tip=ˢ��
refresh.M=R
refresh.key=F5
refresh.Action=onReset
refresh.pic=Refresh.gif





