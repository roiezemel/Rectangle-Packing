package com.example.chipfloorplanningoptimization.representation;

public class BNode<T> {

    private T value;
    private BNode<T> left;
    private BNode<T> right;
    private BNode<T> parent;

    public BNode(T value, BNode<T> parent, BNode<T> left, BNode<T> right) {
        this.value = value;
        this.left = left;
        this.right = right;
        this.parent = parent;
    }

    public BNode(T value, BNode<T> parent) {
        this.value = value;
        this.parent = parent;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public BNode<T> getLeft() {
        return left;
    }

    public void setLeft(BNode<T> left) {
        this.left = left;
    }

    public BNode<T> getRight() {
        return right;
    }

    public void setRight(BNode<T> right) {
        this.right = right;
    }

    public void setParent(BNode<T> parent) {
        this.parent = parent;
    }

    public BNode<T> getParent() {
        return parent;
    }

    public boolean hasLeft() {
        return left != null;
    }

    public boolean hasRight() {
        return right != null;
    }
}
