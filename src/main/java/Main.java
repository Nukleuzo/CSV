import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";

        File dir = new File("src//main//java//");
        File data1 = new File(dir, "data.json");
        File data2 = new File(dir, "data2.json");

        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, String.valueOf(data1));

        List<Employee> list2 = parseXML("data.xml");
        String json2 = listToJson(list2);
        writeString(json2, String.valueOf(data2));
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> list = null;
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {

            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            list = csv.parse();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(list, listType);
    }

    private static void writeString(String json, String fileName) {

        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(json);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static @NotNull List<Employee> parseXML(String fileName) {
        List<Employee> staff = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newDefaultInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document doc = documentBuilder.parse(new File(fileName));
            Node root = doc.getDocumentElement();
            NodeList nodeList = root.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.ELEMENT_NODE == node.getNodeType()) {
                    Employee employee = new Employee();
                    NodeList nodeList1 = node.getChildNodes();
                    for (int x = 0; x < nodeList1.getLength(); x++) {
                        Node child = nodeList1.item(x);
                        if (child.getNodeName().equals("id")) employee.id = Long.parseLong(child.getTextContent());
                        if (child.getNodeName().equals("firstName")) employee.firstName = child.getTextContent();
                        if (child.getNodeName().equals("lastName")) employee.lastName = child.getTextContent();
                        if (child.getNodeName().equals("country")) employee.country = child.getTextContent();
                        if (child.getNodeName().equals("age")) employee.age = Integer.parseInt(child.getTextContent());
                    }
                    staff.add(employee);
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
        return staff;
    }
}