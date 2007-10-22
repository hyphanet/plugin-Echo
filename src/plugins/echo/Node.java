package plugins.echo;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Attribute;
import nu.xom.Text;
import nu.xom.Serializer;

import java.util.Date;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class Node {

	public enum NodeType { POST_NODE, STATIC_PAGE_NODE };
	public static final int CATEGORY_ID_LENGTH = 3;
	
	private String id;
	private NodeType type = null;
	private Date creationDate = null;
	private Document doc = null;
	private Element nodeElement = null;
	private Element modifiedElement = null;
	private Element categoriesElement = null;
	private Element titleElement = null;
	private Element contentElement = null;
	
	public Node (String nodeId, NodeType nodeType) {

		String type;
		switch(nodeType) {
			case POST_NODE :
				type = "post";
				break;
			case STATIC_PAGE_NODE :
				type = "page";
				break;
			default :
				throw new IllegalArgumentException();
		}
		
		this.id = nodeId;
		this.type = nodeType;
		
		nodeElement = new Element("node");
		nodeElement.addAttribute(new Attribute("id", id));
		nodeElement.addAttribute(new Attribute("type", type));
		
		creationDate = new Date();
		Element createdElement = new Element("created");
		createdElement.appendChild(dateToString(creationDate));
		nodeElement.appendChild(createdElement);
		
		modifiedElement = new Element("modified");
		nodeElement.appendChild(modifiedElement);
		
		categoriesElement = new Element("categories");
		nodeElement.appendChild(categoriesElement);

		titleElement = new Element("title");
		nodeElement.appendChild(titleElement);

		contentElement = new Element("content");
		nodeElement.appendChild(contentElement);

		doc = new Document(nodeElement);
		
	}

	protected Node (Document document) {

		this.doc = document;
		
	}

	public static String dateToString(Date date) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		String str = String.valueOf(calendar.get(Calendar.YEAR));
		str += "/" + calendar.get(Calendar.MONTH);
		str += "/" + calendar.get(Calendar.DAY_OF_MONTH);

		return str;
	}
	
	public static Date stringToDate(String str) {
	
		StringTokenizer tokenizer = new StringTokenizer(str, "/");
		if(tokenizer.countTokens() == 3) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, Integer.parseInt(tokenizer.nextToken()));
			calendar.set(Calendar.MONTH, Integer.parseInt(tokenizer.nextToken()));
			calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(tokenizer.nextToken()));
			
			return calendar.getTime();
		}
		
		return null;
	}
	
	private Element getModifiedElement() {
		if(modifiedElement == null)
			modifiedElement = (Element) doc.query("/node/modified").get(0);
		return modifiedElement;
	}

	protected void setModifiedDate(Date date) {
		getModifiedElement().removeChildren();
		modifiedElement.appendChild(dateToString(date));
	}

	public Date getCreationDate() {
		if(creationDate == null) 
			creationDate = stringToDate(((Element) doc.query("/node/modified").get(0)).getValue());
			
		return creationDate;
	}
	
	private Element getCategoriesElement() {
		if(categoriesElement == null)
			categoriesElement = (Element) doc.query("/node/categories").get(0);
		return categoriesElement;

	}

	public boolean isInCategory(String catId) {
		
		return (getCategoriesElement().query("category[@id='" + catId + "']").size() != 0);
	
	}
	
	public void setCategory(String catId, boolean value) {
	
		if(this.isInCategory(catId)) {
			if(!value) {
				getCategoriesElement().query("category[@id='" + catId + "']").get(0).detach();
			}	
			
		} else {
			if(value) {
				Element category = new Element("category");
				category.addAttribute(new Attribute("id", catId));
				getCategoriesElement().appendChild(category);
			}		
		}
	}
	
	public void setCategories(String[] Ids) {

		getCategoriesElement().removeChildren();
				
		for(String id : Ids) {
			Element category = new Element("category");
			category.addAttribute(new Attribute("id", id));
			categoriesElement.appendChild(category);
		}
	}

	private Element getTitleElement() {
		if(titleElement == null)
			titleElement = (Element) doc.query("/node/title").get(0);

		return titleElement;
	}
	
	public String getTitle() {

		return getTitleElement().getValue();
	}

	public void setTitle(String str){
		Element title = getTitleElement();
		title.removeChildren();
		title.appendChild(str);
	}

	private Element getContentElement() {
		if(contentElement == null)
			contentElement = (Element) doc.query("/node/content").get(0);

		return contentElement;
	}
	
	public String getBody() {
		return getContentElement().getValue().trim();
	}

	public void setBody(String str) {
		Element content = getContentElement();
		content.removeChildren();
		content.appendChild(new Text(str));
	}

	public String getId() {
		if(id == null)
			id = ((Attribute) doc.query("/node/@id").get(0)).getValue();
		return id;
	}
	
	public NodeType getType() {
		if(type == null) {
			String t = ((Attribute) doc.query("/node/@type").get(0)).getValue();
			if("post".equals(t))
				type = NodeType.POST_NODE;
			else
				type = NodeType.STATIC_PAGE_NODE;
		}
		
		return type;
	}
	
	protected Document getDoc() {
		return doc;
	}

	/**
	*	Returns a <strong>copy</strong> of the root element
	*	@retrun a copy of the root element
	*/
	public Element getRoot() {

		return (Element) getDoc().getRootElement().copy();

	}
	
	public Node summary() {
	
		WikiMarkupRender render = new TempWikiMarkupRender();
		Node summary = this.copy();
		Text content = (Text) summary.getContentElement().getChild(0);
		
		String str = content.getValue();
		int words = 0;
		int index = 1;
		boolean lastIsSpace = false;
		while(words < 80 && index < str.length()) {

			if(' ' == str.charAt(index++)) {
				if(!lastIsSpace)
					words++;
				lastIsSpace = true;
			} else
				lastIsSpace = false;
		}
		
		if(index != str.length()) {
			str = str.substring(0, index);
			str += "...";
		}
		
		content.setValue(str);
		render.render(summary.getContentElement());

		return summary;
		
	}
	
	public Node render() {
	
		Node renderedNode = this.copy();
		WikiMarkupRender render = new TempWikiMarkupRender();
		
		render.render(renderedNode.getContentElement());
		
		return renderedNode;
			
	}

	/**
	*	Returns a deep copy of this node
	*	@return a deep copy of this node
	*/
	public Node copy() {
	
		return new Node((Document) this.doc.copy());
	
	}
	
}
