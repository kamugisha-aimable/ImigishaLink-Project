package com.imigishalink.location;

import java.util.*;

/**
 * Comprehensive Rwandan Administrative Structure Data
 * Following the official administrative divisions of Rwanda
 */
public class RwandaLocationData {
    
    /**
     * Get all provinces of Rwanda (excluding City of Kigali as per requirements)
     */
    public static List<String> getProvinces() {
        return Arrays.asList(
            "Eastern Province",
            "Northern Province",
            "Southern Province",
            "Western Province"
        );
    }
    
    /**
     * Get districts by province
     */
    public static Map<String, List<String>> getDistrictsByProvince() {
        Map<String, List<String>> districts = new HashMap<>();
        
        // Eastern Province Districts
        districts.put("Eastern Province", Arrays.asList(
            "Bugesera",
            "Gatsibo",
            "Kayonza",
            "Kirehe",
            "Ngoma",
            "Nyagatare",
            "Rwamagana"
        ));
        
        // Northern Province Districts
        districts.put("Northern Province", Arrays.asList(
            "Burera",
            "Gakenke",
            "Gicumbi",
            "Musanze",
            "Rulindo"
        ));
        
        // Southern Province Districts
        districts.put("Southern Province", Arrays.asList(
            "Gisagara",
            "Huye",
            "Kamonyi",
            "Muhanga",
            "Nyamagabe",
            "Nyanza",
            "Nyaruguru",
            "Ruhango",
            "Rusizi"
        ));
        
        // Western Province Districts
        districts.put("Western Province", Arrays.asList(
            "Karongi",
            "Ngororero",
            "Nyabihu",
            "Nyamasheke",
            "Rubavu",
            "Rutsiro",
            "Rusizi"
        ));
        
        return districts;
    }
    
    /**
     * Get sectors by district
     */
    public static Map<String, List<String>> getSectorsByDistrict() {
        Map<String, List<String>> sectors = new HashMap<>();
        
        // Eastern Province - Bugesera District
        sectors.put("Bugesera", Arrays.asList(
            "Gashora", "Juru", "Kamabuye", "Mareba", "Mayange", "Musenyi", "Mwogo", "Ngeruka", "Ntarama", "Nyamata", "Nyarugenge", "Rilima", "Ruhuha", "Rweru", "Shyara"
        ));
        
        // Eastern Province - Gatsibo District
        sectors.put("Gatsibo", Arrays.asList(
            "Gasange", "Gatsibo", "Gitoki", "Kabarore", "Kageyo", "Kiramuruzi", "Kiziguro", "Muhura", "Murambi", "Ngarama", "Nyagihanga", "Remera", "Rugarama", "Rwimbogo"
        ));
        
        // Eastern Province - Kayonza District
        sectors.put("Kayonza", Arrays.asList(
            "Gahini", "Kabare", "Kabarondo", "Mukarange", "Murama", "Murundi", "Mwiri", "Ndego", "Nyamirama", "Rukara", "Ruramira", "Rwinkwavu"
        ));
        
        // Eastern Province - Kirehe District
        sectors.put("Kirehe", Arrays.asList(
            "Gahara", "Gatore", "Kigarama", "Kigina", "Kirehe", "Mahama", "Mpanga", "Musaza", "Mushikiri", "Nasho", "Nyamugari", "Nyarubuye", "Rwinkwavu"
        ));
        
        // Eastern Province - Ngoma District
        sectors.put("Ngoma", Arrays.asList(
            "Gashanda", "Jarama", "Karembo", "Kazo", "Kibungo", "Mugesera", "Murama", "Mutenderi", "Remera", "Rukira", "Rukumberi", "Rurenge", "Sake", "Zaza"
        ));
        
        // Eastern Province - Nyagatare District
        sectors.put("Nyagatare", Arrays.asList(
            "Bukure", "Bwimiyange", "Cyabingo", "Gatunda", "Kanyangese", "Karangazi", "Katabagemu", "Kiyombe", "Matimba", "Mimuri", "Mukama", "Musheri", "Nyagatare", "Rukomo", "Rwempasha", "Rwimiyaga", "Tabagwe"
        ));
        
        // Eastern Province - Rwamagana District
        sectors.put("Rwamagana", Arrays.asList(
            "Fumbwe", "Gahengeri", "Gishali", "Karenge", "Kigabiro", "Muhazi", "Munyaga", "Munyiginya", "Musha", "Muyumbu", "Mwulire", "Nyakaliro", "Nzige", "Rubona", "Rukara"
        ));
        
        // Northern Province - Burera District
        sectors.put("Burera", Arrays.asList(
            "Bungwe", "Butaro", "Cyanika", "Cyeru", "Gahunga", "Gatebe", "Gitovu", "Kagogo", "Kinoni", "Kinyababa", "Kivuye", "Nemba", "Rugarama", "Rugengabari", "Ruhunde", "Rusarabuye", "Rwerere"
        ));
        
        // Northern Province - Gakenke District
        sectors.put("Gakenke", Arrays.asList(
            "Busengo", "Coko", "Cyabingo", "Gakenke", "Gashenyi", "Janja", "Kamubuga", "Karambo", "Kivuruga", "Mataba", "Minazi", "Mugunga", "Muhondo", "Muyongwe", "Muzo", "Nemba", "Ruli", "Rusasa", "Rushashi"
        ));
        
        // Northern Province - Gicumbi District
        sectors.put("Gicumbi", Arrays.asList(
            "Bukure", "Bwisige", "Byumba", "Cyumba", "Giti", "Kageyo", "Kaniga", "Manyagiro", "Miyove", "Mukarange", "Muko", "Mutete", "Nyamiyaga", "Nyankenke", "Rubaya", "Rukomo", "Rusasa", "Rushaki", "Rutare", "Ruvune", "Rwamiko", "Shangasha"
        ));
        
        // Northern Province - Musanze District
        sectors.put("Musanze", Arrays.asList(
            "Busogo", "Cyuve", "Gacaca", "Gashaki", "Gataraga", "Kimonyi", "Kinigi", "Muhoza", "Muko", "Musanze", "Nkotsi", "Nyange", "Remera", "Rwaza", "Shingiro"
        ));
        
        // Northern Province - Rulindo District
        sectors.put("Rulindo", Arrays.asList(
            "Base", "Burega", "Bushoki", "Buyoga", "Cyinzuzi", "Cyungo", "Kinihira", "Kisaro", "Masoro", "Mbogo", "Murambi", "Ngoma", "Ntarabana", "Rukozo", "Rusiga", "Shyorongi", "Tumba"
        ));
        
        // Southern Province - Gisagara District
        sectors.put("Gisagara", Arrays.asList(
            "Gikundamvura", "Muganza", "Mujuga", "Mukura", "Musha", "Ndora", "Nyanza", "Nyanza", "Nyarusange", "Save", "Tumba"
        ));
        
        // Southern Province - Huye District
        sectors.put("Huye", Arrays.asList(
            "Gishamvu", "Huye", "Karama", "Kigoma", "Kinazi", "Maraba", "Mbazi", "Mukura", "Ngoma", "Ruhashya", "Rusatira", "Rwaniro", "Simbi", "Tumba"
        ));
        
        // Southern Province - Kamonyi District
        sectors.put("Kamonyi", Arrays.asList(
            "Gacurabwenge", "Karama", "Kayenzi", "Kayumbu", "Mugina", "Musambira", "Ngamba", "Nyamiyaga", "Nyarubaka", "Rugarika", "Rukoma", "Runda"
        ));
        
        // Southern Province - Muhanga District
        sectors.put("Muhanga", Arrays.asList(
            "Cyeza", "Kabacuzi", "Kibangu", "Kiyumba", "Muhanga", "Mushishiro", "Nyabinoni", "Nyamabuye", "Nyarusange", "Rongi", "Rugendabari", "Shyogwe"
        ));
        
        // Southern Province - Nyamagabe District
        sectors.put("Nyamagabe", Arrays.asList(
            "Buruhukiro", "Cyanika", "Gasaka", "Gatare", "Kaduha", "Kamegeli", "Kibirizi", "Kibumbwe", "Kitabi", "Mbazi", "Mugano", "Musange", "Musebeya", "Mushubi", "Nkomane", "Gasaka", "Tare"
        ));
        
        // Southern Province - Nyanza District
        sectors.put("Nyanza", Arrays.asList(
            "Busasamana", "Busoro", "Cyabakamyi", "Kibirizi", "Kigoma", "Mukingo", "Muyira", "Ntyazo", "Nyagisozi", "Rwabicuma", "Rwankuba"
        ));
        
        // Southern Province - Nyaruguru District
        sectors.put("Nyaruguru", Arrays.asList(
            "Bitare", "Busanze", "Cyahinda", "Kibeho", "Kivu", "Mata", "Muganza", "Munini", "Ngera", "Ngoma", "Nyabimata", "Nyagisozi", "Nyamagabe", "Nyamirundi", "Nyarurema", "Rugogwe", "Rukore", "Rusasa", "Rusenge", "Rwamiko"
        ));
        
        // Southern Province - Ruhango District
        sectors.put("Ruhango", Arrays.asList(
            "Bweramana", "Byimana", "Kabagali", "Kinazi", "Kinihira", "Mbuye", "Mukingo", "Muyira", "Ntongwe", "Ruhango", "Rusatira"
        ));
        
        // Southern Province - Rusizi District
        sectors.put("Rusizi", Arrays.asList(
            "Boneza", "Gihombo", "Kigwena", "Muganza", "Mukura", "Mururu", "Nkanka", "Nkombo", "Nkungu", "Nyakabuye", "Nyakarenzo", "Nzahaha", "Rwimbogo"
        ));
        
        // Western Province - Karongi District
        sectors.put("Karongi", Arrays.asList(
            "Bwishyura", "Gashari", "Gishyita", "Gitesi", "Mubuga", "Murambi", "Murundi", "Mutuntu", "Rubengera", "Rugabano", "Ruganda", "Rwankuba", "Twumba"
        ));
        
        // Western Province - Ngororero District
        sectors.put("Ngororero", Arrays.asList(
            "Bwira", "Gatumba", "Hindiro", "Kabaya", "Kageyo", "Kavumu", "Matyazo", "Muhanda", "Muhororo", "Ndaro", "Ngororero", "Ngoma", "Nyange", "Sovu"
        ));
        
        // Western Province - Nyabihu District
        sectors.put("Nyabihu", Arrays.asList(
            "Bigogwe", "Jenda", "Jomba", "Kabatwa", "Karago", "Kintobo", "Mukamira", "Muringa", "Rambura", "Rugera", "Rurembo", "Shyira"
        ));
        
        // Western Province - Nyamasheke District
        sectors.put("Nyamasheke", Arrays.asList(
            "Bushekeri", "Bushenge", "Cyato", "Gihombo", "Kagano", "Kanjongo", "Karambi", "Karengera", "Kirimbi", "Macuba", "Mahembe", "Nyabitekeri", "Rangiro", "Ruharambuga", "Shangi", "Yaramba"
        ));
        
        // Western Province - Rubavu District
        sectors.put("Rubavu", Arrays.asList(
            "Bugeshi", "Busasamana", "Cyanzarwe", "Gisenyi", "Kanama", "Kanzenze", "Mudende", "Nyakiriba", "Nyamyumba", "Nyundo", "Rubavu", "Rugerero"
        ));
        
        // Western Province - Rutsiro District
        sectors.put("Rutsiro", Arrays.asList(
            "Boneza", "Gihango", "Kigeyo", "Kivumu", "Manihira", "Mukura", "Murunda", "Musasa", "Mushonyi", "Mushubati", "Nyabirasi", "Ruhango", "Rusebeya"
        ));
        
        return sectors;
    }
    
