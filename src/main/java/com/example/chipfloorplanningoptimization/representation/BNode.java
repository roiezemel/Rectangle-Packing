package com.example.chipfloorplanningoptimization.representation;

public class BNode<T> {

    private T value;
    private BNode<T> left;
    private BNode<T> right;
    private BNode<T> parent;

    /**
     * Initialize BNode
     * @param value value
     * @param parent parent pointer
     * @param left left pointer
     * @param right right pointer
     */
    public BNode(T value, BNode<T> parent, BNode<T> left, BNode<T> right) {
        this.value = value;
        this.left = left;
        this.right = right;
        this.parent = parent;
    }

    /**
     * Initialize BNode
     * @param value value
     * @param parent parent pointer
     */
    public BNode(T value, BNode<T> parent) {
        this.value = value;
        this.parent = parent;
    }

    /**
     * Get value
     * @return
     */
    public T getValue() {
        return value;
    }

    /**
     * Set value
     * @param value
     */
    public void setValue(T value) {
        this.value = value;
    }

    /**
     * Get left pointer
     * @return
     */
    public BNode<T> getLeft() {
        return left;
    }

    /**
     * Set left pointer
     * @param left
     */
    public void setLeft(BNode<T> left) {
        this.left = left;
    }

    /**
     * Get right pointer
     * @return
     */
    public BNode<T> getRight() {
        return right;
    }

    /**
     * Set right pointer
     * @param right
     */
    public void setRight(BNode<T> right) {
        this.right = right;
    }

    /**
     * Set parent pointer
     * @param parent
     */
    public void setParent(BNode<T> parent) {
        this.parent = parent;
    }

    /**
     * Get parent pointer
     * @return
     */
    public BNode<T> getParent() {
        return parent;
    }

    /**
     * Has left child
     * @return
     */
    public boolean hasLeft() {
        return left != null;
    }

    /**
     * Has right child
     * @return
     */
    public boolean hasRight() {
        return right != null;
    }
}
