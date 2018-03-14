<Type=TMenuBar>
UI.Item=File;Window
UI.button=PrintShow;|;exit

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.zhText=文件
File.enText=File
File.M=F
File.Item=exit

PrintShow.type=TMenuItem
PrintShow.Text=打印
PrintShow.zhText=打印
PrintShow.enText=Print
PrintShow.Tip=打印
PrintShow.zhTip=打印
PrintShow.enTip=Print
PrintShow.M=P
PrintShow.key=
PrintShow.Action=onPrint
PrintShow.pic=print.gif

exit.Type=TMenuItem
exit.Text=关闭
exit.zhText=关闭
exit.enText=Quit
exit.Tip=关闭
exit.zhTip=关闭
exit.enTip=Quit
exit.M=C
exit.key=Alt+F4
exit.Action=onClose
exit.pic=close.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif
