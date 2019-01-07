package sc.ustc.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import sc.ustc.model.ConstRepo;
import sc.ustc.model.DepedencyBean;
import sc.ustc.model.FieldBean;
import sc.ustc.model.RunTimeVar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static sc.ustc.util.CommonUtil.getXMLDoc;

/**
 * Creator: hfang
 * Date: 2019/01/06 14:53
 * Description:
 **/

public class DependencyResolver {
    private static final String TAG = ProduceTimeFormatted.getCurrentTime()+"sc.ustc.util.DependencyResolver";
    private Map<String, DepedencyBean> beanMap;

    private String diFilePath;

    public DependencyResolver(String filePath) {
        this.diFilePath = filePath;
    }


    public void resolveXML() {
        System.out.println(TAG+"start resolving di.xml");
        Document document = null;
        try {
            document = getXMLDoc(diFilePath);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        beanMap = new HashMap<>();

        assert document != null;
        Element root = document.getRootElement();
        List<Node> beanNodeList = root.selectNodes("bean");

        for (Node aBeanNodeList : beanNodeList) {
            DepedencyBean depedencyBean = new DepedencyBean();
            Element beanEle = (Element) aBeanNodeList;
            String id = beanEle.attributeValue("id");
            String className = beanEle.attributeValue("class");

            depedencyBean.setID(id);
            depedencyBean.setClazz(className);

            System.out.println(TAG+"bean id="+id+" class="+className);

            List<Node> fieldNodeList = beanEle.selectNodes("field");
            if (fieldNodeList.size() > 0) {
                for (Node aFieldNodeList : fieldNodeList) {
                    Element fieldEle = (Element) aFieldNodeList;
                    FieldBean fieldBean = new FieldBean();
                    String fieldName = fieldEle.attributeValue("name");
                    String refBeanName = fieldEle.attributeValue("bean-ref");

                    System.out.println(TAG+"field name="+fieldName+" refBeanName="+refBeanName);
                    fieldBean.setBeanRef(refBeanName);
                    fieldBean.setName(fieldName);

                    depedencyBean.addFieldBeanList(fieldBean);
                }
            }

            beanMap.put(id, depedencyBean);
        }
    }

    public Map<String, DepedencyBean> getBeanMap() {
        return beanMap;
    }

    public void setBeanMap(Map<String, DepedencyBean> beanMap) {
        this.beanMap = beanMap;
    }
}
