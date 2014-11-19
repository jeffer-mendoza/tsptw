/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package command;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author jeffermh
 */
public class Command {

    private static String s;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        File fileInput = new File("./input.txt");
        FileReader fr = new FileReader(fileInput);
        BufferedReader br = new BufferedReader(fr);



        String linea;
        int n = 0;//total de nodos
        int count = 0;//variable que cuenta el numero de ciclos
        int nodos[][] = null;
        int distancias[][] = null;
        int i;
        String celdas[] = null;//variable temporal para guardas los datos de la n-esima linea
        try {
            while ((linea = br.readLine()) != null) {
                if (linea.equals("f")) {
                    break;
                }
                if (count == 0) {//leer solo la primera linea
                    n = Integer.parseInt(linea);//asignar el numero de notos
                    nodos = new int[n][3];
                    distancias = new int[n * (n - 1)][n * (n - 1)];
                    count++;
                    continue;
                }
                if (count < n + 1) {
                    celdas = linea.split("\\ ");//orden:{i, ts, ai, bi}
                    i = Integer.parseInt(celdas[0]);//nodo n-esimo
                    nodos[i][0] = Integer.parseInt(celdas[1]);//primer columna  - ts
                    nodos[i][1] = Integer.parseInt(celdas[2]);//segunda columna - ai
                    nodos[i][2] = Integer.parseInt(celdas[3]);//tercer columna -bi
                } else {
                    celdas = linea.split("\\ ");//orden:{i, j, tdij}
                    distancias[Integer.parseInt(celdas[0])][Integer.parseInt(celdas[1])] = Integer.parseInt(celdas[2]);
                }
                count++;
            }
            generatorLP(n, distancias, nodos);//generar el archivo lp 

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fr) {
                    fr.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    /**
     * funcion utilizada para procesar los array que arrojo el archivo de
     * entrada y lo convierte en un archivo lp, que contine el modelo y las
     * restricciones
     *
     * @param n
     * @param distancias
     * @param nodos
     * @throws IOException
     */
    public static void generatorLP(int n, int distancias[][], int nodos[][]) throws IOException {
        //declaración de todas las variables que se usaran
        String td, a, b, ts, xij, xji, u, h_b, M, pi, p;
        td = a = b = ts = xij = xji = u = h_b = M = pi = p = "";
        String t = "h" + n;//igualacion de t = la n-isma h
        int j_;//variable articial para manejar las restricciones del M grande
        for (int i = 0; i < n; i++) {
            a += "a" + i + "=" + nodos[i][0] + ";\n"; //asignando ai
            b += "b" + i + "=" + nodos[i][1] + ";\n"; //asignando bi
            ts += "ts" + i + "=" + nodos[i][2] + ";\n"; //asignando tsi
            h_b += "h" + i + " <= " + "b" + i + ";\n";//hi<=bi
            if (i != 0) {
                p += "p" + i + " = a" + i + " - " + "h" + i + " + " + "s" + i + ";\n";//pi=ai-hi +si
                pi += "p" + i + " >= 0;\n";//pi >=0
            } else {
                pi += "p" + 0 + " = 0;\n";
            }
            for (int j = 0; j < n; j++) {
                if (i != 0 && i != j) {
                    u += "u" + i + " - " + "u" + j + " + " + n + " x" + i + j + " <= n-1;\n";//ui - uj + n*Xij <= n-1
                }
                if (i != j) {
                    td += "td" + i + j + " = " + distancias[i][j] + "; \n";//tdij
                    j_ = (j == 0) ? n : j;//cambio para asegurar que del ultimo nodo se vaya al nodo inicial 
                    M += "h" + j_ + " - h" + i + " + 100000 - 100000 x" + i + j + " >= td" + i + j + " + " + "ts" + i + " + " + "p" + i + ";\n";//hj - hi +M -M*Xij >= tdij +tsi + pi
                }
                if (i == j) {
                    if (j == 0) {//agregar el 0 al comienzo para cumplir con el operando + siguiente
                        xij += "0";
                        xji += "0";
                    }
                } else {
                    if (j == 0) {//para no agregar el + al inicio
                        xij += "x" + i + j;
                        xji += "x" + j + i;
                    } else {//agregando el operador +
                        xij += " + x" + i + j;
                        xji += " + x" + j + i;
                    }
                }
            }
            //despues de cada ciclo en j, asignar el 1 que es igual a la sumatoria de los SUM xij = 1 y SUM xji = i
            xij += " = 1;\n";
            xji += " = 1;\n";
        }
        //concatenacion total para general el archivo lp
        String lp = "min: t;\n" + "n =" + n + ";\n" + td + a + b + ts + t + xij + xji + u + h_b + M + p + pi;

        String ruta = "./tsptw.lp";
        File archivo = new File(ruta);
        BufferedWriter bw;

        if (archivo.exists()) {
            bw = new BufferedWriter(new FileWriter(archivo));
            bw.write(lp);
        } else {
            bw = new BufferedWriter(new FileWriter(archivo));
            bw.write(lp);
        }

        bw.close();
    }
}
/**
 * para ejecutar el comando en linux
 */
//        Process p = Runtime.getRuntime().exec("./lp_solve conventanasconespera.lp");
//        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
//        while ((s = stdInput.readLine()) != null) {
//            System.out.println(s + "\n");
//        }
/**
 * generar los array manualmente
 */
//        // columnas tsi, ai, bi 
//        int[][] nodo = {{2, 0, 0}, {3, 5, 22}, {6, 13, 22}, {1, 8, 22}};
//        //columnas de td
//        int distancias[][] = new int[4][4];
//        distancias[0][1] = 1;
//        distancias[0][2] = 1;
//        distancias[0][3] = 1;
//        distancias[1][0] = 5;
//        distancias[1][2] = 2;
//        distancias[1][3] = 5;
//        distancias[2][0] = 6;
//        distancias[2][1] = 3;
//        distancias[2][3] = 2;
//        distancias[3][0] = 5;
//        distancias[3][1] = 4;
//        distancias[3][2] = 3;
/**
 * para imprimir los resultados
 */
//mostrando por partes
//        System.out.println("min: t;\n");
//        System.out.println(td);
//        System.out.println(a);
//        System.out.println(b);
//        System.out.println(ts);
//        System.out.println("n =" + n + ";\n");
//        System.out.println(t);
//        System.out.println(xij);
//        System.out.println(xji);
//        System.out.println(u);
//        System.out.println(h_b);
//        System.out.println(M);
//        System.out.println(p);
//        System.out.println(pi);
//mostrando total
//        System.out.println(lp);
        //Escribir el archivo