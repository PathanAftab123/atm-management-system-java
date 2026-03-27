package Model;

public class Account {

    private int id;
    private String name;
    private double balance;
    private int pin;

    public Account() {}

    public Account(int id, String name, double balance, int pin) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.pin = pin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }
}