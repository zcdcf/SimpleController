package water.ustc.action;

import sc.ustc.util.ProduceTimeFormatted;

import javax.xml.stream.FactoryConfigurationError;

/**
 * Creator: hfang
 * Date: 2018/12/11 22:34
 * Description: Use to handle Login Business.
 **/

public class LoginAction {
    private static String SUCCESS_RETURN_VALUE = "success";
    private static String FAILURE_RETURN_VALUE = "failure";
    private static String TAG = "water.ustc.action: Login state ";

    public LoginAction() {

    }

    public String handleLogin() {
        double a = Math.random()*10;
        boolean loginState = false;
        if(a>5) {
            loginState = true;
            System.out.println(ProduceTimeFormatted.getCurrentTime()+TAG+SUCCESS_RETURN_VALUE);
        } else {
            loginState = false;
            System.out.println(ProduceTimeFormatted.getCurrentTime()+TAG+FAILURE_RETURN_VALUE);
        }
        return loginState?SUCCESS_RETURN_VALUE:FAILURE_RETURN_VALUE;
    }
}
