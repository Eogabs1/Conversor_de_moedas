import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Main {

    // Classe para representar a resposta da API
    private static class ExchangeResponse {
        String result;
        String documentation;
        String terms_of_use;
        long time_last_update_unix;
        String time_last_update_utc;
        long time_next_update_unix;
        String time_next_update_utc;
        String base_code;
        String target_code;
        double conversion_rate;
        double conversion_result;
    }

    public static void main(String[] args) {
        // Configurações iniciais
        final String API_KEY = "3dbae6e00e43c44c00f677e6"; // Substitua pela sua chave
        final String URL_BASE = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/pair/";

        Scanner scanner = new Scanner(System.in);
        Gson gson = new Gson(); // Instância do Gson

        System.out.println("=== CONVERSOR DE MOEDAS COM API (USANDO GSON) ===");
        System.out.println("(Usando dados em tempo real)");

        boolean executando = true;

        while (executando) {
            System.out.println("\nOpções disponíveis:");
            System.out.println("1. USD → BRL (Dólar → Real)");
            System.out.println("2. EUR → BRL (Euro → Real)");
            System.out.println("3. GBP → BRL (Libra → Real)");
            System.out.println("4. BRL → USD (Real → Dólar)");
            System.out.println("5. BRL → EUR (Real → Euro)");
            System.out.println("6. Personalizado (qualquer moeda)");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");

            int opcao = scanner.nextInt();

            if (opcao == 0) {
                executando = false;
                System.out.println("Programa encerrado. Até mais!");
                continue;
            }

            try {
                String deMoeda = "", paraMoeda = "";

                switch (opcao) {
                    case 1:
                        deMoeda = "USD";
                        paraMoeda = "BRL";
                        break;
                    case 2:
                        deMoeda = "EUR";
                        paraMoeda = "BRL";
                        break;
                    case 3:
                        deMoeda = "GBP";
                        paraMoeda = "BRL";
                        break;
                    case 4:
                        deMoeda = "BRL";
                        paraMoeda = "USD";
                        break;
                    case 5:
                        deMoeda = "BRL";
                        paraMoeda = "EUR";
                        break;
                    case 6:
                        System.out.print("Digite a moeda de origem (ex: USD, EUR): ");
                        deMoeda = scanner.next().toUpperCase();
                        System.out.print("Digite a moeda de destino (ex: BRL, JPY): ");
                        paraMoeda = scanner.next().toUpperCase();
                        break;
                    default:
                        System.out.println("Opção inválida!");
                        continue;
                }

                System.out.print("Digite o valor a converter: ");
                double valor = scanner.nextDouble();

                String urlString = URL_BASE + deMoeda + "/" + paraMoeda + "/" + valor;
                URL url = new URL(urlString);

                HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
                conexao.setRequestMethod("GET");

                BufferedReader leitor = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
                StringBuilder resposta = new StringBuilder();
                String linha;

                while ((linha = leitor.readLine()) != null) {
                    resposta.append(linha);
                }
                leitor.close();

                // Parse da resposta usando Gson
                ExchangeResponse response = gson.fromJson(resposta.toString(), ExchangeResponse.class);

                System.out.printf("\n%.2f %s = %.2f %s\n", valor, deMoeda, response.conversion_result, paraMoeda);
                System.out.printf("Taxa de câmbio: 1 %s = %.4f %s\n", deMoeda, response.conversion_rate, paraMoeda);
                System.out.println("Última atualização: " + response.time_last_update_utc);

            } catch (Exception e) {
                System.out.println("Ocorreu um erro: " + e.getMessage());
                System.out.println("Verifique:");
                System.out.println("- Sua conexão com a internet");
                System.out.println("- Se a API_KEY está correta");
                System.out.println("- Se as moedas digitadas são válidas");
            }
        }

        scanner.close();
    }
}