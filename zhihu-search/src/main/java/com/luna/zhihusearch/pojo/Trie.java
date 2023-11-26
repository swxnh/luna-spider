package com.luna.zhihusearch.pojo;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.w3c.dom.Node;

import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * utf-8 字典树
 * @author 文轩
 * 这是一个utf-8字典树
 * 用于存储包括中文在内的所有utf-8字符
 * 该数据结构可以用于搜索引擎的关键词提示
 * 也可以用于敏感词过滤
 * 前缀匹配的速度非常快
 * 但是不支持模糊匹配
 * 质数表和质数下标对照表是为了在满足一定随机性的情况下拥有更好的性能
 * 遇到性能问题可以选择扩张质数表和质数下标对照表
 * factor是为了性能和准确性的权衡
 * 为了匹配更多的关键词，可以适当增加factor
 * factor越大，排序的准确性越高，但是性能越差
 *
 * 可能的优化方向
 * 1.增加权重时为路径上的每个节点增加权重
 *   节点内部维护一个有序集合，每次更新权重的时候，就更新有序集合
 *   有序集合的排序规则是权重
 *   这样的话，每次深度优先遍历的时候，不需要取步长,可以直接遍历有序集合
 *   此方案可以确保每次都是取权重最大的前n个
 *   但是这样的话，增加权重的时候就会比较慢,而且会增加维护的复杂度
 *
 * 2.有序深度优先搜索时不先返回字符串，而是先返回二维节点数组
 *  二维节点数组的每一行都是一个路径
 *  所以只需要按照每行的最后一个节点的权重排序就可以了
 *  获取前n行之后，再遍历每一行，获取每一行的字符串
 *  这样的话，可以减少字符串的拼接次数
 *  经过测试，性能反而下降了,放弃
 *
 * 3.todo:新方案的可能性？
 *
 * 序列化问题
 * 1.没有使用标准的get set方法,json序列化会出现问题
 */
@EqualsAndHashCode
@ToString
public class Trie implements Serializable {

    private final TrieNode root;

    /**
     * 查询因子
     */
    private final int factor;

    /**
     * 质数表
     */
    private final int[] primeTable = new int[]{2, 3, 5, 7, 11, 13, 17, 19, 23, 29,
            31, 37, 41, 43, 47, 53, 59, 61, 67, 71
            , 73, 79, 83, 89, 97, 101, 103, 107, 109, 113};

    /**
     * 质数下表对照表
     * 以输入作为下标就可以获得最小的小于输入的质数
     */
    private final int[] primeIndexTable = new int[]{
        //  1   2   3   4   5   6   7   8   9   10
            2,  2,  3,  5,  5,  7,  7,  11, 11, 11,  //0
            11, 13, 13, 17, 17, 17, 17, 19, 19, 23,  //1
            23, 23, 23, 29, 29, 29, 29, 29, 29, 31,  //2
            31, 37, 37, 37, 37, 37, 37, 41, 41, 41,  //3
            41, 43, 43, 47, 47, 47, 47, 53, 53, 53,  //4
            53, 53, 53, 59, 59, 59, 59, 59, 59, 61,  //5
            61, 67, 67, 67, 67, 67, 67, 71, 71, 71,  //6
            71, 73, 73, 79, 79, 79, 79, 83, 83, 83,  //7
            83, 83, 83, 89, 89, 89, 89, 89, 89, 97,  //8
            97, 97, 97, 97, 97, 97, 97, 101,101,101, //9
            101,103,103,107,107,107,107,109,109,113, //10
            113,113,113
    };

    /**
     * 最大质数
     */
    private final int maxPrime = 113;

    /**
     * 随机数生成器
     */
    private final Random random;


    /**
     * 读写互斥锁
     */
    private final ReadWriteLock readWriteLock;

    /**
     * 读锁
     */
    private final Lock readLock;

    /**
     * 写锁
     */
    private final Lock writeLock;


    public Trie(){
        root = new TrieNode();
        random = new Random();
        factor = 10;
        readWriteLock = new ReentrantReadWriteLock();
        readLock = readWriteLock.readLock();
        writeLock = readWriteLock.writeLock();
    }

    public Trie(int factor){
        root = new TrieNode();
        random = new Random();
        this.factor = factor;
        readWriteLock = new ReentrantReadWriteLock();
        readLock = readWriteLock.readLock();
        writeLock = readWriteLock.writeLock();
    }

    /**
     * 插入一个单词
     * @param word
     */
    public void add(String word){
        writeLock.lock();
        try {
            TrieNode node = root;
            for (int i = 0; i < word.length(); i++){
                char currentChar = word.charAt(i);
                if (!node.containsKey(currentChar)){
                    node.put(currentChar, new TrieNode());
                }
                node = node.get(currentChar);
            }
            node.setEnd();
        }finally {
            writeLock.unlock();
        }

    }

    /**
     * 插入全部单词
     */
    public void addAll(Collection<String> words) {
        for (String word : words) {
            add(word);
        }
    }

    /**
     * 插入全部单词
     */
    public void addAll(String[] words) {
        for (String word : words) {
            add(word);
        }
    }

    /**
     * 查找一个单词
     * @param word
     * @return
     */
    public TrieNode searchPrefix(String word){
        TrieNode node = root;
        for (int i = 0; i < word.length(); i++){
            char currentChar = word.charAt(i);
            if (node.containsKey(currentChar)){
                node = node.get(currentChar);
            }else {
                return null;
            }
        }
        return node;
    }

    /**
     * 查找一个单词是否存在
     * @param word 单词
     * @return 是否存在
     */
    public boolean search(String word){
        TrieNode node = searchPrefix(word);
        return node != null && node.isEnd();
    }

    /**
     * 查找一个前缀是否存在
     * @param prefix 前缀
     * @return 是否存在
     */
    public boolean startsWith(String prefix){
        TrieNode node = searchPrefix(prefix);
        return node != null;
    }

    /**
     * 根据前缀获取所有的单词
     * @param prefix
     * @limit 限制数量
     * @return
     */
    public List<String> getWordsByPrefix(String prefix, int limit){
        TrieNode node = searchPrefix(prefix);
        List<String> result = new ArrayList<>(limit);
        if (node != null){
            dfs(node, new StringBuilder(prefix), result, limit);
        }
        return result;
    }
    /**
     * 深度优先遍历
     * @param node
     * @param prefix
     * @param result
     * @param limit
     */
    private void dfs(TrieNode node, StringBuilder prefix, List<String> result, int limit){
        if (result.size() >= limit){
            return;
        }
        if (node.isEnd()){
            result.add(prefix.toString());
        }
        for (Character character : node.getChildrenSet()){
            prefix.append(character);
            dfs(node.get(character), prefix, result, limit);
            prefix.deleteCharAt(prefix.length() - 1);
        }
    }

    /**
     * 为指定单词的末位节点增加权重
     */
    public void plusWeight(String word, int weight){
        TrieNode node = root;
        for (int i = 0; i < word.length(); i++) {
            char currentChar = word.charAt(i);
            if (node.containsKey(currentChar)){
                node = node.get(currentChar);
            }
        }
        node.setWeight(node.getWeight() + weight);
    }

    public void plusWeight(TrieNode node, int weight){
        node.setWeight(node.getWeight() + weight);
    }

    /**
     * 深度优先遍历
     * 查找目标数量的10倍
     * 然后根据权重和选择前n个
     */
    public List<String> getWordsByPrefixOrderByWeight(String prefix, int limit) {
        readLock.lock();
        try {
            TrieNode node = searchPrefix(prefix);
            Map<String,Integer> result = new HashMap<>(limit * factor);
            if (node != null){
                dfsOrderByWeight(node, new StringBuilder(prefix), result, limit * factor);
            }
            List<Map.Entry<String, Integer>> list = new ArrayList<>(result.entrySet());
            list.sort((o1, o2) -> o2.getValue() - o1.getValue());
            List<String> resultSet = new ArrayList<>(limit);
            if (list.size() <= limit){
                limit = list.size();
            }
            for (int i = 0; i < limit; i++) {
                resultSet.add(list.get(i).getKey());
            }
            return resultSet;
        }finally {
            readLock.unlock();
        }


    }

    /**
     * 根据长度获取步长
     */
    private int getStep(int length){
        if (length < maxPrime) {
            /**
             * 随机取一个length到maxPrime之间的质数,包含length不包含maxPrime
             * 这段代码是这样的
             * 1.先取一个maxPrime-length+1到maxPrime之间的随机数
             * 2.然后加上length
             * 3.然后取这个数对应的质数
             */
            return primeIndexTable[random.nextInt(maxPrime - length) + length];
        }else {
            /**
             * 现在情况比较复杂
             * 我们需要找出一个比length小的质数
             * 这个质数不可以是length的因数
             * 并且要保证一定的随机性
             * 从primeTable最后一个质数开始遍历，找出5个符合条件的质数
             * 然后随机取一个
             */
            int[] temp = new int[5];
            int count = 0;
            for (int i = primeTable.length - 1; i >= 0; i--) {
                if (length % primeTable[i] != 0){
                    temp[count++] = primeTable[i];
                    if (count >= 5){
                        break;
                    }
                }
            }
            //最糟糕的情况，没有符合条件的质数
            if (count == 0){
                return 1;
            }
            return temp[random.nextInt(count)];
        }
    }

