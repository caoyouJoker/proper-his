 #
  # Title: ����������
  #
  # Description:����������
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author wangm 2013-06-15
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;new;|;query;|;delete;|;print;|;barcode;|;clear;|;picture;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;query;|;delete;|;print;|;barcode;|;clear;|;picture;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

new.Type=TMenuItem
new.Text=����
new.Tip=����
new.M=N
new.key=Ctrl+N
new.Action=onNew
new.pic=new.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��(Delete)
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

barcode.Type=TMenuItem
barcode.Text=����
barcode.Tip=��ӡ����
barcode.M=T
barcode.key=Ctrl+T
barcode.Action=onBarcode
barcode.pic=barcode.gif

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif

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

picture.Type=TMenuItem
picture.Text=�鿴ͼƬ
picture.Tip=�鿴ͼƬ
picture.M=J
picture.key=Ctrl+J
picture.Action=onPicture
picture.pic=Picture.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif