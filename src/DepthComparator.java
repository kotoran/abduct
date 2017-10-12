import java.util.*;  
class DepthComparator implements Comparator<GameObject>{  
public int compare(GameObject g1,GameObject g2){  
if(g1.depth==g2.depth)  
return 0;  
else if(g1.depth<g2.depth)  
return 1;  
else  
return -1;  
}  
}  

