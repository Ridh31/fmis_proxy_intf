package com.fmis.fmis_proxy_intf.fmis_proxy_intf.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Utility class for generating a unique interface code.
 * Format: [4 random uppercase letters][yyyyMMddHHmm]
 */
public class InterfaceCodeGenerator {

    // Allowed characters for the prefix (A-Z)
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    // Length of the random character prefix
    private static final int CODE_LENGTH = 4;

    // Formatter for date and time component: yyyyMMddHHmm
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    /**
     * Generates a unique interface code.
     *
     * @return the generated code in format: [4 uppercase chars][yyyyMMddHHmm]
     */
    public static String generate() {
        StringBuilder code = new StringBuilder();
        Random random = new Random();

        // Generate 4 random uppercase letters
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(ALPHABET.length());
            code.append(ALPHABET.charAt(index));
        }

        // Append current timestamp in the specified format
        String timestamp = LocalDateTime.now().format(FORMATTER);
        code.append(timestamp);

        return code.toString();
    }

    /**
     * Injects a given interface code as a new XML element <interface_code> inside the root <data> element.
     * This utility parses the XML string into a DOM structure, appends the new element,
     * and serializes it back to a well-formatted XML string.
     *
     * @param xml           The original XML string input.
     * @param interfaceCode The generated interface code to inject into the XML.
     * @return A modified XML string with <interface_code> added inside the <data> element.
     * @throws Exception If the XML is malformed or if the root <data> element is not found.
     */
    public static String injectInterfaceCodeIntoXml(String xml, String interfaceCode) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

        NodeList interfaceCodeNodes = doc.getElementsByTagName("interface_code");

        if (interfaceCodeNodes.getLength() > 0) {
            interfaceCodeNodes.item(0).setTextContent(interfaceCode);
        } else {
            Element interfaceCodeElement = doc.createElement("interface_code");
            interfaceCodeElement.setTextContent(interfaceCode);

            NodeList dataList = doc.getElementsByTagName("data");
            if (dataList.getLength() > 0) {
                // Insert interface_code element as the first child of <data>
                dataList.item(0).insertBefore(interfaceCodeElement, dataList.item(0).getFirstChild());
            } else {
                throw new Exception("Root <data> element not found.");
            }
        }

        removeWhitespaceNodes(doc.getDocumentElement()); // Clean whitespace text nodes

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "no");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));

        // Post-process output: remove blank lines and normalize indentation to 4 spaces
        String xmlOutput = writer.toString();
        String noBlankLines = xmlOutput.replaceAll("(?m)^[ \t]*\r?\n", "");
        noBlankLines = noBlankLines.replaceAll("(?m)^ {8}", "    ");
        noBlankLines = noBlankLines.replaceAll("(?m)^ {6}", "    ");
        noBlankLines = noBlankLines.replaceAll("(?m)^ {2}", "    ");

        return noBlankLines;
    }

    /**
     * Recursively removes whitespace-only text nodes from the DOM.
     * This helps reduce unnecessary blank lines in output XML.
     *
     * @param node the DOM node to clean
     */
    private static void removeWhitespaceNodes(Node node) {
        NodeList list = node.getChildNodes();
        for (int i = list.getLength() - 1; i >= 0; i--) {
            Node child = list.item(i);
            if (child.getNodeType() == Node.TEXT_NODE) {
                if (child.getTextContent().trim().isEmpty()) {
                    node.removeChild(child);
                }
            } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                removeWhitespaceNodes(child);
            }
        }
    }
}