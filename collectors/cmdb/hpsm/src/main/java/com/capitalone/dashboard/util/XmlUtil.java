package com.capitalone.dashboard.util;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.AbstractList;
import java.util.RandomAccess;

public class XmlUtil {
    private XmlUtil(){}

    public static List<Node> asList(NodeList n) {
        return n.getLength()==0?
                Collections.emptyList(): new NodeListWrapper(n);
    }
    public static Map getElementKeyValue(NodeList nodeList){
        if(nodeList == null) return new HashMap();
        Map elements = new HashMap();
        for(Node node: XmlUtil.asList(nodeList)){
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;
                String value = elem.getTextContent();
                elements.put(node.getNodeName(), value);
            }
        }
        return elements;
    }
    public static Map getElementKeyValueByTag(NodeList nodeList, String inputTag){
        if(nodeList == null || inputTag.isEmpty()) return new HashMap();
        Map elements = new HashMap();
        for(Node node: XmlUtil.asList(nodeList)){
            Element elem = (Element) node;
            String tagName = elem.getTagName();
            if(inputTag.equals(tagName)){
                elements = getElementKeyValue(node.getChildNodes());
            }
        }
        return elements;
    }
    static final class NodeListWrapper extends AbstractList<Node>
            implements RandomAccess {
        private final NodeList list;
        NodeListWrapper(NodeList l) {
            list=l;
        }
        public Node get(int index) {
            return list.item(index);
        }
        public int size() {
            return list.getLength();
        }
    }
}
