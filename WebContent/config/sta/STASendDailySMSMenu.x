# 
#  Title:日结报表短信发送平台menu
# 
#  Description:日结报表短信发送平台menu
# 
#  Copyright: Copyright (c) Javahis 2014
# 
#  author wangbin 2014.7.14
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=generate|;sms|;clear|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=generate|;sms|;clear|;close


generate.Type=TMenuItem
generate.Text=生成数据
generate.Tip=生成数据(Alt+G)
generate.M=G
generate.key=Alt+G
generate.Action=onGenerate
generate.pic=inscon.gif


sms.Type=TMenuItem
sms.Text=发短信
sms.Tip=发短信
sms.M=M
sms.key=Ctrl+M
sms.Action=onSendSMS
sms.pic=014.gif


Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新(F5)
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

