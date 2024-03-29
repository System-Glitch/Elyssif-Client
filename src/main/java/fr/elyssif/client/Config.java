/*
 * Elyssif-Client
 * Copyright (C) 2019 Jérémy LAMBERT (System-Glitch)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Singleton class for config storage
 * @author Jérémy LAMBERT
 *
 */
public final class Config {

	private static Config instance;
	private static final String CONFIG_FILE_NAME = "config.xml";
	private static final String PROGRAM_DIRECTORY_PATH = System.getProperty("user.home") + File.separator + ".elyssif";
	private static final String CONFIG_FILE_PATH = PROGRAM_DIRECTORY_PATH + File.separator + CONFIG_FILE_NAME;

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
			Logger.getGlobal().info("Config file not found, export default config to \"" + CONFIG_FILE_PATH + "\"");
			if(!exportDefaultConfig())
				return false;
			else
				Logger.getGlobal().info("Default config exported");
		}

		values = new Hashtable<>();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document xml = builder.parse(export ? new FileInputStream(new File(CONFIG_FILE_PATH)) : Main.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME));
			Element root = xml.getDocumentElement();

			Node node = root.getFirstChild();
			while(node != null) {
				if(node.getNodeName() != "#text" && node.getTextContent() != null && node.getTextContent().length() > 0) {
					if(countChildNodes(node) == 0) {
						values.put(node.getNodeName(), node.getTextContent());
					} else {
						Logger.getGlobal().warning("Config entry \"" + node.getNodeName() + "\" contains child node(s). This entry will be ignored.");
					}
				}
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
		Logger.getGlobal().info("Socket host: " + get("SocketHost"));
		Logger.getGlobal().info("Verbose: " + isVerbose());

		return true;
	}
	
	/**
	 * Count the amount of child nodes (except "#text") inside the given node.
	 * @param node
	 * @return count
	 */
	private int countChildNodes(Node node) {
		int count = 0;
		NodeList list = node.getChildNodes();
		Node child = null;
		
		for(int i = 0 ; i < list.getLength() ; i++) {
			child = list.item(i);
			if(child.getNodeName() != "#text")
				count++;
		}
		return count;
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
		setDefault("Verbose", "false");
		
		setVerbose(get("Verbose").equals("true"));
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
		if(value == null) values.remove(key);
		else values.put(key, value);
	}

	/**
	 * Save the current config into the config file.
	 * @return true on success
	 */
	public final boolean save() {

		if(isExport()) {
			if(!checkDirectory() || !checkPermissions()) return false;
			Logger.getGlobal().info("Saving config...");
			try {
				//Build XML
				DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
				Document document = documentBuilder.newDocument();

				Element root = document.createElement("Config");
				document.appendChild(root);

				values.put("Verbose", String.valueOf(isVerbose()));
				for(Entry<String, String> entry : values.entrySet()) {
					Element field = document.createElement(entry.getKey());
					field.setTextContent(entry.getValue());
					root.appendChild(field);
				}
				values.remove("Verbose");

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

	/**
	 * Check if the config file exists.
	 * @return true if the config file exists in the program's directory
	 */
	private boolean checkConfigExists() {
		return new File(CONFIG_FILE_PATH).exists();
	}

	/**
	 * Create program's directory if needed.
	 * @return boolean false on error
	 */
	private boolean checkDirectory() {
		File file = new File(PROGRAM_DIRECTORY_PATH);
		if(!file.exists() && !file.mkdir()) {
			Logger.getGlobal().log(Level.SEVERE, "Couldn't create directory: " + file.getAbsolutePath());
			return false;
		}
		return true;
	}

	/**
	 * Check if has read and write permission on exported config file.
	 * @return boolean true if has needed permissions
	 */
	private boolean checkPermissions() {
		File file = new File(CONFIG_FILE_PATH);
		boolean ok = true;

		if(!file.exists())
			file = new File(PROGRAM_DIRECTORY_PATH);

		if(!file.canRead()) {
			Logger.getGlobal().log(Level.SEVERE, "Missing read permission on config file: \"" + CONFIG_FILE_PATH + "\"");
			ok = false;
		}

		if(!file.canWrite()) {
			Logger.getGlobal().log(Level.SEVERE, "Missing write permission on config file: \"" + CONFIG_FILE_PATH + "\"");
			ok = false;
		}

		return ok;
	}

	private boolean exportDefaultConfig() {

		if(!checkDirectory() || !checkPermissions()) return false;

		try {
			ClassLoader classLoader = Main.class.getClassLoader();
			InputStream is = classLoader.getResourceAsStream(CONFIG_FILE_NAME);
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
