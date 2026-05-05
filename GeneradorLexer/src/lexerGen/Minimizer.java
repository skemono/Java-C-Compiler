package lexerGen;
import java.util.*;

public class Minimizer {
    //Data structures for minimization
    private List<Set<Integer>> partitions = new ArrayList<>();
    private int[] partitionOf;
    private Map<Integer,Map<Character,Integer>> minTransitions = new HashMap<>();
    private Map<Integer,String> minAccepting = new HashMap<>();
    private int minStartId;

    public void minimize (DFA dfa){
        //Initialize the partition of states and build the initial partition based on accepting and non-accepting states
        partitionOf = new int[dfa.getStateCount()];
        //Build the initial partition of states based on accepting and non-accepting states
        buildInitialPatition(dfa);
        //Refine the partition until it cannot be refined further
        refine(dfa);
        //Build the minimized DFA based on the final partition of states
        buildMinimizedDFA(dfa);

    }
    private void buildInitialPatition(DFA dfa){
        // Group states by their token (accepting) or as non-accepting.
        // States accepting different tokens can never be equivalent, so they
        // must start in separate partitions.
        Map<String, Set<Integer>> groups = new LinkedHashMap<>();

        for (int s = 0; s < dfa.getStateCount(); s++){
            String token = dfa.getAccepting().get(s);
            String key = (token != null) ? token : "__NON_ACCEPTING__";
            groups.computeIfAbsent(key, k -> new HashSet<>()).add(s);
        }

        for (Set<Integer> group : groups.values()){
            int pid = partitions.size();
            partitions.add(group);
            for (int s : group) partitionOf[s] = pid;
        }
    }
    //Helper method to split a partition based on the transitions of the states in the partition and the current partitioning
    private boolean splitPartition(int pid, DFA dfa){
        Set<Integer> P = partitions.get(pid);
        if (P.size() <= 1) return false;
        //Map to group states in the partition based on their transitions for each input character
        for(char c : NFA.ALPHABET){
            Map<Integer,Set<Integer>> subgroups = new LinkedHashMap<>();
            //Group states in the partition based on their transitions for the input character c

            for (int s : P){
                Map<Character, Integer> trans = dfa.getTransitions().get(s);

                int destPartition = -1;
                if(trans != null && trans.containsKey(c)){
                    destPartition = partitionOf[trans.get(c)];

                }
                subgroups.computeIfAbsent(destPartition, k -> new HashSet<>()).add(s);

            }
            //If the partition can be split into more than one subgroup based on the transitions, update the partitions and return true to indicate that a split occurred
            if(subgroups.size() > 1){
                List<Set<Integer>> newGroups = new ArrayList<>(subgroups.values());

                partitions.set(pid, newGroups.get(0));

                for (int s : newGroups.get(0))partitionOf[s] = pid;

                for(int i = 1; i < newGroups.size(); i++){

                    int newPid = partitions.size();
                    partitions.add(newGroups.get(i));
                    for (int s : newGroups.get(i)) partitionOf[s] = newPid;
                }
                return true;

            }

        }
        return false;

    }
    //Helper method to refine the partition of states until it cannot be refined further by splitting partitions based on their transitions and the current partitioning
    private void refine(DFA dfa){
        //Iteratively split partitions until no more splits can be made
        boolean changed = true;
        //For each partition, attempt to split it based on the transitions of the states in the partition and the current partitioning. If any partition is split, set changed to true to indicate that another iteration is needed to check for further splits.
        while(changed){
            changed = false;
            for (int pid = 0; pid < partitions.size(); pid++){
                if (splitPartition(pid, dfa)){
                    changed = true;
                    break;
                }
            }
        }

    }
    //Helper method to build the minimized DFA based on the final partition of states after refinement.
    //  Each partition becomes a single state in the minimized DFA, 
    // and transitions are defined based on the transitions of the representative state of each partition in the original DFA.

    private void buildMinimizedDFA(DFA dfa){
        //The start state of the minimized DFA is the partition that contains the start state of the original DFA

        minStartId = partitionOf[dfa.getStartId()];

        //For each partition, determine the transitions and accepting status of the corresponding state in the minimized DFA based on the representative state of the partition in the original DFA
        for (int pid = 0; pid < partitions.size(); pid++){
            //Get the representative state of the partition

            Set<Integer> group = partitions.get(pid);
            int representative = group.iterator().next();
            //Get the transitions of the representative state in the original DFA and define the transitions for the corresponding state in the minimized DFA based on the partitioning of the destination states

            Map<Character, Integer> trans = dfa.getTransitions().get(representative);

            //If the representative state has transitions,
            //  define the transitions for the corresponding state in the minimized DFA based on the partitioning of the destination states

            if (trans != null){

                Map<Character,Integer> newTrans = new HashMap<>();

                for (Map.Entry<Character,Integer> entry : trans.entrySet()){
                    newTrans.put(entry.getKey(),partitionOf[entry.getValue()]);

                }
                minTransitions.put(pid,newTrans);
            }
            //Determine the accepting status of the corresponding state in the minimized 
            //DFA based on the token accepted by the representative state in the original DFA. 
            //If the representative state is accepting, mark the corresponding state in the minimized DFA as accepting for the same token.
            
            String token = dfa.getAccepting().get(representative);
            if (token != null){
                
                minAccepting.put(pid,token);
                
            }
        }

    }

    //Getters for the minimized DFA
    public Map<Integer,Map<Character,Integer>> getTransitions(){return minTransitions;}
    public Map<Integer,String> getAccepting(){return minAccepting;}
    public int getStartId(){return minStartId;}
    public int getStateCount(){return partitions.size();}


    
}
