package sc.ustc.factory;

import net.sf.cglib.proxy.Enhancer;
import sc.ustc.proxy.ExecutorProxy;

/**
 * Creator: hfang
 * Date: 2018/12/19 10:20
 * Description:
 **/

public class ExecutorProxyFactory {
    public static Object getExecutorProxy(Object target){
        Enhancer enhancer=new Enhancer();
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(new ExecutorProxy());
        return enhancer.create();
    }
}