    /**
     * 深度优先遍历
     * @param node 节点
     * @param stringBuilder 前缀
     * @param result 字符串->权重
     * @param limit 限制数量
     */
    private void dfsOrderByWeight(TrieNode node, StringBuilder stringBuilder, Map<String,Integer> result, int limit) {
        if (result.size() >= limit){
            return;
        }
        if (node.isEnd()){
            result.put(stringBuilder.toString(), node.getWeight());
        }
        final Set<Character> childrenSet = node.getChildrenSet();
        final Character[] characterArray = childrenSet.toArray(new Character[0]);
        //获取步长
        int step = getStep(childrenSet.size());
        int index;
        for (int i = 0; i < childrenSet.size(); i++) {
            index = (i * step) % childrenSet.size();
            stringBuilder.append(characterArray[index]);
            dfsOrderByWeight(node.get(characterArray[index]), stringBuilder, result, limit);
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }

    }






//    /**
//     * 深度优先遍历
//     * 查找目标数量的10倍
//     * 然后根据权重和选择前n个
//     */
//    public List<String> getWordsByPrefixOrderByWeight(String prefix, int limit) {
//        TrieNode startNode = searchPrefix(prefix);
//        Map<TrieNode,List<TrieNode>> result = new HashMap<>(limit * factor);
//        if (startNode != null){
//            List<TrieNode> trieNodeList = new ArrayList<>();
//            dfsOrderByWeight(startNode, trieNodeList, result, limit * factor);
//        }
//        //获取前limit个
//        if (result.size() <= limit){
//            limit = result.size();
//        }
//        //排序
//        List<TrieNode> limited = new ArrayList<>(result.keySet());
//        Collections.sort(limited);
//        limited = limited.subList(0, limit);
//        //获取字符串
//        List<String> resultList = new ArrayList<>(limit);
//        for (TrieNode trieNode : limited) {
//            StringBuilder stringBuilder = new StringBuilder(prefix);
//            for (TrieNode node : result.get(trieNode)) {
//                stringBuilder.append(node.getValue());
//            }
//            resultList.add(stringBuilder.toString());
//        }
//
//
//        return resultList;
//    }

    /**
     * 根据前缀获取对应的节点列表
     * @param prefix 前缀
     * @return 节点列表
     */
    private List<TrieNode> getPrefixNodeList(String prefix) {
        List<TrieNode> trieNodeList = new ArrayList<>(prefix.length());
        TrieNode node = root;
        for (int i = 0; i < prefix.length(); i++){
            char currentChar = prefix.charAt(i);
            if (node.containsKey(currentChar)){
                node = node.get(currentChar);
                trieNodeList.add(node);
            }else {
                return trieNodeList;
            }
        }
        return trieNodeList;
    }


    /**
     * 深度优先遍历
     * @param node 节点
     * @param trieNodeList 字符串节点列表
     * @param result 字符串->权重
     * @param limit 限制数量
     */
    private void dfsOrderByWeight(TrieNode node, List<TrieNode> trieNodeList, Map<TrieNode,List<TrieNode>> result, int limit) {

        if (result.size() >= limit){
            return;
        }

        if (node.isEnd()){
            //如果当前节点是一个单词的结尾
            //那么就把当前节点列表加入到结果集中
            //end节点是唯一的，所以不会有重复的问题？
            result.put(node, new ArrayList<>(trieNodeList));
        }
        final Set<Character> childrenSet = node.getChildrenSet();
        final Character[] characterArray = childrenSet.toArray(new Character[0]);
        //获取步长
        int step = getStep(childrenSet.size());
        int index;
        /**
         * 这里为什么没有随机性？
         */
        for (int i = 0; i < childrenSet.size(); i++) {
            index = (i * step) % childrenSet.size();
            trieNodeList.add(node.get(characterArray[index]));
            dfsOrderByWeight(node.get(characterArray[index]), trieNodeList, result, limit);
            trieNodeList.remove(trieNodeList.size() - 1);
        }


    }


    /**
     * 字典树节点
     * utf-8字符集
     * 动态数组
     */
    @EqualsAndHashCode
    public static class TrieNode implements Comparable<TrieNode>,Serializable{

        /**
         * 存储子节点
         */
        private final HashMap<Character, TrieNode> children;


        /**
         * 是否是一个单词的结尾
         */
        private boolean isEnd;

        /**
         * 权重
         */
        private int weight;

//        /**
//         * 值
//         */
//        private char value;

        /**
         * 构造函数
         */
        public TrieNode(){
            children = new HashMap<>();
            isEnd = false;
            weight = 0;
        }

//        /**
//         * 构造函数
//         */
//        public TrieNode(char value){
//            children = new HashMap<>();
//            isEnd = false;
//            weight = 0;
////            this.value = value;
//        }

        public boolean containsKey(char ch){
            return children.containsKey(ch);
        }

