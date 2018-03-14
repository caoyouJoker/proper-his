#############################################
# <p>Title:临时解锁 </p>
#
# <p>Description:临时解锁 </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company:Javahis </p>
#
# @author yanmm
# @version 1.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;idpicture;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;idpicture;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

idpicture.Type=TMenuItem
idpicture.Text=身份证照片
idpicture.Tip=身份证照片
idpicture.M=
idpicture.key=
idpicture.Action=onIdentificationPic
idpicture.pic=Picture.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

