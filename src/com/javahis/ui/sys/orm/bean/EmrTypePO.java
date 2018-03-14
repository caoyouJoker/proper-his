package com.javahis.ui.sys.orm.bean;

import com.javahis.ui.sys.orm.annotation.Column;
import com.javahis.ui.sys.orm.annotation.PKey;
import com.javahis.ui.sys.orm.annotation.Table;
import com.javahis.ui.sys.orm.tools.Type;



/**
 *
 * @author whaosoft
 *
 */
@Table(tableName = "EMR_NEW_TYPE")
public class EmrTypePO extends BasePOJO{

    @PKey(name = "ID", type = Type.CHAR)
    public String id = null;

    @Column(name = "NAME", type = Type.CHAR)
    public String name = null;

}
