package sc.ustc.factory;

import net.sf.cglib.proxy.Enhancer;
import sc.ustc.proxy.ActionProxy;

/**
 * Creator: hfang
 * Date: 2018/12/19 10:20
 * Description:
 **/

public class ActionProxyFactory {
    public static Object getGcLibDynProxy(Object target){
        Enhancer enhancer=new Enhancer();
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(new ActionProxy());
        return enhancer.create();
    }
}
