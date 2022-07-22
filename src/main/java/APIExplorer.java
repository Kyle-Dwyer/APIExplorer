import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class APIExplorer {
    public static void main(String[] args) {
        //处理dot文件
        String fileName = APIExplorer.class.getResource("graph/odg.dot").getPath();
        HashMap<Integer, String> requestMap = new HashMap<>();
        int[][] matrix;
        if ((matrix = Utils.processDot(fileName, requestMap)) == null) {
            System.out.println("dot 文件处理失败");
            System.exit(-1);
        }
        requestMap.put(0, "root");
        System.out.println(requestMap);
        System.out.println(Arrays.deepToString(matrix));
        QLearning ql = new QLearning();

        ql.init(matrix);
        int seqNum = 10;
        int pathLen = 5;
        ArrayList<ArrayList<String>> sequence = new ArrayList<>();
        for (int num = 0; num < seqNum; num++) {
            //TODO reset system
            int currentState = 0;
            ArrayList<String> path = new ArrayList<>();
            ql.initializeOptionalActionList();
            for (int len = 0; len < pathLen; ) {
                int action = ql.getPolicyFromState(currentState);
                if (action == -1)
                    break;
                int status = action + 1;
                String label = requestMap.get(status);
                System.out.println("本次推荐的action为" + action + "，对应的请求为" + label);
                //TODO send request
                Random random = new Random();
                boolean result = random.nextBoolean();
                if (result) {
                    System.out.println("    请求成功 From state " + requestMap.get(currentState) + " goto state " + label);
                    ql.update(currentState, action);
                    path.add(label);
                    currentState = status;
                    len++;
                } else {
                    System.out.println("    请求失败 " + label);
                    ql.repair(currentState, action);
                }
            }
            System.out.println("本轮请求序列为：" + path + "\n");
            sequence.add(path);
        }
        System.out.println(sequence);
        ql.finish();
    }
}
