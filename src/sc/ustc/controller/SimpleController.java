package sc.ustc.controller;

import sc.ustc.factory.ExecutorProxyFactory;
import sc.ustc.model.Action;
import sc.ustc.model.ConstRepo;
import sc.ustc.model.Interceptor;
import sc.ustc.model.RunTimeVar;
import sc.ustc.util.ConfigFileResolver;
import sc.ustc.util.Executor;
import sc.ustc.util.ProduceTimeFormatted;
import sc.ustc.util.XMLConverter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Creator: hfang
 * Date: 2018/11/25 21:29
 * Description:
 **/

@WebServlet("/SimpleController")
public class SimpleController extends HttpServlet {
    private static final String TAG = ProduceTimeFormatted.getCurrentTime()+"sc.ustc.controller.SimpleController:";

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
        String projectOutputPath = this.getServletContext().getRealPath("/");
        RunTimeVar.projectRootPath = projectOutputPath;
        System.out.println(projectOutputPath);
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

            ConfigFileResolver configFileResolver = new ConfigFileResolver(projectOutputPath+ConstRepo.configFilePath);
            configFileResolver.resolveConfigFile();
            Map<String, Action> actionMap = configFileResolver.getActionMap();
            for(String key: actionMap.keySet()) {
                System.out.println(TAG+" action "+key+" int map");
            }
            Map<String, Interceptor> interceptorMap = configFileResolver.getInterceptorMap();

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
                Executor executorProxy = (Executor) ExecutorProxyFactory.getExecutorProxy(new Executor(objectAction, interceptorMap, parameterMap));
                executorProxy.setAction(objectAction);
                executorProxy.setInterceptorMap(interceptorMap);
                executorProxy.setParameterMap(parameterMap);
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
