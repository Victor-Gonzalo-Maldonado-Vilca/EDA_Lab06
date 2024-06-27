import java.util.Comparator;

public class SplayTree<T> {
    private Comparator<T> comp;
    private int p_size;
    
    private class Node {
        Node left, right, parent;
        T key;
        
        Node(T init) {
            key = init;
        }
    }
    
    private Node root;
    
    public SplayTree(Comparator<T> comp) {
        this.comp = comp;
        root = null;
        p_size = 0;
    }
    
    public SplayTree() {
        this(Comparator.naturalOrder());
    }
    
    private void leftRotate(Node x) {
        Node y = x.right;
        if (y != null) {
            x.right = y.left;
            if (y.left != null) y.left.parent = x;
            y.parent = x.parent;
        }
        
        if (x.parent == null) root = y;
        else if (x == x.parent.left) x.parent.left = y;
        else x.parent.right = y;
        
        if (y != null) y.left = x;
        x.parent = y;
    }
    
    private void rightRotate(Node x) {
        Node y = x.left;
        if (y != null) {
            x.left = y.right;
            if (y.right != null) y.right.parent = x;
            y.parent = x.parent;
        }
        
        if (x.parent == null) root = y;
        else if (x == x.parent.left) x.parent.left = y;
        else x.parent.right = y;
        
        if (y != null) y.right = x;
        x.parent = y;
    }
    
    private void splay(Node x) {
        while (x.parent != null) {
            if (x.parent.parent == null) {
                if (x.parent.left == x) rightRotate(x.parent);
                else leftRotate(x.parent);
            } else if (x.parent.left == x && x.parent.parent.left == x.parent) {
                rightRotate(x.parent.parent);
                rightRotate(x.parent);
            } else if (x.parent.right == x && x.parent.parent.right == x.parent) {
                leftRotate(x.parent.parent);
                leftRotate(x.parent);
            } else if (x.parent.left == x && x.parent.parent.right == x.parent) {
                rightRotate(x.parent);
                leftRotate(x.parent);
            } else {
                leftRotate(x.parent);
                rightRotate(x.parent);
            }
        }
    }
    
    public void insert(T key) {
        Node z = root;
        Node p = null;
        
        while (z != null) {
            p = z;
            if (comp.compare(z.key, key) < 0) z = z.right;
            else z = z.left;
        }
        
        z = new Node(key);
        z.parent = p;
        
        if (p == null) root = z;
        else if (comp.compare(p.key, z.key) < 0) p.right = z;
        else p.left = z;
        
        splay(z);
        p_size++;
    }
    
    public Node find(T key) {
        Node z = root;
        while (z != null) {
            if (comp.compare(z.key, key) < 0) z = z.right;
            else if (comp.compare(z.key, key) > 0) z = z.left;
            else return z;
        }
        return null;
    }
    
    public void erase(T key) {
        Node z = find(key);
        if (z == null) return;
        
        splay(z);
        
        if (z.left == null) replace(z, z.right);
        else if (z.right == null) replace(z, z.left);
        else {
            Node y = subtreeMinimum(z.right);
            if (y.parent != z) {
                replace(y, y.right);
                y.right = z.right;
                y.right.parent = y;
            }
            replace(z, y);
            y.left = z.left;
            y.left.parent = y;
        }
        
        p_size--;
    }
    
    private void replace(Node u, Node v) {
        if (u.parent == null) root = v;
        else if (u == u.parent.left) u.parent.left = v;
        else u.parent.right = v;
        if (v != null) v.parent = u.parent;
    }
    
    private Node subtreeMinimum(Node u) {
        while (u.left != null) u = u.left;
        return u;
    }
    
    private Node subtreeMaximum(Node u) {
        while (u.right != null) u = u.right;
        return u;
    }
    
    public T minimum() {
        if (root == null) throw new IllegalStateException("Tree is empty");
        return subtreeMinimum(root).key;
    }
    
    public T maximum() {
        if (root == null) throw new IllegalStateException("Tree is empty");
        return subtreeMaximum(root).key;
    }
    
    public boolean isEmpty() {
        return root == null;
    }
    
    public int size() {
        return p_size;
    }
}
