package com.example.uc12;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class consists of static utility methods for operating with strings.
 */
public class Utils {

    private static final int INDEX_NOT_FOUND = -1;
    private static final String EMPTY = "";

    /**
     * Returns a new string with appended, if needed.
     *
     * <p>Some examples:
     * <p>"hello world", 8, -1, "end" -> hello world
     * <p>"hello world", 2, 9, "end" -> helloend
     * <p> From this example we can see that we depends on lower case, as delimiter for appending whitespace used.
     * In case when whitespace not found, then the string for append may be added only when {@code upper != source.lenght()}
     * In case when whitespace found, then string for appending added to the end of a new string, which
     * will be concatenated from 0 position to a minimal index of whitespace or upper
     * <p>
     * <p>Note: (some specific conditions)
     * <p> * upper >= -1
     * <p> * upper >= lower || upper == -1
     * <p></p>
     * @param str         source string
     * @param lower       start position for detecting whitespace
     * @param upper       possible end position for appending
     * @param appendToEnd string to append for
     * @return  result string with appending if needed
     * @throws IllegalArgumentException if conditions which related to lower and upper parameters are not valid
     * @see Utils#isTrue(boolean, String)
     * @see Utils#isEmpty(CharSequence)
     * @see Utils#indexOf(CharSequence, CharSequence, int)
     * @see Utils#defaultString(String)
     */
    public static String abbreviate(final String str, int lower, int upper, final String appendToEnd) {
        isTrue(upper >= -1, "upper value cannot be less than -1");
        isTrue(upper >= lower || upper == -1, "upper value is less than lower value");
        if (isEmpty(str)) {
            return str;
        }

        if (lower > str.length()) {
            lower = str.length();
        }

        if (upper == -1 || upper > str.length()) {
            upper = str.length();
        }

        final StringBuilder result = new StringBuilder();
        final int index = indexOf(str, " ", lower);
        if (index == -1) {
            result.append(str, 0, upper);
            if (upper != str.length()) {
                result.append(defaultString(appendToEnd));
            }
        } else {
            result.append(str, 0, Math.min(index, upper));
            result.append(defaultString(appendToEnd));
        }

        return result.toString();
    }

    /**
     * Returns the new string with new number of characters.
     * In case when delimiters will be defined, then the each next char after delimiter
     * will be included into a result string, except case when the next char is a delimiter too.
     *
     * <p>Note (some specific conditions):
     * <p>* When the source string {@code isEmpty} then source string will be returned as a result
     * <p>* When array of delimiters is null, then the result string will contains each char after whitespace,
     * because in this case default delimiter will be used, it`s a whitespace.
     * <p>* When array of delimiter is empty, then default string will be returned {@code EMPTY}
     *
     * @param str   source string
     * @param delimiters   array of char delimiters
     * @return  result string
     * @see Utils#isEmpty(CharSequence)
     * @see Utils#generateDelimiterSet(char[])
     * @see Character#isWhitespace(char)
     * @see Character#charCount(int)
     */
    public static String initials(final String str, final char... delimiters) {
        if (isEmpty(str)) {
            return str;
        }

        if (delimiters != null && delimiters.length == 0) {
            return EMPTY;
        }

        final Set<Integer> delimiterSet = generateDelimiterSet(delimiters);
        final int strLen = str.length();
        final int[] newCodePoints = new int[strLen / 2 + 1];
        int count = 0;
        boolean lastWasGap = true;

        for (int i = 0; i < strLen; ) {
            final int codePoint = str.codePointAt(i);

            if (delimiterSet.contains(codePoint) || delimiters == null && Character.isWhitespace(codePoint)) {
                lastWasGap = true;
            } else if (lastWasGap) {
                newCodePoints[count++] = codePoint;
                lastWasGap = false;
            }

            i += Character.charCount(codePoint);
        }

        return new String(newCodePoints, 0, count);
    }

