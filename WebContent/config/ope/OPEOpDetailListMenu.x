#############################################
# <p>Title:������¼�б�Menu </p>
#
# <p>Description:������¼�б�Menu </p>
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
Window.Text=����
Window.zhText=����
Window.enText=Window
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.zhText=�ļ�
File.enText=File
File.M=F
File.Item=return;|;close

return.Type=TMenuItem
return.Text=����
return.zhText=����
return.enText=
return.Tip=����
return.zhTip=����
return.enTip=
return.M=
return.key=
return.Action=onReturn
return.pic=Undo.gif

close.Type=TMenuItem
close.Text=�˳�
close.zhText=�˳�
close.enText=Quit
close.Tip=�˳�(Alt+F4)
close.zhTip=�˳�
close.enTip=Quit
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
