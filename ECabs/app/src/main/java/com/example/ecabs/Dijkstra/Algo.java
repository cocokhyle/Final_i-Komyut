package com.example.ecabs.Dijkstra;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ecabs.Utils.SQLHelper;

import java.util.*;

public class Algo {

    Cursor cursor;
    public String shortestPath1 = "";
    public String status = "none";

    public void shortestPath(Context context, int startNode, int endNode, boolean isDistance) {

        if (startNode == endNode) {
            status = "die";
            return;
        }

        SQLHelper dbHelper = new SQLHelper(context);
        SQLiteDatabase dbRead = dbHelper.getReadableDatabase();

        String weightColumn = isDistance ? "weight_distance" : "weight_fare";

        cursor = dbRead.rawQuery("SELECT starting, ending, " + weightColumn + " FROM graph", null);

        Map<Integer, Map<Integer, Double>> graph = new HashMap<>();

        // Assuming the cursor has columns "starting", "ending", "weight_distance" or "weight_fare"
        while (cursor.moveToNext()) {
            int starting = cursor.getInt(0);
            int ending = cursor.getInt(1);
            double weight = cursor.getDouble(2);

            // Add edge to the graph considering either weight_distance or weight_fare
            graph.computeIfAbsent(starting, k -> new HashMap<>()).put(ending, weight);
            graph.computeIfAbsent(ending, k -> new HashMap<>()).put(starting, weight);
        }

        cursor.close();
        dbRead.close();

        Map<String, Object> result = dijkstra(graph, startNode, endNode);
        if (result != null) {
            List<Integer> path = (List<Integer>) result.get("path");
            StringBuilder pathString = new StringBuilder();
            for (int node : path) {
                pathString.append(node).append("->");
            }
            pathString.delete(pathString.length() - 2, pathString.length()); // Remove the last " -> "
            shortestPath1 = pathString.toString();
            status = "success";
        } else {
            shortestPath1 = "No path found.";
            status = "fail";
        }
    }

    private Map<String, Object> dijkstra(Map<Integer, Map<Integer, Double>> graph, int start, int end) {
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>((node1, node2) -> Double.compare(node1.cost, node2.cost));
        Map<Integer, Double> costSoFar = new HashMap<>();
        Map<Integer, Integer> predecessors = new HashMap<>();

        priorityQueue.add(new Node(start, 0.0));
        costSoFar.put(start, 0.0);
        predecessors.put(start, null);

        while (!priorityQueue.isEmpty()) {
            Node currentNode = priorityQueue.poll();

            if (currentNode.name == end) {
                return reconstructPath(predecessors, end, costSoFar.get(end));
            }

            for (Map.Entry<Integer, Double> neighbor : graph.get(currentNode.name).entrySet()) {
                double newCost = costSoFar.get(currentNode.name) + neighbor.getValue();

                if (!costSoFar.containsKey(neighbor.getKey()) || newCost < costSoFar.get(neighbor.getKey())) {
                    costSoFar.put(neighbor.getKey(), newCost);
                    predecessors.put(neighbor.getKey(), currentNode.name);
                    priorityQueue.add(new Node(neighbor.getKey(), newCost));
                }
            }
        }

        return null; // No path found
    }

    private Map<String, Object> reconstructPath(Map<Integer, Integer> predecessors, int end, double distance) {
        Map<String, Object> result = new HashMap<>();

        List<Integer> path = new ArrayList<>();
        Integer current = end;

        while (current != null) {
            path.add(current);
            current = predecessors.get(current);
        }

        Collections.reverse(path);

        result.put("path", path);
        result.put("distance", distance);

        return result;
    }

    static class Node {
        int name;
        double cost;

        Node(int name, double cost) {
            this.name = name;
            this.cost = cost;
        }
    }
}
