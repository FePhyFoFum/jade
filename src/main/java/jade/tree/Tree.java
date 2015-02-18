package jade.tree;

public interface Tree {

	TreeNode getRoot();
	Iterable<TreeNode> internalNodes(NodeOrder preorder);
	Iterable<TreeNode> externalNodes();
	int internalNodeCount();
	int externalNodeCount();
	TreeBipartition getBipartition(TreeNode p);
	Iterable<TreeBipartition> bipartitions();

}
