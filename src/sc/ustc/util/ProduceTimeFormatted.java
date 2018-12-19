package sc.ustc.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Creator: hfang
 * Date: 2018/12/12 18:16
 * Description:
 **/

public class ProduceTimeFormatted {

    public static String getCurrentTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");//设置日期格式
        return "["+df.format(new Date())+"] ";
    }
}
