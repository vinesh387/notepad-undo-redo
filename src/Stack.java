/**
 * Custom Stack class implementing undo/redo using a doubly-linked list.
 * Manages text edit history like a timeline. Stores full-text snapshots.
 */
public class Stack {

    private Node head;      // First saved state
    private Node current;   // Current state pointer
    private int maxSize = 1000;
    private int size = 0;

    /**
     * Push a new text state.
     * Clears redo history if called after an undo.
     */
    public void push(String data) {

        // Avoid duplicate back-to-back states
        if (current != null && current.data.equals(data)) {
            return;
        }

        Node newNode = new Node(data);

        if (head == null) {
            // First state
            head = newNode;
            current = newNode;
            size = 1;
            return;
        }

        // If current had forward nodes (redo history), remove them
        if (current.next != null) {
            current.next.prev = null;
            current.next = null;
        }

        // Link new state
        current.next = newNode;
        newNode.prev = current;
        current = newNode;
        size++;

        // Enforce max size limit
        while (size > maxSize && head.next != null) {
            head = head.next;
            head.prev = null;
            size--;
        }
    }

    /**
     * Undo operation.
     * @return previous text state or null if undo not possible.
     */
    public String pop() {
        if (current != null && current.prev != null) {
            current = current.prev;
            return current.data;
        }
        return null; // No undo available
    }

    /**
     * Redo operation.
     * @return next text state or null if redo not possible.
     */
    public String redoPop() {
        if (current != null && current.next != null) {
            current = current.next;
            return current.data;
        }
        return null; // No redo available
    }

    /**
     * @return true if undo operation can be performed.
     */
    public boolean canUndo() {
        return current != null && current.prev != null;
    }

    /**
     * @return true if redo operation can be performed.
     */
    public boolean canRedo() {
        return current != null && current.next != null;
    }

    /**
     * @return current text state without moving the pointer.
     */
    public String peek() {
        return (current != null) ? current.data : "";
    }

    /**
     * Clears all history.
     */
    public void clear() {
        head = null;
        current = null;
        size = 0;
    }

    /**
     * @return number of stored states.
     */
    public int size() {
        return size;
    }

    /**
     * @return true if no states saved.
     */
    public boolean isEmpty() {
        return size == 0;
    }
}