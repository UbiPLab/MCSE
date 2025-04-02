package paper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class Out {

    public static void WriteArraytoFile(BigDecimal[][] A, String I) throws IOException {
        File file = new File(I); //存放数组数据的文件
        FileWriter out = new FileWriter(file);
        int n = A[0].length;
        BigDecimal[][] arr = new BigDecimal[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                out.write(A[i][j] + "\t");
            }
            out.write("\r\n");
        }
        out.close();
    }

    public static void WriteVectortoFile(boolean[] A, String I) throws IOException {
        File file = new File(I); //存放数组数据的文件
        FileWriter out = new FileWriter(file);
        int n = A.length;
        boolean[] arr = new boolean[n];
        for (int i = 0; i < n; i++) {
            out.write(A[i] + "\t");
        }
        out.close();
    }

    public static boolean[] ReadVectorfromFile(String I) throws IOException {
        File file = new File(I);
        boolean[] arr2 ; //读取出的数组
        BufferedReader in = new BufferedReader(new FileReader(file)); //
        String line; //一行数据
        int size = 0;
        if ((line = in.readLine()) != null) {
            String[] temp = line.split("\t");
            size = temp.length;
            arr2= new boolean[size];
            for (int j = 0; j < temp.length; j++) {
                if (temp[j].equals("true") )
                    arr2[j] = true;
                else arr2[j] = false;
            }
        } else {
            arr2=new boolean [1];
            arr2[0]=false;
            System.out.println("------------------------报错");
        }
        int row = 0;
        in.close();
        return arr2;
    }


    //读取文件时，要是不知道数组大小可以用arraylist
    public static BigDecimal[][] ReadArrayfromFile(String I) throws IOException {
        File file = new File(I);
        BufferedReader in = new BufferedReader(new FileReader(file)); //
        String line; //一行数据
        int row = 0;
        int size = 0;
        double temp2;
        BigDecimal[][] arr2;
        if ((line = in.readLine()) != null) {
            String[] temp = line.split("\t");
            size = temp.length;
            arr2 = new BigDecimal[size][size]; //读取出的数组
            for (int j = 0; j < size; j++) {
                //temp2 = Double.parseDouble(temp[j]);
                arr2[row][j] = new BigDecimal(temp[j]);
            }
            row++;
            //逐行读取，并将每个数组放入到数组中
            while ((line = in.readLine()) != null) {
                String[] temp1 = line.split("\t");
                for (int j = 0; j < size; j++) {
                    //temp2 = Double.parseDouble(temp1[j]);
                    arr2[row][j] = new BigDecimal(temp1[j]);
                }
                row++;
            }
        } else {
            System.out.println("------------------------报错");
            arr2 = new BigDecimal[1][1];
            arr2[0][0] = new BigDecimal("0");
        }
        in.close();
        return arr2;
    }

    public static void WriteHashMaptoFile(HashMap<String, ArrayList<BigDecimal[]>> map, String filename) {
        int temp;
        if (map != null) {
            try (FileWriter writer = new FileWriter(filename)) {
                for (String key : map.keySet()) {
                    ArrayList<BigDecimal[]> value = map.get(key);
                    writer.write(key.trim());
                    writer.write('\n');
                    temp = value.get(0).length;
                    for (int i = 0; i < temp; i++) {
                        writer.write(value.get(0)[i] + "\t");
                    }
                    writer.write('\n');
                    for (int i = 0; i < temp; i++) {
                        writer.write(value.get(1)[i] + "\t");
                    }
                    writer.write('\n');
                }
                writer.close();
            } catch (IOException e) {
                System.out.println("Issue: " + filename +
                        " in listaMap");
            }
        } else {
            System.out.println("Null map passed to listaMap.");
        }
    }

    public static HashMap<String, ArrayList<String[]>> ReadHashMapfromFile(String filename) {
        HashMap<String, ArrayList<String[]>> map = new HashMap<String, ArrayList<String[]>>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String word;
            word = reader.readLine();
            while(word != null)  {
                ArrayList<String[]> a=new ArrayList<String[]>();
                String[] response = reader.readLine().split("\t");
                String[] response1 = reader.readLine().split("\t");
                a.add(response);
                a.add(response1);
                map.put(word,a);
                word = reader.readLine();
            }
        } catch (IOException e) {
            System.out.println("Problem reading file: " + filename +
                    " in LookatM");
        }
        return map;
    }
}
