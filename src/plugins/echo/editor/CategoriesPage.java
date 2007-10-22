package plugins.echo.editor;

import plugins.echo.NodesManager;
import freenet.support.api.HTTPRequest;

import java.io.IOException;

import nu.xom.Element;
import nu.xom.Attribute;
import nu.xom.ParsingException;

public class CategoriesPage extends Page {

	private static final int MAX_CATEGORY_NAME_LENGTH = 100;
	
	private NodesManager nodesManager;
	private String formPsw;
	
	public CategoriesPage(NodesManager nodesManager, String formPsw) {

		super("Categories");
		this.nodesManager = nodesManager;
		this.formPsw = formPsw;
		
		
	}

	public void handleHTTPRequest(HTTPRequest request) {
		
		clear();
		
		boolean rename= request.isParameterSet("rename");
				
		if(request.isPartSet("submit")) {
			String name = request.getPartAsString("category-name", MAX_CATEGORY_NAME_LENGTH).trim();
			String catId = request.getParam("rename");
			
			if(! "".equals(name)) {
				if(! nodesManager.renameCategory(catId, name))
					nodesManager.newCategory(name);
				try{
					nodesManager.writeCategories();
				} catch (IOException ioe) {
					appendError(ioe);
				}
				
				rename = false;
				
			} else {
				appendError("Fied \"name\" is empty");
			}
		}
		
		if(request.isParameterSet("delete")) {
			try {
				String cat = request.getParam("delete");
				if(!nodesManager.deleteCategory(cat))
					appendError("The category \"" + cat + "\" does not exist.");
			
				nodesManager.writeCategories();
			} catch (ParsingException pe) {
				appendError(pe);
			} catch (IOException ioe) {
				appendError(ioe);			
			}
		}
		
		if(nodesManager.countCategories() > 0 &&  ! rename) {
		
			Element table = new Element("table");
			Element tHeader = new Element("tr");
			table.appendChild(tHeader);
			HTMLHelper.i18nElement(tHeader, "th", "echo.common.name");
			Element actionCell = HTMLHelper.i18nElement(tHeader, "th", "echo.common.action");
			actionCell.addAttribute(new Attribute("colspan","2"));
			
			String[] ids = nodesManager.getCategoriesIds();
			boolean alternate = false;
			for(String id : ids) {
				Element row = new Element("tr");
				if(alternate)
					row.addAttribute(new Attribute("class", "alternate"));
				
				HTMLHelper.element(row, "td", nodesManager.getCategoryNameById(id));
				HTMLHelper.element(row, "td", HTMLHelper.i18nLink("?rename=" + id, "echo.common.rename"));
				HTMLHelper.element(row, "td", HTMLHelper.i18nLink("?delete=" + id, "echo.common.delete"));
				
				table.appendChild(row);
				alternate = !alternate;
			}
						
			appendContent(table);
		}
		
		String action = "categories";
		if (request.isParameterSet("rename"))
			action+= "?rename=" + request.getParam("rename");
		
		Element form = HTMLHelper.form(action, formPsw);
		form.addAttribute(new Attribute("class", "inline"));
		HTMLHelper.i18nLabel(form, "category-name", (rename) ? "echo.common.rename" : "echo.manage.newCategory");
		Element nameInput = HTMLHelper.input(form, "text", "category-name");
		if(rename)
			nameInput.addAttribute(new Attribute("value", nodesManager.getCategoryNameById(request.getParam("rename"))));
		
		HTMLHelper.input(form, "submit", "submit");
						
		appendContent(form);
	}
}