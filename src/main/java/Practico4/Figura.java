package Practico4;

public class Figura {
    private String forma;
    protected int posicionX;
    protected int posicionY;
    protected int tamaño;
    private boolean esRemota = false;

    public Figura(String formaSeleccionada, int x, int y, int tamaño) {
        this.forma = formaSeleccionada;
        this.posicionX = x;
        this.posicionY = y;
        this.tamaño = tamaño;
    }

    public String getForma()  { return forma;     }
    public int getPosicionX() { return posicionX; }
    public int getPosicionY() { return posicionY; }
    public int getTamaño()    { return tamaño;    }
    public boolean esRemota() { return esRemota; }
    public void setEsRemota(boolean valor) { this.esRemota = valor; }
}