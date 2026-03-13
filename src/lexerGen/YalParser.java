package lexerGen;

import java.util.LinkedHashMap;
import java.util.List;
import java.io.*;
import java.util.ArrayList;

public class YalParser {

//.yal file sections

private String headerSection= "";
private String trailerSection= "";
private LinkedHashMap<String,String> definitions= new LinkedHashMap<>();

//each regex is stored as a key and its corresponding token as a value
private List<String[]> rules= new ArrayList<>();


public YalParser(String filePath) throws IOException{
    parse(filePath);

}


public void parse(String filePath) throws IOException{
    //Read the whole file content as a String 

    String content= new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(filePath)));

    //1. Delete comments
    content = removeComments(content);

    //2.Parse the header section if there is one 
    int idx= 0;
    //We start by skipping any leading whitespace characters in the content using the skipWhiteSpace method. This method takes the content string and the current index as parameters and returns the index of the first non-whitespace character it encounters. We update our index variable to this new value, effectively moving past any leading whitespace in the content.
    idx = skipWhiteSpace(content, idx);
    
    //We check if we are at a {

    if (idx < content.length() && content.charAt(idx) == '{'){
        
        int close = findingMatchingBrace(content, idx);
        headerSection = content.substring(idx+1, close).trim();
        idx = close + 1; // Move the index to the character immediately following the closing brace of the header section
    }
    //3. Parse the definitions section

    idx = skipWhiteSpace(content, idx);
    while (idx < content.length() && content.startsWith("let ",idx)){
        idx += 4; // Move the index past the "let " keyword
        idx = skipWhiteSpace(content, idx);
        int nameStart = idx;

        /* We continue to scan the name of the variable until we find
        a char that is not a letter digit or an underscore, that
        indicates the end of the variable name.
        
        */
        while(idx < content.length() && (Character.isLetterOrDigit(content.charAt(idx)) || content.charAt(idx) 
  == '_')) {
            idx++;
        }
        /*
        Once we have the variable name
         we skip any whitespace characters and expect to find an '=' 
         character that separates the variable name from its definition. If we do not find an '=',
          we throw an exception indicating that the definition is invalid. If we do find an '=',
           we move past it and skip any additional whitespace characters to prepare for reading the variable's definition.
         */
        String name = content.substring(nameStart, idx).trim();
        idx = skipWhiteSpace(content, idx);
        idx++; // Move past the '=' character
        idx = skipWhiteSpace(content, idx);

        //
        int regexStart =idx;
    
        while(idx < content.length()) {
            if(content.startsWith("let ",idx) || content.startsWith("rule ",idx)){
                break;
            }
            idx++;
        }
        /*Once we have the variable name and its definition,
        we store them in the definitions map. 
        The variable name serves as the key, 
        and its corresponding definition (the regex) serves as the value.
         We trim any leading or trailing whitespace 
         from the regex before storing it in the map to ensure 
         that it is clean and properly formatted.
        */

        String regex = content.substring(regexStart, idx).trim();
        definitions.put(name, regex);
        //After processing the current definition, we skip any additional whitespace characters to prepare for the next definition or section in the content.

        idx = skipWhiteSpace(content, idx);
    }

    //4. Extract rules section

    if(idx< content.length() && content.startsWith("rule",idx)){
        idx += 5; //Move past the "rule " keyword

        //Move past the entrypoint name and the = character
        while(idx < content.length() && content.charAt(idx) != '='){
            idx++;
        }
        idx++; // Move past the '=' character
        idx = skipWhiteSpace(content, idx);

        //Parse each rule until we reach the end of the content or encounter a new section
        boolean first = true;
        while(idx < content.length()){
            idx = skipWhiteSpace(content, idx);

            //Rules after the first one should start with a '|'
            if(!first){
                if(idx < content.length() && content.charAt(idx) == '|'){
                    idx++; // Move past the '|' character
                    idx = skipWhiteSpace(content, idx);
                }else{  
                    break; // If we don't find a '|' character, we assume we've reached the end of the rules section and break out of the loop
                }
            }
            first = false;
            //We read the regex part of the rule until we encounter a '{' character, which indicates the start of the action block for that rule. If we reach the end of the content without finding a '{', we throw an exception indicating that the rule is invalid.
            int ruleregexStart=idx;
            while(idx < content.length() && content.charAt(idx) != '{'){
                idx++;
            }
            //If we reach the end of the content without finding a '{', we throw an exception indicating that the rule is invalid.
            String ruleRegex= content.substring(ruleregexStart, idx).trim();

            //Read the action between the braces

            int actionClose = findingMatchingBrace(content, idx);
            /* We extract the action block for the current rule. 
            The action block is enclosed within braces '{' and '}'.
             We use the findingMatchingBrace method to find the index of the closing brace that matches the opening brace at the current index. 
             We then extract the substring between these braces, which represents the action associated with the current rule.
             We trim any leading or trailing whitespace from the action to ensure it is clean and properly formatted before storing it in our rules list along with its corresponding regex.
             */
            String action = content.substring(idx+1, actionClose).trim();
            idx = actionClose + 1; // Move the index to the character immediately following the closing brace of the action block
            rules.add(new String[]{ruleRegex, action});
        }
}
// 5. Extract trailer section if there is one

    idx = skipWhiteSpace(content, idx);
    if (idx < content.length() && content.charAt(idx) == '{'){
        int close = findingMatchingBrace(content, idx);
        trailerSection = content.substring(idx+1, close).trim();
    }
    //6. Expand definitions in rules
    expandDefinitions();


    

}
/*This method is used to expand the definitions in the rules section. 
 It iterates through each rule and replaces any occurrences of defined variables in the regex part of the rule with their corresponding definitions from the definitions map. 
 This allows us to use the defined variables in our rules and have them properly expanded to their full regex definitions when we process the rules later on.
*/
private void expandDefinitions(){
    //We create a list of the keys from the definitions map to iterate through them when replacing occurrences in the rules
    List<String> keys = new ArrayList<>(definitions.keySet());
    for(int i = 0; i < keys.size(); i++){
        String current = definitions.get(keys.get(i));

        for (int j = 0 ; j < i; j++){
            String prev = keys.get(j);
            current = current.replaceAll("\\b" + prev + "\\b", "(" + definitions.get(prev) + ")");
        }
        definitions.put(keys.get(i) , current); 
    }
    //After we have expanded the definitions within the definitions map, we proceed to expand them within the rules. We iterate through each rule and replace any occurrences of defined variables in the regex part of the rule with their corresponding expanded definitions from the definitions map. This ensures that all rules are fully expanded and ready for processing when we use them later on in our lexer generation process.
    for(String[] rule : rules){
        /*We start with the original regex from the rule and then replace any occurrences 
        of defined variables with their corresponding definitions from the definitions map. 
        We iterate through the sorted list of definition keys to ensure that we 
        replace longer variable names before shorter ones, 
        which helps to avoid partial replacements that could occur if a shorter variable 
        name is a substring of a longer one. 
        After replacing all occurrences of defined variables in the regex, 
        we update the rule's regex with the fully expanded version.
        
        */
        String regex = rule[0];
        List<String> sorted = new ArrayList<>(definitions.keySet());
        sorted.sort((a,b) -> b.length() - a.length());  
        for(String name : sorted){
            regex = regex.replaceAll("\\b" + name + "\\b", "(" + definitions.get(name) + ")");
        }
        rule[0] = regex;
        
    }
}







