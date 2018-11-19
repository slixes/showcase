package io.slixes.warehouse;

public interface Warehouse {

  <T extends Resource> void create(T data);

  <T extends Resource> void update(T data);

  <T extends Resource> boolean delete(T data);

  <T extends Resource> boolean purge(T data);

  <T extends Resource> void get(T data);
}
