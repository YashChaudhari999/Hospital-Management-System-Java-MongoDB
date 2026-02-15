package com.yash;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;

/**
 *
 * @author Yash
 */
public class DiagnosisInformationTabelSearchServer {

    public static void main(String[] args) {
        alwaysConnected();
    }

    public static void alwaysConnected() {
        try (ServerSocket serverSocket = new ServerSocket(2011)) {
            String patientIDTextField_Data;
            System.out.println("Server is waiting for connections...");
            System.out.println("Waiting for Clients....");
            Socket clientSocket = serverSocket.accept();
            System.err.println("Connection Estabilished");
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            System.out.println("hi");
            patientIDTextField_Data = in.readLine();
            System.out.println(patientIDTextField_Data);

            MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
            MongoDatabase database = mongoClient.getDatabase("Hospital");
            MongoCollection<Document> collection = database.getCollection("PatientData");
            Document filter = new Document("PatientID", patientIDTextField_Data);

            FindIterable<Document> documents;
            if (!patientIDTextField_Data.equals("")) {
                documents = collection.find(filter).sort(Sorts.descending("_id"));
            } else {
                documents = collection.find().sort(Sorts.descending("_id"));
            }
            System.out.println(filter + "" + "" + documents);
            for (Document document : documents) {
                StringBuilder row = new StringBuilder();
                row.append(document.get("PatientID")).append("~");
                row.append(document.get("Date")).append("~");
                row.append(document.get("Time")).append("~");
                row.append(document.get("PatientName")).append("~");
                row.append(document.get("PatientContact")).append("~");
                row.append(document.get("PatientAge")).append("~");
                row.append(document.get("PatientGender")).append("~");
                row.append(document.get("PatientBloodGroup")).append("~");
                row.append(document.get("PatientAddress"));
                System.out.println(row);
                // Send the row to the client
                out.println(row.toString());
            }
            out.println("null");
        } catch (IOException ex) {
            Logger.getLogger(DiagnosisInformationTabelSearchServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        alwaysConnected();
    }
}