//Methods to get the different sections of the .yal file

//This method removes comments from the .yal file content
private String removeComments(String content){
    StringBuilder sb= new StringBuilder();
    int i = 0;

    //Iterate through the content character by character
    while(i < content.length() -1){
        if(content.charAt(i) == '('  && content.charAt(i+1)==  '*'){
            i+=2;
            //Try to find the closing comment symbol
            while(i< content.length()-1){
                if(content.charAt(i) == '*' && content.charAt(i+1) == ')'){
                    i+=2;
                    break;
                }
                //once we find the closing symbol, we break out of the loop and continue with the rest of the content
                i++;
             }
             //If we reach the end of the content without finding the closing symbol, we break out of the loop
            }else{
                sb.append(content.charAt(i));
                i++;
            }
            
        }
        //Append any remaining characters after the loop
        if (i < content.length()) sb.append(content.charAt(i));
        return sb.toString();   
    }

    public int skipWhiteSpace(String s, int idx){
        //This method is used to skip any whitespace characters in the input string starting from the given index
        while (idx < s.length() && Character.isWhitespace(s.charAt(idx))){
            //If the current character is a whitespace, we increment the index to skip it
            idx++;
        }
        //Once we encounter a non-whitespace character or reach the end of the string, we return the updated index
        return idx;

    }

    //This method is used to find the index of the matching closing brace for a given opening brace in the input string
    private int findingMatchingBrace(String s, int openIdx){
        //We initialize a depth counter to keep track of nested braces. We start with a depth of 1 since we have already encountered an opening brace
        int depth = 1;
        //We start iterating from the character immediately following the opening brace
        int i= openIdx + 1;   
        //We continue iterating until we find the matching closing brace or reach the end of the string    
        while (i < s.length() && depth >0){
            //If we encounter another opening brace, we increment the depth counter. If we encounter a closing brace, we decrement the depth counter
            if(s.charAt(i)== '{') depth++;
            if(s.charAt(i)== '}') depth--;
            //We increment the index to continue searching for the matching closing brace
            i++;
                

        }
        return i-1;      //Once we find the matching closing brace, we return
    }

    //----Getters ----

public String getHeaderSection() {return headerSection;}
public String getTrailerSection() {return trailerSection;}
public LinkedHashMap<String,String> getDefinitions() {return definitions;}
public List<String[]> getRules() {return rules;}

    
}

 