package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;
import org.example.entity.Ejb;
import org.example.entity.Methods;
import org.example.entity.Roles;
import org.example.filter.ArgsValidate;
import org.example.filter.Compare;
import org.example.filter.XmlDiff;
import org.example.services.DataFetch;
import org.example.services.GenerateXml;
import org.example.services.XmlParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class); //initializing logger
    private final XmlParser xmlParser;
    private final DataFetch dataFetch;
    private final GenerateXml generateXml;
    private final Compare compare;
    private final ArgsValidate argsCompare;
    private final XmlDiff xmlDiff;
    private static EntityManagerFactory emf;

    public static void main(String[] args)
    {
        Main main = new Main(); //main class object
        logger.info("Initialized Main class object");
        if(args.length > 0)     //validating argument values
        {
            logger.info("checking the argument length...{}", args.length);
            main.argumentsCompare(args);
        }
        String propertiesFilePath = "./src/main/resources/config.properties";

        try(InputStream input = new FileInputStream(propertiesFilePath)) //try-catch block for exception handling
        {
            Properties prop = new Properties();
            Properties UserProp = new Properties();
            Map<String,Object> properties = new HashMap<>();

            prop.load(input); //loaded properties
            try(InputStream userInput = Main.class.getClassLoader().getResourceAsStream(prop.getProperty("config")))
            {
                UserProp.load(userInput);
            }
            logger.info("username -> {}, password -> {}", UserProp.getProperty("username"), UserProp.getProperty("password"));

            //getting properties from config.properties to pass it to persistence.xml
            properties.put("jakarta.persistence.jdbc.url","jdbc:mysql://localhost:3306/" + prop.getProperty("dbname"));
            properties.put("jakarta.persistence.jdbc.user",UserProp.getProperty("username"));
            properties.put("jakarta.persistence.jdbc.password",UserProp.getProperty("password"));

            Path permission_filepath = Path.of(prop.getProperty("permission.xml")); //xml file directory
            String suffix = prop.getProperty("permission.suffix"); // suffix to find similar xml files in the directory
            Path gen_filepath = Path.of(prop.getProperty("generate.xml")); //path of generated xml file
            Path Sessionbean_filepath = Path.of(prop.getProperty("sessionbean.xml")); //java(sessionBean) & xml file directory
            String xml_suffix = prop.getProperty("sessionbean.suffix"); //EJB.xml file suffix
            String gen_suffix = prop.getProperty("generate.suffix"); //gen.xml generated file suffix

            logger.info("generated suffix -->  {}", gen_suffix);

            String choice = prop.getProperty("choice"); //operation that need to perform

            EntityManager entityManager;
            if(choice.equals("parse")) // only parsing and storing into db (permission xml)
            {
                List<Path> paths = listFiles(permission_filepath, suffix); //listing file paths with similar suffix

                properties.put("jakarta.persistence.schema-generation.database.action","drop-and-create");
                emf = Persistence.createEntityManagerFactory("permit",properties);
                entityManager = emf.createEntityManager();

                for (Path p : paths) //parsing through each xml file in the list
                {
                    logger.info("Parsing the {} file", p);
                    main.xmlParser(p.toString(), entityManager); //storing xml data into db
                }
                System.out.println("\nParsing complete.....\n");
                main.close(entityManager);
            }
            else if(choice.equals("generate")) // only generating new xml based on existing db
            {
                properties.put("jakarta.persistence.schema-generation.database.action","create");
                emf = Persistence.createEntityManagerFactory("permit",properties);
                entityManager = emf.createEntityManager();
                main.FetchData(gen_filepath, entityManager); // fetching data from db and generating xml
                System.out.println("\nGeneration complete.....\n");
                main.close(entityManager);
            }
            else if(choice.equals("compare")) // expose unused ejbs and methods in permission xml
            {
                properties.put("jakarta.persistence.schema-generation.database.action","create");
                emf = Persistence.createEntityManagerFactory("permit",properties);
                entityManager = emf.createEntityManager();
                List<Path> xml_paths = listFiles(Sessionbean_filepath, xml_suffix);
                logger.info("going to compare the xml_paths");
                main.Compare(xml_paths, entityManager);
                System.out.println("\nComparison complete.....\n");
                main.close(entityManager);
            }
            else if(choice.equals("diff"))
            {
                List<Path> PermissionPaths = listFiles(permission_filepath, suffix); //listing file paths with similar suffix
                List<Path> GenerationPaths = listFiles(gen_filepath, gen_suffix);
                main.XMLdiff(PermissionPaths.get(0),GenerationPaths.get(0));
            }
            else
            {
                logger.error("Input undefined...");
                throw new RuntimeException("Wrong Input");
            }



        }
        catch (IOException | PersistenceException e)
        {
            logger.error(String.valueOf(e));
            System.err.println("Exception" + e);
        }
    }

    public Main() //Main class constructor
    {
        //objects are initialized for other classes
        argsCompare = new ArgsValidate();
        xmlParser = new XmlParser();
        dataFetch = new DataFetch();
        generateXml = new GenerateXml();
        compare = new Compare();
        xmlDiff = new XmlDiff();
        logger.info("Into the Main class Constructor");
    }

    public void xmlParser(String filepath,EntityManager entityManager) //function to store xml data into db
    {
        logger.info("calling ParseFile with xmlParser object");
        xmlParser.ParseFile(filepath,entityManager);
    }

    public void FetchData(Path filepath,EntityManager entityManager) //function to fetch data from db
    {
        logger.info("calling Data Fetch service...");
        List<Roles> roles = dataFetch.fetchRoles(entityManager); //fetching roles
        List<Ejb> ejbs = dataFetch.fetchEjbs(entityManager);     //fetching ejbs
        List<Methods> methods = dataFetch.fetchMethods(entityManager); //fetching methods
        logger.info("Finished fetching the data from db");
        generateXml.generate(roles,ejbs,methods,filepath); //generating xml file
    }

    public static List<Path> listFiles(Path path,String suffix) throws IOException
    {
        //listing all the files with similar suffix in the given directory
        logger.info("Listing files from the directories and sub directories");
        List<Path> result;
        try (Stream<Path> walk = Files.walk(path)) {
            logger.info("into try of listFiles.");
            result = walk.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(suffix))
                    .collect(Collectors.toList());
            logger.info("end of try of listFiles.");
        }
        logger.info("returning result of list of file path...");
        return result;
    }

    public void Compare(List<Path> filepath, EntityManager entityManager)
    {
        logger.info("calling expose method from main class");
        compare.expose(filepath, entityManager);
    }

    public void argumentsCompare(String[] args) //CLI arguments validated
    {
        argsCompare.Compare(args);
    }

    public void XMLdiff(Path PermissionPaths, Path GeneratedPaths)
    {
        xmlDiff.ComparingXml(PermissionPaths.toString(),GeneratedPaths.toString());
        logger.info("Calling XmlDiff (comparingXml method) class from Main");
    }

    public void close(EntityManager entityManager)
    {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
            logger.info("closing Entity Manager...");
        }
        if (emf != null && emf.isOpen()) {
            emf.close();
            logger.info("closing Entity Manager Factory...");
        }
    }

}
