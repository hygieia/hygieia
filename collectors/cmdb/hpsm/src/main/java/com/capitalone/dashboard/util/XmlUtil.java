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
        Map elements = new HashMap();
        if (nodeList!=null && nodeList.getLength()>0 ){
            for(int i=0 ; i < nodeList.getLength() ; i++){
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) node;
                    String value = elem.getTextContent();
                    elements.put(node.getNodeName(), value);
                }
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
