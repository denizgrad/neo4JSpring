package com.metu.ceng553.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.cypher.ExecutionEngine;
import org.neo4j.cypher.ExecutionResult;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.impl.util.StringLogger;


//import com.metu.ceng553.entity.Actor;
//import com.metu.ceng553.entity.Collector;

import scala.collection.Iterator;


public class AppService  {
	{
		if(init){
			initialize();
		}
		init = false;
	}

	private static enum RelTypes implements RelationshipType
    {
        ACTED_IN,
        DIRECTED,
        COLLECTS,
        FOLLOWS
    }
	private static final String DB_PATH = "C:/NEO4JTEST";
	public static GraphDatabaseService graphDb = null;
	
    Node firstNode;
    Node secondNode;
    Relationship relationship;
    Label ACTOR;
    Label COLLECTOR;
    Label MOVIE;
    Label DIRECTOR;
    
	public void initialize() {
		
        deleteFileOrDirectory( new File( DB_PATH ) );
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
        registerShutdownHook( graphDb );
        ACTOR = DynamicLabel.label("Actor");
        MOVIE = DynamicLabel.label("Movie");
        DIRECTOR = DynamicLabel.label("Director");
        COLLECTOR = DynamicLabel.label("Collector");
		  try ( Transaction tx = graphDb.beginTx() )
	        {
	        	BufferedReader br = null;
	        	BufferedReader br2 = null;
	        	BufferedReader br3 = null;
	        	BufferedReader br4 = null;
				HashMap<String, Long> actorListWithNodeId = new HashMap<>();
				HashMap<String, Long> directorListWithNodeId = new HashMap<>();
				HashMap<String, Long> collectorListWithNodeId = new HashMap<>();
				HashMap<String, Long> movieListWithNodeId = new HashMap<>();
	    		try {
	    			String sCurrentLine;
	    			br = new BufferedReader(new FileReader("CENG553\\FILMS.txt"));
	    			

	    			
	    			int actorId = 2000;
	    			int directorId = 3000;
	    			while ((sCurrentLine = br.readLine()) != null) {
	    				String[] featureList = sCurrentLine.split(" % ");
	    				String[] actors = featureList[3].split(", ");
	    				
	    				
	    				Node film = graphDb.createNode();
	    				film.addLabel(MOVIE);
	    				film.setProperty("id", featureList[0]);
	    				film.setProperty("title", featureList[1]);
	    				film.setProperty("released", featureList[2]);
	    				film.setProperty("rating", featureList[6]);
	    				movieListWithNodeId.put(featureList[0], film.getId());
	    				
	    				for(int i=0; i<actors.length; i++){
	    					if(!actorListWithNodeId.containsKey(actors[i])){
	    						if(directorListWithNodeId.containsKey(actors[i])){
	    							Node director = graphDb.getNodeById(directorListWithNodeId.get(actors[i]));
	    							director.addLabel(ACTOR);
	    							director.setProperty("userid", directorId);
	    							directorId++;
	    							actorListWithNodeId.put(actors[i], director.getId());
	    							director.createRelationshipTo( film, RelTypes.ACTED_IN);
	    						}
	    						else{
	        						Node actor = graphDb.createNode();
	        						actor.addLabel(ACTOR);
	        						actor.setProperty("userid", actorId);
	        						actorId++;
	        						actor.setProperty("fullname", actors[i]);
	        						actorListWithNodeId.put(actors[i], actor.getId());
	        						actor.createRelationshipTo( film, RelTypes.ACTED_IN);
	    						}
	    					}
	    					else{
	    						Node actor = graphDb.getNodeById(actorListWithNodeId.get(actors[i]));
	    						actor.createRelationshipTo(film, RelTypes.ACTED_IN);
	    					}
	    				}
	    				
	    				if(!directorListWithNodeId.containsKey(featureList[5])){
	    					if(actorListWithNodeId.containsKey(featureList[5])){
	    						Node actor = graphDb.getNodeById(actorListWithNodeId.get(featureList[5]));
	    						actor.addLabel(DIRECTOR);
	    						actor.setProperty("userid", directorId);
	    						directorId++;
	    						directorListWithNodeId.put(featureList[5], actor.getId());
	    						actor.createRelationshipTo(film, RelTypes.DIRECTED);
	    					}
	    					else{
	    						Node director = graphDb.createNode();
	    						director.addLabel(DIRECTOR);
	    						director.setProperty("userid", directorId);
	    						directorId++;
	    						director.setProperty("fullname", featureList[5]);
	    						directorListWithNodeId.put(featureList[5], director.getId());
	    						director.createRelationshipTo( film, RelTypes.DIRECTED);
	    					}
	    				}
	    				else{
	    					Node director = graphDb.getNodeById(directorListWithNodeId.get(featureList[5]));
	    					director.createRelationshipTo( film, RelTypes.DIRECTED);
	    				}
	    				
	    			/*	System.out.println("Id: "+featureList[0]);
	    				System.out.println("Name: "+featureList[1]);
	    				System.out.println("Release Year: "+featureList[2]);
	    				System.out.println("Actors: "+featureList[3]);
	    				System.out.println("Genre: "+featureList[4]);
	    				System.out.println("Director: "+featureList[5]);
	    				System.out.println("Rating: "+featureList[6]);
	    			
	    				*/
	    				/*
	    				for(int i=0; i<actors.length; i++){
	    					System.out.println("Actor"+i+" "+actors[i]);
	    				}
	    				*/
	    			}
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		} finally {
	    			try {
	    				if (br != null)br.close();
	    			} catch (IOException ex) {
	    				ex.printStackTrace();
	    			}
	    		}	
	    		
	    		
	    			try {
	        			String sCurrentLine2;
						br2 = new BufferedReader(new FileReader("CENG553\\collectors.txt"));
		    			while ((sCurrentLine2 = br2.readLine()) != null){
		    				String[] collectorFeatures = sCurrentLine2.split("%");
		    				Node collector = graphDb.createNode();
		    				collector.addLabel(COLLECTOR);
		    				collector.setProperty("userid", collectorFeatures[0]);
		    				collector.setProperty("fullname", collectorFeatures[1]);
		    				collectorListWithNodeId.put(collectorFeatures[0], collector.getId());
		    			}
						
					} catch (IOException e) {
						e.printStackTrace();
					}finally {
		    			try {
		    				if (br2 != null)br2.close();
		    			} catch (IOException ex) {
		    				ex.printStackTrace();
		    			}
		    		}	
	    			
	    			
	    			try {
	        			String sCurrentLine3;
						br3 = new BufferedReader(new FileReader("CENG553\\follow.txt"));
		    			while ((sCurrentLine3 = br3.readLine()) != null){
		    				String[] followList = sCurrentLine3.split("%");
		    				Node first = graphDb.getNodeById(collectorListWithNodeId.get(followList[0]));
		    				Node second = graphDb.getNodeById(collectorListWithNodeId.get(followList[1]));
		    				first.createRelationshipTo(second, RelTypes.FOLLOWS);
		    			}
						
					} catch (IOException e) {
						e.printStackTrace();
					}finally {
		    			try {
		    				if (br3 != null)br3.close();
		    			} catch (IOException ex) {
		    				ex.printStackTrace();
		    			}
		    		}	
	    			
	    			try {
	        			String sCurrentLine4;
						br4 = new BufferedReader(new FileReader("CENG553\\collect.txt"));
		    			while ((sCurrentLine4 = br4.readLine()) != null){
		    				String[] collectList = sCurrentLine4.split("%");
		    				Node first = graphDb.getNodeById(collectorListWithNodeId.get(collectList[0]));
		    				Node second = graphDb.getNodeById(movieListWithNodeId.get(collectList[1]));
		    				first.createRelationshipTo(second, RelTypes.COLLECTS);
		    			}
						
					} catch (IOException e) {
						e.printStackTrace();
					}finally {
		    			try {
		    				if (br4 != null)br4.close();
		    			} catch (IOException ex) {
		    				ex.printStackTrace();
		    			}
		    		}	

	    		tx.success();
	        }
		
	}
	public static void main(String[] args) {
//		GraphDatabaseFactory graphDbFactory = new GraphDatabaseFactory();
//		GraphDatabaseService graphDb = graphDbFactory.newEmbeddedDatabase("C:/TPNeo4jDB");
//		ExecutionEngine execEngine = new ExecutionEngine(graphDb,StringLogger.SYSTEM );
//		execEngine.execute("CREATE (timR:DIRECTOR {userid:1, name:\"Tim Robbins\"})"
//				+ " CREATE (charlizeT:Actor {userid:15, name:\"Charlize Theron\"})"
//				+ " CREATE (juliaR:Actor {userid:16, name:\"Julia Roberts\"})");
//		ExecutionResult execResult = execEngine.execute(" MATCH (a:Actor) RETURN a.name AS name, a.userid AS userid");
//		String results = execResult.dumpToString();
//		System.out.println(results);
	}
	public static boolean  init = true;
//	public void createActor(Actor actor) {
//		// if so no native sql will be written to satisfy assigned task.
//	}

