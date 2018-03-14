<Type=TMenuBar>
UI.Item=File;Window
UI.button=Refresh;exit

Window.Type=TMenu
Window.Text=窗口
Window.zhText=窗口
Window.enText=Window
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.zhText=文件
File.enText=File
File.M=F
File.Item=ModifyPassword;|;exit

ModifyPassword.Type=TMenuItem
ModifyPassword.Text=修改密码
ModifyPassword.zhText=修改密码
ModifyPassword.enText=Modify Password
ModifyPassword.zhTip=修改密码
ModifyPassword.enTip=Modify Password
ModifyPassword.M=P
ModifyPassword.key=
ModifyPassword.Action=onModifyPassword


Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.zhText=刷新
Refresh.enText=Refresh
Refresh.zhTip=重新加载程序
Refresh.enTip=reset
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

exit.Type=TMenuItem
exit.Text=退出
exit.zhText=退出
exit.enText=Exit
exit.zhTip=退出程序
exit.enTip=exit
exit.M=X
exit.key=Alt+F4
exit.Action=onClose
exit.pic=close.gif