package plugins.echo;

import plugins.echo.i18n.I18n;
import plugins.echo.block.BlockManager;
import plugins.echo.editor.*;


import freenet.pluginmanager.FredPlugin;
import freenet.pluginmanager.FredPluginHTTP;
import freenet.pluginmanager.FredPluginHTTPAdvanced;
import freenet.pluginmanager.FredPluginThreadless;
import freenet.pluginmanager.PluginRespirator;
import freenet.pluginmanager.PluginHTTPException;
import freenet.pluginmanager.DownloadPluginHTTPException;
import freenet.pluginmanager.RedirectPluginHTTPException;

import freenet.support.api.HTTPRequest;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.xslt.XSLTransform;


import java.util.HashMap;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
//	* Exceptions !
//	* var Project / projectManager


public class Echo implements FredPlugin, FredPluginHTTP, FredPluginHTTPAdvanced, FredPluginThreadless {
			
	public static final String BASE_URL = "/plugins/plugins.echo.Echo/";
	public static final File BASE_DIR = new File("plugins/Echo/");
	public static final int PROJECT_ID_LENGTH = 3;
	public static final int NODE_ID_LENGTH = 4;
	
	protected PluginRespirator respirator;
	private Builder parser;
	private XSLTransform transform;
	private I18n i18n;
	private HashMap<String,Page> pages;
	private ProjectManager projectManager;
	private Project project;
	private NodesManager nodesManager;
	private BlockManager blockManager;
		
	public void runPlugin(PluginRespirator p) {
		
		try {
			this.respirator = p;
			
			if(!BASE_DIR.exists())
				BASE_DIR.mkdirs();
			
			I18n.setLanguage("en");

			parser = new Builder();
			
			Document styleSheet = parser.build(Echo.class.getClassLoader().getResourceAsStream("/xml/edit.xsl"));

			I18n.translateXML(styleSheet);
			transform = new XSLTransform(styleSheet);

			projectManager = new ProjectManager(BASE_DIR, respirator.getNode().random);
			if(projectManager.countProjects() == 0)
				projectManager.newProject("My Flog");
			
			setProject(projectManager.loadProject("001"));
			String formPsw = respirator.getNode().clientCore.formPassword;
			
			
			pages = new HashMap<String,Page>();

			pages.put("plugins.echo.Echo", StaticPage.createFromContentFile("Welcome", "welcome.xml"));
			
			Page nodePage = new NodePage(nodesManager, formPsw);
			pages.put("newPost", nodePage);
			pages.put("newPage", nodePage);
			pages.put("edit", nodePage);
			
			pages.put("nodes", new NodesPage(project.getNodesManager()));
			pages.put("blocks", new BlocksPage(blockManager, formPsw));
			pages.put("categories", new CategoriesPage(nodesManager, formPsw));
			pages.put("generate", new GeneratePage(projectManager));
			pages.put("insert", new InsertPage(projectManager, respirator.getNode().clientCore.getFCPServer(), formPsw));
			
			pages.put("write", StaticPage.createFromContentFile(I18n.getString("echo.action.write"), "write.xml"));
			pages.put("manage", StaticPage.createFromContentFile(I18n.getString("echo.action.manage"), "manage.xml"));
			pages.put("publish",StaticPage.createFromContentFile(I18n.getString("echo.action.publish"), "publish.xml"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
// 		} catch (IOException ioe) {								//
// 			throw new PluginHTTPException("Cannot open the style sheet", "");		//
// 		} catch (ParsingException pe) {								//  Useless ?
// 			throw new PluginHTTPException("Cannot parse the style sheet", "");		//
// 		} catch (XSLException xe) {								//
// 			throw new PluginHTTPException("Cannot build the XSL transformer", "");		//
// 		}
		
	}

	public void terminate() {
		// TODO
	}

	private void setProject(Project p) {
	
		this.project = p;
		this.nodesManager = project.getNodesManager();
		this.blockManager = project.getBlockManager();
		transform.setParameter("baseDir", project.getProjectDir().getAbsolutePath() + "/");
	
	}
	
	public String handleHTTPGet(HTTPRequest request) throws PluginHTTPException {
		if ("/plugins/plugins.echo.Echo".equals(request.getPath()))
			throw new RedirectPluginHTTPException("", BASE_URL);
			
		String fileName = (new File(request.getPath())).getName();
		
		if ("edit.css".equals(fileName) || "echo-logo-small-0.1.png".equals(fileName)) {
			
			try {
				InputStream in = getClass().getResourceAsStream("/" + fileName);

				int read;
				int off = 0;
				byte[] buffer = new byte[in.available()];
				while((read = in.read(buffer, off, in.available())) != 0) {
					off+=read;
				}

				throw new DownloadPluginHTTPException(buffer, fileName, ("edit.css".equals(fileName)) ? "text/css" : "image/png");
			} catch (IOException ioe) {
				return ioe.getMessage();
			}
			
		}
		
		throw new PluginHTTPException("Unable to handle the request!", BASE_URL);
	}
	
	public String handleHTTPPut(HTTPRequest request) throws PluginHTTPException {
		return null;
	}
	
	public String handleHTTPPost(HTTPRequest request) throws PluginHTTPException {	
		try {
			String fileName = (new File(request.getPath())).getName();
			Page p;
			
			if(pages.containsKey(fileName))
				p = pages.get(fileName);
			else
				p = StaticPage.createFromContentFile("404 error", "http404error.xml");
			
			p.handleHTTPRequest(request);
			
			/*
				Nice but input white space are not respected
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Serializer serializer = new Serializer(baos);
			serializer.setIndent(4);
			serializer.setMaxLength(128);
			serializer.write(new Document((Element) transform.transform(new Document(p.toXML())).get(0)));
			return baos.toString();
			*/
			
			return transform.transform(new Document(p.toXML())).get(0).toXML();
			
		} catch (Exception e) {
			e.printStackTrace();
			return e.toString();
		}
	}
}