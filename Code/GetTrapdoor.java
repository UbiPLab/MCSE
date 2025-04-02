package paper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;


public class GetTrapdoor {
    private static ArrayList<String> symbol = new ArrayList<String>();
    private static BigDecimal R;

    public static void main(String args[]) throws IOException {
        boolean[] indicator = Out.ReadVectorfromFile("F:\\path\\booleanarray.txt");
        BigDecimal M1T[][] = Out.ReadArrayfromFile("F:\\path\\M1T.txt");
        BigDecimal M2T[][] = Out.ReadArrayfromFile("F:\\path\\M2T.txt");//参数为ArrayList中BigDecimal类型向量的大小
        HashMap<String, ArrayList<BigDecimal[]>> Ciphertext = ReadCiphertext();
        HashMap<String, ArrayList<String[]>> ID = Out.ReadHashMapfromFile("F:\\path\\Ciphertext.txt");
//        GetIndex.output2(Ciphertext);
//        System.out.println("----------M1T");
//        System.out.println("----------M2T");
//        GetIndex.outputh(M1T);
//        GetIndex.outputh(M2T);
        Scanner s = new Scanner(System.in);
        System.out.println("请输入搜索关键词：");
        System.out.print("Paper2.Keyword=");
        String n;
        n = s.nextLine();
        long startTime1=System.currentTimeMillis();
        ArrayList<String> FeatureSet = Exfeature(n);
        //System.out.println(FeatureSet);
        ArrayList<BigDecimal> Vector = GetVector(FeatureSet);
        //System.out.println(Vector);
        BigDecimal[] Vector1 = Vector.toArray(new BigDecimal[Vector.size()]);
        //GetIndex.OutVector(Vector1);
        //String ai = Arrays.toString(indicator);
        //System.out.println(ai);
        ArrayList<BigDecimal[]> arraytwo = TrapSplitTwoVector(Vector1, indicator);
        //BigDecimal[] Exvector1 = arraytwo.get(0);
        //BigDecimal[] Exvector2 = arraytwo.get(1);
        //GetIndex.OutVector(Exvector1);
        //GetIndex.OutVector(Exvector2);
        BigDecimal k = new BigDecimal(FeatureSet.size());
        BigDecimal InnerProduct=k.multiply(R);
        ArrayList<BigDecimal[]> E = GetTrapCiphertext(arraytwo,M1T,M2T);
        long endTime1=System.currentTimeMillis();
        System.out.println("程序运行时间： "+ (endTime1 - startTime1)+"ms");
        //BigDecimal[] E1 = E.get(0);
        //BigDecimal[] E2 = E.get(1);
        //GetIndex.OutVector(E1);
        //GetIndex.OutVector(E2);
        long startTime=System.currentTimeMillis();
        ArrayList<String> result = Search(Ciphertext, E, InnerProduct);
        long endTime=System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间： "+ (endTime - startTime)+"ms");
        System.out.println(result);
    }

    public static ArrayList<String> Exfeature(String n) {
        String temp = "0";
        String temp1 = "0";
        String finish = "-";
        ArrayList<String> FeatureSet =new ArrayList<>();
        char[] slist = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '-'};
        String temp3;
        for (int i = 0; i < slist.length; i++) {
            temp3 = Character.toString(slist[i]);
            symbol.add(temp3);
        }
        for (int j = 0; j < n.length(); j++) {
            temp = Character.toString(n.charAt(j));
            if (temp.equals("?")) {

            } else {
                temp1 = String.valueOf(j + 1);
                temp1 = temp.concat(temp1);
                FeatureSet.add(temp1);
            }
        }
        temp = String.valueOf(n.length() + 1);
        temp1 = finish.concat(temp);
        FeatureSet.add(temp1);
        return FeatureSet;
    }

    //将特征集合转化为向量
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
        return array1;
    }

    //将陷门向量每个分量乘一个随机数
    public static BigDecimal[] TrapExvector(BigDecimal[] a) {
        BigDecimal[] b = new BigDecimal[a.length];
        System.arraycopy(a, 0, b, 0, a.length);
        double a1 = new Random().nextDouble() * 7;
        BigDecimal TEMP = GetIndex.Get2Random(a1);
        for (int i = 0; i < a.length; i++) {
            b[i] = b[i].multiply(TEMP);
        }
        return b;
    }

    //分离和扩展向量
    public static ArrayList<BigDecimal[]> TrapSplitTwoVector(BigDecimal[] a, boolean[] indicator) {
        ArrayList<BigDecimal[]> arraytwo = new ArrayList<BigDecimal[]>();
        BigDecimal[] Exvector1 = new BigDecimal[a.length + 1];
        BigDecimal[] Exvector2 = new BigDecimal[a.length + 1];
        double k = new Random().nextDouble()*5;
        R=GetIndex.Get2Random(19+k);
        for (int i = 0; i < a.length; i++) {
            a[i]=a[i].multiply(R);
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

    //得到陷门密文
    public static ArrayList<BigDecimal[]> GetTrapCiphertext(ArrayList<BigDecimal[]> Vector,BigDecimal[][] M1T,BigDecimal[][] M2T) throws IOException {
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

    //读取密文算法
    public static HashMap<String, ArrayList<BigDecimal[]>> ReadCiphertext() {
        HashMap<String, ArrayList<String[]>> Ciphertet = Out.ReadHashMapfromFile("F:\\path\\Ciphertext.txt");
        HashMap<String, ArrayList<BigDecimal[]>> Ciphertext = new HashMap<String, ArrayList<BigDecimal[]>>();
        String temp1;
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


    //搜索算法
    public static ArrayList<String> Search(HashMap<String, ArrayList<BigDecimal[]>> C, ArrayList<BigDecimal[]> T, BigDecimal I) {
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


}