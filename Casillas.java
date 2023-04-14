package Dijkstra;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Stack;
import javax.swing.*;

public class Casillas {

    public static JFrame panel_Inicio;
 
    public static void main(String[] args) {
        int width  = 693;//Dimensiones del JFrame
        int height = 545;
        panel_Inicio = new JFrame("Algoritmo Dijkstra");
        panel_Inicio.setContentPane(new Panel_Tablero(width,height));
        panel_Inicio.pack();
        panel_Inicio.setResizable(false);
        panel_Inicio.setLocationRelativeTo(null);
        panel_Inicio.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel_Inicio.setVisible(true);
    }

    public static class Panel_Tablero extends JPanel {

        private class MyMaze {
            private int dimensionX, dimensionY;         // dimension of maze
            private int gridDimensionX, gridDimensionY; // dimension of output grid
            private char[][] mazeGrid;                  // output grid
            private Celdas[][] casillas;                     // 2d array of Cells

            private class Celdas {
                int x, y; // coordenadas generadas
                ArrayList<Celdas> neighbors = new ArrayList<>();
                boolean muros = true;
                boolean casillas_Abiertas = true;
                
                Celdas(int x, int y) {
                    this(x, y, true);
                }
                
                Celdas(int x, int y, boolean es_Muro) {
                    this.x = x;
                    this.y = y;
                    this.muros = es_Muro;
                }
                
                boolean celda_debajo_Borde() {
                    return this.neighbors.contains(new Celdas(this.x, this.y + 1));
                }
                
                boolean celda_en_Extremos() {
                    return this.neighbors.contains(new Celdas(this.x + 1, this.y));
                }
                
                @Override
                public boolean equals(Object other) {
                    if (!(other instanceof Celdas)) return false;
                    Celdas otherCell = (Celdas) other;
                    return (this.x == otherCell.x && this.y == otherCell.y);
                }
                
                @Override
                public int hashCode() {
                    return this.x + this.y * 256;
                }
            }
            
            public Celdas obtener_Casillas(int x, int y) {
                try {
                    return casillas[x][y];//Cordenadas de cada casilla
                } catch (ArrayIndexOutOfBoundsException e) {
                    return null;
                }
            }
            
            public void actualizar_Grid() {
                char backChar = ' ', wallChar = 'X', cellChar = ' ';
                for (int x = 0; x < gridDimensionX; x ++)
                    for (int y = 0; y < gridDimensionY; y ++)
                        mazeGrid[x][y] = backChar;
                for (int x = 0; x < gridDimensionX; x ++)
                    for (int y = 0; y < gridDimensionY; y ++)
                        if (x % 2 == 0 || y % 2 == 0)
                            mazeGrid[x][y] = wallChar;
                for (int x = 0; x < dimensionX; x++)
                    for (int y = 0; y < dimensionY; y++) {
                        Celdas current = obtener_Casillas(x, y);
                        int gridX = x * 2 + 1, gridY = y * 2 + 1;
                        mazeGrid[gridX][gridY] = cellChar;
                        if (current.celda_debajo_Borde())
                            mazeGrid[gridX][gridY + 1] = cellChar;
                        if (current.celda_en_Extremos())
                            mazeGrid[gridX + 1][gridY] = cellChar;
                    }
            }
        }
       
        private class Casilla {
            int fila;     // Es el numero de celdas por filas.
            int columna;     // Es el numero de celdas por columnas.
            double distancia; // La distancia que se genererará.
            Casilla prev;   // Cada estado dependiendo de la celda.
            
            public Casilla(int fila, int columna){
               this.fila = fila;
               this.columna = columna;
            }
        }
      
        private class compararCeldas_Distancia implements Comparator<Casilla>{
            @Override
            public int compare(Casilla celda1, Casilla celda2){
                return Double.compare(celda1.distancia,celda2.distancia);
            }
        }
      
        private class MouseHandler implements MouseListener, MouseMotionListener {
            private int poner_Fila, poner_Columna, poner_Valor;
            
