package com.nick.tdp.security;

import java.math.BigInteger;

public abstract class ECCurveParams {
	/**
	 *  SECG: http://www.secg.org/
	 * SEC 2: Recommended Elliptic Curve Domain Parameters
	 * Recommended Parameters secp192r1
	 * Elliptic curve domain parameters over Fp are a sextuple:
	 *		T = (p, a, b, G, n, h)
	 *	consisting of an integer p specifying the finite field Fp, two elements a, b ¡Ê Fp specifying an elliptic
	 *	curve E(Fp) defined by the equation:
	 *		E : y2 ¡Ô x3 + a.x + b (mod p)
	 *	
	 * T = (p, a, b, G, n, h) 
	 * 		p = FFFFFFFF FFFFFFFF FFFFFFFF FFFFFFFE FFFFFFFF FFFFFFFF
	 * The curve E: y2 = x3 + ax + b over Fp is defined by:
	 *		a = FFFFFFFF FFFFFFFF FFFFFFFF FFFFFFFE FFFFFFFF FFFFFFFC
	 *		b = 64210519 E59C80E7 0FA7E9AB 72243049 FEB8DEEC C146B9B1
	 * E was chosen verifiably at random as specified in ANSI X9.62 [X9.62] from the seed:
	 * 		S = 3045AE6F C8422F64 ED579528 D38120EA E12196D5
	 * The base point G in compressed form is:
	 *		G = 03 188DA80E B03090F6 7CBF20EB 43A18800 F4FF0AFD 82FF1012
	 * In uncompressed form is:
	 *		G = 04 188DA80E B03090F6 7CBF20EB 43A18800 F4FF0AFD 82FF1012 07192B95 FFC8DA78 631011ED 6B24CDD5 73F977A1 1E794811
	 * The order n of G is:
	 *  	n = FFFFFFFF FFFFFFFF FFFFFFFF 99DEF836 146BC9B1 B4D22831
	 *      h = 01
	 * @author Nick
	 *
	 */
	 /* What is Group Order, ECC_p or ECC_n ?
	  * An elliptic curve on finite field Fq
	  * A set of points within a cyclic addition group Gq
	  * q is the order
	  */
	public static final BigInteger ECC_p = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF7FFFFFFF", 16); 
	public static final BigInteger ECC_a = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF7FFFFFFC", 16);
	public static final BigInteger ECC_b = new BigInteger("1C97BEFC54BD7A8B65ACF89F81D4D4ADC565FA45", 16);
	public static final BigInteger ECC_n = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFF99DEF836146BC9B1B4D22831", 16);  
	public static final String ECC_G_CODE = "024A96B5688EF573284664698968C38BB913CBFC82";
	public static final int ECC_Bit_Length = 192;  /*192-bit*/	
	
	/*
	 * For Back End Server 
	 */
	public static final BigInteger SERVER_PRIVATE_KEY = new BigInteger("de9ff58a22798adf2b31f33c8ca9324414257c1d2fa9cdbd", 16);
}
