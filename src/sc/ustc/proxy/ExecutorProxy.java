package sc.ustc.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import sc.ustc.util.ProduceTimeFormatted;

import java.lang.reflect.Method;

/**
 * Creator: hfang
 * Date: 2018/12/19 10:05
 * Description:
 **/

public class ExecutorProxy implements MethodInterceptor {
    private static final String TAG = ProduceTimeFormatted.getCurrentTime()+"sc.ustc.proxy.ExecutorProxy:";

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println(TAG+"ExecutorProxy get the request");
        Object result = methodProxy.invokeSuper(o, objects);
        return result;
    }

}
