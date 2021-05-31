import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class main {
    public static void main(String[] args) {
        try {
            File file = new File("src/main/resources/source.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);
            document.getDocumentElement().normalize();

            HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
            NodeList entries = document.getElementsByTagName("*");
            for (int i = 0; i<entries.getLength(); i++){
                Element element = (Element) entries.item(i);

                //if entry exists, increment by one
                if (hashMap.containsKey(element.getNodeName()))
                {
                    hashMap.put(element.getNodeName(),hashMap.get(element.getNodeName()) +1);

                    //else create entry
                } else {
                    hashMap.put(element.getNodeName(),1);
                }

            }
            System.out.println(hashMap);
        } catch (ParserConfigurationException a){
            a.printStackTrace();
        }catch (SAXException a){
            a.printStackTrace();
        } catch (IOException a){
            a.printStackTrace();
        }


    }
}
