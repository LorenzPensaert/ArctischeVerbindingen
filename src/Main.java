import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
    private static List<Node> nodes;
    private static List<Edge> edges;

    public static void main(String[] args) {

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            int _numTasks = Integer.parseInt(br.readLine());
            for(int t = 0; t < _numTasks; t++) {

                nodes = new ArrayList<>();
                edges = new ArrayList<>();
                String[] lstSatellitesCentrums = br.readLine().split(" ");

                int _numCentrums = Integer.parseInt(lstSatellitesCentrums[1]);
                int _numSatellites = Integer.parseInt(lstSatellitesCentrums[0]);

                Graph graph = new Graph(_numCentrums);

                for(int c = 0; c < _numCentrums; c++) {
                    graph.addVertex(c+1);
                    String[] lstCoordinates = br.readLine().split(" ");
                    int x = Integer.parseInt(lstCoordinates[0]);
                    int y = Integer.parseInt(lstCoordinates[1]);
                    Node n = new Node(c+1,x,y);
                    nodes.add(n);
                }

                for(int a = 0; a < _numCentrums; a++) {
                    for(int b = 0; b < _numCentrums; b++) {
                        Node nodeA = nodes.get(a);
                        Node nodeB = nodes.get(b);
                        int distanceBetween = nodeA.calcDistance(nodeB);
                        Edge e = new Edge(nodeA, nodeB, distanceBetween);
                        if(!edges.contains(e) && distanceBetween != 0)
                            edges.add(e);
                    }
                }

                for(Edge e : edges) {
                    graph.addEdge(e.first.number,e.second.number,e.distance);
                }

                List<Graph.Edge> lstEdges = graph.applyKrushkalAlgo();

                List<Integer> lstVisited = new ArrayList<>();

                int satellitesLeft = _numSatellites;
                int numEdges = lstEdges.size();
                int highestK = -1;
                for (int i = numEdges - 1; i >= 0; i--) {
                    Graph.Edge e = lstEdges.get(i);
                    if(lstEdges.size() <= 1) highestK = e.weight;
                    if(!(lstVisited.contains(e.src.name) || lstVisited.contains(e.desti.name))){
                        if(!(lstVisited.contains(e.src.name) && lstVisited.contains(e.desti.name)) && satellitesLeft > 1){
                            satellitesLeft -= 2;
                            lstVisited.add(e.src.name);
                            lstVisited.add(e.desti.name);
                        }
                    }
                }
                for(Graph.Edge e : lstEdges) {
                    if(e.weight > highestK && (!(lstVisited.contains(e.src.name) && lstVisited.contains(e.desti.name)))) {
                        highestK = e.weight;
                    }
                }
                System.out.println(highestK);
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    public static class Graph {
        Vertex[] vertices;
        Edge edgeList;
        int maxSize;
        int size;
        int edgeNum;

        public Graph(int maxSize) {
            this.maxSize = maxSize;
            vertices = new Vertex[maxSize];
        }

        public class Vertex {
            int rank;
            Vertex representative;
            int name;
            Neighbour adj;

            Vertex(int name) {
                this.name = name;
                representative = this; // makeset
            }
        }

        public class Neighbour {
            int index;
            Neighbour next;
            int weight;

            Neighbour(int index, int weight, Neighbour next) {
                this.index = index;
                this.weight = weight;
                this.next = next;
            }
        }

        public class Edge {
            Vertex src;
            Vertex desti;
            Edge next;
            int weight;

            Edge(Vertex src, Vertex desti, int weight, Edge next) {
                this.src = src;
                this.desti = desti;
                this.weight = weight;
                this.next = next;
            }
        }

        public void addVertex(int name) {
            vertices[size++] = new Vertex(name);
        }

        public void addEdge(int src, int dest, int weight) {
            vertices[src - 1].adj = new Neighbour(dest - 1, weight, vertices[src - 1].adj);
            edgeList = new Edge(vertices[src - 1], vertices[dest - 1], weight, edgeList);
            edgeNum++;
        }

        public List<Edge> applyKrushkalAlgo() {
            List<Edge> mst = new ArrayList<>();
            Edge[] edges = new Edge[edgeNum];
            int i = 0;
            while (edgeList != null) {
                edges[i] = edgeList;
                i++;
                edgeList = edgeList.next;
            }

            quicksort(edges, 0, edgeNum - 1);
            for (i = 0; i < edgeNum; i++) {
                Vertex u = findSet(edges[i].src);
                Vertex v = findSet(edges[i].desti);
                if (u != v) {
                    mst.add(edges[i]);
                    //System.out.println(edges[i].src.name + " - " + edges[i].desti.name+" weight "+edges[i].weight);
                    union(u, v);
                }
            }
            return mst;
        }

        public Vertex findSet(Vertex u) {
            if (u.representative != u) {
                u.representative = findSet(u.representative); // path compression
            }
            return u.representative;
        }

        public void union(Vertex u, Vertex v) {
            if(u.rank == v.rank){
                v.representative = u;
                u.rank++;
            }else if(u.rank < v.rank){
                v.representative = u;
            }else{
                u.representative = v;
            }
        }

        public void quicksort(Edge[] edges, int start, int end) {
            if (start < end) {
                swap(edges, end, start + (end - start) / 2);
                int pIndex = pivot(edges, start, end);
                quicksort(edges, start, pIndex - 1);
                quicksort(edges, pIndex + 1, end);
            }
        }

        public int pivot(Edge[] edges, int start, int end) {
            int pIndex = start;
            Edge pivot = edges[end];
            for (int i = start; i < end; i++) {
                if (edges[i].weight < pivot.weight) {
                    swap(edges, i, pIndex);
                    pIndex++;
                }
            }
            swap(edges, end, pIndex);
            return pIndex;
        }

        public void swap(Edge[] edges, int index1, int index2) {
            Edge temp = edges[index1];
            edges[index1] = edges[index2];
            edges[index2] = temp;
        }
    }
}

class Node {
    int x, y, number;

    public Node (int number, int x, int y) {
        this.number = number;
        this.x = x;
        this.y = y;
    }

    public int calcDistance(Node other) {
        if(this.equals(other)) return 0;
        int totalX = this.x - other.x;
        int totalY = this.y - other.y;

        return (int)Math.ceil(Math.sqrt(Math.pow(totalX,2) + Math.pow(totalY,2)));
    }

    public boolean equals(Object object) {
        if(object instanceof Node && ((Node)object).x == this.x && ((Node)object).y == this.y) {
            return true;
        } else {
            return false;
        }
    }
}

class Edge {
    Node first, second;
    int distance;

    public Edge(Node first, Node second, int distance) {
        this.first = first;
        this.second = second;
        this.distance = distance;
    }

    public boolean equals(Object object) {
        if(object instanceof Edge && ((((Edge)object).first == this.first && (((Edge)object).second == this.second)) || ((((Edge)object).first == this.second && ((Edge)object).second == this.first)))) {
            return true;
        } else {
            return false;
        }
    }
}