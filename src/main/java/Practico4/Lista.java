package Practico4;

import java.util.Iterator;

public class Lista<E> implements Iterable<E> {
    protected Nodo<E> primero;
    protected int tamano;
    public Lista() {
        tamano = 0;
        primero = null;
    }

    @Override
    public String toString() {
        if (primero == null) {
            return "{VACIA}";
        }
        StringBuilder resultado = new StringBuilder();
        Nodo<E> actual = primero;
        while(actual != null) {
            resultado.append(actual.toString());
            actual = actual.getSiguiente();
        }

        return resultado.toString();
    }

    public void insertar(E o) {
        Nodo<E> nuevo = new Nodo<E>(o);
        nuevo.setSiguiente(primero);
        primero = nuevo;
        tamano++;
    }

    public int tamano() {
        return tamano;
    }

    public Iterator<E> iterator() {
        return new IteradorLista(this);
    }

    public Nodo<E> getPrimero() {
        return primero;
    }

    public void eliminar(int posicion) {
        if (posicion > tamano) {
            return;
        }
        if (posicion < 1) {
            return;
        }
        if (posicion == 1) {
            primero = primero.getSiguiente();
            return;
        }
        int contador = 1;
        Nodo<E> actual = primero;
        while(contador < (posicion - 1)) {
            contador++;
            actual = actual.getSiguiente();
        }
        actual.setSiguiente(actual.getSiguiente().getSiguiente());
        tamano--;
    }

    static class Nodo<E> {
        private E contenido;
        private Nodo<E> siguiente;

        public Nodo(E o) {
            contenido = o;
            siguiente = null;
        }

        public E getContenido() {
            return contenido;
        }

        public void setContenido(E contenido) {
            this.contenido = contenido;
        }

        public Nodo<E> getSiguiente() {
            return siguiente;
        }

        public void setSiguiente(Nodo<E> siguiente) {
            this.siguiente = siguiente;
        }

        @Override
        public String toString() {
            return "[" + contenido.toString() + "] -> ";
        }
    }

    static class IteradorLista<E> implements Iterator<E> {

        private Nodo<E> actual;

        public IteradorLista(Lista<E> lista) {
            actual = lista.getPrimero();
        }

        @Override
        public boolean hasNext() {
            return actual != null;
        }

        @Override
        public E next() {
            E o = actual.getContenido();
            actual = actual.getSiguiente();
            return o;
        }
    }
}