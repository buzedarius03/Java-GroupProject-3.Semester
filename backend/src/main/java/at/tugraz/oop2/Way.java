package at.tugraz.oop2;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Way {
    private long id;
    private List<Element> nodes;
    private Map<String, String> tags;

    public Way(long id) {
        this.id = id;
        this.nodes = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public List<Element> getNodes() {
        return nodes;
    }

    public void addNode(Element node) {
        nodes.add(node);
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }
}