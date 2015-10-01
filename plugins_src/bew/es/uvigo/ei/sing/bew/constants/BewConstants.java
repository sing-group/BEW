package es.uvigo.ei.sing.bew.constants;

/**
 * Class to store constants and variables used in the program.
 * 
 * @author Gael
 * 
 */
public class BewConstants {
	// Constant Strings to Conditions
	public static final String ANTIMICROBIAL = "antimicrobial agent";
	public static final String METACONDITION = "metacondition";
	public static final String AND = "_and_";
	// Constant Strings to web services
	public static final String WEB = "http://biofomics.org/api";
	// public static final String WEB = "http://biofomics.org/biofomics2/api";
	// public static final String WEB = "http://7.28.200.243/biofomics/api";
	// public static final String WEB = "http://192.168.200.34/biofomics/api";

	public static final String LISTMETHODS = "listMethods";
	public static final String LISTCONDITIONS = "listConditions";
	public static final String LISTVALUES = "listConstantValues";
	// Constant Strings to files
	public static final String METHODFILE = "methods.txt";
	public static final String CONDITIONFILE = "conditions.txt";
	public static final String VALUESFILE = "condition_values.txt";
	public static final String INTRASCHEMA = "bml.xsd";
	public static final String INTERSCHEMA = "bml_inter.xsd";
	// Constant Strings to XML
	public static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	public static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
	public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	public static final String APACHE = "http://apache.org/xml/features/validation/schema";
	public static final String NS_URL = "http://www.example.org/bml";
	public static final String SCHEMA_PATH = "http://miabie.org/xml/bml.xsd";
	public static final String NS_URL_INTER = "http://www.example.org/bml_inter";
	public static final String SCHEMA_PATH_INTER = "http://miabie.org/xml/bml_inter.xsd";
	public static final String XML_INTER_HEADER = "ns0:bml_inter";
	// Constant Strings to temp folder
	public static final String TEMPFILENAME = "bew_temp";
	// Constant Strings to security
	public static final String KEYSTRING = "e8ffc7e563116794";
	public static final byte[] IVBYTES = { 0x0A, 0x10, 0x11, 0x00, 0x0B, 0x3C,
			0x33, 0x2A };
	// Constant Integers to sizes
	public static final Integer PLOT_WIDTH = 640;
	public static final Integer PLOT_HEIGTH = 480;
	// Constant name limit
	public static final Integer NAME_LIMIT = 50;
	public static final Integer DESCRIPTION_LIMIT = 500;
	public static final Integer AUTHORS_LIMIT = 100;
	public static final Integer ORGANIZATION_LIMIT = 100;
	// Other Strings
	public static final String DEFAULT_VALUE = "...";

	// Variable Strings
	public static String SAVEDIR = "";
	public static String LOADDIR = "";
	public static String USER = "";
	public static String PASS = "";
	public static String OS = "";

}
