package edu.eci.cvds.prometeo.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface WaitlistService {
    
    /**
     * Añade un usuario a la lista de espera para una sesión
     * @param userId ID del usuario
     * @param sessionId ID de la sesión
     * @return ID de la entrada en la lista de espera
     */
    UUID addToWaitlist(UUID userId, UUID sessionId);
    
    /**
     * Obtiene la posición de un usuario en la lista de espera
     * @param userId ID del usuario
     * @param sessionId ID de la sesión
     * @return posición en la lista (1-based) o 0 si no está en lista
     */
    int getWaitlistPosition(UUID userId, UUID sessionId);
    
    /**
     * Notifica al siguiente usuario en la lista de espera cuando se libera un cupo
     * @param sessionId ID de la sesión con cupo liberado
     * @return true si se notificó exitosamente
     */
    boolean notifyNextInWaitlist(UUID sessionId);
    
    /**
     * Elimina a un usuario de la lista de espera
     * @param userId ID del usuario
     * @param sessionId ID de la sesión
     * @return true si se eliminó correctamente
     */
    boolean removeFromWaitlist(UUID userId, UUID sessionId);
    
    /**
     * Obtiene estadísticas de la lista de espera para una sesión
     * @param sessionId ID de la sesión
     * @return mapa con estadísticas
     */
    Map<String, Object> getWaitlistStats(UUID sessionId);
    
    /**
     * Obtiene la lista de sesiones en las que un usuario está en lista de espera
     * @param userId ID del usuario
     * @return lista de detalles de sesiones
     */
    List<Map<String, Object>> getUserWaitlistSessions(UUID userId);
}