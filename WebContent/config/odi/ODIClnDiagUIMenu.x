<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;delete;|;card;|;deptcln;|;drcln;|;history;|;close

Window.Type=TMenu
Window.Text=窗口
Window.zhText=窗口
Window.enText=Window
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.zhText=文件
File.enText=File
File.M=F
File.Item=save;|;delete;|;card;|;deptcln;|;drcln;|;history;|;close

save.Type=TMenuItem
save.Text=保存
save.zhText=保存
save.enText=Save
save.Tip=保存(Ctrl+S)
save.zhTip=保存(Ctrl+S)
save.enTip=Save(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

delete.Type=TMenuItem
delete.Text=删除
delete.zhText=删除
delete.enText=Delete
delete.Tip=删除
delete.zhTip=删除
delete.enTip=Delete
delete.M=D
delete.Action=onDelete
delete.pic=delete.gif

card.Type=TMenuItem
card.Text=传染病报告卡
card.zhText=传染病报告卡
card.enText=DiseaseCard
card.Tip=传染病报告卡
card.zhTip=传染病报告卡
card.enTip=DiseaseCard
card.M=C
card.Action=onCard
card.pic=card.gif

deptcln.Type=TMenuItem
deptcln.Text=科诊断
deptcln.zhText=科诊断
deptcln.enText=DeptDiag
deptcln.Tip=科诊断
deptcln.zhTip=科诊断
deptcln.enTip=DeptDiag
deptcln.M=C
deptcln.Action=onDeptCln
deptcln.pic=emr-1.gif

drcln.Type=TMenuItem
drcln.Text=医师诊断
drcln.zhText=医师诊断
drcln.enText=DrDiag
drcln.Tip=医师诊断
drcln.zhTip=医师诊断
drcln.enTip=DrDiag
drcln.M=C
drcln.Action=onDrCln
drcln.pic=emr-2.gif

history.Type=TMenuItem
history.Text=操作历史
history.zhText=操作历史
history.enText=OpHistory
history.Tip=操作历史
history.zhTip=操作历史
history.enTip=OpHistory
history.M=H
history.Action=onOpHistory
history.pic=043.gif

close.Type=TMenuItem
close.Text=退出
close.zhText=退出
close.enText=Close
close.Tip=退出(Alt+F4)
close.zhTip=退出(Alt+F4)
close.enTip=Close(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

