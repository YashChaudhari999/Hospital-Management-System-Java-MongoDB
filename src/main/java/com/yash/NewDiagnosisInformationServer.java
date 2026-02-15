// package PatientDetails.src.patientdetails;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

/**
 *
 * @author Yash
 */
public class NewDiagnosisInformationServer {

    public static void main(String[] args) {
        alwaysConnected();
    }

    public static void alwaysConnected() {
        try (ServerSocket serverSocket = new ServerSocket(1939)) {
            String patientIDTextField_Data, symptomsTextField_Data, diagnosisTextField_Data, medicinesTextField_Data,
                    wardRequiredButtonGroup_Data;
            System.out.println("Server is waiting for connections...");
            System.out.println("Waiting for Clients....");
            Socket clientSocket = serverSocket.accept();
            System.err.println("Connection Estabilished");
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            patientIDTextField_Data = in.readLine();
            symptomsTextField_Data = in.readLine();
            diagnosisTextField_Data = in.readLine();
            medicinesTextField_Data = in.readLine();
            wardRequiredButtonGroup_Data = in.readLine();
            System.out.println(
                    patientIDTextField_Data + symptomsTextField_Data + diagnosisTextField_Data + medicinesTextField_Data
                            + wardRequiredButtonGroup_Data);

            MongoClient mongo1 = MongoClients.create("mongodb://localhost:27017");
            MongoDatabase db1 = mongo1.getDatabase("Hospital");
            MongoCollection collection = db1.getCollection("PatientData");

            Document filter = new Document("PatientID", patientIDTextField_Data);

            Document updatedDocument = new Document("PatientSymptoms", symptomsTextField_Data)
                    .append("PatientDiagnosis", diagnosisTextField_Data)
                    .append("PatientMedicines", medicinesTextField_Data)
                    .append("PatientWardRequired", wardRequiredButtonGroup_Data);

            Document updateOperation = new Document("$set", updatedDocument);

            collection.updateOne(filter, updateOperation);
        } catch (IOException ex) {
            Logger.getLogger(NewDiagnosisInformationServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        alwaysConnected();
    }
}