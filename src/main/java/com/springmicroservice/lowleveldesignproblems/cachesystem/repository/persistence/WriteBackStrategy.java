package com.springmicroservice.lowleveldesignproblems.cachesystem.repository.persistence;

import com.springmicroservice.lowleveldesignproblems.cachesystem.domain.model.CacheEntity;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Asynchronous write execution strategy. Operations return immediately to the
 * cache client,
 * while a daemon thread consumes ops from a blocking queue and persists them to
 * the H2 DB.
 */
public class WriteBackStrategy implements PersistenceStrategy {

  private final CacheRepository cacheRepository;
  private final BlockingQueue<CacheOperation> operationsQueue;
  private final Thread workerThread;
  private volatile boolean isRunning;

  private enum OpType {
    SAVE, DELETE
  }

  private static class CacheOperation {
    OpType type;
    CacheEntity entity;
    String key;

    static CacheOperation saveOp(CacheEntity e) {
      CacheOperation op = new CacheOperation();
      op.type = OpType.SAVE;
      op.entity = e;
      return op;
    }

    static CacheOperation deleteOp(String k) {
      CacheOperation op = new CacheOperation();
      op.type = OpType.DELETE;
      op.key = k;
      return op;
    }
  }

  public WriteBackStrategy(CacheRepository cacheRepository) {
    this.cacheRepository = cacheRepository;
    this.operationsQueue = new LinkedBlockingQueue<>();
    this.isRunning = true;

    this.workerThread = new Thread(() -> {
      while (isRunning || !operationsQueue.isEmpty()) {
        try {
          CacheOperation op = operationsQueue.take();
          if (cacheRepository != null) {
            try {
              if (op.type == OpType.SAVE) {
                cacheRepository.save(op.entity);
              } else if (op.type == OpType.DELETE) {
                if (cacheRepository.existsById(op.key)) {
                  cacheRepository.deleteById(op.key);
                }
              }
            } catch (Exception e) {
              // Log and handle DB failure gracefully to prevent daemon death
              e.printStackTrace();
            }
          }
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    });
    this.workerThread.setDaemon(true);
    this.workerThread.start();
  }

  @Override
  public void save(CacheEntity entity) {
    operationsQueue.offer(CacheOperation.saveOp(entity));
  }

  @Override
  public void delete(String key) {
    operationsQueue.offer(CacheOperation.deleteOp(key));
  }

  @Override
  public void shutdown() {
    this.isRunning = false;
    this.workerThread.interrupt();
  }
}
