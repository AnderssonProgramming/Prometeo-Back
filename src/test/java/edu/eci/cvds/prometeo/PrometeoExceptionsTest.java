package edu.eci.cvds.prometeo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Test class for PrometeoExceptions
 */
public class PrometeoExceptionsTest {

    @Test
    public void testConstructorWithMessage() {
        String testMessage = "Test exception message";
        PrometeoExceptions exception = new PrometeoExceptions(testMessage);
        assertEquals(testMessage, exception.getMessage());
    }

    @Test
    public void testExceptionIsRuntimeException() {
        PrometeoExceptions exception = new PrometeoExceptions("Test");
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    public void testConstantValues() {
        // Verify some of the constant values
        assertEquals("El usuario no existe", PrometeoExceptions.NO_EXISTE_USUARIO);
        assertEquals("El usuario no fue encontrado", PrometeoExceptions.USUARIO_NO_ENCONTRADO);
        assertEquals("El usuario ya existe", PrometeoExceptions.YA_EXISTE_USUARIO);
        assertEquals("La rutina no existe", PrometeoExceptions.NO_EXISTE_RUTINA);
        assertEquals("La reserva no existe", PrometeoExceptions.NO_EXISTE_RESERVA);
        assertEquals("Meta no encontrada.", PrometeoExceptions.NO_EXISTE_META);
        assertEquals("El equipo solicitado no existe", PrometeoExceptions.NO_EXISTE_EQUIPO);
    }
    
    @Test
    public void testThrowingException() {
        try {
            throw new PrometeoExceptions(PrometeoExceptions.USUARIO_NO_AUTORIZADO);
        } catch (PrometeoExceptions e) {
            assertEquals(PrometeoExceptions.USUARIO_NO_AUTORIZADO, e.getMessage());
        }
    }
}