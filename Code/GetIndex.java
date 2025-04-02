package paper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.*;

public class GetIndex {
    public static int L = 16;
    public static ArrayList<String> symbol = new ArrayList<String>();
    public static boolean[] indicator;

    public static void main(String[] args) throws IOException {
        //set the lengthest is L
        String file = "F:\\path\\file.txt";
        ArrayList<String> cutwords = ExKeyword(file);
        long startTime1=System.currentTimeMillis();
        HashMap<String, ArrayList<String>> KeyFeatures = ExFeature(cutwords);
        //long endTime1=System.currentTimeMillis(); //获取结束时间
        //System.out.println("特征提取运行时间： "+ (endTime1 - startTime1)+"ms");
        //long startTime2=System.currentTimeMillis();
        HashMap<String, ArrayList<BigDecimal>> Vector = GetVector(KeyFeatures);
        long endTime1=System.currentTimeMillis(); //获取结束时间
        //System.out.println("生成向量运行时间： "+ (endTime2 - startTime2)+"ms");
        System.out.println("原索引运行时间： "+ (endTime1 - startTime1)+"ms");
        long startTime3=System.currentTimeMillis();
        HashMap<String, ArrayList<BigDecimal[]>> IndexExvector = IndexExvector(Vector);
        //long endTime3=System.currentTimeMillis(); //获取结束时间
        //System.out.println("分离和扩充向量运行时间： "+ (endTime3 - startTime3)+"ms");
        //long startTime4=System.currentTimeMillis();
        HashMap<String, ArrayList<BigDecimal[]>> Ciphertext = GetVectorCiphertext(IndexExvector);//
        long endTime3=System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间： "+ (endTime3 - startTime3)+"ms");
        System.out.println("--------------");
        //output(KeyFeatures);
        //output1(Vector);
        //output2(IndexExvector);
        //output2(Ciphertext);
        Out.WriteHashMaptoFile(Ciphertext, "F:\\path\\Ciphertext.txt");

    }

