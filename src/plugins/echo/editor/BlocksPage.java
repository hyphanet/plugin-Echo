package plugins.echo.editor;

import plugins.echo.block.Block;
import plugins.echo.block.BlockManager;
import plugins.echo.i18n.I18n;
import freenet.support.api.HTTPRequest;

import nu.xom.Element;
import nu.xom.Attribute;

import java.io.IOException;

public class BlocksPage extends Page {

	private BlockManager blockManager;
	private String formPsw;

	public BlocksPage(BlockManager blockManager, String formPassword) {

		super("Blocks");
		this.blockManager = blockManager;
		this.formPsw = formPassword;
	}

	public void handleHTTPRequest(HTTPRequest request, boolean isPost) {

		clear();

		if(request.isParameterSet("configure")){
			appendContent(HTMLHelper.element(null, "strong", "TODO"));
			return;
		}
		
		if (request.isPartSet("submit")) {

			String[] blocksIds = blockManager.getIds();
			for(String id : blocksIds) {
				Block block = blockManager.getBlockById(id);
				
				String position = request.getPartAsString("position-" + id, 8);
				block.setPosition(position);
				
				String weight = request.getPartAsString("weight-" + id, 2);
				block.setWeight(Integer.parseInt(weight));
				
				try {
					blockManager.write(block);
				} catch (IOException ioe) {
					appendError(ioe);
				}		
			}
		}

		Element form = HTMLHelper.form("", formPsw);
		Element table = new Element("table");
		form.appendChild(table);
		Element tHeader = new Element("tr");
		table.appendChild(tHeader);
		HTMLHelper.i18nElement(tHeader, "th", "echo.common.name");
		HTMLHelper.i18nElement(tHeader, "th", "echo.common.position");
		HTMLHelper.i18nElement(tHeader, "th", "echo.common.weight");
		HTMLHelper.i18nElement(tHeader, "th", "echo.common.action");

		Block[] blocks = blockManager.getBlocks();
		boolean alternate = false;
		for(Block b : blocks) {
			Element row = new Element("tr");
			if(alternate)
				row.addAttribute(new Attribute("class", "alternate"));
			
			HTMLHelper.element(row, "td", I18n.getString("echo.block." + b.getType().toString()));
	
			Element positionSelect = new Element("select");
			positionSelect.addAttribute(new Attribute("name", "position-" + b.getId()));
			HTMLHelper.option(positionSelect, "left", "left".equals(b.getPosition()));
			HTMLHelper.option(positionSelect, "right", "right".equals(b.getPosition()));
			HTMLHelper.option(positionSelect, "top", "top".equals(b.getPosition()));
			HTMLHelper.option(positionSelect, "bottom", "bottom".equals(b.getPosition()));
			HTMLHelper.option(positionSelect, "disabled", "disabled".equals(b.getPosition()));
			HTMLHelper.element(row, "td", positionSelect);			

			Element weightSelect = new Element("select");
			weightSelect.addAttribute(new Attribute("name", "weight-" + b.getId()));
			for(int i=0; i <= 5; i++) 
				HTMLHelper.option(weightSelect, String.valueOf(i), b.getWeight() == i);
				
			HTMLHelper.element(row, "td", weightSelect);

			Element configureCell = HTMLHelper.element(row, "td", "");
			if(b.isConfigurable())
				HTMLHelper.link(configureCell, "?configure=" + b.getId(), "configure");

			table.appendChild(row);
			alternate = !alternate;
		}
		HTMLHelper.input(form, "submit", "submit");

		appendContent(form);

	}

}