            @Override
            public void mousePressed(MouseEvent evt) {
                int row = (evt.getY() - 10) / tamaño_Tabla;
                int col = (evt.getX() - 10) / tamaño_Tabla;
                
                if (row >= 0 && row < filas && col >= 0 && col < columnas) {
                    if (TiempoReal ? true : !found && !searching){
                        if (TiempoReal)
                            crear_Grid();
                        poner_Fila = row;
                        poner_Columna = col;
                        poner_Valor = grid[row][col];
                        if (poner_Valor == EMPTY)
                            grid[row][col] = OBST;
                        if (poner_Valor == OBST)
                            grid[row][col] = EMPTY;
                        if (TiempoReal && dijkstra.isSelected())
                            initializeDijkstra();
                    }
                        repaint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent evt) {
                int row = (evt.getY() - 10) / tamaño_Tabla;
                int col = (evt.getX() - 10) / tamaño_Tabla;
                if (row >= 0 && row < filas && col >= 0 && col < columnas){
                    if (TiempoReal ? true : !found && !searching){
                        if (TiempoReal)
                            crear_Grid();
                        if (!(row == poner_Fila && col == poner_Columna) && (poner_Valor == ROBOT || poner_Valor == TARGET)){
                            int new_val = grid[row][col];
                            if (new_val == EMPTY){
                                grid[row][col] = poner_Valor;
                                if (poner_Valor == ROBOT) {
                                    inicia_Algoritmo.fila = row;
                                    inicia_Algoritmo.columna = col;
                                } else {
                                    posicion_Casilla.fila = row;
                                    posicion_Casilla.columna = col;
                                }
                                grid[poner_Fila][poner_Columna] = new_val;
                                poner_Fila = row;
                                poner_Columna = col;
                                poner_Valor = grid[row][col];
                            }
                        } else if (grid[row][col] != ROBOT && grid[row][col] != TARGET)
                            grid[row][col] = OBST;
                        if (TiempoReal && dijkstra.isSelected())
                            initializeDijkstra();
                    }
                        repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent evt) { }
            @Override
            public void mouseEntered(MouseEvent evt) { }
            @Override
            public void mouseExited(MouseEvent evt) { }
            @Override
            public void mouseMoved(MouseEvent evt) { }
            @Override
            public void mouseClicked(MouseEvent evt) { }
        }
        
        private class RepaintAction implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent evt) {
                checkTermination();
                repaint();
                if (endOfSearch)
                {
                    animation = false;
                    timer.stop();
                }
            }
        }
              
        private final static int
            INFINITY = Integer.MAX_VALUE, // The representation of the infinite
            EMPTY    = 0,  // empty cell
            OBST     = 1,  // cell with obstacle
            ROBOT    = 2,  // the position of the robot
            TARGET   = 3,  // the position of the target
            FRONTIER = 4,  // cells that form the frontier (OPEN SET)
            CLOSED   = 5,  // cells that form the CLOSED SET
            ROUTE    = 6;  // cells that form the robot-to-target path
        
        JSpinner rowsSpinner, columnsSpinner; //JSpinner de filas y Columnas a seleccionar.
        
        int filas, columnas, tamaño_Tabla;//Se genera el numero de pixeles
        
        ArrayList<Casilla> openSet   = new ArrayList();
        ArrayList<Casilla> closedSet = new ArrayList();
        ArrayList<Casilla> graph     = new ArrayList();
         
        Casilla inicia_Algoritmo;
        Casilla posicion_Casilla;
              
        JButton BtnNuevoTablero, BtnLimpiar, BtnPasoAPaso, BtnAnimacion;
        
        JRadioButton dijkstra;
        
        JSlider slider;
        
        int[][] grid;        // the grid
        boolean TiempoReal;    // Solution is displayed instantly
        boolean found;       // flag that the goal was found
        boolean searching;   // flag that the search is in progress
        boolean endOfSearch; // flag that the search came to an end
        boolean animation;   // flag that the animation is running
        int delay;           // time delay of animation (in msec)
        int expanded;        // the number of nodes that have been expanded
        
