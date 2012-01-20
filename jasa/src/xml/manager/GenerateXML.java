package xml.manager;


import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.jasa.News;

import org.jdom.*;
import org.jdom.output.*;

public class GenerateXML {

    private ArrayList <News> news;
    private Element pubElement, laptops;
    private Document myDocument;

    public GenerateXML(ArrayList<News> news) {
        this.news = news;
        init();
        generateXML();
    }

    private void init() {
        //ProcessingInstruction pi = new ProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"laptops.xsl\"");
        this.pubElement = new Element("stockmarket");
        this.laptops = new Element("stockmarket_news");
        this.myDocument = new Document(pubElement);
        
        this.myDocument.addContent(0, new Comment("XML Generated with JDom"));
      //  this.myDocument.getContent().add(0, pi);
        
    }

    protected void generateXML() {
        for (int i = 0; i < this.news.size(); i++) {
            
            Element n = new Element("news");

            //inside brand
            Element delivertime = new Element("delivertime");
            Element receiversquantity = new Element("receiversquantity");
            Element receiversper = new Element("receiversper");
            Element stocknewvalue = new Element("stocknewvalue");
            
            delivertime.addContent(Integer.toString(news.get(i).getDeliverTime()));
            receiversquantity.addContent(Integer.toString(news.get(i).getReceiversQuantity()));
            receiversper.addContent(Double.toString(news.get(i).getReceiversPer()));
            stocknewvalue.addContent(Double.toString(news.get(i).getStockNewValue()));
            n.addContent(delivertime);
            n.addContent(receiversquantity);
            n.addContent(receiversper);
            n.addContent(stocknewvalue);

            //inside compontents
            ArrayList <Integer> rec = news.get(i).getReceivers();
            Element receivers = new Element("receivers");

            String aux= "";
            for (int j = 0; j < rec.size(); j++)
            	aux= aux + rec.get(j)+",";

            receivers.addContent(aux.substring(0, aux.length() - 1));
                   
            n.addContent(receivers);

            laptops.addContent(n);

        }
        pubElement.addContent(laptops);

        try {
            writeXML();
            System.out.println("XML file Generated: laptops");
        } catch (IOException ex) {
            Logger.getLogger(GenerateXML.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void writeXML() throws IOException {
        // Write it to disk
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        FileWriter writer = new FileWriter("news.xml");
        outputter.output(myDocument, writer);
        writer.close();
    }
}