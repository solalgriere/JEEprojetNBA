package com.actorframework.core.logging;

import com.actorframework.core.actor.Actor;
import com.actorframework.core.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Système de logging dédié aux acteurs.
 * Écrit les logs dans des fichiers séparés par acteur.
 */
@Slf4j
@Component
public class ActorLogger {
    
    private static final String LOG_DIR = "logs/actors/";
    private final Map<String, PrintWriter> logWriters = new ConcurrentHashMap<>();
    private final Map<String, ReentrantLock> locks = new ConcurrentHashMap<>();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    public ActorLogger() {
        // Créer le répertoire de logs
        java.io.File dir = new java.io.File(LOG_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    private PrintWriter getLogWriter(String actorId) {
        return logWriters.computeIfAbsent(actorId, id -> {
            try {
                String fileName = LOG_DIR + actorId + ".log";
                FileWriter fileWriter = new FileWriter(fileName, true);
                return new PrintWriter(fileWriter, true);
            } catch (IOException e) {
                log.error("Failed to create log file for actor {}", actorId, e);
                return null;
            }
        });
    }
    
    private ReentrantLock getLock(String actorId) {
        return locks.computeIfAbsent(actorId, id -> new ReentrantLock());
    }
    
    private void writeLog(String actorId, String event, String details) {
        ReentrantLock lock = getLock(actorId);
        lock.lock();
        try {
            PrintWriter writer = getLogWriter(actorId);
            if (writer != null) {
                String timestamp = LocalDateTime.now().format(formatter);
                writer.println(String.format("[%s] [%s] %s - %s", 
                    timestamp, actorId, event, details));
            }
        } finally {
            lock.unlock();
        }
    }
    
    public void logActorCreation(Actor actor) {
        writeLog(actor.getActorId(), "ACTOR_CREATED", 
            String.format("Actor created at path: %s", actor.getActorPath()));
    }
    
    public void logActorStopped(Actor actor) {
        writeLog(actor.getActorId(), "ACTOR_STOPPED", 
            String.format("Actor stopped at path: %s", actor.getActorPath()));
    }
    
    public void logMessageReceived(Actor actor, Message message) {
        writeLog(actor.getActorId(), "MESSAGE_RECEIVED", 
            String.format("From: %s, Type: %s, ID: %s", 
                message.getSenderPath(), message.getMessageType(), message.getMessageId()));
    }
    
    public void logMessageSent(Actor actor, Message message, Object response) {
        writeLog(actor.getActorId(), "MESSAGE_SENT", 
            String.format("To: %s, Type: %s, Response: %s", 
                message.getReceiverPath(), message.getMessageType(), response));
    }
    
    public void logError(Actor actor, Throwable error, Message message) {
        writeLog(actor.getActorId(), "ERROR", 
            String.format("Error: %s, Message: %s", 
                error.getMessage(), message != null ? message.getMessageId() : "N/A"));
    }
    
    public void close() {
        logWriters.values().forEach(PrintWriter::close);
        logWriters.clear();
        locks.clear();
    }
}

