package com.yash;

import java.io.*;
import java.net.*;
import com.mongodb.client.*;
import org.bson.Document;

public class HospitalServer {

    private static final int PORT = 5000;
    private static MongoDatabase database;

    public static void main(String[] args) throws Exception {

        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");

        database = mongoClient.getDatabase("Hospital");

        ServerSocket serverSocket = new ServerSocket(PORT);

        System.out.println("Hospital Server running on port " + PORT);

        while (true) {
            Socket client = serverSocket.accept();
            new Thread(new ClientHandler(client)).start();
        }
    }

    static class ClientHandler implements Runnable {

        private Socket socket;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try (
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(
                            socket.getOutputStream(), true)) {

                String action = in.readLine();

                switch (action) {

                    case "NEW_PATIENT":
                        handleNewPatient(in, out);
                        break;

                    case "NEW_DIAGNOSIS":
                        handleNewDiagnosis(in, out);
                        break;

                    case "GET_HISTORY":
                        handleHistory(out);
                        break;

                    case "SEARCH_PATIENT":
                        handleSearch(in, out);
                        break;

                    case "UPDATE_PATIENT":
                        handleUpdate(in, out);
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void handleNewPatient(BufferedReader in, PrintWriter out) throws Exception {

            MongoCollection<Document> collection = database.getCollection("PatientData");
            // Read data sent from client
            String nameTextField_Data = in.readLine();
            String contactNumberTextField_Data = in.readLine();
            String ageTextField_Data = in.readLine();
            String genderButtonGroup_Data = in.readLine();
            String bloodGroupList_Data = in.readLine();
            String addressTextArea_Data = in.readLine();
            String majorDiseaseTextArea_Data = in.readLine();

            // Generate Date & Time
            java.time.LocalDateTime localDateTime = java.time.LocalDateTime.now();

            java.time.format.DateTimeFormatter idFormatter = java.time.format.DateTimeFormatter
                    .ofPattern("ddMMyyyyHHmmss");

            java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter
                    .ofPattern("dd-MM-yyyy");

            java.time.format.DateTimeFormatter timeFormatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss");

            String patientID_Data = localDateTime.format(idFormatter);
            String formatdDate = localDateTime.format(dateFormatter);
            String formatdTime = localDateTime.format(timeFormatter);

            // Send Patient ID back to client
            out.println(patientID_Data);

            // Create MongoDB document
            Document doc = new Document()
                    .append("PatientID", patientID_Data)
                    .append("Date", formatdDate)
                    .append("Time", formatdTime)
                    .append("PatientName", nameTextField_Data)
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

            collection.insertOne(doc);

            out.println("SUCCESS");
        }

        private void handleNewDiagnosis(BufferedReader in, PrintWriter out) throws Exception {

            MongoCollection<Document> collection = database.getCollection("PatientData");

            // Read values sent from client
            String patientIDTextField_Data = in.readLine();
            String symptomsTextField_Data = in.readLine();
            String diagnosisTextField_Data = in.readLine();
            String medicinesTextField_Data = in.readLine();
            String wardRequiredButtonGroup_Data = in.readLine();

            // Update MongoDB
            collection.updateOne(
                    com.mongodb.client.model.Filters.eq("PatientID", patientIDTextField_Data),
                    com.mongodb.client.model.Updates.combine(
                            com.mongodb.client.model.Updates.set("PatientSymptoms", symptomsTextField_Data),
                            com.mongodb.client.model.Updates.set("PatientDiagnosis", diagnosisTextField_Data),
                            com.mongodb.client.model.Updates.set("PatientMedicines", medicinesTextField_Data),
                            com.mongodb.client.model.Updates.set("PatientWardRequired", wardRequiredButtonGroup_Data)));

            System.out.println("Diagnosis updated for PatientID: " + patientIDTextField_Data);

            out.println("SUCCESS");
        }

        private void handleHistory(PrintWriter out) {

            MongoCollection<Document> collection = database.getCollection("PatientData");

            com.mongodb.client.FindIterable<Document> documents = collection.find()
                    .sort(com.mongodb.client.model.Sorts.descending("_id"));

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
                row.append(document.get("PatientAddress")).append("~");
                row.append(document.get("PatientMajorDisease")).append("~");
                row.append(document.get("PatientSymptoms")).append("~");
                row.append(document.get("PatientDiagnosis")).append("~");
                row.append(document.get("PatientMedicines")).append("~");
                row.append(document.get("PatientWardRequired"));

                out.println(row.toString());
            }

            out.println("null");

            System.out.println("Full history sent.");
        }

        private void handleSearch(BufferedReader in, PrintWriter out) throws Exception {

            MongoCollection<Document> collection = database.getCollection("PatientData");

            String patientIDTextField_Data = in.readLine();

            com.mongodb.client.FindIterable<Document> documents;

            if (patientIDTextField_Data != null && !patientIDTextField_Data.equals("")) {
                documents = collection
                        .find(com.mongodb.client.model.Filters.eq("PatientID", patientIDTextField_Data))
                        .sort(com.mongodb.client.model.Sorts.descending("_id"));
            } else {
                documents = collection
                        .find()
                        .sort(com.mongodb.client.model.Sorts.descending("_id"));
            }

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

                out.println(row.toString());
            }

            out.println("null"); // Keep same protocol as old server

            System.out.println("Search request handled.");
        }

        private void handleUpdate(BufferedReader in, PrintWriter out) throws Exception {

            MongoCollection<Document> collection = database.getCollection("PatientData");

            String actionType = in.readLine();

            if ("FETCH".equals(actionType)) {

                String patientId = in.readLine();

                Document doc = collection.find(
                        com.mongodb.client.model.Filters.eq("PatientID", patientId)).first();

                if (doc != null) {

                    out.println(doc.getString("PatientName"));
                    out.println(doc.getString("PatientContact"));
                    out.println(doc.getString("PatientAge"));
                    out.println(doc.getString("PatientGender"));
                    out.println(doc.getString("PatientBloodGroup"));
                    out.println(doc.getString("PatientAddress"));
                    out.println(doc.getString("PatientMajorDisease"));

                } else {
                    out.println("NOT_FOUND");
                }

            } else if ("UPDATE".equals(actionType)) {

                String updatePatientIDTextField_Data = in.readLine();
                String updateNameTextField_Data = in.readLine();
                String updateContactNumberTextField_Data = in.readLine();
                String updateAgeTextField_Data = in.readLine();
                String updateGenderButtonGroup_Data = in.readLine();
                String updateBloodGroupList_Data = in.readLine();
                String updateAddressTextArea_Data = in.readLine();
                String updateMajorDiseaseTextArea_Data = in.readLine();

                collection.updateOne(
                        com.mongodb.client.model.Filters.eq("PatientID", updatePatientIDTextField_Data),
                        com.mongodb.client.model.Updates.combine(
                                com.mongodb.client.model.Updates.set("PatientName", updateNameTextField_Data),
                                com.mongodb.client.model.Updates.set("PatientContact",
                                        updateContactNumberTextField_Data),
                                com.mongodb.client.model.Updates.set("PatientAge", updateAgeTextField_Data),
                                com.mongodb.client.model.Updates.set("PatientGender", updateGenderButtonGroup_Data),
                                com.mongodb.client.model.Updates.set("PatientBloodGroup", updateBloodGroupList_Data),
                                com.mongodb.client.model.Updates.set("PatientAddress", updateAddressTextArea_Data),
                                com.mongodb.client.model.Updates.set("PatientMajorDisease",
                                        updateMajorDiseaseTextArea_Data)));

                out.println("UPDATED");
            }
        }

    }
}
