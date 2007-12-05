package plugins.echo.editor;

import plugins.echo.Project;
import plugins.echo.ProjectManager;
import plugins.echo.SiteGenerator;
import plugins.echo.SimpleDirectoryInserter;
import freenet.keys.FreenetURI;
import freenet.support.api.HTTPRequest;
import freenet.node.fcp.FCPServer;

import java.io.File;
import java.net.MalformedURLException;

import nu.xom.Element;
import nu.xom.Attribute;

public class InsertPage extends Page {

	public static final int KEY_INPUT_SIZE = 70;
	public static final int MAX_KEY_LENGTH = 1024*1024;

	private ProjectManager projectManager;
	private Project project;
	private FCPServer fcpServer;
	private String formPassword;
	private	FreenetURI insertURI;
	private FreenetURI requestURI;
 
	public InsertPage(ProjectManager projectManager, FCPServer server, String formPassword){
	
		super("Insert");
		this.formPassword = formPassword;
		this.projectManager = projectManager;
		this.fcpServer = server;
	}

	public void handleHTTPRequest(HTTPRequest request, boolean isPost) {

		clear();
		project = projectManager.getCurrentProject();

		if (request.isPartSet("insert-key")) {
				
// 				if(clientPutDir == null || clientPutDir.hasFinished()) {
					insertURI = null;
					requestURI = null;
					
					try {
						insertURI = new FreenetURI(request.getPartAsString("insert-key", MAX_KEY_LENGTH));
					} catch(MalformedURLException mue) {
						appendError("Invalid insertion key : " + mue.getMessage());
					}
					
					try {
						requestURI = new FreenetURI(request.getPartAsString("request-key", MAX_KEY_LENGTH));
					} catch(MalformedURLException mue) {
						appendError("Invalid request key : " + mue.getMessage());
					}
					
					if(insertURI != null && requestURI != null) {
					
						
						if (! project.getInsertURI().equals(insertURI)){
							project.setInsertURI(insertURI);
							
						}
						
						if (! project.getRequestURI().equals(requestURI)) {
							project.setRequestURI(requestURI);
							
						}

						try {

							SiteGenerator generator = new SiteGenerator(project);
							generator.generate();

							SimpleDirectoryInserter inserter = new SimpleDirectoryInserter(fcpServer);
							inserter.insert(new File(project.getProjectDir(), "out"), "index.html", insertURI);
							
							appendContent(HTMLHelper.link("/queue/", "Go to the queue page."));
							
						} catch (Exception e) {
							appendError(e);
						}	
						

					} else 
						appendContent(insertForm());
// 				}
		} else {
			insertURI = project.getInsertURI();
			requestURI = project.getRequestURI();
			appendContent(insertForm());
		}
	}

	private Element insertForm() {

		Element form = HTMLHelper.form("", formPassword);

		HTMLHelper.label(form, "insert-key", "Insert key");
		Element insertKeyInput = HTMLHelper.input(form, "text", "insert-key");
		insertKeyInput.addAttribute(new Attribute("size", String.valueOf(KEY_INPUT_SIZE)));
		if(insertURI != null)
			insertKeyInput.addAttribute(new Attribute("value", insertURI.toString()));

		HTMLHelper.label(form, "request-key", "Request key");
		Element requestKeyInput = HTMLHelper.input(form, "text", "request-key");
		requestKeyInput.addAttribute(new Attribute("size", String.valueOf(KEY_INPUT_SIZE)));
		if(requestURI != null)
			requestKeyInput.addAttribute(new Attribute("value", requestURI.toString()));

		HTMLHelper.input(form, "submit", "submit");

		return form;
	}

}
