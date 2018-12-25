package sc.ustc.util;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Creator: hfang
 * Date: 2018/12/22 21:48
 * Description: This class is used to convert xml file to html page.
 **/

public class XMLConverter {
    private static final String TAG = ProduceTimeFormatted.getCurrentTime()+"sc.ustc.util.XMLConverter:";
    public static ByteArrayOutputStream ConvertXmlToHtml(String xslPath, String xmlPath) throws FileNotFoundException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = null;
        System.out.println(TAG+"xml file path is "+xmlPath);
        System.out.println(TAG+"xsl file path is "+xslPath);
        StreamSource sourceXsl = new StreamSource(new FileInputStream(xslPath));
        StreamSource sourceXml = new StreamSource(new FileInputStream(xmlPath));
        try {
            transformer = factory.newTransformer(sourceXsl);
            StreamResult output = null;
            ByteArrayOutputStream htmlStream = new ByteArrayOutputStream();
            output = new StreamResult(htmlStream);

            transformer.transform(sourceXml,output);
            return htmlStream;
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return null;
    }
}
