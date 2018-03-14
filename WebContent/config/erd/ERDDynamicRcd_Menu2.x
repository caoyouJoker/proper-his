<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;query;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.enTip=Window
Window.enText=Window
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.enTip=File
File.enText=File
File.M=F
File.Item=save;delete;Refresh;query;|;clear;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.enTip=save
save.enText=save
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.enTip=query
query.enText=query
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.enText=Empty
clear.enTip=Empty
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.enTip=Quit
close.enText=Quit
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif