import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class main {

    //use bidi map to be able to search for keys by value as well
    static BidiMap<String, String> dict=  new DualHashBidiMap<String, String>();
    //static HashMap<String, String> dict = new HashMap<String, String>();

    //list to save terms and their information
    static LinkedList<Term> terme = new LinkedList();

    //map to save terms that are not in the dictionary (either in source file or output file)
    static HashMap<String, Integer> termsNotFound = new HashMap<String, Integer>();

    static void incrementTermsNotFoundCounter(String term){
        if (termsNotFound.containsKey(term)) {
            int value = termsNotFound.get(term)+1;
            termsNotFound.put(term,value);
        }
        else {
            termsNotFound.put(term,1);
        }
    }

    static Term searchTermBySourceName(String sName){
        for (int i = 0; i<terme.size(); i++){
            Term term = terme.get(i);
            if (term.getSourceName().equals(sName)){
                return term;
            }
        }
        return null;
    }

    static Term searchTermByOutputName(String oName){
        for (int i = 0; i<terme.size(); i++){
            Term term = terme.get(i);
            if (term.getOutputName().equals(oName)){
                return term;
            }
        }
        return null;
    }


    public static void main(String[] args) {



        dict.put("creatorName","http://example.com/creatorName>");
        dict.put("affiliation","http://example.com/affiliation");
        dict.put("identifier","http://purl.org/dc/terms/identifier");
        dict.put("alternateIdentifier","http://www.w3.org/2004/02/skos/core#altLabel");
        dict.put("creator","http://purl.org/dc/terms/creator");
        dict.put("title","http://purl.org/dc/terms/title");
        dict.put("publisher","http://purl.org/dc/terms/publisher");
        dict.put("publicationYear","http://example.com/publicationYear");
        dict.put("subject","http://purl.org/dc/terms/subject");
        dict.put("contributorName","http://purl.org/dc/terms/contributor");
        dict.put("language","http://purl.org/dc/terms/language");
        dict.put("resourceType","http://purl.org/dc/terms/type");
        dict.put("format","http://purl.org/dc/terms/format");
        dict.put("rights","http://purl.org/dc/terms/rights");
        dict.put("description", "http://purl.org/dc/terms/description");
        dict.put("geoLocationPlace","http://purl.org/dc/terms/spatial");
        dict.put("relatedIdentifier", "http://purl.org/dc/terms/relation");
        dict.put("date@dateType='Created'","http://purl.org/dc/terms/created");
        dict.put("date@dateType='Available'","http://purl.org/dc/terms/available");
        dict.put("date@dateType='Submitted'","http://purl.org/dc/terms/dateSubmitted");
        dict.put("date@dateType='Issued'","http://purl.org/dc/terms/issued");

        //create Term objects with source name and output data name

        for (Map.Entry<String, String> entry:dict.entrySet()){
            terme.add(new Term(entry.getKey(), entry.getValue()));
        }






        /*
        //create Term objects with corresponding sourceName and OutputName

        HashMap<String, Term> terms = new HashMap<String, Term>();
        terms.put("creatorName",new Term("creatorName","http://example.com/creatorName>"));
        terms.put("affiliation", new Term ("affiliation","http://example.com/affiliation"));
        //todo has several, identical outputNames
        terms.put("identifier", new Term("identifier","http://purl.org/dc/terms/identifier"));
        terms.put("alternateIdentifier", new Term("alternateIdentifier","http://purl.org/dc/terms/identifier"));


        terms.put("creator",new Term("creator","http://purl.org/dc/terms/creator"));
        terms.put("title",new Term("title","http://purl.org/dc/terms/title"));
        terms.put("publisher",new Term("publisher","http://purl.org/dc/terms/publisher"));
        terms.put("publicationYear",new Term("publicationYear","http://example.com/publicationYear"));
        terms.put("subject", new Term("subject","http://purl.org/dc/terms/subject"));
        terms.put("contributorName", new Term("contributorName","http://purl.org/dc/terms/contributor"));
        terms.put("language", new Term("language","http://purl.org/dc/terms/language"));
        terms.put("resourceType", new Term("resourceType","http://purl.org/dc/terms/type"));
        terms.put("format", new Term("format","http://purl.org/dc/terms/format"));
        terms.put("rights", new Term("rights","http://purl.org/dc/terms/rights"));
        terms.put("description", new Term("description", "http://purl.org/dc/terms/description"));
        terms.put("geoLocationPlace", new Term("geoLocationPlace","http://purl.org/dc/terms/location"));
        terms.put("relatedIdentifier", new Term("relatedIdentifier", "http://purl.org/dc/terms/source"));


        //todo distinct dates (created, available, etc.)


*/

        LinkedList<String> exclusions = new LinkedList<String>(Arrays.asList("OAI-PMH", "responseDate", "request", "ListRecords","metadata", "formats", "rightsList", "contributors","resumptionToken", "geoLocations", "titles", "dates", "subjects", "creators", "record", "pointLatitude", "pointLongitude", "polygonPoint", "resource", "geoLocationPolygon", "contributor", "geoLocation", "descriptions", "header", "fundingReferences", "alternateIdentifiers","geoLocationPoint", "setSpec", "datestamp", "relatedIdentifiers"));





        //source file
        try {
            File file = new File("src/main/resources/source.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);
            document.getDocumentElement().normalize();

            //HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
            NodeList entries = document.getElementsByTagName("*");
            for (int i = 0; i < entries.getLength(); i++) {
                Term term = null;
                Element element = (Element) entries.item(i);

                //elements that are mapped based on their attributes are considered in the following if-loops
                if (element.getAttribute("dateType").equals("Created")){
                    term = searchTermBySourceName(element.getNodeName()+"@"+"dateType="+"'"+element.getAttribute("dateType")+"'");
                    //System.out.println("Created date found, search term by Source Name: " + element.getNodeName()+"@"+"dateType="+"'"+element.getAttribute("dateType")+"'");
                } else if (element.getAttribute("dateType").equals("Available")){
                    term = searchTermBySourceName(element.getNodeName()+"@"+"dateType="+"'"+element.getAttribute("dateType")+"'");
                    //System.out.println("Available date found, search term by Source Name: " + element.getNodeName()+"@"+"dateType="+"'"+element.getAttribute("dateType")+"'");
                }else if (element.getAttribute("dateType").equals("Submitted")){
                    term = searchTermBySourceName(element.getNodeName()+"@"+"dateType="+"'"+element.getAttribute("dateType")+"'");
                    //System.out.println("Available date found, search term by Source Name: " + element.getNodeName()+"@"+"dateType="+"'"+element.getAttribute("dateType")+"'");
                }else if (element.getAttribute("dateType").equals("Issued")){
                    term = searchTermBySourceName(element.getNodeName()+"@"+"dateType="+"'"+element.getAttribute("dateType")+"'");
                    //System.out.println("Available date found, search term by Source Name: " + element.getNodeName()+"@"+"dateType="+"'"+element.getAttribute("dateType")+"'");
                }else {
                    term = searchTermBySourceName(element.getNodeName());
                }


                if (term!=null){
                    term.incrementSourceOcurrence();
                }
                /*else if (!element.getAttribute("alternateIdentifier").equals("") && terms.containsKey(element.getNodeName()+"@alternateIdentifierType="+element.getAttribute("alternateIdentifierType")))
                {
                    terms.get(element.getNodeName()+"@alternateIdentifierType="+element.getAttribute("alternateIdentifierType")).incrementSourceOcurrence();
                }*/

                //note down the terms that are unknown to the validation tool
                else
                {
                    if(!exclusions.contains(element.getNodeName())){
                        incrementTermsNotFoundCounter(element.getNodeName());
                        System.out.println("Attributes: " + element.getAttribute("dateType"));
                        //System.err.println("Term with name: " + element.getNodeName() + " not found");
                    }
                }

/*
                //if entry exists, increment by one
                if (hashMap.containsKey(element.getNodeName())) {
                    hashMap.put(element.getNodeName(), hashMap.get(element.getNodeName()) + 1);

                    //else create entry
                } else {
                    hashMap.put(element.getNodeName(), 1);
                }*/

            }
            System.err.println("terms Not found: " + termsNotFound);
            System.out.println(terme);
            //System.out.println(hashMap);
        } catch (ParserConfigurationException a) {
            a.printStackTrace();
        } catch (SAXException a) {
            a.printStackTrace();
        } catch (IOException a) {
            a.printStackTrace();
        }


        try {
            FileInputStream is = new FileInputStream("src/main/resources/solve1.9.nq");

            NxParser nxp = new NxParser();
            //nxp.parse(is);

            //count how often the terms appear in the output file
            HashMap<String, Integer> outMap = new HashMap<String, Integer>();
            for (Iterator<Node[]> it = nxp.parse(is);it.hasNext();){
                Node[] node = it.next();
                String out = "";



                    //we are only interested in the predicate (second entry) => node[1]
                    //if HashMap contains entry, increment by 1

                /*if (node[1].toString().equals("")){
                    terms.get("").incrementOutputOcurrence();
                } else if (node[1].toString().equals("")) {
                    terms.get("").incrementOutputOcurrence();
                }else if (node[1].toString().equals("")) {
                    terms.get("").incrementOutputOcurrence();
                }else if (node[1].toString().equals("")) {
                    terms.get("").incrementOutputOcurrence();
                }else if (node[1].toString().equals("")) {
                    terms.get("").incrementOutputOcurrence();
                }else if (node[1].toString().equals("")) {
                    terms.get("").incrementOutputOcurrence();
                }else if (node[1].toString().equals("")) {
                    terms.get("").incrementOutputOcurrence();
                }else if (node[1].toString().equals("")) {
                    terms.get("").incrementOutputOcurrence();
                }else if (node[1].toString().equals("")) {
                    terms.get("").incrementOutputOcurrence();
                }else if (node[1].toString().equals("")) {
                    terms.get("").incrementOutputOcurrence();
                }else if (node[1].toString().equals("")) {
                    terms.get("").incrementOutputOcurrence();
                }
*/
                //remove "<" before and ">" after predicate
                String predicate = node[1].toString().substring(1,node[1].toString().length()-1);
                Term term = searchTermByOutputName(predicate);
                if (term!=null){
                    term.incrementOutputOcurrence();
                } else {
                   // System.out.println("term not found: " + node[1].toString());
                    incrementTermsNotFoundCounter(predicate);
                }




                    if (outMap.containsKey(node[1].toString())){
                        int value = outMap.get(node[1].toString());
                        outMap.put(node[1].toString(),value++);
                    }
                    //else create entry with counter = 1
                    else {
                        outMap.put(node[1].toString(),1);
                    }
                    out+=node[1];


            }
            System.out.println(terme);
            System.err.println("terms Not found: " + termsNotFound);
            System.out.println(outMap);
            System.out.println("Number of valid terms:" + terme.size());

            //for (Node[] nx : nxp)
                // prints the subject, eg. <http://example.org/>
                //System.out.println(nx[0]);
        }catch (IOException e){
            e.printStackTrace();
        }

        /*

        String fileNameOrUri = "src/main/resources/solve.nq";
        Model model = ModelFactory.createDefaultModel();
        InputStream is = FileManager.get().open(fileNameOrUri);
        if (is != null) {
            model.read(is, null, "N-TRIPLE");
            model.write(System.out, "TURTLE");
        } else {
            System.err.println("cannot read " + fileNameOrUri);;
        }
    }
    */

    /*

        Model model = ModelFactory.createDefaultModel();

        //renamed file from ttl to .trig because it is actually a trig file
        FileManager.get().readModel(model, "src/main/resources/output.trig");

        System.out.println(model.listNameSpaces().toList().toString());
        System.out.println(model.listObjects().toList().toString());
        System.out.println(model.listStatements().toList().toString());
        System.out.println(model.listSubjects().hasNext());
        System.out.println(model.getReader().toString());
        System.out.println(model.createList().toString());
        */


    }


}
