import org.graphstream.graph.*;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

public class SplayTree<T extends Comparable<T>> {

    private Node<T> root;
    private SingleGraph graph; // Declare graph variable

    private class Node<T> {
        T key;
        Node<T> left, right;

        Node(T key) {
            this.key = key;
            left = right = null;
        }
    }

    private Node<T> rightRotate(Node<T> x) {
        Node<T> y = x.left;
        x.left = y.right;
        y.right = x;
        return y;
    }

    private Node<T> leftRotate(Node<T> x) {
        Node<T> y = x.right;
        x.right = y.left;
        y.left = x;
        return y;
    }

    private Node<T> splay(Node<T> root, T key) {
        if (root == null || root.key.equals(key))
            return root;

        if (root.key.compareTo(key) > 0) {
            if (root.left == null) return root;

            if (root.left.key.compareTo(key) > 0) {
                root.left.left = splay(root.left.left, key);
                root = rightRotate(root);
            } else if (root.left.key.compareTo(key) < 0) {
                root.left.right = splay(root.left.right, key);
                if (root.left.right != null)
                    root.left = leftRotate(root.left);
            }

            return (root.left == null) ? root : rightRotate(root);
        } else {
            if (root.right == null) return root;

            if (root.right.key.compareTo(key) > 0) {
                root.right.left = splay(root.right.left, key);
                if (root.right.left != null)
                    root.right = rightRotate(root.right);
            } else if (root.right.key.compareTo(key) < 0) {
                root.right.right = splay(root.right.right, key);
                root = leftRotate(root);
            }

            return (root.right == null) ? root : leftRotate(root);
        }
    }

    public void insert(T key) {
        if (root == null) {
            root = new Node<>(key);
            return;
        }

        root = splay(root, key);

        int cmp = key.compareTo(root.key);

        if (cmp == 0) return;

        Node<T> newNode = new Node<>(key);

        if (cmp < 0) {
            newNode.right = root;
            newNode.left = root.left;
            root.left = null;
        } else {
            newNode.left = root;
            newNode.right = root.right;
            root.right = null;
        }

        root = newNode;
    }

    public void delete(T key) {
        if (root == null) return;

        root = splay(root, key);

        if (!root.key.equals(key)) return;

        if (root.left == null) {
            root = root.right;
        } else {
            Node<T> temp = root.right;
            root = root.left;
            splay(root, key);
            root.right = temp;
        }
    }

    public boolean search(T key) {
        root = splay(root, key);
        return root != null && root.key.equals(key);
    }

    public void printInOrder(Node<T> node) {
        if (node == null) return;
        printInOrder(node.left);
        System.out.print(node.key + " ");
        printInOrder(node.right);
    }

    public void printTree() {
        printInOrder(root);
        System.out.println();
    }

    private void initGraph() {
        graph = new SingleGraph("SplayTree");
        graph.setAttribute("ui.stylesheet",
                "node { size: 30px; fill-color: black; text-color: white; text-size: 15px; }");
    }

    // Method to visualize the SplayTree using GraphStream
    public void visualizeTree() {
        initGraph(); // Initialize the graph
        visualizeSubtree(root);
        Viewer viewer = graph.display();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
    }

    // Recursive method to add nodes and edges to the GraphStream graph
    private void visualizeSubtree(Node<T> node) {
        if (node != null) {
            String nodeId = node.key.toString(); // Get node value as String

            // Add node to graph only if it doesn't exist
            if (graph.getNode(nodeId) == null) {
                org.graphstream.graph.Node graphNode = graph.addNode(nodeId);
                graphNode.setAttribute("ui.label", nodeId); // Set label with node value
                String css = "size: 60px; shape: circle; fill-color: yellow; text-color: black; text-size: 40px;";
                graphNode.setAttribute("ui.style", css);
            }

            // Add edges to children
            if (node.left != null) {
                String leftId = node.left.key.toString();
                if (graph.getNode(leftId) == null) {
                    org.graphstream.graph.Node leftNode = graph.addNode(leftId);
                    leftNode.setAttribute("ui.label", leftId);
                    String css = "size: 60px; shape: circle; fill-color: blue; text-color: black; text-size: 40px;";
                    leftNode.setAttribute("ui.style", css);
                }
                graph.addEdge(nodeId + "-" + leftId, nodeId, leftId);
                visualizeSubtree(node.left);
            }
            if (node.right != null) {
                String rightId = node.right.key.toString();
                if (graph.getNode(rightId) == null) {
                    org.graphstream.graph.Node rightNode = graph.addNode(rightId);
                    rightNode.setAttribute("ui.label", rightId);
                    String css = "size: 60px; shape: circle; fill-color: red; text-color: black; text-size: 40px;";
                    rightNode.setAttribute("ui.style", css);
                }
                graph.addEdge(nodeId + "-" + rightId, nodeId, rightId);
                visualizeSubtree(node.right);
            }
        }
    }

    public static void main(String[] args) {
        // Example usage of the SplayTree from its main class

        // Create a SplayTree of integers
        SplayTree<Integer> splayTree = new SplayTree<>();
        System.setProperty("org.graphstream.ui", "swing");

        splayTree.insert(10);
        splayTree.insert(20);
        splayTree.insert(30);
        splayTree.insert(40);
        splayTree.insert(50);
        splayTree.insert(25);

        System.out.println("Inorder traversal of the modified tree:");
        splayTree.printTree();

        splayTree.search(20);
        System.out.println("After searching for 20:");
        splayTree.printTree();

        splayTree.delete(20);
        System.out.println("After deleting 20:");
        splayTree.printTree();

        // Visualize the SplayTree
        splayTree.visualizeTree();
    }
}
