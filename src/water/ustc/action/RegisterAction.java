package water.ustc.action;

import sc.ustc.util.ProduceTimeFormatted;

/**
 * Creator: hfang
 * Date: 2018/12/12 13:44
 * Description: Use to handle Register Business.
 **/

public class RegisterAction {
    private static final String SUCCESS_RETURN_VALUE = "success";
    private static final String FAILURE_RETURN_VALUE = "failure";
    private static String TAG = "water.ustc.action: Register state ";

    public RegisterAction() {

    }

    public String handleRegister()
    {
        double a = Math.random()*10;
        boolean registerState = false;

        if(a>5) {
            registerState = true;
            System.out.println(ProduceTimeFormatted.getCurrentTime()+TAG+SUCCESS_RETURN_VALUE);
        } else {
            registerState = false;
            System.out.println(ProduceTimeFormatted.getCurrentTime()+TAG+FAILURE_RETURN_VALUE);
        }
        return registerState?SUCCESS_RETURN_VALUE:FAILURE_RETURN_VALUE;
    }

}