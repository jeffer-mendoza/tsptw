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
        float nodos[][] = null;
        float distancias[][] = null;
        int i;
        String celdas[] = null;//variable temporal para guardas los datos de la n-esima linea
        try {
            while ((linea = br.readLine()) != null) {
                if (linea.equals("f")) {
                    break;
                }
                if (count == 0) {//leer solo la primera linea
                    linea = linea.replace("\\ ", "");
                    n = Integer.parseInt(linea);//asignar el numero de notos
                    nodos = new float[n][3];
                    distancias = new float[n * (n - 1)][n * (n - 1)];
                    count++;
                    continue;
                }
                if (count < n + 1) {
                    celdas = linea.split("\\ ");//orden:{i, ts, ai, bi}
                    i = Integer.parseInt(celdas[0]) - 1;//nodo n-esimo
                    nodos[i][0] = Float.parseFloat(celdas[1]);//primer columna  - ts
                    nodos[i][1] = Float.parseFloat(celdas[2]);//segunda columna - ai
                    nodos[i][2] = Float.parseFloat(celdas[3]);//tercer columna -bi
                } else {
                    celdas = linea.split("\\ ");//orden:{i, j, tdij}
                    distancias[Integer.parseInt(celdas[0]) - 1][Integer.parseInt(celdas[1]) - 1] = Float.parseFloat(celdas[2]);
                }
                count++;
            }

            generatorLP(n, distancias, nodos);//generar el archivo lp 
            lp_solve(n);

        } catch (IOException e) {
            e.printStackTrace();
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
    public static void generatorLP(int n, float distancias[][], float nodos[][]) throws IOException {
        System.out.println(n + ";");
        //declaración de todas las variables que se usaran
        String td, a, b, ts, xij, xji, u, h_b, M, pi, p, x;
        td = a = b = ts = xij = xji = u = h_b = M = pi = p = x = "";
        String t = "t = h" + n + ";\n";//igualacion de t = la n-isma h
        int j_;//variable articial para manejar las restricciones del M grande
        for (int i = 0; i < n; i++) {
            a += "a" + i + "=" + nodos[i][1] + ";\n"; //asignando ai
            b += "b" + i + "=" + nodos[i][2] + ";\n"; //asignando bi
            ts += "ts" + i + "=" + nodos[i][0] + ";\n"; //asignando tsi
            h_b += "h" + i + " <= " + "b" + i + ";\n";//hi<=bi
            if (i != 0) {
                p += "p" + i + " = a" + i + " - " + "h" + i + " + " + "s" + i + ";\n";//pi=ai-hi +si
                pi += "p" + i + " >= 0;\n";//pi >=0
            } else {
                pi += "p" + 0 + " = 0;\n";
            }
            for (int j = 0; j < n; j++) {
                if (i == 0 && j == 0) {
                    x += "x" + i + j;
                } else {
                    x += ",x" + i + j;
                }
                if (i != 0 && i != j) {
                    u += "u" + i + " - " + "u" + j + " + " + n + " x" + i + j + " <= n-1;\n";//ui - uj + n*Xij <= n-1
                }
                if (i != j) {
                    td += "td" + i + j + " = " + distancias[i][j] + "; \n";//tdij
                    System.out.println(i + " " + j + " " + distancias[i][j] + ";");

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
        String lp = "min: t;\n" + "n =" + n + ";\n" + td + a + b + ts + t + xij + xji + u + h_b + M + p + pi + "binary  " + x + ";";
//        System.out.println(lp);

        String ruta = "./out.lp";
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

    public static void lp_solve(int n) throws IOException {
        Process p = Runtime.getRuntime().exec("./lp_solve out.lp");

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        int count = 0;
        int limite = 2 * n * n + n + 6;
        int inicio = n * n + 2 * n + 6;
        String celdas[] = null;
        String nodo;
        String s;
        while ((s = stdInput.readLine()) != null) {
            //se elminan todos los espacios en blanco que envia lpsolve
            s = s.replaceAll("\\s", "");
            if (count == 4) {// numero de linea donde aparece la funcion objetivo
                //Aquí se captura el valor de la función objetivo
                System.out.println(s.substring(1) + ";");
                count++;
                continue;
            }
            if (count > inicio && count <= limite) {//intervalo en donde estan las variables Xij
                //formato de llegada Xij{1|0}
                //se realiza separacion de limite 4, se obtiene los siguientes caracteres
                //[0] = X -- [1] = i -- [2] = j -- [3] = [1|0]
                celdas = s.split("", 4);
                System.out.println(celdas[1] + " " + celdas[2] + " " + celdas[3] + ";");
            }
            count++;
        }
    }
}

