#############################################
# <p>Title:��ʱ���� </p>
#
# <p>Description:��ʱ���� </p>
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
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;idpicture;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

idpicture.Type=TMenuItem
idpicture.Text=���֤��Ƭ
idpicture.Tip=���֤��Ƭ
idpicture.M=
idpicture.key=
idpicture.Action=onIdentificationPic
idpicture.pic=Picture.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

