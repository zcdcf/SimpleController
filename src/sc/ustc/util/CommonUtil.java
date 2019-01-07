package sc.ustc.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.io.File;

/**
 * Creator: hfang
 * Date: 2019/01/06 14:53
 * Description:
 **/

public class CommonUtil {
    public static Document getXMLDoc(String filePath) throws DocumentException {
        File file = new File(filePath);
        SAXReader reader = new SAXReader();
        Document document = reader.read(file);
        return document;
    }
}
