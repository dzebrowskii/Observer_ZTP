import java.util.ArrayList;
import java.util.List;

// Interfejs obserwatora
interface BankObserver {
    void notify(BankAccount account, String operation, double amount);
}

// Klasa BankAccount (Subject)
class BankAccount {
    // Właściciel konta
    private String accountHolder;

    // Aktualny stan konta
    private double balance;

    // Lista obserwatorów
    private List<BankObserver> observers = new ArrayList<>();

    public BankAccount(String accountHolder, double initialBalance) {
        this.accountHolder = accountHolder;
        this.balance = initialBalance;
    }

    // Getter dla właściciela konta
    public String getAccountHolder() {
        return accountHolder;
    }

    // Getter dla aktualnego stanu konta
    public double getBalance() {
        return balance;
    }

    // Dodawanie obserwatora
    public void addObserver(BankObserver observer) {
        observers.add(observer);
    }

    // Usuwanie obserwatora
    public void removeObserver(BankObserver observer) {
        observers.remove(observer);
    }

    // Powiadamianie obserwatorów
    private void notifyObservers(String operation, double amount) {
        for (BankObserver observer : observers) {
            observer.notify(this, operation, amount);
        }
    }

    // Wpłacanie pieniędzy na konto
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Kwota wpłaty musi być większa od zera.");
        }
        balance += amount;
        System.out.printf("Wpłata: %.2f. Nowy stan konta: %.2f.%n", amount, balance);
        notifyObservers("Deposit", amount);
    }

    // Wypłacanie pieniędzy z konta
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Kwota wypłaty musi być większa od zera.");
        }
        if (amount > balance) {
            throw new IllegalStateException("Brak wystarczających środków na koncie.");
        }
        balance -= amount;
        System.out.printf("Wypłata: %.2f. Nowy stan konta: %.2f.%n", amount, balance);
        notifyObservers("Withdraw", amount);
    }
}

// Moduł oferujący kredyt
class LoanOfferObserver implements BankObserver {
    private double loanThreshold;

    public LoanOfferObserver(double loanThreshold) {
        this.loanThreshold = loanThreshold;
    }

    @Override
    public void notify(BankAccount account, String operation, double amount) {
        if (account.getBalance() < loanThreshold) {
            System.out.printf("[LoanOffer] Saldo poniżej %.2f. Oferujemy kredyt dla klienta %s.%n", loanThreshold, account.getAccountHolder());
        }
    }
}

// Moduł oferujący lokatę
class SavingsOfferObserver implements BankObserver {
    private double significantDepositThreshold;

    public SavingsOfferObserver(double significantDepositThreshold) {
        this.significantDepositThreshold = significantDepositThreshold;
    }

    @Override
    public void notify(BankAccount account, String operation, double amount) {
        if ("Deposit".equals(operation) && amount >= significantDepositThreshold) {
            System.out.printf("[SavingsOffer] Wpłata powyżej %.2f. Oferujemy lokatę dla klienta %s.%n", significantDepositThreshold, account.getAccountHolder());
        }
    }
}

// Moduł oferujący kartę kredytową
class CreditCardOfferObserver implements BankObserver {
    private int withdrawalCountThreshold;
    private int withdrawalCount = 0;

    public CreditCardOfferObserver(int withdrawalCountThreshold) {
        this.withdrawalCountThreshold = withdrawalCountThreshold;
    }

    @Override
    public void notify(BankAccount account, String operation, double amount) {
        if ("Withdraw".equals(operation)) {
            withdrawalCount++;
            if (withdrawalCount >= withdrawalCountThreshold) {
                System.out.printf("[CreditCardOffer] Wykonano %d wypłat. Oferujemy kartę kredytową dla klienta %s.%n", withdrawalCount, account.getAccountHolder());
                withdrawalCount = 0; // reset licznika po ofercie
            }
        }
    }
}

// Moduł bezpieczeństwa
class SecurityObserver implements BankObserver {
    private double suspiciousTransactionThreshold;

    public SecurityObserver(double suspiciousTransactionThreshold) {
        this.suspiciousTransactionThreshold = suspiciousTransactionThreshold;
    }

    @Override
    public void notify(BankAccount account, String operation, double amount) {
        if ("Withdraw".equals(operation) && amount >= suspiciousTransactionThreshold) {
            System.out.printf("[SecurityAlert] Podejrzana operacja: wypłata %.2f przekracza próg %.2f. Klient: %s.%n",
                    amount, suspiciousTransactionThreshold, account.getAccountHolder());
        }
    }
}


public class Ztp10 {
    public static void main(String[] args) {
        // Tworzenie konta bankowego
        BankAccount account = new BankAccount("Jan Kowalski", 3000);

        // Rejestracja modułów jako obserwatorów
        account.addObserver(new LoanOfferObserver(1000));
        account.addObserver(new SavingsOfferObserver(5000));
        account.addObserver(new CreditCardOfferObserver(3));
        account.addObserver(new SecurityObserver(2000)); // Rejestracja modułu bezpieczeństwa

        // Operacje na koncie
        account.deposit(2000);  // Oferuje lokatę
        account.deposit(6000);  // Oferuje lokatę
        account.withdraw(1500); // Brak akcji
        account.withdraw(2000); // Podejrzana operacja (SecurityAlert)
        account.withdraw(2500); // Oferuje kartę kredytową, podejrzana operacja (SecurityAlert)
        account.withdraw(3000); // Podejrzana operacja (SecurityAlert)

        System.out.printf("%nKońcowy stan konta: %.2f%n", account.getBalance());
    }
}

