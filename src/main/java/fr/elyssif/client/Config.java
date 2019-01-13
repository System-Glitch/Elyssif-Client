package fr.elyssif.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Singleton class for config storage
 * @author Jérémy LAMBERT
 *
 */
public final class Config {

	private static Config instance;
	private static final String CONFIG_FILE_PATH = "config.xml";

	private Hashtable<String, String> values;
	private boolean export = true; //Set to false to prevent config export
	private boolean verbose = false;

	private Config() {}

	/**
	 * Load the config
	 * @return true on success
	 */
	public final synchronized boolean load() {
		Logger.getGlobal().info("Loading config");

		if(export && !checkConfigExists()) {
			Logger.getGlobal().info("Config file not found, export default config...");
			if(!exportDefaultConfig())
				return false;
			else
				Logger.getGlobal().info("Default config exported");
		}

		values = new Hashtable<>();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document xml = builder.parse(export ? new FileInputStream(new File(CONFIG_FILE_PATH)) : Main.class.getClassLoader().getResourceAsStream("config.xml"));
			Element root = xml.getDocumentElement();

			Node node = root.getFirstChild();
			while(node != null) {
				if(node.getNodeName() != "#text")
					values.put(node.getNodeName(), node.getTextContent());
				node = node.getNextSibling();
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			Logger.getGlobal().log(Level.SEVERE, "Couldn't load config", e);
			return false;
		}

		setDefaults();

		Logger.getGlobal().info("Loaded config");
		Logger.getGlobal().info("Current environment: " + get("Environment"));
		Logger.getGlobal().info("Remote host: " + get("Host"));
		Logger.getGlobal().info("Verbose: " + isVerbose());
		return true;
	}

	/**
	 * Set a default value if key doesn't exist.
	 * @param key
	 * @param value
	 */
	private void setDefault(String key, String value) {
		if(!values.containsKey(key))
			set(key, value);
	}

	private void setDefaults() {
		// Set default config here
		// Required values that can be omitted in the config file


		setVerbose(values.containsKey("Verbose") ? get("Verbose").equals("true") : false);
		values.remove("Verbose");
	}

	/**
	 * Get if the config file should be exported.<br>
	 * If true, config file is exported if not exists and is read from external file.<br>
	 * If false, config is loaded from default one from resources inside the jar.
	 * @return export
	 */
	public final boolean isExport() {
		return export;
	}

	/**
	 * Set if the config file should be exported or not.<br>
	 * If true, config file is exported if not exists and is read from external file.<br>
	 * If false, config is loaded from default one from resources inside the jar.
	 * @param export
	 */
	public final void setExport(boolean export) {
		this.export = export;
	}

	/**
	 * Get if extra information should be displayed in the logs.
	 * @return verbose
	 */
	public final boolean isVerbose() {
		return verbose;
	}

	/**
	 * Set if extra information should be displayed in the logs.
	 * @param verbose
	 */
	public final void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/**
	 * Get a value from the config
	 * @param key
	 * @return the value associated with the given key, null if not found
	 */
	public final String get(String key) {
		return values.get(key);
	}

	/**
	 * Set a value in the config. The change is not permanent, use {@link #save() save} to save to disk.
	 * @param key the name of the field. If already exists, override the previous value
	 * @param value
	 * @see {@link #save() save}
	 */
	public final void set(String key, String value) {
		values.put(key, value);
	}

	/**
	 * Save the current config into the config file.
	 * @return true on success
	 */
	public final boolean save() {

		if(isExport()) {
			Logger.getGlobal().info("Saving config...");
			try {
				DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
				Document document = documentBuilder.newDocument();

				Element root = document.createElement("Config");
				document.appendChild(root);

				for(Entry<String, String> entry : values.entrySet()) {
					Logger.getGlobal().info(entry.getKey());
					Element field = document.createElement(entry.getKey());
					field.setTextContent(entry.getValue());
					root.appendChild(field);
				}

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
				DOMSource domSource = new DOMSource(document);
				StreamResult streamResult = new StreamResult(new File(CONFIG_FILE_PATH));
				transformer.transform(domSource, streamResult);
			} catch (ParserConfigurationException | TransformerException e) {
				Logger.getGlobal().log(Level.SEVERE, "Couldn't save config", e);
				return false;
			}

			Logger.getGlobal().info("Config saved.");
		}

		return true;
	}

	private boolean checkConfigExists() {
		return new File(CONFIG_FILE_PATH).exists();
	}

	private boolean exportDefaultConfig() {
		try {
			ClassLoader classLoader = Main.class.getClassLoader();
			InputStream is = classLoader.getResourceAsStream(CONFIG_FILE_PATH);
			Files.copy(is, Paths.get(CONFIG_FILE_PATH));
		} catch (IOException e) {
			Logger.getGlobal().log(Level.SEVERE, "Couldn't export default config", e);
			return false;
		}
		return true;
	}

	public static final Config getInstance() {
		if(instance == null) 
			instance = new Config();
		return instance;
	}
}
