import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Utils {
    public static int[][] processDot(String fileName, Map<Integer, String> requestMap) {
        File file = new File(fileName);
        int[][] matrix = null;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String st;
            while ((st = br.readLine()) != null) {
                if (!st.contains("["))
                    continue;
                if (st.contains("->")) {
                    break;
                } else {
                    String index = st.substring(0, st.indexOf("[")).trim();
                    String label = st.substring(st.indexOf("[") + 1, st.indexOf("]")).trim();
                    requestMap.put(Integer.parseInt(index), label);
                }
            }
            int len = requestMap.size();
            matrix = new int[len][len];
            do {
                if (st == null || !st.contains("->")) {
                    break;
                }
                String first = st.substring(0, st.indexOf("->")).trim();
                String second = st.substring(st.indexOf("->") + 2, st.indexOf("[")).trim();
                matrix[Integer.parseInt(second) - 1][Integer.parseInt(first) - 1] = 1;
            } while ((st = br.readLine()) != null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return matrix;
    }

    public static ArrayList<Integer> findNonZero(int[] list) {
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            if (list[i] != 0)
                result.add(i);
        }
        return result;
    }

    public static int random(double[] weight) {
        List<Double> weightTmp = new ArrayList<>(weight.length + 1);
        weightTmp.add(0.0);
        double sum = 0;
        for (double d : weight) {
            sum += d;
            weightTmp.add(sum);
        }
        Random random = new Random();
        double rand = random.nextDouble();
        int index = 0;
        for (int i = weightTmp.size() - 1; i > 0; i--) {
            if (rand >= weightTmp.get(i)) {
                index = i;
                break;
            }
        }
        return index;
    }
}
