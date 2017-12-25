package com.mycompany.webprogramming;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public enum SimpleIndexer {
    INSTANCE;

    private final HashMap<String, HashSet<Integer>> index;
    private final List<TestDocument> testDocuments;

    SimpleIndexer() {
        index = new HashMap<>();
        testDocuments = new ArrayList<>();

        try {
            initTestDocuments(new File("testDocument.xml"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        initIndex(testDocuments);
    }

    public static SimpleIndexer getInstance() {
        return INSTANCE;
    }

    public List<TestDocument> search(final String input) {
        return search(testDocuments, input);
    }

    public List<TestDocument> search(List<TestDocument> documents, final String input) {
        HashSet<Integer> ids = index.get(input);

        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        return documents.stream()
            .filter(document -> ids.contains(document.getId()))
            .collect(Collectors.toList());
    }

    private void initIndex(List<TestDocument> testDocuments) {
        for (TestDocument testDocument : testDocuments) {
            String[] words = testDocument.getText().split("\\s+");
            for (String word : words) {
                if (index.containsKey(word)) {
                    index.get(word).add(testDocument.getId());
                } else {
                    index.put(word, new HashSet<>());
                }
            }
        }
    }

    private void initTestDocuments(File file)
        throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(file);

        NodeList documents = document.getElementsByTagName("DOCUMENTS");

        for (int i = 0; i < documents.getLength(); ++i) {
            Node currentNode = documents.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) currentNode;
                int id = Integer.parseInt(element.getAttribute("NEWID"));
                String title = null;
                String text = null;

                Node textNode = element.getElementsByTagName("TEXT").item(0);
                if (textNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element textElement = (Element) textNode;

                    NodeList titleNodeList = textElement.getElementsByTagName("TITLE");
                    if (titleNodeList.getLength() == 1) {
                        title = titleNodeList.item(0).getTextContent();
                    }

                    NodeList bodyNodeList = textElement.getElementsByTagName("BODY");
                    if (bodyNodeList.getLength() == 1) {
                        text = bodyNodeList.item(0).getTextContent();
                    }
                }

                if (title != null && text != null) {
                    testDocuments.add(new TestDocument(id, title, text));
                }

            }
        }
    }
}