# 
#  Title:��������ʹ����
# 
#  Description:��������ʹ����
# 
#  Copyright: Copyright (c) Javahis 2015
# 
#  author wangqing 20180119
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;close

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

test.Type=TMenuItem
test.Text=����
test.Tip=����
test.M=E
test.key=Ctrl+E
test.Action=onTest
test.pic=clear.gif



