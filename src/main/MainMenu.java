package main;

import db.DBConnection;
import service.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class MainMenu {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        int loggedInUserId = -1;

        while (true) {

            System.out.println("\n====== ATM System ======");

            System.out.println("1 Login");
            System.out.println("2 Check Balance");
            System.out.println("3 Deposit");
            System.out.println("4 Withdraw");
            System.out.println("5 Mini Statement");
            System.out.println("6 Change PIN");
            System.out.println("7 Fast Cash");
            System.out.println("8 Transfer Money");
            System.out.println("9 Logout");
            System.out.println("10 Exit");

            System.out.print("Enter choice: ");
            int choice = sc.nextInt();

            try (Connection con = DBConnection.getConnection()) {

                // ================= LOGIN =================
                if (choice == 1) {

                    if (loggedInUserId != -1) {

                        System.out.println("⚠ Already Logged In");
                        continue;
                    }

                    System.out.print("Enter Account ID: ");
                    int id = sc.nextInt();

                    System.out.print("Enter PIN: ");
                    int pin = sc.nextInt();

                    if (PinService.login(con, id, pin)) {

                        loggedInUserId = id;

                        System.out.println("Login Successful ");

                    } else {

                        System.out.println("Login Failed ");
                    }
                }
                // ================= CHECK LOGIN =================
                else if (choice >= 2 && choice <= 6) {

                    if (loggedInUserId == -1) {

                        System.out.println(" Please login first");
                        continue;
                    }

                    int id = loggedInUserId;

                    // CHECK BALANCE
                    if (choice == 2) {

                        BalanceService.checkBalance(con, id);
                    }

                    // DEPOSIT
                    else if (choice == 3) {

                        System.out.print("Enter Amount: ");
                        double amount = sc.nextDouble();

                        DepositService.deposit(con, id, amount);
                    }

                    // WITHDRAW
                    else if (choice == 4) {

                        System.out.print("Enter Amount: ");
                        double amount = sc.nextDouble();

                        WithdrawService.withdraw(con, id, amount);
                    }

                    // MINI STATEMENT
                    else if (choice == 5) {

                        MiniStatementService.showMiniStatement(con, id);
                    }

                    // CHANGE PIN
                    else if (choice == 6) {

                        System.out.print("Enter New PIN: ");
                        int newPin = sc.nextInt();

                        PinService.changePin(con, id, newPin);
                    }
                }

                // FAST CASH
                else if (choice == 7) {

                    if (loggedInUserId == -1) {

                        System.out.println(" Please login first");
                        continue;
                    }

                    int id = loggedInUserId;

                    System.out.println("\n--- Fast Cash ---");
                    System.out.println("1 ₹500");
                    System.out.println("2 ₹1000");
                    System.out.println("3 ₹2000");
                    System.out.println("4 ₹5000");

                    System.out.print("Choose amount: ");
                    int opt = sc.nextInt();

                    double amount = 0;

                    if (opt == 1) amount = 500;
                    else if (opt == 2) amount = 1000;
                    else if (opt == 3) amount = 2000;
                    else if (opt == 4) amount = 5000;
                    else {
                        System.out.println("Invalid Option ");
                        continue;
                    }

                    WithdrawService.withdraw(con, id, amount);
                }

                // TRANSFER MONEY
                else if (choice == 8) {

                    if (loggedInUserId == -1) {

                        System.out.println(" Please login first");
                        continue;
                    }

                    int fromId = loggedInUserId;

                    System.out.print("Enter Receiver ID: ");
                    int toId = sc.nextInt();

                    System.out.print("Enter Amount: ");
                    double amount = sc.nextDouble();

                    TransferService.transfer(con,
                            fromId,
                            toId,
                            amount);
                }


                // ================= LOGOUT =================
                else if (choice == 9) {

                    loggedInUserId = -1;

                    System.out.println("Logged Out Successfully ");
                }

                // ================= EXIT =================
                else if (choice == 10) {

                    System.out.println("Thank You for using ATM ");
                    break;
                }

                else {

                    System.out.println("Invalid Choice ");
                }

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }
}