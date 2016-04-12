var System  = Java.type("java.lang.System");
for each (e in System.env.entrySet()) { print(e.key, " : ", e.value) };
