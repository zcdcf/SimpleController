package sc.ustc.model;

/**
 * Creator: hfang
 * Date: 2018/12/19 15:08
 * Description:
 **/

public class Interceptor {
    private String interceptorName;
    private String className;
    private String predoMethodName;
    private String afterdoMethodName;

    public Interceptor(String name) {
        this.interceptorName = name;
    }

    public String getInterceptorName() {
        return interceptorName;
    }

    public void setInterceptorName(String interceptorName) {
        this.interceptorName = interceptorName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPredoMethodName() {
        return predoMethodName;
    }

    public void setPredoMethodName(String predoMethodName) {
        this.predoMethodName = predoMethodName;
    }

    public String getAfterdoMethodName() {
        return afterdoMethodName;
    }

    public void setAfterdoMethodName(String afterdoMethodName) {
        this.afterdoMethodName = afterdoMethodName;
    }

    @Override
    public String toString() {
        return "name:"+interceptorName+" class name:"+className+" predo method name:"+predoMethodName+" afterdo method name:"+afterdoMethodName;
    }
}
