package org.example.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.entity.Ejb;
import org.example.entity.Methods;
import org.example.entity.Roles;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import java.io.*;
import java.nio.file.Path;
import java.util.List;


public class GenerateXml {

    private static final Logger logger = LogManager.getLogger(GenerateXml.class);

    public void generate(List<Roles> roles, List<Ejb> ejbs, List<Methods> methods, Path filePath)
    {
        logger.info("Into the generate xml function");
        XMLOutputFactory factory = XMLOutputFactory.newInstance();

        String xmlFilaPath = filePath.toString() + "/gen.xml";

        File xmlfile = new File(xmlFilaPath);

        try {
            if (!xmlfile.exists()) {
                xmlfile.createNewFile();
            }
            else
            {
                logger.info("file already exist");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (OutputStream outputStream = new FileOutputStream(xmlfile)) {
            XMLStreamWriter xmlWriter = factory.createXMLStreamWriter(outputStream, "UTF-8");

            //xmlWriter.writeStartElement("ejb-jar");
            logger.info("Started generating xml file in {}", xmlfile);

            // Write security roles
            for (Roles role : roles) {
                xmlWriter.writeStartElement("security-role");
                xmlWriter.writeCharacters("\n");
                xmlWriter.writeStartElement("role-name");
                xmlWriter.writeCharacters(role.getRole_name());
                xmlWriter.writeEndElement(); // role-name
                xmlWriter.writeCharacters("\n");
                xmlWriter.writeEndElement(); // security-role
                xmlWriter.writeCharacters("\n");
            }

            // Write method permissions
            for (Roles role : roles) {
                xmlWriter.writeCharacters("\n");
                xmlWriter.writeStartElement("method-permission");
                xmlWriter.writeCharacters("\n");
                xmlWriter.writeStartElement("role-name");
                xmlWriter.writeCharacters(role.getRole_name());
                xmlWriter.writeEndElement(); // role-name
                logger.info("Through-> {}", role.getRole_name());

                int rid = role.getId();

                for (Ejb ejb : ejbs) {
                    Roles roles1 = ejb.getRoles();
                    if(roles1.getId() != rid)
                    {
                        continue;
                    }

                    int eid = ejb.getId();


                    for (Methods method : methods) {
                        Ejb ejb1 = method.getEjb();
                        if(ejb1.getId() != eid)
                        {
                            continue;
                        }
                        xmlWriter.writeStartElement("method");
                        xmlWriter.writeCharacters("\n");
                        xmlWriter.writeStartElement("ejb-name");
                        xmlWriter.writeCharacters(ejb.getEjb_name());
                        xmlWriter.writeEndElement(); // ejb-name
                        xmlWriter.writeCharacters("\n");
                        xmlWriter.writeStartElement("method-name");
                        xmlWriter.writeCharacters(method.getMethod_name());
                        xmlWriter.writeEndElement(); // method-name
                        xmlWriter.writeCharacters("\n");
                        xmlWriter.writeEndElement(); // method
                        xmlWriter.writeCharacters("\n");
                    }

                    xmlWriter.writeCharacters("\n");
                }

                xmlWriter.writeCharacters("\n");

                xmlWriter.writeEndElement(); // method-permission
            }

            //xmlWriter.writeEndElement(); // ejb-jar
            xmlWriter.writeEndDocument();

            xmlWriter.flush();
            logger.info("Finished generating Xml file");


        } catch (XMLStreamException | IOException e) {

            System.err.print("Exception:- " + e);

        }
    }
}
