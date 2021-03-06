package sc.ustc.controller;

import sc.ustc.dao.Configuration;
import sc.ustc.dao.Conversation;
import sc.ustc.factory.ExecutorProxyFactory;
import sc.ustc.model.*;
import sc.ustc.util.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.*;

/**
 * Creator: hfang
 * Date: 2018/11/25 21:29
 * Description:
 **/

@WebServlet("/SimpleController")
public class SimpleController extends HttpServlet {
    private static final String TAG = ProduceTimeFormatted.getCurrentTime()+"sc.ustc.controller.SimpleController:";
    private Map<String, Action> actionMap;
    private Map<String, Interceptor> interceptorMap;
    private Map<String, DepedencyBean> dependencyBeanMap;

    public void init() {
        String projectOutputPath = this.getServletContext().getRealPath("/");
        RunTimeVar.projectRootPath = projectOutputPath;
        System.out.println(projectOutputPath);

        ConfigFileResolver configFileResolver = new ConfigFileResolver(projectOutputPath+ConstRepo.configFilePath);
        configFileResolver.resolveConfigFile();
        actionMap = configFileResolver.getActionMap();
        for(String key: actionMap.keySet()) {
            System.out.println(TAG+" action "+key+" in the map");
        }
        interceptorMap = configFileResolver.getInterceptorMap();

        DependencyResolver dependencyResolver = new DependencyResolver(projectOutputPath+ConstRepo.DEPENDENCY_FILE_PATH);
        dependencyResolver.resolveXML();
        dependencyBeanMap = dependencyResolver.getBeanMap();
        for(String key: dependencyBeanMap.keySet()) {
            System.out.println(TAG+" dependencyBean "+key+" in the map");
        }

        Configuration configuration = new Configuration();
        configuration.resolveORMapping();
        System.out.println(TAG+"resolved ");
        Conversation.setJDBCConfig(configuration.getJDBCConfig());
        System.out.println(TAG+"get JDBCConfig");
        Conversation.setClassBeanList(configuration.getClassBeanList());
        System.out.println(TAG+"get classBeanList");
        Conversation.setKey(configuration.getKey());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // test current path
        System.out.println(System.getProperty("user.dir"));

        // 项目输出的classes路径
        String nodePath = this.getClass().getResource("/").getPath();
        System.out.println(nodePath);
        // 项目输出的根目录路径
        String filePath = nodePath.substring(1, nodePath.length() - 16);
        System.out.println(filePath);
        //
        // set response head content
        response.setContentType("text/html;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        PrintWriter writer = response.getWriter();

        File directory = new File("");// 参数为空
        String proRootPath = directory.getPath();
        System.out.println(proRootPath);


        Map<String, String[]> parameterMap = request.getParameterMap();

        if (parameterMap.isEmpty()) {
            // print default response content
            writer.println("<html>");
            writer.println("<head><title>SimpleController</title></head>");
            writer.println("<body>欢迎使用SimpleController</body>");
            writer.println("</html>");
        } else {
            String requestActionName = request.getParameter(ConstRepo.PAR_NAME);

            boolean findAction = false;
            boolean findResult = false;

            Action objectAction = actionMap.get(requestActionName);
            if (objectAction == null) {
                System.out.println(TAG+"action not found");
                findAction = false;
            } else {
                findAction = true;

                for(String key: parameterMap.keySet()) {
                    System.out.println(TAG + "has parameter " + key + " value=" + parameterMap.get(key)[0]);
                }

                Executor executorProxy = (Executor) ExecutorProxyFactory.getExecutorProxy(new Executor(objectAction, interceptorMap, parameterMap, dependencyBeanMap));
                executorProxy.setAction(objectAction);
                executorProxy.setInterceptorMap(interceptorMap);
                executorProxy.setParameterMap(parameterMap);
                executorProxy.setDepedencyBeanMap(dependencyBeanMap);
                String result = executorProxy.execute();

                Map<String, Map<String, String>> results = objectAction.getResults();
                if (results.containsKey(result)) {
                    Map<String, String> resultInfo = results.get(result);
                    String jumpType = resultInfo.get(ConstRepo.ATTR_JUMP_TYPE);
                    String jumpValue = resultInfo.get(ConstRepo.ATTR_VALUE);
                    findResult = true;

                    if(jumpValue.endsWith("_view.xml")) {
                        String XMLPath = RunTimeVar.projectRootPath+ConstRepo.XML_VIEW_PATH;
                        String XSLPath = RunTimeVar.projectRootPath+ConstRepo.XSL_PATH;
                        response.getWriter().write(Objects.requireNonNull(XMLConverter.ConvertXmlToHtml(XSLPath, XMLPath)).toString());
                    } else {
                        switch (jumpType) {
                            case "forward": {
                                request.getRequestDispatcher(jumpValue).forward(request, response);
                                break;
                            }
                            case "redirect": {
                                response.sendRedirect(jumpValue);
                                break;
                            }
                        }
                    }
                } else {
                    findResult = false;
                }
            }

            if (!findAction) {
                writer.println(ConstRepo.ACTION_NOT_MATCH_INFO);
            } else if (!findResult) {
                writer.println(ConstRepo.RESULT_NOT_FOUND_INFO);
            }
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
