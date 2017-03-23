package com.qibaike.tns.console;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Attribute;
import org.fusesource.jansi.Ansi.Color;
import org.fusesource.jansi.AnsiConsole;

public class TestJansi {

	public static void main(String[] args) throws InterruptedException {
		AnsiConsole.systemInstall();
		System.out.println(Ansi.ansi().fg(Color.YELLOW).a(String.format("%30s", "Hello")).fg(Color.GREEN)
				.a("World").reset());
		
		Thread.sleep(3000L);
		
		System.out.println(Ansi.ansi().a(Attribute.UNDERLINE).fg(Color.YELLOW).a("Hello").fg(Color.GREEN)
				.a("World").reset());
		
		String[][] array = new String[][]{
				{"", "─┬─", ""},
				{"|", "A", "|"},
				{"", "─┴─", ""}};
		
		for(String[] tmp : array){
			System.out.println(Ansi.ansi().fg(Color.RED).a(getNoneEmptyString(tmp[0])).a(getNoneEmptyString(tmp[1])).a(getNoneEmptyString(tmp[2])).reset());
		}
		AnsiConsole.systemUninstall();
	}
	
	static String getNoneEmptyString(String s){
		return String.format("%10s", s);
	}

}
