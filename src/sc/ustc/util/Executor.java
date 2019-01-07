package sc.ustc.util;

import sc.ustc.model.Action;
import sc.ustc.model.DepedencyBean;
import sc.ustc.model.FieldBean;
import sc.ustc.model.Interceptor;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Creator: hfang
 * Date: 2018/12/19 15:56
 * Description:
 **/

public class Executor {
    private Action action;
    private Map<String, Interceptor> interceptorMap;
    private Map<String, String[]> parameterMap;
    private static final String TAG = "sc.ustc.util.Executor: ";
    private Stack<Interceptor> interceptorStack;
    private Map<String, DepedencyBean> depedencyBeanMap;

    public Executor() {

    }

    public Executor(Action action, Map<String, Interceptor> interceptorMap, Map<String, String[]> parameterMap, Map<String, DepedencyBean> depedencyBeanMap) {
        this.action = action;
        this.interceptorMap = interceptorMap;
        this.parameterMap = parameterMap;
        this.depedencyBeanMap = depedencyBeanMap;
    }

    public String execute() {
        String className = action.getClassName();
        String methodName = action.getMethodName();
        System.out.println(TAG + "execute class name is " + className + " method name is " + methodName);
        List<String> interceptorRefList = action.getInterceptorRefList();
        boolean interceptorFound = true;

        for (String key : parameterMap.keySet()) {
            System.out.println(ProduceTimeFormatted.getCurrentTime() + TAG + "has parameter " + key + " value=" + parameterMap.get(key)[0]);
        }

        if (!interceptorRefList.isEmpty()) {
            interceptorStack = new Stack<>();

            // push interceptor into the stack, meanwhile execute the predo method
            for (Iterator iterator = interceptorRefList.iterator(); iterator.hasNext(); ) {
                String refName = (String) iterator.next();

                Interceptor interceptorUsed = interceptorMap.get(refName);
                if (interceptorUsed == null) {
                    interceptorFound = false;
                    System.out.println(ProduceTimeFormatted.getCurrentTime() + TAG + refName + " interceptor not found");
                } else {
                    try {
                        Class interceptorClass = Class.forName(interceptorUsed.getClassName());
                        Constructor constructor = interceptorClass.getConstructor();
                        Object interceptor = constructor.newInstance();
                        Method m = interceptorClass.getMethod(interceptorUsed.getPredoMethodName(), String.class);
                        m.invoke(interceptor, action.getActionName());
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    interceptorStack.push(interceptorUsed);
                }
            }
        }


        Method m = null;
        Object actionClass = null;
        Class handleClass = null;
        String result = null;
        try {
            handleClass = Class.forName(className);
            Constructor constructor = handleClass.getConstructor();
            actionClass = constructor.newInstance();

            // check dependency bean
            String actionBeanID = className.substring(className.lastIndexOf(".") + 1);
            System.out.println(TAG + "actionBeanID=" + actionBeanID);
            if (depedencyBeanMap.get(actionBeanID) != null) {
                System.out.println(TAG + "resolve dependency");
                Field[] fields = handleClass.getDeclaredFields();
                DepedencyBean depedencyBean = depedencyBeanMap.get(actionBeanID);
                List<FieldBean> fieldBeanList = depedencyBean.getFieldBeanList();
                for (FieldBean fieldBean : fieldBeanList) {
                    String refBeanName = fieldBean.getName();
                    String refBeanClassPath = depedencyBeanMap.get(fieldBean.getBeanRef()).getClazz();
                    System.out.println(TAG + "refBeanName=" + refBeanName + " refBeanClassPath=" + refBeanClassPath);

                    Class refBeanClass = Class.forName(refBeanClassPath);
                    Object refBeanInstance = refBeanClass.getConstructor().newInstance();

                    // set bean field
                    Field[] fields1 = refBeanClass.getDeclaredFields();
                    for (Field field : fields1) {
                        field.setAccessible(true);
                        for (String key : parameterMap.keySet()) {
                            if (key.equals(field.getName())) {
                                String fieldName = field.getName();
                                String fieldValue = parameterMap.get(key)[0];
                                System.out.println(ProduceTimeFormatted.getCurrentTime() + TAG + "Setting field " + fieldName+" value is "+fieldValue);
                                field.set(refBeanInstance, fieldValue);
                            }
                        }
                    }

                    // inject bean into action
                    PropertyDescriptor pds[] = Introspector.getBeanInfo(actionClass.getClass()).getPropertyDescriptors();
                    for (PropertyDescriptor pd : pds) {
                        System.out.println("property " + pd.getName());
                        if (pd.getName().equals(refBeanName)) {
                            Method md = pd.getWriteMethod();// 获取set方法
                            md.invoke(actionClass, refBeanInstance);
                        }
                    }
                }
            }

/*            Field[] fields = handleClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                System.out.println(ProduceTimeFormatted.getCurrentTime()+TAG+"Setting field "+field.getName());
                for (String key : parameterMap.keySet()) {
                    if (key.equals(field.getName())) {
                        field.set(actionClass, (parameterMap.get(key))[0]);
                    }
                }
            }*/
            m = handleClass.getMethod(methodName);
            result = (String) m.invoke(actionClass);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }

        while (!interceptorStack.isEmpty()) {
            Method method = null;
            try {
                Interceptor interceptor = interceptorStack.pop();
                Class interceptorClass = Class.forName(interceptor.getClassName());
                Constructor constructor = interceptorClass.getConstructor();
                Object interceptorObj = constructor.newInstance();
                method = interceptorClass.getMethod(interceptor.getAfterdoMethodName(), String.class);
                method.invoke(interceptorObj, result);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setInterceptorMap(Map<String, Interceptor> interceptorMap) {
        this.interceptorMap = interceptorMap;
    }

    public void setParameterMap(Map<String, String[]> parameterMap) {
        this.parameterMap = parameterMap;
    }

    public void setDepedencyBeanMap(Map<String, DepedencyBean> depedencyBeanMap) {
        this.depedencyBeanMap = depedencyBeanMap;
    }

}
