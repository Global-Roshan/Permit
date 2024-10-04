package org.example.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ArgsValidate {

    private static final Logger logger = LogManager.getLogger(ArgsValidate.class); //initializing logger

    public void Compare(String[] args) {

        String propertiesFilePath = "./src/main/resources/config.properties";
        File propertiesFile = new File(propertiesFilePath);

        // Create properties object
        Properties prop = new Properties();

        // Load existing properties from the file
        try {
            if(!propertiesFile.exists())
            {
                propertiesFile.getParentFile().mkdirs();
                prop.setProperty("sessionbean.suffix","EJB.xml");
                prop.setProperty("permission.suffix","desc.xml");
                prop.setProperty("generate.suffix","gen.xml");
                prop.setProperty("generate.xml","/home/rohan/XML_FILES/");
                prop.setProperty("dbname","permit");
                prop.setProperty("sessionbean.xml","/src/main/java/org/example/com/invient/business/");
                prop.setProperty("permission.xml","/home/rohan/XML_FILES/");
                prop.setProperty("config","user.properties");
                prop.setProperty("choice","compare");
            }
            else {
                InputStream input = new FileInputStream(propertiesFile);
                prop.load(input);
            }

        } catch (IOException e) {
            System.out.println("Warning: Config file not found, a new one will be created.");

        }

        boolean valid = true;
        Map<String,Object> properties = new HashMap<>();

        for (String arg : args) {
            if (arg.startsWith("--permissionxml=")) {                       //permission xml file path
                String value = arg.substring("--permissionxml=".length());
                if (value.isEmpty() || !new File(value).exists()) {
                    logger.info("Error: Permission XML path cannot be empty.");
                    valid = false;
                } else {
                    prop.setProperty("permission.xml", value);
                }
            } else if (arg.startsWith("--config=")) {                     //password of db
                String value = arg.substring("--config=".length());
                if (value.isEmpty()) {
                    logger.info("Error: Enter a user filename to login.");
                    valid = false;
                } else {
                    prop.setProperty("config",value);
                }
            } else if (arg.startsWith("--dbname=")) {                       //database name created
                String value = arg.substring("--dbname=".length());
                properties.put("jakarta.persistence.jdbc.url","jdbc:mysql://localhost:3306/" + value);
                logger.info("Args database name checking");

                if (value.isEmpty()) {
                    logger.info("Error: Database name cannot be empty.");
                    valid = false;
                } else {
                    prop.setProperty("dbname", value);
                }

            } else if (arg.startsWith("--sessionbeanxml=")) {               //sessionbean xml file path
                String value = arg.substring("--sessionbeanxml=".length());
                if (value.isEmpty() || !new File(value).exists()) {
                    logger.info("Error: Sessionbean path cannot be empty.");
                    valid = false;
                } else {
                    prop.setProperty("sessionbean.xml", value);
                }
            } else if (arg.startsWith("--generatexml=")) {                  //generating permission xml file path
                String value = arg.substring("--generatexml=".length());
                if (value.isEmpty() || !new File(value).exists()) {
                    logger.info("Error: generating xml file path cannot be empty.");
                    valid = false;
                } else {
                    prop.setProperty("generate.xml", value);
                }
            } else if (arg.startsWith("--choice=")) {                  //generating permission xml file path
                String value = arg.substring("--choice=".length());
                if (value.isEmpty() || (!value.equals("compare") && !value.equals("parse") && !value.equals("generate") && !value.equals("diff") )) {
                    logger.info("Error: Not a valid choice.");
                    valid = false;
                } else {
                    prop.setProperty("choice", value);
                }
            }

        }

        // Only store properties if all values are valid
        if (valid) {
            try (OutputStream output = new FileOutputStream(propertiesFilePath)) {
                prop.store(output, "Updated Configuration Properties");
                logger.info("Properties saved to {}", propertiesFilePath);
            }
            catch (IOException e) {
                System.out.println("inside the catch of valid if condition");
                throw new RuntimeException("Exception: ", e);
            }

        } else {

            logger.info("Error: One or more properties were invalid. No changes were saved.");
            throw new RuntimeException("One or more properties in argument were invalid");
        }


    }
}
