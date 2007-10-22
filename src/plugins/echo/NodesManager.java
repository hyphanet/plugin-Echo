package plugins.echo;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Attribute;
import nu.xom.Serializer;
import nu.xom.ParsingException;

import java.util.HashMap;
import java.util.Date;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

// TODO
// * nodesDirectory -> ..dir
// * Exceptions !!

/**
*	This class provides methods to manage nodes
*/
public class NodesManager {

	private Builder parser;
	private File nodesDir;
	private File categoriesFile;
	private HashMap<String, File> nodes;
	private HashMap<String, String> categories;

	/**
	*	Class constructor specifying the nodes directory to use
	*/
	public NodesManager(File nodesDirectory) throws IOException, ParsingException{

		nodesDir = nodesDirectory;
		categoriesFile = new File(nodesDir.getPath() + File.separator + "categories.xml");
				
		nodes = new HashMap<String, File> ();
		parser = new Builder();
		
		File[] files = nodesDir.listFiles();
		for(File f : files) {
			if(f.getName().matches("[0-9]{" + Echo.NODE_ID_LENGTH + "}.xml"))
				nodes.put(f.getName().substring(0,4), f);
		}

		categories = new HashMap<String, String> ();
		
		if(categoriesFile.exists()) {
			Document categoriesDoc = parser.build(categoriesFile);
			nu.xom.Nodes cats = categoriesDoc.query("//category");
			for(int i=0; i < cats.size(); i++) {
				categories.put(((Element) cats.get(i)).getAttribute("id").getValue() , ((Element) cats.get(i)).getValue());
			}
		} else 
			writeCategories();
	}

	/**
	*	Loads the node referenced by this id
	*	@return the node referenced by this id
	*/
	public Node getNodeById(String nodeId) throws IOException, ParsingException {

		File file = nodes.get(nodeId);
		if(file == null)
			return null;
		
		return new Node(parser.build(file));

	}

	/**
	*	Load all the nodes
	*	@return a list of all the nodes
	*/
	public Nodes getNodes() throws IOException, ParsingException {
	
		Nodes nodes = new Nodes();
		String[] ids = getIds();
		for(String id : ids)
			nodes.append(getNodeById(id));
		
		return nodes;

	}

	/**
	*	Returns the number of nodes referenced in this nodes manager
	*	@return the number of nodes
	*/
	public int size() {

		return nodes.size();

	} 

	/**
	*	Gets a list of all the post nodes
	*	@return a list of the post nodes
	*/
	public Nodes getPosts() throws IOException, ParsingException {
		
		Nodes posts = new Nodes();
		String[] ids = getIds();
		for(String id : ids) {
		
			Node node = getNodeById(id);
			if(node.getType() == Node.NodeType.POST_NODE)
				posts.append(node);
		}
		
		return posts;
	}
	
	// TODO + use Echo.
	public String getFreeNodeId() {

		String id = "";
		for(int i=1; i < 10000; i++) {
			id = String.valueOf(i);
			while(id.length() < 4)
				id = "0" + id;
			
			if(! nodeExists(id))
				return id;
		}

		return "9999";	// Suxx
	}

	/**
	*	Tests whether the node denoted by this id exists.
	*	@return true if and only if the node denoted by this id exists; false otherwise
	*/
	public boolean nodeExists(String nodeId) {
		
		return nodes.containsKey(nodeId);
		
	}

	/**
	*	Removes the node denoted by this id.
	*	@param the id of the node to remove
	*/
	public void deleteNode(String nodeId) throws IOException {

		File file = nodes.get(nodeId);
		if(!file.delete()) {
			throw new IOException(file.getPath() + " cannot be deleted");
		}

		nodes.remove(nodeId);
	}

	public void writeNode(Node node) throws FileNotFoundException, IOException{
		
		File file;
		String nodeId = node.getId();
		
		if(!nodeExists(nodeId)) {
			file = new File (nodesDir.getPath() + File.separator + nodeId + ".xml");
			nodes.put(nodeId, file);
		} else {
			file = nodes.get(nodeId);
		}

		node.setModifiedDate(new Date());

		FileWriter writer = new FileWriter(file);
		writer.write(node.getDoc().toXML());
		writer.close();
		
		/*
			Whitespaces gets stripped
		
		Serializer serializer = new Serializer(new FileOutputStream(file));
		serializer.setIndent(4);
		serializer.setMaxLength(128);
		serializer.write(node.getDoc());
		
		*/
	}

	public String[] getIds() {

		return nodes.keySet().toArray(new String[]{});
		
	}
	
	// Useless ??
	public Element getXMLNodesList() {

		String[] ids = this.getIds();
		Element nodesElement = new Element("nodes");
		
		for(String id : ids) {
			Element node = new Element("node");
			node.addAttribute(new Attribute("id", id));
			nodesElement.appendChild(node);
		}
		return nodesElement;
	}

	/**
	*	Tests whether the category denoted by this id exists.
	*	@param id The id of the category
	*	@return true if the category exists; false otherwise
	*/
	public boolean categoryExists(String id) {

		return categories.containsKey(id);
		
	}
	
	public int countCategories() {
	
		return categories.size();
	
	}
	
	public String getCategoryNameById(String id) {
		
		return categories.get(id);
	
	}
	
	public String[] getCategoriesIds() {

		return categories.keySet().toArray(new String[]{});
		
	}
	
	public void newCategory(String name) {
			
		String id = "";
		for(int i=1; i < 1000; i++) {
			id = String.valueOf(i);
			while(id.length() < 3)
				id = "0" + id;
			
			if(! categories.containsKey(id))
				break;
		}
		
		categories.put(id, name);
	}

	/**
	*	Renames a category
	*	@param id The id of the category
	*	@param newName The new name to set
	*	@return true if the category was successfully; false if the category 'id' does not exists.
	*
	*/
	public boolean renameCategory(String id, String newName) {

		if(! categories.containsKey(id))
			return false;

		categories.put(id, newName);
		return true;
			
	}

	public boolean deleteCategory(String id) throws IOException, ParsingException {

		if(! categories.containsKey(id))
			return false;

		
		String[] ids = getIds();
		for(String i : ids) {
			Node node = getNodeById(i);
			nu.xom.Nodes c = node.getDoc().query("/node/categories/category[@id = '" + id + "']");
			if(c.size() != 0) {
				c.get(0).detach();
				writeNode(node);
			}
		}

		categories.remove(id);
		return true;
	}

	// FIXME : crappy
	public void writeCategories() throws IOException {
		
			Element cats = new Element("categories");
			for(String c : categories.keySet()) {
				Element category = new Element("category");
				category.addAttribute(new Attribute("id", c));
				category.appendChild(categories.get(c));
				cats.appendChild(category);
			}
			
			Serializer serializer = new Serializer(new FileOutputStream(categoriesFile));
			serializer.setIndent(4);
			serializer.setMaxLength(128);
			serializer.write(new Document(cats));
		
	}
}