	public String sql1() {
			ExecutionEngine execEngine = new ExecutionEngine(graphDb,StringLogger.SYSTEM_DEBUG);
        	ExecutionResult er = execEngine.execute("MATCH (actor:Actor:Director) RETURN actor");
        	String results = er.dumpToString();
  	      System.out.println(results);
  	      results = results.replace("\r\n"," <br> ");
  	    return  results;
//        	Iterator<Node> n_column = er.columnAs( "actor" );
//        	List<Actor> retList = new ArrayList<Actor>();
//        	StringBuilder sb = new StringBuilder();
//        	while(n_column.hasNext()){
//        		Node a = n_column.next();
//        		retList.add(new Actor((Long)a.getProperty("userid"), (String) a.getProperty("fullname")));
//        	}
//        	for (Actor a : retList) {
//    			sb.append(a.toString() + "</br>");
//    		}
//    		return sb.toString();
	}

	public String sql2() {
        	ExecutionEngine ee = new ExecutionEngine(graphDb,StringLogger.DEV_NULL);
        	ExecutionResult er = ee.execute("MATCH (a:Actor)-[act:ACTED_IN]->(m:Movie) WITH a,count(act) as actCount WHERE actCount>=5 RETURN a");
        	String results = er.dumpToString();
  	      System.out.println(results);
  	      results = results.replace("\r\n"," <br> ");
  	    return  results;
//        	Iterator<Node> n_column = er.columnAs( "a" );
//        	List<Actor> retList = new ArrayList<Actor>();
//        	StringBuilder sb = new StringBuilder();
//        	while(n_column.hasNext()){
//        		Node a = n_column.next();
//        		retList.add(new Actor((Long)a.getProperty("userid"), (String) a.getProperty("fullname")));
//        	}
//        	for (Actor a : retList) {
//    			sb.append(a.toString() + "</br>");
//    		}
//    		return sb.toString();
	}

