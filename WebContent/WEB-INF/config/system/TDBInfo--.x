Pools.item=javahis

//住连接池名称默认MAIN
Pools.MainPool=javahis

//连接 oracle sql db2 catch
javahis.Type=oracle
javahis.Address=192.168.1.110
javahis.DBName=orcl
javahis.UserName=JAVAHIS
javahis.Password=javahis

//延时关闭连接时间(秒) 0不出发延时关闭
javahis.CloseTime=0

//默认初始化连接数
javahis.DefaultConnectCount=30
//自动恢复到初始连接值
javahis.isResumeConnectCount=Y

//超长时间
javahis.sqltime=50

//运行缓慢的SQL保存的日志
javahis.sqllog=C:\JavaHis\logs\dbTime.log

//优化开关
javahis.checkOFF=Y

//对象吞吐量
javahis.checkObjCount=100

//优化行数
javahis.checkRowCount=1000

//休眠时间
javahis.checkSheepTime=100

//监听时间
javahis.checkTime=300