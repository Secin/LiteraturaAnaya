package com.aluracursos.LiteraturaAnaya.Service;

public interface IConvierteDatos {
    <T> T obtenerDatos(String json, Class<T> clase);
}
