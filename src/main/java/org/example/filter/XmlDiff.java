package org.example.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;
public class XmlDiff {


    private static final Logger logger = LogManager.getLogger(XmlDiff.class); //initializing logger

    public void ComparingXml(String PermissionPath, String GeneratedPath)
    {

        try {
            List<String> oldList = getXMLContentAsList(PermissionPath);
            List<String> newList = getXMLContentAsList(GeneratedPath);


            List<String> diff = myersDiff(oldList, newList);
            System.out.println("Differences written....");

            OutputStream outputStream = new FileOutputStream("./XML/diff.xml");
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter xmlWriter = factory.createXMLStreamWriter(outputStream, "UTF-8");

            xmlWriter.writeStartElement("Differences");
            xmlWriter.writeCharacters("\n");

            for (String line : diff) {
                logger.info("writing the differences in XML file");
                System.out.println(line);
                xmlWriter.writeStartElement("change");
                xmlWriter.writeCharacters(line);
                xmlWriter.writeEndElement();
                xmlWriter.writeCharacters("\n");
            }

            xmlWriter.writeEndElement();
            xmlWriter.flush();

        } catch (Exception e) {
            System.out.println("Exception" + e);
        }
    }

    private List<String> getXMLContentAsList(String fileName) throws Exception {
        List<String> contentList = new ArrayList<>();
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(new FileInputStream(fileName));

        StringBuilder elementContent = new StringBuilder();
        String currentElementName = null;

        while (reader.hasNext()) {
            int event = reader.next();

            if (event == XMLEvent.START_ELEMENT) {
                if (currentElementName != null) {
                    // Close previous element
                    contentList.add("<" + currentElementName + ">" + elementContent.toString().trim() + "</" + currentElementName + ">");
                }
                currentElementName = reader.getLocalName();
                elementContent.setLength(0); // Reset the content for the new element
            } else if (event == XMLEvent.CHARACTERS) {
                elementContent.append(reader.getText());
            } else if (event == XMLEvent.END_ELEMENT) {
                if (currentElementName != null) {
                    contentList.add("<" + currentElementName + ">" + elementContent.toString().trim() + "</" + currentElementName + ">");
                    currentElementName = null; // Reset after adding
                    elementContent.setLength(0); // Reset the content
                }
            }
        }

        // Handle the last element if needed
        if (currentElementName != null) {
            contentList.add("<" + currentElementName + ">" + elementContent.toString().trim() + "</" + currentElementName + ">");
        }

        reader.close();
        return contentList;
    }

    private List<String> myersDiff(List<String> oldList, List<String> newList) {
        List<String> diff = new ArrayList<>();
        int[][] d = new int[oldList.size() + 1][newList.size() + 1];

        for (int i = 0; i <= oldList.size(); i++) {
            for (int j = 0; j <= newList.size(); j++) {
                if (i == 0 || j == 0) {
                    d[i][j] = Math.max(i, j);
                } else if (oldList.get(i - 1).equals(newList.get(j - 1))) {
                    d[i][j] = d[i - 1][j - 1];
                } else {
                    d[i][j] = 1 + Math.min(d[i - 1][j], Math.min(d[i][j - 1], d[i - 1][j - 1]));
                }
            }
        }

        // Backtrack to find differences
        int i = oldList.size();
        int j = newList.size();
        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && oldList.get(i - 1).equals(newList.get(j - 1))) {
                i--;
                j--;
            } else if (j > 0 && (i == 0 || d[i][j - 1] < d[i - 1][j])) {
                diff.add("Added: " + newList.get(j - 1));
                j--;
            } else if (i > 0 && (j == 0 || d[i][j - 1] >= d[i - 1][j])) {
                diff.add("Removed: " + oldList.get(i - 1));
                i--;
            }
        }
        return diff;
    }

}
