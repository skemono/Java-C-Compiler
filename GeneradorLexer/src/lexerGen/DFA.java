package lexerGen;

import lexerGen.util.State;
import java.util.*;

public class DFA {

    


    //Data structure for DFA
    private List<Set<State>> stateList = new ArrayList<>();

    private Map<Set<State>,Integer> stateIds = new HashMap<>();
    
    private Map<Integer,Map<Character,Integer>> transitions = new HashMap<>();
    
    private Map<Integer,String> accepting = new HashMap<>();
    private int startId = 0;


      //Main method to build the DFA from the NFA start state and the list of rules
    public void build(State nfaStart, List<String[]> rules){
        //Compute the epsilon closure of the NFA start state to get the initial DFA state and add it to the DFA
        Set<State> startSet = epsilonClosure(Collections.singleton(nfaStart));
        addState(startSet);
        //Queue for processing DFA states during construction
        Queue<Set<State>> worklist = new LinkedList<>();
        worklist.add(startSet);

        //Process each DFA state in the worklist until it is empty
        while (!worklist.isEmpty()){
            Set<State> T  = worklist.poll();
            int tId = stateIds.get(T);
            
            for(char c : NFA.ALPHABET){
                Set<State> moved = move (T,c);
                if(moved.isEmpty()){
                    continue;
                }
                Set<State> U = epsilonClosure(moved);
                if ( U.isEmpty()){
                    continue;
                }
                if(!stateIds.containsKey(U)){
                    addState(U);
                    worklist.add(U);

                }
                int uId = stateIds.get(U);
                transitions.computeIfAbsent(tId, k -> new HashMap<>()).put(c, uId);

            }


        }
        //After constructing the DFA, mark the accepting states based on the NFA states they represent and the provided rules
        markAccepting(rules);
    }

        //Helper metho to Add a State 
    private int addState(Set<State> stateSet){
        
        int id = stateIds.size();
        //Add the new state set to the list of states and map it to its ID
        stateList.add(stateSet);
        stateIds.put(stateSet, id);
        return id;
    }


    //Epsilon closure of a set of states

    private Set<State> epsilonClosure(Set<State> states){
        //Set to keep track of visited states
        Set<State> closure = new HashSet<>(states);
        //Queue for BFS
        Queue<State> queue = new LinkedList<>(states);
        
        //BFS to find all reachable states through epsilon transitions
        while(!queue.isEmpty()){
            //Get the next state from the queue
            State current = queue.poll();
            //For each epsilon transition from the current state
            for(State next : current.epsilonTransitions){
                //If the next state has not been visited, add it to the closure and queue
                if(closure.add(next)){
                    //If the state was added to the closure, it means it was not visited before, so we add it to the queue
                    queue.add(next);
                }
            }
        }
        return closure;
    }

    //Move method to find the set of states reachable from a set of states on a given input character
    private Set<State> move(Set<State> states, char symbol){
        //Set to keep track of reachable states
       Set<State> result = new HashSet<>();
       //For each state in the input set, find the states reachable on the given symbol and add them to the result set
       for(State s : states){
        //Get the set of states reachable from state s on the input symbol
        Set<State> targets = s.transitions.get(symbol);
        //If there are any reachable states, add them to the result set
        if(targets != null){
            result.addAll(targets);
        }
       } 
       return result;
    }

    
    private void markAccepting(List<String[]> rules){
        //Map to store the priority of each token based on the order of the rules
        Map<String,Integer> priority = new HashMap<>();
        //Assign priority to each token based on its position in the rules list
        for(int i = 0; i < rules.size(); i++){
            priority.put(rules.get(i)[1], i);
        }
        //For each state in the DFA, determine if it is an accepting state and if so, which token it accepts based on the highest priority token among the NFA states it represents
        for(int id = 0; id < stateList.size();id++){
            Set<State> stateSet = stateList.get(id);
            String bestToken = null;
            int bestPriority = Integer.MAX_VALUE;
            //For each NFA state in the DFA state set, check if it is an accepting state and if so, compare its token's priority with the best token found so far
            for ( State nfaState : stateSet){
                if(nfaState.tokenName != null){
                    Integer p = priority.get(nfaState.tokenName);
                    if(p != null && p < bestPriority ){
                        bestToken = nfaState.tokenName;
                        bestPriority = p;

                    }
                }
            }
            //If a best token was found, mark the DFA state as accepting for that token
            if(bestToken != null){
                accepting.put(id,bestToken);
            }
        }
    }
  

    //Getters for DFA components
    public Map<Integer, Map<Character, Integer>> getTransitions() { return transitions; }
    public Map<Integer, String> getAccepting() { return accepting; }
    public int getStartId() { return startId; }
    public int getStateCount() { return stateList.size(); }

}
