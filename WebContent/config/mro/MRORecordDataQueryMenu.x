#
# Title: ������ҳ���ݲ�ѯͳ�Ʋ˵�
#
# Description:������ҳ���ݲ�ѯͳ�Ʋ˵�
#
# Copyright: JavaHis (c) 2018
#
# @author wangb 2018.1.16
# @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;clear;|;export;|;close

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;clear;|;export;|;close

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
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

export.Type=TMenuItem
export.Text=����Excel
export.Tip=����(Ctrl+O)
export.M=O
export.key=Ctrl+O
export.Action=onExport
export.pic=exportexcel.gif