<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;showpat;|;showPatDetail;|;cxMrshow;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;showpat;|;showPatDetail;|;cxMrshow;|;clear;|;close

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

cxMrshow.Type=TMenuItem
cxMrshow.Text=ʱ����CDR
cxMrshow.Tip=ʱ����CDR(Ctrl+Q)
cxMrshow.M=Q
cxMrshow.key=Ctrl+Q
cxMrshow.Action=onCxShow
cxMrshow.pic=038.gif

showpat.Type=TMenuItem
showpat.Text=�ۺ���Ϣ
showpat.zhText=�ۺ���Ϣ
showpat.enText=Pat Info
showpat.Tip=�ۺ���Ϣ
showpat.zhTip=�ۺ���Ϣ
showpat.enTip=Pat Info
showpat.M=P
showpat.key=Ctrl+P
showpat.Action=onQuerySummaryInfo
showpat.pic=patlist.gif

showPatDetail.Type=TMenuItem
showPatDetail.Text=���ξ���
showPatDetail.zhText=���ξ���
showPatDetail.enText=Pat Profile
showPatDetail.Tip=���ξ���
showPatDetail.zhTip=���ξ���
showPatDetail.enTip=Pat Profile
showPatDetail.M=
showPatDetail.key=
showPatDetail.Action=onQueryMedRecord
showPatDetail.pic=pat.gif