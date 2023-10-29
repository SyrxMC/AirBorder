package br.dev.brunoxkk0.airborder.api;

@FunctionalInterface
public interface BorderProvider {

    boolean isOutsideBorder(int chunkX, int chunkZ, int dimension);
}
