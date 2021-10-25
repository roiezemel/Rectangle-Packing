package com.example.chipfloorplanningoptimization.representation;

public class BNode<T> {

    private T value;
    private BNode<T> left;
    private BNode<T> right;

    public BNode(T value, BNode<T> left, BNode<T> right) {
        this.value = value;
        this.left = left;
        this.right = right;
    }

    public BNode(T value) {
        this.value = value;
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
}
