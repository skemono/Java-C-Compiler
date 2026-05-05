package parserGen;

import parserGen.model.Production;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class YalpParser {
    private final List<String> tokens = new ArrayList<>();
    private final List<String> ignoredTokens = new ArrayList<>();
    private final List<Production> productions = new ArrayList<>();

    public YalpParser(String filePath) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        parse(content);
    }

    public YalpParser(String content, boolean isRawContent) {
        parse(content);
    }

    private void parse(String content) {
        content = removeComments(content);
        int sepIdx = content.indexOf("%%");
        if (sepIdx < 0) throw new IllegalArgumentException("Missing %% separator in .yalp file");
        parseTokenSection(content.substring(0, sepIdx));
        parseProductionsSection(content.substring(sepIdx + 2));
    }

    private String removeComments(String content) {
        return content.replaceAll("/\\*[\\s\\S]*?\\*/", "");
    }

    private void parseTokenSection(String section) {
        for (String line : section.split("\\n")) {
            line = line.trim();
            if (line.startsWith("%token")) {
                for (String tok : line.substring(6).trim().split("\\s+")) {
                    if (!tok.isEmpty()) tokens.add(tok);
                }
            } else if (line.startsWith("IGNORE")) {
                String tok = line.substring(6).trim();
                if (!tok.isEmpty()) ignoredTokens.add(tok);
            }
        }
    }

    private void parseProductionsSection(String section) {
        for (String block : section.split(";")) {
            block = block.trim();
            if (block.isEmpty()) continue;
            int colonIdx = block.indexOf(':');
            if (colonIdx < 0) continue;
            String head = block.substring(0, colonIdx).trim();
            if (head.isEmpty()) continue;
            for (String alt : block.substring(colonIdx + 1).split("\\|")) {
                List<String> symbols = new ArrayList<>();
                for (String sym : alt.trim().split("\\s+")) {
                    if (!sym.isEmpty()) symbols.add(sym);
                }
                productions.add(new Production(head, symbols));
            }
        }
    }

    public List<String> getTokens() { return Collections.unmodifiableList(tokens); }
    public List<String> getIgnoredTokens() { return Collections.unmodifiableList(ignoredTokens); }
    public List<Production> getProductions() { return Collections.unmodifiableList(productions); }
}
