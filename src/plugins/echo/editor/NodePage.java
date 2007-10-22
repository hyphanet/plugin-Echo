package plugins.echo.editor;

import plugins.echo.Node;
import plugins.echo.NodesManager;
import freenet.support.api.HTTPRequest;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

import nu.xom.Element;
import nu.xom.Attribute;
import nu.xom.ParsingException;

public class NodePage extends Page {
	
	
	private static final int MAX_TITLE_LENGTH = 200;
	private static final int MAX_BODY_LENGTH = 100000;
	
	private NodesManager nodesManager;
	private final String formPsw;
	
	
	public NodePage(NodesManager nodesManager, String formPassword) {

		super("Node");
		this.nodesManager = nodesManager;
		this.formPsw = formPassword;

	}

	public void handleHTTPRequest(HTTPRequest request) {

		clear();
		Node node = null;		
		
		String fileName = (new File(request.getPath())).getName();
		
		if("edit".equals(fileName)) {

			String nodeId = request.getParam("node");
			
			setTitle("Edit");
			try {

				node = nodesManager.getNodeById(nodeId);
				if(node == null) {
					appendError("The node " + nodeId + "does not exist");
					return;
				}

			} catch (IOException ioe) {
				appendError(ioe);
				return;
			} catch (ParsingException pe) {
				appendError("The node " + nodeId  + " is damaged : " + pe.getMessage());
				return;
			}
				
		} else if(fileName.equals("newPost")) {
			setTitle("New Post");
			node = new Node(nodesManager.getFreeNodeId(), Node.NodeType.POST_NODE);
			
		} else if(fileName.equals("newPage")) {
			setTitle("New Page");
			node = new Node(nodesManager.getFreeNodeId(), Node.NodeType.STATIC_PAGE_NODE);
		}

		if(request.isPartSet("edit-title") && request.isPartSet("body")) {
			String title = request.getPartAsString("edit-title", MAX_TITLE_LENGTH).trim();
			String body = request.getPartAsString("body", MAX_BODY_LENGTH).trim();
			
			if("".equals(title))
				appendError("Field \"title\" is empty");

			if("".equals(body)) 
				appendError("Field \"body\" is empty");

			node.setTitle(title);
			node.setBody(body);
			
			String[] cats = nodesManager.getCategoriesIds();
			for(String cat : cats)
				node.setCategory(cat, request.isPartSet("category-" + cat));
				
			if(countErrors() == 0) {
				
				try {

					nodesManager.writeNode(node);
					
				} catch (FileNotFoundException fnfe) {
					appendError(fnfe);
				} catch (IOException ioe) {
					appendError("Cannot write node " + node.getId() + " : " + ioe.getMessage());
				} catch (Exception e) {
					appendError(e);
				}
				
				appendContent("Saved");
				return;
			}

		}
		
		Element form = HTMLHelper.form("", formPsw);

		HTMLHelper.i18nLabel(form, "edit-title", "echo.common.title");
		Element titleInput = HTMLHelper.input(form, "text", "edit-title");
		titleInput.addAttribute(new Attribute("size", "100"));
		titleInput.addAttribute(new Attribute("value", node.getTitle()));

		HTMLHelper.i18nLabel(form, "edit-body", "echo.write.nodeBody");

		Element textarea = new Element("textarea");
		textarea.addAttribute(new Attribute("id", "edit-body"));
		textarea.addAttribute(new Attribute("name", "body"));
		textarea.addAttribute(new Attribute("cols", "100"));
		textarea.addAttribute(new Attribute("rows", "50"));
		textarea.appendChild((node.getBody().equals("")) ? " " : node.getBody());
		form.appendChild(textarea);

		if(nodesManager.countCategories() != 0) {

			Element fieldset = new Element("fieldset");
			form.appendChild(fieldset);
			HTMLHelper.i18nElement(fieldset, "legend", "echo.common.categories");

			String[] ids = nodesManager.getCategoriesIds();
			for(String id : ids) {
				Element checkbox = HTMLHelper.input(fieldset, "checkbox", "category-" + id);
				checkbox.addAttribute(new Attribute("class", "inline"));

				if(node.isInCategory(id))
					checkbox.addAttribute(new Attribute("checked", "checked"));

				HTMLHelper.label(fieldset, "category-" + id, nodesManager.getCategoryNameById(id));
			}

		}

		HTMLHelper.input(form, "submit", "submit");

		appendContent(form);
		
	}



}
