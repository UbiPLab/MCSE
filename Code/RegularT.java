package paper;

import jdk.swing.interop.SwingInterOpUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class RegularT {
    private static BigDecimal R;
    private static ArrayList<String> symbol = new ArrayList<String>();
    public static void main(String[] args) throws IOException {
        boolean[] indicator = Out.ReadVectorfromFile("F:\\path\\booleanarray.txt");
        BigDecimal M1T[][] = Out.ReadArrayfromFile("F:\\path\\M1T.txt");
        BigDecimal M2T[][] = Out.ReadArrayfromFile("F:\\path\\M2T.txt");
        HashMap<String, ArrayList<BigDecimal[]>> Ciphertext = multiwildcard.ReadCiphertext();
        Scanner s = new Scanner(System.in);
        System.out.println("请输入搜索关键词：");
        String[] a = s.nextLine().split(",");
        int SL = Integer.valueOf(a[1]);
        int length = a[0].length();
        ArrayList<String> Feature3 =ExFeature(a[0]);
        double k1 = new Random().nextDouble() * 5;
        R = GetIndex.Get2Random(19 + k1);
        BigDecimal m = new BigDecimal(SL+1);
        BigDecimal InnerProduct = m.multiply(R);
        char[] slist = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '-'};
        String temp3;
        for (int i = 0; i < slist.length; i++) {
            temp3 = Character.toString(slist[i]);
            symbol.add(temp3);
        }
        //System.out.println(Feature3);
        ArrayList<BigDecimal> Vector = GetVector(Feature3);
        BigDecimal[] Vector1 = Vector.toArray(new BigDecimal[Vector.size()]);
        ArrayList<BigDecimal[]> arraytwo1 = TrapSplitTwoVector(Vector1, indicator);
        ArrayList<BigDecimal[]> E = multiwildcard.GetTrapCiphertext(arraytwo1, M1T, M2T);
        /*BigDecimal[] E1 = E.get(0);
        System.out.println(Arrays.toString(E1));
        BigDecimal[] E2 = E.get(1);
        System.out.println(Arrays.toString(E2));*/
        ArrayList<String> result = Search1(Ciphertext, E, InnerProduct);
        //System.out.println(result);
    }

    public static ArrayList<String> ExFeature(String a){
        ArrayList<String> Feature3 = new ArrayList<>();
        String temp = "0";
        int number = 1;
        char[] slist = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        ArrayList<String> TEMP = new ArrayList<>();
        String temp1 = "0";
        String finish = "-";
        for (int i = 0; i < a.length(); i++) {
            if (a.charAt(i) == '(') {
                i++;
                while (a.charAt(i) != ')') {
                    temp = Character.toString(a.charAt(i));
                    temp1 = String.valueOf(number);
                    temp1 = temp.concat(temp1);
                    Feature3.add(temp1);
                    i++;
                }
            } else if (a.charAt(i) == '[') {
                while (a.charAt(i) != ']') {
                    temp = Character.toString(a.charAt(i));
                    temp1 = String.valueOf(number);
                    temp1 = temp.concat(temp1);
                    TEMP.add(temp1);
                    i++;
                }
                for (int j = 0; j < slist.length; j++) {
                    temp = Character.toString(slist[j]);
                    temp1 = String.valueOf(number);
                    temp1 = temp.concat(temp1);
                    Feature3.add(temp1);
                }
                Feature3.removeAll(TEMP);
            } else {
                temp = Character.toString(a.charAt(i));
                temp1 = String.valueOf(number);
                temp1 = temp.concat(temp1);
                Feature3.add(temp1);
            }
            number++;
        }
        temp1 = String.valueOf(number);
        temp1 = finish.concat(temp1);
        Feature3.add(temp1);
        return Feature3;
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

    public static ArrayList<BigDecimal[]> GetTrapCiphertext(ArrayList<BigDecimal[]> Vector, BigDecimal[][] M1T, BigDecimal[][] M2T) throws IOException {
        int size;
        size = Vector.get(0).length;
        ArrayList<BigDecimal[]> Encrypt2 = new ArrayList<BigDecimal[]>();
        BigDecimal[] E1 = Mutiply(M1T, Vector.get(0));
        BigDecimal[] E2 = Mutiply(M2T, Vector.get(1));
        Encrypt2.add(E1);
        Encrypt2.add(E2);
        return Encrypt2;
    }

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

}

