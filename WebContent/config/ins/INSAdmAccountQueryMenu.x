 #
  # Title: סԺҽ���ۺ϶��˲�ѯ
  #
  # Description:סԺҽ���ۺ϶��˲�ѯ
  #
  # Copyright: JavaHis (c) 2009
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;export;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;export;|;clear;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.Action=onQuery
query.pic=query.gif


clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

export.Type=TMenuItem
export.Text=���
export.Tip=���
execrpt.M=E
export.Action=onExport
export.pic=export.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
