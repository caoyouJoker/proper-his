<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;examine;examineCancel;addFile;excel;|;fileOK;fileCancel;|;signPdf;|;showSignPdf;|;emr;|;close

File.Type=TMenu
File.Text=�ļ�
File.zhText=�ļ�
File.enText=File
File.M=F
File.Item=query;|;examine;examineCancel;|;addFile;excel;|;fileOK;fileCancel;|;signPdf;|;showSignPdf;|;emr;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.zhText=��ѯ
query.enText=Query
query.Tip=��ѯ
query.zhTip=��ѯ
query.enTip=Query
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

addFile.Type=TMenuItem
addFile.Text=�ϲ�������ҳ
addFile.zhText=�ϲ�������ҳ
addFile.enText= �ϲ�������ҳ
addFile.Tip=�ϲ�������ҳ
addFile.zhTip=�ϲ�������ҳ
addFile.enTip=�ϲ�������ҳ
addFile.M=Q
addFile.key=Ctrl+H
addFile.Action=onAddFile
addFile.pic=039.gif

examine.Type=TMenuItem
examine.Text=���ͨ��
examine.zhText=���ͨ��
examine.enText= ���ͨ��
examine.Tip=���ͨ��
examine.zhTip=���ͨ��
examine.enTip=���ͨ��
examine.M=Q
examine.key=Ctrl+F
examine.Action=onExamine
examine.pic=022.gif


examineCancel.Type=TMenuItem
examineCancel.Text=����˻�
examineCancel.zhText=����˻�
examineCancel.enText=onexamineCancel
examineCancel.Tip=����˻�
examineCancel.zhTip=����˻�
examineCancel.M=C
examineCancel.Action=onExamineCancel
examineCancel.pic=027.gif


fileOK.Type=TMenuItem
fileOK.Text=�鵵ͨ��
fileOK.zhText=�鵵ͨ��
fileOK.enText= �鵵ͨ��
fileOK.Tip=�鵵ͨ��
fileOK.zhTip=�鵵ͨ��
fileOK.enTip=�鵵ͨ��
fileOK.M=Q
fileOK.Action=onFileOK
fileOK.pic=007.gif


fileCancel.Type=TMenuItem
fileCancel.Text=�鵵�˻�
fileCancel.zhText=�鵵�˻�
fileCancel.enText=onfileCancel
fileCancel.Tip=�鵵�˻�
fileCancel.zhTip=�鵵�˻�
fileCancel.M=C
fileCancel.Action=onFileCancel
fileCancel.pic=027.gif

close.Type=TMenuItem
close.Text=�˳�
close.zhText=�˳�
close.enText=Quit
close.Tip=�˳�
close.zhTip=�˳�
close.enTip=Quit
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

excel.Type=TMenuItem
excel.Text=���Excel
excel.Tip=���Ctrl+E)
excel.M=E
excel.key=Ctrl+E
excel.Action=onExecl
excel.pic=exportexcel.gif

signPdf.Type=TMenuItem
signPdf.Text=ǩ��
signPdf.Tip=ǩ��
signPdf.Action=signPdf
signPdf.pic=emr-2.gif

emr.Type=TMenuItem
emr.Text=�������
emr.zhText=�������
emr.enText=�������
emr.Tip=�������
emr.zhTip=�������
emr.enTip=�������
emr.M=S
emr.Action=onSealed
emr.pic=emr-1.gif

showSignPdf.Type=TMenuItem
showSignPdf.Text=ǩ����ʾ
showSignPdf.Tip=ǩ����ʾ
showSignPdf.Action=showSignPdf
showSignPdf.pic=005.gif
