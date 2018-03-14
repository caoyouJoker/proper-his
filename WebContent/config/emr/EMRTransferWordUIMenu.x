<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;fromuser;|;touser;|;exit

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

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

fromuser.Type=TMenuItem
fromuser.Text=交班人签字
fromuser.Tip=交班人签字
fromuser.M=S
fromuser.key=Ctrl+S
fromuser.Action=onFromuser
fromuser.pic=bedcard.gif

touser.Type=TMenuItem
touser.Text=接班人签字
touser.Tip=接班人签字
touser.M=S
touser.key=Ctrl+S
touser.Action=onTouser
touser.pic=bedcard.gif


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
