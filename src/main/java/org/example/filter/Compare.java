package org.example.filter;

import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.entity.Ejb;
import org.example.entity.Methods;
import org.example.services.DataFetch;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.*;

public class Compare {

    DataFetch dataFetch = new DataFetch();
    private static final Logger logger = LogManager.getLogger(Compare.class); //initializing logger

    public void expose(List<Path> filepath, EntityManager entityManager)
    {
        try
        {
            Map<String,Set<String>> methods = new HashMap<>(); // methods from reading java files
            Map<String, Set<String>> permission_methods = new HashMap<>();
            logger.info("going through each xmlFilePath...");


            //fetching from db which was stored in permission.xml
            List<Ejb> Ejbs = dataFetch.fetchEjbs(entityManager);
            List<Methods> Methods = dataFetch.fetchMethods(entityManager);


            for (Path p : filepath) {

                XMLInputFactory factory = XMLInputFactory.newInstance();
                try(FileInputStream fis = new FileInputStream(p.toString())) {
                    XMLStreamReader reader = factory.createXMLStreamReader(fis);
                    logger.info("this is path p {}", p.toString());
                    Map<String,Set<String>> TempMethods = getMethods(reader);

                    for(String key : TempMethods.keySet())
                    {
                        methods.put(key,TempMethods.get(key));
                    }

                    //all the methods are listed here.....
                    //which are read from java sessionbean files
                }
            }


            String curr_ejbName = "";
            Set<String> local_methodname = new HashSet<>();

            for (Methods method : Methods) {
                String currMethodName = method.getMethod_name();
                local_methodname.add(currMethodName);
                Ejb ejb = method.getEjb();

                if (!curr_ejbName.equals(ejb.getEjb_name())) {
                    // Only add to permission_methods if it's not the first ejb
                    if (!curr_ejbName.isEmpty()) {

                        if(permission_methods.containsKey(curr_ejbName))
                        {
                            Set<String> existingMethods = permission_methods.get(curr_ejbName);
                            existingMethods.addAll(local_methodname);
                        }
                        else {
                            permission_methods.put(curr_ejbName, new HashSet<>(local_methodname)); // Use a new list to store
                        }
                    }

                    curr_ejbName = ejb.getEjb_name();

                    local_methodname.clear(); // Clear the current list for new methods

                }


            }

            // Don't forget to add the last ejb after exiting the loop
            if (!curr_ejbName.isEmpty()) {
                permission_methods.put(curr_ejbName, new HashSet<>(local_methodname));
            }




            for(String key : permission_methods.keySet())
            {
                Set<String> values = permission_methods.get(key);
                //System.out.println("values inside -- " + values + " " + values.contains("*"));
                if(values.contains("*"))
                {
                    methods.remove(key);
                }
                else if(!values.isEmpty())
                {
                    Set<String> valuesInMethods = methods.get(key);
                    if (valuesInMethods != null) {
                        // Remove any value from valuesA that exists in valuesB
                        valuesInMethods.removeAll(values);
                        if(valuesInMethods.isEmpty())
                        {
                            methods.remove(key);
                        }
                    }

                }
            }


            logger.info("size of methods-> {}", methods.size());


            System.out.println("\nmethods that are not in Permission.xml but there in SessionBeans file : \n" + methods);

        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Set<String>> getMethods(XMLStreamReader reader) throws XMLStreamException, ClassNotFoundException {

        Map<String, Set<String>> methodNames = new HashMap<>();
        String CurrEjbName = "";
        Set<String> set_names = new HashSet<>();

        while(reader.hasNext())
        {

            int event = reader.next();



            if(event == XMLStreamConstants.START_ELEMENT)
            {

                if("ejb-name".equals(reader.getLocalName()))
                {
                    CurrEjbName = reader.getElementText();
                }
                if("local".equals(reader.getLocalName()))
                {

                    String localName = reader.getElementText();
                    Class<?> clazz = Class.forName(localName); // error here

                    Method[] methods = clazz.getMethods();

                    for(Method m : methods)
                    {
                        set_names.add(m.getName());
                    }

                    //System.out.println("EJB name inside the for loop ------> " + CurrEjbName);
                    methodNames.put(CurrEjbName,new HashSet<>(set_names));

                }
            }
        }

        return methodNames;
    }



}
