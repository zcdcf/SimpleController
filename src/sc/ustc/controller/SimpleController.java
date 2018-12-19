package sc.ustc.controller;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import sc.ustc.model.ConstRepo;
import sc.ustc.util.ProduceTimeFormatted;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Creator: hfang
 * Date: 2018/11/25 21:29
 * Description:
 **/

@WebServlet("/SimpleController")
public class SimpleController extends HttpServlet {
    private static final String TAG = "sc.ustc.controller.SimpleController:";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        // test current path
        System.out.println(System.getProperty("user.dir"));

        // 项目输出的classes路径
        String nodePath = this.getClass().getResource("/").getPath();
        System.out.println(nodePath);
        // 项目输出的根目录路径
        String filePath = nodePath.substring(1, nodePath.length() - 16);
        System.out.println(filePath);
        //
        String projectOutputPath = this.getServletContext().getRealPath("/");
        System.out.println(projectOutputPath);
        // set response head content
        response.setContentType("text/html;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        PrintWriter writer = response.getWriter();

        File directory = new File("");// 参数为空
        String proRootPath = directory.getCanonicalPath();
        System.out.println(proRootPath);

        // print default response content
        writer.println("<html>");
        writer.println("<head><title>SimpleController</title></head>");
        writer.println("<body>欢迎使用SimpleController</body>");
        writer.println("</html>");

        String requestActionName = request.getParameter(ConstRepo.PAR_NAME);

        Document configFile = null;
        try {
            configFile = getXMLDoc(projectOutputPath+ ConstRepo.configFilePath);
            System.out.println(ProduceTimeFormatted.getCurrentTime()+TAG+" Successfully read the file");
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Element root = configFile.getRootElement();
        System.out.println(ProduceTimeFormatted.getCurrentTime()+TAG+"Root Element is "+root.getName());
        boolean findAction = false;
        boolean findResult = false;

        List<Node> list = configFile.selectNodes("//sc-configuration/controller/action");
        for (Iterator<Node> it = list.iterator(); it.hasNext();) {
            Element node = (Element) it.next();
            String nodeName = node.attributeValue(ConstRepo.ATTR_NAME);
            System.out.println(ProduceTimeFormatted.getCurrentTime()+TAG+"tag:action "+"attribute name is "+nodeName);

            if(nodeName.equals(requestActionName)) {
                findAction = true;
            } else {
                continue;
            }

            List<Node> interceptorRefNodes = node.selectNodes("//interceptor-ref");
            List<Node> interceptorList = new ArrayList<>();
            boolean hasInterceptor = false;
            Map<String, Map<String, String>> interceptorInfoMap = new HashMap<>();
            if(interceptorRefNodes.size()!=0) {
                interceptorList = configFile.selectNodes("//sc-configuration/interceptor");

                if(interceptorList.size()==0) {
                    writer.println(ConstRepo.INTERCEPTOR_NOT_FOUND_INFO);
                } else {
                    for(Iterator<Node> iterator = interceptorList.iterator(); iterator.hasNext(); ) {
                        Element interceptorNode = (Element) iterator.next();
                        String interceptorName = interceptorNode.attributeValue(ConstRepo.ATTR_NAME);
                        for(Iterator<Node> it = interceptorRefNodes.iterator(); it.hasNext(); ) {
                            Element refEle = (Element) it.next();
                            if(refEle.attributeValue(ConstRepo.ATTR_NAME).equals(interceptorName)) {
                                Map<String, String> info = new HashMap<>();
                                info.put("interceptorClass", interceptorNode.attributeValue(ConstRepo.ATTR_CLASS));
                                info.put("predoMethod", interceptorNode.attributeValue("predo"));
                                info.put("afterdoMethdo", interceptorNode.attributeValue("afterdo"));
                                interceptorInfoMap.put(interceptorNode.attributeValue(ConstRepo.ATTR_NAME),info);
                                it.remove();
                                iterator.remove();
                                hasInterceptor = true;
                                break;
                            }
                        }
                    }
                }
            }
            // use reflection to find class to handle action
            String className = node.attributeValue(ConstRepo.ATTR_CLASS);
            String methodName = node.attributeValue(ConstRepo.ATTR_METHOD);
            System.out.println(ProduceTimeFormatted.getCurrentTime()+TAG+"handle class name is "+className+" handle method is "+methodName);
            Method m = null;
            Object loginActionClass = null;
            Class handleClass = null;
            String result = null;
            try {
                handleClass = Class.forName(className);
                Constructor constructor = handleClass.getConstructor();
                loginActionClass = constructor.newInstance();
                m = handleClass.getMethod(methodName);
                result = (String) m.invoke(loginActionClass);
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


            for(Iterator<Element> subIt = node.elementIterator(); subIt.hasNext();) {
               Element subEle = subIt.next();
               if (subEle.getName().equals("result") && result.equals(subEle.attributeValue(ConstRepo.ATTR_NAME))) {
                   String jumpType = subEle.attributeValue(ConstRepo.ATTR_JUMP_TYPE);
                   switch (jumpType) {
                       case "forward":{
                           request.getRequestDispatcher(subEle.attributeValue(ConstRepo.ATTR_VALUE)).forward(request,response);
                           break;
                       }
                       case "redirect":{
                           response.sendRedirect(subEle.attributeValue(ConstRepo.ATTR_VALUE));
                           break;
                       }
                   }
                   findResult = true;
                   break;
               }
            }

            if(findAction) {
                break;
            }

        }

        if(!findAction) {
            writer.println(ConstRepo.ACTION_NOT_MATCH_INFO);
        } else if(!findResult) {
            writer.println(ConstRepo.RESULT_NOT_FOUND_INFO);
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public Document getXMLDoc(String filePath) throws DocumentException {
        File file = new File(filePath);
        SAXReader reader = new SAXReader();
        Document document = reader.read(file);
        return document;
    }
}
