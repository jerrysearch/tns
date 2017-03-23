package com.qibaike.tns.console;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

/**
 * User: Sergey Mashkov
 * Date: 6/21/12
 */
public class Main {

    private static final int MAX = 30;

    public static void main(String[] args) throws Exception {
        AnsiConsole.systemInstall();

        for (int i = 0; i <= 10; ++i) {
            printProgress(i * 10);
            Thread.sleep(500);
        }

        AnsiConsole.out().print(Ansi.ansi().reset().fg(Ansi.Color.WHITE));

        AnsiConsole.systemUninstall();
    }

    private static void printProgress(int progress) {
        double v = ((double)progress) / 100.;
        v *= MAX;

        int chars = (int)Math.floor(v);
        Ansi.Color color = Ansi.Color.RED;
        if (progress > 75) {
            color = Ansi.Color.GREEN;
        } else if (progress > 50) {
            color = Ansi.Color.YELLOW;
        }

        Ansi a = Ansi.ansi(80).restorCursorPosition().saveCursorPosition().a(" ").bold().fg(color).a("[");
        int i;
        for (i = 1; i <= chars; ++i) {
            if (i == chars) {
                a.a(">");
            } else {
                a.a("=");
            }
        }
        a.boldOff();
        for (; i <= MAX; ++i) {
            a.a("-");
        }

        a.bold().a("] ").fg(Ansi.Color.WHITE).a(progress).a("%").boldOff();

        AnsiConsole.out().print(a);
    }
}