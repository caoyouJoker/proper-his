# 
#  Title:介入室医生-胸痛中心病历
# 
#  Description:CT检伤
# 
#  Copyright: Copyright (c) Javahis 2017
# 
#  author WangQing 2017.2.22
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=Query;save;clear;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;Refresh;close

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif


save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif


close.Type=TMenuItem
close.Text=关闭
close.Tip=关闭
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
