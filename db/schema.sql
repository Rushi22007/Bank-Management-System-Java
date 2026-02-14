-- Schema for Bank Management System (SQLite)
-- Creates tables used by the Java app and seeds a demo user.

PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS signup (
    form_no     TEXT PRIMARY KEY,
    name        TEXT NOT NULL,
    fname       TEXT NOT NULL,
    dob         TEXT NOT NULL,
    gender      TEXT NOT NULL,
    email       TEXT NOT NULL,
    marital     TEXT NOT NULL,
    address     TEXT NOT NULL,
    city        TEXT NOT NULL,
    pin_code    TEXT NOT NULL,
    state       TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS signup_two (
    form_no          TEXT PRIMARY KEY,
    religion         TEXT NOT NULL,
    category         TEXT NOT NULL,
    income           TEXT NOT NULL,
    education        TEXT NOT NULL,
    occupation       TEXT NOT NULL,
    pan              TEXT NOT NULL,
    aadhaar          TEXT NOT NULL,
    senior_citizen   TEXT NOT NULL,
    existing_account TEXT NOT NULL,
    FOREIGN KEY(form_no) REFERENCES signup(form_no) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS account (
    form_no      TEXT NOT NULL,
    account_type TEXT NOT NULL,
    services     TEXT NOT NULL,
    card_no      TEXT PRIMARY KEY,
    pin          TEXT UNIQUE NOT NULL,
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(form_no) REFERENCES signup(form_no) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS transactions (
    id       INTEGER PRIMARY KEY AUTOINCREMENT,
    pin      TEXT NOT NULL,
    date_ms  INTEGER NOT NULL,
    type     TEXT NOT NULL,
    amount   INTEGER NOT NULL,
    FOREIGN KEY(pin) REFERENCES account(pin) ON DELETE CASCADE
);

-- Seed demo data (same as the Java initializer)
INSERT OR IGNORE INTO signup(form_no, name, fname, dob, gender, email, marital, address, city, pin_code, state)
VALUES ('0001', 'Demo User', 'Demo Father', '01/01/1990', 'Other', 'demo@example.com', 'Other', '123 Demo Street', 'Demo City', '000000', 'Demo State');

INSERT OR IGNORE INTO signup_two(form_no, religion, category, income, education, occupation, pan, aadhaar, senior_citizen, existing_account)
VALUES ('0001', 'Other', 'Other', 'Null', 'Graduate', 'Salaried', 'AAAAA0000A', '000000000000', 'No', 'No');

INSERT OR IGNORE INTO account(form_no, account_type, services, card_no, pin)
VALUES ('0001', 'Saving Account', 'ATM CARD', '1234567890123456', '1234');

INSERT OR IGNORE INTO transactions(pin, date_ms, type, amount)
VALUES ('1234', strftime('%s','now')*1000, 'Deposit', 10000);
