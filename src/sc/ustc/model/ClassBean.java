package sc.ustc.model;

import java.util.List;

/**
 * Creator: hfang
 * Date: 2019/01/05 20:29
 * Description:
 **/

public class ClassBean {
    private String beanName;
    private String tableName;
    private String ID;
    private List<PropertyBean> propertyList;

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getId() {
        return ID;
    }

    public void setId(String ID) {
        this.ID = ID;
    }

    public List<PropertyBean> getPropertyList() {
        return propertyList;
    }

    public void setPropertyList(List<PropertyBean> propertyList) {
        this.propertyList = propertyList;
    }
}
