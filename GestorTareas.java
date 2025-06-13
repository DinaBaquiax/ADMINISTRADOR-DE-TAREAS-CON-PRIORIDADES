import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class GestorTareas {
    static class Tarea {
        String nombre;
        String descripcion;
        int prioridad; // 1: Alta, 2: Media, 3: Baja
        String categoria;
        boolean completada;

        public Tarea(String nombre, String descripcion, int prioridad, String categoria, boolean completada) {
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.prioridad = prioridad;
            this.categoria = categoria;
            this.completada = completada;
        }
    }

    static ArrayList<Tarea> listaTareas = new ArrayList<>();
    static Stack<String> historialAcciones = new Stack<>();
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        int opcion;
        do {
            System.out.println("\n+--------------------------+");
            System.out.println("|     GESTIÓN DE TAREAS    |");
            System.out.println("+--------------------------+");
            System.out.println("| 1. Agregar tarea         |");
            System.out.println("| 2. Mostrar tareas        |");
            System.out.println("| 3. Ordenar tareas        |");
            System.out.println("| 4. Marcar como completada|");
            System.out.println("| 5. Eliminar tarea        |");
            System.out.println("| 6. Editar tarea          |");
            System.out.println("| 7. Exportar resumen CSV  |");
            System.out.println("| 8. Exportar resumen TXT  |");
            System.out.println("| 9. Ver historial         |");
            System.out.println("| 0. Salir                 |");
            System.out.println("+--------------------------+");
            System.out.print("Seleccione una opción: ");
            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1 -> agregarTarea();
                case 2 -> mostrarTareas();
                case 3 -> ordenarTareasPorPrioridad();
                case 4 -> marcarCompletada();
                case 5 -> eliminarTarea();
                case 6 -> editarTarea();
                case 7 -> exportarResumenCSV();
                case 8 -> exportarResumenTexto();
                case 9 -> verHistorial();
                case 0 -> System.out.println("¡Hasta luego!");
                default -> System.out.println("Opción inválida.");
            }
        } while (opcion != 0);
    }

    public static void agregarTarea() {
        System.out.print("Nombre: ");
        String nombre = sc.nextLine();
        System.out.print("Descripción: ");
        String descripcion = sc.nextLine();

        System.out.println("(" + descripcion.length() + " caracteres)");
        System.out.println("Compactación de frecuencia: " + compactarFrecuencia(descripcion));

        System.out.print("Prioridad (1=Alta, 2=Media, 3=Baja): ");
        int prioridad = sc.nextInt();
        sc.nextLine();
        System.out.print("Categoría: ");
        String categoria = sc.nextLine();

        listaTareas.add(new Tarea(nombre, descripcion, prioridad, categoria, false));
        historialAcciones.push("Tarea agregada: " + nombre);
        System.out.println("Tarea agregada.");
    }

    public static void mostrarTareas() {
        if (listaTareas.isEmpty()) {
            System.out.println("No hay tareas.");
            return;
        }

        int i = 1;
        for (Tarea t : listaTareas) {
            System.out.printf("%d. %s [%d caracteres] - %s - %s - %s\n",
                i++, t.nombre,
                t.descripcion.length(),
                prioridadTexto(t.prioridad),
                t.categoria,
                t.completada ? "Completada" : "Pendiente");
        }
    }

    public static void ordenarTareasPorPrioridad() {
        listaTareas.sort(Comparator.comparingInt(t -> t.prioridad));
        System.out.println("Tareas ordenadas por prioridad (Alta a Baja).");
    }

    public static void marcarCompletada() {
        mostrarTareas();
        System.out.print("Número de tarea a completar: ");
        int index = sc.nextInt() - 1;
        if (index >= 0 && index < listaTareas.size()) {
            listaTareas.get(index).completada = true;
            historialAcciones.push("Tarea completada: " + listaTareas.get(index).nombre);
            System.out.println("Tarea marcada como completada.");
        } else {
            System.out.println("Índice inválido.");
        }
    }

    public static void eliminarTarea() {
        mostrarTareas();
        System.out.print("Número de tarea a eliminar: ");
        int index = sc.nextInt() - 1;
        if (index >= 0 && index < listaTareas.size()) {
            historialAcciones.push("Tarea eliminada: " + listaTareas.get(index).nombre);
            listaTareas.remove(index);
            System.out.println("Tarea eliminada.");
        } else {
            System.out.println("Índice inválido.");
        }
    }

    public static void editarTarea() {
        mostrarTareas();
        System.out.print("Número de tarea a editar: ");
        int index = sc.nextInt() - 1;
        sc.nextLine();
        if (index >= 0 && index < listaTareas.size()) {
            Tarea t = listaTareas.get(index);
            System.out.print("Nuevo nombre (" + t.nombre + "): ");
            t.nombre = sc.nextLine();
            System.out.print("Nueva descripción: ");
            t.descripcion = sc.nextLine();

            System.out.println("Compactación de frecuencia: " + compactarFrecuencia(t.descripcion));

            System.out.print("Nueva prioridad (1=Alta, 2=Media, 3=Baja): ");
            t.prioridad = sc.nextInt();
            sc.nextLine();
            System.out.print("Nueva categoría: ");
            t.categoria = sc.nextLine();

            historialAcciones.push("Tarea editada: " + t.nombre);
            System.out.println("Tarea editada correctamente.");
        } else {
            System.out.println("Índice inválido.");
        }
    }

    public static void exportarResumenCSV() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("resumen.csv"))) {
            writer.write("Nombre,Descripción,Caracteres,Prioridad,Categoría,Estado\n");
            for (Tarea t : listaTareas) {
                String estado = t.completada ? "Completada" : "Pendiente";
                String prioridadStr = prioridadTexto(t.prioridad);
                writer.write(String.format("\"%s\",\"%s\",%d,%s,%s,%s\n",
                        t.nombre, t.descripcion, t.descripcion.length(),
                        prioridadStr, t.categoria, estado));
            }
        }
        System.out.println("Resumen exportado a resumen.csv");
    }

    public static void exportarResumenTexto() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("resumen.txt"))) {
            String formatoFila = "| %-12s | %-20s | %-10s | %-9s | %-10s | %-10s |\n";
            String separador = "+--------------+----------------------+------------+-----------+------------+------------+\n";

            writer.write(separador);
            writer.write(String.format(formatoFila, "Nombre", "Descripción", "Caracteres", "Prioridad", "Categoría", "Estado"));
            writer.write(separador);
            for (Tarea t : listaTareas) {
                String prioridadTexto = prioridadTexto(t.prioridad);
                writer.write(String.format(
                    formatoFila,
                    t.nombre,
                    t.descripcion.length() > 20 ? t.descripcion.substring(0, 17) + "..." : t.descripcion,
                    t.descripcion.length(),
                    prioridadTexto,
                    t.categoria,
                    t.completada ? "Completada" : "Pendiente"
                ));
            }
            writer.write(separador);
        }
        System.out.println("Resumen exportado a resumen.txt");
    }

    public static void verHistorial() {
        if (historialAcciones.isEmpty()) {
            System.out.println("No hay acciones registradas.");
            return;
        }

        System.out.println("\nHistorial de acciones:");
        for (String accion : historialAcciones) {
            System.out.println("- " + accion);
        }
    }

    public static String prioridadTexto(int prioridad) {
        return switch (prioridad) {
            case 1 -> "Alta";
            case 2 -> "Media";
            case 3 -> "Baja";
            default -> "N/A";
        };
    }

    public static String compactarFrecuencia(String texto) {
        Map<Character, Integer> contador = new TreeMap<>();
        for (char c : texto.toCharArray()) {
            if (c != ' ') { // Ignorar espacios en la compactación
                contador.put(c, contador.getOrDefault(c, 0) + 1);
            }
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Character, Integer> e : contador.entrySet()) {
            sb.append(e.getKey()).append(e.getValue());
        }
        return sb.toString();
    }
}

