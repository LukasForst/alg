package alg;


import java.util.*;

public class IntervalDeletionProvider {
    private final Interval interval;
    private final Node root;

    private final int numberOfAllNodes;
    private final TreeSet<Node> components;

    private int[] valuesAfterDeletion;
    private int numberOfDeletedNodes;

    public IntervalDeletionProvider(Data data) {
        interval = data.getInterval();
        root = data.getTreeRoot();
        numberOfAllNodes = data.getNumberOfNodesInTree();


        numberOfDeletedNodes = 0;
        components = new TreeSet<>();
    }

    private int idx = 0;

    public AbstractMap.SimpleEntry<Integer, Integer> solve() {
        AbstractMap.SimpleEntry<Integer, Integer> solution = null;

        if (interval.intervalState(root.getValue()) != IntervalResult.IN_INTERVAL) components.add(root);

        startRemoving(root);

        valuesAfterDeletion = new int[numberOfAllNodes - numberOfDeletedNodes];
        createNewCollectionOfValues();
//        System.err.println("Components count: " + components.size());

        Node newRoot = getRootComponent(0, valuesAfterDeletion.length - 1);
        solution = removeSubTree(newRoot);

        assert newRoot != null;

        newRoot.insertLeft(merge(0, solution.getKey() - 1));
        newRoot.insertRight(merge(solution.getValue() + 1, valuesAfterDeletion.length - 1));
//        System.err.println("New root: " + newRoot.getValue());
        return getResult(newRoot);
    }

    private void createNewCollectionOfValues() {
        for (Node node : components) {
            recursiveCreate(node);
        }
    }

    private void recursiveCreate(Node node) {
        if (node == null) return;
        recursiveCreate(node.getLeft());
        valuesAfterDeletion[idx++] = node.getValue();
        recursiveCreate(node.getRight());
    }

    private Node merge(int min, int max) {
        Node root = getRootComponent(min, max);
        if (root == null) return null;

        AbstractMap.SimpleEntry<Integer, Integer> minMax = removeSubTree(root);

        root.insertLeft(merge(min, minMax.getKey() - 1));
        root.insertRight(merge(minMax.getValue() + 1, max));

        return root;
    }

    private AbstractMap.SimpleEntry<Integer, Integer> getResult(Node node) {
        int maxDepth = 0;
        Queue<Node> toVisit = new LinkedList<>();
        toVisit.add(node);
        int[] count = new int[numberOfAllNodes - numberOfDeletedNodes];
        node.setDepth(0);

        while (!toVisit.isEmpty()) {
            Node processed = toVisit.poll();
            int depth = processed.getDepth();
            maxDepth = Integer.max(depth, maxDepth);

            count[depth]++;

            if (processed.getLeft() != null) {
                Node left = processed.getLeft();
                left.setDepth(depth + 1);
                toVisit.add(left);
            }

            if (processed.getRight() != null) {
                Node right = processed.getRight();
                right.setDepth(depth + 1);
                toVisit.add(right);
            }
        }

        int nodesInDepth = count[maxDepth - 1];
        return new AbstractMap.SimpleEntry<>(maxDepth, nodesInDepth);
    }

    private AbstractMap.SimpleEntry<Integer, Integer> removeSubTree(Node subtreeRoot) {
        Stack<Node> toVisit = new Stack<>();
        toVisit.push(subtreeRoot);
        int minValueInSubtree = Integer.MAX_VALUE;
        int maxValueInSubtree = Integer.MIN_VALUE;
        while (!toVisit.empty()) {
            Node processed = toVisit.pop();

            minValueInSubtree = Integer.min(minValueInSubtree, processed.getValue());
            maxValueInSubtree = Integer.max(maxValueInSubtree, processed.getValue());

            if (processed.getLeft() != null) toVisit.push(processed.getLeft());
            if (processed.getRight() != null) toVisit.push(processed.getRight());
        }

        return new AbstractMap.SimpleEntry<>(Arrays.binarySearch(valuesAfterDeletion, minValueInSubtree), Arrays.binarySearch(valuesAfterDeletion, maxValueInSubtree));
    }

