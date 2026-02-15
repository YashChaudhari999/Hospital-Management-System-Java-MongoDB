
// package PatientDetails.src.patientdetails;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 *
 * @author Yash
 */
public class NewPatientServer {

    public static void main(String[] args) {
        alwaysConnected();
    }

    public static void alwaysConnected() {
        try (ServerSocket serverSocket = new ServerSocket(2001)) {
            String nameTextField_Data, contactNumberTextField_Data, ageTextField_Data, genderButtonGroup_Data,
                    bloodGroupList_Data,
                    addressTextArea_Data, majorDiseaseTextArea_Data;
            System.out.println("Server is waiting for connections...");
            System.out.println("Waiting for Clients....");
            Socket clientSocket = serverSocket.accept();
            System.err.println("Connection Estabilished");
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            nameTextField_Data = in.readLine();
            contactNumberTextField_Data = in.readLine();
            ageTextField_Data = in.readLine();
            genderButtonGroup_Data = in.readLine();
            bloodGroupList_Data = in.readLine();
            addressTextArea_Data = in.readLine();
            majorDiseaseTextArea_Data = in.readLine();

            // Get the current local date and time
            LocalDateTime localDateTime = LocalDateTime.now();
            // Define a date-time formatter to format the output
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss");
            DateTimeFormatter date = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm:ss");
            // Format and display the local date and time
            String formatdDate = localDateTime.format(date);
            String formatdTime = localDateTime.format(time);
            String patientID_Data = localDateTime.format(formatter);

            System.out.println("Local Date and Time: " + patientID_Data);
            System.out.println("Local Date and Time: " + formatdDate + "Time" + formatdTime);
            out.println(patientID_Data);

            System.out.println(
                    nameTextField_Data + contactNumberTextField_Data + ageTextField_Data + genderButtonGroup_Data
                            + bloodGroupList_Data +
                            addressTextArea_Data + majorDiseaseTextArea_Data);

            MongoClient mongo1 = MongoClients.create("mongodb://localhost:27017");
            MongoDatabase db1 = mongo1.getDatabase("Hospital");
            MongoCollection collection = db1.getCollection("PatientData");
            Document d1 = new Document().append("PatientID", patientID_Data)
                    .append("Date", formatdDate)
                    .append("Time", formatdTime)
                    .append("PatientName", nameTextField_Data)
                    .append("PatientContact", contactNumberTextField_Data)
                    .append("PatientContact", contactNumberTextField_Data)
                    .append("PatientAge", ageTextField_Data)
                    .append("PatientGender", genderButtonGroup_Data)
                    .append("PatientBloodGroup", bloodGroupList_Data)
                    .append("PatientAddress", addressTextArea_Data)
                    .append("PatientMajorDisease", majorDiseaseTextArea_Data)
                    .append("PatientSymptoms", "")
                    .append("PatientDiagnosis", "")
                    .append("PatientMedicines", "")
                    .append("PatientWardRequired", "");
            collection.insertOne(d1);
        } catch (IOException ex) {
            Logger.getLogger(NewPatientServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        alwaysConnected();
    }
}