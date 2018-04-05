#####################################################
# <p>Title:预约住院Menu </p>
#
# <p>Description: 预约住院Menu</p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK
# @version 1.0
#####################################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;new;|;Wrist;|;cancel;|;resv;|;close

Window.Type=TMenu
Window.Text=窗口
Window.enTip=Window
Window.enText=Window
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.enText=File
File.enTip=File
File.M=F
File.Item=save;|;new;|;Wrist;|;cancel;|;resv;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.enTip=save
save.enText=save
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

new.Type=TMenuItem
new.Text=新增
new.Tip=新增(Ctrl+N)
new.M=N
new.key=Ctrl+N
new.Action=onNew
new.pic=new.gif


Wrist.Type=TMenuItem
Wrist.Text=打印腕带
Wrist.Tip=打印腕带
Wrist.M=
Wrist.key=
Wrist.Action=onWrist
Wrist.pic=print-1.gif

cancel.Type=TMenuItem
cancel.Text=取消注册
cancel.Tip=取消注册
cancel.M=
cancel.key=
cancel.Action=onCancel
cancel.pic=030.gif


close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.enTip=Log out
close.enText=Log out
close.M=X
close.key=Alt+F4
close.Action=onClosePanel
close.pic=close.gif

resv.Type=TMenuItem
resv.Text=重新入院
resv.Tip=重新入院
resv.M=
resv.key=
resv.Action=onResv
resv.pic=Redo.gif



