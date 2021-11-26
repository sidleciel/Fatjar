package com.xtse;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Launcher {

    private final static String OUT_FILE_NAME_PREFIX = "lib";
    private final static String OUT_FILE_NAME_SUFFIX = ".jar";

    private static ArrayList<String> jarsPath = new ArrayList<String>();
    private static String assetsPath;
    private static String outputPath;

    public static void main(String[] args) {
        String outFileName;
        FatJarCreator fatjarCreator = new FatJarCreator();

        String osName = System.getProperty("os.name");// Windows 7 , Mac OS X
        System.out.println("os.name=" + osName);
        String userdir = System.getProperty("user.dir");//当前工作目录
        System.out.println("user.dir=" + userdir);
        String javaClassPath = System.getProperty("java.class.path");//类工作目录，有可能是相对路径
        System.out.println("java.class.path=" + javaClassPath);
        String codeSource = fatjarCreator.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();//类jar绝对路径
        if (codeSource.contains("Windows")) {
            codeSource = codeSource.substring(1);
        }
        System.out.println("codeSource=" + codeSource);

        String projPath = codeSource.substring(0, codeSource.lastIndexOf("/"));
        System.out.println("work path is: " + projPath);

        if (!parse(projPath + "/fatjar_config.xml")) {
            System.out.println("Create fat jar failed !");
            return;
        }

        String outDirPath = "";
        if (outputPath == null || "".equals(outputPath)) {
            outDirPath = projPath + "/out/";
            outFileName = outDirPath + getName();
        } else {
            outFileName = outputPath;
            outDirPath = new File(outputPath).getParentFile().getPath() + "";
        }

        File outDir = new File(outDirPath);
        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        if (fatjarCreator.create(jarsPath, assetsPath, outFileName)) {
            System.out.println("outFileName:" + outFileName);
        }
    }

    private static String getName() {
        String name = null;

        Date date = new Date();
        SimpleDateFormat dateformat = new SimpleDateFormat("MMddHHmm");
        String dateSuffix = dateformat.format(date);

        name = OUT_FILE_NAME_PREFIX + dateSuffix + OUT_FILE_NAME_SUFFIX;

        return name;
    }

    // Load and parse XML file into DOM
    public static boolean parse(String filePath) {
        boolean result = true;
        jarsPath.clear();
        Document document = null;

        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = builder.parse(new File(filePath));

            Element rootElement = document.getDocumentElement();

            NodeList nodes = rootElement.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (node.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                if ("jars".equals(node.getNodeName())) {

                    NodeList nodeList = ((Element) node).getElementsByTagName("jar");
                    if (nodeList != null) {
                        for (int j = 0; j < nodeList.getLength(); j++) {
                            Element element = (Element) nodeList.item(j);
                            jarsPath.add(element.getAttribute("path"));
                        }
                    }
                } else if ("assets".equals(node.getNodeName())) {
                    assetsPath = ((Element) node).getAttribute("path");
                } else if ("output".equals(node.getNodeName())) {
                    outputPath = ((Element) node).getAttribute("path");
                }
            }

        } catch (ParserConfigurationException e) {
            result = false;
            e.printStackTrace();
        } catch (SAXException e) {
            result = false;
            e.printStackTrace();
        } catch (IOException e) {
            result = false;
            e.printStackTrace();
        }
        return result;
    }
}
