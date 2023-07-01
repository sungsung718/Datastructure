import java.io.*;
import java.util.*;

public class Subway
{
    public static void main(String args[]) {
        try {
            String file = args[0]; // need to check
            readFile(file);
        } catch (IOException e) {
            System.out.println(e);
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line;
        try {
            while (!(line = br.readLine()).equals("QUIT")) {
                String[] input = line.split(" ");
                Vertex[] shortestRoute = findShortestRoute(input[0], input[1]);
                printShortestRoute(shortestRoute);
            }
        } catch (IOException e) {
            System.out.println(e);
        }

    }

    private static void readFile(String file) throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;

        HashMap<String, Integer> stationNumToIndexTable = new HashMap<>();

        while (!(line = br.readLine()).trim().isEmpty()) {
            String[] stationInfo = line.split(" ");
            String stationNum = stationInfo[0];
            String stationName = stationInfo[1];
            String stationLine = stationInfo[2];
            stations[numStations] = new Station(numStations,
                    stationNum, stationName, stationLine);
            stationNumToIndexTable.put(stationNum, numStations);
            if (!transferTable.containsKey(stationName)) {
                transferTable.put(stationName, new TransferInfo());
            }
            transferTable.get(stationName).transferList.add(numStations);
            numStations++;
        }

        adjList = new ArrayList[numStations];
        for (int i=0; i<numStations; i++) {
            adjList[i] = new ArrayList<>();
        }
        while (!(line = br.readLine()).trim().isEmpty()) {
            String[] adjInfo = line.split(" ");
            int stationIndex1 = stationNumToIndexTable.get(adjInfo[0]);
            int stationIndex2 = stationNumToIndexTable.get(adjInfo[1]);
            int weight = Integer.parseInt(adjInfo[2]);
            adjList[stationIndex1].add(new AdjVertexInfo(stationIndex2, weight));
        }

        while ((line = br.readLine()) != null) {
            String[] transInfo = line.split(" ");
            String stationName = transInfo[0];
            int transferTime = Integer.parseInt(transInfo[1]);
            transferTable.get(stationName).transferTime = transferTime;
        }
    }

    private static final int MAX_STATION_NUM = 100000;
    private static int numStations = 0;
    private static Station[] stations = new Station[MAX_STATION_NUM];
    private static ArrayList<AdjVertexInfo>[] adjList;
    private static HashMap<String, TransferInfo> transferTable = new HashMap<>();

    private static Vertex[] findShortestRoute(String src, String dest) {
        ArrayList<Integer> srcIndexes = transferTable.get(src).transferList;
        ArrayList<Integer> destIndexes = transferTable.get(dest).transferList;
        Vertex tmp = null, resultDest = null;
        for (int srcIndex: srcIndexes) {
            tmp = findShortestRouteWithSingleSource(srcIndex, destIndexes);
            if ((resultDest==null) || tmp.cost< resultDest.cost) {
                resultDest = tmp;
            }
        }

        Stack<Vertex> vertexStack = new Stack<>();
        tmp = resultDest;
        while (tmp.prev!=tmp) {
            vertexStack.push(tmp);
            tmp = tmp.prev;
        }
        vertexStack.push(tmp);
        Vertex[] shortestRoute = new Vertex[vertexStack.size()];
        for (int i=0; i< shortestRoute.length; i++) {
            shortestRoute[i] = vertexStack.pop();
        }

        return shortestRoute;
    }

