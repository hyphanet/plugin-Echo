package plugins.echo;

import plugins.echo.block.Block;
import plugins.echo.block.BlockManager;

import nu.xom.*; 
import nu.xom.xslt.*;

import java.io.*;
import plugins.echo.editor.InsertPage;

public class SiteGenerator {
	
	public static final int POSTS_PER_PAGE = 5;
	
	private NodesManager nodesManager;
	private BlockManager blockManager;
	private Builder parser;
	private XSLTransform transform;
	private XSLTransform rssTransform;
	private Serializer serializer;
	private Document template;
	private Project project;
	private File outDir;
	private Element blocksElement;
	
	public SiteGenerator(Project project) throws IOException, ParsingException, XSLException {
	
		this.project = project;
		this.outDir = new File(project.getProjectDir(), "out/");
		nodesManager = project.getNodesManager();
		blockManager = project.getBlockManager();
		parser = new Builder();
		
		template = parser.build(getClass().getResourceAsStream("/xml/test.xsl"));

		transform = new XSLTransform(template);
		transform.setParameter("basedir", project.getProjectDir().getAbsolutePath() + "/");
		transform.setParameter("project-title", project.getTitle());


		
		serializer = new Serializer(System.out); 
		serializer.setIndent(4);
		serializer.setMaxLength(128);
		
		blocksElement = new Element("blocks");
		Block[] blocks = blockManager.getBlocks();
		
		for(Block b : blocks) {
			if(! b.getPosition().equals("disabled"))
				blocksElement.appendChild(b.toXMLElement());
		}
	}
	
	private void makePage(Element e, String fileName) throws XSLException, IOException{
	
		Element page = new Element("page");
		blocksElement.detach();
		page.appendChild(blocksElement);
		page.appendChild(e);
		
		nu.xom.Nodes result = transform.transform(new Document(page));
		serializer.setOutputStream(new FileOutputStream(new File(outDir.getPath(), fileName)));
		serializer.write(new Document((Element) result.get(0)));
		
	} 
	
	public void generate() throws Exception{	// TODO : Pfouille !!

		outDir.mkdirs();
		
		Nodes nodes = nodesManager.getNodes();
		for(Node node : nodes) {
			makePage(node.render().getRoot(), node.getId() + ".html");
		}
		
		Nodes posts = nodes.getPosts();
		posts.sortByCreationDate();
		
		Element index = new Element("index");
		for(Node post : posts) {
			index.appendChild(post.summary().getRoot());
		}
		makePage(index, InsertPage.DEFAULT_DOCUMENT_NAME);
		
// 		writeToFile(rssTransform.transform(new Document(index)), "feed.rss");
				
		String[] categories = nodesManager.getCategoriesIds();
		for(String category : categories) {
			index = new Element("index");
			index.addAttribute(new Attribute("category", category));
			for(Node post : posts) {
				if(post.isInCategory(category))
					index.appendChild(post.summary().getRoot());
			}
			makePage(index, "category-" + category + ".html");
		}
		
		
		InputStream in = getClass().getResourceAsStream("/style.css");
		FileOutputStream cssFile = new FileOutputStream(outDir.getPath() + File.separator + "style.css");
		
		byte[] buffer = new byte[1024];
		int read;
		while((read = in.read(buffer)) != -1) {
			cssFile.write(buffer, 0, read);
		}
		
		in.close();
		cssFile.close();
		
	}
	
	public File getOutDir() {
		
		return outDir;
	
	}
}