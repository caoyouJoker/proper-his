Module.item=query;delete;update;insert

query.TYPE=TSQL
query.SQL=SELECT SUPTYPE_CODE,SUPTYPE_DESC,PY1,PY2,DESCRIPTION,PACK_MODE,TYPE_FLG,OPT_USER,OPT_DATE,OPT_TERM &
           FROM INV_SUPTYPE ORDER BY SUPTYPE_CODE
query.ITEM=SUPTYPE_CODE;PACK_MODE;TYPE_FLG
query.SUPTYPE_CODE=SUPTYPE_CODE=<SUPTYPE_CODE>
query.PACK_MODE=PACK_MODE=<PACK_MODE>
query.TYPE_FLG=TYPE_FLG=<TYPE_FLG>
query.Debug=N


delete.TYPE=TSQL
delete.SQL=DELETE INV_SUPTYPE WHERE SUPTYPE_CODE=<SUPTYPE_CODE>


update.TYPE=TSQL
update.SQL=UPDATE INV_SUPTYPE SET SUPTYPE_DESC=<SUPTYPE_DESC>,PY1=<PY1>,PY2=<PY2>, &
           DESCRIPTION=<DESCRIPTION>,PACK_MODE=<PACK_MODE>,TYPE_FLG=<TYPE_FLG>, &
           OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
           WHERE SUPTYPE_CODE=<SUPTYPE_CODE>

insert.TYPE=TSQL
insert.SQL=INSERT INTO INV_SUPTYPE(SUPTYPE_CODE,SUPTYPE_DESC,PY1,PY2,DESCRIPTION,PACK_MODE,TYPE_FLG,OPT_USER,OPT_DATE,OPT_TERM) &
           VALUES(<SUPTYPE_CODE>,<SUPTYPE_DESC>,<PY1>,<PY2>,<DESCRIPTION>,<PACK_MODE>,<TYPE_FLG>,<OPT_USER>,SYSDATE,<OPT_TERM>)