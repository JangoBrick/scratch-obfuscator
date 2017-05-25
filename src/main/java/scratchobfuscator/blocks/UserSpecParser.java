package scratchobfuscator.blocks;

import java.util.ArrayList;
import java.util.List;


/**
 * This class can convert the textual representation of a custom block's user
 * spec into an instance of {@link UserSpec}.
 */
public class UserSpecParser
{
    private UserSpecParser()
    {
    }

    /**
     * Parses the given string (advanced quoting supported) and returns the
     * resulting {@link UserSpec} object.
     * 
     * @param s The string to parse.
     * @return The parse result.
     */
    public static UserSpec parse(String s)
    {
        UserSpec spec = new UserSpec();

        split(s).forEach(spl -> {
            if (spl.startsWith("%")) {
                spec.addParameter(spl.substring(1));
            } else {
                spec.addLabel(spl);
            }
        });

        return spec;
    }

    /**
     * Splits the given string (advanced quoting supported) into its different
     * parts.
     * 
     * <p>
     * <b>Example:</b> {@code test %block with "%a long parameter"}<br>
     * <b>Output:</b> {@code [test, %block, with, %a long parameter]}
     * 
     * @param s The string to split.
     * @return The string's parts.
     */
    public static List<String> split(String s)
    {
        List<String> parts = new ArrayList<>();

        for (int i = 0, n = s.length(); i < n; ++i) {

            char c = s.charAt(i);

            if (c == ' ') {
                continue;
            } else if (c == '"') {
                String quoteContents = consumeQuoted(s, i);
                parts.add(quoteContents);
                i += quoteContents.length() + 1;
            } else {
                int endIndex = s.indexOf(' ', i + 1);
                if (endIndex < 0) {
                    endIndex = s.length();
                }
                String word = s.substring(i, endIndex);
                parts.add(word);
                i += word.length();
            }

        }

        return parts;
    }

    private static String consumeQuoted(String s, int firstQuoteIndex)
    {
        StringBuilder sb = new StringBuilder();

        for (int i = firstQuoteIndex + 1, n = s.length(); i < n; ++i) {
            char c = s.charAt(i);
            if (c == '"' && (i + 1 >= n || s.charAt(i + 1) == ' ')) {
                return sb.toString();
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }
}