    /**
     * Returns a string in which each char are replaced by the opposite case.
     * <p>Note: empty or nullable strigns will be ignored.
     *
     * @param str source string
     * @return string with a new case for each character if non-null or empty
     * @see Utils#isEmpty(CharSequence)
     * @see Character#isUpperCase(char)
     * @see Character#isTitleCase(char)
     * @see Character#toLowerCase(char)
     * @see Character#toTitleCase(char)
     * @see Character#toUpperCase(char)
     */
    public static String swapCase(final String str) {
        if (isEmpty(str)) {
            return str;
        }

        final int strLen = str.length();
        final int[] newCodePoints = new int[strLen];
        int outOffset = 0;
        boolean whitespace = true;
        for (int index = 0; index < strLen; ) {
            final int oldCodepoint = str.codePointAt(index);
            final int newCodePoint;
            if (Character.isUpperCase(oldCodepoint) || Character.isTitleCase(oldCodepoint)) {
                newCodePoint = Character.toLowerCase(oldCodepoint);
                whitespace = false;
            } else if (Character.isLowerCase(oldCodepoint)) {
                if (whitespace) {
                    newCodePoint = Character.toTitleCase(oldCodepoint);
                    whitespace = false;
                } else {
                    newCodePoint = Character.toUpperCase(oldCodepoint);
                }
            } else {
                whitespace = Character.isWhitespace(oldCodepoint);
                newCodePoint = oldCodepoint;
            }
            newCodePoints[outOffset++] = newCodePoint;
            index += Character.charCount(newCodePoint);
        }
        return new String(newCodePoints, 0, outOffset);
    }

    /**
     *                              Z
     *                        Z
     *         .,.,        z
     *       (((((())    z
     *      ((('_  _`) '
     *      ((G   \ |)
     *     (((`   " ,
     *      .((\.:~:          .--------------.
     *      __.| `"'.__      | \              |
     *   .~~   `---'   ~.    |  .             :
     *  /                `   |   `-.__________)
     * |             ~       |  :             :
     * |                     |  :  |
     * |    _                |     |   [ ##   :
     *  \    ~~-.            |  ,   oo_______.'
     *   `_   ( \) _____/~~~~ `--___
     *   | ~`-)  ) `-.   `---   ( - a:f -
     *   |   '///`  | `-.
     *   |     | |  |    `-.
     *   |     | |  |       `-.
     *   |     | |\ |
     *   |     | | \|
     *    `-.  | |  |
     *       `-| '
     *
     * @param str
     * @param wrapLength
     * @param newLineStr
     * @param wrapLongWords
     * @param wrapOn
     * @return
     */
    public static String wrap(final String str,
                              int wrapLength,
                              String newLineStr,
                              final boolean wrapLongWords,
                              String wrapOn) {
        if (str == null) {
            return null;
        }
        if (newLineStr == null) {
            newLineStr = System.lineSeparator();
        }
        if (wrapLength < 1) {
            wrapLength = 1;
        }
        if (isBlank(wrapOn)) {
            wrapOn = " ";
        }
        final Pattern patternToWrapOn = Pattern.compile(wrapOn);
        final int inputLineLength = str.length();
        int offset = 0;
        final StringBuilder wrappedLine = new StringBuilder(inputLineLength + 32);
        int matcherSize = -1;

        while (offset < inputLineLength) {
            int spaceToWrapAt = -1;
            Matcher matcher = patternToWrapOn.matcher(str.substring(offset,
                    Math.min((int) Math.min(Integer.MAX_VALUE, offset + wrapLength + 1L), inputLineLength)));
            if (matcher.find()) {
                if (matcher.start() == 0) {
                    matcherSize = matcher.end();
                    if (matcherSize != 0) {
                        offset += matcher.end();
                        continue;
                    }
                    offset += 1;
                }
                spaceToWrapAt = matcher.start() + offset;
            }

            if (inputLineLength - offset <= wrapLength) {
                break;
            }

            while (matcher.find()) {
                spaceToWrapAt = matcher.start() + offset;
            }

            if (spaceToWrapAt >= offset) {
                wrappedLine.append(str, offset, spaceToWrapAt);
                wrappedLine.append(newLineStr);
                offset = spaceToWrapAt + 1;
            } else if (wrapLongWords) {
                if (matcherSize == 0) {
                    offset--;
                }
                wrappedLine.append(str, offset, wrapLength + offset);
                wrappedLine.append(newLineStr);
                offset += wrapLength;
                matcherSize = -1;
            } else {
                matcher = patternToWrapOn.matcher(str.substring(offset + wrapLength));
                if (matcher.find()) {
                    matcherSize = matcher.end() - matcher.start();
                    spaceToWrapAt = matcher.start() + offset + wrapLength;
                }

                if (spaceToWrapAt >= 0) {
                    if (matcherSize == 0 && offset != 0) {
                        offset--;
                    }
                    wrappedLine.append(str, offset, spaceToWrapAt);
                    wrappedLine.append(newLineStr);
                    offset = spaceToWrapAt + 1;
                } else {
                    if (matcherSize == 0 && offset != 0) {
                        offset--;
                    }
                    wrappedLine.append(str, offset, str.length());
                    offset = inputLineLength;
                    matcherSize = -1;
                }
            }
        }

        if (matcherSize == 0 && offset < inputLineLength) {
            offset--;
        }

        wrappedLine.append(str, offset, str.length());

        return wrappedLine.toString();
    }

    /**
     * Returns Unicode code points for the given delimiters if non-null.
     * In case the delimiters array is null then returns a {@code Set} with a single entry with code for whitespace,
     * otherwise returns empty {@code Set}.
     *
     * @param delimiters array with delimiters
     * @return a {@code Set<Int>} of Unicode code points
     * @see Character#codePointAt(char[], int)
     */
    private static Set<Integer> generateDelimiterSet(final char[] delimiters) {
        final Set<Integer> delimiterHashSet = new HashSet<>();
        if (delimiters == null || delimiters.length == 0) {
            if (delimiters == null) {
                delimiterHashSet.add(Character.codePointAt(new char[]{' '}, 0));
            }

            return delimiterHashSet;
        }

        for (int index = 0; index < delimiters.length; index++) {
            delimiterHashSet.add(Character.codePointAt(delimiters, index));
        }
        return delimiterHashSet;
    }

    /**
     * Check if the given expression is {@code true} and
     * throws a customized {@link IllegalArgumentException} if it isn`t.
     *
     * @param expression an expression to check if true
     * @param message    the detail message
     * @throws IllegalArgumentException if expression is false with custom message
     */
    private static void isTrue(final boolean expression, final String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Returns the length of the given char sequence if the given argument is not null and 0 otherwise.
     *
     * @param cs char sequence
     * @return 0 if the given sequence is null and length() otherwise
     */
    private static int length(final CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

    /**
     * Returns true if the provided char sequence is null or length equals 0 otherwise returns false.
     *
     * @param cs char sequence
     * @return true is sequence is null or length = 0, otherwise false
     */
    private static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * Returns true if the length equals 0 of the given sequence or doesn't contain whitespaces,
     * otherwise false.
     *
     * @param cs char sequence
     * @return true if sequence do not contains whitespaces or empty, otherwise returns false
     */
    private static boolean isBlank(final CharSequence cs) {
        final int strLen = length(cs);
        if (strLen == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the result of calling {@code toString} method on the given sequence if non-null,
     * otherwise returns default value {@code EMPTY}.
     *
     * @param str source string
     * @return the result of the calling {@code toString} method
     */
    private static String defaultString(final String str) {
        return Objects.toString(str, EMPTY);
    }

    /**
     * Returns the index within this string of the first occurrence of the specified substring
     * if source and sequence for search are non-null and occurrence exists,
     * otherwise returns @{code INDEX_NOT_FOUND}
     *
     * @param seq       source sequence to search in
     * @param searchSeq sequence to search for
     * @param startPos  index from which to start the search
     * @return the index of the first occurrence of the sequence to search for or
     * {@code INDEX_NOT_FOUND} of there is no occurrence
     */
    private static int indexOf(final CharSequence seq, final CharSequence searchSeq, final int startPos) {
        if (seq == null || searchSeq == null) {
            return INDEX_NOT_FOUND;
        }
        if (seq instanceof String) {
            return ((String) seq).indexOf(searchSeq.toString(), startPos);
        }
        if (seq instanceof StringBuilder) {
            return ((StringBuilder) seq).indexOf(searchSeq.toString(), startPos);
        }
        if (seq instanceof StringBuffer) {
            return ((StringBuffer) seq).indexOf(searchSeq.toString(), startPos);
        }
        return seq.toString().indexOf(searchSeq.toString(), startPos);
    }
}
