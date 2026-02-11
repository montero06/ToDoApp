package com.example.tarealarga1trimestre.Data;

public class TaskStats {
    private final int total;
    private final int prioritarias;
    private final int completadas;
    private final int enProgreso;
    private final int noIniciadas;
    private final double progresoPromedio;
    private final double diasPromedioObjetivo;

    public TaskStats(int total, int prioritarias, int completadas, int enProgreso, int noIniciadas,
                     double progresoPromedio, double diasPromedioObjetivo) {
        this.total = total;
        this.prioritarias = prioritarias;
        this.completadas = completadas;
        this.enProgreso = enProgreso;
        this.noIniciadas = noIniciadas;
        this.progresoPromedio = progresoPromedio;
        this.diasPromedioObjetivo = diasPromedioObjetivo;
    }

    public int getTotal() { return total; }
    public int getPrioritarias() { return prioritarias; }
    public int getCompletadas() { return completadas; }
    public int getEnProgreso() { return enProgreso; }
    public int getNoIniciadas() { return noIniciadas; }
    public double getProgresoPromedio() { return progresoPromedio; }
    public double getDiasPromedioObjetivo() { return diasPromedioObjetivo; }
}
