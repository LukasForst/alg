package alg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MatrixReader {
    public static ExamSolver read() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            int[] size = Arrays.stream(reader.readLine().split(" ")).mapToInt(Integer::parseInt).toArray();
            int rows = size[0];
            int columns = size[1];

            int[] start = Arrays.stream(reader.readLine().split(" ")).mapToInt(Integer::parseInt).toArray();
            int startRowIndex = start[0] - 1;
            int startColumnIndex = start[1] - 1;

            int[] end = Arrays.stream(reader.readLine().split(" ")).mapToInt(Integer::parseInt).toArray();
            int endRowIndex = end[0] - 1;
            int endColumnIndex = end[1] - 1;

            Point[][] result = new Point[rows][columns];
            for (int i = 0; i < rows; i++) {
                int[] readData = Arrays.stream(reader.readLine().split(" ")).mapToInt(Integer::parseInt).toArray();
                for (int j = 0; j < readData.length; j++) {
                    result[i][j] = new Point(readData[j]);
                }
            }

            return new ExamSolver(result, rows, columns, startRowIndex, startColumnIndex, endRowIndex, endColumnIndex);
        } catch (Exception e) {
            Logger.getGlobal().log(Level.WARNING, e.toString());
            throw new IllegalStateException(e);
        }
    }
}
