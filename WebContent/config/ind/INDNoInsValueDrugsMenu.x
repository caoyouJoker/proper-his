  #
  # Title: ��ҽ������ҩƷ�ص���Ʒ��
  #
  # Description: ��ҽ������ҩƷ�ص���Ʒ��
  #
  # Copyright: ProperSoft (c) 2016
  #
  # @author wukai on 20161026
  # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;print;export;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;print;export;|;clear;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=P
print.key=F4
print.Action=onPrint
print.pic=print.gif

export.Type=TMenuItem
export.Text=����Excel
export.Tip=����Excel(Alt+E)
export.M=G
export.key=Alt+E
export.Action=onExport
export.pic=exportexcel.gif