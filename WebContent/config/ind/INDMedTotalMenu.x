 #
  # Title: סԺҩ������ͳ�Ʊ���
  #
  # Description: סԺҩ������ͳ�Ʊ���
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author lij 2017.05.18
 # @version 1.0

<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;clear;|;export;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;clear;|;export;|;close

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

export.Type=TMenuItem
export.Text=����
export.Tip=����
export.M=E
export.key=Ctrl+E
export.Action=onExport
export.pic=export.gif

//print.Type=TMenuItem
//print.Text=��ӡ
//print.Tip=��ӡ
//print.M=P
//print.key=Ctrl+P
//print.Action=onPrint
//print.pic=print.gif