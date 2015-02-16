package jade.tree;

public interface Tree {

	TreeNode getRoot();
	Iterable<TreeNode> internalNodes(NodeOrder preorder);
	Iterable<TreeNode> externalNodes();
	int internalNodeCount();
	TreeBipartition getBipartition(TreeNode p);

}
