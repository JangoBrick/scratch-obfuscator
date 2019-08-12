package scratchobfuscator.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Represents a part-wise assembly of a custom block's user spec string. This
 * facilitates changing labels and parameters. Further, the {@link #toString()}
 * method allows instances to be converted back to valid specs.
 */
public class UserSpec
{
    private final List<Part> parts = new ArrayList<>();

    /**
     * @return The number of parts (labels and parameters).
     */
    public int getPartCount()
    {
        return parts.size();
    }

    /**
     * @param index The part index.
     * @return Whether the part at the given index is a parameter (vs. label).
     */
    public boolean isParameter(int index)
    {
        return parts.get(index).isParam;
    }

    /**
     * @param index The part index.
     * @return The part's textual value (label string OR parameter name).
     */
    public String getText(int index)
    {
        return parts.get(index).text;
    }

    /**
     * Updates the part's textual value (label string if the part is a label, or
     * parameter name if it is a parameter).
     *
     * @param index The part index.
     * @param text The new text value.
     */
    public void setText(int index, String text)
    {
        final Part p = parts.get(index);
        p.text = text;
    }

    /**
     * Adds a label part to the end of this spec.
     *
     * @param text The label string.
     */
    public void addLabel(String text)
    {
        parts.add(new Part(false, text));
    }

    /**
     * Adds a parameter part to the end of this spec.
     *
     * @param text The parameter name.
     */
    public void addParameter(String text)
    {
        parts.add(new Part(true, text));
    }

    /**
     * Returns the user spec converted to its string form.
     *
     * @return The user spec, as a string.
     */
    @Override
    public String toString()
    {
        return parts.stream().map(Part::toString).collect(Collectors.joining(" "));
    }

    /**
     * Checks whether this spec and the given one are regarded as "similar" or
     * "the same" by BYOB. This takes into account labels and parameter
     * positions, but not parameter names.
     *
     * <p>
     * The check is relevant since BYOB itself cannot tell apart matching specs.
     *
     * @param other The spec to compare this with.
     * @return Whether the two specs are "similar".
     */
    public boolean isSimilar(UserSpec other)
    {
        if (other == null || getPartCount() != other.getPartCount()) {
            return false;
        }

        for (int i = 0, n = getPartCount(); i < n; ++i) {
            if (isParameter(i) != other.isParameter(i)) {
                return false;
            }
            if (!isParameter(i) && !getText(i).equals(other.getText(i))) {
                return false;
            }
        }

        return true;
    }

    private static class Part
    {
        private boolean isParam;
        private String text;

        public Part(boolean isParam, String text)
        {
            this.isParam = isParam;
            this.text = text;
        }

        @Override
        public String toString()
        {
            if (text.indexOf(' ') >= 0 || text.indexOf('"') >= 0) {
                return '"' + (isParam ? ('%' + text) : text) + '"';
            }

            return isParam ? ('%' + text) : text;
        }
    }
}
