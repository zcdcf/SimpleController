package sc.ustc.dao;

import sc.ustc.util.ProduceTimeFormatted;

import java.sql.*;

/**
 * Creator: hfang
 * Date: 2018/12/26 09:16
 * Description:
 **/

abstract public class BaseDAO {
    protected String url;
    protected String userName;
    protected String userPassword;
    protected String driver;
    private static final String TAG = ProduceTimeFormatted.getCurrentTime()+"sc.ustc.dao:";

    public BaseDAO(String url, String userName, String userPassword, String driver) {
        this.url = url;
        this.userName = userName;
        this.userPassword = userPassword;
        this.driver = driver;
    }

    public Connection openDBConnection() {
        Connection connection = null;
        // open the connection
        try {
            System.out.println(TAG+"driver is "+driver);
            System.out.println(TAG+"url is "+url);
            Class.forName(driver);
            connection = DriverManager.getConnection(url);
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }

        System.out.println(TAG+"Opened database successfully");
        return connection;
    }

    public boolean closeDBConnection(Connection connection) {
        try {
            connection.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    abstract public Object query(String sql);
    abstract public boolean insert(String sql);
    abstract public boolean update(String sql);
    abstract public boolean delete(String sql);

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getDriver(){
        return this.driver;
    }

    public String getUserName() {
        return this.userName;
    }

}
