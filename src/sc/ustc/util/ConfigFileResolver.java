package sc.ustc.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import sc.ustc.model.Action;
import sc.ustc.model.ConstRepo;
import sc.ustc.model.Interceptor;

import java.io.File;
import java.util.*;

import static sc.ustc.model.ConstRepo.ATTR_CLASS;
import static sc.ustc.model.ConstRepo.ATTR_NAME;
import static sc.ustc.model.ConstRepo.INTERCEPTOR_NOT_FOUND_INFO;


/**
 * Creator: hfang
 * Date: 2018/12/19 14:55
 * Description:
 **/

public class ConfigFileResolver {
    private String configFilePath;
    private List<Action> actionList = new ArrayList<>();
    private List<Interceptor> interceptorList = new ArrayList<>();
    private static final String TAG = "sc.ustc.util.ConfigFileResolver:";
    private static final String ACTION_XPATH = "//sc-configuration/controller/action";

    public ConfigFileResolver(String configFilePath) {
        this.configFilePath = configFilePath;
    }

    public void resolveConfigFile() {
        Document configFile = null;
        try {
            configFile = getXMLDoc(configFilePath);
            System.out.println(ProduceTimeFormatted.getCurrentTime()+TAG+" Successfully read the file");
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Element root = configFile.getRootElement();
        System.out.println(ProduceTimeFormatted.getCurrentTime()+TAG+"Root Element is "+root.getName());

        List<Node> list = configFile.selectNodes(ACTION_XPATH);
        for (Iterator<Node> it = list.iterator(); it.hasNext();) {
            Element node = (Element) it.next();
            String nodeName = node.attributeValue(ConstRepo.ATTR_NAME);
            Action action = new Action(node.attributeValue(nodeName);
            System.out.println(ProduceTimeFormatted.getCurrentTime() + TAG + "tag:action " + "attribute name is " + nodeName);

            action.setClassName(node.attributeValue(ConstRepo.ATTR_CLASS));
            action.setMethodName(node.attributeValue(ConstRepo.ATTR_METHOD));

            List<Node> interceptorRefNodes = node.selectNodes("//interceptor-ref");
            for(Iterator<Node> iterator; iterator.hasNext(); ) {
                Element refEle = (Element) iterator.next();
                action.addtInterceptorRedList(refEle.attributeValue(ConstRepo.ATTR_NAME));
            }

            List<Node> resultNodes = node.selectNodes("//result");
            for(Iterator<Node> iterator; iterator.hasNext(); ) {
                Element resultEle = (Element) iterator.next();
                Map<String, String> resultAttr = new HashMap<>();
                resultAttr.put(ConstRepo.ATTR_JUMP_TYPE, resultEle.attributeValue(ConstRepo.ATTR_JUMP_TYPE));
                resultAttr.put(ConstRepo.ATTR_VALUE, resultEle.attributeValue(ConstRepo.ATTR_VALUE));
                action.addResults(resultEle.attributeValue(ConstRepo.ATTR_NAME), resultAttr);
            }
        }
    }

    public List<Action> getActionList() {
        return actionList;
    }

    public List<Interceptor> getInterceptorList() {
        return interceptorList;
    }

    private Document getXMLDoc(String filePath) throws DocumentException {
        File file = new File(filePath);
        SAXReader reader = new SAXReader();
        Document document = reader.read(file);
        return document;
    }
}
