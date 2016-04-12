package ch01.fundamentals;

import java.util.ArrayList;
import java.util.Arrays;

// 11. Write a program that reads a line of text and prints all characters that are not ASCII,
// together with their Unicode values.

// http://stackoverflow.com/questions/5585919/creating-unicode-character-from-its-number

public class Ch0111Unicode {
    
    public static ArrayList<String> getNonASCII(String s) {
        int[] a = s.codePoints().toArray();
        ArrayList<String> o = new ArrayList<>();
        for (int i = 0; i < a.length; i++) {
            if (a[i] > 127) {
                o.add(Arrays.toString(Character.toChars(a[i])));
            }
        }
        return o;
    }

    public static void main(String[] args) {
        // assume ASCII = codepoints 0-127; http://man7.org/linux/man-pages/man7/ascii.7.html
        
        // http://www.humancomp.org/unichtm/danish8.htm (Danish UTF-8)
        String ex1 = "Der findes en mandtalsliste fra nogle få år ______ H. C. Andersens fødsel.";
        System.out.println(getNonASCII(ex1));
        // [[å], [å], [ø]]
        
        //http://www.humancomp.org/unichtm/croattxt.htm; Croatian UCS-2
        String ex2 = "Dodigović. Kako se Vi zovete?";
        System.out.println(getNonASCII(ex2));
        // [[ć]]

        // http://www.humancomp.org/unichtm/maopoem8.htm; Chinese UTF-8;
        String ex3 = "到处群魔乱舞";
        System.out.println(getNonASCII(ex3));
        // [[到], [处], [群], [魔], [乱], [舞]]
        
        // http://www.humancomp.org/unichtm/russmnv8.htm; Cyrillic UTF-8
        String ex4 = "Американские суда находятся в международных водах.";
        System.out.println(getNonASCII(ex4));
        // [[А], [м], [е], [р], [и], [к], [а], [н], [с], [к], [и], [е], [с], [у], [д],
        // [а], [н], [а], [х], [о], [д], [я], [т], [с], [я], [в], [м], [е], [ж], [д], 
        // [у], [н], [а], [р], [о], [д], [н], [ы], [х], [в], [о], [д], [а], [х]]
        
        // http://www.humancomp.org/unichtm/huseyin8.htm; Turkish UTF-8
        String ex5 = "Yukarda mavi gök, asağıda yağız yer yaratıldıkta; ikisinin arasında insan oğlu yaratılmış.";
        System.out.println(getNonASCII(ex5));
        // [[ö], [ğ], [ı], [ğ], [ı], [ı], [ı], [ı], [ğ], [ı], [ı], [ş]]
        
        System.exit(0);

        // from javaimpatient.zip ch01/sec05 (included in this project and downloaded from
        // http://horstmann.com/javaimpatient/javaimpatient.zip
        String javatm = "Java\u2122"; 
        System.out.println(javatm); // Java™
        System.out.println(Arrays.toString(javatm.codePoints().toArray())); // [74, 97, 118, 97, 8482]
        System.out.println(javatm.length()); // 5
        System.out.println(Integer.toString(8482, 16)); //2122
        char[] c = Character.toChars(Integer.parseInt("8482", 16));
        System.out.println(Arrays.toString(c)); // [蒂]
        char[] c2 = Character.toChars(8482);
        System.out.println(Arrays.toString(c2)); // [™]
        String hex = Integer.toHexString(8482);
        System.out.println(hex); // 2122
        long hexAsLong = Long.valueOf(hex, 16).longValue();
        System.out.println(hexAsLong); // 8482
        System.out.printf("\\u%x\n", hexAsLong); // \u2122
        String tm = String.format("\\u%x\n", hexAsLong);
        System.out.println(tm); // \u2122
        
    }

}
