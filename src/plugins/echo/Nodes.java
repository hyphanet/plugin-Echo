package plugins.echo;

import java.util.Vector;
import java.util.Comparator;
import java.util.Collections;
import java.util.Iterator;

/**
* 	A simple list of nodes
*/
public class Nodes implements Iterable<Node> {

	private Vector<Node> nodes;
	
	/** 
    	* Class constructor.
	*/
	public Nodes() {
		
		nodes = new Vector<Node>();
		
	}
	
	public class CreationDateComparator implements Comparator {
	
		public int compare(Object node1, Object node2) {
			if(! (node1 instanceof Node && node2 instanceof Node))
				throw new ClassCastException();
			
			return ((Node) node1).getCreationDate().compareTo(((Node) node2).getCreationDate());
		}
	}
	
	/**
	*	Adds a node at the end of the list.
	*	@param node the node to add to the list
	*/
	public void append(Node node) {
	
		nodes.add(node);
		
	}

	/**
	*	Returns the index<sup>th</sup> node of the list.
	*	@param index the index of the node to return
	*	@return the node at the specified position 
	*/
	public Node get(int index) {
	
		return nodes.get(index);
		
	}

	/**
	*	Remove the index<sup>th</sup> node of the list
	*	@param index the index of the node to remove
	*	@return	the index<sup>th</sup> node of the list, that was removed
	*/
	public Node remove(int index) {
	
		return nodes.remove(index);
	
	}
	
	/**
	*	Returns all the post nodes contained in this list
	*	@return the posts nodes contained in this list
	*/
	public Nodes getPosts() {
	
		Nodes posts = new Nodes();
		for(Node n : this) {
			if(n.getType() == Node.NodeType.POST_NODE)
				posts.append(n);
		}
		
		return posts;
	}
	
	/**
	*	Returns the number of nodes in this list.
	*	@return the number of nodes in this list
	*/
	public int size() {
	
		return nodes.size();
		
	}
	
	/**
	*	Sorts the list by creation date of the nodes using the CreationDateComparator.
	*/
	public void sortByCreationDate() {
	
		Collections.sort(nodes, new CreationDateComparator());

	}
	/**
	*	Returns a iterator of the elements in this list.
	*	@return a iterator of the elements in this list.
	*/
	public Iterator<Node> iterator() {
			
		return nodes.iterator();
		
	}

}