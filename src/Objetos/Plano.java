package Objetos;

import java.awt.Point;
import java.util.ArrayList;


public class Plano {

    private ArrayList<Nodo> vecNodos = new ArrayList();
    private ArrayList<Arco> vecArcos = new ArrayList();
    
    public Plano() {
    }
    
    // GETTER Y SETTER
    public ArrayList<Arco> getArcos() {
        return vecArcos;
    }

    public void setArcos(ArrayList<Arco> arcos) {
        this.vecArcos = arcos;
    }

    public ArrayList<Nodo> getNodos() {
        return vecNodos;
    }

    public void setNodos(ArrayList<Nodo> nodos) {
        this.vecNodos = nodos;
    }
    
    // METODOS DE NODOS
    public Nodo buscarNodo(Point punto) {
        
        for (Nodo nodo : vecNodos) {
            int radio = 80 / 2;
            double distancia = Math.sqrt(Math.pow(nodo.getX() - punto.x, 2) + Math.pow(nodo.getY() - punto.y, 2));
            if (distancia < radio) {
                return nodo;
            }
        }
        return null;
    }
    
    public Nodo buscarNodo(int id) {
        
        for (Nodo nodo : vecNodos) {
            if (nodo.getId() == id) {
                return nodo;
            }
        }
        return null;
    }
    
    public void moverNodo(Nodo nodo, Point punto) {
        
        for (int i = 0; i < vecNodos.size(); i++) {
            if (vecNodos.get(i).getId() == nodo.getId()) {
                nodo.setX(punto.x);
                nodo.setY(punto.y);
                vecNodos.set(i, nodo);
                break;
            }
        }
    }
    
    public void modificarNodo(Nodo nodo, int id) {
        
        for (int i = 0; i < vecNodos.size(); i++) {
            if (vecNodos.get(i).getId() == id) {
                vecNodos.set(i, nodo);
                break;
            }
        }
    }
    
    public void eliminarNodo(Nodo nodo) {
        
        for (int i = 0; i < vecNodos.size(); i++) {
            if (vecNodos.get(i).getId() == nodo.getId()) {
                for (int j = 0; j < vecArcos.size(); j++) {
                    if (vecArcos.get(j).getNodoOrigen() == nodo.getId()
                        || vecArcos.get(j).getNodoDestino() == nodo.getId()) {
                            vecArcos.remove(j--);
                    }
                }
                vecNodos.remove(i);
                break;
            }
        }
    }
    
    public boolean adicionarNodo(Nodo nodoN) {

        for (Nodo nodo : vecNodos) { 
            // VERIFICAR ID UNICO
            if (nodo.getId() == nodoN.getId()) {
                return false;
            }
        }
        vecNodos.add(nodoN);
        return true;
    }

    // METODOS DE ARCOS
    public Arco buscarArco(Point punto) {
        
        for (Arco arco : vecArcos) {
            Nodo nI = buscarNodo(arco.getNodoOrigen());
            Nodo nF = buscarNodo(arco.getNodoDestino());
            
            double m = (double) (nI.getY() - nF.getY())/(double) (nI.getX() - nF.getX());
            double yAux = m * (punto.x - nI.getX()) + nI.getY();

            if (Math.abs(yAux - punto.y) < 8) {
                return arco;
            }
        }
        return null;
    }

    public Arco buscarArco(int id) {
        
        for (Arco arco : vecArcos) {
            if (arco.getId() == id) {
                return arco;
            }
        }
        return null;
    }
        
    public boolean adicionarArco(Arco arcoN) {

        for (Arco arco : vecArcos) {
            
            if (arco.getId() == arcoN.getId()) {         
                return false;
            }
        }
        vecArcos.add(arcoN);
        return true;
    }

    public void modificarArco(Arco arco, int id) {
        
        for (int i = 0; i < vecArcos.size(); i++) {
            if (vecArcos.get(i).getId() == id) {
                vecArcos.set(i, arco);
                break;
            }
        }
    }

    public void eliminarArco(Arco arco) {
        
        for (int i = 0; i < vecArcos.size(); i++) {
            if (vecArcos.get(i).getId() == arco.getId()) {
                vecArcos.remove(i);
                break;
            }
        }
    }
    


}
