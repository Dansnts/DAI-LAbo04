package ch.heigvd.dai;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "Main", mixinStandardHelpOptions = true,
        description = "Lance l'API sur un port spécifié.")
public class Main implements Runnable {

    @Option(names = {"-p", "--port"}, description = "Le port sur lequel l'API écoute", defaultValue = "7000")
    private int port;

    public static void main(String[] args) {
        try {
            // Exécuter l'application avec les arguments de ligne de commande
            int exitCode = new CommandLine(new Main()).execute(args);
            System.exit(exitCode);
        } catch (Exception e) {
            // En cas d'exception, afficher une erreur et retourner un code différent de 0
            System.err.println("Erreur fatale : " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        try {
            // Lancer l'API avec le port spécifié
            API mainAPI = new API(port);
            System.out.println("Lancement de l'API sur le port : " + port);
            mainAPI.start();

            // Garder le thread principal actif
            synchronized (this) {
                wait();
            }
        } catch (InterruptedException e) {
            System.out.println("L'application a été interrompue.");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution de l'API : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
