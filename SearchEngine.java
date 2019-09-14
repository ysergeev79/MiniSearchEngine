import java.util.*;
import java.io.*;

// This class implements a google-like search engine
public class SearchEngine {

    public HashMap<String, LinkedList<String> > wordIndex;   // this will contain a set of pairs (String, LinkedList of Strings)	
    public DirectedGraph internet;             // this is our internet graph
    
    
    
    // Constructor initializes everything to empty data structures
    // It also sets the location of the internet files
    SearchEngine() {
	// Below is the directory that contains all the internet files
	HtmlParsing.internetFilesLocation = "internetFiles";
	wordIndex = new HashMap<String, LinkedList<String> > ();		
	internet = new DirectedGraph();				
    } // end of constructor//2017s
    
    
    // Returns a String description of a searchEngine
    public String toString () {
	return "wordIndex:\n" + wordIndex + "\ninternet:\n" + internet;
    }
    
    
    // This does a graph traversal of the internet, starting at the given url.
    // For each new vertex seen, it updates the wordIndex, the internet graph,
    // and the set of visited vertices.
    
    void traverseInternet(String url) throws Exception {
	
	
    	
    	internet.addVertex(url); // add current url as a vertex to the graph
    	internet.setVisited(url,true); // mark it as visited
    	
    	LinkedList<String> content = HtmlParsing.getContent(url); // get all of the words associated to given url
    	LinkedList<String> neighborUrls = HtmlParsing.getLinks(url); // get all neighboring urls
    	
    	
    	Iterator<String> i = content.iterator(); // iterate through content, and add each word to HashMap wordIndex
    	
    	while (i.hasNext()) {
    	    String word =  i.next();
    	    
    	 
    	    
    	    if ( !wordIndex.containsKey(word)) { // if the word is new to wordIndex, add it to the HashMap and create a LinkedList for it
    	    	
    	    	LinkedList<String> listOfUrls = new LinkedList<String>();
    	    	listOfUrls.addLast(url);
    	    	wordIndex.put(word,listOfUrls);
    	    	
    	    }else{
    	    	
                 if ( !( wordIndex.get(word)).contains(url) ) { //if the word already exists, but doesn't contain the url, add the url
    	    		
    	    		     wordIndex.get(word).addLast(url);
    	    		}
    	    	
    	    	
    	    }
    	    
    	}
    	            
    	//iterate over the neighboring urls
    	i = neighborUrls.iterator(); 
    	    		
        while (i.hasNext()) {
    	String vertex = i.next(); 

    	internet.addEdge(url, vertex); // add an edge between the initial url vertex and the new url vertex
    	if (!internet.getVisited(vertex)){ //if the new neighboring url has not been visited, perform the traverseInternet(String url) method on it
    		
    	traverseInternet(vertex);
    	}
    	    		
   
   }
	
	
    } // end of traverseInternet
    
    
    /* This computes the pageRanks for every vertex in the internet graph.
       It will only be called after the internet graph has been constructed using 
       traverseInternet.
       Use the iterative procedure described in the text of the assignment to
       compute the pageRanks for every vertices in the graph. 
       
       
    */
    void computePageRanks() {
	
    	
    	LinkedList<String> vertexList = internet.getVertices(); // linked list of all vertices in the internet graph
    	
    	
    	Iterator<String> i = vertexList.iterator(); //iterate through every vertex
    	
    	while (i.hasNext()) {
    	   
    		String vertex = i.next();
    		internet.setPageRank(vertex,1); // set their page rank as 1 as an initiation
    	}
    	for (int j = 0; j<100; j ++) { // perform 100 iterations for convergence
    	    i = vertexList.iterator();
    	    while (i.hasNext()) {
    	       // perform the page rank formula on every url and set its pageRank
    	    	String url = i.next();
    	    	double pageRank = 0.5;
    	    	LinkedList<String> references = internet.getEdgesInto(url);
    	        Iterator<String> k = references.iterator();
    	        while ( k.hasNext() ) {
    	        String ref =  k.next();
    	        double pgOfRef = internet.getPageRank(ref);
    	        double degree = internet.getOutDegree(ref);
    	        pageRank += 0.5*(pgOfRef/degree);
    	}
    	internet.setPageRank(url, pageRank );
   
    	    }
    	}
    	    } // end of computePageRanks
	
 
    
	
    /* Returns the URL of the page with the high page-rank containing the query word
       Returns the String "" if no web site contains the query.
       This method can only be called after the computePageRanks method has been executed.
       Start by obtaining the list of URLs containing the query word. Then return the URL 
       with the highest pageRank.
       
    */
    String getBestURL(String query) {
	
    	LinkedList<String> webSites;
    	if ( wordIndex.containsKey(query) ) { // if the wordIndx contains the query word, get the urls that contain the query word
    		webSites = wordIndex.get(query);
    	}
    	else {
    		return new String(""); // returns "" if no url contains query
    	}
    	Iterator<String> i = webSites.iterator();  //iterate through the websites to find the highest pagerank
    	String bestSite = "";
    	double bestPR = 0;
    	while (i.hasNext()) {
    	    String url = i.next();
    	    double rank = internet.getPageRank(url);
    	    
    	    if ( rank > bestPR ) {
    	bestPR = rank;
    	bestSite = url;
    	    }
    	}
    	
    	return bestSite;
    	    } // end of getBestURL
    
    
	
    public static void main(String args[]) throws Exception{		
	SearchEngine mySearchEngine = new SearchEngine();
	
	mySearchEngine.traverseInternet("http://www.cs.mcgill.ca");
	
	
	mySearchEngine.computePageRanks();
	
	BufferedReader stndin = new BufferedReader(new InputStreamReader(System.in));
	String query;
	do {
	    System.out.print("Enter query: ");
	    query = stndin.readLine();
	    if ( query != null && query.length() > 0 ) {
		System.out.println("Best site = " + mySearchEngine.getBestURL(query));
	    }
	} while (query!=null && query.length()>0);
				
    } // end of main
}
