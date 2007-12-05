package plugins.echo.editor;

import freenet.crypt.RandomSource;
import plugins.echo.Project;
import plugins.echo.ProjectManager;
import plugins.echo.SiteGenerator;
import plugins.echo.SimpleDirectoryInserter;
import freenet.keys.FreenetURI;
import freenet.keys.USK;
import freenet.support.api.HTTPRequest;
import freenet.node.fcp.FCPServer;

import java.io.File;
import java.net.MalformedURLException;

import nu.xom.Element;
import nu.xom.Attribute;

public class InsertPage extends Page {

	public static final String DEFAULT_DOCUMENT_NAME = "index.html";
	public static final int KEY_INPUT_SIZE = 70;
	public static final int MAX_KEY_LENGTH = 1024*1024;

	private ProjectManager projectManager;
	private Project project;
	private FCPServer fcpServer;
	private String formPassword;
	private final RandomSource random;
 
	public InsertPage(ProjectManager projectManager, FCPServer server, String formPassword, RandomSource rand){
	
		super("Insert");
		this.formPassword = formPassword;
		this.projectManager = projectManager;
		this.fcpServer = server;
		this.random = rand;
	}

	public void handleHTTPRequest(HTTPRequest request, boolean isPost) {

		clear();
		project = projectManager.getCurrentProject();
		USK requestURI = null;
		try {
			FreenetURI tmp = project.getRequestURI();
			if(tmp == null) { // generate the keypair
				project.setInsertURI(Project.generateKeys(random, DEFAULT_DOCUMENT_NAME));
				tmp = project.getRequestURI();
			}
			requestURI = USK.create(tmp.setKeyType("USK"));
		} catch (MalformedURLException e) {
			appendError(e);
			return;
		}

		if (request.isPartSet("insert-it") && isPost) {					
			try {
				FreenetURI insertUri = project.getInsertURI();
				
				SiteGenerator generator = new SiteGenerator(project);
				generator.generate();

				SimpleDirectoryInserter inserter = new SimpleDirectoryInserter(fcpServer);
				inserter.insert(new File(project.getProjectDir(), "out"), DEFAULT_DOCUMENT_NAME, insertUri);

				project.incrementEditionNumber();
				appendContent(HTMLHelper.link("/queue/", "Go to the queue page."));
			} catch(Exception e) {
				appendError(e);
				return;
			}
// 				}
		} else {
			appendContent(insertForm(requestURI));
		}
	}

	private Element insertForm(USK uri) {

		Element form = HTMLHelper.form("", formPassword);

		HTMLHelper.label(form, "request-key", "Request key:");
		appendContent(HTMLHelper.link('/'+uri.getURI().toString(), uri.getURI().toString()));
		
		Element actionInput = HTMLHelper.input(form, "hidden", "insert-it");
		actionInput.addAttribute(new Attribute("name", "insert-it"));

		HTMLHelper.input(form, "submit", "submit");

		return form;
	}

}
