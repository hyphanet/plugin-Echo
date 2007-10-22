package plugins.echo.block;

import java.net.URL;

import freenet.keys.FreenetURI;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Attribute;

public class BlogrollBlock extends Block {

	protected BlogrollBlock(String id) {
		
		super(id, Block.BlockType.BLOG_ROLL);
		
	}
	
	public BlogrollBlock (Document document) {
	
		super(document);
	
	}
	
	public final boolean isConfigurable() {
	
		return true;
		
	}
	
	public final BlockType getType() {
	
		return Block.BlockType.BLOG_ROLL;
	
	}
	
	public void addLink(String name, URL url) {
	
		Element link = new Element("link");
		link.addAttribute(new Attribute("url", url.toExternalForm()));
		link.appendChild(name);
		block.appendChild(link);
	
	}
	
	public void addLink(String name, FreenetURI key) {
	
		Element link = new Element("link");
		link.addAttribute(new Attribute("key",key.toString()));
		link.appendChild(name);
		block.appendChild(link);
	}
	
}