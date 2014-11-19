/* Objective function */

min: t;

/***************Parámetros*****************/
/* tdij = distancia de la ciudad i a la j*/
td01=1;
td02=1;
td03=1;
td10=5;
td12=2;
td13=5;
td20=6;
td21=3;
td23=2;
td30=5;
td31=4;
td32=3;
/* ai = tiempo minimo de llegada a la ciudad i*/
a0=0;
a1=5;
a2=13;
a3=8;

/* bi= tiempo máximo de llegada a la ciudad j*/
b0=0;
b1=22;
b2=22;
b3=22;

/* tsi= Tiempo de servicio en la ciudad i*/
ts0= 2;
ts1= 3;
ts2= 6;
ts3= 1;

/* n= numero de ciudades*/
n=4;

/***************Variables*****************/
/* Xij = 1, si se decide pasar por la ciudad i a la j, 0 de lo contrario
   hi = tiempo acumulado hasta llegar a la ciudad i
   hi+1= tiempo total hasta devolverse a la ciudad 0
   pi= espera que se produce en la ciudad i
   ui y si= variables auxiliares.

*/


/* Se debe minimizar t (tiempo total) el cual es igual a hi+1*/
t=h4 ;

/***************Restricciones*****************/

/* sumatoria de Xij =1,   j++*/
x01 + x02 + x03=1;
x10 + x12 + x13=1;
x20 + x21 + x23=1;
x30 + x31 + x32=1;

/* sumatoria de Xij =1,   i++*/
x01 + x21 + x31=1;
x10 + x20 + x30=1;
x02 + x12 + x32=1;
x03 + x13 + x23=1;


/* ui - uj + n*Xij <= n-1    ... Con esto se garantiza que pase por todas las ciudades y se llegue al destino de nuevo*/
/* 4= n, pero no lo pude reemplazar ya que ipsolve me daba error*/
u1 - u0 + 4 x10 <=n-1;
u1 - u2 + 4 x12 <=n-1;
u1 - u3 + 4 x13 <=n-1;
u2 - u0 + 4 x20 <=n-1;
u2 - u1 + 4 x21 <=n-1;
u2 - u3 + 4 x23 <=n-1;
u3 - u0 + 4 x30 <=n-1;
u3 - u1 + 4 x31 <=n-1;
u3 - u2 + 4 x32 <=n-1;


/* hi<=bi ... se debe llegar a la ciudad i antes de que se cumpla el tiempo limite*/
h0<= b0;
h1<= b1;
h2<= b2;
h3<= b3;




/* hj - hi +M -M*Xij >= tdij +tsi + pi ..  con esto se garantiza que el tiempo
acumulado hasta llegar a la ciudad j sera el tiempo acumulado hasta llegar a la ciudad i
 mas las esperas, distancias y tiempos de servicio correspondientes. */
h1 - h0 +100000 - 100000 x01 >=td01 +ts0 +p0;
h2 - h0 +100000 - 100000 x02 >=td02 +ts0 +p0;
h3 - h0 +100000 - 100000 x03 >=td03 +ts0 +p0;
h2 - h1 +100000 - 100000 x12 >=td12 +ts1 +p1;
h3 - h1 +100000 - 100000 x13 >=td13 +ts1 +p1;
h1 - h2 +100000 - 100000 x21 >=td21 +ts2 +p2;
h3 - h2 +100000 - 100000 x23 >=td23 +ts2 +p2;
h1 - h3 +100000 - 100000 x31 >=td31 +ts3 +p3;
h2 - h3 +100000 - 100000 x32 >=td32 +ts3 +p3;
h4 - h1 +100000 - 100000 x10 >=td10 +ts1 +p1;
h4 - h2 +100000 - 100000 x20 >=td20 +ts2 +p2;
h4 - h3 +100000 - 100000 x30 >=td30 +ts3 +p3;


/*pi=ai-hi +si .... la espera debe ser ai-hi si hi<ai y cero en caso contrario,
por tal motivo se suma una variable auxiliar para lograr que pi sea cero, en caso de
que la resta de ai-hi de un valor negativo*/
p1=a1-h1 +s1;
p2=a2-h2 +s2;
p3=a3-h3 +s3;
/* Se garantiza que pi sea mayor o igual a cero.*/
p0=0;
p1>=0;
p2>=0;
p3>=0;











bin x01,x02,x03,x10,x12,x13,x20,x21,x23,x30,x31,x32;


/* Variable bounds */
