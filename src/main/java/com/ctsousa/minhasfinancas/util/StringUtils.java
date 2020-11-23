package com.ctsousa.minhasfinancas.util;

import java.math.BigDecimal;

public abstract class StringUtils {
	
	public static BigDecimal parse(String str) {
		return new BigDecimal(str.replace(",", "."));
	}
}
