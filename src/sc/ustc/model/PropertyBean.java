package sc.ustc.model;

/**
 * Creator: hfang
 * Date: 2019/01/05 20:31
 * Description:
 **/

public class PropertyBean {
    private String filedName;
    private String columnName;
    private String type;
    private Boolean lazy;

    public String getFiledName() {
        return filedName;
    }
    public void setFiledName(String filedName) {
        this.filedName = filedName;
    }
    public String getColumnName() {
        return columnName;
    }
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public Boolean getLazy() {
        return lazy;
    }
    public void setLazy(Boolean lazy) {
        this.lazy = lazy;
    }

}
