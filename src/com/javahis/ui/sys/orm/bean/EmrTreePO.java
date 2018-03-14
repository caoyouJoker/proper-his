package com.javahis.ui.sys.orm.bean;

import java.math.BigDecimal;

import com.javahis.ui.sys.orm.annotation.Column;
import com.javahis.ui.sys.orm.annotation.PKey;
import com.javahis.ui.sys.orm.annotation.Table;
import com.javahis.ui.sys.orm.tools.Type;



/**
 *
 * @author whaosoft
 *
 */
@Table(tableName = "EMR_NEW_TREE")
public class EmrTreePO extends BasePOJO{

    @PKey(name = "ID", type = Type.CHAR)
    public String id = null;

    @Column(name = "NAME", type = Type.CHAR)
    public String name = null;

    @Column(name = "PARENT_ID", type = Type.CHAR)
    public String parentId = null;

    @Column(name = "TYPE_ID", type = Type.CHAR)
    public String typeId = null;

    @Column(name = "CLASS_CODE", type = Type.CHAR)
    public String classCode = null;

    @Column(name = "T_STATUS", type = Type.CHAR)
    public String tStatus = null;

    @Column(name = "T_SEQ", type = Type.NUM)
    public BigDecimal tSeq = null;



    /**
     *
     */
    public EmrTreePO(){

    }

    /**
     *
     * @param id
     * @param name
     */
	public EmrTreePO(String id, String name) {

		this.id = id;
		this.name = name;
	}
}
