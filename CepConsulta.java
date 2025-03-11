package trabalho1;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class CepConsulta {
    private static final String LOG_FILE = "cep_log.txt";
    private static final String API_URL = "https://viacep.com.br/ws/";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Consultar CEP");
            System.out.println("2. Listar CEPs consultados");
            System.out.println("3. Sair");
            System.out.print("Escolha uma opção: ");

            int opcao = scanner.nextInt();
            scanner.nextLine(); // Consumir a quebra de linha

            switch (opcao) {
                case 1:
                    System.out.print("Digite o CEP: ");
                    String cep = scanner.nextLine();
                    consultarCep(cep);
                    break;
                case 2:
                    listarCepsConsultados();
                    break;
                case 3:
                    System.out.println("Saindo...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private static void consultarCep(String cep) {
        try {
            System.out.println("Conectando à API ViaCEP...");
            URL url = new URL(API_URL + cep + "/json/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                System.out.println("Erro na conexão: Código " + responseCode);
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            System.out.println("\nResultado da consulta:\n" + response.toString());
            salvarLog(cep);

        } catch (Exception e) {
            System.out.println("Erro ao consultar CEP: " + e.getMessage());
        }
    }

    private static void listarCepsConsultados() {
        try {
            if (!Files.exists(Paths.get(LOG_FILE))) {
                System.out.println("Nenhum CEP foi consultado ainda.");
                return;
            }
            System.out.println("CEPs consultados:");
            Files.lines(Paths.get(LOG_FILE)).forEach(System.out::println);
        } catch (IOException e) {
            System.out.println("Erro ao ler o log: " + e.getMessage());
        }
    }

    private static void salvarLog(String cep) {
        try (FileWriter writer = new FileWriter(LOG_FILE, true);
             BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            String timestamp = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z"));
            bufferedWriter.write(timestamp + " - CEP Consultado: " + cep);
            bufferedWriter.newLine();
        } catch (IOException e) {
            System.out.println("Erro ao salvar o log: " + e.getMessage());
        }
    }
}
