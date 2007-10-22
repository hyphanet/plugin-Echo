package plugins.echo.block;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Attribute;

public abstract class Block {
	
	public enum BlockType { 
		RECENT_POSTS("recent-posts"), CATEGORIES("categories"), BLOG_ROLL("blog-roll");
		
		private String description;
		
		BlockType(String desc) {
		
			this.description = desc;
		
		}

		public String toString() {
		
			return description;
			
		}
		
		public static BlockType value(String str) {
			for(BlockType type : values()) {
				if(type.toString().equals(str))
					return type;
			}
			
			throw new IllegalArgumentException("Cannot parse into a BlockType element : '" + str + "'");
		}
		
	};	

	public static final int BLOCK_ID_LENGTH = 3;
	
	protected Element block;
	
	public Block(Document document) {
	
		this.block = document.getRootElement();
	
	}
	
	protected Block(String id, BlockType type) {
		
		block = new Element("block");
		block.addAttribute(new Attribute("id", id));
		block.addAttribute(new Attribute("type", type.toString()));
		block.addAttribute(new Attribute("position", "disabled"));
		block.addAttribute(new Attribute("weight", "5"));		
	
	}
	
	public abstract boolean isConfigurable();
	
	public abstract BlockType getType();
	
	public final String getId() {
	
		return block.getAttribute("id").getValue();
		
	}
	
	public String getPosition() {
	
		return block.getAttribute("position").getValue();
		
	}
	
	public void setPosition(String newPosition) {
	
		block.getAttribute("position").setValue(newPosition);
	
	}
	
	public int getWeight() {
	
		return Integer.parseInt(block.getAttribute("weight").getValue());
		
	}
	
	public void setWeight(int newWeight) {
	
		block.getAttribute("weight").setValue(String.valueOf(newWeight));
	
	}
	
	public Element toXMLElement() {
	
		return (Element) block.copy();
	
	}
	
	public Document getDoc() {
	
		return new Document((Element) block.copy());
	
	}
	
}