        RepaintAction action = new RepaintAction();
        
        Timer timer;
      
        public Panel_Tablero(int width, int height) {
      
            super.setLayout(null);
            
            MouseHandler listener = new MouseHandler();
            super.addMouseListener(listener);
            super.addMouseMotionListener(listener);

            super.setBorder(BorderFactory.createMatteBorder(2,2,2,2,Color.red));
            super.setPreferredSize( new Dimension(width,height) );

            grid = new int[filas][columnas];

            // We create the contents of the panel

            JLabel rowsLbl = new JLabel("# de filas:", JLabel.RIGHT);
            rowsLbl.setFont(new Font("Helvetica",Font.PLAIN,13));

            SpinnerModel rowModel = new SpinnerNumberModel(10, 2, 100,1);
            rowsSpinner = new JSpinner(rowModel);
 
            JLabel columnsLbl = new JLabel("# de columnas:", JLabel.RIGHT);
            columnsLbl.setFont(new Font("Helvetica",Font.PLAIN,13));

            SpinnerModel colModel = new SpinnerNumberModel(10, 2,100,1);
            columnsSpinner = new JSpinner(colModel);

            BtnNuevoTablero = new JButton("Nuevo tablero");
            BtnNuevoTablero.setBackground(Color.lightGray);
            BtnNuevoTablero.setToolTipText("Clears and redraws the grid according to the given rows and columns");
            BtnNuevoTablero.addActionListener(this::resetButtonActionPerformed);

            BtnLimpiar = new JButton("Limpiar Tablero");
            BtnLimpiar.setBackground(Color.lightGray);
            BtnLimpiar.setToolTipText
                    ("First click: clears search, Second click: clears obstacles");
            BtnLimpiar.addActionListener(this::clearButtonActionPerformed);

            BtnPasoAPaso = new JButton("Paso a paso");
            BtnPasoAPaso.setBackground(Color.lightGray);
            BtnPasoAPaso.setToolTipText
                    ("The search is performed step-by-step for every click");
            BtnPasoAPaso.addActionListener(this::stepButtonActionPerformed);

            BtnAnimacion = new JButton("Animación");
            BtnAnimacion.setBackground(Color.lightGray);
            BtnAnimacion.setToolTipText
                    ("The search is performed automatically");
            BtnAnimacion.addActionListener(this::animationButtonActionPerformed);

            JLabel delayLbl = new JLabel("Delay (0-1000 msec)", JLabel.CENTER);
            delayLbl.setFont(new Font("Helvetica",Font.PLAIN,10));
            
            slider = new JSlider(0,1000,500); // initial value of delay 500 msec
            slider.setToolTipText
                    ("Regulates the delay for each step (0 to 1000 msec)");
            
            delay = slider.getValue();
            
            ButtonGroup algoGroup = new ButtonGroup();

            dijkstra = new JRadioButton("Dijkstra");
            dijkstra.setToolTipText("Dijkstra's algorithm");
            algoGroup.add(dijkstra);

            JPanel algoPanel = new JPanel();

            super.add(rowsLbl);
            super.add(rowsSpinner);
            super.add(columnsLbl);
            super.add(columnsSpinner);
            super.add(BtnNuevoTablero);
            super.add(BtnLimpiar);
            super.add(BtnPasoAPaso);
            super.add(BtnAnimacion);
            super.add(delayLbl);
            super.add(slider);
            super.add(dijkstra);
            super.add(algoPanel);

            rowsLbl.setBounds(520, 5, 130, 25);//#Numero de filas
            rowsSpinner.setBounds(655, 5, 35, 25);//Cuadro de numero de filas del tablero
            columnsLbl.setBounds(520, 35, 130, 25);//Numero de columnas
            columnsSpinner.setBounds(655, 35, 35, 25);//Cuadro de numeros de columnas en el tablero
            BtnNuevoTablero.setBounds(520, 65, 170, 25);//Boton del nuevo tablero
            BtnLimpiar.setBounds(520, 95, 170, 25);//Boton de limpiar tablero
            BtnPasoAPaso.setBounds(520, 125, 170, 25);//Boton del paso a paso
            BtnAnimacion.setBounds(520, 155, 170, 25);//Boton de animacion
            delayLbl.setBounds(520, 200, 170, 25);//Label del delay
            slider.setBounds(520, 215, 170, 25);//Barra del delay
            dijkstra.setBounds(530, 300, 70, 25);//checkBox del Dijkstra
            algoPanel.setLocation(520,280);
            algoPanel.setSize(170, 100);

            timer = new Timer(delay, action);
            
            initializeGrid(false);
        }

