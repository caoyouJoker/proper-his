 #
  # Title: �Ž�������������ϰ
  #
  # Description: �Ž�������������ϰ
  #
  # Copyright: JavaHis (c) 2011
  #
  # @author Zhangjg 2011.04.11
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;handle;undo;query;picture;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;handle;undo;query;picture;clear;|;close

save.Type=TMenuItem
save.Text=�ϲ�
save.Tip=�ϲ�
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

picture.Type=TMenuItem
picture.Text=��������֤
picture.Tip=��������֤
picture.M=
picture.key=
picture.Action=onIdCardNo
picture.pic=idcard.gif

handle.Type=TMenuItem
handle.Text=����
handle.Tip=����
handle.M=H
handle.key=Ctrl+H
handle.Action=onHandle
handle.pic=execute.gif

undo.Type=TMenuItem
undo.Text=����
undo.Tip=����
undo.M=U
undo.key=Ctrl+U
undo.Action=onUndo
undo.pic=Undo.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif