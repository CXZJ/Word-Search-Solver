import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WordSearch {

    private int rows;
    private int columns;
    private char[][] theBoard;
    private String[] theWords;
    private BufferedReader puzzleStream;
    private BufferedReader wordStream;
    private BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    public WordSearch() throws IOException {
        puzzleStream = openFile("Enter puzzle file");
        wordStream = openFile("Enter dictionary file");
        System.out.println("Reading files...");
        readPuzzle();
        readWords();
    }

    private BufferedReader openFile(String message) {
        String fileName = "";
        FileReader theFile;
        BufferedReader fileIn = null;
        do {
            System.out.println(message + ": ");
            try {
                fileName = in.readLine();
                if (fileName == null)
                    System.exit(0);
                theFile = new FileReader(fileName);
                fileIn = new BufferedReader(theFile);
            } catch (IOException e) {
                System.err.println("Cannot open " + fileName);
            }
        } while (fileIn == null);

        System.out.println("Opened " + fileName);
        return fileIn;
    }

    private void readWords() throws IOException {
        List<String> words = new ArrayList<String>();

        String lastWord = null;
        String thisWord;

        while ((thisWord = wordStream.readLine()) != null) {
            if (lastWord != null && thisWord.compareTo(lastWord) < 0) {
                System.err.println("Dictionary is not sorted... skipping");
                continue;
            }
            words.add(thisWord);
            lastWord = thisWord;
        }

        theWords = new String[words.size()];
        theWords = words.toArray(theWords);
    }

    private void readPuzzle() throws IOException {
        String oneLine;
        List<String> puzzleLines = new ArrayList<String>();
        if ((oneLine = puzzleStream.readLine()) == null)
            throw new IOException("No lines in puzzle file");

        columns = oneLine.length();
        puzzleLines.add(oneLine);

        while ((oneLine = puzzleStream.readLine()) != null) {
            if (oneLine.length() != columns)
                System.err.println("Puzzle is not rectangular; skipping row");
            else
                puzzleLines.add(oneLine);
        }

        rows = puzzleLines.size();
        theBoard = new char[rows][columns];

        int r = 0;
        for (String theLine : puzzleLines)
            theBoard[r++] = theLine.toCharArray();
    }

    private int solveDirection(int baseRow, int baseCol, int rowDelta, int colDelta) {
        int count = 0;
        StringBuilder sb = new StringBuilder();

        int currentRow = baseRow;
        int currentCol = baseCol;

        while (currentRow >= 0 && currentRow < rows &&
                currentCol >= 0 && currentCol < columns) {
            sb.append(theBoard[currentRow][currentCol]);
            int result = prefixSearch(theWords, sb.toString());
            if (result >= 0) {
                count++;
            } else if (result < 0 && result != -1) {
                break; // Not a prefix, stop searching
            }
            currentRow += rowDelta;
            currentCol += colDelta;
        }

        return count;
    }

    public int solvePuzzle() {
        int count = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                for (int rd = -1; rd <= 1; rd++) {
                    for (int cd = -1; cd <= 1; cd++) {
                        if (rd != 0 || cd != 0) {
                            count += solveDirection(row, col, rd, cd);
                        }
                    }
                }
            }
        }
        return count;
    }

    private int prefixSearch(String[] a, String x) {
        return binarySearch(a, x);
    }

    private int binarySearch(String[] a, String x) {
        int low = 0;
        int high = a.length - 1;

        while (low <= high) {
            int mid = (low + high) / 2;
            int cmp = a[mid].compareTo(x);

            if (cmp < 0)
                low = mid + 1;
            else if (cmp > 0)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found
    }

    public static void main(String[] args) {
        try {
            WordSearch puzzle = new WordSearch();
            System.out.println("Total occurrences of words in the puzzle: " + puzzle.solvePuzzle());
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }
}
