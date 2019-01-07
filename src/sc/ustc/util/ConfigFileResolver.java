package sc.ustc.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import sc.ustc.model.Action;
import sc.ustc.model.ConstRepo;
import sc.ustc.model.Interceptor;

import java.util.*;

import static sc.ustc.util.CommonUtil.getXMLDoc;


/**
 * Creator: hfang
 * Date: 2018/12/19 14:55
 * Description:
 **/

public class ConfigFileResolver {
    private String configFilePath;
    private Map<String, Action> actionMap = new HashMap<>();
    private Map<String, Interceptor> interceptorMap = new HashMap<>();
    private static final String TAG = ProduceTimeFormatted.getCurrentTime()+"sc.ustc.util.ConfigFileResolver:";
    private static final String ACTION_XPATH = "//sc-configuration/controller/action";
    private static final String INTERCEPTOR_XPATH = "//sc-configuration/interceptor";

    public ConfigFileResolver(String configFilePath) {
        this.configFilePath = configFilePath;
    }

    public void resolveConfigFile() {
        Document configFile = null;
        try {
            configFile = getXMLDoc(configFilePath);
            System.out.println(TAG+"Successfully read the file");
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Element root = configFile.getRootElement();
        System.out.println(TAG+"Root Element is "+root.getName());

        List<Node> actionList = configFile.selectNodes(ACTION_XPATH);
        for (Iterator<Node> it = actionList.iterator(); it.hasNext();) {
            Element node = (Element) it.next();
            String nodeName = node.attributeValue(ConstRepo.ATTR_NAME);
            Action action = new Action(nodeName);
            System.out.println(TAG + "action " + "attribute name is " + nodeName);

            action.setClassName(node.attributeValue(ConstRepo.ATTR_CLASS));
            action.setMethodName(node.attributeValue(ConstRepo.ATTR_METHOD));

            List<Node> interceptorRefNodes = node.selectNodes("interceptor-ref");
            for(Iterator<Node> iterator = interceptorRefNodes.iterator();iterator.hasNext(); ) {
                Element refEle = (Element) iterator.next();
                System.out.println(refEle.toString());
                String refName = refEle.attributeValue(ConstRepo.ATTR_NAME);
                System.out.println(TAG+"action attribute interceptor-ref name is "+refName);
                action.addtInterceptorRefList(refName);
            }

            List<Node> resultNodes = node.selectNodes("result");
            for(Iterator<Node> iterator = resultNodes.iterator(); iterator.hasNext(); ) {
                Element resultEle = (Element) iterator.next();
                Map<String, String> resultAttr = new HashMap<>();
                resultAttr.put(ConstRepo.ATTR_JUMP_TYPE, resultEle.attributeValue(ConstRepo.ATTR_JUMP_TYPE));
                resultAttr.put(ConstRepo.ATTR_VALUE, resultEle.attributeValue(ConstRepo.ATTR_VALUE));
                System.out.println(TAG+"action result value is "+resultEle.attributeValue(ConstRepo.ATTR_NAME));
                action.addResults(resultEle.attributeValue(ConstRepo.ATTR_NAME), resultAttr);
            }

            actionMap.put(action.getActionName(), action);
        }

        List<Node> interceptorList = configFile.selectNodes(INTERCEPTOR_XPATH);
        for(Iterator<Node> it = interceptorList.iterator(); it.hasNext(); ) {
            Element node = (Element) it.next();
            String name = node.attributeValue(ConstRepo.ATTR_NAME);
            String className = node.attributeValue(ConstRepo.ATTR_CLASS);
            String predoMethod = node.attributeValue("predo");
            String afterMethod = node.attributeValue("afterdo");

            Interceptor interceptor = new Interceptor(name);
            interceptor.setAfterdoMethodName(afterMethod);
            interceptor.setClassName(className);
            interceptor.setPredoMethodName(predoMethod);

            System.out.println(TAG+"interceptor info "+interceptor.toString());
            interceptorMap.put(name, interceptor);
        }
    }

    public Map<String, Action> getActionMap() {
        for(String key:actionMap.keySet()) {
            System.out.println(TAG + " key "+key+" in ActionMap");
        }
        return actionMap;

    }

    public Map<String, Interceptor> getInterceptorMap() {
        return interceptorMap;
    }

}
