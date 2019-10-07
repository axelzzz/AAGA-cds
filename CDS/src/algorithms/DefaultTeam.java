package algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class DefaultTeam {
	
	
	
	public boolean contains(ArrayList<Edge> edges, Point p1, Point p2) {		 
		for(Edge e:edges) {
			if((e.getP1()==p1 && e.getP2()==p2) || (e.getP2()==p1 && e.getP1()==p2)) {
				return true;
			}			
		}
		return false;
	}
	
	
	public boolean contains2(ArrayList<WEdge> wedges, Point p1, Point p2) {		 
		for(WEdge e:wedges) {
			if((e.getP1()==p1 && e.getP2()==p2) || (e.getP2()==p1 && e.getP1()==p2)) {
				return true;
			}			
		}
		return false;
	}
	
	
	//pour obtenir les aretes candidates en prenant en compte le seuil
	  public ArrayList<Edge> getTousEdges(ArrayList<Point> points, int edgeThreshold){ 
		  ArrayList<Edge> res = new ArrayList<Edge>();		 
		  for(int i = 0 ; i < points.size() ; i++){
			  Point pi = points.get(i);
			  for(int j = 0 ; j < points.size() ; j++){	
				  Point pj = points.get(j);
				  if(pi.equals(pj) || contains(res, pi, pj) )					  
					  continue;	
				  if(pi.distance(pj) <= edgeThreshold)
					  res.add(new Edge(pi, pj));				  		
			  }			  
		  }
		  System.out.println("ENDDDD");
		  return res;
	  }
	  
	  
	//calcule tableau de distances en fct de edgeThreshold
	public double[][] distances(ArrayList<Point> points, int edgeThreshold){
		
			double[][] res=new double[points.size()][points.size()];
			
			for(int i=0 ; i<points.size() ; i++){
				for(int j=0 ; j<points.size() ; j++){				
					Point pi = points.get(i);
					Point pj = points.get(j);				
					double dist = pi.distance(pj);
					if(dist < edgeThreshold)res[i][j]=dist;
					else{
						res[i][j]=Double.POSITIVE_INFINITY;
					}				
				}
			}			
			return res;
		}
		
		
	
	public ArrayList<WEdge> graphK(ArrayList<Point> points, int edgeThreshold, ArrayList<Point> hitPoints){  		  
		 
	      double[][] dist = distances(points, edgeThreshold);
		  ArrayList<WEdge> grapheK = new ArrayList<>();
		  
		  for(Point u:hitPoints){
			  for(Point v:hitPoints){
				  if(u.equals(v)) continue;					 
				  grapheK.add(new WEdge(u, v, dist[points.indexOf(u)][points.indexOf(v)]));				  
			  }
		  }		  
		  return grapheK;		  
	  }
	
		
	public ArrayList<Point> fromWEdgesToPoints(ArrayList<WEdge> wedges){		  
			 
		ArrayList<Point> res = new ArrayList<Point>();
			  
		for(WEdge e:wedges) {
			
			if(res.contains(e.getP1()) ){
				if(res.contains(e.getP2()) ) continue;
				res.add(e.getP2());				  
			}
			else {
				res.add(e.getP1());	
			}			  		  
		}
		
		return res;	  
	}
		 
		 
		
		
		
	public ArrayList<WEdge> trierWEdges(ArrayList<WEdge> wedges){  
			  
		ArrayList<WEdge> tmp = (ArrayList<WEdge>) wedges.clone();
			  
		ArrayList<WEdge> res = new ArrayList<WEdge>();		  
			  
		while(tmp.size()!=0) {
			
			double poidsMin = Double.MAX_VALUE;
			WEdge vectMin = null;
			
			for(WEdge ed:tmp){				  
				double poids = ed.getDistance();
				if (poidsMin > poids){
					poidsMin = poids;
					vectMin = ed;
				}				  
			}
			
			tmp.remove(vectMin);
			res.add(vectMin);	  			  
		}	
		
		return res;
	}
		 
		 
		 
		private Tree2D wedgesToTree(ArrayList<WEdge> wedges, Point root) { //pour exo 2
			    ArrayList<WEdge> remainder = new ArrayList<WEdge>();
			    ArrayList<Point> subTreeRoots = new ArrayList<Point>();
			    WEdge current;
			    //int i = 0;
			    while (wedges.size()!=0) {
			    	//System.out.println("i "+i);
			    	//i++;
			      current = wedges.remove(0);
			      //System.out.println("etiquette p1 : "+current.getP1().getEtiquette()+" etiquette p2 : "+current.getP2().getEtiquette());
			      if (current.getP1().equals(root)) {
			        subTreeRoots.add(current.getP2());
			      } else {
			        if (current.getP2().equals(root)) {
			          subTreeRoots.add(current.getP1());
			        } else {
			          remainder.add(current);
			        }
			      }
			    }
			    
			    ArrayList<Tree2D> subTrees = new ArrayList<Tree2D>();
			    for (Point subTreeRoot: subTreeRoots) subTrees.add(wedgesToTree((ArrayList<WEdge>)remainder.clone(),subTreeRoot));

			    return new Tree2D(root, subTrees);
		}
		 
		 
		 
		public Tree2D kruskal2(ArrayList<WEdge> wedges) {		  
			  
			  	ArrayList<Point> points = fromWEdgesToPoints(wedges);	  	
				ArrayList<WEdge> aretes = (ArrayList<WEdge>) wedges.clone();
				
				ArrayList<WEdge> candidatesTriees = new ArrayList<WEdge>();
				candidatesTriees = trierWEdges(aretes);
				ArrayList<WEdge> solution = new ArrayList<WEdge>();		

			    WEdge current;
			    NameTag forest = new NameTag(points);
			    while (candidatesTriees.size()!=0) {
			      current = candidatesTriees.remove(0);
			      if (forest.tag(current.getP1())!=forest.tag(current.getP2())) {
			        solution.add(current);
			        forest.reTag(forest.tag(current.getP1()),forest.tag(current.getP2()));
			      }
			    }
			    return wedgesToTree(solution,solution.get(0).getP1());	    
		 	}
			
		public ArrayList<WEdge> fromTreeToWEdges(Tree2D tree){ 		 

				 ArrayList<WEdge> res = new ArrayList<>();
				 Point root = tree.getRoot();
				 for(Tree2D t:tree.getSubTrees()) {
					 res.add(new WEdge(root, t.getRoot(), root.distance(t.getRoot())));
					 res.addAll(fromTreeToWEdges(t));
				 }
				 return res;
				
			  }
			
			
			
		public int[][] calculShortestPaths(ArrayList<Point> points, int edgeThreshold) {
			    int[][] paths= new int[points.size()][points.size()];
			    double[][] dist = distances(points, edgeThreshold);
			    
			    for(int i=0 ; i < paths.length ; i++){
			    	for(int j=0 ; j< paths.length ;j++){
			    		
			    		paths[i][j]=j; //dire qu'a l'init le plus court chemin de i a j est directement de i a j
			    	}
			    }	    
			    for (int k=0 ; k<paths.length ; k++){ 
			    	for (int i=0 ; i<paths.length ; i++){     		
			    		for (int j=0 ; j<paths.length ; j++){ 
			    			if(dist[i][j] > (dist[i][k] + dist[k][j]) ) {
			    				//System.out.println("DANS IF DE SHORTESTP");
			    				paths[i][j]=paths[i][k];
			    				//System.out.println("i : "+i+" j : "+j+" paths[i][j] : "+paths[i][j]);
			    				dist[i][j]=dist[i][k]+dist[j][k];	
			    				//System.out.println("i : "+i+" j : "+j+" dist[i][j] : "+dist[i][j]);
			    			}
			    		}
			    	}
			    	//System.out.println("k : "+k);
			    }
			    //return dist;
			    return paths;
			  }
			
			
		public ArrayList<WEdge> fromTreeToPathsOfG(Tree2D tree, ArrayList<Point> pointsDeG, int edgeThreshold){ 
			 ArrayList<WEdge> arbreDeK = fromTreeToWEdges(tree);
			 ArrayList<WEdge> graphH = new ArrayList<>();
			
			 int[][] t = calculShortestPaths(pointsDeG, edgeThreshold);	

			 for(WEdge we:arbreDeK) {
				Point from = we.getP1();
				Point to = we.getP2();				 
				Point next = pointsDeG.get(t[pointsDeG.indexOf(from)][pointsDeG.indexOf(to)]);
					 
				ArrayList<Point> cheminFromTo = new ArrayList<>();
				cheminFromTo.add(from);
				while(!from.equals(to)) {
					
					cheminFromTo.add(next);					 
					from = next;
					next = pointsDeG.get(t[pointsDeG.indexOf(from)][pointsDeG.indexOf(to)]);					 
				}
				
				cheminFromTo.add(to);
				//System.out.println("taille du chemin "+cheminFromTo.size());
					 
				for(int i=0 ; i<cheminFromTo.size() -1 ; i++) {
					Point pi = cheminFromTo.get(i);
					Point piPlusUn = cheminFromTo.get(i+1);
					graphH.add(new WEdge(pi, piPlusUn, pi.distance(piPlusUn)));
				}				 
			}	
			 
			 return graphH;
		 }	
		
	
		
		public Tree2D calculSteiner(ArrayList<Point> points, int edgeThreshold, ArrayList<Point> hitPoints) {
		    
			  //etape 1 init graphK
			  ArrayList<WEdge> grapheK = graphK(points, edgeThreshold, hitPoints);	

			  //etape 2 kruskal(K)
			  Tree2D t = kruskal2(grapheK);	  
			  
			  //etape 3 H = traduire T en chemins dans G	 
			  ArrayList<WEdge> grapheH = fromTreeToPathsOfG(t, points, edgeThreshold);
			  
			  //etape 4
			  Tree2D steinerTree = kruskal2(grapheH); 
			  
			  //etape 5
			  return steinerTree;
		  }
		
	
		
		public ArrayList<Point> thePath(WEdge e,List<Point> noDoublon,int [][] pa){
			ArrayList<Point> res = new ArrayList<Point>();
			Point tmp = e.getP1();
			
	 		while(!tmp.equals(e.getP2())){
	 			res.add(tmp);
				int i = noDoublon.indexOf(tmp);
				int j = noDoublon.indexOf(e.getP2());
				tmp = noDoublon.get(pa[i][j]);
	 		}
	 		res.add(e.getP2());
	 		return res;
		}
		
		
		
		public ArrayList<Point> calculSteiner2(ArrayList<Point> points, ArrayList<Point> allPoints, int edgeThreshold) {

			int [][] distPath = calculShortestPaths(allPoints, edgeThreshold);
		
			ArrayList<WEdge> graphK = graphK(allPoints, edgeThreshold, points);		
			
			ArrayList<WEdge> kruskalK = fromTreeToWEdges(kruskal2(graphK));
			
			ArrayList<Point> steiner = new ArrayList<Point>();
			
			for(int i = 0 ; i < kruskalK.size() ; i++){
				WEdge e = kruskalK.get(i);
				ArrayList<Point> p = thePath(e,allPoints,distPath);
				for(Point p1 : p)
					if(!steiner.contains(p1)) 
						steiner.add(p1);				
			}
			return steiner;
		}
		
		public boolean estVoisin(Point a, Point b,  int edgeThreshold) {
			
			if(a.distance(b)<=edgeThreshold)return true;
			return false;		
		}
		
		public boolean estVoisinDauMoinsN(Point a,  ArrayList<Point> points, int edgeThreshold, int n) {
			
			int cpt = 0;
			for(Point p:points) {
				if(a.equals(p))continue;
				if(estVoisin(a, p, edgeThreshold))cpt++;
			}
			if(cpt >= n)return true;
			return false;		
		}
		
		public boolean estVoisinDePersonne(Point a, ArrayList<Point> points, int edgeThreshold) {
			for(Point p:points) {
				if(a.distance(p) <= edgeThreshold)return false;
			}
			return true;
		}
		
		
		
		public ArrayList<Point> getStable1(ArrayList<Point> points, int edgeThreshold){
			
			ArrayList<Point> res = new ArrayList<>();		
			
			res.add(points.get(0));
			for(Point p:points) {			
				if(estVoisinDePersonne(p, res, edgeThreshold))res.add(p);		
			}		
			return res;		
		}
		
		
		
		
		public ArrayList<Point> stable2(ArrayList<Point> points, int edgeThreshold){
			Random r = new Random();
			ArrayList<Point> res = new ArrayList<>();
			int rand = r.nextInt(points.size());
			//System.out.println("R "+rand);
			res.add(points.get(rand));
			for(Point p:points) {			
				if(estVoisinDePersonne(p, res, edgeThreshold))res.add(p);		
			}		
			return res;
		}
		
		
		
		/*
		public ArrayList<Point> stable2(ArrayList<Point> points, int edgeThreshold){
			
			ArrayList<Point> res = new ArrayList<>();
			
			Random r = new Random();			
			int rand = r.nextInt(points.size());
			
			Point prand = points.remove(rand);
			res.add(prand);
			
			for(Point p:points) {			
				if(estVoisinDePersonne(p, res, edgeThreshold))res.add(p);
				else
					points.add(prand);
			}		
			return res;
		}
		*/
		
		
		public ArrayList<Point> getStable2(ArrayList<Point> points, int edgeThreshold){
			ArrayList<Point> stable = stable2(points, edgeThreshold);
			int cpt=0;
			while(cpt < 500 && points.size() > 0) {
				System.out.println("nb pts "+points.size());
				ArrayList<Point> tmp = stable2(points, edgeThreshold);
				if(tmp.size() > stable.size())stable = tmp;
				cpt++;
			}
			return stable;		
		}
		
		
		
		public HashMap<Point, Color> marquageInit(ArrayList<Point> udg, ArrayList<Point> mis){
			
			HashMap<Point, Color> res = new HashMap<>();		
			for(Point p:udg) {			
				if(mis.contains(p)) 
					res.put(p, Color.BLACK);			
				else 
					res.put(p, Color.GREY);				
			}		
			return res;
		}
		
		
		
		public ArrayList<Point> getGreyNodes(HashMap<Point, Color> map){
			ArrayList<Point> greyNodes = new ArrayList<>();
			
			for(Entry<Point, Color> entry : map.entrySet()) {
				if(entry.getValue() == Color.GREY)greyNodes.add(entry.getKey());
			}
			return greyNodes;    
		}
		
		
		
		
		//calcule les noeuds gris qui sont voisins d'aucun noeuds gris
		//on en trouve 2 dans le input points de base
		public ArrayList<Point> noeudsGrisNonConnectesEntreEux(HashMap<Point, Color> map, int edgeThreshold){
			ArrayList<Point> greyNodes = new ArrayList<>();
			
			for(Entry<Point, Color> entry : map.entrySet()) {
				if(entry.getValue() == Color.GREY) {
					Point p1 = entry.getKey();
					boolean isConnectedToOtherGrey = false;
					for(Entry<Point, Color> entry2 : map.entrySet()) {
						Point p2 = entry2.getKey();
						if(entry2.getValue() == Color.GREY && (!p1.equals(p2))) {						
							if(p1.distance(p2) <= edgeThreshold)isConnectedToOtherGrey = true;
						}					
					}
					if(!(isConnectedToOtherGrey))greyNodes.add(p1);
				}				
			}
			return greyNodes;    
		}
		
		
		
		public ArrayList<Point> getBlackNodes(HashMap<Point, Color> map){
			ArrayList<Point> blackNodes = new ArrayList<>();
			
			for(Entry<Point, Color> entry : map.entrySet()) {
				if(entry.getValue() == Color.BLACK)blackNodes.add(entry.getKey());
			}
			return blackNodes;    
		}
		
		
		
		public boolean existeGreyNodeAdjToIBlackNodes(HashMap<Point, Color> map, int edgeThreshold, int i) {
			ArrayList<Point> g = getGreyNodes(map);
			ArrayList<Point> b = getBlackNodes(map);
			
			for(Point p:g) {
				if(estVoisinDauMoinsN(p, b, edgeThreshold, i)) { 
					map.put(p, Color.BLUE);
					return true;
				}
			}
			return false;		
		}
		
		
		
		public ArrayList<Point> getBlueNodes(HashMap<Point, Color> map){
			ArrayList<Point> blueNodes = new ArrayList<>();
			
			for(Entry<Point, Color> entry : map.entrySet()) {
				if(entry.getValue() == Color.BLUE)blueNodes.add(entry.getKey());
			}
			return blueNodes;    
		}
		
		
		
		//recuperer les noeuds noirs d'un composant
		public ArrayList<Point> getBlackNodesFromTheBBComp(HashMap<Point, Color> map, ArrayList<Point> comp) {
			
			ArrayList<Point> res = new ArrayList<Point>();
			
			for(Entry<Point, Color> entry : map.entrySet()) {
				Point p = entry.getKey();
				//on parcourt les dominateurs, si ils font partie du composant on les retourne
				if(entry.getValue() == Color.BLACK) {				
					if(comp.contains(p)) res.add(p);				
				}
			}
			return res;			
		}	
		
		
		
		public Color getColorFromPoint(HashMap<Point, Color> map, Point p) {
			
			for(Entry<Point, Color> entry : map.entrySet()) {
				if(p.equals(entry.getKey()))return entry.getValue();
			}
			return null;
		}

		
		
		public boolean estVoisinPointComp(ArrayList<Point> comp, Point p, int edgeThreshold, HashMap<Point, Color> map){
			
			//si le point est voisin d'un blacknode du composant, alors il est voisin du composant, on fusionne le point dans le composant
			ArrayList<Point> blacknodes = getBlackNodesFromTheBBComp(map, comp);
			
			for(Point b:blacknodes) {
				if(b.distance(p) <= edgeThreshold) {
					//on ajoute le point dans le composant (fusion)
					comp.add(p);
					return true;
				}
			}
			return false;
		}
		
		
		
		public boolean estPointVoisinDauMoinsIComposants(ArrayList<ArrayList<Point>> comps , Point p, int n, int edgeThreshold , HashMap<Point, Color> map){
			
			int tmp = n;
			
			for(ArrayList<Point> comp : comps)			
				if(estVoisinPointComp(comp, p, edgeThreshold, map))tmp--;		
			
			if(tmp > 0)return false;
			return true;
		}
		
		
		
		public int nbBlueNodes(HashMap<Point, Color> map) {
			int sum=0;
			for(Entry<Point, Color> entry : map.entrySet())if(entry.getValue() == Color.BLUE)sum++;
			return sum;
		}
		
		
		
		public int nbGreyNodes(HashMap<Point, Color> map) {
			int sum=0;
			for(Entry<Point, Color> entry : map.entrySet())if(entry.getValue() == Color.GREY)sum++;
			return sum;
		}
		
		
		
		public ArrayList<Point> getBBCompOfTheBlackNode(HashMap<Point, Color> map,  Point p, ArrayList<ArrayList<Point>> comps, int edgeThreshold){
			
			if(map.get(p) == Color.BLACK) {
				for(ArrayList<Point> comp : comps) {
					if(comp.contains(p))return comp;
				}
			}
			return null;
		}
		
		
		
		
		//quand on cherche si le point en parametre (qui est normalement gris) est voisin d'au moins i black nodes
		public boolean estVoisinDeAtLeastIblackNodesDeDifferentsBBComp(HashMap<Point, Color> map,  Point p, int n, ArrayList<ArrayList<Point>> comps, int edgeThreshold) {		
					
			int tmp = 0;
					
			for(ArrayList<Point> comp : comps) {
						
				if(estVoisinPointComp(comp, p, edgeThreshold, map)){
					tmp++;
					if(tmp >= n)return true;
				}
			}	
			return false;		
		}
				
				
			
		public ArrayList<ArrayList<Point>> getCompVoisinsOfPoint(Point p, HashMap<Point, Color> map, int edgeThreshold, ArrayList<ArrayList<Point>> comps){
				
			ArrayList<ArrayList<Point>> voisins = new ArrayList<>();
				
			for(ArrayList<Point> comp:comps) {
				if(estVoisinPointComp(comp, p, edgeThreshold, map))voisins.add(comp);
			}
				
			return voisins;		
		}

		
		
		public boolean existGreyNodeAdjToAtLeastIBlackNodeInDifferentBBComp(HashMap<Point, Color> map, int edgeThreshold, ArrayList<ArrayList<Point>> comps, int n) {
			
			for(Entry<Point, Color> entry : map.entrySet()) {
				Point p = entry.getKey();
				Color c = entry.getValue();
				if(c == Color.GREY) {
					
					//si le point courant gris est voisin de n noeuds noirs de differents black/blue components, on le transforme en bleu, 
					//on fusionne les composants voisins et on ajoute le noeud courant au resultat de la fusion
					if(estVoisinDeAtLeastIblackNodesDeDifferentsBBComp(map, p, n, comps, edgeThreshold)) {
						//le point gris devient un bleu
						entry.setValue(Color.BLUE);	
						ArrayList<ArrayList<Point>> compsVoisins = getCompVoisinsOfPoint(p, map, edgeThreshold, comps);
						//on supprime les comps voisins de la liste des comps,  
						comps.removeAll(compsVoisins);
						ArrayList<Point> voisinsInOne = new ArrayList<>();
						//on les fusionne
						for(ArrayList<Point> compsv : compsVoisins) {
							voisinsInOne.addAll(compsv);
						}
						//puis ajoute le point,
						voisinsInOne.add(p);
						// puis le resultat de tout ca est reinjecte dans la liste des composants
						comps.add(voisinsInOne);
						return true;
					}				
				}
			}
			return false;
		}
		
		
		
		public ArrayList<Point> AlgoAv2(HashMap<Point, Color> map, int edgeThreshold){
			ArrayList<Point> res = new ArrayList<>();
			ArrayList<Point> dominants = getBlackNodes(map);
			ArrayList<ArrayList<Point>> components = new ArrayList<>();		
			
			//au depart chaque noeud de l'ensemble dominant est considere comme un composant bleu/noir connexe
			for(int i = 0 ; i < dominants.size() ; i++){
				
				components.add(new ArrayList<>());
				components.get(i).add(dominants.get(i));
			}			
			
			for(int i = 5 ; i >= 2 ; i--) {

				while(existGreyNodeAdjToAtLeastIBlackNodeInDifferentBBComp(map, edgeThreshold, components, i)) {
					//System.out.println("nb blue nodes "+nbBlueNodes(map));
					//System.out.println("nb grey nodes "+nbGreyNodes(map));
				}
			}
					
			for(Entry<Point, Color> entry : map.entrySet())if(entry.getValue() == Color.BLUE)res.add(entry.getKey());
			res.addAll(dominants);
			
			return res;
		}
		
		
		
		//on parcourt la liste des points, pour chaque point on cree une liste de ses voisins
		//si chaque point est voisin d'un autre alors tous les points sont atteignables, d'ou ils sont connexes
		public boolean estConnexe(ArrayList<Point> points, int edgeThreshold) {
			
			ArrayList<Point> toVisit = new ArrayList<>();
			ArrayList<Point> notVisited = new ArrayList<>(points);
			
			toVisit.add(notVisited.remove(0));
			
			while(toVisit.size() > 0) {
				
				Point tmp = toVisit.remove(0);
				ArrayList<Point> tmpL = new ArrayList<>();
				
				for(Point p : notVisited) {
					if(tmp.distance(p) <= edgeThreshold)tmpL.add(p);
				}
				
				//toVisit.remove(tmp);			
				toVisit.addAll(tmpL);
				notVisited.removeAll(tmpL);
				//System.out.println("dans while");
			}
			
			if(notVisited.size() == 0)return true;
			return false;		
			
		}	
		
		
		
		
		//chaque point de l'ensemble de depart est soit dans toTest soit un voisin d'au moins un point de toTest
		public boolean estStable(ArrayList<Point> points, ArrayList<Point> toTest, int edgeThreshold){
	        int cpt=0;

	        for(Point p: points){
	            for(Point q : toTest){
	                if( !p.equals(q) && p.distance(q) < edgeThreshold)
	                    cpt ++;
	            }
	            if(cpt == 0)
	                return false;
	            cpt =0;
	        }

	        return true;
	    }
		
		
		
		
		/*
		 public boolean isDominant(ArrayList<Point> origins,
					ArrayList<Point> toTest,
					int edgeThreshold) {

			ArrayList<Point> clone = (ArrayList<Point>) origins.clone();
			
			for(int i = 0 ; i < toTest.size() ; i++) {
				Point p = toTest.get(i);
				clone.remove(p);
				clone.removeAll(neighbor(p, origins, edgeThreshold));
			}
			
			return clone.size() == 0;
		}
		 
		 */
		
		
		//heuristique de suppression
		//on parcourt la liste, si le point courant n'est pas un dominant et que si le graphe reste connexe en supprimant le point, on le supprime sinon on passe au suivant
		public ArrayList<Point> heuristique1(ArrayList<Point> tree, ArrayList<Point> points, int edgeThreshold){
			
			ArrayList<Point> res = new ArrayList<Point>(tree);
			
			//parcours dans l'ordre de la liste
			for(Point p:tree) {
				//System.out.println("ICI");
				ArrayList<Point> tmp = new ArrayList<>(tree);
					
				tmp.remove(p);
				//si le graphe avec suppression du point courant est connexe et tjr un ens dominant, on maj le graphe
				if(estConnexe(tmp, edgeThreshold) && estStable(points, tmp, edgeThreshold)) {
					//System.out.println("estCONNEXE");
					res = tmp;				
				}						
			}
			
			return res;		
		}
		
		
		
		public ArrayList<Point> heuristique1AvecBoucle(ArrayList<Point> tree,ArrayList<Point> points, int edgeThreshold){
			int i = 0;
			int nbloopMaxUseful = 0;
			while(i < 100) {
				ArrayList<Point> tmp = heuristique1(tree, points, edgeThreshold);
				if(tmp.size() < tree.size()) {
					tree = tmp;
					nbloopMaxUseful++;
				}
				i++;
			}
			System.out.println(nbloopMaxUseful);
			return tree;
		}
		
		
		
		//on calcule de nouveaux graphes en parcourant de maniere random et on garde le meilleur res
		public ArrayList<Point> heuristique1AvecOrdreDifferent(ArrayList<Point> tree, ArrayList<Point> points, int edgeThreshold){
			
			ArrayList<Integer> randomUsed = new ArrayList<Integer>();
			ArrayList<Point> res = new ArrayList<Point>(tree);
			
			//parcours dans l'ordre de la liste
			for(Point p:tree) {
				//System.out.println("ICI");
				ArrayList<Point> tmp = new ArrayList<>(tree);
				Random r = new Random();
				Integer rand = (Integer)r.nextInt(tree.size() - 1);
				while(randomUsed.contains(randomUsed.get(rand.intValue()))) {
					rand = (Integer)r.nextInt(tree.size() - 1);
					System.out.println("dans while");
				}
				randomUsed.add(rand);
				tmp.remove(rand.intValue());
				//si le graphe avec suppression du point courant est connexe et tjr un ens dominant, on maj le graphe
				if(estConnexe(tmp, edgeThreshold) && estStable(points, tmp, edgeThreshold)) {
					//System.out.println("estCONNEXE");
					res = tmp;				
				}						
			}
			
			return res;	
		}
		
		
		
		public ArrayList<Point> heuristique1AvecOrdreDifferentAvecBoucle(ArrayList<Point> tree,ArrayList<Point> points, int edgeThreshold){
			int i = 0;
			int nbloopMaxUseful = 0;
			while(i < 200) {
				ArrayList<Point> tmp = heuristique1AvecOrdreDifferent(tree, points, edgeThreshold);
				if(tmp.size() < tree.size()) {
					tree = tmp;
					nbloopMaxUseful++;
				}
				i++;
			}
			System.out.println(nbloopMaxUseful);
			return tree;
		}
		
		
		
		//local search
		//on parcourt le graphe, si on peut remplacer 2 points par 1, on le fait
		public ArrayList<Point> localSearch1(ArrayList<Point> points, int edgeThreshold){
			return null;
		}
		
		
		
		
		
  public ArrayList<Point> calculConnectedDominatingSet(ArrayList<Point> points, int edgeThreshold) {
	//etape 1 calcul MIS, (on peut tenter de maximiser sa taille avec getStable2)
	  //ArrayList<Point> stable = stable2(points, edgeThreshold);
		ArrayList<Point> stable = getStable2(points, edgeThreshold);
		
	//etape 2 marquage
		HashMap<Point, Color> mark = marquageInit(points, stable);	
		
	//etape 3 calcul CDS	
		//ArrayList<Point> result = calculSteiner2(stable, points, edgeThreshold);;
		ArrayList<Point> result = AlgoAv2(mark, edgeThreshold);

		//result = fromWEdgesToPoints(fromTreeToWEdges(calculSteiner(points, edgeThreshold, stable))); 
	 
		//return result;
	    //return heuristique1(result, points, edgeThreshold);
		//return stable;
		
		return heuristique1AvecBoucle(result, points, edgeThreshold);
		//return heuristique1AvecOrdreDifferent(result, points, edgeThreshold);
		//return heuristique1AvecOrdreDifferentAvecBoucle(result, points, edgeThreshold);
		//return stable;
  }
  
  
  //FILE PRINTER
  private void saveToFile(String filename,ArrayList<Point> result){
    int index=0;
    try {
      while(true){
        BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(filename+Integer.toString(index)+".points")));
        try {
          input.close();
        } catch (IOException e) {
          System.err.println("I/O exception: unable to close "+filename+Integer.toString(index)+".points");
        }
        index++;
      }
    } catch (FileNotFoundException e) {
      printToFile(filename+Integer.toString(index)+".points",result);
    }
  }
  private void printToFile(String filename,ArrayList<Point> points){
    try {
      PrintStream output = new PrintStream(new FileOutputStream(filename));
      int x,y;
      for (Point p:points) output.println(Integer.toString((int)p.getX())+" "+Integer.toString((int)p.getY()));
      output.close();
    } catch (FileNotFoundException e) {
      System.err.println("I/O exception: unable to create "+filename);
    }
  }

  //FILE LOADER
  private ArrayList<Point> readFromFile(String filename) {
    String line;
    String[] coordinates;
    ArrayList<Point> points=new ArrayList<Point>();
    try {
      BufferedReader input = new BufferedReader(
          new InputStreamReader(new FileInputStream(filename))
          );
      try {
        while ((line=input.readLine())!=null) {
          coordinates=line.split("\\s+");
          points.add(new Point(Integer.parseInt(coordinates[0]),
                Integer.parseInt(coordinates[1])));
        }
      } catch (IOException e) {
        System.err.println("Exception: interrupted I/O.");
      } finally {
        try {
          input.close();
        } catch (IOException e) {
          System.err.println("I/O exception: unable to close "+filename);
        }
      }
    } catch (FileNotFoundException e) {
      System.err.println("Input file not found.");
    }
    return points;
  }
}