        private void initializeGrid(Boolean makeMaze) {                                           
            filas    = (int)(rowsSpinner.getValue());
            columnas = (int)(columnsSpinner.getValue());
            
            if (makeMaze && filas % 2 == 0)
                filas -= 1;
            if (makeMaze && columnas % 2 == 0)
                columnas -= 1;
            
            tamaño_Tabla = 500/(filas > columnas ? filas : columnas);
            grid = new int[filas][columnas];
            inicia_Algoritmo = new Casilla(filas-2,1);
            posicion_Casilla = new Casilla(1,columnas-2);
            
            crear_Grid();            
        }
   
        private void crear_Grid() {
            if (searching || endOfSearch){ 
                for (int r = 0; r < filas; r++)
                    for (int c = 0; c < columnas; c++) {
                        if (grid[r][c] == FRONTIER || grid[r][c] == CLOSED || grid[r][c] == ROUTE)
                            grid[r][c] = EMPTY;
                        if (grid[r][c] == ROBOT)
                            inicia_Algoritmo = new Casilla(r,c);
                        if (grid[r][c] == TARGET)
                            posicion_Casilla = new Casilla(r,c);
                    }
                searching = false;
            } else {
                for (int r = 0; r < filas; r++)
                    for (int c = 0; c < columnas; c++)
                        grid[r][c] = EMPTY;
                inicia_Algoritmo = new Casilla(filas-2,1);
                posicion_Casilla = new Casilla(1,columnas-2);
            }
            
            expanded = 0;
            found = false;
            searching = false;
            endOfSearch = false;
         
            openSet.removeAll(openSet);
            openSet.add(inicia_Algoritmo);
            closedSet.removeAll(closedSet);
         
            grid[posicion_Casilla.fila][posicion_Casilla.columna] = TARGET; 
            grid[inicia_Algoritmo.fila][inicia_Algoritmo.columna] = ROBOT;
            timer.stop();
            repaint();
        }

        private void enableRadiosAndChecks() {                                           
            slider.setEnabled(true);
            dijkstra.setEnabled(true);
        }
    
        private void disableRadiosAndChecks() {                                           
            slider.setEnabled(false);
            dijkstra.setEnabled(false);
        }
    
        private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {                                           
            animation = false;
            TiempoReal = false;
            BtnPasoAPaso.setEnabled(true);
            BtnAnimacion.setEnabled(true);
            enableRadiosAndChecks();
            initializeGrid(false);
        }
        
        private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {
            animation = false;
            TiempoReal = false;
            BtnPasoAPaso.setEnabled(true);
            BtnAnimacion.setEnabled(true);
            enableRadiosAndChecks();
            crear_Grid();
        }
    
        private void stepButtonActionPerformed(java.awt.event.ActionEvent evt) {
            animation = false;
            timer.stop();
            if (found || endOfSearch)
                return;
            if (!searching && dijkstra.isSelected())
                initializeDijkstra();
            searching = true;
            disableRadiosAndChecks();
            slider.setEnabled(true);
            checkTermination();
            repaint();
        } 
    
