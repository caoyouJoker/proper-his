Module.item=getPopedom;insert;delete;getStructureList;getStructureForRole;deleteRolePopedom;deleteUserRolePopedom

getPopedom.Type=TSQL
getPopedom.SQL=SELECT COUNT(ROLE_CODE) AS COUNT &
	         FROM SYS_ROLE_POPEDOM &
	        WHERE ROLE_CODE=<ROLE_CODE> AND &
	              GROUP_CODE=<GROUP_CODE> AND &
	              CODE=<CODE>

insert.Type=TSQL
insert.SQL=INSERT INTO SYS_ROLE_POPEDOM (ROLE_CODE,GROUP_CODE,CODE,OPT_USER,OPT_DATE,OPT_TERM)&
		VALUES(<ROLE_CODE>,<GROUP_CODE>,<CODE>,<OPT_USER>,SYSDATE,<OPT_TERM>)
insert.Debug=N

delete.Type=TSQL
delete.SQL=DELETE FROM SYS_ROLE_POPEDOM &
		 WHERE ROLE_CODE=<ROLE_CODE> AND &
	               GROUP_CODE=<GROUP_CODE> AND &
	               CODE=<CODE>

//得到主程列表
getStructureList.Type=TSQL
getStructureList.SQL=SELECT ID,CHN_DESC AS NAME,DATA,STATE FROM SYS_DICTIONARY WHERE ID in (SELECT CODE FROM SYS_ROLE_POPEDOM WHERE GROUP_CODE='SYS_SUBSYSTEM' AND ROLE_CODE=<ROLE_CODE>) AND GROUP_ID='SYS_SUBSYSTEM'
getStructureList.Debug=N


getStructureForRole.Type=TSQL
getStructureForRole.SQL=SELECT GROUP_ID,ID,CHN_DESC AS NAME,TYPE,PARENT_ID,STATE,DATA &
			  FROM SYS_DICTIONARY,SYS_ROLE_POPEDOM &
    			 WHERE GROUP_ID=<GROUP_ID> &
    		           AND GROUP_ID=SYS_ROLE_POPEDOM.GROUP_CODE &
    		           AND ID=SYS_ROLE_POPEDOM.CODE &
        	           AND SYS_ROLE_POPEDOM.ROLE_CODE=<ROLE_CODE> &
    		      ORDER BY SEQ
    		      
    		      
deleteRolePopedom.Type=TSQL
deleteRolePopedom.SQL=DELETE FROM SYS_ROLE_POPEDOM WHERE GROUP_CODE=<GROUP_CODE> AND  CODE=<CODE>

deleteUserRolePopedom.Type=TSQL
deleteUserRolePopedom.SQL=DELETE FROM SYS_ROLE_POPEDOM WHERE GROUP_CODE=<GROUP_CODE> OR CODE=<CODE>
