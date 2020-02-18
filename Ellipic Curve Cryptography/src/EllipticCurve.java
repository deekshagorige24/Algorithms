/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Deeksha & Sumitha
 */
import java.math.BigInteger;
import java.util.Scanner;
import java.util.Random;

public class EllipticCurve {
	/*computes point BigInteger[] a3 as specified by the formula
	from the given points 𝑎1 and 𝑎2 and given values of 𝑑 and 𝑝 */
    static BigInteger[] mul(BigInteger[] a1, BigInteger[] a2, BigInteger d, BigInteger p)
    {
    	BigInteger[] a3 = new BigInteger[2];
    	try {

        BigInteger input = new BigInteger("1");
        BigInteger num1_x = (a1[0].multiply(a2[1])).mod(p);
        BigInteger num2_x = (a1[1].multiply(a2[0])).mod(p);
        BigInteger u1 = (num1_x.add(num2_x)).mod(p);
        BigInteger dem = (((((d.multiply(a1[0])).multiply(a2[0])).multiply(a1[1])).multiply(a2[1]))).mod(p);
        BigInteger v1= (input.add(dem)).mod(p);
        BigInteger x3 = u1.multiply(v1.modInverse(p)).mod(p);
        BigInteger num1_y = (a1[1].multiply(a2[1])).mod(p);
        BigInteger num2_y = (a1[0].multiply(a2[0])).mod(p);
        BigInteger u2 = (num1_y.subtract(num2_y)).mod(p);
        BigInteger v2 = (input.subtract(dem)).mod(p);
        BigInteger y3 = u2.multiply(v2.modInverse(p)).mod(p);
        a3[0] = x3;
        a3[1] = y3;
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.getMessage());
    	}
        return a3;
    }
    /*computes the curve point 𝑏 = Math.pow(a,𝑚) given a point '𝑎' , a BigInteger exponent 𝑚,
     * and values of 𝑑 and 𝑝.
     */
    static BigInteger[] exp(BigInteger[] a, BigInteger m, BigInteger d, BigInteger p){
    	BigInteger[] b = {BigInteger.ZERO, BigInteger.ONE};
    	try {
    		for (int i=m.bitLength()-1;i>=0;i--){
    			b = mul(b,b,d,p);
    			if (m.testBit(i)){
    				b = mul(b,a,d,p);
    			}
    		}
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.getMessage());
    	}
        return b;
    }
    /*recovers the exponent (“discrete logarithm”) 𝑚 modulo 𝑛 and
     * counts the number 𝑘 of steps necessary for that computation.
     */
    static BigInteger[] rho(BigInteger[] a, BigInteger[] b, BigInteger d, BigInteger p, BigInteger n){
        BigInteger alphak = BigInteger.ZERO;
        BigInteger alpha2k = BigInteger.ZERO;
        BigInteger betak = BigInteger.ZERO;
        BigInteger beta2k = BigInteger.ZERO;;
        BigInteger[] zk = new BigInteger[2];
        zk[0] = BigInteger.ZERO;
        zk[1] = BigInteger.ONE;
        BigInteger[] z2k = new BigInteger[2];
        z2k[0] = BigInteger.ZERO;
        z2k[1] = BigInteger.ONE;
        int k = 0;
        do{
        	switch((zk[0].mod(BigInteger.valueOf(3))).intValue()){
        		case 0:
        			zk = mul(b,zk,d,p);
        			alphak = (alphak.add(BigInteger.ONE));
        			//betak = betak;
        			break;

        		case 1:
        			zk = mul(zk,zk,d,p);
        			alphak = (alphak.multiply(BigInteger.valueOf(2)));
        			betak = (betak.multiply(BigInteger.valueOf(2)));
        			break;

        		case 2:
        			zk = mul(a,zk,d,p);
        			//alphak = alphak;
        			betak = (betak.add(BigInteger.ONE));
        			break;
            }
        	for (int i = 0;i < 2;i++) {
        		switch(z2k[0].mod(BigInteger.valueOf(3)).intValue()){
        			case 0:
        				z2k = mul(b,z2k,d,p);
        				alpha2k = alpha2k.add(BigInteger.ONE);
        				//beta2k = beta2k;
        				break;
        			case 1:
        				z2k = mul(z2k,z2k,d,p);
        				alpha2k = alpha2k.multiply(BigInteger.valueOf(2));
        				beta2k = beta2k.multiply(BigInteger.valueOf(2));
        				break;
        			case 2:
        				z2k = mul(a,z2k,d,p);
        				//alpha2k = alpha2k;
        				beta2k = beta2k.add(BigInteger.ONE);
        				break;
            	}
        	}
        	k = k + 1;
        }while((zk[0].compareTo(z2k[0]) != 0) || (zk[1].compareTo(z2k[1]) != 0)) ;

        BigInteger numerator = (beta2k.subtract(betak)).mod(n);
        BigInteger denominator = (alphak.subtract(alpha2k)).mod(n);

        BigInteger m = numerator.multiply(denominator.modInverse(n)).mod(n);

        return new BigInteger[]{m, new BigInteger(Integer.toString(k))};
    }

    /* generates a random BigInteger exponent 𝑚 modulo 𝑛,
     * computes 𝑏 = Math.pow(a,m) using method exp(), recovers the discrete logarithm 𝑚′ from 𝑎 and 𝑏
     * using method rho(), checks whether 𝑚 = 𝑚′ (and throws a RuntimeException if they don’t match)
     * and returns the number of steps 𝑘 that method rho needed to compute 𝑚.
     */
    static long check(BigInteger[] a, BigInteger d, BigInteger p, BigInteger n){

        long k = 0L;
        Random rand = new Random();
        int genLong = rand.nextInt(n.intValue());
        BigInteger m = (BigInteger.valueOf(genLong));
        BigInteger[] b = exp(a, m, d, p);

        BigInteger[] result = rho(a,b,d,p,n);


        if (m.compareTo(result[0]) == 0) {
            System.out.println("m  = " + m);
            System.out.println("b  : " + b[0] + " " + b[1]);
            System.out.println("m' = " + result[0]);

            k = result[1].longValue();
        } else {
            throw new RuntimeException("m " + m + " does not match m' " + result[0]);
        }

        return k;
   }

    /*Driver program that calls the check() method
     * and calculates the average number of steps
     */
    static long calculateAverage(BigInteger[] a, BigInteger d, BigInteger p, BigInteger n, long N){

       long total = 0;
       for(int i = 0; i <= N; i++){
           long k = check(a, d, p, n);
           total += k;
           System.out.println("K value = " + k);
       }

       long avg =  total / N;

       return avg;
   }


    public static void main(String args[]){
        Scanner input1 = new Scanner(System.in);
        BigInteger[] a1 = new BigInteger[2];
        BigInteger[] a2 = new BigInteger[2];
        BigInteger[] a = new BigInteger[2];
        BigInteger input = new BigInteger("1");

        System.out.println("Enter the elements of a: ");
        for (int v =0; v<2; v++){
            int num3= input1.nextInt();
            String s3 = Integer.toString(num3);
            BigInteger big_num3 =  new BigInteger(s3);
            a[v] = big_num3;
        }

        System.out.println("Enter the value of 'D': ");
        int d_val = input1.nextInt();
        String s3 = Integer.toString(d_val);
        BigInteger d = new BigInteger(s3);

        System.out.println("Enter the prime value of 'P': ");
        int p_val = input1.nextInt();
        String s4 = Integer.toString(p_val);
        BigInteger p = new BigInteger(s4);

        System.out.println("Enter the value of 'n' : ");
        int n_val = input1.nextInt();
        String s6 = Integer.toString(n_val);
        BigInteger n = new BigInteger(s6);

        long k = calculateAverage(a,d,p,n,1000);
        System.out.println("k "+ k);

    }
}
