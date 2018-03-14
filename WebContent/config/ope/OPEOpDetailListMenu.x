#############################################
# <p>Title:手术记录列表Menu </p>
#
# <p>Description:手术记录列表Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK 2009.09.28
# @version 4.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=return;|;close

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
File.Item=return;|;close

return.Type=TMenuItem
return.Text=传回
return.zhText=传回
return.enText=
return.Tip=传回
return.zhTip=传回
return.enTip=
return.M=
return.key=
return.Action=onReturn
return.pic=Undo.gif

close.Type=TMenuItem
close.Text=退出
close.zhText=退出
close.enText=Quit
close.Tip=退出(Alt+F4)
close.zhTip=退出
close.enTip=Quit
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
