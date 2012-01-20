package xml.manager;

import java.util.ArrayList;

import net.sourceforge.jasa.News;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NameList;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class ParseXML {
	
	private News user;
	private ArrayList<News> news;
	
	public ParseXML() {
		news = new ArrayList <News>();
	}
	
	public void ReadUserXmlFile(Document doc){
        doc.getDocumentElement().normalize();   
        
        System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
        NodeList nList = doc.getElementsByTagName("news");
        System.out.println("-----------------------");
        
        for (int temp = 0; temp < nList.getLength(); temp++) {
 
        		   user=new News();
                   Node nNode = nList.item(temp);
                   if (nNode.getNodeType() == Node.ELEMENT_NODE) {
 
                        Element eElement = (Element) nNode;
 
                        //-------
                        NodeList delivertime = eElement.getElementsByTagName("delivertime");
                        Element nameElement = (Element)delivertime.item(0);
                        user.setDeliverTime(Integer.parseInt(((((Node)nameElement.getChildNodes().item(0)).getNodeValue().trim())))) ;
  //                       System.out.println("delivertime : " + user.getDeliverTime());
                        
 //                        System.out.println("************************************************************");
//                        //-------
                        NodeList receiversquantity = eElement.getElementsByTagName("receiversquantity");
                        Element emailElement = (Element)receiversquantity.item(0);
                        user.setReceiversQuantity(Integer.parseInt((((Node)emailElement.getChildNodes().item(0)).getNodeValue().trim()))) ;
  //                       System.out.println("receiversquantity : " + user.getReceiversQuantity());
  //                       System.out.println("************************************************************");
//                         //-------
//                         
                        NodeList receiversper = eElement.getElementsByTagName("receiversper");
                        Element passElement = (Element)receiversper.item(0);
                        user.setReceiversPer(Double.parseDouble((((Node)passElement.getChildNodes().item(0)).getNodeValue().trim())));
    //                     System.out.println("receiversper : " + user.getReceiversPer());
      //                   System.out.println("************************************************************");
//                         //-------
                        NodeList stocknewvalue = eElement.getElementsByTagName("stocknewvalue");
                        Element iduserElement = (Element)stocknewvalue.item(0);
                         user.setStockNewValue((Double.parseDouble(((Node)iduserElement.getChildNodes().item(0)).getNodeValue().trim())));
      //                   System.out.println("stocknewvalue : " + user.getStockNewValue());
      //                   System.out.println("************************************************************");
//                         //-------
                        NodeList receivers = eElement.getElementsByTagName("receivers");
                        Element creditElement = (Element)receivers.item(0);
                        String[] coiso=((Node)creditElement.getChildNodes().item(0)).getNodeValue().trim().split(",");

                        for (int z=0;z<coiso.length;z++)                        	
                        	user.getReceivers().add(Integer.parseInt(coiso[z]));

                        
      //                   System.out.println("************************************************************");
                   }
                   news.add(user);
                }
        
       
        
    }

	public News getUser() {
		return user;
	}

	public void setUser(News user) {
		this.user = user;
	}

	public ArrayList<News> getNews() {
		return news;
	}

	public void setNews(ArrayList<News> news) {
		this.news = news;
	}
	
	
	
}
