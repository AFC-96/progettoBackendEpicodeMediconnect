# MediConnect Backend API

Un'API REST completa per una piattaforma di telemedicina, sviluppata con **Spring Boot 4.1**, **PostgreSQL 16** e autenticazione **JWT**.

---

## Panoramica del Progetto

MediConnect è un sistema backend per la telemedicina che consente ai **pazienti** di prenotare appuntamenti con i **medici**, ricevere **consultazioni**, gestire **prescrizioni** e **referti di laboratorio**, e consultare informazioni sui **farmaci** tramite l'API OpenFDA.

### Modello del Dominio

```
User (base)
├── Patient (profilo con informazioni mediche)
└── Doctor  (profilo con specializzazione)

Appointment ──collega──► Doctor + Patient
└── Consultation (note SOAP)

MedicalRecord (astratto, @Inheritance JOINED)
├── Prescription (farmaco, dosaggio, validità)
└── LabResult    (esame, risultato, flag anomalia)

Notification (log email)
Role (PATIENT | DOCTOR | ADMIN)
```

---

## Stack Tecnologico

| Livello | Tecnologia |
|---|---|
| Framework | Spring Boot 4.1.0 |
| Linguaggio | Java 21 |
| Database | PostgreSQL 16 |
| ORM | Hibernate / Spring Data JPA |
| Sicurezza | Spring Security + JWT (jjwt) |
| Email | JavaMailSender + Thymeleaf |
| API Esterna 1 | JavaMail SMTP (notifiche email) |
| API Esterna 2 | OpenFDA Drug API (ricerca farmaci e avvertenze) |
| Build | Maven Wrapper |

---

## Avvio dell'Applicazione

### Prerequisiti

- Java 21+
- Maven (oppure usa il wrapper incluso `./mvnw`)
- PostgreSQL 16 in esecuzione in locale

### 1. Configurazione PostgreSQL

```sql
-- Come superutente postgres:
CREATE USER mediconnect WITH PASSWORD 'mediconnect123';
CREATE DATABASE mediconnect_db OWNER mediconnect;

-- Inserimento ruoli base:
\c mediconnect_db
INSERT INTO roles (name) VALUES ('PATIENT'), ('DOCTOR'), ('ADMIN');
```

### 2. Configurazione Ambiente

Il file `.env` è già configurato per lo sviluppo locale:

```env
DB_URL=jdbc:postgresql://localhost:5432/mediconnect_db
DB_USERNAME=mediconnect
DB_PASSWORD=mediconnect123
JWT_SECRET=chiave_segreta_jwt
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=tua_email@gmail.com
MAIL_PASSWORD=password_app_gmail
```

### 3. Avvio

```bash
cd backend/mediconnectBackend
./mvnw spring-boot:run
```

Il server si avvia su **http://localhost:8086**

---

## Autenticazione

Autenticazione basata su JWT. Includi il token in ogni richiesta protetta:

```
Authorization: Bearer <token>
```

### Ruoli e Permessi

| Ruolo | Permessi |
|---|---|
| `PATIENT` | Prenotare appuntamenti, visualizzare profilo, prescrizioni, referti, consultazioni |
| `DOCTOR` | Visualizzare pazienti, creare consultazioni, prescrizioni, referti, aggiornare stato |
| `ADMIN` | Accesso completo — gestione utenti e ruoli |

---

## Endpoint API

### Autenticazione (pubblico)
| Metodo | Endpoint | Descrizione |
|---|---|---|
| POST | `/api/auth/register` | Registrazione nuovo utente (ruolo PATIENT assegnato) |
| POST | `/api/auth/login` | Login e ricezione token JWT |
| POST | `/api/auth/forget-password?email=` | Invio codice di reset password |
| POST | `/api/auth/reset-password` | Reset password tramite codice |

### Utenti (autenticato)
| Metodo | Endpoint | Descrizione |
|---|---|---|
| GET | `/api/users/me` | Ottieni profilo utente corrente |
| GET | `/api/users/{id}` | Ottieni utente per ID (ADMIN) |
| GET | `/api/users` | Lista tutti gli utenti (ADMIN) |
| PUT | `/api/users/password` | Aggiorna password |
| POST | `/api/users/profile-picture` | Carica foto profilo |

### Medici (GET pubblico, PUT autenticato)
| Metodo | Endpoint | Descrizione |
|---|---|---|
| GET | `/api/doctors` | Lista tutti i medici |
| GET | `/api/doctors/{id}` | Ottieni medico per ID |
| GET | `/api/doctors/specialization/{spec}` | Filtra per specializzazione |
| GET | `/api/doctors/me` | Ottieni profilo medico personale (DOCTOR) |
| PUT | `/api/doctors/me` | Aggiorna profilo medico personale (DOCTOR) |

### Pazienti (autenticato)
| Metodo | Endpoint | Descrizione |
|---|---|---|
| GET | `/api/patients/me` | Ottieni profilo paziente personale (PATIENT) |
| PUT | `/api/patients/me` | Aggiorna profilo paziente personale (PATIENT) |
| GET | `/api/patients/{id}` | Ottieni paziente per ID (DOCTOR, ADMIN) |

### Appuntamenti (autenticato)
| Metodo | Endpoint | Descrizione |
|---|---|---|
| POST | `/api/appointments` | Prenota appuntamento (PATIENT) |
| GET | `/api/appointments` | Lista i miei appuntamenti |
| GET | `/api/appointments/{id}` | Ottieni appuntamento per ID |
| PATCH | `/api/appointments/{id}/cancel` | Cancella appuntamento |
| PATCH | `/api/appointments/{id}/status?status=` | Aggiorna stato (DOCTOR, ADMIN) |

