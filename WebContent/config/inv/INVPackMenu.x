 #
  # Title: ��Ӧ�����
  #
  # Description:��Ӧ�����
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author fudw
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;new;|;delete;|;query;|;clear;|;picture;|;PicUpload;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;new;|;delete;|;query;|;clear;|;picture;|;PicUpload;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onUpdate
save.pic=save.gif

new.Type=TMenuItem
new.Text=����
new.Tip=����
new.M=N
new.key=Ctrl+N
new.Action=onNew
new.pic=new.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif


query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

picture.Type=TMenuItem
picture.Text=�鿴ͼƬ
picture.Tip=�鿴ͼƬ
picture.M=J
picture.key=Ctrl+J
picture.Action=onPicture
picture.pic=Picture.gif

PicUpload.Type=TMenuItem
PicUpload.Text=�ϴ�ͼƬ
PicUpload.Tip=�ϴ�ͼƬ
PicUpload.M=U
PicUpload.key=Ctrl+U
PicUpload.Action=onImportPicture
PicUpload.pic=022.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif



clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif