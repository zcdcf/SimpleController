package sc.ustc.dao;

import sc.ustc.model.ClassBean;
import sc.ustc.model.PropertyBean;
import sc.ustc.proxy.BeanProxy;
import sc.ustc.util.CommonUtil;
import sc.ustc.util.ProduceTimeFormatted;

import javax.sql.PooledConnection;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

/**
 * Creator: hfang
 * Date: 2019/01/05 20:57
 * Description:
 **/

public class Conversation {
    private static final String TAG = ProduceTimeFormatted.getCurrentTime()+"sc.ustc.dao.Conversation:";
    private static Map<String,String> JDBCConfig;
    private static List<ClassBean> classBeanList;
    private static List<PropertyBean> propertyBeanList;
    private static String key;

    public Conversation() {
    }

    public static CachedRowSet query(Object obj, String table, String field) throws SQLException, ClassNotFoundException {
        PropertyDescriptor pds[];
        String ID = null;
        try {
            pds = Introspector.getBeanInfo(obj.getClass()).getPropertyDescriptors();
            for (PropertyDescriptor pd : pds) {
                System.out.println("property " + pd.getName());
                if (pd.getName().contains("ID")) {
                    Method md = pd.getReadMethod();// 获取get方法
                    ID = (String) md.invoke(obj);
                    System.out.println(TAG+"id="+ID);
                    break;
                }
            }
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        Connection connection = getJDBConnection();
        Statement statement = connection.createStatement();
        // default use ID to search
        String sql = "select "+field+" from "+table+" WHERE "+key+"=\""+ID+"\"";
        System.out.println(TAG+"query sql:"+sql);
        ResultSet resultSet = statement.executeQuery(sql);

        RowSetFactory factory = RowSetProvider.newFactory();
        CachedRowSet  rowSet= factory.createCachedRowSet();
        rowSet.populate(resultSet);
        closeJDBConnection(connection);
        return rowSet;
    }

    public static void setJDBCConfig(Map<String, String> JDBCConfig) {
        Conversation.JDBCConfig = JDBCConfig;
    }

    public static void setClassBeanList(List<ClassBean> classBeanList) {
        Conversation.classBeanList = classBeanList;
    }

    public static void setKey(String key) {
        Conversation.key = key;
    }

    public Object getObject(Object obj, String field) {
        Class<?> clas = obj.getClass();
        System.out.println(TAG+clas.getName());

        ClassBean clasBean = null;
        // find if query bean is in the or_mapping bean list
        for ( ClassBean classBean : classBeanList ) {
            System.out.println(classBean.getBeanName());
            String str = clas.getName();
            String clasName = str.substring(str.lastIndexOf(".")+1);
            System.out.println(TAG+clasName);
            if ( classBean.getBeanName().equals( clasName ) ) {
                System.out.println(TAG+"found");
                clasBean = classBean;
                propertyBeanList = classBean.getPropertyList();
                break;
            }
        }

        if ( clasBean == null ) {
            System.out.println("映射错误");
            return null;
        }

        // resolve lazy property
        List<PropertyBean> lazyPropertyList = new ArrayList<>();
        StringBuilder builder = new StringBuilder("");
        for (PropertyBean propertyBean : propertyBeanList) {
            if(!propertyBean.getLazy()) {
                builder.append(propertyBean.getColumnName());
                builder.append(",");
            } else {
                lazyPropertyList.add(propertyBean);
            }
        }
        builder.append(key);
        String queriedColumn = builder.toString().substring(0,builder.length());

        String table = clasBean.getTableName();
        List<PropertyBean> propertyList = clasBean.getPropertyList();
        // store variant name and value in query bean
        Map<String,String> map = new HashMap<String, String>();
        map.put("table", table);
        String columnName = null;
        for (PropertyBean property : propertyList) {
            // get getter of the variant
            String fieldName =  property.getFiledName();
            System.out.println(TAG+fieldName);
            String rear = Character.toUpperCase(fieldName.charAt(0))+fieldName.substring(1);
            String methodName = "get"+rear;
            System.out.println(TAG+methodName);
            // get variant type
            String type = property.getType();
            String result = null;
            try {
                Method method = clas.getMethod(methodName);
                if ( type.equals("TEXT")) {
                    // get value
                    result = (String) method.invoke(obj);
                    if ( result == null ) {
                        continue;
                    } else {
                        columnName = property.getColumnName();
                        map.put(columnName, result);
                        System.out.println(TAG+"column="+columnName+" value="+result);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ResultSet resultSet = null;
        Connection connection = null;
        String queryColumn = translateFieldToColumn(field, propertyBeanList);
        try {
            connection = getJDBConnection();
            Statement statement = connection.createStatement();
            String sql = "select "+queriedColumn+" from "+map.get("table")+" where "+queryColumn+"=\""+map.get(queryColumn)+"\"";
            System.out.println(TAG+"construct sql "+sql);
            resultSet = statement.executeQuery(sql);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

//        Object beanProxy = BeanProxyFactory.getBeanProxy(obj);
        BeanProxy beanProxy = new BeanProxy(lazyPropertyList, map.get("table"));
        Object proxy = beanProxy.getProxy(obj.getClass());
        try {
            CommonUtil.resultSetToBean(resultSet, proxy, propertyBeanList, key);
            closeJDBConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return proxy;
    }

    public static Connection getJDBConnection() throws ClassNotFoundException, SQLException {
        String driver = JDBCConfig.get("driver_class");
        String url = JDBCConfig.get("url_path");
        String userName = JDBCConfig.get("db_username");
        String password = JDBCConfig.get("db_userpassword");
        System.out.println(TAG+"driver="+driver+", url="+url);
        System.out.println(TAG+"userName="+userName+",password="+password);
        Connection connection = null;
        try {
            // 通过反射机制获得jdbc驱动
            Class.forName(driver);
            connection = DriverManager.getConnection(url,userName,password);
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException(driver + "对应的JDBC驱动没有找到！");
        }
        return connection;
    }

    public static void closeJDBConnection(Connection connection) throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    private String translateFieldToColumn(String field, List<PropertyBean> propertyBeanList) {
        String columnName = null;
        for(PropertyBean propertyBean:propertyBeanList) {
            if(propertyBean.getFiledName().equals(field)) {
                columnName = propertyBean.getColumnName();
            }
        }

        return columnName;
    }
}
