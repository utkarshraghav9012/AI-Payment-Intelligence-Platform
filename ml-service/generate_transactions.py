import csv
import random
from datetime import datetime, timedelta
from pathlib import Path


OUTPUT_FILE = (
    Path(__file__).resolve().parent.parent
    / "datasets"
    / "transactions"
    / "transactions.csv"
)

TOTAL_TRANSACTIONS = 1000

MERCHANTS = [
    "MERCHANT001",
    "MERCHANT002",
    "MERCHANT003",
    "MERCHANT004",
    "MERCHANT005",
]

PAYMENT_METHODS = [
    "UPI",
    "CARD",
    "NET_BANKING",
    "WALLET",
]

FAILURE_REASONS = [
    "TIMEOUT",
    "BANK_DECLINED",
    "INSUFFICIENT_FUNDS",
    "NETWORK_ERROR",
    "UNKNOWN",
]

FAILURE_WEIGHTS = [
    0.30,
    0.25,
    0.20,
    0.15,
    0.10,
]


def generate_failure_reason():
    return random.choices(
        FAILURE_REASONS,
        weights=FAILURE_WEIGHTS,
        k=1
    )[0]


def generate_transaction(index):
    transaction_id = f"TXN{100000 + index}"

    merchant_id = random.choice(MERCHANTS)

    customer_id = f"CUSTOMER{random.randint(1000, 9999)}"

    amount = round(random.uniform(100, 25000), 2)

    payment_method = random.choice(PAYMENT_METHODS)

    # Around 82% successful and 18% failed transactions
    is_success = random.random() < 0.82

    if is_success:
        status = "SUCCESS"
        failure_reason = ""
        retry_count = 0
    else:
        status = "FAILED"
        failure_reason = generate_failure_reason()
        retry_count = random.randint(0, 3)

    created_at = datetime.now() - timedelta(
        days=random.randint(0, 30),
        hours=random.randint(0, 23),
        minutes=random.randint(0, 59),
        seconds=random.randint(0, 59)
    )

    return [
        transaction_id,
        merchant_id,
        customer_id,
        amount,
        payment_method,
        status,
        failure_reason,
        retry_count,
        created_at.isoformat(),
    ]


def main():

    OUTPUT_FILE.parent.mkdir(
        parents=True,
        exist_ok=True
    )

    headers = [
        "transaction_id",
        "merchant_id",
        "customer_id",
        "amount",
        "payment_method",
        "status",
        "failure_reason",
        "retry_count",
        "created_at",
    ]

    with open(
        OUTPUT_FILE,
        "w",
        newline="",
        encoding="utf-8"
    ) as file:

        writer = csv.writer(file)

        writer.writerow(headers)

        for index in range(1, TOTAL_TRANSACTIONS + 1):
            writer.writerow(
                generate_transaction(index)
            )

    print(f"Generated {TOTAL_TRANSACTIONS} transactions.")
    print(f"Saved to: {OUTPUT_FILE}")


if __name__ == "__main__":
    main()