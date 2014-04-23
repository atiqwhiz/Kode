package com.vaani.ood.elevator.main;

public class Console {
	private static boolean DEBUG = false;
	
	public static void info(String text) {
		System.out.println(text);
	}
	
	public static void debug(String text) {
		if(DEBUG) System.out.println(text);
	}

	public static void debug_variable(String var_name, Object variable) {
		debug(var_name+" : "+variable);
	}
}
