package sc.ustc.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Creator: hfang
 * Date: 2018/12/19 10:05
 * Description:
 **/

public class ActionProxy implements MethodInterceptor {

    Object actionInterceptor;

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        return null;
    }

    public void setActionInterceptor(Object interceptor) {
        this.actionInterceptor = interceptor;
    }
}
