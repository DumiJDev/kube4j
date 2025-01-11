package io.github.dumijdev.kube4j.builder.logs;

import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LogCollector implements Closeable {
  protected final List<String> logs = new LinkedList<>();
  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
  protected boolean finished = false;
  protected final LogCollectionListener listener;
  protected final String id;

  public LogCollector(LogCollectionListener listener, String id) {
    this.listener = listener;
    this.id = id;
  }

  // Método para coletar os logs
  public void collect(String log) {
    lock.writeLock().lock();
    try {
      logs.add(log);
    } finally {
      lock.writeLock().unlock();
    }
  }

  // Notifica o LogManager que a coleta terminou
  public void finish() {
    lock.writeLock().lock();
    try {
      finished = true;
      if (listener != null) {
        listener.onLogsCollected(logId());
      }
    } finally {
      lock.writeLock().unlock();
    }
  }

  private String logId() {
    return id;
  }

  // Verifica se a coleta terminou
  public boolean isFinished() {
    lock.readLock().lock();
    try {
      return finished;
    } finally {
      lock.readLock().unlock();
    }
  }

  // Retorna os logs desde um determinado offset
  public List<String> getLogsFromOffset(int offset) {
    lock.readLock().lock();
    try {
      if (offset >= logs.size()) {
        return new LinkedList<>();
      }
      return new LinkedList<>(logs.subList(offset, logs.size()));
    } finally {
      lock.readLock().unlock();
    }
  }

  // Cria um novo listener que sempre começará a ler os logs desde o início
  public LogListener createListener() {
    // Reinicia o offset para que o listener comece do zero
    return new LogListener(this);
  }

  // Getter para logs, utilizado pelo listener para leitura
  public List<String> getLogs() {
    lock.readLock().lock();
    try {
      return new LinkedList<>(logs);
    } finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public void close() throws IOException {
    finish();
  }
}
