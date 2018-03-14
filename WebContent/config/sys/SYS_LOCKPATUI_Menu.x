<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;query;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;query;|;clear;|;close

save.Type=TMenuItem
save.Text=解开
save.Tip=解开(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=026.gif

query.Type=TMenuItem
query.Text=重新整理
query.Tip=重新整理
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=tempsave.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

clear.Type=TMenuItem
clear.Text=全部解开
clear.Tip=全部解开(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=034.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

