package sc.ustc.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Creator: hfang
 * Date: 2019/01/06 14:56
 * Description:
 **/

public class DepedencyBean {
    private String ID;
    private String clazz;
    private List<FieldBean> fieldBeanList;


    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public List<FieldBean> getFieldBeanList() {
        return fieldBeanList;
    }

    public void setFieldBeanList(List<FieldBean> fieldBeanList) {
        this.fieldBeanList = fieldBeanList;
    }

    public void addFieldBeanList(FieldBean fieldBean) {
        if(fieldBeanList==null) {
            fieldBeanList = new ArrayList<>();
        }
        fieldBeanList.add(fieldBean);
    }
}