        private void animationButtonActionPerformed(java.awt.event.ActionEvent evt) {
            animation = true;
            if (!searching && dijkstra.isSelected())
                initializeDijkstra();
            searching = true;
            disableRadiosAndChecks();
            slider.setEnabled(true);
            delay = slider.getValue();
            timer.setDelay(delay);
            timer.start();
        }
       
        public void checkTermination() {
            
            if ((dijkstra.isSelected() && graph.isEmpty()) ||
                          (!dijkstra.isSelected() && openSet.isEmpty()) ) {
                endOfSearch = true;
                grid[inicia_Algoritmo.fila][inicia_Algoritmo.columna]=ROBOT;
                BtnPasoAPaso.setEnabled(false);
                BtnAnimacion.setEnabled(false);
                repaint();
            } else {
                expandNode();
                if (found) {
                    endOfSearch = true;
                    plotRoute();
                    BtnPasoAPaso.setEnabled(false);
                    BtnAnimacion.setEnabled(false);
                    slider.setEnabled(false);
                    repaint();
                }
            }
        }

        private void expandNode(){
            if (dijkstra.isSelected()){
                Casilla u;
                if (graph.isEmpty())
                    return;
                
                u = graph.remove(0);
                closedSet.add(u);
                if (u.fila == posicion_Casilla.fila && u.columna == posicion_Casilla.columna){
                    found = true;
                    return;
                }
                expanded++;
                grid[u.fila][u.columna] = CLOSED;
                if (u.distancia == INFINITY){
                    return;
                }
                
                ArrayList<Casilla> neighbors = createSuccesors(u, false);
                for (Casilla v: neighbors) {
                    double alt = u.distancia + distBetween(u,v);
                    if (alt < v.distancia) {
                        v.distancia = alt;
                        v.prev = u;
                        grid[v.fila][v.columna] = FRONTIER;
                        Collections.sort(graph, new compararCeldas_Distancia());
                    }
                }
            } 
        }
        
        private ArrayList<Casilla> createSuccesors(Casilla current, boolean makeConnected){
            int r = current.fila;
            int c = current.columna;
            
            ArrayList<Casilla> temp = new ArrayList<>();
            
            if (r > 0 && grid[r-1][c] != OBST &&
                    ((dijkstra.isSelected()) ? true :
                          isInList(openSet,new Casilla(r-1,c)) == -1 &&
                          isInList(closedSet,new Casilla(r-1,c)) == -1)) {
                Casilla cell = new Casilla(r-1,c);
                
                if (dijkstra.isSelected()){
                    if (makeConnected)
                        temp.add(cell);
                    else {
                        int graphIndex = isInList(graph,cell);
                        if (graphIndex > -1)
                            temp.add(graph.get(graphIndex));
                    }
                } else {
                    cell.prev = current;
                    temp.add(cell);
                 }
            }
            
            if (c < columnas-1 && grid[r][c+1] != OBST &&
                    ((dijkstra.isSelected())? true :
                          isInList(openSet,new Casilla(r,c+1)) == -1 &&
                          isInList(closedSet,new Casilla(r,c+1)) == -1)) {
                Casilla cell = new Casilla(r,c+1);
                if (dijkstra.isSelected()){
                    if (makeConnected)
                        temp.add(cell);
                    else {
                        int graphIndex = isInList(graph,cell);
                        if (graphIndex > -1)
                            temp.add(graph.get(graphIndex));
                    }
                } else {
                    cell.prev = current;
                    temp.add(cell);
                }
            }
            
            if (r < filas-1 && grid[r+1][c] != OBST && ((dijkstra.isSelected()) ? true : isInList(openSet,new Casilla(r+1,c)) == -1 && isInList(closedSet,new Casilla(r+1,c)) == -1)) {
                Casilla cell = new Casilla(r+1,c);
                if (dijkstra.isSelected()){
                    if (makeConnected)
                        temp.add(cell);
                    else {
                        int graphIndex = isInList(graph,cell);
                        if (graphIndex > -1)
                            temp.add(graph.get(graphIndex));
                    }
                } else {
                    cell.prev = current;
                    temp.add(cell);
                }
            }
            
            if (c > 0 && grid[r][c-1] != OBST && ((dijkstra.isSelected()) ? true : isInList(openSet,new Casilla(r,c-1)) == -1 && isInList(closedSet,new Casilla(r,c-1)) == -1)) {
                Casilla cell = new Casilla(r,c-1);
                if (dijkstra.isSelected()){
                    if (makeConnected)
                        temp.add(cell);
                    else {
                        int graphIndex = isInList(graph,cell);
                        if (graphIndex > -1)
                            temp.add(graph.get(graphIndex));
                    }
                } else {
                    cell.prev = current;
                    temp.add(cell);
                }
            }
            return temp;
        }
        
