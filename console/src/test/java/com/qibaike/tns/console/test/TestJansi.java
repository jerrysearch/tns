package com.qibaike.tns.console.test;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Attribute;
import org.fusesource.jansi.Ansi.Color;
import org.fusesource.jansi.AnsiConsole;

public class TestJansi {

	public static void main(String[] args) throws InterruptedException {
		AnsiConsole.systemInstall();
		
		System.out.println(Ansi.ansi().a(Attribute.UNDERLINE).fg(Color.YELLOW).a("Hello").fg(Color.GREEN)
				.a("World").reset());
		
		Thread.sleep(3000L);
		
		System.out.println(Ansi.ansi().a(Attribute.UNDERLINE).fg(Color.YELLOW).a("Hello").fg(Color.GREEN)
				.a("World").reset());
		AnsiConsole.systemUninstall();
	}

}
