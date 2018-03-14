# 
#  Title:CT检伤
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
UI.button=query;save;clear;close

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;save;refresh;clear;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=refresh

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=X
query.key=Alt+F4
query.Action=onQuery
query.pic=Query.gif

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=E
clear.key=Ctrl+E
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=关闭
close.Tip=关闭
close.M=X
close.key=Alt+F4

close.Action=onClose
close.pic=close.gif

refresh.Type=TMenuItem
refresh.Text=刷新
refresh.Tip=刷新
refresh.M=R
refresh.key=F5
refresh.Action=onReset
refresh.pic=Refresh.gif





