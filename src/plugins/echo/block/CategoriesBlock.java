package plugins.echo.block;

import nu.xom.Document;


public class CategoriesBlock extends Block {
	
	protected CategoriesBlock(String id) {
		
		super(id, Block.BlockType.CATEGORIES);
		
	}
	
	public CategoriesBlock(Document document) {
		
		super(document);
	
	}
	
	public final boolean isConfigurable() {
	
		return false;
		
	}
	
	public final BlockType getType() {
	
		return Block.BlockType.CATEGORIES;
	
	}
	
}