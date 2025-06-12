import java.util.*;
import java.io.*;

class Tarea implements Comparable<Tarea> {
    String nombre;
    String descripcion;
    int prioridad; // 1 = Alta, 2 = Media, 3 = Baja
    String categoria;
    boolean completada;

    public Tarea(String nombre, String descripcion, int prioridad, String categoria) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.categoria = categoria;
        this.completada = false;
    }

    @Override
    public int compareTo(Tarea otra) {
        return Integer.compare(this.prioridad, otra.prioridad);
    }

    @Override
    public String toString() {
        String prioridadTexto = switch (prioridad) {
            case 1 -> "Alta (Urgente)";
            case 2 -> "Media";
            case 3 -> "Baja";
            default -> "Desconocida";
        };
        return "[" + (completada ? "✔" : "✘") + "] " + nombre + " (Prioridad: " + prioridadTexto + ", Categoría: " + categoria + ")";
    }
}

public class GestorTareas {
    private PriorityQueue<Tarea> colaPrioridad = new PriorityQueue<>(); 
    private List<Tarea> listaTareas = new ArrayList<>();  
    private Stack<String> historial = new Stack<>();
    private final Scanner scanner = new Scanner(System.in);

    public void agregarTarea() {
        System.out.print("Nombre de la tarea: ");
        String nombre = scanner.nextLine();
        System.out.print("Descripción: ");
        String descripcion = scanner.nextLine();
        System.out.print("Prioridad (1 = Alta, 2 = Media, 3 = Baja): ");
        int prioridad = Integer.parseInt(scanner.nextLine());
        System.out.print("Categoría: ");
        String categoria = scanner.nextLine();

        Tarea tarea = new Tarea(nombre, descripcion, prioridad, categoria);
        listaTareas.add(tarea);
        colaPrioridad.offer(tarea);
        historial.push("Agregada tarea: " + nombre);

        System.out.println();
        mostrarTareas();
    }

    public void completarTarea() {
        System.out.print("Nombre de la tarea a completar: ");
        String nombre = scanner.nextLine();
        for (Tarea t : listaTareas) {
            if (t.nombre.equalsIgnoreCase(nombre)) {
                t.completada = true;
                historial.push("Completada tarea: " + nombre);
                System.out.println("Tarea marcada como completada.\n");
                mostrarTareas();
                return;
            }
        }
        System.out.println("Tarea no encontrada.");
    }

    public void eliminarTarea() {
        System.out.print("Nombre de la tarea a eliminar: ");
        String nombre = scanner.nextLine();
        boolean eliminada = listaTareas.removeIf(t -> t.nombre.equalsIgnoreCase(nombre));
        colaPrioridad.removeIf(t -> t.nombre.equalsIgnoreCase(nombre));
        if (eliminada) {
            historial.push("Eliminada tarea: " + nombre);
            System.out.println("Tarea eliminada.\n");
            mostrarTareas();
        } else {
            System.out.println("Tarea no encontrada.");
        }
    }

    public void mostrarTareas() {
        System.out.println("--- Tareas Pendientes Ordenadas por Prioridad ---");
        PriorityQueue<Tarea> copia = new PriorityQueue<>(colaPrioridad);
        boolean hayPendientes = false;
        while (!copia.isEmpty()) {
            Tarea t = copia.poll();
            if (!t.completada) {
                System.out.println(t);
                hayPendientes = true;
            }
        }
        if (!hayPendientes) {
            System.out.println("(No hay tareas pendientes)");
        }
    }

    public void mostrarHistorial() {
        System.out.println("--- Historial de Acciones ---");
        if (historial.isEmpty()) {
            System.out.println("(Historial vacío)");
        } else {
            for (String accion : historial) {
                System.out.println(accion);
            }
        }
    }

    public void exportarResumen() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("resumen.csv"))) {
            writer.write("Nombre,Descripción,Prioridad,Categoría,Estado\n");
            for (Tarea t : listaTareas) {
                writer.write(t.nombre + "," + t.descripcion + "," + t.prioridad + "," + t.categoria + "," + (t.completada ? "Completada" : "Pendiente") + "\n");
            }
        }
        System.out.println("Resumen exportado a resumen.csv");
    }

    public void menu() throws IOException {
        int opcion;
        do {
            System.out.println("\n--- Menú ---");
            System.out.println("1. Agregar tarea");
            System.out.println("2. Completar tarea");
            System.out.println("3. Eliminar tarea");
            System.out.println("4. Mostrar tareas");
            System.out.println("5. Mostrar historial");
            System.out.println("6. Exportar resumen");
            System.out.println("0. Salir");
            System.out.print("Elige una opción: ");
            String entrada = scanner.nextLine();
            try {
                opcion = Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                opcion = -1;
            }

            switch (opcion) {
                case 1 -> agregarTarea();
                case 2 -> completarTarea();
                case 3 -> eliminarTarea();
                case 4 -> mostrarTareas();
                case 5 -> mostrarHistorial();
                case 6 -> exportarResumen();
                case 0 -> System.out.println("Saliendo...");
                default -> System.out.println("Opción no válida");
            }
        } while (opcion != 0);
    }

    public static void main(String[] args) throws IOException {
        GestorTareas gestor = new GestorTareas();
        gestor.menu();
    }
}
