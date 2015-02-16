package jade.tree;

import java.util.List;

public interface TreeNode {

	TreeNode getParent();
	Object getLabel();
	boolean isExternal();
	boolean isInternal();
	boolean isTheRoot();
	Iterable<TreeNode> getDescendantLeaves();
	int getChildCount();
	TreeNode getChild(int i);
	List<TreeNode> getChildren();
	double getBL();
	String getNewick(boolean showBranchLengths);

}
