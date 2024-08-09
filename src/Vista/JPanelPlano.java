
package Vista;

import Objetos.Arco;
import Objetos.Nodo;
import Objetos.Plano;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class JPanelPlano extends JPanel implements MouseListener, MouseMotionListener {

    private Plano plano;
    private int accion = 0;

    private Point mousePos1;
    private Point mousePos2;

    private int nodo1;
    private int nodo2;

    private ArrayList<String> historial;

    // constructor
    public JPanelPlano() {
        addMouseMotionListener(this);
        addMouseListener(this);

        this.plano = new Plano();
        this.historial = new ArrayList<>();

        setBackground(Color.white);
        setLocation(155, 20);
        setSize(795, 565);
    }

    //--------------------------------------------------------------------------
    // funcion control + z
    public void deshacer() {
        
        // guarda un historial
        if (historial.isEmpty()) {
            return;
        }

        String accion = historial.get(historial.size() - 1);
        historial.remove(historial.size() - 1);

        String parts[] = accion.split(",");

        System.out.println(parts[0]);

        if (parts[0].equals("eliminar")) {
            if (parts[1].equals("nodo")) {
                int id = Integer.parseInt(parts[2]);
                plano.eliminarNodo(plano.buscarNodo(id));
            } else {
                int id = Integer.parseInt(parts[2]);
                plano.eliminarArco(plano.buscarArco(id));
            }
        } else if (parts[0].equals("modificar")) {
            if (parts[1].equals("nodo")) {
                int id = Integer.parseInt(parts[2]);
                int idV = Integer.parseInt(parts[3]);
                int tpV = Integer.parseInt(parts[4]);

                Nodo aux = plano.buscarNodo(id);
                Nodo nd = new Nodo(idV, tpV, aux.getX(), aux.getY());

                plano.modificarNodo(nd, id);

                for (int i = 0; i < plano.getArcos().size(); i++) {
                    if (plano.getArcos().get(i).getNodoDestino() == id) {
                        plano.getArcos().get(i).setNodoDestino(idV);
                    }
                    if (plano.getArcos().get(i).getNodoOrigen() == id) {
                        plano.getArcos().get(i).setNodoOrigen(idV);
                    }
                }
            } else {
                int id = Integer.parseInt(parts[2]);
                int peso = Integer.parseInt(parts[3]);

                plano.buscarArco(id).setPeso(peso);
            }
        } else if (parts[0].equals("crear")) {
            if (parts[1].equals("nodo")) {
                int id = Integer.parseInt(parts[2]);
                int tipo = Integer.parseInt(parts[3]);
                int x = Integer.parseInt(parts[4]);
                int y = Integer.parseInt(parts[5]);

                plano.adicionarNodo(new Nodo(id, tipo, x, y));
            } else {
                int id = Integer.parseInt(parts[2]);
                int peso = Integer.parseInt(parts[3]);
                int origen = Integer.parseInt(parts[4]);
                int destino = Integer.parseInt(parts[5]);

                plano.adicionarArco(new Arco(id, peso, origen, destino));
            }
        }
        repaint();
    }

    // buscar posicion del nodo en el vector
    public int posNodo(int id) {
        for (int i = 0; i < plano.getNodos().size(); i++) {
            if (plano.getNodos().get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    public Plano algoritmo() {

        int N = plano.getNodos().size();
        if (N == 0) {
            throw new NullPointerException();
        }
        // matriz del grafo
        int graph[][] = new int[N][N];

        // buscando posicion del nodo fuente y nodo sumidero
        int nodoF = 0, nodoS = N - 1;
        for (int i = 0; i < N; i++) {
            if (plano.getNodos().get(i).getTipo() == 1) {
                nodoF = i;
            }
            if (plano.getNodos().get(i).getTipo() == 3) {
                nodoS = i;
            }
        }
        System.out.println("fuente: " + nodoF);
        System.out.println("Sumidero: " + nodoS);

        // convertir grafo a matriz 
        for (Arco arc : plano.getArcos()) {
            int i = posNodo(arc.getNodoOrigen());
            int j = posNodo(arc.getNodoDestino());

            graph[i][j] = arc.getPeso();
        }

        // imprimiendo matriz
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.print(graph[i][j] + " ");
            }
            System.out.println("");
        }

        return fordFulkerson(graph, nodoF, nodoS, N);
    }

    private boolean bfs(int rGraph[][], int s, int t, int N, int parent[]) {

        boolean visited[] = new boolean[N];
        for (int i = 0; i < N; ++i) {
            visited[i] = false;
        }

        LinkedList<Integer> queue = new LinkedList<>();
        queue.add(s);
        visited[s] = true;
        parent[s] = -1;
        
        // mientras cola no este vacia
        while (queue.size() != 0) {
            int u = queue.poll();

            for (int v = 0; v < N; v++) {
                if (visited[v] == false && rGraph[u][v] > 0) {

                    if (v == t) {
                        parent[v] = u;
                        return true;
                    }
                    queue.add(v);
                    parent[v] = u;
                    visited[v] = true;
                }
            }
        }

        return false;
    }

    private Plano fordFulkerson(int graph[][], int s, int t, int N) {
        int u, v;

        // matriz copia
        int rGraph[][] = new int[N][N];

        for (u = 0; u < N; u++) {
            for (v = 0; v < N; v++) {
                rGraph[u][v] = graph[u][v];
            }
        }

        int parent[] = new int[N];
        int max_flow = 0;

        System.out.println("Cuello de botella:");
        
        while (bfs(rGraph, s, t, N, parent)) {

            int path_flow = Integer.MAX_VALUE;
            
            // hallar arcos con capacidad minima
            for (v = t; v != s; v = parent[v]) {
                u = parent[v];

                path_flow = Math.min(path_flow, rGraph[u][v]);
            }

            // restar path flow a los arcos
            for (v = t; v != s; v = parent[v]) {
                u = parent[v];
                rGraph[u][v] -= path_flow;
            }

            max_flow += path_flow;
            System.out.println("arco: "+path_flow);
        }

        JOptionPane.showMessageDialog(this, "Flujo máximo del grafo: " + max_flow);

        return mostrarGrafoResidual(rGraph, s, t, N);
    }

    public Plano mostrarGrafoResidual(int graph[][], int s, int t, int N) {

        // funcion para convertir la matriz resultado a grafo
        Plano rs = new Plano();
        rs.setNodos(plano.getNodos());

        int cont = 0;
        for (Arco arc : plano.getArcos()) {
            int i = posNodo(arc.getNodoOrigen());
            int j = posNodo(arc.getNodoDestino());

            Arco aux = new Arco(cont++, graph[i][j],
                    arc.getNodoOrigen(), arc.getNodoDestino());

            rs.adicionarArco(aux);
        }

        return rs;
    }

    public int obtenerNuevoId() {
        int id = plano.getArcos().size();

        while (plano.buscarArco(id) != null) {
            id++;
        }
        return id;
    }

    //--------------------------------------------------------------------------
    public void setPlano(Plano plano) {
        this.plano = plano;
        repaint();
    }

    public Plano getPlano() {
        return plano;
    }

    public int getModo() {
        return accion;
    }

    public void accionBoton(int estado) {
        this.accion = estado;

        mousePos1 = null;
        repaint();
    }

    //--------------------------------------------------------------------------
    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        //--PINTANDO CELDAS-----------------------------------------------------
        g.setColor(Color.lightGray);

        for (int i = 0; i < 90; i++) {
            g.drawLine(i * 20 + 1, 0, i * 20 + 1, 1000);
            g.drawLine(0, i * 20 + 1, 1000, i * 20 + 1);
        }

        //--MOUSE ALZADO--------------------------------------------------------
        if (mousePos1 != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setStroke(new BasicStroke(5));
            if (accion == 1) {
                
                g2d.setColor(Color.black);
                g2d.drawOval(mousePos1.x - 5, mousePos1.y - 5, 10, 10);

            } else if (accion == 2) {

                if (mousePos2 != null) {
                    g2d.setStroke(new BasicStroke(1));
                    g2d.setColor(Color.black);
                    g2d.drawLine(mousePos1.x, mousePos1.y, mousePos2.x, mousePos2.y);
                }
            }
            g2d.setStroke(new BasicStroke(1));
        }

        //--PINTANDO ARCOS------------------------------------------------------
        if (!plano.getArcos().isEmpty()) {

            double radians = 0, mag = 0, m = 0;
            Point punto = new Point();

            try {

                for (Arco arco : plano.getArcos()) {
                    double angle1 = 180 - 35;
                    double angle2 = 180 + 35;
                    double anguloEntreP = 90;
                    Nodo nodoSource = plano.buscarNodo(arco.getNodoOrigen());
                    Nodo nodoSink = plano.buscarNodo(arco.getNodoDestino());

                    if (nodoSink.getX() != nodoSource.getX()) {
                        m = (double) (nodoSink.getY() - nodoSource.getY()) / (double) (nodoSink.getX() - nodoSource.getX());
                        anguloEntreP = Math.toDegrees(Math.atan(m));
                    }

                    radians = Math.abs(Math.toRadians(anguloEntreP));

                    mag = Math.sqrt(Math.pow(nodoSource.getX() - nodoSink.getX(), 2) + Math.pow(nodoSource.getY() - nodoSink.getY(), 2)) - 45;

                    if (nodoSource.getY() < nodoSink.getY()) {
                        if (nodoSource.getX() < nodoSink.getX()) {
                            angle1 += anguloEntreP;
                            angle2 += anguloEntreP;
                            punto.y = (int) (nodoSource.getY() + mag * Math.sin(radians));
                            punto.x = (int) (nodoSource.getX() + mag * Math.cos(radians));
                        } else {
                            angle1 += anguloEntreP + 180;
                            angle2 += anguloEntreP + 180;
                            punto.y = (int) (nodoSource.getY() + mag * Math.sin(radians));
                            punto.x = (int) (nodoSource.getX() - mag * Math.cos(radians));
                        }
                    } else {
                        if (nodoSource.getX() < nodoSink.getX()) {
                            angle1 += anguloEntreP;
                            angle2 += anguloEntreP;
                            punto.y = (int) (nodoSource.getY() - mag * Math.sin(radians));
                            punto.x = (int) (nodoSource.getX() + mag * Math.cos(radians));
                        } else {
                            angle1 -= 180 - anguloEntreP;
                            angle2 -= 180 - anguloEntreP;
                            punto.y = (int) (nodoSource.getY() - mag * Math.sin(radians));
                            punto.x = (int) (nodoSource.getX() - mag * Math.cos(radians));
                        }
                    }
                    g.setColor(Color.black);

                    if (arco.getPeso() == 0) {
                        Graphics2D g2d = (Graphics2D) g;
                        float guiones[] = {10, 10};
                        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, guiones, 0));
                        g2d.drawLine(nodoSource.getX(), nodoSource.getY(), punto.x, punto.y);
                        g2d.drawLine(punto.x, punto.y, (int) (punto.x + Math.cos(Math.toRadians(angle1)) * 20), (int) (punto.y + Math.sin(Math.toRadians(angle1)) * 20));
                        g2d.drawLine(punto.x, punto.y, (int) (punto.x + Math.cos(Math.toRadians(angle2)) * 20), (int) (punto.y + Math.sin(Math.toRadians(angle2)) * 20));
                        g2d.setStroke(new BasicStroke());
                    } else {
                        g.drawLine(nodoSource.getX(), nodoSource.getY(), punto.x, punto.y);

                        g.drawLine(punto.x, punto.y, (int) (punto.x + Math.cos(Math.toRadians(angle1)) * 20), (int) (punto.y + Math.sin(Math.toRadians(angle1)) * 20));
                        g.drawLine(punto.x, punto.y, (int) (punto.x + Math.cos(Math.toRadians(angle2)) * 20), (int) (punto.y + Math.sin(Math.toRadians(angle2)) * 20));
                    }

                    int iT = (nodoSource.getX() + nodoSink.getX()) / 2;
                    int jT = (nodoSource.getY() + nodoSink.getY()) / 2;

                    g.setColor(new Color(126, 24, 3));
                    g.setFont(new Font(Font.SANS_SERIF, 0, 20));
                    g.drawString(arco.getPeso() + "", iT, jT);

                }
            } catch (Exception e) {
            }

        }
        //--PINTANDO NODOS------------------------------------------------------
        if (plano.getNodos() != null) {
            for (Nodo nodo : plano.getNodos()) {

                g.setColor(Color.cyan);
                if (nodo.getTipo() == 2) {
                    g.setColor(Color.YELLOW);
                }

                g.fillOval((int) nodo.getX() - 40, (int) nodo.getY() - 40, 80, 80);
                g.setColor(Color.black);
                g.drawOval((int) nodo.getX() - 40, (int) nodo.getY() - 40, 80, 80);

                g.setFont(new Font(Font.SERIF, 0, 40));
                g.drawString(String.valueOf(nodo.getId()), nodo.getX() - 8, nodo.getY() + 8);
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent me) {

        //--MOVER NODO----------------------------------------------------------
        if (accion == 3) {

            Nodo nodoC = plano.buscarNodo(me.getPoint());
            if (nodoC != null) {

                mousePos1 = me.getPoint();
                plano.moverNodo(nodoC, mousePos1);
                repaint();
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent me) {

        if (accion == 1) {
            mousePos1 = me.getPoint();
            repaint();
        } else if (accion == 2) {

            if (mousePos1 != null) {

                mousePos2 = me.getPoint();
                repaint();
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent me) {

        if (accion == 1) {
            mousePos1 = me.getPoint();

            if (plano.buscarNodo(mousePos1) == null) {

                try {
                    int id, tipo;

                    id = Integer.parseInt(JOptionPane.showInputDialog(this, "Digite Identificador Unico: "));
                    tipo = Integer.parseInt(JOptionPane.showInputDialog(this, "Tipo...\n1) Fuente \n2) intermedio \n3) Sumidero \nDigite Tipo elemento (1-3): "));
                    if(tipo==1 || tipo==2 || tipo==3){
                        Nodo nuevoN = new Nodo(id, tipo, mousePos1.x, mousePos1.y);
                        historial.add("eliminar,nodo," + id);
                        plano.adicionarNodo(nuevoN);
                        JOptionPane.showMessageDialog(this, "Nodo agregado con exito");
                    }
                    else{
                        JOptionPane.showMessageDialog(this, "tipo no Valido.");
                    }

                } catch (HeadlessException | NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Error datos no validos");
                }

                accion = 0;
            }
        } else if (accion == 2) {

            Nodo nodoC = plano.buscarNodo(me.getPoint());
            if (nodoC != null) {

                if (mousePos1 == null) {

                    nodo1 = nodoC.getId();
                    mousePos1 = me.getPoint();

                } else {

                    nodo2 = nodoC.getId();
                    mousePos2 = me.getPoint();

                    try {
                        int id = obtenerNuevoId();
                        int peso = Integer.parseInt(JOptionPane.showInputDialog(this, "Digite el peso: "));

                        Arco arcoN = new Arco(id, peso, nodo1, nodo2);
                        Arco arc = plano.buscarArco(id);
                        if (arc == null) {
                            if (peso > 0) {
                                historial.add("eliminar,arco," + id);
                                plano.adicionarArco(arcoN);
                                JOptionPane.showMessageDialog(this, "Arco agregado con exito");
                            } else {
                                JOptionPane.showMessageDialog(this, "Ingrese un peso valido");
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "Arco ya existe");
                        }

                    } catch (HeadlessException | NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "Error datos no validos");
                    }

                    accion = 0;
                }
            }
        } else if (accion == 4) {

            Nodo nodoC = plano.buscarNodo(me.getPoint());
            if (nodoC != null) {

                try {
                    int id = Integer.parseInt(JOptionPane.showInputDialog(this, "Digite Identificador del nodo: "));

                    if (plano.buscarNodo(id) == null) {

                        int tipo = Integer.parseInt(JOptionPane.showInputDialog(this, "Digite Tipo elemento (1-3): "));

                        Nodo nuevoN = new Nodo(id, tipo, nodoC.getX(), nodoC.getY());

                        historial.add("modificar,nodo," + id + "," + nodoC.getId() + "," + nodoC.getTipo());

                        plano.modificarNodo(nuevoN, nodoC.getId());

                        for (int i = 0; i < plano.getArcos().size(); i++) {
                            if (plano.getArcos().get(i).getNodoDestino() == nodoC.getId()) {
                                plano.getArcos().get(i).setNodoDestino(id);
                            }
                            if (plano.getArcos().get(i).getNodoOrigen() == nodoC.getId()) {
                                plano.getArcos().get(i).setNodoOrigen(id);
                            }
                        }
                        JOptionPane.showMessageDialog(null, "nodo modificado con exito");
                    } else {
                        JOptionPane.showMessageDialog(null, "id existente, no puede ser creado");
                    }

                } catch (HeadlessException | NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Error datos no validos");
                }
                accion = 0;
            }
        } else if (accion == 5) {

            Nodo nodoC = plano.buscarNodo(me.getPoint());
            if (nodoC != null) {

                JOptionPane.showMessageDialog(this, "Nodo Eliminado");
                historial.add("crear,nodo," + nodoC.getId() + "," + nodoC.getTipo() + "," + nodoC.getX() + "," + nodoC.getY());
                plano.eliminarNodo(nodoC);
                repaint();
            }
            
        } else if (accion == 6) {

            Arco arcoC = plano.buscarArco(me.getPoint());
            if (arcoC != null) {

                try {
                    int peso = Integer.parseInt(JOptionPane.showInputDialog(this, "Digite el peso: "));

                    if (peso > 0) {
                        Arco nuevoA = new Arco(arcoC.getId(), peso, arcoC.getNodoOrigen(), arcoC.getNodoDestino());

                        historial.add("modificar,arco," + arcoC.getId() + "," + arcoC.getPeso());

                        plano.modificarArco(nuevoA, arcoC.getId());
                        JOptionPane.showMessageDialog(this, "Arco modificado con exito");
                    } else {
                        JOptionPane.showMessageDialog(this, "Ingrese un peso valido");
                    }

                } catch (HeadlessException | NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Error datos no validos");
                }
                accion = 0;
            }
        } else if (accion == 7) {

            Arco arcoC = plano.buscarArco(me.getPoint());
            if (arcoC != null) {

                JOptionPane.showMessageDialog(this, "Arco Eliminado");
                historial.add("crear,arco," + arcoC.getId() + "," + arcoC.getPeso() + "," + arcoC.getNodoOrigen() + "," + arcoC.getNodoDestino());
                plano.eliminarArco(arcoC);
                repaint();
            }
        }

        repaint();
    }

    @Override
    public void mousePressed(MouseEvent me) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }
}
