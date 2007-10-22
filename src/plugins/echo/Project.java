package plugins.echo;

import plugins.echo.block.BlockManager;

import freenet.keys.FreenetURI;

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

	private File projectDir;
	private File projectConfigFile;
	private Properties projectConfig;
	private NodesManager nodesManager;
	private BlockManager blockManager;
	
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
		
		return getURI("insertURI");
		
	}

	/**
	*	Returns the request URI of this project
	*	@return the request URI of this project
	*/
	public FreenetURI getRequestURI() {
		
		return getURI("requestURI");
	
	}

	private FreenetURI getURI(String key) {
		
		String str = projectConfig.getProperty(key);
		if(str == null)
			return null;
		
		try {
			return new FreenetURI(str);
			
		} catch (MalformedURLException mue) {
			return null;
		}
	}

	/**
	*	Registers the URI to insert this project
	*	@param uri the new insert URI
	*/
	public void setInsertURI(FreenetURI uri) {
	
		projectConfig.setProperty("insertURI", uri.toString());
	
	}
	
	/**
	*	Registers the URI to request the generated files of this project
	*	@param uri the new request URI
	*/
	public void setRequestURI(FreenetURI uri) {
	
		projectConfig.setProperty("requestURI", uri.toString());
	
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
	public void writeConfig() throws FileNotFoundException, IOException {
	
		FileOutputStream out = new FileOutputStream(projectConfigFile);
		projectConfig.storeToXML(out, null);
		out.close();
		
	}


}