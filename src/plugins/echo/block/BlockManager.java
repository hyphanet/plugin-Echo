package plugins.echo.block;

import plugins.echo.Project;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.io.IOException;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.Serializer;
import nu.xom.ParsingException;

public class BlockManager {

	private File blocksDir;
	private Builder parser;
	private HashMap<String,Block> blocks;
	
	public BlockManager (File blocksDir) throws ParsingException, IOException{
	
		this.blocksDir = blocksDir;
		this.parser = new Builder();
		this.blocks = new HashMap<String,Block>();
		
		File[] files = blocksDir.listFiles();
		for(File f : files) {
			if(f.getName().matches("[0-9]{" + Block.BLOCK_ID_LENGTH + "}.xml")) {
			
				Block block = null;
				Document doc = parser.build(f);
				String type = doc.query("/block/@type").get(0).getValue();
				switch (Block.BlockType.value(type)) {
					case BLOG_ROLL:
						block = new BlogrollBlock(doc);
						break;
					case CATEGORIES:
						block = new CategoriesBlock(doc);
						break;
				}
				
				if(block != null)
					blocks.put(block.getId(), block);
			}
		}
	}
	
	public Block getBlockById(String id) {
	
		return blocks.get(id);
	
	}
	
	public boolean blockExists(String id) {
	
		return blocks.containsKey(id);
	
	}
	
	public String[] getIds() {
	
		return blocks.keySet().toArray(new String[]{});
		
	}
	
	public Block[] getBlocks() {
		
		String[] ids = getIds();
		Block[] blocksTable = new Block[ids.length];
		for(int i=0; i < ids.length; i++)
			blocksTable[i] = getBlockById(ids[i]);
				
		return blocksTable;
	}
	
	public void write(Block block) throws IOException {
	
		String id = block.getId();
		
		if(!blocks.containsKey(id))
			blocks.put(id, block);
		
		Serializer serializer = new Serializer(new FileOutputStream(blocksDir.getPath() + File.separator + id + ".xml"));
		serializer.setIndent(4);
		serializer.setMaxLength(128);
		serializer.write(block.getDoc());
	
	}
	
	public void createDefaultBlocks() throws IOException {
	
		Block blogroll = new BlogrollBlock("001");
		write(blogroll);
		
		Block categories = new CategoriesBlock("002");
		categories.setPosition("right");
		write(categories);
	
	}

}