<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;examine;examineCancel;addFile;excel;|;fileOK;fileCancel;|;signPdf;|;showSignPdf;|;emr;|;close

File.Type=TMenu
File.Text=文件
File.zhText=文件
File.enText=File
File.M=F
File.Item=query;|;examine;examineCancel;|;addFile;excel;|;fileOK;fileCancel;|;signPdf;|;showSignPdf;|;emr;|;close

query.Type=TMenuItem
query.Text=查询
query.zhText=查询
query.enText=Query
query.Tip=查询
query.zhTip=查询
query.enTip=Query
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

addFile.Type=TMenuItem
addFile.Text=合并病案首页
addFile.zhText=合并病案首页
addFile.enText= 合并病案首页
addFile.Tip=合并病案首页
addFile.zhTip=合并病案首页
addFile.enTip=合并病案首页
addFile.M=Q
addFile.key=Ctrl+H
addFile.Action=onAddFile
addFile.pic=039.gif

examine.Type=TMenuItem
examine.Text=审核通过
examine.zhText=审核通过
examine.enText= 审核通过
examine.Tip=审核通过
examine.zhTip=审核通过
examine.enTip=审核通过
examine.M=Q
examine.key=Ctrl+F
examine.Action=onExamine
examine.pic=022.gif


examineCancel.Type=TMenuItem
examineCancel.Text=审核退回
examineCancel.zhText=审核退回
examineCancel.enText=onexamineCancel
examineCancel.Tip=审核退回
examineCancel.zhTip=审核退回
examineCancel.M=C
examineCancel.Action=onExamineCancel
examineCancel.pic=027.gif


fileOK.Type=TMenuItem
fileOK.Text=归档通过
fileOK.zhText=归档通过
fileOK.enText= 归档通过
fileOK.Tip=归档通过
fileOK.zhTip=归档通过
fileOK.enTip=归档通过
fileOK.M=Q
fileOK.Action=onFileOK
fileOK.pic=007.gif


fileCancel.Type=TMenuItem
fileCancel.Text=归档退回
fileCancel.zhText=归档退回
fileCancel.enText=onfileCancel
fileCancel.Tip=归档退回
fileCancel.zhTip=归档退回
fileCancel.M=C
fileCancel.Action=onFileCancel
fileCancel.pic=027.gif

close.Type=TMenuItem
close.Text=退出
close.zhText=退出
close.enText=Quit
close.Tip=退出
close.zhTip=退出
close.enTip=Quit
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

excel.Type=TMenuItem
excel.Text=汇出Excel
excel.Tip=汇出Ctrl+E)
excel.M=E
excel.key=Ctrl+E
excel.Action=onExecl
excel.pic=exportexcel.gif

signPdf.Type=TMenuItem
signPdf.Text=签章
signPdf.Tip=签章
signPdf.Action=signPdf
signPdf.pic=emr-2.gif

emr.Type=TMenuItem
emr.Text=病历封存
emr.zhText=病历封存
emr.enText=病历封存
emr.Tip=病历封存
emr.zhTip=病历封存
emr.enTip=病历封存
emr.M=S
emr.Action=onSealed
emr.pic=emr-1.gif

showSignPdf.Type=TMenuItem
showSignPdf.Text=签章显示
showSignPdf.Tip=签章显示
showSignPdf.Action=showSignPdf
showSignPdf.pic=005.gif
