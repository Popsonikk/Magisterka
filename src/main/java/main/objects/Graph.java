package main.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
    private List<Node> nodes;
    private List<Edge> edges;

    public Graph()
    {
        nodes=new ArrayList<>();
        edges=new ArrayList<>();

    }

    public List<Node> getNodes() {
        return nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void clear()
    {
        nodes.clear();
        edges.clear();

    }
    public Map<String,DijkstraEdges>  getDijkstraFormat()
    {
        Map<String,DijkstraEdges> edgesMap=new HashMap<>();
        for(Edge e:edges)
        {
            String id1=e.getNodes().get(0).getId();
            String id2=e.getNodes().get(1).getId();
            updateMap(edgesMap,id1,id2,e.getWeight());
            updateMap(edgesMap,id2,id1,e.getWeight());
        }
        for(Map.Entry<String,DijkstraEdges> entry:edgesMap.entrySet() )
        {
            System.out.println(entry.getKey()+":");
            for(int i=0;i<entry.getValue().getNodes().size();i++)
            {
                System.out.println("id: "+entry.getValue().getNodes().get(i)+" wg: "+entry.getValue().getWeights().get(i));
            }
        }
        return edgesMap;

    }
    private void updateMap(Map<String,DijkstraEdges> map,String id, String  node, int w)
    {
        if(!map.containsKey(id))
            map.put(id,new DijkstraEdges(node,w));
        else
        {
            DijkstraEdges de=map.get(id);
            de.add(node,w);
            map.replace(id,de);
        }
    }
}