    private Node getRootComponent(int min, int max) {
        if (min > max) return null;
        int nodesCount = max - min + 1;

        int index = ((1 + nodesCount) / 2) + min;
        int nodeValue = valuesAfterDeletion[index - 1];

        Node previous = null;
        Node current = null;
        for (Node component : components) {
            current = component;

            if (previous == null) {
                previous = current;
                if (previous.getValue() > nodeValue) return previous;
            }

            if (previous.getValue() > nodeValue || current.getValue() < nodeValue) {
                previous = current;
            } else {
                if (containsNode(previous, nodeValue)) {
                    return previous;
                } else if (containsNode(current, nodeValue)) {
                    return current;
                } else {
                    throw new IllegalStateException("Left component is smaller and right is bigger but they does not contain " + nodeValue);
                }
            }
        }
        return current;
    }

    private boolean containsNode(Node startNode, int value) {
        if (startNode == null)
            return false;
        Stack<Node> stackToVisit = new Stack<>();
        stackToVisit.push(startNode);
        while (!stackToVisit.empty()) {
            Node processed = stackToVisit.pop();

            int val = processed.getValue();
            if (val == value) return true;

            if (val < value && processed.getRight() != null) {
                stackToVisit.push(processed.getRight());
            } else if (processed.getLeft() != null) {
                stackToVisit.push(processed.getLeft());
            }
        }

        return false;
    }

    private void startRemoving(Node node) {
        if (node == null) return;

        switch (interval.intervalState(node.getValue())) {
            case BIGGER:
                verifyConnection(node, IntervalResult.BIGGER);
                startRemoving(node.getLeft());
                break;
            case SMALLER:
                verifyConnection(node, IntervalResult.SMALLER);
                startRemoving(node.getRight());
                break;
            case IN_INTERVAL:
                removeNode(node);
                break;
        }
    }

    private void verifyConnection(Node node, IntervalResult result) {
        Node parent = node.getParent();
        if (parent == null) return;
        switch (result) {
            case BIGGER:
                if (interval.intervalState(parent.getValue()) == IntervalResult.SMALLER) {
                    parent.setRight(null);
                    node.setParent(null);
                    components.add(node);
                    components.add(getMostTopNode(parent));
                }
                break;
            case SMALLER:
                if (interval.intervalState(parent.getValue()) == IntervalResult.BIGGER) {
                    parent.setLeft(null);
                    node.setParent(null);
                    components.add(node);
                    components.add(getMostTopNode(parent));
                }
                break;
            case IN_INTERVAL:
                break;
        }

    }

    private Node getMostTopNode(Node node) {
        if (node == null) return null;
        while (node.getParent() != null) {
            node = node.getParent();
        }
        return node;
    }

    private Node getMostRight(Node node) {
        while (node != null) {
            if (interval.intervalState(node.getValue()) == IntervalResult.IN_INTERVAL) {
                return node;
            }
            node = node.getRight();
        }
        return null;
    }

    private Node getMostLeft(Node node) {
        while (node != null) {
            if (interval.intervalState(node.getValue()) == IntervalResult.IN_INTERVAL) {
                return node;
            }
            node = node.getLeft();
        }
        return null;
    }

    private void removeNode(Node toRemove) {
        numberOfDeletedNodes++;

        Node parent = toRemove.getParent();
        if (parent != null) {
            if (parent.getLeft() == toRemove) {
                parent.setLeft(null);
            } else {
                parent.setRight(null);
            }
            toRemove.setParent(null);
        }

        Node right = toRemove.getRight();
        if (right != null) {
            switch (interval.intervalState(right.getValue())) {
                case BIGGER:
                    right.setParent(null);
                    toRemove.setRight(null);
                    components.add(right);

                    Node nextLeft = getMostLeft(right.getLeft());
                    if (nextLeft != null) {
                        removeNode(nextLeft);
                    }

                    break;
                case SMALLER:
                    throw new IllegalStateException("This should not happened, smaller node on the right side.");
                case IN_INTERVAL:
                    removeNode(right);
                    break;
            }
        }

        Node left = toRemove.getLeft();
        if (left != null) {
            switch (interval.intervalState(left.getValue())) {
                case BIGGER:
                    throw new IllegalStateException("This should not happened, bigger node on the left side.");
                case SMALLER:
                    left.setParent(null);
                    toRemove.setLeft(null);
                    components.add(left);

                    Node nextRight = getMostRight(left.getRight());
                    if (nextRight != null) {
                        removeNode(nextRight);
                    }
                    break;
                case IN_INTERVAL:
                    removeNode(left);
                    break;
            }
        }
    }
}
