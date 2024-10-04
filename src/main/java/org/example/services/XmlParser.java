package org.example.services;

import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.entity.Roles;
import org.example.entity.Ejb;
import org.example.entity.Methods;

import javax.xml.stream.XMLInputFactory;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.*;

public class XmlParser {

    private final PersistData pd = new PersistData();
    private static final Logger logger = LogManager.getLogger(XmlParser.class);

    public void ParseFile(String filepath, EntityManager entityManager)
    {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        try(FileInputStream fis = new FileInputStream(filepath)){
            logger.info("reading the file with path: {}", filepath);
            XMLStreamReader reader = factory.createXMLStreamReader(fis);

            List<Methods> methodList = new ArrayList<>();
            Ejb currentEJB = null;
            Roles currentRole = null;

            logger.info("Started parsing the xml file");
            while(reader.hasNext())
            {
                int event = reader.next();

                if(event == XMLStreamConstants.START_ELEMENT)
                {
                    if("security-role".equals(reader.getLocalName()))
                    {
                        skipElement(reader);
                    }
                    else if("role-name".equals(reader.getLocalName()))
                    {
                        String roleName = reader.getElementText();
                        currentRole = new Roles(roleName);

                        pd.PersistRole(currentRole,entityManager);
                    }
                    else if("ejb-name".equals(reader.getLocalName()))
                    {
                        String ejbName = reader.getElementText();
                        if( (currentEJB != null) && (!(currentEJB.getEjb_name().equals(ejbName))) )
                        {
                            logger.info("order of contents {}",methodList);
                            pd.PersistEjb(currentEJB,entityManager);
                            pd.PersistMethod(methodList,entityManager);
                            methodList = new ArrayList<>();
                            currentEJB = new Ejb(ejbName, currentRole);
                        }
                        else if(currentEJB == null) {
                            currentEJB = new Ejb(ejbName, currentRole);
                        }

                    }
                    else if("method-name".equals(reader.getLocalName()))
                    {
                        String methodName = reader.getElementText();
                        if (currentEJB != null) {
                            Methods method = new Methods(methodName,currentEJB);

                            methodList.add(method);
                        }
                    }
                }
            }
            pd.PersistEjb(currentEJB,entityManager);
            pd.PersistMethod(methodList,entityManager);
            logger.info("Finished parsing the xml file");


        } catch (Exception e) {
            logger.error("Error during Run Time", e);
            throw new RuntimeException(e);
        }

    }

    private static void skipElement(XMLStreamReader reader) throws XMLStreamException {
        int depth = 1;
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                depth++;
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                depth--;
                if (depth == 0) {
                    break;
                }
            }
        }
    }
}
