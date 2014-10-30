package jade.tree;

public class TreeParsingError extends Exception {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String message = "";
	
	public TreeParsingError (String message) {
        super();
        this.message = message;
    }
    
    @Override
    public String toString() {
    	return message;
    }
}
