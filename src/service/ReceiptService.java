package service;

import java.io.FileWriter;
import java.time.LocalDateTime;

public class ReceiptService {

    public static void generateReceipt(int id,
                                       String type,
                                       double amount,
                                       double balance) {

        try (FileWriter fw =
                     new FileWriter("transaction_receipt.txt", true)) {

            fw.write("\n====== ATM RECEIPT ======\n");

            fw.write("Account ID: " + id + "\n");

            fw.write("Transaction: " + type + "\n");

            fw.write("Amount: ₹" + amount + "\n");

            fw.write("Remaining Balance: ₹" + balance + "\n");

            fw.write("Date: "
                    + LocalDateTime.now()
                    + "\n");

            fw.write("==========================\n");

            System.out.println(
                    "Receipt Generated");

        }

        catch (Exception e) {

            System.out.println(
                    "Receipt Generation Failed");

            e.printStackTrace();
        }
    }
}