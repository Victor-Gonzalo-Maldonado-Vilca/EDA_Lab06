import java.util.Random;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

public class SkipList {
    private static final int MAX_LEVEL = 6; // Número máximo de niveles en la Skip List
    private Node head; // Nodo de inicio de la Skip List
    private int level; // Número actual de niveles de la Skip List
    private Random random; // Generador de números aleatorios para determinar niveles
    private Graph graph; // GraphStream graph

    public SkipList() {
        this.head = new Node(Integer.MIN_VALUE, MAX_LEVEL);
        this.level = 0;
        this.random = new Random();
        this.graph = new SingleGraph("SkipList");
        this.graph.setStrict(false);
        this.graph.setAutoCreate(true);

    }

    // Clase interna que representa un nodo en la Skip List
    private class Node {
        int value;
        Node[] forward; // Arreglo de referencias hacia adelante en cada nivel

        Node(int value, int level) {
            this.value = value;
            this.forward = new Node[level + 1];
        }
    }

    // Método para insertar un valor en la Skip List
    public void insert(int value) {
        // Crear un nuevo nodo
        Node newNode = new Node(value, randomLevel());

        // Actualizar los punteros forward en cada nivel
        Node current = head;
        for (int i = level; i >= 0; i--) {
            while (current.forward[i] != null && current.forward[i].value < value) {
                current = current.forward[i];
            }
            if (i <= newNode.forward.length - 1) {
                newNode.forward[i] = current.forward[i];
                current.forward[i] = newNode;
            }
        }

        // Actualizar el nivel máximo de la Skip List si es necesario
        if (newNode.forward.length - 1 > level) {
            level = newNode.forward.length - 1;
        }

        addGraphNode(newNode);
        addGraphEdges();
    }

    // Método para buscar un valor en la Skip List
    public boolean search(int value) {
        Node current = head;
        for (int i = level; i >= 0; i--) {
            while (current.forward[i] != null && current.forward[i].value < value) {
                current = current.forward[i];
            }
        }
        current = current.forward[0];
        return current != null && current.value == value;
    }

    // Método para eliminar un valor de la Skip List
    public void delete(int value) {
        Node[] update = new Node[level + 1];
        Node current = head;
        for (int i = level; i >= 0; i--) {
            while (current.forward[i] != null && current.forward[i].value < value) {
                current = current.forward[i];
            }
            update[i] = current;
        }
        current = current.forward[0];
        if (current != null && current.value == value) {
            for (int i = 0; i <= level; i++) {
                if (update[i].forward[i] != current)
                    break;
                update[i].forward[i] = current.forward[i];
            }
            while (level > 0 && head.forward[level] == null) {
                level--;
            }

            removeGraphNode(current);
            addGraphEdges();
        }
    }

    // Método para generar aleatoriamente el nivel de un nuevo nodo
    private int randomLevel() {
        int lvl = 0;
        while (lvl < MAX_LEVEL && random.nextDouble() < 0.5) {
            lvl++;
        }
        return lvl;
    }

    // Método para agregar un nodo al gráfico con estilos
    private void addGraphNode(Node node) {
        String nodeId = String.valueOf(node.value);
        org.graphstream.graph.Node graphNode = graph.addNode(nodeId);
        graphNode.setAttribute("ui.label", String.valueOf(node.value));
        String css = "node { size: 60px; shape: circle; fill-color: yellow; text-color: black; text-size: 40px; } ";
        this.graph.setAttribute("ui.stylesheet", css);
    }

    // Método para remover un nodo del gráfico
    private void removeGraphNode(Node node) {
        String nodeId = String.valueOf(node.value);
        if (graph.getNode(nodeId) != null) {
            graph.removeNode(nodeId);
        }
    }

    // Método para agregar los bordes del gráfico
    private void addGraphEdges() {
        graph.clear();
        for (int i = 0; i <= level; i++) {
            Node current = head.forward[i];
            while (current != null) {
                addGraphNode(current);
                String currentId = String.valueOf(current.value);
                String nextId = current.forward[i] == null ? null : String.valueOf(current.forward[i].value);
                if (nextId != null) {
                    graph.addEdge(currentId + "-" + nextId, currentId, nextId, true);
                }
                current = current.forward[i];
            }
        }
    }

    // Método para imprimir la Skip List (para propósitos de prueba y depuración)
    public void print() {
        for (int i = level; i >= 0; i--) {
            Node current = head.forward[i];
            System.out.print("Level " + i + ": ");
            while (current != null) {
                System.out.print(current.value + " ");
                current = current.forward[i];
            }
            System.out.println();
        }
    }

    // Método principal para probar la Skip List
    public static void main(String[] args) {
        // Configurar la propiedad del sistema para el paquete de UI de GraphStream
        System.setProperty("org.graphstream.ui", "swing");

        SkipList skipList = new SkipList();

        skipList.insert(1);
        skipList.insert(4);
        skipList.insert(5);
        skipList.insert(8);
        skipList.insert(10);
        skipList.insert(12);

        System.out.println("Skip List después de la inserción:");
        skipList.print();

        int searchValue = 8;
        System.out.println("¿Se encuentra " + searchValue + " en la Skip List? " + skipList.search(searchValue));

        int deleteValue = 5;
        skipList.delete(deleteValue);
        System.out.println("Skip List después de eliminar " + deleteValue + ":");
        skipList.print();

        // Visualizar el gráfico
        skipList.graph.display();
    }
}