        private double distBetween(Casilla u, Casilla v){
            double dist;
            int dx = u.columna-v.columna;
            int dy = u.fila-v.fila;
                dist = Math.abs(dx)+Math.abs(dy);
            return dist;
        }
        
        private int isInList(ArrayList<Casilla> list, Casilla current){
            int index = -1;
            for (int i = 0 ; i < list.size(); i++) {
                Casilla listItem = list.get(i);
                if (current.fila == listItem.fila && current.columna == listItem.columna) {
                    index = i;
                    break;
                }
            }
            return index;
        }
        
        private void plotRoute(){
            int steps = 0;
            double distance = 0;
            int index = isInList(closedSet,posicion_Casilla);
            Casilla cur = closedSet.get(index);
            grid[cur.fila][cur.columna]= TARGET;
            do {
                steps++;
                distance++;
                cur = cur.prev;
                grid[cur.fila][cur.columna] = ROUTE;
            } while (!(cur.fila == inicia_Algoritmo.fila && cur.columna == inicia_Algoritmo.columna));
            grid[inicia_Algoritmo.fila][inicia_Algoritmo.columna]=ROBOT;
        }
        
        private void findConnectedComponent(Casilla v){
            Stack<Casilla> stack;
            stack = new Stack();
            ArrayList<Casilla> succesors;
            stack.push(v);
            graph.add(v);
            while(!stack.isEmpty()){
                v = stack.pop();
                succesors = createSuccesors(v, true);
                for (Casilla c: succesors) {
                    if (isInList(graph, c) == -1){
                        stack.push(c);
                        graph.add(c);
                    }
                }
            }
        }
        
        private void initializeDijkstra() {
            graph.removeAll(graph);
            findConnectedComponent(inicia_Algoritmo);
            
            for (Casilla v: graph) {
                v.distancia = INFINITY;
                v.prev = null;
            }
            graph.get(isInList(graph,inicia_Algoritmo)).distancia = 0;
            
            Collections.sort(graph, new compararCeldas_Distancia());
            closedSet.removeAll(closedSet);
        }

       
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.DARK_GRAY);
            g.fillRect(10, 10, columnas*tamaño_Tabla+1, filas*tamaño_Tabla+1);

            for (int r = 0; r < filas; r++) {
                for (int c = 0; c < columnas; c++) {
                    if (grid[r][c] == EMPTY) {
                        g.setColor(Color.WHITE);
                    } else if (grid[r][c] == ROBOT) {
                        g.setColor(Color.RED);
                    } else if (grid[r][c] == TARGET) {
                        g.setColor(Color.GREEN);
                    } else if (grid[r][c] == OBST) {
                        g.setColor(Color.BLACK);
                    } else if (grid[r][c] == FRONTIER) {
                        g.setColor(Color.BLUE);
                    } else if (grid[r][c] == CLOSED) {
                        g.setColor(Color.CYAN);
                    } else if (grid[r][c] == ROUTE) {
                        g.setColor(Color.YELLOW);
                    }
                    g.fillRect(11 + c*tamaño_Tabla, 11 + r*tamaño_Tabla, tamaño_Tabla - 1, tamaño_Tabla - 1);
                }
            }
        } // end paintComponent()
    } // end nested classs MazePanel
} // end class Maze