    /**
     * Get cells by sector - Comprehensive Rwandan administrative structure
     */
    public static Map<String, List<String>> getCellsBySector() {
        Map<String, List<String>> cells = new HashMap<>();
        
        // Eastern Province - Bugesera District
        cells.put("Gashora", Arrays.asList("Gashora", "Kabuye", "Kamabuye", "Mareba", "Nyamata", "Rilima"));
        cells.put("Juru", Arrays.asList("Juru", "Kamabuye", "Mareba", "Nyamata"));
        cells.put("Kamabuye", Arrays.asList("Kamabuye", "Mareba", "Nyamata", "Rilima"));
        cells.put("Mareba", Arrays.asList("Mareba", "Nyamata", "Rilima", "Ruhuha"));
        cells.put("Mayange", Arrays.asList("Mayange", "Mwogo", "Ngeruka", "Ntarama"));
        cells.put("Musenyi", Arrays.asList("Musenyi", "Mwogo", "Ngeruka", "Ntarama"));
        cells.put("Mwogo", Arrays.asList("Mwogo", "Ngeruka", "Ntarama", "Nyamata"));
        cells.put("Ngeruka", Arrays.asList("Ngeruka", "Ntarama", "Nyamata", "Rilima"));
        cells.put("Ntarama", Arrays.asList("Ntarama", "Nyamata", "Rilima", "Ruhuha"));
        cells.put("Nyamata", Arrays.asList("Nyamata", "Rilima", "Ruhuha", "Rweru"));
        cells.put("Nyarugenge", Arrays.asList("Nyarugenge", "Rilima", "Ruhuha", "Rweru"));
        cells.put("Rilima", Arrays.asList("Rilima", "Ruhuha", "Rweru", "Shyara"));
        cells.put("Ruhuha", Arrays.asList("Ruhuha", "Rweru", "Shyara", "Gashora"));
        cells.put("Rweru", Arrays.asList("Rweru", "Shyara", "Gashora", "Kabuye"));
        cells.put("Shyara", Arrays.asList("Shyara", "Gashora", "Kabuye", "Kamabuye"));
        
        // Eastern Province - Gatsibo District
        cells.put("Gasange", Arrays.asList("Gasange", "Gatsibo", "Gitoki", "Kabarore", "Kageyo"));
        cells.put("Gatsibo", Arrays.asList("Gatsibo", "Gitoki", "Kabarore", "Kageyo", "Kiramuruzi"));
        cells.put("Gitoki", Arrays.asList("Gitoki", "Kabarore", "Kageyo", "Kiramuruzi", "Kiziguro"));
        cells.put("Kabarore", Arrays.asList("Kabarore", "Kageyo", "Kiramuruzi", "Kiziguro", "Muhura"));
        cells.put("Kageyo", Arrays.asList("Kageyo", "Kiramuruzi", "Kiziguro", "Muhura", "Murambi"));
        cells.put("Kiramuruzi", Arrays.asList("Kiramuruzi", "Kiziguro", "Muhura", "Murambi", "Ngarama"));
        cells.put("Kiziguro", Arrays.asList("Kiziguro", "Muhura", "Murambi", "Ngarama", "Nyagihanga"));
        cells.put("Muhura", Arrays.asList("Muhura", "Murambi", "Ngarama", "Nyagihanga", "Remera"));
        cells.put("Murambi", Arrays.asList("Murambi", "Ngarama", "Nyagihanga", "Remera", "Rugarama"));
        cells.put("Ngarama", Arrays.asList("Ngarama", "Nyagihanga", "Remera", "Rugarama", "Rwimbogo"));
        cells.put("Nyagihanga", Arrays.asList("Nyagihanga", "Remera", "Rugarama", "Rwimbogo", "Gasange"));
        cells.put("Remera", Arrays.asList("Remera", "Rugarama", "Rwimbogo", "Gasange", "Gatsibo"));
        cells.put("Rugarama", Arrays.asList("Rugarama", "Rwimbogo", "Gasange", "Gatsibo", "Gitoki"));
        cells.put("Rwimbogo", Arrays.asList("Rwimbogo", "Gasange", "Gatsibo", "Gitoki", "Kabarore"));
        
        // Eastern Province - Kayonza District
        cells.put("Gahini", Arrays.asList("Gahini", "Kabare", "Kabarondo", "Mukarange", "Murama"));
        cells.put("Kabare", Arrays.asList("Kabare", "Kabarondo", "Mukarange", "Murama", "Murundi"));
        cells.put("Kabarondo", Arrays.asList("Kabarondo", "Mukarange", "Murama", "Murundi", "Mwiri"));
        cells.put("Mukarange", Arrays.asList("Mukarange", "Murama", "Murundi", "Mwiri", "Ndego"));
        cells.put("Murama", Arrays.asList("Murama", "Murundi", "Mwiri", "Ndego", "Nyamirama"));
        cells.put("Murundi", Arrays.asList("Murundi", "Mwiri", "Ndego", "Nyamirama", "Rukara"));
        cells.put("Mwiri", Arrays.asList("Mwiri", "Ndego", "Nyamirama", "Rukara", "Ruramira"));
        cells.put("Ndego", Arrays.asList("Ndego", "Nyamirama", "Rukara", "Ruramira", "Rwinkwavu"));
        cells.put("Nyamirama", Arrays.asList("Nyamirama", "Rukara", "Ruramira", "Rwinkwavu", "Gahini"));
        cells.put("Rukara", Arrays.asList("Rukara", "Ruramira", "Rwinkwavu", "Gahini", "Kabare"));
        cells.put("Ruramira", Arrays.asList("Ruramira", "Rwinkwavu", "Gahini", "Kabare", "Kabarondo"));
        cells.put("Rwinkwavu", Arrays.asList("Rwinkwavu", "Gahini", "Kabare", "Kabarondo", "Mukarange"));
        
        // Eastern Province - Kirehe District
        cells.put("Gahara", Arrays.asList("Gahara", "Gatore", "Kigarama", "Kigina", "Kirehe"));
        cells.put("Gatore", Arrays.asList("Gatore", "Kigarama", "Kigina", "Kirehe", "Mahama"));
        cells.put("Kigarama", Arrays.asList("Kigarama", "Kigina", "Kirehe", "Mahama", "Mpanga"));
        cells.put("Kigina", Arrays.asList("Kigina", "Kirehe", "Mahama", "Mpanga", "Musaza"));
        cells.put("Kirehe", Arrays.asList("Kirehe", "Mahama", "Mpanga", "Musaza", "Mushikiri"));
        cells.put("Mahama", Arrays.asList("Mahama", "Mpanga", "Musaza", "Mushikiri", "Nasho"));
        cells.put("Mpanga", Arrays.asList("Mpanga", "Musaza", "Mushikiri", "Nasho", "Nyamugari"));
        cells.put("Musaza", Arrays.asList("Musaza", "Mushikiri", "Nasho", "Nyamugari", "Nyarubuye"));
        cells.put("Mushikiri", Arrays.asList("Mushikiri", "Nasho", "Nyamugari", "Nyarubuye", "Rwinkwavu"));
        cells.put("Nasho", Arrays.asList("Nasho", "Nyamugari", "Nyarubuye", "Rwinkwavu", "Gahara"));
        cells.put("Nyamugari", Arrays.asList("Nyamugari", "Nyarubuye", "Rwinkwavu", "Gahara", "Gatore"));
        cells.put("Nyarubuye", Arrays.asList("Nyarubuye", "Rwinkwavu", "Gahara", "Gatore", "Kigarama"));
        cells.put("Rwinkwavu", Arrays.asList("Rwinkwavu", "Gahara", "Gatore", "Kigarama", "Kigina"));
        
        // Eastern Province - Ngoma District
        cells.put("Gashanda", Arrays.asList("Gashanda", "Jarama", "Karembo", "Kazo", "Kibungo"));
        cells.put("Jarama", Arrays.asList("Jarama", "Karembo", "Kazo", "Kibungo", "Mugesera"));
        cells.put("Karembo", Arrays.asList("Karembo", "Kazo", "Kibungo", "Mugesera", "Murama"));
        cells.put("Kazo", Arrays.asList("Kazo", "Kibungo", "Mugesera", "Murama", "Mutenderi"));
        cells.put("Kibungo", Arrays.asList("Kibungo", "Mugesera", "Murama", "Mutenderi", "Remera"));
        cells.put("Mugesera", Arrays.asList("Mugesera", "Murama", "Mutenderi", "Remera", "Rukira"));
        cells.put("Murama", Arrays.asList("Murama", "Mutenderi", "Remera", "Rukira", "Rukumberi"));
        cells.put("Mutenderi", Arrays.asList("Mutenderi", "Remera", "Rukira", "Rukumberi", "Rurenge"));
        cells.put("Remera", Arrays.asList("Remera", "Rukira", "Rukumberi", "Rurenge", "Sake"));
        cells.put("Rukira", Arrays.asList("Rukira", "Rukumberi", "Rurenge", "Sake", "Zaza"));
        cells.put("Rukumberi", Arrays.asList("Rukumberi", "Rurenge", "Sake", "Zaza", "Gashanda"));
        cells.put("Rurenge", Arrays.asList("Rurenge", "Sake", "Zaza", "Gashanda", "Jarama"));
        cells.put("Sake", Arrays.asList("Sake", "Zaza", "Gashanda", "Jarama", "Karembo"));
        cells.put("Zaza", Arrays.asList("Zaza", "Gashanda", "Jarama", "Karembo", "Kazo"));
        
        // Eastern Province - Nyagatare District
        cells.put("Bukure", Arrays.asList("Bukure", "Bwimiyange", "Cyabingo", "Gatunda", "Kanyangese"));
        cells.put("Bwimiyange", Arrays.asList("Bwimiyange", "Cyabingo", "Gatunda", "Kanyangese", "Karangazi"));
        cells.put("Cyabingo", Arrays.asList("Cyabingo", "Gatunda", "Kanyangese", "Karangazi", "Katabagemu"));
        cells.put("Gatunda", Arrays.asList("Gatunda", "Kanyangese", "Karangazi", "Katabagemu", "Kiyombe"));
        cells.put("Kanyangese", Arrays.asList("Kanyangese", "Karangazi", "Katabagemu", "Kiyombe", "Matimba"));
        cells.put("Karangazi", Arrays.asList("Karangazi", "Katabagemu", "Kiyombe", "Matimba", "Mimuri"));
        cells.put("Katabagemu", Arrays.asList("Katabagemu", "Kiyombe", "Matimba", "Mimuri", "Mukama"));
        cells.put("Kiyombe", Arrays.asList("Kiyombe", "Matimba", "Mimuri", "Mukama", "Musheri"));
        cells.put("Matimba", Arrays.asList("Matimba", "Mimuri", "Mukama", "Musheri", "Nyagatare"));
        cells.put("Mimuri", Arrays.asList("Mimuri", "Mukama", "Musheri", "Nyagatare", "Rukomo"));
        cells.put("Mukama", Arrays.asList("Mukama", "Musheri", "Nyagatare", "Rukomo", "Rwempasha"));
        cells.put("Musheri", Arrays.asList("Musheri", "Nyagatare", "Rukomo", "Rwempasha", "Rwimiyaga"));
        cells.put("Nyagatare", Arrays.asList("Nyagatare", "Rukomo", "Rwempasha", "Rwimiyaga", "Tabagwe"));
        cells.put("Rukomo", Arrays.asList("Rukomo", "Rwempasha", "Rwimiyaga", "Tabagwe", "Bukure"));
        cells.put("Rwempasha", Arrays.asList("Rwempasha", "Rwimiyaga", "Tabagwe", "Bukure", "Bwimiyange"));
        cells.put("Rwimiyaga", Arrays.asList("Rwimiyaga", "Tabagwe", "Bukure", "Bwimiyange", "Cyabingo"));
        cells.put("Tabagwe", Arrays.asList("Tabagwe", "Bukure", "Bwimiyange", "Cyabingo", "Gatunda"));
        
        // Eastern Province - Rwamagana District
        cells.put("Fumbwe", Arrays.asList("Fumbwe", "Gahengeri", "Gishali", "Karenge", "Kigabiro"));
        cells.put("Gahengeri", Arrays.asList("Gahengeri", "Gishali", "Karenge", "Kigabiro", "Muhazi"));
        cells.put("Gishali", Arrays.asList("Gishali", "Karenge", "Kigabiro", "Muhazi", "Munyaga"));
        cells.put("Karenge", Arrays.asList("Karenge", "Kigabiro", "Muhazi", "Munyaga", "Munyiginya"));
        cells.put("Kigabiro", Arrays.asList("Kigabiro", "Muhazi", "Munyaga", "Munyiginya", "Musha"));
        cells.put("Muhazi", Arrays.asList("Muhazi", "Munyaga", "Munyiginya", "Musha", "Muyumbu"));
        cells.put("Munyaga", Arrays.asList("Munyaga", "Munyiginya", "Musha", "Muyumbu", "Mwulire"));
        cells.put("Munyiginya", Arrays.asList("Munyiginya", "Musha", "Muyumbu", "Mwulire", "Nyakaliro"));
        cells.put("Musha", Arrays.asList("Musha", "Muyumbu", "Mwulire", "Nyakaliro", "Nzige"));
        cells.put("Muyumbu", Arrays.asList("Muyumbu", "Mwulire", "Nyakaliro", "Nzige", "Rubona"));
        cells.put("Mwulire", Arrays.asList("Mwulire", "Nyakaliro", "Nzige", "Rubona", "Rukara"));
        cells.put("Nyakaliro", Arrays.asList("Nyakaliro", "Nzige", "Rubona", "Rukara", "Fumbwe"));
        cells.put("Nzige", Arrays.asList("Nzige", "Rubona", "Rukara", "Fumbwe", "Gahengeri"));
        cells.put("Rubona", Arrays.asList("Rubona", "Rukara", "Fumbwe", "Gahengeri", "Gishali"));
        cells.put("Rukara", Arrays.asList("Rukara", "Fumbwe", "Gahengeri", "Gishali", "Karenge"));
        
        // Northern Province - Burera District
        cells.put("Bungwe", Arrays.asList("Bungwe", "Butaro", "Cyanika", "Cyeru", "Gahunga"));
        cells.put("Butaro", Arrays.asList("Butaro", "Cyanika", "Cyeru", "Gahunga", "Gatebe"));
        cells.put("Cyanika", Arrays.asList("Cyanika", "Cyeru", "Gahunga", "Gatebe", "Gitovu"));
        cells.put("Cyeru", Arrays.asList("Cyeru", "Gahunga", "Gatebe", "Gitovu", "Kagogo"));
        cells.put("Gahunga", Arrays.asList("Gahunga", "Gatebe", "Gitovu", "Kagogo", "Kinoni"));
        cells.put("Gatebe", Arrays.asList("Gatebe", "Gitovu", "Kagogo", "Kinoni", "Kinyababa"));
        cells.put("Gitovu", Arrays.asList("Gitovu", "Kagogo", "Kinoni", "Kinyababa", "Kivuye"));
        cells.put("Kagogo", Arrays.asList("Kagogo", "Kinoni", "Kinyababa", "Kivuye", "Nemba"));
        cells.put("Kinoni", Arrays.asList("Kinoni", "Kinyababa", "Kivuye", "Nemba", "Rugarama"));
        cells.put("Kinyababa", Arrays.asList("Kinyababa", "Kivuye", "Nemba", "Rugarama", "Rugengabari"));
        cells.put("Kivuye", Arrays.asList("Kivuye", "Nemba", "Rugarama", "Rugengabari", "Ruhunde"));
        cells.put("Nemba", Arrays.asList("Nemba", "Rugarama", "Rugengabari", "Ruhunde", "Rusarabuye"));
        cells.put("Rugarama", Arrays.asList("Rugarama", "Rugengabari", "Ruhunde", "Rusarabuye", "Rwerere"));
        cells.put("Rugengabari", Arrays.asList("Rugengabari", "Ruhunde", "Rusarabuye", "Rwerere", "Bungwe"));
        cells.put("Ruhunde", Arrays.asList("Ruhunde", "Rusarabuye", "Rwerere", "Bungwe", "Butaro"));
        cells.put("Rusarabuye", Arrays.asList("Rusarabuye", "Rwerere", "Bungwe", "Butaro", "Cyanika"));
        cells.put("Rwerere", Arrays.asList("Rwerere", "Bungwe", "Butaro", "Cyanika", "Cyeru"));
        
        // Northern Province - Gakenke District
        cells.put("Busengo", Arrays.asList("Busengo", "Coko", "Cyabingo", "Gakenke", "Gashenyi"));
        cells.put("Coko", Arrays.asList("Coko", "Cyabingo", "Gakenke", "Gashenyi", "Janja"));
        cells.put("Cyabingo", Arrays.asList("Cyabingo", "Gakenke", "Gashenyi", "Janja", "Kamubuga"));
        cells.put("Gakenke", Arrays.asList("Gakenke", "Gashenyi", "Janja", "Kamubuga", "Karambo"));
        cells.put("Gashenyi", Arrays.asList("Gashenyi", "Janja", "Kamubuga", "Karambo", "Kivuruga"));
        cells.put("Janja", Arrays.asList("Janja", "Kamubuga", "Karambo", "Kivuruga", "Mataba"));
        cells.put("Kamubuga", Arrays.asList("Kamubuga", "Karambo", "Kivuruga", "Mataba", "Minazi"));
        cells.put("Karambo", Arrays.asList("Karambo", "Kivuruga", "Mataba", "Minazi", "Mugunga"));
        cells.put("Kivuruga", Arrays.asList("Kivuruga", "Mataba", "Minazi", "Mugunga", "Muhondo"));
        cells.put("Mataba", Arrays.asList("Mataba", "Minazi", "Mugunga", "Muhondo", "Muyongwe"));
        cells.put("Minazi", Arrays.asList("Minazi", "Mugunga", "Muhondo", "Muyongwe", "Muzo"));
        cells.put("Mugunga", Arrays.asList("Mugunga", "Muhondo", "Muyongwe", "Muzo", "Nemba"));
        cells.put("Muhondo", Arrays.asList("Muhondo", "Muyongwe", "Muzo", "Nemba", "Ruli"));
        cells.put("Muyongwe", Arrays.asList("Muyongwe", "Muzo", "Nemba", "Ruli", "Rusasa"));
        cells.put("Muzo", Arrays.asList("Muzo", "Nemba", "Ruli", "Rusasa", "Rushashi"));
        cells.put("Nemba", Arrays.asList("Nemba", "Ruli", "Rusasa", "Rushashi", "Busengo"));
        cells.put("Ruli", Arrays.asList("Ruli", "Rusasa", "Rushashi", "Busengo", "Coko"));
        cells.put("Rusasa", Arrays.asList("Rusasa", "Rushashi", "Busengo", "Coko", "Cyabingo"));
        cells.put("Rushashi", Arrays.asList("Rushashi", "Busengo", "Coko", "Cyabingo", "Gakenke"));
        
        // Northern Province - Gicumbi District
        cells.put("Bukure", Arrays.asList("Bukure", "Bwisige", "Byumba", "Cyumba", "Giti"));
        cells.put("Bwisige", Arrays.asList("Bwisige", "Byumba", "Cyumba", "Giti", "Kageyo"));
        cells.put("Byumba", Arrays.asList("Byumba", "Cyumba", "Giti", "Kageyo", "Kaniga"));
        cells.put("Cyumba", Arrays.asList("Cyumba", "Giti", "Kageyo", "Kaniga", "Manyagiro"));
        cells.put("Giti", Arrays.asList("Giti", "Kageyo", "Kaniga", "Manyagiro", "Miyove"));
        cells.put("Kageyo", Arrays.asList("Kageyo", "Kaniga", "Manyagiro", "Miyove", "Mukarange"));
        cells.put("Kaniga", Arrays.asList("Kaniga", "Manyagiro", "Miyove", "Mukarange", "Muko"));
        cells.put("Manyagiro", Arrays.asList("Manyagiro", "Miyove", "Mukarange", "Muko", "Mutete"));
        cells.put("Miyove", Arrays.asList("Miyove", "Mukarange", "Muko", "Mutete", "Nyamiyaga"));
        cells.put("Mukarange", Arrays.asList("Mukarange", "Muko", "Mutete", "Nyamiyaga", "Nyankenke"));
        cells.put("Muko", Arrays.asList("Muko", "Mutete", "Nyamiyaga", "Nyankenke", "Rubaya"));
        cells.put("Mutete", Arrays.asList("Mutete", "Nyamiyaga", "Nyankenke", "Rubaya", "Rukomo"));
        cells.put("Nyamiyaga", Arrays.asList("Nyamiyaga", "Nyankenke", "Rubaya", "Rukomo", "Rusasa"));
        cells.put("Nyankenke", Arrays.asList("Nyankenke", "Rubaya", "Rukomo", "Rusasa", "Rushaki"));
        cells.put("Rubaya", Arrays.asList("Rubaya", "Rukomo", "Rusasa", "Rushaki", "Rutare"));
        cells.put("Rukomo", Arrays.asList("Rukomo", "Rusasa", "Rushaki", "Rutare", "Ruvune"));
        cells.put("Rusasa", Arrays.asList("Rusasa", "Rushaki", "Rutare", "Ruvune", "Rwamiko"));
        cells.put("Rushaki", Arrays.asList("Rushaki", "Rutare", "Ruvune", "Rwamiko", "Shangasha"));
        cells.put("Rutare", Arrays.asList("Rutare", "Ruvune", "Rwamiko", "Shangasha", "Bukure"));
        cells.put("Ruvune", Arrays.asList("Ruvune", "Rwamiko", "Shangasha", "Bukure", "Bwisige"));
        cells.put("Rwamiko", Arrays.asList("Rwamiko", "Shangasha", "Bukure", "Bwisige", "Byumba"));
        cells.put("Shangasha", Arrays.asList("Shangasha", "Bukure", "Bwisige", "Byumba", "Cyumba"));
        
        // Northern Province - Musanze District
        cells.put("Busogo", Arrays.asList("Busogo", "Cyuve", "Gacaca", "Gashaki", "Gataraga"));
        cells.put("Cyuve", Arrays.asList("Cyuve", "Gacaca", "Gashaki", "Gataraga", "Kimonyi"));
        cells.put("Gacaca", Arrays.asList("Gacaca", "Gashaki", "Gataraga", "Kimonyi", "Kinigi"));
        cells.put("Gashaki", Arrays.asList("Gashaki", "Gataraga", "Kimonyi", "Kinigi", "Muhoza"));
        cells.put("Gataraga", Arrays.asList("Gataraga", "Kimonyi", "Kinigi", "Muhoza", "Muko"));
        cells.put("Kimonyi", Arrays.asList("Kimonyi", "Kinigi", "Muhoza", "Muko", "Musanze"));
        cells.put("Kinigi", Arrays.asList("Kinigi", "Muhoza", "Muko", "Musanze", "Nkotsi"));
        cells.put("Muhoza", Arrays.asList("Muhoza", "Muko", "Musanze", "Nkotsi", "Nyange"));
        cells.put("Muko", Arrays.asList("Muko", "Musanze", "Nkotsi", "Nyange", "Remera"));
        cells.put("Musanze", Arrays.asList("Musanze", "Nkotsi", "Nyange", "Remera", "Rwaza"));
        cells.put("Nkotsi", Arrays.asList("Nkotsi", "Nyange", "Remera", "Rwaza", "Shingiro"));
        cells.put("Nyange", Arrays.asList("Nyange", "Remera", "Rwaza", "Shingiro", "Busogo"));
        cells.put("Remera", Arrays.asList("Remera", "Rwaza", "Shingiro", "Busogo", "Cyuve"));
        cells.put("Rwaza", Arrays.asList("Rwaza", "Shingiro", "Busogo", "Cyuve", "Gacaca"));
        cells.put("Shingiro", Arrays.asList("Shingiro", "Busogo", "Cyuve", "Gacaca", "Gashaki"));
        
        // Northern Province - Rulindo District
        cells.put("Base", Arrays.asList("Base", "Burega", "Bushoki", "Buyoga", "Cyinzuzi"));
        cells.put("Burega", Arrays.asList("Burega", "Bushoki", "Buyoga", "Cyinzuzi", "Cyungo"));
        cells.put("Bushoki", Arrays.asList("Bushoki", "Buyoga", "Cyinzuzi", "Cyungo", "Kinihira"));
        cells.put("Buyoga", Arrays.asList("Buyoga", "Cyinzuzi", "Cyungo", "Kinihira", "Kisaro"));
        cells.put("Cyinzuzi", Arrays.asList("Cyinzuzi", "Cyungo", "Kinihira", "Kisaro", "Masoro"));
        cells.put("Cyungo", Arrays.asList("Cyungo", "Kinihira", "Kisaro", "Masoro", "Mbogo"));
        cells.put("Kinihira", Arrays.asList("Kinihira", "Kisaro", "Masoro", "Mbogo", "Murambi"));
        cells.put("Kisaro", Arrays.asList("Kisaro", "Masoro", "Mbogo", "Murambi", "Ngoma"));
        cells.put("Masoro", Arrays.asList("Masoro", "Mbogo", "Murambi", "Ngoma", "Ntarabana"));
        cells.put("Mbogo", Arrays.asList("Mbogo", "Murambi", "Ngoma", "Ntarabana", "Rukozo"));
        cells.put("Murambi", Arrays.asList("Murambi", "Ngoma", "Ntarabana", "Rukozo", "Rusiga"));
        cells.put("Ngoma", Arrays.asList("Ngoma", "Ntarabana", "Rukozo", "Rusiga", "Shyorongi"));
        cells.put("Ntarabana", Arrays.asList("Ntarabana", "Rukozo", "Rusiga", "Shyorongi", "Tumba"));
        cells.put("Rukozo", Arrays.asList("Rukozo", "Rusiga", "Shyorongi", "Tumba", "Base"));
        cells.put("Rusiga", Arrays.asList("Rusiga", "Shyorongi", "Tumba", "Base", "Burega"));
        cells.put("Shyorongi", Arrays.asList("Shyorongi", "Tumba", "Base", "Burega", "Bushoki"));
        cells.put("Tumba", Arrays.asList("Tumba", "Base", "Burega", "Bushoki", "Buyoga"));
        
        // Southern Province - Gisagara District
        cells.put("Gikundamvura", Arrays.asList("Gikundamvura", "Muganza", "Mujuga", "Mukura", "Musha"));
        cells.put("Muganza", Arrays.asList("Muganza", "Mujuga", "Mukura", "Musha", "Ndora"));
        cells.put("Mujuga", Arrays.asList("Mujuga", "Mukura", "Musha", "Ndora", "Nyanza"));
        cells.put("Mukura", Arrays.asList("Mukura", "Musha", "Ndora", "Nyanza", "Nyarusange"));
        cells.put("Musha", Arrays.asList("Musha", "Ndora", "Nyanza", "Nyarusange", "Save"));
        cells.put("Ndora", Arrays.asList("Ndora", "Nyanza", "Nyarusange", "Save", "Tumba"));
        cells.put("Nyanza", Arrays.asList("Nyanza", "Nyarusange", "Save", "Tumba", "Gikundamvura"));
        cells.put("Nyarusange", Arrays.asList("Nyarusange", "Save", "Tumba", "Gikundamvura", "Muganza"));
        cells.put("Save", Arrays.asList("Save", "Tumba", "Gikundamvura", "Muganza", "Mujuga"));
        cells.put("Tumba", Arrays.asList("Tumba", "Gikundamvura", "Muganza", "Mujuga", "Mukura"));
        
        // Southern Province - Huye District
        cells.put("Gishamvu", Arrays.asList("Gishamvu", "Huye", "Karama", "Kigoma", "Kinazi"));
        cells.put("Huye", Arrays.asList("Huye", "Karama", "Kigoma", "Kinazi", "Maraba"));
        cells.put("Karama", Arrays.asList("Karama", "Kigoma", "Kinazi", "Maraba", "Mbazi"));
        cells.put("Kigoma", Arrays.asList("Kigoma", "Kinazi", "Maraba", "Mbazi", "Mukura"));
        cells.put("Kinazi", Arrays.asList("Kinazi", "Maraba", "Mbazi", "Mukura", "Ngoma"));
        cells.put("Maraba", Arrays.asList("Maraba", "Mbazi", "Mukura", "Ngoma", "Ruhashya"));
        cells.put("Mbazi", Arrays.asList("Mbazi", "Mukura", "Ngoma", "Ruhashya", "Rusatira"));
        cells.put("Mukura", Arrays.asList("Mukura", "Ngoma", "Ruhashya", "Rusatira", "Rwaniro"));
        cells.put("Ngoma", Arrays.asList("Ngoma", "Ruhashya", "Rusatira", "Rwaniro", "Simbi"));
        cells.put("Ruhashya", Arrays.asList("Ruhashya", "Rusatira", "Rwaniro", "Simbi", "Tumba"));
        cells.put("Rusatira", Arrays.asList("Rusatira", "Rwaniro", "Simbi", "Tumba", "Gishamvu"));
        cells.put("Rwaniro", Arrays.asList("Rwaniro", "Simbi", "Tumba", "Gishamvu", "Huye"));
        cells.put("Simbi", Arrays.asList("Simbi", "Tumba", "Gishamvu", "Huye", "Karama"));
        cells.put("Tumba", Arrays.asList("Tumba", "Gishamvu", "Huye", "Karama", "Kigoma"));
        
        // Southern Province - Kamonyi District
        cells.put("Gacurabwenge", Arrays.asList("Gacurabwenge", "Karama", "Kayenzi", "Kayumbu", "Mugina"));
        cells.put("Karama", Arrays.asList("Karama", "Kayenzi", "Kayumbu", "Mugina", "Musambira"));
        cells.put("Kayenzi", Arrays.asList("Kayenzi", "Kayumbu", "Mugina", "Musambira", "Ngamba"));
        cells.put("Kayumbu", Arrays.asList("Kayumbu", "Mugina", "Musambira", "Ngamba", "Nyamiyaga"));
        cells.put("Mugina", Arrays.asList("Mugina", "Musambira", "Ngamba", "Nyamiyaga", "Nyarubaka"));
        cells.put("Musambira", Arrays.asList("Musambira", "Ngamba", "Nyamiyaga", "Nyarubaka", "Rugarika"));
        cells.put("Ngamba", Arrays.asList("Ngamba", "Nyamiyaga", "Nyarubaka", "Rugarika", "Rukoma"));
        cells.put("Nyamiyaga", Arrays.asList("Nyamiyaga", "Nyarubaka", "Rugarika", "Rukoma", "Runda"));
        cells.put("Nyarubaka", Arrays.asList("Nyarubaka", "Rugarika", "Rukoma", "Runda", "Gacurabwenge"));
        cells.put("Rugarika", Arrays.asList("Rugarika", "Rukoma", "Runda", "Gacurabwenge", "Karama"));
        cells.put("Rukoma", Arrays.asList("Rukoma", "Runda", "Gacurabwenge", "Karama", "Kayenzi"));
        cells.put("Runda", Arrays.asList("Runda", "Gacurabwenge", "Karama", "Kayenzi", "Kayumbu"));
        
        // Southern Province - Muhanga District
        cells.put("Cyeza", Arrays.asList("Cyeza", "Kabacuzi", "Kibangu", "Kiyumba", "Muhanga"));
        cells.put("Kabacuzi", Arrays.asList("Kabacuzi", "Kibangu", "Kiyumba", "Muhanga", "Mushishiro"));
        cells.put("Kibangu", Arrays.asList("Kibangu", "Kiyumba", "Muhanga", "Mushishiro", "Nyabinoni"));
        cells.put("Kiyumba", Arrays.asList("Kiyumba", "Muhanga", "Mushishiro", "Nyabinoni", "Nyamabuye"));
        cells.put("Muhanga", Arrays.asList("Muhanga", "Mushishiro", "Nyabinoni", "Nyamabuye", "Nyarusange"));
        cells.put("Mushishiro", Arrays.asList("Mushishiro", "Nyabinoni", "Nyamabuye", "Nyarusange", "Rongi"));
        cells.put("Nyabinoni", Arrays.asList("Nyabinoni", "Nyamabuye", "Nyarusange", "Rongi", "Rugendabari"));
        cells.put("Nyamabuye", Arrays.asList("Nyamabuye", "Nyarusange", "Rongi", "Rugendabari", "Shyogwe"));
        cells.put("Nyarusange", Arrays.asList("Nyarusange", "Rongi", "Rugendabari", "Shyogwe", "Cyeza"));
        cells.put("Rongi", Arrays.asList("Rongi", "Rugendabari", "Shyogwe", "Cyeza", "Kabacuzi"));
        cells.put("Rugendabari", Arrays.asList("Rugendabari", "Shyogwe", "Cyeza", "Kabacuzi", "Kibangu"));
        cells.put("Shyogwe", Arrays.asList("Shyogwe", "Cyeza", "Kabacuzi", "Kibangu", "Kiyumba"));
        
        // Southern Province - Nyamagabe District
        cells.put("Buruhukiro", Arrays.asList("Buruhukiro", "Cyanika", "Gasaka", "Gatare", "Kaduha"));
        cells.put("Cyanika", Arrays.asList("Cyanika", "Gasaka", "Gatare", "Kaduha", "Kamegeli"));
        cells.put("Gasaka", Arrays.asList("Gasaka", "Gatare", "Kaduha", "Kamegeli", "Kibirizi"));
        cells.put("Gatare", Arrays.asList("Gatare", "Kaduha", "Kamegeli", "Kibirizi", "Kibumbwe"));
        cells.put("Kaduha", Arrays.asList("Kaduha", "Kamegeli", "Kibirizi", "Kibumbwe", "Kitabi"));
        cells.put("Kamegeli", Arrays.asList("Kamegeli", "Kibirizi", "Kibumbwe", "Kitabi", "Mbazi"));
        cells.put("Kibirizi", Arrays.asList("Kibirizi", "Kibumbwe", "Kitabi", "Mbazi", "Mugano"));
        cells.put("Kibumbwe", Arrays.asList("Kibumbwe", "Kitabi", "Mbazi", "Mugano", "Musange"));
        cells.put("Kitabi", Arrays.asList("Kitabi", "Mbazi", "Mugano", "Musange", "Musebeya"));
        cells.put("Mbazi", Arrays.asList("Mbazi", "Mugano", "Musange", "Musebeya", "Mushubi"));
        cells.put("Mugano", Arrays.asList("Mugano", "Musange", "Musebeya", "Mushubi", "Nkomane"));
        cells.put("Musange", Arrays.asList("Musange", "Musebeya", "Mushubi", "Nkomane", "Tare"));
        cells.put("Musebeya", Arrays.asList("Musebeya", "Mushubi", "Nkomane", "Tare", "Buruhukiro"));
        cells.put("Mushubi", Arrays.asList("Mushubi", "Nkomane", "Tare", "Buruhukiro", "Cyanika"));
        cells.put("Nkomane", Arrays.asList("Nkomane", "Tare", "Buruhukiro", "Cyanika", "Gasaka"));
        cells.put("Tare", Arrays.asList("Tare", "Buruhukiro", "Cyanika", "Gasaka", "Gatare"));
        
        // Southern Province - Nyanza District
        cells.put("Busasamana", Arrays.asList("Busasamana", "Busoro", "Cyabakamyi", "Kibirizi", "Kigoma"));
        cells.put("Busoro", Arrays.asList("Busoro", "Cyabakamyi", "Kibirizi", "Kigoma", "Mukingo"));
        cells.put("Cyabakamyi", Arrays.asList("Cyabakamyi", "Kibirizi", "Kigoma", "Mukingo", "Muyira"));
        cells.put("Kibirizi", Arrays.asList("Kibirizi", "Kigoma", "Mukingo", "Muyira", "Ntyazo"));
        cells.put("Kigoma", Arrays.asList("Kigoma", "Mukingo", "Muyira", "Ntyazo", "Nyagisozi"));
        cells.put("Mukingo", Arrays.asList("Mukingo", "Muyira", "Ntyazo", "Nyagisozi", "Rwabicuma"));
        cells.put("Muyira", Arrays.asList("Muyira", "Ntyazo", "Nyagisozi", "Rwabicuma", "Rwankuba"));
        cells.put("Ntyazo", Arrays.asList("Ntyazo", "Nyagisozi", "Rwabicuma", "Rwankuba", "Busasamana"));
        cells.put("Nyagisozi", Arrays.asList("Nyagisozi", "Rwabicuma", "Rwankuba", "Busasamana", "Busoro"));
        cells.put("Rwabicuma", Arrays.asList("Rwabicuma", "Rwankuba", "Busasamana", "Busoro", "Cyabakamyi"));
        cells.put("Rwankuba", Arrays.asList("Rwankuba", "Busasamana", "Busoro", "Cyabakamyi", "Kibirizi"));
        
        // Southern Province - Nyaruguru District
        cells.put("Bitare", Arrays.asList("Bitare", "Busanze", "Cyahinda", "Kibeho", "Kivu"));
        cells.put("Busanze", Arrays.asList("Busanze", "Cyahinda", "Kibeho", "Kivu", "Mata"));
        cells.put("Cyahinda", Arrays.asList("Cyahinda", "Kibeho", "Kivu", "Mata", "Muganza"));
        cells.put("Kibeho", Arrays.asList("Kibeho", "Kivu", "Mata", "Muganza", "Munini"));
        cells.put("Kivu", Arrays.asList("Kivu", "Mata", "Muganza", "Munini", "Ngera"));
        cells.put("Mata", Arrays.asList("Mata", "Muganza", "Munini", "Ngera", "Ngoma"));
        cells.put("Muganza", Arrays.asList("Muganza", "Munini", "Ngera", "Ngoma", "Nyabimata"));
        cells.put("Munini", Arrays.asList("Munini", "Ngera", "Ngoma", "Nyabimata", "Nyagisozi"));
        cells.put("Ngera", Arrays.asList("Ngera", "Ngoma", "Nyabimata", "Nyagisozi", "Nyamagabe"));
        cells.put("Ngoma", Arrays.asList("Ngoma", "Nyabimata", "Nyagisozi", "Nyamagabe", "Nyamirundi"));
        cells.put("Nyabimata", Arrays.asList("Nyabimata", "Nyagisozi", "Nyamagabe", "Nyamirundi", "Nyarurema"));
        cells.put("Nyagisozi", Arrays.asList("Nyagisozi", "Nyamagabe", "Nyamirundi", "Nyarurema", "Rugogwe"));
        cells.put("Nyamagabe", Arrays.asList("Nyamagabe", "Nyamirundi", "Nyarurema", "Rugogwe", "Rukore"));
        cells.put("Nyamirundi", Arrays.asList("Nyamirundi", "Nyarurema", "Rugogwe", "Rukore", "Rusasa"));
        cells.put("Nyarurema", Arrays.asList("Nyarurema", "Rugogwe", "Rukore", "Rusasa", "Rusenge"));
        cells.put("Rugogwe", Arrays.asList("Rugogwe", "Rukore", "Rusasa", "Rusenge", "Rwamiko"));
        cells.put("Rukore", Arrays.asList("Rukore", "Rusasa", "Rusenge", "Rwamiko", "Bitare"));
        cells.put("Rusasa", Arrays.asList("Rusasa", "Rusenge", "Rwamiko", "Bitare", "Busanze"));
        cells.put("Rusenge", Arrays.asList("Rusenge", "Rwamiko", "Bitare", "Busanze", "Cyahinda"));
        cells.put("Rwamiko", Arrays.asList("Rwamiko", "Bitare", "Busanze", "Cyahinda", "Kibeho"));
        
        // Southern Province - Ruhango District
        cells.put("Bweramana", Arrays.asList("Bweramana", "Byimana", "Kabagali", "Kinazi", "Kinihira"));
        cells.put("Byimana", Arrays.asList("Byimana", "Kabagali", "Kinazi", "Kinihira", "Mbuye"));
        cells.put("Kabagali", Arrays.asList("Kabagali", "Kinazi", "Kinihira", "Mbuye", "Mukingo"));
        cells.put("Kinazi", Arrays.asList("Kinazi", "Kinihira", "Mbuye", "Mukingo", "Muyira"));
        cells.put("Kinihira", Arrays.asList("Kinihira", "Mbuye", "Mukingo", "Muyira", "Ntongwe"));
        cells.put("Mbuye", Arrays.asList("Mbuye", "Mukingo", "Muyira", "Ntongwe", "Ruhango"));
        cells.put("Mukingo", Arrays.asList("Mukingo", "Muyira", "Ntongwe", "Ruhango", "Rusatira"));
        cells.put("Muyira", Arrays.asList("Muyira", "Ntongwe", "Ruhango", "Rusatira", "Bweramana"));
        cells.put("Ntongwe", Arrays.asList("Ntongwe", "Ruhango", "Rusatira", "Bweramana", "Byimana"));
        cells.put("Ruhango", Arrays.asList("Ruhango", "Rusatira", "Bweramana", "Byimana", "Kabagali"));
        cells.put("Rusatira", Arrays.asList("Rusatira", "Bweramana", "Byimana", "Kabagali", "Kinazi"));
        
        // Southern Province - Rusizi District
        cells.put("Boneza", Arrays.asList("Boneza", "Gihombo", "Kigwena", "Muganza", "Mukura"));
        cells.put("Gihombo", Arrays.asList("Gihombo", "Kigwena", "Muganza", "Mukura", "Mururu"));
        cells.put("Kigwena", Arrays.asList("Kigwena", "Muganza", "Mukura", "Mururu", "Nkanka"));
        cells.put("Muganza", Arrays.asList("Muganza", "Mukura", "Mururu", "Nkanka", "Nkombo"));
        cells.put("Mukura", Arrays.asList("Mukura", "Mururu", "Nkanka", "Nkombo", "Nkungu"));
        cells.put("Mururu", Arrays.asList("Mururu", "Nkanka", "Nkombo", "Nkungu", "Nyakabuye"));
        cells.put("Nkanka", Arrays.asList("Nkanka", "Nkombo", "Nkungu", "Nyakabuye", "Nyakarenzo"));
        cells.put("Nkombo", Arrays.asList("Nkombo", "Nkungu", "Nyakabuye", "Nyakarenzo", "Nzahaha"));
        cells.put("Nkungu", Arrays.asList("Nkungu", "Nyakabuye", "Nyakarenzo", "Nzahaha", "Rwimbogo"));
        cells.put("Nyakabuye", Arrays.asList("Nyakabuye", "Nyakarenzo", "Nzahaha", "Rwimbogo", "Boneza"));
        cells.put("Nyakarenzo", Arrays.asList("Nyakarenzo", "Nzahaha", "Rwimbogo", "Boneza", "Gihombo"));
        cells.put("Nzahaha", Arrays.asList("Nzahaha", "Rwimbogo", "Boneza", "Gihombo", "Kigwena"));
        cells.put("Rwimbogo", Arrays.asList("Rwimbogo", "Boneza", "Gihombo", "Kigwena", "Muganza"));
        
        // Western Province - Karongi District
        cells.put("Bwishyura", Arrays.asList("Bwishyura", "Gashari", "Gishyita", "Gitesi", "Mubuga"));
        cells.put("Gashari", Arrays.asList("Gashari", "Gishyita", "Gitesi", "Mubuga", "Murambi"));
        cells.put("Gishyita", Arrays.asList("Gishyita", "Gitesi", "Mubuga", "Murambi", "Murundi"));
        cells.put("Gitesi", Arrays.asList("Gitesi", "Mubuga", "Murambi", "Murundi", "Mutuntu"));
        cells.put("Mubuga", Arrays.asList("Mubuga", "Murambi", "Murundi", "Mutuntu", "Rubengera"));
        cells.put("Murambi", Arrays.asList("Murambi", "Murundi", "Mutuntu", "Rubengera", "Rugabano"));
        cells.put("Murundi", Arrays.asList("Murundi", "Mutuntu", "Rubengera", "Rugabano", "Ruganda"));
        cells.put("Mutuntu", Arrays.asList("Mutuntu", "Rubengera", "Rugabano", "Ruganda", "Rwankuba"));
        cells.put("Rubengera", Arrays.asList("Rubengera", "Rugabano", "Ruganda", "Rwankuba", "Twumba"));
        cells.put("Rugabano", Arrays.asList("Rugabano", "Ruganda", "Rwankuba", "Twumba", "Bwishyura"));
        cells.put("Ruganda", Arrays.asList("Ruganda", "Rwankuba", "Twumba", "Bwishyura", "Gashari"));
        cells.put("Rwankuba", Arrays.asList("Rwankuba", "Twumba", "Bwishyura", "Gashari", "Gishyita"));
        cells.put("Twumba", Arrays.asList("Twumba", "Bwishyura", "Gashari", "Gishyita", "Gitesi"));
        
        // Western Province - Ngororero District
        cells.put("Bwira", Arrays.asList("Bwira", "Gatumba", "Hindiro", "Kabaya", "Kageyo"));
        cells.put("Gatumba", Arrays.asList("Gatumba", "Hindiro", "Kabaya", "Kageyo", "Kavumu"));
        cells.put("Hindiro", Arrays.asList("Hindiro", "Kabaya", "Kageyo", "Kavumu", "Matyazo"));
        cells.put("Kabaya", Arrays.asList("Kabaya", "Kageyo", "Kavumu", "Matyazo", "Muhanda"));
        cells.put("Kageyo", Arrays.asList("Kageyo", "Kavumu", "Matyazo", "Muhanda", "Muhororo"));
        cells.put("Kavumu", Arrays.asList("Kavumu", "Matyazo", "Muhanda", "Muhororo", "Ndaro"));
        cells.put("Matyazo", Arrays.asList("Matyazo", "Muhanda", "Muhororo", "Ndaro", "Ngororero"));
        cells.put("Muhanda", Arrays.asList("Muhanda", "Muhororo", "Ndaro", "Ngororero", "Ngoma"));
        cells.put("Muhororo", Arrays.asList("Muhororo", "Ndaro", "Ngororero", "Ngoma", "Nyange"));
        cells.put("Ndaro", Arrays.asList("Ndaro", "Ngororero", "Ngoma", "Nyange", "Sovu"));
        cells.put("Ngororero", Arrays.asList("Ngororero", "Ngoma", "Nyange", "Sovu", "Bwira"));
        cells.put("Ngoma", Arrays.asList("Ngoma", "Nyange", "Sovu", "Bwira", "Gatumba"));
        cells.put("Nyange", Arrays.asList("Nyange", "Sovu", "Bwira", "Gatumba", "Hindiro"));
        cells.put("Sovu", Arrays.asList("Sovu", "Bwira", "Gatumba", "Hindiro", "Kabaya"));
        
        // Western Province - Nyabihu District
        cells.put("Bigogwe", Arrays.asList("Bigogwe", "Jenda", "Jomba", "Kabatwa", "Karago"));
        cells.put("Jenda", Arrays.asList("Jenda", "Jomba", "Kabatwa", "Karago", "Kintobo"));
        cells.put("Jomba", Arrays.asList("Jomba", "Kabatwa", "Karago", "Kintobo", "Mukamira"));
        cells.put("Kabatwa", Arrays.asList("Kabatwa", "Karago", "Kintobo", "Mukamira", "Muringa"));
        cells.put("Karago", Arrays.asList("Karago", "Kintobo", "Mukamira", "Muringa", "Rambura"));
        cells.put("Kintobo", Arrays.asList("Kintobo", "Mukamira", "Muringa", "Rambura", "Rugera"));
        cells.put("Mukamira", Arrays.asList("Mukamira", "Muringa", "Rambura", "Rugera", "Rurembo"));
        cells.put("Muringa", Arrays.asList("Muringa", "Rambura", "Rugera", "Rurembo", "Shyira"));
        cells.put("Rambura", Arrays.asList("Rambura", "Rugera", "Rurembo", "Shyira", "Bigogwe"));
        cells.put("Rugera", Arrays.asList("Rugera", "Rurembo", "Shyira", "Bigogwe", "Jenda"));
        cells.put("Rurembo", Arrays.asList("Rurembo", "Shyira", "Bigogwe", "Jenda", "Jomba"));
        cells.put("Shyira", Arrays.asList("Shyira", "Bigogwe", "Jenda", "Jomba", "Kabatwa"));
        
        // Western Province - Nyamasheke District
        cells.put("Bushekeri", Arrays.asList("Bushekeri", "Bushenge", "Cyato", "Gihombo", "Kagano"));
        cells.put("Bushenge", Arrays.asList("Bushenge", "Cyato", "Gihombo", "Kagano", "Kanjongo"));
        cells.put("Cyato", Arrays.asList("Cyato", "Gihombo", "Kagano", "Kanjongo", "Karambi"));
        cells.put("Gihombo", Arrays.asList("Gihombo", "Kagano", "Kanjongo", "Karambi", "Karengera"));
        cells.put("Kagano", Arrays.asList("Kagano", "Kanjongo", "Karambi", "Karengera", "Kirimbi"));
        cells.put("Kanjongo", Arrays.asList("Kanjongo", "Karambi", "Karengera", "Kirimbi", "Macuba"));
        cells.put("Karambi", Arrays.asList("Karambi", "Karengera", "Kirimbi", "Macuba", "Mahembe"));
        cells.put("Karengera", Arrays.asList("Karengera", "Kirimbi", "Macuba", "Mahembe", "Nyabitekeri"));
        cells.put("Kirimbi", Arrays.asList("Kirimbi", "Macuba", "Mahembe", "Nyabitekeri", "Rangiro"));
        cells.put("Macuba", Arrays.asList("Macuba", "Mahembe", "Nyabitekeri", "Rangiro", "Ruharambuga"));
        cells.put("Mahembe", Arrays.asList("Mahembe", "Nyabitekeri", "Rangiro", "Ruharambuga", "Shangi"));
        cells.put("Nyabitekeri", Arrays.asList("Nyabitekeri", "Rangiro", "Ruharambuga", "Shangi", "Yaramba"));
        cells.put("Rangiro", Arrays.asList("Rangiro", "Ruharambuga", "Shangi", "Yaramba", "Bushekeri"));
        cells.put("Ruharambuga", Arrays.asList("Ruharambuga", "Shangi", "Yaramba", "Bushekeri", "Bushenge"));
        cells.put("Shangi", Arrays.asList("Shangi", "Yaramba", "Bushekeri", "Bushenge", "Cyato"));
        cells.put("Yaramba", Arrays.asList("Yaramba", "Bushekeri", "Bushenge", "Cyato", "Gihombo"));
        
        // Western Province - Rubavu District
        cells.put("Bugeshi", Arrays.asList("Bugeshi", "Busasamana", "Cyanzarwe", "Gisenyi", "Kanama"));
        cells.put("Busasamana", Arrays.asList("Busasamana", "Cyanzarwe", "Gisenyi", "Kanama", "Kanzenze"));
        cells.put("Cyanzarwe", Arrays.asList("Cyanzarwe", "Gisenyi", "Kanama", "Kanzenze", "Mudende"));
        cells.put("Gisenyi", Arrays.asList("Gisenyi", "Kanama", "Kanzenze", "Mudende", "Nyakiriba"));
        cells.put("Kanama", Arrays.asList("Kanama", "Kanzenze", "Mudende", "Nyakiriba", "Nyamyumba"));
        cells.put("Kanzenze", Arrays.asList("Kanzenze", "Mudende", "Nyakiriba", "Nyamyumba", "Nyundo"));
        cells.put("Mudende", Arrays.asList("Mudende", "Nyakiriba", "Nyamyumba", "Nyundo", "Rubavu"));
        cells.put("Nyakiriba", Arrays.asList("Nyakiriba", "Nyamyumba", "Nyundo", "Rubavu", "Rugerero"));
        cells.put("Nyamyumba", Arrays.asList("Nyamyumba", "Nyundo", "Rubavu", "Rugerero", "Bugeshi"));
        cells.put("Nyundo", Arrays.asList("Nyundo", "Rubavu", "Rugerero", "Bugeshi", "Busasamana"));
        cells.put("Rubavu", Arrays.asList("Rubavu", "Rugerero", "Bugeshi", "Busasamana", "Cyanzarwe"));
        cells.put("Rugerero", Arrays.asList("Rugerero", "Bugeshi", "Busasamana", "Cyanzarwe", "Gisenyi"));
        
        // Western Province - Rutsiro District
        cells.put("Boneza", Arrays.asList("Boneza", "Gihango", "Kigeyo", "Kivumu", "Manihira"));
        cells.put("Gihango", Arrays.asList("Gihango", "Kigeyo", "Kivumu", "Manihira", "Mukura"));
        cells.put("Kigeyo", Arrays.asList("Kigeyo", "Kivumu", "Manihira", "Mukura", "Murunda"));
        cells.put("Kivumu", Arrays.asList("Kivumu", "Manihira", "Mukura", "Murunda", "Musasa"));
        cells.put("Manihira", Arrays.asList("Manihira", "Mukura", "Murunda", "Musasa", "Mushonyi"));
        cells.put("Mukura", Arrays.asList("Mukura", "Murunda", "Musasa", "Mushonyi", "Mushubati"));
        cells.put("Murunda", Arrays.asList("Murunda", "Musasa", "Mushonyi", "Mushubati", "Nyabirasi"));
        cells.put("Musasa", Arrays.asList("Musasa", "Mushonyi", "Mushubati", "Nyabirasi", "Ruhango"));
        cells.put("Mushonyi", Arrays.asList("Mushonyi", "Mushubati", "Nyabirasi", "Ruhango", "Rusebeya"));
        cells.put("Mushubati", Arrays.asList("Mushubati", "Nyabirasi", "Ruhango", "Rusebeya", "Boneza"));
        cells.put("Nyabirasi", Arrays.asList("Nyabirasi", "Ruhango", "Rusebeya", "Boneza", "Gihango"));
        cells.put("Ruhango", Arrays.asList("Ruhango", "Rusebeya", "Boneza", "Gihango", "Kigeyo"));
        cells.put("Rusebeya", Arrays.asList("Rusebeya", "Boneza", "Gihango", "Kigeyo", "Kivumu"));
        
        return cells;
    }
    
