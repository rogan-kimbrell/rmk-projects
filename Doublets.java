
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Arrays;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Iterator;

import java.util.stream.Collectors;

/**
 * Provides an implementation of the WordLadderGame interface. 
 *
 * @author Your Name (you@auburn.edu)
 */
public class Doublets{

    // The word list used to validate words.
    // Must be instantiated and populated in the constructor.
    /////////////////////////////////////////////////////////////////////////////
    // DECLARE A FIELD NAMED lexicon HERE. THIS FIELD IS USED TO STORE ALL THE //
    // WORDS IN THE WORD LIST. YOU CAN CREATE YOUR OWN COLLECTION FOR THIS     //
    // PURPOSE OF YOU CAN USE ONE OF THE JCF COLLECTIONS. SUGGESTED CHOICES    //
    // ARE TreeSet (a red-black tree) OR HashSet (a closed addressed hash      //
    // table with chaining).
    /////////////////////////////////////////////////////////////////////////////
    HashSet<String> lexicon = new HashSet<String>();
    HashSet<String> sameLengthLexicon = new HashSet<>();

    /**
     * Instantiates a new instance of Doublets with the lexicon populated with
     * the strings in the provided InputStream. The InputStream can be formatted
     * in different ways as long as the first string on each line is a word to be
     * stored in the lexicon.
     */
    public Doublets(InputStream in) {
        try {
            //////////////////////////////////////
            // INSTANTIATE lexicon OBJECT HERE  //
            //////////////////////////////////////

            Scanner s =
                new Scanner(new BufferedReader(new InputStreamReader(in)));
            while (s.hasNext()) {
                String str = s.next();
                /////////////////////////////////////////////////////////////
                // INSERT CODE HERE TO APPROPRIATELY STORE str IN lexicon. //
                 /////////////////////////////////////////////////////////////
                lexicon.add(str.toLowerCase());
                s.nextLine();
            }
            in.close();
        }
        catch (java.io.IOException e) {
            System.err.println("Error reading from InputStream.");
            System.exit(1);
        }
    }

    public int getWordCount(){
        return lexicon.size();
    }

    public boolean isWord(String str){
        return lexicon.contains(str);
    }

    public int getHammingDistance(String str1, String str2){

        if(str1.length() != str2.length()) return -1;

        char[] str1List = str1.toCharArray();
        char[] str2List = str2.toCharArray();
        int count = 0;

        for(int i = 0; i < str1List.length; i++){
            if(str1List[i] != str2List[i]) count++;
        }


        return count;
    }

    public List<String> getNeighbors(String word){
        int wordLength = word.length();
        ArrayList<String> neighbors = new ArrayList<>();
        
        if(sameLengthLexicon == null){
         for(String a : lexicon){
            if(a.length() == wordLength) sameLengthLexicon.add(a);
         }
        }

        for(String a : sameLengthLexicon){
            if(getHammingDistance(a, word) == 1) neighbors.add(a);
        }


        return neighbors;
    }

    public boolean isWordLadder(List<String> sequence){

        Iterator<String> itr = sequence.iterator();
        String prev = "";

        if(itr.hasNext()) prev = itr.next();
        else return false;

        while(itr.hasNext()){
            String curr = itr.next();
            if(getHammingDistance(curr, prev) != 1) return false;
            prev = curr;

            if(!isWord(prev)) return false;
        }
        return true;
    }

    public List<String> getMinLadder(String start, String end){

        if(start.length() != end.length()) return new ArrayList<>();
        if(!isWord(start) || !isWord(end)) return new ArrayList<>();
        if(start.equals(end)){
            ArrayList<String> path = new ArrayList<>();
            path.add(start);
            return path;
        }


        HashMap<String, Integer> distances = new HashMap<>();
        HashMap<String, String> prev = new HashMap<>();
        PriorityQueue<Node> queue = new PriorityQueue<Node>((a,b) -> a.distance - b.distance);
        queue.add(new Node(0, start));
        distances.put(start, 0);
        prev.put(start, null);
     
        while(!queue.isEmpty()){
            Node currentNode = queue.poll();
            int currentDistance = currentNode.distance;

            if(currentNode.val.equals(end)) break;

            if(currentDistance > distances.get(currentNode.val)) continue;

            for(String s : getNeighbors(currentNode.val)){

                int g = currentDistance + 1;
                int h = getHammingDistance(s, end);
                int f = g + h;
                
                if(!distances.containsKey(s)) distances.put(s, Integer.MAX_VALUE);
                if(g < distances.get(s)){
                    distances.put(s, g);
                    prev.put(s, currentNode.val);
                    queue.add(new Node(f, s));
                }
            }
        }

        if (!prev.containsKey(end)) return new ArrayList<>();

        ArrayList<String> path = new ArrayList<>();
        String current = end;


        while(current != null){
            path.add(0, current);
            current = prev.get(current);
        }

        return path;
    }

    private class Node{
        int distance;
        String val;

        public Node(int distance, String val){
            this.distance = distance;
            this.val = val;
        }
    }
}
