package sc.ustc.dao;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import sc.ustc.model.ClassBean;
import sc.ustc.model.ConstRepo;
import sc.ustc.model.PropertyBean;
import sc.ustc.model.RunTimeVar;

import java.util.*;

import static sc.ustc.util.CommonUtil.getXMLDoc;

/**
 * Creator: hfang
 * Date: 2019/01/05 20:24
 * Description:
 **/

public class Configuration {
    private Map<String, String> JDBCConfig;
    private List<ClassBean> classBeanList;

    public void resolveORMapping() {
        Document mappingDoc = null;
        try {
            mappingDoc = getXMLDoc(RunTimeVar.projectRootPath+ConstRepo.OR_MAPPING_PATH);
            JDBCConfig = resolveJDBConfigMap(mappingDoc);
            classBeanList = resolveClassBeanList(mappingDoc);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> resolveJDBConfigMap(Document mappingDoc) {
        Element root = mappingDoc.getRootElement();

        Element JDBCElement = root.element("jdbc");
        @SuppressWarnings("unchecked")
        List<Element> propertyElementList = JDBCElement.elements();
        Map<String, String> JDBCConfig = new HashMap<>();
        for (Element property : propertyElementList) {
            // property.attribute("name").getValue();
            String name = property.element("name").getTextTrim();
            String value = property.element("value").getTextTrim();
            JDBCConfig.put(name, value);
        }
        return JDBCConfig;
    }

    private List<ClassBean> resolveClassBeanList(Document mappingDoc) {
        // 存储解析or_mapping.xml得到ClassBean对象
        List<ClassBean> classBeanList = new LinkedList<ClassBean>();
        // 获取根节点下的所有class节点
        @SuppressWarnings("unchecked")
        Element root = mappingDoc.getRootElement();
        List<Node> allClassNode = root.selectNodes("class");
        for(Iterator iterator = allClassNode.iterator(); iterator.hasNext(); ) {
            Element curClass = (Element) iterator.next();
            ClassBean classBean = new ClassBean();
            classBean.setBeanName(curClass.element("name").getTextTrim());
            classBean.setTableName(curClass.element("table").getTextTrim());

            List<Node> allPropertyNode = curClass.selectNodes("property");

            List<PropertyBean> propertyList = new LinkedList<>();
            for(Iterator it = allPropertyNode.iterator(); it.hasNext(); ) {
                Element curProperty = (Element) it.next();
                PropertyBean propertyBean = new PropertyBean();
                propertyBean.setFiledName(curProperty.element("name").getTextTrim());
                propertyBean.setColumnName(curProperty.element("column").getTextTrim());
                propertyBean.setType(curProperty.element("type").getTextTrim());
                propertyBean.setLazy(curProperty.element("lazy").getTextTrim());
                propertyList.add(propertyBean);
            }
            classBean.setPropertyList(propertyList);
            classBeanList.add(classBean);
        }
        return classBeanList;
    }

    public List<ClassBean> getClassBeanList() {
        return classBeanList;
    }

    public Map<String, String> getJDBCConfig() {
        return JDBCConfig;
    }
}
