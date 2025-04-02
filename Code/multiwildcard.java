package paper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class multiwildcard {
    private static ArrayList<String> symbol = new ArrayList<String>();
    private static BigDecimal R;

    public static void main(String[] args) throws IOException {
        Scanner s = new Scanner(System.in);
        System.out.println("请输入搜索关键词：");
        String[] a = s.nextLine().split(",");
        //SL是搜索表达式的长度
        int SL = Integer.valueOf(a[1]);
        //record the locations that * occurs
        ArrayList<Integer> location = new ArrayList<Integer>();
        ArrayList<String> Featureset1 = new ArrayList<String>();
        ArrayList<ArrayList<String>> Featureset2 = new ArrayList<ArrayList<String>>();
        ArrayList<BigDecimal> Vector = new ArrayList<BigDecimal>();
        ArrayList<ArrayList<BigDecimal>> MUVector = new ArrayList<ArrayList<BigDecimal>>();
        HashMap<String, ArrayList<String[]>> ID = Out.ReadHashMapfromFile("F:\\path\\Ciphertext.txt");
        boolean[] indicator = Out.ReadVectorfromFile("F:\\path\\booleanarray.txt");
        //输出指示布尔向量
        // String in = Arrays.toString(indicator);
        BigDecimal M1T[][] = Out.ReadArrayfromFile("F:\\path\\M1T.txt");
        BigDecimal M2T[][] = Out.ReadArrayfromFile("F:\\path\\M2T.txt");
        HashMap<String, ArrayList<BigDecimal[]>> Ciphertext = ReadCiphertext();
        ArrayList<BigDecimal[]> arraytwo1 = new ArrayList<BigDecimal[]>();
        ArrayList<BigDecimal[]> arraytwo2 = new ArrayList<BigDecimal[]>();
        ArrayList<ArrayList<BigDecimal[]>> C = new ArrayList<ArrayList<BigDecimal[]>>();
        ArrayList<BigDecimal[]> E = new ArrayList<BigDecimal[]>();
        BigDecimal InnerProduct = new BigDecimal("0");
        int count = 0;
        int k = 0;
        char[] slist = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '-'};
        //char[] slist = {'a', 'b', 'c', 'd', '-'};
        String temp3;
        for (int i = 0; i < slist.length; i++) {
            temp3 = Character.toString(slist[i]);
            symbol.add(temp3);
        }
        for (int i = 0; i < a[0].length(); i++) {
            if (a[0].charAt(i) == '*') {
                count++;
                location.add(i);
            }
            k++;
        }
        double k1 = new Random().nextDouble() * 5;
        R = GetIndex.Get2Random(19 + k1);
        if (count == 0 || count == 1) {
            //提取特征
            long startTime1=System.currentTimeMillis();
            Featureset1 = ExFeature1(count, location, a[0], SL);

            //生成向量

            Vector = GetVector(Featureset1);

            BigDecimal[] Vector1 = Vector.toArray(new BigDecimal[Vector.size()]);
            //分离和扩展向量

            arraytwo1 = TrapSplitTwoVector(Vector1, indicator);

            //BigDecimal[] Exvector1 = arraytwo1.get(0);
            //BigDecimal[] Exvector2 = arraytwo1.get(1);
            BigDecimal m = new BigDecimal(Featureset1.size());
            InnerProduct = m.multiply(R);
            //生成向量密文

            E = GetTrapCiphertext(arraytwo1, M1T, M2T);
            long endTime1=System.currentTimeMillis();
            System.out.println("程序运行时间： "+ (endTime1 - startTime1)+"ms");
            /*BigDecimal[] E1 = E.get(0);
            System.out.println(Arrays.toString(E1));
            BigDecimal[] E2 = E.get(1);
            System.out.println(Arrays.toString(E2));*/

        }
        if (count == 2) {
            //提取特征
            long startTime11=System.currentTimeMillis();
            Featureset2 = ExFeature2(count, location, a[0], SL);

            BigDecimal m = new BigDecimal(Featureset2.get(0).size());
            InnerProduct = m.multiply(R);
            //生成向量

            for (ArrayList<String> tmp : Featureset2) {
                MUVector.add(GetVector(tmp));
            }

            //output2(MUVector);
            System.out.println("---------------");
            //分离、扩展、加密向量
            for (ArrayList<BigDecimal> tmp : MUVector) {
                BigDecimal[] Vector2 = tmp.toArray(new BigDecimal[tmp.size()]);
                arraytwo2 = TrapSplitTwoVector(Vector2, indicator);
                /*BigDecimal[] Exvector1 = arraytwo1.get(0);
                BigDecimal[] Exvector2 = arraytwo1.get(1);*/
                ArrayList<BigDecimal[]> K = GetTrapCiphertext(arraytwo2, M1T, M2T);
                /*BigDecimal[] E1 = E.get(0);
                BigDecimal[] E2 = E.get(1);*/
                C.add(K);
            }
            long endTime11=System.currentTimeMillis();
            System.out.println("程序运行时间： "+ (endTime11 - startTime11)+"ms");
            //output3(C);

        }
        ArrayList<String> result = new ArrayList<String>();
        if (count == 0 || count == 1) {
            long startTime2=System.currentTimeMillis();
            result = Search1(Ciphertext, E, InnerProduct);
            long endTime2=System.currentTimeMillis();
            System.out.println("程序运行时间： "+ (endTime2 - startTime2)+"ms");
        }
        if (count == 2) {
            long startTime3=System.currentTimeMillis();
            result = Search2(Ciphertext, C, InnerProduct);
            long endTime3=System.currentTimeMillis();
            System.out.println("程序运行时间： "+ (endTime3 - startTime3)+"ms");
        }
        System.out.println("search result: "+result);

    }

    //矩阵与向量的乘法
    public static BigDecimal[] Mutiply(BigDecimal[][] a, BigDecimal[] Vector) {
        int size = Vector.length;
        BigDecimal[] E = new BigDecimal[size];
        BigDecimal Z = new BigDecimal("0");
        for (int i = 0; i < Vector.length; i++) {
            E[i] = Z;
            for (int j = 0; j < Vector.length; j++) {
                E[i] = E[i].add(Vector[j].multiply(a[i][j]));
            }
        }
        return E;
    }

    //得到陷门密文
    public static ArrayList<BigDecimal[]> GetTrapCiphertext(ArrayList<BigDecimal[]> Vector, BigDecimal[][] M1T, BigDecimal[][] M2T) throws IOException {
        int size;
        size = Vector.get(0).length;
        ArrayList<BigDecimal[]> Encrypt2 = new ArrayList<BigDecimal[]>();
        BigDecimal[] E1 = new BigDecimal[size];
        BigDecimal[] E2 = new BigDecimal[size];
        E1 = Mutiply(M1T, Vector.get(0));
        E2 = Mutiply(M2T, Vector.get(1));
        Encrypt2.add(E1);
        Encrypt2.add(E2);
        return Encrypt2;
    }

    public static ArrayList<BigDecimal[]> TrapSplitTwoVector(BigDecimal[] a, boolean[] indicator) {
        ArrayList<BigDecimal[]> arraytwo = new ArrayList<BigDecimal[]>();
        BigDecimal[] Exvector1 = new BigDecimal[a.length + 1];
        BigDecimal[] Exvector2 = new BigDecimal[a.length + 1];
        /**/
        for (int i = 0; i < a.length; i++) {
            a[i] = a[i].multiply(R);
        }
        double b;
        for (int i = 0; i < a.length; i++) {
            if (!indicator[i])
                Exvector1[i] = Exvector2[i] = a[i];
            else {
                b = new Random().nextDouble() * 3;
                Exvector1[i] = GetIndex.Get2Random(b);
                Exvector2[i] = a[i].subtract(Exvector1[i]);
            }
        }
        Exvector1[a.length] = GetIndex.Get2Random(new Random().nextDouble() * 3);
        Exvector2[a.length] = GetIndex.Get2Random(new Random().nextDouble() * 3);
        arraytwo.add(Exvector1);
        arraytwo.add(Exvector2);
        return arraytwo;
    }

    public static ArrayList<BigDecimal> GetVector(ArrayList<String> FeatureSet) {
        ArrayList<BigDecimal> array1 = new ArrayList<BigDecimal>();
        String temp3 = "0";
        BigDecimal Z = new BigDecimal("0");
        BigDecimal O = new BigDecimal("1");
        for (int j = 0; j < symbol.size(); j++) {
            for (int k = 1; k < GetIndex.L + 1; k++) {
                temp3 = symbol.get(j).concat(String.valueOf(k));
                if (FeatureSet.contains(temp3)) {
                    array1.add(O);
                } else array1.add(Z);
            }
        }
        //System.out.println(array1);
        return array1;
    }

    public static ArrayList<String> ExFeature1(int count, ArrayList<Integer> location, String A, Integer N) {
        String temp = "0";
        String temp1 = "0";
        String temp2 = "0";
        String finish = "-";
        ArrayList<String> FeatureSet = new ArrayList<String>();
        if (count == 0) {
            for (int i = 0; i <= A.length() - 1; i++) {
                temp = Character.toString(A.charAt(i));
                temp1 = String.valueOf(i + 1);
                temp1 = temp.concat(temp1);
                FeatureSet.add(temp1);
            }
            temp1=String.valueOf(N+1);
            temp1=finish.concat(temp1);
            FeatureSet.add(temp1);
        }
        if (count == 1) {
            int j = location.get(0);
            int RL = N - A.length();
            for (int i = 0; i <= j - 1; i++) {
                temp = Character.toString(A.charAt(i));
                temp1 = String.valueOf(i + 1);
                temp1 = temp.concat(temp1);
                FeatureSet.add(temp1);
            }

            for (int i = j + 1; i < A.length(); i++) {
                temp = Character.toString(A.charAt(i));
                temp1 = String.valueOf(i + RL + 1);
                temp1 = temp.concat(temp1);
                FeatureSet.add(temp1);
            }
            temp2 = String.valueOf(N + 1);
            temp1 = finish.concat(temp2);
            FeatureSet.add(temp1);
        }
        //output2(FeatureSet);
        //System.out.println(FeatureSet);
        return (FeatureSet);
    }

    public static ArrayList<ArrayList<String>> ExFeature2(int count, ArrayList<Integer> location, String A, Integer N) {
        String temp = "0";
        String temp1 = "0";
        String temp2 = "0";
        String finish = "-";
        ArrayList<String> FeatureSet = new ArrayList<String>();
        ArrayList<ArrayList<String>> Trapdoors = new ArrayList<ArrayList<String>>();
        int RL = N - A.length();
        int p = RL + 2;
        int j1 = location.get(0);
        int j2 = location.get(1);
        for (int i = 0; i <= j1 - 1; i++) {
            temp = Character.toString(A.charAt(i));
            temp1 = String.valueOf(i + 1);
            temp1 = temp.concat(temp1);
            FeatureSet.add(temp1);
        }
        for (int i = j2 + 1; i <= A.length() - 1; i++) {
            temp = Character.toString(A.charAt(i));
            temp1 = String.valueOf(i + 1 + RL);
            temp1 = temp.concat(temp1);
            FeatureSet.add(temp1);
        }
        temp2 = String.valueOf(N + 1);
        temp1 = finish.concat(temp2);
        FeatureSet.add(temp1);
        for (int q = 0; q <= p; q++) {
            ArrayList<String> Features = new ArrayList<>();
            for (int i = j1 + 1; i <= j2 - 1; i++) {
                temp = Character.toString(A.charAt(i));
                temp1 = String.valueOf(i + q);
                temp1 = temp.concat(temp1);
                Features.add(temp1);
            }
            Features.addAll(FeatureSet);
            Trapdoors.add(Features);
        }
        //output1(Trapdoors);
        return (Trapdoors);
    }

    public static HashMap<String, ArrayList<BigDecimal[]>> ReadCiphertext() {
        HashMap<String, ArrayList<String[]>> Ciphertet = Out.ReadHashMapfromFile("F:\\path\\Ciphertext.txt");
        HashMap<String, ArrayList<BigDecimal[]>> Ciphertext = new HashMap<String, ArrayList<BigDecimal[]>>();
        String[] temp2;
        BigDecimal[] temp3;
        BigDecimal[] temp4;
        for (String word : Ciphertet.keySet()) {
            ArrayList<BigDecimal[]> temp = new ArrayList<BigDecimal[]>();
            ArrayList<String[]> value = Ciphertet.get(word);
            temp2 = value.get(0);
            temp3 = new BigDecimal[temp2.length];
            for (int i = 0; i < temp2.length; i++) {
                temp3[i] = new BigDecimal(temp2[i]);
            }
            temp2 = value.get(1);
            temp4 = new BigDecimal[temp2.length];
            for (int i = 0; i < temp2.length; i++) {
                temp4[i] = new BigDecimal(temp2[i]);
            }
            temp.add(temp3);
            temp.add(temp4);
            Ciphertext.put(word, temp);
        }
        return Ciphertext;
    }


    //output the ArrayList< ArrayList<String>>
    public static void output1(ArrayList<ArrayList<String>> Trapdoors) {
        for (ArrayList TMP : Trapdoors) {
            System.out.println(TMP);
        }
    }

    public static void output2(ArrayList<ArrayList<BigDecimal>> Trapdoors) {
        for (ArrayList TMP : Trapdoors) {
            System.out.println(TMP);
        }
    }

    public static void output3(ArrayList<ArrayList<BigDecimal[]>> K) {
        for (ArrayList<BigDecimal[]> TMP : K) {
            System.out.println(Arrays.toString(TMP.get(0)));
            System.out.println(Arrays.toString(TMP.get(1)));
        }
    }

    public static ArrayList<String> Search1(HashMap<String, ArrayList<BigDecimal[]>> C, ArrayList<BigDecimal[]> T, BigDecimal I) {
        ArrayList<BigDecimal[]> temp;
        ArrayList<String> result = new ArrayList<String>();
        BigDecimal[] temp1 = T.get(0);
        BigDecimal[] temp2 = T.get(1);
        BigDecimal[] temp3;
        BigDecimal[] temp4;
        BigDecimal temp5;
        BigDecimal temp6;
        BigDecimal temp7;
        BigDecimal O = new BigDecimal("1");
        int size = temp1.length;
        for (String word : C.keySet()) {
            BigDecimal temps = new BigDecimal("0");
            temp = C.get(word);
            temp3 = temp.get(0);
            temp4 = temp.get(1);
            for (int i = 0; i < size; i++) {
                temp5 = temp1[i].multiply(temp3[i]);
                temp6 = temp2[i].multiply(temp4[i]);
                temp7 = temp5.add(temp6);
                temps = temps.add(temp7);
            }
            if (temps.compareTo(I) == 1) {
                result.add(word);
            }
        }
        return result;
    }

    public static ArrayList<String> Search2(HashMap<String, ArrayList<BigDecimal[]>> C, ArrayList<ArrayList<BigDecimal[]>> T, BigDecimal I) {
        ArrayList<BigDecimal[]> temp;
        ArrayList<String> result = new ArrayList<String>();
        for (ArrayList<BigDecimal[]> tmp : T) {
            BigDecimal[] temp1 = tmp.get(0);
            BigDecimal[] temp2 = tmp.get(1);
            BigDecimal[] temp3;
            BigDecimal[] temp4;
            BigDecimal temp5;
            BigDecimal temp6;
            BigDecimal temp7;
            BigDecimal O = new BigDecimal("1");
            int size = temp1.length;
            for (String word : C.keySet()) {
                BigDecimal temps = new BigDecimal("0");
                temp = C.get(word);
                temp3 = temp.get(0);
                temp4 = temp.get(1);
                for (int i = 0; i < size; i++) {
                    temp5 = temp1[i].multiply(temp3[i]);
                    temp6 = temp2[i].multiply(temp4[i]);
                    temp7 = temp5.add(temp6);
                    temps = temps.add(temp7);
                }
                if (temps.compareTo(I) == 1) {
                    result.add(word);
                }
            }
        }
        result = new ArrayList<>(new HashSet<>(result));
        return result;
    }

}