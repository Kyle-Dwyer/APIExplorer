import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDArrays;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.index.NDIndex;
import ai.djl.ndarray.types.DataType;
import ai.djl.ndarray.types.Shape;

import java.util.ArrayList;
import java.util.Arrays;

public class QLearning {
    private final static double tau = 1;    // coefficient
    private final static double lambda = 0.9;   // discount factor
    private final static int penalty = -1;
    int[] dependency;

    NDManager manager;
    private NDArray N;  // Transformation count
    private NDArray Q;  // Q learning
    private ArrayList<ArrayList<Integer>> edges;
    private ArrayList<Integer> optionalActionList;

    public void init(int[][] matrix) {
        int statesCount = matrix.length + 1;
        int actionCount = matrix.length;
        manager = NDManager.newBaseManager();

        // 创建 NDArray
        N = manager.ones(new Shape(statesCount, actionCount), DataType.INT32);

        //初始化action的关系
        edges = new ArrayList<>();
        for (int index = 0; index < actionCount; index++)
            edges.add(new ArrayList<>());
        NDArray ndMatrix = manager.create(matrix);

        NDArray ndEdges = ndMatrix.nonzero().toType(DataType.INT32, false);
        dependency = new int[actionCount];
        Arrays.fill(dependency, 1);
        for (int i = 0; i < ndEdges.size(0); i++) {
            edges.get(ndEdges.getInt(i, 0)).add(ndEdges.getInt(i, 1));
            dependency[ndEdges.getInt(i, 1)] = 0;
        }

        //初始化没有入度的作为初始可选action
        initializeOptionalActionList();

        //根据出度初始化Q
        Q = NDArrays.concat(new NDList(manager.create(dependency).expandDims(0), ndMatrix), 0).toType(DataType.FLOAT64, false);
    }

    void initializeOptionalActionList() {
        optionalActionList = new ArrayList<>(Utils.findNonZero(dependency));
    }

    private void updateQ(int previousState, int action, double curiosity) {
        int nextState = action + 1;
        double newValue = curiosity + lambda * Q.get(nextState).max().getDouble();
        Q.set(new NDIndex(previousState, action), newValue);

    }

    void update(int previousState, int action) {
        //更新N，获取curiosity
        N.set(new NDIndex(previousState, action), N.getInt(previousState, action) + 1);
        double curiosity = 1 / Math.sqrt(N.getInt(previousState, action));
        //更新Q
        updateQ(previousState, action, curiosity);
        //更新optionalActionList
        boolean result;
        do {
            result = optionalActionList.remove(new Integer(action));
        } while (result);
        optionalActionList.addAll(edges.get(action));
    }

    void repair(int previous, int action) {
        //更新Q
        updateQ(previous, action, penalty);
    }

    //Gumbel-Softmax分布
    double[] calculateWeight(NDArray ndOptionalQValue) {
        // 首先创造U(0,1)
        NDArray union = manager.randomUniform(0, 1, ndOptionalQValue.getShape());
        // Gumbel-Max Trick
        NDArray gumbel = union.add(ndOptionalQValue).div(tau);
        // Softmax
        NDArray gumbelSoftmax = gumbel.softmax(0);
        return gumbelSoftmax.toDoubleArray();
    }

    int getPolicyFromState(int state) {
        System.out.println(optionalActionList);
        //没有可以执行的请求了
        if (optionalActionList.size() == 0)
            return -1;
        //从范围内计算权重
        double[] optionalQValue = new double[optionalActionList.size()];
        for (int i = 0; i < optionalActionList.size(); i++)
            optionalQValue[i] = Q.getDouble(state, optionalActionList.get(i));
        NDArray ndOptionalQValue = manager.create(optionalQValue);
        double[] weights = calculateWeight(ndOptionalQValue);
        //随机数选取一个action
        int index = Utils.random(weights);
        return optionalActionList.get(index);
    }


    public void finish() {
        manager.close();
    }
}