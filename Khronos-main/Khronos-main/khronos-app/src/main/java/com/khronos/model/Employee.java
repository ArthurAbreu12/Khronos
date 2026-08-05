package com.khronos.model;

public class Employee {

    private int id;
    private String nome;
    private String cargo;
    private double salario;
    private boolean ativo;

    public Employee() {
    }

    public Employee(int id, String nome, String cargo, double salario, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.cargo = cargo;
        this.salario = salario;
        this.ativo = ativo;
    }

    public Employee(String nome, String cargo, double salario) {
        this.nome = nome;
        this.cargo = cargo;
        this.salario = salario;
        this.ativo = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return nome;
    }
}
