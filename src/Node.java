/**
 * Node class for doubly-linked list implementation.
 * Stores a single text state for undo/redo functionality.
 */
class Node {
    String data;
    Node next;
    Node prev;

    Node(String data) {
        this.data = data;
    }
}
