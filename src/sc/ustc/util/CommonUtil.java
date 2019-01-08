package sc.ustc.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import sc.ustc.model.PropertyBean;
import sc.ustc.proxy.BeanProxy;

import java.io.File;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;

/**
 * Creator: hfang
 * Date: 2019/01/06 14:53
 * Description:
 **/

public class CommonUtil {
    private static final String TAG = ProduceTimeFormatted.getCurrentTime() + "sc.ustc.util.CommonUtil:";

    public static Document getXMLDoc(String filePath) throws DocumentException {
        File file = new File(filePath);
        SAXReader reader = new SAXReader();
        Document document = reader.read(file);
        return document;
    }

    public static void resultSetToBean(ResultSet rs, Object proxy, List<PropertyBean> propertyBeanList, String key) throws Exception {
        ResultSetMetaData metaData = rs.getMetaData();
        while (rs.next()) {
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String colName = metaData.getColumnLabel(i).toUpperCase();
                String fieldName = null;
                for (PropertyBean propertyBean : propertyBeanList) {
                    System.out.println(TAG+"propertyBean column="+propertyBean.getColumnName()+" set column="+colName);
                    if (propertyBean.getColumnName().equals(colName)) {
                        fieldName = propertyBean.getFiledName();
                        break;
                    } else if(key.equals(colName)) {
                        fieldName = key;
                        break;
                    }
                }
                String setMethodName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                Object value = rs.getObject(colName);
                if (value == null) {
                    continue;
                }
                try {
                    System.out.println(TAG + "setMethodName=" + setMethodName);
                    Method setMethod = proxy.getClass().getMethod(setMethodName, value.getClass());
                    setMethod.invoke(proxy, value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

