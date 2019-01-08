package sc.ustc.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import sc.ustc.dao.Configuration;
import sc.ustc.dao.Conversation;
import sc.ustc.model.ConstRepo;
import sc.ustc.model.PropertyBean;
import sc.ustc.util.CommonUtil;

import javax.sql.rowset.CachedRowSet;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.List;

/**
 * Creator: hfang
 * Date: 2019/01/07 18:54
 * Description:
 **/

public class BeanProxy implements MethodInterceptor {
    private List<PropertyBean> lazyLoadList;
    private String table;
    private Enhancer enhancer = new Enhancer();

    public BeanProxy(List<PropertyBean> lazyLoadList, String table) {
        this.lazyLoadList = lazyLoadList;
        this.table = table;
    }

    public Object getProxy(Class clazz) {
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(this);

        return enhancer.create();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        if (method.getName().startsWith("get") && methodProxy.invokeSuper(o, args) == null) {
            System.out.println("Need to load lazy value:" + method.getName());
            String fieldName = method.getName().substring(3);

            CachedRowSet rowSet = null;
            for (PropertyBean propertyBean : lazyLoadList) {
                if (propertyBean.getFiledName().toUpperCase().contains(fieldName.toUpperCase())) {
                    rowSet = Conversation.query(o, table, propertyBean.getColumnName());
                }
            }

            if(rowSet==null) {
                return null;
            } else {
                CommonUtil.resultSetToBean(rowSet, o, lazyLoadList, ConstRepo.DEFAULT_KEY);
            }
            Method mt = o.getClass().getMethod(method.getName());
            String result = (String) mt.invoke(o);
            return result;
        } else {
            return methodProxy.invokeSuper(o, args);
        }
    }

    public List<PropertyBean> getLazyLoadList() {
        return lazyLoadList;
    }

    public void setLazyLoadList(List<PropertyBean> lazyLoadList) {
        this.lazyLoadList = lazyLoadList;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }
}
