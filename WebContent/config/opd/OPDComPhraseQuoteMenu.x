<Type=TMenuBar>
UI.Item=File;Window
UI.button=setback;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=setback;|;close

setback.Type=TMenuItem
setback.Text=刷新
setback.Tip=刷新
setback.M=R
setback.key=F5
setback.Action=onReset
setback.pic=Refresh.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif