package edu.eci.cvds.prometeo;

/**
 * This class contains all the exceptions that we'll do in Prometeo.
 * @author Cristian Santiago Pedraza Rodríguez
 * @author Andersson David Sánchez Méndez
 * @author Santiago Botero
 * @author Juan Andrés Rodríguez Peñuela
 * @author Ricardo Ayala
 * 
 * @version 2025
 */
public class PrometeoExceptions extends RuntimeException {

    public static final String NO_EXISTE_USUARIO = "El usuario no existe";
    public static final String USUARIO_NO_ENCONTRADO = "El usuario no fue encontrado";
    public static final String YA_EXISTE_USUARIO = "El usuario ya existe";
    public static final String NO_EXISTE_RUTINA = "La rutina no existe";
    public static final String NO_EXISTE_RESERVA = "La reserva no existe";
    public static final String YA_EXISTE_RUTINA = "La rutina ya existe";
    public static final String NO_EXISTE_SESION = "La sesión de gimnasio no existe";
    public static final String SESION_NO_ENCONTRADA = "La sesión de gimnasio no fue encontrada";
    public static final String ID_NO_VALIDO = "El id no es valido";
    public static final String CORREO_NO_VALIDO = "El correo no es valido";
    public static final String YA_EXISTE_CORREO = "El correo ya existe";
    public static final String HORA_NO_VALIDA = "La hora no es valida";
    public static final String DIA_NO_VALIDO = "El dia no es valido";
    public static final String CAPACIDAD_NO_VALIDA = "La capacidad no es valida";
    public static final String MEDIDA_NO_VALIDA = "La medida no es válida";
    public static final String NOMBRE_NO_VALIDO = "El nombre no es valido";
    public static final String APELLIDO_NO_VALIDO = "El apellido no es valido";
    public static final String NO_ES_ENTRENADOR = "El usuario no tiene permisos de entrenador";
    public static final String CODIGO_PROGRAMA_NO_VALIDO = "El código de programa no es válido";
    public static final String NOMBRE_EJERCICIO_NO_VALIDO = "El nombre del ejercicio no es válido";
    public static final String NIVEL_DIFICULTAD_NO_VALIDO = "El nivel de dificultad no es válido";
    public static final String FECHA_PASADA = "La fecha de reserva no puede ser en el pasado";
    public static final String CAPACIDAD_EXCEDIDA = "La capacidad máxima de la sesión ha sido excedida";
    public static final String PESO_NO_VALIDO = "El peso ingresado no es válido";
    public static final String REPETICIONES_NO_VALIDAS = "El número de repeticiones no es válido";
    public static final String SERIES_NO_VALIDAS = "El número de series no es válido";
    public static final String YA_EXISTE_RESERVA = "Ya existe una reserva para esta sesión";
    public static final String OBJETIVO_NO_VALIDO = "El objetivo de la rutina no puede estar vacío";
    public static final String CANCELACION_TARDIA = "No se puede cancelar la reserva con menos de 2 horas de anticipación";
    
    // Nuevos mensajes para GymReservationService
    public static final String HORARIO_NO_DISPONIBLE = "El horario seleccionado no está disponible";
    public static final String LIMITE_RESERVAS_ALCANZADO = "El usuario ha alcanzado el límite máximo de reservas activas";
    public static final String USUARIO_NO_AUTORIZADO = "El usuario no está autorizado para realizar esta acción";
    public static final String RESERVA_YA_CANCELADA = "La reserva ya ha sido cancelada";
    public static final String NO_CANCELAR_RESERVAS_PASADAS = "No se pueden cancelar reservas pasadas";
    public static final String SOLO_RESERVAS_CONFIRMADAS = "Solo las reservas confirmadas pueden ser marcadas como asistidas";
    public static final String EQUIPAMIENTO_NO_DISPONIBLE = "Ninguno de los equipos solicitados está disponible";
    public static final String NO_EXISTE_EQUIPAMIENTO = "El equipamiento solicitado no existe";
    public static final String SESION_YA_EXISTE_HORARIO = "Una sesión ya ha sido agendada en este horario";
    
    /**
     * Constructor of the class.
     * @param message The message of the exception.
     */
    public PrometeoExceptions(String message) {
        super(message);
    }
}