package plugins.echo;

import net.sf.textile4j.Textile;
import nu.xom.Builder;
import nu.xom.Node;
import nu.xom.Element;
import nu.xom.Text;

/**
*	This is an horrible temporary class for rendering wiki markup util WikiMarkupRender is finished.
*/
public class TempWikiMarkupRender extends WikiMarkupRender {

	private Textile textile;
	
	public TempWikiMarkupRender() {

		this.textile = new Textile();
		
	}

	public void render(Element xml) {

		try {
			Builder parser = new Builder();
			
			Text originalContent = (Text) xml.getChild(0);
	
			Element renderedContent = parser.build("<rendered>" +  textile.process(originalContent.getValue()) + "</rendered>", null).getRootElement();
			
			while (renderedContent.getChildCount() != 0) {
				nu.xom.Node node = renderedContent.getChild(0);
				node.detach();
				xml.appendChild(node);
			}
			
			originalContent.detach();
			
		} catch (Exception e) {		//Ugly
			e.printStackTrace();
		}		
	}

}