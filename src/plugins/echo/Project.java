package plugins.echo;

import freenet.crypt.RandomSource;
import plugins.echo.block.BlockManager;

import freenet.keys.FreenetURI;

import freenet.keys.InsertableClientSSK;
import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import nu.xom.ParsingException;

/**
*	This class represents a project and provides basics methods to manage it.
*/
public class Project {

	private final File projectDir;
	private final File projectConfigFile;
	private final Properties projectConfig;
	private NodesManager nodesManager;
	private BlockManager blockManager;
	private InsertableClientSSK keys = null;
	private long edition = 1;
	
	/**
	* Class constructor specifying the base dir of the project.
	*/
	public Project(File projectDir) throws FileNotFoundException, ParsingException, IOException {
		
		this.projectDir = projectDir;
		this.projectConfigFile = new File(projectDir, "conf.xml");
		this.projectConfig = new Properties();
		projectConfig.loadFromXML(new FileInputStream(projectConfigFile));
		
		nodesManager = new NodesManager(new File(projectDir.getPath() + File.separator + "nodes"));
		blockManager = new BlockManager(new File(projectDir.getPath() + File.separator + "blocks"));
	}
	
	public Project(File baseDir, String projectTitle, String id) {
		this.projectDir = new File(baseDir.getPath() + File.separator + id);
		this.projectConfigFile = new File(projectDir.getPath() + File.separator + "conf.xml");
		this.projectConfig = new Properties();
		
		projectDir.mkdirs();
		(new File(projectDir.getPath() + File.separator + "nodes")).mkdirs();
		(new File(projectDir.getPath() + File.separator + "blocks")).mkdirs();

		projectConfig.setProperty("title", projectTitle);
		try {
			writeConfig();
			this.nodesManager = new NodesManager(new File(projectDir.getPath() + File.separator + "nodes"));
			this.blockManager = new BlockManager(new File(projectDir.getPath() + File.separator + "blocks"));
		} catch(ParsingException e) {
		} catch(FileNotFoundException e) {
		} catch(IOException e) {
			System.err.println("ECHO: Error writing the config. file!!" + e.getMessage());
		}
	}
				
	/**
	*	Returns the project base dir
	*	@return the project base dir
	*/
	public File getProjectDir() {
	
		return projectDir;
	
	}
	
	/**
	*	Returns the title of this project
	*	@return the title of this project
	*/
	public String getTitle() {
	
		return projectConfig.getProperty("title");
	
	}
	
	/**
	*	Returns the insert URI of this project
	*	@return the insert URI of this project
	*/
	public FreenetURI getInsertURI() {
		InsertableClientSSK key = getKeys();
		return (key == null ? null : key.getInsertURI());
		
	}

	/**
	*	Returns the request URI of this project
	*	@return the request URI of this project
	*/
	public FreenetURI getRequestURI() {
		InsertableClientSSK key = getKeys();
		return (key == null ? null : key.getURI().setSuggestedEdition(edition));
	
	}
	
	public static InsertableClientSSK generateKeys(RandomSource rand, String docName) {
		return InsertableClientSSK.createRandom(rand, docName);
	}

	private InsertableClientSSK getKeys() {
		if(keys == null) {
			String str = projectConfig.getProperty("insertURI");
			String ed = projectConfig.getProperty("edition");
			if(str == null || ed == null) {
				return null;
			}
			try {
				keys = InsertableClientSSK.create(new FreenetURI(str));
				edition = Long.parseLong(ed);
			} catch(MalformedURLException mue) {
				return null;
			}
		}
		return keys;
	}

	/**
	*	Registers the URI to insert this project
	*	@param uri the new insert URI
	*/
	public InsertableClientSSK setInsertURI(InsertableClientSSK uri) {
		projectConfig.setProperty("insertURI", uri.toString());
		projectConfig.setProperty("edition", "1");
		writeConfig();
		
		return keys = uri;
	}
	
	public void incrementEditionNumber() {
		projectConfig.setProperty("insertURI", keys.toString());
		projectConfig.setProperty("edition", String.valueOf(edition));
		
		writeConfig();
	}
	
	/**
	*	Returns a NodesManager instance for this project
	*	@return a NodesManager instance for this project
	*/
	public NodesManager getNodesManager() {
	
		return nodesManager;
	
	}

	
	/**
	*	Returns a BlockManager instance for this project
	*	@return a BlockManager instance for this project
	*/
	public BlockManager getBlockManager() {
	
		return blockManager;
	
	}

	/**
	*	Stores the project config into the file conf.xml
	*/
	public void writeConfig(){
		try {
			FileOutputStream out = new FileOutputStream(projectConfigFile);
			projectConfig.storeToXML(out, null);
			out.close();
		} catch (FileNotFoundException e){
		} catch (IOException ioe) {
		}
	}


}