	public String sql3() {
		ExecutionEngine ee2 = new ExecutionEngine(graphDb,StringLogger.DEV_NULL);
 	    ExecutionResult er2 = ee2.execute("MATCH (actor:Actor{ fullname: 'Edward Norton' })-[:ACTED_IN]->(y:Movie)<-[:ACTED_IN]-(otherActors:Actor) RETURN count(otherActors) as total");
    	Iterator<Long> n_column2 = er2.columnAs( "total" );
    	while(n_column2.hasNext()){
    		Long a = n_column2.next();
    		return a.toString();
    	}
  	    return  "0";
	}

	public String sql4() {
//			ExecutionEngine ee = new ExecutionEngine(graphDb,StringLogger.DEV_NULL);
//     	    ExecutionResult er = ee.execute(
//				  "MATCH (a1:Actor {name:\"Edward Norton\"})-[:ACTED_IN]->(m:Movie) "
//				+ "WITH a1,collect(m) as movies "
//				+ "MATCH (c:Collector)-[:COLLECTS]->(m:Movie) "
//				+ "WHERE ALL(m IN movies WHERE (c)-[:COLLECTS]->(m) ) "
//				+ "RETURN DISTINCT c.name AS name, c.userid AS userid");
//     	   String results = er.dumpToString();
// 	      System.out.println(results);
//  	      results = results.replace("\r\n"," <br> ");
//  	    return  results;
		 try ( Transaction tx = graphDb.beginTx() )
	        {
		ArrayList<Object> nodeList = new ArrayList<>();
    	ArrayList<Object> nodeAsilList = new ArrayList<>();
    	int test1 = 0;
    	int query4Temp = 0;
    	for(Node node: graphDb.findNodesByLabelAndProperty(ACTOR, "fullname", "Edward Norton")){
    		Traverser friendsTraverser = getActedIn(node);
    		
    		for ( Path friendPath : friendsTraverser )
            {
    			Traverser friendTraverser2 = getCollectIn((friendPath.endNode()));
    			for(Path friendPath2 : friendTraverser2){
    				if(query4Temp == 0){
    					nodeList.add(friendPath2.endNode().getProperty("userid"));
    				}
    				else{
    					if(nodeList.contains(friendPath2.endNode().getProperty("userid"))){
    						nodeAsilList.add(friendPath2.endNode().getProperty("userid"));
    					}
    				}
    			}
    			if(query4Temp != 0){
    				nodeList.clear();
    				for (int i=0; i<nodeAsilList.size(); i++){
    					nodeList.add(nodeAsilList.get(i));
    				}
    			}
    			query4Temp++;
    			System.out.println(friendPath.endNode().getProperty("title"));
            }
    	}
    	StringBuilder sb = new StringBuilder();
    	for(int i=0; i<nodeAsilList.size(); i++){
    		for(Node x : graphDb.findNodesByLabelAndProperty(COLLECTOR, "userid", nodeAsilList.get(i))){
    			sb.append(x.getProperty("userid")+" "+x.getProperty("fullname")+"<br>");
    		}
    	}
    	System.out.println(test1);
    	return sb.toString();
        }
	}