    //extract keywords from a file and put them in ArrayList
    public static ArrayList<String> ExKeyword(String dirc) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(dirc));
        ArrayList<String> cutwords = new ArrayList<String>();
        String readLine = null;
        while ((readLine = br.readLine()) != null) {
            String[] wordsArr1 = readLine.split("[^a-zA-Z]");  //过滤出只含有字母的
            for (String word : wordsArr1) {
                if (word.length() > 1) {  //去除长number, dict度为0的行
                    cutwords.add(word);
                }
            }
        }
        return cutwords;
    }

    //extract features from all keywords
    public static HashMap<String, ArrayList<String>> ExFeature(ArrayList<String> keyword) {
        String[] a = keyword.toArray(new String[keyword.size()]);
        HashMap<String, ArrayList<String>> KeyFeatures = new HashMap<String, ArrayList<String>>();
        String temp = "0";
        String temp1;
        String finish = "-";
        String temp2 = "0";
        char temp0 = '0';
        for (int i = 0; i < a.length; i++) {
            temp2 = a[i];
            ArrayList<String> Features = new ArrayList<String>();
            for (int j = 0; j < a[i].length(); j++) {
                temp = Character.toString(temp2.charAt(j));
                temp1 = String.valueOf(j + 1);
                temp1 = temp.concat(temp1);
                Features.add(temp1);
            }
            temp2 = String.valueOf(a[i].length() + 1);
            temp1 = finish.concat(temp2);
            Features.add(temp1);
            KeyFeatures.put(a[i], Features);
        }
        return KeyFeatures;
    }

    //generate vectors for all keyword
    public static HashMap<String, ArrayList<BigDecimal>> GetVector(HashMap<String, ArrayList<String>> KeyFeatures) {
        char[] slist = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '-'};
        //char[] slist = {'a', 'b', 'c', 'd','-'};
        HashMap<String, ArrayList<BigDecimal>> Vector = new HashMap<String, ArrayList<BigDecimal>>();
        String temp;
        for (int i = 0; i < slist.length; i++) {
            temp = Character.toString(slist[i]);
            symbol.add(temp);
        }
        Iterator iter = KeyFeatures.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entrys = (Map.Entry) iter.next();
            String word = entrys.getKey().toString();
            ArrayList<String> array = (ArrayList<String>) entrys.getValue();
            ArrayList<BigDecimal> array1 = new ArrayList<BigDecimal>();
            BigDecimal Z = new BigDecimal("0");
            BigDecimal O = new BigDecimal("1");
            String temp3 = "0";
            for (int j = 0; j < symbol.size(); j++) {
                for (int k = 1; k < L + 1; k++) {
                    temp3 = symbol.get(j).concat(String.valueOf(k));
                    if (array.contains(temp3)) {
                        array1.add(O);
                    } else array1.add(Z);
                }
            }
            Vector.put(word, array1);
        }
        return Vector;
    }

    //分离和扩展向量
    public static HashMap<String, ArrayList<BigDecimal[]>> IndexExvector(HashMap<String, ArrayList<BigDecimal>> vector) throws IOException {
        HashMap<String, ArrayList<BigDecimal[]>> a = new HashMap<String, ArrayList<BigDecimal[]>>();
        Map.Entry entry = vector.entrySet().iterator().next();
        String key= (String)entry.getKey();
        ArrayList<BigDecimal> value=(ArrayList<BigDecimal>)entry.getValue();
        boolean[] indicator=new boolean[value.size()];
        for (int i = 0; i < indicator.length; i++) {
            indicator[i] = new Random().nextBoolean();
        }
        //OutVector(indicator);
        Out.WriteVectortoFile(indicator, "F:\\path\\booleanarray.txt");
        Set set = vector.keySet();
        Iterator it = set.iterator();
        Iterator iter = vector.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entrys = (Map.Entry) iter.next();
            String word = entrys.getKey().toString();
            ArrayList<BigDecimal> array = (ArrayList<BigDecimal>) entrys.getValue();
            BigDecimal[] Vector1 = array.toArray(new BigDecimal[array.size()]);
            ArrayList<BigDecimal[]> arraytwo = new ArrayList<BigDecimal[]>();
            arraytwo = IndexSplitTwoVector(Vector1, indicator);
            a.put(word, arraytwo);
        }

        return a;
    }

    //向量与矩阵相乘生成向量密文
    public static HashMap<String, ArrayList<BigDecimal[]>> GetVectorCiphertext(HashMap<String, ArrayList<BigDecimal[]>> Vector) throws IOException {
        Set set = Vector.keySet();
        Iterator it = set.iterator();
        Iterator iter = Vector.entrySet().iterator();
        int size;
        //没有判断it.HasNext()，当没有值时有可能会出错
        ArrayList<BigDecimal[]> TEMP = Vector.get(it.next());
        size = TEMP.get(0).length;
        BigDecimal[][] M1 = Out.ReadArrayfromFile("F:\\path\\M11.txt");
        BigDecimal[][] M2 = Out.ReadArrayfromFile("F:\\path\\M22.txt");
        HashMap<String, ArrayList<BigDecimal[]>> a = new HashMap<String, ArrayList<BigDecimal[]>>();
        BigDecimal[] E1 = new BigDecimal[size];
        BigDecimal[] E2 = new BigDecimal[size];
        while (iter.hasNext()) {
            ArrayList<BigDecimal[]> Encrypt2 = new ArrayList<BigDecimal[]>();
            Map.Entry entrys = (Map.Entry) iter.next();
            String word = entrys.getKey().toString();
            ArrayList<BigDecimal[]> arraytwo = (ArrayList<BigDecimal[]>) entrys.getValue();
            E1 = mutiply(arraytwo.get(0), M1);
            E2 = mutiply(arraytwo.get(1), M2);
            Encrypt2.add(E1);
            Encrypt2.add(E2);
            a.put(word, Encrypt2);
        }
        return a;

    }

    //split algorithm of index build in Wildcard keyword search
    public static ArrayList<BigDecimal[]> IndexSplitTwoVector(BigDecimal[] a, boolean[] indicator) {
        ArrayList<BigDecimal[]> arraytwo = new ArrayList<BigDecimal[]>();
        BigDecimal[] Exvector1 = new BigDecimal[a.length + 1];
        BigDecimal[] Exvector2 = new BigDecimal[a.length + 1];
        double b;
        for (int i = 0; i < a.length; i++) {
            if (indicator[i])
                Exvector1[i] = Exvector2[i] = a[i];
            else {
                b = new Random().nextDouble() * 3;
                Exvector1[i] = Get2Random(b);
                Exvector2[i] = a[i].subtract(Exvector1[i]);
            }
        }
        Exvector1[a.length] = Get2Random(new Random().nextDouble() * 3);
        Exvector2[a.length] = Get2Random(new Random().nextDouble() * 3);
        arraytwo.add(Exvector1);
        arraytwo.add(Exvector2);
        return arraytwo;
    }

    //将多位小数截取两个精度的小数
    public static BigDecimal Get2Random(double a) {
        String result;
        result = String.format("%.2f", a);
        BigDecimal b = new BigDecimal(result);
        return b;
        //Double doubleString= Double.parseDouble(result);
        //return doubleString;
    }

    //向量与矩阵乘法
    public static BigDecimal[] mutiply(BigDecimal evector[], BigDecimal Matrix[][]) {
        BigDecimal arr[] = new BigDecimal[evector.length];
        BigDecimal Z = new BigDecimal("0");
        for (int i = 0; i < evector.length; i++) {
            arr[i] = Z;
            for (int j = 0; j < evector.length; j++) {
                arr[i] = arr[i].add(evector[j].multiply(Matrix[j][i]));
            }
        }
        return arr;
    }

    //输出布尔向量
    public static void OutVector(boolean[] a) {
        String ai = Arrays.toString(a);
        System.out.println(ai);
    }

    //输出BigDecimal向量
    public static void OutVector(BigDecimal[] a) {
        String ai = Arrays.toString(a);
        System.out.println(ai);
    }

    //输出矩阵
    static void outputh(BigDecimal[][] a) {
        for (int x = 0; x < a.length; x++) {
            for (int y = 0; y < a.length; y++) {
                System.out.print(a[x][y] + "  ");
            }
            System.out.println();
        }
    }

    //output the hashmap
    public static void output1(HashMap<String, ArrayList<BigDecimal>> Vector) {
        Iterator iter = Vector.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entrys = (Map.Entry) iter.next();
            System.out.print(entrys.getKey().toString() + " ");
            System.out.println(entrys.getValue());
        }
    }

    public static void output(HashMap<String, ArrayList<String>> Vector) {
        Iterator iter = Vector.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entrys = (Map.Entry) iter.next();
            System.out.print(entrys.getKey().toString() + " ");
            System.out.println(entrys.getValue());
        }
    }

    public static void output2(HashMap<String, ArrayList<BigDecimal[]>> Vector) {
        Iterator iter = Vector.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entrys = (Map.Entry) iter.next();
            System.out.print(entrys.getKey().toString() + " ");
            ArrayList<BigDecimal[]> temp = (ArrayList<BigDecimal[]>) entrys.getValue();

            System.out.println(Arrays.toString(temp.get(1)));
        }
    }

}

