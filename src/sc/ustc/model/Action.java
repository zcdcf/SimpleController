package sc.ustc.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Creator: hfang
 * Date: 2018/12/19 14:58
 * Description:
 **/

public class Action {
    private String actionName;
    private String className;
    private String methodName;
    private List<String> interceptorRefList = new ArrayList<>();
    private Map<String, Map<String, String>> results = new HashMap<>();

    public Action(String name) {
        this.actionName = name;
    }
    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public Map<String, Map<String, String>> getResults() {
        return results;
    }

    public void addResults(String resultValue, Map<String, String> resultsContent) {
        this.results.put(resultValue, resultsContent);
    }

    public List<String> getInterceptorRefList() {
        return interceptorRefList;
    }

    public void addtInterceptorRefList(String refName) {
        this.interceptorRefList.add(refName);
    }
}
