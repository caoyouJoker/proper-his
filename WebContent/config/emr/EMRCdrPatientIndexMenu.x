<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;showpat;|;showPatDetail;|;cxMrshow;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;showpat;|;showPatDetail;|;cxMrshow;|;clear;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

cxMrshow.Type=TMenuItem
cxMrshow.Text=时间轴CDR
cxMrshow.Tip=时间轴CDR(Ctrl+Q)
cxMrshow.M=Q
cxMrshow.key=Ctrl+Q
cxMrshow.Action=onCxShow
cxMrshow.pic=038.gif

showpat.Type=TMenuItem
showpat.Text=综合信息
showpat.zhText=综合信息
showpat.enText=Pat Info
showpat.Tip=综合信息
showpat.zhTip=综合信息
showpat.enTip=Pat Info
showpat.M=P
showpat.key=Ctrl+P
showpat.Action=onQuerySummaryInfo
showpat.pic=patlist.gif

showPatDetail.Type=TMenuItem
showPatDetail.Text=单次就诊
showPatDetail.zhText=单次就诊
showPatDetail.enText=Pat Profile
showPatDetail.Tip=单次就诊
showPatDetail.zhTip=单次就诊
showPatDetail.enTip=Pat Profile
showPatDetail.M=
showPatDetail.key=
showPatDetail.Action=onQueryMedRecord
showPatDetail.pic=pat.gif