	public String sql5() {
			ExecutionEngine ee = new ExecutionEngine(graphDb,StringLogger.DEV_NULL);
	 	    ExecutionResult er = ee.execute(
					  " MATCH (c:Collector)-[:COLLECTS]->(m:Movie {title:\"The Shawshank Redemption\"})"
					+ " RETURN c LIMIT 10");
	 	   String results = er.dumpToString();
		      System.out.println(results);
	  	      results = results.replace("\r\n"," <br> ");
	    	    return  results;
//	 	   Iterator<Node> n_column = er.columnAs("c");
//			List<Collector> retList = new ArrayList<Collector>();
//			StringBuilder sb = new StringBuilder();
//			while (n_column.hasNext()) {
//				Node a = n_column.next();
//				retList.add(new Collector((Long) a.getProperty("userid"), (String) a.getProperty("fullname")));
//			}
//			for (Collector a : retList) {
//				sb.append(a.toString() + "</br>");
//			}
//			return sb.toString();
	}

	private static void deleteFileOrDirectory(File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				for (File child : file.listFiles()) {
					deleteFileOrDirectory(child);
				}
			}
			file.delete();
		}
	}
	 private static void registerShutdownHook( final GraphDatabaseService graphDb )
	    {
	        Runtime.getRuntime().addShutdownHook( new Thread()
	        {
	            @Override
	            public void run()
	            {
	                graphDb.shutdown();
	            }
	        } );
	    }
	public void initializeDepr() {
		
		deleteFileOrDirectory( new File( DB_PATH ) );
		System.out.println("DB deleted");
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
        registerShutdownHook( graphDb );
//        driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "pass"));
//		session = driver.session();
        	ExecutionEngine execEngine = new ExecutionEngine(graphDb,StringLogger.SYSTEM_DEBUG);
//        	ExecutionResult er = //ee.execute("MATCH (n) DETACH DELETE n");

		// Create Actors and Directors
        	execEngine.execute("CREATE (timR:Actor {userid:1, name:\"Tim Robbins\"})"
				+ " CREATE (morganF:Actor {userid:2, name:\"Morgan Freeman\"})"

				+ " CREATE (bobG:Actor {userid:3, name:\"Bob Gunton\"})"
				+ " CREATE (frankD:Director {userid:4, name:\"Frank Darabont\"})"
				+ " CREATE (robertDN:Actor:Director {userid:4, name:\"Robert De Niro\"})"
				+ " CREATE (michaelD:Actor {userid:5, name:\"Michael Douglas\"})"
				+ " CREATE (jonT:Director {userid:6, name:\"Jon Turteltaub\"})"
				+ " CREATE (mattD:Actor {userid:7, name:\"Jon Turteltaub\"})"
				+ " CREATE (angelinaJ:Actor:Director {userid:8, name:\"Angelina Julie\"})"
				+ " CREATE (timurB:Director {userid:9, name:\"Timur Bekmambetov\"})"
				+ " CREATE (jamesMc:Actor {userid:10, name:\"James Mc Avoy\"})"
				+ " CREATE (tomH:Actor:Director {userid:11, name:\"Tom Hanks\"})"
				+ " CREATE (michaelCD:Actor {userid:12, name:\"Michael Clark Duncan\"})"
				+ " CREATE (davidM:Actor {userid:13, name:\"David Morse\"})"
				+ " CREATE (livT:Actor {userid:14, name:\"Liv Tyler\"})"
				+ " CREATE (charlizeT:Actor {userid:15, name:\"Charlize Theron\"})"
				+ " CREATE (juliaR:Actor {userid:16, name:\"Julia Roberts\"})"
				+ " CREATE (sarahM:Actor {userid:17, name:\"Sarah Mahoney\"})"
				+ " CREATE (brianDP:Director {userid:18, name:\"Brian De Palma\"})"
				+ " CREATE (bruceW:Actor {userid:19, name:\"Bruce Willis\"})"
				+ " CREATE (stephenC:Director {userid:20, name:\"Stephen Campanelli\"})"
				+ " CREATE (olgaK:Actor {userid:21, name:\"Olga Kurylenko\"})"
				+ " CREATE (jamesP:Actor {userid:22, name:\"James Purefoy\"})"
				+ " CREATE (davidF:Director {userid:23, name:\"David Fincher\"})"
				+ " CREATE (bradP:Actor {userid:24, name:\"Brad Pitt\"})"
				+ " CREATE (edwardN:Actor {userid:25, name:\"Edward Norton\"})"
				+ " CREATE (meatL:Actor {userid:26, name:\"Meat Loaf\"})"
				+ " CREATE (melanieL:Actor {userid:27, name:\"Melanie Laurent\"})"
				+ " CREATE (tonyK:Director {userid:28, name:\"Tony Kaye\"})"
				+ " CREATE (edwardF:Actor {userid:29, name:\"Edward Furlong\"})"
				+ " CREATE (beverlyDA:Actor {userid:30, name:\"Beverly Di Angelo\"})"

				// Create Movies and ACTED_IN - DIRECTED Relations
				+ " CREATE (tsr:Movie {id:1,title:\"The Shawshank Redemption\",releasedYear:1994,rating:9.3,genre:[\"Crime\",\"Drama\"]})"
				+ " CREATE (frankD)-[:DIRECTED]->(tsr)" + " CREATE (timR)-[:ACTED_IN]->(tsr)"
				+ " CREATE (morganF)-[:ACTED_IN]->(tsr)" + " CREATE (bobG)-[:ACTED_IN]->(tsr)"
				+ " CREATE (lv:Movie {id:2,title:\"Last Vegas\",releasedYear:2013,rating:6.6,genre:[\"Comedy\"]})"
				+ " CREATE (jonT)-[:DIRECTED]->(lv)" + " CREATE (morganF)-[:ACTED_IN]->(lv)"
				+ " CREATE (michaelD)-[:ACTED_IN]->(lv)" + " CREATE (robertDN)-[:ACTED_IN]->(lv)"
				+ " CREATE (tgs:Movie {id:3,title:\"The Good Shepherd\",releasedYear:2006,rating:6.7,genre:[\"Drama\",\"History\",\"Thriller\"]})"
				+ " CREATE (robertDN)-[:DIRECTED]->(tgs)" + " CREATE (robertDN)-[:ACTED_IN]->(tgs)"
				+ " CREATE (mattD)-[:ACTED_IN]->(tgs)" + " CREATE (angelinaJ)-[:ACTED_IN]->(tgs)"
				+ " CREATE (w:Movie {id:4,title:\"Wanted\",releasedYear:2008,rating:6.7,genre:[\"Action\",\"Crime\",\"Fantasy\",\"Thriller\"]})"
				+ " CREATE (timurB)-[:DIRECTED]->(w)" + " CREATE (angelinaJ)-[:ACTED_IN]->(w)"
				+ " CREATE (morganF)-[:ACTED_IN]->(w)" + " CREATE (jamesMc)-[:ACTED_IN]->(w)"
				+ " CREATE (tgm:Movie {id:5,title:\"The Green Mile\",releasedYear:1999,rating:8.5,genre:[\"Drama\",\"Crime\",\"Fantasy\",\"Mystery\"]})"
				+ " CREATE (frankD)-[:DIRECTED]->(tgm)" + " CREATE (tomH)-[:ACTED_IN]->(tgm)"
				+ " CREATE (michaelCD)-[:ACTED_IN]->(tgm)" + " CREATE (davidM)-[:ACTED_IN]->(tgm)"
				+ " CREATE (ttyd:Movie {id:6,title:\"That Thing You Do\",releasedYear:1996,rating:6.9,genre:[\"Comedy\",\"Drama\",\"Music\"]})"
				+ " CREATE (tomH)-[:DIRECTED]->(ttyd)" + " CREATE (tomH)-[:ACTED_IN]->(ttyd)"
				+ " CREATE (livT)-[:ACTED_IN]->(ttyd)" + " CREATE (charlizeT)-[:ACTED_IN]->(ttyd)"
				+ " CREATE (lc:Movie {id:7,title:\"Larry Crowne\",releasedYear:2011,rating:6.1,genre:[\"Comedy\",\"Drama\",\"Romance\"]})"
				+ " CREATE (tomH)-[:DIRECTED]->(lc)" + " CREATE (tomH)-[:ACTED_IN]->(lc)"
				+ " CREATE (juliaR)-[:ACTED_IN]->(lc)" + " CREATE (sarahM)-[:ACTED_IN]->(lc)"
				+ " CREATE (tbv:Movie {id:8,title:\"The Bonfire of the Vanities\",releasedYear:1990,rating:5.4,genre:[\"Comedy\",\"Drama\",\"Romance\"]})"
				+ " CREATE (brianDP)-[:DIRECTED]->(tbv)" + " CREATE (tomH)-[:ACTED_IN]->(tbv)"
				+ " CREATE (morganF)-[:ACTED_IN]->(tbv)" + " CREATE (bruceW)-[:ACTED_IN]->(tbv)"
				+ " CREATE (m:Movie {id:9,title:\"Momentum\",releasedYear:2015,rating:5.5,genre:[\"Action\",\"Crime\",\"Thriller\"]})"
				+ " CREATE (stephenC)-[:DIRECTED]->(m)" + " CREATE (morganF)-[:ACTED_IN]->(m)"
				+ " CREATE (olgaK)-[:ACTED_IN]->(m)" + " CREATE (jamesP)-[:ACTED_IN]->(m)"
				+ " CREATE (fc:Movie {id:10,title:\"Fight Club\",releasedYear:1999,rating:8.8,genre:[\"Drama\"]})"
				+ " CREATE (davidF)-[:DIRECTED]->(fc)" + " CREATE (bradP)-[:ACTED_IN]->(fc)"
				+ " CREATE (edwardN)-[:ACTED_IN]->(fc)" + " CREATE (meatL)-[:ACTED_IN]->(fc)"
				+ " CREATE (bts:Movie {id:11,title:\"By the Sea\",releasedYear:2015,rating:5.2,genre:[\"Drama\",\"Romance\"]})"
				+ " CREATE (angelinaJ)-[:DIRECTED]->(bts)" + " CREATE (angelinaJ)-[:ACTED_IN]->(bts)"
				+ " CREATE (bradP)-[:ACTED_IN]->(bts)" + " CREATE (melanieL)-[:ACTED_IN]->(bts)"
				+ " CREATE (ahx:Movie {id:12,title:\"American History X\",releasedYear:1998,rating:8.6,genre:[\"Drama\",\"Crime\"]})"
				+ " CREATE (tonyK)-[:DIRECTED]->(ahx)" + " CREATE (edwardN)-[:ACTED_IN]->(ahx)"
				+ " CREATE (edwardF)-[:ACTED_IN]->(ahx)" + " CREATE (beverlyDA)-[:ACTED_IN]->(ahx)"

				// Create Collectors and Collects relations
				+ " CREATE (c1:Collector {userid:1001, name:\"Collector One\"})" + " CREATE (c1)-[:COLLECTS]->(tsr)"
				+ " CREATE (c1)-[:COLLECTS]->(fc)" + " CREATE (c1)-[:COLLECTS]->(ahx)"
				+ " CREATE (c2:Collector {userid:1002, name:\"Collector Two\"})" + " CREATE (c2)-[:COLLECTS]->(tsr)"
				+ " CREATE (c2)-[:COLLECTS]->(tgm)" + " CREATE (c2)-[:COLLECTS]->(m)"
				+ " CREATE (c3:Collector {userid:1003, name:\"Collector Three\"})" + " CREATE (c3)-[:COLLECTS]->(tsr)"
				+ " CREATE (c3)-[:COLLECTS]->(lc)" + " CREATE (c3)-[:COLLECTS]->(bts)"
				+ " CREATE (c4:Collector {userid:1004, name:\"Collector Four\"})" + " CREATE (c4)-[:COLLECTS]->(tsr)"
				+ " CREATE (c4)-[:COLLECTS]->(lv)" + " CREATE (c4)-[:COLLECTS]->(w)"
				+ " CREATE (c5:Collector {userid:1005, name:\"Collector Five\"})" + " CREATE (c5)-[:COLLECTS]->(tsr)"
				+ " CREATE (c5)-[:COLLECTS]->(fc)" + " CREATE (c5)-[:COLLECTS]->(ttyd)"
				+ " CREATE (c6:Collector {userid:1006, name:\"Collector Six\"})" + " CREATE (c6)-[:COLLECTS]->(tsr)"
				+ " CREATE (c6)-[:COLLECTS]->(tbv)" + " CREATE (c6)-[:COLLECTS]->(lc)"
				+ " CREATE (c7:Collector {userid:1007, name:\"Collector Seven\"})" + " CREATE (c7)-[:COLLECTS]->(tsr)"
				+ " CREATE (c7)-[:COLLECTS]->(bts)" + " CREATE (c7)-[:COLLECTS]->(ahx)"
				+ " CREATE (c8:Collector {userid:1008, name:\"Collector Eight\"})" + " CREATE (c8)-[:COLLECTS]->(tsr)"
				+ " CREATE (c8)-[:COLLECTS]->(fc)" + " CREATE (c8)-[:COLLECTS]->(ahx)"
				+ " CREATE (c9:Collector {userid:1009, name:\"Collector Nine\"})" + " CREATE (c9)-[:COLLECTS]->(tsr)"
				+ " CREATE (c9)-[:COLLECTS]->(lc)" + " CREATE (c9)-[:COLLECTS]->(bts)"
				+ " CREATE (c10:Collector {userid:1010, name:\"Collector Ten\"})" + " CREATE (c10)-[:COLLECTS]->(tsr)"
				+ " CREATE (c10)-[:COLLECTS]->(fc)" + " CREATE (c10)-[:COLLECTS]->(lv)"
				+ " CREATE (c11:Collector {userid:1011, name:\"Collector Eleven\"})"
				+ " CREATE (c11)-[:COLLECTS]->(tsr)" + " CREATE (c11)-[:COLLECTS]->(tgm)"
				+ " CREATE (c11)-[:COLLECTS]->(ttyd)"

				// Create FOLLOW Relations between Collectors
				+ " CREATE (c1)-[:FOLLOWS]->(c2)" + " CREATE (c2)-[:FOLLOWS]->(c3)" + " CREATE (c2)-[:FOLLOWS]->(c4)"
				+ " CREATE (c3)-[:FOLLOWS]->(c4)" + " CREATE (c3)-[:FOLLOWS]->(c5)" + " CREATE (c3)-[:FOLLOWS]->(c6)"
				+ " CREATE (c4)-[:FOLLOWS]->(c5)" + " CREATE (c4)-[:FOLLOWS]->(c6)" + " CREATE (c4)-[:FOLLOWS]->(c7)"
				+ " CREATE (c5)-[:FOLLOWS]->(c6)" + " CREATE (c6)-[:FOLLOWS]->(c7)" + " CREATE (c6)-[:FOLLOWS]->(c8)"
				+ " CREATE (c7)-[:FOLLOWS]->(c8)" + " CREATE (c7)-[:FOLLOWS]->(c9)" + " CREATE (c7)-[:FOLLOWS]->(c10)"
				+ " CREATE (c8)-[:FOLLOWS]->(c1)" + " CREATE (c8)-[:FOLLOWS]->(c2)" + " CREATE (c9)-[:FOLLOWS]->(c3)"
				+ " CREATE (c10)-[:FOLLOWS]->(c4)" + " CREATE (c10)-[:FOLLOWS]->(c5)" + " CREATE (c10)-[:FOLLOWS]->(c6)"
				+ " CREATE (c10)-[:FOLLOWS]->(c7)" + " CREATE (c10)-[:FOLLOWS]->(c11)"
				+ " CREATE (c11)-[:FOLLOWS]->(c9)" + " CREATE (c11)-[:FOLLOWS]->(c10)");

		System.out.println("DB initialized");
	}
	/*
	 * 
	START n=(Collector {userid:"1001"})
MATCH (n)-[r:FOLLOWS*0..2]-(m)
WITH n, m, reduce(s = '', rel IN r | s + rel.name + ',') as rels
RETURN n.name, m.name, rels;
	 */
	
	public String sql6() {
//		ExecutionEngine execEngine = new ExecutionEngine(graphDb,StringLogger.SYSTEM_DEBUG );
//		ExecutionResult execResult = execEngine.execute("MATCH (m)-[r:FOLLOWS*0..3]-(n:Collector {userid:1001}) WITH n, m, reduce(s = '', rel IN r | s + rel.name + ',') as rels RETURN DISTINCT n.name, m.name, rels;");
//		String results = execResult.dumpToString();
//		System.out.println(results);
//	      results = results.replace("\r\n"," <br> ");
	      
	      StringBuilder sb = new StringBuilder();
	        try ( Transaction tx = graphDb.beginTx() )
	        {
				for(Node node: graphDb.findNodesByLabelAndProperty(COLLECTOR, "userid", "1001")){
					Traverser friendsTraverser = getFollowsOut(node);
					for(Path friendPath : friendsTraverser){
						Node nodew = friendPath.endNode();
						sb.append(nodew.getProperty("fullname")+"<br>");
					}
					
				}
		  	    return  sb.toString();
	        }
	}
	private Traverser getActedIn(
            final Node person )
    {
        TraversalDescription td = graphDb.traversalDescription()
                .breadthFirst()
                .relationships( RelTypes.ACTED_IN, Direction.OUTGOING )
                .evaluator( Evaluators.excludeStartPosition() );
        return td.traverse( person );
    }
    
    private Traverser getCollectIn(
            final Node person )
    {
        TraversalDescription td = graphDb.traversalDescription()
                .breadthFirst()
                .relationships( RelTypes.COLLECTS, Direction.INCOMING)
                .evaluator( Evaluators.excludeStartPosition() );
        return td.traverse( person );
    }
    
    private Traverser getFollowsOut(
            final Node person )
    {
        TraversalDescription td = graphDb.traversalDescription()
                .breadthFirst()
                .relationships( RelTypes.FOLLOWS, Direction.OUTGOING)
                .evaluator( Evaluators.excludeStartPosition())
                .evaluator(Evaluators.toDepth(2));
        return td.traverse( person );
    }
}
