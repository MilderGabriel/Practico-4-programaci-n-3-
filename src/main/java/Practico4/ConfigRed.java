package Practico4;

import java.util.HashMap;
import java.util.Map;

public class ConfigRed {

    public static final int PUERTO = 6767;

    public static final String CMD_HOLA     = "HOLA";
    public static final String CMD_LISTA    = "LISTA";
    public static final String CMD_FIGURA   = "FIGURA";
    public static final String CMD_OK       = "OK";
    public static final String CMD_CHAU     = "CHAU";

    public static final String REGEX_CUADRADO =
            "^CUADRADO\\s+([0-9]+)\\s+([0-9]+)\\s+([0-9]+)\\s+([0-9]+)$";

    public static final String REGEX_CIRCULO =
            "^CIRCULO\\s+([0-9]+)\\s+([0-9]+)\\s+([0-9]+)$";

    public static final String REGEX_NUMERO  = "^([0-9]+)$";
    public static final Map<TipoComando, String> expresiones = new HashMap<>();

    static {
        expresiones.put(TipoComando.Hola, CMD_HOLA);
        expresiones.put(TipoComando.Lista, CMD_LISTA);
        expresiones.put(TipoComando.Figura, CMD_FIGURA);
        expresiones.put(TipoComando.Cuadrado, REGEX_CUADRADO);
        expresiones.put(TipoComando.Circulo,  REGEX_CIRCULO);
        expresiones.put(TipoComando.Ok, CMD_OK);
        expresiones.put(TipoComando.Chau, CMD_CHAU);
    }
}