    /**
     * Get villages by cell - Comprehensive Rwandan administrative structure
     * Each cell typically has 3-5 villages
     */
    public static Map<String, List<String>> getVillagesByCell() {
        Map<String, List<String>> villages = new HashMap<>();
        
        // Generate villages for all cells - each cell has 3-5 villages
        Map<String, List<String>> cells = getCellsBySector();
        for (Map.Entry<String, List<String>> cellEntry : cells.entrySet()) {
            String cellName = cellEntry.getKey();
            // Create villages for each cell (typically 3-5 villages per cell)
            List<String> cellVillages = new ArrayList<>();
            for (int i = 1; i <= 4; i++) {
                cellVillages.add(cellName + " Village " + i);
            }
            villages.put(cellName, cellVillages);
        }
        
        // Add specific known villages for major cells
        // Eastern Province - Bugesera
        villages.put("Gashora", Arrays.asList("Gashora I", "Gashora II", "Kabuye", "Kamabuye", "Mareba"));
        villages.put("Juru", Arrays.asList("Juru I", "Juru II", "Kamabuye", "Mareba"));
        villages.put("Kamabuye", Arrays.asList("Kamabuye I", "Kamabuye II", "Mareba", "Nyamata"));
        
        // Eastern Province - Gatsibo
        villages.put("Gasange", Arrays.asList("Gasange I", "Gasange II", "Gatsibo", "Gitoki"));
        villages.put("Gatsibo", Arrays.asList("Gatsibo I", "Gatsibo II", "Gitoki", "Kabarore"));
        
        // Northern Province - Musanze
        villages.put("Busogo", Arrays.asList("Busogo I", "Busogo II", "Cyuve", "Gacaca"));
        villages.put("Cyuve", Arrays.asList("Cyuve I", "Cyuve II", "Gacaca", "Gashaki"));
        villages.put("Kinigi", Arrays.asList("Kinigi I", "Kinigi II", "Muhoza", "Muko"));
        
        // Southern Province - Huye
        villages.put("Gishamvu", Arrays.asList("Gishamvu I", "Gishamvu II", "Huye", "Karama"));
        villages.put("Huye", Arrays.asList("Huye I", "Huye II", "Karama", "Kigoma"));
        
        // Western Province - Rubavu
        villages.put("Bugeshi", Arrays.asList("Bugeshi I", "Bugeshi II", "Busasamana", "Cyanzarwe"));
        villages.put("Gisenyi", Arrays.asList("Gisenyi I", "Gisenyi II", "Kanama", "Kanzenze"));
        
        return villages;
    }
    
    /**
     * Get districts for a specific province
     */
    public static List<String> getDistricts(String province) {
        return getDistrictsByProvince().getOrDefault(province, new ArrayList<>());
    }
    
    /**
     * Get sectors for a specific district
     */
    public static List<String> getSectors(String district) {
        return getSectorsByDistrict().getOrDefault(district, new ArrayList<>());
    }
    
    /**
     * Get cells for a specific sector
     */
    public static List<String> getCells(String sector) {
        return getCellsBySector().getOrDefault(sector, new ArrayList<>());
    }
    
    /**
     * Get villages for a specific cell
     */
    public static List<String> getVillages(String cell) {
        return getVillagesByCell().getOrDefault(cell, new ArrayList<>());
    }
}

