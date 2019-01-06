package sc.ustc.dao;

import sc.ustc.model.ClassBean;
import sc.ustc.model.PropertyBean;
import sc.ustc.util.ProduceTimeFormatted;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Creator: hfang
 * Date: 2019/01/05 20:57
 * Description:
 **/

public class Conversation {
    private static final String TAG = ProduceTimeFormatted.getCurrentTime()+"sc.ustc.dao.Conversation:";
    private Map<String,String> JDBCConfig;
    private List<ClassBean> classBeanList;

    public Conversation() {
        Configuration configuration = new Configuration();
        configuration.resolveORMapping();
        System.out.println(TAG+"resolved ");
        JDBCConfig = configuration.getJDBCConfig();
        System.out.println(TAG+"get JDBCConfig");
        classBeanList = configuration.getClassBeanList();
        System.out.println(TAG+"get classBeanList");
    }

    public Object query(Object obj, String field) {
        CachedRowSet rowSet = getObject(obj, field);
        return rowSet;
    }

    private CachedRowSet getObject(Object obj, String field) {
        Class<?> clas = obj.getClass();
        System.out.println(TAG+clas.getName());

        ClassBean clasBean = null;
        for ( ClassBean classBean : classBeanList ) {
            System.out.println(classBean.getBeanName());
            String str = clas.getName();
            String clasName = str.substring(str.lastIndexOf(".")+1);
            System.out.println(TAG+clasName);
            if ( classBean.getBeanName().equals( clasName ) ) {
                System.out.println(TAG+"found");
                clasBean = classBean;
                break;
            }
        }

        if ( clasBean == null ) {
            System.out.println("映射错误");
            return null;
        }

        //从映射关系中得到使用的表
        String table = clasBean.getTableName();
        //传入的对象所有的成员变量名都在propertyList中
        List<PropertyBean> propertyList = clasBean.getPropertyList();
        //存储对应的成员变量及其值
        Map<String,String> map = new HashMap<String, String>();
        map.put("table", table);
        String columnName = null;
        for (PropertyBean property : propertyList) {
            //得到fieldName对应的get方法
            String fieldName =  property.getFiledName();
            System.out.println(TAG+fieldName);
            String rear = Character.toUpperCase(fieldName.charAt(0))+fieldName.substring(1);
            String methodName = "get"+rear;
            System.out.println(TAG+methodName);
            //得到成员变量类型
            String type = property.getType();
            String result = null;
            //根据方法名及参数找到对应的get方法
            try {
                Method method = clas.getMethod(methodName);
                if ( type.equals("TEXT")) {
                    //类型转换,得到fieldName成员变量的值
                    result = (String) method.invoke(obj);
                    //如果变量没有被初始化
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
        CachedRowSet rowSet = null;
        try {
            Connection connection = getJDBConnection();
            Statement statement = connection.createStatement();
            String sql = "select * from "+map.get("table")+" where "+field+"=\""+map.get("NAME")+"\"";
            ResultSet resultSet = statement.executeQuery(sql);
            RowSetFactory factory = RowSetProvider.newFactory();
            rowSet = factory.createCachedRowSet();
            rowSet.populate(resultSet);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowSet;
    }

    public Connection getJDBConnection() throws ClassNotFoundException, SQLException {
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

    public void closeJDBConnection(Connection connection) throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