        public TrieNode get(Character ch){
            return children.get(ch);
        }

        public Set<Character> getChildrenSet() {
            return children.keySet();
        }

        public void put(Character ch, TrieNode node){
            children.put(ch, node);
        }

        public void setEnd(){
            isEnd = true;
        }

        public boolean isEnd(){
            return isEnd;
        }


        public int getWeight(){
            return weight;
        }

        public void setWeight(int weight){
            if (this.isEnd) {
                this.weight = weight;
            }else {
                throw new IllegalArgumentException("不是一个单词的结尾");
            }
        }

//        public char getValue() {
//            return value;
//        }
//
//        public void setValue(char value) {
//            this.value = value;
//        }




        @Override
        public int compareTo(TrieNode trieNode) {
            return this.weight - trieNode.weight;
        }
    }


//    /**
//     * 测试
//     */
//    public static void main(String[] args) throws IOException {
//        //读取文件
//        File file = new File("C:\\Users\\文轩\\Desktop\\无标题.txt");
//
//        //逐行读取
//        FileReader fileReader = new FileReader(file);
//        BufferedReader bufferedReader = new BufferedReader(fileReader);
//        String line;
//
//        //创建字典树
//        Trie trie = new Trie();
//        try {
//            while ((line = bufferedReader.readLine()) != null){
//                //分词
//                String[] split = line.split(" ");
//                //标点符号分词
//                String[] split1;
//                for (String string : split) {
//                    String[] split2 = string.split("[\\pP\\p{Punct}]");
//                    for (String s : split2) {
//                        trie.add(s);
//                    }
//                }
//            }
//        }catch (Exception e){
//            System.out.println(e);
//        }
//
//        trie.plusWeight("你是否也有为心爱的人坠入黑暗深渊踏入邪恶魔道的决心", 1000);
//
//        //测试
//        long start = System.currentTimeMillis();
//        System.out.println(trie.getWordsByPrefixOrderByWeight("中", 10));
//        System.out.println(trie.getWordsByPrefixOrderByWeight("你是", 10));
//        System.out.println(trie.getWordsByPrefixOrderByWeight("你好", 10));
//        System.out.println(trie.getWordsByPrefixOrderByWeight("你好啊", 10));
//        System.out.println(trie.getWordsByPrefixOrderByWeight("测试", 10));
//        System.out.println(trie.startsWith("你"));
//        long end = System.currentTimeMillis();
//        System.out.println("耗时：" + (end - start) + "ms");
//
//
//        test();
//
//    }
//
//    /**
//     * 测试
//     */
//    public static void test() throws IOException {
//        //读取文件
//        File file = new File("C:\\Users\\文轩\\Desktop\\无标题.txt");
//
//        //逐行读取
//        FileReader fileReader = new FileReader(file);
//        BufferedReader bufferedReader = new BufferedReader(fileReader);
//        String line;
//
//        //创建数组
//        List<String> list = new ArrayList<>(100000);
//        while ((line = bufferedReader.readLine()) != null) {
//            //分词
//            String[] split = line.split(" ");
//            //标点符号分词
//            String[] split1;
//            for (String string : split) {
//                String[] split2 = string.split("[\\pP\\p{Punct}]");
//                for (String s : split2) {
//                    list.add(s);
//                }
//            }
//        }
//
//        Set<String> set1 = new HashSet<>();
//        Set<String> set2 = new HashSet<>();
//        Set<String> set3 = new HashSet<>();
//        Set<String> set4 = new HashSet<>();
//        Set<String> set5 = new HashSet<>();
//        long start = System.currentTimeMillis();
//        for (String string : list) {
//            if (string.startsWith("中删除")) {
//                set1.add(string);
//                if (set1.size() >= 10){
//                    break;
//                }
//            }
//
//        }
//        System.out.println(set1);
//
//
//        for (String string : list) {
//            if (string.startsWith("你")) {
//                set2.add(string);
//                if (set2.size() >= 10){
//                    break;
//                }
//            }
//
//        }
//        System.out.println(set2);
//
//        for (String string : list) {
//            if (string.startsWith("你好")) {
//                set3.add(string);
//                if (set3.size() >= 10){
//                    break;
//                }
//            }
//
//        }
//        System.out.println(set3);
//
//        for (String string : list) {
//            if (string.startsWith("你好啊")) {
//                set4.add(string);
//                if (set4.size() >= 10){
//                    break;
//                }
//            }
//
//        }
//        System.out.println(set4);
//
//        for (String string : list) {
//            if (string.startsWith("测试")) {
//                set5.add(string);
//                if (set5.size() >= 10){
//                    break;
//                }
//            }
//
//        }
//        System.out.println(set5);
//
//        long end = System.currentTimeMillis();
//        System.out.println("耗时：" + (end - start) + "ms");
//    }

}
