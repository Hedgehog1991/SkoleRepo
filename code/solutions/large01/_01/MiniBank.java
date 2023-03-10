package solutions.large01._01;

import solutions.large01.Terminal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class MiniBank
{
    //-------------------------------------------------------------------------
    //# Static
    private static final HashMap<String, String> login = new HashMap<>();
    private static final HashMap<String, Integer> money = new HashMap<>();

    //# Initialize static context
    static {
        login.put("Marcus", "1234");
        money.put("Marcus", 9001);

        login.put("Sucram", "4321");
        money.put("Sucram", 1234);
    }

    //-------------------------------------------------------------------------
    //# Fields
    private String location;
    private String loggedInUser = null;

    //-------------------------------------------------------------------------
    //# Constructor
    public MiniBank(String location) {
        this.setLocation(location);
    }

    //-------------------------------------------------------------------------
    //# Getter-methods
    public String getLocation() {
        return this.location;
    }

    public Optional<String> getLoggedInUser() {
        return Optional.ofNullable(this.loggedInUser);
    }

    //-------------------------------------------------------------------------
    //# Setter-methods
    private void setLocation(String value) {
        this.location = value;
    }

    private void setLoggedInUser(String value) {
        this.loggedInUser = value;
    }

    //-------------------------------------------------------------------------
    //# Methods
    private void login(String username) {
        this.setLoggedInUser(username);
    }

    private void logout() {
        this.login(null);
    }

    /**
     * Returnerer saldoen til innlogget bruker
     *
     * @return Saldo som heltall
     */
    private int getBalance() {
        Optional<String> loggedInUser = this.getLoggedInUser();

        return loggedInUser.map(this::getBalance).orElse(0);
    }

    /**
     * Returnerer saldoen tilnyttet et gitt brukernavn
     *
     * @param username Brukernavn som  skal sjekkes
     * @return Saldo som heltall
     */
    private int getBalance(String username) {
        return Optional.ofNullable(MiniBank.money.get(username)).orElse(0);
    }

    /**
     * Printer ut hovedmenyen
     */
    private void mainMenu() {
        ArrayList<String> menuOptions = new ArrayList<>();

        menuOptions.add("Logg inn");
        menuOptions.add("Avslutt");

        int choice  = Terminal.printMenuAndReturnChoice("Hovedmeny", menuOptions);

        switch (choice) {
            case 1 -> this.handleLogin();
            case 2 -> this.handleExit();
        }
    }

    /**
     * H??ndterer prosessen med ?? logge inn
     */
    private void handleLogin() {
        System.out.println();

        String username = Terminal.getText("Skriv inn ditt brukernavn");

        if (MiniBank.login.containsKey(username)) {
            String pin  = Terminal.getText("Skriv inn din pinkode");

            if (MiniBank.login.get(username).equals(pin)) {
                this.login(username);
                this.handleLoggedIn();

                return;
            }
            else {
                Terminal.log("MiniBank::handleLogin", "Brukernavn funnet, feil pinkode skrevet inn!");
            }
        }
        else {
            Terminal.log("MiniBank::handleLogin", "Brukernavn skrevet ikke funnet!");
        }

        System.out.println("Feil innlogging!");

        this.mainMenu();
    }

    /**
     * H??ndterer prosessen etter innlogging var vellykket
     */
    private void handleLoggedIn() {
        ArrayList<String> menuOptions = new ArrayList<>();

        menuOptions.add("Vis saldo");
        menuOptions.add("Sett inn penger");
        menuOptions.add("Ta ut penger");
        menuOptions.add("Send penger til en annen");
        menuOptions.add("Logg ut");

        int choice = Terminal.printMenuAndReturnChoice("Hovedmeny", menuOptions);

        switch (choice) {
            case 1 -> this.handlePrintBalance();
            case 2 -> this.handleDepositMoney();
            case 3 -> this.handleWithdrawMoney();
            case 4 -> this.handleTransferMoney();
            case 5 -> {
                this.logout();
                this.mainMenu();
            }
        }

        if (choice != 5) {
            this.handleLoggedIn();
        }
    }

    /**
     * H??ndterer det ?? printe ut saldoen til innlogget bruker
     */
    private void handlePrintBalance() {
        Optional<String> loggedInUser = this.getLoggedInUser();

        if (loggedInUser.isPresent()) {
            String username = loggedInUser.get();

            int balance = MiniBank.money.get(username);

            System.out.printf("%nSaldo: %s = kr %d,-%n", username, balance);
        }
        else {
            Terminal.log("Terminal::handlePrintBalance", "Bruker ikke logget inn!");
        }
    }

    /**
     * H??ndterer det ?? sette inn penger som innlogget bruker
     */
    private void handleDepositMoney() {
        Optional<String> loggedInUser = this.getLoggedInUser();

        if (loggedInUser.isPresent()) {
            String username = loggedInUser.get();

            int deposit = Terminal.getInteger("Hvor mye ??nsker du ?? sette inn?");
            int balance = this.getBalance(username);

            MiniBank.money.put(username, this.getBalance() + deposit);
        }
        else {
            Terminal.log("MiniBank::handleDepositMoney", "Bruker ikke logget inn!");
        }
    }

    /**
     * H??ndterer det ?? ta ut penger som innlogget bruker
     */
    private void handleWithdrawMoney() {
        Optional<String> loggedInUser = this.getLoggedInUser();

        if (loggedInUser.isPresent()) {
            String username = loggedInUser.get();

            int withdraw = Terminal.getInteger("Hvor mye ??nsker du ?? ta ut?");

            if (this.getBalance() >= withdraw) {
                MiniBank.money.put(username, this.getBalance() - withdraw);
            }
            else {
                System.out.println("Du har ikke nok penger til ?? utf??re dette!");
            }
        }
        else {
            Terminal.log("MiniBank::handleWithdrawMoney", "Bruker ikke logget inn!");
        }
    }

    /**
     * H??ndterer det ?? overf??re penger fra innlogget bruker til ??nsket mottaker
     */
    private void handleTransferMoney() {
        Optional<String> loggedInUser = this.getLoggedInUser();

        if (loggedInUser.isPresent()) {
            String target = Terminal.getText("Hva er brukernavnet til mottaker?");

            if (MiniBank.login.containsKey(target)) {
                String source = loggedInUser.get();

                if (source.equals(target)) {
                    System.out.println("Mottakeren kan ikke v??re den samme som sender penger!");

                    return;
                }

                int transfer = Terminal.getInteger("Hvor mye ??nsker du ?? overf??re?");

                if (this.getBalance() >= transfer) {
                    MiniBank.money.put(source, this.getBalance() - transfer);

                    MiniBank.money.put(target, this.getBalance(target) + transfer);

                    Terminal.log(
                            "MiniBank::handleTransferMoney",
                            "Overf??rte kr %d,- fra \"%s\" til \"%s\"".formatted(
                                    transfer, source, target
                            )
                    );
                }
                else {
                    System.out.println("Du har ikke nok penger til ?? overf??re dette bel??pet!");
                }
            }
            else {
                System.out.println("Kunne ikke finne mottakeren i systemet!");
            }
        }
        else {
            Terminal.log("MiniBank::handleTransferMoney", "Bruker ikke logget inn!");
        }
    }

    private void handleExit() {
        if (this.getLoggedInUser().isPresent()) {
            this.logout();
        }

        Terminal.log("Terminal::exit", "Avslutter programmet ...");

        System.exit(0);
    }

    //-------------------------------------------------------------------------
    //# Main-method -- Hvor programmet v??rt f??rst kj??rer som en applikasjon
    public static void main(String[] args) {
        Terminal.log("MiniBank::main", "Starter opp programmet ...");

        MiniBank bank = new MiniBank("Bankgaten 123");

        bank.mainMenu();
    }
}