### Consultazioni (autenticato)
| Metodo | Endpoint | Descrizione |
|---|---|---|
| POST | `/api/consultations` | Crea consultazione (DOCTOR) |
| GET | `/api/consultations/appointment/{id}` | Ottieni consultazione per appuntamento |
| GET | `/api/consultations/me` | Le mie consultazioni (PATIENT) |
| PUT | `/api/consultations/{id}` | Aggiorna consultazione (DOCTOR) |

### Cartella Clinica — Prescrizioni (autenticato)
| Metodo | Endpoint | Descrizione |
|---|---|---|
| POST | `/api/medical-records/prescriptions` | Crea prescrizione (DOCTOR) |
| GET | `/api/medical-records/prescriptions/appointment/{id}` | Prescrizioni per appuntamento |
| GET | `/api/medical-records/prescriptions/me` | Le mie prescrizioni (PATIENT) |

### Cartella Clinica — Referti di Laboratorio (autenticato)
| Metodo | Endpoint | Descrizione |
|---|---|---|
| POST | `/api/medical-records/lab-results` | Crea referto di laboratorio (DOCTOR) |
| GET | `/api/medical-records/lab-results/appointment/{id}` | Referti per appuntamento |
| GET | `/api/medical-records/lab-results/me` | I miei referti (PATIENT) |

### Storico Clinico (autenticato)
| Metodo | Endpoint | Descrizione |
|---|---|---|
| GET | `/api/medical-records/history/me` | Storico clinico completo (PATIENT) |

### Ricerca Farmaci — OpenFDA (pubblico)
| Metodo | Endpoint | Descrizione |
|---|---|---|
| GET | `/api/drugs/search?name={nome}&limit={n}` | Cerca farmaco per nome commerciale |
| GET | `/api/drugs/interactions?drugName={nome}` | Interazioni farmacologiche |
| GET | `/api/drugs/warnings?drugName={nome}` | Avvertenze e controindicazioni |

### Ruoli (autenticato)
| Metodo | Endpoint | Descrizione |
|---|---|---|
| GET | `/api/roles` | Lista tutti i ruoli (ADMIN) |
| POST | `/api/roles` | Crea ruolo (ADMIN) |
| PUT | `/api/roles/{id}` | Aggiorna ruolo (ADMIN) |
| DELETE | `/api/roles/{id}` | Elimina ruolo (ADMIN) |

---

## API di Terze Parti

### 1. JavaMail SMTP (Notifiche Email)
- Utilizzo: email di benvenuto alla registrazione, conferma appuntamenti, codici reset password, notifiche di cancellazione
- Motore di template: Thymeleaf (template HTML)
- Configurazione: `MAIL_HOST`, `MAIL_USERNAME`, `MAIL_PASSWORD` nel file `.env`

### 2. OpenFDA Drug Label API
- URL base: `https://api.fda.gov/drug/label.json`
- Nessuna API key richiesta
- Utilizzo: ricerca informazioni farmaci, avvertenze sulle interazioni, alert di sicurezza
- Endpoint esposti: `/api/drugs/search`, `/api/drugs/interactions`, `/api/drugs/warnings`

---

## Schema del Database

**12 tabelle totali:**

| Tabella | Descrizione |
|---|---|
| `users` | Account utente base |
| `roles` | PATIENT, DOCTOR, ADMIN |
| `user_roles` | Join many-to-many utenti-ruoli |
| `patients` | Profilo esteso del paziente |
| `doctors` | Profilo esteso del medico |
| `appointments` | Appuntamenti prenotati |
| `consultations` | Note di consultazione SOAP |
| `medical_records` | Tabella base (ereditarietà JPA) |
| `prescriptions` | Prescrizioni mediche (extends medical_records) |
| `lab_results` | Referti di laboratorio (extends medical_records) |
| `notifications` | Log delle email inviate |
| `password_reset_codes` | Codici di reset con scadenza |

---

## Note sulla Sicurezza

- Password cifrate con **BCrypt**
- Token JWT con scadenza configurabile (default **90 giorni**)
- Campo password escluso da tutte le risposte API (`@JsonIgnore`)
- CORS configurato (limitare le origini in produzione)

---

## Struttura del Progetto

```
src/main/java/com/mediconnect/
├── appointment/     controller, dto, entity, repo, service
├── config/          AppConfig (ModelMapper, RestTemplate, TemplateEngine)
├── consultation/    controller, dto, entity, repo, service
├── doctor/          controller, dto, entity, repo, service
├── drug/            controller, service  ← Integrazione OpenFDA
├── enums/           AppointmentStatus, BloodGroup, Genotype, Specialization
├── exceptions/      GlobalExceptionHandler, eccezioni personalizzate
├── medical/         controller, dto, entity, repo, service  ← Ereditarietà JPA
├── notification/    dto, entity, repo, service
├── patient/         controller, dto, entity, repo, service
├── res/             Wrapper Response<T>
├── role/            controller, entity, repo, service
├── security/        SecurityFilter, AuthFilter, utilità JWT
└── users/           controller, dto, entity, repo, service
```

---

## Test degli Endpoint

Importa il file **`MediConnect.postman_collection.json`** in Postman.

Tutte le richieste includono payload di esempio, header di autenticazione e parametri già configurati. Il token JWT viene salvato automaticamente nella variabile `{{token}}` dopo il login.

