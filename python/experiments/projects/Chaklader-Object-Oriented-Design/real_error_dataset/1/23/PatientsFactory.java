//package design.practice.designEx.newhospital;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class PatientsFactory {
//
//    public static List<Patient> getPatients(String listOfPatient) {
//    	List<Patient> patientsList = new ArrayList<Patient>();
//
//        String[] healthStatusAcronymArray = factorySpliter(listOfPatient, ",");
//
//        for (int i = 0; i < healthStatusAcronymArray.length; i++) {
//            HealthStatus healthStatus = HealthStatus.getHealthStatusByAcronym(healthStatusAcronymArray[i]);
//            Patient patient = new Patient(healthStatus);
//            patientsList.add(patient);
//        }
//        return patientsList;
//    }
//
//    private static String[] factorySpliter(String string, String separator) {
//        return string.split(separator);
//    }
//
//}
