Module.item=initdiatree;initordertree

//≤È—Ø
initdiatree.Type=TSQL
initdiatree.SQL=SELECT ID AS ID,CHN_DESC AS NAME ,ENG_DESC AS TAG FROM SYS_DICTIONARY WHERE GROUP_ID='OPDDIATREE' ORDER BY SEQ
initdiatree.Debug=N

//≤È—Ø
initordertree.Type=TSQL
initordertree.SQL=SELECT ID AS ID,CHN_DESC AS NAME ,ENG_DESC AS TAG FROM SYS_DICTIONARY WHERE GROUP_ID='OPDORDERTREE' ORDER BY SEQ
initordertree.Debug=N