    private static Vertex findShortestRouteWithSingleSource(int srcIndex, ArrayList<Integer> destIndexes) {
        boolean[] visited = new boolean[numStations];
        Arrays.fill(visited, false);
        HashMap<Integer, Vertex> costTable = new HashMap<>();
        PriorityQueue<Vertex> minHeap = new PriorityQueue<>();
        Vertex srcVertex, resultVertex = null, tmp;

        srcVertex = new Vertex(srcIndex);
        costTable.put(srcIndex, srcVertex);
        minHeap.add(srcVertex);

        while (!isAllDestVisited(destIndexes, visited) && !minHeap.isEmpty()) {
            Vertex minVertex = minHeap.poll();
            visited[minVertex.index] = true;
            for (AdjVertexInfo adjVertexInfo : adjList[minVertex.index]) {
                if (!visited[adjVertexInfo.index]) {
                    Vertex adjVertex;
                    if (!costTable.containsKey(adjVertexInfo.index)) {
                        adjVertex = new Vertex(adjVertexInfo.index,
                                minVertex.cost + adjVertexInfo.weight,
                                minVertex);
                        costTable.put(adjVertexInfo.index, adjVertex);
                    } else {
                        adjVertex = costTable.get(adjVertexInfo.index);
                        if (minVertex.cost + adjVertexInfo.weight < adjVertex.cost) {
                            adjVertex.cost = minVertex.cost + adjVertexInfo.weight;
                            adjVertex.prev = minVertex;
                        }
                    }
                    minHeap.add(adjVertex);
                }
            }

            String nameOfMinVertex = stations[minVertex.index].name;
            if (transferTable.containsKey(nameOfMinVertex)) {
                TransferInfo transferInfo = transferTable.get(nameOfMinVertex);
                for (int transferIndex : transferInfo.transferList) {
                    if ((transferIndex != minVertex.index) && !visited[transferIndex]) {
                        Vertex transferVertex;
                        if (!costTable.containsKey(transferIndex)) {
                            transferVertex = new Vertex(transferIndex,
                                    minVertex.cost + transferInfo.transferTime,
                                    minVertex);
                            costTable.put(transferIndex, transferVertex);
                        } else {
                            transferVertex = costTable.get(transferIndex);
                            if (minVertex.cost + transferInfo.transferTime < transferVertex.cost) {
                                transferVertex.cost = minVertex.cost + transferInfo.transferTime;
                                transferVertex.prev = minVertex;
                            }
                        }
                        minHeap.add(transferVertex);
                    }
                }
            }
        }

        for (int destIndex: destIndexes) {
            if (costTable.containsKey(destIndex)) {
                tmp = costTable.get(destIndex);
                if (resultVertex==null || tmp.cost<resultVertex.cost)
                    resultVertex = tmp;
            }
        }

        return resultVertex;
    }

    private static boolean isAllDestVisited(ArrayList<Integer> destIndexes, boolean[] visited) {
        for (int destIndex: destIndexes) {
            if (!visited[destIndex])
                return false;
        }
        return true;
    }

    private static void printShortestRoute(Vertex[] shortestRoute) {
        StringBuilder sb = new StringBuilder();
        String prev = "";
        String curr = "";
        boolean isPrevTransfer = false;

        for (Vertex vertex: shortestRoute) {
            curr = stations[vertex.index].name;
            if (prev.equals(curr)) {
                sb.append('[');
                sb.append(prev);
                sb.append("] ");
                isPrevTransfer = true;
            } else if (isPrevTransfer) {
                isPrevTransfer = false;
            } else {
                sb.append(prev);
                sb.append(' ');
            }
            prev = curr;
        }
        if (!isPrevTransfer)
            sb.append(curr);
        // sb.append("\r\n");

        // System.out.print(sb.deleteCharAt(0));
        // System.out.print(shortestRoute[shortestRoute.length-1].cost);
        // System.out.print("\r\n");
        System.out.println(sb.deleteCharAt(0));
        System.out.println(shortestRoute[shortestRoute.length-1].cost);
    }
}

class Station {
    int index;
    String num;
    String name;
    String line;

    public Station(int index, String num, String name, String line) {
        this.index = index;
        this.num = num;
        this.name = name;
        this.line = line;
    }
}

class AdjVertexInfo {
    int index;
    int weight;

    public AdjVertexInfo (int index, int weight) {
        this.index = index;
        this.weight = weight;
    }
}

class TransferInfo {
    ArrayList<Integer> transferList;
    int transferTime;

    public TransferInfo() {
        transferList = new ArrayList<>();
        transferTime = 5;
    }
}

class Vertex implements Comparable<Vertex>{
    int index;
    int cost;
    Vertex prev;

    public Vertex(int index) {
        this.index = index;
        cost = 0;
        prev = this;
    }

    public Vertex(int index, int cost, Vertex prev) {
        this.index = index;
        this.cost = cost;
        this.prev = prev;
    }

    @Override
    public int compareTo(Vertex vertex) {
        return this.cost- vertex.cost;
    }
}