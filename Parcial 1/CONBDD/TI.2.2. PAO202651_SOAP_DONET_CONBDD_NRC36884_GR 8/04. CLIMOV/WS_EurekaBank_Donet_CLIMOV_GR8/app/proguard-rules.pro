# Reglas de ProGuard del cliente movil.
# ksoap2 usa reflexion para serializar los objetos SOAP.
-keep class org.ksoap2.** { *; }
-keep class org.kxml2.** { *; }
-keep class ec.edu.monster.climov.modelo.** { *; }
