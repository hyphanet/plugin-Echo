package plugins.echo.editor;

import plugins.echo.Project;
import plugins.echo.ProjectManager;
import plugins.echo.SiteGenerator;
import plugins.echo.SimpleDirectoryInserter;
import freenet.keys.FreenetURI;
import freenet.keys.InsertableClientSSK;
import freenet.keys.USK;
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
	private	InsertableClientSSK insertURI;
 
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
					
					try {
						insertURI = InsertableClientSSK.create(new FreenetURI(request.getPartAsString("insert-key", MAX_KEY_LENGTH)));
					} catch(MalformedURLException mue) {
						appendError("Invalid insertion key : " + mue.getMessage());
					}
					
					if(insertURI != null) {						
						try {
							SiteGenerator generator = new SiteGenerator(project);
							generator.generate();

							SimpleDirectoryInserter inserter = new SimpleDirectoryInserter(fcpServer);
							inserter.insert(new File(project.getProjectDir(), "out"), "index.html", insertURI.getInsertURI());
							
							insertURI = InsertableClientSSK.create(insertURI.getInsertURI().setSuggestedEdition(insertURI.getURI().getSuggestedEdition() + 1));
							project.setInsertURI(insertURI);
							
							appendContent(HTMLHelper.link("/queue/", "Go to the queue page."));
						} catch (Exception e) {
							appendError(e);
						}
					} else 
						appendContent(insertForm());
// 				}
		} else {
			insertURI = project.getInsertURI();
			appendContent(insertForm());
		}
	}

	private Element insertForm() {

		Element form = HTMLHelper.form("", formPassword);

		HTMLHelper.label(form, "insert-key", "Insert key");
		Element insertKeyInput = HTMLHelper.input(form, "text", "insert-key");
		insertKeyInput.addAttribute(new Attribute("size", String.valueOf(KEY_INPUT_SIZE)));
		insertKeyInput.addAttribute(new Attribute("value", insertURI.toString()));

		HTMLHelper.label(form, "request-key", "Request key");
		Element requestKeyInput = HTMLHelper.input(form, "text", "request-key");
		requestKeyInput.addAttribute(new Attribute("size", String.valueOf(KEY_INPUT_SIZE)));
		try {
			requestKeyInput.addAttribute(new Attribute("value", USK.create(insertURI.getURI()).toString()));
		} catch (MalformedURLException e) {}

		HTMLHelper.input(form, "submit", "submit");

		return form;
	}

}
