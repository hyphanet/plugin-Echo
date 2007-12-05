package plugins.echo.editor;

import plugins.echo.Node;
import plugins.echo.Nodes;
import plugins.echo.NodesManager;
import freenet.support.api.HTTPRequest;

import nu.xom.Element;
import nu.xom.Attribute;

import java.io.IOException;

public class NodesPage extends Page {

	private NodesManager nodesManager;

	public NodesPage(NodesManager nodesManager){
	
		super("My Nodes");
		this.nodesManager = nodesManager;

	}

	public void handleHTTPRequest(HTTPRequest req, boolean isPost) {

		clear();
		
		if(req.isParameterSet("delete")) {
			String id = req.getParam("delete");
			if(nodesManager.nodeExists(id))
				try {
					nodesManager.deleteNode(id);
				} catch(IOException ioe) {
					appendError("Unable to delete the node " + id + " : " + ioe.getMessage());
				}
			else
				appendError("The node " + id + "does not exist");
		}
		
		if(nodesManager.size() == 0) {
			appendContent("You don't have any node, ");
			appendContent(HTMLHelper.link("write","create a new one"));
		} else {
			
			try {
				Nodes nodes = nodesManager.getNodes();
				Element table = new Element("table");
				Element tHeader = new Element("tr");
				table.appendChild(tHeader);
				HTMLHelper.element(tHeader, "th", "Id");
				HTMLHelper.i18nElement(tHeader, "th", "echo.common.date");
				HTMLHelper.i18nElement(tHeader, "th", "echo.common.title");
				HTMLHelper.i18nElement(tHeader, "th", "echo.common.nodeType");
				Element action = HTMLHelper.i18nElement(tHeader, "th", "echo.common.action");
				action.addAttribute(new Attribute("colspan", "2"));
				
				boolean altern = false;
				for(Node n : nodes) {
	
					Element row = new Element("tr");
					if(altern)
						row.addAttribute(new Attribute("class", "alternate"));

					HTMLHelper.element(row, "td", n.getId());
					HTMLHelper.element(row, "td", n.getCreationDate().toString());
					HTMLHelper.element(row, "td", n.getTitle());
					HTMLHelper.element(row, "td", "type");	// TODO
					HTMLHelper.element(row, "td", HTMLHelper.i18nLink("edit?node=" + n.getId(), "echo.common.edit"));
					HTMLHelper.element(row, "td", HTMLHelper.i18nLink("?delete=" + n.getId(), "echo.common.delete"));

					table.appendChild(row);
					altern = !altern;
	
				}

				appendContent(table);

			} catch (Exception e) {
				appendError(e);
			}
		}

	}
}
