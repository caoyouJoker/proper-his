 #
  # Title: ����������
  #
  # Description:����������
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2009-05-06
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;delete;|;query;|;clear;|;toExcel1;|;newPats;|;print;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;delete;|;query;|;clear;|;toExcel1;|;newPats;|;print;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��(Delete)
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

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

export.Type=TMenuItem
export.Text=���ö���
export.Tip=���ö���
execrpt.M=E
export.Action=onExport
export.pic=045.gif

toExcel.Type=TMenuItem
toExcel.Text=���뷢Ʊ��Ϣ
toExcel.zhText=���뷢Ʊ��Ϣ
toExcel.enText=���뷢Ʊ��Ϣ
toExcel.Tip=���뷢Ʊ��Ϣ
toExcel.zhTip=���뷢Ʊ��Ϣ
toExcel.enTip=���뷢Ʊ��Ϣ
toExcel.M=S
toExcel.key=Ctrl+E
toExcel.Action=onImpInvoiceFromXML
toExcel.pic=export.gif

toExcel1.Type=TMenuItem
toExcel1.Text=����װ�䵥
toExcel1.Tip=����װ�䵥
toExcel1.zhTip=����װ�䵥
toExcel1.enTip=����װ�䵥
toExcel1.M=S
toExcel1.Action=onImpExcel
toExcel1.pic=export.gif

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=P
print.Action=onPrint
print.pic=print.gif

newPats.Type=TMenuItem
newPats.Text=����װ�䵥
newPats.Tip=����װ�䵥
newPats.M=Q
newPats.key=
newPats.Action=onInsertPatByExl
newPats.pic=002.gif